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
import org.alfresco.webdrone.WebDroneUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

public class RmCopyOrMoveUnfiledContentPage extends SharePage
{

    private final static long PATH_EXPAND_MAX_WAIT = 30000;

    public RmCopyOrMoveUnfiledContentPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RmCopyOrMoveUnfiledContentPage render(RenderTime timer)
    {
        WebDroneUtil.checkMandotaryParam("timer", timer);

        while (true)
        {
            timer.start();
            try
            {
                // if search body is found we are rendered
                By rmSearch = By.xpath("//span[contains(@class,'ygtvlabel') and text()='Unfiled Records']");
                WebElement rmSearchElement = drone.find(rmSearch);
                if (rmSearchElement.isDisplayed())
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

    @SuppressWarnings("unchecked")
    @Override
    public RmCopyOrMoveUnfiledContentPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public RmCopyOrMoveUnfiledContentPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public void selectPath(String path)
    {
        try
        {
            String[] pathElements = path.split("/");
            int depth = 1;
            for(String element : pathElements)
            {
                if((element != null) && !"".equals(element))
                {
                    WebElement pathElementSpan = drone.findAndWait(By.xpath("//table[contains(@class,'ygtvdepth" + depth + "')]//span[text()='" + element + "']"), PATH_EXPAND_MAX_WAIT);
                    pathElementSpan.click();
                    drone.findAndWait(By.xpath("//table[contains(@class,'ygtvdepth" + depth + "') and contains(@class,'ygtv-expanded')]//span[text()='" + element + "']"), PATH_EXPAND_MAX_WAIT);
                    depth++;
                }
            }
            WebElement okButton = drone.findAndWait(By.xpath("//button[contains(@id,'ok-button')]"), PATH_EXPAND_MAX_WAIT);
            okButton.click();
        }
        catch(TimeoutException e) { }
    }
}
