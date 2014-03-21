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

import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;

/**
 * Records management Define Roles page.
 *
 * @author Polina Lushchinskaya
 * @version 1.1
 */
public class RmConsoleDefineRolesPage extends RmSitePage {

    //private static final By NEW_ROLE = By.cssSelector("#newRole-button");

    /**
     * Constructor.
     *
     * @param drone {@link org.alfresco.webdrone.WebDrone}
     */
    protected RmConsoleDefineRolesPage(WebDrone drone) {
        super(drone);
    }

    @Override
    public <T extends HtmlPage> T render(RenderTime renderTime) {
        return null;
    }

    @Override
    public <T extends HtmlPage> T render(long l) {
        return null;
    }

    @Override
    public <T extends HtmlPage> T render() {
        return null;
    }
}
