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

import org.alfresco.po.rm.RmUploadFilePage;
import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.fileplan.filter.FilePlanFilter;
import org.alfresco.po.rm.fileplan.filter.unfiledrecords.UnfiledRecordsContainer;
import org.alfresco.po.rm.fileplan.toolbar.CreateNewRecordFolderDialog;
import org.alfresco.po.rm.util.RmPageObjectUtils;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.util.FailedTestListener;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * This test suite tests the following new features:
 * <p>
 * <ul>
 *  <li>Creating a folder in the unfiled records container (also sub folders)</li>
 *  <li>Filing a record into the unfiled records container (directly to the root or into folders in the container)</li>
 *  <li>Changing the meta data of a folder in the unfiled records container</li>
 *  <li>Viewing the details of a folder in the unfiled records container</li>
 *  <li>Viewing the manage permissions page of the unfiled records container</li>
 *  <li>Viewing the manage rules page of the unfiled records container</li>
 *  <li>Deleting a record/folder in the unfiled records container</li>
 * </ul>
 * <p>
 * @author Tuna Aksoy
 * @version 2.2
 */
@Listeners(FailedTestListener.class)
public class UnfiledRecordsContainerIntTest extends AbstractIntegrationTest
{
    private static final String RM_UNFILED_RECORDS_CONTAINER_NAME = "Test folder name";
    private static final String RM_UNFILED_RECORDS_CONTAINER_TITLE = "Test folder title";
    private static final String RM_UNFILED_RECORDS_CONTAINER_DESC = "Test folder description";
    private static final By INPUT_TITLE_SELECTOR = By.name("prop_cm_title");
    private static final By INPUT_DESCRIPTION_SELECTOR = By.name("prop_cm_description");
    private static final By NAVIGATION_MENU_FILE_PLAN = By.cssSelector("div#HEADER_SITE_DOCUMENTLIBRARY");
    private FilePlanPage filePlanPage;
    private FilePlanFilter filePlanFilter;
    private UnfiledRecordsContainer unfiledRecordsContainer;

    @Test
    public void selectFilePlan()
    {
        RmPageObjectUtils.select(drone, NAVIGATION_MENU_FILE_PLAN);
        filePlanPage = drone.getCurrentPage().render();
        Assert.assertNotNull(filePlanPage);
    }

    /**
     * Helper method to select the unfiled records container
     *
     * @param name {@link String} The name of file/record in the unfiled records container
     */
    private void selectUnfiledRecordsContainer(String name)
    {
        filePlanFilter = filePlanPage.getFilePlanFilter();
        Assert.assertNotNull(filePlanFilter);
        Assert.assertTrue(filePlanFilter.isUnfiledRecordsContainerDisplayed());
        unfiledRecordsContainer = filePlanFilter.selectUnfiledRecordsContainer().render(name);
        Assert.assertNotNull(unfiledRecordsContainer);
    }

    @Test(dependsOnMethods="selectFilePlan")
    public void navigateToUnfiledRecords()
    {
        selectUnfiledRecordsContainer(null);
    }

    @Test(dependsOnMethods="navigateToUnfiledRecords")
    public void fileRecord() throws IOException
    {
        RmUploadFilePage rmRecordFileDialog = unfiledRecordsContainer.selectCreateNewUnfiledRecordsContainerFile().render();
        Assert.assertNotNull(rmRecordFileDialog);
        String fileName = Long.valueOf(System.currentTimeMillis()).toString();
        fileElectronicRecordToUnfiledRecordsContainer(drone, rmRecordFileDialog, fileName);
        unfiledRecordsContainer = unfiledRecordsContainer.render(fileName);
        Assert.assertNotNull(unfiledRecordsContainer);
        Assert.assertEquals(1, unfiledRecordsContainer.getFiles().size());
    }

    @Test(dependsOnMethods="fileRecord")
    public void createUnfiledRecordsFolder() throws IOException
    {
        CreateNewRecordFolderDialog createNewFolderDialog = unfiledRecordsContainer.selectCreateNewUnfiledRecordsContainerFolder().render();
        Assert.assertNotNull(createNewFolderDialog);
        Assert.assertNotNull(createNewFolderDialog.getRecordFolderId());
        createNewFolderDialog.enterName(RM_UNFILED_RECORDS_CONTAINER_NAME);
        createNewFolderDialog.enterTitle(RM_UNFILED_RECORDS_CONTAINER_TITLE);
        createNewFolderDialog.enterDescription(RM_UNFILED_RECORDS_CONTAINER_DESC);
        unfiledRecordsContainer = ((UnfiledRecordsContainer) createNewFolderDialog.selectSave()).render(RM_UNFILED_RECORDS_CONTAINER_NAME);
        Assert.assertNotNull(unfiledRecordsContainer);
        Assert.assertEquals(2, unfiledRecordsContainer.getFiles().size());
    }

    @Test(dependsOnMethods="createUnfiledRecordsFolder")
    public void fileRecordInUnfiledRecordsFolder() throws IOException
    {
        FileDirectoryInfo fileDirectoryInfo = unfiledRecordsContainer.getFileDirectoryInfo(RM_UNFILED_RECORDS_CONTAINER_NAME);
        Assert.assertNotNull(fileDirectoryInfo);
        fileDirectoryInfo.clickOnTitle();
        unfiledRecordsContainer.render();
        fileRecord();
    }

    @Test(dependsOnMethods="fileRecordInUnfiledRecordsFolder")
    public void editUnfiledRecordsFolderMetadata()
    {
        selectFilePlan();
        selectUnfiledRecordsContainer(RM_UNFILED_RECORDS_CONTAINER_NAME);
        FileDirectoryInfo folder = unfiledRecordsContainer.getFileDirectoryInfo(RM_UNFILED_RECORDS_CONTAINER_NAME);
        WebElement selectMoreAction = folder.selectMoreAction();
        selectMoreAction.click();
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
        selectFilePlan();
        selectUnfiledRecordsContainer(RM_UNFILED_RECORDS_CONTAINER_NAME);
        FileDirectoryInfo folder = unfiledRecordsContainer.getFileDirectoryInfo(RM_UNFILED_RECORDS_CONTAINER_NAME);
        WebElement selectMoreAction = folder.selectMoreAction();
        selectMoreAction.click();
        WebElement folderDetails = folder.findElement(By.cssSelector("div.rm-record-folder-view-details>a"));
        folderDetails.click();
    }

    @Test(dependsOnMethods="clickUnfiledRecordsFolderDetails")
    public void managePermissions()
    {
        selectFilePlan();
        selectUnfiledRecordsContainer(RM_UNFILED_RECORDS_CONTAINER_NAME);
        FileDirectoryInfo folder = unfiledRecordsContainer.getFileDirectoryInfo(RM_UNFILED_RECORDS_CONTAINER_NAME);
        WebElement selectMoreAction = folder.selectMoreAction();
        selectMoreAction.click();
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
        selectFilePlan();
        selectUnfiledRecordsContainer(RM_UNFILED_RECORDS_CONTAINER_NAME);
        FileDirectoryInfo folder = unfiledRecordsContainer.getFileDirectoryInfo(RM_UNFILED_RECORDS_CONTAINER_NAME);
        WebElement selectMoreAction = folder.selectMoreAction();
        selectMoreAction.click();

        WebElement showMore = folder.findElement(By.cssSelector("div.internal-show-more>a"));
        showMore.click();

        WebElement folderRules = folder.findElement(By.cssSelector("div.rm-manage-rules>a"));
        folderRules.click();
    }

    @Test(dependsOnMethods="manageRules")
    public void deleteFiledRecordAndFolder()
    {
        selectFilePlan();
        selectUnfiledRecordsContainer(RM_UNFILED_RECORDS_CONTAINER_NAME);
        for (FileDirectoryInfo fileDirectoryInfo : unfiledRecordsContainer.getFiles())
        {
            fileDirectoryInfo.selectDelete();
            WebElement confirmDelete = drone.find(By.cssSelector("div#prompt div.ft span span button"));
            confirmDelete.click();
        }
    }
}