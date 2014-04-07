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

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;

/**
 * Records management Link To Rule page.
 *
 * @author Polina Lushchinskaya
 * @version 1.1
 */
public class RmLinkToRulePage extends RmFolderRulesWithRules {

    public static final By UNLINK_RULE_BUTTON = By.cssSelector("button[id$='unlink-button-button']");
    public static final By VIEW_RULE_SET_BUTTON = By.cssSelector("button[id$='view-button-button']");
    public static final By CHANGE_BUTTON = By.cssSelector("button[id$='change-button-button']");
    public static final By RULE_ITEMS = By.cssSelector("div[class$='rules-linked']");

    public RmLinkToRulePage(WebDrone drone) {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RmLinkToRulePage render(RenderTime timer)
    {
        elementRender(timer,
                getVisibleRenderElement(UNLINK_RULE_BUTTON),
                getVisibleRenderElement(VIEW_RULE_SET_BUTTON),
                getVisibleRenderElement(CHANGE_BUTTON),
                getVisibleRenderElement(RULE_ITEMS));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public RmLinkToRulePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public RmLinkToRulePage render(final long time)
    {
        return render(new RenderTime(time));
    }
}
