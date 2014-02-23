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
package org.alfresco.po.rm.fileplan;

import java.util.concurrent.TimeUnit;

import org.alfresco.po.rm.RmFolderRulesPage;
import org.alfresco.po.rm.RmUploadFilePage;
import org.alfresco.po.rm.fileplan.filter.FilePlanFilter;
import org.alfresco.po.rm.fileplan.toolbar.CreateNewRecordCategoryDialog;
import org.alfresco.po.rm.fileplan.toolbar.CreateNewRecordFolderDialog;
import org.alfresco.po.rm.util.RmPageObjectUtils;
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
    protected static final By MANAGE_RULES_BTN = By.cssSelector("button[id$='_default-manageRules-button-button']");
    protected static final By NEW_CATEGORY_BTN = By.cssSelector("button[id$='default-newCategory-button-button']");
    protected static final By NEW_FOLDER_BTN = By.cssSelector("button[id$='default-newFolder-button-button']");
    protected static final By NEW_FILE_BTN = By.cssSelector("button[id$='default-fileUpload-button-button']");
    protected static final By RM_ADD_META_DATA_LINK = By.cssSelector("div#onActionAddRecordMetadata a");
    protected static final By RECORD = By.cssSelector("tbody.yui-dt-data > tr");
    protected static final By DESCRIPTION = By.cssSelector("div[id$='_default-description'] div");
    protected static final By FILEPLAN = By.id("template_x002e_tree_x002e_documentlibrary_x0023_default");
    protected boolean inFilePlanRoot;
    protected boolean inRecordCategory;
    protected boolean inRecordFolder;

    /**
     * Indicates that the user is in the file plan root
     *
     * @param inFilePlanRoot <code>true</code> if the user is in the file plan root <code>false</code> otherwise
     */
    public void setInFilePlanRoot(boolean inFilePlanRoot)
    {
        this.inFilePlanRoot = inFilePlanRoot;
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
        super(drone);
    }

    /**
     * @see org.alfresco.webdrone.Render#render(org.alfresco.webdrone.RenderTime)
     */
    @SuppressWarnings("unchecked")
    @Override
    public FilePlanPage render(RenderTime timer)
    {
        WebDroneUtil.checkMandotaryParam("timer", timer);

        return render(timer, null);
    }

    /**
     * FIXME!!!
     *
     * @param expectedRecordOrFolderName
     * @return
     */
    public FilePlanPage render(String expectedRecordOrFolderName)
    {
        RenderTime timer = new RenderTime(maxPageLoadingTime);
        return render(timer, expectedRecordOrFolderName);
    }

    /**
     * FIXME!!!
     *
     * @param timer
     * @param expectedRecordOrFolderName
     * @return
     */
    private FilePlanPage render(RenderTime timer, String expectedRecordOrFolderName)
    {
        WebDroneUtil.checkMandotaryParam("timer", timer);
        // "expectedRecordOrFolderName" can be blank

        boolean found = false;
        while (true)
        {
            setViewType(getNavigation().getViewType());
            timer.start();
            try
            {
                if (RmPageObjectUtils.isDisplayed(drone, FILEPLAN) && !isJSMessageDisplayed())
                {
                    if (StringUtils.isNotBlank(expectedRecordOrFolderName) && !found)
                    {
                        for (FileDirectoryInfo fileDirectoryInfo : getFiles())
                        {
                            if (fileDirectoryInfo.getName().contains(expectedRecordOrFolderName))
                            {
                                found = true;
                                break;
                            }
                        }
                        if (!found)
                        {
                            continue;
                        }
                    }
                    long timeOut = TimeUnit.SECONDS.convert(maxPageLoadingTime, TimeUnit.MILLISECONDS);
                    if (inFilePlanRoot)
                    {
                        if (!isCreateNewCategoryDisplayed())
                        {
                            continue;
                        }
                        else
                        {
                            drone.waitUntilElementClickable(NEW_CATEGORY_BTN, timeOut);
                        }
                    }
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
                        }
                    }
                    break;
                }
            }
            catch (NoSuchElementException e)
            {
            }
            finally
            {
                timer.end();
                // FIXME!!!
                setInFilePlanRoot(false);
                setInRecordCategory(false);
                setInRecordFolder(false);
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
        return RmPageObjectUtils.isDisplayed(drone, NEW_CATEGORY_BTN);
    }

    /**
     * Action mimicking select click on new category button.
     *
     * @return {@link CreateNewRecordCategoryDialog} Returns the create category form dialog
     */
    public CreateNewRecordCategoryDialog selectCreateNewCategory()
    {
        RmPageObjectUtils.select(drone, NEW_CATEGORY_BTN);
        return new CreateNewRecordCategoryDialog(drone);
    }

    /**
     * Checks visibility of create new folder button
     * on the file plan page header.
     *
     * @return <code>true</code> if visible <code>false</code> otherwise
     */
    public boolean isCreateNewFolderDisplayed()
    {
        return RmPageObjectUtils.isDisplayed(drone, NEW_FOLDER_BTN);
    }

    /**
     * Action mimicking select click on new folder button.
     *
     * @return {@link CreateNewRecordFolderDialog} Returns the create folder form dialog
     */
    public CreateNewRecordFolderDialog selectCreateNewFolder()
    {
        RmPageObjectUtils.select(drone, NEW_FOLDER_BTN);
        return new CreateNewRecordFolderDialog(drone);
    }

    /**
     * Checks visibility of file button on the file plan page header.
     *
     * @return <code>true</code> if visible <code>false</code> otherwise
     */
    public boolean isFileRecordDisplayed()
    {
        return RmPageObjectUtils.isDisplayed(drone, NEW_FILE_BTN);
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
     * @return {@link CreateNewRecordFolderDialog} Returns the create folder folder form dialog
     */
    public CreateNewRecordFolderDialog selectCreateNewUnfiledRecordsContainerFolder()
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
        return new RmUploadFilePage(drone);
    }

    /**
     * Verify if records exists.
     *
     * @return <code>true</code> if visible <code>false</code> otherwise
     */
    public boolean hasRecords()
    {
        return RmPageObjectUtils.isDisplayed(drone, RECORD);
    }

    /**
     * Verify if header sub navigation has manage rules button visible.
     *
     * @return <code>true</code> if visible <code>false</code> otherwise
     */
    public boolean isManageRulesDisplayed()
    {
        return RmPageObjectUtils.isDisplayed(drone, MANAGE_RULES_BTN);
    }

    /**
     * Action of click on manage rules button.
     *
     * @return {@link RmFolderRulesPage} page response
     */
    public RmFolderRulesPage selectManageRules()
    {
        RmPageObjectUtils.select(drone, MANAGE_RULES_BTN);
        return new RmFolderRulesPage(drone);
    }

    /**
     * Get html element on the side of the page with file plan sub
     * navigation also known as filters.
     *
     * @return {@link FilePlanFilter} side element filter
     */
    public FilePlanFilter getFilePlanFilter()
    {
        return new FilePlanFilter(drone);
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
        return RmPageObjectUtils.isDisplayed(drone, RM_ADD_META_DATA_LINK);
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

    // FIXME: This method will be deleted after the original method has been fixed
    @Override
    public FileDirectoryInfo getFileDirectoryInfo(String title)
    {
        FileDirectoryInfo result = null;
        for (FileDirectoryInfo fileDirectoryInfo : getFiles())
        {
            if (fileDirectoryInfo.getName().equals(title))
            {
                result = fileDirectoryInfo;
                break;
            }
        }
        return result;
    }
}