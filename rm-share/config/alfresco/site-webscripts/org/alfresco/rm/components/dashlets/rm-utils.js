function isRmAdmin(conn)
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
         isRmSite = (obj.sitePreset == "rm-site-dashboard" || obj.sitePreset == "rm-site-dod5015-dashboard");
      }
   }

   return isRmSite;
}