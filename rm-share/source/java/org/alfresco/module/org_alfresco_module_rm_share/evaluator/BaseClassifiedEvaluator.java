/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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

import static org.apache.commons.lang.StringUtils.isNotBlank;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Base class for classified evaluators
 *
 * @author Tuna Aksoy
 * @since 3.0
 */
public abstract class BaseClassifiedEvaluator extends BaseRMEvaluator
{
    /** Classified aspect */
    protected static final String ASPECT_CLASSIFIED = "clf:classified";

    /**
     * Helper method to check if a node has the classified aspect applied
     *
     * @param jsonObject {@link JSONObject} representing the node
     * @return <code>true</code> if the node has the classified aspect applied, <code>false</code> otherwise
     */
    protected boolean hasClassifiedAspect(JSONObject jsonObject)
    {
        return hasApect(jsonObject, ASPECT_CLASSIFIED);
    }

    /**
     * Helper method to check if a node has the given aspect
     *
     * @param jsonObject {@link JSONObject} representing the node
     * @param aspect {@link String} The aspect name
     * @return <code>true</code> if the node has the given aspect, <code>false</code> otherwise
     */
    protected boolean hasApect(JSONObject jsonObject, String aspect)
    {
        boolean hasAspect = false;
        JSONArray nodeAspects = getNodeAspects(jsonObject);
        if (nodeAspects != null && nodeAspects.contains(aspect))
        {
            hasAspect = true;
        }
        return hasAspect;
    }

    /**
     * Helper method to check if the node is in a site with the given preset
     *
     * @param jsonObject {@link JSONObject} representing the node
     * @param preset {@link String} The site preset
     * @return <code>true</code> if the node is in a site with the given preset, <code>false</code> otherwise
     */
    protected boolean isInSite(JSONObject jsonObject, String preset)
    {
        boolean isInSite = false;
        String sitePreset = getSitePreset(jsonObject);
        if (isNotBlank(sitePreset) && sitePreset.equals(preset))
        {
            isInSite = true;
        }
        return isInSite;
    }
}
