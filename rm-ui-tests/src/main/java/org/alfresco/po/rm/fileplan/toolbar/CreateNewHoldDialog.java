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
package org.alfresco.po.rm.fileplan.toolbar;

import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Represents the create new hold dialog.
 *
 * @author Tuna Aksoy
 * @since 2.2
 */
public class CreateNewHoldDialog extends Dialog
{
    /** Selectors */
    protected static final By REASON_INPUT = By.cssSelector("textarea[id$='_default-createFolder_prop_rma_holdReason']");
    protected static final By DELETE_HOLD_INPUT = By.cssSelector("input[id$='_default-createFolder_prop_rma_deleteWhenEmpty-entry']");

    /**
     * Constructor.
     *
     * @param drone {@link WebDrone}
     */
    public CreateNewHoldDialog(WebDrone drone)
    {
        super(drone);
    }

    /**
     * @see org.alfresco.po.rm.fileplan.toolbar.Dialog#render(org.alfresco.webdrone.RenderTime)
     */
    @SuppressWarnings("unchecked")
    @Override
    public CreateNewHoldDialog render(RenderTime timer)
    {
        WebDroneUtil.checkMandotaryParam("timer", timer);

        RenderElement name = RenderElement.getVisibleRenderElement(NAME_INPUT);
        RenderElement reason = RenderElement.getVisibleRenderElement(REASON_INPUT);

        elementRender(timer, name, reason);

        return this;
    }

    /**
     * @see org.alfresco.po.rm.fileplan.toolbar.Dialog#render(long)
     */
    @SuppressWarnings("unchecked")
    @Override
    public CreateNewHoldDialog render(long time)
    {
        WebDroneUtil.checkMandotaryParam("time", time);

        RenderTime timer = new RenderTime(time);
        return render(timer);
    }

    /**
     * @see org.alfresco.po.rm.fileplan.toolbar.Dialog#render()
     */
    @SuppressWarnings("unchecked")
    @Override
    public CreateNewHoldDialog render()
    {
        return render(maxPageLoadingTime);
    }

    /**
     * Enter reason value to name reason field.
     *
     * @param reason {@link String} Name of new category
     */
    public void enterReason(final String reason)
    {
        WebDroneUtil.checkMandotaryParam("reason", reason);

        WebElement input = drone.findAndWait(REASON_INPUT);
        input.clear();
        input.sendKeys(reason);
    }

    /**
     * Tick/Untick the check box to determine if the hold
     * should be delete if it is empty.
     *
     * @param tick {@link Boolean} tick for delete the hold if it is empty untick otherwise
     */
    public void tickDeleteHold(final boolean tick)
    {
        WebDroneUtil.checkMandotaryParam("tick", tick);

        WebElement checkbox = drone.findAndWait(DELETE_HOLD_INPUT);
        boolean selected = checkbox.isSelected();
        if ((tick && !selected) || !tick && selected)
        {
            checkbox.click();
        }
    }
}