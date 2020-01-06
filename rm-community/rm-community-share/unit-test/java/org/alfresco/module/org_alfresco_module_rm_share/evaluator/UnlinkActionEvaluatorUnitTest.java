/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2020 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software.
 * -
 * If the software was purchased under a paid Alfresco license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 * -
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * -
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * -
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

package org.alfresco.module.org_alfresco_module_rm_share.evaluator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.alfresco.test.BaseUnitTest;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;

/**
 * Unlink action evaluator unit test
 * 
 * @author Roy Wetherall
 * @since  2.3
 */
public class UnlinkActionEvaluatorUnitTest extends BaseUnitTest
{
    /** unlink action evaluator */
    private UnlinkActionEvaluator evaluator = new UnlinkActionEvaluator();
    
    /**
     * Given that the "linked" indicator is not present
     * When evaluated
     * Then the result is false
     */
    @Test
    public void linkedIndicatorMissing() throws Exception
    {
        JSONObject json = (JSONObject)new JSONParser().parse("{\"node\":{\"rmNode\":{\"indicators\":[\"" + generateText() + "\"]}}}");
        assertFalse(evaluator.evaluate(json));
    }

    /**
     * Given that the primary parent matches the current parent
     * And the "linked" indicator is present
     * When evaluated
     * Then the result is false
     */
    @Test
    public void inPrimaryParent() throws Exception
    {
        String nodeRef = generateText();
        JSONObject json = (JSONObject)new JSONParser().parse(
                  "{"
                + "  \"node\":"
                + "  {  "
                + "    \"rmNode\":"
                + "    {"
                + "      \"indicators\":[\"multiParent\"],"
                + "      \"primaryParentNodeRef\":\"" + nodeRef + "\""
                + "    }"
                + "  },"
                + "  \"parent\":"
                + "  {"
                + "    \"nodeRef\":\"" + nodeRef + "\""
                + "  }"
                + "}"); 
        assertFalse(evaluator.evaluate(json));
    }
    
    /**
     * Given that the primary parent does not match the current parent
     * And the "linked" indicator is present
     * When evaluated
     * Then the result is true
     */
    @Test
    public void notInPrimaryParent() throws Exception
    {
        JSONObject json = (JSONObject)new JSONParser().parse(
                  "{"
                + "  \"node\":"
                + "  {  "
                + "    \"rmNode\":"
                + "    {"
                + "      \"indicators\":[\"multiParent\"],"
                + "      \"primaryParentNodeRef\":\"" + generateText() + "\""
                + "    }"
                + "  },"
                + "  \"parent\":"
                + "  {"
                + "    \"nodeRef\":\"" + generateText() + "\""
                + "  }"
                + "}"); 
        assertTrue(evaluator.evaluate(json));
    }
}
