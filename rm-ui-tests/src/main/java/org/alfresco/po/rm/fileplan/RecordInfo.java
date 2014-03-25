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

import org.alfresco.po.rm.fileplan.action.AddRecordMetadataAction;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.FileDirectoryInfoImpl;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Record information class.
 * <p>
 * Encapsulates the information found about a record on the row of the file plan navigation page.
 *
 * @author Roy Wetherall
 * @since 2.2
 */
public class RecordInfo
{
    /** record actions */
    private static final By EDIT_RECORD_METADATA_ACTION = By.cssSelector("div.rm-add-record-metadata a");

    /** web drone */
    private WebDrone drone;

    /** file directory information */
    private FileDirectoryInfo fileDirectoryInfo;

    /**
     * Constructor
     *
     * @param drone                 web drone
     * @param fileDirectoryInfo     file directory information
     */
    public RecordInfo(WebDrone drone, FileDirectoryInfo fileDirectoryInfo)
    {
        this.drone = drone;
        this.fileDirectoryInfo = fileDirectoryInfo;
    }

    /**
     * Click on the title of the record
     *
     * @return  {@link RecordDetailsPage}   record details page
     */
    public RecordDetailsPage clickTitle()
    {
        fileDirectoryInfo.clickOnTitle();
        return new RecordDetailsPage(drone);
    }

    /**
     * Helper method to indicate whether an action is visible or not.
     *
     * @param action    action selector
     * @return boolean  true if visible, false otherwise
     */
    private boolean isVisibleAction(By action)
    {
        fileDirectoryInfo.selectMoreAction().click();
        try
        {
            WebElement link = drone.find(action);
            return link.isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Helper method to click on an action
     *
     * @param action    action selector
     */
    private void clickAction(By action)
    {
        fileDirectoryInfo.selectMoreAction().click();
        WebElement link = ((FileDirectoryInfoImpl)fileDirectoryInfo).findAndWait(action);
        link.click();
    }

    /**
     * Indicates whether the add record metadata action is visible or not
     *
     * @return  boolean true if visible, false otherwise
     */
    public boolean isVisibleAddRecordMetadata()
    {
        return isVisibleAction(EDIT_RECORD_METADATA_ACTION);
    }

    /**
     * Click on the add record metadata action and render page
     *
     * @return
     */
    public AddRecordMetadataAction clickAddRecordMetadata()
    {
        clickAction(EDIT_RECORD_METADATA_ACTION);
        return new AddRecordMetadataAction(drone).render();
    }
}
