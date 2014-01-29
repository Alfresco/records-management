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
package org.alfresco.po.rm;

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
    private static final String CREATE_SITE = "ul.create-site-menuitem>li>a";
    private static final String CREATE_SITE_DOJO_SUPPORT = "td#HEADER_SITES_MENU_CREATE_SITE_text";

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
     * Mimics the action of selecting create site link.
     *
     * @return {@link RmCreateSitePage} Returns records management create site page
     */
    public RmCreateSitePage selectCreateSite()
    {
        selectSitesDropdown();
        String selectorText = dojoSupport ? CREATE_SITE_DOJO_SUPPORT : CREATE_SITE;
        By selector = By.cssSelector(selectorText);
        WebElement selectorElement = drone.find(selector);
        selectorElement.click();
        return new RmCreateSitePage(drone);
    }
}