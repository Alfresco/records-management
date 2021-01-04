<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
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
function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   AlfrescoUtil.param('container', 'documentLibrary');

   var folderDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site,
   {
      actions: true
   });
   if (folderDetails)
   {
      model.folderDetailsJSON = jsonUtils.toJSONString(folderDetails);
      doclibCommon();
   }
   
   model.syncMode = syncMode.getValue();

   // Widget instantiation metadata...
   var folderActions = {
      id : "rmFolderActions", 
      name : "Alfresco.rm.doclib.FolderActions",
      options : {
         nodeRef : model.nodeRef,
         siteId : (model.site != null) ? model.site : null,
         containerId : model.container,
         rootNode : model.rootNode,
         repositoryRoot : AlfrescoUtil.getRootNode(),
         replicationUrlMapping : (model.replicationUrlMapping != null) ? model.replicationUrlMapping : "{}",
         repositoryBrowsing : (model.rootNode != null),
         folderDetails : folderDetails,
         syncMode : model.syncMode != null ? model.syncMode : ""
      }
   };
   if (model.repositoryUrl != null)
   {
      folderActions.options.repositoryUrl = model.repositoryUrl;
   }
   
   model.widgets = [folderActions];
}

main();
