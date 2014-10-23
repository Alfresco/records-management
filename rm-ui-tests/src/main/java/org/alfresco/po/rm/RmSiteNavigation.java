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

import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.share.site.SiteNavigation;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Records management site navigation.
 *
 * @author Michael Suzuki
 * @author Tuna Aksoy
 * @version 1.7.1
 */
public class RmSiteNavigation extends SiteNavigation
{
    private static final By MENU_BAR = By.cssSelector("#HEADER_NAVIGATION_MENU_BAR");
    private static final By RECORD_SEARCH = By.cssSelector("div#HEADER_SITE_RMSEARCH");
    private static final By RECORD_SEARCH_TXT = By.cssSelector("span#HEADER_SITE_RMSEARCH_text");
    private static final By FILE_PLAN = By.cssSelector("div#HEADER_SITE_DOCUMENTLIBRARY");
    private static final By FILE_PLAN_TXT = By.cssSelector("span#HEADER_SITE_DOCUMENTLIBRARY_text");
    private static final By RM_CONSOLE_TXT = By.cssSelector("span#HEADER_SITE_RM_MANAGEMENT_CONSOLE_text");

    /**
     * Constructor.
     * @param drone {@link WebDrone}
     */
    public RmSiteNavigation(WebDrone drone)
    {
        super(drone);
        WebElement menuBar = drone.find(MENU_BAR);
        setWebElement(menuBar);
    }

    /**
     * Check if the site navigation has file plan link highlighted.
     * This is only available in RM module and is basically doc lib with a different label
     *
     * @return <code>true</code> if link is highlighted <code>false</code> otherwise
     */
    public boolean isFilePlanActive()
    {
        return isLinkActive(FILE_PLAN);
    }

    /**
     * Checks if File plan link is displayed.
     *
     * @return <code>true</code> if displayed <code>false</code> otherwise
     */
    public boolean isFilePlanDisplayed()
    {
        try
        {
            WebElement filePlanButton = drone.find(FILE_PLAN);
            // Do a text check as its using the same css as doc lib.
            String label = filePlanButton.getText();
            String filePlan = "File plan";
            if (StringUtils.isNotBlank(label) && filePlan.equalsIgnoreCase(label.trim()))
            {
                return true;
            }
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * Mimics selecting the file plan link on site navigation bar.
     *
     * @return {@link FilePlanPage} Returns the file plan page object
     */
    public FilePlanPage selectFilePlan()
    {
        select(FILE_PLAN_TXT);
        return new FilePlanPage(getDrone());
    }

    /**
     * Mimics selecting the record search link on site navigation bar.
     *
     * @return {@link RecordSearchPage} Return the record search page
     */
    public RecordSearchPage selectRecordSearch()
    {
        if (!isRecordSearchDisplayed())
        {
            clickMoreIfExist();
        }
        select(RECORD_SEARCH_TXT);
        return new RecordSearchPage(drone);
    }

    /**
     * Checks if record search is displayed.
     *
     * @return <code>true</code> if displayed <code>false</code> otherwise
     */
    public boolean isRecordSearchDisplayed()
    {
        return isLinkDisplayed(RECORD_SEARCH_TXT);
    }

    /**
     * Checks if record search link is active.
     *
     * @return <code>true</code> if  high lighted <code>false</code> otherwise
     */
    public boolean isRecordSearchActive()
    {
        return isLinkActive(RECORD_SEARCH);
    }

    /**
     * Checks if site members link is active.
     *
     * @return <code>true</code> if high lighted <code>false</code> otherwise
     */
    public boolean isSelectSiteMembersActive()
    {
        return isLinkActive(SITE_MEMBERS);
    }

    /**
     * Mimics selecting the records management console link on site navigation bar.
     *
     * @return {@link RmConsolePage} Returns the records management console page
     */
    public RmConsolePage selectRMConsole()
    {
        if(isMoreDisplayed())
        {
            clickMoreIfExist();
        }
        select(RM_CONSOLE_TXT);
        return new RmConsolePage(drone);
    }
    
    /**
     * Checks if RmconsolePage is displayed.
     * @return<code>true</code> if displayed<code>false</code>otherwise
     */
    public boolean isRmConsolePageDisplayed()
    {
        return isLinkDisplayed(RM_CONSOLE_TXT);
    }
    
    /**
     * Checks if item is displayed.
     *
     * @return true if displayed
     */
    public boolean isLinkDisplayed(final By by)
    {
        if (by != null)
        {
            try
            {
                return drone.findAndWait(by).isDisplayed();
            }
            catch (TimeoutException te)
            {
            }
        }
        return false;
    }
}