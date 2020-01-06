<#--
 #%L
 Alfresco Records Management Module
 %%
 Copyright (C) 2005 - 2020 Alfresco Software Limited
 %%
 This file is part of the Alfresco software.
 -
 If the software was purchased under a paid Alfresco license, the terms of
 the paid license agreement will prevail.  Otherwise, the software is
 provided under the following open source license terms:
 -
 Alfresco is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 -
 Alfresco is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.
 -
 You should have received a copy of the GNU Lesser General Public License
 along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 #L%
-->
<#if folderDetailsJSON??>
   <#assign el=args.htmlid?js_string>
   <script type="text/javascript">//<![CDATA[
      new Alfresco.rm.doclib.FolderActions("${el}").setOptions(
      {
         nodeRef: "${nodeRef?js_string}",
         siteId: <#if site??>"${site?js_string}"<#else>null</#if>,
         containerId: "${container?js_string}",
         rootNode: "${rootNode}",
         <#if repositoryUrl??>repositoryUrl: "${repositoryUrl}",</#if>
         replicationUrlMapping: ${jsonUtils.toJSONString(replicationUrlMappingJSON)!"{}"},
         repositoryBrowsing: ${(rootNode??)?string},
         folderDetails: ${folderDetailsJSON}
      }).setMessages(
         ${messages}
      );
   //]]></script>

   <div id="${el}-body" class="folder-actions folder-details-panel">
      <h2 id="${el}-heading" class="thin dark">
         ${msg("heading")}
      </h2>
      <div class="doclist">
         <div id="${el}-actionSet" class="action-set"></div>
      </div>
   </div>

   <script type="text/javascript">//<![CDATA[
      Alfresco.util.createTwister("${el}-heading", "FolderActions");
   //]]></script>
</#if>
