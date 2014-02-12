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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.alfresco.po.share.Pagination;
import org.alfresco.po.share.site.ManageRulesPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

/**
 * Records management file plan page, based on the document library
 * page with specific action and operation related to file plan.
 *
 * @author Michael Suzuki
 * @author Tuna Aksoy
 * @version 1.7.1
 */
public class FilePlanPage extends RmSitePage
{
    private static Log logger = LogFactory.getLog(FilePlanPage.class);
    private static final String NODEREF_ID = "id";
    private static final String FILE_DIRECTORY_INFO_ROW_TITLE = "//h3//a[text()='%s']/../../../../..";
    private static final String FILE_DIRECTORY_INFO_ROW_NUMBER = "tbody.yui-dt-data tr:nth-of-type(%d)";
    private static final String VALUE = "value";
    private static final String PAGINATION_BUTTON_NEXT = "a.yui-pg-next";
    private static final String PAGINATION_BUTTON_PREVIOUS = "a.yui-pg-previous";
    private static final String JS_SCRIPT_CHECK_DOCLIST = "return Alfresco.util.ComponentManager.findFirst('Alfresco.DocumentList').widgets.dataTable._getViewRecords();";
    private static final String NODEREF_LOCATOR = "input[id^='checkbox-yui']";
    private static final By MANAGE_RULES_BTN = By.cssSelector("button[id$='_default-manageRules-button-button']");
    private static final By UNFILED_MANAGE_RULES_BTN = By.cssSelector("button[id$='_default-unfiledManageRules-button-button']");
    private static final By NEW_CATEGORY_BTN = By.cssSelector("button[id$='default-newCategory-button-button']");
    private static final By NEW_FOLDER_BTN = By.cssSelector("button[id$='default-newFolder-button-button']");
    private static final By NEW_FILE_BTN = By.cssSelector("button[id$='default-fileUpload-button-button']");
    private static final By NODE_REF_CSS = By.cssSelector("td div.yui-dt-liner input");
    private static final By RM_ADD_META_DATA_LINK = By.cssSelector("div#onActionAddRecordMetadata a");
    private static final By RECORD = By.cssSelector("tbody.yui-dt-data > tr");
    private static final By DESCRIPTION = By.cssSelector("div[id$='_default-description'] div");
    private static final By FILEPLAN = By.id("template_x002e_tree_x002e_documentlibrary_x0023_default");
    private boolean expectingRecord;
    private String expectedRecordName;

    /**
     * Indicates that a record/folder will be expected in the file plan
     *
     * @param expectingRecord <code>true</code> if a record/folder is expected <code>false</code> otherwise
     */
    public void setExpectingRecord(boolean expectingRecord)
    {
        this.expectingRecord = expectingRecord;
    }

    /**
     * Set the name of the expected record/folder
     *
     * @param expectedRecordName Name of the expected record/folder
     */
    public void setExpectedRecordName(String expectedRecordName)
    {
        this.expectedRecordName = expectedRecordName;
    }

    /**
     * Constructor.
     *
     * @param drone {@link WebDrone}
     */
    public FilePlanPage(WebDrone drone)
    {
        super(drone);
        this.expectingRecord = false;
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
        this.expectingRecord = hasRecords;
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
                    if (drone.getCurrentUrl().contains("filter=unfiledRecords"))
                    {
                        if (isUnfiledRecordsContainerFileDisplayed() && isUnfiledRecordsContainerFolderDisplayed())
                        {
                            long timeOut = TimeUnit.SECONDS.convert(maxPageLoadingTime, TimeUnit.MILLISECONDS);
                            drone.waitUntilElementClickable(NEW_FILE_BTN, timeOut);
                            drone.waitUntilElementClickable(NEW_FOLDER_BTN, timeOut);
                            if (expectingRecord)
                            {
                                if (StringUtils.isNotBlank(expectedRecordName))
                                {
                                    boolean found = false;
                                    for (FileDirectoryInfo fileDirectoryInfo : getFiles())
                                    {
                                        if (fileDirectoryInfo.getName().contains(expectedRecordName))
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
                    if (expectingRecord)
                    {
                        if (StringUtils.isNotBlank(expectedRecordName))
                        {
                            boolean found = false;
                            for (FileDirectoryInfo fileDirectoryInfo : getFiles())
                            {
                                if (fileDirectoryInfo.getName().contains(expectedRecordName))
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
        waitForEnabled(NEW_CATEGORY_BTN).click();
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
        waitForEnabled(NEW_FOLDER_BTN).click();

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
        waitForEnabled(NEW_FILE_BTN).click();
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
     * @return {@link RMManageRulesPage} page response
     */
    public RmManageRulesPage selectManageRules()
    {
        WebElement manageRules = drone.findAndWait(MANAGE_RULES_BTN);
        manageRules.click();
        return new RmManageRulesPage(drone);
    }

    /**
     * Action of click on manage rules button.
     *
     * @return {@link RMManageRulesPage} page response
     */
    public RmManageRulesPage selectUnfiledManageRules()
    {
        WebElement manageRules = drone.findAndWait(UNFILED_MANAGE_RULES_BTN);
        manageRules.click();
        return new RmManageRulesPage(drone);
    }

    /**
     * Checks if pagination next button is active.
     *
     * @return <code>true</code> if next page exists <code>false</code> otherwise
     */
    public boolean hasNextPage()
    {
        return Pagination.hasPaginationButton(drone, PAGINATION_BUTTON_NEXT);
    }

    /**
     * Checks if pagination previous button is active.
     *
     * @return <code>true</code> if next page exists <code>false</code> otherwise
     */
    public boolean hasPreviousPage()
    {
        return Pagination.hasPaginationButton(drone, PAGINATION_BUTTON_PREVIOUS);
    }

    /**
     * Selects the button next on the pagination bar.
     */
    public void selectNextPage()
    {
        Pagination.selectPagiantionButton(drone, PAGINATION_BUTTON_NEXT);
    }

    /**
     * Selects the button previous on the pagination bar.
     */
    public void selectPreviousPage()
    {
        Pagination.selectPagiantionButton(drone, PAGINATION_BUTTON_PREVIOUS);
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
     * Select a particular file directory info row based on the title.
     *
     * @param title {@link String} item title
     * @return {@link FileDirectoryInfo} page response
     */
    public FileDirectoryInfo getFileDirectoryInfo(final String title)
    {
        WebDroneUtil.checkMandotaryParam("title", title);

        try
        {
            String formattedRow = String.format(FILE_DIRECTORY_INFO_ROW_TITLE, title);
            By RowByXpath = By.xpath(formattedRow);
            WebElement row = drone.find(RowByXpath);
            WebElement nodeRefElement = row.findElement(NODE_REF_CSS);
            String nodeRef = nodeRefElement.getAttribute(VALUE);
            return new FileDirectoryInfo(nodeRef, row, drone);
        }
        catch (NoSuchElementException e)
        {
            throw new PageException(String.format("File directory info with title %s was not found", title), e);
        }
    }

    /**
     * Select a particular file directory info row
     * based on the count, the accepted range is 1-50.
     *
     * @param number Integer item row
     * @return {@link FileDirectoryInfo} page response
     */
    public FileDirectoryInfo getFileDirectoryInfo(final Integer number)
    {
        if(number == null || !((number > 0) && (number < 50)))
        {
            throw new IllegalArgumentException("A valid number range of 1 to 50 is required");
        }

        try
        {
            String formattedRow = String.format(FILE_DIRECTORY_INFO_ROW_NUMBER, number);
            By rowSelector = By.cssSelector(formattedRow);
            WebElement row = drone.find(rowSelector);
            WebElement nodeRefElement = row.findElement(NODE_REF_CSS);
            String nodeRef = nodeRefElement.getAttribute(NODEREF_ID);
            return new FileDirectoryInfo(nodeRef, row, drone);
        }
        catch (NoSuchElementException e)
        {
            throw new PageException(String.format("File directory info row %d was not found", number), e);
        }
    }

    /**
     * Extracts the results from result table that matches the file name.
     *
     * @return Collection of {@link FileDirectoryInfo} relating to result
     */
    public List<FileDirectoryInfo> getFiles()
    {
        try
        {
            boolean noFiles = !hasFiles();
            if (logger.isTraceEnabled())
            {
                logger.trace(String.format("Documet list has no files: %s", noFiles));
            }

            if (noFiles)
            {
                return Collections.emptyList();
            }

            By nodeRefLocator = By.cssSelector(NODEREF_LOCATOR);
            List<WebElement> results = drone.findAll(nodeRefLocator);
            if (logger.isTraceEnabled())
            {
                logger.trace(String.format("getFiles list is empty: %s file size %d",
                        results.isEmpty(),
                        results.size()));
            }

            if (!results.isEmpty())
            {
                List<FileDirectoryInfo> fileDirectoryList = new ArrayList<FileDirectoryInfo>();
                for (WebElement result : results)
                {
                    FileDirectoryInfo file = new FileDirectoryInfo(result.getAttribute(VALUE), result, drone);
                    if(logger.isTraceEnabled())
                    {
                        logger.trace("adding file" + file.getName());
                    }
                    fileDirectoryList.add(file);
                }
                return fileDirectoryList;
            }

            //Try again as we are expecting results.
            return getFiles();
        }
        catch (NoSuchElementException e)
        {
        }
        catch (StaleElementReferenceException e)
        {
            if(logger.isTraceEnabled())
            {
                logger.debug("found stale element retrying get files");
            }
        }
        //Try again as we should have results else upload instructions view would be piked up.
        return getFiles();
    }

    /**
     * Checks document list is populated by injecting a javascript in to
     * an alfresco component that renders the document list.
     *
     * @return <code>true</code> if collection of documents exists <code>false</code> otherwise
     */
    public boolean hasFiles()
    {
        try
        {
            ArrayList<?> objs = (ArrayList<?>) drone.executeJavaScript(JS_SCRIPT_CHECK_DOCLIST);
            if (!objs.isEmpty())
            {
                return true;
            }
        }
        catch (Exception e)
        {
        }
        return false;
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