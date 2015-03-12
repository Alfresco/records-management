<import resource="classpath:alfresco/site-webscripts/org/alfresco/rm/components/console/rm-console.lib.js">

function main()
{
   // test user capabilities - can they access "Users and Groups"?
   model.hasAccess = hasCapability(remote.connect("alfresco"), "ManageAccessControls");
}

main();