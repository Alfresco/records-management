function main()
{
   // Call the repo for the data sets
   var conn = remote.connect("alfresco");
   var res = conn.get("/api/rma/datasets?site=" + url.templateArgs.site);

   var data = {};

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

   model.data = data;
}

main();
