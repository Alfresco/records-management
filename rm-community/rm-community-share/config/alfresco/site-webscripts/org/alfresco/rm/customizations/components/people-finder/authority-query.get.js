<import resource="/org/alfresco/components/people-finder/authority-query.get.js">

/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2021 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software.
 * -
 * If the software was purchased under a paid Alfresco license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 * -
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * -
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * -
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

function shouldFilter(callResult)
{
   var result = false;

   if (new RegExp("ExtendedReaders").test(callResult.shortName) ||
         new RegExp("ExtendedWriters").test(callResult.shortName) ||
         new RegExp("GROUP_Administrator").test(callResult.fullName))
   {
      result = true;
   }

   return result;
}

var getRmMappings = function()
{
   // Remove "GROUP_EVERYONE" from the mapping
   var mappings = getMappings();
   if (args.defGroupsFor === 'rm')
   {
      mappings.pop();
   }

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
