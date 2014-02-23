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

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Base class for the dialogs
 *
 * @author Tuna Aksoy
 * @since 2.2
 */
public class BaseDialog extends SharePage
{
    private static final By NAME_INPUT = By.cssSelector("input[id$='_default-createFolder_prop_cm_name']");
    private static final By TITLE_INPUT = By.cssSelector("input[id$='_default-createFolder_prop_cm_title']");
    private static final By IDENTIFIER = By.cssSelector("input[id$='_default-createFolder_prop_rma_identifier']");
    private static final By DESCRIPTION_INPUT = By.tagName("textarea");
    private static final By SAVE = By.cssSelector("button[id$='_default-createFolder-form-submit-button']");
    private static final By CANCEL = By.cssSelector("button[id$='_default-createFolder-form-cancel-button']");
    private static final String VALUE = "value";

    /**
     * Constructor.
     *
     * @param drone {@link WebDrone}
     */
    protected BaseDialog(WebDrone drone)
    {
        super(drone);
    }

    /**
     * @see org.alfresco.webdrone.Render#render(org.alfresco.webdrone.RenderTime)
     */
    @SuppressWarnings("unchecked")
    @Override
    public BaseDialog render(RenderTime timer)
    {
        WebDroneUtil.checkMandotaryParam("timer", timer);

        RenderElement name = RenderElement.getVisibleRenderElement(NAME_INPUT);
        RenderElement element = RenderElement.getVisibleRenderElement(TITLE_INPUT);
        RenderElement description = RenderElement.getVisibleRenderElement(DESCRIPTION_INPUT);
        RenderElement id = RenderElement.getVisibleRenderElement(IDENTIFIER);

        elementRender(timer, name, element, description, id);

        return this;
    }

    /**
     * @see org.alfresco.webdrone.Render#render(long)
     */
    @SuppressWarnings("unchecked")
    @Override
    public BaseDialog render(long time)
    {
        WebDroneUtil.checkMandotaryParam("time", time);

        RenderTime timer = new RenderTime(time);
        return render(timer);
    }

    /**
     * @see org.alfresco.webdrone.Render#render()
     */
    @SuppressWarnings("unchecked")
    @Override
    public BaseDialog render()
    {
        return render(maxPageLoadingTime);
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
     * Enter id value to record category/folder id input field.
     *
     * @param id {@link String} New record category/folder id
     */
    public void enterId(final String id)
    {
        WebDroneUtil.checkMandotaryParam("id", id);

        WebElement identifier = drone.findAndWait(IDENTIFIER);
        identifier.clear();
        identifier.sendKeys(id);
    }

    /**
     * Gets the new record category/folder id input value.
     *
     * @return {@link String} Record category/folder id
     */
    public String getId()
    {
        WebElement identifier = drone.findAndWait(IDENTIFIER);
        return identifier.getAttribute(VALUE);
    }

    /**
     * Action that selects the save button.
     *
     * @return {@link HtmlPage} Returns the current page
     */
    public HtmlPage selectSave()
    {
        WebElement save = drone.findAndWait(SAVE);
        save.click();
        canResume();
        return drone.getCurrentPage();
    }

    /**
     * Action that selects the cancel button.
     *
     * @return {@link HtmlPage} Returns the current page
     */
    public HtmlPage selectCancel()
    {
        WebElement cancel = drone.findAndWait(CANCEL);
        cancel.click();
        return drone.getCurrentPage();
    }
}