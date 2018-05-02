/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2018 Alfresco Software Limited
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

import java.util.HashMap;

import org.json.simple.JSONObject;

/**
 * evaluate whether to show the accession action
 *
 * @author: ross gale
 */
public class AccessionEvaluator extends BaseRMEvaluator
{
    private static final String NODE = "node";

    private static final String COMBINE_DISPOSITION_STEP_CONDITIONS = "combineDispositionStepConditions";


    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        JSONObject node = (JSONObject) jsonObject.get(NODE);
        HashMap properties = (HashMap)((HashMap) node.get("rmNode")).get("properties");

        if(properties.containsKey(COMBINE_DISPOSITION_STEP_CONDITIONS) && properties.get(COMBINE_DISPOSITION_STEP_CONDITIONS) != null && (Boolean) properties.get(COMBINE_DISPOSITION_STEP_CONDITIONS))
        {
            return !properties.containsKey("incompleteDispositionEvent");
        }
        return true;
    }
}
