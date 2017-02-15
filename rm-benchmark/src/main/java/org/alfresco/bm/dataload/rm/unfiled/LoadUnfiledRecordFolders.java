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

package org.alfresco.bm.dataload.rm.unfiled;

import static org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponentType.UNFILED_RECORD_FOLDER_TYPE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

import org.alfresco.bm.cm.FolderData;
import org.alfresco.bm.dataload.RMBaseEventProcessor;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.user.UserData;
import org.alfresco.rest.core.RestAPIFactory;
import org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponent;
import org.alfresco.rest.rm.community.requests.igCoreAPI.FilePlanComponentAPI;
import org.alfresco.utility.model.UserModel;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Unfiled record folders structure creation event
 *
 * @author Silviu Dinuta
 * @since 2.6
 *
 */
public class LoadUnfiledRecordFolders extends RMBaseEventProcessor
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
        if (context == null || path == null || rootFoldersToCreate == null || foldersToCreate == null)
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

        return loadUnfiledRecordFolder(folder, rootFoldersToCreate, foldersToCreate);
    }

    private EventResult loadUnfiledRecordFolder(FolderData container, int rootFoldersToCreate, int foldersToCreate)
                throws IOException
    {
        UserData user = getUser(logger);
        String username = user.getUsername();
        String password = user.getPassword();
        FilePlanComponentAPI api = restAPIFactory.getFilePlanComponentsAPI(new UserModel(username, password));

        try
        {
            List<Event> scheduleEvents = new ArrayList<Event>();
            FilePlanComponent filePlanComponent = api.getFilePlanComponent(container.getId(), "include=path");

            //Create root unfiled record folders
            if(rootFoldersToCreate > 0)
            {
                super.resumeTimer();
                createFilePlanComponent(container, api, filePlanComponent, rootFoldersToCreate, ROOT_UNFILED_RECORD_FOLDER_NAME_IDENTIFIER, UNFILED_RECORD_FOLDER_TYPE,
                                        container.getContext(), loadUnfiledRecordFolderDelay);
                super.suspendTimer();
                String lockedPath = container.getPath() + "/locked";
                fileFolderService.deleteFolder(container.getContext(), lockedPath, false);
            }

            //Create unfiled record folder children
            if(foldersToCreate > 0)
            {
                super.resumeTimer();
                createFilePlanComponent(container, api, filePlanComponent, foldersToCreate, UNFILED_RECORD_FOLDER_NAME_IDENTIFIER, UNFILED_RECORD_FOLDER_TYPE,
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
                        .add("username", username)
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
                        .append("username", username)
                        .append("path", container.getPath())
                        .append("stack", stack)
                        .get();
            // Build failure result
            return new EventResult(data, false);
        }
    }
}
