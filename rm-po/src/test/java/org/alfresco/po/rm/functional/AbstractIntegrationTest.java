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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.alfresco.po.rm.RmCreateSitePage;
import org.alfresco.po.rm.RmCreateSitePage.RMSiteCompliance;
import org.alfresco.po.rm.RmDashBoardPage;
import org.alfresco.po.rm.RmUploadFilePage;
import org.alfresco.po.rm.common.AbstractRecordsManagementTest;
import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.fileplan.filter.unfiledrecords.UnfiledRecordsContainer;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * Abstract Records Management integration test.
 *
 * @author Roy Wetherall
 * @since 2.2
 */
public abstract class AbstractIntegrationTest extends AbstractRecordsManagementTest
{
    /** File record dialog constants */
    protected static final By PROMPT_PANEL_ID = By.id("prompt");
    protected static final By BUTTON_TAG_NAME = By.tagName("button");
    protected static final String ELECTRONIC = "Electronic";

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

        // Create RM Site
        RmDashBoardPage site = ((RmDashBoardPage) createSite.createRMSite(RMSiteCompliance.STANDARD)).rmRender();
        Assert.assertNotNull(site);
        Assert.assertTrue(RmCreateSitePage.RM_SITE_NAME.equalsIgnoreCase(site.getPageTitle()));
        Assert.assertTrue(site.getRMSiteNavigation().isDashboardActive());
        Assert.assertFalse(site.getRMSiteNavigation().isFilePlanActive());
    }

    /**
     * Helper method to delete RM site
     */
    protected void deleteRMSite()
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
     * FIXME!!!
     *
     * @param drone
     * @param rmRecordFileDialog
     * @param fileName
     * @throws IOException
     */
    private static HtmlPage fileElectronicRecord(final WebDrone drone, final RmUploadFilePage rmRecordFileDialog, String fileName) throws IOException
    {
        WebElement prompt = drone.findAndWait(PROMPT_PANEL_ID);
        List<WebElement> elements = prompt.findElements(BUTTON_TAG_NAME);
        WebElement electronicRecordButton = rmRecordFileDialog.findButton(ELECTRONIC, elements);
        electronicRecordButton.click();

        String name = StringUtils.isNotBlank(fileName) ? fileName : Long.valueOf(System.currentTimeMillis()).toString();
        File file = SiteUtil.prepareFile(name);
        String filePath = file.getCanonicalPath();
        return rmRecordFileDialog.uploadFile(filePath);
    }

    /**
     * Helper method to file a record
     *
     * @param drone
     * @param rmRecordFileDialog
     * @param fileName
     * @throws IOException
     */
    protected static void fileElectronicRecordToFilePlan(final WebDrone drone, final RmUploadFilePage rmRecordFileDialog, String fileName) throws IOException
    {
        WebDroneUtil.checkMandotaryParam("drone", drone);
        WebDroneUtil.checkMandotaryParam("rmRecordFileDialog", rmRecordFileDialog);
        // FileName can be blank. In this case a name will be generated

        FilePlanPage filePlanPage = (FilePlanPage) fileElectronicRecord(drone, rmRecordFileDialog, fileName);
        filePlanPage.render(fileName);
    }

    /**
     * Helper method to file a record
     *
     * @param drone
     * @param rmRecordFileDialog
     * @param fileName
     * @throws IOException
     */
    protected static void fileElectronicRecordToUnfiledRecordsContainer(final WebDrone drone, final RmUploadFilePage rmRecordFileDialog, String fileName) throws IOException
    {
        WebDroneUtil.checkMandotaryParam("drone", drone);
        WebDroneUtil.checkMandotaryParam("rmRecordFileDialog", rmRecordFileDialog);
        // FileName can be blank. In this case a name will be generated

        UnfiledRecordsContainer unfiledRecordsContainer = (UnfiledRecordsContainer) fileElectronicRecord(drone, rmRecordFileDialog, fileName);
        unfiledRecordsContainer.render(fileName);
    }
}