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

import org.alfresco.web.evaluator.BaseEvaluator;
import org.apache.commons.lang.StringUtils;
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
    private static final String PROP_RECORDABLE_VERSION_POLICY = "rmv:recordableVersionPolicy";

    /**
     * Evaluation execution implementation.
     *
     * @param jsonObject The json object representing the document wrapping the node as received from a Rhino script
     * @param policy The recordable version policy
     * @return <code>true</code> if the node's recordable version property value equals to <code>policy</code>, <code>false</code> otherwise
     */
    protected boolean evaluate(JSONObject jsonObject, String policy)
    {
        boolean result = false;
        String recordableVersionPolicy = (String) getProperty(jsonObject, PROP_RECORDABLE_VERSION_POLICY);
        if (StringUtils.isNotBlank(recordableVersionPolicy) && recordableVersionPolicy.equalsIgnoreCase(policy))
        {
            result = true;
        }
        return result;
    }
}
