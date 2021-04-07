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
<script type="text/javascript" charset="utf-8">
    new Alfresco.rm.component.RMAudit('${htmlid}-audit').setOptions(
    {
       <#if (nodeRef?exists)>nodeRef: '${nodeRef}',</#if>
       siteId: "${page.url.templateArgs.site}",
       containerId: "${template.properties.container!"documentLibrary"}",
       viewMode: Alfresco.rm.component.RMAudit.VIEW_MODE_COMPACT,
       capabilities: ${capabilities}
    }).setMessages(${messages});
</script>
<div id="${htmlid}-audit">
   <div class="yui-gc">
      <div class="yui-u first">
         <#-- for a specified noderef -->
         <#if (page.url.args.nodeName??)>
            <h1>${msg("label.title-for", page.url.args.nodeName)?html}</h1>
         <#else>
            <h1>${msg("label.title")}</h1>
         </#if>
      </div>
      <div class="auditActions">
         <button id="${htmlid}-audit-export" name="${htmlid}-audit-export" class="audit-export">${msg("label.button-export")}</button>
         <button id="${htmlid}-audit-file-record" name="${htmlid}-audit-file-record" class="audit-file-record">${msg("label.button-file-record")}</button>
      </div>
      <div style="clear:both;"/>
   </div>
   <div class="audit-info">
   <#-- only for full log (not noderef) -->
   <#if (!page.url.args.nodeName??)>
      <span class="label">${msg('label.property')}:</span>
      <span class="value">${msg('label.all')}</span>
      <span class="label">${msg('label.user')}:</span>
      <span class="value">${msg('label.all')}</span>
      <span class="label">${msg('label.event')}:</span>
      <span class="value">${msg('label.all')}</span>
   </#if>
   </div>
   <#list auditStatus.entries as x>
      <div class="audit-entry">
         <div class="audit-entry-header">
            <span class="label">${msg('label.timestamp')}:</span>
            <span class="value">${x.timestampDate?datetime?string("EEE MMM dd yyyy HH:mm:ss 'GMT'Z")}</span>
            <span class="label">${msg('label.user')}:</span>
            <span class="value">${x.fullName?html}</span>
            <span class="label">${msg('label.event')}:</span>
            <span class="value">${x.event?html}</span>
         </div>
         <div class="audit-entry-node">
            <span class="label">${msg('label.identifier')}:</span><span class="value">${x.identifier?html}</span>
            <span class="label">${msg('label.type')}:</span><span class="value">${x.nodeType?html}</span>
            <span class="label">${msg('label.location')}:</span><span class="value">${x.path?html}</span>
         </div>
         <#if (x.changedValues?size >0)>
            <table class="changed-values-table" cellspacing="0">
               <thead>
                  <tr>
                     <th>${msg('label.property')}</th>
                     <th>${msg('label.previous-value')}</th>
                     <th>${msg('label.new-value')}</th>
                  </tr>
               </thead>
               <tbody>
                  <#list x.changedValues as v>
                  <tr>
                     <td>${v.name?html}</td>
                  <#if (v.previous?? && v.previous != "")>
                     <td>${v.previous?html}</td>
                  <#else>
                     <td>${msg('label.no-previous')?html}</td>
                  </#if>
                  <#if (v.new?? && v.new != "")>
                     <td>${v.new?html}</td>
                  <#else>
                     <td>${msg('label.no-next')?html}</td>
                  </#if>
                  </tr>
                  </#list>
               </tbody>
            </table>
         </#if>
      </div>
   </#list>
</div>
