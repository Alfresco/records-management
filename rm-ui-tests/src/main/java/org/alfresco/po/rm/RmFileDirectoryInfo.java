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

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.site.document.FileDirectoryInfoImpl;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Extends {@link FileDirectoryInfoImpl} to add RM specific methods
 *
 * @author Tuna Aksoy
 * @since 2.2
 */
public class RmFileDirectoryInfo extends FileDirectoryInfoImpl
{
    private static final By BANNER = By.cssSelector("div.info-banner");
    private static final By CREATE_RECORD = By.cssSelector("div#onActionSimpleRepoAction.rm-create-record a");

    /**
     * Constructor.
     *
     * @param nodeRef {@link String}
     * @param webElement {@link WebElement}
     * @param drone {@link WebDrone}
     */
    public RmFileDirectoryInfo(String nodeRef, WebElement webElement,
            WebDrone drone)
    {
        super(nodeRef, webElement, drone);
    }

    /**
     * Action of selecting the declare records button
     * that appear in the action drop down.
     *
     * @return {@link HtmlPage}
     */
    public HtmlPage declareRecord()
    {
        selectMoreAction().click();
        try
        {
            WebElement createRecord = drone.find(CREATE_RECORD);
            createRecord.click();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find declare record button from the drop down", nse);
        }
        // FIXME: canResume has been removed from HtmlElement in WebDrone
        // That's why the code has been commented out. If this breaks any tests
        // we need to find a solution.
        //canResume();
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Verify if item is a record.
     * Part of Records management module, when a document is a record
     * a small banner info is displayed indicating that it is a record.
     *
     * @return <code>true</code> if record banner is visible <code>false</code> otherwise
     */
    public boolean isRecord()
    {
        try
        {
            return drone.find(BANNER).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }
}