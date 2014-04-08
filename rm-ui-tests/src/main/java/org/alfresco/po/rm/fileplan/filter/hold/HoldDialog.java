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
package org.alfresco.po.rm.fileplan.filter.hold;

import org.alfresco.po.rm.util.RmPageObjectUtils;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.openqa.selenium.By;

/**
 * Represents the hold dialogs (add to a hold and remove from hold).
 *
 * @author Tuna Aksoy
 * @since 2.2
 */
public class HoldDialog extends SharePage
{
    /** Selectors for the hold dialogs */
    private static final By TITLE = By.cssSelector("div[id^='Alfresco.rm.module.'][id*='-title'].hd");
    private static final By TABLE = By.cssSelector("div[id^='Alfresco.rm.module.'][id*='-listofholds'].hold");
    private static final By OK_BUTTON = By.cssSelector("button[id^='Alfresco.rm.module.'][id*='-ok-button']");
    private static final By CANCEL_BUTTON = By.cssSelector("button[id^='Alfresco.rm.module.'][id*='-cancel-button']");
    private static final String HOLD_CHECHBOX_EXPRESSION = "//div[text()='%s']/ancestor::tr/td[contains(@class, 'check')]/div/input";

    /**
     * Constructor.
     *
     * @param drone {@link WebDrone} Web drone instance
     */
    public HoldDialog(WebDrone drone)
    {
        super(drone);
    }

    /**
     * @see org.alfresco.webdrone.Render#render(org.alfresco.webdrone.RenderTime)
     */
    @SuppressWarnings("unchecked")
    @Override
    public HoldDialog render(RenderTime timer)
    {
        WebDroneUtil.checkMandotaryParam("timer", timer);

        RenderElement title = RenderElement.getVisibleRenderElement(TITLE);
        RenderElement table = RenderElement.getVisibleRenderElement(TABLE);
        RenderElement okButton = RenderElement.getVisibleRenderElement(OK_BUTTON);
        RenderElement cancelButton = RenderElement.getVisibleRenderElement(CANCEL_BUTTON);

        elementRender(timer, title, table, okButton, cancelButton);

        return this;
    }

    /**
     * @see org.alfresco.webdrone.Render#render(long)
     */
    @SuppressWarnings("unchecked")
    @Override
    public HoldDialog render(long time)
    {
        WebDroneUtil.checkMandotaryParam("time", time);

        RenderTime timer = new RenderTime(time);
        return render(timer);
    }

    /**
     * @see org.alfresco.webdrone.Render#render()
     */
    @SuppressWarnings("unchecked")
    @Override
    public HoldDialog render()
    {
        return render(maxPageLoadingTime);
    }

    /**
     * Clicks the 'OK' button
     */
    public void clickOK()
    {
        RmPageObjectUtils.select(drone, OK_BUTTON);
    }

    /**
     * Clicks the 'Cancel' button
     */
    public void clickCancel()
    {
        RmPageObjectUtils.select(drone, CANCEL_BUTTON);
    }

    /**
     * Selects the check box of the given hold name
     *
     * @param holdName The name of the hold which should be checked
     */
    public void selectCheckBox(String holdName)
    {
        WebDroneUtil.checkMandotaryParam("holdName", holdName);

        RmPageObjectUtils.select(drone, By.xpath(String.format(HOLD_CHECHBOX_EXPRESSION, holdName)));
    }
}