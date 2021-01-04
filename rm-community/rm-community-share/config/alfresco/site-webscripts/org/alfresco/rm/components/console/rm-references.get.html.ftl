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
<#if !hasAccess>
   <#include "./rm-console-access.ftl">
<#else>
<!--[if IE]>
<iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe>
<![endif]-->
<input id="yui-history-field" type="hidden" />

<script type="text/javascript">//<![CDATA[
   new Alfresco.rm.component.RMRecordsReferences("${args.htmlid}").setMessages(
      ${messages}
   );
//]]></script>

<#assign el=args.htmlid>
<div id="${el}-body" class="rm-references">

   <!-- View panel -->
   <div id="${el}-view" class="hidden">
      <div class="yui-g">
         <div class="yui-u first title">
            ${msg("label.view-references-title")}
         </div>
         <div class="yui-u buttons">
            <span class="yui-button yui-push-button" id="${el}-newreference-button">
               <span class="first-child"><button>${msg("button.newreference")}</button></span>
            </span>
         </div>
      </div>
      <div id="${el}-references" class="rm-references-list"></div>
   </div>

   <!-- Edit panel -->
   <div id="${el}-edit" class="hidden">
      <div class="title">
         <span id="${el}-create-title">${msg("label.create-references-title")}:&nbsp;</span>
         <span id="${el}-edit-title">${msg("label.edit-references-title")}:&nbsp;</span>
      </div>

      <form id="${el}-edit-form" method="" action="">
         <div class="edit-main">
            <div class="header-bar">
               <span>${msg("label.general")}:</span>
            </div>

            <!-- General -->
            <div class="field-row">
               <span>${msg("label.type")}:</span>
            </div>
            
            <!-- Bi-directional -->
            <div id="${el}-bidirectional-section">
               <div class="field-row">
                  <input id="${el}-type-bidirectional" name="referenceType" type="radio" value="bidirectional"/>
                  <label for="${el}-type-bidirectional">${msg("label.bidirectional")}</label>
               </div>
               <div>
                  <span class="crud-label">${msg("label.label")}:</span>
               </div>
               <div>
                  <input class="crud-input" id="${el}-bidirectional-label" name="label" type="text"/>
               </div>
            </div>

            <!-- Parent / Child -->
            <div id="${el}-parentchild-section">
               <div class="field-row">
                  <input id="${el}-type-parentchild" name="referenceType" type="radio" value="parentchild"/>
                  <label for="${el}-type-parentchild">${msg("label.parentchild")}</label>
               </div>
               <div>
                  <span class="crud-label">${msg("label.source")}:</span>
               </div>
               <div>
                  <input class="crud-input" id="${el}-parentchild-source" name="source" type="text"/>
               </div>
               <div>
                  <span class="crud-label">${msg("label.target")}:</span>
               </div>
               <div>
                  <input class="crud-input" id="${el}-parentchild-target" name="target" type="text"/>
               </div>
            </div>
            
         </div>

         <!-- Buttons -->
         <div class="button-row">
            <span class="yui-button yui-push-button" id="${el}-save-button">
               <span class="first-child"><button>${msg("button.save")}</button></span>
            </span>
            <span class="yui-button yui-push-button" id="${el}-cancel-button">
               <span class="first-child"><button>${msg("button.cancel")}</button></span>
            </span>
         </div>

      </form>
   </div>

</div>
</#if>
