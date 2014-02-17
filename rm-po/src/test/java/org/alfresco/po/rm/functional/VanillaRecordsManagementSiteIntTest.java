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
package org.alfresco.po.rm.functional;

import org.alfresco.po.rm.CreateNewCategoryForm;
import org.alfresco.po.rm.CreateNewFolderForm;
import org.alfresco.po.rm.FilePlanPage;
import org.alfresco.po.rm.RmUploadFilePage;
import org.alfresco.po.rm.RmCreateSitePage.RMSiteCompliance;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.util.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Vanilla records management site integration test.
 *
 * @author Roy Wetherall
 * @since 2.2
 */
@Listeners(FailedTestListener.class)
public class VanillaRecordsManagementSiteIntTest extends AbstractIntegrationTest
{
    private static final String TITLE = "title";
    private static final String NAME = "name";
    private static final String DESC = "description";

    @Override
    public void setup()
    {
        // log into Share
        login(username, password);
    }

    @Test
    public void testDODSite() throws Exception
    {
        // create DOD site
        createRMSite(RMSiteCompliance.DOD5015);

        FilePlanPage filePlan = dashBoard.getRMNavigation().selectFilePlan().render();
        Assert.assertNotNull(filePlan);

        Assert.assertTrue(filePlan.isCreateNewCategoryDisplayed());
        CreateNewCategoryForm createNewCategory = filePlan.selectCreateNewCategory().render();
        Assert.assertNotNull(createNewCategory);

        createNewCategory.enterName(NAME);
        createNewCategory.enterTitle(TITLE);
        createNewCategory.enterDescription(DESC);

        filePlan.setExpectingRecordOrFolder(true);
        filePlan.setExpectedRecordOrFolderName(NAME);
        filePlan = createNewCategory.selectSave().render();
        Assert.assertNotNull(filePlan);

        FileDirectoryInfo recordCategory = filePlan.getFileDirectoryInfo(NAME);
        Assert.assertNotNull(recordCategory);

        recordCategory.clickOnTitle();
        filePlan.setInRecordCategory(true);
        filePlan = filePlan.render();
        Assert.assertNotNull(filePlan);

        // create record folder
        Assert.assertTrue(filePlan.isCreateNewFolderDisplayed());
        CreateNewFolderForm createNewFolder = filePlan.selectCreateNewFolder().render();
        Assert.assertNotNull(createNewFolder);

        createNewFolder.enterName(NAME);
        createNewFolder.enterTitle(TITLE);
        createNewFolder.enterDescription(DESC);

        filePlan.setExpectingRecordOrFolder(true);
        filePlan.setExpectedRecordOrFolderName(NAME);
        filePlan = createNewFolder.selectSave().render();
        Assert.assertNotNull(filePlan);

        FileDirectoryInfo recordFolder = filePlan.getFileDirectoryInfo(NAME);
        Assert.assertNotNull(recordFolder);

        recordFolder.clickOnTitle();
        filePlan.setInRecordFolder(true);
        filePlan = filePlan.render();
        Assert.assertNotNull(filePlan);

        // file a record
        Assert.assertTrue(filePlan.isUnfiledRecordsContainerFileDisplayed());
        RmUploadFilePage rmRecordFileDialog = filePlan.selectCreateNewUnfiledRecordsContainerFile().render();
        Assert.assertNotNull(rmRecordFileDialog);

        String fileName = Long.valueOf(System.currentTimeMillis()).toString();
        fileElectronicRecord(drone, rmRecordFileDialog, fileName);
        filePlan = filePlan.render();
        Assert.assertNotNull(filePlan);

        // FIXME: view record details

        // delete DOD site
        deleteRMSite();
    }

    @Test
    public void testVanillaSite() throws Exception
    {
        // create vanilla site
        createRMSite();

        FilePlanPage filePlan = dashBoard.getRMNavigation().selectFilePlan().render();
        Assert.assertNotNull(filePlan);

        Assert.assertTrue(filePlan.isCreateNewCategoryDisplayed());
        CreateNewCategoryForm createNewCategory = filePlan.selectCreateNewCategory().render();
        Assert.assertNotNull(createNewCategory);

        createNewCategory.enterName(NAME);
        createNewCategory.enterTitle(TITLE);
        createNewCategory.enterDescription(DESC);

        filePlan.setExpectingRecordOrFolder(true);
        filePlan.setExpectedRecordOrFolderName(NAME);
        filePlan = createNewCategory.selectSave().render();
        Assert.assertNotNull(filePlan);

        FileDirectoryInfo recordCategory = filePlan.getFileDirectoryInfo(NAME);
        Assert.assertNotNull(recordCategory);

        recordCategory.clickOnTitle();
        filePlan.setInRecordCategory(true);
        filePlan = filePlan.render();
        Assert.assertNotNull(filePlan);

        // create record folder
        Assert.assertTrue(filePlan.isCreateNewFolderDisplayed());
        CreateNewFolderForm createNewFolder = filePlan.selectCreateNewFolder().render();
        Assert.assertNotNull(createNewFolder);

        createNewFolder.enterName(NAME);
        createNewFolder.enterTitle(TITLE);
        createNewFolder.enterDescription(DESC);

        filePlan.setExpectingRecordOrFolder(true);
        filePlan.setExpectedRecordOrFolderName(NAME);
        filePlan = createNewFolder.selectSave().render();
        Assert.assertNotNull(filePlan);

        FileDirectoryInfo recordFolder = filePlan.getFileDirectoryInfo(NAME);
        Assert.assertNotNull(recordFolder);

        recordFolder.clickOnTitle();
        filePlan.setInRecordFolder(true);
        filePlan = filePlan.render();
        Assert.assertNotNull(filePlan);

        // file a record
        Assert.assertTrue(filePlan.isUnfiledRecordsContainerFileDisplayed());
        RmUploadFilePage rmRecordFileDialog = filePlan.selectCreateNewUnfiledRecordsContainerFile().render();
        Assert.assertNotNull(rmRecordFileDialog);

        String fileName = Long.valueOf(System.currentTimeMillis()).toString();
        fileElectronicRecord(drone, rmRecordFileDialog, fileName);
        filePlan = filePlan.render();
        Assert.assertNotNull(filePlan);

        // FIXME: view record details

        // delete RM vanilla site
        deleteRMSite();
    }
}
