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
package org.alfresco.po.rm.util;

import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Utility methods for the page objects
 *
 * @author Tuna Aksoy
 * @since 2.2
 */
public class RmPageObjectUtils
{
    /**
     * Helper method to check if a {@link WebElement} is displayed
     *
     * @param webDrone {@link WebDrone} The web drone instance
     * @param selector {@link By} The selector which is used to find the {@link WebElement}
     * @return <code>true</code> if the {@link WebElement} is visible <code>false</code> otherwise
     */
    public static boolean isDisplayed(WebDrone webDrone, By selector)
    {
        WebDroneUtil.checkMandotaryParam("webDrone", webDrone);
        WebDroneUtil.checkMandotaryParam("selector", selector);

        boolean isDisplayed = false;
        try
        {
            isDisplayed = webDrone.find(selector).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return isDisplayed;
    }

    /**
     * Helper method to click on a {@link WebElement}
     *
     * @param webDrone {@link WebDrone} The web drone instance
     * @param selector {@link By} The css selector which is used to find the {@link WebElement}
     * @return
     */
    public static void select(WebDrone webDrone, By selector)
    {
        WebDroneUtil.checkMandotaryParam("webDrone", webDrone);
        WebDroneUtil.checkMandotaryParam("selector", selector);

        webDrone.findAndWait(selector).click();
    }
}