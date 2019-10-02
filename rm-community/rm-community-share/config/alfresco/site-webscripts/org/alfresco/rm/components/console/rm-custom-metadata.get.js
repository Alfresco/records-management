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
 * RM Custom Metadata WebScript component
 */
function main()
{
   var constraints = [];

   var conn = remote.connect("alfresco");

   // retrieve the RM constraints - an array is returned
   var res = conn.get("/api/rma/admin/rmconstraints?withEmptyLists=false");
   if (res.status == 200)
   {
      constraints = eval('(' + res + ')').data;
   }
   model.constraints = constraints;


   // retrieve the customisable aspects and types
   var customisable = [];
   var res2 = conn.get("/api/rma/admin/customisable");
   if (res2.status == 200)
   {
	   customisable = eval('(' + res2 + ')').data;
   }
   model.customisable = customisable;

   // test user capabilities - can they access Custom Metadata?
   model.hasAccess = hasCapability(conn, "CreateModifyDestroyFileplanTypes");
}

main();
