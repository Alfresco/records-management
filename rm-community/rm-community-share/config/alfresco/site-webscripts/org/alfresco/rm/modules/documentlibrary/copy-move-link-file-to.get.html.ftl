<#assign el=args.htmlid?html>
<div id="${el}-dialog" class="site-folder">
   <div id="${el}-title" class="hd"></div>
   <div class="bd">
      <div class="yui-g">
         <h2 id="${el}-header">${msg("header")}</h2>
      </div>
      <div id="${el}-treeview" class="treeview"></div>
      <div class="bdft">
         <input type="button" id="${el}-ok" value="${msg("button.ok")}" />
         <input type="button" id="${el}-cancel" value="${msg("button.cancel")}" />
      </div>
   </div>
</div>
<#assign treeConfig = config.scoped["DocumentLibrary"]["tree"]!>
<#if treeConfig.getChildValue??>
   <#assign evaluateChildFoldersSite = treeConfig.getChildValue("evaluate-child-folders")!"true">
   <#assign maximumFolderCountSite = treeConfig.getChildValue("maximum-folder-count")!"-1">
</#if>
<script type="text/javascript">//<![CDATA[
   Alfresco.util.addMessages(${messages}, "Alfresco.rm.module.CopyMoveLinkFileTo");
   Alfresco.util.ComponentManager.get("${el}").setOptions(
   {
      evaluateChildFolders: ${evaluateChildFolders!"true"},
      maximumFolderCount: ${(maximumFolderCount!"-1")}
   });
//]]></script>
