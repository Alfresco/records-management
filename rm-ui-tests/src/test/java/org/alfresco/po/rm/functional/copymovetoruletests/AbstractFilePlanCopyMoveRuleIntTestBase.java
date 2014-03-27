/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.po.rm.functional.copymovetoruletests;

import java.util.Date;

import org.alfresco.po.rm.RmActionSelectorEnterpImpl;
import org.alfresco.po.rm.RmCreateRulePage;
import org.alfresco.po.rm.RmFolderRulesPage;
import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.fileplan.filter.FilePlanFilter;
import org.alfresco.po.rm.fileplan.filter.unfiledrecords.UnfiledRecordsContainer;
import org.alfresco.po.rm.fileplan.toolbar.CreateNewRecordCategoryDialog;
import org.alfresco.po.rm.fileplan.toolbar.CreateNewRecordFolderDialog;
import org.alfresco.po.rm.functional.AbstractIntegrationTest;
import org.alfresco.po.rm.util.RmPageObjectUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

/**
 * Base class for the move to and copy to filed rules test classes
 *
 * @author Mark Hibbins
 * @version 2.2
 */
abstract class AbstractFilePlanCopyMoveRuleIntTestBase extends AbstractIntegrationTest
{
    protected final static long MAX_WAIT_TIME = 5000;
    private final static By NAVIGATION_MENU_FILE_PLAN = By.cssSelector("div#HEADER_SITE_DOCUMENTLIBRARY");

    protected FilePlanPage filePlanPage;
    protected UnfiledRecordsContainer unfiledRecordsContainer;

    protected enum RuleType
    {
        COPY_TO, MOVE_TO, FILE_TO
    }

    /**
     * Create a rule for the current file plan category/folder
     *
     * @param ruleTitle  Rule title
     * @param ruleType  Rule type
     * @param path  Path to copy/move to
     * @param create  If true then check the create path checkbox
     */
    protected void createFilePlanRule(String ruleTitle, RuleType ruleType, String path, boolean create)
    {
        RmFolderRulesPage manageRulesPage = filePlanPage.selectManageRules().render();
        createRule(manageRulesPage, ruleTitle, ruleType, path, create);
    }

    /**
     * Create a rule for the current unfiled records category/folder
     *
     * @param ruleTitle  Rule title
     * @param ruleType  Rule type
     * @param path  Path to copy/move to
     * @param create  If true then check the create path checkbox
     */
    protected void createUnfiledRule(String ruleTitle, RuleType ruleType, String path, boolean create)
    {
        navigateToUnfiledRecords();
        RmFolderRulesPage manageRulesPage = unfiledRecordsContainer.selectManageRules().render();
        createRule(manageRulesPage, ruleTitle, ruleType, path, create);
    }

    /**
     * Create a rule
     *
     * @param manageRulesPage  A handle on the current rules page
     * @param ruleTitle  Rule title
     * @param ruleType  Rule type
     * @param path  Path to copy/move to
     * @param create  If true then check the create path checkbox
     */
    private void createRule(RmFolderRulesPage manageRulesPage, String ruleTitle, RuleType ruleType, String path, boolean create)
    {
        RmCreateRulePage rulesPage = manageRulesPage.openCreateRulePage().render();
        rulesPage.fillNameField(ruleTitle);
        RmActionSelectorEnterpImpl actionSelectorEnter = rulesPage.getActionOptionsObj();
        switch(ruleType)
        {
            case COPY_TO:
                actionSelectorEnter.selectCopyTo(path, create);
                break;
            case MOVE_TO:
                actionSelectorEnter.selectMoveTo(path, create);
                break;
            case FILE_TO:
                actionSelectorEnter.selectFileTo(path, create);
                break;
        }
        rulesPage.clickCreate().render();
        drone.findAndWait(
                By.xpath(
                        "//div[@class='parameters']/"
                        + "child::span[contains(@class, 'paramname_path')]/"
                        + "child::span[.='" + path + "']"),
                        MAX_WAIT_TIME);
    }

    /**
     * Navigate to the file plan
     */
    protected void navigateToFilePlan()
    {
        RmPageObjectUtils.select(drone, NAVIGATION_MENU_FILE_PLAN);
        filePlanPage = drone.getCurrentPage().render();
        Assert.assertNotNull(filePlanPage);
    }

    /**
     * Create the specified category and folder
     *
     * @param category
     * @param folder
     */
    protected void createSourceCategoryAndFolder(String category, String folder)
    {
        navigateToFilePlan();
        CreateNewRecordCategoryDialog createCategoryDialog = filePlanPage.selectCreateNewCategory().render();
        Assert.assertNotNull(createCategoryDialog);
        Assert.assertNotNull(createCategoryDialog.getRecordCategoryId());
        createCategoryDialog.enterName(category);
        createCategoryDialog.enterTitle(category);
        createCategoryDialog.selectSave();
        filePlanPage = filePlanPage.selectCategory(category, MAX_WAIT_TIME).render();
        CreateNewRecordFolderDialog createFolderDialog = filePlanPage.selectCreateNewFolder().render();
        Assert.assertNotNull(createFolderDialog);
        Assert.assertNotNull(createFolderDialog.getRecordFolderId());
        createFolderDialog.enterName(folder);
        createFolderDialog.enterTitle(folder);
        createFolderDialog.selectSave();
        filePlanPage = filePlanPage.selectFolder(folder, MAX_WAIT_TIME).render();
    }

    /**
     * Create a record with the specified name and file it to the specified path using the file to rule
     *
     * @param recordName
     * @param path
     */
    protected void fileRecordToPath(String recordName, String path)
    {
        navigateToUnfiledRecords();
        createUnfiledRule("fileToRule", RuleType.FILE_TO, path, true);
        createContentAndDeclareAsRecord(recordName);
    }

    /**
     * Create new content in the sample site an declare it as a record in order to exercise the
     * rule we created in the previous step.
     *
     * @param testRecordName  Name of record to create (date/time appended to name)
     */
    protected void createContentAndDeclareAsRecord(String testRecordName)
    {
        drone.navigateTo(shareUrl + "/page/site/swsdp/documentlibrary");
        WebElement createFileAction = drone.findAndWait(By.xpath("//div[@class='create-content']/descendant::button[.='Create...']"), MAX_WAIT_TIME);
        createFileAction.click();
        WebElement createPlainTextFileAction = drone.findAndWait(By.xpath("//div[@class='create-content']/descendant::span[.='Plain Text...']"), MAX_WAIT_TIME);
        createPlainTextFileAction.click();
        WebElement fileNameField = drone.findAndWait(By.xpath("//input[@name='prop_cm_name']"), MAX_WAIT_TIME);
        fileNameField.clear();
        fileNameField.sendKeys(testRecordName + "-" + new Date().getTime());
        WebElement createButton = drone.findAndWait(By.xpath("//button[.='Create']"), MAX_WAIT_TIME);
        createButton.click();
        WebElement createRecordAction = drone.findAndWait(By.cssSelector("div.rm-create-record>a"), MAX_WAIT_TIME);
        createRecordAction.click();
        WebElement okButton = drone.findAndWait(By.xpath("//div[@class='ft']/descendant::button[.='OK']"), MAX_WAIT_TIME);
        okButton.click();
    }

    /**
     * Navigate to the unfiled records container
     */
    protected void navigateToUnfiledRecords()
    {
        RmPageObjectUtils.select(drone, NAVIGATION_MENU_FILE_PLAN);
        FilePlanPage filePlanPage = drone.getCurrentPage().render();
        Assert.assertNotNull(filePlanPage);
        FilePlanFilter filePlanFilter = filePlanPage.getFilePlanFilter();
        Assert.assertNotNull(filePlanFilter);
        Assert.assertTrue(filePlanFilter.isUnfiledRecordsContainerDisplayed());
        unfiledRecordsContainer = filePlanFilter.selectUnfiledRecordsContainer().render();
        Assert.assertNotNull(unfiledRecordsContainer);
    }
}