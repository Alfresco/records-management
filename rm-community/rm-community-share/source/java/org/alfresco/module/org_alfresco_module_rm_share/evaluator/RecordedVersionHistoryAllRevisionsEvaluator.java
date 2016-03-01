 
package org.alfresco.module.org_alfresco_module_rm_share.evaluator;

import org.json.simple.JSONObject;

/**
 * Evaluates the indicator for documents which have the recordable version policy "ALL" set.
 *
 * @author Tuna Aksoy
 * @since 2.3
 */
public class RecordedVersionHistoryAllRevisionsEvaluator extends BaseRecordedVersionHistoryEvaluator
{
    /** Recordable version policy */
    private static final String ALL = "ALL";

    /**
     * @see org.alfresco.web.evaluator.BaseEvaluator#evaluate(org.json.simple.JSONObject)
     */
    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        return evaluate(jsonObject, ALL);
    }
}
