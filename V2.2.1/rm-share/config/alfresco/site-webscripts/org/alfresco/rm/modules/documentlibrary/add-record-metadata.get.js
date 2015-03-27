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
   var callUri = "/api/rma/recordmetadataaspects";
   if(args.nodeRef != null)
   {
      callUri += "?noderef=" + args.nodeRef;
   }
   var result = remote.call(callUri);
   if (result.status == 200)
   {
      var rmAspects = eval('(' + result + ')').data.recordMetaDataAspects;
      var recordTypes = [];

      if (args.nodeRef != null)
      {
         result = remote.call("/api/rmmetadata?extended=true&noderef=" + args.nodeRef);
         var nodeAspects = eval('(' + result + ')').aspects;

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
      }
      else
      {
         for (var i = 0; i < rmAspects.length; i++)
         {
            recordTypes.push(rmAspects[i]);
         }
      }

      // Add and set in model
      recordTypes.sort(sortById);
      model.recordTypes = recordTypes;
   }
}

main();
