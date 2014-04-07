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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.alfresco.po.rm.RmCreateSitePage.RMSiteCompliance;
import org.alfresco.po.rm.RmUploadFilePage;
import org.alfresco.po.rm.common.AbstractIntegrationTest;
import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.fileplan.RecordDetailsPage;
import org.alfresco.po.rm.fileplan.RecordInfo;
import org.alfresco.po.rm.fileplan.action.AddRecordMetadataAction;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.webdrone.HtmlPage;
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

    /**
     * Setup tests
     */
    @Override
    public void setup()
    {
        // log into Share
        login(username, password);
    }

    /**
     * Load test data 
     * 
     * @return  {@link FilePlanPage} File plan page object
     */
    private FilePlanPage loadTestData() throws IOException
    {
        // get file plan root
        FilePlanPage filePlan = FilePlanPage.getFilePlanRoot(rmSiteDashBoard);

        // create new category
        filePlan = createNewCategory(filePlan, NAME, TITLE, DESC);

        // click on category
        filePlan = filePlan.selectCategory(NAME, drone.getDefaultWaitTime()).render();

        // create record folder
        filePlan = createNewRecordFolder(filePlan, NAME, TITLE, DESC);

        // click on record folder
        filePlan = filePlan.selectFolder(NAME, drone.getDefaultWaitTime()).render();

        // file a record
        return fileElectronicToRecordFolder(filePlan);

    }

    /**
     * Test creation of a DoD RM site
     */
    @Test
    public void testDODSite() throws Exception
    {
        // create DOD site
        deleteRMSite();
        createRMSite(RMSiteCompliance.DOD5015);
        FilePlanPage filePlan = loadTestData();

        List<FileDirectoryInfo> files = filePlan.getFiles();
        Assert.assertEquals(1, files.size());

        // check the add record metadata action on the record
        RecordInfo recordInfo = filePlan.getRecordInfo(0);
        Assert.assertTrue(recordInfo.isVisibleAddRecordMetadata());
        AddRecordMetadataAction addRecordMetadata = recordInfo.clickAddRecordMetadata();
        checkDoDAddRecordMetadata(addRecordMetadata);

        // view record details
        recordInfo = filePlan.render().getRecordInfo(0);
        RecordDetailsPage recordDetails = recordInfo.clickTitle();
        recordDetails.render();

        // check that the DOD properties are visible
        Assert.assertTrue(recordDetails.isPropertySetVisible("DOD5015"));

        // check that the add record metadata action is visible
        Assert.assertTrue(recordDetails.isAddRecordMetaDataVisible());

        // select the add record metadata action and check the expected aspects are present
        addRecordMetadata = recordDetails.selectAddRecordMetadata().render();
        checkDoDAddRecordMetadata(addRecordMetadata);

        // delete DOD site
        deleteRMSite();
    }

    /**
     * Helper method to check whether the DoD record meta data is correctly shown.
     * 
     * @param addRecordMetadata the add record metadata action page object
     */
    private HtmlPage checkDoDAddRecordMetadata(AddRecordMetadataAction addRecordMetadata)
    {
        // check that all the DoD aspects are showing
        List<String> aspects = addRecordMetadata.getAllRecordAspects();
        Assert.assertEquals(4, aspects.size());
        Assert.assertTrue(aspects.contains("dod:scannedRecord"));
        Assert.assertTrue(aspects.contains("dod:pdfRecord"));
        Assert.assertTrue(aspects.contains("dod:digitalPhotographRecord"));
        Assert.assertTrue(aspects.contains("dod:webRecord"));
        return addRecordMetadata.clickCancel();
    }

    /**
     * Test a 'vanilla' RM site creation
     */
    @Test
    public void testVanillaSite() throws Exception
    {
        // create vanilla site
        deleteRMSite();
        createRMSite();
        FilePlanPage filePlan = loadTestData();

        // check the add record metadata action on the record
        RecordInfo recordInfo = filePlan.getRecordInfo(0);
        Assert.assertFalse(recordInfo.isVisibleAddRecordMetadata());

        // view record details
        RecordDetailsPage recordDetails = recordInfo.clickTitle().render();

        // check that the DOD properties are not visible
        Assert.assertFalse(recordDetails.isPropertySetVisible("DOD5015"));

        // check that the add record metadata action is not visible (since there are not aspects out of the box for vanilla)
        Assert.assertFalse(recordDetails.isAddRecordMetaDataVisible());

        // delete RM vanilla site
        deleteRMSite();
    }

    /**
     * Helper method to file an electronic record to a record folder
     * 
     * @param filePlan  file plan page object
     * @return {@link FilePlanPage} file plan page object
     */
    private FilePlanPage fileElectronicToRecordFolder(FilePlanPage filePlan) throws IOException
    {
        // generate random file name
        String fileName = Long.valueOf(System.currentTimeMillis()).toString();

        // open file dialog
        RmUploadFilePage rmRecordFileDialog = filePlan.selectFile();

        // select to upload electronic record
        rmRecordFileDialog.selectElectronic(drone);

        // upload file
        File file = SiteUtil.prepareFile(fileName);
        String filePath = file.getCanonicalPath();
        filePlan = (FilePlanPage)rmRecordFileDialog.uploadFile(filePath);

        // render file plan
        filePlan.setInRecordFolder(true);
        return filePlan.render(fileName);
    }
}