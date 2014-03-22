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

import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

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

    // FIXME: Description
    public static enum DispositionAction
    {
        // FIXME: DispositionAction should only have value and not numberPosition, xpath or name. See AfterPeriodOf as an example
        ACCESSION(0, By.xpath("//a[text()='Accession']"), "Accession", "accession"),
        DESTROY(2,  By.xpath("//a[text()='Destroy']"), "Destroy", "destroy"),
        RETAIN(3,  By.xpath("//a[text()='Retain']"), "Retain", "retain"),
        TRANSFER(4,  By.xpath("//a[text()='Transfer']"), "Transfer", "transfer"),
        CUTOFF(1,  By.xpath("//a[text()='Cut off']"), "Cut off", "cutoff");

        private final int numberPosition;
        private final By xpath;
        private final String name;
        private final String value;

        DispositionAction(int numberPosition, By xpath, String name, String value)
        {
            this.numberPosition = numberPosition;
            this.xpath = xpath;
            this.name = name;
            this.value = value;
        }

        public int getNumberPosition()
        {
            return numberPosition;
        }

        public By getXpath()
        {
            return xpath;
        }

        public String getName()
        {
            return name;
        }

        public String getValue()
        {
            return value;
        }
    }

    /**
     * Constructor
     *
     * @param drone FIXME: Description!!!
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
        // FIXME: Check parameter for public methods

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
        // FIXME: Check parameter for public methods

        return render(new RenderTime(time));
    }

    /**
     * Helper method that clicks by element
     *
     * @param locator element By locator
     */
    public void click(By locator)
    {
        // FIXME: Check parameter for public methods

        WebElement element = drone.findAndWait(locator);
        drone.mouseOverOnElement(element);
        element.click();
    }

    /**
     * FIXME: Description!!!
     *
     * @return FIXME!!!
     */
    public RmEditDispositionSchedulePage selectEditDisposition()
    {
        click(EDIT_SCHEDULE_BUTTON);
        return new RmEditDispositionSchedulePage(drone).render();
    }
}
