<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/include/toolbar.lib.js">

function getRMActionSet(myConfig)
{
   // Actions
   var multiSelectConfig = config.scoped["DocumentLibrary"]["multi-select"],
      multiSelectActions = multiSelectConfig.getChildren("action"),
      actionSet = [];

   var multiSelectAction;
   for (var i = 0; i < multiSelectActions.size(); i++)
   {
      multiSelectAction = multiSelectActions.get(i);
      attr = multiSelectAction.attributes;

      if(attr["rmaction"] == "true" && (!attr["syncMode"] || attr["syncMode"].toString() == syncMode.value))
      {
         // Multi-Select Actions
         action = {
            id: attr["id"] ? attr["id"].toString() : "",
            type: attr["type"] ? attr["type"].toString() : "",
            permission: attr["permission"] ? attr["permission"].toString() : "",
            asset: attr["asset"] ? attr["asset"].toString() : "",
            href: attr["href"] ? attr["href"].toString() : "",
            label: attr["label"] ? attr["label"].toString() : "",
            hasAspect: attr["hasAspect"] ? attr["hasAspect"].toString() : "",
            notAspect: attr["notAspect"] ? attr["notAspect"].toString() : "",
            hasProperty: attr["hasProperty"] ? attr["hasProperty"].toString() : "",
            notProperty: attr["notProperty"] ? attr["notProperty"].toString() : ""
         };

         actionSet.push(action)
      }
   }

   model.actionSet = actionSet;
}

function rm_main()
{
   var myConfig = new XML(config.script);

   getPreferences();
   getRMActionSet(myConfig);
   getCreateContent(myConfig);
   getRepositoryBrowserRoot();

   toolbar.syncMode = syncMode.value;
}

rm_main();