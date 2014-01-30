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
package org.alfresco.po.rm;

import org.alfresco.po.rm.util.RmUtils;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;

/**
 * Create new category pop up page form. The form that is
 * displayed to user when creating a new category.
 *
 * This is only available to Records management module and
 * is accessed via the file plan page when a user selects
 * the create new category button.
 *
 * @author Michael Suzuki
 * @author Tuna Aksoy
 * @version 1.7.1
 */
public class CreateNewCategoryForm extends BaseCreateNewForm
{
    /**
     * Constructor.
     *
     * @param drone {@link WebDrone}
     */
    protected CreateNewCategoryForm(WebDrone drone)
    {
        super(drone);
    }

    /**
     * @see org.alfresco.po.rm.BaseCreateNewForm#render(org.alfresco.webdrone.RenderTime)
     */
    @SuppressWarnings("unchecked")
    @Override
    public CreateNewCategoryForm render(RenderTime timer)
    {
        RmUtils.checkMandotaryParam("timer", timer);

        return (CreateNewCategoryForm) super.render(timer);
    }

    /**
     * @see org.alfresco.po.rm.BaseCreateNewForm#render(long)
     */
    @SuppressWarnings("unchecked")
    @Override
    public CreateNewCategoryForm render(long time)
    {
        return (CreateNewCategoryForm) super.render(time);
    }

    /**
     * @see org.alfresco.po.rm.BaseCreateNewForm#render()
     */
    @SuppressWarnings("unchecked")
    @Override
    public CreateNewCategoryForm render()
    {
        return (CreateNewCategoryForm) super.render();
    }

    /**
     * Enter record category id value to record category id input field.
     *
     * @param recordCategoryId {@link String} New record category id
     */
    public void enterRecordCategoryId(final String recordCategoryId)
    {
        RmUtils.checkMandotaryParam("recordCategoryId", recordCategoryId);

        super.enterId(recordCategoryId);
    }

    /**
     * Gets the new record category id input value.
     *
     * @return {@link String} Record category id
     */
    public String getRecordCategoryId()
    {
        return super.getId();
    }
}