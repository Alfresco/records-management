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
package org.alfresco.po.rm.fileplan.filter;

import java.util.List;

import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Records Management file plan navigation element.
 * This html element gives the possibility to reach
 * the file plan tree root from a container like
 * "transfers", "holds" or "unfiled records".
 *
 * @author Tuna Aksoy
 * @since 2.2
 */
public class FilePlanNavigation extends HtmlElement
{
    /** Selector */
    private static final By TREE_NODES = By.cssSelector("div.treeview div[id*='treeview'] tbody span");

    /**
     * Constructor.
     *
     * @param drone {@link WebDrone} Web drone instance
     */
    public FilePlanNavigation(WebDrone drone)
    {
        super(drone);
    }

    /**
     * Select the file plan navigation tree root node
     *
     * @return {@link FilePlanPage} Returns the file plan page
     */
    public FilePlanPage selectFilePlanTreeRootNode()
    {
        WebElement root = null;
        List<WebElement> elements = drone.findAll(TREE_NODES);
        for (WebElement webElement : elements)
        {
            // FIXME: The hard coded string will be fixed. Loading properties values from language files needs to be refactored first!
            if (webElement.getText().equals("File Plan"))
            {
                root = webElement;
                break;
            }
        }
        if (root == null)
        {
            throw new IllegalStateException("The root of the file plan tree could not be found!");
        }
        root.click();
        return drone.getCurrentPage().render();
    }
}
