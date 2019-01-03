<#--
 #%L
 Alfresco Records Management Module
 %%
 Copyright (C) 2005 - 2019 Alfresco Software Limited
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
<#assign el=args.htmlid?html>
<div id="${el}-dialog" class="site-folder">
   <div id="${el}-title" class="hd"></div>
   <div class="bd">
      <div class="yui-g">
         <h2 id="${el}-header">${msg("header")}</h2>
      </div>
      <div id="${el}-treeview" class="treeview"></div>
      <div class="bdft">
         <input type="button" id="${el}-ok" value="${msg("button.ok")}" />
         <input type="button" id="${el}-cancel" value="${msg("button.cancel")}" />
      </div>
   </div>
</div>
<#assign treeConfig = config.scoped["DocumentLibrary"]["tree"]!>
<#if treeConfig.getChildValue??>
   <#assign evaluateChildFoldersSite = treeConfig.getChildValue("evaluate-child-folders")!"true">
   <#assign maximumFolderCountSite = treeConfig.getChildValue("maximum-folder-count")!"-1">
</#if>
<script type="text/javascript">//<![CDATA[
   Alfresco.util.addMessages(${messages}, "Alfresco.rm.module.CopyMoveLinkFileTo");
   Alfresco.util.ComponentManager.get("${el}").setOptions(
   {
      evaluateChildFolders: ${evaluateChildFolders!"true"},
      maximumFolderCount: ${(maximumFolderCount!"-1")}
   });
//]]></script>
