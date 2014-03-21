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
package org.alfresco.po.rm.unit;

import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.functional.AbstractIntegrationTest;
import org.alfresco.po.share.util.FailedTestListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertTrue;

/**
 * Tests file plan page.
 *
 * @author Michael Suzuki
 * @version 1.7.1
 */
@Listeners(FailedTestListener.class)
public class FilePlanPageTest extends AbstractIntegrationTest
{
    @Test
    public void openPage()
    {
        drone.navigateTo(shareUrl + "/page/site/rm/documentlibrary");
        FilePlanPage page = (FilePlanPage) rmSiteDashBoard.selectFilePlan();
    }

    @Test (dependsOnMethods="openPage")
    public void verifyFilePlanPage(){
        FilePlanPage filePlan = drone.getCurrentPage().render();
        assertTrue(filePlan.isCreateNewCategoryDisplayed());
        assertTrue(filePlan.isCreateNewCategoryDisplayed());
        assertTrue(filePlan.isCreateNewFolderDisplayed());
    }

    @Test (dependsOnMethods="verifyFilePlanPage")
    public void createRootCategory(){
        FilePlanPage filePlan = drone.getCurrentPage().render();
        filePlan.createCategory("New Category", true);
        assertTrue(filePlan.isCategoryCreated("New Category"));
    }

    @Test (dependsOnMethods="createRootCategory")
    public void navigateToCategory(){
        FilePlanPage filePlan = drone.getCurrentPage().render();
        filePlan.navigateToFolder("New Category");
    }

    @Test (dependsOnMethods="navigateToCategory")
    public void createFolder(){
        FilePlanPage filePlan = drone.getCurrentPage().render();
        filePlan.createFolder("New FolderName");
        filePlan.navigateToFolder("New FolderName");
        assertTrue(filePlan.isCreateNewFileDisplayed());
    }

    @Test (dependsOnMethods="createFolder")
    public void createRecord(){
        FilePlanPage filePlan = drone.getCurrentPage().render();
        filePlan.createRecord("New RecordName");
    }
}