/**
 * RuleConfigActionCustom.
 *
 * @namespace Alfresco
 * @class Alfresco.RuleConfigActionCustom
 */
(function()
{
  /**
   * Alfresco Slingshot aliases
   */
   var $hasEventInterest = Alfresco.util.hasEventInterest;

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
         Freeze:
         {
            edit: function(configDef, ruleConfig, configEl)
            {
               this._hideParameters(configDef.parameterDefinitions);
               configDef.parameterDefinitions.push({
                  type: "arca:freeze-dialog-button",
                  _buttonLabel: this.msg("button.freeze")
               });
               return configDef;
            }
         },
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
         },
         FileTo:
         {
            edit: function(configDef, ruleConfig, configEl)
            {
               this._hideParameters(configDef.parameterDefinitions);

               configDef.parameterDefinitions.splice(0, 0,
               {
                  type: "arca:rm-destination-dialog-button",
                  _buttonLabel: this.msg("button.select-folder"),
                  _destinationParam: "destination-folder"
               });

               configDef.parameterDefinitions.splice(1, 0,
               {
                  type: "arca:record-path-help-icon"
               });

               var path = this._getParamDef(configDef, "path");
               path._type = "hidden";
               path._displayLabelToRight = false;
               path._hideColon = true;

               var createRecordFolder = this._getParamDef(configDef, "createRecordFolder");
               createRecordFolder._type = null;
               createRecordFolder._displayLabelToRight = false;
               createRecordFolder._hideColon = true;

               return configDef;
            }
         },
         AddRecordTypes:
         {
            edit: function(configDef, ruleConfig, configEl)
            {
               this._hideParameters(configDef.parameterDefinitions);
               configDef.parameterDefinitions.push({
                  type: "arca:rm-add-record-types-dialog-button",
                  _buttonLabel: this.msg("button.addRecordTypes")
               });
               return configDef;
            }
         }
      },
      renderers:
      {
         "arca:freeze-dialog-button":
         {
            manual: { edit: true },
            currentCtx: {},
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               this._createButton(containerEl, configDef, paramDef, ruleConfig, function RCAC_freezeFormButton_onClick(type, obj)
               {
                  this.renderers["arca:freeze-dialog-button"].currentCtx =
                  {
                     configDef: obj.configDef,
                     ruleConfig: obj.ruleConfig
                  };
                  Alfresco.util.PopupManager.getUserInput(
                  {
                     title: this.msg("message.freeze.title"),
                     text: this.msg("message.freeze.reason"),
                     okButtonText: this.msg("button.ok"),
                     value: this._getParameters(obj.configDef).reason || "",
                     callback:
                     {
                        fn: function RCAC_freezeOKButton_callback(value)
                        {
                           var ctx = this.renderers["arca:freeze-dialog-button"].currentCtx;
                           this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, "reason", value);
                           this._updateSubmitElements(ctx.configDef);
                        },
                        scope: this
                     }
                  });
               });
            }
         },
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
                        this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, "ruleCreator", Alfresco.constants.USERNAME);
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
         },
         "arca:rm-destination-dialog-button":
         {
            manual: { edit: true },
            currentCtx: {},
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               this._createButton(containerEl, configDef, paramDef, ruleConfig, function RCA_destinationDialogButton_onClick(type, obj)
               {
                  this.renderers["arca:rm-destination-dialog-button"].currentCtx =
                  {
                     configDef: obj.configDef,
                     ruleConfig: obj.ruleConfig,
                     paramDef: obj.paramDef
                  };
                  if (!this.widgets.destinationDialog)
                  {
                     this.widgets.destinationDialog = new Alfresco.module.DoclibGlobalFolder(this.id + "-destinationDialog");
                     this.widgets.destinationDialog.setOptions(
                     {
                        title: this.msg("dialog.destination.title")
                     });

                     YAHOO.Bubbling.on("folderSelected", function (layer, args)
                     {
                        if ($hasEventInterest(this.widgets.destinationDialog, args))
                        {
                           var selectedFolder = args[1].selectedFolder;
                           if (selectedFolder !== null)
                           {
                              var ctx = this.renderers["arca:destination-dialog-button"].currentCtx;
                              this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, "path", selectedFolder.path);
                              Dom.get(this.id + "-recordFolderPath").value = selectedFolder.path;
                              this._updateSubmitElements(ctx.configDef);
                           }
                        }
                     }, this);
                  }
                  var pathNodeRef = this._getParameters(obj.configDef)["destination-folder"],
                     allowedViewModes =
                     [
                        Alfresco.module.DoclibGlobalFolder.VIEW_MODE_SITE
                     ];

                  if (this.options.repositoryBrowsing === true)
                  {
                     allowedViewModes.push(Alfresco.module.DoclibGlobalFolder.VIEW_MODE_REPOSITORY, Alfresco.module.DoclibGlobalFolder.VIEW_MODE_USERHOME);
                  }
                  this.widgets.destinationDialog.setOptions(
                  {
                     allowedViewModes: allowedViewModes,
                     nodeRef: this.options.rootNode,
                     pathNodeRef: pathNodeRef ? new Alfresco.util.NodeRef(pathNodeRef) : null
                  });
                  this.widgets.destinationDialog.showDialog();
               });

               this._createLabel(this._getParamDef(configDef, "path").displayLabel, containerEl);
               var value = ruleConfig.parameterValues && ruleConfig.parameterValues.path;
               var el = document.createElement("input");
               el.setAttribute("type", "text");
               el.setAttribute("name", "-");
               el.setAttribute("title", paramDef.displayLabel ? paramDef.displayLabel : paramDef.name);
               el.setAttribute("param", paramDef.name);
               el.setAttribute("value", (value != undefined && value != null) ? value : "");
               el.setAttribute("id", this.id + "-recordFolderPath");
               el.addEventListener("blur", function()
               {
                  Selector.query("[param=" + "path" + "]")[0].value = this.value;
               }, false);
               containerEl.appendChild(el);
            }
         },
         "arca:rm-add-record-types-dialog-button":
         {
            manual: { edit: true },
            currentCtx: {},
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               this._createButton(containerEl, configDef, paramDef, ruleConfig, function RCAC_addRecordTypesFormButton_onClick(type, obj)
               {
                  this.renderers["arca:rm-add-record-types-dialog-button"].currentCtx =
                  {
                     configDef: obj.configDef,
                     ruleConfig: obj.ruleConfig
                  };

                  var me = this;

                  // Intercept before dialog show and change the button type and the onClick functionality
                  var doBeforeDialogShow = function DLTB_requestInfo_doBeforeDialogShow(p_form, p_dialog)
                  {
                     var selectedTypes = me._getParameters(obj.configDef).recordTypes;
                     if (selectedTypes)
                     {
                        selectedTypes = selectedTypes.split(',');
                        var types = Dom.get(this.id + "-addRecordMetadataDialog-recordType");

                        for (var i = 0; i < types.length; i++)
                        {
                           if (Alfresco.util.arrayContains(selectedTypes, types[i].value))
                           {
                              types[i].selected = true;
                           }
                        }
                     }

                     // Change the button type and functionality
                     var okButton = p_dialog.widgets.okButton;
                     okButton._configs.type.value = "push";
                     okButton.on('click', function(type, args)
                     {
                        var ctx = this.renderers["arca:rm-add-record-types-dialog-button"].currentCtx,
                           selectedRecordTypes = [],
                           recordTypes = Dom.get(this.id + "-addRecordMetadataDialog-recordType");

                        for (var i = 0; i < recordTypes.length; i++)
                        {
                           if (recordTypes[i].selected)
                           {
                              selectedRecordTypes.push(recordTypes[i].value);
                           }
                        }

                        this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, "recordTypes", selectedRecordTypes.join(','));
                        this._updateSubmitElements(ctx.configDef);

                        // Hide dialog
                        args.hide();
                     }, addRecordMetadataDialog, this);
                  };

                  // Open the set record type dialog
                  var addRecordMetadataDialog = new Alfresco.module.SimpleDialog(this.id + "-addRecordMetadataDialog").setOptions(
                  {
                     width: "30em",
                     templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "rm/modules/documentlibrary/add-record-metadata",
                     actionUrl: null,
                     destroyOnHide: true,
                     doBeforeDialogShow:
                     {
                        fn: doBeforeDialogShow,
                        scope: this
                     }
                  });
                  addRecordMetadataDialog.show();
               });
            }
         },
         "arca:record-path-help-icon":
         {
            manual: { edit: true },
            currentCtx: {},
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               var me = this,
                  image = document.createElement("img");
               image.src = Alfresco.constants.URL_CONTEXT + "res/components/form/images/help.png";
               image.title = this.msg("record.folder.path.title");
               image.onclick = function()
               {
                  var balloon = Alfresco.util.createInfoBalloon(this, {
                     html: me.msg("record.folder.path.help"),
                     width: "25em"
                  });
                  balloon.show();
               };

               var helpIcon = document.createElement("span");
               helpIcon.setAttribute('class', 'help-icon');
               helpIcon.appendChild(image);
               containerEl.appendChild(helpIcon);
            }
         }
      }
   });
})();