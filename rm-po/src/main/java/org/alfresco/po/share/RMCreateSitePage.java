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
package org.alfresco.po.share;

import java.util.List;

import org.alfresco.po.rm.RMDashBoardPage;
import org.alfresco.po.share.site.CreateSitePage;
import org.alfresco.po.share.site.SiteType;
import org.alfresco.webdrone.ElementState;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Extends {@link CreateSitePage} in order to add RM specific methods
 *
 * @author Tuna Aksoy
 * @since 2.2
 */
public class RMCreateSitePage extends CreateSitePage
{
    public RMCreateSitePage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RMCreateSitePage render(RenderTime timer)
    {
        return (RMCreateSitePage) super.render(timer);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RMCreateSitePage render()
    {
        return (RMCreateSitePage) super.render();
    }

    @SuppressWarnings("unchecked")
    @Override
    public RMCreateSitePage render(final long time)
    {
        return (RMCreateSitePage) super.render(time);
    }

    /**
     * Create a new public site action.
     *
     * @param siteName String mandatory field
     * @return {@link HtmlPage} page response
     */
    public HtmlPage createRMSite()
    {
        // FIXME!!!
        return createNewSite(null, null, false, false, SiteType.RecordsManagement);
    }

    protected HtmlPage createSite(final String siteName, final String description,
            final SiteType siteType)
    {
        switch (siteType)
        {
            case RecordsManagement:
                selectSiteType(siteType);
                drone.find(SUBMIT_BUTTON).click();
                return new RMDashBoardPage(drone);

            case Collaboration:
                WebElement inputSiteName = drone.findAndWait(INPUT_TITLE);
                inputSiteName.sendKeys(siteName);
                if(description != null)
                {
                    WebElement inputDescription = drone.find(INPUT_DESCRIPTION);
                    inputDescription.clear();
                    inputDescription.sendKeys(description);
                }
                selectSiteType(siteType);
                return submit(SUBMIT_BUTTON, ElementState.DELETE_FROM_DOM);
                //drone.find(SUBMIT_BUTTON).click();
                //return new SiteDashboardPage(drone);
            default:
                throw new PageOperationException("No site type match found for: " + siteType +
                        " out of the following possible options: RecordsManagment or Collaboration");
        }
    }
    /**
     * Action of selecting site type drop down.
     * @param type of site
     */
    public void selectSiteType(SiteType siteType)
    {
        WebElement dropdown = drone.find(By.tagName("select"));
        //Check option size if only one in dropdown return.
        List<WebElement> options = dropdown.findElements(By.tagName("option"));
        if(options.isEmpty() || options.size() > 1)
        {
            WebElement siteOption;
            switch (siteType)
            {
            case RecordsManagement:
                siteOption = dropdown.findElement(By.cssSelector("option:nth-of-type(2)"));
                break;
            case Collaboration:
                siteOption = dropdown.findElement(By.cssSelector("option:nth-of-type(1)"));
                break;
            default:
                throw new PageOperationException("No suitable site type was found");
            }
            siteOption.click();
        }
    }

    /**
     * Checks if drop down contains option for records management.
     * This can be confirmed by checking the dropdown id
     * @return true if id matches alfresco-rm
     */
    public boolean isRecordManagementTypeSupported()
    {
        try
        {
            return drone.find(By.id("alfresco-rm-createSite-instance-sitePreset")).isDisplayed();
        }
        catch (NoSuchElementException e){}
        return false;
    }
}
