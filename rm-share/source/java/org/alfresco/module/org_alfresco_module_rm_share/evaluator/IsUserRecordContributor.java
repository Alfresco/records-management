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
import org.alfresco.util.ParameterCheck;
import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONObject;

/**
 * Checks if the user is a record contributor
 *
 * @author Roy Wetherall
 * @since 2.3
 */
public class IsUserRecordContributor extends BaseEvaluator
{
    /** json names */
    private static final String IS_RECORD_CONTRIBUTOR_GROUP_ENABLED = "isRecordContributorGroupEnabled";
    private static final String RM_SHOW_ACTIONS = "rmShowActions";

    /**
     * @see org.alfresco.web.evaluator.BaseEvaluator#evaluate(org.json.simple.JSONObject)
     */
    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        ParameterCheck.mandatory("jsonObject", jsonObject);

        boolean result = true;
        try
        {
            JSONObject node = (JSONObject) jsonObject.get("node");
            if (node != null)
            {
                if ((Boolean) node.get(IS_RECORD_CONTRIBUTOR_GROUP_ENABLED))
                {
                    result = (Boolean) node.get(RM_SHOW_ACTIONS);
                }
            }
            else
            {
                result = false;
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Exception whilst running UI evaluator: " + err.getMessage());
        }

        return result;
    }
}
