/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
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
    

    /**
     * Retrieve a JSONArray of applicable indicators
     *
     * @param jsonObject JSONObject containing a "node" object as returned from the ApplicationScriptUtils class.
     * @return JSONArray containing applicable indicators the UI may choose to display
     */
    private JSONArray getRMIndicators(JSONObject jsonObject)
    {
        JSONArray indicators = null;

        try
        {
            JSONObject rmNode = getRMNode(jsonObject);
            if (rmNode != null)
            {
                indicators = (JSONArray) rmNode.get("indicators");
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Exception whilst running UI evaluator: " + err);
        }

        return indicators;
    }
}
