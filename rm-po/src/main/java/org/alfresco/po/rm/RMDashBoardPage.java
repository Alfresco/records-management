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

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.RMNavigation;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;

/**
 * Records management dash board page object.
 *
 * @author Michael Suzuki
 * @version 1.7.1
 */
public class RMDashBoardPage extends DashBoardPage
{
    /**
     * Constructor.
     * @param drone {@link WebDrone}
     */
    public RMDashBoardPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RMDashBoardPage render(RenderTime timer)
    {
        return (RMDashBoardPage) super.render(timer);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RMDashBoardPage render(long time)
    {
        return (RMDashBoardPage) super.render(time);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RMDashBoardPage render()
    {
        return (RMDashBoardPage) super.render();
    }

    /**
     * Renders the RM Dash board page
     *
     * @return {@link RMDashBoardPage} page object
     */
    public RMDashBoardPage rmRender()
    {
        try
        {
            RenderTime timer = new RenderTime(maxPageLoadingTime);
            getDashlet("site-members").render(timer);
            getDashlet("site-contents").render(timer);
            getDashlet("site-activities").render(timer);
        }
        catch (PageException pe)
        {
            throw new PageException(this.getClass().getName() + " failed to render in time", pe);
        }
        return this;
    }

    /**
     * Gets the navigation object for RM.
     *
     * @return {@link RMNavigation} page object
     */
    public RMNavigation getRMNavigation()
    {
        return new RMNavigation(drone);
    }

    /**
     * Gets the site navigation object for RM.
     *
     * @return {@link RMSiteNavigation} page object
     */
    public RMSiteNavigation getRMSiteNavigation()
    {
        return new RMSiteNavigation(drone);
    }
}
