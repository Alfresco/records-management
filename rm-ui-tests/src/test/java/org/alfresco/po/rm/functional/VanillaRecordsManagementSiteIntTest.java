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

import java.io.File;
import java.io.IOException;

import org.alfresco.po.rm.RmCreateSitePage.RMSiteCompliance;
import org.alfresco.po.rm.RmUploadFilePage;
import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.fileplan.RecordDetailsPage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.junit.Assert;
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

    @Test
    public void testDODSite() throws Exception
    {
        // create DOD site
        createRMSite(RMSiteCompliance.DOD5015);
        FilePlanPage filePlan = loadTestData();
        
        // view record details
        RecordDetailsPage recordDetails = filePlan.selectRecord(0, drone.getDefaultWaitTime());
        recordDetails.render();
        
        // check that the DOD properties are visible
        Assert.assertTrue(recordDetails.isPropertySetVisible("DOD5015"));
        
        // delete DOD site
        deleteRMSite();
    }

    @Test
    public void testVanillaSite() throws Exception
    {
        // create vanilla site
        createRMSite();
        FilePlanPage filePlan = loadTestData();

        // view record details
        RecordDetailsPage recordDetails = filePlan.selectRecord(0, drone.getDefaultWaitTime());
        recordDetails.render();
        
        // check that the DOD properties are visible
        Assert.assertFalse(recordDetails.isPropertySetVisible("DOD5015"));

        // delete RM vanilla site
        deleteRMSite();
    }
        
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