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
package org.alfresco.po.rm.functional;

import java.util.Date;
import java.util.List;

import org.alfresco.po.rm.RmActionSelectorEnterpImpl;
import org.alfresco.po.rm.RmCreateRulePage;
import org.alfresco.po.rm.RmFolderRulesPage;
import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.fileplan.filter.FilePlanFilter;
import org.alfresco.po.rm.fileplan.filter.unfiledrecords.UnfiledRecordsContainer;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.util.FailedTestListener;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * This test suite tests the following new features:
 *
 *  - Create a file to rule that creates a full path that doesn't currently exists
 *
 * @author Mark Hibbins
 * @version 2.2
 */
@Listeners(FailedTestListener.class)
public class FileToCreatePathIntTest extends AbstractIntegrationTest
{
    private final static long MAX_WAIT_TIME = 60000;
    private final static String RULE_TITLE = "Path creation test rule";
    private final static String FILE_TO_PATH = "/one/two/three";

    /**
     * This test creates a rule that uses file to to file a new record to a non-existent path.
     */
    @Test
    public void createAndTestFileToRuleThatCreatesAPath()
    {
        createRule();
        createContentAndDeclareAsRecord();
        ensureSampleDocumentHasBeenFiledCorrectly();
    }

    /**
     * Create the test file-to rule
     */
    private void createRule()
    {
        FilePlanPage filePlanPage = rmSiteDashBoard.selectFilePlan().render();
        FilePlanFilter filePlanFilter = filePlanPage.getFilePlanFilter();
        UnfiledRecordsContainer unfiledRecordsContainer = filePlanFilter.selectUnfiledRecordsContainer().render();
        RmFolderRulesPage manageRulesPage = unfiledRecordsContainer.selectManageRules().render();
        RmCreateRulePage rulesPage = manageRulesPage.openCreateRulePage().render();
        rulesPage.fillNameField(RULE_TITLE);
        RmActionSelectorEnterpImpl actionSelectorEnter = rulesPage.getActionOptionsObj();
        actionSelectorEnter.selectFileTo(FILE_TO_PATH, true);
        rulesPage.clickCreate().render();
        drone.findAndWait(
                By.xpath(
                        "//div[@class='parameters']/"
                        + "child::span[contains(@class, 'paramname_path')]/"
                        + "child::span[.='" + FILE_TO_PATH + "']"),
                        MAX_WAIT_TIME);
    }

    /**
     * Create new content in the sample site an declare it as a record in order to exercise the
     * rule we created in the previous step.
     */
    private void createContentAndDeclareAsRecord()
    {
        drone.navigateTo(shareUrl + "/page/site/swsdp/documentlibrary");
        WebElement createFileAction = drone.findAndWait(By.xpath("//div[@class='create-content']/descendant::button[.='Create...']"), MAX_WAIT_TIME);
        createFileAction.click();
        WebElement createPlainTextFileAction = drone.findAndWait(By.xpath("//div[@class='create-content']/descendant::span[.='Plain Text...']"), MAX_WAIT_TIME);
        createPlainTextFileAction.click();
        WebElement fileNameField = drone.findAndWait(By.xpath("//input[@name='prop_cm_name']"), MAX_WAIT_TIME);
        fileNameField.clear();
        fileNameField.sendKeys("fileToTestFile-" + new Date().getTime());
        WebElement createButton = drone.findAndWait(By.xpath("//button[.='Create']"), MAX_WAIT_TIME);
        createButton.click();
        WebElement createRecordAction = drone.findAndWait(By.cssSelector("div.rm-create-record>a"), MAX_WAIT_TIME);
        createRecordAction.click();
        WebElement okButton = drone.findAndWait(By.xpath("//div[@class='ft']/descendant::button[.='OK']"), MAX_WAIT_TIME);
        okButton.click();
    }

    /**
     * Ensure that the document we created has been filed in the the specified path correctly.
     */
    private void ensureSampleDocumentHasBeenFiledCorrectly()
    {
        drone.navigateTo(shareUrl + "/page/site/rm/documentlibrary");
        FilePlanPage filePlanPage = drone.getCurrentPage().render();
        FilePlanPage one = filePlanPage.selectCategory("one", MAX_WAIT_TIME).render();
        FilePlanPage two = one.selectCategory("two", MAX_WAIT_TIME).render();
        FilePlanPage three = two.selectFolder("three", MAX_WAIT_TIME).render();
        List<FileDirectoryInfo> files = three.getFiles();
        Assert.assertEquals(1, files.size());
    }

}