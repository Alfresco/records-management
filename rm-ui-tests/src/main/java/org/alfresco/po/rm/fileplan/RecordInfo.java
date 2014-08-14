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
import org.alfresco.po.rm.fileplan.filter.hold.HoldDialog;
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
    private static final By ADD_TO_HOLD = By.cssSelector("div.rm-add-to-hold a");
    private static final By REMOVE_FROM_HOLD = By.cssSelector("div.rm-remove-from-hold a");
    private static final String NODE_NAME_SELECTOR_EXPRESSION = "//a[text() = '%s']";

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
        ((FileDirectoryInfoImpl) fileDirectoryInfo).selectMoreLink();
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
        ((FileDirectoryInfoImpl) fileDirectoryInfo).selectMoreLink();
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

    /**
     * Click on the add to hold action and renders the page
     *
     * @return Returns the hold dialog
     */
    public HoldDialog clickAddToHold()
    {
        clickAction(ADD_TO_HOLD);
        return new HoldDialog(drone).render();
    }

    /**
     * Click on the remove from hold action and renders the page
     *
     * @return Returns the hold dialog
     */
    public HoldDialog clickRemoveFromHold()
    {
        // FIXME
        // clickAction method will not work here as there are a limited number of action for a record in a hold,
        // so the selectMore action will not appear as an option. It's worth having a better approach in the core
        // and not worrying about how many actions there will be. 
        By nodeNameSelector = By.xpath(String.format(NODE_NAME_SELECTOR_EXPRESSION, fileDirectoryInfo.getName()));
        FileDirectoryInfoImpl fileDirectoryInfoImpl = (FileDirectoryInfoImpl) fileDirectoryInfo;
        WebElement actions = fileDirectoryInfoImpl.findElement(nodeNameSelector);
        drone.mouseOverOnElement(actions);
        fileDirectoryInfoImpl.findElement(REMOVE_FROM_HOLD).click();
        return new HoldDialog(drone).render();
    }
}
