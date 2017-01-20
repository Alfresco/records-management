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
 * Timed event that declares pre-scheduled records
 * 
 * @author Ana Bozianu
 * @since 2.6
 */
public class DeclareRecords extends RMBaseEventProcessor
{
    private String eventNameRecordsDeclared;

    public void setEventNameRecordsDeclared(String eventNameRecordsDeclared)
    {
        this.eventNameRecordsDeclared = eventNameRecordsDeclared;
    }

    @Override
    protected EventResult processEvent(Event event) throws Exception
    {
        StringBuilder eventOutputMsg = new StringBuilder("Declaring files as records: \n");

        return new EventResult(eventOutputMsg.toString(), new Event(eventNameRecordsDeclared, null));
    }

}
