<@markup id="rm-widgets" target="widgets" action="after" scope="global">
   <@processJsonModel group="documentlibrary"/>
</@markup>

<@markup id="rm-js" target="js" action="after" scope="global">
   <@script type="text/javascript" src="${url.context}/res/components/documentlibrary/rm-documentlist-view-detailed.js" group="documentlibrary"/>
   <@script type="text/javascript" src="${url.context}/res/components/documentlibrary/rm-documentlist-view-simple.js" group="documentlibrary"/>
</@markup>