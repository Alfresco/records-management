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

import org.alfresco.po.rm.RmFactoryPage;

/**
 * Test class for {@link RmFactoryPage}
 *
 * @author Tuna Aksoy
 * @since 2.2
 */
public class RmFactoryPageTest
{
//    private static Log logger = LogFactory.getLog(RmFactoryPageTest.class);
//    private final String BASE_URL = getBaseUrl();
//    private final String RM_DASHBOARD = "rmdashboard";
//    private final String RM_DASHBOARD_URL = BASE_URL + "/page/site/rm/dashboard";
//    private final String RM_SITE_MEMBERS = "rmSiteMembers";
//    private final String RM_SITE_MEMBERS_URL = BASE_URL + "/page/site/rm/site-members";
//    private final String FILE_PLAN = "filePlan";
//    private final String FILE_PLAN_URL = BASE_URL + "/page/site/rm/documentlibrary";
//    private final String RM_SEARCH = "rmsearch";
//    private final String RM_SEARCH_URL = BASE_URL + "/page/site/rm/rmsearch";
//    private final String RM_CONSOLE = "rmConsole";
//    private final String RM_CONSOLE_URL = BASE_URL + "/page/console/rm-console/";
//
//    @Test(groups={"unit"})
//    public void resolveUrls()
//    {
//        ShareProperties properties = new ShareProperties(AlfrescoVersion.Enterprise42.toString());
//        WebDrone drone = new WebDroneImpl(new HtmlUnitDriver(), properties);
//
//        try
//        {
//            long start = System.currentTimeMillis();
//
//            SharePage page = resolvePage(RM_DASHBOARD_URL, RM_DASHBOARD, drone);
//            Assert.assertTrue(page instanceof RmDashBoardPage);
//            Assert.assertFalse(page instanceof FilePlanPage);
//
//            page = resolvePage(RM_SITE_MEMBERS_URL, RM_SITE_MEMBERS, drone);
//            Assert.assertTrue(page instanceof RmSiteMembersPage);
//
//            page = resolvePage(FILE_PLAN_URL, FILE_PLAN, drone);
//            Assert.assertTrue(page instanceof FilePlanPage);
//
//            page = resolvePage(RM_SEARCH_URL, RM_SEARCH,  drone);
//            Assert.assertTrue(page instanceof RecordSearchPage);
//
//            page = resolvePage(RM_CONSOLE_URL, RM_CONSOLE, drone);
//            Assert.assertTrue(page instanceof RmConsolePage);
//
//            long duration = System.currentTimeMillis() - start;
//            logger.info("Total duration of test in milliseconds: " + duration);
//        }
//        finally
//        {
//            drone.quit();
//        }
//    }
//
//    /**
//     * Gets the base URL
//     *
//     * @return {@link String} The base URL
//     */
//    private String getBaseUrl()
//    {
//        Properties prop = new Properties();
//        ClassLoader loader = Thread.currentThread().getContextClassLoader();
//        try
//        {
//            prop.load(loader.getResourceAsStream("sharepo.properties"));
//            prop.load(loader.getResourceAsStream("sharepo.hybrid.properties"));
//            prop.load(loader.getResourceAsStream("test.properties"));
//            prop.load(loader.getResourceAsStream("module.properties"));
//        }
//        catch (IOException error)
//        {
//            logger.info("Cannot load properties files: '" + "");
//        }
//        String shareTarget = prop.getProperty("share.target");
//        return StrSubstitutor.replace(shareTarget, prop);
//    }
//
//    /**
//     * Wrapper to measure time of operation.
//     */
//    private SharePage resolvePage(final String url, final String name, WebDrone drone)
//    {
//        long startProcess = System.currentTimeMillis();
//        SharePage page = RmFactoryPage.getPage(url, drone);
//        long endProcess = System.currentTimeMillis() - startProcess;
//        logger.info(String.format("The page %s returned in %d", name, endProcess));
//        return page;
//    }
}