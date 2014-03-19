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

import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.ActionSelectorEnterpImpl;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Extends the {@link ActionSelectorEnterpImpl} in order to add the RM specific actions
 *
 * @author Tuna Aksoy
 * @since 2.2
 */
public class RmActionSelectorEnterpImpl extends ActionSelectorEnterpImpl
{
    private final static long MAX_WAIT_TIME = 60000;

    public static enum PerformActions
    {
        COMPLETE_RECORD(1, "Complete record"),
        REOPEN_RECORD(2, "Reopen record"),
        OPEN_RECORD_FOLDER(3, "Open record folder"),
        CLOSE_RECORD_FOLDER(4, "Close record folder"),
        FILE_TO(5, "File to"),
        REJECT(6, "Reject"),
        REQUEST_INFORMATION(7, "Request information"),
        COMPLETE_EVENT(8, "Complete event"),
        ADD_RECORD_TYPES(9, "Add record types"),
        EXECUTE_SCRIPT(10, "Execute script"),
        SEND_EMAIL(11, "Send email"),
        SET_PROPERTY_VALUE(12, "Set property value");


        private final int numberPosition;
        private final String value;

        PerformActions(int numberPosition, String value)
        {
            this.numberPosition = numberPosition;
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }
    }

    public RmActionSelectorEnterpImpl(WebDrone drone)
    {
        super(drone);
    }

    public void selectAction(PerformActions action){
        super.selectAction(action.numberPosition);
    }

    public void selectFileTo()
    {
        super.selectAction(PerformActions.FILE_TO.numberPosition);
    }

    public void selectFileTo(String path, boolean createRecordPath)
    {
        selectFileTo();
        setFileToPath(path, MAX_WAIT_TIME);
        if (createRecordPath)
        {
            toggleCreateRecordPath(MAX_WAIT_TIME);
        }
    }

    private void setFileToPath(String path, long timeout)
    {
        WebElement input = getDrone().findAndWait(By.className("yui-ac-input"), timeout);
        input.clear();
        input.sendKeys(path);
    }

    private void toggleCreateRecordPath(long timeout)
    {
        WebElement checkBox = getDrone().findAndWait(By.xpath("//span[@class='menutype_action menuname_fileTo paramtype_d_boolean paramname_createRecordPath']/child::input"), timeout);
        checkBox.click();
    }
}
