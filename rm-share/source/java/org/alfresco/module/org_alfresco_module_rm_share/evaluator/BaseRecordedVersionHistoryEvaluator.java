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

import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONObject;

/**
 * Abstract base class for the recorded version history evaluators
 *
 * @author Tuna Aksoy
 * @since 2.3
 */
public abstract class BaseRecordedVersionHistoryEvaluator extends BaseEvaluator
{
    /** Recordable version policy property */
    private String PROP_RECORDABLE_VERSION_POLICY = "rmv:recordableVersionPolicy";

    /**
     * Evaluation execution implementation.
     *
     * @param jsonObject The json object representing the document wrapping the node as received from a Rhino script
     * @param policy The recordable version policy
     * @return <code>true</code> if the node's recordable version property value equals to <code>policy</code>, <code>false</code> otherwise
     */
    protected boolean evaluate(JSONObject jsonObject, String policy)
    {
        return ((String) getProperty(jsonObject, PROP_RECORDABLE_VERSION_POLICY)).equalsIgnoreCase(policy);
    }
}
