package org.alfresco.rm.pageobjecttests;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.ManageRulesPage;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.RulesPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.po.util.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class ManageRulesPageTest extends AbstractTest
{
    private String siteName;
    private String folderName;
    @SuppressWarnings("unused")
    @BeforeClass
    private void prepare() throws Exception
    {
        siteName = "rules-" + System.currentTimeMillis();
        folderName = "michaels-test";
        ShareUtil.loginAs(drone, shareUrl, username, password).render();
        SiteUtil.createSite(drone, siteName, "description", "Public");
        SiteDashboardPage page = drone.getCurrentPage().render();
        DocumentLibraryPage documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        newFolderPage.createNewFolder(folderName).render();
    }
    
    @Test
    public void testPage()
    {
        ManageRulesPage page = new ManageRulesPage(drone);
        Assert.assertNotNull(page);
    }
    
    @Test(expectedExceptions=IllegalArgumentException.class)
    public void testPageWithNull()
    {
        ManageRulesPage page = new ManageRulesPage(null);
        Assert.assertNotNull(page);
    }
    
    @Test
    public void createNewRule()
    {
        drone.navigateTo(shareUrl + String.format("/page/site/%s/documentlibrary",siteName));
        DocumentLibraryPage libPage = drone.getCurrentPage().render();
        FileDirectoryInfo row = libPage.getFileDirectoryInfo(folderName);
        Assert.assertNotNull(row);
        ManageRulesPage manageRules = row.selectManageRules().render();
        Assert.assertNotNull(manageRules);
        RulesPage rulesPage = manageRules.selectCreateRules().render();
        Assert.assertNotNull(rulesPage);
        
        //check cancel works
        manageRules = rulesPage.selectCancel().render();
        rulesPage = manageRules.selectCreateRules().render();
        
        //Complete form
        rulesPage.selectPerformAction("Declare as record");
        rulesPage.enterTitle("m1");
        rulesPage.enterDescription("michael first rule");
        ManageRulesPage manageRulesUpdated = rulesPage.selectCreate().render();
        Assert.assertNotNull(manageRulesUpdated);
    }
}
