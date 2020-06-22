/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2020 Alfresco Software Limited
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
 * @namespace
 * @class DM.RuleConfigActionCustom
 */
if (typeof DM == "undefined" || !DM)
{
   var DM = {};
}
(function () {
   /**
    * Alfresco Slingshot aliases
    */
   var $hasEventInterest = Alfresco.util.hasEventInterest;

   DM.RuleConfigActionCustom = function (htmlId) {
      DM.RuleConfigActionCustom.superclass.constructor.call(this, htmlId);

      // Re-register with our own name
      this.name = "DM.RuleConfigActionCustom";
      Alfresco.util.ComponentManager.reregister(this);

      // Instance variables
      this.customisations = YAHOO.lang.merge(this.customisations, DM.RuleConfigActionCustom.superclass.customisations);
      this.renderers = YAHOO.lang.merge(this.renderers, DM.RuleConfigActionCustom.superclass.renderers);

      return this;
   };

   YAHOO.extend(DM.RuleConfigActionCustom, Alfresco.RuleConfigAction,
      {
         customisations:
            {
               DeclareAndFileRecord:
                  {
                     text: function (configDef, ruleConfig, configEl) {
                        this._getParamDef(configDef, "path").displayLabel = this.msg("create-record.path.label");
                        this._getParamDef(configDef, "hideRecord").displayLabel = this.msg("create-record.hideRecord.label");
                        return configDef;
                     },
                     edit: function (configDef, ruleConfig, configEl) {
                        this._hideParameters(configDef.parameterDefinitions);

                        configDef.parameterDefinitions.splice(0, 0,
                           {
                              type: "arca:dm-fileTo-destination-dialog-button",
                              _buttonLabel: this.msg("button.select-folder"),
                              _destinationParam: "destination-folder"
                           });

                        configDef.parameterDefinitions.splice(1, 0,
                           {
                              type: "arca:record-path-help-icon"
                           });

                        var path = this._getParamDef(configDef, "path");
                        path._type = "hidden";
                        path.displayLabel = this.msg("create-record.path.label"),
                        path._displayLabelToRight = false;
                        path._hideColon = true;

                        var hideRecord = this._getParamDef(configDef, "hideRecord");
                        hideRecord._type = null;
                        hideRecord.displayLabel = this.msg("create-record.hideRecord.label"),
                           hideRecord._displayLabelToRight = false;
                        hideRecord._hideColon = true;

                        return configDef;
                     }
                  }
            },
         renderers:
            {
               "arca:dm-fileTo-destination-dialog-button":
                  {
                     manual: { edit: true },
                     currentCtx: {},
                     edit: function (containerEl, configDef, paramDef, ruleConfig, value)
                     {
                        var mode = "declareAndFile";

                        var selectedPath = ruleConfig.parameterValues && ruleConfig.parameterValues.path;
                        this._createButton(containerEl, configDef, paramDef, ruleConfig, function RCA_destinationDialogButton_onClick(type, obj)
                        {
                           this.renderers["arca:dm-fileTo-destination-dialog-button"].currentCtx =
                              {
                                 configDef: obj.configDef,
                                 ruleConfig: obj.ruleConfig,
                                 paramDef: obj.paramDef
                              };
                           this.widgets.copyMoveLinkFileToDialog = new Alfresco.rm.module.CopyMoveLinkFileTo(this.id + "-destinationDialog");
                           this.widgets.copyMoveLinkFileToDialog.setOptions(
                              {
                                 title: this.msg("dialog.destination.title"),
                                 mode: mode,
                                 files: "",
                                 siteId: "rm",
                                 path: selectedPath,
                                 unfiled: false
                              });

                           YAHOO.Bubbling.on("folderSelected", function (layer, args)
                           {
                              if ($hasEventInterest(this.widgets.copyMoveLinkFileToDialog, args))
                              {
                                 var selectedFolder = args[1].selectedFolder;
                                 if (selectedFolder !== null)
                                 {
                                    var ctx = this.renderers["arca:dm-fileTo-destination-dialog-button"].currentCtx;
                                    var path = (selectedFolder.path !== "/") ? selectedFolder.path : "";
                                    this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, "path", path);
                                    Dom.get(this.id + "-recordFolderPath").value = path;
                                    this._updateSubmitElements(ctx.configDef);
                                    this.widgets.copyMoveLinkFileToDialog.setOptions({
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
                           this.widgets.copyMoveLinkFileToDialog.setOptions(
                              {
                                 allowedViewModes: allowedViewModes,
                                 nodeRef: this.options.rootNode,
                                 pathNodeRef: pathNodeRef ? new Alfresco.util.NodeRef(pathNodeRef) : null
                              });

                           this.widgets.copyMoveLinkFileToDialog.onOK = function()
                           {
                              YAHOO.Bubbling.fire("folderSelected",
                                 {
                                    selectedFolder: this.selectedNode ? this.selectedNode.data : null,
                                    eventGroup: this
                                 });
                              this.widgets.dialog.hide();
                           }

                           var me = this;
                           this.widgets.copyMoveLinkFileToDialog._showDialog = function()
                           {
                              this.widgets.okButton.set("label", me.msg("create-record.button"));
                              return Alfresco.rm.module.CopyMoveLinkFileTo.superclass._showDialog.apply(this, arguments);
                           }

                           this.widgets.copyMoveLinkFileToDialog.showDialog();
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
