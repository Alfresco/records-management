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
package org.alfresco.rm.functional.copymovetoruletests;

import org.alfresco.po.share.util.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Test the record copy to rule within the unfiled records container
 *
 * @author Mark Hibbins
 * @version 2.2
 */
@Listeners(FailedTestListener.class)
public class UnfiledRecordCopyToRuleIntTest extends AbstractUnfiledCopyMoveRuleIntTestBase
{
    private final static String RULE_TITLE = "Copy to test rule";
    private final static String TARGET_FOLDER_NAME = "target";
    private final static String TARGET_FOLDER_PATH = "/target";
    private final static String TEST_RECORD_NAME = "testRecord";

    /**
     * Test the record copy to rule within the unfiled records container
     */
    @Test
    public void unfiledRecordsCopyToRecordTest()
    {
        navigateToUnfiledRecords();
        createUnfiledFolder(TARGET_FOLDER_NAME);
        createRule(RULE_TITLE, RuleType.COPY_TO, TARGET_FOLDER_PATH, true);
        createContentAndDeclareAsRecord(TEST_RECORD_NAME);
        navigateToUnfiledRecords();
        Assert.assertEquals(unfiledRecordsContainer.getFiles().size(), 2);
        navigateToPath("/target");
        Assert.assertEquals(unfiledRecordsContainer.getFiles().size(), 1);
    }
}