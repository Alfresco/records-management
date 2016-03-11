package org.alfresco.module.org_alfresco_module_rm_share.evaluator;

import org.alfresco.error.AlfrescoRuntimeException;
import org.json.simple.JSONObject;

/**
 * Evaluates returned UI convenience type
 *
 * @author: mikeh
 */
public class UITypeEvaluator extends BaseRMEvaluator
{
    private String type;

    /**
     * The name of the node type to check for
     *
     * @param type
     */
    public void setType(String type)
    {
        this.type = type;
    }

    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        if (type == null)
        {
            return false;
        }

        if (isDocLibRecord(jsonObject))
        {
            return false;
        }

        try
        {
            JSONObject rmNode = getRMNode(jsonObject);
            if (rmNode == null)
            {
                return false;
            }
            else
            {
                String uiType = (String) rmNode.get("uiType");
                if (uiType.equalsIgnoreCase(type))
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
}
