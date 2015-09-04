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