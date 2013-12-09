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

import org.alfresco.po.share.dashlet.Dashlet;
import org.alfresco.po.share.dashlet.FactoryShareDashlet;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;

/**
 * Records management dash board page object.
 * @author Michael Suzuki
 * @version 1.7.1
 */
public class RMDashBoardPage extends RMSitePage
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
        while (true)
        {
            try
            {
                timer.start();
                // Check site is being created message has disappeared
                if (!isJSMessageDisplayed())
                {
                    try
                    {
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
            }
            catch (Exception e)
            {
                // Catch stale element exception caused by js message on page
            }
            finally
            {
                timer.end();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public RMDashBoardPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public RMDashBoardPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Gets dashlets in the dashboard page.
     *
     * @param name
     *            String title of dashlet
     * @return HtmlPage page object
     * @throws Exception
     */
    public Dashlet getDashlet(final String name)
    {
        return FactoryShareDashlet.getPage(drone, name);
    }
}
