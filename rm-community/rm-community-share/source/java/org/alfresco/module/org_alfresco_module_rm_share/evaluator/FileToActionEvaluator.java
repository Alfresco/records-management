/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2019 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software.
 * -
 * If the software was purchased under a paid Alfresco license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 * -
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * -
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * -
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

package org.alfresco.module.org_alfresco_module_rm_share.evaluator;

import org.json.simple.JSONObject;

/**
 * Checks if the user can file to a specific location
 *
 * @author Roxana Lucanu
 */

public class FileToActionEvaluator extends BaseRMEvaluator
{
    /**
     * Node attribute
     */
    private static final String NODE = "node";

    /**
     * Is the location visible to the current user
     */
    private static final String IS_VISIBLE_FOR_CURRENT_USER = "isVisibleForCurrentUser";

    /**
     * @see org.alfresco.module.org_alfresco_module_rm_share.evaluator.BaseRMEvaluator#evaluate(org.json.simple.JSONObject)
     */
    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        boolean result = false;
        JSONObject node = (JSONObject) jsonObject.get(NODE);

        if (node != null)
        {
            Object hasFilingPermission = node.get(IS_VISIBLE_FOR_CURRENT_USER);
            if (hasFilingPermission != null && (Boolean) hasFilingPermission)
            {
                result = true;
            }
        }
        return result;
    }
}
