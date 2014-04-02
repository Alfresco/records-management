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
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.WhenSelectorImpl;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

import static org.alfresco.webdrone.WebDroneUtil.checkMandotaryParam;

/**
 * Extends the {@link CreateRulePage} to add RM specific methods
 *
 * @author Tuna Aksoy
 * @since 2.2
 */
public class RmCreateRulePage extends CreateRulePage
{
    public static final By ACTION_OPTIONS_SELECT            = By
            .cssSelector("ul[id$=ruleConfigAction-configs]>li select[class$='config-name']");
    public static final By SAVE_BUTTON                      = By
            .cssSelector("span[id*='save-button'] button[id*='save-button']");
    public static final By CANCEL_BUTTON                     = By
        .cssSelector("span[id*='cancel-button'] button[id*='cancel-button']");
    public static final By SELECT_PROPERTY_BUTTON           = By.xpath("//button[text()='Select...']");
    //private static final By CREATED_ALERT                   = By.xpath(".//*[@id='message']/div/span");
    public static final By SELECT_PROPERTY_DIALOG           = By
            .cssSelector("div[id$='ruleConfigAction-selectSetPropertyDialog-dialog']");
    public static final By SELECT_CRITERIA_DIALOG           = By
            .cssSelector("div[id$='ruleConfigIfCondition-showMoreDialog-dialog']");
    private static final By SHOW_ALL_LABEL                  = By.xpath("//span[text()='All']");
    private static final By OK_PROPERTY                     = By
            .cssSelector("button[id$='ruleConfigAction-selectSetPropertyDialog-ok-button-button']");

    public static final By PROPERTY_VALUE_INPUT             = By
            .xpath("//span[contains(@class, 'menuname_setPropertyValue')]//input[@type='text']");
    public static By CRITERIAS_SELECT                       = By
            .cssSelector("ul[id$=ruleConfigIfCondition-configs]>li select[class$='config-name']");
    public static By POSITION_SELECT                        = By.cssSelector("select[title$='position']");
    public static By DISPOSITION_STEP_SELECT                = By.cssSelector("select[title$='action']");

    public static enum RuleCriterias
    {
        ALL_ITEMS("All Items"),
        PUBLICATION_DATE("Publication Date"),
        DISPOSITION_AUTHORITY("Disposition Authority"),
        DISPOSITION_INSTRUCTIONS("Disposition Instructions"),
        NAME("Name"),
        ORIGINATING_ORGANIZATION("Originating Organization"),
        ORIGINATOR("Originator"),
        TITLE("Title"),
        HAS_TAG("Has tag"),
        HAS_CATEGORY("Has category"),
        CONTENT_OF_TYPE("Content of type or sub-type"),
        HAS_ASPECT("Has aspect"),
        TYPE_OF_RECORDS("Type of records management item"),
        HAS_RECORD_TYPE("Has record type"),
        RECORD_COMPLETED("Record completed"),
        RECORD_FILED("Record filed"),
        RECORD_FOLDER_CLOSED("Record folder closed."),
        VITAL_RECORD("Vital record"),
        HAS_DISPOSITION_ACTION("Has disposition action"),
        CLASSIFIED_BY_DISPOSITION("Classified by disposition schedule"),
        CUTOFF("Cutoff"),
        FROZEN("Frozen"),
        SHOW_MORE("Show more...");

        private final String value;

        RuleCriterias(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }
    }

    public static enum WhenExecute
    {
        ANY("ANY"),
        NEXT("NEXT"),
        PREVIOUS("PREVIOUS");

        private final String value;

        WhenExecute(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }
    }

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

    public WhenSelectorImpl getWhenOptionObj()
    {
        return new WhenSelectorImpl(drone);
    }

    /**
     * Action Click on save button
     *
     * @return  {@link RmFolderRulesWithRules} page response
     */
    public RmFolderRulesWithRules clickSave()
    {
        click(SAVE_BUTTON);
        return new RmFolderRulesWithRules(drone).render();
    }

    /**
     * Helper method that clicks by element
     *
     * @param locator element By locator
     */
    private void click(By locator)
    {
        WebElement element = drone.findAndWait(locator);
        element.click();
    }

    /**
     * Action select property value from select property dialog
     *
     * @param propertyValue value of property
     * @return  {@link RmCreateRulePage} page response
     */
    public RmCreateRulePage selectSetProperty(String propertyValue)
    {
        checkMandotaryParam("propertyValue", propertyValue);
        drone.findAndWait(SELECT_PROPERTY_BUTTON).isDisplayed();
        click(SELECT_PROPERTY_BUTTON);
        drone.findAndWait(SELECT_PROPERTY_DIALOG).isDisplayed();
        click(SHOW_ALL_LABEL);
        WebElement selectValue = drone.findAndWait(By.xpath("//div[text() = '" + propertyValue + "']"));
        selectValue.click();
        click(OK_PROPERTY);
        return new RmCreateRulePage(drone).render();
    }

    /**
     * Action select criteria by value tag
     *
     * @param criteriaOptionValue  criteria name
     */
    public void selectCriteriaOption(String criteriaOptionValue)
    {
        checkMandotaryParam("criteriaOptionValue", criteriaOptionValue);
        List<WebElement> criteriaOptions = drone.findAndWaitForElements(CRITERIAS_SELECT);
        List<Select> criteriaSelects = new ArrayList<Select>();
        for (WebElement whenOption : criteriaOptions)
        {
            criteriaSelects.add(new Select(whenOption));
        }
        criteriaSelects.get(criteriaSelects.size()-1).selectByValue(criteriaOptionValue);
    }

    /**
     * Action select Disposition created value when Has disposition criteria selected
     *
     * @param whenOptionValue - when disposiotion step will execute
     */
    public void selectWhenDispositionOption(String whenOptionValue)
    {
        checkMandotaryParam("whenOptionValue", whenOptionValue);
        List<WebElement> criteriaOptions = drone.findAndWaitForElements(POSITION_SELECT);
        List<Select> criteriaSelects = new ArrayList<Select>();
        for (WebElement whenOption : criteriaOptions)
        {
            criteriaSelects.add(new Select(whenOption));
        }
        criteriaSelects.get(criteriaSelects.size()-1).selectByValue(whenOptionValue);
    }

    /**
     * Action select Disposiotion step value when Has disposition criteria selected
     *
     * @param dispositionStepValue
     */
    public void selectDispositionStep(String dispositionStepValue)
    {
        checkMandotaryParam("dispositionStepValue", dispositionStepValue);
        List<WebElement> criteriaOptions = drone.findAndWaitForElements(DISPOSITION_STEP_SELECT);
        List<Select> criteriaSelects = new ArrayList<Select>();
        for (WebElement whenOption : criteriaOptions)
        {
            criteriaSelects.add(new Select(whenOption));
        }


        criteriaSelects.get(criteriaSelects.size()-1).selectByValue(dispositionStepValue);
    }

    /**
     * Action select Rule Action by value
     *
     * @param actionOptionValue
     */
    public void selectRmAction(String actionOptionValue)
    {
        checkMandotaryParam("actionOptionValue", actionOptionValue);
        List<WebElement> actionOptions = drone.findAndWaitForElements(ACTION_OPTIONS_SELECT);
        List<Select> actionSelects = new ArrayList<Select>();
        for (WebElement actionOption : actionOptions)
        {
            actionSelects.add(new Select(actionOption));
        }
        actionSelects.get(actionSelects.size()-1).selectByValue(actionOptionValue);
    }
}
