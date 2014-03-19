package org.alfresco.po.rm.fileplan;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Created by polly on 3/17/14.
 */
public class RmEditDispositionSchedulePage extends RmCreateDispositionPage {

    public static By DISPOSITION_FORM           = By.cssSelector("div[class$='disposition-form']");
    public static By SAVE_BUTTON                = By.xpath("//button[text()='Save']");
    public static By CANCEL_BUTTON              = By.xpath("//button[text()='Cancel']");
    public static By DONE_BUTTON                = By.xpath("//button[text()='Done']");
    public static By AFTER_PERIOD_CHKBOX        = By.cssSelector("input[class$='period-enabled']");
    public static By WHEN_EVENT_OCCURS_CHKBOX   = By.cssSelector("input[class$='pevents-enabled']");
    public static By DESCRIPTION_AREA           = By.cssSelector("textarea[id$='description']");
    public static By PERIOD_INPUT               = By.cssSelector("input[class$='period-amount']");
    public static By PERIOD_SELECT              = By.cssSelector("select[class$='period-unit']");
    public static By PERIOD_ACTION_SELECT       = By.cssSelector("select[class$='period-action']");

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

    @SuppressWarnings("unchecked")
    @Override
    public RmEditDispositionSchedulePage render(RenderTime timer)
    {
        elementRender(timer,
                getVisibleRenderElement(DISPOSITION_FORM),
                getVisibleRenderElement(SAVE_BUTTON),
                getVisibleRenderElement(DONE_BUTTON),
                getVisibleRenderElement(AFTER_PERIOD_CHKBOX),
                getVisibleRenderElement(WHEN_EVENT_OCCURS_CHKBOX),
                getVisibleRenderElement(DESCRIPTION_AREA),
                getVisibleRenderElement(PERIOD_INPUT),
                getVisibleRenderElement(PERIOD_SELECT),
                getVisibleRenderElement(PERIOD_ACTION_SELECT),
                getVisibleRenderElement(CANCEL_BUTTON));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public RmEditDispositionSchedulePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public RmEditDispositionSchedulePage render(final long time)
    {
        return render(new RenderTime(time));
    }

    public RmEditDispositionSchedulePage selectAfterPeriodOf(AfterPeriodOf period, String intValue, AfterPeriodOfFrom fromOptionNumber){
        WebElement afterPeriod = drone.findAndWait(AFTER_PERIOD_CHKBOX);
        if(!afterPeriod.isSelected()){
            afterPeriod.click();
        }
        if(intValue!=null){
            WebElement title = drone.find(PERIOD_INPUT);
            title.clear();
            title.sendKeys(intValue);
        }
        if(fromOptionNumber!=null){
            List<WebElement> fromOption = drone.findAndWaitForElements(PERIOD_ACTION_SELECT);
            List<Select> fromSelect = new ArrayList<Select>();
            for (WebElement action : fromOption)
            {
                fromSelect.add(new Select(action));
            }
            fromSelect.get(fromSelect.size()-1).selectByIndex(fromOptionNumber.numberPosition);
        }
        return new RmEditDispositionSchedulePage(drone).render();
    }

    public RmEditDispositionSchedulePage selectAfterPeriodOf(AfterPeriodOf period){
        return selectAfterPeriodOf(period, null, null);
    }
}
