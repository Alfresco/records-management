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
