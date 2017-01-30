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

import org.alfresco.bm.dataload.RMBaseEventProcessor;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.rest.core.RestAPIFactory;
import org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponent;
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
    @Autowired
    private RestAPIFactory restAPIFactory;

    private String eventNameInPlaceRecordsDeclared;

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

        FilePlanComponent record = restAPIFactory.getFilesAPI().declareAsRecord(id);

        String statusCode = restAPIFactory.getRmRestWrapper().getStatusCode();
        if(statusCode.equals(HttpStatus.OK))
        {
            eventOutputMsg.append("success");
        }
        else
        {
            eventOutputMsg.append("failed with code " + statusCode);
        }

        return new EventResult(eventOutputMsg.toString(), new Event(eventNameInPlaceRecordsDeclared, null));
    }

}
