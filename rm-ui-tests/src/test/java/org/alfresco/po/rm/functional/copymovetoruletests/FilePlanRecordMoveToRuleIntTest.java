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

import org.alfresco.po.share.util.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Test the record move to rule within the file plan
 *
 * @author Mark Hibbins
 * @version 2.2
 */
@Listeners(FailedTestListener.class)
public class FilePlanRecordMoveToRuleIntTest extends AbstractFilePlanCopyMoveRuleIntTestBase
{
    private final static String RULE_TITLE = "Move to test rule";
    private final static String SOURCE_CATEGORY = "sourceCategory";
    private final static String SOURCE_FOLDER = "sourceFolder";
    private final static String SOURCE_FOLDER_PATH = "/" + SOURCE_CATEGORY + "/" + SOURCE_FOLDER;
    private final static String TARGET_CATEGORY = "targetCategory";
    private final static String TARGET_FOLDER = "targetFolder";
    private final static String TARGET_FOLDER_PATH = "/" + TARGET_CATEGORY + "/" + TARGET_FOLDER;
    private final static String TEST_RECORD_NAME = "testRecord";

    /**
     * Test the record copy to rule within the file plan
     */
    @Test
    public void filedRecordsMoveToRecordTest()
    {
        navigateToFilePlan();
        createSourceCategoryAndFolder(SOURCE_CATEGORY, SOURCE_FOLDER);
        createFilePlanRule(RULE_TITLE, RuleType.MOVE_TO, TARGET_FOLDER_PATH, true);
        fileRecordToPath(TEST_RECORD_NAME, SOURCE_FOLDER_PATH);
        navigateToFilePlan();
        filePlanPage = filePlanPage.selectCategory(SOURCE_CATEGORY, MAX_WAIT_TIME).render();
        filePlanPage = filePlanPage.selectFolder(SOURCE_FOLDER, MAX_WAIT_TIME).render();
        try
        {
            Assert.assertEquals(0, filePlanPage.getFiles().size());
        }
        catch(UnsupportedOperationException e)
        {
            if(!e.getMessage().startsWith("there are no elements"))
            {
                throw e;
            }
        }
        navigateToFilePlan();
        filePlanPage = filePlanPage.selectCategory(TARGET_CATEGORY, MAX_WAIT_TIME).render();
        filePlanPage = filePlanPage.selectFolder(TARGET_FOLDER, MAX_WAIT_TIME).render();
        Assert.assertEquals(1, filePlanPage.getFiles().size());
    }
}