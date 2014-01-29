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

import org.alfresco.po.share.site.CreateSitePage;
import org.alfresco.po.share.site.SiteType;
import org.alfresco.po.utils.RmUtils;
import org.alfresco.webdrone.ElementState;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.lang3.StringUtils;
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
    private static final By SITE_PRESET = By.id("alfresco-rm-createSite-instance-sitePreset");
    private static final By OPTION_COLLAB_SITE = By.cssSelector("option:nth-of-type(1)");
    private static final By OPTION_RM_SITE = By.cssSelector("option:nth-of-type(2)");
    private static final By OPTION = By.tagName("option");
    private static final By SELECT = By.tagName("select");

    /**
     * Constructor.
     *
     * @param drone {@link WebDrone}
     */
    public RMCreateSitePage(WebDrone drone)
    {
        super(drone);
    }

    /**
     * @see org.alfresco.po.share.site.CreateSitePage#render(org.alfresco.webdrone.RenderTime)
     */
    @SuppressWarnings("unchecked")
    @Override
    public RMCreateSitePage render(RenderTime timer)
    {
        RmUtils.checkMandotaryParam("timer", timer);

        return (RMCreateSitePage) super.render(timer);
    }

    /**
     * @see org.alfresco.po.share.site.CreateSitePage#render()
     */
    @SuppressWarnings("unchecked")
    @Override
    public RMCreateSitePage render()
    {
        return (RMCreateSitePage) super.render();
    }

    /**
     * @see org.alfresco.po.share.site.CreateSitePage#render(long)
     */
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

    /**
     * @see org.alfresco.po.share.site.CreateSitePage#createSite(java.lang.String, java.lang.String, org.alfresco.po.share.site.SiteType)
     */
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
                WebElement siteNameElement = drone.findAndWait(INPUT_TITLE);
                siteNameElement.sendKeys(siteName);
                if (StringUtils.isNotBlank(description))
                {
                    WebElement descriptionElement = drone.find(INPUT_DESCRIPTION);
                    descriptionElement.clear();
                    descriptionElement.sendKeys(description);
                }
                selectSiteType(siteType);
                return submit(SUBMIT_BUTTON, ElementState.DELETE_FROM_DOM);

            default:
                throw new PageOperationException("No site type match found for: " + siteType +
                        " out of the following possible options: RecordsManagment or Collaboration");
        }
    }

    /**
     * @see org.alfresco.po.share.site.CreateSitePage#selectSiteType(org.alfresco.po.share.site.SiteType)
     */
    public void selectSiteType(SiteType siteType)
    {
        RmUtils.checkMandotaryParam("siteType", siteType);

        WebElement dropdown = drone.find(SELECT);
        // Check option size if only one in dropdown return.
        List<WebElement> options = dropdown.findElements(OPTION);
        if (options.isEmpty() || options.size() > 1)
        {
            WebElement siteOption;
            switch (siteType)
            {
                case RecordsManagement:
                    siteOption = dropdown.findElement(OPTION_RM_SITE);
                    break;

                case Collaboration:
                    siteOption = dropdown.findElement(OPTION_COLLAB_SITE);
                    break;

                default:
                    throw new PageOperationException("No suitable site type was found");
            }
            siteOption.click();
        }
    }

    /**
     * Checks if drop down contains option for records management.
     * This can be confirmed by checking the dropdown id.
     *
     * @return <code>true</code> if id matches alfresco-rm <code>false</code> otherwise
     */
    public boolean isRecordManagementTypeSupported()
    {
        try
        {
            WebElement sitePreset = drone.find(SITE_PRESET);
            return sitePreset.isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }
}