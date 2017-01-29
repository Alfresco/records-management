/*
 * Copyright (C) 2005-2017 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.bm.dataload.rm.fileplan;

import static org.alfresco.bm.data.DataCreationState.Created;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.alfresco.bm.dataload.RMBaseEventProcessor;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.session.SessionService;
import org.alfresco.bm.site.SiteData;
import org.alfresco.bm.site.SiteDataService;
import org.alfresco.rest.core.RestWrapper;
import org.alfresco.rest.model.RestNodeModel;
import org.alfresco.rest.model.RestNodeModelsCollection;
import org.alfresco.rest.model.RestSiteContainerModel;
import org.alfresco.rest.model.RestSiteModel;
import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.model.UserModel;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

/**
 * Loader class that schedules the records declare event by creating the preconditions for {@link DeclareInPlaceRecords} event.
 *  - checks the state of the system
 *  - creates the community site and uploads files to be declared as records (if they don't exist)
 *  - creates the declare record events in the benchmark database in the SCHEDULED state
 *
 * @author Ana Bozianu
 * @since 2.6
 */
public class ScheduleInPlaceRecordLoaders extends RMBaseEventProcessor
{
    private boolean enabled = false;
    private String collabSiteId;
    private List<String> collabSitePaths;
    private String recordsToDeclare;
    private String username;
    private String password;
    private String eventNameDeclareInPlaceRecords;
    private String eventNameSkipDeclareInPlaceRecords;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SiteDataService siteDataService;

    @Autowired
    private RestWrapper restCoreAPI;

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public void setCollabSiteId(String collabSiteId)
    {
        this.collabSiteId = collabSiteId.toLowerCase();
    }

    public void setCollabSitePaths(String collabSitePathsString)
    {
        if (isNotBlank(collabSitePathsString))
        {
            this.collabSitePaths = Arrays.asList(collabSitePathsString.split(","));
        }
        else
        {
            this.collabSitePaths = new ArrayList<>();;
        }
    }

    public void setRecordsToDeclare(String recordsToDeclare)
    {
        this.recordsToDeclare = recordsToDeclare;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public void setEventNameDeclareInPlaceRecords(String eventNameDeclareInPlaceRecords)
    {
        this.eventNameDeclareInPlaceRecords = eventNameDeclareInPlaceRecords;
    }

    public void setEventNameSkipDeclareInPlaceRecords(String eventNameSkipDeclareInPlaceRecords)
    {
        this.eventNameSkipDeclareInPlaceRecords = eventNameSkipDeclareInPlaceRecords;
    }

    @Override
    protected EventResult processEvent(Event event) throws Exception
    {
        if (!enabled)
        {
            StringBuilder eventOutputMsg = new StringBuilder("Declaring in place records not wanted.");
            return new EventResult(eventOutputMsg.toString(), new Event(eventNameSkipDeclareInPlaceRecords, null));
        }

        StringBuilder eventOutputMsg = new StringBuilder("Preparing files to declare: \n");

        restCoreAPI.authenticateUser(new UserModel(username, password));

        // get or create collaboration site
        loadCollaborationSite(eventOutputMsg);

        // schedule the files to be declared as records
        List<Event> events = loadFilesToBeDeclared(eventOutputMsg);

        return new EventResult(eventOutputMsg.toString(), events);
    }

    /**
     * Helper method that makes sure the site exists on the server and loads it in the benchmark DB
     */
    private void loadCollaborationSite(StringBuilder eventOutputMsg) throws Exception
    {
        // Check if site exists on server
        Optional<RestSiteModel> colabSiteOptional = restCoreAPI
                .withCoreAPI()
                .getSites().getEntries().stream().filter(s->s.onModel().getId().equalsIgnoreCase(collabSiteId)).findFirst();

        if(!colabSiteOptional.isPresent())
        {
            // TODO create site 
            throw new NotImplementedException("The collaboration site to declare records from must exist");
        }
        RestSiteModel colabSite = colabSiteOptional.get();

        // Store the collaboration site in benchmark's DB
        SiteData colabSiteData = siteDataService.getSite(collabSiteId);
        if(colabSiteData == null)
        {
            // load site in Benchmark's DB
            colabSiteData = new SiteData();
            colabSiteData.setSiteId(collabSiteId);
            colabSiteData.setTitle(colabSite.onModel().getTitle());
            colabSiteData.setGuid(colabSite.onModel().getGuid());
            colabSiteData.setDescription(colabSite.onModel().getDescription());
            colabSiteData.setSitePreset(colabSite.onModel().getPreset());
            colabSiteData.setVisibility(colabSite.onModel().getVisibility().toString());
            colabSiteData.setCreationState(Created);
            siteDataService.addSite(colabSiteData);

            eventOutputMsg.append("   Added site '" + collabSiteId + "' as created.\n");
        }
    }

    /**
     * Helper method that loads the files to be declared in the benchmark DB as scheduled
     */
    private List<Event> loadFilesToBeDeclared(StringBuilder eventOutputMsg) throws Exception
    {
        RestSiteContainerModel documentLibrary = restCoreAPI.withCoreAPI().usingSite(collabSiteId).getSiteContainer("documentLibrary");

        if(collabSitePaths.isEmpty())
        {
            // list the whole fileplan
            return listContainer(documentLibrary.getId(), eventOutputMsg);
        }
        else
        {
            // list the provided paths
            List<Event> events = new ArrayList<>();
            for(String relativePath : collabSitePaths)
            {
                ContentModel docLibrary = new ContentModel();
                docLibrary.setNodeRef(documentLibrary.getId());
                RestNodeModelsCollection children = restCoreAPI.withParams("relativePath="+relativePath).withCoreAPI().usingNode(docLibrary).listChildren();
                for(RestNodeModel child : children.getEntries())
                {
                    events.addAll(handleExistingNode(child.onModel(), eventOutputMsg));
                }
            }
            return events;
        }
    }

    /**
     * Helper method that handles the current existing node. 
     * If the code is a file it stores it in the DB else calls the method again on children nodes.
     * 
     * @param currentNode the root node to start iterating from
     * @param eventOutputMsg output for logs
     * return 
     * @throws Exception
     */
    private List<Event> handleExistingNode(RestNodeModel node, StringBuilder eventOutputMsg) throws Exception
    {
        if(node.getIsFile())
        {
            eventOutputMsg.append("sheduled file to be declared as record: " + node.getId());

            // save it in benchmark's database
            
            // create an event 
            DBObject loadData = BasicDBObjectBuilder.start()
                    //.add(FIELD_CONTEXT, node.getContext())
                    .add(FIELD_ID, node.getId())
                    .add(FIELD_SITE_MANAGER, username)
                    .get();
            Event loadEvent = new Event(eventNameDeclareInPlaceRecords, loadData);
            // Each load event must be associated with a session
            String sessionId = sessionService.startSession(loadData);
            loadEvent.setSessionId(sessionId);
            // Add the event to the list
            return Arrays.asList(loadEvent);
        }
        else
        {
            return listContainer(node.getId(), eventOutputMsg);
        }
    }

    private List<Event> listContainer(String currentNodeId, StringBuilder eventOutputMsg) throws Exception
    {
        ContentModel currentNodeModel = new ContentModel();
        currentNodeModel.setNodeRef(currentNodeId);
        RestNodeModelsCollection children = restCoreAPI.withCoreAPI().usingNode(currentNodeModel).listChildren();
        List<Event> events = new ArrayList<>();
        for(RestNodeModel child : children.getEntries())
        {
            events.addAll(handleExistingNode(child.onModel(), eventOutputMsg));
        }
        return events;
    }
}
