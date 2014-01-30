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
package org.alfresco.po.rm.util;

import org.alfresco.po.rm.RmDashBoardPage;
import org.alfresco.po.share.LoginPage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;

/**
 * Util class for the RM page objects
 *
 * @author Tuna Aksoy
 * @since 2.2
 */
public class RmPoUtils
{
    /**
     * Logs user into share.
     *
     * @param drone {@link WebDrone}
     * @param url {@link String} Share url
     * @param userInfo {@link String} username and password
     *
     * @return {@link HtmlPage} page response
     */
    public static HtmlPage loginAs(final WebDrone drone, final String url, final String ... userInfo)
    {
        RmUtils.checkMandotaryParam("drone", drone);
        RmUtils.checkMandotaryParam("url", url);
        RmUtils.checkMandotaryParam("userInfo", userInfo);

        drone.navigateTo(url);
        LoginPage loginPage = new LoginPage(drone).render();
        loginPage.loginAs(userInfo[0], userInfo[1]);

        return new RmDashBoardPage(drone);
    }
}