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
	return hasCapability(cap, capabilities);
}

function hasCapability(cap, capabilities)
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