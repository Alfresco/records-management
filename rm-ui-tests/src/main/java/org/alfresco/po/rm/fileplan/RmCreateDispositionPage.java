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

import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;
import static org.alfresco.webdrone.WebDroneUtil.checkMandotaryParam;

/**
 * Records management create disposition page.
 *
 * @author Polina Lushchinskaya
 * @version 1.1
 * @since 2.2
 */
public class RmCreateDispositionPage extends FolderDetailsPage
{
    /** Constants for the selectors */
    public static By CREATE_DISPOSITION_BUTTON  = By.cssSelector("button[id$='createschedule-button-button']");
    public static By DISPOSITION_SECTION        = By.cssSelector("div[class$='disposition']");
    public static By EDIT_PROPERTIES_BUTTON     = By.cssSelector("button[id$='editproperties-button-button']");
    public static By EDIT_SCHEDULE_BUTTON       = By.cssSelector("button[id$='editschedule-button-button']");

    /**
     * Disposion Actions
     */
    public static enum DispositionAction
    {
        // FIXME: DispositionAction should only have value and not numberPosition, xpath or name. See AfterPeriodOf as an example
        /* Don't agree with comment. Can Delete Number position. But, for example, On create Disposition page there no cretarias to select disposiotion except xpath
         * And on Create Rule page we can select by value. So, we need xpath and value too.
         */
        /* FIXME: In this case it's true that the "value" is not enough
         * The reason for that is after we create a YUI menu button we loose the "value" in the UI
         * and there is no other reliable way for selecting.
         * You can keep the "name" but extract the text to a properties file.
         * No hard coded text in the code please.
         * And please delete numberPosition and xpath
         */
        ACCESSION("accession.step", "accession"),
        DESTROY("destroy.step", "destroy"),
        RETAIN("retain.step", "retain"),
        TRANSFER("transfer.step", "transfer"),
        CUTOFF("cutoff.step", "cutoff");

        public final String name;
        private final String value;

        DispositionAction(String name, String value)
        {
            this.name = name;
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
     * @param drone {@link WebDrone}
     */
    public RmCreateDispositionPage(WebDrone drone)
    {
        super(drone);
    }

    /**
     * @see org.alfresco.po.share.site.document.FolderDetailsPage#render(org.alfresco.webdrone.RenderTime)
     */
    @SuppressWarnings("unchecked")
    @Override
    public RmCreateDispositionPage render(RenderTime timer)
    {
        checkMandotaryParam("timer", timer);

        elementRender(timer,
                getVisibleRenderElement(DISPOSITION_SECTION),
                getVisibleRenderElement(EDIT_PROPERTIES_BUTTON),
                getVisibleRenderElement(EDIT_SCHEDULE_BUTTON));
        return this;
    }

    /**
     * @see org.alfresco.po.share.site.document.FolderDetailsPage#render()
     */
    @SuppressWarnings("unchecked")
    @Override
    public RmCreateDispositionPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * @see org.alfresco.po.share.site.document.FolderDetailsPage#render(long)
     */
    @SuppressWarnings("unchecked")
    @Override
    public RmCreateDispositionPage render(final long time)
    {
        checkMandotaryParam("time", time);

        return render(new RenderTime(time));
    }

    /**
     * Helper method that clicks by element
     *
     * @param locator element By locator
     */
    public void click(By locator)
    {
        checkMandotaryParam("locator", locator);

        WebElement element = drone.findAndWait(locator);
        drone.mouseOverOnElement(element);
        element.click();
    }

    /**
     * Action Click Edit Disposition Button
     *
     * @return Edit Disposition Page
     */
    public RmEditDispositionSchedulePage selectEditDisposition()
    {
        click(EDIT_SCHEDULE_BUTTON);
        return new RmEditDispositionSchedulePage(drone).render();
    }
}
