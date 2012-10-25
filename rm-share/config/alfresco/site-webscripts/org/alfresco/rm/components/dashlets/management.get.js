<import resource="classpath:/alfresco/site-webscripts/org/alfresco/rm/components/dashlets/rm-utils.js">

function main()
{
   // Check if the user is an RM Admin
   var conn = remote.connect("alfresco");
   if (isRmAdmin(conn))
   {
      model.isRmAdmin = true;

      // Check for RMA site existence
      var res = conn.get("/api/sites/rm");
      if (res.status == 404)
      {
         // site does not exist yet
         model.foundsite = false;
      }
      else if (res.status == 200)
      {
         // site already exists
         model.foundsite = true;
      }
   }
   else
   {
      model.isRmAdmin = false;
   }
}

main();
