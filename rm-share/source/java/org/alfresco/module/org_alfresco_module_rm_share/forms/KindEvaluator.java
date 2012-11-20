/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_rm_share.forms;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alfresco.web.config.forms.ServiceBasedEvaluator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.util.URLEncoder;

/**
 * File plan component kind forms evaluator.
 * 
 * @author Roy Wetherall
 */
public class KindEvaluator extends ServiceBasedEvaluator
{
    private static final String JSON_KIND = "kind";
    
    private static Log logger = LogFactory.getLog(KindEvaluator.class);
    
    protected static final Pattern nodeRefPattern = Pattern.compile(".+://.+/.+");

    /**
     * Determines whether the given node type matches the path of the given
     * object
     * 
     * @see org.springframework.extensions.config.evaluator.Evaluator#applies(java.lang.Object,
     *      java.lang.String)
     */
    public boolean applies(Object obj, String condition)
    {
        boolean result = false;

        if (obj instanceof String)
        {
            String objAsString = (String) obj;
            // quick test before running slow match for full NodeRef pattern
            if (objAsString.indexOf(':') != -1 || objAsString.startsWith("{") == true)
            {
                Matcher m = nodeRefPattern.matcher(objAsString);
                if (m.matches())
                {
                    try
                    {
                        String jsonResponseString = callService("/api/rmmetadata?noderef=" + objAsString);
                        
                        if (jsonResponseString != null)
                        {
                            result = checkJsonAgainstCondition(condition, jsonResponseString);
                        }
                        else if (getLogger().isWarnEnabled())
                        {
                            getLogger().warn("RM metadata service response appears to be null!");
                        }
                    }                    
                    catch (ConnectorServiceException e)
                    {
                        if (getLogger().isWarnEnabled())
                        {
                            getLogger().warn("Failed to connect to RM metadata service.", e);
                        }
                    }
                }
                else
                {
                    try
                    {
                        String jsonResponseString = callService("/api/rmmetadata?type=" + URLEncoder.encodeUriComponent(objAsString));
                        
                        if (jsonResponseString != null)
                        {
                            result = checkJsonAgainstCondition(condition, jsonResponseString);
                        }
                        else if (getLogger().isWarnEnabled())
                        {
                            getLogger().warn("RM metadata service response appears to be null!");
                        }
                    }                    
                    catch (ConnectorServiceException e)
                    {
                        if (getLogger().isWarnEnabled())
                        {
                            getLogger().warn("Failed to connect to RM metadata service.", e);
                        }
                    }                    
                }
            }
        }

        return result;
    }
    
    protected boolean checkJsonAgainstCondition(String condition, String jsonResponseString)
    {
        boolean result = false;
        try
        {
            JSONObject json = new JSONObject(new JSONTokener(jsonResponseString));
            String kind = json.getString(JSON_KIND);
            result = condition.equals(kind);            
        } 
        catch (JSONException e)
        {
            if (getLogger().isWarnEnabled())
            {
                getLogger().warn("Failed to find RM kind in JSON response from metadata service: " + e.getMessage());
            }
        }
        return result;
    }

    @Override
    protected Log getLogger()
    {
        return logger;
    }
}
