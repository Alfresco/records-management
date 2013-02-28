/**
 * RuleConfigActionCustom.
 *
 * @namespace Alfresco
 * @class Alfresco.RuleConfigActionCustom
 */
(function()
{
   Alfresco.RuleConfigActionCustom = function(htmlId)
   {
      Alfresco.RuleConfigActionCustom.superclass.constructor.call(this, htmlId);

      // Re-register with our own name
      this.name = "Alfresco.RuleConfigActionCustom";
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
         Reject:
         {
            edit: function(configDef, ruleConfig, configEl)
            {
               this._hideParameters(configDef.parameterDefinitions);
               configDef.parameterDefinitions.push({
                  type: "arca:reject-dialog-button",
                  _buttonLabel: this.msg("button.reject")
               });
               return configDef;
            }
         }
      },
      renderers:
      {
         "arca:reject-dialog-button":
         {
            manual: { edit: true },
            currentCtx: {},
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               this._createButton(containerEl, configDef, paramDef, ruleConfig, function RCAC_rejectFormButton_onClick(type, obj)
               {
                  this.renderers["arca:reject-dialog-button"].currentCtx =
                  {
                     configDef: obj.configDef,
                     ruleConfig: obj.ruleConfig
                  };
                  Alfresco.util.PopupManager.getUserInput(
                  {
                     title: this.msg("message.reject.title"),
                     text: this.msg("message.reject.reason"),
                     okButtonText: this.msg("button.ok"),
                     value: this._getParameters(obj.configDef).reason || "",
                     callback:
                     {
                        fn: function RCAC_rejectOKButton_callback(value)
                        {
                           var ctx = this.renderers["arca:reject-dialog-button"].currentCtx;
                           this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, "reason", value);
                           this._updateSubmitElements(ctx.configDef);
                        },
                        scope: this
                     }
                  });
               });
            }
         }
      }
   });
 })();