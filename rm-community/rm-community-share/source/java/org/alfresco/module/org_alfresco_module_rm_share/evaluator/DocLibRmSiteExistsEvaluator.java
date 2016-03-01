 
package org.alfresco.module.org_alfresco_module_rm_share.evaluator;

import org.json.simple.JSONObject;

/**
 * Checks if an RM site exists or not
 *
 * @author Tuna Aksoy
 * @since 2.1
 */
public class DocLibRmSiteExistsEvaluator extends BaseRMEvaluator
{
    /**
     * @see org.alfresco.web.evaluator.BaseEvaluator#evaluate(org.json.simple.JSONObject)
     */
    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        boolean result = false;
        Object isRMSiteCreated = ((JSONObject)jsonObject.get("node")).get("isRmSiteCreated");
        if (isRMSiteCreated != null && ((Boolean) isRMSiteCreated).booleanValue())
        {
            result = true;
        }
        return result;
    }
}
