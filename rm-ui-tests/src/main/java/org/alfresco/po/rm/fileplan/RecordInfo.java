/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.rm.fileplan;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Set;

import org.alfresco.po.rm.RmCopyOrMoveUnfiledContentPage;
import org.alfresco.po.rm.fileplan.action.AddRecordMetadataAction;
import org.alfresco.po.rm.fileplan.filter.hold.HoldDialog;
import org.alfresco.po.rm.fileplan.toolbar.ViewAuditLogDialog;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.FileDirectoryInfoImpl;
import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
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
    private static final By REMOVE_FROM_HOLD = By.cssSelector("div#onActionRemoveFromHold>a");
    private static final String NODE_NAME_SELECTOR_EXPRESSION = "//a[text() = '%s']";
    private static final By COPY_TO = By.cssSelector(".rm-copy-record-to");
    private static final By VIEW_AUDIT_RECORD = By.cssSelector("div.rm-view-audit-log>a");
    private static final By COMPLETE_RECORD = By.cssSelector("div#onActionDeclare>a");
    private static final By MOVE_TO = By.cssSelector("div#onActionMoveTo>a");
    private static final By LINK_TO = By.cssSelector("div#onActionLinkTo>a");
    private static final By DELETE = By.cssSelector("div#onActionDelete>a");
    private static final By REQUEST_INFORMATION = By.cssSelector("div.rm-request-info>a.action-link");
    private static final By VIEW_AUDITDIT_LOG = By.cssSelector("div#onActionViewAuditLog>a");
    private static final By EDIT_META_DATA = By.cssSelector(".rm-edit-details>a");
    private static final By DOWNLOAD = By.cssSelector("div.rm-document-download a");
    private static final By HOLD_ACTIONS = By.cssSelector("div#yui-gen139");
    private static final By ON_HOLD = By.cssSelector("img[title='On Hold']");
    protected long maxPageLoadingTime;
    // private static final By REQUEST_FORINFO = By.cssSelector("div#onActionRequestInfo>a");

    /** web drone */
    private WebDrone drone;

    /** file directory information */
    private FileDirectoryInfo fileDirectoryInfo;

    private HtmlElement htmlElement;

    /**
     * Constructor
     *
     * @param drone web drone
     * @param fileDirectoryInfo file directory information
     */
    public RecordInfo(WebDrone drone, FileDirectoryInfo fileDirectoryInfo)
    {
        this.drone = drone;
        this.fileDirectoryInfo = fileDirectoryInfo;
        this.htmlElement = (HtmlElement) fileDirectoryInfo;
    }

    /**
     * Click on the title of the record
     *
     * @return {@link RecordDetailsPage} record details page
     */
    public RecordDetailsPage clickTitle()
    {
        fileDirectoryInfo.clickOnTitle();
        return new RecordDetailsPage(drone);
    }

    /**
     * Helper method to indicate whether an action is visible or not.
     *
     * @param action action selector
     * @return boolean true if visible, false otherwise
     */
    public boolean isVisibleAction(By action)
    {
        ((FileDirectoryInfoImpl) fileDirectoryInfo).selectMoreLink();
        try
        {
            WebElement deleteLink = htmlElement.findElement(action);
            return deleteLink.isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * Helper method to click on an action
     *
     * @param action action selector
     */
    public void clickAction(By action)
    {
        ((FileDirectoryInfoImpl) fileDirectoryInfo).selectMoreLink();
        this.htmlElement.findAndWait(action).click();
    }

    /**
     * This method is to verify
     * actions after hold
     * 
     * @param action
     * @return
     */
    public boolean afterHoldIsVisibleAction(By action)
    {
        // FIXME
        // clickAction method will not work here as there are a limited number of action for a record in a hold,
        // so the selectMore action will not appear as an option. It's worth having a better approach in the core
        // and not worrying about how many actions there will be.

        // By nodeNameSelector = By.xpath(String.format(NODE_NAME_SELECTOR_EXPRESSION, fileDirectoryInfo.getName()));
        // drone.waitForElement(By.xpath(String.format(NODE_NAME_SELECTOR_EXPRESSION, fileDirectoryInfo.getName())), SECONDS.convert(maxPageLoadingTime,
        // MILLISECONDS));
        By nodeNameSelector = By.xpath(String.format(NODE_NAME_SELECTOR_EXPRESSION, fileDirectoryInfo.getName()));
        FileDirectoryInfoImpl fileDirectoryInfoImpl = (FileDirectoryInfoImpl) fileDirectoryInfo;
        WebElement actions = fileDirectoryInfoImpl.findElement(nodeNameSelector);
        drone.mouseOverOnElement(actions);
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
     * click on Delete action link
     * perfroms delete record
     * action.
     */
    public void clickDeleteRecord()
    {
        clickAction(DELETE);
        WebElement confirmDelete = drone.find(By.cssSelector("div#prompt div.ft span span button"));
        confirmDelete.click();
        drone.waitUntilVisible(By.cssSelector("div.bd"), "successfully", 10);
        drone.waitUntilNotVisibleWithParitalText(By.cssSelector("div.bd"), "successfully", 10);

    }

    /**
     * click on CompleteRecord action link
     * perfroms complete record
     * action.
     */
    public void clickCompleteRecord()
    {
        clickAction(COMPLETE_RECORD);
        drone.waitUntilVisible(By.cssSelector("div.bd"), "completed", 10);
        drone.waitUntilNotVisibleWithParitalText(By.cssSelector("div.bd"), "completed", 10);
    }

    /**
     * verifies whether
     * the record is completed.
     * 
     * @return boolean
     */
    public boolean verifyCompleteRecord()
    {

        try
        {
            WebElement banner = drone.find(By.cssSelector("div#yui-gen300"));
            return (banner.isDisplayed());
        }
        catch (NoSuchElementException e)
        {
            return true;
        }
        catch (TimeoutException e)
        {
            return true;
        }

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
     * Click on Link to action to link the records wit
     * various folders.
     * 
     * @return Returns the RmCopyOrMoveUnfiledContentPage
     */
    public RmCopyOrMoveUnfiledContentPage clickLinkto()
    {
        clickAction(LINK_TO);
        return new RmCopyOrMoveUnfiledContentPage(drone);
    }

    /**
     * Click on verifylog action to verify the logs
     * 
     * @return Returns ViewAuditDialog
     * @throws InterruptedException
     */
    public ViewAuditLogDialog clickVerifyLog() throws InterruptedException
    {
        clickAction(VIEW_AUDIT_RECORD);

        return new ViewAuditLogDialog(drone);
    }

    /**
     * This method is switch back to
     * RecordInfoPage
     * 
     * @param drone
     * @throws InterruptedException
     */
    public void switchToRecordInfoPage(WebDrone drone) throws InterruptedException
    {
        WebDroneUtil.checkMandotaryParam("drone", drone);
        Set<String> windowHandles = drone.getWindowHandles();
        for (String windowHandle : windowHandles)
        {
            drone.switchToWindow(windowHandle);
            if (drone.getTitle().endsWith("Document Details"))
            {
                break;
            }
        }
    }

    /**
     * This method is used to switch to Auditlog window.
     * 
     * @param drone
     * @throws InterruptedException
     */

    public void switchToAuditLog(WebDrone drone) throws InterruptedException
    {
        WebDroneUtil.checkMandotaryParam("drone", drone);
        Set<String> windowHandles = drone.getWindowHandles();
        for (String windowHandle : windowHandles)
        {
            drone.switchToWindow(windowHandle);
            if (drone.getTitle().endsWith("Audit Log"))
            {
                drone.switchToWindow(windowHandle);
                System.out.println("drone information" + drone.getTitle());
                break;
            }
        }
    }

    /**
     * Click on the remove from hold action
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
        drone.waitUntilElementClickable(REMOVE_FROM_HOLD, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        fileDirectoryInfoImpl.findElement(REMOVE_FROM_HOLD).click();
        return new HoldDialog(drone).render();
    }

    /**
     * Click on Copy to action copy the record to folder.
     * 
     * @return Returns RmCopyOrMoveUnfiledContentPage
     */
    public RmCopyOrMoveUnfiledContentPage clickCopyto()
    {
        fileDirectoryInfo.selectCopyTo();
        return new RmCopyOrMoveUnfiledContentPage(drone);
    }

    /**
     * Click on Move to action to move the records
     * between the folders.
     * 
     * @return Returns RmCopyOrMoveUnfiledContentPage
     */
    public RmCopyOrMoveUnfiledContentPage clickMoveto()
    {
        clickAction(MOVE_TO);
        return new RmCopyOrMoveUnfiledContentPage(drone);
    }

    /**
     * Indicates whether the add record metadata action is visible or not
     *
     * @return boolean true if visible, false otherwise
     */
    public boolean isVisibleAddRecordMetadata()
    {
        return isVisibleAction(EDIT_RECORD_METADATA_ACTION);
    }

    /**
     * This to verify wheter the RemoveFromHold action visible
     * 
     * @return Returns True/fase
     */
    public boolean IsVisibleRemoveFromHold()
    {
        boolean result = (afterHoldIsVisibleAction(REMOVE_FROM_HOLD));
        return result;

    }

    /**
     * This is verify whether the Moveto action present
     * 
     * @return Returns True/fase
     */

    public boolean IsVisibleMoveTo()
    {
        return isVisibleAction(MOVE_TO);
    }

    /**
     * This is verify whether the Linkto Action present
     * 
     * @return Returns True/fase
     */
    public boolean IsVisibleLinkTo()
    {
        return isVisibleAction(LINK_TO);
    }

    /**
     * This is to verify whether the CompleteRecord Action Present
     * 
     * @return Returns True/fase
     */
    public boolean IsVisibleCompleteRecord()
    {
        return isVisibleAction(COMPLETE_RECORD);
    }

    /**
     * This is to verify whether the Delete Action Present
     * 
     * @return Returns True/fase
     */
    public boolean IsVisibleDelete()
    {
        return isVisibleAction(DELETE);
    }

    /**
     * This is to verify whether the RequestInfo Action Present
     * 
     * @return Returns True/fase
     */
    public boolean IsVisibleRequestInfo()
    {
        return isVisibleAction(REQUEST_INFORMATION);
    }

    /**
     * This is to verify whether the Copyto Action Present
     * 
     * @return Returns True/fase
     */
    public boolean IsVisibleCopyto()
    {
        return isVisibleAction(COPY_TO);
    }

    /**
     * This is to verify whether the EditMetaData Action Present
     * 
     * @return Returns True/fase
     */
    public boolean IsVisibleEditMetaData()
    {
        return isVisibleAction(EDIT_META_DATA);

    }

    /**
     * This is to verify whether the ViewAuditLog Action Present
     * 
     * @return Returns True/fase
     */
    public boolean IsVisibleViewAuditLog()
    {
        return isVisibleAction(VIEW_AUDITDIT_LOG);
    }

    /**
     * This is to verify whether the AddtoHold Action Present
     * 
     * @return Returns True/fase
     */

    public boolean IsVisibleAddtoHold()
    {
        return isVisibleAction(ADD_TO_HOLD);
    }

    /**
     * This is to verify whether the DownLoad Action Present
     * 
     * @return Returns True/fase
     */
    public boolean IsVisibleDownload()
    {
        return isVisibleAction(DOWNLOAD);
    }

    /**
     * This is to verify whether the ApplyHold Action Present
     * 
     * @return Returns True/fase
     */
    public boolean IsVisibleHoldActions()
    {
        return isVisibleAction(HOLD_ACTIONS);
    }

    /**
     * click on Request for Info.
     * 
     * @return Returns RMInfoRequestforRecordPage
     */

    public RMInfoRequestforRecordPage clickRequestforInfo()
    {

        clickAction(REQUEST_INFORMATION);
        return new RMInfoRequestforRecordPage(drone);

    }

    /**
     * click on Editmeta data action
     * 
     * @return returns EditDocumentPropertiesPage
     */
    public EditDocumentPropertiesPage clickEditMetadata()
    {

        clickAction(EDIT_META_DATA);
        return new EditDocumentPropertiesPage(drone);

    }
    
    /**
     * This method verifies the
     * on hold symbol
     * 
     * @return
     */
    public boolean isOnHold()
    {
        try
        {
           WebElement onHold=  drone.find(ON_HOLD);
           return onHold.isDisplayed();
            
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }
}
