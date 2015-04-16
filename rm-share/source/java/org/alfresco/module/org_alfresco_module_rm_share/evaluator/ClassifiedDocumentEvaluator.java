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

import static org.alfresco.util.ParameterCheck.mandatory;

import org.json.simple.JSONObject;

/**
 * Evaluator for classified document
 *
 * @author Tuna Aksoy
 * @since 3.0
 */
public class ClassifiedDocumentEvaluator extends BaseClassifiedEvaluator
{
    /** Collab site preset */
    private static final String COLLAB_SITE_PRESET = "site-dashboard";

    /** Type content */
    private static final String TYPE_CONTENT = "cm:content";

    /**
     * @see org.alfresco.web.evaluator.BaseEvaluator#evaluate(org.json.simple.JSONObject)
     */
    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        mandatory("jsonObject", jsonObject);

        boolean hasClassifiedAspect = hasClassifiedAspect(jsonObject);
        boolean isInCollabSite = isInSite(jsonObject, COLLAB_SITE_PRESET);
        boolean isDocument = getNodeType(jsonObject).equals(TYPE_CONTENT);

        return hasClassifiedAspect && isInCollabSite && isDocument;
    }
}
