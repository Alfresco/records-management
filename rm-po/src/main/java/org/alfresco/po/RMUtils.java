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
package org.alfresco.po;

import org.alfresco.po.rm.RMDashBoardPage;
import org.alfresco.po.share.LoginPage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;

/**
 * Util class for the RM page objects
 *
 * @author Tuna Aksoy
 * @since 2.2
 */
public class RMUtils
{
    /**
     * Logs user into share.
     * @param drone {@link WebDrone}
     * @param url Share url
     * @param userInfo username and password
     * @return {@link HtmlPage} page response
     */
    public static HtmlPage loginAs(final WebDrone drone, final String url, final String ... userInfo)
    {
        drone.navigateTo(url);
        LoginPage lp = new LoginPage(drone).render();
        lp.loginAs(userInfo[0], userInfo[1]);
        return new RMDashBoardPage(drone);
    }
}
