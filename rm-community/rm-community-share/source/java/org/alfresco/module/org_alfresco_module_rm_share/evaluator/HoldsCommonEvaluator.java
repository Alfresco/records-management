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

import org.alfresco.web.evaluator.Evaluator;
import org.json.simple.JSONObject;

/**
 * Combined evaluator for records and active content
 * for AddTo/RemoveFrom Hold action
 *
 * @author Roxana Lucanu
 * @since 3.2
 */

public class HoldsCommonEvaluator extends BaseRMEvaluator
{
    /** evaluator for records */
    private Evaluator recordEvaluator;

    /** evaluator for active content */
    private Evaluator docEvaluator;

    private static final String ASPECT_FILEPLAN_COMPONENT = "rma:filePlanComponent";

    /**
     * Sets the record evaluator
     *
     * @param recordEvaluator
     */
    public void setRecordEvaluator(Evaluator recordEvaluator)
    {
        this.recordEvaluator = recordEvaluator;
    }

    /**
     * Sets the active content evaluator
     *
     * @param docEvaluator
     */
    public void setDocEvaluator(Evaluator docEvaluator)
    {
        this.docEvaluator = docEvaluator;
    }

    /**
     * Calls specific evaluator considering the document type
     *
     * @param jsonObject
     * @return boolean
     */
    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        if (getNodeAspects(jsonObject).contains(ASPECT_FILEPLAN_COMPONENT))
        {
            return recordEvaluator.evaluate(jsonObject);
        }
        return docEvaluator.evaluate(jsonObject);
    }
}
