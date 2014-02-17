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
package org.alfresco.po.rm;

import java.util.concurrent.TimeUnit;

import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Records management file plan page, based on the document library
 * page with specific action and operation related to file plan.
 *
 * @author Michael Suzuki
 * @author Tuna Aksoy
 * @version 1.7.1
 */
public class FilePlanPage extends DocumentLibraryPage
{
    private static final By MANAGE_RULES_BTN = By.cssSelector("button[id$='_default-manageRules-button-button']");
    private static final By UNFILED_MANAGE_RULES_BTN = By.cssSelector("button[id$='_default-unfiledManageRules-button-button']");
    private static final By NEW_CATEGORY_BTN = By.cssSelector("button[id$='default-newCategory-button-button']");
    private static final By NEW_FOLDER_BTN = By.cssSelector("button[id$='default-newFolder-button-button']");
    private static final By NEW_FILE_BTN = By.cssSelector("button[id$='default-fileUpload-button-button']");
    private static final By RM_ADD_META_DATA_LINK = By.cssSelector("div#onActionAddRecordMetadata a");
    private static final By RECORD = By.cssSelector("tbody.yui-dt-data > tr");
    private static final By DESCRIPTION = By.cssSelector("div[id$='_default-description'] div");
    private static final By FILEPLAN = By.id("template_x002e_tree_x002e_documentlibrary_x0023_default");
    private boolean expectingRecordOrFolder;
    private String expectedRecordOrFolderName;
    private boolean inRecordCategory;
    private boolean inRecordFolder;

    /**
     * Indicates that a record/folder will be expected in the file plan
     *
     * @param expectingRecordOrFolder <code>true</code> if a record/folder is expected <code>false</code> otherwise
     */
    public void setExpectingRecordOrFolder(boolean expectingRecordOrFolder)
    {
        this.expectingRecordOrFolder = expectingRecordOrFolder;
    }

    /**
     * Set the name of the expected record/folder
     *
     * @param expectedRecordOrFolderName Name of the expected record/folder
     */
    public void setExpectedRecordOrFolderName(String expectedRecordOrFolderName)
    {
        this.expectedRecordOrFolderName = expectedRecordOrFolderName;
    }

    /**
     * Indicates that the user is in a record category
     *
     * @param inRecordCategory <code>true</code> if the user is in a record category <code>false</code> otherwise
     */
    public void setInRecordCategory(boolean inRecordCategory)
    {
        this.inRecordCategory = inRecordCategory;
    }

    /**
     * Indicates that the user is in a record folder
     *
     * @param inRecordFolder <code>true</code> if the user is in a record folder <code>false</code> otherwise
     */
    public void setInRecordFolder(boolean inRecordFolder)
    {
        this.inRecordFolder = inRecordFolder;
    }

    /**
     * Constructor.
     *
     * @param drone {@link WebDrone}
     */
    public FilePlanPage(WebDrone drone)
    {
        this(drone, false);
    }

    /**
     * Constructor.
     *
     * @param drone {@link WebDrone}
     * @param hasRecords {@link Boolean}
     */
    public FilePlanPage(WebDrone drone, boolean hasRecords)
    {
        super(drone);
        this.expectingRecordOrFolder = hasRecords;
        setViewType(getNavigation().getViewType());
    }

    /**
     * @see org.alfresco.webdrone.Render#render(org.alfresco.webdrone.RenderTime)
     */
    @SuppressWarnings("unchecked")
    @Override
    public FilePlanPage render(RenderTime timer)
    {
        WebDroneUtil.checkMandotaryParam("timer", timer);

        while (true)
        {
            timer.start();
            try
            {
                WebElement filePlan = drone.find(FILEPLAN);
                if (filePlan.isDisplayed() && !isJSMessageDisplayed())
                {
                    long timeOut = TimeUnit.SECONDS.convert(maxPageLoadingTime, TimeUnit.MILLISECONDS);
                    if (drone.getCurrentUrl().contains("filter=unfiledRecords"))
                    {
                        if (isUnfiledRecordsContainerFileDisplayed() && isUnfiledRecordsContainerFolderDisplayed())
                        {
                            drone.waitUntilElementClickable(NEW_FILE_BTN, timeOut);
                            drone.waitUntilElementClickable(NEW_FOLDER_BTN, timeOut);
                            if (expectingRecordOrFolder)
                            {
                                if (StringUtils.isNotBlank(expectedRecordOrFolderName))
                                {
                                    boolean found = false;
                                    for (FileDirectoryInfo fileDirectoryInfo : getFiles())
                                    {
                                        if (fileDirectoryInfo.getName().contains(expectedRecordOrFolderName))
                                        {
                                            found = true;
                                            break;
                                        }
                                    }
                                    if (found)
                                    {
                                        break;
                                    }
                                }
                                else
                                {
                                    if (hasFiles())
                                    {
                                        break;
                                    }
                                }
                            }
                        }
                        else
                        {
                            continue;
                        }
                    }
                    // FIXME: Assume we are in the file plan view. Need to split to smaller objects for having individual render methods (e.g. for filters)
                    if (inRecordCategory)
                    {
                        if (!(isCreateNewCategoryDisplayed() && isCreateNewFolderDisplayed()))
                        {
                            continue;
                        }
                        else
                        {
                            drone.waitUntilElementClickable(NEW_CATEGORY_BTN, timeOut);
                            drone.waitUntilElementClickable(NEW_FOLDER_BTN, timeOut);
                            break;
                        }
                    }
                    if (inRecordFolder)
                    {
                        if (!isFileRecordDisplayed())
                        {
                            continue;
                        }
                        else
                        {
                            drone.waitUntilElementClickable(NEW_FILE_BTN, timeOut);
                            break;
                        }
                    }
                    if (expectingRecordOrFolder)
                    {
                        if (StringUtils.isNotBlank(expectedRecordOrFolderName))
                        {
                            boolean found = false;
                            for (FileDirectoryInfo fileDirectoryInfo : getFiles())
                            {
                                if (fileDirectoryInfo.getName().contains(expectedRecordOrFolderName))
                                {
                                    found = true;
                                    break;
                                }
                            }
                            if (found)
                            {
                                break;
                            }
                        }
                        else
                        {
                            if (hasFiles())
                            {
                                break;
                            }
                        }
                    }
                    else
                    {
                        break;
                    }
                }
            }
            catch (NoSuchElementException e)
            {
            }
            finally
            {
                timer.end();
            }
        }
        return this;
    }

    /**
     * @see org.alfresco.webdrone.Render#render(long)
     */
    @SuppressWarnings("unchecked")
    @Override
    public FilePlanPage render(long time)
    {
        RenderTime timer = new RenderTime(time);
        return render(timer);
    }

    /**
     * @see org.alfresco.webdrone.Render#render()
     */
    @SuppressWarnings("unchecked")
    @Override
    public FilePlanPage render()
    {
        RenderTime timer = new RenderTime(maxPageLoadingTime);
        return render(timer);
    }

    /**
     * Checks visibility of create new category button
     * on the file plan page header.
     *
     * @return <code>true</code> if visible <code>false</code> otherwise
     */
    public boolean isCreateNewCategoryDisplayed()
    {
        try
        {
            WebElement newCategory = drone.findAndWait(NEW_CATEGORY_BTN);
            return newCategory.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * Action mimicking select click on new category button.
     *
     * @return {@link CreateNewCategoryForm} Returns the create category form dialog
     */
    public CreateNewCategoryForm selectCreateNewCategory()
    {
        drone.findAndWait(NEW_CATEGORY_BTN).click();
        return new CreateNewCategoryForm(drone);
    }

    /**
     * Checks visibility of create new folder button
     * on the file plan page header.
     *
     * @return <code>true</code> if visible <code>false</code> otherwise
     */
    public boolean isCreateNewFolderDisplayed()
    {
        try
        {
            WebElement newFolder = drone.findAndWait(NEW_FOLDER_BTN);
            return newFolder.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * Action mimicking select click on new folder button.
     *
     * @return {@link CreateNewFolderForm} Returns the create folder form dialog
     */
    public CreateNewFolderForm selectCreateNewFolder()
    {
        drone.findAndWait(NEW_FOLDER_BTN).click();

        // need to check the dialog is there before we continue
        // TODO add this into a dialog for base class for convenience?
        //drone.waitForElement(By.cssSelector("div[id$='createFolder-dialog']"), 5);

        return new CreateNewFolderForm(drone);
    }

    /**
     * Checks visibility of file button on the file plan page header.
     *
     * @return <code>true</code> if visible <code>false</code> otherwise
     */
    public boolean isFileRecordDisplayed()
    {
        try
        {
            WebElement newFile = drone.findAndWait(NEW_FILE_BTN);
            return newFile.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * Action mimicking select click on file button on the file plan page header.
     *
     * @return {@link RmUploadFilePage} Returns the upload file page for RM
     */
    public RmUploadFilePage selectFileRecord()
    {
        drone.findAndWait(NEW_FILE_BTN).click();
        return new RmUploadFilePage(drone);
    }

    /**
     * Checks visibility of create new folder button in the unfiled records container.
     *
     * @return <code>true</code> if visible <code>false</code> otherwise
     */
    public boolean isUnfiledRecordsContainerFolderDisplayed()
    {
        return isCreateNewFolderDisplayed();
    }

    /**
     * Action mimicking select click on new folder button for unfiled records container.
     *
     * @return {@link CreateNewFolderForm} Returns the create folder folder form dialog
     */
    public CreateNewFolderForm selectCreateNewUnfiledRecordsContainerFolder()
    {
        return selectCreateNewFolder();
    }

    /**
     * Checks visibility of file button in the unfiled records container.
     *
     * @return <code>true</code> if visible <code>false</code> otherwise
     */
    public boolean isUnfiledRecordsContainerFileDisplayed()
    {
        return isFileRecordDisplayed();
    }

    /**
     * Action mimicking select click on file button for unfiled records container.
     *
     * @return {@link RmUploadFilePage} Returns the upload file page for RM
     */
    public RmUploadFilePage selectCreateNewUnfiledRecordsContainerFile()
    {
        return selectFileRecord();
    }

    /**
     * Verify if records exists.
     *
     * @return <code>true</code> if visible <code>false</code> otherwise
     */
    public boolean hasRecords()
    {
        try
        {
            WebElement record = drone.findAndWait(RECORD);
            return record.isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * Verify if header sub navigation has manage rules button visible.
     *
     * @return <code>true</code> if visible <code>false</code> otherwise
     */
    public boolean isManageRulesDisplayed()
    {
        try
        {
            WebElement manageRules = drone.findAndWait(MANAGE_RULES_BTN);
            return manageRules.isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * Action of click on manage rules button.
     *
     * @return {@link RmFolderRulesPage} page response
     */
    public RmFolderRulesPage selectManageRules()
    {
        WebElement manageRules = drone.findAndWait(MANAGE_RULES_BTN);
        manageRules.click();
        return new RmFolderRulesPage(drone);
    }

    /**
     * Action of click on manage rules button.
     *
     * @return {@link RmFolderRulesPage} page response
     */
    public RmFolderRulesPage selectUnfiledManageRules()
    {
        WebElement manageRules = drone.findAndWait(UNFILED_MANAGE_RULES_BTN);
        manageRules.click();
        return new RmFolderRulesPage(drone);
    }

    /**
     * Get html element on the side of the page with file plan sub
     * navigation also known as filters.
     *
     * @return {@link FilePlanNavigation} side element filter
     */
    public FilePlanNavigation getFilePlanNavigation()
    {
        return new FilePlanNavigation(drone);
    }

    /**
     * The file plan filter description.
     * Options are unfiled records, transfers and holds.
     *
     * @return String name of filtered view
     */
    public String getFilePlanDescription()
    {
        WebElement description = drone.findAndWait(DESCRIPTION);
        return description.getText();
    }

    /**
     * Checked if add record metadata link is visibile.
     * This is a records management feature.
     *
     * @return <code>true</code> if link is visible <code>false</code> otherwise
     */
    public boolean isAddRecordMetaDataVisible()
    {
        try
        {
            WebElement addMetaDataLink = drone.find(RM_ADD_META_DATA_LINK);
            return addMetaDataLink.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * Selects the title of the category link.
     *
     * @param title String category title
     * @return FilePlanPage page response object
     */
    public FilePlanPage selectCategory(final String title, final long timeout)
    {
        selectEntry(title, timeout).click();
        return new FilePlanPage(drone);
    }

    /**
     * Selects the title of the folder link.
     *
     * @param title String folder title
     * @return FilePlanPage page response object
     */
    public FilePlanPage selectFolder(final String title, final long timeout)
    {
        selectEntry(title, timeout).click();
        return new FilePlanPage(drone);
    }

    /**
     * Selects an entry regardless of type (file or folder)
     * @return
     */
    protected WebElement selectEntry(final String title, final long timeout)
    {
        if(title == null || title.isEmpty()) throw new IllegalArgumentException("Title is required");
        String search = String.format("//h3/span/a[text()='%s']",title);
        return drone.findAndWait(By.xpath(search), timeout);
    }

}