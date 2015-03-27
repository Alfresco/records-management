<@markup id="rm-js" target="js" action="replace" scope="global">
   <@script src="${url.context}/res/components/manage-permissions/manage-permissions.js" group="manage-permissions"/>
   <@script src="${url.context}/res/components/people-finder/authority-finder.js" group="manage-permissions"/>
   <#-- FIXME!!! action="after" does not work properly. Check after upgrading the Alfresco dependency. Current dependency is 4.2.0 -->
   <@script src="${url.context}/res/rm/components/manage-permissions/manage-permissions.js" group="manage-permissions"/>
   <@script src="${url.context}/res/rm/components/people-finder/authority-finder.js" group="manage-permissions"/>
</@markup>