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
package org.alfresco.po.share;

import org.alfresco.po.rm.FilePlanPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;

/**
 * Extends {@link DocumentDetailsPage} to add RM specific methods
 *
 * @author Tuna Aksoy
 * @since 2.2
 */
public class RMDocumentDetailsPage extends DocumentDetailsPage
{
    private static final By RM_ADD_META_DATA_LINK = By.cssSelector("div#onActionAddRecordMetadata a");

    public RMDocumentDetailsPage(WebDrone drone)
    {
        super(drone);
    }

    private static final By DELCARE_RM_LINK = By.cssSelector("div#onActionSimpleRepoAction.rm-create-record a");

    /**
     * Verifies if the page has rendered completely by checking the page load is
     * complete and in addition it will observe key HTML elements have rendered.
     *
     * @param timer Max time to wait
     * @return {@link DocumentDetailsPage}
     */
    @SuppressWarnings("unchecked")
    @Override
    public synchronized RMDocumentDetailsPage render(RenderTime timer)
    {
        while (true)
        {
            timer.start();
            synchronized (this)
            {
                try{ this.wait(100L); } catch (InterruptedException e) {}
            }
            try
            {
                //If popup is not displayed start render check
                if(!drone.find(By.cssSelector("div.bd")).isDisplayed())
                {
                    String docVersionOnScreen = drone.find(By.cssSelector(DOCUMENT_VERSION_PLACEHOLDER)).getText().trim();
                    // If the text is not what we expect it to be, then repeat
                    if (this.previousVersion != null && docVersionOnScreen.equals(this.previousVersion))
                    {
                        // We are still seeing the old version number
                        // Go around again
                        continue;
                    }
                    // Populate the doc version
                    this.documentVersion = docVersionOnScreen;
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

    @SuppressWarnings("unchecked")
    @Override
    public RMDocumentDetailsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public RMDocumentDetailsPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Mimics the action of selecting declare record.
     * This link is only available in the records
     * management module and is displayed in the action
     * link section of the document details page.
     */
    public FilePlanPage selectDeclareRecod()
    {
        drone.find(DELCARE_RM_LINK).click();
        canResume();
        return new FilePlanPage(drone);
    }

    /**
     * Checked if add record metadata link is visibile.
     * This is a records management feature.
     * @return true if link is visible
     */
    public boolean isAddRecordMetaDataVisible()
    {
        try
        {
            return drone.find(RM_ADD_META_DATA_LINK).isDisplayed();
        }
        catch (NoSuchElementException nse) { }
        return false;
    }

    /**
     * Checks if hide record link is displayed.
     * This will only be visible under the following
     * condition:
     * <ul>
     *  <li> Record management module enabled</li>
     *  <li> When the document has been declared as record</li>
     * </ul>
     * @return true if link is displayed
     */
    public boolean isHideRecordLinkDisplayed()
    {
        try
        {
            return drone.find(By.cssSelector("div#onHideRecordAction.rm-hide-record")).isDisplayed();
        }
        catch (NoSuchElementException nse) { }
        return false;
    }
}
