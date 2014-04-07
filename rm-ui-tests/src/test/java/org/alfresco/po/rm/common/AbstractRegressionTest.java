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
package org.alfresco.po.rm.common;

import static org.alfresco.webdrone.WebDroneUtil.checkMandotaryParam;

import org.alfresco.po.rm.RmConsolePage;
import org.alfresco.po.rm.RmConsoleUsersAndGroups;
import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.util.RmPageObjectUtils;
import org.testng.annotations.Test;

/**
 * Abstract regression test
 * 
 * @author Roy Wetherall
 */
public abstract class AbstractRegressionTest extends AbstractRecordsManagementTest
{    
    /** default user password */
    protected static final String PASSWORD = "password";
    
    /**
     * Execute regression test
     */
    @Test(groups = {"rmRegression"})
    public void regressionTest() throws Throwable
    {
        try
        {
             // login as admin user
            login();
            try
            {
                // navigate to RM site
                openRMSite(isExisitingRMSiteDeletedOnStartup());               
            }
            finally
            {
                // logout
                logout();
            }
            
            // preconditions
            preConditions();
        
            
            // test execution
            testExecution();
                        
            // clean up site if required to
            login();
            try
            {
                if (isRMSiteDeletedOnTearDown())
                {
                    deleteRMSite();
                }
            }
            finally
            {
                // logout
                logout();
            }
        }
        catch (Throwable e)
        {
            // report error
            reportError(getClass().getCanonicalName(), e);
        }
    }

    /**
     * Test's preconditions
     */
    abstract protected void preConditions() throws Exception;
    
    /**
     * Test execution
     */
    abstract protected void testExecution() throws Exception;   

    /**
     * Helper method to generate a name from the class name
     * 
     * @return  {@link String}  random name incorporating class name
     */
    protected String generateNameFromClass()
    {
        return getClass().getSimpleName().replace("_", "-") + RmPageObjectUtils.getRandomString(3);
    }
    
    /**
     * Helper method to assign users to role
     * 
     * TODO this should be on a page object
     * 
     * @param filePlan
     * @param roleName
     * @param users
     */
    protected void assignUsersToRole(FilePlanPage filePlan, String roleName, String ... users)
    {
        checkMandotaryParam("filePlan", filePlan);
        checkMandotaryParam("roleName", roleName);

        RmConsolePage consolePage= filePlan.openRmConsolePage();
        RmConsoleUsersAndGroups newRole = consolePage.openUsersAndGroupsPage();
        
        for (String user : users)
        {
            newRole.assignUserToRole(user, roleName).render();
        }
    }

}
