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

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;

/**
 * Create new record folder popup page form. The form that is
 * displayed to user when creating a new record folder.
 *
 * This is only available to Records management module and
 * is accessed via the file plan page when a user selects
 * the create new folder button.
 *
 * @author Tuna Aksoy
 * @since 2.2
 */
public class CreateNewFolderForm extends BaseCreateNewForm
{
    /**
     * Constructor.
     *
     * @param drone {@link WebDrone}
     */
    protected CreateNewFolderForm(WebDrone drone)
    {
        super(drone);
    }

    /**
     * @see org.alfresco.po.rm.BaseCreateNewForm#render(org.alfresco.webdrone.RenderTime)
     */
    @SuppressWarnings("unchecked")
    @Override
    public CreateNewFolderForm render(RenderTime timer)
    {
        WebDroneUtil.checkMandotaryParam("timer", timer);

        return (CreateNewFolderForm) super.render(timer);
    }

    /**
     * @see org.alfresco.po.rm.BaseCreateNewForm#render(long)
     */
    @SuppressWarnings("unchecked")
    @Override
    public CreateNewFolderForm render(long time)
    {
        return (CreateNewFolderForm) super.render(time);
    }

    /**
     * @see org.alfresco.po.rm.BaseCreateNewForm#render()
     */
    @SuppressWarnings("unchecked")
    @Override
    public CreateNewFolderForm render()
    {
        return (CreateNewFolderForm) super.render();
    }

    /**
     * Enter record folder id value to record folder id input field.
     *
     * @param title {@link String} New record folder id
     */
    public void enterRecordFolderId(final String recordFolderId)
    {
        WebDroneUtil.checkMandotaryParam("recordFolderId", recordFolderId);

        super.enterId(recordFolderId);
    }

    /**
     * Gets the new record folder id input value.
     *
     * @return {@link String} Record folder id
     */
    public String getRecordFolderId()
    {
        return super.getId();
    }
}