<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[

Alfresco.util.addMessages(${messages}, "Alfresco.module.PropertyPicker");

var cmp = Alfresco.util.ComponentManager.get("${el}");

<#if transientContentProperties??>
cmp.setOptions(
{
   transientProperties: {
      "d:content" : ${transientContentProperties}
   }
   <#if (classFilter??)>,classFilter: ${classFilter}</#if>
});
</#if>

var tabs = cmp.options.tabs,
   treeNodes = tabs[Alfresco.util.arrayIndex(tabs, "properties", "id")].treeNodes,
   allIndex = Alfresco.util.arrayIndex(treeNodes, "all", "id"),
   aspectsIndex = Alfresco.util.arrayIndex(treeNodes, "aspects", "id"),
   typesIndex = Alfresco.util.arrayIndex(treeNodes, "types", "id");

treeNodes[allIndex].listItems.url = "{url.proxy}api/rm/properties?siteId=" + Alfresco.constants.SITE;
treeNodes[aspectsIndex].treeNodes.url = "{url.proxy}api/rm/classes?cf=aspect&siteId=" + Alfresco.constants.SITE;
treeNodes[typesIndex].treeNodes.url = "{url.proxy}api/rm/classes?cf=aspect&siteId=" + Alfresco.constants.SITE;

//]]></script>