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

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Records management New Role page.
 *
 * @author Polina Lushchinskaya
 * @version 1.1
 */
public class RmConsoleNewRolePage extends RmSitePage {

    /**
     * Values of roles on new Role page
     */
    public static enum RoleValue
    {
        MANAGE_RULES("input[id$='ManageRules']"),
        VIEW_RECORDS("input[id$='ViewRecords']");

        private final String cssSelector;

        RoleValue(String cssSelector)
        {
            this.cssSelector = cssSelector;
        }

        public String getValue()
        {
            return cssSelector;
        }
    }

    public static final By ROLE_NAME_INPUT = By.cssSelector("div.#roleName");

    /**
     * Constructor.
     *
     * @param drone {@link org.alfresco.webdrone.WebDrone}
     */
    public RmConsoleNewRolePage(WebDrone drone) {
        super(drone);
    }

    /**
     * @see org.alfresco.webdrone.Render#render(org.alfresco.webdrone.RenderTime)
     */
    @SuppressWarnings("unchecked")
    @Override
    public RmConsoleNewRolePage render(RenderTime timer)
    {
        WebDroneUtil.checkMandotaryParam("timer", timer);

        while (true)
        {
            timer.start();
            try
            {
                // if search body is found we are rendered
                By rmConsole = By.cssSelector("div[id$='_rm-console']");
                WebElement rmConsoleElement = drone.find(rmConsole);
                if (rmConsoleElement.isDisplayed())
                {
                    break;
                }
            }
            catch (NoSuchElementException e)
            {
            }
            finally
            {
                timer.end();
            }
        }
        return this;
    }

    /**
     * @see org.alfresco.webdrone.Render#render(long)
     */
    @SuppressWarnings("unchecked")
    @Override
    public RmConsoleNewRolePage render(long time)
    {
        RenderTime timer = new RenderTime(time);
        return render(timer);
    }

    /**
     * @see org.alfresco.webdrone.Render#render()
     */
    @SuppressWarnings("unchecked")
    @Override
    public RmConsoleNewRolePage render()
    {
        RenderTime timer = new RenderTime(maxPageLoadingTime);
        return render(timer);
    }

    /**
     * Method select Role on new role page
     *
     * @param drone {@link org.alfresco.webdrone.WebDrone}
     * @param value Role value
     */
    public static void checkRole(WebDrone drone, String value)
    {
        WebDroneUtil.checkMandotaryParam("drone", drone);
        WebDroneUtil.checkMandotaryParam("value", value);
        try
        {
            WebElement selectRole = drone.find(By.cssSelector(value));
            if(!selectRole.isSelected())
            {
                selectRole.click();
            }
        }
        catch (NoSuchElementException te)
        {
            te.getStackTrace();
        }
    }
}
