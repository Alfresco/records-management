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
package org.alfresco.rm.po;


import org.alfresco.po.rm.RmSiteDashBoardPage;
import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.rm.common.AbstractRecordsManagementTest;
import org.testng.Assert;
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
public class FilePlanPageTest extends AbstractRecordsManagementTest
{
    /** Generate names */
    private String categoryName = generateNameFromClass();
    private String folderName = generateNameFromClass();
    private String recordName = generateNameFromClass();
    
    
    /**
     * Test the creation and navigation of the file plan hierarchy via the page object
     */
    @Test
    public void createAndNavigateFilePlanHierarchy()
    {
        RmSiteDashBoardPage rmSiteDashBoard = drone.getCurrentPage().render();
        FilePlanPage filePlan = rmSiteDashBoard.selectFilePlan().render();
        Assert.assertTrue(filePlan.isCreateNewCategoryDisplayed());
        Assert.assertTrue(filePlan.isCreateNewFolderDisplayed());

        // create root category
        filePlan.createCategory(categoryName, true);
        Assert.assertTrue(filePlan.isCategoryCreated(categoryName));
        filePlan.navigateToFolder(categoryName);

        // create record folder
        filePlan.createFolder(folderName);
        filePlan.navigateToFolder(folderName);
        Assert.assertTrue(filePlan.isCreateNewFileDisplayed());

        // file record
        // FIXME this creates a non-electronic record .. rename method and allow values to be sent
        filePlan.createRecord(recordName);
    }
}