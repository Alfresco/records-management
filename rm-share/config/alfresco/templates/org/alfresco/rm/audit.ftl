<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader >
   <@script type="text/javascript" src="${url.context}/res/rm/js/alfresco-rm.js"></@script>
   <@script type="text/javascript" src="${url.context}/res/rm/js/event-delegator.js"></@script>
</@templateHeader>

<@templateBody>
   <div id="bd">
      <div id="audit-popup-log" class="audit-popup-log">
         <div id="yui-main">
            <@region id="rmaudit" scope="template" protected=true />
         </div>
      </div>
   </div>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>