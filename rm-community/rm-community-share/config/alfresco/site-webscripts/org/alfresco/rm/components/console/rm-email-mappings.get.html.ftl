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
<script type="text/javascript">//<![CDATA[
   new Alfresco.rm.component.PropertyMenu('${htmlid}-rmproperty-button').setOptions(
   {
      showIMAPFields: true,
      groups: ${jsonUtils.toJSONString(groups)}
   });
   new Alfresco.rm.component.RMEmailMappings('${htmlid}').setOptions(
   {
      email:
      [
      <#list emailmapkeys as emailmapkey>
         '${emailmapkey}'<#if emailmapkey_has_next>,</#if>
      </#list>
      ]
   }).setMessages(${messages});
//]]</script>

<div id="${htmlid}" class="rm-email-mappings">
   <div>
      <h2>${msg('label.email-mappings')}</h2>
      <div>
         <span>${msg('label.map')}</span>
         <input type="text" name="emailProperty-text" value="" id="emailProperty-text" style="width:200px;"/>
         <span id="${htmlid}-emailProperty-button" class="align-left yui-button yui-push-button">
            <span class="first-child">
               <button id="${htmlid}-emailProperty-button" type="button" class="thin-button"><img src="${page.url.context}/res/components/images/expanded.png" title="${msg('label.select-email')}"/></button>
            </span>
         </span>
         <div id="email-menu-container"></div>
         <span>${msg('label.to')}</span>
         <button id="${htmlid}-rmproperty-button">${msg("message.select")}&nbsp;&#9662;</button>
         <span id="${htmlid}-add-mapping-button" class="align-left yui-button yui-push-button">
            <span class="first-child">
               <button id="${htmlid}-add-mapping-button" type="button">${msg('label.add')}</button>
            </span>
         </span>
      </div>
   </div>
   <div id="emailMappings-list" class="rm-email-mappings-list">
      <ul>
      </ul>
   </div>
</div>
</#if>
