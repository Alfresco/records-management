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
import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.share.util.FailedTestListener;
import org.junit.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;


/**
 * RMA-1373 - Create rule - some rules defined
 * 
 * @author Roy Wetherall
 * @since 2.2
 */
@Test
@Listeners(FailedTestListener.class)
public class RMA_1373 extends AbstractManageRulesRegressionTest
{
    /** test data */
    protected String ruleName = generateNameFromClass();
    protected String user1 = generateNameFromClass();
    protected String user2 = generateNameFromClass();
    protected String categoryName = generateNameFromClass();
    protected String folderName = generateNameFromClass();
    
    /**
     * Marking as a bug.
     * 
     * Currently the "new rule" button doesn't work when there is an existing rule.
     */
    @Test(groups = {"rmRegression", "rmBug"})
    public void regressionTest() throws Throwable
    {
        super.regressionTest();
    }
    
    /**
     * @see org.alfresco.po.rm.common.AbstractRegressionTest#preConditions()
     */
    @Override
    protected void preConditions() throws Exception
    {        
        // create the user
        createEnterpriseUser(user1);
        createEnterpriseUser(user2);
        
        login();
        try
        {
            // open the file plan
            openRMSite(false);
            FilePlanPage filePlan = (FilePlanPage)rmSiteDashBoard.selectFilePlan().render();
    
            // add test user to RM admin role
            assignUsersToRole(filePlan, SystemRoles.RECORDS_MANAGEMENT_ADMINISTRATOR.getValue(), user1, user2);
            
            // create category
            openRMSite(false);
            filePlan = (FilePlanPage)rmSiteDashBoard.selectFilePlan().render();
            filePlan.createCategory(categoryName, true);
        }
        finally
        {
            logout();
        }
        
        // as user1
        login(user1, PASSWORD);
        try
        {          
            // navigate to the file plan
            openRMSite(false);
            FilePlanPage filePlan = (FilePlanPage)rmSiteDashBoard.selectFilePlan().render();
            
            // navigate to created category
            filePlan.navigateToFolder(categoryName);
            
            // create rule
            createRule(ruleName, RmActionSelectorEnterpImpl.PerformActions.COMPLETE_EVENT, WhenOption.INBOUND);
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
        // as user2
        login(user2, PASSWORD);
        try
        {          
            // navigate to the file plan
            openRMSite(false);
            FilePlanPage filePlan = (FilePlanPage)rmSiteDashBoard.selectFilePlan().render();
            
            // navigate to created category
            filePlan.navigateToFolder(categoryName);
            
            // create rule
            createRule(ruleName, RmActionSelectorEnterpImpl.PerformActions.CLOSE_RECORD_FOLDER, WhenOption.INBOUND, true, false);
            filePlan = rmSiteDashBoard.selectFilePlan().render();
            
            // create a record folder
            filePlan.navigateToFolder(categoryName);
            filePlan.createFolder(folderName);
            
            // ensure the rule was applied
            Assert.assertTrue("Failed to apply rule", filePlan.isFolderClosed(folderName));
        }
        finally
        {
            logout();
        }
             
    }
}
