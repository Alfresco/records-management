function loadRmRuleConfigDefinitions()
{
   var connector = remote.connect("alfresco"),
      ruleConfigDefinitions = [],
      callResult = connector.get("/api/rm/rm-actiondefinitions");

   if (callResult.status == 200)
   {
      ruleConfigDefinitions = eval('(' + callResult + ')').data;
   }

   return ruleConfigDefinitions;
}

/*
function loadRmRuleConstraints()
{
   return {};
}

function loadRmRuleConstraintsFilter()
{
   return {};
}
*/

function main()
{
   // Load rule config definitions, or in this case "ActionDefinition:s"
   model.ruleConfigDefinitions = jsonUtils.toJSONString(loadRmRuleConfigDefinitions());

   /*
   // Load constraints for rule types
   model.constraints = jsonUtils.toJSONString(loadRmRuleConstraints());

   // Load aspects and types that shall be visible
   model.constraintsFilter = jsonUtils.toJSONString(loadRmRuleConstraintsFilter());
   */
}

main();