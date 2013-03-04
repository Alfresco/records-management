<import resource="classpath:/alfresco/site-webscripts/org/alfresco/rm/components/dashlets/rm-utils.js">

function main()
{
   var site = url.templateArgs.site;

   model.data = {};
   model.isRmAdmin = false;

   // Check if the user is an RM Admin
   var conn = remote.connect("alfresco");
   if (isRmAdmin(conn))
   {
      model.data = getDataSets(conn, site);
      model.isRmAdmin = true;
   }

   // Check if the site is an RM site
   model.isRmSite = isRmSite(conn, site);
}

main();
