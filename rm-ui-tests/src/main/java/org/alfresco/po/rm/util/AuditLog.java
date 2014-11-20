/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.rm.util;

/**
 * Representation of Content Details that can be used while create/edit the content.
 * 
 * @author Shan Nagarajan
 * @since 1.6.1
 */
public class AuditLog
{

    private String timeStamp;
    private String user;
    private String event;
    private String identifier;
    private String type;
    private String location;

    public AuditLog(String timeStamp, String user, String event, String identifier,String type, String location)
    {
        this.timeStamp = timeStamp;
        this.user = user;
        this.event = event;
        this.identifier = identifier;
        this.type = type;
        this.location = location;
    }

 

    public String getTimeStamp()
    {
        return timeStamp;
    }



    public void setTimeStamp(String timeStamp)
    {
        this.timeStamp = timeStamp;
    }



    public String getUser()
    {
        return user;
    }



    public void setUser(String user)
    {
        this.user = user;
    }



    public String getEvent()
    {
        return event;
    }



    public void setEvent(String event)
    {
        this.event = event;
    }



    public String getIdentifier()
    {
        return identifier;
    }



    public void setIdentifier(String identifier)
    {
        this.identifier = identifier;
    }



    public String getType()
    {
        return type;
    }



    public void setType(String type)
    {
        this.type = type;
    }



    public String getLocation()
    {
        return location;
    }



    public void setLocation(String location)
    {
        this.location = location;
    }
}
