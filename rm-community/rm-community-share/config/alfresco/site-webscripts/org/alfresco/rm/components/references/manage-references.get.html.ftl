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
<script type="text/javascript" charset="utf-8">
   new Alfresco.rm.component.ManageReferences("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      nodeRef: "${(page.url.args.nodeRef!"")?js_string}",
      parentNodeRef: "${(page.url.args.parentNodeRef!"")?js_string}",
      docName: "${(page.url.args.docName!"")?js_string}"
   }).setMessages(${messages});
</script>
<div id="${args.htmlid}" class="rm-manage-references">
   <h2 class="title">${msg("label.title",'${page.url.args.docName!""}')?html}</h2>
   <button type="button" id="manageReferences-newReference" name="manageReferences-newReference" value="newRef" class="newRef">${msg('label.new-reference')}</button>

   <h3 class="subtitle">${msg('label.references-to')}</h3>
   <#-- 'from' is from this record to other records -->
   <#if (references.from?size > 0)>
   <ol id="${args.htmlid}-from">
      <#list references.from as ref>
      <li class="fromRef"><span>${ref.label?html} <a href="${url.context}/page/site/rm/document-details?nodeRef=${ref.targetRef}" title="${ref.refDocName}">${ref.refDocName}</a></span><button id="deleteReference-but-from-${ref_index}" class="deleteRef refAction" value="${ref.refId}">${msg('label.delete')}</button></li>
      </#list>
   </ol>
   </#if>
   <p id="from-no-refs" <#if (references.from?size == 0)>class="active"</#if>">${msg('label.no-references')}</p>

   <h3 class="subtitle">${msg('label.references-from')}</h3>
   <#-- 'to' is to this record from other records -->
   <#if (references.to?size > 0)>
   <ol id="${args.htmlid}-to">
      <#list references.to as ref>
      <li class="toRef"><span>${ref.label?html} <a href="${url.context}/page/site/rm/document-details?nodeRef=${ref.sourceRef}" title="${ref.refDocName}">${ref.refDocName}</a></span><button id="deleteReference-but-to-${ref_index}" class="deleteRef refAction" value="${ref.refId}">${msg('label.delete')}</button></li>
      </#list>
   </ol>
   </#if>
   <p id="to-no-refs" <#if (references.to?size == 0)>class="active"</#if>">${msg('label.no-references')}</p>
   <div class="componentFtr">
      <button id="manageReferences-doneRef" class="doneRef refAction" value="done">${msg('label.done')}</button>
   </div>
</div>
