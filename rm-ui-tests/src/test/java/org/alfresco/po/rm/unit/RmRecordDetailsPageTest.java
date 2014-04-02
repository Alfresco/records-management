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
import org.alfresco.po.rm.functional.RmAbstractTest;
import org.openqa.selenium.By;
import org.testng.Assert;
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

    @Override
    @BeforeClass(groups={"RM"})
    public void doSetup()
    {
        setup();
        FilePlanPage filePlanPage = (FilePlanPage) rmSiteDashBoard.selectFilePlan();
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
        assertTrue(isElementPresent(RecordDetailsPage.RM_ADD_META_DATA_LINK));
        assertTrue(isElementPresent(RecordDetailsPage.RM_EDIT_META_DATA_LINK));
        assertTrue(isElementPresent(RecordDetailsPage.RM_COPY_TO_LINK));
        assertTrue(isElementPresent(RecordDetailsPage.RM_MOVE_TO_LINK));
        assertTrue(isElementPresent(RecordDetailsPage.RM_LINK_TO_LINK));
        assertTrue(isElementPresent(RecordDetailsPage.RM_DELETE_LINK));
        assertTrue(isElementPresent(RecordDetailsPage.RM_VIEW_AUDIT_LOG_LINK));
        assertTrue(isElementPresent(RecordDetailsPage.RM_FREEZE_LINK));

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
    }

    @Test (dependsOnMethods = "openEditMetadataPage")
    public void freezeRecordAction()
    {
        RecordDetailsPage detailsPage = new RecordDetailsPage(drone);
        click(RM_FREEZE_LINK);
        assertTrue(isElementPresent(FREEZE_REASON_WINDOW));
        assertTrue(isElementPresent(FREEZE_REASON_INPUT));
        freezeRecord("Freeze reason");
        assertTrue(isElementPresent(RM_UNFREEZE_LINK));
    }

    @Test (dependsOnMethods = "freezeRecordAction")
    public void unfreezeRecord()
    {
        RecordDetailsPage detailsPage = new RecordDetailsPage(drone);
        click(RM_UNFREEZE_LINK);
        drone.isRenderComplete(MAX_WAIT_TIME);
        assertTrue(isElementPresent(RecordDetailsPage.RM_ADD_META_DATA_LINK));
        assertTrue(isElementPresent(RecordDetailsPage.RM_EDIT_META_DATA_LINK));
        assertTrue(isElementPresent(RecordDetailsPage.RM_COPY_TO_LINK));
        assertTrue(isElementPresent(RecordDetailsPage.RM_MOVE_TO_LINK));
        assertTrue(isElementPresent(RecordDetailsPage.RM_LINK_TO_LINK));
        assertTrue(isElementPresent(RecordDetailsPage.RM_DELETE_LINK));
        assertTrue(isElementPresent(RecordDetailsPage.RM_VIEW_AUDIT_LOG_LINK));
        assertTrue(isElementPresent(RecordDetailsPage.RM_FREEZE_LINK));

    }
}
