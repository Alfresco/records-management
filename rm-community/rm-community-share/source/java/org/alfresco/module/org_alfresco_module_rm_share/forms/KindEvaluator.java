 
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

    protected static final Pattern NODE_REF_PATTERN = Pattern.compile(".+://.+/.+");

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
            if (objAsString.indexOf(':') != -1 || objAsString.startsWith("{"))
            {
                Matcher m = NODE_REF_PATTERN.matcher(objAsString);
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
