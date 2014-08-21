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
package org.alfresco.rm.common;

import java.io.IOException;

import org.alfresco.po.rm.RmCreateSitePage;
import org.alfresco.po.rm.RmCreateSitePage.RMSiteCompliance;
import org.alfresco.po.rm.RmSiteDashBoardPage;
import org.alfresco.po.rm.util.RmPageObjectUtils;
import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.webdrone.RenderTime;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

/**
 * Abstract Records Management test, manages
 * the life cycle of all records management based tests.
 * The spring context and creation of WebDrone is managed in AbstractTest
 * while AbstractRecordsManagementTest ensures that RM site is created
 * and removed at the end of the functional test and will include
 * RM related helper methods.
 *  
 *
 * @author Roy Wetherall
 * @author Michael Suzuki
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
     * Setup standard rm site.
     * @throws Exception 
     */
    @BeforeSuite
    public void setup() throws Exception
    {
        try
        {
            setupContext("share-po-test-context.xml");
            getWebDrone();
            createRMSite(RMSiteCompliance.STANDARD);
        }
        finally
        {
            drone.quit();
        }
    }
    
    /**
     * Test teardown,remove rm site
     * @throws Exception 
     */
    @AfterSuite(alwaysRun=true)
    public void teardown() throws Exception
    {
        deleteRMSite();
    }
    
    @AfterTest
    public void quitWebDrone() throws Exception
    {
        if(drone != null)
        {
            drone.quit();
        }
    }
    
    /**
     * Helper method to login with the default credentials
     * @throws Exception 
     */
    @BeforeClass
    protected void loginToRmDashboard() throws Exception
    {
        login(username, password);
        String url = shareUrl.replace("/share", "/share/page/site/rm/dashboard");
        drone.navigateTo(url);
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
     * Helper method to open RM site
     */
    protected void openRMSite(boolean deleteExisting)
    {
        SiteFinderPage siteFinderPage = SiteUtil.searchSite(drone, RmCreateSitePage.RM_SITE_NAME).render();
        siteFinderPage.selectSite(RmCreateSitePage.RM_SITE_NAME);
    }

    /**
     * Helper method to create RM site
     * @throws Exception 
     */
    protected void createRMSite(RMSiteCompliance compliance) throws Exception
    {
        drone.navigateTo(shareUrl);
        login(username,password);
        SharePage page = (SharePage) drone.getCurrentPage(); 
        // Click create site dialog
        page.getNav().selectCreateSite().render();
        //TODO Fix me RMCreateSitePage should have a common interface with create site page.
        RmCreateSitePage createSite = new RmCreateSitePage(drone);
        // Create RM Site
        createSite.createRMSite(compliance);
        logout();
        drone.closeWindow();
    }

    /**
     * Helper method to delete RM site
     * @throws Exception 
     */
    private void deleteRMSite() throws Exception
    {
        try
        {
            getWebDrone();
            login(username,password);
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
        finally
        {
            closeWebDrone();
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

        // rethrow exception so that the correct stack trace is displayed in Eclipse
        throw exception;
    }

    /**
     * Get custom stack trace for display in log.
     * 
     * @param exception exception
     * @return String   custom stack trace
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
     * 
     * @return
     */
    protected String genearateNameFromTest()
    {
        return testName.replace("_", "-") + RmPageObjectUtils.getRandomString(3);
    }
    /**
     * Helper method that logs into share
     * to rm site dashboard.
     *
     * @param userName  user name
     * @param password  password
     */
    protected void login(String userName, String password)
    {
        drone.navigateTo(shareUrl);
        LoginPage loginPage = new LoginPage(drone).render();
        loginPage.loginAs(userName, password);
    }
    protected void login()
    {
        login(username,password);
    }
}
