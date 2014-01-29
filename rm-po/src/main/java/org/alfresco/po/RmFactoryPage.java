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
package org.alfresco.po;

import org.alfresco.po.rm.FilePlanPage;
import org.alfresco.po.rm.RmConsolePage;
import org.alfresco.po.rm.RmDashBoardPage;
import org.alfresco.po.rm.RmSiteMembersPage;
import org.alfresco.po.rm.RecordSearchPage;
import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.ShareErrorPopup;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.utils.RmUtils;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Extends the {@link FactorySharePage} for RM specific methods
 *
 * @author Tuna Aksoy
 * @since 2.2
 */
public class RmFactoryPage extends FactorySharePage
{
    private static final String RM_S = "rm-%s";
    private static final String SITE_RM = "site/rm";
    private static final String DASHBOARD = "dashboard";
    private static final String LOGIN = "login";
    private static final String RM_FILE_PLAN = "rm-documentlibrary";
    private static final String RM_DASHBOARD = "rm-dashboard";
    private static final String RM_RMSEARCH = "rm-rmsearch";
    private static final String RM_CONSOLE = "rm-console";
    private static final String RM_SITE_MEMBERS = "rm-site-members";

    /**
     * Constructor.
     */
    public RmFactoryPage()
    {
        super();
        pages.put(RM_SITE_MEMBERS, RmSiteMembersPage.class);
        pages.put(RM_CONSOLE, RmConsolePage.class);
        pages.put(RM_RMSEARCH, RecordSearchPage.class);
        pages.put(RM_DASHBOARD, RmDashBoardPage.class);
        pages.put(RM_FILE_PLAN, FilePlanPage.class);
    }

    /**
     * @see org.alfresco.po.share.FactorySharePage#getPage(org.alfresco.webdrone.WebDrone)
     */
    public HtmlPage getPage(WebDrone drone)
    {
        RmUtils.checkMandotaryParam("drone", drone);

        return RmFactoryPage.resolvePage(drone);
    }

    /**
     * Creates the appropriate page object based on the current page the
     * {@link WebDrone} is on.
     *
     * @param drone {@link WebDrone} Alfresco unmanned web browser client
     * @return {@link SharePage} The page object response
     * @throws PageException
     */
    public static HtmlPage resolvePage(final WebDrone drone) throws PageException
    {
        RmUtils.checkMandotaryParam("drone", drone);

        // Determine if user is logged in if not return login page
        String title = drone.getTitle().toLowerCase();
        if (title.contains(LOGIN))
        {
            return new LoginPage(drone);
        }
        else
        {
            try
            {
                WebElement errorPrompt = drone.find(By.cssSelector(FAILURE_PROMPT));
                if (errorPrompt.isDisplayed())
                {
                    return new ShareErrorPopup(drone);
                }
            }
            catch (NoSuchElementException nse)
            {
            }

            // Determine what page we're on based on url
            return RmFactoryPage.getPage(drone.getCurrentUrl(), drone);
        }
    }

    /**
     * Resolves the required page based on the URL containing a keyword
     * that identify's the page the drone is currently on. Once a the name
     * is extracted it is used to get the class from the map which is
     * then instantiated.
     *
     * @param driver {@link WebDriver} Browser client
     * @return {@link SharePage} Share page object
     */
    public static SharePage getPage(final String url, WebDrone drone)
    {
        RmUtils.checkMandotaryParam("url", url);
        RmUtils.checkMandotaryParam("drone", drone);

        String pageName = RmFactoryPage.resolvePage(url);
        return instantiatePage(drone, pages.get(pageName));
    }

    /**
     * Extracts the String value from the last occurrence of slash in the url.
     *
     * @param url {@link String} url.
     * @return {@link String} Page title
     */
    protected static String resolvePage(String url)
    {
        RmUtils.checkMandotaryParam("url", url);

        if (url.endsWith(DASHBOARD) && url.contains(SITE_RM))
        {
            return RM_DASHBOARD;
        }

        // Check if rm based url
        if (url.contains(SITE_RM))
        {
            String val[] = url.split(SITE_RM + "/");
            return String.format(RM_S, val[1]);
        }

        return FactorySharePage.resolvePage(url);
    }
}