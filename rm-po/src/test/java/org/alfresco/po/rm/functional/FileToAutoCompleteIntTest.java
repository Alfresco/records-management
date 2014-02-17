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
import org.alfresco.po.rm.RmActionSelectorEnterpImpl;
import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.po.share.site.contentrule.createrules.CreateRulePage;
import org.alfresco.po.share.util.FailedTestListener;
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
public class FileToAutoCompleteIntTest extends AbstractIntegrationTest
{
    private final static long MAX_WAIT_TIME = 60000;

    /**
     * Test the auto suggestion functionality for the fragment '/da'.
     *
     * The expected results are that we should see all of the date suggestions.
     */
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

    /**
     * Test the auto suggestion functionality for the fragment '/date.mon'.
     *
     * The expected results are that we should see all of the date.month suggestions.
     */
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

    /**
     * Test the auto suggestion functionality for the fragment '/mon'.
     *
     * The expected results are that we should see all of the date.month suggestions.
     */
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

    /**
     * Test the auto suggestion functionality for the given fragment, expected suggestions, the suggestion to choose
     * from the provided suggestions and the final value of the path once this value is chosen.
     *
     * @param fragment             The fragment to initially type in to the path input (e.g. '/mon')
     * @param expectedSuggestions  A list of expected suggestions as they would appear to the user
     * @param suggestionToChoose   The suggestion to choose from the provided suggestions
     * @param expectedPath         The expected path value once the suggestion has been chosen
     */
    private void testAutoComplete(String fragment, String[] expectedSuggestions, String suggestionToChoose, String expectedPath)
    {
        // FIXME!!! Click on the link rather than navigating
        drone.navigateTo(shareUrl + "/page/site/rm/documentlibrary");
        FilePlanPage filePlanPage = drone.getCurrentPage().render();
        FilePlanNavigation filePlanNavigation = filePlanPage.getFilePlanNavigation();
        filePlanPage = filePlanNavigation.selectUnfiledRecords().render();
        FolderRulesPage manageRulesPage = filePlanPage.selectUnfiledManageRules().render();
        CreateRulePage createRulePage = manageRulesPage.openCreateRulePage().render();
        RmActionSelectorEnterpImpl actionSelectorEnter = (RmActionSelectorEnterpImpl) createRulePage.getActionOptionsObj();
        actionSelectorEnter.selectFileTo();

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