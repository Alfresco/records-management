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

import org.alfresco.po.rm.util.RmPageObjectUtils;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Records management console page object.
 *
 * @author Michael Suzuki
 * @author Tuna Aksoy
 * @since 1.7.1
 */
public class RmConsolePage extends RmSitePage
{
    public static enum RmConsoleMenu
    {
        DEFINE_ROLES(By.xpath("//a[@title = 'Create and edit roles']")),
        USERS_AND_GROUPS(By.xpath("//a[@title = 'Users and groups']")),
        RECORDS_MANAGEMENT_RECVORDS_MANAGER(By.xpath("//a[@title = 'Create and edit roles']")),
        RECORDS_MANAGEMENT_SECURITY_OFFICER(By.xpath("//a[@title = 'Create and edit roles']")),
        RECORDS_MANAGEMENT_User(By.xpath("//a[@title = 'Create and edit roles']"));

        private final By locator;

        RmConsoleMenu(By locator)
        {
            this.locator = locator;
        }

        public By getLocator ()
        {
            return locator;
        }
}
    /**
     * Constructor.
     *
     * @param drone {@link WebDrone}
     */
    public RmConsolePage(WebDrone drone)
    {
        super(drone);
    }

    /**
     * @see org.alfresco.webdrone.Render#render(org.alfresco.webdrone.RenderTime)
     */
    @SuppressWarnings("unchecked")
    @Override
    public RmConsolePage render(RenderTime timer)
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
    public RmConsolePage render(long time)
    {
        RenderTime timer = new RenderTime(time);
        return render(timer);
    }

    /**
     * @see org.alfresco.webdrone.Render#render()
     */
    @SuppressWarnings("unchecked")
    @Override
    public RmConsolePage render()
    {
        RenderTime timer = new RenderTime(maxPageLoadingTime);
        return render(timer);
    }

    /**
     * Action Select Define Roles Page from RM console Menu
     *
     * @return  {@link RmConsoleDefineRolesPage}
     */
    public RmConsoleDefineRolesPage openDefineRolesPage(){
        RmPageObjectUtils.select(drone, RmConsoleMenu.DEFINE_ROLES.locator);
        return new RmConsoleDefineRolesPage(drone).render();
    }

    /**
     * Action Select Users And Groups Page from RM console Menu
     *
     * @return  {@link RmConsoleUsersAndGroups}
     */
    public RmConsoleUsersAndGroups openUsersAndGroupsPage(){
        RmPageObjectUtils.select(drone, RmConsoleMenu.USERS_AND_GROUPS.locator);
        return new RmConsoleUsersAndGroups(drone).render();
    }
}