/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.po.rm.common;

import org.alfresco.po.rm.RmSiteDashBoardPage;
import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.LoginPage;

/**
 * Abstract Records Management test
 *
 * @author Roy Wetherall
 * @since 2.2
 */
public abstract class AbstractRecordsManagementTest extends AbstractTest
{
    /** RM site dashboard for logged in user */
    protected RmSiteDashBoardPage rmSiteDashBoard;

    /**
     * Helper method that logs into share and sets the dashboard PO
     *
     * @param userName  user name
     * @param password  password
     */
    protected void login(String userName, String password)
    {
        drone.navigateTo(shareUrl);
        LoginPage loginPage = new LoginPage(drone).render();

        loginPage.loginAs(username, password);
        rmSiteDashBoard = new RmSiteDashBoardPage(drone).render();
    }
}
