<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/rules/rule-edit.get.js">

function rm_main()
{
   var result = [];
   if (model.scripts != null)
   {
      result = model.scripts;
   }
   if (model.constraints != null)
   {
      var scripts = model.constraints["rm-ac-scripts"];
      if (scripts != null)
      {
         result = scripts;
      }
   }
   return result;
}
model.scripts = rm_main();

model.widgets[0].name = "Alfresco.rm.RuleEdit";