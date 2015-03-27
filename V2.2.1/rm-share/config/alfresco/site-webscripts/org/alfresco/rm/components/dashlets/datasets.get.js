<import resource="classpath:/alfresco/site-webscripts/org/alfresco/rm/components/dashlets/rm-utils.js">

function main()
{
   var site = url.templateArgs.site;

   model.data = {};
   model.isAdmin = false;

   // Check if the user is an RM Admin
   var conn = remote.connect("alfresco");
   if (isAdmin(conn))
   {
      model.data = getDataSets(conn, site);
      model.isAdmin = true;
   }

   // Check if the site is an RM site
   model.isRmSite = isRmSite(conn, site);
}

main();
