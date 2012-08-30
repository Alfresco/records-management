<import resource="classpath:alfresco/site-webscripts/org/alfresco/rm/components/console/rm-console.lib.js">

function main()
{
   var conn = remote.connect("alfresco");
   
   // test user capabilities - can they access Email Mappings?
   var hasAccess = hasCapability(conn, "MapEmailMetadata");
   if (hasAccess)
   {
      var groups = [];
      var res = conn.get("/slingshot/rmsearchproperties");
      if (res.status == 200)
      {
   	   groups = eval('(' + res + ')').data.groups;
      }
      model.groups = groups;
   }
   model.hasAccess = hasAccess;
}

main();
