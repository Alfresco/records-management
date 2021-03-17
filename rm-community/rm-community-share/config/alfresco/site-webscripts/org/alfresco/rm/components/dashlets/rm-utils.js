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
function isAdmin(conn)
{
   var roles = conn.get("/api/rma/admin/rmroles?user=" + encodeURIComponent(user.id));
   var rolesData = eval('(' + roles + ')').data;

   if (isEmpty(rolesData))
   {
      // No roles found. Probably there is no file plan
      // Check if the user is "admin"
      return user.isAdmin;
   }

   for (var role in rolesData)
   {
      if (rolesData[role].name === "Administrator")
      {
         return true;
      }
   }
   return false;
}

function isEmpty(jsonObject)
{
   var key;
   for (key in jsonObject) {
      return false;
   }
   return true;
}

function getDataSets(conn, site)
{
   var data = {},
      json = conn.get("/api/rma/datasets?site=" + site);

   if (json.status == 200)
   {
      var obj = eval('(' + json + ')');
      if (obj)
      {
         data = obj.data;
      }
   }

   return data;
}

function isRmSite(conn, site)
{
   var isRmSite = false,
      json = conn.get("/api/sites/" + site);

   if (json.status == 200)
   {
      var obj = eval('(' + json + ')');
      if (obj)
      {
         isRmSite = obj.sitePreset == "rm-site-dashboard";
      }
   }

   return isRmSite;
}

function isRmUser(conn) {
   var roles = conn.get("/api/rma/admin/rmroles?user=" + encodeURIComponent(user.id));
   var rolesData = eval('(' + roles + ')').data;

   if (isEmpty(rolesData))
   {
      // No roles found or there is no file plan
      return false;
   }
   return true;
}
