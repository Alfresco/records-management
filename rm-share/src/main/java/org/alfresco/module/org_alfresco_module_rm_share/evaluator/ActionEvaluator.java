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
            throw new AlfrescoRuntimeException("Exception whilst running UI evaluator: " + err.getMessage());
        }

        return false;
    }
}
