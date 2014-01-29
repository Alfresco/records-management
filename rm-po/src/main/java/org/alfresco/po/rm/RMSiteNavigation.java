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

import org.alfresco.po.share.site.AbstractSiteNavigation;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Records management site navigation.
 *
 * @author Michael Suzuki
 * @author Tuna Aksoy
 * @version 1.7.1
 */
public class RMSiteNavigation extends AbstractSiteNavigation
{
    private static final By MENU_BAR = By.cssSelector("div.alf-menu-bar");
    private static final By MORE_BTN = By.cssSelector("span#HEADER_SITE_MORE_PAGES_text");
    private static final By SITE_MEMBERS = By.cssSelector("div#HEADER_SITE_MEMBERS");
    private static final By SITE_MEMBERS_TXT = By.cssSelector("span#HEADER_SITE_MEMBERS_text");
    private static final By RECORD_SEARCH = By.cssSelector("div#HEADER_SITE_RMSEARCH");
    private static final By RECORD_SEARCH_TXT = By.cssSelector("span#HEADER_SITE_RMSEARCH_text");
    private static final By FILE_PLAN = By.cssSelector("div#HEADER_SITE_DOCUMENTLIBRARY");
    private static final By FILE_PLAN_TXT = By.cssSelector("span#HEADER_SITE_DOCUMENTLIBRARY_text");
    private static final By RM_CONSOLE_TXT = By.cssSelector("span#HEADER_SITE_RM_MANAGEMENT_CONSOLE_text");

    /**
     * Constructor.
     * @param drone {@link WebDrone}
     */
    public RMSiteNavigation(WebDrone drone)
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
            WebElement filePlanButton = find(FILE_PLAN);
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
     * Checks if More drop down is displayed.
     *
     * @return <code>true</code> if displayed <code>false</code> otherwise
     */
    public boolean isMoreDisplayed()
    {
        return isLinkDisplayed(MORE_BTN);
    }

    /**
     * Checks if More drop down is displayed.
     *
     * @return <code>true</code> if displayed <code>false</code> otherwise
     */
    public void selectMore()
    {
        WebElement moreButton = drone.find(MORE_BTN);
        moreButton.click();
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
            selectMore();
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
        return isLinkDisplayed(RECORD_SEARCH);
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
     * Mimics selecting the site members link on site navigation bar.
     *
     * @return {@link RMSiteMembersPage} Return the records management site member page
     */
    public RMSiteMembersPage selectSiteMembers()
    {
        if (!isSelectSiteMembersDisplayed())
        {
            selectMore();
        }

        select(SITE_MEMBERS_TXT);
        return new RMSiteMembersPage(drone);
    }

    /**
     * Checks if site members is displayed.
     *
     * @return <code>true</code> if displayed <code>false</code> otherwise
     */
    public boolean isSelectSiteMembersDisplayed()
    {
        return isLinkDisplayed(SITE_MEMBERS);
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
     * @return {@link RMConsolePage} Returns the records management console page
     */
    public RMConsolePage selectRMConsole()
    {
        selectMore();
        select(RM_CONSOLE_TXT);
        return new RMConsolePage(drone);
    }
}