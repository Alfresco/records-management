<import resource="/org/alfresco/components/people-finder/authority-query.get.js">

function shouldFilter(callResult)
{
   var result = false;

   if (new RegExp("ExtendedReaders").test(callResult.shortName) || new RegExp("ExtendedWriters").test(callResult.shortName))
   {
      result = true;
   }

   return result;
}

var getRmMappings = function()
{
   // Remove "GROUP_EVERYONE" from the mapping
   var mappings = getMappings();
   mappings.pop();

   var additionalMappings = [];
   if (args.showGroups != null && args.showGroups == "true")
   {
      additionalMappings.push(
      {
         type: MAPPING_TYPE.API,
         url: "/api/groups?shortNameFilter=" + encodeURIComponent(args.filter) + "&zone=" + encodeURIComponent("APP.DEFAULT"),
         rootObject: "data",
         fn: mapGroup
      });
   }

   return mappings.concat(additionalMappings);
};

function rm_main()
{
   var mappings = getRmMappings(),
      connector = remote.connect("alfresco"),
      authorities = [],
      mapping, result, data, i, ii, j, jj;

   for (i = 0, ii = mappings.length; i < ii; i++)
   {
      mapping = mappings[i];
      if (mapping.type == MAPPING_TYPE.API)
      {
         result = connector.get(mapping.url);
         if (result.status == 200)
         {
            data = eval('(' + result + ')');
            for (j = 0, jj = data[mapping.rootObject].length; j < jj; j++)
            {
               var callResult = mapping.fn.call(this, data[mapping.rootObject][j]),
                  isGroup = mapping.rootObject == "data";
               if (!isGroup || !shouldFilter(callResult))
               {
                  authorities.push(callResult);
               }
            }
         }
      }
      else if (mapping.type == MAPPING_TYPE.STATIC)
      {
         for (j = 0, jj = mapping.data.length; j < jj; j++)
         {
            authorities.push(mapping.fn.call(this, mapping.data[j]));
         }
      }
   }

   return authorities;
}

model.authorities = rm_main();