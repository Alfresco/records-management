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
package org.alfresco.po.rm;

import java.util.List;

import org.alfresco.po.rm.util.RmPageObjectUtils;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * This class extends the {@link UploadFilePage} in order to make filing records possible.
 * This is a temporary solution. After moving RM related classes to its own module this class won't be used anymore.
 *
 * @author Tuna Aksoy
 * @since 2.2
 */
public class RmUploadFilePage extends UploadFilePage
{
    protected static final By PROMPT_PANEL_ID = By.id("prompt");
    protected static final By BUTTON_TAG_NAME = By.tagName("button");
    
    protected static final String ELECTRONIC = "Electronic";
    protected static final String NONELECTRONIC = "Non-Electronic";
    protected static final String CANCEL = "Cancel";
    
    private static final By FILE_DATA_FILE = By.cssSelector("input[id$='default-filedata-file']");
    private static final By FILE_SELECTION = By.cssSelector("input.dnd-file-selection-button");
    private static final By HTML_UPLOAD = By.cssSelector("button[id*='html-upload']");
    private Log logger = LogFactory.getLog(this.getClass());

    /**
     * Constructor.
     *
     * @param drone {@link WebDrone}
     */
    public RmUploadFilePage(WebDrone drone)
    {
        super(drone);
    }

    /**
     * @see org.alfresco.po.share.site.UploadFilePage#render(org.alfresco.webdrone.RenderTime)
     */
    @SuppressWarnings("unchecked")
    @Override
    public RmUploadFilePage render(RenderTime timer)
    {
        WebDroneUtil.checkMandotaryParam("timer", timer);

        while (true)
        {
            timer.start();
            try
            {                
                if (RmPageObjectUtils.isDisplayed(drone, By.cssSelector("div#prompt_h")))
                {
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
     * @see org.alfresco.po.share.site.UploadFilePage#render()
     */
    @SuppressWarnings("unchecked")
    @Override
    public RmUploadFilePage render()
    {
        RenderTime timer = new RenderTime(maxPageLoadingTime);
        return render(timer);
    }

    /**
     * @see org.alfresco.po.share.site.UploadFilePage#render(long)
     */
    @SuppressWarnings("unchecked")
    @Override
    public RmUploadFilePage render(final long time)
    {
        RenderTime timer = new RenderTime(time);
        return render(timer);
    }
    
    /**
     * Select electronic from prompt dialog
     * 
     * @param drone web drone
     */
    public void selectElectronic(WebDrone drone)
    {
        WebElement prompt = drone.findAndWait(PROMPT_PANEL_ID);
        List<WebElement> elements = prompt.findElements(BUTTON_TAG_NAME);
        WebElement electronicRecordButton = findButton(ELECTRONIC, elements);
        electronicRecordButton.click();
    }
    
    /**
     * Select non electronic from prompt dialog
     * 
     * @param drone web drone
     */
    public void selectNonElectronic(WebDrone drone)
    {
        WebElement prompt = drone.findAndWait(PROMPT_PANEL_ID);
        List<WebElement> elements = prompt.findElements(BUTTON_TAG_NAME);
        WebElement electronicRecordButton = findButton(NONELECTRONIC, elements);
        electronicRecordButton.click();
    }
    
    /**
     * Select cancel from prompt dialog
     * 
     * @param drone web drone
     */
    public void selectCancel(WebDrone drone)
    {
        WebElement prompt = drone.findAndWait(PROMPT_PANEL_ID);
        List<WebElement> elements = prompt.findElements(BUTTON_TAG_NAME);
        WebElement electronicRecordButton = findButton(CANCEL, elements);
        electronicRecordButton.click();
    }

    /**
     * Action that selects the submit upload button.
     *
     * @return <code>true</code> if submitted <code>false</code> otherwise
     */
    private void submitUpload()
    {
        try
        {
            WebElement htmlUpload = drone.find(HTML_UPLOAD);
            HtmlElement okButton = new HtmlElement(htmlUpload, drone);
            String ready = okButton.click();

            if (logger.isTraceEnabled())
            {
                logger.trace(String.format("Operation completed in: %s",ready));
            }

            while (true)
            {
                try
                {
                    //Verify button has been actioned
                    if (!htmlUpload.isDisplayed())
                    {
                        break;
                    }
                }
                catch (NoSuchElementException e)
                {
                    break;
                }
            }

        }

        //Check result has been updated
        catch (TimeoutException te)
        {
        }
    }

    /**
     * Uploads a file by entering the file location into the input field and
     * submitting the form.
     *
     * @param filePath String file location to upload
     * @return {@link HtmlPage} File plan page response
     */
    public HtmlPage uploadFile(final String filePath)
    {
        WebElement fileSelection;
        if (alfrescoVersion.isFileUploadHtml5())
        {
            fileSelection = drone.find(FILE_SELECTION);
            fileSelection.sendKeys(filePath);
        }
        else
        {
            fileSelection = drone.find(FILE_DATA_FILE);
            fileSelection.sendKeys(filePath);
            submitUpload();
        }

        if (logger.isTraceEnabled())
        {
            logger.trace("Upload button has been actioned");
        }

        return RmFactoryPage.getPage(drone.getCurrentUrl(), drone);
    }
}