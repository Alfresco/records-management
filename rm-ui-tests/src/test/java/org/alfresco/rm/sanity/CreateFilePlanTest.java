package org.alfresco.rm.sanity;

import static org.alfresco.po.rm.RmCreateSitePage.RMSiteCompliance.STANDARD;
/**
 * Sanity Test:Create File Plan funcionality
 * RMA-2665
 * 
 * @author hamara
 */
import static org.alfresco.po.rm.RmSiteType.RECORDS_MANAGEMENT;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.alfresco.po.rm.RmCreateSitePage;
import org.alfresco.po.rm.RmSiteDashBoardPage;
import org.alfresco.po.rm.RmUploadFilePage;
import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.fileplan.filter.FilePlanFilter;
import org.alfresco.po.rm.fileplan.filter.hold.HoldsContainer;
import org.alfresco.po.rm.fileplan.filter.unfiledrecords.UnfiledRecordsContainer;
import org.alfresco.po.rm.fileplan.toolbar.CreateNewRecordFolderDialog;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.user.UserSiteItem;
import org.alfresco.po.share.user.UserSitesPage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.rm.util.RMTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(RMTestListener.class)
public class CreateFilePlanTest extends AbstractSanityTest
{
    private static Log logger = LogFactory.getLog(CreateFilePlanTest.class);
    protected static final String sitetype = "RecordsManagement";
    private static final String RM_SITE_NAME = "Records Management";
    protected static String password = "password";
    private RmCreateSitePage rmcreateSitePage;
    private RmSiteDashBoardPage rmSiteDashBoardPage;
    private static final String USER_NAME = "RMA_2665";
    private String categoryName = generateNameFromClass();
    private String subcat = generateNameFromClass();
    private String folderName2 = generateNameFromClass();
    // private String folderName = generateNameFromClass();
    private String recordName = generateNameFromClass();
    // private String subfolderName = generateNameFromClass();
    public String folderName1 = generateNameFromClass();
    protected RmSiteDashBoardPage rmSiteDashBoard;
    protected String siteVisibility = "public";
    protected String siteName;
    private SitePage collaborationSitePage;
    private DocumentLibraryPage documentLibraryPage;
    private UploadFilePage uploadFilePage;
    private SharePage page;
    private static final String HOLD_NAME1 = "Hold1";
    private static final String HOLD_NAME2 = "Hold2";
    private static final String HOLD_REASON = "Reason for hold";
    private static final String FILE_NAME = "RMA_2665.txt";
    private String name = generateNameFromClass();
    private String title = generateNameFromClass();

    /**
     * Part of the setup creates the Alfresco admin user.
     * Which will be used in RMA-2665 test.
     * 
     * @throws IOException
     * @throws Exception
     */
    @BeforeClass
    public void setup() throws Exception
    {
        siteName = String.format("Site-%d", System.currentTimeMillis());
        createEnterpriseUserWithAdminGroup(USER_NAME);
    }

    /**
     * Creates the RM File Plan and
     * Category, subcategory, folder, electronic record and non electronic record {@link rmCreateFilePlan}
     */
    public void rmCreateFilePlan() throws IOException
    {
        try
        {
            DashBoardPage dashBoardPage = ShareUtil.logInAs(drone, USER_NAME, password).render();
            rmcreateSitePage = dashBoardPage.getNav().selectCreateSite().render();
            rmcreateSitePage.selectSiteType(RECORDS_MANAGEMENT);
            rmcreateSitePage.selectRMSiteCompliance(STANDARD);
            rmSiteDashBoardPage = rmcreateSitePage.selectOk().render();
            FilePlanPage filePlanPage = rmSiteDashBoardPage.getRMSiteNavigation().selectFilePlan().render();
            filePlanPage.createCategory(categoryName, true);
            filePlanPage.selectCategory(categoryName, 30000);
            filePlanPage.createCategory(subcat, true);
            filePlanPage.selectCategory(subcat, 0);
            filePlanPage.createFolder(folderName1);
            filePlanPage.createFolder(folderName2);
            filePlanPage.navigateToFolder(folderName1);
            RmUploadFilePage rmUploadFilePage = filePlanPage.selectFile().render();
            UploadFilePage filePage = rmUploadFilePage.selectElectronic(drone);
            filePage.uploadFile(getFileFromTestData(FILE_NAME, "").getCanonicalPath()).render();
        }
        finally
        {
            ShareUtil.logout(drone);
        }

    }

    /**
     * Creates Collaboration Site.
     * Upload a file and
     * declare the file as record. {@link createCollaborationSite}
     */
    public void createCollaborationSite() throws Exception
    {

        try
        {
            login(USER_NAME, password);
            if (SiteUtil.createSite(drone, siteName, siteVisibility))
                collaborationSitePage = drone.getCurrentPage().render();
            else
                System.out.println("collaboration site create error");

            collaborationSitePage = drone.getCurrentPage().render();
            documentLibraryPage = collaborationSitePage.getSiteNav().selectSiteDocumentLibrary().render();
            uploadFilePage = documentLibraryPage.getNavigation().selectFileUpload().render();
            File file = SiteUtil.prepareFile(recordName);
            documentLibraryPage = uploadFilePage.uploadFile(file.getCanonicalPath()).render();
            documentLibraryPage.render();
            documentLibraryPage.getFileDirectoryInfo(file.getName()).declareRecord();
        }
        finally
        {
            ShareUtil.logout(drone);
        }

    }

    /**
     * Create a folder in UnfiledRecords
     * Verify the record from collaboration site {@link rmUnfiledRecords}
     */
    public void rmUnfiledRecords() throws Exception
    {

        try
        {
            page = loginAs(USER_NAME, "password");
            UserSitesPage userSitesPage = page.getNav().selectMySites().render();
            UserSiteItem rmSiteItem = userSitesPage.getSite(RM_SITE_NAME);
            rmSiteItem.clickOnSiteName().render();
            RmSiteDashBoardPage rmSiteDashBoardPage = drone.getCurrentPage().render();
            FilePlanPage filePlanPage = rmSiteDashBoardPage.getRMSiteNavigation().selectFilePlan().render();
            FilePlanFilter filePlanFilter = filePlanPage.getFilePlanFilter();
            UnfiledRecordsContainer unfiledRecordsContainer = filePlanFilter.selectUnfiledRecordsContainer().render();
            CreateNewRecordFolderDialog createNewRecordFolderDialog = unfiledRecordsContainer.selectCreateNewUnfiledRecordsContainerFolder().render();
            createNewRecordFolderDialog.enterName(name);
            createNewRecordFolderDialog.enterTitle(title);
            createNewRecordFolderDialog.selectSave();
            List<FileDirectoryInfo> results = unfiledRecordsContainer.getFiles();
            if (logger.isTraceEnabled())
            {
                logger.trace("results are not null: " + results);
                logger.trace("results empty: " + results.isEmpty());
            }
            Assert.assertNotNull(results);
            Assert.assertFalse(results.isEmpty());
            boolean isFolder1 = results.get(0).isFolderType();
            boolean isRecord = results.get(1).isTypeRecord();
            Assert.assertEquals(isRecord, true);
            Assert.assertTrue(isFolder1);

        }
        finally
        {
            ShareUtil.logout(drone);
        }
    }

    /**
     * Create holds in RM File Plan{@link rmCreateHolds}
     */
    public void rmCreateHolds()
    {
        try
        {
            login(USER_NAME, password);
            String url = shareUrl.replace("/share", "/share/page/site/rm/dashboard");
            drone.navigateTo(url);
            rmSiteDashBoard = new RmSiteDashBoardPage(drone).render();
            // FilePlanPage filePlanPage = rmSiteDashBoard.getRMSiteNavigation().selectFilePlan().render();
            FilePlanPage filePlan = FilePlanPage.getFilePlanRoot(rmSiteDashBoard);
            FilePlanFilter filePlanFilter = filePlan.getFilePlanFilter();
            filePlanFilter.selectHoldsContainer().render();
            HoldsContainer.createNewHold(drone, HOLD_NAME1, HOLD_REASON);
            HoldsContainer.createNewHold(drone, HOLD_NAME2, HOLD_REASON);
        }
        finally
        {
            ShareUtil.logout(drone);
        }
    }

    @Test
    public void RMA_2665() throws Exception
    {
        rmCreateFilePlan();
        createCollaborationSite();
        rmUnfiledRecords();
        rmCreateHolds();
    }

    @AfterClass
    public void tearDown() throws Exception
    {
        ShareUtil.loginAs(drone, shareUrl, USER_NAME, password).render();
        SiteUtil.deleteSite(drone, RM_SITE_NAME);
        ShareUtil.logout(drone);
        deleteUser(USER_NAME);
    }

}