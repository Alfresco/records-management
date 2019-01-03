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
<script type="text/javascript">//<![CDATA[
   new Alfresco.rm.component.Disposition("${args.htmlid}").setOptions(
   {
      nodeRef: "${(nodeRef!"")?js_string}",
      displayName: "${(displayName!"")?js_string}",
      siteId: "${site!""}",
      dipositionScheduleNodeRef: "${dipositionScheduleNodeRef!""}"
   }).setMessages(
      ${messages}
   );

//]]></script>
<#assign el=args.htmlid>

<div class="disposition">

   <div class="heading">${msg("disposition-schedule.heading")}</div>

<#if hasDispositionSchedule>
   <div>
      <div class="header">
         <div class="title">${msg("title.properties")}</div>
         <div class="buttons">
            <#if allowEditDispositionSchedule>
            <span id="${el}-editproperties-button" class="yui-button inline-button">
               <span class="first-child">
                  <button type="button">${msg("button.edit")}</button>
               </span>
            </span>
            </#if>
         </div>
      </div>

      <div class="properties">
         <div class="field">
            <span class="label">${msg("label.dispositionAuthority")}:</span>
            <span class="value">${(authority!"")?html}</span>
         </div>
         <div class="field">
            <span class="label">${msg("label.dispositionInstructions")}:</span>
            <span class="value">${(instructions!"")?html}</span>
         </div>
         <div class="field">
            <span class="label">${msg("label.appliedTo")}:</span>
            <span class="value"><#if (recordLevelDisposition)>${msg("label.appliedTo.record")}<#else>${msg("label.appliedTo.folder")}</#if></span>
         </div>
         <div class="field">
            <span class="label">${msg("label.unpublishedUpdates")}:</span>
            <span class="value">${unpublishedUpdates?string("Yes", "No")}</span>
         </div>	
         
      </div>

      <div class="header">
         <div class="title">${msg("title.actions")}</div>
         <div class="buttons">
            <#if allowEditDispositionSchedule>
            <span id="${el}-editschedule-button" class="yui-button inline-button">
               <span class="first-child">
                  <button type="button">${msg("button.edit")}</button>
               </span>
            </span>
            </#if>
         </div>
      </div>

      <div id="${el}-actions" class="actions">
      <#if (actions?size > 0)>
         <#list actions as action>
         <div class="action">
            <div class="no">${action.index + 1}</div>
            <div class="more collapsed"><a href="#">${msg("link.description")}</a></div>
            <div class="name">${action.title}</div>
            <div class="description" style="display: none;"><#if (action.description?has_content)>${action.description?html}<#else>${msg("label.nodescription")}</#if></div>
         </div>
         </#list>
      <#else>
         ${msg("label.noactions")}
      </#if>
      </div>
   </div>
<#else>
   <div>
      <div class="header">
         <div class="title">${msg("title.noDispositionSchedule")}</div>
         <div class="buttons">
            <#if allowCreateDispositionSchedule>
            <span id="${el}-createschedule-button" class="yui-button inline-button">
               <span class="first-child">
                  <button type="button">${msg("button.createDispositionSchedule")}</button>
               </span>
            </span>
            </#if>
         </div>
      </div>
   </div>
</#if>

</div>
