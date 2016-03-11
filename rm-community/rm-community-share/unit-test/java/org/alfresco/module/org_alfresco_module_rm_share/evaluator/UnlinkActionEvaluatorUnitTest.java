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
