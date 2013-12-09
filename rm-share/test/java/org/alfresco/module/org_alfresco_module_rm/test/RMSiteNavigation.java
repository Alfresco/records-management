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
package org.alfresco.module.org_alfresco_module_rm.test;

import org.alfresco.po.share.site.AbstractSiteNavigation;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;


/**
 * Records management site navigation.
 * @author Michael Suzuki
 * @version 1.7.1
 *
 */
public class RMSiteNavigation extends AbstractSiteNavigation
{
    private static final By RM_CONSOLE_TXT_CSS_SELECTOR = By.cssSelector("span#HEADER_SITE_RM_MANAGEMENT_CONSOLE_text");
    private static final By SITE_MEMBERS_CSS_SELECTOR = By.cssSelector("div#HEADER_SITE_MEMBERS");
    private static final By SITE_MEMBERS_TXT_CSS_SELECTOR = By.cssSelector("span#HEADER_SITE_MEMBERS_text");
    private static final By RM_SEARCH_CSS = By.cssSelector("div#HEADER_SITE_RMSEARCH");
    private static final By RM_SEARCH_TXT_CSS = By.cssSelector("span#HEADER_SITE_RMSEARCH_text");
    private static final By MORE_BTN_CSS = By.cssSelector("span#HEADER_SITE_MORE_PAGES_text");
    private static final By FILE_PLAN_CSS = By.cssSelector("div#HEADER_SITE_DOCUMENTLIBRARY");
    private static final By FILE_PLAN_TXT_CSS = By.cssSelector("span#HEADER_SITE_DOCUMENTLIBRARY_text");

    /**
     * Constructor.
     * @param drone {@link WebDrone}
     */
    public RMSiteNavigation(WebDrone drone)
    {
        super(drone);
        WebElement nav = drone.find(By.cssSelector("div.alf-menu-bar"));
        setWebElement(nav);
    }
    /**
     * Check if the site navigation has file plan link highlighted.
     * This is only available in RM module and is basically doc lib
     * with a different label
     * @return if link is highlighted
     */
    public boolean isFilePlanActive()
    {
        return isLinkActive(FILE_PLAN_CSS);
    }


    /**
     * Checks if File plan link is displayed.
     * @return true if displayed
     */
    public boolean isFilePlanDisplayed()
    {
        try
        {
            WebElement filePlanButton = find(FILE_PLAN_CSS);
            //Do a text check as its using the same css as doc lib.
            String label = filePlanButton.getText();
            if(label != null && "File plan".equalsIgnoreCase(label.trim()))
            {
                return true;
            }
        }
        catch (NoSuchElementException e) { }
        return false;
    }

    /**
     * Checks if More drop down is displayed.
     * @return true if displayed
     */
    public boolean isMoreDisplayed()
    {
        return isLinkDisplayed(MORE_BTN_CSS);
    }

    /**
     * Checks if More drop down is displayed.
     * @return true if displayed
     */
    public void selectMore()
    {
        drone.find(MORE_BTN_CSS).click();
    }

    /**
     * Mimics selecting the file plan link on
     * site navigation bar.
     */
    public FilePlanPage selectFilePlan()
    {
        select(FILE_PLAN_TXT_CSS);
        return new FilePlanPage(getDrone());
    }

    /**
     * Mimics selecting the record search link on
     * site navigation bar.
     */
    public RecordSearchPage selectRecordSearch()
    {
        if(!isRecordSearchDisplayed())
        {
            selectMore();
        }
        select(RM_SEARCH_TXT_CSS);
        return new RecordSearchPage(drone);
    }

    /**
     * Checks if record search is displayed.
     * @return true if displayed
     */
    public boolean isRecordSearchDisplayed()
    {
        return isLinkDisplayed(RM_SEARCH_CSS);
    }

    /**
     * Checks if record search link is active.
     * @return true if  high lighted
     */
    public boolean isRecordSearchActive()
    {
        return isLinkActive(RM_SEARCH_CSS);
    }

    /**
     * Mimics selecting the site members link on
     * site navigation bar.
     */
    public RMSiteMembersPage selectSiteMembers()
    {
        if(!isSelectSiteMembersDisplayed())
        {
            selectMore();
        }
        select(SITE_MEMBERS_TXT_CSS_SELECTOR);
        return new RMSiteMembersPage(drone);
    }

    /**
     * Checks if site members is displayed.
     * @return true if displayed
     */
    public boolean isSelectSiteMembersDisplayed()
    {
        return isLinkDisplayed(SITE_MEMBERS_CSS_SELECTOR);
    }

    /**
     * Checks if site members link is active.
     * @return true if  high lighted
     */
    public boolean isSelectSiteMembersActive()
    {
        return isLinkActive(SITE_MEMBERS_CSS_SELECTOR);
    }

    /**
     * Mimics selecting the records management console link on
     * site navigation bar.
     */
    public RMConsolePage selectRMConsole()
    {
        selectMore();
        select(RM_CONSOLE_TXT_CSS_SELECTOR);
        return new RMConsolePage(drone);
    }

}
