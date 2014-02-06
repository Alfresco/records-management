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

import org.alfresco.po.rm.RmCreateSitePage.RMSiteCompliance;
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
    public void testDODSite()
    {
        // create DOD site
        createRMSite(RMSiteCompliance.DOD5015);
        
        // TODO test DOD site
        
        // delete DOD site
        deleteRMSite();
            
    }
    
    @Test
    public void testVanillaSite()
    {
        // create vanilla site
        createRMSite();
        
        // TODO test vanilla site
        
        // delete RM vanilla site
        deleteRMSite();
        
    }

}
