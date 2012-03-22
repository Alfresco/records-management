/**
 * Sort helper function for objects with ids
 *
 * @param obj1
 * @param obj2
 */
function sortById(obj1, obj2)
{
   return (obj1.id > obj2.id) ? 1 : (obj1.id < obj2.id) ? -1 : 0;
}

/**
 * Main entrypoint for component webscript logic
 *
 * @method main
 */
function main()
{
   // Load rm specific record types
   var result = remote.call("/api/rma/recordmetadataaspects");
   if (result.status == 200)
   {
      var rmAspects = eval('(' + result + ')').data.recordMetaDataAspects;
      result = remote.call("/slingshot/node/" + args.nodeRef.replace("://", "/"));
      var nodeAspects = eval('(' + result + ')').aspects;

      var recordTypes = [];
      for (var i = 0; i < rmAspects.length; i++)
      {
         for (var j = 0; j < nodeAspects.length; j++)
         {
            if (rmAspects[i].id == nodeAspects[j].prefixedName)
            {
               break;
            }
         }
         if (j == nodeAspects.length)
         {
            recordTypes.push(rmAspects[i]);
         }
      }

      // Add and set in model
      recordTypes.sort(sortById);
      model.recordTypes = recordTypes;
   }
   else if (repoJSON.status.code)
   {
      status.setCode(repoJSON.status.code, repoJSON.message);
      return;
   }
}

main();
