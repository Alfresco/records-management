/**
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Document Library Actions module
 *
 * @namespace Alfresco.doclib
 * @class Alfresco.doclib.Actions
 */
(function()
{
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

                  // Fire event so compnents on page are refreshed
                  YAHOO.Bubbling.fire("metadataRefresh");
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
})();