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

import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Extends {@link FolderRulesPage} to add RM specific methods
 *
 * @author Tuna Aksoy
 * @since 2.2
 */
public class RmFolderRulesPage extends FolderRulesPage
{
    private static final By LINK_CREATE_RULE_PAGE_SELECTOR = By.cssSelector("div[class=dialog-option] a[href*='rule-edit']");
    private static final By LINK_TO_RULE_DIALOG_SELECTOR = By.cssSelector("div[class=dialog-option] a[id*='linkToRuleSet']");
    private static final By LINK_TO_RULE_DIALOG = By.cssSelector("div[id$='rulesPicker-dialog']");
    public static final By LINK_BUTTON = By.cssSelector("button[id$='rulesPicker-ok-button']");

    public RmFolderRulesPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RmFolderRulesPage render(RenderTime timer)
    {
        return (RmFolderRulesPage) super.render(timer);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RmFolderRulesPage render(long time)
    {
        return (RmFolderRulesPage) super.render(time);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RmFolderRulesPage render()
    {
        return (RmFolderRulesPage) super.render();
    }

    public RmCreateRulePage openCreateRulePage()
    {
        WebElement element = drone.findAndWait(LINK_CREATE_RULE_PAGE_SELECTOR);
        element.click();
        return drone.getCurrentPage().render();
    }

    /**
     * Action opens Link To Dialog
     */
    public void openLinkToDialog()
    {
        WebElement element = drone.findAndWait(LINK_TO_RULE_DIALOG_SELECTOR);
        element.click();
        isDisplay(LINK_TO_RULE_DIALOG);
    }

    /**
     * Verifies if element Displayed
     * @param locator element By locator
     * @return  Displays or not (true/false)
     */
    public boolean isDisplay(By locator)
    {
        WebDroneUtil.checkMandotaryParam("locator", locator);
        try
        {
            return drone.findAndWait(locator, 2000).isDisplayed();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Action select Link To Rule from Create Rule Page
     *
     * @return {@link org.alfresco.po.rm.RmLinkToRulePage}
     */
    public RmLinkToRulePage clickLink()
    {
        click(LINK_BUTTON);
        return new RmLinkToRulePage(drone).render();
    }

    /**
     * Helper method that clicks by element
     *
     * @param locator element By locator
     */
    public void click(By locator)
    {
        WebElement element = drone.findAndWait(locator);
        drone.mouseOverOnElement(element);
        element.click();
    }

}
