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

package org.alfresco.module.org_alfresco_module_rm_share.forms;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.web.config.forms.FormSet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Form UI GET method implementation override.
 * <p>
 * Allows custom and record metadata to be automatically displayed without need for form modificiations.
 *
 * @author Roy Wetherall
 */
public class FormUIGet extends org.alfresco.web.scripts.forms.FormUIGet
{
    private static final String SET_RM_CUSTOM = "rm-custom";
    private static final String SET_RM_METADATA = "rm-metadata";
    private static final String LABEL_ID_PREFIX = "label.set.";

    /**
     * @see org.alfresco.web.scripts.forms.FormUIGet#getVisibleFieldsInSet(org.alfresco.web.scripts.forms.FormUIGet.ModelContext, org.alfresco.web.config.forms.FormSet)
     */
    @Override
    protected List<String> getVisibleFieldsInSet(ModelContext context, FormSet setConfig)
    {
        List<String> result = null;
        String id = setConfig.getSetId();

        if (SET_RM_CUSTOM.equals(id) || id.startsWith(SET_RM_METADATA))
        {
            Map<String, List<String>> setMembership = discoverSetMembership(context);
            result = setMembership.get(id);
        }
        else
        {
            result = super.getVisibleFieldsInSet(context, setConfig);
        }


        return result;
    }

    /**
     * @see org.alfresco.web.scripts.forms.FormUIGet#processVisibleFields(org.alfresco.web.scripts.forms.FormUIGet.ModelContext)
     */
    @Override
    protected void processVisibleFields(ModelContext context)
    {
        // iterate over the root sets and generate a model for each one
        for (FormSet setConfig : getRootSetsAsList(context))
        {
            Set set = generateSetModelUsingVisibleFields(context, setConfig);

            // if the set got created (as it contained fields or other sets)
            // add it to the structure list in the model context
            if (set != null)
            {
                context.getStructure().add(set);
            }
        }
    }

    /**
     * @see org.alfresco.web.scripts.forms.FormUIGet#processServerFields(org.alfresco.web.scripts.forms.FormUIGet.ModelContext)
     */
    @Override
    protected void processServerFields(ModelContext context)
    {
        if (context.getFormConfig() != null)
        {
            // discover the set membership of the fields using the form definition
            Map<String, List<String>> setMembership = discoverSetMembership(context);

            // get root sets from config and build set structure using config and lists built above
            for (FormSet setConfig : getRootSetsAsList(context))
            {
                Set set = generateSetModelUsingServerFields(context, setConfig, setMembership);

                // if the set got created (as it contained fields or other sets)
                // add it to the structure list in the model context
                if (set != null)
                {
                    context.getStructure().add(set);
                }
            }
        }
        else
        {
            // as there is no config at all generate a default set that contains
            // all the fields returned in the form definition
            Set set = generateDefaultSetModelUsingServerFields(context);
            context.getStructure().add(set);
        }
    }

    /**
     * Gets the root sets as a list, including the dynamically discovered record meta-data sets.
     *
     * @param context The model context
     * @return The root sets as list, including the dynamically discovered record meta-data sets.
     */
    protected List<FormSet> getRootSetsAsList(ModelContext context)
    {
        List<FormSet> result = context.getFormConfig().getRootSetsAsList();
        result.addAll(getRecordMetaDataSetConfig(context));
        return result;
    }

    /**
     * Gets all the record meta-data sets present in the form data.
     *
     * @param context The model context
     * @return All the record meta-data sets present in the form data.
     */
    protected Collection<FormSet> getRecordMetaDataSetConfig(ModelContext context)
    {
        Map<String, FormSet> result = new HashMap<String, FormSet>(13);

        try
        {
            // get list of fields from form definition
            JSONObject data = context.getFormDefinition().getJSONObject(MODEL_DATA);

            JSONObject definition = data.getJSONObject(MODEL_DEFINITION);
            JSONArray fieldsFromServer = definition.getJSONArray(MODEL_FIELDS);

            // iterate around fields and pull out the record metadata sets
            for (int x = 0; x < fieldsFromServer.length(); x++)
            {
                JSONObject fieldDefinition = fieldsFromServer.getJSONObject(x);
                if (fieldDefinition.has(MODEL_GROUP))
                {
                    String set = fieldDefinition.getString(MODEL_GROUP);
                    if (!result.containsKey(set) && set.startsWith(SET_RM_METADATA))
                    {
                        FormSet formSet = new FormSet(set, null, "panel", null, LABEL_ID_PREFIX + set);
                        result.put(set, formSet);
                    }
                }
            }
        }
        catch (JSONException je)
        {
            // do nothing
        }

        return result.values();
    }
}
