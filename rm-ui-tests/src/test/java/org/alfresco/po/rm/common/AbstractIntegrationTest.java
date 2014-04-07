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
package org.alfresco.po.rm.common;

import java.io.File;
import java.io.IOException;

import org.alfresco.po.rm.RmUploadFilePage;
import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.fileplan.filter.unfiledrecords.UnfiledRecordsContainer;
import org.alfresco.po.rm.fileplan.toolbar.CreateNewRecordCategoryDialog;
import org.alfresco.po.rm.fileplan.toolbar.CreateNewRecordFolderDialog;
import org.alfresco.po.rm.util.RmPageObjectUtils;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Abstract Records Management integration test.
 *
 * @author Roy Wetherall
 * @since 2.2
 */
public abstract class AbstractIntegrationTest extends AbstractRecordsManagementTest
{
    
    // TODO need to move these out of here!!
    /** File record dialog constants */
    protected static final By PROMPT_PANEL_ID = By.id("prompt");
    protected static final By BUTTON_TAG_NAME = By.tagName("button");
    protected static final String ELECTRONIC = "Electronic";
    
    /** RM page object utils */
    protected RmPageObjectUtils pageObjectUtils = new RmPageObjectUtils();
    
    /**
     * Test setup
     */
    protected void setup()
    {
        // TODO not sure if this is the right way to do this!
        pageObjectUtils.loadProperties("rm_en.properties");
        
        // log into Share
        login(username, password);

        // open the RM site
        openRMSite(isExisitingRMSiteDeletedOnStartup());
    }

    /**
     * Test teardown
     */
    protected void teardown()
    {
        if (isRMSiteDeletedOnTearDown())
        {
            // delete RM site
            deleteRMSite();
        }
    }

    /**
     * Helper method to file a record
     *
     * @param drone {@link WebDrone} The web drone instance
     * @param rmRecordFileDialog {@link RmUploadFilePage} The upload file dialog for RM
     * @param fileName {@link String} The name of the file
     * @throws IOException Can throw an {@link IOException} whilst getting the canonical path
     */
    private HtmlPage fileElectronicRecord(final WebDrone drone, final RmUploadFilePage rmRecordFileDialog, String fileName) throws IOException
    {
        // select to upload electronic record
        rmRecordFileDialog.selectElectronic(drone);

        String name = StringUtils.isNotBlank(fileName) ? fileName : Long.valueOf(System.currentTimeMillis()).toString();
        File file = SiteUtil.prepareFile(name);
        String filePath = file.getCanonicalPath();
        return rmRecordFileDialog.uploadFile(filePath);
    }

    /**
     * Helper method to file a record in the hold container
     *
     * @param drone {@link WebDrone} The web drone instance
     * @param rmRecordFileDialog {@link RmUploadFilePage} The upload file dialog for RM
     * @param fileName {@link String} The name of the file
     * @throws IOException Can throw an {@link IOException} whilst getting the canonical path
     */
    protected void fileElectronicRecordToFilePlan(final WebDrone drone, final RmUploadFilePage rmRecordFileDialog, String fileName) throws IOException
    {
        WebDroneUtil.checkMandotaryParam("drone", drone);
        WebDroneUtil.checkMandotaryParam("rmRecordFileDialog", rmRecordFileDialog);
        // FileName can be blank. In this case a name will be generated

        FilePlanPage filePlanPage = (FilePlanPage) fileElectronicRecord(drone, rmRecordFileDialog, fileName);
        filePlanPage.render(fileName);
    }

    /**
     * Helper method to file a record in the unfiled records container
     *
     * @param drone {@link WebDrone} The web drone instance
     * @param rmRecordFileDialog {@link RmUploadFilePage} The upload file dialog for RM
     * @param fileName {@link String} The name of the file
     * @throws IOException Can throw an {@link IOException} whilst getting the canonical path
     */
    protected void fileElectronicRecordToUnfiledRecordsContainer(final WebDrone drone, final RmUploadFilePage rmRecordFileDialog, String fileName) throws IOException
    {
        WebDroneUtil.checkMandotaryParam("drone", drone);
        WebDroneUtil.checkMandotaryParam("rmRecordFileDialog", rmRecordFileDialog);
        // FileName can be blank. In this case a name will be generated

        UnfiledRecordsContainer unfiledRecordsContainer = (UnfiledRecordsContainer) fileElectronicRecord(drone, rmRecordFileDialog, fileName);
        unfiledRecordsContainer.render(fileName);
    }

    /**
     * Helper method to create a new record category in the file plan root or a record category
     *
     * @param filePlan      file plan page, should be showing the parent record category
     * @param name          name
     * @param title         title
     * @param description   description
     * @return {@link FilePlanPage} file plan page showing the parent record category with the new category in the list
     */
    protected FilePlanPage createNewCategory(FilePlanPage filePlan, String name, String title, String description)
    {
        WebDroneUtil.checkMandotaryParam("filePlan", filePlan);
        WebDroneUtil.checkMandotaryParam("name", name);
        WebDroneUtil.checkMandotaryParam("title", name);

        // TODO check that the file plan is in the correct state

        CreateNewRecordCategoryDialog createNewCategory = filePlan.selectCreateNewCategory().render();
        createNewCategory.enterName(name);
        createNewCategory.enterTitle(title);
        if (!description.isEmpty())
        {
            createNewCategory.enterDescription(description);
        }
        filePlan = ((FilePlanPage) createNewCategory.selectSave());
        filePlan.setInFilePlanRoot(true);
        return filePlan.render(name);
    }

    /**
     * Helper method to create a new record folder in the a record category
     *
     * @param filePlan      file plan page, should be showing the parent record folder
     * @param name          name
     * @param title         title
     * @param description   description
     * @return {@link FilePlanPage} file plan page showing the parent record record category with the new record folder in the list
     */
    protected FilePlanPage createNewRecordFolder(FilePlanPage filePlan, String name, String title, String description)
    {
        WebDroneUtil.checkMandotaryParam("filePlan", filePlan);
        WebDroneUtil.checkMandotaryParam("name", name);
        WebDroneUtil.checkMandotaryParam("title", name);

        // TODO check that the file plan is in the correct state

        CreateNewRecordFolderDialog createNewFolder = filePlan.selectCreateNewFolder().render();
        createNewFolder.enterName(name);
        createNewFolder.enterTitle(title);
        if (!description.isEmpty())
        {
            createNewFolder.enterDescription(description);
        }
        filePlan = ((FilePlanPage) createNewFolder.selectSave());
        filePlan.setInRecordCategory(true);
        return filePlan.render(name);
    }

    /**
     * Helper method that clicks by element
     *
     * @param locator element By locator
     */
    public void click(By locator)
    {
        WebElement element = drone.findAndWait(locator);
        drone.mouseOverOnElement(element);
        element.click();
    }

    /**
     * Helper method that type to input field
     *
     * @param locator element By locator
     */
    public void type(By locator, String text)
    {
        WebElement title = drone.find(locator);
        title.clear();
        title.sendKeys(text);
    }
}