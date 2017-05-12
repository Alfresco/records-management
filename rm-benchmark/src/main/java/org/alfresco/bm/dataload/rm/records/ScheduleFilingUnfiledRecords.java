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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

import org.alfresco.bm.cm.FolderData;
import org.alfresco.bm.dataload.RMBaseEventProcessor;
import org.alfresco.bm.dataload.rm.services.ExecutionState;
import org.alfresco.bm.dataload.rm.services.RecordData;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.session.SessionService;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

/**
 * Prepare event for filing unfiled records
 *
 * @author Silviu Dinuta
 * @since 2.6
 */
public class ScheduleFilingUnfiledRecords extends RMBaseEventProcessor
{
    public static final String DONE_EVENT_MSG = "Filing completed.  Raising 'done' event.";
    public static final String FILING_UNFILED_RECORDS_NOT_WANTED_MSG = "Filing unfiled records not wanted.";
    private static final String DEFAULT_EVENT_NAME_RESCHEDULE_SELF = "scheduleFilingUnfiledRecords";
    private static final String DEFAULT_EVENT_NAME_FILE_UNFILED_RECORDS = "fileUnfiledRecords";
    private static final String DEFAULT_EVENT_NAME_COMPLETE = "filingUnfiledRecordsComplete";
    private boolean fileUnfiledRecords = false;
    private Integer maxActiveLoaders;
    private long loadCheckDelay;
    private List<String> fileToRecordFolderPaths;
    private List<String> fileFromUnfiledPaths;

    private Integer recordFilingLimit;
    private String eventNameFileUnfiledRecords = DEFAULT_EVENT_NAME_FILE_UNFILED_RECORDS;
    private String eventNameComplete = DEFAULT_EVENT_NAME_COMPLETE;
    private String eventNameRescheduleSelf = DEFAULT_EVENT_NAME_RESCHEDULE_SELF;

    private LinkedHashMap<FolderData, Integer> mapOfRecordsPerRecordFolder = null;

    @Autowired
    private SessionService sessionService;

    public void setFileUnfiledRecords(boolean fileUnfiledRecords)
    {
        this.fileUnfiledRecords = fileUnfiledRecords;
    }

    public void setMaxActiveLoaders(Integer maxActiveLoaders)
    {
        this.maxActiveLoaders = maxActiveLoaders;
    }

    public void setLoadCheckDelay(long loadCheckDelay)
    {
        this.loadCheckDelay = loadCheckDelay;
    }

    public void setFileToRecordFolderPaths(String fileToRecordFolderPathsStr)
    {
        if(isNotBlank(fileToRecordFolderPathsStr))
        {
            this.fileToRecordFolderPaths = Arrays.asList(fileToRecordFolderPathsStr.split(","));
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

    public void setFileFromUnfiledPaths(String fileFromUnfiledPathsStr)
    {
        if(isNotBlank(fileFromUnfiledPathsStr))
        {
            this.fileFromUnfiledPaths = Arrays.asList(fileFromUnfiledPathsStr.split(","));
        }
    }

    public void setEventNameFileUnfiledRecords(String eventNameFileUnfiledRecords)
    {
        this.eventNameFileUnfiledRecords = eventNameFileUnfiledRecords;
    }

    public String getEventNameFileUnfiledRecords()
    {
        return eventNameFileUnfiledRecords;
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
    protected EventResult processEvent(Event event) throws Exception
    {
        if (!fileUnfiledRecords)
        {
            return new EventResult(FILING_UNFILED_RECORDS_NOT_WANTED_MSG, new Event(getEventNameComplete(), null));
        }

        // Are there still sessions active?
        long sessionCount = sessionService.getActiveSessionsCount();
        int loaderSessionsToCreate = maxActiveLoaders - (int) sessionCount;
        List<Event> nextEvents = new ArrayList<Event>(maxActiveLoaders);

        if(recordFilingLimit >= 0)
        {
            //Prepare Records
            prepareUnfiledRecords(loaderSessionsToCreate, nextEvents);
        }

        // If there are no events, then we have finished
        String msg = null;
        if (loaderSessionsToCreate > 0 && nextEvents.size() == 0)
        {
            // There are no records to load even though there are sessions available
            mapOfRecordsPerRecordFolder = null;
            Event nextEvent = new Event(getEventNameComplete(), null);
            nextEvents.add(nextEvent);
            msg = DONE_EVENT_MSG;
        }
        else
        {
            // Reschedule self
            Event nextEvent = new Event(getEventNameRescheduleSelf(), System.currentTimeMillis() + loadCheckDelay, null);
            nextEvents.add(nextEvent);
            msg = "Raised further " + (nextEvents.size() - 1) + " events and rescheduled self.";
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(msg);
        }

        EventResult result = new EventResult(msg, nextEvents);
        return result;
    }

    /**
     * Helper method for preparing events for filing unfiled records randomly in the record folders structure or in specified record folder paths.
     *
     * @param loaderSessionsToCreate - the number of still active loader sessions
     * @param nextEvents - list of prepared events
     */
    private void prepareUnfiledRecords(int loaderSessionsToCreate, List<Event> nextEvents)
    {
        int numberOfRecords = recordFilingLimit;
        //initialiaze the number of records to file to all only once if the limit is set to 0
        if(mapOfRecordsPerRecordFolder == null && recordFilingLimit == 0)
        {
            List<String> listOfUnfiledRecordFoldersPaths = null;
            if(fileFromUnfiledPaths !=null && fileFromUnfiledPaths.size() > 0)
            {
                listOfUnfiledRecordFoldersPaths = getListOfUnfiledRecordFoldersPaths();
            }
            numberOfRecords = (int) recordService.getRecordCountInSpecifiedPaths(ExecutionState.UNFILED_RECORD_DECLARED.name(), listOfUnfiledRecordFoldersPaths);
        }
        //if the number of records to load is not greater than 0, nothing to file
        if(mapOfRecordsPerRecordFolder == null && numberOfRecords == 0)
        {
            return;
        }
        mapOfRecordsPerRecordFolder = calculateListOfEmptyFolders(mapOfRecordsPerRecordFolder, fileToRecordFolderPaths, numberOfRecords);
        List<FolderData> emptyFolders = new ArrayList<FolderData>();
        emptyFolders.addAll(mapOfRecordsPerRecordFolder.keySet());
        while (nextEvents.size() < loaderSessionsToCreate)
        {
            if (mapOfRecordsPerRecordFolder == null || mapOfRecordsPerRecordFolder.size() == 0)
            {
                break;
            }
            // Schedule a load for each folder
            for (FolderData emptyFolder : emptyFolders)
            {
                int recordsToCreate = mapOfRecordsPerRecordFolder.get(emptyFolder) - (int) emptyFolder.getFileCount();
                if (recordsToCreate <= 0)
                {
                    mapOfRecordsPerRecordFolder.remove(emptyFolder);
                }
                else
                {
                    try
                    {
                        List<String> recordIdsToLoad = getRecordIdsToLoad(recordsToCreate);
                        int availableRecordsToLoad = recordIdsToLoad.size();
                        // no other records found for filing, break here
                        if(availableRecordsToLoad == 0)
                        {
                            mapOfRecordsPerRecordFolder = null;
                            break;
                        }
                        // Create a lock folder that has too many files and folders so that it won't be picked up
                        // by this process in subsequent trawls
                        String lockPath = emptyFolder.getPath() + "/locked";
                        FolderData lockFolder = new FolderData(UUID.randomUUID().toString(), emptyFolder.getContext(),
                                    lockPath, Long.MAX_VALUE, Long.MAX_VALUE);
                        fileFolderService.createNewFolder(lockFolder);
                        // We locked this, so the load can be scheduled.
                        // The loader will remove the lock when it completes
                        DBObject loadData = BasicDBObjectBuilder.start().add(FIELD_CONTEXT, emptyFolder.getContext())
                                    .add(FIELD_PATH, emptyFolder.getPath())
                                    .add(FIELD_RECORDS_TO_FILE, recordIdsToLoad)
                                    .get();
                        Event loadEvent = new Event(getEventNameFileUnfiledRecords(), loadData);
                        // Each load event must be associated with a session
                        String sessionId = sessionService.startSession(loadData);
                        loadEvent.setSessionId(sessionId);
                        // Add the event to the list
                        nextEvents.add(loadEvent);
                        // found less than requested unfiled records for filing, break here, after the event for loading existing ones was scheduled for filing
                        if(availableRecordsToLoad > 0 && availableRecordsToLoad < recordsToCreate)
                        {
                            mapOfRecordsPerRecordFolder = null;
                            break;
                        }
                        mapOfRecordsPerRecordFolder.remove(emptyFolder);
                    }
                    catch (Exception e)
                    {
                        mapOfRecordsPerRecordFolder.remove(emptyFolder);
                        // The lock was already applied; find another
                        continue;
                    }
                }
                // Check if we have enough
                if (nextEvents.size() >= loaderSessionsToCreate)
                {
                    break;
                }
            }
        }
    }

    /**
     * Helper method for obtaining the id's of the random unfiled records to be filed in one record folder.
     *
     * @param recordsToFile the number of unfiled record to file in one folder
     * @return the list of id's of random unfiled records to file
     */
    private List<String> getRecordIdsToLoad(int recordsToFile)
    {
        List<String> unfiledRecordsIdsToFile = new ArrayList<>();
        List<String> listOfUnfiledRecordFoldersPaths = null;
        if(fileFromUnfiledPaths !=null && fileFromUnfiledPaths.size() > 0)
        {
            listOfUnfiledRecordFoldersPaths = getListOfUnfiledRecordFoldersPaths();
        }
        for (int i = 0; i < recordsToFile; i++)
        {
            RecordData randomRecord = recordService.getRandomRecord(ExecutionState.UNFILED_RECORD_DECLARED.name(), listOfUnfiledRecordFoldersPaths);

            if(randomRecord == null)
            {
                break;
            }
            unfiledRecordsIdsToFile.add(randomRecord.getId());
            randomRecord.setExecutionState(ExecutionState.UNFILED_RECORD_SCHEDULED_FOR_FILING);
            recordService.updateRecord(randomRecord);
        }
        return unfiledRecordsIdsToFile;
    }

    /**
     * Helper method for obtaining parent paths to file unfiled records from.
     *
     * @return all parent paths to file unfiled records from.
     */
    private List<String> getListOfUnfiledRecordFoldersPaths()
    {
        LinkedHashSet<String> allUnfiledParentPaths = getAllUnfiledParentPaths();
        List<String> availableUnfiledRecordFolderPaths = new ArrayList<>();
        LinkedHashSet<FolderData> unfiledFolderStructerFromExistentProvidedPaths = new LinkedHashSet<FolderData>();
        for(String path : fileFromUnfiledPaths)
        {
            if(path.equals("/"))
            {
                path = "";
            }
            else if(!path.startsWith("/"))
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
    private LinkedHashSet<String> getAllUnfiledParentPaths()
    {
        List<RecordData> existingRecords = getAllUnfiledRecords();
        LinkedHashSet<String> existingPaths = new LinkedHashSet<>();
        for(RecordData record : existingRecords)
        {
            existingPaths.add(record.getParentPath());
        }
        return existingPaths;
    }
}
