<import resource="classpath:alfresco/site-webscripts/org/alfresco/rm/components/console/rm-console.lib.js">

function main()
{
   model.action = page.url.args.action || 'view';
   model.roleId = page.url.args.roleId || null;

   var conn = remote.connect("alfresco");

   // test user capabilities - can they access Define Roles?
   model.hasAccess = hasCapability(conn, "CreateModifyDestroyRoles");

   if (model.hasAccess)
   {
      model.groupedCapabilities = getGroupedCapabilities(conn);
   }
}

main();