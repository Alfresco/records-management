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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.bm.cm.FolderData;
import org.alfresco.bm.dataload.RMBaseEventProcessor;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.session.SessionService;
import org.springframework.beans.factory.annotation.Autowired;

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
    public static final String DONE_EVENT_MSG = "Filing completed.  Raising 'done' event.";
    public static final String EVENT_NAME_FILE_UNFILED_RECORD= "fileUnfiledRecord";
    public static final long DEFAULT_FILE_UNFILED_RECORD_DELAY = 100L;
    private long fileUnfiledRecordDelay = DEFAULT_FILE_UNFILED_RECORD_DELAY;
    String eventNameFileUnfiledRecord = EVENT_NAME_FILE_UNFILED_RECORD;
    private static final String DEFAULT_EVENT_NAME_RESCHEDULE_SELF = "fileUnfiledRecords";
    private static final String DEFAULT_EVENT_NAME_COMPLETE = "unfiledRecordsFiled";
    private String eventNameComplete = DEFAULT_EVENT_NAME_COMPLETE;
    private String eventNameRescheduleSelf = DEFAULT_EVENT_NAME_RESCHEDULE_SELF;
    private Integer maxActiveLoaders;

    @Autowired
    private SessionService sessionService;

    public void setFileUnfiledRecordDelay(long fileUnfiledRecordDelay)
    {
        this.fileUnfiledRecordDelay = fileUnfiledRecordDelay;
    }

    public String getEventNameFileUnfiledRecord()
    {
        return eventNameFileUnfiledRecord;
    }

    public void setEventNameFileUnfiledRecord(String eventNameFileUnfiledRecord)
    {
        this.eventNameFileUnfiledRecord = eventNameFileUnfiledRecord;
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

    public void setMaxActiveLoaders(Integer maxActiveLoaders)
    {
        this.maxActiveLoaders = maxActiveLoaders;
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

        @SuppressWarnings("unchecked")
        List<String> recordsToFileIds = (List<String>) dataObj.get(FIELD_RECORDS_TO_FILE);
        if (context == null || path == null || recordsToFileIds == null)
        {
            return new EventResult("Request data not complete for filing unfiled records: " + dataObj, false);
        }

        // Get the folder
        FolderData folder = fileFolderService.getFolder(context, path);
        if (folder == null)
        {
            throw new IllegalStateException("No such folder recorded: " + dataObj);
        }

        long sessionCount = sessionService.getActiveSessionsCount();
        int loaderSessionsToCreate = maxActiveLoaders - (int) sessionCount;
        List<Event> nextEvents = new ArrayList<Event>(maxActiveLoaders);

        fileRecords(loaderSessionsToCreate, nextEvents, folder, recordsToFileIds);
        // If there are no events, then we have finished
        String msg = null;
        if (loaderSessionsToCreate > 0 && nextEvents.size() == 0)
        {
            // There are no records to load even though there are sessions available

            // Clean up the lock
            String lockedPath = folder.getPath() + "/locked";
            fileFolderService.deleteFolder(folder.getContext(), lockedPath, false);

            Event nextEvent = new Event(getEventNameComplete(), null);
            nextEvents.add(nextEvent);
            msg = DONE_EVENT_MSG;
        }
        else
        {
            // Reschedule self
            DBObject dbObj = BasicDBObjectBuilder.start().add(FIELD_CONTEXT, context)
                        .add(FIELD_PATH, path)
                        .add(FIELD_RECORDS_TO_FILE, recordsToFileIds)
                        .get();
            Event nextEvent = new Event(getEventNameRescheduleSelf(), System.currentTimeMillis() + fileUnfiledRecordDelay, dbObj);
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
     * Helper method that file specified numbers of records in specified record folder.
     *
     * @param loaderSessionsToCreate - the number of still active loader sessions
     * @param nextEvents - list of prepared events
     * @param container - record folder
     * @param recordsToFileIds - list of the id's of unfiled records to file
     * @throws IOException
     */
    private void fileRecords(int loaderSessionsToCreate, List<Event> nextEvents, FolderData container, List<String> recordsToFileIds) throws IOException
    {
        List<String> recordIds = new ArrayList<String>();
        recordIds.addAll(recordsToFileIds);
        while (nextEvents.size() < loaderSessionsToCreate)
        {
            if(recordsToFileIds.size() == 0)
            {
                break;
            }
            for (String recordId : recordIds)
            {
                try
                {
                    DBObject eventData = BasicDBObjectBuilder.start()
                                .add(FIELD_CONTEXT, container.getContext())
                                .add(FIELD_PATH, container.getPath())
                                .add(FIELD_LOAD_OPERATION, FILE_RECORD_OPERATION)
                                .add(FIELD_RECORD_ID, recordId)
                                .get();
                    Event nextEvent = new Event(getEventNameFileUnfiledRecord(), eventData);
                    // Each load event must be associated with a session
                    String sessionId = sessionService.startSession(eventData);
                    nextEvent.setSessionId(sessionId);
                    // Add the event to the list
                    nextEvents.add(nextEvent);
                    recordsToFileIds.remove(recordId);
                }
                catch (Exception e)
                {
                    recordsToFileIds.remove(recordId);
                    continue;
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
