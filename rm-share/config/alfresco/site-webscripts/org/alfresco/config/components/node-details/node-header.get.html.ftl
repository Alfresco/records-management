<@markup id="rm-css" target="css" action="after" scope="global">
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/rm/components/document-details/node-header.css" group="document-details"/>
</@>

<@markup id="rm-widgets" target="html" action="before" scope="global">
   <#if isClassified>
      <div class="status-banner theme-bg-color-2 theme-border-4 classified-banner">
         <span class="classified-info">${msg("banner.classification.info")}: ${node.properties["clf:currentClassification"].label}</span>
      </div>
   </#if>
</@markup>