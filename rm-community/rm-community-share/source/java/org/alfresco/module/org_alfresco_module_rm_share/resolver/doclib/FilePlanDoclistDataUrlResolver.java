package org.alfresco.module.org_alfresco_module_rm_share.resolver.doclib;

import java.util.HashMap;

import org.alfresco.web.resolver.doclib.DefaultDoclistDataUrlResolver;
import org.springframework.extensions.surf.util.URLEncoder;

/**
 * Returns the url to the RM fileplan (The specific repository doclist webscript to use for RM).
 *
 * @author ewinlof
 */
public class FilePlanDoclistDataUrlResolver extends DefaultDoclistDataUrlResolver
{
    /**
     * Returns the url to the RM specific repository doclist webscript to use, a fileplan.
     *
     * @param webscript The repo doclib2 webscript to use, i.e. doclist or node
     * @param params doclib2 webscript specific parameters
     * @param args url parameters, i.e. pagination parameters
     * @return The url to use when asking the repository doclist webscript.
     */
    @Override
    public String resolve(String webscript, String params, HashMap<String, String> args)
    {
        return "/slingshot/doclib2/rm/" + webscript + "/" + URLEncoder.encodeUri(params) + getArgsAsParameters(args);
    }
}
