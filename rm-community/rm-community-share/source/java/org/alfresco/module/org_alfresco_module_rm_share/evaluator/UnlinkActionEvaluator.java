/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2021 Alfresco Software Limited
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

import org.alfresco.error.AlfrescoRuntimeException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Evaluate whether the unlink action should be shown or not.
 *
 * @author Roy Wetherall
 * @since 2.3
 */
public class UnlinkActionEvaluator extends BaseRMEvaluator
{
    /** value constants */
    private static final String INDICATOR = "multiParent";
    private static final String NODE = "node";
    private static final String RM_NODE = "rmNode";
    private static final String PRIMARY_PARENT = "primaryParentNodeRef";
    private static final String PARENT = "parent";
    private static final String NODE_REF = "nodeRef";
    
    /**
     * @see org.alfresco.web.evaluator.BaseEvaluator#evaluate(org.json.simple.JSONObject)
     */
    @Override
    public boolean evaluate(JSONObject jsonObject)
    {       
        boolean result = false;

        try
        {
            // first check that the multi parent indicator is present
            JSONArray indicators = getRMIndicators(jsonObject);
            if (indicators != null && indicators.contains(INDICATOR))
            {
                // now check that we are not in the primary location
                String primaryParent = (String)((JSONObject)((JSONObject)jsonObject.get(NODE)).get(RM_NODE)).get(PRIMARY_PARENT);
                String parent = (String)((JSONObject)jsonObject.get(PARENT)).get(NODE_REF);
                if (!primaryParent.equals(parent))
                {
                    result = true;
                }                
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Exception whilst running UI evaluator: " + err);
        }

        return result;
    }
}
