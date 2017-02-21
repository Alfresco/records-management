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

import static org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponentType.RECORD_CATEGORY_TYPE;
import static org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponentType.RECORD_FOLDER_TYPE;

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
 * FilePlan structure creation event.
 *
 * @author Silviu Dinuta
 * @since 2.6
 *
 */
public class LoadFilePlan extends RMBaseEventProcessor
{

    public static final long DEFAULT_LOAD_FILEPLAN_DELAY = 100L;
    public static final int DEFAULT_ROOT_CATEGORY_NUMBER = 5;
    public static final int DEFAULT_AVERAGE_CATEGORY_DEPTH = 4;
    public static final int DEFAULT_AVERAGE_CATEGORY_VARIANCE = 2;
    private long loadFilePlanDelay = DEFAULT_LOAD_FILEPLAN_DELAY;

    private String eventNameRecordCategoryLoaded;

    @Autowired
    private RestAPIFactory restAPIFactory;


    public String getEventNameRecordCategoryLoaded()
    {
        return eventNameRecordCategoryLoaded;
    }

    public void setEventNameRecordCategoryLoaded(String eventNameRecordCategoryLoaded)
    {
        this.eventNameRecordCategoryLoaded = eventNameRecordCategoryLoaded;
    }

    public long getLoadFilePlanDelay()
    {
        return loadFilePlanDelay;
    }

    /**
     * Override the {@link #DEFAULT_LOAD_FILEPLAN_DELAY default} time between creation requests
     */
    public void setLoadFilePlanDelay(long loadFilePlanDelay)
    {
        this.loadFilePlanDelay = loadFilePlanDelay;
    }

    @Override
    public EventResult processEvent(Event event) throws Exception
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
        Integer rootCategoriesToCreate = (Integer) dataObj.get(FIELD_ROOT_CATEGORIES_TO_CREATE);
        Integer categoriesToCreate = (Integer) dataObj.get(FIELD_CATEGORIES_TO_CREATE);
        Integer foldersToCreate = (Integer) dataObj.get(FIELD_FOLDERS_TO_CREATE);
        if (context == null || path == null || foldersToCreate == null || categoriesToCreate == null
                    || rootCategoriesToCreate == null)
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
            return new EventResult("Load scheduling should create a session for each loader.", false);
        }

        return loadCategory(folder, rootCategoriesToCreate, categoriesToCreate, foldersToCreate);
    }

    /**
     * Helper method that creates specified number of root record categories if the specified container is the filePlan,
     * specified number of record categories and record folder children if the container is a record category,
     * or only specified number of record folders if we are on the last level or record categories.
     *
     * @param container - filePlan, root record category, or ordinary record category
     * @param rootFoldersToCreate - number of root record categories to create
     * @param categoriesToCreate - number of record category children
     * @param foldersToCreate - number of record folder children
     * @return EventResult - the loading result or error if there was an exception on loading
     * @throws IOException
     */
    private EventResult loadCategory(FolderData container, int rootCategoriesToCreate, int categoriesToCreate,
                int foldersToCreate) throws IOException
    {
        UserData user = getRandomUser(logger);
        String username = user.getUsername();
        String password = user.getPassword();
        FilePlanComponentAPI api = restAPIFactory.getFilePlanComponentsAPI(new UserModel(username, password));

        try
        {
            List<Event> scheduleEvents = new ArrayList<Event>();
            FilePlanComponent filePlanComponent = api.getFilePlanComponent(container.getId(), "include=path");

            // Create root categories
            if(rootCategoriesToCreate > 0)
            {
                super.resumeTimer();
                createFilePlanComponent(container, api, filePlanComponent, rootCategoriesToCreate,
                            ROOT_CATEGORY_NAME_IDENTIFIER,
                            RECORD_CATEGORY_TYPE, RECORD_CATEGORY_CONTEXT, loadFilePlanDelay);
                super.suspendTimer();
                String lockedPath = container.getPath() + "/locked";
                fileFolderService.deleteFolder(container.getContext(), lockedPath, false);
            }

            // Create categories
            if(categoriesToCreate > 0)
            {
                super.resumeTimer();
                createFilePlanComponent(container, api, filePlanComponent, categoriesToCreate,
                            CATEGORY_NAME_IDENTIFIER,
                            RECORD_CATEGORY_TYPE, RECORD_CATEGORY_CONTEXT, loadFilePlanDelay);
                super.suspendTimer();
                String lockedPath = container.getPath() + "/locked";
                fileFolderService.deleteFolder(container.getContext(), lockedPath, false);
            }

            // Create folders
            if(foldersToCreate > 0)
            {
                super.resumeTimer();
                createFilePlanComponent(container, api, filePlanComponent, foldersToCreate,
                            RECORD_FOLDER_NAME_IDENTIFIER,
                            RECORD_FOLDER_TYPE, RECORD_FOLDER_CONTEXT, loadFilePlanDelay);
                super.suspendTimer();
                String lockedPath = container.getPath() + "/locked";
                fileFolderService.deleteFolder(container.getContext(), lockedPath, false);
            }

            DBObject eventData = BasicDBObjectBuilder.start()
                        .add(FIELD_CONTEXT, container.getContext())
                        .add(FIELD_PATH, container.getPath()).get();
            Event event = new Event(eventNameRecordCategoryLoaded,eventData);
            scheduleEvents.add(event);

            DBObject resultData = BasicDBObjectBuilder.start()
                        .add("msg", "Created " + rootCategoriesToCreate + " root categories, " + categoriesToCreate + " categories and " + foldersToCreate
                                    + " record folders.")
                        .add("path", container.getPath())
                        .add("username", username).get();

            return new EventResult(resultData, scheduleEvents);
        }
        catch (Exception e)
        {
            String error = e.getMessage();
            String stack = ExceptionUtils.getStackTrace(e);
            // Grab REST API information
            DBObject data = BasicDBObjectBuilder.start().append("error", error).append("username", username)
                        .append("path", container.getPath()).append("stack", stack).get();
            // Build failure result
            return new EventResult(data, false);
        }
    }
}