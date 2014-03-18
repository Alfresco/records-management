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
package org.alfresco.po.rm.functional;

import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.fileplan.filter.FilePlanFilter;
import org.alfresco.po.rm.fileplan.filter.hold.HoldsContainer;
import org.alfresco.po.rm.fileplan.toolbar.CreateNewHoldDialog;
import org.alfresco.po.rm.util.RmPageObjectUtils;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.util.FailedTestListener;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * This test suite tests the following new features:
 * <p>
 * <ul>
 *  <li>Creating a new hold in the holds container</li>
 *  <li>Viewing the manage permissions page for the for hold container</li>
 *  <li>Viewing the manage permissions page for the newly created hold</li>
 *  <li>Viewing the details page of the hold container</li>
 *  <li>Deleting the new hold in the holds container</li>
 * </ul>
 * <p>
 * @author Tuna Aksoy
 * @version 2.2
 */
@Listeners(FailedTestListener.class)
public class HoldContainerIntTest extends AbstractIntegrationTest
{
    /** Constants for the new hold dialog */
    private static final String NAME = "New Hold";
    private static final String REASON = "Reason for hold";
    private static final String EDITED_NAME = "Edited Name";

    /** Selectors */
    private static final String ACTION_SELECTOR_TEXT_MANAGE_PERMISSONS = "div.rm-manage-permissions>a";
    private static final String ACTION_SELECTOR_TEXT_EDIT_DETAILS = "div.rm-edit-details>a";
    private static final String ACTION_SELECTOR_TEXT_DELETE = "div.rm-delete>a";
    private static final String ACTIONS = "td:nth-of-type(5)";
    private static final By MANAGE_PERMISSIONS_BUTTON = By.cssSelector("button[id$='default-holdPermissions-button-button']");
    private static final By ADD_USER_GROUP_BUTTON = By.cssSelector("button[id$='-addusergroup-button-button']");
    private static final By FINISH_BUTTON = By.cssSelector("button[id$='-finish-button-button']");
    private static final By PROMPT = By.cssSelector("div#prompt div.ft span span button");
    private static final By INPUT_NAME_SELECTOR = By.name("prop_cm_name");
    private static final By INPUT_DESCRIPTION_SELECTOR = By.name("prop_cm_description");
    private static final By INPUT_REASON_SELECTOR = By.name("prop_rma_holdReason");
    
    @Override
    protected boolean isRMSiteDeletedOnTearDown()
    {
        return false;
    }

    @Override
    protected void setup()
    {
        super.setup();
        
        // show the hold container
        FilePlanPage filePlan = FilePlanPage.getFilePlanRoot(rmSiteDashBoard);
        FilePlanFilter filePlanFilter = filePlan.getFilePlanFilter();
        filePlanFilter.selectHoldsContainer().render();
    }

    /**
     * Helper method to click on an action for a given hold
     *
     * @param selector {@link String} The selector text for the action to select
     */
    private void clickAction(HoldsContainer holdsContainer, String selector, String name)
    {
        FileDirectoryInfo hold = holdsContainer.getFileDirectoryInfo(name);
        WebElement actions = hold.findElement(By.cssSelector(ACTIONS));
        drone.mouseOverOnElement(actions);
        hold.findElement(By.cssSelector(selector)).click();
    }

    @Test
    public void createNewHold()
    {
        HoldsContainer holdsContainer = new HoldsContainer(drone).render();
        CreateNewHoldDialog newHoldDialog = holdsContainer.selectCreateNewHold().render();
        newHoldDialog.enterName(NAME);
        newHoldDialog.enterReason(REASON);
        holdsContainer = ((HoldsContainer) newHoldDialog.selectSave()).render(NAME);
    }

    @Test(dependsOnMethods="createNewHold")
    public void managePermissionsForRoot()
    {
        RmPageObjectUtils.select(drone, MANAGE_PERMISSIONS_BUTTON);
        RmPageObjectUtils.select(drone, ADD_USER_GROUP_BUTTON);
        RmPageObjectUtils.select(drone, FINISH_BUTTON);
    }

    @Test(dependsOnMethods="managePermissionsForRoot")
    public void managePermissions()
    {
        HoldsContainer holdsContainer = new HoldsContainer(drone).render(NAME);
        clickAction(holdsContainer, ACTION_SELECTOR_TEXT_MANAGE_PERMISSONS, NAME);
        RmPageObjectUtils.select(drone, ADD_USER_GROUP_BUTTON);
        RmPageObjectUtils.select(drone, FINISH_BUTTON);
    }

    @Test(dependsOnMethods="managePermissions")
    public void editDetails()
    {
        HoldsContainer holdsContainer = new HoldsContainer(drone).render(NAME);
        clickAction(holdsContainer, ACTION_SELECTOR_TEXT_EDIT_DETAILS, NAME);

        drone.waitForElement(INPUT_NAME_SELECTOR, 5);
        WebElement title = drone.find(INPUT_NAME_SELECTOR);
        title.clear();
        title.sendKeys(EDITED_NAME);

        WebElement description = drone.find(INPUT_DESCRIPTION_SELECTOR);
        description.clear();
        description.sendKeys("My new description...");

        WebElement reason = drone.find(INPUT_REASON_SELECTOR);
        reason.clear();
        reason.sendKeys("My new reason...");

        WebElement saveButton = drone.find(By.cssSelector("button[id$='form-submit-button']"));
        saveButton.click();
    }

    @Test(dependsOnMethods="editDetails")
    public void deleteHold()
    {
        HoldsContainer holdsContainer = new HoldsContainer(drone).render(EDITED_NAME);
        clickAction(holdsContainer, ACTION_SELECTOR_TEXT_DELETE, EDITED_NAME);
        RmPageObjectUtils.select(drone, PROMPT);
    }
}