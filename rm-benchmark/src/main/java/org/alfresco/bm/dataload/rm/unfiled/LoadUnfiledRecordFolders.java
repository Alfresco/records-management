/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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

package org.alfresco.bm.dataload.rm.unfiled;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.bm.cm.FolderData;
import org.alfresco.bm.dataload.RmBaseEventProcessor;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.restapi.RestAPIFactory;
import org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponent;
import org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponentType;
import org.alfresco.rest.rm.community.requests.FilePlanComponentAPI;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

/**
 * Unfiled record folders structure creation event
 *
 * @author Silviu Dinuta
 * @since 1.0
 *
 */
public class LoadUnfiledRecordFolders extends RmBaseEventProcessor
{
    public static final long DEFAULT_LOAD_UNFILED_RECORD_FOLDER_DELAY = 100L;

    private String eventNameUnfiledRecordFoldersLoaded;
    private long loadUnfiledRecordFolderDelay = DEFAULT_LOAD_UNFILED_RECORD_FOLDER_DELAY;

    @Autowired
    private RestAPIFactory restAPIFactory;

    public String getEventNameUnfiledRecordFoldersLoaded()
    {
        return eventNameUnfiledRecordFoldersLoaded;
    }

    public void setEventNameUnfiledRecordFoldersLoaded(String eventNameUnfiledRecordFoldersLoaded)
    {
        this.eventNameUnfiledRecordFoldersLoaded = eventNameUnfiledRecordFoldersLoaded;
    }


    public long getLoadUnfiledRecordFolderDelay()
    {
        return loadUnfiledRecordFolderDelay;
    }

    /**
     * Override the {@link #DEFAULT_Site_CREATION_DELAY default} time between creation requests
     */
    public void setLoadUnfiledRecordFolderDelay(long loadUnfiledRecordFolderDelay)
    {
        this.loadUnfiledRecordFolderDelay = loadUnfiledRecordFolderDelay;
    }

    @Override
    protected EventResult processEvent(Event event) throws Exception
    {
        super.suspendTimer();

        if (event == null)
        {
            throw new IllegalStateException("This processor requires an event.");
        }

        DBObject dataObj = (DBObject) event.getData();
        if (dataObj == null)
        {
            throw new IllegalStateException("This processor requires data with field " + FIELD_PATH);
        }
        String context = (String) dataObj.get(FIELD_CONTEXT);
        String path = (String) dataObj.get(FIELD_PATH);
        Integer rootFoldersToCreate = (Integer) dataObj.get(FIELD_UNFILED_ROOT_FOLDERS_TO_CREATE);
        Integer foldersToCreate = (Integer) dataObj.get(FIELD_UNFILED_FOLDERS_TO_CREATE);
        String siteManager = (String) dataObj.get(FIELD_SITE_MANAGER);
        if (context == null || path == null || rootFoldersToCreate == null || foldersToCreate == null || isBlank(siteManager))
        {
            return new EventResult("Request data not complete for folder loading: " + dataObj, false);
        }

        // Get the folder
        FolderData folder = fileFolderService.getFolder(context, path);
        if (folder == null)
        {
            throw new IllegalStateException("No such folder recorded: " + dataObj);
        }
        // Get the session
        String sessionId = event.getSessionId();
        if (sessionId == null)
        {
            return new EventResult("Load scheduling should create a session for each loader.",false);
        }

        return loadUnfiledRecordFolder(folder, rootFoldersToCreate, foldersToCreate, siteManager);
    }

    private EventResult loadUnfiledRecordFolder(FolderData container, int rootFoldersToCreate, int foldersToCreate, String siteManager)
                throws IOException
    {
        FilePlanComponentAPI api = restAPIFactory.getFilePlanComponentAPI(siteManager);
        api.setParameters("include=path");

        try
        {
            List<Event> scheduleEvents = new ArrayList<Event>();
            FilePlanComponent filePlanComponent = api.getFilePlanComponent(container.getId());

            //Create root unfiled record folders
            if(rootFoldersToCreate > 0)
            {
                super.resumeTimer();
                createFilePlanComponent(container, api, filePlanComponent, rootFoldersToCreate, ROOT_UNFILED_RECORD_FOLDER_NAME_IDENTIFIER, FilePlanComponentType.UNFILED_RECORD_FOLDER_TYPE.toString(),
                                        container.getContext(), loadUnfiledRecordFolderDelay);
                super.suspendTimer();
                String lockedPath = container.getPath() + "/locked";
                fileFolderService.deleteFolder(container.getContext(), lockedPath, false);
            }

            //Create unfiled record folder children
            if(foldersToCreate > 0)
            {
                super.resumeTimer();
                createFilePlanComponent(container, api, filePlanComponent, foldersToCreate, UNFILED_RECORD_FOLDER_NAME_IDENTIFIER, FilePlanComponentType.UNFILED_RECORD_FOLDER_TYPE.toString(),
                                        container.getContext(), loadUnfiledRecordFolderDelay);
                super.suspendTimer();
                // Clean up the lock
                String lockedPath = container.getPath() + "/locked";
                fileFolderService.deleteFolder(container.getContext(), lockedPath, false);
            }

            DBObject eventData = BasicDBObjectBuilder.start()
                        .add(FIELD_CONTEXT, container.getContext())
                        .add(FIELD_PATH, container.getPath()).get();
           Event nextEvent = new Event(eventNameUnfiledRecordFoldersLoaded, eventData);

           scheduleEvents.add(nextEvent);
            DBObject resultData = BasicDBObjectBuilder.start()
                        .add("msg", "Created " + rootFoldersToCreate + " root unfiled record folders and " + foldersToCreate + " unfiled folders children.")
                        .add("path", container.getPath())
                        .add("username", siteManager)
                        .get();

            return new EventResult(resultData, scheduleEvents);
        }
        catch (Exception e)
        {
            String error = e.getMessage();
            String stack = ExceptionUtils.getStackTrace(e);
            // Grab REST API information
            DBObject data = BasicDBObjectBuilder.start()
                        .append("error", error)
                        .append("username", siteManager)
                        .append("path", container.getPath())
                        .append("stack", stack)
                        .get();
            // Build failure result
            return new EventResult(data, false);
        }
    }
}
