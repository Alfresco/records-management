/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.po.rm;

import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.share.Navigation;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Extends {@link Navigation} to add RM specific methods
 *
 * @author Tuna Aksoy
 * @since 2.2
 */
public class RmNavigation extends Navigation
{

    private static final By FILE_PLAN_LINK = By.cssSelector("a[href$='rm/documentlibrary']");

    /**
     * Constructor.
     *
     * @param drone {@link WebDrone}
     */
    public RmNavigation(WebDrone drone)
    {
        super(drone);
    }

    /**
     * @return
     */
    public FilePlanPage selectFilePlan()
    {
        WebElement filePlanLink = drone.find(FILE_PLAN_LINK);
        filePlanLink.click();
        return new FilePlanPage(drone);
    }
    
}