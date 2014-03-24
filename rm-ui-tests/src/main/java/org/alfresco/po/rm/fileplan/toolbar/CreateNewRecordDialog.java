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

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Records management create new record dialog.
 *
 * @author Polina Lushchinskaya
 * @version 1.1
 * @since 2.2
 */
public class CreateNewRecordDialog extends SharePage
{
    /** Constants for selectors */
    // FIXME: Change the xpath expression. No hard coded text.
    public static final By ELECTRONIC_BUTTON        = By.xpath("//button[text()='Electronic']");
    public static final By NON_ELECTRONIC_BUTTON    = By.xpath("//button[text()='Non-electronic']");
    public static final By CANCEL_FILE_BUTTON       = By.xpath("//button[text()='Cancel']");
    public static final By PANEL_CONTAINER          = By.xpath("//div[contains(@class, 'panel-container')]");
    public static final By DESCRIPTION_INPUT        = By.tagName("textarea");
    public static final By NAME_INPUT               = By.cssSelector("input[id$='_default-createRecord_prop_cm_name']");
    public static final By TITLE_INPUT              = By.cssSelector("input[id$='_default-createRecord_prop_cm_title']");
    public static final By PHYSICAL_SIZE_INPUT      = By.cssSelector("input[id$='_default-createRecord_prop_rma_physicalSize']");
    public static final By NUMBER_OF_COPIES_INPUT   = By.cssSelector("input[id$='_default-createRecord_prop_rma_numberOfCopies']");
    public static final By STORAGE_LOCATION_INPUT   = By.cssSelector("input[id$='_default-createRecord_prop_rma_storageLocation']");
    public static final By SHELF_INPUT              = By.cssSelector("input[id$='_default-createRecord_prop_rma_shelf']");
    public static final By BOX_INPUT                = By.cssSelector("input[id$='default-createRecord_prop_rma_box']");
    public static final By FILE_INPUT               = By.cssSelector("input[id$='_default-createRecord_prop_rma_file']");
    public static final By SAVE_BUTTON              = By.cssSelector("button[id$='createRecord-form-submit-button']");
    public static final By CANCEL_BUTTON            = By.cssSelector("button[id$='createRecord-form-cancel-button']");

    /**
     * Constructor.
     *
     * @param drone {@link WebDrone} Web Drone
     */
    public CreateNewRecordDialog(WebDrone drone)
    {
        super(drone);
    }

    /**
     * @see org.alfresco.po.rm.fileplan.toolbar.Dialog#render(org.alfresco.webdrone.RenderTime)
     */
    @SuppressWarnings("unchecked")
    @Override
    public CreateNewRecordDialog render(RenderTime timer)
    {
        WebDroneUtil.checkMandotaryParam("timer", timer);

        elementRender(timer,
                getVisibleRenderElement(NAME_INPUT),
                getVisibleRenderElement(TITLE_INPUT),
                getVisibleRenderElement(PHYSICAL_SIZE_INPUT),
                getVisibleRenderElement(NUMBER_OF_COPIES_INPUT),
                getVisibleRenderElement(STORAGE_LOCATION_INPUT),
                getVisibleRenderElement(SHELF_INPUT),
                getVisibleRenderElement(BOX_INPUT),
                getVisibleRenderElement(FILE_INPUT),
                getVisibleRenderElement(DESCRIPTION_INPUT));
        return this;
    }

    /**
     * @see org.alfresco.webdrone.Render#render()
     */
    @SuppressWarnings("unchecked")
    @Override
    public CreateNewRecordDialog render()
    {
        return this.render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * @see org.alfresco.webdrone.Render#render(long)
     */
    @SuppressWarnings("unchecked")
    @Override
    public CreateNewRecordDialog render(final long time)
    {
        WebDroneUtil.checkMandotaryParam("time", time);

        return this.render(new RenderTime(time));
    }

    /**
     * Enter name value to name input field.
     *
     * @param name {@link String} Name of new category
     */
    public void enterName(final String name)
    {
        WebDroneUtil.checkMandotaryParam("name", name);

        WebElement nameInput = drone.findAndWait(NAME_INPUT);
        nameInput.clear();
        nameInput.sendKeys(name);
    }

    /**
     * Enter title value to title input field.
     *
     * @param title {@link String} New folder title
     */
    public void enterTitle(final String title)
    {
        WebDroneUtil.checkMandotaryParam("title", title);

        WebElement titleInput = drone.findAndWait(TITLE_INPUT);
        titleInput.clear();
        titleInput.sendKeys(title);
    }

    /**
     * Enter description value to description input field.
     *
     * @param description {@link String} New category/folder description
     */
    public void enterDescription(final String description)
    {
        WebDroneUtil.checkMandotaryParam("description", description);

        WebElement descriptionInput = drone.findAndWait(DESCRIPTION_INPUT);
        descriptionInput.clear();
        descriptionInput.sendKeys(description);
    }

    /**
     * Action that selects the save button.
     *
     * @return {@link FilePlanPage} Returns the current page
     */
    public FilePlanPage selectSave()
    {
        WebElement save = drone.findAndWait(SAVE_BUTTON);
        save.click();
        canResume();
        return new FilePlanPage(drone).render();
    }

    /**
     * Action that selects the cancel button.
     *
     * @return {@link FilePlanPage} Returns the current page
     */
    public FilePlanPage selectCancel()
    {
        WebElement cancel = drone.findAndWait(CANCEL_BUTTON);
        cancel.click();
        return new FilePlanPage(drone).render();
    }
}
