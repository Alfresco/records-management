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
package org.alfresco.po.rm.unit;

import org.alfresco.po.rm.RmConsolePage;
import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.functional.RmAbstractTest;
import org.alfresco.po.share.util.FailedTestListener;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.alfresco.po.rm.RmConsolePage.RmConsoleMenu;
import static org.alfresco.po.rm.RmConsoleUsersAndGroups.ADD_BUTTON;
import static org.alfresco.po.rm.RmConsoleUsersAndGroups.SystemRoles;

/**
 * Tests record management console page.
 *
 * @author Michael Suzuki
 * @version 1.7.1
 * @since 2.2
 */
@Listeners(FailedTestListener.class)
public class RmConsolePageTest extends RmAbstractTest
{
    @Test
    public void createPage()
    {
        FilePlanPage filePlanPage = (FilePlanPage) rmSiteDashBoard.selectFilePlan();
        filePlanPage.openRmConsolePage();
    }

    @Test (dependsOnMethods="createPage")
    public void verifyRmConsole()
    {
        for (RmConsoleMenu link : RmConsoleMenu.values())
        {
            Assert.assertTrue(isElementPresent(link.getLocator()));
        }
    }

    @Test (dependsOnMethods="verifyRmConsole")
    public void openUserAndRolesPage()
    {
        RmConsolePage consolePage = new RmConsolePage(drone).render();
        consolePage.openUsersAndGroupsPage();
        Assert.assertTrue(isElementPresent(ADD_BUTTON));
        for (SystemRoles link : SystemRoles.values()){
            Assert.assertTrue(isElementPresent(By.cssSelector("#role-" + link.getValue())));
        }
    }

}