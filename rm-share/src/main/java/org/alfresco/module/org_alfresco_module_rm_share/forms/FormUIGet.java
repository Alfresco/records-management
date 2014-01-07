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

import java.util.List;
import java.util.Map;

import org.alfresco.web.config.forms.FormSet;

/**
 * @author Roy Wetherall
 */
public class FormUIGet extends org.alfresco.web.scripts.forms.FormUIGet
{
    private static final String SET_RM_CUSTOM = "rm-custom";
    private static final String SET_RM_METADATA = "rm-metadata";
    
    /**
     * @see org.alfresco.web.scripts.forms.FormUIGet#getVisibleFieldsInSet(org.alfresco.web.scripts.forms.FormUIGet.ModelContext, org.alfresco.web.config.forms.FormSet)
     */
    @Override
    protected List<String> getVisibleFieldsInSet(ModelContext context, FormSet setConfig)
    {
        List<String> result = null; 
        String id = setConfig.getSetId();
        
        if (SET_RM_CUSTOM.equals(id) == true || id.startsWith(SET_RM_METADATA) == true)
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
    
    // TODO .. we will need to override this so that we can add new set's automatically!
    //      .. atm we have to hard code the set into the form definition
    
//    @Override
//    protected void processServerFields(ModelContext context)
//    {
//        super.processServerFields(context);
//        
//        // try adding your custom set's here
//        Set mySet = new Set("mysetid", "My Set");
//        context.getStructure().addSet(mySet);
//    }

}
