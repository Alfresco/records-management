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
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

public class RmCopyOrMoveUnfiledContentPage extends SharePage
{

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
      
  
    public RmCopyOrMoveUnfiledContentPage render(RenderTime timer, String typePage)
    {
        WebDroneUtil.checkMandotaryParam("timer", timer);

        while (true)
        {
            timer.start();
            try
            {
                // if search body is found we are rendered
                 By rmSearch = By.xpath("//span[contains(@class,'ygtvlabel') and text()= '" + typePage + "']");

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
            for(String element : pathElements)
            {
                if((element != null) && !"".equals(element))
                {
                    By xpath = By.xpath("//td[contains(@class,' ygtvcontent')]/span[text()='" + element + "']");
                    drone.waitUntilElementClickable(xpath, 10);
                    try
                    {
                        drone.find(xpath).click();
                    }
                    catch (StaleElementReferenceException e)
                    {
                        drone.find(xpath).click();   
                    }
                }
            }
            By OK_BUTTON = By.xpath("//div[contains(@id, 'treeview')]/../div[@class='bdft']/span[contains(@id, 'ok')]/span/button[contains(@id, 'ok-button')]");
            drone.waitForElement(OK_BUTTON, 10);
            WebElement okButton = drone.findFirstDisplayedElement(OK_BUTTON);
            okButton.click();
            drone.waitUntilVisible(By.cssSelector("div.bd"), "Successfully", 10);
            drone.waitUntilNotVisibleWithParitalText(By.cssSelector("div.bd"), "Successfully", 10);
        }
        catch(TimeoutException e) 
        {
            System.out.println(e.getMessage());
        }
    }
    //Remove after closing the Jira
   /* public void moveSelectPath(String path)
    {
        try
        {
            String[] pathElements = path.split("/");
            for(String element : pathElements)
            {
                if((element != null) && !"".equals(element))
                {
                    By xpath = By.xpath("//td[contains(@class,' ygtvcontent')]/span[text()='" + element + "']");
                    drone.waitUntilElementClickable(xpath, 10);
                    try
                    {
                        drone.find(xpath).click();
                    }
                    catch (StaleElementReferenceException e)
                    {
                        drone.find(xpath).click();   
                    }
                }
            }
            WebElement okButton = drone.findAndWait(By.xpath("//button[contains(@id,'ok-button')]"));
            okButton.click();
        }
        catch(TimeoutException e) { }
    }*/
}
