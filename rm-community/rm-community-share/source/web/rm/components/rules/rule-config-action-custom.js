/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2021 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software.
 * -
 * If the software was purchased under a paid Alfresco license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 * -
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * -
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * -
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
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
            text: function(configDef, ruleConfig, configEl)
            {
               this._getParamDef(configDef, "createRecordPath").displayLabel = this.msg("fileTo.createRecordPath.label");
               this._getParamDef(configDef, "path").displayLabel = this.msg("fileTo.path.label");
               return configDef;
            },
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
               path.displayLabel = this.msg("fileTo.path.label"),
               path._displayLabelToRight = false;
               path._hideColon = true;

               var createRecordPath = this._getParamDef(configDef, "createRecordPath");
               createRecordPath._type = null;
               createRecordPath.displayLabel = this.msg("fileTo.createRecordPath.label"),
               createRecordPath._displayLabelToRight = false;
               createRecordPath._hideColon = true;

               return configDef;
            }
         },
         CopyTo:
         {
            text: function(configDef, ruleConfig, configEl)
            {
               this._getParamDef(configDef, "createRecordPath").displayLabel = this.msg("fileTo.createRecordPath.label");
               this._getParamDef(configDef, "path").displayLabel = this.msg("fileTo.path.label");
               return configDef;
            },
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
               path.displayLabel = this.msg("fileTo.path.label"),
               path._displayLabelToRight = false;
               path._hideColon = true;

               var createRecordPath = this._getParamDef(configDef, "createRecordPath");
               createRecordPath._type = null;
               createRecordPath.displayLabel = this.msg("fileTo.createRecordPath.label"),
               createRecordPath._displayLabelToRight = false;
               createRecordPath._hideColon = true;

               return configDef;
            }
         },
         MoveTo:
         {
            text: function(configDef, ruleConfig, configEl)
            {
               this._getParamDef(configDef, "createRecordPath").displayLabel = this.msg("fileTo.createRecordPath.label");
               this._getParamDef(configDef, "path").displayLabel = this.msg("fileTo.path.label");
               return configDef;
            },
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
               path.displayLabel = this.msg("fileTo.path.label"),
               path._displayLabelToRight = false;
               path._hideColon = true;

               var createRecordPath = this._getParamDef(configDef, "createRecordPath");
               createRecordPath._type = null;
               createRecordPath.displayLabel = this.msg("fileTo.createRecordPath.label"),
               createRecordPath._displayLabelToRight = false;
               createRecordPath._hideColon = true;

               return configDef;
            }
         },
         LinkTo:
         {
            text: function(configDef, ruleConfig, configEl)
            {
               this._getParamDef(configDef, "createRecordPath").displayLabel = this.msg("fileTo.createRecordPath.label");
               this._getParamDef(configDef, "path").displayLabel = this.msg("fileTo.path.label");
               return configDef;
            },
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
               path.displayLabel = this.msg("fileTo.path.label"),
               path._displayLabelToRight = false;
               path._hideColon = true;

               var createRecordPath = this._getParamDef(configDef, "createRecordPath");
               createRecordPath._type = null;
               createRecordPath.displayLabel = this.msg("fileTo.createRecordPath.label"),
               createRecordPath._displayLabelToRight = false;
               createRecordPath._hideColon = true;

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
               var unfiledParameter = Alfresco.util.getQueryStringParameter("unfiled");
               var unfiled = (configDef.name != "fileTo") && (unfiledParameter == "true");
               var mode;
               switch(configDef.name)
               {
                  case "moveTo": mode = "move"; break;
                  case "copyTo": mode = "copy"; break;
                  case "linkTo": mode = "link"; break;
                  default: mode = "file"; break;
               }
               if (this.widgets.destinationDialog)
               {
                  this.widgets.destinationDialog.setOptions(
                  {
                     unfiled: unfiled
                  });
               }
               var selectedPath = ruleConfig.parameterValues && ruleConfig.parameterValues.path;
               this._createButton(containerEl, configDef, paramDef, ruleConfig, function RCA_destinationDialogButton_onClick(type, obj)
               {
                  this.renderers["arca:rm-destination-dialog-button"].currentCtx =
                  {
                     configDef: obj.configDef,
                     ruleConfig: obj.ruleConfig,
                     paramDef: obj.paramDef
                  };
                  this.widgets.destinationDialog = new Alfresco.rm.module.CopyMoveLinkFileTo(this.id + "-destinationDialog");
                  this.widgets.destinationDialog.setOptions(
                  {
                     title: this.msg("dialog.destination.title"),
                     mode: mode,
                     files: "",
                     siteId: this.options.siteId,
                     path: selectedPath,
                     unfiled: unfiled
                  });

                  YAHOO.Bubbling.on("folderSelected", function (layer, args)
                  {
                     if ($hasEventInterest(this.widgets.destinationDialog, args))
                     {
                        var selectedFolder = args[1].selectedFolder;
                        if (selectedFolder !== null)
                        {
                           var ctx = this.renderers["arca:rm-destination-dialog-button"].currentCtx;
                           var path = selectedFolder.path;
                           if(unfiled)
                           {
                              if((path.match(/\//g)||[]).length < 2)
                              {
                                  path = '/';
                        	  }
                              else
                              {
                                  path = path.replace(/^\/.*?\//, '/');
                              }
                           }
                           this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, "path", path);
                           Dom.get(this.id + "-recordFolderPath").value = path;
                           this._updateSubmitElements(ctx.configDef);
                           this.widgets.destinationDialog.setOptions({
                              path: selectedFolder.path
                           });
                        }
                     }
                  }, this);
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

                  this.widgets.destinationDialog.onOK = function()
                  {
                     YAHOO.Bubbling.fire("folderSelected",
                     {
                        selectedFolder: this.selectedNode ? this.selectedNode.data : null,
                        eventGroup: this
                     });
                     this.widgets.dialog.hide();
                  }

                  var me = this;
                  this.widgets.destinationDialog._showDialog = function()
                  {
                     this.widgets.okButton.set("label", me.msg("button.ok"));
                     return Alfresco.rm.module.CopyMoveLinkFileTo.superclass._showDialog.apply(this, arguments);
                  }

                  this.widgets.destinationDialog.showDialog();
               });
               this._createLabel(this._getParamDef(configDef, "path").displayLabel, containerEl);
               var el = document.createElement("input");
               el.setAttribute("type", "text");
               el.setAttribute("name", "-");
               el.setAttribute("title", paramDef.displayLabel ? paramDef.displayLabel : paramDef.name);
               el.setAttribute("param", paramDef.name);
               el.setAttribute("value", (selectedPath != undefined && selectedPath != null) ? selectedPath : "");
               el.setAttribute("id", this.id + "-recordFolderPath");
               el.addEventListener("blur", function()
               {
                  Selector.query("[param=" + "path" + "]")[0].value = this.value;
               }, false);

               containerEl.appendChild(el);

               // create an autocomplete div which will get populated with the drop down containing
               // the autocomplete suggestions
               var autoCompleteDiv = document.createElement("div");
               containerEl.appendChild(autoCompleteDiv);
               var dataSource = new YAHOO.util.XHRDataSource(Alfresco.constants.PROXY_URI + "api/rm/rm-substitutionsuggestions");
               dataSource.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
               dataSource.responseSchema =
               {
                  resultsList : "substitutions"
               };

               // create the auto complete widget.
               var autoComp = new YAHOO.widget.AutoComplete(el, autoCompleteDiv, dataSource);
               // sanity check as server should limit number of results based on configuration
               autoComp.maxResultsDisplayed = 30;

               // fix any css applied by creating the autocomplete widget and tweak a couple of other styles
               YUIDom.setStyle(autoCompleteDiv, "width", "500px");
               YUIDom.setStyle(el, "position", "relative");
               YUIDom.setStyle(el, "width", "500px");

               // format the items in the autosuggest list
               var me = this;
               autoComp.formatResult = function(oResultData, sQuery, sResultMatch) {
                  var prefix = "";
                  var postfix = "";
                  var message = "";
                  if(sResultMatch.indexOf("/") == 0)
                  {
                     message =
                        "<strong><code><span class='substitutionSuggestion'>"
                        + sResultMatch
                        + "</span></code></strong>";
                  }
                  else if(sResultMatch.indexOf("node.") == 0)
                  {
                     message = me.msg("file-to.substitution." + sResultMatch.replace(/:/g,'.') + ".label");
                     message =
                        "<span class='substitutionSuggestion'>"
                        + (message.indexOf("file-to.substitution.") != -1 ? " <em>{" + sResultMatch + "}</em>" : message + " <em>{" + sResultMatch + "}</em>")
                        + "</span>";
                  }
                  else
                  {
                     message = me.msg("file-to.substitution." + sResultMatch + ".label");
                     message =
                        "<span class='substitutionSuggestion'>"
                        + (message.indexOf("file-to.substitution.") != -1 ? " <em>{" + sResultMatch + "}</em>" : message + " <em>{" + sResultMatch + "}</em>")
                        + "</span>";
                  }
                  return message;
               };

               // work in both directions from the cursor to get the current fragment to send to the
               // substitution suggestions api
               autoComp.generateRequest = function(sQuery)
               {
                  Selector.query("[param=" + "path" + "]")[0].value = el.value;
                  var fragmentDetails = getAutoCompleteFragment(el.value, getCursorPosition(el));
                  autoCompleteSelectPreFragment = fragmentDetails[0];
                  var fragment = fragmentDetails[1];
                  autoCompleteSelectPostFragment = fragmentDetails[2];
                  var parameterString = "?fragment=" + fragment.replace(/ /g,'+') + "&path=";
                  if((autoCompleteSelectPreFragment.indexOf("{") == -1) && (autoCompleteSelectPreFragment.indexOf("}") == -1))
                  {
                     parameterString += autoCompleteSelectPreFragment.replace(/ /g,'+');
                  }
                  parameterString += "&unfiled=" + unfiled;
                  parameterString = Alfresco.util.encodeURIPath(parameterString);
                  return parameterString;
               }

               // handle the autocomplete selection handler so we place the suggestion in the
               // current path value at the correct place rather than the default behaviour
               // of overwriting the whole thing
               var itemSelectHandler = function(sType, aArgs)
               {
                  var oData = aArgs[2];
                  var selectedValue = oData[0];
                  var path = autoCompleteSelectPreFragment;
                  if(selectedValue.indexOf("/") == 0)
                  {
                     if((autoCompleteSelectPreFragment.length > 0) && (autoCompleteSelectPreFragment.charAt(autoCompleteSelectPreFragment.length - 1) == "/"))
                     {
                        path += selectedValue.substring(1);
                     }
                     else
                     {
                        path += selectedValue;
                     }
                  }
                  else
                  {
                     if(!((autoCompleteSelectPreFragment.length > 0) && (autoCompleteSelectPreFragment.charAt(autoCompleteSelectPreFragment.length - 1) == "{")))
                     {
                        path += "{";
                     }
                     path += selectedValue;
                     if(!((autoCompleteSelectPostFragment.length > 0) && (autoCompleteSelectPostFragment.charAt(0) == "}")))
                     {
                        path += "}";
                     }
                  }
                  path += autoCompleteSelectPostFragment;
                  el.value = path;
               };
               autoComp.itemSelectEvent.subscribe(itemSelectHandler);

               function getCursorPosition(textField)
               {
                  if(!textField) return;
                  if('selectionStart' in textField)
                  {
                     return textField.selectionStart;
                  }
                  else if(document.selection)
                  {
                        // IE8
                        input.focus();
                        var sel = document.selection.createRange();
                        var selLen = document.selection.createRange().text.length;
                        sel.moveStart('character', -input.value.length);
                        return sel.text.length - selLen;
                  }
               }

               // get the auto complete fragment from the whole path by traveling in both directions
               // from the cursor position looking for curly braces and path separators
               function getAutoCompleteFragment(fullPathText, cursorPosition)
               {
                  var fragment = fullPathText;
                  var preFragment = "";
                  var postFragment = "";
                  if(cursorPosition != undefined)
                  {
                     var preCursorText = fullPathText.substring(0, cursorPosition);
                     var postCursorText = fullPathText.substring(cursorPosition);
                     var lastPathDelim = preCursorText.lastIndexOf('/');
                     var lastStartSubstitutionDelim = preCursorText.lastIndexOf('{');
                     var lastEndSubstitutionDelim = preCursorText.lastIndexOf('}');
                     var startFragment = Math.max(lastPathDelim, lastStartSubstitutionDelim, lastEndSubstitutionDelim);
                     var firstPathDelim = postCursorText.indexOf('/');
                     var firstStartSubstitutionDelim = postCursorText.indexOf('{');
                     var firstEndSubstitutionDelim = postCursorText.indexOf('}');
                     var endFragment = Math.min(
                        firstPathDelim == -1 ? Number.MAX_VALUE : firstPathDelim,
                        firstStartSubstitutionDelim == -1 ? Number.MAX_VALUE : firstStartSubstitutionDelim,
                        firstEndSubstitutionDelim == -1 ? Number.MAX_VALUE : firstEndSubstitutionDelim
                     );
                     preFragment = preCursorText.substring(0, startFragment + 1);
                     fragment = preCursorText.substring(startFragment + 1) + (endFragment == -1 ? postCursorText : postCursorText.substring(0, endFragment));
                     postFragment = postCursorText.substring(endFragment);
                  }
                  return [preFragment, fragment, postFragment];
               }
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
