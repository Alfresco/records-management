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
package org.alfresco.po.rm.fileplan;

import static org.alfresco.po.rm.fileplan.RmCreateDispositionPage.CREATE_DISPOSITION_BUTTON;
import static org.alfresco.po.rm.fileplan.toolbar.CreateNewRecordDialog.NON_ELECTRONIC_BUTTON;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.alfresco.po.rm.RmConsolePage;
import org.alfresco.po.rm.RmFolderRulesPage;
import org.alfresco.po.rm.RmFolderRulesWithRules;
import org.alfresco.po.rm.RmSiteDashBoardPage;
import org.alfresco.po.rm.RmUploadFilePage;
import org.alfresco.po.rm.fileplan.filter.FilePlanFilter;
import org.alfresco.po.rm.fileplan.toolbar.CreateNewRecordCategoryDialog;
import org.alfresco.po.rm.fileplan.toolbar.CreateNewRecordDialog;
import org.alfresco.po.rm.fileplan.toolbar.CreateNewRecordFolderDialog;
import org.alfresco.po.rm.util.RmPageObjectUtils;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.FolderDetailsPage;
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
    /** tool bar buttons */
    protected static final By MANAGE_RULES_BTN = By.cssSelector("button[id$='_default-manageRules-button-button']");
    public static final By NEW_FILE_BTN = By.cssSelector("button[id$='default-fileUpload-button-button']");
    public static final By NEW_UNFILED_RECORDS_FOLDER_BTN = By.cssSelector("button[id$='default-newUnfiledRecordsFolder-button-button']");
    public static final By RM_ADD_META_DATA_LINK = By.cssSelector("div#onActionAddRecordMetadata a");
    public static final By RECORD_MANAGEMENT_CONSOLE = By.xpath("//span[contains(@id, 'HEADER_SITE_RM_MANAGEMENT_CONSOLE')]");
    protected static final By NEW_CATEGORY_BTN = By.cssSelector("button[id$='default-newCategory-button-button']");
    protected static final By NEW_FOLDER_BTN = By.cssSelector("button[id$='default-newFolder-button-button']");
    protected static final By FILE_BTN = By.cssSelector("button[id$='default-fileUpload-button-button']");

    protected static final By RECORD = By.cssSelector("tbody.yui-dt-data > tr");
    protected static final By DESCRIPTION = By.cssSelector("div[id$='_default-description'] div");
    protected static final By FILEPLAN = By.id("template_x002e_tree_x002e_documentlibrary_x0023_default");
    protected static final By FILEPLAN_NAV = By.cssSelector("div[id$='navBar']");

    protected final static long MAX_WAIT_TIME = 60000;

    /** actions */
    protected static final By EDIT_RECORD_METADATA_ACTION = By.cssSelector("");

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
     * Helper method to get the root file plan page.
     * <p>
     * Returned file plan page is correct initialised and render has been run.
     *
     * @param   rmSiteDashBoard         records management dashboard
     * @return  {@link FilePlanPage}    rendered file plan page
     */
    public static FilePlanPage getFilePlanRoot(RmSiteDashBoardPage rmSiteDashBoard)
    {
        FilePlanPage filePlan = rmSiteDashBoard.getRMNavigation().selectFilePlan();
        filePlan.setInFilePlanRoot(true);
        return filePlan.render();
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
     * Renders the page and waits until the element with the
     * expected name has has been displayed.
     *
     * @param expectedName {@link String} The name of the expected element
     * @return {@link FilePlanPage} The file plan page displaying the expected element
     */
    public FilePlanPage render(String expectedName)
    {
        // "expectedName" can be blank so no check required

        RenderTime timer = new RenderTime(maxPageLoadingTime);
        return render(timer, expectedName);
    }

    /**
     * Renders the page and waits until the element with the
     * expected name has has been displayed.
     *
     * timer {@link RenderTime} time to wait
     * @param expectedName {@link String} The name of the expected element
     * @return {@link FilePlanPage} The file plan page displaying the expected element
     */
    private FilePlanPage render(RenderTime timer, String expectedName)
    {
        WebDroneUtil.checkMandotaryParam("timer", timer);
        // "expectedName" can be blank

        boolean found = false;
        while (true)
        {
            timer.start();
            try
            {
                if (RmPageObjectUtils.isDisplayed(drone, FILEPLAN) &&
                    RmPageObjectUtils.isDisplayed(drone, FILEPLAN_NAV) &&
                    !isJSMessageDisplayed())
                {
                    setViewType(getNavigation().getViewType());

                    if (StringUtils.isNotBlank(expectedName) && !found)
                    {
                        for (FileDirectoryInfo fileDirectoryInfo : getFiles())
                        {
                            if (fileDirectoryInfo.getName().contains(expectedName))
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
                        if (RmPageObjectUtils.isDisplayed(drone, FILE_BTN))
                        {
                            drone.waitUntilElementClickable(FILE_BTN, timeOut);
                        }
                        else
                        {
                            continue;
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
     * Verify if header sub navigation has File button visible.
     *
     * @return <code>true</code> if visible <code>false</code> otherwise
     */
    public boolean isCreateNewFileDisplayed()
    {
        return RmPageObjectUtils.isDisplayed(drone, NEW_FILE_BTN);
    }

    /**
     * Action of click on manage rules button.
     *
     * @return {@link RmFolderRulesWithRules} page response
     */
    public RmFolderRulesWithRules selectManageRulesWithRules()
    {
        RmPageObjectUtils.select(drone, MANAGE_RULES_BTN);
        return new RmFolderRulesWithRules(drone).render();
    }

    /**
     * Get html element on the side of the page with file plan sub
     * navigation also known as filters.
     *
     * @return {@link FilePlanFilter} side element filter
     * Click on the 'file' action on the toolbar
     *
     * @return  {@link RmUploadFilePage}    rm upload page
     */
    public RmUploadFilePage selectFile()
    {
        RmPageObjectUtils.select(drone, FILE_BTN);
        return new RmUploadFilePage(drone);
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
        FilePlanPage filePlan = new FilePlanPage(drone);
        filePlan.setInRecordCategory(true);
        return filePlan;
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
        FilePlanPage filePlan = new FilePlanPage(drone);
        filePlan.setInRecordFolder(true);
        return filePlan;
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

    /**
     * Gets the record information for a given row index.
     * <p>
     * Note that this is zero indexed.
     *
     * @param index                 index
     * @return {@link RecordInfo}   record information
     */
    public RecordInfo getRecordInfo(final int index)
    {
        List<FileDirectoryInfo> list = getFiles();
        return new RecordInfo(drone, list.get(index));
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

    /**
     * Actyion verifies if folder is closed
     *
     * @param folderName Name of verified folder
     * @return true/false Is Folder closed or Not
     */
    public boolean isFolderClosed(String folderName)
    {
        WebDroneUtil.checkMandotaryParam("folderName", folderName);

        By closeIcon = By.xpath("//a[contains(text(), '" + folderName + "')]" +
                "/ancestor::tr//div[@class='status']//img[@title='Closed']");
        return RmPageObjectUtils.isDisplayed(drone, closeIcon);
    }

    /**
     * Action of click on RmConsolePage button.
     *
     * @return {@link RmFolderRulesWithRules} page response
     */
    public RmConsolePage openRmConsolePage()
    {
        RmPageObjectUtils.select(drone, RECORD_MANAGEMENT_CONSOLE);
        return new RmConsolePage(drone).render();
    }

    /**
     * Action of click on Create Non-Electronic Record button.
     *
     * @return {@link CreateNewRecordDialog} page response
     */
    public CreateNewRecordDialog selectNewNonElectronicRecord()
    {
        RmPageObjectUtils.select(drone, NEW_FILE_BTN);
        drone.findAndWait(By.xpath("//div[contains(@class, 'panel-container')]")).isDisplayed();
        WebElement nonElectronic = drone.findAndWait(NON_ELECTRONIC_BUTTON);
        nonElectronic.click();
        return new CreateNewRecordDialog(drone).render();
    }

    /**
     * Action open Folder/Category Details page using MouseOver
     *
     * @param itemValue Value of Item that need to open Dettails Page
     * @return  {@link FolderDetailsPage} page response
     */
    public FolderDetailsPage openDetailsPage(String itemValue)
    {
        WebDroneUtil.checkMandotaryParam("itemValue", itemValue);

        FilePlanPage filePlan = drone.getCurrentPage().render();
        FileDirectoryInfo folder = filePlan.getFileDirectoryInfo(itemValue);
        WebElement selectMoreAction = folder.selectMoreAction();
        selectMoreAction.click();
        WebElement viewDetails = folder.findElement(By.cssSelector("div[class$='view-details']>a"));
        viewDetails.click();
        return new FolderDetailsPage(drone).render();
    }

    /**
     * Action click Cut Off for element
     *
     * @param itemValue Value of Item that should be CutOff
     * @return {@link FilePlanPage} page response
     */
    public FilePlanPage cutOffAction(String itemValue)
    {
        WebDroneUtil.checkMandotaryParam("itemValue", itemValue);

        FilePlanPage filePlan = drone.getCurrentPage().render();
        FileDirectoryInfo folder = filePlan.getFileDirectoryInfo(itemValue);
        WebElement selectMoreAction = folder.selectMoreAction();
        selectMoreAction.click();
        WebElement viewDetails = folder.findElement(By.cssSelector("div[class$='rm-cutoff']>a"));
        viewDetails.click();
        filePlan.setInRecordCategory(true);
        return new FilePlanPage(drone).render();
    }

    /**
     * Action open Record Details page using MouseOver
     *
     * @param itemValue value of created folder/category/record
     * @return  {@link FolderDetailsPage} page response
     */
    public FolderDetailsPage openRecordDetailsPage(String itemValue)
    {
        WebDroneUtil.checkMandotaryParam("itemValue", itemValue);

        drone.getCurrentPage().render();
        WebElement record = drone.findAndWait(By.xpath("//span//a[contains(text(), '" + itemValue + "')]"));
        record.click();
        return new FolderDetailsPage(drone).render();
    }

    /**
     * Action click Create Disposition on Category Details Page
     *
     * @return  {@link RmCreateDispositionPage} page response
     */
    public RmCreateDispositionPage openCreateDisposition()
    {
        click(CREATE_DISPOSITION_BUTTON);
        return new RmCreateDispositionPage(drone).render();
    }

    /**
     * Helper method that clicks by element
     *
     * @param locator element By locator
     */
    public void click(By locator)
    {
        WebDroneUtil.checkMandotaryParam("locator", locator);

        WebElement element = drone.findAndWait(locator);
        drone.mouseOverOnElement(element);
        element.click();
    }

    /**
     * Action create a category/subcategory
     *
     * @param categoryName Name of created Category
     * @param isRootFolder root or no-root node
     * @return {@link FilePlanPage} page response
     */
    public FilePlanPage createCategory(String categoryName, boolean isRootFolder)
    {
        WebDroneUtil.checkMandotaryParam("categoryName", categoryName);
        WebDroneUtil.checkMandotaryParam("isRootFolder", isRootFolder);

        FilePlanPage filePlan = drone.getCurrentPage().render();
        filePlan.setInFilePlanRoot(isRootFolder);
        filePlan = filePlan.render();

        CreateNewRecordCategoryDialog createNewCategory = filePlan.selectCreateNewCategory().render();
        createNewCategory.enterName(categoryName);
        createNewCategory.enterTitle(categoryName);
        createNewCategory.enterDescription(categoryName);

        filePlan = ((FilePlanPage) createNewCategory.selectSave());
        filePlan.setInFilePlanRoot(isRootFolder);
        return new FilePlanPage(drone).render();
    }

    /**
     * Action verifies if category exists in file plan
     *
     * @param categoryName name of Category
     * @return  true/false return is category present on page
     */
    public boolean isCategoryCreated(String categoryName)
    {
        WebDroneUtil.checkMandotaryParam("categoryName", categoryName);

        return RmPageObjectUtils.isDisplayed(drone, By.
                xpath("//span//a[contains(text(), '" + categoryName + "')]"));
    }

    /**
     * Action create a folder
     *
     * @param folderName Name of created Folder
     * @return {@link FilePlanPage} page response
     */
    public FilePlanPage createFolder(String folderName)
    {
        WebDroneUtil.checkMandotaryParam("folderName", folderName);

        FilePlanPage filePlan = drone.getCurrentPage().render();
        CreateNewRecordFolderDialog createNewFolder = filePlan.selectCreateNewFolder().render();
        createNewFolder.enterName(folderName);
        createNewFolder.enterTitle(folderName);
        createNewFolder.enterDescription(folderName);

        filePlan = ((FilePlanPage) createNewFolder.selectSave());
        filePlan.setInRecordCategory(true);
        return filePlan.render(folderName);
    }

    /**
     * Action create a record
     *
     * @param recordName Name of created Record
     * @return {@link FilePlanPage} page response
     */
    public FilePlanPage createRecord(String recordName)
    {
        WebDroneUtil.checkMandotaryParam("recordName", recordName);

        FilePlanPage filePlan = drone.getCurrentPage().render();
        CreateNewRecordDialog createNewRecord = filePlan.selectNewNonElectronicRecord();
        createNewRecord.enterName(recordName);
        createNewRecord.enterTitle(recordName);
        createNewRecord.enterDescription(recordName);

        filePlan = ((FilePlanPage) createNewRecord.selectSave());
        filePlan.setInRecordFolder(true);
        return filePlan.render(recordName);
    }

    /**
     * Action navigates to already created category/folder
     *
     * @param folderName Folder Name
     * @return {@link FilePlanPage} page response
     */
    public FilePlanPage navigateToFolder(String folderName)
    {
        WebDroneUtil.checkMandotaryParam("folderName", folderName);

        FilePlanPage filePlan = drone.getCurrentPage().render();
        FileDirectoryInfo recordCategory = filePlan.getFileDirectoryInfo(folderName);
        recordCategory.clickOnTitle();
        filePlan.setInRecordCategory(true);
        return new FilePlanPage(drone).render();
    }
}