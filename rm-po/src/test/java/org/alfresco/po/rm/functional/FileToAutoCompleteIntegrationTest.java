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

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.rm.FilePlanNavigation;
import org.alfresco.po.rm.FilePlanPage;
import org.alfresco.po.rm.RmManageRulesPage;
import org.alfresco.po.rm.RmRulesPage;
import org.alfresco.po.share.util.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * This test suite tests the following new features:
 *
 *  - Allow auto-completion suggestions to be presented to the user based on path fragments
 *
 * @author Mark Hibbins
 * @version 2.2
 */
@Listeners(FailedTestListener.class)
public class FileToAutoCompleteIntegrationTest extends AbstractIntegrationTest
{
    private final static long MAX_WAIT_TIME = 60000;

    private final static String FILE_TO_ACTION = "File to";

    @SuppressWarnings("unused")
    private static Log logger = LogFactory.getLog(FileToAutoCompleteIntegrationTest.class);

    private RmRulesPage rulesPage;
    private RmManageRulesPage manageRulesPage;

    @Test
    public void testAutoSuggestionDateFragment()
    {
        String fragment = "/da";
        String[] expectedSuggestions = {
                "Short Day (Mon)",
                "Long Day (Monday)",
                "Day Number (1)",
                "Short Month (Jan)",
                "Long Month (January)",
                "Month Number (01)",
                "Short Year (14)",
                "Long Year (2014)"
            };
        testAutoComplete(fragment, expectedSuggestions, expectedSuggestions[0], "/{date.day.short}");
    }

    @Test
    public void testAutoSuggestionDateMonthFragment()
    {
        String fragment = "/date.mon";
        String[] expectedSuggestions = {
                "Short Month (Jan)",
                "Long Month (January)",
                "Month Number (01)"
            };
        testAutoComplete(fragment, expectedSuggestions, expectedSuggestions[1], "/{date.month.long}");
    }

    @Test
    public void testAutoSuggestionMonthFragment()
    {
        String fragment = "/mon";
        String[] expectedSuggestions = {
                "Short Month (Jan)",
                "Long Month (January)",
                "Month Number (01)"
            };
        testAutoComplete(fragment, expectedSuggestions, expectedSuggestions[2], "/{date.month.number}");
    }

    private void testAutoComplete(String fragment, String[] expectedSuggestions, String suggestionToChoose, String expectedPath)
    {
        // FIXME!!! Click on the link rather than navigating
        drone.navigateTo(shareUrl + "/page/site/rm/documentlibrary");
        FilePlanPage filePlanPage = drone.getCurrentPage().render();
        FilePlanNavigation filePlanNavigation = filePlanPage.getFilePlanNavigation();
        filePlanPage = filePlanNavigation.selectUnfiledRecords().render();
        this.manageRulesPage = filePlanPage.selectUnfiledManageRules().render();
        this.rulesPage = manageRulesPage.selectCreateRules().render();
        rulesPage.selectPerformAction(FILE_TO_ACTION);

        // send the fragment key strokes
        WebElement input = drone.findAndWait(By.className("yui-ac-input"), MAX_WAIT_TIME);
        input.clear();
        for(int i = 0; i < fragment.length(); i++)
        {
            input.sendKeys("" + fragment.charAt(i));
        }

        // read the suggestions
        WebElement dropDownList = drone.findAndWait(By.xpath("//div[@class='yui-ac-bd']/child::ul"), MAX_WAIT_TIME);
        Assert.assertTrue(dropDownList.isDisplayed());
        WebElement lastExpectedListItem = drone.findAndWait(By.xpath("//div[@class='yui-ac-bd']/child::ul/child::li[.='" + expectedSuggestions[expectedSuggestions.length - 1] + "']"), MAX_WAIT_TIME);
        Assert.assertTrue(lastExpectedListItem.isDisplayed());
        List<WebElement> listItems = dropDownList.findElements(By.tagName("li"));
        List<String> suggestions = new ArrayList<String>();
        WebElement suggestionToSelect = null;
        for(WebElement listItem : listItems)
        {
            suggestions.add(listItem.getText());
            if(suggestionToChoose.equals(listItem.getText()))
            {
                suggestionToSelect = listItem;
            }
        }

        // ensure that the expected suggestions have been supplied
        for(String expectedSuggestion : expectedSuggestions)
        {
            Assert.assertTrue(suggestions.contains(expectedSuggestion));
        }

        // select the specified suggestion and ensure that the path field is populated correctly
        Assert.assertNotNull(suggestionToSelect);
        suggestionToSelect.click();
        String path = input.getAttribute("value");
        Assert.assertEquals(path, expectedPath);
    }


}