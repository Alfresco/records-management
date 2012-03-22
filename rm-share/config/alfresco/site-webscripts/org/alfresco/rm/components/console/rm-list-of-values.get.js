<import resource="classpath:alfresco/site-webscripts/org/alfresco/rm/components/console/rm-console.lib.js">

function main()
{
   var conn = remote.connect("alfresco");
   
   // test user capabilities - can they access LOV?
   model.hasAccess = hasCapability(conn, "CreateAndAssociateSelectionLists");
}

main();
