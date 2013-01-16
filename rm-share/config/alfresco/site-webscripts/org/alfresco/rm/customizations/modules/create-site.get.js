<import resource="classpath:/alfresco/site-webscripts/org/alfresco/rm/components/dashlets/rm-utils.js">

// Check if the user is an RM Admin
if (isRmAdmin(remote.connect("alfresco")))
{
   model.sitePresets.push(
   {
      id: "rm-site-dashboard",
      name: msg.get("type.recordsManagementSite")
   });
}