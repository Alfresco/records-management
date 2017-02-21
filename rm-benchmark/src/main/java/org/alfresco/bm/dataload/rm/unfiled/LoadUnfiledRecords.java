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
 * Unfiled records creation event
 *
 * @author Silviu Dinuta
 * @since 2.6
 *
 */
public class LoadUnfiledRecords extends RMBaseEventProcessor
{

    public static final String EVENT_NAME_UNFILED_RECORDS_LOADED = "unfiledRecordsLoaded";
    public static final long DEFAULT_LOAD_UNFILED_RECORD_DELAY = 100L;

    @Autowired
    private RestAPIFactory restAPIFactory;

    private String eventNameUnfiledRecordsLoaded = EVENT_NAME_UNFILED_RECORDS_LOADED;
    private long loadUnfiledRecordDelay = DEFAULT_LOAD_UNFILED_RECORD_DELAY;

    public String getEventNameUnfiledRecordsLoaded()
    {
        return eventNameUnfiledRecordsLoaded;
    }

    public void setEventNameUnfiledRecordsLoaded(String eventNameUnfiledRecordsLoaded)
    {
        this.eventNameUnfiledRecordsLoaded = eventNameUnfiledRecordsLoaded;
    }

    public long getLoadUnfiledRecordDelay()
    {
        return loadUnfiledRecordDelay;
    }

    public void setLoadUnfiledRecordDelay(long loadUnfiledRecordDelay)
    {
        this.loadUnfiledRecordDelay = loadUnfiledRecordDelay;
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
        Integer recordsToCreate = (Integer) dataObj.get(FIELD_RECORDS_TO_CREATE);
        if (context == null || path == null || recordsToCreate == null)
        {
            return new EventResult("Request data not complete for records loading: " + dataObj, false);
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

        return loadRecords(folder, recordsToCreate);
    }

    /**
     * Helper method that load specified numbers of unfiled records in specified unfiled record container of unfiled record folder.
     *
     * @param container - unfiled record container, or an unfiled record folder
     * @param recordsToCreate - number of records to create
     * @return EventResult - the loading result or error if there was an exception on loading
     * @throws IOException
     */
    private EventResult loadRecords(FolderData container, int recordsToCreate)
                throws IOException
    {
        UserData user = getRandomUser(logger);
        String username = user.getUsername();
        String password = user.getPassword();
        FilePlanComponentAPI api = restAPIFactory.getFilePlanComponentsAPI(new UserModel(username, password));
        try
        {
            List<Event> scheduleEvents = new ArrayList<Event>();
            FilePlanComponent filePlanComponent = api.getFilePlanComponent(container.getId());

            //Create records
            if(recordsToCreate > 0)
            {
                super.resumeTimer();
                //TODO uncomment this and remove createRecord when RM-4564 issue is fixed
                //uploadElectronicRecord(container, api, filePlanComponent, recordsToCreate, RECORD_NAME_IDENTIFIER, loadUnfiledRecordDelay);
                createRecord(container, api, filePlanComponent, recordsToCreate, RECORD_NAME_IDENTIFIER, loadUnfiledRecordDelay);
                super.suspendTimer();
                // Clean up the lock
                String lockedPath = container.getPath() + "/locked";
                fileFolderService.deleteFolder(container.getContext(), lockedPath, false);
            }

            DBObject eventData = BasicDBObjectBuilder.start()
                        .add(FIELD_CONTEXT, container.getContext())
                        .add(FIELD_PATH, container.getPath()).get();
           Event nextEvent = new Event(eventNameUnfiledRecordsLoaded, eventData);

           scheduleEvents.add(nextEvent);
            DBObject resultData = BasicDBObjectBuilder.start()
                        .add("msg", "Created " + recordsToCreate + " records.")
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