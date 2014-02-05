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
package org.alfresco.rm;

import org.alfresco.po.rm.RmCreateSitePage;
import org.alfresco.po.rm.RmCreateSitePage.RMSiteCompliance;
import org.alfresco.po.rm.RmDashBoardPage;
import org.alfresco.po.rm.util.RmPoUtils;
import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.SiteType;
import org.alfresco.po.share.util.SiteUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * Abstract Records Management integration test.
 * 
 * @author Roy Wetherall
 * @since 2.2
 */
public abstract class AbstractIntegrationTest extends AbstractTest
{
    /** Default site details */
    protected static final String RM_SITE_NAME = "Records Management";
    protected static final String RM_SITE_DESC = "Records Management Site";
    protected static final String RM_SITE_URL = "rm";
    
    /** RM dashboard for loged in user */
    protected RmDashBoardPage dashBoard;

    /**
     * Executed before class
     */
    @BeforeClass(groups={"RM","nonCloud"})
    public void doSetup()
    {
        setup();
    }
    
    /**
     * Test setup
     */
    protected void setup()
    {
        // log into Share
        login(username, password);

        // create RM site
        createRMSite();
    }

    /**
     * Executed after class
     */
    @AfterClass(groups={"RM","nonCloud"})
    public void doTeardown()
    {        
        teardown();
    }
    
    /**
     * Test teardown
     */
    protected void teardown()
    {
        // delete RM site
        deleteRMSite();
    }
    
    /**
     * Helper method that logs into share and sets the dashboard PO
     * 
     * @param userName  user name
     * @param password  password
     */
    protected void login(String userName, String password)
    {
        dashBoard = RmPoUtils.loginAs(drone, shareUrl, username, password).render();        
    }
    
    /**
     * Helper method to create a 'vanilla' RM site
     */
    protected void createRMSite()
    {
        createRMSite(RMSiteCompliance.STANDARD);
    }
    
    /**
     * Helper method to create RM site
     */
    protected void createRMSite(RMSiteCompliance compliance)
    {
        // delete RM site
        deleteRMSite();
        
        // Click create site dialog
        RmCreateSitePage createSite = dashBoard.getRMNavigation().selectCreateSite().render();
        Assert.assertTrue(createSite.isCreateSiteDialogDisplayed());

        // Select RM Site
        createSite.selectSiteType(SiteType.RecordsManagement);
        Assert.assertEquals(createSite.getSiteName(), RM_SITE_NAME);
        Assert.assertEquals(createSite.getDescription(), RM_SITE_DESC);
        Assert.assertEquals(createSite.getSiteUrl(), RM_SITE_URL);

        // Create RM Site
        RmDashBoardPage site = ((RmDashBoardPage) createSite.createRMSite(RMSiteCompliance.STANDARD)).rmRender();
        Assert.assertNotNull(site);
        Assert.assertTrue(RM_SITE_NAME.equalsIgnoreCase(site.getPageTitle()));
        Assert.assertTrue(site.getRMSiteNavigation().isDashboardActive());
        Assert.assertFalse(site.getRMSiteNavigation().isFilePlanActive());        
    }
    
    /**
     * Helper method to delete RM site
     */
    protected void deleteRMSite()
    {
        // Check if the RM Site already exists, if so delete it
        SiteFinderPage siteFinderPage = SiteUtil.searchSite(drone, RM_SITE_NAME).render();
        if (siteFinderPage.hasResults() == true)
        {
            siteFinderPage.deleteSite(RM_SITE_NAME).render();
        }       
    }

}
