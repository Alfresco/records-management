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
package org.alfresco.po.rm.regression.managerules.v1;

import static org.alfresco.po.rm.RmConsoleUsersAndGroups.selectGroup;
import static org.alfresco.po.rm.RmConsoleUsersAndGroups.userLinkRmConsole;
import static org.alfresco.po.rm.RmSiteDashBoardPage.NAVIGATION_MENU_FILE_PLAN;
import static org.alfresco.po.rm.RmSiteDashBoardPage.NAVIGATION_MENU_RECORDS_SEARCH;
import static org.alfresco.po.rm.RmSiteDashBoardPage.NAVIGATION_MENU_RM_CONSOLE;
import static org.alfresco.po.rm.RmSiteDashBoardPage.NAVIGATION_MENU_SITE_DASHBOARD;
import static org.alfresco.po.rm.RmSiteDashBoardPage.NAVIGATION_MENU_SITE_MEMBERS;
import static org.alfresco.po.rm.fileplan.RecordDetailsPage.ACTIONS;
import static org.alfresco.po.rm.fileplan.RecordDetailsPage.COPY_MOVE_TO_BUTTON;
import static org.alfresco.po.rm.fileplan.RecordDetailsPage.EDIT_METADATA_BOX_INPUT;
import static org.alfresco.po.rm.fileplan.RecordDetailsPage.EDIT_METADATA_DESCRIPTION_INPUT;
import static org.alfresco.po.rm.fileplan.RecordDetailsPage.EDIT_METADATA_FILE_INPUT;
import static org.alfresco.po.rm.fileplan.RecordDetailsPage.EDIT_METADATA_FORM;
import static org.alfresco.po.rm.fileplan.RecordDetailsPage.EDIT_METADATA_NAME_INPUT;
import static org.alfresco.po.rm.fileplan.RecordDetailsPage.EDIT_METADATA_SHELF_INPUT;
import static org.alfresco.po.rm.fileplan.RecordDetailsPage.EDIT_METADATA_STORAGE_LOCATION;
import static org.alfresco.po.rm.fileplan.RecordDetailsPage.EDIT_METADATA_TITLE_INPUT;
import static org.alfresco.po.rm.fileplan.RecordDetailsPage.EDIT_NUMBER_OF_COPIES_INPUT;
import static org.alfresco.po.rm.fileplan.RecordDetailsPage.EDIT_PHYSICAL_SIZE_INPUT;
import static org.alfresco.po.rm.fileplan.RecordDetailsPage.EVENTS;
import static org.alfresco.po.rm.fileplan.RecordDetailsPage.PROPERTIES;
import static org.alfresco.po.rm.fileplan.RecordDetailsPage.PROPERTY_SET_HEADER;
import static org.alfresco.po.rm.fileplan.RecordDetailsPage.REFERENCES;
import static org.alfresco.po.rm.fileplan.RecordDetailsPage.RM_ADD_TO_HOLD_LINK;
import static org.alfresco.po.rm.fileplan.RecordDetailsPage.RM_COMPLETE_RECORD;
import static org.alfresco.po.rm.fileplan.RecordDetailsPage.RM_COPY_TO_LINK;
import static org.alfresco.po.rm.fileplan.RecordDetailsPage.RM_DELETE_LINK;
import static org.alfresco.po.rm.fileplan.RecordDetailsPage.RM_EDIT_META_DATA_LINK;
import static org.alfresco.po.rm.fileplan.RecordDetailsPage.RM_LINK_TO_LINK;
import static org.alfresco.po.rm.fileplan.RecordDetailsPage.RM_MOVE_TO_LINK;
import static org.alfresco.po.rm.fileplan.RecordDetailsPage.RM_REMOVE_FROM_HOLD_LINK;
import static org.alfresco.po.rm.fileplan.RecordDetailsPage.RM_REQUEST_INFORMATION_LINK;
import static org.alfresco.po.rm.fileplan.RecordDetailsPage.RM_VIEW_AUDIT_LOG_LINK;
import static org.alfresco.po.rm.fileplan.RecordDetailsPage.SAVE_BUTTON;
import static org.alfresco.po.rm.fileplan.RecordDetailsPage.auditLabelsValue;
import static org.alfresco.po.rm.fileplan.RecordDetailsPage.switchToAuditLog;
import static org.alfresco.po.rm.fileplan.RecordDetailsPage.switchToDetailsPage;

import java.util.Arrays;

import org.alfresco.po.rm.RmConsolePage;
import org.alfresco.po.rm.RmConsoleUsersAndGroups.SystemRoles;
import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.fileplan.RecordDetailsPage;
import org.alfresco.po.rm.fileplan.filter.FilePlanFilter;
import org.alfresco.po.rm.fileplan.filter.hold.HoldsContainer;
import org.alfresco.po.rm.util.RmPageObjectUtils;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.webdrone.WebDroneUtil;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Listeners;

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
  // @BeforeClass
    public void doSetup()
    {
        pageObjectUtils.loadProperties("rm_en.properties");
        login();
        deleteRMSite();
        ShareUtil.logout(drone);
    }

    /**
     * Executed after class
     */
    @Override
 //   @AfterClass
    public void doTeardown()
    {
        ShareUtil.logout(drone);
        login();
        deleteRMSite();
    }

  //  @Test
    public void RMA_2664() throws Throwable
    {
        String testName = this.testName.replace("_", "-");
        String userName = testName + RmPageObjectUtils.getRandomString(3);

        try
        {
            createEnterpriseUser(userName);
            login(userName, DEFAULT_USER_PASSWORD);
            createRMSite();
            FilePlanPage filePlan = openRmSite();
            Assert.assertTrue(getText(NAVIGATION_MENU_FILE_PLAN).contains(pageObjectUtils.getPropertyValue("rm.file.plan.link")),
                "Failed to present File Plan link on RM Site Dashboard Page");
            Assert.assertTrue(getText(NAVIGATION_MENU_RECORDS_SEARCH).contains(pageObjectUtils.getPropertyValue("rm.records.search.link")),
                "Failed to present Records Search link on RM Site Dashboard Page");
            Assert.assertTrue(getText(NAVIGATION_MENU_SITE_DASHBOARD).contains(pageObjectUtils.getPropertyValue("rm.site.dashboard.link")),
                "Failed to present Site Dashboard link on RM Site Dashboard Page");
            Assert.assertTrue(getText(NAVIGATION_MENU_SITE_MEMBERS).contains(pageObjectUtils.getPropertyValue("rm.site.members.link")),
                "Failed to present Site Members link on RM Site Dashboard Page");
            Assert.assertTrue(getText(NAVIGATION_MENU_RM_CONSOLE).contains(pageObjectUtils.getPropertyValue("rm.records.management.console.link")),
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
            reportError(testName, e);
        }
        finally 
        {
            ShareUtil.logout(drone);
            login(userName, DEFAULT_USER_PASSWORD);
            deleteRMSite();
        }
    }

  //  @Test
    public void RMA_2665() throws Throwable
    {
        String testName = this.testName.replace("_", "-");
        String userName = testName + RmPageObjectUtils.getRandomString(3);
        String siteName = RmPageObjectUtils.getRandomString(5);
        String categoryName = testName + RmPageObjectUtils.getRandomString(3);
        String subCategoryName = testName + RmPageObjectUtils.getRandomString(3);
        String folderName = testName + RmPageObjectUtils.getRandomString(3);
        String folderName1 = testName + RmPageObjectUtils.getRandomString(3);
        String electronicRecord = testName + RmPageObjectUtils.getRandomString(3);
        String nonElectronicRecord = testName + RmPageObjectUtils.getRandomString(3);
        String fileName = testName + RmPageObjectUtils.getRandomString(3);
        String holdName = testName + RmPageObjectUtils.getRandomString(3);

        try
        {
            createEnterpriseUser(userName);
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

            filePlanFilter.selectHoldsContainer().render();
            HoldsContainer.createNewHold(drone, holdName, holdName);
            Assert.assertTrue(isElementPresent(commonLine(holdName)),
                "Failed to create hold container");
        }
        catch (Throwable e)
        {
            reportError(testName, e);
        }
        finally 
        {
            ShareUtil.logout(drone);
            login(userName, DEFAULT_USER_PASSWORD);
            deleteRMSite();
        }
    }

    /**
     * RMA-2666: Manage Incomplete Records
     */
    //TODO Refactor
    //@Test
    public void RMA_2666() throws Throwable
    {
        String testName = this.testName.replace("_", "-");
        String userName = testName + RmPageObjectUtils.getRandomString(3);
        String categoryName = testName + RmPageObjectUtils.getRandomString(3);
        String folderName = testName + RmPageObjectUtils.getRandomString(3);
        String folderName1 = testName + RmPageObjectUtils.getRandomString(3);
        String electronicRecord = testName + RmPageObjectUtils.getRandomString(3);
        String nonElectronicRecord = testName + RmPageObjectUtils.getRandomString(3);
        String holdName = testName + RmPageObjectUtils.getRandomString(3);

        try
        {
            createEnterpriseUser(userName);
            login(userName, DEFAULT_USER_PASSWORD);
            createRMSite();
            FilePlanPage filePlan = openRmSite();
            filePlan.createCategory(categoryName, true);
            Assert.assertTrue(isElementPresent(commonLine(categoryName)), "Failed to present category in File plan");
            filePlan.navigateToFolder(categoryName);
            filePlan.createFolder(folderName);
            Assert.assertTrue(isElementPresent(commonLine(folderName)), "Failed to present Folder in File plan");
            filePlan.createFolder(folderName1);
            Assert.assertTrue(isElementPresent(commonLine(folderName1)), "Failed to present Folder1 in File plan");
            filePlan.navigateToFolder(folderName1);
            fileElectronicToRecordFolder(electronicRecord);
            Assert.assertTrue(isElementPresent(commonLine(electronicRecord)), "Failed to present Electronic Record in File plan");

            FilePlanFilter filePlanFilter = filePlan.getFilePlanFilter();
            filePlanFilter.selectHoldsContainer().render();
            HoldsContainer.createNewHold(drone, holdName, holdName);

            filePlan = (FilePlanPage) rmSiteDashBoard.selectFilePlan();
            //filePlan.Navigate(categoryName, folderName);
            filePlan.navigateToFolder(categoryName);
            filePlan.navigateToFolder(folderName);
            filePlan.createRecord(nonElectronicRecord);
            Assert.assertTrue(isElementPresent(commonLine(nonElectronicRecord)), "Failed to present Non-Electronic Record in File plan");

            //Verify Actions Available for nonElectronic Record
            RecordDetailsPage detailsPage = filePlan.openRecordDetailsPage(nonElectronicRecord);
            drone.isRenderComplete(MAX_WAIT_TIME);
            Assert.assertTrue(getText(RM_EDIT_META_DATA_LINK).contains(pageObjectUtils.getPropertyValue("record.edit.metadata.link")),
                "Failed to present Edit Metadata link on Records Details Page");
            Assert.assertTrue(getText(RM_COMPLETE_RECORD).contains(pageObjectUtils.getPropertyValue("record.complete.record.link")),
                "Failed to present Edit Metadata link on Records Details Page");
            Assert.assertTrue(getText(RM_COPY_TO_LINK).contains(pageObjectUtils.getPropertyValue("record.copy.to.link")),
                "Failed to present Edit Metadata link on Records Details Page");
            Assert.assertTrue(getText(RM_MOVE_TO_LINK).contains(pageObjectUtils.getPropertyValue("record.move.to.link")),
                "Failed to present Edit Metadata link on Records Details Page");
            Assert.assertTrue(getText(RM_LINK_TO_LINK).contains(pageObjectUtils.getPropertyValue("record.link.to.link")),
                "Failed to present Edit Metadata link on Records Details Page");
            Assert.assertTrue(getText(RM_DELETE_LINK).contains(pageObjectUtils.getPropertyValue("record.delete.link")),
                "Failed to present Edit Metadata link on Records Details Page");
            Assert.assertTrue(getText(RM_VIEW_AUDIT_LOG_LINK).contains(pageObjectUtils.getPropertyValue("record.view.audit.log.link")),
                "Failed to present Edit Metadata link on Records Details Page");
            Assert.assertTrue(getText(RM_ADD_TO_HOLD_LINK).contains(pageObjectUtils.getPropertyValue("record.add.to.hold.link")),
                "Failed to present Edit Metadata link on Records Details Page");
            Assert.assertTrue(getText(RM_REQUEST_INFORMATION_LINK).contains(pageObjectUtils.getPropertyValue("record.request.information.link")),
                "Failed to present Edit Metadata link on Records Details Page");
            Assert.assertTrue(isElementPresent(PROPERTY_SET_HEADER));

            Assert.assertTrue(isElementPresent(PROPERTIES));
            Assert.assertTrue(isElementPresent(ACTIONS));
            Assert.assertTrue(isElementPresent(REFERENCES));
            Assert.assertTrue(isElementPresent(EVENTS));

            //Verify Metadata Page
            detailsPage.openEditMetadataPage();

            Assert.assertTrue(isElementPresent(EDIT_METADATA_FORM),  "Failed to Present Edit Metadata form");
            Assert.assertTrue(isElementPresent(EDIT_METADATA_NAME_INPUT), "Failed to present name input");
            Assert.assertTrue(isElementPresent(EDIT_METADATA_TITLE_INPUT), "Failed to present title input");
            Assert.assertTrue(isElementPresent(EDIT_METADATA_DESCRIPTION_INPUT), "Failed to present description input");
            Assert.assertTrue(isElementPresent(EDIT_PHYSICAL_SIZE_INPUT), "Failed to present physical size input");
            Assert.assertTrue(isElementPresent(EDIT_NUMBER_OF_COPIES_INPUT), "Failed to present number of copies input");
            Assert.assertTrue(isElementPresent(EDIT_METADATA_STORAGE_LOCATION), "Failed to present storage location input");
            Assert.assertTrue(isElementPresent(EDIT_METADATA_SHELF_INPUT), "Failed to present shelf input");
            Assert.assertTrue(isElementPresent(EDIT_METADATA_BOX_INPUT), "Failed to present box input");
            Assert.assertTrue(isElementPresent(EDIT_METADATA_FILE_INPUT), "Failed to present file input");

            Assert.assertTrue(isEditable(EDIT_METADATA_NAME_INPUT));
            Assert.assertTrue(isEditable(EDIT_METADATA_TITLE_INPUT));
            Assert.assertTrue(isEditable(EDIT_METADATA_DESCRIPTION_INPUT));
            Assert.assertTrue(isEditable(EDIT_PHYSICAL_SIZE_INPUT));
            Assert.assertTrue(isEditable(EDIT_NUMBER_OF_COPIES_INPUT));
            Assert.assertTrue(isEditable(EDIT_METADATA_STORAGE_LOCATION));
            Assert.assertTrue(isEditable(EDIT_METADATA_SHELF_INPUT));
            Assert.assertTrue(isEditable(EDIT_METADATA_BOX_INPUT));
            Assert.assertTrue(isEditable(EDIT_METADATA_FILE_INPUT));

            type(EDIT_METADATA_DESCRIPTION_INPUT, testName);
            click(SAVE_BUTTON);
            detailsPage = (RecordDetailsPage) drone.getCurrentPage();
            drone.isRenderComplete(MAX_WAIT_TIME);
            //Copy to Folder2
            click(RM_COPY_TO_LINK);
            drone.findAndWait(By.cssSelector("div[id$=dialog] h2"));
            click(commonLink(pageObjectUtils.getPropertyValue("rm.file.plan.link")));
            drone.findAndWait(commonLink(categoryName), MAX_WAIT_TIME);
            click(commonLink(categoryName));
            drone.findAndWait(commonLink(folderName1), MAX_WAIT_TIME);
            click(commonLink(folderName1));
            click(COPY_MOVE_TO_BUTTON);
            waitUntilCreatedAlert();
            //click audit log
            click(RM_VIEW_AUDIT_LOG_LINK);
            switchToAuditLog(drone);
            Assert.assertTrue(isElementPresent(auditLabelsValue(pageObjectUtils.getPropertyValue("audit.event.label"),
                pageObjectUtils.getPropertyValue("audit.updated.metadata.label"))));
            Assert.assertTrue(isElementPresent(auditLabelsValue(pageObjectUtils.getPropertyValue("audit.event.label"),
                pageObjectUtils.getPropertyValue("audit.created.object.label"))));
            //Verify that audit does not contains unusefull information
//            int auditLines = drone.findAndWaitForElements(AUDIT_SECTIONS).size();
//            Assert.assertEquals(auditLines, 3, "RM-979: Incorrect audit events when creation non-electronic records");
            drone.closeWindow();
            switchToDetailsPage(drone);
            addToHold(holdName);
            drone.isRenderComplete(MAX_WAIT_TIME);
            Assert.assertTrue(getText(RM_VIEW_AUDIT_LOG_LINK).contains(pageObjectUtils.getPropertyValue("record.view.audit.log.link")),
                "Failed to present Edit Metadata link on Records Details Page");
            Assert.assertTrue(getText(RM_REMOVE_FROM_HOLD_LINK).contains(pageObjectUtils.getPropertyValue("record.remove.from.hold.link")),
                "Failed to present Edit Metadata link on Records Details Page");
             Assert.assertTrue(getText(RM_ADD_TO_HOLD_LINK).contains(pageObjectUtils.getPropertyValue("record.add.to.hold.link")),
                "Failed to present Edit Metadata link on Records Details Page");
            click(RM_REMOVE_FROM_HOLD_LINK);
            drone.isRenderComplete(MAX_WAIT_TIME);
            Assert.assertTrue(getText(RM_EDIT_META_DATA_LINK).contains(pageObjectUtils.getPropertyValue("record.edit.metadata.link")),
                "Failed to present Edit Metadata link on Records Details Page");
            Assert.assertTrue(getText(RM_COMPLETE_RECORD).contains(pageObjectUtils.getPropertyValue("record.complete.record.link")),
                "Failed to present Edit Metadata link on Records Details Page");
            Assert.assertTrue(getText(RM_COPY_TO_LINK).contains(pageObjectUtils.getPropertyValue("record.copy.to.link")),
                "Failed to present Edit Metadata link on Records Details Page");
            Assert.assertTrue(getText(RM_MOVE_TO_LINK).contains(pageObjectUtils.getPropertyValue("record.move.to.link")),
                "Failed to present Edit Metadata link on Records Details Page");
            Assert.assertTrue(getText(RM_LINK_TO_LINK).contains(pageObjectUtils.getPropertyValue("record.link.to.link")),
                "Failed to present Edit Metadata link on Records Details Page");
            Assert.assertTrue(getText(RM_DELETE_LINK).contains(pageObjectUtils.getPropertyValue("record.delete.link")),
                "Failed to present Edit Metadata link on Records Details Page");
            Assert.assertTrue(getText(RM_VIEW_AUDIT_LOG_LINK).contains(pageObjectUtils.getPropertyValue("record.view.audit.log.link")),
                "Failed to present Edit Metadata link on Records Details Page");
            Assert.assertTrue(getText(RM_ADD_TO_HOLD_LINK).contains(pageObjectUtils.getPropertyValue("record.add.to.hold.link")),
                "Failed to present Edit Metadata link on Records Details Page");
            Assert.assertTrue(getText(RM_REQUEST_INFORMATION_LINK).contains(pageObjectUtils.getPropertyValue("record.request.information.link")),
                "Failed to present Edit Metadata link on Records Details Page");

            click(RM_DELETE_LINK);
            drone.findAndWait(INFORMATION_WINDOW);
            click(buttonByText(pageObjectUtils.getPropertyValue("delete.button")));

            filePlan = openRmSite();
            Assert.assertTrue(isElementPresent(commonLine(categoryName)), "Failed to present category in File plan");
            filePlan.navigateToFolder(categoryName);
            Assert.assertTrue(isElementPresent(commonLine(folderName)), "Failed to present Folder in File plan");
            Assert.assertTrue(isElementPresent(commonLine(folderName1)), "Failed to present Folder1 in File plan");
            filePlan.navigateToFolder(folderName1);
            drone.findAndWait(commonLine(electronicRecord), MAX_WAIT_TIME);
            Assert.assertTrue(isElementPresent(commonLine(nonElectronicRecord)),
                "Failed to Copy Record");
            filePlan.openRecordDetailsPage(nonElectronicRecord);
            click(RM_MOVE_TO_LINK);
            drone.findAndWait(By.cssSelector("div[id$=dialog] h2"));
            click(commonLink(pageObjectUtils.getPropertyValue("rm.file.plan.link")));
            drone.findAndWait(commonLink(categoryName), MAX_WAIT_TIME);
            click(commonLink(categoryName));
            drone.findAndWait(commonLink(folderName), MAX_WAIT_TIME);
            click(commonLink(folderName));
            click(COPY_MOVE_TO_BUTTON);
            waitUntilCreatedAlert();
            //Verify that record moved
            filePlan = openRmSite();
            Assert.assertTrue(isElementPresent(commonLine(categoryName)), "Failed to present category in File plan");
            filePlan.navigateToFolder(categoryName);
            Assert.assertTrue(isElementPresent(commonLine(folderName)), "Failed to present Folder in File plan");
            Assert.assertTrue(isElementPresent(commonLine(folderName1)), "Failed to present Folder1 in File plan");
            filePlan.navigateToFolder(folderName);
            Assert.assertTrue(isElementPresent(commonLine(nonElectronicRecord)), "Failed to Move Record");
            filePlan.openRecordDetailsPage(nonElectronicRecord);
            click(RM_LINK_TO_LINK);
            drone.findAndWait(By.cssSelector("div[id$=dialog] h2"));
            click(commonLink(pageObjectUtils.getPropertyValue("rm.file.plan.link")));
            drone.findAndWait(commonLink(categoryName), MAX_WAIT_TIME);
            click(commonLink(categoryName));
            drone.findAndWait(commonLink(folderName1), MAX_WAIT_TIME);
            click(commonLink(folderName1));
            click(COPY_MOVE_TO_BUTTON);
            waitUntilCreatedAlert();
            filePlan = openRmSite();
            Assert.assertTrue(isElementPresent(commonLine(categoryName)), "Failed to present category in File plan");
            filePlan.navigateToFolder(categoryName);
            Assert.assertTrue(isElementPresent(commonLine(folderName)), "Failed to present Folder in File plan");
            Assert.assertTrue(isElementPresent(commonLine(folderName1)), "Failed to present Folder1 in File plan");
            filePlan.navigateToFolder(folderName);
            Assert.assertTrue(isElementPresent(commonLine(nonElectronicRecord)), "Failed to Present Record");
            Assert.assertTrue(filePlan.isRecordLinked(nonElectronicRecord), "Failed to Link to Record");
            //Complete Record
            filePlan.openRecordDetailsPage(nonElectronicRecord);
            click(RM_COMPLETE_RECORD);
            waitUntilCreatedAlert();
            drone.isRenderComplete(MAX_WAIT_TIME);
            filePlan = openRmSite();
            Assert.assertTrue(isElementPresent(commonLine(categoryName)),  "Failed to present category in File plan");
            filePlan.navigateToFolder(categoryName);
            Assert.assertTrue(isElementPresent(commonLine(folderName)), "Failed to present Folder in File plan");
            Assert.assertTrue(isElementPresent(commonLine(folderName1)), "Failed to present Folder1 in File plan");
            filePlan.navigateToFolder(folderName);
            Assert.assertTrue(isElementPresent(commonLine(nonElectronicRecord)), "Failed to Present Record");
            Assert.assertFalse(filePlan.isInfoBannerExists(nonElectronicRecord, pageObjectUtils.getPropertyValue("incomplete.record.banner")),
                "Failed to Link to Record");
            //verify that Linked record completed too
            filePlan = openRmSite();
            Assert.assertTrue(isElementPresent(commonLine(categoryName)),  "Failed to present category in File plan");
            filePlan.navigateToFolder(categoryName);
            Assert.assertTrue(isElementPresent(commonLine(folderName)), "Failed to present Folder in File plan");
            Assert.assertTrue(isElementPresent(commonLine(folderName1)), "Failed to present Folder1 in File plan");
            filePlan.navigateToFolder(folderName1);
            Assert.assertTrue(isElementPresent(commonLine(nonElectronicRecord)), "Failed to Present Record");
            Assert.assertFalse(filePlan.isInfoBannerExists(nonElectronicRecord, pageObjectUtils.getPropertyValue("incomplete.record.banner")),
                "Failed to Link to Record");

            //Complete electronic Record
            filePlan.openRecordDetailsPage(electronicRecord);
            click(RM_COMPLETE_RECORD);
            waitUntilCreatedAlert();
            filePlan = openRmSite();
            Assert.assertTrue(isElementPresent(commonLine(categoryName)),  "Failed to present category in File plan");
            filePlan.navigateToFolder(categoryName);
            Assert.assertTrue(isElementPresent(commonLine(folderName)), "Failed to present Folder in File plan");
            Assert.assertTrue(isElementPresent(commonLine(folderName1)), "Failed to present Folder1 in File plan");
            filePlan.navigateToFolder(folderName1);
            Assert.assertTrue(isElementPresent(commonLine(nonElectronicRecord)), "Failed to Present Record");
            Assert.assertFalse(filePlan.isInfoBannerExists(electronicRecord, pageObjectUtils.getPropertyValue("incomplete.record.banner")),
                "Failed to Link to Record");
        }
        catch (Throwable e)
        {
            reportError(testName, e);
        }
        finally 
        {
            ShareUtil.logout(drone);
            login(userName, DEFAULT_USER_PASSWORD);
            deleteRMSite();
        }
    }

    public void Navigate(String...path)
    {
        WebDroneUtil.checkMandotaryParam("path", path);
        while(path != null)
        {
            //navigateToFolder(path[0]);
            String[] temp = Arrays.copyOfRange(path, 0, path.length - 1);
            Navigate(temp);
        }
    }
}
