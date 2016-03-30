<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/data/surf-doclist.lib.js">

/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
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

/* 
 * Override getAllMetadataTemplates to return rm templates first 
 * fix for RM-3202
 */ 
DocList.getAllMetadataTemplates = function getAllMetadataTemplates()
{
   var scopedRoot = config.scoped["DocumentLibrary"]["metadata-templates"],
      configs, templates = {}, templateConfig, templateId, templateIndex, template, templateParamConfig, orderedConfigs = [];

   try
   {
      configs = scopedRoot.getChildren("template");
 
      if (configs)
      {
         // Order the config templates by bringing rm templates first
         for (var i = 0; i < configs.size(); i++)
         {
            orderedConfigs.push(configs.get(i));
         }
         orderedConfigs.sort(fnRMOrderTemplatesById);

         for each (templateConfig in orderedConfigs)
         {
            templateId = templateConfig.getAttribute("id");
            if (templateId)
            {
               template = templates[templateId] ||
               {
                  id: templateId
               };

               DocList.fnAddIfNotNull(template, DocList.getEvaluatorConfig(templateConfig), "evaluators");
               template.title = DocList.getTemplateTitleConfig(templateConfig);
               // Banners and Lines are special cases: we need to merge instead of replace to allow for custom overrides by id
               template.banners = DocList.merge(template.banners || {}, DocList.getTemplateBannerConfig(templateConfig) || {});
               template.lines = DocList.merge(template.lines || {}, DocList.getTemplateLineConfig(templateConfig) || {});

               templates[templateId] = DocList.merge({}, template);
            }
         }
      }
   }
   catch(e)
   {
   }

   return templates;
};


/**
 * Order template by id, bring rm templates first
 */
var fnRMOrderTemplatesById = function fnRMOrderTemplatesById(item1, item2)
{
   if(item1.getAttribute("id").startsWith("rm"))  return -1;
   if(item2.getAttribute("id").startsWith("rm"))  return 1;
   return 0;
};
