package org.alfresco.rm.sanity;

import static org.alfresco.po.rm.RmCreateSitePage.RMSiteCompliance.STANDARD;
import static org.alfresco.po.rm.RmSiteType.RECORDS_MANAGEMENT;

import java.io.IOException;

import org.alfresco.po.rm.RmCopyOrMoveUnfiledContentPage;
import org.alfresco.po.rm.RmCreateSitePage;
import org.alfresco.po.rm.RmSiteDashBoardPage;
import org.alfresco.po.rm.RmUploadFilePage;
import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.fileplan.RMInfoRequestforRecordPage;
import org.alfresco.po.rm.fileplan.RecordDetailsPage;
import org.alfresco.po.rm.fileplan.RecordInfo;
import org.alfresco.po.rm.fileplan.SelectDialog;
import org.alfresco.po.rm.fileplan.filter.FilePlanFilter;
import org.alfresco.po.rm.fileplan.filter.hold.HoldDialog;
import org.alfresco.po.rm.fileplan.filter.hold.HoldsContainer;
import org.alfresco.po.rm.fileplan.toolbar.ViewAuditLogDialog;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.DocumentAction;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.rm.util.RMTestListener;
import org.alfresco.webdrone.RenderTime;
import org.springframework.util.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Sanity Test:ManageIncompleteRecordsTest
 * RMA-2666
 * 
 * @author hamara
 */
@Listeners(RMTestListener.class)
public class ManageIncompleteRecordsTest extends AbstractSanityTest
{
    private static final String RM_SITE_NAME = "Records Management";
    protected static final String sitetype = "RecordsManagement";
    // private static final String RM_SITE_NAME = "Records Management";
    protected static String password = "password";
    private RmCreateSitePage rmcreateSitePage;
    private RmSiteDashBoardPage rmSiteDashBoardPage;
    private static final String USER_NAME = "RMA_2666";
    private String categoryName = "Category1";
    private String folderName2 = "Folder2";
    // private String folderName = generateNameFromClass();
    private String recordName = generateNameFromClass();
    // private String subfolderName = generateNameFromClass();
    private String folderName1 = "Folder1";
    protected RmSiteDashBoardPage rmSiteDashBoard;
    protected String siteVisibility = "public";
    private static final String HOLD_NAME1 = "Hold1";
    private static final String HOLD_NAME2 = "Hold2";
    private static final String HOLD_REASON = "Reason for hold";
    private static final String FILE_NAME = "RMA_2665.txt";
    private RenderTime timer = new RenderTime(50000);
    private String typePage = "Category1";
    FilePlanPage filePlanPage;
    DocumentAction action;
    final long timeout = 3000;

    /**
     * Part of the setup creates the Alfresco admin user.
     * Creates RMsite with fileplan and holds
     * 
     * @throws IOException
     * @throws Exception
     */
    @BeforeClass
    public void setup() throws Exception
    {
        createEnterpriseUserWithAdminGroup(USER_NAME);
        rmCreateFilePlan();
        rmCreateHolds();
    }

    /**
     * Creates the RM File Plan and
     * Category, subcategory, folder, electronic record and non electronic record {@link rmCreateFilePlan}
     */
    public void rmCreateFilePlan() throws IOException
    {
        DashBoardPage dashBoardPage = ShareUtil.logInAs(drone, USER_NAME, password).render();
        rmcreateSitePage = dashBoardPage.getNav().selectCreateSite().render();
        rmcreateSitePage.selectSiteType(RECORDS_MANAGEMENT);
        rmcreateSitePage.selectRMSiteCompliance(STANDARD);
        rmSiteDashBoardPage = rmcreateSitePage.selectOk().render();
        filePlanPage = rmSiteDashBoardPage.getRMSiteNavigation().selectFilePlan().render();
        filePlanPage.createCategory(categoryName, true).render();
        filePlanPage.selectCategory(categoryName, timeout).render();
        filePlanPage.createFolder(folderName1).render();
        filePlanPage.createFolder(folderName2).render();
        filePlanPage.navigateToFolder(folderName1).render();
        RmUploadFilePage rmUploadFilePage = filePlanPage.selectFile().render();
        UploadFilePage filePage = rmUploadFilePage.selectElectronic(drone);
        filePage.uploadFile(SiteUtil.prepareFile(FILE_NAME).getCanonicalPath()).render();
        filePlanPage.createRecord(recordName);
    }

    /**
     * Create holds in RM File Plan{@link rmCreateHolds}
     */
    public void rmCreateHolds()
    {

        filePlanPage.render();
        // filePlanPage = FilePlanPage.getFilePlanRoot(rmSiteDashBoard).render();
        FilePlanFilter filePlanFilter = filePlanPage.getFilePlanFilter();
        filePlanFilter.selectHoldsContainer().render();
        HoldsContainer.createNewHold(drone, HOLD_NAME1, HOLD_REASON).render();
        HoldsContainer.createNewHold(drone, HOLD_NAME2, HOLD_REASON).render();
    }

    /**
     * Verifies the Non Electronic Record actions
     * 
     * @throws Exception
     */
    public void verifyNERecordactions() throws Exception
    {
        filePlanPage.render();
        filePlanPage = rmSiteDashBoardPage.getRMSiteNavigation().selectFilePlan().render();
        filePlanPage = filePlanPage.selectCategory(categoryName, timeout).render();
        filePlanPage.navigateToFolder(folderName1).render();
        RecordInfo info = filePlanPage.getRecordInfo(0);
        Assert.isTrue(info.IsVisibleMoveTo());
        Assert.isTrue(info.IsVisibleCompleteRecord());
        Assert.isTrue(info.IsVisibleLinkTo());
        Assert.isTrue(info.IsVisibleViewAuditLog());
        Assert.isTrue(info.IsVisibleRequestInfo());
        Assert.isTrue(info.IsVisibleCopyto());
        Assert.isTrue(info.IsVisibleEditMetaData());
        Assert.isTrue(info.IsVisibleAddtoHold());
        filePlanPage = filePlanPage.selectFilePlanNavigation().render();
    }

    /**
     * Verifies the Non Electronic Record Details Page
     * has the following sections
     * 
     * @throws Exception
     */
    public void verifyNERecordDetails() throws Exception
    {

        filePlanPage.render();
        filePlanPage = filePlanPage.selectCategory(categoryName, timeout).render();
        filePlanPage.navigateToFolder(folderName1).render();
        RecordDetailsPage recordDetailsPage = filePlanPage.openRecordDetailsPage(recordName).render();
        Assert.isTrue(recordDetailsPage.istActionPresent());
        Assert.isTrue(recordDetailsPage.isSharePanePresent());
        Assert.isTrue(recordDetailsPage.isPropertiesPanelPresent());
        Assert.isTrue(recordDetailsPage.isReferencesPresent());
        Assert.isTrue(recordDetailsPage.isPropertySectionVisible("Record"));
        Assert.isTrue(recordDetailsPage.isPropertySectionVisible("Disposition Schedule"));
        filePlanPage = recordDetailsPage.getFilePlan().render();

    }

    /**
     * Verifies the Electronic Record actions
     * 
     * @throws Exception
     */

    public void verifyERecordactions() throws Exception
    {

        filePlanPage.render();
        filePlanPage = filePlanPage.selectCategory(categoryName, timeout).render();
        filePlanPage.navigateToFolder(folderName1).render();
        RecordInfo info = filePlanPage.getRecordInfo(1);
        Assert.isTrue(info.IsVisibleMoveTo());
        Assert.isTrue(info.IsVisibleCompleteRecord());
        Assert.isTrue(info.IsVisibleLinkTo());
        Assert.isTrue(info.IsVisibleViewAuditLog());
        Assert.isTrue(info.IsVisibleRequestInfo());
        Assert.isTrue(info.IsVisibleCopyto());
        Assert.isTrue(info.IsVisibleEditMetaData());
        Assert.isTrue(info.IsVisibleDownload());
        filePlanPage = filePlanPage.selectFilePlanNavigation().render();
    }

    /**
     * Performs the Non electronic record Edit Meta Data
     * 
     * @throws Exception
     */
    public void verifyEditMetaData() throws Exception
    {
        filePlanPage = filePlanPage.selectCategory(categoryName, timeout).render();
        filePlanPage = filePlanPage.navigateToFolder(folderName1).render();
        RecordInfo info = filePlanPage.getRecordInfo(0);
        EditDocumentPropertiesPage editMetaDataPage = info.clickEditMetadata().render();
        editMetaDataPage.setName("test");
        editMetaDataPage.setDocumentTitle("test title");
        editMetaDataPage.setDescription("test description");
        editMetaDataPage.selectSave();
        filePlanPage = drone.getCurrentPage().render();
    }

    /**
     * Performs the Non electronic record copy action
     * 
     * @throws Exception
     */
    public void verifyNERecordcopy() throws Exception
    {
        RecordInfo info = filePlanPage.getRecordInfo(0);
        RmCopyOrMoveUnfiledContentPage contentPage = info.clickCopyto().render(timer, typePage);
        contentPage.selectPath("File Plan/Category1/Folder2");
    }

    /**
     * Verifies the logs for the copy action
     * 
     * @throws Exception
     */
    public void verifyNERecordCopyLogs() throws Exception
    {
        filePlanPage.render();
        filePlanPage = filePlanPage.selectFilePlanNavigation().render();
        filePlanPage = filePlanPage.selectCategory(categoryName, 3000).render();
        filePlanPage = filePlanPage.navigateToFolder(folderName2).render();
        RecordInfo info = filePlanPage.getRecordInfo(0);
        ViewAuditLogDialog viewAuditLogDialog = info.clickVerifyLog();
        info.switchToAuditLog(drone);
        Assert.isTrue(viewAuditLogDialog.isGetCopyLog());
        viewAuditLogDialog.switchToRecordInfoPage();

    }

    /**
     * Performs add to hold action
     * 
     * @throws Exception
     */
    public void addtoHold() throws Exception
    {
        filePlanPage.render();
        filePlanPage = filePlanPage.selectFilePlanNavigation().render();
        filePlanPage = filePlanPage.selectCategory(categoryName, timeout).render();
        filePlanPage = filePlanPage.navigateToFolder(folderName1).render();
        RecordInfo info = filePlanPage.getRecordInfo(0);
        HoldDialog holdDialog = info.clickAddToHold().render();
        holdDialog.selectCheckBox(HOLD_NAME2);
        holdDialog.clickOK();
    }

    /**
     * Performs delete hold action
     * 
     * @throws Exception
     */
    public void deleteHold() throws Exception
    {
        filePlanPage.render();
        filePlanPage = filePlanPage.selectFilePlanNavigation().render();
        filePlanPage = filePlanPage.selectCategory(categoryName, 3000).render();
        filePlanPage = filePlanPage.navigateToFolder(folderName1).render();
        RecordInfo info = filePlanPage.getRecordInfo(0);
        Assert.isTrue(info.IsVisibleRemoveFromHold());
        HoldDialog holdDialog = info.clickRemoveFromHold();
        holdDialog.selectCheckBox(HOLD_NAME2);
        holdDialog.clickOK();
    }

    /**
     * Verifies the Non Electronic Record actions
     * after the delte hold
     */
    public void verifyNEActionsAfterDeleteHold() throws Exception
    {
        filePlanPage.render();
        filePlanPage = filePlanPage.selectFilePlanNavigation();
        filePlanPage = filePlanPage.selectCategory(categoryName, timeout).render();
        filePlanPage = filePlanPage.navigateToFolder(folderName1).render();
        RecordInfo info = filePlanPage.getRecordInfo(0);
        Assert.isTrue(info.IsVisibleMoveTo());
        Assert.isTrue(info.IsVisibleCompleteRecord());
        Assert.isTrue(info.IsVisibleLinkTo());
        Assert.isTrue(info.IsVisibleViewAuditLog());
        Assert.isTrue(info.IsVisibleRequestInfo());
        Assert.isTrue(info.IsVisibleCopyto());
        Assert.isTrue(info.IsVisibleEditMetaData());
        Assert.isTrue(info.IsVisibleDownload());
    }

    /**
     * Performs the dlelete record
     * action non electronic record
     * 
     * @throws Exception
     */
    public void deleteRecord() throws Exception
    {
        filePlanPage.render();
        filePlanPage = filePlanPage.selectFilePlanNavigation().render();
        filePlanPage = filePlanPage.selectCategory(categoryName, timeout).render();
        filePlanPage = filePlanPage.navigateToFolder(folderName1).render();
        RecordInfo info = filePlanPage.getRecordInfo(0);
        info.clickDeleteRecord();
    }

    /**
     * Performs move record action on non electronic record
     * 
     * @throws Exception
     */
    public void moveRecord() throws Exception
    {
        filePlanPage.render();
        filePlanPage = filePlanPage.selectFilePlanNavigation().render();
        filePlanPage = filePlanPage.selectCategory(categoryName, timeout).render();
        filePlanPage = filePlanPage.navigateToFolder(folderName2).render();
        RecordInfo info = filePlanPage.getRecordInfo(0);
        RmCopyOrMoveUnfiledContentPage contentPage = info.clickMoveto().render(timer, typePage);
        contentPage.selectPath("File Plan/Category1/Folder1");
    }

    /**
     * Performs link record action
     * 
     * @throws Exception
     */
    public void linkRecord() throws Exception
    {

        filePlanPage.render();
        filePlanPage = filePlanPage.selectFilePlanNavigation().render();
        filePlanPage = filePlanPage.selectCategory(categoryName, timeout).render();
        filePlanPage = filePlanPage.navigateToFolder(folderName1).render();
        RecordInfo info = filePlanPage.getRecordInfo(0);
        RmCopyOrMoveUnfiledContentPage contentPage = info.clickLinkto();
        contentPage.selectPath("File Plan/Category1/Folder2");
    }

    /**
     * Performs Complete Record action on Non electronic Record.
     * 
     * @throws Exception
     */
    public void completeNERecord() throws Exception
    {

        filePlanPage.render();
        filePlanPage = filePlanPage.selectFilePlanNavigation().render();
        filePlanPage = filePlanPage.selectCategory(categoryName, timeout).render();
        filePlanPage = filePlanPage.navigateToFolder(folderName1).render();
        RecordInfo info = filePlanPage.getRecordInfo(0);
        // Perform Complete record Action
        info.clickCompleteRecord();

        // Verfify whether the banner incomplete disappears
        Assert.isTrue((info.verifyCompleteRecord()));

        // Browse to destination linked record
        filePlanPage = filePlanPage.selectFilePlanNavigation().render();
        filePlanPage = filePlanPage.selectCategory(categoryName, timeout).render();
        filePlanPage = filePlanPage.navigateToFolder(folderName2).render();
        info = filePlanPage.getRecordInfo(0);

        // verify the linked record is also completed.
        Assert.isTrue(info.verifyCompleteRecord());

    }

    /**
     * Performs Request Information action
     * 
     * @throws Exception
     */
    public void RequestInformation() throws Exception
    {

        filePlanPage.render();
        filePlanPage = filePlanPage.selectFilePlanNavigation().render();
        filePlanPage = filePlanPage.selectCategory(categoryName, timeout).render();
        filePlanPage = filePlanPage.navigateToFolder(folderName1).render();
        RecordInfo info = filePlanPage.getRecordInfo(1);
        RMInfoRequestforRecordPage rmInfoRequestforRecordPage = info.clickRequestforInfo();
        rmInfoRequestforRecordPage.formPresent("Request information from");
        SelectDialog selectDialog = rmInfoRequestforRecordPage.clickSelect();
        String user = "Administrator";
        selectDialog.searchtUser(user);
        boolean result = selectDialog.verifyIsUser();
        Assert.isTrue(result);
        selectDialog.selectUser(user);
        String comment = "workflow";
        rmInfoRequestforRecordPage.provideInfo(comment);
    }

    /**
     * Performs Complete Record action on electronic Record.
     * 
     * @throws Exception
     */
    public void completeERecord() throws Exception
    {

        filePlanPage.render();
        filePlanPage = filePlanPage.selectFilePlanNavigation().render();
        filePlanPage = filePlanPage.selectCategory(categoryName, timeout).render();
        filePlanPage = filePlanPage.navigateToFolder(folderName1).render();
        RecordInfo info = filePlanPage.getRecordInfo(1);
        // Perform Complete record Action
        info.clickCompleteRecord();
        // Verfify whether the banner incomplete disappears
        Assert.isTrue(info.verifyCompleteRecord());
        ShareUtil.logout(drone);
    }

    @Test
    public void RMA_2666() throws Exception
    {
        verifyNERecordactions();
        verifyNERecordDetails();
        verifyERecordactions();
        verifyEditMetaData();
        verifyNERecordcopy();
        verifyNERecordCopyLogs();
        addtoHold();
        deleteHold();
        deleteRecord();
        moveRecord();
        linkRecord();
        verifyNEActionsAfterDeleteHold();
        completeNERecord();
        RequestInformation();
        completeERecord();
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