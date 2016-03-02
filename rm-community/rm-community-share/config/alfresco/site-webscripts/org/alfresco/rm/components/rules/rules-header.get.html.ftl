<#--
 #%L
 Alfresco Records Management Module
 %%
 Copyright (C) 2005 - 2016 Alfresco Software Limited
 %%
 This file is part of the Alfresco software. 
 
 If the software was purchased under a paid Alfresco license, the terms of 
 the paid license agreement will prevail.  Otherwise, the software is 
 provided under the following open source license terms:
 
 Alfresco is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 Alfresco is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.
 
 You should have received a copy of the GNU Lesser General Public License
 along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 #L%
-->
<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/rules/rules-header.css" group="rules"/>
   <@link href="${url.context}/res/components/form/form.css" group="rules"/>
   <@link href="${url.context}/res/modules/documentlibrary/global-folder.css" group="rules"/>
   <@link href="${url.context}/res/modules/rules/rules-picker.css" group="rules"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/rules/rules-header.js" group="rules"/>
   <#-- Modified for RM -->
   <@script src="${url.context}/res/rm/components/rules/rules-header.js" group="rules"/>
   <@script src="${url.context}/res/modules/documentlibrary/global-folder.js" group="rules"/>
   <@script src="${url.context}/res/modules/rules/rules-picker.js" group="rules"/>
   <@script src="${url.context}/res/rm/components/rules/rules-picker.js" group="rules"/>
</@>

<@markup id="widgets">
   <@createWidgets group="rules"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid>
      <#assign inheritRulesClass="off">
      <#if inheritRules>
         <#assign inheritRulesClass="on">
      </#if>
      <div id="${el}-body" class="rules-header">
         <div class="yui-g">
            <div class="yui-u first rules-title">
               <h1><span id="${el}-title"></span>: ${msg("header.rules")}</h1>
            </div>
            <div class="yui-u rules-actions">
               <span id="${el}-inheritButtonContainer" class="inherit inherit-${inheritRulesClass}">
                     <span id="${el}-inheritButton" class="yui-button yui-push-button">
                        <span class="first-child">
                           <button>${msg("button.inherit." + inheritRulesClass)}</button>
                        </span>
                     </span>
               </span>
               <span id="${el}-actions" class="hidden">
                  <span class="separator">&nbsp;</span>
                  <button class="new" id="${el}-newRule-button" tabindex="0">${msg("button.new-rule")}</button>
                  <span class="hidden">
                     <button class="copy" id="${el}-copyRuleFrom-button" tabindex="0">${msg("button.copy-rule-from")}</button>
                  </span>
                  <button class="run" id="${el}-runRules-menu" tabindex="0">${msg("menu.run")}</button>
                  <select class="run-menu" id="${el}-runRules-options">
                     <option value="run">${msg("menu.option.run")}</option>
                     <option value="run-recursive">${msg("menu.option.run-recursive")}</option>
                  </select>
               </span>
            </div>
         </div>
      </div>
   </@>
</@>
