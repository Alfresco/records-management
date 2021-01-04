<#--
 #%L
 Alfresco Records Management Module
 %%
 Copyright (C) 2005 - 2021 Alfresco Software Limited
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
<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/rules/rules-none.css" group="rules"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/rules/rules-none.js" group="rules"/>
   <@script src="${url.context}/res/rm/components/rules/rules-none.js" group="rules"/>
</@>

<@markup id="widgets">
   <@createWidgets group="rules"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#include "/org/alfresco/include/alfresco-macros.lib.ftl" />
      <#assign el=args.htmlid>
      <div id="${el}-body" class="rules-none">
         <div id="${el}-inheritedRules" class="rules-info theme-bg-color-2 theme-border-3 hidden">
            <span>${msg("label.folderInheritsRules")}</span>
         </div>
         <div class="dialog-options theme-bg-color-6 theme-border-3">
            <h2>${msg("header")}</h2>
            <div class="dialog-option">
               <#assign href>rule-edit?unfiled=${page.url.args.unfiled}&nodeRef=${(page.url.args.nodeRef!"")?url}</#assign>
               <a href="${siteURL(href)}">${msg("header.create-rule")}</a>
               <div>${msg("text.create-rule")}</div>
            </div>
            <div class="dialog-option">
               <a id="${el}-linkToRuleSet" href="#">${msg("header.link-to-rule-set")}</a>
               <div>${msg("text.link-to-rule-set")}</div>
            </div>
         </div>
      </div>
   </@>
</@>
