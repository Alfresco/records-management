package org.alfresco.rm.functionaltests;

import java.io.IOException;
import java.util.List;

import org.alfresco.po.RMUtils;
import org.alfresco.po.rm.CreateNewFolderForm;
import org.alfresco.po.rm.FilePlanNavigation;
import org.alfresco.po.rm.FilePlanPage;
import org.alfresco.po.rm.RMDashBoardPage;
import org.alfresco.po.rm.RMUploadFilePage;
import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.RMCreateSitePage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.SiteType;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.po.util.FailedTestListener;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * This test suite tests the following new features:
 *
 *  - Create a folder in the unfiled records container (also sub folders)
 *  - File a record into the unfiled records container (directly to the root or into folders in the container)
 *  - Change the meta data of a folder in the unfiled records container
 *  - View the details of a folder in the unfiled records container
 *  - Delete a record/folder in the unfiled records container
 *
 * @author Tuna Aksoy
 * @version 2.2
 */
@Listeners(FailedTestListener.class)
public class UnfiledRecordsContainerTest extends AbstractTest
{
    private static final String RM_SITE_NAME = "Records Management";
    private static final String RM_SITE_DESC = "Records Management Site";
    private static final String RM_SITE_URL = "rm";
    private static final String RM_UNFILED_RECORDS = "Unfiled Records";
    private static final String RM_UNFILED_RECORDS_CONTAINER_NAME = "Test folder name";
    private static final String RM_UNFILED_RECORDS_CONTAINER_TITLE = "Test folder title";
    private static final String RM_UNFILED_RECORDS_CONTAINER_DESC = "Test folder description";
    private static final By PROMPT_PANEL_ID = By.id("prompt");
    private static final By BUTTON_TAG_NAME = By.tagName("button");
    private static final By INPUT_TITLE_SELECTOR = By.cssSelector("input[id$='prop_cm_title']");
    private static final By INPUT_DESCRIPTION_SELECTOR = By.cssSelector("textarea[id$='prop_cm_description']");
    private RMDashBoardPage dashBoard;
    private FilePlanPage filePlanPage;

    @BeforeClass(groups={"RM","nonCloud"})
    public void setUp()
    {
        // Login to Share
        dashBoard = RMUtils.loginAs(drone, shareUrl, username, password).render();

        // Check if the RM Site already exists, if so delete it
        SiteFinderPage siteFinderPage = SiteUtil.searchSite(drone, RM_SITE_NAME).render();
        if (siteFinderPage.hasResults() == true)
        {
            siteFinderPage.deleteSite(RM_SITE_NAME).render();
        }

        // Click create site dialog
        RMCreateSitePage createSite = dashBoard.getRMNavigation().selectCreateSite().render();
        Assert.assertTrue(createSite.isCreateSiteDialogDisplayed());

        // Select RM Site
        createSite.selectSiteType(SiteType.RecordsManagement);
        Assert.assertEquals(createSite.getSiteName(), RM_SITE_NAME);
        Assert.assertEquals(createSite.getDescription(), RM_SITE_DESC);
        Assert.assertEquals(createSite.getSiteUrl(), RM_SITE_URL);

        // Create RM Site
        RMDashBoardPage site = ((RMDashBoardPage) createSite.createRMSite()).rmRender();
        Assert.assertNotNull(site);
        Assert.assertTrue(RM_SITE_NAME.equalsIgnoreCase(site.getPageTitle()));
        Assert.assertTrue(site.getRMSiteNavigation().isDashboardActive());
        Assert.assertFalse(site.getRMSiteNavigation().isFilePlanActive());
    }

    @AfterClass(groups={"RM","nonCloud"})
    public void teardown()
    {
        // Delete the RM Site
        SiteUtil.deleteSite(drone, RM_SITE_NAME);
        Assert.assertFalse(SiteUtil.searchSite(drone, RM_SITE_NAME).hasResults());
    }

    @Test
    public void navigateToUnfiledRecords()
    {
        drone.navigateTo(shareUrl + "/page/site/rm/documentlibrary");
        filePlanPage = drone.getCurrentPage().render();
        FilePlanNavigation nav = filePlanPage.getFilePlanNavigation();

        Assert.assertNotNull(nav);
        Assert.assertTrue(nav.isUnfiledRecordsVisible());
        filePlanPage = nav.selectUnfiledRecords().render();
        String current = filePlanPage.getFilePlanDescription();
        Assert.assertTrue(current.contains(RM_UNFILED_RECORDS));
    }

    @Test(dependsOnMethods="navigateToUnfiledRecords")
    public void fileRecord() throws IOException
    {
        Assert.assertTrue(filePlanPage.isUnfiledRecordsContainerFileDisplayed());
        RMUploadFilePage uploadForm = filePlanPage.selectCreateNewUnfiledRecordsContainerFile().render();

        WebElement prompt = drone.findAndWait(PROMPT_PANEL_ID);
        List<WebElement> elements = prompt.findElements(BUTTON_TAG_NAME);
        WebElement electronicRecordButton = uploadForm.findButton("Electronic", elements);
        electronicRecordButton.click();
        uploadForm.uploadFile(SiteUtil.prepareFile(Long.valueOf(System.currentTimeMillis()).toString()).getCanonicalPath()).render();
    }

    @Test(dependsOnMethods="fileRecord")
    public void createUnfiledRecordsFolder()
    {
        Assert.assertTrue(filePlanPage.isUnfiledRecordsContainerFolderDisplayed());
        CreateNewFolderForm form = filePlanPage.selectCreateNewUnfiledRecordsContainerFolder().render();
        Assert.assertNotNull(form.getRecordFolderId());
        form.enterName(RM_UNFILED_RECORDS_CONTAINER_NAME);
        form.enterTitle(RM_UNFILED_RECORDS_CONTAINER_TITLE);
        form.enterDescription(RM_UNFILED_RECORDS_CONTAINER_DESC);
        filePlanPage = form.selectSave().render();
        // FIXME: The render method for the "FilePlanPage" must be fixed!!!
        drone.waitFor(2000);
        Assert.assertTrue(filePlanPage.getFiles().size() == 2);
    }

    @Test(dependsOnMethods="createUnfiledRecordsFolder")
    public void fileRecordInUnfiledRecordsFolder() throws IOException
    {
        filePlanPage.getFileDirectoryInfo(RM_UNFILED_RECORDS_CONTAINER_NAME).clickOnTitle();
        fileRecord();
    }

    @Test(dependsOnMethods="fileRecordInUnfiledRecordsFolder")
    public void goBackToUnfiledRecordsContainerRoot()
    {
        // FIXME: The render method for the "FilePlanPage" must be fixed!!!
        filePlanPage.getFilePlanNavigation().selectUnfiledRecords().render();
        drone.waitFor(2000);
    }

    @Test(dependsOnMethods="goBackToUnfiledRecordsContainerRoot")
    public void editUnfiledRecordsFolderMetadata()
    {
        FileDirectoryInfo folder = filePlanPage.getFileDirectoryInfo(RM_UNFILED_RECORDS_CONTAINER_NAME);
        WebElement actions = folder.findElement(By.cssSelector("td:nth-of-type(5)"));
        drone.mouseOverOnElement(actions);
        WebElement editProperties = folder.findElement(By.cssSelector("div.rm-edit-details>a"));
        editProperties.click();

        // FIXME: Need to find out why it fails without waiting!!!
        drone.waitFor(2000);

        WebElement title = drone.find(INPUT_TITLE_SELECTOR);
        title.clear();
        title.sendKeys("My new title ABC");

        WebElement description = drone.find(INPUT_DESCRIPTION_SELECTOR);
        description.clear();
        description.sendKeys("My new description...");

        WebElement saveButton = drone.find(By.cssSelector("button[id$='form-submit-button']"));
        if(saveButton.isDisplayed())
        {
            saveButton.click();
        }
    }

    @Test(dependsOnMethods="editUnfiledRecordsFolderMetadata")
    public void clickUnfiledRecordsFolderDetails()
    {
        // FIXME: The render method for the "FilePlanPage" must be fixed!!!
        drone.waitFor(2000);
        filePlanPage = filePlanPage.render();

        FileDirectoryInfo folder = filePlanPage.getFileDirectoryInfo(RM_UNFILED_RECORDS_CONTAINER_NAME);
        WebElement actions = folder.findElement(By.cssSelector("td:nth-of-type(5)"));
        drone.mouseOverOnElement(actions);
        WebElement folderDetails = folder.findElement(By.cssSelector("div.rm-record-folder-view-details>a"));
        folderDetails.click();
    }

    @Test(dependsOnMethods={"clickUnfiledRecordsFolderDetails", "navigateToUnfiledRecords"})
    public void deleteFiledRecordAndFolder()
    {
        // FIXME: Need to find out why it fails without waiting!!!
        drone.waitFor(2000);

        for (FileDirectoryInfo fileDirectoryInfo : filePlanPage.getFiles())
        {
            fileDirectoryInfo.selectDelete();
            WebElement confirmDelete = drone.find(By.cssSelector("div#prompt div.ft span span button"));
            confirmDelete.click();
            // FIXME: Need to find out why it fails without waiting!!!
            drone.waitFor(2000);
        }
    }
}
