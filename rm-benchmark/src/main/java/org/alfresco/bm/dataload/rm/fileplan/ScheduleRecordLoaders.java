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

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
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
 * Prepare event for loading records in filePlan folder structure.
 *
 * @author Silviu Dinuta
 * @since 2.6
 *
 */
public class ScheduleRecordLoaders extends RMBaseEventProcessor
{
    public static final String EVENT_NAME_LOAD_RECORDS = "loadRecord";
    public static final String EVENT_NAME_SCHEDULE_RECORD_LOADERS = "scheduleRecordLoaders";
    public static final String EVENT_NAME_LOADING_COMPLETE = "loadingRecordsComplete";

    @Autowired
    private SessionService sessionService;

    private int maxActiveLoaders;
    private long loadCheckDelay;
    private boolean uploadRecords;
    private int recordsNumber;
    private String recordFolderPaths;
    private List<String> paths;
    private String eventNameLoadRecords = EVENT_NAME_LOAD_RECORDS;
    private String eventNameScheduleRecordLoaders = EVENT_NAME_SCHEDULE_RECORD_LOADERS;
    private String eventNameLoadingComplete = EVENT_NAME_LOADING_COMPLETE;

    private LinkedHashMap<FolderData, Integer> mapOfRecordsPerRecordFolder = null;

    /**
     * @return the uploadRecords
     */
    public boolean isUploadRecords()
    {
        return uploadRecords;
    }

    /**
     * @param uploadRecords the uploadRecords to set
     */
    public void setUploadRecords(boolean uploadRecords)
    {
        this.uploadRecords = uploadRecords;
    }

    /**
     * @param recordFolderPaths the recordFolderPaths to set
     */
    public void setRecordFolderPaths(String recordFolderPaths)
    {
        this.recordFolderPaths = recordFolderPaths;
        if (isNotBlank(this.recordFolderPaths))
        {
            paths = Arrays.asList(this.recordFolderPaths.split(","));
        }
    }

    /**
     * @param recordsNumber the recordsNumber to set
     */
    public void setRecordsNumber(int recordsNumber)
    {
        this.recordsNumber = recordsNumber;
    }

    /**
     * @param loadCheckDelay the loadCheckDelay to set
     */
    public void setLoadCheckDelay(long loadCheckDelay)
    {
        this.loadCheckDelay = loadCheckDelay;
    }

    /**
     * @param maxActiveLoaders the maxActiveLoaders to set
     */
    public void setMaxActiveLoaders(int maxActiveLoaders)
    {
        this.maxActiveLoaders = maxActiveLoaders;
    }

    /**
     * @return the eventNameLoadRecords
     */
    public String getEventNameLoadRecords()
    {
        return eventNameLoadRecords;
    }

    /**
     * @param eventNameLoadRecords the eventNameLoadRecords to set
     */
    public void setEventNameLoadRecords(String eventNameLoadRecords)
    {
        this.eventNameLoadRecords = eventNameLoadRecords;
    }

    /**
     * @return the eventNameScheduleRecordLoaders
     */
    public String getEventNameScheduleRecordLoaders()
    {
        return eventNameScheduleRecordLoaders;
    }

    /**
     * @param eventNameScheduleRecordLoaders the eventNameScheduleRecordLoaders to set
     */
    public void setEventNameScheduleRecordLoaders(String eventNameScheduleRecordLoaders)
    {
        this.eventNameScheduleRecordLoaders = eventNameScheduleRecordLoaders;
    }

    /**
     * @return the eventNameLoadingComplete
     */
    public String getEventNameLoadingComplete()
    {
        return eventNameLoadingComplete;
    }

    /**
     * @param eventNameLoadingComplete the eventNameLoadingComplete to set
     */
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
        List<Event> nextEvents = new ArrayList<Event>(maxActiveLoaders);

        // Do we actually need to do anything
        if (!isUploadRecords())
        {
            return new EventResult("Uploading of records into File Plan not wanted, continue with loading data.",
                                   new Event(getEventNameLoadingComplete(), null));
        }
        if (recordsNumber > 0)
        {
            // Prepare Records
            prepareRecords(loaderSessionsToCreate, nextEvents);
        }

        // If there are no events, then we have finished
        String msg = null;
        if (loaderSessionsToCreate > 0 && nextEvents.isEmpty())
        {
            // There are no records to load even though there are sessions available
            mapOfRecordsPerRecordFolder = null;
            Event nextEvent = new Event(getEventNameLoadingComplete(), null);
            nextEvents.add(nextEvent);
            msg = "Loading completed.  Raising 'done' event.";
        }
        else
        {
            // Reschedule self
            Event nextEvent = new Event(getEventNameScheduleRecordLoaders(), System.currentTimeMillis() + loadCheckDelay,
                        null);
            nextEvents.add(nextEvent);
            msg = "Raised further " + (nextEvents.size() - 1) + " events and rescheduled self.";
        }

        if (eventProcessorLogger.isDebugEnabled())
        {
            eventProcessorLogger.debug(msg);
        }

        EventResult result = new EventResult(msg, nextEvents);
        return result;
    }

    /**
     * Helper method for preparing events for loading records randomly in the record folders structure or in specified record folder paths.
     *
     * @param loaderSessionsToCreate - the number of still active loader sessions
     * @param nextEvents - list of prepared events
     */
    private void prepareRecords(int loaderSessionsToCreate, List<Event> nextEvents)
    {
        mapOfRecordsPerRecordFolder = calculateListOfEmptyFolders(mapOfRecordsPerRecordFolder, paths, recordsNumber);
        List<FolderData> emptyFolders = (mapOfRecordsPerRecordFolder == null) ?
                    new ArrayList<>() :
                    new ArrayList<>(mapOfRecordsPerRecordFolder.keySet());
        while (nextEvents.size() < loaderSessionsToCreate)
        {
            if (mapOfRecordsPerRecordFolder == null || mapOfRecordsPerRecordFolder.isEmpty())
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
                        DBObject loadData = BasicDBObjectBuilder.start()
                                    .add(FIELD_CONTEXT, emptyFolder.getContext())
                                    .add(FIELD_PATH, emptyFolder.getPath())
                                    .add(FIELD_LOAD_OPERATION, LOAD_RECORD_OPERATION)
                                    .get();
                        int i;
                        for(i = 0; i < recordsToCreate; i++)
                        {
                            Event loadEvent = new Event(getEventNameLoadRecords(), loadData);
                            // Each load event must be associated with a session
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
                            mapOfRecordsPerRecordFolder.remove(emptyFolder);
                        }
                        else
                        {
                            mapOfRecordsPerRecordFolder.put(emptyFolder, mapOfRecordsPerRecordFolder.get(emptyFolder) - i-1);
                        }
                    }
                    catch (Exception e)
                    {
                        mapOfRecordsPerRecordFolder.remove(emptyFolder);
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