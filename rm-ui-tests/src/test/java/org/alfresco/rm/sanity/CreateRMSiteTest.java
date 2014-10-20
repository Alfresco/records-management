package org.alfresco.rm.sanity;

import static org.alfresco.po.rm.RmCreateSitePage.RMSiteCompliance.DOD5015;
import static org.alfresco.po.rm.RmCreateSitePage.RMSiteCompliance.STANDARD;
import static org.alfresco.po.rm.RmSiteType.RECORDS_MANAGEMENT;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.alfresco.po.rm.RmConsolePage;
import org.alfresco.po.rm.RmConsoleUsersAndGroups;
import org.alfresco.po.rm.RmCreateSitePage;
import org.alfresco.po.rm.RmCreateSitePage.RMSiteCompliance;
import org.alfresco.po.rm.RmSiteDashBoardPage;
import org.alfresco.po.rm.RmSiteNavigation;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.rm.util.RMTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Sanity test related to Create RM Site functionality.
 * 
 * @author Hema Amara
 */
@Listeners(RMTestListener.class)
public class CreateRMSiteTest extends AbstractSanityTest
{
    private static final String ADMIN_USER_NAME = "admin";
    protected static final String sitetype = "RecordsManagement";
    private static final String RM_SITE_NAME = "Records Management";
    protected static String password = "password";
    private RmCreateSitePage rmcreateSitePage;
    private RmSiteDashBoardPage rmSiteDashBoardPage;
    private static final String USER_NAME = "RMA_2664";

    /**
     * Part of the setup creates the Alfresco admin user.
     * Which will be used in RMA-2664 test.
     * 
     * @throws Exception
     */
    @BeforeClass
    public void setup() throws Exception
    {
        createEnterpriseUserWithAdminGroup(USER_NAME);
    }

    /**
     * Verifies different options available in {@link RmCreateSitePage}.
     */
    public void rmCreateSiteFormVerify() throws Exception
    {
        DashBoardPage dashBoardPage = ShareUtil.logInAs(drone, USER_NAME, password).render();
        
        rmcreateSitePage = dashBoardPage.getNav().selectCreateSite().render();

        rmcreateSitePage.selectSiteType(RECORDS_MANAGEMENT);

        // comparing create site form, site name value with RMsite n
        assertEquals(rmcreateSitePage.getSiteName(), RmCreateSitePage.RM_SITE_NAME);
        
        // comparing create site form, site url value with RMsite URL
        assertEquals(rmcreateSitePage.getSiteUrl(), RmCreateSitePage.RM_SITE_URL);
        
        // verifying site description
        Assert.assertEquals(rmcreateSitePage.getDescription(), RmCreateSitePage.RM_SITE_DESC);
        
        //Name and URL Name fields are disabled for editing
        assertTrue(rmcreateSitePage.isNameEditingDisaabled(), "RM Site Name should be disabled for editing");
        assertTrue(rmcreateSitePage.isUrlNameEditingDisaabled(), "RM Site URL Name should be disabled for editing");
        
        // verifying the visibility rmsite
        assertTrue(rmcreateSitePage.isPublic());
        
        // verifying whether site type has records management type selected
        assertTrue(rmcreateSitePage.isRecordManagementTypeSupported());
        
        // verify the complaince size and complainces in complainces section.
        List<RMSiteCompliance> compliances = rmcreateSitePage.getallRmsiteComplainces();
        assertEquals(compliances.size(), 2);
        assertEquals(compliances.get(0), STANDARD);
        assertEquals(compliances.get(1), DOD5015);
    }

    /**
     * Create Records Management Standard Site.
     * Verifies {@link RmSiteDashBoardPage} have a Site Dashboard, File Plan, Record Search, Management Console & Site Members.
     */
    public void rmDashBoardVerify() throws Exception
    {
        
        rmcreateSitePage.selectRMSiteCompliance(STANDARD);
        rmSiteDashBoardPage = rmcreateSitePage.selectOk().render();
        
        // get rmnavigation page
        RmSiteNavigation rmSiteNavigation = rmSiteDashBoardPage.getRMSiteNavigation();

        // verify fileplage,sitemembers,recordsearch and recordsmanagement cosnole visibility
        assertTrue(rmSiteNavigation.isDashboardDisplayed());
        assertTrue(rmSiteNavigation.isFilePlanDisplayed());
        assertTrue(rmSiteNavigation.isRecordSearchDisplayed());
        assertTrue(rmSiteNavigation.isRmConsolePageDisplayed());
        assertTrue(rmSiteNavigation.isSelectSiteMembersDisplayed());
    }

    /** 
     * Render the {@link RmConsoleUsersAndGroups} Page.
     * Verifies User in RM User and Groups Page.
     */
    public void rmUsersGroupsVerify() throws Exception
    {
        RmConsolePage rcp = rmSiteDashBoardPage.getRMSiteNavigation().selectRMConsole().render();
        assertNotNull(rcp.openUsersAndGroupsPage().render());
        RmConsoleUsersAndGroups consoleUsersAndGroups = rcp.openUsersAndGroupsPage().render();
        List<ShareLink> userlist = consoleUsersAndGroups.getUSerlist();
        assertTrue(userlist.size() >= 2, "Minimum 2 user should be there");
        userlist.contains(ADMIN_USER_NAME);
        userlist.contains(USER_NAME);
        ShareUtil.logout(drone);
    }

    @Test
    public void RMA_2664() throws Exception
    {
        rmCreateSiteFormVerify();
        rmDashBoardVerify();
        rmUsersGroupsVerify();
    }
    
    /**
     * Delete the RM site which is created part of the Test.
     * Also delete the user created part for RMA-2664 test.
     * 
     * @throws Exception
     */
    @AfterClass
    public void tearDown() throws Exception
    {
        ShareUtil.logInAs(drone, USER_NAME, password).render();
        SiteUtil.deleteSite(drone, RM_SITE_NAME);
        ShareUtil.logout(drone);
        deleteUser(USER_NAME);
    }
}
