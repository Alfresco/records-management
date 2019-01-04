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
<#assign isTrue=false>
<#if field.value?exists && field.value?is_boolean><#assign isTrue=field.value></#if>

<div class="form-field">
   <#if form.mode == "view">
      <div class="viewmode-field">
         <span class="viewmode-label">${field.label?html}:</span>
         <span class="viewmode-value"><#if isTrue>${msg("form.control.checkbox.yes")}<#else>${msg("form.control.checkbox.no")}</#if></span>
      </div>
   <#else>
      <input id="${fieldHtmlId}" type="hidden" name="${field.name}" value="<#if isTrue>true<#else>false</#if>" />
      <input id="${fieldHtmlId}-entry" type="checkbox" name="-" <#if field.description?exists>title="${field.description}"</#if>
             <#if isTrue> value="true" checked="checked"</#if> <#if field.disabled>disabled="true"</#if> 
             <#if field.control.params.styleClass?exists>class="${field.control.params.styleClass}"</#if> 
             onchange='javascript:YAHOO.util.Dom.get("${fieldHtmlId}").value=YAHOO.util.Dom.get("${fieldHtmlId}-entry").checked;YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);' />
      <label for="${fieldHtmlId}-entry" class="checkbox">${field.label?html}<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
   </#if>
</div>
