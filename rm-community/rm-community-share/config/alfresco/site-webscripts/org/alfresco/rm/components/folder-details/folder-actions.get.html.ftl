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

<#include "/org/alfresco/components/component.head.inc">

<@standalone>
   <@markup id="css" >
      <#-- CSS Dependencies -->
      <@link href="${url.context}/res/components/folder-details/folder-actions.css" group="folder-details"/>
   </@>
   
   <@markup id="js">
      <#-- JavaScript Dependencies -->
      <@script src="${url.context}/res/components/folder-details/folder-actions.js" group="folder-details"/>
      <@script src="${url.context}/res/rm/components/folder-details/folder-actions.js" group="folder-details"/>
   </@>
   
   <@markup id="widgets">
      <@createWidgets group="folder-details"/>
      <@inlineScript group="folder-details">
         YAHOO.util.Event.onContentReady("${args.htmlid?js_string}-heading", function() {
            Alfresco.util.createTwister("${args.htmlid?js_string}-heading", "FolderActions");
         });
      </@>
   </@>
   
   <@markup id="html">
      <@uniqueIdDiv>
         <#if folderDetailsJSON??>
            <#assign el=args.htmlid?html>
            <div id="${el}-body" class="folder-actions folder-details-panel">
               <h2 id="${el}-heading" class="thin dark">
                  ${msg("heading")}
               </h2>
               <div class="doclist">
                  <div id="${el}-actionSet" class="action-set"></div>
               </div>
            </div>
         </#if>
      </@>
   </@>
</@>
