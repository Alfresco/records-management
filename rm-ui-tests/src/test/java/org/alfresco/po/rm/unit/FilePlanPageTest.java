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

import static org.testng.AssertJUnit.assertTrue;

import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.functional.AbstractIntegrationTest;
import org.alfresco.po.share.util.FailedTestListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Tests file plan page.
 *
 * @author Michael Suzuki
 * @version 1.7.1
 * @since 2.2
 */
@Listeners(FailedTestListener.class)
public class FilePlanPageTest extends AbstractIntegrationTest
{
    /** Constants */
    private static final String NEW_CATEGORY = "New Category";
    private static final String NEW_FOLDER_NAME = "New FolderName";
    private static final String NEW_RECORD_NAME = "New RecordName";

    @Test
    public void selectFilePlan()
    {
        rmSiteDashBoard.selectFilePlan();
    }

    @Test (dependsOnMethods="selectFilePlan")
    public void verifyFilePlanPage()
    {
        FilePlanPage filePlan = drone.getCurrentPage().render();
        assertTrue(filePlan.isCreateNewCategoryDisplayed());
        assertTrue(filePlan.isCreateNewFolderDisplayed());
    }

    @Test (dependsOnMethods="verifyFilePlanPage")
    public void createRootCategory()
    {
        FilePlanPage filePlan = drone.getCurrentPage().render();
        filePlan.createCategory(NEW_CATEGORY, true);
        assertTrue(filePlan.isCategoryCreated(NEW_CATEGORY));
    }

    @Test (dependsOnMethods="createRootCategory")
    public void navigateToCategory()
    {
        FilePlanPage filePlan = drone.getCurrentPage().render();
        filePlan.navigateToFolder(NEW_CATEGORY);
    }

    @Test (dependsOnMethods="navigateToCategory")
    public void createFolder()
    {
        FilePlanPage filePlan = drone.getCurrentPage().render();
        filePlan.createFolder(NEW_FOLDER_NAME);
        filePlan.navigateToFolder(NEW_FOLDER_NAME);
        assertTrue(filePlan.isCreateNewFileDisplayed());
    }

    @Test (dependsOnMethods="createFolder")
    public void createRecord()
    {
        FilePlanPage filePlan = drone.getCurrentPage().render();
        filePlan.createRecord(NEW_RECORD_NAME);
    }
}