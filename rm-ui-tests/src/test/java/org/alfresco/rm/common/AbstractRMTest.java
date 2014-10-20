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

import org.alfresco.po.rm.RmCreateSitePage;
import org.alfresco.po.rm.RmCreateSitePage.RMSiteCompliance;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.webdrone.RenderTime;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;


/**
 * Abstract Records Management test, manages the life cycle of all records management based tests.
 * Creates the spring context, WebDrone instances and ensures that RM site is created before any of the tests
 * are executed.
 * 
 * @author Michael Suzuki
 * @author Shan Najarajan
 * @since 2.3
 *
 */
public class AbstractRMTest extends AbstractTest
{

    @BeforeSuite(alwaysRun = true)
    @Parameters({ "contextFileName" })
    public void setupContext() throws Exception
    {
        super.setupContext();
        super.createRMSite(RMSiteCompliance.STANDARD);
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
     * Helper method to login with the default credentials
     * @throws Exception 
     */
    @BeforeClass
    protected void login() throws Exception
    {
        login(username, password);
    }
    
    @BeforeClass(alwaysRun = true)
    public void getWebDrone() throws Exception
    {
        super.getWebDrone();
    }

}
