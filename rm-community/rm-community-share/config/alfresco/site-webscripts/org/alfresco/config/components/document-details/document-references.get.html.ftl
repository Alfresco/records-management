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
<#if docName??>
   <#include "/org/alfresco/include/alfresco-macros.lib.ftl" />
   <#assign el=args.htmlid?js_string/>
   <script type="text/javascript">//<![CDATA[
      new Alfresco.rm.component.DocumentReferences("${el}").setOptions(
      {
         siteId: "${(site!"")?js_string}",
         containerId: "${(container!"")?js_string}",
         nodeRef : "${nodeRef?js_string}"
      }).setMessages(
         ${messages}
      );
   //]]></script>

   <div id="${el}-body" class="document-references document-details-panel">

      <h2 id="${el}-heading" class="thin dark">
         ${msg("label.heading")}
         <#if allowEditReferences>
         <span class="alfresco-twister-actions">
            <a href="${siteURL("rm-references?nodeRef="+nodeRef+"&parentNodeRef="+parentNodeRef+"&docName="+docName)}" class="edit" title="${msg("label.manage-references")}">&nbsp;</a>
         </span>
         </#if>
      </h2>

      <div class="reflist panel-body">
         <h3 class="thin dark">${msg('label.references-to-this')}</h3>
         <hr/>
      <#if (references.toThisNode?size > 0)>
         <ul>
         <#list references.toThisNode as ref>
            <li>${ref.label?html} <a href="${url.context}/page/site/rm/document-details?nodeRef=${ref.targetRef}"><span>${docNames.to[ref_index]}</span></a></li>
         </#list>
         </ul>
      <#else>
         <p class="no-ref-messages">${msg('message.no-messages')}</p>
      </#if>
         <h3 class="thin dark">${msg('label.references-from-this')}</h3>
         <hr/>
      <#if (references.fromThisNode?size > 0)>
         <ul>
         <#list references.fromThisNode as ref>
            <li>${ref.label?html} <a href="${url.context}/page/site/rm/document-details?nodeRef=${ref.targetRef}"><span>${docNames.from[ref_index]}</span></a></li>
         </#list>
         </ul>
      <#else>
         <p class="no-ref-messages">${msg('message.no-messages')}</p>
      </#if>
      </div>

      <script type="text/javascript">//<![CDATA[
         Alfresco.util.createTwister("${el}-heading", "RecordsDocumentActions");
      //]]></script>

   </div>
</#if>
