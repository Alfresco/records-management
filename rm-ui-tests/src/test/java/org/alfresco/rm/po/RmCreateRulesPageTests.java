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

import org.alfresco.po.rm.RmCreateRulePage;
import org.alfresco.po.rm.RmFolderRulesPage;
import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.rm.common.AbstractRecordsManagementTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Polina Lushchinskaya
 * @version 2.2
 */
public class RmCreateRulesPageTests extends AbstractRecordsManagementTest
{
    /** Generate names */
    private String categoryName = generateNameFromClass();
    private String folderName = generateNameFromClass();
    private String folderName2 = "second-" + generateNameFromClass();
    @BeforeClass
    public void navigateToConsole()
    {
        FilePlanPage filePlanPage = rmSiteDashBoard.selectFilePlan().render();
        filePlanPage = filePlanPage.createCategory(categoryName, true).render();
        filePlanPage = filePlanPage.navigateToFolder(categoryName).render();
        filePlanPage = filePlanPage.createFolder(folderName).render();
        filePlanPage = filePlanPage.createFolder(folderName2).render();
    }

    @Test
    public void createRulePage()
    {
        FilePlanPage filePlan = new FilePlanPage(drone);
        RmFolderRulesPage manageRulesPage = filePlan.selectManageRules().render();
        RmCreateRulePage rulePage = manageRulesPage.openCreateRulePage().render();
        Assert.assertNotNull(rulePage);
        //TODO FIXME create a rule and assert it was created.
    }
//TODO fix me, i should be in a page object.
//    @Test (dependsOnMethods = "createRulePage")
//    public void verifyPageWithRule()
//    {
//        FilePlanPage filePlan = new FilePlanPage(drone);
//        filePlan = (FilePlanPage) rmSiteDashBoard.selectFilePlan();
//        filePlan.navigateToFolder(TEST_CATEGORY);
//        filePlan.navigateToFolder(FOLDER_1);
//        createRule(RULE_NAME, RmActionSelectorEnterpImpl.PerformActions.CLOSE_RECORD_FOLDER, WhenOption.INBOUND);
//        //Verify page
//        assertTrue(isElementPresent(RmFolderRulesWithRules.EDIT_BUTTON));
//        assertTrue(isElementPresent(RmFolderRulesWithRules.DELETE_BUTTON));
//        assertTrue(isElementPresent(RmFolderRulesWithRules.NEW_RULE_BUTTON));
//        assertTrue(isElementPresent(RmFolderRulesWithRules.RULE_DETAILS_BLOCK));
//        assertTrue(isElementPresent(RmFolderRulesWithRules.RULE_ITEMS));
//    }
//TODO fix me, i should be in a page object.
//    @Test (dependsOnMethods = "verifyPageWithRule")
//    public void verifyLinkToRule()
//    {
//        FilePlanPage filePlan = (FilePlanPage) rmSiteDashBoard.selectFilePlan();
//        filePlan.navigateToFolder(TEST_CATEGORY);
//        filePlan.navigateToFolder(FOLDER_2);
//        RmFolderRulesPage manageRulesPage = filePlan.selectManageRules().render();
//
//        manageRulesPage.openLinkToDialog();
//        WebElement siteLink = drone.findAndWait(SITE_RM_PICKER,  MAX_WAIT_TIME);
//        siteLink.click();
//        WebElement categoryLink = drone.findAndWait(commonLink(TEST_CATEGORY), MAX_WAIT_TIME);
//        categoryLink.click();
//        WebElement folderLink = drone.findAndWait(commonLink(FOLDER_1), MAX_WAIT_TIME);
//        folderLink.click();
//        WebElement ruleLink = drone.findAndWait(By.xpath(ruleLinkLocator(RULE_NAME)), MAX_WAIT_TIME);
//        ruleLink.click();
//        drone.waitUntilElementClickable(LINK_BUTTON, MAX_WAIT_TIME);
//        manageRulesPage.clickLink();
//        new RmLinkToRulePage(drone).render();
//        assertTrue(isElementPresent(RmLinkToRulePage.UNLINK_RULE_BUTTON));
//        assertTrue(isElementPresent(RmLinkToRulePage.VIEW_RULE_SET_BUTTON));
//        assertTrue(isElementPresent(RmLinkToRulePage.CHANGE_BUTTON));
//        assertTrue(isElementPresent(RmLinkToRulePage.RULE_ITEMS));
//    }
}
