 
package org.alfresco.module.org_alfresco_module_rm_share.evaluator;

import org.alfresco.error.AlfrescoRuntimeException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Check for the validity of Records Management indicators
 *
 * @author: mikeh
 */
public class IndicatorEvaluator extends BaseRMEvaluator
{
    private String indicator;

    private boolean expected = true;

    /**
     * The name of the indicator to check for
     *
     * @param indicator
     */
    public void setIndicator(String indicator)
    {
        this.indicator = indicator;
    }

    public void setExpected(boolean expected)
    {
        this.expected = expected;
    }

    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        if (indicator == null)
        {
            return false;
        }

        boolean result = false;

        try
        {
            JSONArray indicators = getRMIndicators(jsonObject);
            if (indicators != null && indicators.contains(indicator))
            {
                result = true;
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Exception whilst running UI evaluator: " + err);
        }

        return (result == expected);
    }
}
