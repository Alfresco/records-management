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
package org.alfresco.rm.functional;

import java.io.IOException;

import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.fileplan.RecordInfo;
import org.alfresco.po.rm.fileplan.filter.FilePlanFilter;
import org.alfresco.po.rm.fileplan.filter.hold.HoldDialog;
import org.alfresco.po.rm.fileplan.filter.hold.HoldsContainer;
import org.alfresco.po.rm.fileplan.toolbar.CreateNewHoldDialog;
import org.alfresco.po.rm.util.RmPageObjectUtils;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.FileDirectoryInfoImpl;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.rm.common.AbstractIntegrationTest;
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
 *  <li>Adding a record to the hold</li>
 *  <li>Removing a record from the hold</li>
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
    private static final String HOLD_NAME = "New Hold";
    private static final String HOLD_REASON = "Reason for hold";
    private static final String NEW_HOLD_NAME = "Edited Name";
    private static final String NEW_HOLD_DESCRIPTION = "My new description...";
    private static final String NEW_HOLD_REASON = "My new reason...";

    /** Constants for the file plan items */
    private static final String CATEGORY_NAME = "Category Name";
    private static final String FOLDER_NAME = "Folder Name";

    /** Selectors */
    private static final String ACTION_SELECTOR_TEXT_MANAGE_PERMISSONS = "div.rm-manage-permissions>a";
    private static final String ACTION_SELECTOR_TEXT_EDIT_DETAILS = "div.rm-edit-details>a";
    private static final String ACTION_SELECTOR_TEXT_DELETE = "div.rm-delete>a";
    private static final String ACTIONS = "td:nth-of-type(5)";
    private static final By MANAGE_PERMISSIONS_BUTTON = By.cssSelector("button[id$='default-holdPermissions-button-button']");
    private static final By ADD_USER_GROUP_BUTTON = By.cssSelector("button[id$='-addusergroup-button-button']");
    private static final By FINISH_BUTTON = By.cssSelector("button[id$='-finish-button-button']");
    private static final By SUBMIT_BUTTON = By.cssSelector("button[id$='form-submit-button']");
    private static final By PROMPT = By.cssSelector("div#prompt div.ft span span button");
    private static final By INPUT_NAME_SELECTOR = By.name("prop_cm_name");
    private static final By INPUT_DESCRIPTION_SELECTOR = By.name("prop_cm_description");
    private static final By INPUT_REASON_SELECTOR = By.name("prop_rma_holdReason");

    @Override
    public void setup()
    {
        super.setup();
        goToHoldsContainer(null);
    }

    /**
     * Helper method which renders the hold with the given name in the holds container
     *
     * @param holdName {@link String} The name of the hold
     * @return Gives back the hold object with the given name
     */
    private HoldsContainer goToHoldsContainer(String holdName)
    {
        FilePlanPage filePlan = FilePlanPage.getFilePlanRoot(rmSiteDashBoard);
        FilePlanFilter filePlanFilter = filePlan.getFilePlanFilter();
        return filePlanFilter.selectHoldsContainer().render(holdName);
    }

    /**
     * Helper method to click on an action for a given hold
     *
     * @param selector {@link String} The selector text for the action to select
     */
    private void clickAction(HoldsContainer holdsContainer, String selector, String name)
    {
        FileDirectoryInfo hold = holdsContainer.getFileDirectoryInfo(name);
        WebElement actions = ((FileDirectoryInfoImpl) hold).findElement(By.cssSelector(ACTIONS));
        drone.mouseOverOnElement(actions);
        RmPageObjectUtils.select(drone, By.cssSelector(selector));
    }

    @Test
    public void createNewHold()
    {
        HoldsContainer holdsContainer = new HoldsContainer(drone).render();
        CreateNewHoldDialog newHoldDialog = holdsContainer.selectCreateNewHold().render();
        newHoldDialog.enterName(HOLD_NAME);
        newHoldDialog.enterReason(HOLD_REASON);
        holdsContainer = ((HoldsContainer) newHoldDialog.selectSave()).render(HOLD_NAME);
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
        HoldsContainer holdsContainer = new HoldsContainer(drone).render(HOLD_NAME);
        clickAction(holdsContainer, ACTION_SELECTOR_TEXT_MANAGE_PERMISSONS, HOLD_NAME);
        RmPageObjectUtils.select(drone, ADD_USER_GROUP_BUTTON);
        RmPageObjectUtils.select(drone, FINISH_BUTTON);
    }

    @Test(dependsOnMethods="managePermissions")
    public void editDetails()
    {
        HoldsContainer holdsContainer = new HoldsContainer(drone).render(HOLD_NAME);
        clickAction(holdsContainer, ACTION_SELECTOR_TEXT_EDIT_DETAILS, HOLD_NAME);

        drone.waitForElement(INPUT_NAME_SELECTOR, 5);
        WebElement title = drone.find(INPUT_NAME_SELECTOR);
        title.clear();
        title.sendKeys(NEW_HOLD_NAME);

        WebElement description = drone.find(INPUT_DESCRIPTION_SELECTOR);
        description.clear();
        description.sendKeys(NEW_HOLD_DESCRIPTION);

        WebElement reason = drone.find(INPUT_REASON_SELECTOR);
        reason.clear();
        reason.sendKeys(NEW_HOLD_REASON);

        RmPageObjectUtils.select(drone, SUBMIT_BUTTON);
    }

    @Test(dependsOnMethods="editDetails")
    public void addToHoldRemoveFromHold() throws IOException
    {
        HoldsContainer holdsContainer = drone.getCurrentPage().render();
        FilePlanPage filePlan = holdsContainer.selectFilePlanNavigation();
        filePlan.createCategory(CATEGORY_NAME, true);
        filePlan.render(CATEGORY_NAME);
        filePlan.selectFolder(CATEGORY_NAME);
        filePlan.createFolder(FOLDER_NAME);
        filePlan.render(FOLDER_NAME);
        filePlan.selectFolder(FOLDER_NAME);
        String recordName = Long.valueOf(System.currentTimeMillis()).toString();
        fileElectronicRecordToFilePlan(drone, filePlan.selectFile().render(), recordName);
        RecordInfo record = filePlan.getRecordInfo(recordName);
        HoldDialog dialog = record.clickAddToHold();
        dialog.selectCheckBox(NEW_HOLD_NAME);
        dialog.clickOK();
        filePlan.render(recordName);
        dialog = record.clickRemoveFromHold();
        dialog.selectCheckBox(NEW_HOLD_NAME);
        dialog.clickOK();
    }

    @Test(dependsOnMethods="addToHoldRemoveFromHold")
    public void deleteHold()
    {
        HoldsContainer holdsContainer = goToHoldsContainer(NEW_HOLD_NAME);
        clickAction(holdsContainer, ACTION_SELECTOR_TEXT_DELETE, NEW_HOLD_NAME);
        RmPageObjectUtils.select(drone, PROMPT);
    }
}