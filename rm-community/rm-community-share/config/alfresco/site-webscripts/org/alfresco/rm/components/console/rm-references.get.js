<import resource="classpath:alfresco/site-webscripts/org/alfresco/rm/components/console/rm-console.lib.js">

function main()
{
   var conn = remote.connect("alfresco");
   
   // test user capabilities - can they access References?
   model.hasAccess = hasCapability(conn, "CreateModifyDestroyReferenceTypes");
}

main();
