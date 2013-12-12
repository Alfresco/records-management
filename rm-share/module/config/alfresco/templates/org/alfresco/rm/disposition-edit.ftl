<#include "/org/alfresco/include/alfresco-template.ftl" />
<#import "/org/alfresco/import/alfresco-layout.ftl" as layout />
<@templateHeader />

<@templateBody>
   <div id="alf-hd">
      <@region scope="global" id="share-header" chromeless="true"/>
      <h1 class="sub-title"><#if page.titleId??>${msg(page.titleId)!page.title}<#else>${page.title}</#if></h1>
   </div>
   <div id="bd">
      <@region id="disposition-edit" scope="template" protected=true />
   </div>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>
