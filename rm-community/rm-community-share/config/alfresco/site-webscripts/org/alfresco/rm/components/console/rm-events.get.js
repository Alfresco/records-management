<import resource="classpath:alfresco/site-webscripts/org/alfresco/rm/components/console/rm-console.lib.js">

/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2019 Alfresco Software Limited
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
 * Sort helper function for objects with labels
 *
 * @param obj1
 * @param obj2
 */
function sortByLabel(obj1, obj2)
{
   return (obj1.eventTypeDisplayLabel > obj2.eventTypeDisplayLabel) ? 1 : (obj1.eventTypeDisplayLabel < obj2.eventTypeDisplayLabel) ? -1 : 0;
}

/**
 * Main entry point for component webscript logic
 *
 * @method main
 */
function main()
{
   var conn = remote.connect("alfresco");

   // test user capabilities - can they access Events?
   model.hasAccess = hasCapability(conn, "CreateModifyDestroyEvents");

   // retrieve event types
   var repoResponse = conn.get("/api/rma/admin/rmeventtypes");
   if (repoResponse.status == 401)
   {
      status.setCode(repoResponse.status, "error.loggedOut");
      return;
   }
   else
   {
      var repoJSON = eval('(' + repoResponse + ')');

      // Check if we got a positive result
      if (repoJSON.data)
      {
         // Transform events from object to array and sort it
         var data = repoJSON.data;
         var eventTypes = [];
         if (data)
         {
            for (var key in data)
            {
               eventTypes.push(data[key]);
            }
            eventTypes.sort(sortByLabel);
         }

         // Set model values
         model.eventTypes = eventTypes;
      }
      else if (repoJSON.status.code)
      {
         status.setCode(repoJSON.status.code, repoJSON.message);
         return;
      }
   }
}

main();
