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
package org.alfresco.po.rm.fileplan;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Records management Edit Disposition page.
 *
 * @author Polina Lushchinskaya
 * @version 1.1
 */
public class RmEditDispositionSchedulePage extends RmCreateDispositionPage {

    public static By EDIT_DISPOSITION_SECTION   = By.cssSelector("div[id$='rm-disposition-edit']");
    public static By ADD_STEP_BUTTON            = By.cssSelector("button[id$='createaction-button-button']");
    public static By DISPOSITION_FORM           = By.cssSelector("div[class$='disposition-form']");
    public static By SAVE_BUTTON                = By.xpath("//div[contains(@style,'block')]//button[contains(text(),'Save')]");
    public static By CANCEL_BUTTON              = By.xpath("//div[contains(@style,'block')]//button[text()='Cancel']");
    public static By DONE_BUTTON                = By.xpath("//button[text()='Done']");
    public static By AFTER_PERIOD_CHKBOX        = By.cssSelector("div[style*='block'] input[class$='period-enabled']");
    public static By WHEN_EVENT_OCCURS_CHKBOX   = By.cssSelector("div[style*='block'] input[class$='events-enabled']");
    public static By DESCRIPTION_AREA           = By.cssSelector("div[style*='block'] textarea[id$='description']");
    public static By PERIOD_INPUT               = By.cssSelector("div[style*='block'] input[class$='period-amount']");
    public static By PERIOD_SELECT              = By.cssSelector("div[style*='block'] select[class$='period-unit']");
    public static By PERIOD_ACTION_SELECT       = By.cssSelector("div[style*='block'] select[class$='period-action']");

    public static enum AfterPeriodOf{
        DAY(0, "Day"),
        END_OF_FINANCIAL_MONTH(1,  "End Of Financial Month"),
        END_OF_FINANCIAL_QUARTER(2,  "End Of Financial Quarter"),
        END_OF_FINANCIAL_YEAR(3,  "End Of Financial Year"),
        END_OF_MONTH(4,  "End Of Month"),
        END_OF_QUARTER(5,  "End Of Quarter"),
        END_OF_YEAR(6,  "End Of Year"),
        IMMEDIATELY(7,  "Immediately"),
        MONTH(8,  "Month"),
        NONE(9,  "None"),
        QUARTER(10,  "Quarter"),
        XML_DURATION(11,  "XML Duration"),
        YEAR(12,  "Year");

        private final int numberPosition;
        private final String value;

        AfterPeriodOf(int numberPosition, String value)
        {
            this.numberPosition = numberPosition;
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }
    }

    public static enum AfterPeriodOfFrom{
        CREATED_DATE(0, "Created Date"),
        DISPOSITION_ACTION(1,  "Disposition Action");

        private final int numberPosition;
        private final String value;

        AfterPeriodOfFrom(int numberPosition, String value)
        {
            this.numberPosition = numberPosition;
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }
    }

    /**
     * Constructor
     *
     * @param drone
     */
    public RmEditDispositionSchedulePage(WebDrone drone) {
        super(drone);
    }

    @Override
    public RmEditDispositionSchedulePage render(RenderTime timer)
    {
        elementRender(timer,
                getVisibleRenderElement(EDIT_DISPOSITION_SECTION),
                getVisibleRenderElement(ADD_STEP_BUTTON));
        return this;
    }

    @Override
    public RmEditDispositionSchedulePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @Override
    public RmEditDispositionSchedulePage render(final long time)
    {
        return render(new RenderTime(time));
    }

    public RmEditDispositionSchedulePage selectAfterPeriodOf(AfterPeriodOf period, String description, String intValue, AfterPeriodOfFrom fromOptionNumber){
        checkAfterPeriodChkBox();
        if(intValue!=null){
            inputPeriodValue(intValue);
        }
        if(fromOptionNumber!=null){
            selectFromOption(fromOptionNumber);
        }
        selectPeriod(period);
        inputDescription(description);
        saveStepButton();
        return new RmEditDispositionSchedulePage(drone).render();
    }

    public RmEditDispositionSchedulePage selectAfterEventCompleted(AfterPeriodOf period, String description, String intValue, AfterPeriodOfFrom fromOptionNumber){
        checkAfterPeriodChkBox();
        if(intValue!=null){
            inputPeriodValue(intValue);
        }
        if(fromOptionNumber!=null){
            selectFromOption(fromOptionNumber);
        }
        selectPeriod(period);
        inputDescription(description);
        saveStepButton();
        return new RmEditDispositionSchedulePage(drone).render();
    }

    public RmEditDispositionSchedulePage selectAfterPeriodOf(AfterPeriodOf period, String description){
        return selectAfterPeriodOf(period, description, null, null);
    }

    public void selectDispositionStep(DispositionAction step)
    {
        click(ADD_STEP_BUTTON);
        click(step.getXpath());
        drone.findAndWait(DISPOSITION_FORM);
    }

    private void saveStepButton(){
        List<WebElement> saveElements = drone.findAndWaitForElements(SAVE_BUTTON);
        for (WebElement button : saveElements)
        {
            if (button.isDisplayed()){
                button.click();
            }
        }
    }

    private void inputDescription(String description){
        List<WebElement> descriptionElements = drone.findAndWaitForElements(DESCRIPTION_AREA);
        for (WebElement desc : descriptionElements)
        {
            if (desc.isDisplayed()){
                desc.clear();
                desc.sendKeys(description);
            }
        }
    }

    private void selectPeriod(AfterPeriodOf period){
        List<WebElement> periodOption = drone.findAndWaitForElements(PERIOD_SELECT);
        List<Select> periodSelect = new ArrayList<>();
        for (WebElement action : periodOption)
        {
            if (action.isDisplayed()){
                periodSelect.add(new Select(action));
            }
        }
        periodSelect.get(periodSelect.size()-1).selectByIndex(period.numberPosition);
    }

    private void selectFromOption(AfterPeriodOfFrom fromOptionNumber){
        List<WebElement> fromOption = drone.findAndWaitForElements(PERIOD_ACTION_SELECT);
        List<Select> fromSelect = new ArrayList<Select>();
        for (WebElement action : fromOption)
        {
            if (action.isDisplayed()){
                fromSelect.add(new Select(action));
            }
        }
        fromSelect.get(fromSelect.size()-1).selectByIndex(fromOptionNumber.numberPosition);
    }

    private void checkAfterPeriodChkBox(){
        List<WebElement> fromOption = drone.findAndWaitForElements(AFTER_PERIOD_CHKBOX);
        for (WebElement afterPeriod : fromOption)
        {
            if (afterPeriod.isDisplayed()){
                if(!afterPeriod.isSelected()){
                    afterPeriod.click();
                }
            }
        }
    }

    private void inputPeriodValue(String value){
        List<WebElement> periodElements = drone.findAndWaitForElements(DESCRIPTION_AREA);
        for (WebElement period : periodElements)
        {
            if (period.isDisplayed()){
                period.clear();
                period.sendKeys(value);
            }
        }
    }

    public RmCreateDispositionPage clickDoneButton(){
        List<WebElement> saveElements = drone.findAndWaitForElements(DONE_BUTTON);
        for (WebElement button : saveElements)
        {
            if (button.isDisplayed()){
                button.click();
            }
        }
        return new RmCreateDispositionPage(drone).render();
    }
}
