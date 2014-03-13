/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Base class for RM evaluators
 *
 * @author: mikeh
 */
public abstract class BaseRMEvaluator extends BaseEvaluator
{
    public final boolean isDocLibRecord(JSONObject jsonObject)
    {
        boolean result = false;

        String siteId = getSiteId(jsonObject);
        JSONObject rmNode = getRMNode(jsonObject);

        // TODO .. need to check the type of the site (not assume the id of the site)
        // TODO .. need to ensure this is a record (not something else)

        if (rmNode != null && siteId != null && !siteId.equals("rm"))
        {
            result = true;
        }
        return result;
    }

    /**
     * Retrieve a JSONObject representing the RM extended properties
     *
     * @param jsonObject JSONObject containing a "node" object as returned from the ApplicationScriptUtils class.
     * @return JSONArray containing aspects on the node
     */
    public final JSONObject getRMNode(JSONObject jsonObject)
    {
        JSONObject rmNode = null;

        try
        {
            JSONObject node = (JSONObject) jsonObject.get("node");

            if (node != null)
            {
                rmNode = (JSONObject) node.get("rmNode");
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Exception whilst running UI evaluator: " + err);
        }

        return rmNode;
    }

    /**
     * Retrieve a JSONArray of applicable indicators
     *
     * @param jsonObject JSONObject containing a "node" object as returned from the ApplicationScriptUtils class.
     * @return JSONArray containing applicable indicators the UI may choose to display
     */
    public final JSONArray getRMIndicators(JSONObject jsonObject)
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

    /**
     * Retrieve a JSONArray of applicable actions
     *
     * @param jsonObject JSONObject containing a "node" object as returned from the ApplicationScriptUtils class.
     * @return JSONArray containing applicable actions the UI may choose to display
     */
    public final JSONArray getRMActions(JSONObject jsonObject)
    {
        JSONArray actions = null;

        try
        {
            JSONObject rmNode = getRMNode(jsonObject);
            if (rmNode != null)
            {
                actions = (JSONArray) rmNode.get("actions");
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Exception whilst running UI evaluator: " + err);
        }

        return actions;
    }
}
