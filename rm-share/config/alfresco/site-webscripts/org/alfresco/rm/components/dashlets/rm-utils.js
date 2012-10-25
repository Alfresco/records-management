function isRmAdmin(conn)
{
   var roles = conn.get("/api/rma/admin/rmroles?user=" + encodeURIComponent(user.id));
   var rolesData = eval('(' + roles + ')').data;

   for (var role in rolesData)
   {
      if (rolesData[role].name === "Administrator")
      {
         return true;
      }
   }
   return false;
}