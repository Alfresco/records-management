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
package org.alfresco.po.rm.fileplan.filter.hold;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.fileplan.toolbar.CreateNewHoldDialog;
import org.alfresco.po.rm.util.RmPageObjectUtils;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;

/**
 * File plan filter for holds container
 *
 * @author Tuna Aksoy
 * @since 2.2
 */
public class HoldsContainer extends FilePlanPage
{
    private static final By NEW_HOLD_BTN = By.cssSelector("button[id$='default-newHold-button-button']");

    /**
     * Constructor.
     *
     * @param drone {@link WebDrone}
     */
    public HoldsContainer(WebDrone drone)
    {
        super(drone);
    }

    /**
     * @see org.alfresco.webdrone.Render#render(org.alfresco.webdrone.RenderTime)
     */
    @SuppressWarnings("unchecked")
    @Override
    public HoldsContainer render(RenderTime timer)
    {
        WebDroneUtil.checkMandotaryParam("timer", timer);

        return render(timer, null);
    }

    /**
     * @see org.alfresco.webdrone.Render#render(long)
     */
    @SuppressWarnings("unchecked")
    @Override
    public HoldsContainer render(long time)
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
    public HoldsContainer render()
    {
        RenderTime timer = new RenderTime(maxPageLoadingTime);
        return render(timer);
    }

    /**
     * Renders the page and waits until the element with the
     * expected name has has been displayed.
     *
     * @param expectedName {@link String} The name of the expected element
     * @return {@link HoldsContainer} The holds container displaying the expected element
     */
    public HoldsContainer render(String expectedName)
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
     * @return {@link HoldsContainer} The holds container displaying the expected element
     */
    private HoldsContainer render(RenderTime timer, String expectedName)
    {
        WebDroneUtil.checkMandotaryParam("timer", timer);
        // "expectedName" can be blank so no check required

        while (true)
        {
            timer.start();
            try
            {
                if (RmPageObjectUtils.isDisplayed(drone, FILEPLAN) &&
                    RmPageObjectUtils.isDisplayed(drone, FILEPLAN_NAV)  && 
                    !isJSMessageDisplayed() && 
                    toolbarButtonsDisplayed())
                {
                    setViewType(getNavigation().getViewType());
                    
                    waitUntilToolbarButtonsClickable();
                    if (StringUtils.isNotBlank(expectedName))
                    {
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
                    else
                    {
                        break;
                    }
                }
                else
                {
                    continue;
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
     * Checks if the toolbar buttons are displayed.
     *
     * @return <code>true</code> if the toolbar buttons are displayed <code>false</code> otherwise
     */
    private boolean toolbarButtonsDisplayed()
    {
        return RmPageObjectUtils.isDisplayed(drone, NEW_HOLD_BTN);
    }

    /**
     * Waits until the toolbar buttons are clickable
     */
    private void waitUntilToolbarButtonsClickable()
    {
        long timeOut = TimeUnit.SECONDS.convert(maxPageLoadingTime, TimeUnit.MILLISECONDS);
        drone.waitUntilElementClickable(NEW_HOLD_BTN, timeOut);
    }

    /**
     * Action mimicking select click on new hold button.
     *
     * @return {@link CreateNewHoldDialog} Returns the new hold dialog
     */
    public CreateNewHoldDialog selectCreateNewHold()
    {
        RmPageObjectUtils.select(drone, NEW_HOLD_BTN);
        return new CreateNewHoldDialog(drone);
    }
}