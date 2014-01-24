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

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Create new record folder popup page form.
 * The form that is displayed to user when creating
 * a new record folder.
 * 
 * This is only available to Records management module and
 * is accessed via the file plan page when a user selects
 * the create new folder button.
 *
 * @author Tuna Aksoy
 * @since 2.2
 */
public class CreateNewFolderForm extends SharePage
{
    private final By nameInput = By.cssSelector("input[id$='_default-createFolder_prop_cm_name']");
    private final By titleInput = By.cssSelector("input[id$='_default-createFolder_prop_cm_title']");
    private final By descriptionInput = By.tagName("textarea");
    private final By recordFolderIdentifier = By.cssSelector("input[id$='_default-createFolder_prop_rma_identifier']");
    private RenderElement name = RenderElement.getVisibleRenderElement(nameInput);
    private RenderElement title = RenderElement.getVisibleRenderElement(titleInput);
    private RenderElement description = RenderElement.getVisibleRenderElement(descriptionInput);
    private RenderElement recordFolderId = RenderElement.getVisibleRenderElement(recordFolderIdentifier);

    protected CreateNewFolderForm(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreateNewFolderForm render(RenderTime timer)
    {
        elementRender(timer, name, title, description, recordFolderId);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreateNewFolderForm render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreateNewFolderForm render()
    {
        return render(maxPageLoadingTime);
    }

    /**
     * Enter name value to name input field.
     * @param name String name of new folder
     */
    public void enterName(final String name)
    {
        if(name == null) throw new IllegalArgumentException("Name value required");
        WebElement input = drone.find(nameInput);
        input.clear();
        input.sendKeys(name);
    }

    /**
     * Enter title value to title input field.
     * @param title String new folder title
     */
    public void enterTitle(final String title)
    {
        if(title == null) throw new IllegalArgumentException("Title value required");
        WebElement input = drone.find(titleInput);
        input.clear();
        input.sendKeys(title);
    }

    /**
     * Enter description value to description input field.
     * @param title String new folder description
     */
    public void enterDescription(final String description)
    {
        if(description == null) throw new IllegalArgumentException("Description value required");
        WebElement input = drone.find(descriptionInput);
        input.clear();
        input.sendKeys(description);
    }

    /**
     * Enter record folder id value to record folder id input field.
     * @param title String new record folder id
     */
    public void enterRecordFolderId(final String recordFolderId)
    {
        if(recordFolderId == null) throw new IllegalArgumentException("Record Folder Id value required");
        WebElement input = drone.find(recordFolderIdentifier);
        input.clear();
        input.sendKeys(recordFolderId);
    }

    /**
     * Gets the new record folder id input value.
     * @return String record folder id
     */
    public String getRecordFolderId()
    {
        WebElement input = drone.find(recordFolderIdentifier);
        return input.getAttribute("value");
    }

    /**
     * Action that selects the save button.
     */
    public FilePlanPage selectSave()
    {
        drone.find(By.cssSelector("button[id$='_default-createFolder-form-submit-button']")).click();
        canResume();
        return new FilePlanPage(drone, true);
    }

    /**
     * Action that selects the cancel button.
     */
    public FilePlanPage selectCancel()
    {
        drone.find(By.cssSelector("button[id$='_default-createFolder-form-cancel-button']")).click();
        return new FilePlanPage(drone);
    }
}
