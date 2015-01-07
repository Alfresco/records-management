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

import org.json.simple.JSONObject;

/**
 * Evaluates the indicator for records that have the versionRecord aspect
 *
 * @author Roy Wetherall
 * @since 2.3
 */
public class VersionRecordEvaluator extends BaseRMEvaluator
{
    private static final String ASPECT_VERSION_RECORD = "rmv:versionRecord";
    
   /**
     * @see org.alfresco.web.evaluator.BaseEvaluator#evaluate(org.json.simple.JSONObject)
     */
    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        boolean result = false;
        
        if (!isDocLibRecord(jsonObject) && 
            getNodeAspects(jsonObject).contains(ASPECT_VERSION_RECORD))
        {
            result = true;
        }
        
        return result;
    }
}
