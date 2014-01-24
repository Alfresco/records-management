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

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

/**
 * Records management console page object.
 * @author Michael Suzuki
 *
 */
public class RMConsolePage extends RMSitePage
{
    public RMConsolePage(WebDrone drone)
    {
        super(drone);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public RMConsolePage render(RenderTime timer)
    {
        while(true)
        {
            timer.start();
            try
            {
                //if search body is found we are rendered
                if(drone.find(By.cssSelector("div[id$='_rm-console']")).isDisplayed())
                {
                    break;
                }
            }
            catch (NoSuchElementException e){ }
            finally { timer.end();}
        }
        return this;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public RMConsolePage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public RMConsolePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
    
}
