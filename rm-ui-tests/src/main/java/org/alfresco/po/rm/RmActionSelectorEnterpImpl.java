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
import org.alfresco.webdrone.WebDroneUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Extends the {@link ActionSelectorEnterpImpl} in order to add the RM specific actions
 *
 * @author Tuna Aksoy
 * @since 2.2
 */
public class RmActionSelectorEnterpImpl extends ActionSelectorEnterpImpl
{
    // FIXME web drone should be protected in share-po class
    private WebDrone drone;

    /**
     * Select locator
     */
    private static final By ACTION_OPTIONS_SELECT = By.cssSelector("div.action select");

    private static final By CREATE_RECORD_CHECKBOX = By.xpath("//input[@param='createRecordPath']");// By.cssSelector("span[class*='createRecordPath']");

    /**
     * Wait Time variable
     */
    private final static long MAX_WAIT_TIME = 60000;

    /**
     * Rules Actions
     */
    public static enum PerformActions
    {
        COMPLETE_RECORD("declareRecord"),
        REOPEN_RECORD("undeclareRecord"),
        OPEN_RECORD_FOLDER("openRecordFolder"),
        CLOSE_RECORD_FOLDER("closeRecordFolder"),
        FILE_TO("fileTo"),
        COPY_TO("copyTo"),
        MOVE_TO("moveTo"),
        REJECT("reject"),
        REQUEST_INFORMATION("requestInfo"),
        COMPLETE_EVENT("completeEvent"),
        ADD_RECORD_TYPES("addRecordTypes"),
        EXECUTE_SCRIPT("executeScript"),
        SEND_EMAIL("sendEmail"),
        SET_PROPERTY_VALUE("setPropertyValue"),
        VIEW_AUDIT_LOG_LINK("viewAuditLog");

        private final String value;

        PerformActions(String value)
        {
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
     * @param drone web drone
     */
    public RmActionSelectorEnterpImpl(WebDrone drone)
    {
        super(drone);
        this.drone = drone;
    }

    /**
     * Action select action from Perform Action combobox
     *
     * @param action action from Perform Actions enumeration
     */
    public void selectAction(PerformActions action)
    {
        WebDroneUtil.checkMandotaryParam("action", action);

        Select dropdown = new Select(drone.findAndWait(ACTION_OPTIONS_SELECT));
        dropdown.selectByValue(action.getValue());
    }

    /**
     * Action select File To from Perform Action combobox
     *
     * @param path where is file to path
     * @param createRecordPath
     */
    public void selectFileTo(String path, boolean createRecordPath)
    {
        selectAction(PerformActions.FILE_TO);
        selectCopyMoveFileTo(path, createRecordPath);
    }

    /**
     * Action select File To from Perform Action combobox
     *
     * @param path where is file to path
     * @param createRecordPath
     */
    public void selectCopyTo(String path, boolean createRecordPath)
    {
        selectAction(PerformActions.COPY_TO);
        selectCopyMoveFileTo(path, createRecordPath);
    }

    /**
     * Action select File To from Perform Action combobox
     *
     * @param path where is file to path
     * @param createRecordPath
     */
    public void selectMoveTo(String path, boolean createRecordPath)
    {
        selectAction(PerformActions.MOVE_TO);
        selectCopyMoveFileTo(path, createRecordPath);
    }

    /**
     * Action select Copy/Move/File-To utility
     *
     * @param path where is file to path
     * @param createRecordPath
     */
    public void selectCopyMoveFileTo(String path, boolean createRecordPath)
    {
        WebDroneUtil.checkMandotaryParam("createRecordPath", createRecordPath);

        if (createRecordPath)
        {
            toggleCreateRecordPath(MAX_WAIT_TIME);
        }
        setFileToPath(path, MAX_WAIT_TIME);
    }

    /**
     * Input File to path
     *
     * @param path File To path
     * @param timeout   time out
     */
    private void setFileToPath(String path, long timeout)
    {
        WebDroneUtil.checkMandotaryParam("path", path);
        WebDroneUtil.checkMandotaryParam("timeout", timeout);

        WebElement input = getDrone().findAndWait(By.className("yui-ac-input"), timeout);
        input.clear();
        input.sendKeys(path);
        // I know we shouldn't have sleeps but I really can't find another way to figure out when yui autocomplete has finished doing its thing
        try { Thread.sleep(2000); } catch(InterruptedException e) { }
    }

    /**
     * Helper method to toogle the create path check box
     *
     * @param timeout time out
     */
    private void toggleCreateRecordPath(long timeout)
    {
        WebElement checkBox = getDrone().findAndWait(CREATE_RECORD_CHECKBOX, timeout);
        checkBox.click();
    }
    
    /**
     *Helper method to view audit log
     * 
     */
    
    public void selectViewAuditLog(String path, boolean createRecordPath)
    {
        selectAction(PerformActions.VIEW_AUDIT_LOG_LINK);
        verifyViewAuditLog();
    }
    public void verifyViewAuditLog(){
        WebElement linkVerifyViewAuditLog = getDrone().findAndWait(By.cssSelector("div.rm-view-audit-log"));
        linkVerifyViewAuditLog.click();
    }
}
