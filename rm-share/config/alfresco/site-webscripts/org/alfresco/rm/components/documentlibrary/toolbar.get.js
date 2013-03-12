const PREFERENCES_ROOT = "org.alfresco.share.documentList";

/**
 * Main entrypoint for component webscript logic
 *
 * @method main
 */
function main()
{
   var result, preferences = {};

   // Request the current user's preferences
   var result = remote.call("/api/people/" + encodeURIComponent(user.name) + "/preferences");
   if (result.status == 200 && result != "{}")
   {
      var prefs = eval('(' + result + ')');
      try
      {
         // Populate the preferences object literal for easy look-up later
         preferences = eval('(prefs.' + PREFERENCES_ROOT + ')');
         if (typeof preferences != "object")
         {
            preferences = {};
         }
      }
      catch (e)
      {
      }
   }

   model.preferences = preferences;

   // Actions
   var myConfig = new XML(config.script),
      xmlActions = myConfig.actions,
      actionSet = [];

   for each (xmlAction in xmlActions.action)
   {
      actionSet.push(
      {
         id: xmlAction.@id.toString(),
         permission: xmlAction.@permission.toString(),
         label: xmlAction.@label.toString(),
         type: xmlAction.@type.toString(),
         asset: xmlAction.@asset.toString(),
         href: xmlAction.@href.toString(),
         hasAspect: xmlAction.@hasAspect.toString(),
         notAspect: xmlAction.@notAspect.toString(),
         hasProperty: xmlAction.@hasProperty.toString(),
         notProperty: xmlAction.@notProperty.toString()
      });
   }

   model.actionSet = actionSet;
}

main();
