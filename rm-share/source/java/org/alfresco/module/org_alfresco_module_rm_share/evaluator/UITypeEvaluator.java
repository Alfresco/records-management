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
