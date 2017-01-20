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

/**
 * Loader class that schedules the records declare event by creating the preconditions for {@link DeclareRecords} event.
 *  - checks the state of the system
 *  - creates the community site and uploads files to be declared as records (if they don't exist)
 *  - creates the declare record events in the benchmark database in the SCHEDULES state
 *
 * @author Ana Bozianu
 * @since 2.6
 */
public class ScheduleDeclareRecordLoaders extends RMBaseEventProcessor
{
    private String collabSiteId;
    private String collabSitePath;
    private String recordsToDeclare;

    private String eventNameDeclareRecords;

    public void setCollabSiteId(String collabSiteId)
    {
        this.collabSiteId = collabSiteId;
    }

    public void setCollabSitePath(String collabSitePath)
    {
        this.collabSitePath = collabSitePath;
    }

    public void setRecordsToDeclare(String recordsToDeclare)
    {
        this.recordsToDeclare = recordsToDeclare;
    }

    public void setEventNameDeclareRecords(String eventNameDeclareRecords)
    {
        this.eventNameDeclareRecords = eventNameDeclareRecords;
    }

    @Override
    protected EventResult processEvent(Event event) throws Exception
    {
        StringBuilder eventOutputMsg = new StringBuilder("Preparing files to declare: \n");

        // create collaboration site

        // upload files to collaboration site

        // schedule the created files to be declared as records

        return new EventResult(eventOutputMsg.toString(), new Event(eventNameDeclareRecords, null));
    }

}
