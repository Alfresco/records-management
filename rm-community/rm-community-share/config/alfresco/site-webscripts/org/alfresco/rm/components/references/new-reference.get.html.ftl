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
<#assign controlId = htmlid + "-cntrl">
<#assign pickerId = controlId + "-picker">
<script type="text/javascript">//<![CDATA[
   new Alfresco.rm.component.NewReference("${args.htmlid}").setOptions(
   {
      controlId: "${controlId}",
      pickerId: "selection",
      nodeRef: "${(page.url.args.nodeRef)?js_string}",
      siteId: "${page.url.templateArgs.site!""}",
      parentNodeRef: "${(page.url.args.parentNodeRef)?js_string}",
      docName: "${(page.url.args.docName!"")?js_string}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div class="rm-new-reference">
   <div class="heading">
      ${msg("label.title-new-reference")}
   </div>
</div>

<div class="form-container">
   <div class="caption"><span class="mandatory-indicator">*</span>${msg('label.required')}</div>
   <div class="form-fields" id="template_x002e_dod5015-edit-metadata_x002e_edit-metadata-form-fields">
      <div class="form-field">
         <label for="new-ref-name">${msg("label.name")}:<span class="mandatory-indicator">*</span></label>
         <input id="new-ref-name" name="new-ref-name" value="" type="text">
      </div>

      <div class="form-field">
         <label for="${controlId}-showPicker-button">${msg("label.record-link")}:<span class="mandatory-indicator">*</span></label>
         <p id="selection"></p>
         <button id="${htmlid}-docPicker-showPicker-button" name="${htmlid}-docPicker-showPicker-button" >${msg("label.select")}</button>
      </div>

      <div class="form-field">
         <label for="record-rel">${msg("label.record-rel")}:<span class="mandatory-indicator">*</span></label>
         <select name="record-rel" id="record-rel">
            <#list reference_types as ref>
            <#if (ref.label?exists)>
               <option value="${ref.refId}">${ref.label?html}</option>
            <#else>
               <option value="${ref.refId}">${ref.source?html} / ${ref.target?html}</option>
            </#if>
            </#list>
         </select>
      </div>
   </div>
   <div class="form-buttons">
      <span class="submitCreate">
         <span class="yui-button yui-push-button" id="${htmlid}-create">
            <span class="first-child"><button>${msg('label.create')}</button></span>
         </span>
      </span>
      <span class="cancelCreate">
         <span class="yui-button yui-push-button" id="${htmlid}-cancel">
            <span class="first-child"><button>${msg('label.cancel')}</button></span>
         </span>
      </span>
   </div>
   <input type="hidden" name="${controlId}" value="" id="${controlId}" />
   </form>
</div>
