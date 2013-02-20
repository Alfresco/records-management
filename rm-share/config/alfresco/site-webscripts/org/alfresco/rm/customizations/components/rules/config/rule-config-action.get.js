<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/rules/config/rule-config.lib.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/config.lib.js">

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

function main()
{
   model.ruleConfigDefinitions = jsonUtils.toJSONString(loadRmRuleConfigDefinitions());
}

main();