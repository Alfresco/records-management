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

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;

/**
 * Creates a new record folder dialog.
 *
 * @author Tuna Aksoy
 * @since 2.2
 */
public class CreateNewRecordFolderDialog extends BaseDialog
{
    /**
     * Constructor.
     *
     * @param drone {@link WebDrone}
     */
    public CreateNewRecordFolderDialog(WebDrone drone)
    {
        super(drone);
    }

    /**
     * @see org.alfresco.po.rm.fileplan.toolbar.BaseDialog#render(org.alfresco.webdrone.RenderTime)
     */
    @SuppressWarnings("unchecked")
    @Override
    public CreateNewRecordFolderDialog render(RenderTime timer)
    {
        WebDroneUtil.checkMandotaryParam("timer", timer);

        return (CreateNewRecordFolderDialog) super.render(timer);
    }

    /**
     * @see org.alfresco.po.rm.fileplan.toolbar.BaseDialog#render(long)
     */
    @SuppressWarnings("unchecked")
    @Override
    public CreateNewRecordFolderDialog render(long time)
    {
        WebDroneUtil.checkMandotaryParam("time", time);

        return (CreateNewRecordFolderDialog) super.render(time);
    }

    /**
     * @see org.alfresco.po.rm.fileplan.toolbar.BaseDialog#render()
     */
    @SuppressWarnings("unchecked")
    @Override
    public CreateNewRecordFolderDialog render()
    {
        return (CreateNewRecordFolderDialog) super.render();
    }

    /**
     * Enter record folder id value to record folder id input field.
     *
     * @param title {@link String} Record folder id
     */
    public void enterRecordFolderId(final String recordFolderId)
    {
        WebDroneUtil.checkMandotaryParam("recordFolderId", recordFolderId);

        super.enterId(recordFolderId);
    }

    /**
     * Gets the record folder id input value.
     *
     * @return {@link String} Record folder id
     */
    public String getRecordFolderId()
    {
        return super.getId();
    }
}