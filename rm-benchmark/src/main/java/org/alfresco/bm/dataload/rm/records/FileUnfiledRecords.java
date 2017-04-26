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

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.alfresco.bm.cm.FolderData;
import org.alfresco.bm.dataload.RMBaseEventProcessor;
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
 * Filing unfiled records event
 *
 * @author Silviu Dinuta
 * @since 2.6
 */
public class FileUnfiledRecords extends RMBaseEventProcessor
{
    public static final String EVENT_NAME_UNFILED_RECORDS_FILED = "unfiledRecordsLoaded";
    public static final long DEFAULT_FILE_UNFILED_RECORD_DELAY = 100L;
    private long fileUnfiledRecordDelay = DEFAULT_FILE_UNFILED_RECORD_DELAY;
    private List<String> fileFromUnfiledPaths;
    private Integer recordFilingLimit;
    String eventNameUnfiledRecordsFiled = EVENT_NAME_UNFILED_RECORDS_FILED;

    public long getFileUnfiledRecordDelay()
    {
        return fileUnfiledRecordDelay;
    }

    public void setFileUnfiledRecordDelay(long fileUnfiledRecordDelay)
    {
        this.fileUnfiledRecordDelay = fileUnfiledRecordDelay;
    }

    public String getEventNameUnfiledRecordsFiled()
    {
        return eventNameUnfiledRecordsFiled;
    }

    public void setEventNameUnfiledRecordsFiled(String eventNameUnfiledRecordsFiled)
    {
        this.eventNameUnfiledRecordsFiled = eventNameUnfiledRecordsFiled;
    }

    public void setFileFromUnfiledPaths(String fileFromUnfiledPathsStr)
    {
        if(isNotBlank(fileFromUnfiledPathsStr))
        {
            this.fileFromUnfiledPaths = Arrays.asList(fileFromUnfiledPathsStr.split(","));
        }
    }

    public void setRecordFilingLimit(String recordFilingLimitString)
    {
        if(isNotBlank(recordFilingLimitString))
        {
            this.recordFilingLimit = Integer.parseInt(recordFilingLimitString);
        }
        else
        {
            this.recordFilingLimit = 0;
        }
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
        Integer recordsToFile = (Integer) dataObj.get(FIELD_RECORDS_TO_FILE);
        if (context == null || path == null || recordsToFile == null)
        {
            return new EventResult("Request data not complete for filing unfiled records: " + dataObj, false);
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

        return fileRecords(folder, recordsToFile);
    }

    /**
     * Helper method that file specified numbers of records in specified record folder.
     *
     * @param container - record folder
     * @param recordsToFile - number of records to file
     * @return EventResult - the filing result or error if there was an exception on filing
     * @throws IOException
     */
    private EventResult fileRecords(FolderData container, int recordsToFile) throws IOException
    {
        UserData user = getRandomUser(logger);
        String username = user.getUsername();
        String password = user.getPassword();
        UserModel userModel = new UserModel(username, password);
        try
        {
            List<Event> scheduleEvents = new ArrayList<Event>();
            // FileRecords records
            if (recordsToFile > 0)
            {
                recordsToFile = fileRecord(container, userModel, recordsToFile, fileUnfiledRecordDelay);
                // Clean up the lock
                String lockedPath = container.getPath() + "/locked";
                fileFolderService.deleteFolder(container.getContext(), lockedPath, false);
            }

            DBObject eventData = BasicDBObjectBuilder.start().add(FIELD_CONTEXT, container.getContext())
                        .add(FIELD_PATH, container.getPath()).get();
            Event nextEvent = new Event(getEventNameUnfiledRecordsFiled(), eventData);

            scheduleEvents.add(nextEvent);
            DBObject resultData = BasicDBObjectBuilder.start().add("msg", "Filed " + recordsToFile + " records.")
                        .add("path", container.getPath()).add("username", username).get();

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

    /**
     * Helper method for filing specified number of unfiled records in specified record folder.
     *
     * @param folder - record folder in which the unfiled records will be filed
     * @param userModel - UserModel instance with which rest api will be called
     * @param recordsToFile - number of unfiled records to file
     * @param delay - delay between filing records
     * @return the number of filed records.
     * @throws Exception
     */
    public int fileRecord(FolderData folder, UserModel userModel, int recordsToFile, long delay) throws Exception
    {
        String folderPath = folder.getPath();
        String parentId = folder.getId();

        RecordBodyFile recordBodyFileModel = RecordBodyFile.builder()
                    .targetParentId(parentId)
                    .build();

        List<String> listOfUnfiledRecordFoldersPaths = null;
        if(recordFilingLimit > 0 && fileFromUnfiledPaths !=null && fileFromUnfiledPaths.size() > 0)
        {
            listOfUnfiledRecordFoldersPaths = getListOfUnfiledRecordFoldersPaths();
        }
        for (int i = 0; i < recordsToFile; i++)
        {
            RecordData randomRecord = recordService.getRandomRecord(ExecutionState.UNFILED_RECORD_DECLARED.name(), listOfUnfiledRecordFoldersPaths);

            if(randomRecord == null)
            {
                return i;
            }
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
        }
        return recordsToFile;
    }

    /**
     * Helper method for obtaining parent paths to file unfiled records from.
     *
     * @return all parent paths to file unfiled records from.
     */
    private List<String> getListOfUnfiledRecordFoldersPaths()
    {
        Set<String> allUnfiledParentPaths = getAllUnfiledParentPaths();
        List<String> availableUnfiledRecordFolderPaths = new ArrayList<>();
        LinkedHashSet<FolderData> unfiledFolderStructerFromExistentProvidedPaths = new LinkedHashSet<FolderData>();
        for(String path : fileFromUnfiledPaths)
        {
            if(!path.startsWith("/"))
            {
                path = "/" + path;
            }
            FolderData folder = fileFolderService.getFolder(UNFILED_CONTEXT, UNFILED_RECORD_CONTAINER_PATH + path);
            if(folder != null)//if folder exists
            {
                unfiledFolderStructerFromExistentProvidedPaths.addAll(getUnfiledRecordFolders(folder));
            }
            else
            {
                //unfiledRecordFolder with specified path does not exist
            }
        }
        //add unfiled record folders from existent paths
        if(unfiledFolderStructerFromExistentProvidedPaths.size() > 0)
        {
            for(FolderData availableFolder : unfiledFolderStructerFromExistentProvidedPaths)
            {
                String availableFolderPath = availableFolder.getPath();
                if(allUnfiledParentPaths.contains(availableFolderPath))
                {
                    availableUnfiledRecordFolderPaths.add(availableFolderPath);
                }
            }
        }
        // configured paths did not existed in db and something went wrong with creation for all of them, initialize to existing structure in this case
        if(availableUnfiledRecordFolderPaths.size() == 0)
        {
            availableUnfiledRecordFolderPaths.addAll(allUnfiledParentPaths);
        }
        return availableUnfiledRecordFolderPaths;
    }

    /**
     * Helper method to obtain all parent paths for unfiled records present on db.
     *
     * @return all unfiled unfiled parent paths.
     */
    private Set<String> getAllUnfiledParentPaths()
    {
        List<RecordData> existingRecords = getAllUnfiledRecords();
        Set<String> existingPaths = new HashSet<>();
        for(RecordData record : existingRecords)
        {
            existingPaths.add(record.getParentPath());
        }
        return existingPaths;
    }
}
