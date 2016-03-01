 
package org.alfresco.module.org_alfresco_module_rm_share.evaluator;

import org.json.simple.JSONObject;

/**
 * Evaluates the indicator for records that have the versionRecord aspect
 *
 * @author Roy Wetherall
 * @since 2.3
 */
public class VersionRecordEvaluator extends BaseRMEvaluator
{
    private static final String ASPECT_VERSION_RECORD = "rmv:versionRecord";
    
   /**
     * @see org.alfresco.web.evaluator.BaseEvaluator#evaluate(org.json.simple.JSONObject)
     */
    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        boolean result = false;
        
        if (!isDocLibRecord(jsonObject) && 
            getNodeAspects(jsonObject).contains(ASPECT_VERSION_RECORD))
        {
            result = true;
        }
        
        return result;
    }
}
