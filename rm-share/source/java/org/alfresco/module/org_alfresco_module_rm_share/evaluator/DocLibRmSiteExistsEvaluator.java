/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.springframework.extensions.surf.WebFrameworkServiceRegistry;
import org.springframework.extensions.webscripts.ScriptRemote;
import org.springframework.extensions.webscripts.connector.Response;

/**
 * Checks if an RM site exists or not
 *
 * @author Tuna Aksoy
 * @since 2.1
 */
public class DocLibRmSiteExistsEvaluator extends BaseRMEvaluator
{
    /** Logger */
    private static Log logger = LogFactory.getLog(DocLibRmSiteExistsEvaluator.class);

    /** Web Framework Service Registry */
    private WebFrameworkServiceRegistry serviceRegistry = null;

    /**
     * @param serviceRegistry   web framework service registry
     */
    public void setServiceRegistry(WebFrameworkServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
    }

    /**
     * @see org.alfresco.web.evaluator.BaseEvaluator#evaluate(org.json.simple.JSONObject)
     */
    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        boolean result = false;
        ScriptRemote scriptRemote = serviceRegistry.getScriptRemote();
        Response response = scriptRemote.connect().get("/api/sites");
        if (response.getStatus().getCode() == 200)
        {
            try
            {
                JSONArray responseAsJsonArray = new JSONArray(response.getResponse());
                for (int i = 0; i < responseAsJsonArray.length(); i++)
                {
                    org.json.JSONObject site = responseAsJsonArray.getJSONObject(i);
                    if (site != null && site.getString("sitePreset").equals("rm-site-dashboard"))
                    {
                        result = true;
                        break;
                    }
                }
            }
            catch (JSONException e)
            {
                logger.error("An error occurred while checking the site presets: '" + e.getMessage() + "'.");
            }
        }
        return result;
    }
}
