<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader />

<@templateBody>
   <div id="alf-hd">
      <@region scope="global" id="share-header" chromeless="true"/>
   </div>
   <div id="bd">
      <div class="yui-t1">
         <div id="yui-main">
            <@region id="references" scope="template" protected=true />
         </div>
      </div>
   </div>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>
