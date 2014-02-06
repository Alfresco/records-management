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
package org.alfresco.po.rm.functional;

import java.io.IOException;

import org.alfresco.po.rm.CreateNewFolderForm;
import org.alfresco.po.rm.FilePlanNavigation;
import org.alfresco.po.rm.FilePlanPage;
import org.alfresco.po.rm.RmUploadFilePage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.util.FailedTestListener;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * This test suite tests the following new features:
 *
 *  - Create a folder in the unfiled records container (also sub folders)
 *  - File a record into the unfiled records container (directly to the root or into folders in the container)
 *  - Change the meta data of a folder in the unfiled records container
 *  - View the details of a folder in the unfiled records container
 *  - Delete a record/folder in the unfiled records container
 *
 * @author Tuna Aksoy
 * @version 2.2
 */
@Listeners(FailedTestListener.class)
public class UnfiledRecordsContainerIntTest extends AbstractIntegrationTest
{
    // private static final String RM_UNFILED_RECORDS = "Unfiled Records";
    private static final String RM_UNFILED_RECORDS_CONTAINER_NAME = "Test folder name";
    private static final String RM_UNFILED_RECORDS_CONTAINER_TITLE = "Test folder title";
    private static final String RM_UNFILED_RECORDS_CONTAINER_DESC = "Test folder description";
    private static final By INPUT_TITLE_SELECTOR = By.name("prop_cm_title");
    private static final By INPUT_DESCRIPTION_SELECTOR = By.name("prop_cm_description");

    private FilePlanPage filePlanPage;

    @Test
    public void navigateToUnfiledRecords()
    {
        // FIXME!!! Click on the link rather than navigating
        drone.navigateTo(shareUrl + "/page/site/rm/documentlibrary");
        filePlanPage = drone.getCurrentPage().render();

        // FIXME!!!
        /*
        String filePlanDescription = filePlanPage.getFilePlanDescription();
        Assert.assertTrue(filePlanDescription.contains(RM_UNFILED_RECORDS));
        */

        FilePlanNavigation filePlanNavigation = filePlanPage.getFilePlanNavigation();
        Assert.assertNotNull(filePlanNavigation);
        Assert.assertTrue(filePlanNavigation.isUnfiledRecordsVisible());
        filePlanPage = filePlanNavigation.selectUnfiledRecords().render();
    }

    @Test(dependsOnMethods="navigateToUnfiledRecords")
    public void fileRecord() throws IOException
    {
        Assert.assertTrue(filePlanPage.isUnfiledRecordsContainerFileDisplayed());
        RmUploadFilePage rmRecordFileDialog = filePlanPage.selectCreateNewUnfiledRecordsContainerFile().render();
        String fileName = Long.valueOf(System.currentTimeMillis()).toString();
        fileElectronicRecord(drone, rmRecordFileDialog, fileName);
        filePlanPage = filePlanPage.render();
        Assert.assertEquals(1, filePlanPage.getFiles().size());
    }

    @Test(dependsOnMethods="fileRecord")
    public void createUnfiledRecordsFolder() throws IOException
    {
        Assert.assertTrue(filePlanPage.isUnfiledRecordsContainerFolderDisplayed());
        CreateNewFolderForm createNewFolderDialog = filePlanPage.selectCreateNewUnfiledRecordsContainerFolder().render();
        Assert.assertNotNull(createNewFolderDialog.getRecordFolderId());
        createNewFolderDialog.enterName(RM_UNFILED_RECORDS_CONTAINER_NAME);
        createNewFolderDialog.enterTitle(RM_UNFILED_RECORDS_CONTAINER_TITLE);
        createNewFolderDialog.enterDescription(RM_UNFILED_RECORDS_CONTAINER_DESC);
        filePlanPage = createNewFolderDialog.selectSave().render();
        Assert.assertEquals(2, filePlanPage.getFiles().size());
    }

    @Test(dependsOnMethods="createUnfiledRecordsFolder")
    public void fileRecordInUnfiledRecordsFolder() throws IOException
    {
        FileDirectoryInfo fileDirectoryInfo = filePlanPage.getFileDirectoryInfo(RM_UNFILED_RECORDS_CONTAINER_NAME);
        fileDirectoryInfo.clickOnTitle();
        fileRecord();
    }

    @Test(dependsOnMethods="fileRecordInUnfiledRecordsFolder")
    public void editUnfiledRecordsFolderMetadata()
    {
        navigateToUnfiledRecords();

        FileDirectoryInfo folder = filePlanPage.getFileDirectoryInfo(RM_UNFILED_RECORDS_CONTAINER_NAME);
        WebElement actions = folder.findElement(By.cssSelector("td:nth-of-type(5)"));
        drone.mouseOver(actions);
        WebElement editProperties = folder.findElement(By.cssSelector("div.rm-edit-details>a"));
        editProperties.click();

        drone.waitForElement(INPUT_TITLE_SELECTOR, 5);
        WebElement title = drone.find(INPUT_TITLE_SELECTOR);
        title.clear();
        title.sendKeys("My new title ABC");

        WebElement description = drone.find(INPUT_DESCRIPTION_SELECTOR);
        description.clear();
        description.sendKeys("My new description...");

        WebElement saveButton = drone.find(By.cssSelector("button[id$='form-submit-button']"));
        saveButton.click();
    }

    @Test(dependsOnMethods="editUnfiledRecordsFolderMetadata")
    public void clickUnfiledRecordsFolderDetails()
    {
        navigateToUnfiledRecords();

        FileDirectoryInfo folder = filePlanPage.getFileDirectoryInfo(RM_UNFILED_RECORDS_CONTAINER_NAME);
        WebElement actions = folder.findElement(By.cssSelector("td:nth-of-type(5)"));
        drone.mouseOver(actions);
        WebElement folderDetails = folder.findElement(By.cssSelector("div.rm-record-folder-view-details>a"));
        folderDetails.click();
    }

    @Test(dependsOnMethods="clickUnfiledRecordsFolderDetails")
    public void managePermissions()
    {
        navigateToUnfiledRecords();

        FileDirectoryInfo folder = filePlanPage.getFileDirectoryInfo(RM_UNFILED_RECORDS_CONTAINER_NAME);
        WebElement actions = folder.findElement(By.cssSelector("td:nth-of-type(5)"));
        drone.mouseOver(actions);
        WebElement folderPermissions = folder.findElement(By.cssSelector("div.rm-manage-permissions>a"));
        folderPermissions.click();

        WebElement addUserOrGroupButton = drone.findAndWait(By.cssSelector("button[id$='-addusergroup-button-button']"));
        addUserOrGroupButton.click();

        WebElement doneButton = drone.findAndWait(By.cssSelector("button[id$='-finish-button-button']"));
        doneButton.click();
    }

    @Test(dependsOnMethods="managePermissions")
    public void manageRules()
    {
        navigateToUnfiledRecords();

        FileDirectoryInfo folder = filePlanPage.getFileDirectoryInfo(RM_UNFILED_RECORDS_CONTAINER_NAME);
        WebElement actions = folder.findElement(By.cssSelector("td:nth-of-type(5)"));
        drone.mouseOver(actions);

        WebElement showMore = folder.findAndWait(By.cssSelector("div.internal-show-more>a"));
        showMore.click();

        WebElement folderRules = folder.findElement(By.cssSelector("div.rm-manage-rules>a"));
        folderRules.click();
    }

    @Test(dependsOnMethods="manageRules")
    public void deleteFiledRecordAndFolder()
    {
        navigateToUnfiledRecords();

        for (FileDirectoryInfo fileDirectoryInfo : filePlanPage.getFiles())
        {
            fileDirectoryInfo.selectDelete();
            WebElement confirmDelete = drone.find(By.cssSelector("div#prompt div.ft span span button"));
            confirmDelete.click();
        }
    }
}