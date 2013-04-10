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
         },
         RequestInfo:
         {
            edit: function(configDef, ruleConfig, configEl)
            {
               this._hideParameters(configDef.parameterDefinitions);
               configDef.parameterDefinitions.push({
                  type: "arca:requestInfo-dialog-button",
                  _buttonLabel: this.msg("button.requestInfo")
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
         },
         "arca:requestInfo-dialog-button":
         {
            manual: { edit: true },
            currentCtx: {},
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               this._createButton(containerEl, configDef, paramDef, ruleConfig, function RCAC_requestInfoFormButton_onClick(type, obj)
               {
                  this.renderers["arca:requestInfo-dialog-button"].currentCtx =
                  {
                     configDef: obj.configDef,
                     ruleConfig: obj.ruleConfig
                  };

                  // Intercept before dialog show and change the button type and the onClick functionality
                  // And also hide the div for showing the record information
                  var doBeforeDialogShow = function DLTB_requestInfo_doBeforeDialogShow(p_form, p_dialog)
                  {
                     var recordInfo_div = p_dialog.dialog.form.children[0].children[0],
                        assignees = Dom.get(p_dialog.id + "_assoc_rmwf_mixedAssignees"),
                        requestedInfo = Dom.get(p_dialog.id + "_prop_rmwf_requestedInformation");

                     recordInfo_div.style.display = "none";
                     assignees.value = this._getParameters(obj.configDef).assignees || "";
                     requestedInfo.value = this._getParameters(obj.configDef).requestedInfo || "";

                     // Change the button type and functionality
                     var okButton = p_dialog.widgets.okButton;
                     okButton._configs.type.value = "push";
                     okButton.on('click', function(type, args)
                     {
                        var ctx = this.renderers["arca:requestInfo-dialog-button"].currentCtx;
                        this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, "assignees", assignees.value);
                        this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, "requestedInfo", requestedInfo.value);
                        this._updateSubmitElements(ctx.configDef);

                        // Hide dialog
                        args.hide();
                     }, requestInfo, this);
                  };

                  var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&mode={mode}&submitType={submitType}&showCancelButton=true",
                  {
                     htmlid: this.id + "-startWorkflowForm-" + Alfresco.util.generateDomId(),
                     itemKind: "workflow",
                     itemId: "activiti$activitiRequestForInformation",
                     mode: "create",
                     submitType: "json",
                     showCaption: true,
                     formUI: true,
                     showCancelButton: true
                  });

                  // Using Forms Service, so always create new instance
                  var requestInfo = new Alfresco.module.SimpleDialog(this.id + "-request-info");

                  requestInfo.setOptions(
                  {
                     width: "auto",
                     templateUrl: templateUrl,
                     actionUrl: null,
                     destroyOnHide: true,
                     doBeforeDialogShow:
                     {
                        fn: doBeforeDialogShow,
                        scope: this
                     }
                  }).show();
               });
            }
         }
      }
   });
})();