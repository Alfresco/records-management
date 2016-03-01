/*
 * #%L
 * This file is part of Alfresco.
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
<import resource="classpath:alfresco/site-webscripts/org/alfresco/rm/components/console/rm-console.lib.js">

function main()
{
   var conn = remote.connect("alfresco");

   // test user capabilities - can they access Email Mappings?
   var hasAccess = hasCapability(conn, "MapEmailMetadata");
   if (hasAccess)
   {
      var groups = [];
      var res = conn.get("/slingshot/rmsearchproperties");  // TODO we should be passing the file plan here
      if (res.status == 200)
      {
   	   groups = eval('(' + res + ')').data.groups;
      }
      model.groups = groups;

      var emailmapkeys = [];
      var result = conn.get("/api/rma/admin/emailmapkeys");
      if (result.status == 200)
      {
         emailmapkeys = eval('(' + result + ')').data.emailmapkeys;
      }
      model.emailmapkeys = emailmapkeys;
   }
   model.hasAccess = hasAccess;
}

main();