 
package org.alfresco.module.org_alfresco_module_rm_share.evaluator;

import org.alfresco.web.evaluator.BaseEvaluator;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;

/**
 * Abstract base class for the recorded version history evaluators
 *
 * @author Tuna Aksoy
 * @since 2.3
 */
public abstract class BaseRecordedVersionHistoryEvaluator extends BaseEvaluator
{
    /** Recordable version policy property */
    private static final String PROP_RECORDABLE_VERSION_POLICY = "rmv:recordableVersionPolicy";

    /**
     * Evaluation execution implementation.
     *
     * @param jsonObject The json object representing the document wrapping the node as received from a Rhino script
     * @param policy The recordable version policy
     * @return <code>true</code> if the node's recordable version property value equals to <code>policy</code>, <code>false</code> otherwise
     */
    protected boolean evaluate(JSONObject jsonObject, String policy)
    {
        boolean result = false;
        String recordableVersionPolicy = (String) getProperty(jsonObject, PROP_RECORDABLE_VERSION_POLICY);
        if (StringUtils.isNotBlank(recordableVersionPolicy) && recordableVersionPolicy.equalsIgnoreCase(policy))
        {
            result = true;
        }
        return result;
    }
}
