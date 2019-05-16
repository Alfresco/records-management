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

package org.alfresco.bm.dataload;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.bm.cm.FolderData;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.user.UserData;
import org.alfresco.utility.model.UserModel;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

/**
 * Abstract Class for loading and filing one single component.
 *
 * @author Silviu Dinuta
 * @since 2.6
 *
 */
public abstract class RMAbstractLoadComponent extends RMBaseEventProcessor
{
    private String eventNameComplete;
    public static final long DEFAULT_DELAY = 0L;
    private long delay = DEFAULT_DELAY;

    public void setEventNameComplete(String eventNameComplete)
    {
        this.eventNameComplete = eventNameComplete;
    }

    public String getEventNameComplete()
    {
        return eventNameComplete;
    }

    public void setDelay(long delay)
    {
        this.delay = delay;
    }

    public long getDelay()
    {
        return delay;
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
        String operation = (String) dataObj.get(FIELD_LOAD_OPERATION);

        if (context == null || path == null || isBlank(operation))
        {
            return new EventResult("Request data not complete for filing unfiled record: " + dataObj, false);
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

        switch (operation)
        {
            case FILE_RECORD_OPERATION:
            case LOAD_UNFILED_RECORD_OPERATION:
            case LOAD_RECORD_OPERATION:
            case LOAD_ROOT_CATEGORY_OPERATION:
            case LOAD_SUB_CATEGORY_OPERATION:
            case LOAD_RECORD_FOLDER_OPERATION:
            case LOAD_ROOT_UNFILED_RECORD_FOLDER_OPERATION:
            case LOAD_UNFILED_RECORD_FOLDER_OPERATION:
                return loadOperation(folder, dataObj, operation);
            default:
                throw new IllegalStateException("Unsuported operation: " + operation);
        }
    }

    /**
     * Helper method to load component specified by operation parameter in specified folder.
     *
     * @param folder - the folder to load component in
     * @param dataObj - the query object that has record id information when filing one record
     * @param operation - load operation, that specifies which type of component to load
     * @return EventResult - the loading result or error if there was an exception on loading
     */
    private EventResult loadOperation(FolderData folder, DBObject dataObj, String operation)
    {
        if(FILE_RECORD_OPERATION.equals(operation))
        {
            String recordId = (String) dataObj.get(FIELD_RECORD_ID);
            if (isBlank(recordId))
            {
                return new EventResult("Request data not complete for filing unfiled record: " + dataObj, false);
            }
        }

        UserData user = getRandomUser(eventProcessorLogger);
        String username = user.getUsername();
        String password = user.getPassword();
        UserModel userModel = new UserModel(username, password);
        try
        {
            List<Event> scheduleEvents = new ArrayList<Event>();
            String message = executeOperation(folder, dataObj, operation, userModel);

            DBObject eventData = BasicDBObjectBuilder.start()
                        .add(FIELD_CONTEXT, folder.getContext())
                        .add(FIELD_PATH, folder.getPath()).get();
            Event nextEvent = new Event(getEventNameComplete(), eventData);

            scheduleEvents.add(nextEvent);
            DBObject resultData = BasicDBObjectBuilder.start()
                        .add("msg", message)
                        .add("path", folder.getPath())
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
                        .append("path", folder.getPath())
                        .append("stack", stack)
                        .get();
            // Build failure result
            return new EventResult(data, false);
        }
    }

    /**
     * Loads/files components and returns loading/filing message.
     *
     * @param folder - the folder to load component in
     * @param dataObj - the query object that has record id information when filing one record
     * @param operation - load operation, that specifies which type of component to load
     * @param userModel - user model with which the component is loaded/filed in specified folder
     * @return String - loading/filing message.
     * @throws Exception
     */
    abstract String executeOperation(FolderData folder, DBObject dataObj, String operation, UserModel userModel) throws Exception;
}
