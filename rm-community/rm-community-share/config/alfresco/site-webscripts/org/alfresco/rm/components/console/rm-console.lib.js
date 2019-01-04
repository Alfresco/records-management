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
/**
 * Helper to determine if the current user has the given Capability in any assigned Role
 * 
 * @method hasCapability
 * @param conn Connector to use
 * @param cap Capability ID to test e.g. "AccessAudit"
 * @return true if the capability is present for this user, false otherwise
 */
function hasCapability(conn, cap)
{
   var capabilities = getCapabilities(conn);
   return hasCapabilityImpl(cap, capabilities);
}

function hasCapabilityImpl(cap, capabilities)
{
   var result = false;
   if (capabilities !== null)
   {
      for each (var c in capabilities)
      {
         if (c == cap)
         {
            result = true;
            break;
         }
      }
   }
   return result;	
}

function getCapabilities(conn)
{
   var result = null;
   var res = conn.get("/api/capabilities?includeAll=true");
   if (res.status == 200)
   {
      var data = eval('(' + res + ')').data;
      result = data.capabilities;
   }
   return result;
}

function getGroupedCapabilities(conn)
{
   var result = null;
   var res = conn.get("/api/capabilities?grouped=true");
   if (res.status == 200)
   {
      var data = eval('(' + res + ')').data;
      result = data.groupedCapabilities;
   }
   return result;
}
