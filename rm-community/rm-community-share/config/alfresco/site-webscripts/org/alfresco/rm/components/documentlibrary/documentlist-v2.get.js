<import resource="classpath:/alfresco/site-webscripts/org/alfresco/rm/components/documentlibrary/documentlist-v2.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/upload/uploadable.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/include/documentlist.lib.js">

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

doclibCommon();

function widgets()
{
   var useTitle = "true";
   var docLibConfig = config.scoped["DocumentLibrary"];
   if (docLibConfig != null)
   {
      var tmp = docLibConfig["use-title"];
      useTitle = tmp != null ? tmp : "true";
   }

   var docListToolbar = {
      id: "DocListToolbar",
      name: "Alfresco.rm.component.DocListToolbar",
      assignTo: "docListToolbar",
      options: {
         siteId: (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         hideNavBar: Boolean(toolbar.preferences.hideNavBar)
      }
   };

   var documentList = {
      id : "DocumentList",
      name : "Alfresco.rm.component.DocumentList",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         containerId : template.properties.container != null ? template.properties.container : "documentLibrary",
         rootNode : model.rootNode != null ? model.rootNode : "null",
         usePagination : (args.pagination == "true"),
         sortAscending : (model.preferences.sortAscending != null ? model.preferences.sortAscending : true),
         sortField : model.preferences.sortField != null ? model.preferences.sortField : "cm:name",
         showFolders : (model.preferences.showFolders != null ? model.preferences.showFolders : true),
         simpleView : model.preferences.simpleView != null ? model.preferences.simpleView : "null",
         viewRenderers: model.viewRenderers,
         highlightFile : page.url.args["file"] != null ? page.url.args["file"] : "",
         replicationUrlMapping : model.replicationUrlMapping != null ? model.replicationUrlMapping : "{}",
         repositoryBrowsing : model.rootNode != null,
         useTitle : (model.useTitle != null ? model.useTitle == "true" : true),
         userIsSiteManager : model.userIsSiteManager,
         associatedToolbar: { _alfValue: "docListToolbar", _alfType: "REFERENCE" },
         commonComponentStyle : model.commonComponentStyle,
         suppressComponent : model.suppressComponent
      }
   };
   if (model.repositoryUrl != null)
   {
      documentList.options.repositoryUrl = model.repositoryUrl;
   }

   model.widgets = [docListToolbar, documentList];
}

widgets();

model.jsonModel = {
   services: [
      "alfresco/services/DocumentService",
      "alfresco/services/NotificationService",
      "alfresco/services/OptionsService",
      "alfresco/services/DialogService",
      "rm/services/RelationshipService"
   ]
};
