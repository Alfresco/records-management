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
package org.alfresco.po.rm.regression.managerules;

import static org.alfresco.po.rm.RmConsoleUsersAndGroups.*;
import static org.alfresco.po.rm.RmSiteDashBoardPage.*;

import org.alfresco.po.rm.RmConsolePage;
import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.fileplan.filter.FilePlanFilter;
import org.alfresco.po.rm.fileplan.filter.unfiledrecords.UnfiledRecordsContainer;
import org.alfresco.po.rm.functional.RmAbstractTest;
import org.alfresco.po.rm.util.RmPageObjectUtils;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.util.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Sanity Tests
 *
 * @author Polina Lushchinskaya
 * @version 1.0
 */
@Listeners(FailedTestListener.class)
public class SanityTests extends RmAbstractTest
{

    /**
     * Executed before class
     */
    @BeforeClass
    public void doSetup()
    {
        setup();
        loadProperties("rm_en.properties");
    }

    /**
     * Executed after class
     */
    @Override
    @AfterClass
    public void doTeardown()
    {
        ShareUtil.logout(drone);
        login();
        deleteRMSite();
    }

    @Test
    public void RMA_2664()
    {
        String testName = Thread.currentThread().getStackTrace()[1].getMethodName().replace("_", "-");
        String userName = testName + RmPageObjectUtils.getRandomString(3);

        try
        {
            ShareUtil.logout(drone);
            login();
            deleteRMSite();
            ShareUtil.logout(drone);
            CreateUser(userName);
            login(userName, DEFAULT_USER_PASSWORD);
            createRMSite();
            FilePlanPage filePlan = openRmSite();
            Assert.assertTrue(getText(NAVIGATION_MENU_FILE_PLAN).contains(getPropertyValue("rm.file.plan.link")),
                "Failed to present File Plan link on RM Site Dashboard Page");
            Assert.assertTrue(getText(NAVIGATION_MENU_RECORDS_SEARCH).contains(getPropertyValue("rm.records.search.link")),
                "Failed to present Records Search link on RM Site Dashboard Page");
            Assert.assertTrue(getText(NAVIGATION_MENU_SITE_DASHBOARD).contains(getPropertyValue("rm.site.dashboard.link")),
                "Failed to present Site Dashboard link on RM Site Dashboard Page");
            Assert.assertTrue(getText(NAVIGATION_MENU_SITE_MEMBERS).contains(getPropertyValue("rm.site.members.link")),
                "Failed to present Site Members link on RM Site Dashboard Page");
            Assert.assertTrue(getText(NAVIGATION_MENU_RM_CONSOLE).contains(getPropertyValue("rm.records.management.console.link")),
                "Failed to present RM Console link on RM Site Dashboard Page");
            // Navigate to RM Console > Users and Groups
            RmConsolePage consolePage= filePlan.openRmConsolePage();
            consolePage.openUsersAndGroupsPage();
            selectGroup(drone, SystemRoles.RECORDS_MANAGEMENT_ADMINISTRATOR.getValue());
            Assert.assertTrue(isElementPresent(userLinkRmConsole(userName)),
                "There is no user that create RM site in RM admin group");
            Assert.assertTrue(isElementPresent(userLinkRmConsole("rmadmin")),
                "There is no rmadmin in RM admin group");

        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally {
            ShareUtil.logout(drone);
            login(userName, DEFAULT_USER_PASSWORD);
            deleteRMSite();
        }
    }

    @Test
    public void RMA_2665()
    {
        String testName = Thread.currentThread().getStackTrace()[1].getMethodName().replace("_", "-");
        String userName = testName + RmPageObjectUtils.getRandomString(3);
        String siteName = RmPageObjectUtils.getRandomString(5);
        String categoryName = testName + RmPageObjectUtils.getRandomString(3);
        String subCategoryName = testName + RmPageObjectUtils.getRandomString(3);
        String folderName = testName + RmPageObjectUtils.getRandomString(3);
        String folderName1 = testName + RmPageObjectUtils.getRandomString(3);
        String electronicRecord = testName + RmPageObjectUtils.getRandomString(3);
        String nonElectronicRecord = testName + RmPageObjectUtils.getRandomString(3);
        String fileName = testName + RmPageObjectUtils.getRandomString(3);

        try
        {
            ShareUtil.logout(drone);
            login();
            deleteRMSite();
            ShareUtil.logout(drone);
            CreateUser(userName);
            login(userName, DEFAULT_USER_PASSWORD);
            createRMSite();
            FilePlanPage filePlan = openRmSite();
            filePlan.createCategory(categoryName, true);
            Assert.assertTrue(isElementPresent(commonLine(categoryName)),
                "Failed to present category in File plan");
            filePlan = openRmSite();
            filePlan.navigateToFolder(categoryName);
            filePlan.createCategory(subCategoryName, false);
            Assert.assertTrue(isElementPresent(commonLine(subCategoryName)),
                "Failed to present subCategory in File plan");
            filePlan.navigateToFolder(subCategoryName);
            filePlan.createFolder(folderName);
            Assert.assertTrue(isElementPresent(commonLine(folderName)),
                "Failed to present Folder in File plan");
            filePlan.createFolder(folderName1);
            Assert.assertTrue(isElementPresent(commonLine(folderName1)),
                "Failed to present Folder1 in File plan");
            filePlan.navigateToFolder(folderName);
            filePlan.createRecord(nonElectronicRecord);
            Assert.assertTrue(isElementPresent(commonLine(nonElectronicRecord)),
                "Failed to present Non-Electronic Record in File plan");
            filePlan = (FilePlanPage) rmSiteDashBoard.selectFilePlan();
            filePlan.navigateToFolder(categoryName);
            filePlan.navigateToFolder(subCategoryName);
            filePlan.navigateToFolder(folderName1);
            fileElectronicToRecordFolder(electronicRecord);
            Assert.assertTrue(isElementPresent(commonLine(electronicRecord)),
                "Failed to present Electronic Record in File plan");

            //Any Collaboration (public) site is created
            createCollaborationSite(siteName);
            //Any Content Created in Collaboration Site
            createContentAndDeclareAsRecord(siteName, fileName);

            filePlan = (FilePlanPage) rmSiteDashBoard.selectFilePlan();
            FilePlanFilter filePlanFilter = filePlan.getFilePlanFilter();
            filePlanFilter.selectUnfiledRecordsContainer().render();
            Assert.assertTrue(isElementPresent(commonLine(fileName)),
                "Failed to present Unfiled Record in UnFiled filter");
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally {
            ShareUtil.logout(drone);
            login(userName, DEFAULT_USER_PASSWORD);
            deleteRMSite();
        }
    }
}
