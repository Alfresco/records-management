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

package org.alfresco.module.org_alfresco_module_rm_share.resolver.doclib;

import org.alfresco.web.resolver.doclib.DefaultDoclistActionGroupResolver;
import org.json.simple.JSONObject;

/**
 * Extended default doclist action group resolver.
 *
 * @see DefaultDoclistActionGroupResolver
 * @author Roy Wetherall
 * @since 2.1
 */
public class ExtendedDefaultDoclistActionGroupResolver extends DefaultDoclistActionGroupResolver
{
    private static final String FROZEN_ACTIVE_CONTENT = "frozencontent";

    /** RM group resolver **/
    private FilePlanDoclistActionGroupResolver filePlanDoclistActionGroupResolver;

    /**
     * @param filePlanDoclistActionGroupResolver    rm group resolver
     */
    public void setFilePlanDoclistActionGroupResolver(FilePlanDoclistActionGroupResolver filePlanDoclistActionGroupResolver)
    {
        this.filePlanDoclistActionGroupResolver = filePlanDoclistActionGroupResolver;
    }

    /**
     * Extend the default resolution code to account for records.
     *
     * @see org.alfresco.web.resolver.doclib.DefaultDoclistActionGroupResolver#resolve(org.json.simple.JSONObject, java.lang.String)
     */
    @Override
    public String resolve(JSONObject jsonObject, String view)
    {
        String result = null;

        // get the json object representing the node
        JSONObject node = (org.json.simple.JSONObject)jsonObject.get("node");

        // determine whether we are dealing with a RM node or not
        Boolean isRMNode = (Boolean) node.get("isRmNode");
        String nodeType = (String) node.get("uiType");
        if ((isRMNode != null && isRMNode.booleanValue()) || FROZEN_ACTIVE_CONTENT.equals(nodeType))
        {
            // use the file plan resolver
            result = filePlanDoclistActionGroupResolver.resolve(jsonObject, view, true);
        }
        else
        {
            // use the default resolver
            result = super.resolve(jsonObject, view);
        }

        return result;
    }
}
