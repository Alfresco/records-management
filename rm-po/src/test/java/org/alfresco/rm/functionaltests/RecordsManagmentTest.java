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
package org.alfresco.rm.functionaltests;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.util.FailedTestListener;
import org.testng.annotations.Listeners;

/**
 * Tests for the record management module.
 *
 * @author Michael Suzuki
 * @version 1.7.1
 */
@Listeners(FailedTestListener.class)
public class RecordsManagmentTest extends AbstractTest
{
//    private static final String RM_SITE_NAME = "Records Management";
//    private static final String RM_SITE_DESCRIPTION = "Records Management Site";
//    private static final String RM_SITE_URL = "rm";
//    private static final String RM_FILE_PLAN_URL_SUFFIX = "/page/site/rm/documentlibrary";
//    private static final String RM_DASHBOARD_URL_SUFFIX = "/page/site/rm/dashboard";
//    private static final String RM_UNFILED_RECORDS = "Unfiled Records";
//    private static final String RM_CATEGORY_NAME = "Test category name";
//    private static final String RM_CATEGORY_TITLE = "Test category title";
//    private static final String RM_CATEGORY_DESC = "Test category description";
//    private static final String RM_FOLDER_NAME = "Test folder name";
//    private static final String RM_FOLDER_TITLE = "Test folder title";
//    private static final String RM_FOLDER_DESC = "Test folder desc";
//    private static final String RM_TEST_FILE_NAME = "rmtest";
//    private static final String COLLAB_SITE_NAME = "ddSiteTest" + System.currentTimeMillis();
//    private static final String COLLAB_SITE_DESC = "description";
//    private static final String COLLAB_SITE_VISIBILITY = "Public";
//    private static final By PROMPT_PANEL_ID = By.id("prompt");
//    private static final By BUTTON_TAG_NAME = By.tagName("button");
//    private RMDashBoardPage dashBoardPage;
//    private FilePlanPage filePlanPage;
//    private File file, file2;
//    private String rmFilePlanUrl, rmDashbordUrl;
//
//    @SuppressWarnings("unused")
//    @BeforeClass(groups={"RM", "nonCloud"})
//    private void prepare() throws Exception
//    {
//        dashBoardPage = ShareUtil.loginAs(drone, shareUrl, username, password).render();
//        SiteUtil.createSite(drone, COLLAB_SITE_NAME, COLLAB_SITE_DESC, COLLAB_SITE_VISIBILITY);
//
//        file = SiteUtil.prepareFile();
//        file2 = SiteUtil.prepareFile(RM_TEST_FILE_NAME);
//        rmFilePlanUrl = shareUrl + RM_FILE_PLAN_URL_SUFFIX;
//        rmDashbordUrl = shareUrl + RM_DASHBOARD_URL_SUFFIX;
//
//        SiteDashboardPage siteDashboardPage = drone.getCurrentPage().render();
//        DocumentLibraryPage documentLibraryPage = siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();
//
//        UploadFilePage uploadFilePage = documentLibraryPage.getNavigation().selectFileUpload().render();
//        documentLibraryPage = uploadFilePage.uploadFile(file.getCanonicalPath()).render();
//
//        uploadFilePage = documentLibraryPage.getNavigation().selectFileUpload().render();
//        documentLibraryPage = uploadFilePage.uploadFile(file2.getCanonicalPath()).render();
//    }
//
//    @AfterClass(groups={"RM", "nonCloud"})
//    public void teardown()
//    {
//        SiteUtil.deleteSite(drone, COLLAB_SITE_NAME);
//        SiteUtil.deleteSite(drone, RM_SITE_NAME);
//    }
//
//    @Test()
//    public void manageRules()
//    {
//        drone.navigateTo(rmFilePlanUrl);
//        filePlanPage = drone.getCurrentPage().render();
//        // FIXME
//        drone.waitFor(2000);
//        Assert.assertTrue(filePlanPage.isManageRulesDisplayed());
//        ManageRulesPage manageRulesPage = filePlanPage.selectManageRules().render();
//        Assert.assertNotNull(manageRulesPage);
//    }
//
//    @Test(groups={"RM", "nonCloud"})
//    public void createRMSite()
//    {
//        RMCreateSitePage createSitePage = dashBoardPage.getRMNavigation().selectCreateSite().render();
//        Assert.assertTrue(createSitePage.isCreateSiteDialogDisplayed());
//        createSitePage.selectSiteType(SiteType.RecordsManagement);
//        Assert.assertEquals(createSitePage.getSiteName(), RM_SITE_NAME);
//        Assert.assertEquals(createSitePage.getDescription(), RM_SITE_DESCRIPTION);
//        Assert.assertEquals(createSitePage.getSiteUrl(), RM_SITE_URL);
//
//        createSitePage.selectSiteType(SiteType.Collaboration);
//        Assert.assertEquals(createSitePage.getSiteName(), "");
//        Assert.assertEquals(createSitePage.getDescription(), "");
//        Assert.assertEquals(createSitePage.getSiteUrl(), "");
//
//        RMDashBoardPage rmDashBoardPage = createSitePage.createRMSite().render();
//        Assert.assertNotNull(rmDashBoardPage);
//        Assert.assertTrue(RM_SITE_NAME.equalsIgnoreCase(rmDashBoardPage.getPageTitle()));
//        Assert.assertTrue(rmDashBoardPage.getRMSiteNavigation().isDashboardActive());
//        Assert.assertFalse(rmDashBoardPage.getRMSiteNavigation().isFilePlanActive());
//    }
//
//    @Test(dependsOnMethods="createRMSite")
//    public void testRMNavigation()
//    {
//        drone.navigateTo(rmDashbordUrl);
//        RMDashBoardPage rmDashBoardPage = drone.getCurrentPage().render();
//        RMSiteNavigation rmSiteNavigation = rmDashBoardPage.getRMSiteNavigation();
//        filePlanPage = rmSiteNavigation.selectFilePlan().render();
//        rmSiteNavigation = filePlanPage.getSiteNav();
//        Assert.assertFalse(rmSiteNavigation.isDashboardActive());
//        Assert.assertTrue(rmSiteNavigation.isFilePlanActive());
//        Assert.assertTrue(rmSiteNavigation.isFilePlanDisplayed());
//        Assert.assertTrue(rmSiteNavigation.isMoreDisplayed());
//        Assert.assertFalse(rmSiteNavigation.isSelectSiteMembersDisplayed());
//
//        RMSitePage dashboard = rmSiteNavigation.selectSiteDashBoard().render();
//        rmSiteNavigation = dashboard.getSiteNav();
//        Assert.assertTrue(rmSiteNavigation.isDashboardDisplayed());
//        Assert.assertTrue(rmSiteNavigation.isDashboardActive());
//        Assert.assertFalse(rmSiteNavigation.isFilePlanActive());
//        Assert.assertFalse(rmSiteNavigation.isRecordSearchDisplayed());
//        Assert.assertFalse(rmSiteNavigation.isSelectSiteMembersDisplayed());
//        Assert.assertTrue(rmSiteNavigation.isMoreDisplayed());
//
//        RecordSearchPage recordSearch = rmSiteNavigation.selectRecordSearch().render();
//        rmSiteNavigation = recordSearch.getSiteNav();
//
//        Assert.assertTrue(rmSiteNavigation.isRecordSearchDisplayed());
//        Assert.assertTrue(rmSiteNavigation.isRecordSearchActive());
//        Assert.assertFalse(rmSiteNavigation.isDashboardActive());
//        Assert.assertFalse(rmSiteNavigation.isFilePlanDisplayed());
//        Assert.assertFalse(rmSiteNavigation.isSelectSiteMembersDisplayed());
//        Assert.assertTrue(rmSiteNavigation.isMoreDisplayed());
//
//        RMSiteMembersPage siteMembers = rmSiteNavigation.selectSiteMembers().render();
//        rmSiteNavigation = siteMembers.getSiteNav();
//        Assert.assertTrue(rmSiteNavigation.isSelectSiteMembersDisplayed());
//        Assert.assertTrue(rmSiteNavigation.isSelectSiteMembersActive());
//        Assert.assertFalse(rmSiteNavigation.isDashboardActive());
//        Assert.assertFalse(rmSiteNavigation.isFilePlanDisplayed());
//        Assert.assertTrue(rmSiteNavigation.isMoreDisplayed());
//
//        RMConsolePage console = rmSiteNavigation.selectRMConsole().render();
//        Assert.assertNotNull(console);
//        Assert.assertTrue("Records Management Console".equalsIgnoreCase(rmDashBoardPage.getPageTitle()));
//    }
//
//    @Test(dependsOnMethods="createRMSite")
//    public void createCategory()
//    {
//        drone.navigateTo(rmFilePlanUrl);
//        filePlanPage = drone.getCurrentPage().render();
//        Assert.assertTrue(filePlanPage.isCreateNewCategoryDisplayed());
//        CreateNewCategoryForm form = filePlanPage.selectCreateNewCategory().render();
//        Assert.assertNotNull(form.getRecordCategoryId());
//        form.enterName(RM_CATEGORY_NAME);
//        form.enterTitle(RM_CATEGORY_TITLE);
//        form.enterDescription(RM_CATEGORY_DESC);
//        filePlanPage = form.selectSave().render();
//    }
//
//    @Test(dependsOnMethods="createCategory")
//    public void createFolder()
//    {
//        drone.navigateTo(rmFilePlanUrl);
//        filePlanPage = drone.getCurrentPage().render();
//        Assert.assertTrue(filePlanPage.isCreateNewCategoryDisplayed());
//        Assert.assertTrue(filePlanPage.hasRecords());
//        FileDirectoryInfo recordsManagemnetCategory = filePlanPage.getFileDirectoryInfo(RM_CATEGORY_NAME);
//        Assert.assertNotNull(recordsManagemnetCategory);
//        recordsManagemnetCategory.clickOnTitle();
//        Assert.assertTrue(filePlanPage.isCreateNewCategoryDisplayed());
//        Assert.assertTrue(filePlanPage.isCreateNewFolderDisplayed());
//        // FIXME
//        drone.waitFor(2000);
//        CreateNewFolderForm form = filePlanPage.selectCreateNewFolder().render();
//        form.enterName(RM_FOLDER_NAME);
//        form.enterTitle(RM_FOLDER_TITLE);
//        form.enterDescription(RM_FOLDER_DESC);
//        filePlanPage = form.selectSave().render();
//    }
//
//    @Test(dependsOnMethods="createFolder")
//    public void createRecord() throws IOException
//    {
//        Assert.assertTrue(filePlanPage.isCreateNewFolderDisplayed());
//        Assert.assertTrue(filePlanPage.hasRecords());
//        FileDirectoryInfo recordsManagemnetFolder = filePlanPage.getFileDirectoryInfo(RM_FOLDER_NAME);
//        Assert.assertNotNull(recordsManagemnetFolder);
//        recordsManagemnetFolder.clickOnTitle();
//        Assert.assertTrue(filePlanPage.isFileRecordDisplayed());
//        // FIXME
//        drone.waitFor(2000);
//        RMUploadFilePage rmUploadFilePage = filePlanPage.selectFileRecord().render();
//        WebElement prompt = drone.findAndWait(PROMPT_PANEL_ID);
//        List<WebElement> elements = prompt.findElements(BUTTON_TAG_NAME);
//        WebElement electronicRecordButton = rmUploadFilePage.findButton("Electronic", elements);
//        electronicRecordButton.click();
//        rmUploadFilePage.uploadFile(SiteUtil.prepareFile(Long.valueOf(System.currentTimeMillis()).toString()).getCanonicalPath()).render();
//
//
//        /*
//        drone.navigateTo(shareUrl + String.format("/page/site/%s/documentlibrary",siteName));
//        DocumentLibraryPage libPage = drone.getCurrentPage().render();
//        FileDirectoryInfo file = libPage.getFileDirectoryInfo(1);
//        Assert.assertNotNull(file);
//        Assert.assertFalse(file.isRecord());
//        libPage = file.declareRecord().render();
//        file = libPage.getFileDirectoryInfo(1);
//        Assert.assertTrue(file.isRecord());
//
//        DocumentDetailsPage detailsPage = libPage.selectFile(file2.getName()).render();
//        Assert.assertFalse(detailsPage.isHideRecordLinkDisplayed());
//        Assert.assertTrue(detailsPage.isDeclareRecordVisible());
//        Assert.assertFalse(detailsPage.isAddRecordMetaDataVisible());
//        FilePlanPage filePlanPage = detailsPage.selectDeclareRecod().render();
//        Assert.assertTrue(filePlanPage.isAddRecordMetaDataVisible());
//        */
//    }
//
//    @Test(dependsOnMethods="createRecord")
//    public void navigateToUnfiledRecords()
//    {
//        drone.navigateTo(rmFilePlanUrl);
//        filePlanPage = drone.getCurrentPage().render();
//        FilePlanNavigation nav = filePlanPage.getFilePlanNavigation();
//
//        Assert.assertNotNull(nav);
//        Assert.assertTrue(nav.isUnfiledRecordsVisible());
//        filePlanPage = nav.selectUnfiledRecords().render();
//        String current = filePlanPage.getFilePlanDescription();
//        Assert.assertTrue(current.contains(RM_UNFILED_RECORDS));
//        /*
//        List<FileDirectoryInfo> files = filePlanPage.getFiles();
//        Assert.assertEquals(2, files.size());
//        String name = files.get(0).getName();
//        Assert.assertNotNull(name);
//        FileDirectoryInfo result = filePlanPage.getFileDirectoryInfo(name);
//        Assert.assertNotNull(result);
//        Assert.assertEquals(result.getName(), name);
//        */
//    }
}
