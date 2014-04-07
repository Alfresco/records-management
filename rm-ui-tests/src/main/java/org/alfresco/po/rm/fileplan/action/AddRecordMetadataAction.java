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
package org.alfresco.po.rm.fileplan.action;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Page object for 'Add Record Metadata' action
 * 
 * @author Roy Wetherall
 * @since 2.2
 */
@SuppressWarnings("unchecked")
public class AddRecordMetadataAction extends SharePage
{
    /** selectors */
    private static final By SELECT_RECORD_TYPES = By.cssSelector("select[id$='default-addRecordMetadataDialog-recordType']");
    private static final By OK = By.cssSelector("button[id$='ok-button']");
    private static final By CANCEL = By.cssSelector("button[id$='cancel-button']");
    
    /**
     * Constructor
     * 
     * @param drone     web drone
     */
    public AddRecordMetadataAction(WebDrone drone)
    {
        super(drone);
    }

    /**
     * @see org.alfresco.webdrone.Render#render(org.alfresco.webdrone.RenderTime)
     */
    @Override
    public AddRecordMetadataAction render(RenderTime timer)
    {
        WebDroneUtil.checkMandotaryParam("timer", timer);

        RenderElement recordTypes = RenderElement.getVisibleRenderElement(SELECT_RECORD_TYPES);
        RenderElement ok = RenderElement.getVisibleRenderElement(OK);
        RenderElement cancel = RenderElement.getVisibleRenderElement(CANCEL);
        
        elementRender(timer, recordTypes, ok, cancel);
        
        return this;
    }
    
    /**
     * @see org.alfresco.webdrone.Render#render(long)
     */
    @Override
    public AddRecordMetadataAction render(long time)
    {
        WebDroneUtil.checkMandotaryParam("time", time);

        RenderTime timer = new RenderTime(time);
        return render(timer);
    }

    /**
     * @see org.alfresco.webdrone.Render#render()
     */
    @Override
    public AddRecordMetadataAction render()
    {
        return render(maxPageLoadingTime);
    }
    
    /**
     * Get all the record aspects shown in the action dialog.
     * 
     * @return  {@link List}<{@link String}>    list of all the aspects shown
     */
    public List<String> getAllRecordAspects()
    {
        Select dropdown = new Select(drone.findAndWait(SELECT_RECORD_TYPES));
        List<WebElement> options = dropdown.getOptions();
        List<String> result = new ArrayList<String>(options.size());
        for (WebElement option : options)
        {
            result.add(option.getAttribute("value"));
        }
        return result;
    }
    
    /**
     * Get the selected record aspects
     * 
     * @return  {@link List}<{@link String}>    list of the selected aspects
     */
    public List<String> getSelectedRecordAspects()
    {
        Select dropdown = new Select(drone.findAndWait(SELECT_RECORD_TYPES));
        List<WebElement> options = dropdown.getAllSelectedOptions();
        List<String> result = new ArrayList<String>(options.size());
        for (WebElement option : options)
        {
            result.add(option.getAttribute("value"));
        }
        return result;        
    }
    
    /**
     * Set the selected record aspects
     * 
     * @param values    record aspects
     */
    public void setSelectedRecordAspects(List<String> values)
    {
        Select dropdown = new Select(drone.findAndWait(SELECT_RECORD_TYPES));
        for (String value : values)
        {
            dropdown.selectByValue(value);
        }
    }
    
    /**
     * Select ok button
     * 
     * @return  {@link HtmlPage}  
     */
    public HtmlPage clickOk()
    {
        WebElement save = drone.findAndWait(OK);
        save.click();
        canResume();
        return drone.getCurrentPage();
    }

    /**
     * Select cancel button
     * 
     * @return
     */
    public HtmlPage clickCancel()
    {
        WebElement cancel = drone.findAndWait(CANCEL);
        cancel.click();
        return drone.getCurrentPage();
    }
}
