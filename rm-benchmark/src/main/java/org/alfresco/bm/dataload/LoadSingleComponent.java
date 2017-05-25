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
import java.util.concurrent.TimeUnit;

import org.alfresco.bm.cm.FolderData;
import org.alfresco.bm.dataload.rm.services.ExecutionState;
import org.alfresco.bm.dataload.rm.services.RecordData;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.user.UserData;
import org.alfresco.rest.rm.community.model.record.RecordBodyFile;
import org.alfresco.rest.rm.community.requests.gscore.api.RecordsAPI;
import org.alfresco.utility.model.UserModel;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

/**
 * Common event for loading one single rm component. This event will load: one root record category, one sub-category, one record folder, one record, one root unfiled record folder,
 * one child unfiled record folder, one unfiled record. The will also file one unfiled record.
 *
 * @author Silviu Dinuta
 * @since 2.6
 *
 */
public class LoadSingleComponent extends RMBaseEventProcessor
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
                return fileRecordOperation(folder, dataObj);
            case LOAD_UNFILED_RECORD_OPERATION:
            case LOAD_RECORD_OPERATION:
            case LOAD_ROOT_CATEGORY_OPERATION:
            case LOAD_SUB_CATEGORY_OPERATION:
            case LOAD_RECORD_FOLDER_OPERATION:
            case LOAD_ROOT_UNFILED_RECORD_FOLDER_OPERATION:
            case LOAD_UNFILED_RECORD_FOLDER_OPERATION:
                return loadOperation(folder, operation);
            default:
                throw new IllegalStateException("Unsuported operation: " + operation);
        }
    }

    /**
     * Helper method to load component specified by operation parameter in specified folder.
     *
     * @param folder - the folder to load component in
     * @param operation - load operation, that specifies which type of component to load
     * @return EventResult - the loading result or error if there was an exception on loading
     */
    private EventResult loadOperation(FolderData folder, String operation)
    {
        UserData user = getRandomUser(logger);
        String username = user.getUsername();
        String password = user.getPassword();
        UserModel userModel = new UserModel(username, password);
        try
        {
            List<Event> scheduleEvents = new ArrayList<Event>();
            String message = "";
            switch (operation)
            {
                case LOAD_UNFILED_RECORD_OPERATION:
                {
                    super.resumeTimer();
                    uploadElectronicRecordInUnfiledContext(folder, userModel, RECORD_NAME_IDENTIFIER, delay);
                    super.suspendTimer();
                    message = "Created 1 record.";
                    break;
                }
                case LOAD_RECORD_OPERATION:
                {
                    super.resumeTimer();
                    uploadElectronicRecordInRecordFolder(folder, userModel, RECORD_NAME_IDENTIFIER, delay);
                    super.suspendTimer();
                    message = "Created 1 record.";
                    break;
                }
                case LOAD_ROOT_CATEGORY_OPERATION:
                {
                    super.resumeTimer();
                    createRootCategory(folder, userModel, ROOT_CATEGORY_NAME_IDENTIFIER, RECORD_CATEGORY_CONTEXT, delay);
                    super.suspendTimer();
                    message = "Created 1 root category.";
                    break;
                }
                case LOAD_SUB_CATEGORY_OPERATION:
                {
                    super.resumeTimer();
                    createSubCategory(folder, userModel, CATEGORY_NAME_IDENTIFIER, RECORD_CATEGORY_CONTEXT, delay);
                    super.suspendTimer();
                    message = "Created 1 sub-category";
                    break;
                }
                case LOAD_RECORD_FOLDER_OPERATION:
                {
                    super.resumeTimer();
                    createRecordFolder(folder, userModel, RECORD_FOLDER_NAME_IDENTIFIER, RECORD_FOLDER_CONTEXT, delay);
                    super.suspendTimer();
                    message = "Created 1 record folder.";
                    break;
                }
                case LOAD_ROOT_UNFILED_RECORD_FOLDER_OPERATION:
                {
                    super.resumeTimer();
                    createRootUnfiledRecordFolder(folder, userModel, ROOT_UNFILED_RECORD_FOLDER_NAME_IDENTIFIER, folder.getContext(), delay);
                    super.suspendTimer();
                    message = "Created 1 root unfiled record folder.";
                    break;
                }
                case LOAD_UNFILED_RECORD_FOLDER_OPERATION:
                {
                    super.resumeTimer();
                    createUnfiledRecordFolder(folder, userModel, UNFILED_RECORD_FOLDER_NAME_IDENTIFIER, folder.getContext(), delay);
                    super.suspendTimer();
                    message = "Created 1 unfiled record folder.";
                    break;
                }
            }

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
     * Helper method to file the unfiled record with specified id in specified record folder.
     *
     * @param folder - the record folded to file unfiled record in
     * @param dataObj DBObject - query object that contains the id of the unfiled record to be filed
     * @return EventResult - the filing result or error if there was an exception on filing
     */
    private EventResult fileRecordOperation(FolderData folder, DBObject dataObj)
    {
        String recordId = (String) dataObj.get(FIELD_RECORD_ID);
        if (isBlank(recordId))
        {
            return new EventResult("Request data not complete for filing unfiled record: " + dataObj, false);
        }
        UserData user = getRandomUser(logger);
        String username = user.getUsername();
        String password = user.getPassword();
        UserModel userModel = new UserModel(username, password);
        try
        {
            List<Event> scheduleEvents = new ArrayList<Event>();
            // FileRecords records
            String folderPath = folder.getPath();
            String parentId = folder.getId();

            RecordBodyFile recordBodyFileModel = RecordBodyFile.builder()
                        .targetParentId(parentId)
                        .build();

            RecordData randomRecord = recordService.getRecord(recordId);
            super.resumeTimer();
            RecordsAPI recordsAPI = getRestAPIFactory().getRecordsAPI(userModel);
            recordsAPI.fileRecord(recordBodyFileModel, randomRecord.getId());
            super.suspendTimer();
            // Increment counts
            fileFolderService.incrementFileCount(folder.getContext(), folderPath, 1);

            // Decrement counts for unfiled record folder or unfiled container
            String unfiledParentPath = randomRecord.getParentPath();
            fileFolderService.incrementFileCount(UNFILED_CONTEXT, unfiledParentPath, -1);

            //change parent path to the new parent
            randomRecord.setParentPath(folderPath);
            randomRecord.setExecutionState(ExecutionState.RECORD_FILED);
            recordService.updateRecord(randomRecord);
            TimeUnit.MILLISECONDS.sleep(delay);

            DBObject eventData = BasicDBObjectBuilder.start().add(FIELD_CONTEXT, folder.getContext())
                        .add(FIELD_PATH, folder.getPath()).get();
            Event nextEvent = new Event(getEventNameComplete(), eventData);

            scheduleEvents.add(nextEvent);
            DBObject resultData = BasicDBObjectBuilder.start().add("msg", "Filed record with id " + recordId + ".")
                        .add("path", folder.getPath()).add("username", username).get();

            return new EventResult(resultData, scheduleEvents);
        }
        catch (Exception e)
        {
            String error = e.getMessage();
            String stack = ExceptionUtils.getStackTrace(e);
            // Grab REST API information
            DBObject data = BasicDBObjectBuilder.start().append("error", error).append("username", username)
                        .append("path", folder.getPath()).append("stack", stack).get();
            // Build failure result
            return new EventResult(data, false);
        }
    }

}
