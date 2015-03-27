<#include "/org/alfresco/include/alfresco-template.ftl" />
<#import "/org/alfresco/import/alfresco-layout.ftl" as layout />
<@templateHeader />

<@templateBody>
<div id="bd">
   <@region id="fileplan-report" scope="template" protected=true />
   <script type="text/javascript">//<![CDATA[
       window.print();
   //]]></script>
</div>
</@>
