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

import java.util.Collections;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.util.ParameterCheck;
import org.alfresco.web.evaluator.BaseEvaluator;
import org.alfresco.web.extensibility.SlingshotEvaluatorUtil;
import org.json.simple.JSONObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;

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
    private static final String RECORD_CONTRIBUTOR_GROUP_NAME = "recordContributorGroupName";

    /** slingshot evaluator util */
    protected SlingshotEvaluatorUtil util = null;

    /**
     * @param slingshotExtensibilityUtil    {@link SlingshotEvaluatorUtil}
     */
    public void setSlingshotEvaluatorUtil(SlingshotEvaluatorUtil slingshotExtensibilityUtil)
    {
        util = slingshotExtensibilityUtil;
    }

    /**
     * @see org.alfresco.web.evaluator.BaseEvaluator#evaluate(org.json.simple.JSONObject)
     */
    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        boolean result = true;
        ParameterCheck.mandatory("jsonObject", jsonObject);
        try
        {
            JSONObject node = (JSONObject) jsonObject.get("node");
            if (node != null)
            {
                boolean isEnabled = (Boolean)node.get(IS_RECORD_CONTRIBUTOR_GROUP_ENABLED);
                if (isEnabled)
                {
                    // get the name of the record contributor group
                    String groupName = (String)node.get(RECORD_CONTRIBUTOR_GROUP_NAME);

                    // check the record contributor group
                    final RequestContext rc = ThreadLocalRequestContext.getRequestContext();
                    result = util.isMemberOfGroups(rc, Collections.singletonList("GROUP_" + groupName), true);
                }
                else
                {
                    // if group check is not enabled then allow
                    result = true;
                }
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Exception whilst running UI evaluator: " + err.getMessage());
        };
        return result;
    }
}
