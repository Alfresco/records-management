<@markup id="rm-js" target="js" action="after" scope="global">
    <@inlineScript group="console">
    YAHOO.util.Event.onContentReady("${args.htmlid?js_string}", function() {
        // override the Console Users behaviour, as we've now deeplinked to this page from elsewhere in the app (e.g. security clearance)
        Alfresco.util.ComponentManager.findFirst("Alfresco.ConsoleUsers").onGoBackClick = function(){history.go(-1)}
    });
    </@inlineScript>
</@>