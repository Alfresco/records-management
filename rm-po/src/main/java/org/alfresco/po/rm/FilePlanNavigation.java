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

import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Records management file plan side navigation.
 *
 * @author Michael Suzuki
 * @author Tuna Aksoy
 * @version 1.7.1
 */
public class FilePlanNavigation extends HtmlElement
{
    private static final By FILEPLAN_FILTER = By.cssSelector("div.filter.fileplan-filter");
    private static final By UNFILED_RECORDS_LINK = By.cssSelector("span.unfiledRecords a");

    /**
     * Constructor.
     *
     * @param drone {@link WebDrone}
     */
    public FilePlanNavigation(WebDrone drone)
    {
        super(drone);
        WebElement filePlanFilter = drone.find(FILEPLAN_FILTER);
        setWebElement(filePlanFilter);
    }

    /**
     * Check if the unfiled record link is displayed.
     *
     * @return <code>true</code> if link is displayed <code>false</code> otherwise
     */
    public boolean isUnfiledRecordsVisible()
    {
        try
        {
            WebElement unfileRecordsLink = find(UNFILED_RECORDS_LINK);
            return unfileRecordsLink.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * Select the link of unfiled records.
     *
     * @return {@link FilePlanPage} Returns the file plan page
     */
    public FilePlanPage selectUnfiledRecords()
    {
        WebElement unfileRecordsLink = find(UNFILED_RECORDS_LINK);
        unfileRecordsLink.click();
        return new FilePlanPage(drone);
    }
}