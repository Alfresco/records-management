 
package org.alfresco.module.org_alfresco_module_rm_share.evaluator;

import org.json.simple.JSONObject;

/**
 * Record indicator shown in non-RM sites that contain records
 *
 * @author: mikeh
 */
public class DocLibRecordIndicatorEvaluator extends BaseRMEvaluator
{
    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        return isDocLibRecord(jsonObject);
    }
}
