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

import java.io.IOException;

import org.alfresco.po.rm.RmCreateSitePage;
import org.alfresco.po.rm.RmCreateSitePage.RMSiteCompliance;
import org.alfresco.po.rm.RmSiteDashBoardPage;
import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.webdrone.RenderTime;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * Abstract Records Management test
 *
 * @author Roy Wetherall
 * @since 2.2
 */
public abstract class AbstractRecordsManagementTest extends AbstractTest
{
    /** max wati time */
    protected final static long MAX_WAIT_TIME = 60000;
    
    /** logger */
    protected static Log logger = LogFactory.getLog(AbstractRecordsManagementTest.class);
    
    /** RM site dashboard for logged in user */
    protected RmSiteDashBoardPage rmSiteDashBoard;

    /**
     * Indicates whether an existing RM site should be delete on
     * test startup
     *
     * @return  boolean true if site should be deleted, false otherwise
     */
    protected boolean isExisitingRMSiteDeletedOnStartup()
    {
        return true;
    }

    /**
     * Indicates whether an existing RM site should be deleted on
     * test tear down.
     *
     * @return  boolean true if site should be deleted, false otherwise
     */
    protected boolean isRMSiteDeletedOnTearDown()
    {
        return true;
    }

    /**
     * Executed before class
     */
    @BeforeClass(groups={"RM","nonCloud"})
    public void doSetup()
    {
        setup();
    }
    
    /**
     * Setup test
     */
    protected void setup()
    {
        // do nothing
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
        // do nothing
    }
    
    /**
     * Helper method to login with the default credentials
     */
    protected void login()
    {
        login(username, password);
    }
    
    /**
     * Helper method that logs into share and sets the dashboard PO
     *
     * @param userName  user name
     * @param password  password
     */
    protected void login(String userName, String password)
    {
        drone.navigateTo(shareUrl);
        LoginPage loginPage = new LoginPage(drone).render();

        loginPage.loginAs(userName, password);
        rmSiteDashBoard = new RmSiteDashBoardPage(drone).render();
    }
    
    /**
     * Helper method that logs the current user out of share
     */
    protected void logout()
    {
        ShareUtil.logout(drone);
    }
    
    /**
     * Helper method to create a 'vanilla' RM site
     */
    public void createRMSite()
    {
        createRMSite(RMSiteCompliance.STANDARD);
    }
    
    /**
     * Helper method to open RM site
     */
    protected void openRMSite(boolean deleteExisting)
    {
        SiteFinderPage siteFinderPage = SiteUtil.searchSite(drone, RmCreateSitePage.RM_SITE_NAME).render();
        if (siteFinderPage.hasResults())
        {
            if (deleteExisting)
            {
                deleteRMSite();
                createRMSite();
            }
            else
            {
                siteFinderPage.selectSite(RmCreateSitePage.RM_SITE_NAME);
            }
        }
        else
        {
            // create a new RM site
            createRMSite();
        }
    }

    /**
     * Helper method to create RM site
     */
    public void createRMSite(RMSiteCompliance compliance)
    {
        // Click create site dialog
        RmCreateSitePage createSite = rmSiteDashBoard.getRMNavigation().selectCreateSite().render();
        Assert.assertTrue(createSite.isCreateSiteDialogDisplayed());

        // Create RM Site
        RmSiteDashBoardPage site = ((RmSiteDashBoardPage) createSite.createRMSite(compliance)).rmRender();
        Assert.assertNotNull(site);
        Assert.assertTrue(RmCreateSitePage.RM_SITE_NAME.equalsIgnoreCase(site.getPageTitle()));
        Assert.assertTrue(site.getRMSiteNavigation().isDashboardActive());
        Assert.assertFalse(site.getRMSiteNavigation().isFilePlanActive());
    }

    /**
     * Helper method to delete RM site
     */
    public void deleteRMSite()
    {
        // Check if the RM Site already exists, if so delete it
        SiteFinderPage siteFinderPage = SiteUtil.searchSite(drone, RmCreateSitePage.RM_SITE_NAME).render();
        if (siteFinderPage.hasResults())
        {
            siteFinderPage = siteFinderPage.deleteSite(RmCreateSitePage.RM_SITE_NAME).render();

            while (siteFinderPage.hasResults())
            {
                RenderTime timer = new RenderTime(5000);
                timer.start();
                try
                {
                    siteFinderPage = siteFinderPage.render();
                }
                catch (NoSuchElementException nse)
                {
                }
                finally
                {
                    timer.end();
                }
            }

            Assert.assertFalse(siteFinderPage.hasResults());
        }
    }
    
    /**
     * Report any error that is thrown.  Re-throw once reported.
     * 
     * @param driver        
     * @param testName
     * @param exception
     */
    protected void reportError(String testName, Throwable exception) throws Throwable
    {
        logger.error("Error in Test: " + testName, exception);
        try
        {
            saveScreenShot(testName);
            savePageSource(testName);
        }
        catch (IOException e)
        {
            logger.error("Unable to save screen shot of Test: " + testName + " : " + getCustomStackTrace(exception));
        }

        // rethrow exception
        throw exception;
    }

    /**
     * 
     * @param exception
     * @return
     */
    protected String getCustomStackTrace(Throwable exception)
    {
        final StringBuilder result = new StringBuilder();
        result.append(exception.toString());
        final String newline = System.getProperty("line.separator");
        result.append(newline);

        for (StackTraceElement element : exception.getStackTrace())
        {
            result.append(element);
            result.append(newline);
        }
        return result.toString();
    }
}
