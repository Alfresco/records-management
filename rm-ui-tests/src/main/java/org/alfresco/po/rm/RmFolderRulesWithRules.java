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

import java.util.List;

import org.alfresco.po.rm.util.RmPageObjectUtils;
import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Records management rules page with existing rules.
 *
 * @author Polina Lushchinskaya
 * @version 1.1
 * @since 2.2
 */
public class RmFolderRulesWithRules extends FolderRulesPage
{
    /** Constants for the selectors */
    public static final By EDIT_BUTTON              = By.cssSelector("button[id*='edit-button-button']");
    public static final By DELETE_BUTTON            = By.cssSelector("button[id*='delete-button-button']");
    public static final By NEW_RULE_BUTTON          = By.cssSelector("button[id*='default-newRule-button-button']");
    public static final By RULE_DETAILS_BLOCK       = By.cssSelector("div[id*='default-body']>div[id*='rule-details']");
    public static final By RUN_RULES_BUTTON         = By.cssSelector("button[id$='default-runRules-menu-button']");
    public static final By RUN_RULES_FOR_FOLDER     = By.xpath("//a[text()='Run rules for this folder']");
    public static final By RUN_RULES_FOR_SUBFOLDER  = By.xpath("//a[text() = 'Run rules for this folder and its subfolders']");
    public static final By RULE_ITEMS               = By.cssSelector("ul[class*='rules-list-container']>li[class*='rules-list-item']");

    private static final By ALERT_DELETE_BLOCK      = By.cssSelector("div[id='prompt']");
    //Delete and Cancel button has same css.
    private static final By ALERT_DELETE_OK         = By.xpath("//button[text()='Delete']");
    //private static final By SAVE_BUTTON             = By.xpath("//button[text()='Save']");

    /**
     * Constructor
     *
     * @param drone {@link WebDrone} Web drone
     */
    public RmFolderRulesWithRules(WebDrone drone)
    {
        super(drone);
    }

    /**
     * @see org.alfresco.po.share.site.contentrule.FolderRulesPage#render(org.alfresco.webdrone.RenderTime)
     */
    @SuppressWarnings("unchecked")
    @Override
    public RmFolderRulesWithRules render(RenderTime timer)
    {
        WebDroneUtil.checkMandotaryParam("timer", timer);

        elementRender(timer,
                getVisibleRenderElement(TITLE_SELECTOR),
                getVisibleRenderElement(EDIT_BUTTON),
                getVisibleRenderElement(DELETE_BUTTON));
        return this;
    }

    /**
     * @see org.alfresco.po.share.site.contentrule.FolderRulesPage#render()
     */
    @SuppressWarnings("unchecked")
    @Override
    public RmFolderRulesWithRules render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * @see org.alfresco.po.share.site.contentrule.FolderRulesPage#render(long)
     */
    @SuppressWarnings("unchecked")
    @Override
    public RmFolderRulesWithRules render(final long time)
    {
        WebDroneUtil.checkMandotaryParam("time", time);

        return render(new RenderTime(time));
    }

    /**
     * Method verifies if rules details block is displayed
     * 
     * @return true/false Rule Details sector is Displayed
     */
    private boolean isRuleDetailsDisplay()
    {
        boolean isDisplayed = false;
        if (RmPageObjectUtils.isDisplayed(drone, RULE_DETAILS_BLOCK) &&
                RmPageObjectUtils.isDisplayed(drone, EDIT_BUTTON) &&
                RmPageObjectUtils.isDisplayed(drone, DELETE_BUTTON))
        {
            isDisplayed = true;
        }
        return isDisplayed;
    }

    /**
     * Delete rule from rules details page
     * @param ruleName Name for Rule
     * @return {@link RmFolderRulesPage}
     */
    public RmFolderRulesPage deleteRule(String ruleName)
    {
        WebDroneUtil.checkMandotaryParam("ruleName", ruleName);

        List<WebElement> ruleItems = drone.findAndWaitForElements(RULE_ITEMS);
        for (WebElement ruleItem : ruleItems)
        {
            if (ruleItem.getText().contains(ruleName))
            {
                ruleItem.click();
                render(new RenderTime(maxPageLoadingTime));
                click(DELETE_BUTTON);
                drone.findAndWait(ALERT_DELETE_BLOCK).findElement(ALERT_DELETE_OK).click();
                return new RmFolderRulesPage(drone).render();
            }
        }
        throw new PageOperationException("Rule with name:" + ruleName + " not found on Page");
    }

    /**
     * Action click on new rule button
     * @return {@link RmCreateRulePage}
     */
    public RmCreateRulePage clickNewRuleButton()
    {
        click(NEW_RULE_BUTTON);
        return drone.getCurrentPage().render();
    }

    /**
     * Action click on edit rule button
     * @return {@link RmCreateRulePage}
     */
    public RmCreateRulePage clickEditButton()
    {
        click(EDIT_BUTTON);
        return drone.getCurrentPage().render();
    }

    /**
     * Helper method that clicks by element
     *
     * @param locator element By locator
     */
    public void click(By locator)
    {
        WebDroneUtil.checkMandotaryParam("locator", locator);

        WebElement element = drone.findAndWait(locator);
        drone.mouseOverOnElement(element);
        element.click();
    }

    /**
     * Helper method verifies if folder rule page is correct
     * 
     * @param folderName Folder Name
     * @return correct or incorrect page
     */
    public boolean isPageCorrect(String folderName)
    {
        WebDroneUtil.checkMandotaryParam("folderName", folderName);

        return (super.isTitleCorrect(folderName) && isRuleDetailsDisplay());
    }
}
