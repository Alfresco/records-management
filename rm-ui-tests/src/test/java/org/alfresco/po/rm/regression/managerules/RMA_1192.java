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
package org.alfresco.po.rm.regression.managerules;

import org.alfresco.po.rm.RmActionSelectorEnterpImpl;
import org.alfresco.po.rm.RmConsoleUsersAndGroups.SystemRoles;
import org.alfresco.po.rm.RmFolderRulesWithRules;
import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.share.util.FailedTestListener;
import org.junit.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;


/**
 * RMA-1192 - Delete Rule
 * 
 * @author Roy Wetherall
 * @since 2.2
 */
@Test
@Listeners(FailedTestListener.class)
public class RMA_1192 extends AbstractManageRulesRegressionTest
{
    /** test data */
    protected String ruleName = generateNameFromClass();
    protected String user = generateNameFromClass();
    protected String categoryName = generateNameFromClass();
    protected String folderName1 = generateNameFromClass();
    protected String folderName2 = generateNameFromClass();
   
    /**
     * @see org.alfresco.po.rm.common.AbstractRegressionTest#preConditions()
     */
    @Override
    protected void preConditions() throws Exception
    {        
        // create the user
        createEnterpriseUser(user);
        
        login();
        try
        {
            // open the file plan
            openRMSite(false);
            FilePlanPage filePlan = (FilePlanPage)rmSiteDashBoard.selectFilePlan().render();
    
            // add test user to RM admin role
            assignUsersToRole(filePlan, SystemRoles.RECORDS_MANAGEMENT_ADMINISTRATOR.getValue(), user);
            
            // create category
            openRMSite(false);
            filePlan = (FilePlanPage)rmSiteDashBoard.selectFilePlan().render();
            filePlan.createCategory(categoryName, true);
            
            // navigate to created category
            filePlan.navigateToFolder(categoryName);
            
            // create rule
            createRule(ruleName, RmActionSelectorEnterpImpl.PerformActions.CLOSE_RECORD_FOLDER, WhenOption.INBOUND);
        }
        finally
        {
            logout();
        }
    }
    
    /**
     * @see org.alfresco.po.rm.common.AbstractRegressionTest#testExecution()
     */
    @Override
    protected void testExecution()
    {
        // as user
        login(user, PASSWORD);
        try
        {          
            // navigate to the file plan
            openRMSite(false);
            FilePlanPage filePlan = (FilePlanPage)rmSiteDashBoard.selectFilePlan().render();
            
            // navigate to created category
            filePlan.navigateToFolder(categoryName);
            
            // create a folder and show the rule is running
            filePlan.createFolder(folderName1);
            Assert.assertTrue(filePlan.isFolderClosed(folderName1));
            
            // delete the rule
            RmFolderRulesWithRules manageRulesPage = filePlan.selectManageRulesWithRules();
            manageRulesPage.deleteRule(ruleName);
            
            // create a folder and show the rule isn't running 
            filePlan = rmSiteDashBoard.selectFilePlan().render();
            filePlan.navigateToFolder(categoryName);
            filePlan.createFolder(folderName2);
            Assert.assertFalse(filePlan.isFolderClosed(folderName2));

        }
        finally
        {
            logout();
        }
             
    }
}
