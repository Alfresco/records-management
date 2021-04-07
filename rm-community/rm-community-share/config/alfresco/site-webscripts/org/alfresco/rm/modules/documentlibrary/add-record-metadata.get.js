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
