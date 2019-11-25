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

import java.util.HashMap;
import java.util.Optional;

import org.json.simple.JSONObject;

/**
 * Check for an incomplete disposition event and the 'combineDispositionStepConditions' property
 *
 * This will prevent an action being offered while there is an incomplete event if the user has requested to combine an event and date condition
 *
 * @author ross gale
 */
public class IncompleteEventEvaluator extends BaseRMEvaluator
{
    private static final String NODE = "node";

    //Property name for boolean value indicating if all conditions need to be fulfilled for the disposition step to be available
    private static final String COMBINE_DISPOSITION_STEP_CONDITIONS = "combineDispositionStepConditions";

    private static final String DISPOSITION_EVENT_COMBINATION = "dispositionEventCombination";

    private static final String INCOMPLETE_DISPOSITION_EVENT = "incompleteDispositionEvent";


    /**
     * Returns false if there is an incomplete event and the combineDispositionStepConditions property is true
     * otherwise return true.
     * @param jsonObject
     * @return boolean
     */
    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        JSONObject node = (JSONObject) jsonObject.get(NODE);
        HashMap properties = (HashMap)((HashMap) node.get("rmNode")).get("properties");

        Optional<Boolean> combineProp = Optional.ofNullable((Boolean) properties.get(COMBINE_DISPOSITION_STEP_CONDITIONS));

        if (combineProp.orElse(false))
        {
            String combineEvents = (String) properties.get(DISPOSITION_EVENT_COMBINATION);
            if(combineEvents != null && combineEvents.equals("and"))
            {
                return !properties.containsKey(INCOMPLETE_DISPOSITION_EVENT);
            }
        }
        return true;

    }
}
