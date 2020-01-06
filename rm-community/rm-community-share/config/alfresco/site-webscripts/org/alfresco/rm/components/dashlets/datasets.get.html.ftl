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
<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
   new Alfresco.rm.dashlet.DataSet("${el}").setMessages(${messages});
//]]></script>
<div class="dashlet">
   <div class="title">${msg("dataSet.header.title")}</div>
   <#if isRmSite>
      <#if isAdmin>
         <div class="body dataset-body">
            <label class="dataset-label">${msg("dataSet.label")}:</label>
            <span class="align-left yui-button yui-menu-button" id="${el}-dataSets">
               <span class="first-child">
                  <button type="button" tabindex="0"></button>
               </span>
            </span>
            <select id="${el}-dataSets-menu">
               <#list data.datasets as dataset>
               {
               <#if !("${dataset.isLoaded}"?eval)>
                  <option value="${dataset.id}">${dataset.label}</option>
               <#else>
                  <option value="${dataset.id}" disabled="disabled">${dataset.label}</option>
               </#if>
               }<#if dataset_has_next>,</#if>
               </#list>
            </select>
            <span id="${el}-import-button" class="align-left yui-button yui-push-button">
               <span class="first-child">
                  <button id="${el}-import-button" type="button" disabled="true">${msg("dataSet.importButton.text")}</button>
               </span>
            </span>
         </div>
      <#else>
         <div class="body theme-color-1">
            <div class="dataset-not-rm-admin">
               ${msg("dataset.not-rm-admin")}
            </div>
         </div>
      </#if>
   <#else>
      <div class="body theme-color-1">
         <div class="dataset-not-rm-admin">
            ${msg("dataset.not-rm-site")}
         </div>
      </div>
   </#if>
</div>
