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
package org.alfresco.po.rm.functional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.alfresco.po.rm.RmActionSelectorEnterpImpl;
import org.alfresco.po.rm.RmActionSelectorEnterpImpl.PerformActions;
import org.alfresco.po.rm.common.AbstractIntegrationTest;
import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.fileplan.filter.FilePlanFilter;
import org.alfresco.po.rm.fileplan.filter.unfiledrecords.UnfiledRecordsContainer;
import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.po.share.site.contentrule.createrules.CreateRulePage;
import org.alfresco.po.share.util.FailedTestListener;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
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

    @Test
    public void runAutoCompleteTests()
    {
        testAutoSuggestionDateMonthFragment();
        testAutoSuggestionMonthFragment();
        testAutoSuggestionDateFragment();
        testAutoSuggestionTitleFragment();
    }

    /**
     * Test the auto suggestion functionality for the fragment '/date.mon'.
     *
     * The expected results are that we should see all of the date.month suggestions.
     */
    public void testAutoSuggestionDateMonthFragment()
    {
        String fragment = "/date.mon";
        String[] expectedSuggestions = {
                "Short Month",
                "Month",
                "Long Month",
                "Month Number"
            };
        testAutoComplete(fragment, expectedSuggestions, expectedSuggestions[2], "/{date.month.long}");
    }

    /**
     * Test the auto suggestion functionality for the fragment '/mon'.
     *
     * The expected results are that we should see all of the date.month suggestions.
     */
    public void testAutoSuggestionMonthFragment()
    {
        String fragment = "/mon";
        String[] expectedSuggestions = {
                "Short Month",
                "Month",
                "Long Month",
                "Month Number"
            };
        testAutoComplete(fragment, expectedSuggestions, expectedSuggestions[3], "/{date.month.number}");
    }

    /**
     * Test the auto suggestion functionality for the fragment '/da'.
     *
     * The expected results are that we should see all of the date suggestions along with a node
     * suggestion for date filed.
     */
    public void testAutoSuggestionDateFragment()
    {
        String fragment = "/da";
        String[] expectedSuggestions = {
                "Date filed",
                "Short Day",
                "Day",
                "Long Day",
                "Day Number",
                "Day of Year"
            };
        testAutoComplete(fragment, expectedSuggestions, expectedSuggestions[1], "/{date.day.short}");
    }

    /**
     * Test the auto suggestion functionality for the fragment '/title'.
     *
     * The expected results are that we should see all of the date suggestions.
     */
    public void testAutoSuggestionTitleFragment()
    {
        String fragment = "/title";
        String[] expectedSuggestions = {
                "Title"
            };
        testAutoComplete(fragment, expectedSuggestions, expectedSuggestions[0], "/{node.cm:title}");
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
        FilePlanPage filePlanPage = rmSiteDashBoard.selectFilePlan().render();
        FilePlanFilter filePlanFilter = filePlanPage.getFilePlanFilter();
        UnfiledRecordsContainer unfiledRecordsContainer = filePlanFilter.selectUnfiledRecordsContainer().render();
        FolderRulesPage manageRulesPage = unfiledRecordsContainer.selectManageRules().render();
        CreateRulePage createRulePage = manageRulesPage.openCreateRulePage().render();
        RmActionSelectorEnterpImpl actionSelectorEnter = (RmActionSelectorEnterpImpl) createRulePage.getActionOptionsObj();
        actionSelectorEnter.selectAction(PerformActions.FILE_TO);

        // send the fragment key strokes
        WebElement input = drone.findAndWait(By.className("yui-ac-input"), MAX_WAIT_TIME);
        input.clear();
        input.sendKeys(fragment);

        // check the resulting suggestions
        WebElement suggestionToSelect = checkResultingSuggestions(expectedSuggestions, suggestionToChoose, 30000);

        // select the specified suggestion and ensure that the path field is populated correctly
        Assert.assertNotNull(suggestionToSelect);
        suggestionToSelect.click();
        String path = input.getAttribute("value");
        Assert.assertEquals(path, expectedPath);
    }

    private WebElement checkResultingSuggestions(String[] expectedSuggestions, String suggestionToChoose, long timeout)
    {
        long start = new Date().getTime();
        boolean timedOut = false;
        WebElement suggestionToSelect = null;
        boolean foundExpectedSuggestions = false;
        List<String> actualSuggestions = null;
        do
        {
            try
            {
                suggestionToSelect = null;
                foundExpectedSuggestions = false;

                // read the suggestions
                WebElement dropDownList = drone.findAndWait(By.xpath("//div[@class='yui-ac-bd']/child::ul"), MAX_WAIT_TIME);
                if(dropDownList.isDisplayed())
                {
                    WebElement expectedListItem = drone.findAndWait(By.className("substitutionSuggestion"), MAX_WAIT_TIME);
                    if(expectedListItem.isDisplayed())
                    {
                        List<WebElement> listItems = dropDownList.findElements(By.className("substitutionSuggestion"));
                        actualSuggestions = new ArrayList<String>();

                        for(WebElement listItem : listItems)
                        {
                            actualSuggestions.add(listItem.getText());
                            if(listItem.getText().startsWith(suggestionToChoose))
                            {
                                suggestionToSelect = listItem;
                            }
                        }

                        // ensure that the expected suggestions have been supplied
                        boolean foundAllSuggestions = true;
                        for(String expectedSuggestion : expectedSuggestions)
                        {
                            boolean foundExpectedSuggestion = false;
                            for(String suggestion : actualSuggestions)
                            {
                                if(suggestion.startsWith(expectedSuggestion))
                                {
                                    foundExpectedSuggestion = true;
                                    break;
                                }
                            }
                            if(!foundExpectedSuggestion)
                            {
                                foundAllSuggestions = false;
                                break;
                            }
                        }
                        foundExpectedSuggestions = foundAllSuggestions;
                    }
                }
            }
            catch(StaleElementReferenceException e) { }

            if((suggestionToSelect == null) || (!foundExpectedSuggestions))
            {
                long now = new Date().getTime();
                if((now - start) > timeout)
                {
                    timedOut = true;
                }
                else
                {
                    try { Thread.sleep(1000); } catch(InterruptedException e) { }
                }
            }
        } while(!timedOut && !foundExpectedSuggestions);

        if(timedOut)
        {
            StringBuffer message = new StringBuffer();
            message.append("autocomplete results not as expected.\nExpected... ");
            for(String expectedSuggestion : expectedSuggestions)
            {
                message.append(expectedSuggestion);
                message.append(",");
            }
            message.append("\nActual...   ");
            if(actualSuggestions != null)
            {
                for(String actualSuggestion: actualSuggestions)
                {
                    message.append(actualSuggestion);
                    message.append(",");
                }
            }
            Assert.fail(message.toString());
        }
        return suggestionToSelect;
    }
}