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

import static org.alfresco.po.rm.fileplan.toolbar.CreateNewRecordDialog.BOX_INPUT;
import static org.alfresco.po.rm.fileplan.toolbar.CreateNewRecordDialog.CANCEL_BUTTON;
import static org.alfresco.po.rm.fileplan.toolbar.CreateNewRecordDialog.CANCEL_FILE_BUTTON;
import static org.alfresco.po.rm.fileplan.toolbar.CreateNewRecordDialog.DESCRIPTION_INPUT;
import static org.alfresco.po.rm.fileplan.toolbar.CreateNewRecordDialog.ELECTRONIC_BUTTON;
import static org.alfresco.po.rm.fileplan.toolbar.CreateNewRecordDialog.FILE_INPUT;
import static org.alfresco.po.rm.fileplan.toolbar.CreateNewRecordDialog.NAME_INPUT;
import static org.alfresco.po.rm.fileplan.toolbar.CreateNewRecordDialog.NON_ELECTRONIC_BUTTON;
import static org.alfresco.po.rm.fileplan.toolbar.CreateNewRecordDialog.NUMBER_OF_COPIES_INPUT;
import static org.alfresco.po.rm.fileplan.toolbar.CreateNewRecordDialog.PHYSICAL_SIZE_INPUT;
import static org.alfresco.po.rm.fileplan.toolbar.CreateNewRecordDialog.SAVE_BUTTON;
import static org.alfresco.po.rm.fileplan.toolbar.CreateNewRecordDialog.SHELF_INPUT;
import static org.alfresco.po.rm.fileplan.toolbar.CreateNewRecordDialog.STORAGE_LOCATION_INPUT;
import static org.alfresco.po.rm.fileplan.toolbar.CreateNewRecordDialog.TITLE_INPUT;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.fileplan.toolbar.CreateNewRecordDialog;
import org.alfresco.po.rm.functional.AbstractIntegrationTest;
import org.alfresco.po.rm.util.RmPageObjectUtils;
import org.openqa.selenium.By;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Polina Lushchinskaya
 * @version 1.1
 */
public class RmCreateRecordPageTest extends AbstractIntegrationTest {

    @Override
    @BeforeClass(groups={"RM"})
    public void doSetup()
    {
        setup();
        drone.navigateTo(shareUrl + "/page/site/rm/documentlibrary");
        FilePlanPage page = (FilePlanPage) rmSiteDashBoard.selectFilePlan();
        page.createCategory("Test Category", true);
        page.navigateToFolder("Test Category");
        page.createFolder("Test Folder");
        page.navigateToFolder("Test Folder");
    }

    @Test
    public void createNonElectronicRecord(){
        drone.getCurrentPage().render();
        RmPageObjectUtils.select(drone, FilePlanPage.NEW_FILE_BTN);
        assertTrue(isDisplay(drone, By.xpath("//div[contains(@class, 'panel-container')]")));
        assertTrue(isDisplay(drone, ELECTRONIC_BUTTON));
        assertTrue(isDisplay(drone, NON_ELECTRONIC_BUTTON));
        assertTrue(isDisplay(drone, CANCEL_FILE_BUTTON));
        click(NON_ELECTRONIC_BUTTON);
        new CreateNewRecordDialog(drone).render();
    }

    @Test (dependsOnMethods="createNonElectronicRecord")
    public void verifyCreateRecordDialog(){
        assertTrue(isDisplay(drone, NAME_INPUT));
        assertTrue(isDisplay(drone, TITLE_INPUT));
        assertTrue(isDisplay(drone, PHYSICAL_SIZE_INPUT));
        assertTrue(isDisplay(drone, NUMBER_OF_COPIES_INPUT));
        assertTrue(isDisplay(drone, STORAGE_LOCATION_INPUT));
        assertTrue(isDisplay(drone, SHELF_INPUT));
        assertTrue(isDisplay(drone, BOX_INPUT));
        assertTrue(isDisplay(drone, FILE_INPUT));
        assertTrue(isDisplay(drone, DESCRIPTION_INPUT));
        assertTrue(isDisplay(drone, CANCEL_BUTTON));
        assertTrue(isDisplay(drone, SAVE_BUTTON));
    }

    @Test (dependsOnMethods="verifyCreateRecordDialog")
    public void cancelCreateRecord(){
        CreateNewRecordDialog recordDialog = new CreateNewRecordDialog(drone);
        recordDialog.enterName("Name");
        recordDialog.enterTitle("Title");
        recordDialog.enterDescription("Description");
        FilePlanPage filePlanPage = recordDialog.selectCancel();
        filePlanPage.setInRecordFolder(true);
        assertFalse(isDisplay(drone, By.xpath("//span//a[contains(text(), 'Name')]")));
    }

    @Test (dependsOnMethods="cancelCreateRecord")
    public void createSaveRecord(){
        FilePlanPage filePlanPage = drone.getCurrentPage().render();
        CreateNewRecordDialog recordDialog = filePlanPage.selectNewNonElectronicRecord();
        recordDialog.enterName("Name");
        recordDialog.enterTitle("Title");
        recordDialog.enterDescription("Description");

        filePlanPage = ((FilePlanPage) recordDialog.selectSave());
        filePlanPage.setInRecordFolder(true);
        assertTrue(isDisplay(drone, By.xpath("//span//a[contains(text(), 'Name')]")));
    }

}
