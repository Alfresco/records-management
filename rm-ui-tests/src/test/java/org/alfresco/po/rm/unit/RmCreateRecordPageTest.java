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
package org.alfresco.po.rm.unit;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import org.alfresco.po.rm.common.AbstractRecordsManagementTest;
import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.fileplan.toolbar.CreateNewRecordDialog;
import org.alfresco.po.rm.util.RmPageObjectUtils;
import org.openqa.selenium.By;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests RM create record page
 *
 * @author Polina Lushchinskaya
 * @version 1.1
 * @since 2.2
 */
public class RmCreateRecordPageTest extends AbstractRecordsManagementTest
{
    private static final String TEST_CATEGORY = "Test Category";
    private static final String TEST_FOLDER = "Test Folder";
    private static final String NAME = "Name";
    private static final String TITLE = "Title";
    private static final String DESCRIPTION = "Description";
    private static final By RECORD_NAME = By.xpath("//span//a[contains(text(), '" + NAME + "')]");

    @BeforeClass
    public void navigateToConsole()
    {
        FilePlanPage filePlanPage = rmSiteDashBoard.selectFilePlan().render();
        filePlanPage = filePlanPage.createCategory(TEST_CATEGORY, true).render();
        filePlanPage = filePlanPage.navigateToFolder(TEST_CATEGORY).render();
        filePlanPage = filePlanPage.createFolder(TEST_FOLDER).render();
        filePlanPage.navigateToFolder(TEST_FOLDER);
    }

    @Test
    public void createNonElectronicRecord()
    {
        RmPageObjectUtils.select(drone, FilePlanPage.NEW_FILE_BTN);
        assertTrue(RmPageObjectUtils.isDisplayed(drone, CreateNewRecordDialog.PANEL_CONTAINER));
        assertTrue(RmPageObjectUtils.isDisplayed(drone, CreateNewRecordDialog.ELECTRONIC_BUTTON));
        assertTrue(RmPageObjectUtils.isDisplayed(drone, CreateNewRecordDialog.NON_ELECTRONIC_BUTTON));
        assertTrue(RmPageObjectUtils.isDisplayed(drone, CreateNewRecordDialog.CANCEL_FILE_BUTTON));
        //TODO FIXME move below into page object!
        drone.find(CreateNewRecordDialog.NON_ELECTRONIC_BUTTON).click();
        new CreateNewRecordDialog(drone).render();
    }

    @Test (dependsOnMethods="createNonElectronicRecord")
    public void verifyCreateRecordDialog()
    {
        assertTrue(RmPageObjectUtils.isDisplayed(drone, CreateNewRecordDialog.NAME_INPUT));
        assertTrue(RmPageObjectUtils.isDisplayed(drone, CreateNewRecordDialog.TITLE_INPUT));
        assertTrue(RmPageObjectUtils.isDisplayed(drone, CreateNewRecordDialog.PHYSICAL_SIZE_INPUT));
        assertTrue(RmPageObjectUtils.isDisplayed(drone, CreateNewRecordDialog.NUMBER_OF_COPIES_INPUT));
        assertTrue(RmPageObjectUtils.isDisplayed(drone, CreateNewRecordDialog.STORAGE_LOCATION_INPUT));
        assertTrue(RmPageObjectUtils.isDisplayed(drone, CreateNewRecordDialog.SHELF_INPUT));
        assertTrue(RmPageObjectUtils.isDisplayed(drone, CreateNewRecordDialog.BOX_INPUT));
        assertTrue(RmPageObjectUtils.isDisplayed(drone, CreateNewRecordDialog.FILE_INPUT));
        assertTrue(RmPageObjectUtils.isDisplayed(drone, CreateNewRecordDialog.DESCRIPTION_INPUT));
        assertTrue(RmPageObjectUtils.isDisplayed(drone, CreateNewRecordDialog.CANCEL_BUTTON));
        assertTrue(RmPageObjectUtils.isDisplayed(drone, CreateNewRecordDialog.SAVE_BUTTON));
    }

    @Test (dependsOnMethods="verifyCreateRecordDialog")
    public void cancelCreateRecord()
    {
        CreateNewRecordDialog recordDialog = new CreateNewRecordDialog(drone);
        recordDialog.enterName(NAME);
        recordDialog.enterTitle(TITLE);
        recordDialog.enterDescription(DESCRIPTION);
        FilePlanPage filePlanPage = recordDialog.selectCancel();
        filePlanPage.setInRecordFolder(true);
        assertFalse(RmPageObjectUtils.isDisplayed(drone, RECORD_NAME));
    }

    @Test (dependsOnMethods="cancelCreateRecord")
    public void createSaveRecord()
    {
        FilePlanPage filePlanPage = (FilePlanPage) drone.getCurrentPage();
        CreateNewRecordDialog recordDialog = filePlanPage.selectNewNonElectronicRecord();
        recordDialog.enterName(NAME);
        recordDialog.enterTitle(TITLE);
        recordDialog.enterDescription(DESCRIPTION);

        filePlanPage = recordDialog.selectSave().render();
        assertTrue(RmPageObjectUtils.isDisplayed(drone, RECORD_NAME));
    }
}