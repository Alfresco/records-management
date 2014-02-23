/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;

/**
 * Creates a new record category dialog.
 *
 * @author Michael Suzuki
 * @author Tuna Aksoy
 * @version 1.7.1
 */
public class CreateNewRecordCategoryDialog extends BaseDialog
{
    /**
     * Constructor.
     *
     * @param drone {@link WebDrone}
     */
    public CreateNewRecordCategoryDialog(WebDrone drone)
    {
        super(drone);
    }

    /**
     * @see org.alfresco.po.rm.fileplan.toolbar.BaseDialog#render(org.alfresco.webdrone.RenderTime)
     */
    @SuppressWarnings("unchecked")
    @Override
    public CreateNewRecordCategoryDialog render(RenderTime timer)
    {
        WebDroneUtil.checkMandotaryParam("timer", timer);

        return (CreateNewRecordCategoryDialog) super.render(timer);
    }

    /**
     * @see org.alfresco.po.rm.fileplan.toolbar.BaseDialog#render(long)
     */
    @SuppressWarnings("unchecked")
    @Override
    public CreateNewRecordCategoryDialog render(long time)
    {
        WebDroneUtil.checkMandotaryParam("time", time);

        return (CreateNewRecordCategoryDialog) super.render(time);
    }

    /**
     * @see org.alfresco.po.rm.fileplan.toolbar.BaseDialog#render()
     */
    @SuppressWarnings("unchecked")
    @Override
    public CreateNewRecordCategoryDialog render()
    {
        return (CreateNewRecordCategoryDialog) super.render();
    }

    /**
     * Enter record category id value to record category id input field.
     *
     * @param recordCategoryId {@link String} Record category id
     */
    public void enterRecordCategoryId(final String recordCategoryId)
    {
        WebDroneUtil.checkMandotaryParam("recordCategoryId", recordCategoryId);

        super.enterId(recordCategoryId);
    }

    /**
     * Gets the record category id input value.
     *
     * @return {@link String} Record category id
     */
    public String getRecordCategoryId()
    {
        return super.getId();
    }
}