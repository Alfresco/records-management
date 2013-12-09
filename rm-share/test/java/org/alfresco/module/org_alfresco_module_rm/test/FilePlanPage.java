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
package org.alfresco.module.org_alfresco_module_rm.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alfresco.po.share.Pagination;
import org.alfresco.po.share.site.ManageRulesPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
/**
 * Records managment file plan page, based on
 * the document library page with specific action
 * and operation related to file plan.
 * @author Michael Suzuki
 * @version 1.7.1
 */
public class FilePlanPage extends RMSitePage
{
    private static Log logger = LogFactory.getLog(FilePlanPage.class);
    private static final String PAGINATION_BUTTON_NEXT = "a.yui-pg-next";
    private static final String PAGINATION_BUTTON_PREVIOUS = "a.yui-pg-previous";
    private static final String JS_SCRIPT_CHECK_DOCLIST = "return Alfresco.util.ComponentManager.findFirst('Alfresco.DocumentList').widgets.dataTable._getViewRecords();";
    private static final By MANAGE_RULES_BTN = By.cssSelector("button[id$='_default-manageRules-button-button']");
    private static final By NEW_CATEGORY_BTN = By.cssSelector("button[id$='default-newCategory-button-button']");
    private static final By NODE_REF_CSS = By.cssSelector("td div.yui-dt-liner input");
    private static final String NODEREF_LOCATOR = "input[id^='checkbox-yui']";
    private static final By RM_ADD_META_DATA_LINK = By.cssSelector("div#onActionAddRecordMetadata a");
    private final boolean expectingRecord;

    /**
     * Constructor.
     * @param drone {@link WebDrone}
     */
    public FilePlanPage(WebDrone drone)
    {
        super(drone);
        this.expectingRecord = false;
    }
    /**
     * Constructor.
     * @param drone {@link WebDrone}
     */
    public FilePlanPage(WebDrone drone, boolean hasRecords)
    {
        super(drone);
        this.expectingRecord = hasRecords;
    }

    private static final String FILE_PLAN_TREE_ID = "template_x002e_tree_x002e_documentlibrary_x0023_rm-fileplan";

    @SuppressWarnings("unchecked")
    @Override
    public FilePlanPage render(RenderTime timer)
    {

        while(true)
        {
            timer.start();
            try
            {
                if(drone.find(By.id(FILE_PLAN_TREE_ID)).isDisplayed())
                {
                    if(expectingRecord)
                    {
                        if(hasRecords())
                        {
                            break;
                        }
                    }
                    else
                    {
                        break;
                    }
                }
            }
            catch (NoSuchElementException e){ }
            finally
            {
                timer.end();
            }
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FilePlanPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public FilePlanPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Checks visibility of create new category button
     * on the file plan page header.
     * @return true if visible
     */
    public boolean isCreateNewCategoryDisplayed()
    {
        try
        {
            return drone.find(NEW_CATEGORY_BTN).isDisplayed();
        }
        catch (NoSuchElementException nse){ }
        return false;
    }

    /**
     * Action mimicking select click on new category button.
     */
    public CreateNewCategoryForm selectCreateNewCategory()
    {
        drone.find(NEW_CATEGORY_BTN).click();
        return new CreateNewCategoryForm(drone);
    }

    /**
     * Verify if records exists.
     * @return true if visible
     */
    public boolean hasRecords()
    {
        try
        {
            return drone.find(By.cssSelector("tbody.yui-dt-data > tr")).isDisplayed();
        }
        catch (NoSuchElementException e){ }
        return false;
    }
    /**
     * Verify if header sub navigation has manage rules button visible.
     * @return true if button
     */
    public boolean isManageRulesDisplayed()
    {
        try
        {
            return drone.find(MANAGE_RULES_BTN).isDisplayed();
        }
        catch (NoSuchElementException e) { }
        return false;
    }

    /**
     * Action of click on manage rules button.
     * @return {@link RMManageRulesPage} page response
     */
    public ManageRulesPage selectManageRules()
    {
        drone.find(MANAGE_RULES_BTN).click();
        return new ManageRulesPage(drone);
    }
    /**
     * Checks if pagination next button is active.
     * @return true if next page exists
     */
    public boolean hasNextPage()
    {
        return Pagination.hasPaginationButton(drone, PAGINATION_BUTTON_NEXT);
    }

    /**
     * Checks if pagination previous button is active.
     * @return true if next page exists
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
     * Get html element on the side of the page with
     * file plan sub navigation also known as filters.
     * @return {@link FilePlanNavigation} side element filter
     */
    public FilePlanNavigation getFilePlanNavigation()
    {
        return new FilePlanNavigation(drone);
    }
    /**
     * The file plan filter description.
     * Options are unfiled records, transfers and holds.
     * @return String name of filtered view
     */
    public String getFilePlanDescription()
    {
        return drone.findAndWait(By.cssSelector("div[id$='_default-description'] div")).getText();
    }
    /**
     * Select a particular file directory info row based on the title.
     * @param title String item title
     * @return {@link FileDirectoryInfo} page response
     */
    public FileDirectoryInfo getFileDirectoryInfo(final String title)
    {
        if(title == null || title.isEmpty())
        {
            throw new IllegalArgumentException("Title is required");
        }
        try
        {
            WebElement row = drone.find(By.xpath(String.format("//h3//a[text()='%s']/../../../../..",title)));
            String nodeRef = row.findElement(NODE_REF_CSS).getAttribute("value");
            return new FileDirectoryInfo(nodeRef, row, drone);
        }
        catch (NoSuchElementException e)
        {
            throw new PageException(String.format("File directory info with title %s was not found",title), e);
        }
    }

    /**
     * Select a particular file directory info row
     * based on the count, the accepted range is 1-50.
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
            WebElement row = drone.find(By.cssSelector(String.format("tbody.yui-dt-data tr:nth-of-type(%d)", number)));
            String nodeRef = row.findElement(NODE_REF_CSS).getAttribute("id");
            return new FileDirectoryInfo(nodeRef, row, drone);
        }
        catch (NoSuchElementException e)
        {
            throw new PageException(String.format("File directory info row %d was not found",number), e);
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
            if(logger.isTraceEnabled()) logger.trace(String.format("Documet list has no files: %s", noFiles));

            if(noFiles)
            {
                return Collections.emptyList();
            }

            List<WebElement> results = drone.findAll(By.cssSelector(NODEREF_LOCATOR));
            if(logger.isTraceEnabled())
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
                    FileDirectoryInfo file = new FileDirectoryInfo(result.getAttribute("value"), result, drone);
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
        catch (NoSuchElementException e) { }
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
     * Checks document list is populated by injecting
     * a javascript in to an alfresco component that
     * renders the document list.
     * @return true if collection of documents exists
     */
    public boolean hasFiles()
    {
        try
        {
            ArrayList<?> objs = (ArrayList<?>) drone.executeJavaScript(JS_SCRIPT_CHECK_DOCLIST);
            if(!objs.isEmpty())
            {
                return true;
            }
        }
        catch (Exception e) { }
        return false;
    }
    /**
     * Checked if add record metadata link is visibile.
     * This is a records management feature.
     * @return true if link is visible
     */
    public boolean isAddRecordMetaDataVisible()
    {
        try
        {
            return drone.find(RM_ADD_META_DATA_LINK).isDisplayed();
        }
        catch (NoSuchElementException nse) { }
        return false;
    }
}
