<import resource="classpath:/alfresco/site-webscripts/org/alfresco/rm/components/dashlets/rm-utils.js">

function main()
{
   var data = {};

   // Check if the user is an RM Admin
   var conn = remote.connect("alfresco");
   if (isRmAdmin(conn))
   {
      model.isRmAdmin = true;

      // Call the repo for the data sets
      var res = conn.get("/api/rma/datasets?site=" + url.templateArgs.site);

      // Check the response
      if (res.status == 200)
      {
         // Create javascript objects from the server response
         var obj = eval('(' + res + ')');
         if (obj)
         {
            data = obj.data;
         }
      }
   }
   else
   {
      model.isRmAdmin = false;
   }

   model.data = data;
}

main();
