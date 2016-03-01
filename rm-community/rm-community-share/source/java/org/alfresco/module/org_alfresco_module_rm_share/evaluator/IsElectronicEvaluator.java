 
package org.alfresco.module.org_alfresco_module_rm_share.evaluator;

import org.json.simple.JSONObject;

/**
 * @author Roy Wetherall
 */
public class IsElectronicEvaluator extends IndicatorEvaluator
{
    public IsElectronicEvaluator()
    {
        setIndicator("nonElectronic");
    }

    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        return !super.evaluate(jsonObject);
    }
}
