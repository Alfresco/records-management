package org.alfresco.module.org_alfresco_module_rm_share.evaluator;

import org.alfresco.error.AlfrescoRuntimeException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Check for the validity of Records Management actions
 *
 * @author: mikeh
 */
public class ActionEvaluator extends BaseRMEvaluator
{
    private String action;

    /**
     * The name of the action to check for
     *
     * @param action
     */
    public void setAction(String action)
    {
        this.action = action;
    }

    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        if (action == null)
        {
            return false;
        }

        try
        {
            JSONArray actions = getRMActions(jsonObject);
            if (actions == null)
            {
                return false;
            }
            else
            {
                if (actions.contains(action))
                {
                    return true;
                }
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Exception whilst running UI evaluator: " + err);
        }

        return false;
    }
    
    /**
     * Retrieve a JSONArray of applicable actions
     *
     * @param jsonObject JSONObject containing a "node" object as returned from the ApplicationScriptUtils class.
     * @return JSONArray containing applicable actions the UI may choose to display
     */
    private JSONArray getRMActions(JSONObject jsonObject)
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
