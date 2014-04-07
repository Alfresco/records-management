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

import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.fileplan.RecordDetailsPage;
import org.alfresco.po.rm.fileplan.filter.FilePlanFilter;
import org.alfresco.po.rm.fileplan.filter.hold.HoldsContainer;
import org.alfresco.po.rm.regression.managerules.v1.RmAbstractTest;
import org.openqa.selenium.By;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.alfresco.po.rm.fileplan.RecordDetailsPage.*;
import static org.testng.Assert.assertTrue;

/**
 * @author Polina Lushchinskaya
 * @version 1.1
 */
public class RmRecordDetailsPageTest extends RmAbstractTest
{
    private static final String TEST_CATEGORY = "Test Category";
    private static final String TEST_FOLDER = "Test Folder";
    private static final String NAME = "Name";
    private static final String HOLD_NAME = "Hold Name";

    @Override
    @BeforeClass(groups={"RM"})
    public void doSetup()
    {
        setup();
        FilePlanPage filePlanPage = (FilePlanPage) rmSiteDashBoard.selectFilePlan();
        FilePlanFilter filePlanFilter = filePlanPage.getFilePlanFilter();
        filePlanFilter.selectHoldsContainer().render();
        HoldsContainer.createNewHold(drone, HOLD_NAME, HOLD_NAME);
        filePlanPage = (FilePlanPage) rmSiteDashBoard.selectFilePlan();
        filePlanPage.createCategory(TEST_CATEGORY, true);
        filePlanPage.navigateToFolder(TEST_CATEGORY);
        filePlanPage.createFolder(TEST_FOLDER);
        filePlanPage.navigateToFolder(TEST_FOLDER);
        filePlanPage.createRecord(NAME);
    }

    @Test
    public void openRecordDetailsPage()
    {
        FilePlanPage filePlan = new FilePlanPage(drone);
        filePlan.openRecordDetailsPage(NAME);
        assertTrue(isElementPresent(RecordDetailsPage.RM_EDIT_META_DATA_LINK));
        assertTrue(isElementPresent(RecordDetailsPage.RM_COPY_TO_LINK));
        assertTrue(isElementPresent(RecordDetailsPage.RM_MOVE_TO_LINK));
        assertTrue(isElementPresent(RecordDetailsPage.RM_LINK_TO_LINK));
        assertTrue(isElementPresent(RecordDetailsPage.RM_DELETE_LINK));
        assertTrue(isElementPresent(RecordDetailsPage.RM_VIEW_AUDIT_LOG_LINK));
        assertTrue(isElementPresent(RecordDetailsPage.RM_ADD_TO_HOLD_LINK));

        assertTrue(isElementPresent(RecordDetailsPage.RM_REQUEST_INFORMATION_LINK));
        assertTrue(isElementPresent(RecordDetailsPage.PROPERTY_SET_HEADER));
        assertTrue(isElementPresent(RecordDetailsPage.PROPERTIES));
        assertTrue(isElementPresent(RecordDetailsPage.ACTIONS));
        assertTrue(isElementPresent(RecordDetailsPage.REFERENCES));
        assertTrue(isElementPresent(RecordDetailsPage.EVENTS));

    }

    @Test (dependsOnMethods = "openRecordDetailsPage")
    public void openEditMetadataPage()
    {
        RecordDetailsPage detailsPage = new RecordDetailsPage(drone);
        detailsPage.openEditMetadataPage();
        assertTrue(isElementPresent(EDIT_METADATA_FORM),
            "Failed to Present Edit Metadata form");
        assertTrue(isElementPresent(EDIT_METADATA_NAME_INPUT),
            "Failed to present name input");
        assertTrue(isElementPresent(EDIT_METADATA_TITLE_INPUT),
            "Failed to present title input");
        assertTrue(isElementPresent(EDIT_METADATA_DESCRIPTION_INPUT),
            "Failed to present description input");
        assertTrue(isElementPresent(EDIT_PHYSICAL_SIZE_INPUT),
            "Failed to present physical size input");
        assertTrue(isElementPresent(EDIT_NUMBER_OF_COPIES_INPUT),
            "Failed to present number of copies input");
        assertTrue(isElementPresent(EDIT_METADATA_STORAGE_LOCATION),
            "Failed to present storage location input");
        assertTrue(isElementPresent(EDIT_METADATA_SHELF_INPUT),
            "Failed to present shelf input");
        assertTrue(isElementPresent(EDIT_METADATA_BOX_INPUT),
            "Failed to present box input");
        assertTrue(isElementPresent(EDIT_METADATA_FILE_INPUT),
            "Failed to present file input");
        assertTrue(isElementPresent(SAVE_BUTTON),
            "Failed to present Save button");
        assertTrue(isElementPresent(CANCEL_BUTTON),
            "Failed to present Cancel button");
        click(CANCEL_BUTTON);
    }

    @Test (dependsOnMethods = "openEditMetadataPage")
    public void holdRecordAction()
    {
        new RecordDetailsPage(drone);
        click(RM_ADD_TO_HOLD_LINK);
        assertTrue(isElementPresent(ADD_TO_HOLD_DIALOG));
        click(By.xpath(" //div[text()='" + HOLD_NAME + "']//ancestor::tr//input[contains(@class, 'checkbox')]"));
        assertTrue(isElementPresent(ADD_TO_HOLD_OK_BUTTON));
        click(RecordDetailsPage.ADD_TO_HOLD_OK_BUTTON);
        drone.isRenderComplete(MAX_WAIT_TIME);
        assertTrue(isElementPresent(RM_REMOVE_FROM_HOLD_LINK));
    }

    @Test (dependsOnMethods = "holdRecordAction")
    public void unfreezeRecord()
    {
        new RecordDetailsPage(drone);
        click(RM_REMOVE_FROM_HOLD_LINK);
        click(By.xpath(" //div[text()='" + HOLD_NAME + "']//ancestor::tr//input[contains(@class, 'checkbox')]"));
        assertTrue(isElementPresent(REMOVE_FROM_HOLD_OK_BUTTON));
        click(RecordDetailsPage.REMOVE_FROM_HOLD_OK_BUTTON);
        drone.isRenderComplete(MAX_WAIT_TIME);
        assertTrue(isElementPresent(RecordDetailsPage.RM_EDIT_META_DATA_LINK));
        assertTrue(isElementPresent(RecordDetailsPage.RM_COPY_TO_LINK));
        assertTrue(isElementPresent(RecordDetailsPage.RM_MOVE_TO_LINK));
        assertTrue(isElementPresent(RecordDetailsPage.RM_LINK_TO_LINK));
        assertTrue(isElementPresent(RecordDetailsPage.RM_DELETE_LINK));
        assertTrue(isElementPresent(RecordDetailsPage.RM_VIEW_AUDIT_LOG_LINK));
        assertTrue(isElementPresent(RecordDetailsPage.RM_ADD_TO_HOLD_LINK));

    }
}
