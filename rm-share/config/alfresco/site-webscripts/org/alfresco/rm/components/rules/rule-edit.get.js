<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/rules/rule-edit.get.js">

function rm_main()
{
	   var scripts;

	   if (model.constraints != null) {
	      scripts = model.constraints["rm-ac-scripts"];

		  if (scripts != null) {
	         return scripts;
		  }
	   }
	   return model.scripts;
}
model.scripts = rm_main();

model.widgets[0].name = "Alfresco.rm.RuleEdit";