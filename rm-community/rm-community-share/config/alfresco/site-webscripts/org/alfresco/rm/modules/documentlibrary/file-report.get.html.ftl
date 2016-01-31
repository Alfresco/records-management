<#assign el=args.htmlid?html>
<div id="${el}-dialog" class="site-folder">
   <div id="${el}-title" class="hd"></div>
   <div class="bd">
      <div class="file-report-unfiled-records">
         <input type="checkbox" id="${el}-unfiled-records" checked="true" />
         <label for="${el}-unfiled-records">${msg("label.unfiled-records")}</label>
         <hr>
      </div>
      <div class="yui-g">
         <h2 id="${el}-header">${msg("header")}</h2>
      </div>
      <div id="${el}-treeview" class="treeview file-report-treeview-disabled"></div>
      <div class="bdft">
         <input type="button" id="${el}-ok" value="${msg("button.ok")}" />
         <input type="button" id="${el}-cancel" value="${msg("button.cancel")}" />
      </div>
   </div>
</div>
<#assign treeConfig = config.scoped["DocumentLibrary"]["tree"]!>
<#if treeConfig.getChildValue??><#assign evaluateChildFolders = treeConfig.getChildValue("evaluate-child-folders")!"true"></#if>
<script type="text/javascript">//<![CDATA[
   Alfresco.util.addMessages(${messages}, "Alfresco.rm.module.FileReport");
   Alfresco.util.ComponentManager.get("${el}").options.evaluateChildFolders = ${evaluateChildFolders!"true"};
//]]></script>
