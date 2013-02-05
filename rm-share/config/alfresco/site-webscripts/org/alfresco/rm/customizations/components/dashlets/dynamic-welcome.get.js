function main()
{
   var userIsSiteManager = false,
      userIsMember = false,
      userIsSiteConsumer = true,
      json = remote.call("/api/sites/" + page.url.templateArgs.site + "/memberships/" + encodeURIComponent(user.name)),
      obj = null;

   if (json.status == 200)
   {
      obj = eval('(' + json + ')');

      if (obj)
      {
         userIsMember = true;
         userIsSiteManager = obj.role == "SiteManager";
         userIsSiteConsumer = obj.role == "SiteConsumer";

         if (userIsSiteManager || (userIsMember && !userIsSiteConsumer))
         {
            model.columns[2].actionHref = "documentlibrary";
         }
      }
   }
}
main();