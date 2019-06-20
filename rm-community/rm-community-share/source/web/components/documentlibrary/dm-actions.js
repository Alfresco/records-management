/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2019 Alfresco Software Limited
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
 * Document Library Actions module
 *
 * @namespace Alfresco.doclib
 * @class Alfresco.doclib.Actions
 */
(function()
{
   var $html = Alfresco.util.encodeHTML;

   YAHOO.Bubbling.fire("registerAction",
   {
      actionName: "onHideRecordAction",
      fn: function DLTB_onHideRecordAction(record, owner)
      {
         var me = this;
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("message.confirm.hide.record.title"),
            text: this.msg("message.confirm.hide.record"),
            buttons: [
            {
               text: this.msg("button.ok"),
               handler: function DLTB_onHideRecordAction_confirm_ok()
               {
                  me.onActionSimpleRepoAction(record, owner);
                  this.destroy();
               }
            },
            {
               text: this.msg("button.cancel"),
               handler: function DLTB_onHideRecordAction_confirm_cancel()
               {
                  this.destroy();
               },
               isDefault: true
            }]
         });
      }
   });

   YAHOO.Bubbling.fire("registerAction",
   {
      actionName: "onCreateRecordSuccess",
      fn: function DLTB_onCreateRecordSuccess(record, owner)
      {
         if (this.actionsView === "details")
         {
            YAHOO.Bubbling.unsubscribe("metadataRefresh", this.doRefresh, this);
            window.location.reload(true);
         }
      }
   });

   YAHOO.Bubbling.fire("registerAction",
   {
      actionName: "onHideRecordSuccess",
      fn: function DLTB_onHideRecordSuccess(record, owner)
      {
         if (this.actionsView === "details")
         {
            window.location.href = window.location.href.split("document-details")[0] + "documentlibrary";
         }
      }
   });

   YAHOO.Bubbling.fire("registerAction",
   {
      actionName: "onActionDmMoveTo",
      fn: function DLTB_onActionDmMoveTo(record, owner)
      {
         this.modules.dmMoveTo = new Alfresco.module.DoclibCopyMoveTo(this.id + "-dmMoveTo");

         this.modules.dmMoveTo.setOptions(
         {
            mode: "move",
            siteId: this.options.siteId,
            containerId: this.options.containerId,
            path: this.currentPath,
            files: record,
            rootNode: this.options.rootNode,
            parentId: this.getParentNodeRef(record),
            width: "10em"
         }).showDialog();

         this.modules.dmMoveTo._showDialog = function DLTB__showDialog()
         {
            this.widgets.okButton.set("label", this.msg("button"));

            Dom.getPreviousSibling(this.id + "-modeGroup").setAttribute("style", "display:none");
            Dom.get(this.id + "-modeGroup").setAttribute("style", "display:none");
            Dom.getPreviousSibling(this.id + "-sitePicker").setAttribute("style", "display:none");
            Dom.get(this.id + "-sitePicker").setAttribute("style", "display:none");
            Dom.get(this.id + "-treeview").setAttribute("style", "width:35em !important");

            return Alfresco.module.DoclibCopyMoveTo.superclass._showDialog.apply(this, arguments);
         }

         var me = this;
         this.modules.dmMoveTo.onOK = function DLTB_onOK(e, p_obj)
         {
            record.targetNodeRef = this.selectedNode.data.nodeRef;
            me.onActionSimpleRepoAction(record, owner);
            this.widgets.dialog.hide();
         }
      }
   });

   YAHOO.Bubbling.fire("registerAction",
   {
      actionName: "onRecordedVersionConfig",
      fn: function DLTB_onRecordedVersionConfig(record, owner)
      {
         var nodeRef = record.nodeRef.replace(":/", ""),
            me = this;
         this.modules.recordedVersionConfig = new Alfresco.module.SimpleDialog(this.id + "-recordedVersionConfig").setOptions(
         {
            width: "40em",
            templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "rm/modules/documentlibrary/recorded-version-config?nodeRef=" + nodeRef,
            actionUrl: Alfresco.constants.PROXY_URI + "slingshot/doclib/action/recorded-version-config/node/" + nodeRef,
            onSuccess:
            {
               fn: function RDLA_onRecordedVersionConfig_SimpleDialog_success(response)
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: me.msg("message.recordedVersionConfig.success")
                  });

                  if (this.actionsView !== "details")
                  {
                     // Fire event so compnents on page are refreshed
                     YAHOO.Bubbling.fire("metadataRefresh");
                  }
               },
               scope: this
            },
            onFailure:
            {
               fn: function RDLA_onRecordedVersionConfig_SimpleDialog_failure(response)
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: me.msg("message.recordedVersionConfig.failure")
                  });
               },
               scope: this
            }
         });
         this.modules.recordedVersionConfig.show();
      }
   });

   YAHOO.Bubbling.fire("registerAction",
      {
         actionName: "onActionDeclareAndFileTo",
         fn: function DLTB_onActionDeclareAndFileTo(assets, owner) {
            if (!this.modules.copyMoveLinkFileTo)
            {
               this.modules.copyMoveLinkFileTo = new Alfresco.rm.module.CopyMoveLinkFileTo(this.id + "-copyMoveLinkFileTo");
            }

            this.modules.copyMoveLinkFileTo.setOptions(
               {
                  mode: "declareAndFile",
                  siteId: "rm",
                  containerId: this.options.containerId,
                  path: this.currentPath,
                  files: assets,
                  unfiled: false,
                  width: "40em"
               }).showDialog();

            var me = this;
            this.modules.copyMoveLinkFileTo.onOK = function DLTB_onOK(e, p_obj) {
               assets.path = me.modules.copyMoveLinkFileTo.selectedNode.data.path.substr(1);
               me.onActionSimpleRepoAction(assets, owner);
               this.widgets.dialog.hide();
            }
         }

      });

   onRejectedRecordInfo = function RM_onRejectedRecordInfo(nodeRef, displayName, rejectReason, userId, date)
   {
      new Alfresco.module.SimpleDialog("rejectedRecordInfoDialog").setOptions(
      {
         templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "rm/modules/documentlibrary/rejected-record-info",
         actionUrl: null,
         doBeforeDialogShow:
         {
            fn: function RM_onRejectedRecordInfo_doBeforeDialogShow(p_form, p_dialog)
            {
               var description = Dom.get("rejectedRecordInfoDialog-description");
               description.innerHTML = YAHOO.lang.substitute(description.innerHTML, {'displayName' : displayName});
               Dom.get("rejectedRecordInfoDialog-userId").setAttribute("value", userId);
               Dom.get("rejectedRecordInfoDialog-date").setAttribute("value", date);
               Dom.get("rejectedRecordInfoDialog-rejectReason").innerHTML = decodeURI(rejectReason);
            },
            scope: this
         }
      }).show();
   };

   onRejectedRecordClose = function RM_onRejectedRecordClose(nodeRef, title, text, buttonYes, buttonNo)
   {
      Alfresco.util.PopupManager.displayPrompt(
      {
         title: title,
         text: text,
         buttons: [
         {
            text: buttonYes,
            handler: function DLTB_onRejectedRecordClose_confirm_yes()
            {
               Alfresco.util.Ajax.jsonPost(
               {
                  url: Alfresco.constants.PROXY_URI + 'slingshot/doclib/action/aspects/node/' + nodeRef.replace(":/", ""),
                  dataObj:
                  {
                     added: [],
                     removed: ["rma:recordRejectionDetails"]
                  },
                  successCallback:
                  {
                     fn: function DLTB_onRejectedRecordClose_confirm_success()
                     {
                        YAHOO.Bubbling.fire("metadataRefresh");
                     },
                     scope: this
                  },
                  failureCallback:
                  {
                     fn: function DLTB_onRejectedRecordClose_confirm_failure(response)
                     {
                        Alfresco.util.PopupManager.displayMessage(
                        {
                           text: Alfresco.util.Ajax.sanitizeMarkup(response.serverResponse.responseText)
                        });
                     }
                  }
               });

               this.destroy();
            }
         },
         {
            text: buttonNo,
            handler: function DLTB_onRejectedRecordClose_confirm_no()
            {
               this.destroy();
            },
            isDefault: true
         }]
      });
   };

   onChildClassificationCompleteClose = function RM_onChildClassificationCompleteClose(nodeRef, title, text, buttonYes, buttonNo) {
      Alfresco.util.PopupManager.displayPrompt(
      {
         title: title,
         text: text,
         buttons: [
         {
            text: buttonYes,
            handler: function DLTB_onChildClassificationCompleteClose_confirm_yes() {
               Alfresco.util.Ajax.jsonPost(
               {
                  url: Alfresco.constants.PROXY_URI + 'api/node/' + nodeRef.replace(":/", "") + '/childclassificationcompleteddismiss',
                  dataObj: {},
                  successCallback:
                  {
                     fn: function DLTB_onChildClassificationCompleteClose_confirm_success() {
                        YAHOO.Bubbling.fire("metadataRefresh");
                     },
                     scope: this
                  },
                  failureCallback:
                  {
                     fn: function DLTB_onChildClassificationCompleteClose_confirm_failure(response) {
                        Alfresco.util.PopupManager.displayMessage(
                        {
                           text: Alfresco.util.Ajax.sanitizeMarkup(response.serverResponse.responseText)
                        });
                     }
                  }
               });

               this.destroy();
            }
         },
         {
            text: buttonNo,
            handler: function DLTB_onChildClassificationCompleteClose_confirm_no() {
               this.destroy();
            },
            isDefault: true
         }]
      });
   };

   YAHOO.Bubbling.fire("registerRenderer",
   {
      propertyName: "RM_rejectedRecordInfo",
      renderer: function RM_rejectedRecordInfo_renderer(record, label)
      {
         var funcArgs = "\"",
            properties = record.node.properties;
         funcArgs += record.nodeRef;
         funcArgs += "\",\"";
         funcArgs += record.displayName;
         funcArgs += "\",\"";
         funcArgs += encodeURI(properties["rma:recordRejectionReason"]);
         funcArgs += "\",\"";
         funcArgs += properties["rma:recordRejectionUserId"];
         funcArgs += "\",\"";
         funcArgs += Alfresco.util.formatDate(properties["rma:recordRejectionDate"].iso8601);
         funcArgs += "\"";

         return '<a href="#" onclick="onRejectedRecordInfo(' + $html(funcArgs) + ');return false;" title="' + this.msg("banner.rejected-record.info") + '" class="item item-rejected-record-info">&nbsp;</a>';
      }
   });

   YAHOO.Bubbling.fire("registerRenderer",
   {
      propertyName: "RM_rejectedRecordClose",
      renderer: function RM_rejectedRecordClose_renderer(record, label)
      {
         var funcArgs = "\"";
         funcArgs += record.nodeRef;
         funcArgs += "\",\"";
         funcArgs += this.msg("message.confirm.close-rejected-record.title");
         funcArgs += "\",\"";
         funcArgs += this.msg("message.confirm.close-rejected-record");
         funcArgs += "\",\"";
         funcArgs += this.msg("button.yes");
         funcArgs += "\",\"";
         funcArgs += this.msg("button.no");
         funcArgs += "\"";

         return '<a href="#" onclick="onRejectedRecordClose(' + $html(funcArgs) + ');return false;" title="' + this.msg("banner.rejected-record.close") + '" class="item item-rejected-record-close">&nbsp;</a>';
      }
   });

   YAHOO.Bubbling.fire("registerRenderer",
   {
      propertyName: "RM_childClassificationCompleteClose",
      renderer: function RM_childClassificationCompleteClose_renderer(record, label) {
         var funcArgs = "\"";
         funcArgs += record.nodeRef;
         funcArgs += "\",\"";
         funcArgs += this.msg("message.confirm.close-child-classification-complete.title");
         funcArgs += "\",\"";
         funcArgs += this.msg("message.confirm.close-child-classification-complete");
         funcArgs += "\",\"";
         funcArgs += this.msg("button.yes");
         funcArgs += "\",\"";
         funcArgs += this.msg("button.no");
         funcArgs += "\"";

         return '<a href="#" onclick="onChildClassificationCompleteClose(' + $html(funcArgs) + ');return false;" title="' + this.msg("banner.child-classification-complete.close") + '" class="item item-rejected-record-close">&nbsp;</a>';
      }
   });

   /**
    *  Create a bridge between an Aikau data-update event and a YAHOO meta-data-refresh event.
    */
   require(["rm/services/AlfRmActionBridge"], function (Bridge)
   {
      var bridge = new Bridge();
      bridge.alfSubscribe("ALF_DOCLIST_RELOAD_DATA", function()
      {
         // Are we on a Document List page (Collab site or RM File Plan)?
         if (Alfresco.util.ComponentManager.findFirst("Alfresco.DocumentList"))
         {
            // If we are, trigger a data refresh to update:
            YAHOO.Bubbling.fire("metadataRefresh");
         }
         else
         {
            // otherwise reload the page to update:
            window.location.reload(true);
         }
      });
   });
})();
