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
package org.alfresco.po.rm.fileplan.filter;

import org.alfresco.po.rm.fileplan.filter.hold.HoldsContainer;
import org.alfresco.po.rm.fileplan.filter.unfiledrecords.UnfiledRecordsContainer;
import org.alfresco.po.rm.util.RmPageObjectUtils;
import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Records management file plan filter.
 *
 * @author Michael Suzuki
 * @author Tuna Aksoy
 * @version 1.7.1
 */
public class FilePlanFilter extends HtmlElement
{
    /** File plan filter link */
    private static final By FILEPLAN_FILTER = By.cssSelector("div.filter.fileplan-filter");
    private static final By UNFILED_RECORDS_CONTAINER_LINK = By.cssSelector("span.unfiledRecords a");
    private static final By HOLDS_CONTAINER_LINK = By.cssSelector("span.holds a");

    /**
     * Constructor.
     *
     * @param drone {@link WebDrone}
     */
    public FilePlanFilter(WebDrone drone)
    {
        super(drone);
        WebElement filePlanFilter = drone.find(FILEPLAN_FILTER);
        setWebElement(filePlanFilter);
    }

    /**
     * Check if the unfiled records container is displayed.
     *
     * @return <code>true</code> if the unfiled records container is displayed <code>false</code> otherwise
     */
    public boolean isUnfiledRecordsContainerDisplayed()
    {
        return RmPageObjectUtils.isDisplayed(drone, UNFILED_RECORDS_CONTAINER_LINK);
    }

    /**
     * Select the unfiled records container.
     *
     * @return {@link UnfiledRecordsContainer} Returns the unfiled records container
     */
    public UnfiledRecordsContainer selectUnfiledRecordsContainer()
    {
        RmPageObjectUtils.select(drone, UNFILED_RECORDS_CONTAINER_LINK);
        return new UnfiledRecordsContainer(drone);
    }

    /**
     * Checks if the holds container is displayed.
     *
     * @return <code>true</code> if the holds container is displayed <code>false</code> otherwise
     */
    public boolean isHoldContainerDisplayed()
    {
        return RmPageObjectUtils.isDisplayed(drone, HOLDS_CONTAINER_LINK);
    }

    /**
     * Select the holds container
     *
     * @return {@link HoldsContainer} Returns the holds container
     */
    public HoldsContainer selectHoldsContainer()
    {
        RmPageObjectUtils.select(drone, HOLDS_CONTAINER_LINK);
        return new HoldsContainer(drone);
    }
}