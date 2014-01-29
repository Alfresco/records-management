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
package org.alfresco.rm;

import org.alfresco.po.RMFactoryPage;
import org.alfresco.po.rm.FilePlanPage;
import org.alfresco.po.rm.RMConsolePage;
import org.alfresco.po.rm.RMDashBoardPage;
import org.alfresco.po.rm.RMSiteMembersPage;
import org.alfresco.po.rm.RecordSearchPage;
import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.ShareProperties;
import org.alfresco.po.share.site.ManageRulesPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test class for {@link RMFactoryPage}
 *
 * @author Tuna Aksoy
 * @since 2.2
 */
public class RMFactoryPageTest
{
    private final String baseUrl = "http://localhost:8081/share";
    private static Log logger = LogFactory.getLog(RMFactoryPageTest.class);
    private final String rmManageRules = baseUrl + "%s/page/site/rm/folder-rules?nodeRef=workspace://SpacesStore/2ce53134-4677-43c7-9816-a25b4a61cae4";
    private final String rmdashboard = baseUrl + "%s/page/site/rm/dashboard";
    private final String rmSiteMembers = baseUrl + "%s/page/site/rm/site-members";
    private final String filePlan = baseUrl + "%s/page/site/rm/documentlibrary";
    private final String rmsearch = baseUrl + "%s/page/site/rm/rmsearch";
    private final String rmConsole = baseUrl + "%s/page/console/rm-console/";

    @Test(groups={"unit"})
    public void resolveUrls()
    {
        WebDrone drone = new WebDroneImpl(new HtmlUnitDriver(), new ShareProperties(AlfrescoVersion.Enterprise42.toString()));

        try
        {
            long start = System.currentTimeMillis();

            SharePage page = resolvePage(rmManageRules, "rmManageRulesPage", drone);
            Assert.assertTrue(page instanceof ManageRulesPage);

            page = resolvePage(rmdashboard, "rmdashboard", drone);
            Assert.assertTrue(page instanceof RMDashBoardPage);
            Assert.assertFalse(page instanceof FilePlanPage);

            page = resolvePage(rmSiteMembers, "rmSiteMembers", drone);
            Assert.assertTrue(page instanceof RMSiteMembersPage);

            page = resolvePage(filePlan, "filePlan", drone);
            Assert.assertTrue(page instanceof FilePlanPage);

            page = resolvePage(rmsearch, "rmsearch",  drone);
            Assert.assertTrue(page instanceof RecordSearchPage);

            page = resolvePage(rmConsole, "rmConsole", drone);
            Assert.assertTrue(page instanceof RMConsolePage);

            long duration = System.currentTimeMillis() - start;
            logger.info("Total duration of test in milliseconds: " + duration);
        }
        finally
        {
            drone.quit();
        }
    }

    /**
     * Wrapper to measure time of operation.
     */
    private SharePage resolvePage(final String url, final String name, WebDrone drone)
    {
        long startProcess = System.currentTimeMillis();
        SharePage page = FactorySharePage.getPage(url, drone);
        long endProcess = System.currentTimeMillis() - startProcess;
        logger.info(String.format("The page %s returned in %d", name, endProcess));
        return page;
    }
}