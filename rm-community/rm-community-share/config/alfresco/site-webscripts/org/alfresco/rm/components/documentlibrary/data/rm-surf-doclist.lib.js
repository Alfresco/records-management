<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/data/surf-doclist.lib.js">

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

// Keep a reference to the original implementation
DocList.inheritedGetAllMetadataTemplates = DocList.getAllMetadataTemplates;

/* 
 * Override getAllMetadataTemplates to return rm templates first 
 * fix for RM-3202
 */ 
DocList.getAllMetadataTemplates = function getAllMetadataTemplates()
{
   var templates, template, rmTemplates = [], otherTemplates = [], orderedTemplates = {};

   try
   {
      // get the original list of templates
      templates = DocList.inheritedGetAllMetadataTemplates();

      // separate the rm templates from the other templates
      for each (template in templates)
      {
          if(template.id.startsWith("rm"))
          {
              rmTemplates.push(template);
          }
          else
          {
              otherTemplates.push(template);
          }
      }

      // add the rm templates to the final template list
      for each(template in rmTemplates)
      {
          orderedTemplates[template.id] = template;
      }
      // add the other templates to the final template list 
      for each(template in otherTemplates)
      {
          orderedTemplates[template.id] = template;
      }
   }
   catch(e)
   {
   }

   return orderedTemplates;
};
