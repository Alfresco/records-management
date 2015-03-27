<import resource="classpath:alfresco/site-webscripts/org/alfresco/rm/components/console/rm-console.lib.js">

function main()
{
   var meta = [];
   
   var conn = remote.connect("alfresco");
   
   // retrieve user capabilities - can they access Audit?
   var capabilities = getCapabilities(conn);
   var hasAccess = hasCapabilityImpl("AuditAdmin", capabilities);
   if (hasAccess)
   {
      model.events = retrieveAuditEvents(conn);
      model.eventsStr = model.events.toSource();
      model.enabled = getAuditStatus(conn);
      model.capabilities = capabilities.toSource();
      
      var groups = [];
      var res = conn.get("/slingshot/rmsearchproperties");  // TODO we should be passing the file plan here
      if (res.status == 200)
      {
   	   groups = eval('(' + res + ')').data.groups;
      }
      model.groups = groups;
      
   }
   model.hasAccess = hasAccess;
}

function retrieveAuditEvents(conn)
{
   var res = conn.get("/api/rma/admin/listofvalues");
   if (res.status == 200)
   {
      return eval('(' + res + ')').data.auditEvents.items;
   }
   else
   {
      return [];
   }
}

function getAuditStatus(conn)
{
	var res = conn.get("/api/rma/admin/rmauditlog/status")
	if (res.status == 200)
    {
       return eval('(' + res + ')').data.enabled;
    }
    else
    {
      return true;
    }
}

main();
