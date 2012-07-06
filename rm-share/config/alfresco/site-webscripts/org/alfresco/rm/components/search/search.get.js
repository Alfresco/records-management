/**
 * RM Search WebScript component
 */
function main()
{
   var conn = remote.connect("alfresco");
   var groups = [];
   var res = conn.get("/slingshot/rmsearchproperties");
   if (res.status == 200)
   {
	   groups = eval('(' + res + ')').data.groups;
   }
   model.groups = groups;
   
}

main();
