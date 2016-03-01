 
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
