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

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.WebDrone;

/**
 * Records management site page abstract.
 *
 * @author Michael Suzuki
 * @author Tuna Aksoy
 * @version 1.7.1
 */
public abstract class RMSitePage extends SharePage
{
    /**
     * Constructor.
     *
     * @param drone {@link WebDrone}
     */
    protected RMSitePage(WebDrone drone)
    {
        super(drone);
    }

    /**
     * Get main navigation.
     *
     * @return {@link RMSiteNavigation} Navigation page object
     */
    public RMSiteNavigation getSiteNav()
    {
        return new RMSiteNavigation(drone);
    }
}