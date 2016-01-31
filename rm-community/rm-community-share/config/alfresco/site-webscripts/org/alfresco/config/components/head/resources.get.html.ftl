<@markup id="rm-resources" target="resources" action="after" scope="global">
   <@script src="${url.context}/res/rm/js/alfresco-rm.js" group="template-common"/>
   <@script src="${url.context}/res/rm/js/event-delegator.js" group="template-common"/>
   <@script src="${url.context}/res/rm/modules/create-site.js" group="template-common"/>
</@markup>

<@markup id="rm-shareConstants" target="shareConstants" action="after">
    <@inlineScript group="template-common">
        Alfresco.constants.USER_FULLNAME = "${(user.fullName!"")}";
    </@inlineScript>
</@markup>