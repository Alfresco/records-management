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
<#--
Added the readonly attribute and the colors for the style. If you set the read-only parameter
in rm-share-workflow-form-config.xml for a field the control template becomes disabled which
means the user will not be able to copy any text form the textarea.
-->

<#if field.control.params.rows??><#assign rows=field.control.params.rows><#else><#assign rows=3></#if>
<#if field.control.params.columns??><#assign columns=field.control.params.columns><#else><#assign columns=60></#if>

<div class="form-field">
   <#if form.mode == "view">
      <div class="viewmode-field">
         <#if field.mandatory && field.value == "">
            <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
         </#if>
         <span class="viewmode-label">${field.label?html}:</span>
         <#if field.control.params.activateLinks?? && field.control.params.activateLinks == "true">
            <#assign fieldValue=field.value?html?replace("((http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?\\^=%&:\\/~\\+#]*[\\w\\-\\@?\\^=%&\\/~\\+#])?)", "<a href=\"$1\" target=\"_blank\">$1</a>", "r")>
         <#else>
            <#assign fieldValue=field.value?html>
         </#if>
         <span class="viewmode-value"><#if fieldValue == "">${msg("form.control.novalue")}<#else>${fieldValue}</#if></span>
      </div>
   <#else>
      <label for="${fieldHtmlId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
      <textarea id="${fieldHtmlId}" name="${field.name}" rows="${rows}" cols="${columns}" tabindex="0" readonly="true"
                <#if field.description??>title="${field.description}"</#if>
                <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
                <#assign readOnlyColorStyle="color: #808080;background-color: #D4D0C8">
                <#if field.control.params.style??>
                   style="${readOnlyColorStyle};${field.control.params.style}"
                <#else>
                   style="${readOnlyColorStyle}"
               </#if>
            <#if field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if>>${field.value?html}</textarea>
      <@formLib.renderFieldHelp field=field />
   </#if>
</div>
