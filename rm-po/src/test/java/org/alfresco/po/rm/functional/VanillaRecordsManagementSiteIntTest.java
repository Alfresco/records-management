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
    @Override
    public void setup()
    {
        // log into Share
        login(username, password);
    }
    
    @Override
    protected void teardown()
    {
        // do nothing 
    }
    
    @Test
    public void testDODSite() throws Exception
    {
        // create DOD site
        createRMSite(RMSiteCompliance.DOD5015);

        FilePlanPage filePlan = dashBoard.getRMNavigation().selectFilePlan().render();
        drone.waitForPageLoad(5);
        
        CreateNewCategoryForm createNewCategory = filePlan.selectCreateNewCategory().render();
        createNewCategory.enterName("name");
        createNewCategory.enterTitle("title");
        createNewCategory.enterDescription("description");
        createNewCategory.selectSave().render();
                
        FileDirectoryInfo recordCategory = filePlan.getFileDirectoryInfo("name");     
        recordCategory.clickOnTitle();        
        filePlan.render();
        
        // create record folder
        CreateNewFolderForm createNewFolder = filePlan.selectCreateNewFolder().render();
        createNewFolder.enterName("name");
        createNewFolder.enterTitle("title");
        createNewFolder.enterDescription("description");
        createNewFolder.selectSave().render();
                
        FileDirectoryInfo recordFolder = filePlan.getFileDirectoryInfo("name");     
        recordFolder.clickOnTitle();        
        filePlan.render();
        
        // file a record
        RmUploadFilePage rmRecordFileDialog = filePlan.selectCreateNewUnfiledRecordsContainerFile().render();
        String fileName = Long.valueOf(System.currentTimeMillis()).toString();
        fileElectronicRecord(drone, rmRecordFileDialog, fileName);
        filePlan = filePlan.render();
        
        // view record details
        
        // delete DOD site
        deleteRMSite();
            
    }
    
    @Test
    public void testVanillaSite() throws Exception
    {
        // create vanilla site
        createRMSite();

        FilePlanPage filePlan = dashBoard.getRMNavigation().selectFilePlan().render();
        drone.waitForPageLoad(5);
        
        CreateNewCategoryForm createNewCategory = filePlan.selectCreateNewCategory().render();
        createNewCategory.enterName("name");
        createNewCategory.enterTitle("title");
        createNewCategory.enterDescription("description");
        createNewCategory.selectSave().render();
                
        FileDirectoryInfo recordCategory = filePlan.getFileDirectoryInfo("name");     
        recordCategory.clickOnTitle();        
        filePlan.render();
        
        // create record folder
        CreateNewFolderForm createNewFolder = filePlan.selectCreateNewFolder().render();
        createNewFolder.enterName("name");
        createNewFolder.enterTitle("title");
        createNewFolder.enterDescription("description");
        createNewFolder.selectSave().render();
                
        FileDirectoryInfo recordFolder = filePlan.getFileDirectoryInfo("name");     
        recordFolder.clickOnTitle();        
        filePlan.render();
        
        // file a record
        RmUploadFilePage rmRecordFileDialog = filePlan.selectCreateNewUnfiledRecordsContainerFile().render();
        String fileName = Long.valueOf(System.currentTimeMillis()).toString();
        fileElectronicRecord(drone, rmRecordFileDialog, fileName);
        filePlan = filePlan.render();
        
        // view record details
        
        // delete RM vanilla site
        deleteRMSite();
        
    }

}
