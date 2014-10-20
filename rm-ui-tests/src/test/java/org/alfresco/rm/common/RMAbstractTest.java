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

import static org.alfresco.webdrone.WebDroneUtil.checkMandotaryParam;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.alfresco.po.rm.RmConsolePage;
import org.alfresco.po.rm.RmConsoleUsersAndGroups;
import org.alfresco.po.rm.RmCreateSitePage;
import org.alfresco.po.rm.RmCreateSitePage.RMSiteCompliance;
import org.alfresco.po.rm.RmSiteDashBoardPage;
import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.util.RmPageObjectUtils;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.util.ShareTestProperty;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.NoSuchElementException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

/**
 * Abstract Records Management test, manages the life cycle of all records management based tests.
 * Creates the spring context, WebDrone instances and ensures that RM site is created before any of the tests
 * are executed. 
 * @author Michael Suzuki
 * @since 2.3
 * @author hamara
 */
public class RMAbstractTest
{
        private static Log logger = LogFactory.getLog(RMAbstractTest.class);
        private static ApplicationContext ctx;
        protected static String password;
        protected static String username;
        protected static String shareUrl;
        protected WebDrone drone;
        private String testName;
        protected RmSiteDashBoardPage rmSiteDashBoard;
    
        public WebDrone getDrone()
        {
            return drone;
        }

        @BeforeSuite(alwaysRun = true)
        @Parameters({"contextFileName"})
        public void setupContext() throws Exception
        {
                if(logger.isTraceEnabled())
                {
                    logger.trace("Starting test context");
                }
    
            List<String> contextXMLList = new ArrayList<String>();
            contextXMLList.add("share-po-test-context.xml");
            contextXMLList.add("webdrone-context.xml");
            ctx = new ClassPathXmlApplicationContext(contextXMLList.toArray(new String[contextXMLList.size()]));
    
            ShareTestProperty t = (ShareTestProperty) ctx.getBean("shareTestProperties");
            shareUrl = t.getShareUrl();
            username = t.getUsername();
            password = t.getPassword();
            createRMSite(RMSiteCompliance.STANDARD);
        }
        
        /**
         * Helper method to create RM site.  
         * @throws Exception 
         */
        private void createRMSite(RMSiteCompliance compliance) throws Exception
        {
            getWebDrone();
            drone.navigateTo(shareUrl);
            RmSiteDashBoardPage page = loginToRMSiteDashBoard(username, password).render();
            // Click create site dialog
            RmCreateSitePage createSite = page.getRMNavigation().selectCreateSite().render();
            // Create RM Site
            createSite.createRMSite(compliance).render();
            logout();
            closeWebDrone();           
        }
        
        /**
         * Helper method that logs the current user out of share
         */
        protected void logout()
        {
            ShareUtil.logout(drone);
        }
        
        @BeforeClass(alwaysRun = true)
        public void getWebDrone() throws Exception
        {
            drone = (WebDrone) ctx.getBean("webDrone");
            drone.maximize();
        }
        
        @AfterClass(alwaysRun = true)
        public void closeWebDrone()
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Closing web drone");
            }
            // Close the browser
            if (drone != null)
            {
                drone.quit();
                drone = null;
            }
        }
    
        /**
         * Remove RM site and data.
         */
        @AfterSuite(alwaysRun = true)
        public void teardown()
        {
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
         * Helper method that logs into share and navigates
         * to rm site dash board.
         * @param userName  user name
         * @param password  password
         */
        protected HtmlPage loginToRMSiteDashBoard(String userName, String password)
        {
            drone.navigateTo(shareUrl);
            LoginPage loginPage = new LoginPage(drone).render();
            loginPage.loginAs(userName, password);
            String url = shareUrl.replace("/share", "/share/page/site/rm/dashboard");
            drone.navigateTo(url);
            return drone.getCurrentPage();
        }
        
        /**
         * Helper method to generate a name from the class name
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
        }    @BeforeMethod(alwaysRun = true) 
        protected void startSession(Method method) throws Exception
        { 
            testName = method.getName(); 
            if(logger.isTraceEnabled())
            {
                logger.trace(String.format("Test run:%s.%s", 
                                            method.getDeclaringClass().getCanonicalName(),
                                            testName));
            }
        }
        
        /**
         * Helper method to login with the default credentials
         * @throws Exception 
         */
        protected void login() throws Exception
        {
            login(username, password);
        }
        
        /**
         * Helper method that logs into share and navigates
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
            String url = shareUrl.replace("/share", "/share/page/site/rm/dashboard");
            drone.navigateTo(url);
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


