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

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneImpl;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Records management site page abstract.
 *
 * @author Michael Suzuki
 * @author Tuna Aksoy
 * @version 1.7.1
 */
public abstract class RmSitePage extends SharePage
{
    /**
     * Constructor.
     *
     * @param drone {@link WebDrone}
     */
    protected RmSitePage(WebDrone drone)
    {
        super(drone);
    }

    /**
     * Get main navigation.
     *
     * @return {@link RmSiteNavigation} Navigation page object
     */
    public RmSiteNavigation getSiteNav()
    {
        return new RmSiteNavigation(drone);
    }
     
    /**
     * Wait for element to be enabled 
     * 
     * @param by
     */
    protected WebElement waitForEnabled(By by)
    {
        long maxPageLoadingTime = ((WebDroneImpl)drone).getMaxPageRenderWaitTime();        
        return waitForEnabled(by, new RenderTime(maxPageLoadingTime));
    }
    
    /**
     * Wait for element to be enabled
     * 
     * @param by
     * @param timer
     */
    protected WebElement waitForEnabled(By by, RenderTime timer)
    {
        while (true)
        {
            timer.start();
            try
            {
                WebElement webElement = drone.findAndWait(by);
                if (webElement.isEnabled() == true)
                {
                    return webElement;
                }
            }
            finally
            {
                timer.end();
            }
        }
        
    }
}