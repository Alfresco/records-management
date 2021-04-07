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
   new Alfresco.rm.component.RMEvents("${args.htmlid}").setOptions(
   {
      eventTypes: { <#list eventTypes as eventType>"${eventType.eventTypeName}" : "${eventType.eventTypeDisplayLabel}"<#if eventType_has_next>, </#if></#list> }
   }).setMessages(${messages});
//]]></script>

<#assign el=args.htmlid>
<div id="${el}-body" class="rm-events">

   <!-- View panel -->
   <div id="${el}-view" class="hidden">
      <div class="yui-g">
         <div class="yui-u first title">
            ${msg("label.viewevents.title")}
         </div>
         <div class="yui-u buttons">
            <span class="yui-button yui-push-button" id="${el}-newevent-button">
               <span class="first-child"><button>${msg("button.newevent")}</button></span>
            </span>
         </div>
      </div>
      <div id="${el}-events" class="rm-events-list"></div>
   </div>

   <!-- Edit panel -->
   <div id="${el}-edit" class="hidden">
      <div class="yui-g">
         <div class="yui-u first title">
            <span id="${el}-create-title">${msg("label.createevent.title")}:&nbsp;</span>
            <span id="${el}-edit-title">${msg("label.editevent.title")}:&nbsp;</span>
         </div>
         <div class="yui-u caption">
            <span class="mandatory-indicator">*</span>${msg("label.required")}
         </div>
      </div>

      <form id="${el}-edit-form" method="" action="">
         <input id="${el}-eventName" name="eventName" type="hidden" value=""/>
         <div class="edit-main">
            <div class="header-bar">
               <span>${msg("label.general")}:</span>
            </div>

            <!-- Label -->
            <div>
               <span class="crud-label">${msg("label.label")}: *</span>
            </div>
            <div>
               <input class="crud-input" id="${el}-eventDisplayLabel" name="eventDisplayLabel" type="text" maxlength="255"/>
            </div>

            <!-- Type -->
            <div>
               <span class="crud-label">${msg("label.type")}: *</span>
            </div>
            <div>
               <select class="crud-input type" id="${el}-eventType" name="eventType">
                  <#list eventTypes as eventType>
                  <option value="${eventType.eventTypeName}">${eventType.eventTypeDisplayLabel}</option>
                  </#list>
               </select>
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
