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
import static org.mockito.Mockito.mock;

import org.alfresco.test.BaseUnitTest;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;

/**
 * Action evaluator unit test
 * 
 * @author Roy Wetherall
 * @since  2.3
 */
public class ActionEvaluatorUnitTest extends BaseUnitTest
{
    /** action evaluator */
    private ActionEvaluator actionEvaluator = new ActionEvaluator();
    
    /**
     * Given the action is null
     * When evaluated
     * Then the result is false
     */
    @Test
    public void actionIsNull()
    {
        assertFalse(actionEvaluator.evaluate(mock(JSONObject.class)));
    }
    
    /**
     * Given there are no actions
     * When evaluated
     * Then the result is false
     */
    @Test
    public void noActions()
    {
        actionEvaluator.setAction(generateText());        
        assertFalse(actionEvaluator.evaluate(mock(JSONObject.class)));
    }
    
    /**
     * Given that the action is not in the action list
     * When evaluated
     * Then the result is false
     */
    @Test
    public void actionNotInActionList() throws Exception
    {
        // set action 
        actionEvaluator.setAction(generateText());
        
        // create json
        JSONObject json = (JSONObject)new JSONParser().parse("{\"node\":{\"rmNode\":{\"actions\":[\"" + generateText() + "\"]}}}");
        
        // show evaluation true
        assertFalse(actionEvaluator.evaluate(json));
    }
    
    /**
     * Given that the action is in the action list
     * When evaluated
     * Then the result it true
     */
    @Test
    public void actionInActionList() throws Exception
    {
        // set action 
        String action = generateText();
        actionEvaluator.setAction(action);
        
        // create json
        JSONObject json = (JSONObject)new JSONParser().parse("{\"node\":{\"rmNode\":{\"actions\":[\"" + action + "\"]}}}");
        
        // show evaluation true
        assertTrue(actionEvaluator.evaluate(json));
    }

}
