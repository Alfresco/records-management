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
package org.alfresco.rm.po;

import org.alfresco.po.rm.RmCreateSitePage;
import org.alfresco.po.rm.RmCreateSitePage.RMSiteCompliance;
import org.alfresco.po.rm.RmSiteType;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.rm.common.AbstractRecordsManagementTest;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Tests RM create site page
 *
 * @author Roy Wetherall
 * @since 2.2
 */
@Listeners(FailedTestListener.class)
public class RmCreateSitePageTest extends AbstractRecordsManagementTest
{
    /** create site page object */
    RmCreateSitePage page;

    @BeforeClass(groups={"RM","nonCloud"})
    public void doSetup() 
    {
        // render create site page
        page = rmSiteDashBoard.getRMNavigation().selectCreateSite().render();
        Assert.assertTrue(page.isCreateSiteDialogDisplayed());
    }

    @Test
    public void testInitialState()
    {
        // check the initial states of the controls
        Assert.assertTrue(page.getSiteName().isEmpty());
        Assert.assertTrue(page.getSiteUrl().isEmpty());
        Assert.assertTrue(page.getDescription().isEmpty());

        Assert.assertEquals(RmSiteType.COLLABORATION, page.rmGetSiteType());
        // TODO check contents of drop down

        // TODO check site visibility

        Assert.assertFalse(drone.find(RmCreateSitePage.SELECT_COMPLIANCE).isDisplayed());
    }

    @Test(dependsOnMethods="testInitialState")
    public void testCloseReopen()
    {
        // select RM type
        page.selectSiteType(RmSiteType.RECORDS_MANAGEMENT);
        Assert.assertTrue(drone.find(RmCreateSitePage.SELECT_COMPLIANCE).isDisplayed());

        // cancel page
        page.cancel();

        // reopen page
        page = rmSiteDashBoard.getRMNavigation().selectCreateSite().render();
        Assert.assertTrue(page.isCreateSiteDialogDisplayed());

        // check the initial states of the controls
        Assert.assertTrue(page.getSiteName().isEmpty());
        Assert.assertTrue(page.getSiteUrl().isEmpty());
        Assert.assertTrue(page.getDescription().isEmpty());

        Assert.assertEquals(RmSiteType.COLLABORATION, page.rmGetSiteType());
        // TODO check contents of drop down

        // TODO check site visibility

        Assert.assertFalse(drone.find(RmCreateSitePage.SELECT_COMPLIANCE).isDisplayed());
    }

    @Test
    public void testSetName()
    {
        Assert.assertTrue(page.getSiteName().isEmpty());
        Assert.assertTrue(page.getSiteUrl().isEmpty());

        // set the site name
        drone.find(By.name("title")).sendKeys("testing");

        // check the url
        Assert.assertEquals(page.getSiteName(), "testing");
        Assert.assertEquals(page.getSiteUrl(), "testing");

        // set the site name
        drone.find(By.name("title")).sendKeys(" with space");

        // check the url
        Assert.assertEquals(page.getSiteName(), "testing with space");
        Assert.assertEquals(page.getSiteUrl(), "testing-with-space");
    }

    @Test
    public void testSetType()
    {
        // select the records management type
        page.selectSiteType(RmSiteType.RECORDS_MANAGEMENT);

        // check the controls after selecting RM site type
        Assert.assertEquals(page.getSiteName(), RmCreateSitePage.RM_SITE_NAME);
        Assert.assertEquals(page.getDescription(), RmCreateSitePage.RM_SITE_DESC);
        Assert.assertEquals(page.getSiteUrl(), RmCreateSitePage.RM_SITE_URL);

        Assert.assertEquals(RmSiteType.RECORDS_MANAGEMENT, page.rmGetSiteType());

        // TODO check site visibility

        // check the compliance drop down
        Assert.assertTrue(drone.find(RmCreateSitePage.SELECT_COMPLIANCE).isDisplayed());
        // TODO check whats in the compliance drop down

        // select DOD compliance
        page.selectRMSiteCompliance(RMSiteCompliance.DOD5015);

        // select collaboration site
        page.selectSiteType(RmSiteType.COLLABORATION);

        // check the controls
        Assert.assertTrue(page.getSiteName().isEmpty());
        Assert.assertTrue(page.getSiteUrl().isEmpty());
        Assert.assertTrue(page.getDescription().isEmpty());
        Assert.assertEquals(page.getSiteType(), RmSiteType.COLLABORATION);

        // TODO check site visibility

        Assert.assertFalse(drone.find(RmCreateSitePage.SELECT_COMPLIANCE).isDisplayed());

    }
}