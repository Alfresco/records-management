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

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.split;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

import org.alfresco.bm.cm.FolderData;
import org.alfresco.bm.dataload.RMBaseEventProcessor;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.session.SessionService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Prepare event for loading unfiled records
 *
 * @author Silviu Dinuta
 * @since 2.6
 *
 */
public class ScheduleUnfiledRecordLoaders extends RMBaseEventProcessor
{
    public static final String EVENT_NAME_LOAD_UNFILED_RECORDS = "loadUnfiledRecord";
    public static final String EVENT_NAME_SCHEDULE_LOADERS = "scheduleUnfiledRecordLoaders";
    public static final String EVENT_NAME_LOADING_COMPLETE = "loadingUnfiledRecordsComplete";

    @Autowired
    private SessionService sessionService;

    private int maxActiveLoaders;
    private boolean uploadUnfiledRecords;
    private int unfiledRecordsNumber;
    private String unfiledRecordFolderPaths;
    private List<String> paths;
    private long loadCheckDelay;
    private String eventNameLoadUnfiledRecords = EVENT_NAME_LOAD_UNFILED_RECORDS;
    private String eventNameScheduleLoaders = EVENT_NAME_SCHEDULE_LOADERS;
    private String eventNameLoadingComplete = EVENT_NAME_LOADING_COMPLETE;
    private LinkedHashMap<FolderData, Integer> mapOfRecordsPerUnfiledRecordFolder = null;

    public void setMaxActiveLoaders(int maxActiveLoaders)
    {
        this.maxActiveLoaders = maxActiveLoaders;
    }

    public boolean isUploadUnfiledRecords()
    {
        return uploadUnfiledRecords;
    }

    public void setUploadUnfiledRecords(boolean uploadUnfiledRecords)
    {
        this.uploadUnfiledRecords = uploadUnfiledRecords;
    }

    public void setUnfiledRecordsNumber(int unfiledRecordsNumber)
    {
        this.unfiledRecordsNumber = unfiledRecordsNumber;
    }

    public void setUnfiledRecordFolderPaths(String unfiledRecordFolderPaths)
    {
        this.unfiledRecordFolderPaths = unfiledRecordFolderPaths;
        if(isNotBlank(this.unfiledRecordFolderPaths))
        {
            paths = Arrays.asList(this.unfiledRecordFolderPaths.split(","));
        }
    }

    public void setLoadCheckDelay(long loadCheckDelay)
    {
        this.loadCheckDelay = loadCheckDelay;
    }

    public String getEventNameLoadUnfiledRecords()
    {
        return eventNameLoadUnfiledRecords;
    }

    public void setEventNameLoadUnfiledRecords(String eventNameLoadUnfiledRecords)
    {
        this.eventNameLoadUnfiledRecords = eventNameLoadUnfiledRecords;
    }

    public String getEventNameScheduleLoaders()
    {
        return eventNameScheduleLoaders;
    }

    public void setEventNameScheduleLoaders(String eventNameScheduleLoaders)
    {
        this.eventNameScheduleLoaders = eventNameScheduleLoaders;
    }

    public String getEventNameLoadingComplete()
    {
        return eventNameLoadingComplete;
    }

    public void setEventNameLoadingComplete(String eventNameLoadingComplete)
    {
        this.eventNameLoadingComplete = eventNameLoadingComplete;
    }

    @Override
    protected EventResult processEvent(Event event) throws Exception
    {
        // Are there still sessions active?
        long sessionCount = sessionService.getActiveSessionsCount();
        int loaderSessionsToCreate = maxActiveLoaders - (int) sessionCount;
        List<Event> nextEvents = new ArrayList<>(maxActiveLoaders);

        // Do we actually need to do anything
        if (!isUploadUnfiledRecords())
        {
            return new EventResult("Uploading of Unfiled Records not wanted.", new Event(getEventNameLoadingComplete(), null));
        }
        if(unfiledRecordsNumber > 0)
        {
            //Prepare Records
            prepareUnfiledRecords(loaderSessionsToCreate, nextEvents);
        }

        // If there are no events, then we have finished
        String msg = null;
        if (loaderSessionsToCreate > 0 && nextEvents.isEmpty())
        {
            // There are no records to load even though there are sessions available
            mapOfRecordsPerUnfiledRecordFolder = null;
            Event nextEvent = new Event(getEventNameLoadingComplete(), null);
            nextEvents.add(nextEvent);
            msg = "Loading completed.  Raising 'done' event.";
        }
        else
        {
            // Reschedule self
            Event nextEvent = new Event(getEventNameScheduleLoaders(), System.currentTimeMillis() + loadCheckDelay, null);
            nextEvents.add(nextEvent);
            msg = "Raised further " + (nextEvents.size() - 1) + " events and rescheduled self.";
        }

        if (eventProcessorLogger.isDebugEnabled())
        {
            eventProcessorLogger.debug(msg);
        }

        return new EventResult(msg, nextEvents);
    }

    /**
     * Helper method that initialize the unfiled record folders that can receive loaded unfiled records.
     * This method, also calculates the number of records to  add to the initialized unfiled record folders.
     */
    private void calculateListOfEmptyFolders()
    {
        if(mapOfRecordsPerUnfiledRecordFolder == null)
        {
            mapOfRecordsPerUnfiledRecordFolder = new LinkedHashMap<>();
            List<FolderData> unfiledRecordFoldersThatNeedRecords = new ArrayList<>();
            if(paths == null || paths.isEmpty())
            {
                unfiledRecordFoldersThatNeedRecords.addAll(initialiseFoldersToExistingStructure(UNFILED_CONTEXT));
            }
            else
            {
                LinkedHashSet<FolderData> unfiledFolderStructerFromExistentProvidedPaths = new LinkedHashSet<>();
                for(String path : paths)
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
                        try
                        {
                            folder = createFolder(path);
                            unfiledRecordFoldersThatNeedRecords.add(folder);
                        }
                        catch(Exception e)
                        {
                            // something went wrong on creating current path structure, not all required paths will be created
                        }
                    }
                }
                //add unfiled record folders from existent paths
                if(!unfiledFolderStructerFromExistentProvidedPaths.isEmpty())
                {
                    unfiledRecordFoldersThatNeedRecords.addAll(unfiledFolderStructerFromExistentProvidedPaths);
                }
                // configured paths did not existed in db and something went wrong with creation for all of them, initialize to existing structure in this case
                if(unfiledRecordFoldersThatNeedRecords.isEmpty())
                {
                    unfiledRecordFoldersThatNeedRecords.addAll(initialiseFoldersToExistingStructure(UNFILED_CONTEXT));
                }
            }
           if(!unfiledRecordFoldersThatNeedRecords.isEmpty())
           {
               mapOfRecordsPerUnfiledRecordFolder = distributeNumberOfRecords(unfiledRecordFoldersThatNeedRecords, unfiledRecordsNumber);
           }
        }
    }

    /**
     * Helper method used for creating in alfresco repo and in mongo DB, unfiled record folders from configured path elements.
     *
     * @param path - path element
     * @return created unfiled record folder, or existent unfiled record folder, if it already created
     * @throws Exception
     */
    private FolderData createFolder(String path) throws Exception
    {
        //create inexistent elements from configured paths as admin
        List<String> pathElements = Arrays.asList(split(path, "/"));
        FolderData parentFolder = fileFolderService.getFolder(UNFILED_CONTEXT, UNFILED_RECORD_CONTAINER_PATH);
        // for(String pathElement: pathElements)
        int pathElementsLength = pathElements.size();
        for (int i = 0; i < pathElementsLength; i++)
        {
            String pathElement = pathElements.get(i);
            FolderData folder = fileFolderService.getFolder(UNFILED_CONTEXT, parentFolder.getPath() + "/" + pathElement);
            if(folder != null)
            {
                parentFolder = folder;
            }
            else
            {
                if(i == 0)
                {
                    //create root unfiled record folder
                    parentFolder = createRootUnfiledRecordFolderWithFixedName(parentFolder, pathElement);
                }
                else
                {
                    //create child unfiled record folder
                    parentFolder = createUnfiledRecordFolderWithFixedName(parentFolder, pathElement);
                }
            }
        }
        return parentFolder;
    }

    /**
     * Helper method for preparing events for loading unfiled records randomly in the unfiled record folders structure or in specified unfiled record folder paths.
     *
     * @param loaderSessionsToCreate - the number of still active loader sessions
     * @param nextEvents - list of prepared events
     */
    private void prepareUnfiledRecords(int loaderSessionsToCreate, List<Event> nextEvents)
    {
        calculateListOfEmptyFolders();
        List<FolderData> emptyFolders = (mapOfRecordsPerUnfiledRecordFolder == null) ?
                    new ArrayList<>() :
                    new ArrayList<>(mapOfRecordsPerUnfiledRecordFolder.keySet());
        while (nextEvents.size() < loaderSessionsToCreate)
        {
            if(mapOfRecordsPerUnfiledRecordFolder == null || mapOfRecordsPerUnfiledRecordFolder.isEmpty())
            {
                break;
            }
            // Schedule a load for each folder
            for (FolderData emptyFolder : emptyFolders)
            {
                int recordsToCreate = mapOfRecordsPerUnfiledRecordFolder.get(emptyFolder) - (int) emptyFolder.getFileCount();
                if(recordsToCreate <= 0)
                {
                    mapOfRecordsPerUnfiledRecordFolder.remove(emptyFolder);
                }
                else
                {
                    try
                    {
                        DBObject loadData = BasicDBObjectBuilder.start()
                                .add(FIELD_CONTEXT, emptyFolder.getContext())
                                .add(FIELD_PATH, emptyFolder.getPath())
                                .add(FIELD_LOAD_OPERATION, LOAD_UNFILED_RECORD_OPERATION)
                                .get();
                        int i;
                        for(i = 0; i < recordsToCreate; i++)
                        {
                            Event loadEvent = new Event(getEventNameLoadUnfiledRecords(), loadData);
                            String sessionId = sessionService.startSession(loadData);
                            loadEvent.setSessionId(sessionId);
                            // Add the event to the list
                            nextEvents.add(loadEvent);

                            // Check if we have enough
                            if (nextEvents.size() >= loaderSessionsToCreate)
                            {
                                break;
                            }
                        }
                        if (i == recordsToCreate)
                        {
                            mapOfRecordsPerUnfiledRecordFolder.remove(emptyFolder);
                        }
                        else
                        {
                            mapOfRecordsPerUnfiledRecordFolder.put(emptyFolder, mapOfRecordsPerUnfiledRecordFolder.get(emptyFolder) - i-1);
                        }
                    }
                    catch (Exception e)
                    {
                        mapOfRecordsPerUnfiledRecordFolder.remove(emptyFolder);
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
}