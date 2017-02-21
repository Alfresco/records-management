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

import java.util.concurrent.TimeUnit;

import org.alfresco.bm.dataload.RMBaseEventProcessor;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.rest.core.RestAPIFactory;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.mongodb.DBObject;

/**
 * Timed event that declares as record pre-scheduled file
 * 
 * @author Ana Bozianu
 * @since 2.6
 */
public class DeclareInPlaceRecords extends RMBaseEventProcessor
{
    public static final long DEFAULT_DECLARE_RECORDS_DELAY = 10000L;
    private long declareInPlaceRecordDelay = DEFAULT_DECLARE_RECORDS_DELAY;
    private String eventNameInPlaceRecordsDeclared;

    @Autowired
    private RestAPIFactory restAPIFactory;

    public void setEventNameInPlaceRecordsDeclared(String eventNameInPlaceRecordsDeclared)
    {
        this.eventNameInPlaceRecordsDeclared = eventNameInPlaceRecordsDeclared;
    }

    @Override
    protected EventResult processEvent(Event event) throws Exception
    {
        StringBuilder eventOutputMsg = new StringBuilder("Declaring file as record: \n");

        super.suspendTimer();

        if (event == null)
        {
            throw new IllegalStateException("This processor requires an event.");
        }

        DBObject dataObj = (DBObject) event.getData();
        if (dataObj == null)
        {
            throw new IllegalStateException("This processor requires data with field " + FIELD_ID);
        }
        String id = (String) dataObj.get(FIELD_ID);
        String siteManager = (String) dataObj.get(FIELD_SITE_MANAGER);

        if (id == null)
        {
            throw new IllegalStateException("This processor requires data with field " + FIELD_ID);
        }

        // Call the REST API 
        super.resumeTimer();
        restAPIFactory.getFilesAPI(new UserModel(siteManager, siteManager)).declareAsRecord(id);
        String statusCode = restAPIFactory.getRmRestWrapper().getStatusCode();
        super.suspendTimer();
        TimeUnit.MILLISECONDS.sleep(declareInPlaceRecordDelay);

        if(HttpStatus.valueOf(Integer.parseInt(statusCode)) == HttpStatus.CREATED)
        {
            eventOutputMsg.append("success");
        }
        else
        {
            eventOutputMsg.append("Failed with code " + statusCode + ".\n " + 
                                   restAPIFactory.getRmRestWrapper().assertLastError().getBriefSummary() + ". \n" +
                                   restAPIFactory.getRmRestWrapper().assertLastError().getStackTrace());
        }

        return new EventResult(eventOutputMsg.toString(), new Event(eventNameInPlaceRecordsDeclared, null));
    }

}
