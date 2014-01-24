/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.po.share;

import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;

/**
 * Extends {@link Navigation} to add RM specific methods
 *
 * @author Tuna Aksoy
 * @since 2.2
 */
public class RMNavigation extends Navigation
{
    public RMNavigation(WebDrone drone)
    {
        super(drone);
    }

    /**
     * Mimics the action of selecting create site link.
     *
     * @return HtmlPage people finder page object
     */
    public RMCreateSitePage selectCreateSite()
    {
        selectSitesDropdown();
        String selector = dojoSupport ? "td#HEADER_SITES_MENU_CREATE_SITE_text" : "ul.create-site-menuitem>li>a";
        drone.find(By.cssSelector(selector)).click();
        return new RMCreateSitePage(drone);
    }
}