<#include "/org/alfresco/components/documentlibrary/include/documentlist.lib.ftl" />
<@documentlistTemplate>
<script type="text/javascript">//<![CDATA[
   new Alfresco.rm.component.DocumentList("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      containerId: "${args.container!"documentLibrary"}",
      rootNode: "${rootNode!"null"}",
      usePagination: ${(args.pagination!false)?string},
      sortAscending: ${(preferences.sortAscending!true)?string},
      sortField: "${(preferences.sortField!"cm:name")?js_string}",
      showFolders: ${(preferences.showFolders!true)?string},
      simpleView: ${(preferences.simpleView!false)?string},
      highlightFile: "${(page.url.args["file"]!"")?js_string}",
      replicationUrlMapping: ${replicationUrlMappingJSON!"{}"},
      repositoryBrowsing: ${(rootNode??)?string},
      useTitle: ${(useTitle!true)?string},
      userIsSiteManager: ${(userIsSiteManager!false)?string}
   }).setMessages(
      ${messages}
   );
//]]></script>
</@>
