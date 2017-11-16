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
package org.alfresco.bm.dataload.rm.records;

import static org.alfresco.bm.data.DataCreationState.Created;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.alfresco.bm.dataload.RMBaseEventProcessor;
import org.alfresco.bm.dataload.rm.exceptions.DuplicateRecordException;
import org.alfresco.bm.dataload.rm.exceptions.EventAlreadyScheduledException;
import org.alfresco.bm.dataload.rm.services.ExecutionState;
import org.alfresco.bm.dataload.rm.services.RecordContext;
import org.alfresco.bm.dataload.rm.services.RecordData;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.session.SessionService;
import org.alfresco.bm.site.SiteData;
import org.alfresco.rest.core.RestWrapper;
import org.alfresco.rest.model.RestNodeModel;
import org.alfresco.rest.model.RestNodeModelsCollection;
import org.alfresco.rest.model.RestSiteContainerModel;
import org.alfresco.rest.model.RestSiteModel;
import org.alfresco.rest.model.builder.NodesBuilder.NodeDetail;
import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.model.UserModel;
import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

/**
 * Loader class that schedules the {@link DeclareInPlaceRecords} event by creating the preconditions.
 *  - creates/loads the community site and uploads files to be declared as records (if they don't exist)
 *  - preloads the file ids
 *  - schedules a set of events that run in parallel and reschedules self
 *
 * @author Ana Bozianu
 * @since 2.6
 */
public class ScheduleInPlaceRecordLoaders extends RMBaseEventProcessor implements InitializingBean
{
    public static final String DONE_EVENT_MSG = "Raising 'done' event.";
    public static final String DECLARING_NOT_WANTED_MSG = "Declaring in place records not wanted.";
    private static final String DEFAULT_EVENT_NAME_RESCHEDULE_SELF = "scheduleInPlaceRecordLoaders";
    private static final String DEFAULT_EVENT_NAME_DECLARE_IN_PLACE_RECORD = "declareInPlaceRecord";
    private static final String DEFAULT_EVENT_NAME_COMPLETE = "declaringInPlaceRecordsComplete";
    private boolean enabled = false;
    private Integer maxActiveLoaders;
    private long loadCheckDelay;
    private String collabSiteId;
    private List<String> collabSitePaths;
    private String username;
    private String password;
    private String eventNameDeclareInPlaceRecord = DEFAULT_EVENT_NAME_DECLARE_IN_PLACE_RECORD;
    private String eventNameComplete = DEFAULT_EVENT_NAME_COMPLETE;
    private String eventNameRescheduleSelf = DEFAULT_EVENT_NAME_RESCHEDULE_SELF;

    private Integer recordDeclarationLimit;
    private int numberOfRecordsDeclared = 0;
    // buffer with the file ids of unscheduled files
    private Queue<RecordData> unscheduledFilesCache;
    private Set<String> fullLoadedFolders;
    private final static int FILES_TO_SCHEDULE_BUFFER_SIZE = 1000;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private RestWrapper restCoreAPI;

    public ScheduleInPlaceRecordLoaders()
    {
        unscheduledFilesCache = new LinkedList<>();
        fullLoadedFolders = new HashSet<>();
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public void setMaxActiveLoaders(int maxActiveLoaders)
    {
        this.maxActiveLoaders = maxActiveLoaders;
    }

    public void setLoadCheckDelay(long loadCheckDelay)
    {
        this.loadCheckDelay = loadCheckDelay;
    }

    public void setCollabSiteId(String collabSiteId)
    {
        this.collabSiteId = collabSiteId.toLowerCase();
    }

    public void setCollabSitePaths(String collabSitePathsString)
    {
        if(isNotBlank(collabSitePathsString))
        {
            collabSitePaths = Arrays.asList(collabSitePathsString.split(","));
        }
        else
        {
            collabSitePaths = new ArrayList<>();
            collabSitePaths.add(new String("")); // If no paths specified use the document library
        }
    }

    public void setRecordDeclarationLimit(String recordDeclarationLimitString)
    {
        if(isNotBlank(recordDeclarationLimitString))
        {
            this.recordDeclarationLimit = Integer.parseInt(recordDeclarationLimitString);
        }
        else
        {
            this.recordDeclarationLimit = 0;
        }
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public void setEventNameDeclareInPlaceRecord(String eventNameDeclareInPlaceRecord)
    {
        this.eventNameDeclareInPlaceRecord = eventNameDeclareInPlaceRecord;
    }

    public String getEventNameDeclareInPlaceRecord()
    {
        return eventNameDeclareInPlaceRecord;
    }

    public void setEventNameComplete(String eventNameComplete)
    {
        this.eventNameComplete = eventNameComplete;
    }

    public String getEventNameComplete()
    {
        return eventNameComplete;
    }

    public void setEventNameRescheduleSelf(String eventNameRescheduleSelf)
    {
        this.eventNameRescheduleSelf = eventNameRescheduleSelf;
    }

    public String getEventNameRescheduleSelf()
    {
        return eventNameRescheduleSelf;
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        Assert.notNull(maxActiveLoaders);
        Assert.notNull(collabSiteId);
        Assert.notEmpty(collabSitePaths);
        Assert.notNull(username);
        Assert.notNull(password);
        Assert.notNull(getEventNameDeclareInPlaceRecord());
        Assert.notNull(getEventNameComplete());
        Assert.notNull(getEventNameRescheduleSelf());
        Assert.notNull(recordDeclarationLimit);
    }

    @Override
    protected EventResult processEvent(Event event) throws Exception
    {
        if (!enabled)
        {
            return new EventResult(DECLARING_NOT_WANTED_MSG, new Event(getEventNameComplete(), null));
        }

        long sessionCount = sessionService.getActiveSessionsCount();
        int loaderSessionsToCreate = maxActiveLoaders - (int) sessionCount;
        StringBuilder eventOutputMsg = new StringBuilder();
        List<Event> nextEvents = new ArrayList<>(loaderSessionsToCreate + 1);

        /*
         * Prepare files
         */
        restCoreAPI.authenticateUser(new UserModel(username, password));
        prepareFilesToBeDeclared(eventOutputMsg);

        if(unscheduledFilesCache.isEmpty())
        {
            // Make sure there are no files in process of being declared.
            int scheduledInPlaceRecords = (int) recordService.getRecordCountInSpecifiedPaths(ExecutionState.SCHEDULED.name(), null);
            if(scheduledInPlaceRecords > 0)
            {
                // Reschedule self. Allow events to finish before raising the done event
                Event nextEvent = new Event(getEventNameRescheduleSelf(), System.currentTimeMillis() + loadCheckDelay, null);
                nextEvents.add(nextEvent);
                eventOutputMsg.append("Waiting for " + scheduledInPlaceRecords + " in progress declare in place records events. Rescheduled self.");

                return new EventResult(eventOutputMsg.toString(), nextEvents);
            }

            // no more files to declare, raise done event
            return new EventResult(DONE_EVENT_MSG, new Event(getEventNameComplete(), null));
        }

        /*
         * Schedule worker events
         */
        for (int i = 0; i < loaderSessionsToCreate; i++)
        {
            RecordData record = unscheduledFilesCache.poll();
            if(record == null)
            {
                break;
            }
            try
            {
                nextEvents.add(scheduleFile(record, eventOutputMsg));
            }
            catch(EventAlreadyScheduledException ex)
            {
                logger.info("File " + record.getId() + " has already been scheduled. Skip it.", ex);
            }
        }
        numberOfRecordsDeclared += nextEvents.size();

        /*
         * Reschedule self
         */
        Event nextEvent = new Event(getEventNameRescheduleSelf(), System.currentTimeMillis() + loadCheckDelay, null);
        nextEvents.add(nextEvent);
        eventOutputMsg.append("Raised further " + (nextEvents.size() - 1) + " events and rescheduled self.");

        return new EventResult(eventOutputMsg.toString(), nextEvents);
    }

    /**
     * Helper method that makes sure the collaboration site contains enough files to declare.
     * If the collaboration site doesn't have enough files it creates new empty files.
     * The method caches the ids of the files ready to be declared in unscheduledFileBuffer queue.
     *
     * @param eventOutputMsg
     * @throws Exception
     */
    public void prepareFilesToBeDeclared(StringBuilder eventOutputMsg) throws Exception
    {
        if(!unscheduledFilesCache.isEmpty())
        {
            return;
        }
        eventOutputMsg.append("Preparing files to declare: \n");

        // Get the collaboration site document library
        String documentLibraryNodeId = getCollaborationSiteDoclib(eventOutputMsg);

        // Get the existing files in the provided paths
        for(String relativePath : collabSitePaths)
        {
            if(numberOfFilesLeftToPreload() <= 0)
            {
                // we have enough files cached
                return;
            }
            preloadExistingFiles(documentLibraryNodeId, relativePath, eventOutputMsg);
        }

        /*
         * Not enough files to load, create new files
         */
        if(recordDeclarationLimit == 0)
        {
            // If number of records to declare is 0 declare all files we can find but don't create new files
            return;
        }

        int filesToCreate = numberOfFilesLeftToPreload();
        if(filesToCreate > 0)
        {
            // Create a folder in document library
            ContentModel currentNodeModel = new ContentModel();
            currentNodeModel.setNodeRef(documentLibraryNodeId);
            NodeDetail targetFolder = restCoreAPI.withCoreAPI().usingNode(currentNodeModel).defineNodes().folder("AutoGeneratedFiles");

            for(int i = 0; i < filesToCreate; i++)
            {
                // Create a new file
                NodeDetail file = targetFolder.file("recordToBe");
                eventOutputMsg.append("Created file " + file.getId() + ".");

                RecordData record = new RecordData(file.getId(), RecordContext.IN_PLACE_RECORD, file.getName(), null, null, ExecutionState.SCHEDULED);
                unscheduledFilesCache.add(record);

                if(numberOfFilesLeftToPreload() <= 0)
                {
                    return;
                }
            }

        }
    }

    /**
     * Helper method that computes the number of files left to preload so that the scheduler has enough files to work with
     * @return the number of records left to preload
     */
    private int numberOfFilesLeftToPreload()
    {
        if(recordDeclarationLimit == 0)
        {
            // If number of records to declare is 0 load all the files we can find
            return FILES_TO_SCHEDULE_BUFFER_SIZE - unscheduledFilesCache.size();
        }
        return Integer.min(recordDeclarationLimit - numberOfRecordsDeclared, FILES_TO_SCHEDULE_BUFFER_SIZE) - unscheduledFilesCache.size();
    }

    /**
     *  Helper method that makes sure the site exists on the server and loads it in the benchmark DB
     *
     * @param eventOutputMsg
     * @return the collaboration site's document library id
     * @throws Exception
     */
    private String getCollaborationSiteDoclib(StringBuilder eventOutputMsg) throws Exception
    {
        // Check if the collaboration site exists on server using the REST api
        RestSiteModel colabSite = restCoreAPI.withCoreAPI().usingSite(collabSiteId).getSite();

        if (Integer.parseInt(restCoreAPI.getStatusCode()) == HttpStatus.SC_NOT_FOUND)
        {
            // The collaboration site doesn't exist, create it
            colabSite = restCoreAPI.withCoreAPI().usingSite(collabSiteId).createSite();
        }

        // Store the collaboration site in benchmark's DB
        SiteData colabSiteData = siteDataService.getSite(collabSiteId);
        if(colabSiteData == null)
        {
            // Store site info in Benchmark's DB
            colabSiteData = new SiteData();
            colabSiteData.setSiteId(collabSiteId);
            colabSiteData.setTitle(colabSite.getTitle());
            colabSiteData.setGuid(colabSite.getGuid());
            colabSiteData.setDescription(colabSite.getDescription());
            colabSiteData.setSitePreset(colabSite.getPreset());
            colabSiteData.setVisibility(colabSite.getVisibility().toString());
            colabSiteData.setCreationState(Created);
            siteDataService.addSite(colabSiteData);

            eventOutputMsg.append(" Added site \"" + collabSiteId + "\" as created.\n");
        }

        // Get site's document library
        RestSiteContainerModel documentLibrary = restCoreAPI.withCoreAPI().usingSite(collabSiteId).getSiteContainer("documentLibrary");
        return documentLibrary.getId();
    }

    /**
     * Helper method that iterates the hierarchy tree starting from a folder and caches the file ids
     * If the path doesn't exist nothing happens
     *
     * @param currentNodeId
     * @param relativePath
     * @param eventOutputMsg
     * @throws Exception
     */
    private void preloadExistingFiles(String currentNodeId, String relativePath, StringBuilder eventOutputMsg) throws Exception
    {
        boolean moreChildren;
        int skipCount = 0;
        do
        {
            ContentModel currentNodeModel = new ContentModel();
            currentNodeModel.setNodeRef(currentNodeId);
            RestNodeModelsCollection children = restCoreAPI.withParams("where=(isPrimary=true)", "relativePath="+relativePath, "skipCount="+skipCount).withCoreAPI().usingNode(currentNodeModel).listChildren();
            if(Integer.parseInt(restCoreAPI.getStatusCode()) == HttpStatus.SC_NOT_FOUND)
            {
                return;
            }

            for(RestNodeModel child : children.getEntries())
            {
                if(numberOfFilesLeftToPreload() <= 0)
                {
                    // we have enough files
                    return;
                }

                if(child.onModel().getIsFile())
                {
                    RecordData record = new RecordData(child.onModel().getId(), RecordContext.IN_PLACE_RECORD, child.onModel().getName(), null, null, ExecutionState.SCHEDULED);
                    unscheduledFilesCache.add(record);
                }
                else if(!fullLoadedFolders.contains(child.onModel().getId()))
                {
                    preloadExistingFiles(child.onModel().getId(), "", eventOutputMsg);
                }
            }

            moreChildren = children.getPagination().isHasMoreItems();
            skipCount += children.getPagination().getCount();
        }
        while(moreChildren);

        // mark the folder as complete to avoid listing its children in a following iteration
        fullLoadedFolders.add(currentNodeId);
    }

    /**
     * Helper method that creates a declare record event for the provided file
     *
     * @param fileToSchedule info of the file to declare as record
     * @param eventOutputMsg
     * @return the declare as record event for the provided file
     * @throws EventAlreadyScheduledException if the provided file has already been scheduled
     */
    private Event scheduleFile(RecordData fileToSchedule, StringBuilder eventOutputMsg) throws EventAlreadyScheduledException
    {
        eventOutputMsg.append("Sheduled file to be declared as record: " + fileToSchedule.getId() + ". ");

        // Create record in database to lock it
        try{
            recordService.createRecord(fileToSchedule);
        }
        catch(DuplicateRecordException ex)
        {
            throw new EventAlreadyScheduledException(eventNameDeclareInPlaceRecord, fileToSchedule.getId());
        }

        // Create an event
        DBObject declareData = BasicDBObjectBuilder.start()
                .add(FIELD_ID, fileToSchedule.getId())
                .add(FIELD_USERNAME, username)
                .add(FIELD_PASSWORD, password)
                .get();

        Event declareEvent = new Event(getEventNameDeclareInPlaceRecord(), declareData);
        // Each load event must be associated with a session
        String sessionId = sessionService.startSession(declareData);
        declareEvent.setSessionId(sessionId);

        return declareEvent;
    }
}
