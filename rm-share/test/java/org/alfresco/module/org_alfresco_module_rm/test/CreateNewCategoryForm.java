/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_rm.test;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Create new category popup page form.
 * The form that is displayed to user when creating
 * a new category.
 *
 * This is only available to Records management module and
 * is accessed via the file plan page when a user selects
 * the create new category button.
 *
 * @author Michael Suzuki
 * @version 1.7.1
 */
public class CreateNewCategoryForm extends SharePage
{
    private final By nameInput = By.cssSelector("input[id$='_default-createFolder_prop_cm_name']");
    private final By titleInput = By.cssSelector("input[id$='_default-createFolder_prop_cm_title']");
    private final By descriptionInput = By.tagName("textarea");
    private final By recordCategoryIdentifier = By.cssSelector("input[id$='_default-createFolder_prop_rma_identifier']");
    private RenderElement name = RenderElement.getVisibleRenderElement(nameInput);
    private RenderElement title = RenderElement.getVisibleRenderElement(titleInput);
    private RenderElement description = RenderElement.getVisibleRenderElement(descriptionInput);
    private RenderElement recordCategoryId = RenderElement.getVisibleRenderElement(recordCategoryIdentifier);

    protected CreateNewCategoryForm(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreateNewCategoryForm render(RenderTime timer)
    {
        elementRender(timer, name, title, description, recordCategoryId);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreateNewCategoryForm render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreateNewCategoryForm render()
    {
        return render(maxPageLoadingTime);
    }

    /**
     * Enter name value to name input field.
     * @param name String name of new category
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
     * @param title String new category title
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
     * @param title String new category description
     */
    public void enterDescription(final String description)
    {
        if(description == null) throw new IllegalArgumentException("Description value required");
        WebElement input = drone.find(descriptionInput);
        input.clear();
        input.sendKeys(description);
    }

    /**
     * Enter record category id value to record category id input field.
     * @param title String new record category id
     */
    public void enterRecordCategoryId(final String recordCategoryId)
    {
        if(recordCategoryId == null) throw new IllegalArgumentException("Record Category Id value required");
        WebElement input = drone.find(recordCategoryIdentifier);
        input.clear();
        input.sendKeys(recordCategoryId);
    }
    /**
     * Gets the new record category id input value.
     * @return String record category id
     */
    public String getRecordCategoryId()
    {
        WebElement input = drone.find(recordCategoryIdentifier);
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
