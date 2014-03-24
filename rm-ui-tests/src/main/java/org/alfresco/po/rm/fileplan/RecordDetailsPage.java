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

import java.util.List;

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
    private static final By HIDE_RECORD = By.cssSelector("div#onHideRecordAction.rm-hide-record");
    private static final By POP_UP = By.cssSelector("div.bd");
    private static final By RM_ADD_META_DATA_LINK = By.cssSelector("div#onActionAddRecordMetadata a");
    private static final By DELCARE_RM_LINK = By.cssSelector("div#onActionSimpleRepoAction.rm-create-record a");
    private static final By PROPERTY_SET_HEADER = By.cssSelector("div.set-panel-heading");
    
    /** key rm areas on the record details page that need to be rendered */
    private static final By PROPERTIES = By.cssSelector("div.form-fields");
    private static final By ACTIONS = By.cssSelector("div.action-set");
    private static final By REFERENCES = By.cssSelector("div[id$='rm-references']");
    private static final By EVENTS = By.cssSelector("div[id$='rm-events']");
    
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
    @SuppressWarnings("unchecked")
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
                //If popup is not displayed start render check
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
                throw new PageException("Document version not rendered in time",te);
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
    @SuppressWarnings("unchecked")
    @Override
    public RecordDetailsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * @see org.alfresco.po.share.site.document.DocumentDetailsPage#render(long)
     */
    @SuppressWarnings("unchecked")
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
        WebElement declareRmLink = drone.find(DELCARE_RM_LINK);
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
     *  <li> Record management module enabled</li>
     *  <li> When the document has been declared as record</li>
     * </ul>
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
     * @return boolean  true if present, false otherwise
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
}