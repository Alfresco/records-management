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

import static org.alfresco.webdrone.WebDroneUtil.checkMandotaryParam;

import java.util.List;
import java.util.Set;

import org.alfresco.po.rm.fileplan.action.AddRecordMetadataAction;
import org.alfresco.po.rm.util.RmPageObjectUtils;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Extends {@link DocumentDetailsPage} to add RM specific methods for records
 *
 * @author Tuna Aksoy
 * @since 2.2
 */
public class RecordDetailsPage extends DocumentDetailsPage
{
    private static int MAX_WAIT_TIME = 6000;
    private static final By HIDE_RECORD = By.cssSelector("div#onHideRecordAction.rm-hide-record");
    private static final By POP_UP = By.cssSelector("div.bd");
    public static final By RM_ADD_META_DATA_LINK = By.cssSelector("div#onActionAddRecordMetadata a");
    public static final By DECLARE_RM_LINK = By.cssSelector("div#onActionSimpleRepoAction.rm-create-record a");
    public static final By RM_COMPLETE_RECORD = By.cssSelector("div#onActionDeclare.rm-declare a");
    public static final By RM_EDIT_META_DATA_LINK = By.cssSelector("div.rm-edit-details a");
    public static final By RM_COPY_TO_LINK = By.cssSelector("div#onActionCopyTo.rm-copy-record-to a");
    public static final By RM_MOVE_TO_LINK = By.cssSelector("div#onActionMoveTo.rm-move-record-to a");
    public static final By RM_LINK_TO_LINK = By.cssSelector("div#onActionLinkTo.rm-link-to a");
    public static final By RM_DELETE_LINK = By.cssSelector("div#onActionDelete.rm-delete a");
    public static final By RM_VIEW_AUDIT_LOG_LINK = By.cssSelector("div#onActionViewAuditLog.rm-view-audit-log a");
    public static final By RM_ADD_TO_HOLD_LINK = By.cssSelector("div#onActionAddToHold.rm-add-to-hold a");
    public static final By RM_REMOVE_FROM_HOLD_LINK = By.cssSelector("div#onActionRemoveFromHold.rm-remove-from-hold a");
    public static final By RM_REQUEST_INFORMATION_LINK = By.cssSelector("div#onActionRequestInfo.rm-request-info a");
    public static final By PROPERTY_SET_HEADER = By.cssSelector("div.set-panel-heading");
    public static final By ADD_TO_HOLD_OK_BUTTON = By.cssSelector("button[id$='AddToHold-ok-button']");
    public static final By REMOVE_FROM_HOLD_OK_BUTTON = By.cssSelector("button[id$='RemoveFromHold-ok-button']");

    /** key rm areas on the record details page that need to be rendered */
    public static final By PROPERTIES = By.cssSelector("div.form-fields");
    public static final By ACTIONS = By.cssSelector("div.action-set");
    public static final By REFERENCES = By.cssSelector("div[id$='rm-references']");
    public static final By EVENTS = By.cssSelector("div[id$='rm-events']");

    /** edit metadata page locators */
    public static final By EDIT_METADATA_FORM = By.cssSelector("div[id*=edit-metadata].form-fields");
    public static final By EDIT_METADATA_NAME_INPUT = By.cssSelector("input[id$='default_prop_cm_name']");
    public static final By EDIT_METADATA_TITLE_INPUT = By.cssSelector("input[id$='default_prop_cm_title']");
    public static final By EDIT_METADATA_DESCRIPTION_INPUT = By.cssSelector("textarea[id$='default_prop_cm_description']");
    public static final By EDIT_PHYSICAL_SIZE_INPUT = By.cssSelector("input[id$='default_prop_rma_physicalSize']");
    public static final By EDIT_NUMBER_OF_COPIES_INPUT = By.cssSelector("input[id$='default_prop_rma_numberOfCopies']");
    public static final By EDIT_METADATA_STORAGE_LOCATION = By.cssSelector("input[id$='default_prop_rma_storageLocation']");
    public static final By EDIT_METADATA_SHELF_INPUT = By.cssSelector("input[id$='default_prop_rma_shelf']");
    public static final By EDIT_METADATA_BOX_INPUT = By.cssSelector("input[id$='default_prop_rma_box']");
    public static final By EDIT_METADATA_FILE_INPUT = By.cssSelector("input[id$='default_prop_rma_file']");
    public static final By SAVE_BUTTON = By.cssSelector("button[id$='submit-button']");
    public static final By CANCEL_BUTTON = By.cssSelector("button[id$='cancel-button']");
    public static final By COPY_MOVE_TO_BUTTON = By.cssSelector("button[id$='ok-button']");
    public static final By ACTIONS_BUTTON = By.cssSelector("div.document-actions.document-details-panel h2");
    public static final By REFERENCES_BUTTON = By.cssSelector("div.document-references.document-details-panel h2");
    public static final By PROPERTIES_SUBSECTIONS = By.cssSelector("div.set-panel-heading");
    protected static final By FILEPLAN = By.cssSelector("#HEADER_SITE_DOCUMENTLIBRARY");

    /**
     * Constructor.
     *
     * @param drone {@link WebDrone}
     */
    public RecordDetailsPage(WebDrone drone)
    {
        super(drone);
    }

    /**
     * Verifies if the page has rendered completely by checking the page load is
     * complete and in addition it will observe key HTML elements have rendered.
     *
     * @param timer Max time to wait
     * @return {@link DocumentDetailsPage}
     */
    @Override
    public synchronized RecordDetailsPage render(RenderTime timer)
    {
        WebDroneUtil.checkMandotaryParam("timer", timer);

        while (true)
        {
            timer.start();
            synchronized (this)
            {
                try
                {
                    this.wait(100L);
                }
                catch (InterruptedException e)
                {
                }
            }
            try
            {
                // If popup is not displayed start render check
                WebElement popUp = drone.find(POP_UP);
                if (!popUp.isDisplayed())
                {
                    WebElement documentVersion = drone.find(By.cssSelector(DOCUMENT_VERSION_PLACEHOLDER));
                    String docVersionOnScreen = documentVersion.getText().trim();
                    // If the text is not what we expect it to be, then repeat
                    if (StringUtils.isNotBlank(this.previousVersion) && docVersionOnScreen.equals(this.previousVersion))
                    {
                        // We are still seeing the old version number
                        // Go around again
                        continue;
                    }

                    // Populate the doc version
                    this.documentVersion = docVersionOnScreen;

                    // make sure the key RM areas are ready
                    drone.findAndWait(ACTIONS);
                    drone.findAndWait(PROPERTIES);
                    drone.findAndWait(REFERENCES);
                    drone.findAndWait(EVENTS);

                    break;
                }
            }
            catch (TimeoutException te)
            {
                throw new PageException("Document version not rendered in time", te);
            }
            catch (NoSuchElementException te)
            {
                // Expected if the page has not rendered
            }
            catch (StaleElementReferenceException e)
            {
                // This occurs occasionally, as well
            }
            finally
            {
                timer.end();
            }
        }
        return this;
    }

    /**
     * @see org.alfresco.po.share.site.document.DocumentDetailsPage#render()
     */
    @Override
    public RecordDetailsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * @see org.alfresco.po.share.site.document.DocumentDetailsPage#render(long)
     */
    @Override
    public RecordDetailsPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Mimics the action of selecting declare record.
     *
     * @return {@link FilePlanPage} Returns the file plan page object
     */
    public FilePlanPage selectDeclareRecod()
    {
        WebElement declareRmLink = drone.find(DECLARE_RM_LINK);
        declareRmLink.click();
        canResume();
        return new FilePlanPage(drone);
    }

    /**
     * Checked if add record metadata link is visibile.
     * This is a records management feature.
     *
     * @return <code>true</code> if link is visible <code>false</code> otherwise
     */
    public boolean isAddRecordMetaDataVisible()
    {
        try
        {
            WebElement addMetaDataLink = drone.find(RM_ADD_META_DATA_LINK);
            return addMetaDataLink.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * Select 'Add Record Metadata' action
     * 
     * @return {@link AddRecordMetadataAction} page object
     */
    public AddRecordMetadataAction selectAddRecordMetadata()
    {
        RmPageObjectUtils.select(drone, RM_ADD_META_DATA_LINK);
        return new AddRecordMetadataAction(drone);
    }

    /**
     * Checks if hide record link is displayed.
     * This will only be visible under the following
     * condition:
     * <ul>
     * <li>Record management module enabled</li>
     * <li>When the document has been declared as record</li>
     * </ul>
     * 
     * @return <code>true</code> if link is displayed <code>false</code> otherwise
     */
    public boolean isHideRecordLinkDisplayed()
    {
        try
        {
            WebElement hideRecord = drone.find(HIDE_RECORD);
            return hideRecord.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * Indicates whether a property set is visible in the record details view.
     * 
     * @param title title of the property set
     * @return boolean true if present, false otherwise
     */
    public boolean isPropertySetVisible(String title)
    {
        boolean result = false;
        List<WebElement> webElements = drone.findAll(PROPERTY_SET_HEADER);
        for (WebElement webElement : webElements)
        {
            if (webElement.getText().contains(title))
            {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Action opens Edit Record Metadata page
     */
    public void openEditMetadataPage()
    {
        new RecordDetailsPage(drone);
        click(RM_EDIT_META_DATA_LINK);
        drone.waitForElement(EDIT_METADATA_FORM, MAX_WAIT_TIME);
    }

    /**
     * Helper method that clicks by element
     *
     * @param locator element By locator
     */
    public void click(By locator)
    {
        checkMandotaryParam("locator", locator);

        WebElement element = drone.findAndWait(locator);
        drone.mouseOverOnElement(element);
        element.click();
    }

    public static By auditLabelsValue(String label, String value)
    {
        WebDroneUtil.checkMandotaryParam("label", label);
        WebDroneUtil.checkMandotaryParam("value", value);
        return By.xpath("//div[@class='audit-entry']//div[@class='audit-entry-header']" + "//span[contains(text(), '" + label
                + "')]/following-sibling::span[text()='" + value + "']");
    }

    /**
     * Switch to Audit Log
     */
    public static void switchToAuditLog(WebDrone drone) throws InterruptedException
    {
        WebDroneUtil.checkMandotaryParam("drone", drone);
        Set<String> windowHandles = drone.getWindowHandles();
        for (String windowHandle : windowHandles)
        {
            drone.switchToWindow(windowHandle);
            if (drone.getTitle().endsWith("Audit Log"))
            {
                break;
            }
        }
    }

    /**
     * Switch to Details Page
     */
    public static void switchToDetailsPage(WebDrone drone) throws InterruptedException
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
     * This is to verify whether the Action Present
     * 
     * @return Returns True/fase
     */
    public boolean istActionPresent()
    {

        try
        {
            WebElement actions_button = drone.find(ACTIONS_BUTTON);
            return actions_button.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;

    }

    /**
     * This is to verify whether
     * the References section is there
     * 
     * @return Returns True/fase
     */
    public boolean isReferencesPresent()
    {

        try
        {
            WebElement actions_button = drone.find(REFERENCES_BUTTON);
            return actions_button.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;

    }

    /**
     * This is to verify whether the
     * subsections in proeprties are Present
     * 
     * @return Returns True/fase
     */

    public boolean isPropertySectionVisible(String title)
    {
        boolean result = false;
        List<WebElement> webElements = drone.findAll(PROPERTIES_SUBSECTIONS);
        for (WebElement webElement : webElements)
        {
            if (webElement.getText().contains(title))
            {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * This is to connect to Fileplan
     * after the RecordDetails page
     * operations.
     */

    public FilePlanPage getFilePlan()
    {
        try
        {

            drone.findAndWait(FILEPLAN).click();

        }
        catch (NoSuchElementException e)
        {

        }
        return new FilePlanPage(drone);
    }
}