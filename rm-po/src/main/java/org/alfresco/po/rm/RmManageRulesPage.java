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

import org.alfresco.po.share.site.ManageRulesPage;
import org.alfresco.po.share.site.RulesPage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * This class extends the {@link UploadFilePage} in order to make filing records possible.
 * This is a temporary solution. After moving RM related classes to its own module this class won't be used anymore.
 *
 * @author Mark Hibbins
 * @since 2.2
 */
public class RmManageRulesPage extends ManageRulesPage
{
    /**
     * Constructor.
     *
     * @param drone {@link WebDrone}
     */
    public RmManageRulesPage(WebDrone drone)
    {
        super(drone);
    }

    /**
     * Action of selecting create rules link.
     * @return {@link RulesPage} page response
     */
    public RmRulesPage selectCreateRules()
    {
        drone.find(By.partialLinkText("Create Rules")).click();
        return new RmRulesPage(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RmManageRulesPage render(RenderTime timer)
    {
        return (RmManageRulesPage) super.render(timer);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RmManageRulesPage render(long time)
    {
        return (RmManageRulesPage) super.render(time);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RmManageRulesPage render()
    {
        return (RmManageRulesPage) super.render();
    }

    public void selectRule(String ruleName)
    {
        WebElement checkBox = drone.find(By.linkText(ruleName));
        checkBox.click();
    }


}
