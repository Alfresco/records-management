/**
 * RuleConfigActionCustom.
 *
 * @namespace YourCompany
 * @class YourCompany.RuleConfigActionCustom
 */
(function()
{
   Alfresco.RuleConfigActionCustom = function(htmlId)
   {
      Alfresco.RuleConfigActionCustom.superclass.constructor.call(this, htmlId);

      // Re-register with our own name
      this.name = "AlfrescoRM.RuleConfigActionCustom";
      Alfresco.util.ComponentManager.reregister(this);

      // Instance variables
      this.customisations = YAHOO.lang.merge(this.customisations, Alfresco.RuleConfigActionCustom.superclass.customisations);
      this.renderers = YAHOO.lang.merge(this.renderers, Alfresco.RuleConfigActionCustom.superclass.renderers);

      return this;
   };

   YAHOO.extend(Alfresco.RuleConfigActionCustom, Alfresco.RuleConfigAction,
   {    
      customisations:
      {
         FileRecord:
         {
            text: function(configDef, ruleConfig, configEl)
            {
               // Display as path
               this._getParamDef(configDef, "destination-record-folder")._type = "path";

               return configDef;
            },
            edit: function(configDef, ruleConfig, configEl)
            {
               // Hide parameters since we are using a custom ui
               this._hideParameters(configDef.parameterDefinitions);

               // Make parameter renderer create a "Destination" button that displays an destination folder browser
               configDef.parameterDefinitions.splice(0,0,
               {
                  type: "arca:destination-dialog-button",
                  displayLabel: this.msg("label.to"),
                  _buttonLabel: this.msg("button.select-folder"),
                  _destinationParam: "destination-record-folder"
               });

               return configDef;
            }
         }
      }
   });
 })();