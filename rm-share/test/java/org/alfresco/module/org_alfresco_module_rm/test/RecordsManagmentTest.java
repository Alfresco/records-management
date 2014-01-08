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
package org.alfresco.module.org_alfresco_module_rm.test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.rm.CreateNewCategoryForm;
import org.alfresco.po.share.rm.FilePlanNavigation;
import org.alfresco.po.share.rm.FilePlanPage;
import org.alfresco.po.share.rm.RMConsolePage;
import org.alfresco.po.share.rm.RMDashBoardPage;
import org.alfresco.po.share.rm.RMSiteMembersPage;
import org.alfresco.po.share.rm.RMSiteNavigation;
import org.alfresco.po.share.rm.RMSitePage;
import org.alfresco.po.share.rm.RecordSearchPage;
import org.alfresco.po.share.site.CreateSitePage;
import org.alfresco.po.share.site.ManageRulesPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteType;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.po.util.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Tests record management module.
 * @author Michael Suzuki
 * @version 1.7.1
 */
@Listeners(FailedTestListener.class)
public class RecordsManagmentTest extends AbstractTest
{
    private final String rmSiteName = "Records Management";
    private final String siteDescription = "Records Management Site";
    private final String siteUrl = "rm";
    private DashBoardPage dashBoard;
    private String siteName;
    private File file, file2;

    @SuppressWarnings("unused")
    @BeforeClass(groups={"RM","nonCloud"})
    private void prepare() throws Exception
    {
        siteName = "ddSiteTest" + System.currentTimeMillis();
        dashBoard = ShareUtil.loginAs(drone, shareUrl, username, password).render();
        SiteUtil.createSite(drone, siteName, "description", "Public");
        file = SiteUtil.prepareFile();
        file2 = SiteUtil.prepareFile("rmtest");
        SiteDashboardPage page = drone.getCurrentPage().render();
        DocumentLibraryPage documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file.getCanonicalPath()).render();
        uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file2.getCanonicalPath()).render();
    }

    @AfterClass(groups={"RM","nonCloud"})
    public void teardown()
    {
        SiteUtil.deleteSite(drone, siteName);
        SiteUtil.deleteSite(drone, rmSiteName);
    }
    @Test()
    public void manageRules()
    {
        drone.navigateTo(shareUrl + "/page/site/rm/documentlibrary");
        FilePlanPage filePlanPage = drone.getCurrentPage().render();
        Assert.assertTrue(filePlanPage.isManageRulesDisplayed());
        ManageRulesPage rulesPage = filePlanPage.selectManageRules().render();
        Assert.assertNotNull(rulesPage);
    }
    /**
     * Test Site creation.
     *
     * @throws Exception if error
     */
    @Test(groups={"RM","nonCloud"})
    public void createRMSite()
    {
        CreateSitePage createSite = dashBoard.getNav().selectCreateSite().render();
        Assert.assertTrue(createSite.isCreateSiteDialogDisplayed());
        createSite.selectSiteType(SiteType.RecordsManagement);
        Assert.assertEquals(createSite.getSiteName(), rmSiteName);
        Assert.assertEquals(createSite.getDescription(), siteDescription);
        Assert.assertEquals(createSite.getSiteUrl(), siteUrl);

        createSite.selectSiteType(SiteType.Collaboration);
        Assert.assertEquals(createSite.getSiteName(), "");
        Assert.assertEquals(createSite.getDescription(), "");
        Assert.assertEquals(createSite.getSiteUrl(), "");

        RMDashBoardPage site = createSite.createRMSite().render();
        Assert.assertNotNull(site);
        Assert.assertTrue(rmSiteName.equalsIgnoreCase(site.getPageTitle()));
        Assert.assertTrue(site.getSiteNav().isDashboardActive());
        Assert.assertFalse(site.getSiteNav().isFilePlanActive());
    }

    @Test(dependsOnMethods="createRMSite")
    public void testRMNavigation()
    {
        drone.navigateTo(shareUrl + "/page/site/rm/dashboard");
        RMDashBoardPage site = drone.getCurrentPage().render();
        RMSiteNavigation nav = site.getSiteNav();
        FilePlanPage filePlan = nav.selectFilePlan().render();
        nav = filePlan.getSiteNav();
        Assert.assertFalse(nav.isDashboardActive());
        Assert.assertTrue(nav.isFilePlanActive());
        Assert.assertTrue(nav.isFilePlanDisplayed());
        Assert.assertTrue(nav.isMoreDisplayed());
        Assert.assertFalse(nav.isSelectSiteMembersDisplayed());

        RMSitePage dashboard = nav.selectSiteDashBoard().render();
        nav = dashboard.getSiteNav();
        Assert.assertTrue(nav.isDashboardDisplayed());
        Assert.assertTrue(nav.isDashboardActive());
        Assert.assertFalse(nav.isFilePlanActive());
        Assert.assertFalse(nav.isRecordSearchDisplayed());
        Assert.assertFalse(nav.isSelectSiteMembersDisplayed());
        Assert.assertTrue(nav.isMoreDisplayed());

        RecordSearchPage recordSearch = nav.selectRecordSearch().render();
        nav = recordSearch.getSiteNav();

        Assert.assertTrue(nav.isRecordSearchDisplayed());
        Assert.assertTrue(nav.isRecordSearchActive());
        Assert.assertFalse(nav.isDashboardActive());
        Assert.assertFalse(nav.isFilePlanDisplayed());
        Assert.assertFalse(nav.isSelectSiteMembersDisplayed());
        Assert.assertTrue(nav.isMoreDisplayed());

        RMSiteMembersPage siteMembers = nav.selectSiteMembers().render();
        nav = siteMembers.getSiteNav();
        Assert.assertTrue(nav.isSelectSiteMembersDisplayed());
        Assert.assertTrue(nav.isSelectSiteMembersActive());
        Assert.assertFalse(nav.isDashboardActive());
        Assert.assertFalse(nav.isFilePlanDisplayed());
        Assert.assertTrue(nav.isMoreDisplayed());

        RMConsolePage console = nav.selectRMConsole().render();
        Assert.assertNotNull(console);
        Assert.assertTrue("Records Management Console".equalsIgnoreCase(site.getPageTitle()));
    }

    @Test(dependsOnMethods="createRMSite")
    public void createCategory()
    {
        drone.navigateTo(shareUrl + "/page/site/rm/documentlibrary");
        FilePlanPage filePlanPage = drone.getCurrentPage().render();
        Assert.assertTrue(filePlanPage.isCreateNewCategoryDisplayed());
        CreateNewCategoryForm form = filePlanPage.selectCreateNewCategory().render();
        Assert.assertNotNull(form.getRecordCategoryId());
        filePlanPage = form.selectCancel().render();

        form = filePlanPage.selectCreateNewCategory().render();
        form.enterName("rmtest");
        form.enterTitle("rmtest-title");
        form.enterDescription("test rm new record");
        filePlanPage = form.selectSave().render();
        Assert.assertTrue(filePlanPage.hasRecords());
    }

    @Test(dependsOnMethods="createRMSite")
    public void createRecord() throws IOException
    {
        drone.navigateTo(shareUrl + String.format("/page/site/%s/documentlibrary",siteName));
        DocumentLibraryPage libPage = drone.getCurrentPage().render();
        FileDirectoryInfo file = libPage.getFileDirectoryInfo(1);
        Assert.assertNotNull(file);
        Assert.assertFalse(file.isRecord());
        libPage = file.declareRecord().render();
        file = libPage.getFileDirectoryInfo(1);
        Assert.assertTrue(file.isRecord());

        DocumentDetailsPage detailsPage = libPage.selectFile(file2.getName()).render();
        Assert.assertFalse(detailsPage.isHideRecordLinkDisplayed());
        Assert.assertTrue(detailsPage.isDeclareRecordVisible());
        Assert.assertFalse(detailsPage.isAddRecordMetaDataVisible());
        FilePlanPage filePlanPage = detailsPage.selectDeclareRecod().render();
        Assert.assertTrue(filePlanPage.isAddRecordMetaDataVisible());
    }
    @Test(dependsOnMethods="createRecord")
    public void navigateToUnfiledRecords()
    {
        drone.navigateTo(shareUrl + "/page/site/rm/documentlibrary");
        FilePlanPage filePlanPage = drone.getCurrentPage().render();
        FilePlanNavigation nav = filePlanPage.getFilePlanNavigation();

        Assert.assertNotNull(nav);
        Assert.assertTrue(nav.isUnfiledRecordsVisible());
        filePlanPage = nav.selectUnfiledRecords().render();
        String current = filePlanPage.getFilePlanDescription();
        Assert.assertTrue(current.contains("Unfiled Records"));
        List<FileDirectoryInfo> files = filePlanPage.getFiles();
        Assert.assertEquals(2, files.size());
        String name = files.get(0).getName();
        Assert.assertNotNull(name);
        FileDirectoryInfo result = filePlanPage.getFileDirectoryInfo(name);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getName(),name);


    }
}
