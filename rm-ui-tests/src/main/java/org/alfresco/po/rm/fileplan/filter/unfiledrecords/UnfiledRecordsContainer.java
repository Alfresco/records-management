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
package org.alfresco.po.rm.fileplan.filter.unfiledrecords;

import java.util.List;
import java.util.NoSuchElementException;

import org.alfresco.po.rm.RmFolderRulesPage;
import org.alfresco.po.rm.RmUploadFilePage;
import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.fileplan.toolbar.CreateNewRecordFolderDialog;
import org.alfresco.po.rm.util.RmPageObjectUtils;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.webdrone.ElementState;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * File plan filter for unfiled records container
 *
 * @author Tuna Aksoy
 * @since 2.2
 */
public class UnfiledRecordsContainer extends FilePlanPage
{
    protected static final By NEW_UNFILED_RECORDS_FOLDER_BTN = By.cssSelector("button[id$='default-newUnfiledRecordsFolder-button-button']");
    protected static final By NEW_DECLARE_RECORD_BTN = By.cssSelector("button[id$='default-declareRecord-button-button']");
    protected static final By MANAGE_RULES_BTN = By.cssSelector("button[id$='_default-unfiledManageRules-button-button']");

    /**
     * Constructor.
     *
     * @param drone {@link WebDrone}
     */
    public UnfiledRecordsContainer(WebDrone drone)
    {
        super(drone);
    }

    /**
     * @see org.alfresco.webdrone.Render#render(org.alfresco.webdrone.RenderTime)
     */
    @SuppressWarnings("unchecked")
    @Override
    public UnfiledRecordsContainer render(RenderTime timer)
    {
        WebDroneUtil.checkMandotaryParam("timer", timer);

        return render(timer, null);
    }

    /**
     * @see org.alfresco.webdrone.Render#render(long)
     */
    @SuppressWarnings("unchecked")
    @Override
    public UnfiledRecordsContainer render(long time)
    {
        WebDroneUtil.checkMandotaryParam("time", time);

        RenderTime timer = new RenderTime(time);
        return render(timer);
    }

    /**
     * @see org.alfresco.webdrone.Render#render()
     */
    @SuppressWarnings("unchecked")
    @Override
    public UnfiledRecordsContainer render()
    {
        RenderTime timer = new RenderTime(maxPageLoadingTime);
        return render(timer);
    }

    /**
     * Renders the page and waits until the element with the
     * expected name has has been displayed.
     *
     * @param expectedName {@link String} The name of the expected element
     * @return {@link UnfiledRecordsContainer} The unfiled records container displaying the expected element
     */
    public UnfiledRecordsContainer render(String expectedName)
    {
        // "expectedName" can be blank

        RenderTime timer = new RenderTime(maxPageLoadingTime);
        return render(timer, expectedName);
    }

    /**
     * Renders the page and waits until the element with the
     * expected name has has been displayed.
     *
     * timer {@link RenderTime} time to wait
     * @param expectedName {@link String} The name of the expected element
     * @return {@link UnfiledRecordsContainer} The unfiled records container displaying the expected element
     */
    private UnfiledRecordsContainer render(RenderTime timer, String expectedName)
    {
        WebDroneUtil.checkMandotaryParam("timer", timer);
        // "expectedName" can be blank

        timer.start();
        try
        {
            RenderElement filePlan = RenderElement.getVisibleRenderElement(FILEPLAN);
            RenderElement filePlanNav = RenderElement.getVisibleRenderElement(FILEPLAN_NAV);
            RenderElement unfiledRecordsFolderButton = new RenderElement(NEW_UNFILED_RECORDS_FOLDER_BTN, ElementState.CLICKABLE);
            RenderElement newDeclareRecordButton = new RenderElement(NEW_DECLARE_RECORD_BTN, ElementState.CLICKABLE);

            elementRender(timer, filePlan, filePlanNav, unfiledRecordsFolderButton, newDeclareRecordButton);
            setViewType(getNavigation().getViewType());

            if (StringUtils.isNotBlank(expectedName))
            {
                while (true)
                {
                    if (timer.timeLeft() <= 0)
                    {
                        throw new PageRenderTimeException("'" + this.getClass().getName() + "' failed to render in time.");
                    }
                    boolean found = false;
                    for (FileDirectoryInfo fileDirectoryInfo : getFiles())
                    {
                        if (fileDirectoryInfo.getName().contains(expectedName))
                        {
                            found = true;
                            break;
                        }
                    }
                    if (found)
                    {
                        break;
                    }
                    else
                    {
                        continue;
                    }
                }
            }

        }
        catch (NoSuchElementException e)
        {
            // todo  .. lets output the error somewhere!
        }
        finally
        {
            timer.end();
        }
        return this;
    }

    /**
     * Action mimicking select click on new folder button for unfiled records container.
     *
     * @return {@link CreateNewRecordFolderDialog} Returns the create folder folder form dialog
     */
    public CreateNewRecordFolderDialog selectCreateNewUnfiledRecordsContainerFolder()
    {
        RmPageObjectUtils.select(drone, NEW_UNFILED_RECORDS_FOLDER_BTN);
        return new CreateNewRecordFolderDialog(drone);
    }

    /**
     * Action mimicking select click on file button for unfiled records container.
     *
     * @return {@link RmUploadFilePage} Returns the upload file page for RM
     */
    public RmUploadFilePage selectCreateNewUnfiledRecordsContainerFile()
    {
        RmPageObjectUtils.select(drone, NEW_DECLARE_RECORD_BTN);
        return new RmUploadFilePage(drone);
    }

    /**
     * Action of click on manage rules button.
     *
     * @return {@link RmFolderRulesPage} folder rules page
     */
    public RmFolderRulesPage selectManageRules()
    {
        RmPageObjectUtils.select(drone, MANAGE_RULES_BTN);
        return new RmFolderRulesPage(drone);
    }

    /**
     * Selects the title of the folder link.
     *
     * @param title String folder title
     * @return FilePlanPage page response object
     */
    public UnfiledRecordsContainer selectUnfiledFolder(final String title, final long timeout)
    {
        if(title == null || title.isEmpty()) throw new IllegalArgumentException("Title is required");
        String search = String.format("//h3/a[text()='%s']",title);
        WebElement element = drone.findAndWait(By.xpath(search), timeout);
        element.click();
        UnfiledRecordsContainer unfiledRecordsContainer = new UnfiledRecordsContainer(drone);
        unfiledRecordsContainer.render(timeout);
        return unfiledRecordsContainer;
    }

    /**
     * The super.getFiles() occasionally throws an unsupported operation exception du to a timing
     * issue. If this happens call the method again.
     *
     * @see org.alfresco.po.share.site.document.DocumentLibraryPage#getFiles()
     */
    public List<FileDirectoryInfo> getFiles()
    {
        try
        {
            return super.getFiles();
        }
        catch(UnsupportedOperationException e) { }
        return getFiles();
    }
}