/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_rm_share.resolver.doclib;

import org.alfresco.web.resolver.doclib.DefaultDoclistDataUrlResolver;
import org.springframework.extensions.surf.util.URLEncoder;

import java.util.HashMap;

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
