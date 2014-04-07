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
 * RMA-1193 - Create rule - no rules defined
 * 
 * @author Roy Wetherall
 * @since 2.2
 */
@Test
@Listeners(FailedTestListener.class)
public class RMA_1193 extends AbstractManageRulesRegressionTest
{
    /** test data */
    protected String ruleName = generateNameFromClass();
    protected String user = generateNameFromClass();
    protected String categoryName = generateNameFromClass();
    protected String folderName = generateNameFromClass();
    
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
        login(user, PASSWORD);
        try
        {
            // TODO make sure file plan has correct state set and render include expected folders
            
            openRMSite(false);
            FilePlanPage filePlan = (FilePlanPage)rmSiteDashBoard.selectFilePlan().render();
            
            // navigate to created category
            filePlan.navigateToFolder(categoryName);
            
            // create rule
            createRule(ruleName, RmActionSelectorEnterpImpl.PerformActions.CLOSE_RECORD_FOLDER, WhenOption.INBOUND);
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
