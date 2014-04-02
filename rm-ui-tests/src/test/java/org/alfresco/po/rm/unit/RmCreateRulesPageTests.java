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
package org.alfresco.po.rm.unit;

import org.alfresco.po.rm.*;
import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.functional.RmAbstractTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.alfresco.po.rm.RmCreateRulePage.ACTION_OPTIONS_SELECT;
import static org.alfresco.po.rm.RmCreateRulePage.CANCEL_BUTTON;
import static org.alfresco.po.rm.RmFolderRulesPage.LINK_BUTTON;
import static org.testng.Assert.*;

/**
 * @author Polina Lushchinskaya
 * @version 2.2
 */
public class RmCreateRulesPageTests extends RmAbstractTest
{
    private static final String TEST_CATEGORY = "Test Category";
    private static final String FOLDER_1 = "Folder 1";
    private static final String FOLDER_2 = "Folder 2";
    private static final String RULE_NAME = "Rule Name";

    @Override
    @BeforeClass(groups={"RM"})
    public void doSetup()
    {
        setup();
        FilePlanPage filePlan = openRmSite();
        filePlan.createCategory(TEST_CATEGORY, true);
        filePlan.navigateToFolder(TEST_CATEGORY);
        filePlan.createFolder(FOLDER_1);
        filePlan.createFolder(FOLDER_2);
    }

    @Test
    public void createRulePage(){
        FilePlanPage filePlan = new FilePlanPage(drone);
        RmFolderRulesPage manageRulesPage = filePlan.selectManageRules().render();
        RmCreateRulePage rulesPage = manageRulesPage.openCreateRulePage().render();
        assertTrue(isElementPresent(ACTION_OPTIONS_SELECT));
        assertTrue(isElementPresent(CANCEL_BUTTON));
        click(CANCEL_BUTTON);

    }

    @Test (dependsOnMethods = "createRulePage")
    public void verifyPageWithRule(){
        FilePlanPage filePlan = new FilePlanPage(drone);
        filePlan = (FilePlanPage) rmSiteDashBoard.selectFilePlan();
        filePlan.navigateToFolder(TEST_CATEGORY);
        filePlan.navigateToFolder(FOLDER_1);
        createRule(RULE_NAME, RmActionSelectorEnterpImpl.PerformActions.CLOSE_RECORD_FOLDER, WhenOption.INBOUND);
        //Verify page
        assertTrue(isElementPresent(RmFolderRulesWithRules.EDIT_BUTTON));
        assertTrue(isElementPresent(RmFolderRulesWithRules.DELETE_BUTTON));
        assertTrue(isElementPresent(RmFolderRulesWithRules.NEW_RULE_BUTTON));
        assertTrue(isElementPresent(RmFolderRulesWithRules.RULE_DETAILS_BLOCK));
        assertTrue(isElementPresent(RmFolderRulesWithRules.RULE_ITEMS));
    }

    @Test (dependsOnMethods = "verifyPageWithRule")
    public void verifyLinkToRule(){
        FilePlanPage filePlan = (FilePlanPage) rmSiteDashBoard.selectFilePlan();
        filePlan.navigateToFolder(TEST_CATEGORY);
        filePlan.navigateToFolder(FOLDER_2);
        RmFolderRulesPage manageRulesPage = filePlan.selectManageRules().render();

        manageRulesPage.openLinkToDialog();
        WebElement siteLink = drone.findAndWait(By.xpath("//div[contains(@id, 'sitePicker')]//span[contains(text(), 'Records Management')]"), MAX_WAIT_TIME);
        siteLink.click();
        WebElement categoryLink = drone.findAndWait(By.xpath("//span[contains(text(), '"+TEST_CATEGORY+"')]"), MAX_WAIT_TIME);
        categoryLink.click();
        WebElement folderLink = drone.findAndWait(By.xpath("//span[contains(text(), '"+FOLDER_1+"')]"), MAX_WAIT_TIME);
        folderLink.click();
        WebElement ruleLink = drone.findAndWait(By.xpath("//div[contains(@id, 'rulePicker')]//span[contains(text(), '"+RULE_NAME+"')]"), MAX_WAIT_TIME);
        ruleLink.click();
        drone.waitUntilElementClickable(LINK_BUTTON, MAX_WAIT_TIME);
        manageRulesPage.clickLink();
        RmLinkToRulePage linkToRulePage  = new RmLinkToRulePage(drone).render();
        assertTrue(isElementPresent(RmLinkToRulePage.UNLINK_RULE_BUTTON));
        assertTrue(isElementPresent(RmLinkToRulePage.VIEW_RULE_SET_BUTTON));
        assertTrue(isElementPresent(RmLinkToRulePage.CHANGE_BUTTON));
        assertTrue(isElementPresent(RmLinkToRulePage.RULE_ITEMS));
    }
}
