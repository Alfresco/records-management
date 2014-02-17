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

import org.alfresco.po.share.site.contentrule.createrules.CreateRulePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;

/**
 * Extends the {@link CreateRulePage} to add RM specific methods
 *
 * @author Tuna Aksoy
 * @since 2.2
 */
public class RmCreateRulePage extends CreateRulePage
{
    public RmCreateRulePage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RmCreateRulePage render(RenderTime timer)
    {
        return (RmCreateRulePage) super.render(timer);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RmCreateRulePage render(long time)
    {
        return (RmCreateRulePage) super.render(time);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RmCreateRulePage render()
    {
        return (RmCreateRulePage) super.render();
    }

    @SuppressWarnings("unchecked")
    public RmActionSelectorEnterpImpl getActionOptionsObj()
    {
        return new RmActionSelectorEnterpImpl(drone);
    }
}
