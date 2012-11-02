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