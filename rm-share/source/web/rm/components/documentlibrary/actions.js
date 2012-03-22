/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
 * RM Document Library Actions module
 * 
 * @namespace Alfresco.doclib
 * @class Alfresco.rm.doclib.Actions
 */
(function()
{
   /**
    * Alfresco.rm.doclib.Actions namespace
    */
   Alfresco.rm.doclib.Actions = {};

   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $combine = Alfresco.util.combinePaths,
      $siteURL = Alfresco.util.siteURL,
      $isValueSet = Alfresco.util.isValueSet;

   Alfresco.rm.doclib.Actions.prototype =
   {
      /**
       * The urls to be used when creating links in the action cell
       *
       * @method getActionUrls
       * @param record {object} Object literal representing the node
       * @param siteId {string} Optional siteId override for site-based locations
       * @return {object} Object literal containing URLs to be substituted in action placeholders
       */
      getActionUrls: function RDLA_getActionUrls(record, siteId)
      {
         var jsNode = record.jsNode,
            nodeRef = jsNode.isLink ? jsNode.linkedNode.nodeRef : jsNode.nodeRef,
            strNodeRef = nodeRef.toString(),
            nodeRefUri = nodeRef.uri,
            contentUrl = jsNode.contentURL,
            siteObj = YAHOO.lang.isString(siteId) ? { site: siteId } : null,
            fnPageURL = Alfresco.util.bind(function(page)
            {
               return Alfresco.util.siteURL(page, siteObj);
            }, this),
            filePlan = (new Alfresco.util.NodeRef(jsNode.properties.rma_rootNodeRef)).uri;

         return (
         {
            downloadUrl: $combine(Alfresco.constants.PROXY_URI, contentUrl) + "?a=true",
            viewUrl:  $combine(Alfresco.constants.PROXY_URI, contentUrl) + "\" target=\"_blank",
            documentDetailsUrl: fnPageURL("document-details?nodeRef=" + strNodeRef),
            folderDetailsUrl: fnPageURL("folder-details?nodeRef=" + strNodeRef),
            editMetadataUrl: fnPageURL("edit-metadata?nodeRef=" + strNodeRef),
            inlineEditUrl: fnPageURL("inline-edit?nodeRef=" + strNodeRef),
            recordSeriesDetailsUrl: fnPageURL("rm-record-series-details?nodeRef=" + nodeRef),
            recordCategoryDetailsUrl: fnPageURL("rm-record-category-details?nodeRef=" + nodeRef),
            recordFolderDetailsUrl: fnPageURL("rm-record-folder-details?nodeRef=" + nodeRef),
            transfersZipUrl: $combine(Alfresco.constants.PROXY_URI, "api/node", filePlan, "transfers", nodeRef.id),
            managePermissionsUrl: fnPageURL("rm-permissions?nodeRef=" + nodeRef + "&itemName=" + encodeURIComponent(record.displayName) + "&nodeType=" + jsNode.type)
         });
      },

      /**
       * Public Action implementations.
       *
       * NOTE: Actions are defined in alphabetical order by convention.
       */
      
      /**
       * Accession action.
       *
       * @method onActionAccession
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionAccession: function RDLA_onActionAccession(assets)
      {
         this._rmAction("message.accession", assets, "accession", null,
         {
            success:
            {
               callback:
               {
                  fn: this._transferAccessionComplete,
                  obj:
                  {
                     displayName: YAHOO.lang.isArray(assets) ? this.msg("message.multi-select", assets.length) : $html(assets.displayName)
                  },
                  scope: this
               }
            }
         });
      },

      /**
       * Accession Complete action.
       *
       * @method onActionAccessionComplete
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionAccessionComplete: function RDLA_onActionAccessionComplete(assets)
      {
         this._rmAction("message.accession-complete", assets, "accessionComplete");
      },

      /**
       * Copy single document or folder.
       *
       * @method onActionCopyTo
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionCopyTo: function RDLA_onActionCopyTo(assets)
      {
         this._copyMoveFileTo("copy", assets);
      },

      /**
       * File single document or folder.
       *
       * @method onActionFileTo
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionFileTo: function RDLA_onActionFileTo(assets)
      {
         this._copyMoveFileTo("file", assets);
      },

      /**
       * Move single document or folder.
       *
       * @method onActionMoveTo
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionMoveTo: function RDLA_onActionMoveTo(assets)
      {
         this._copyMoveFileTo("move", assets);
      },
      
      /**
       * Declare Record action.
       * Special case handling due to the ability to jump to the Edit Metadata page if the action failed.
       *
       * @method onActionDeclare
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionDeclare: function RDLA_onActionDeclare(assets)
      {
         var displayName = $html(assets.displayName),
            editMetadataUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/edit-metadata?nodeRef=" + assets.nodeRef;

         this._rmAction("message.declare", assets, "declareRecord", null,
         {
            failure:
            {
               message: null,
               callback:
               {
                  fn: function RDLA_oAD_failure(data)
                  {
                     Alfresco.util.PopupManager.displayPrompt(
                     {
                        title: this.msg("message.declare.failure", displayName),
                        text: this.msg("message.declare.failure.more"),
                        buttons: [
                        {
                           text: this.msg("actions.edit-details"),
                           handler: function RDLA_oAD_failure_editDetails()
                           {
                              window.location = editMetadataUrl;
                              this.destroy();
                           },
                           isDefault: true
                        },
                        {
                           text: this.msg("button.cancel"),
                           handler: function RDLA_oAD_failure_cancel()
                           {
                              this.destroy();
                           }
                        }]
                     });
                  },
                  scope: this
               }
            }
         });
      },

      /**
       * Destroy action.
       *
       * @method onActionDestroy
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionDestroy: function RDLA_onActionDestroy(assets)
      {
         // If "Destroy" was triggered from the documentlist assets contain an object instead of an array
         var me = this,
            noOfAssets = YAHOO.lang.isArray(assets) ? assets.length : 1,
            text;
         
         if (noOfAssets == 1)
         {
            text = this.msg("message.confirm.destroy", $html((YAHOO.lang.isArray(assets) ? assets[0].displayName : assets.displayName)));
         }
         else
         {
            text = this.msg("message.confirm.destroyMultiple", noOfAssets);             
         }

         // Show the first confirmation dialog
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("message.confirm.destroy.title"),
            text: text,
            buttons: [
            {
               text: this.msg("button.ok"),
               handler: function RDLA_onActionDestroy_confirm_ok()
               {
                  // Hide the first confirmation dialog
                  this.destroy();

                  // Display the second confirmation dialog
                  text = (noOfAssets == 1 ? me.msg("message.confirm2.destroy") : me.msg("message.confirm2.destroyMultiple"));
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     title: me.msg("message.confirm2.destroy.title"),
                     text: text,
                     buttons: [
                     {
                        text: me.msg("button.ok"),
                        handler: function RDLA_onActionDestroy_confirm2_ok()
                        {
                           // Hide the second confirmation dialog
                           this.destroy();

                           // Call the destroy action
                           me._rmAction("message.destroy", assets, "destroy");
                        },
                        isDefault: true
                     },
                     {
                        text: me.msg("button.cancel"),
                        handler: function RDLA_onActionDestroy_confirm2_cancel()
                        {
                           // Hide the second confirmation dialog
                           this.destroy();
                        }
                     }]
                  });

               },
               isDefault: true
            },
            {
               text: this.msg("button.cancel"),
               handler: function RDLA_onActionDestroy_confirm_cancel()
               {
                  // Hide the first confirmation dialog
                  this.destroy();
               }
            }]
         });

      },

      /**
       * Edit Disposition As Of Date action.
       *
       * @method onActionEditDispositionAsOf
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionEditDispositionAsOf: function RDLA_onActionEditDispositionAsOf(assets)
      {
         var calendarId = Alfresco.util.generateDomId(),
            properties = assets.jsNode.properties,
            asOfDate = Alfresco.util.fromExplodedJSONDate(properties.rma_recordSearchDispositionActionAsOf),
            panel,
            calendar;
         
         panel = Alfresco.util.PopupManager.getUserInput(
         {
            title: this.msg("message.edit-disposition-as-of-date.title"),
            html: '<div id="' + calendarId + '"></div>',
            initialShow: false,
            okButtonText: this.msg("button.update"),
            callback:
            {
               fn: function RDLA_onActionEditDispositionAsOf_callback(unused, cal)
               {
                  this._rmAction("message.edit-disposition-as-of-date", assets, "editDispositionActionAsOfDate",
                  {
                     asOfDate:
                     {
                        iso8601: Alfresco.util.toISO8601(cal.getSelectedDates()[0])
                     }
                  });
               },
               scope: this
            }
         });

         var page = (asOfDate.getMonth() + 1) + "/" + asOfDate.getFullYear(),
            selected = (asOfDate.getMonth() + 1) + "/" + asOfDate.getDate() + "/" + asOfDate.getFullYear();   
         calendar = new YAHOO.widget.Calendar(calendarId,
         {
            iframe: false
         });
         calendar.cfg.setProperty("pagedate", page);
         calendar.cfg.setProperty("selected", selected);
         calendar.render();
         calendar.show();
         // Center the calendar
         Dom.setStyle(calendarId, "margin", "0 2em");
         // Only now can we set the panel button's callback reference to the calendar, as it was undefined on panel creation
         panel.cfg.getProperty("buttons")[0].handler.obj.callback.obj = calendar;
         panel.center();
         panel.show();
      },

      /**
       * Edit Hold Details action.
       *
       * @method onActionEditHoldDetails
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionEditHoldDetails: function RDLA_onActionEditHoldDetails(assets)
      {
         var properties = assets.jsNode.properties;

         Alfresco.util.PopupManager.getUserInput(
         {
            title: this.msg("message.edit-hold.title"),
            text: this.msg("message.edit-hold.reason.label"),
            value: properties.rma_holdReason,
            okButtonText: this.msg("button.update"),
            callback:
            {
               fn: function RDLA_onActionEditHoldDetails_callback(value)
               {
                  this._rmAction("message.edit-hold", assets, "editHoldReason",
                  {
                     "reason": value
                  });
               },
               scope: this
            }
         });
      },

      /**
       * Edit Review As Of Date action.
       *
       * @method onActionEditReviewAsOf
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionEditReviewAsOf: function RDLA_onActionEditReviewAsOf(assets)
      {
         var properties = assets.jsNode.properties,
            asOfDate = new Date();

         if (properties == null) 
         {
            nodeId = Alfresco.util.NodeRef(assets.nodeRef);

            url = Alfresco.constants.PROXY_URI
               + "slingshot/rmsearch/rm"
               + '?site=rm&query=(ASPECT:"rma:record" AND ASPECT:"rma:declaredRecord") AND (rma:identifier:'
               + nodeId.id + ') AND NOT ASPECT:"rma:versionedRecord"';

            YAHOO.util.Connect.asyncRequest("GET", url, 
            {
               success: function(resp) 
               {
                  item = YAHOO.lang.JSON.parse(resp.responseText);
                  if (null != item.items[0].properties.rma_reviewAsOf) 
                  {
                     asOfDate = new Date(item.items[0].properties.rma_reviewAsOf);
                     var page = (asOfDate.getMonth() + 1) + "/" + asOfDate.getFullYear(),
                        selected = (asOfDate.getMonth() + 1) + "/" + asOfDate.getDate() + "/" + asOfDate.getFullYear();
                     calendar.cfg.setProperty("pagedate", page);
                     calendar.cfg.setProperty("selected", selected);
                     calendar.render();
                  }
               }
            }, null);
         }
         else 
         {
            asOfDate = Alfresco.util.fromExplodedJSONDate(properties.rma_reviewAsOf);
         }

         var calendarId = Alfresco.util.generateDomId(),
            panel,
            calendar;
         
         panel = Alfresco.util.PopupManager.getUserInput(
         {
            title: this.msg("message.edit-review-as-of-date.title"),
            html: '<div id="' + calendarId + '"></div>',
            initialShow: false,
            okButtonText: this.msg("button.update"),
            callback:
            {
               fn: function RDLA_onActionEditReviewAsOf_callback(unused, cal)
               {
                  this._rmAction("message.edit-review-as-of-date", assets, "editReviewAsOfDate",
                  {
                     asOfDate:
                     {
                        iso8601: Alfresco.util.toISO8601(cal.getSelectedDates()[0])
                     }
                  });
               },
               scope: this
            }
         });

         var page = (asOfDate.getMonth() + 1) + "/" + asOfDate.getFullYear(),
            selected = (asOfDate.getMonth() + 1) + "/" + asOfDate.getDate() + "/" + asOfDate.getFullYear();   

         calendar = new YAHOO.widget.Calendar(calendarId,
         {
            iframe: false
         });
         calendar.cfg.setProperty("pagedate", page);
         calendar.cfg.setProperty("selected", selected);
         calendar.render();
         calendar.show();
         // Center the calendar
         Dom.setStyle(calendarId, "margin", "0 2em");
         // Only now can we set the panel button's callback reference to the calendar, as it was undefined on panel creation
         panel.cfg.getProperty("buttons")[0].handler.obj.callback.obj = calendar;
         panel.center();
         panel.show();
      },

      /**
       * Export action.
       *
       * @method onActionExport
       * @param assets {array} Array representing one or more file(s) or folder(s) to be exported
       */
      onActionExport: function RDLA_onActionExport(assets)
      {
         // Save the nodeRefs
         var nodeRefs = [];
         for (var i = 0, ii = assets.length; i < ii; i++)
         {
            nodeRefs.push(assets[i].nodeRef);
         }

         // Open the export dialog
         if (!this.modules.exportDialog)
         {
            // Load if for the first time
            this.modules.exportDialog = new Alfresco.module.SimpleDialog(this.id + "-exportDialog").setOptions(
            {
               width: "30em",
               templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "rm/modules/documentlibrary/export",
               actionUrl: Alfresco.constants.PROXY_URI + "api/rma/admin/export",
               firstFocus: this.id + "-exportDialog-acp",
               doBeforeFormSubmit:
               {
                  fn: function RDLA_onActionExport_SimpleDialog_doBeforeFormSubmit()
                  {
                     // Close dialog now since no callback is provided since we are submitting in a hidden iframe.
                     this.modules.exportDialog.hide();
                  },
                  scope: this
               }
            });
         }

         // doBeforeDialogShow needs re-registering each time as nodeRefs array is dynamic
         this.modules.exportDialog.setOptions(
         {
            clearForm: true,
            doBeforeDialogShow:
            {
               fn: function RDLA_onActionExport_SimpleDialog_doBeforeDialogShow(p_config, p_simpleDialog, p_obj)
               {
                  // Set the hidden nodeRefs field to a comma-separated list of nodeRefs
                  Dom.get(this.id + "-exportDialog-nodeRefs").value = p_obj.join(",");
                  var failure = "window.parent.Alfresco.util.ComponentManager.get('" + this.id + "')";
                  Dom.get(this.id + "-exportDialog-failureCallbackFunction").value = failure + ".onExportFailure";
                  Dom.get(this.id + "-exportDialog-failureCallbackScope").value = failure;
               },
               obj: nodeRefs,
               scope: this
            }
         });

         this.modules.exportDialog.show();
      },


      /**
       * Called from the hidden ifram if the Export action fails.
       *
       * @method onExportFailure
       * @param error {object} Object literal describing the error
       * @param error.status.code {string} The http status code
       * @param error.status.name {string} The error name
       * @param error.status.description {string} A description of the error status
       * @param error.message {string} An error message describing the error
       */
      onExportFailure: function RDLA_onExportFailure(error)
      {
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("message.failure"),
            text: error.message
         });
      },

      /**
       * File Transfer Report action.
       *
       * @method onActionFileTransferReport
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionFileTransferReport: function RDLA_onActionFileTransferReport(assets)
      {
         if (!this.modules.fileTransferReport)
         {
            this.modules.fileTransferReport = new Alfresco.rm.module.FileTransferReport(this.id + "-fileTransferReport");
         }

         this.modules.fileTransferReport.setOptions(
         {
            siteId: this.options.siteId,
            containerId: this.options.containerId,
            path: this.currentPath,
            fileplanNodeRef: this.doclistMetadata.filePlan,
            transfer: assets
         }).showDialog();
      },

      /**
       * Freeze action.
       *
       * @method onActionFreeze
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionFreeze: function RDLA_onActionFreeze(assets)
      {
         Alfresco.util.PopupManager.getUserInput(
         {
            title: this.msg("message.freeze.title", assets.length),
            text: this.msg("message.freeze.reason"),
            okButtonText: this.msg("button.freeze.record"),
            callback:
            {
               fn: function RDLA_onActionFreeze_callback(value)
               {
                  this._rmAction("message.freeze", assets, "freeze",
                  {
                     "reason": value
                  });
               },
               scope: this
            }
         });
      },

      /**
       * Set Record Type
       *
       * @method onActionAddRecordMetadata
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionAddRecordMetadata: function RDLA_onActionAddRecordMetadata(assets)
      {
         // Open the set record type dialog
         var addRecordMetadataWebscriptUrl = Alfresco.constants.PROXY_URI + "slingshot/doclib/action/aspects/node/" + assets.nodeRef.replace(":/", "");
         this.modules.addRecordMetadataDialog = new Alfresco.module.SimpleDialog(this.id + "-addRecordMetadataDialog").setOptions(
         {
            width: "30em",
            templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "rm/modules/documentlibrary/add-record-metadata?nodeRef=" + encodeURIComponent(assets.nodeRef),
            actionUrl: addRecordMetadataWebscriptUrl,
            firstFocus: this.id + "-addRecordMetadataDialog-recordType",
            onSuccess:
            {
               fn: function RDLA_onActionAddRecordMetadata_SimpleDialog_success(response)
               {
                  // Fire event so compnents on page are refreshed
                  YAHOO.Bubbling.fire("metadataRefresh");
               }
            }
         });
         this.modules.addRecordMetadataDialog.show();
      },

      /**
       * Transfer action.
       *
       * @method onActionTransfer
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionTransfer: function RDLA_onActionTransfer(assets)
      {
         this._rmAction("message.transfer", assets, "transfer", null,
         {
            success:
            {
               callback:
               {
                  fn: this._transferAccessionComplete,
                  obj:
                  {
                     displayName: YAHOO.lang.isArray(assets) ? this.msg("message.multi-select", assets.length) : $html(assets.displayName)
                  },
                  scope: this
               }
            }
         });
      },

      /**
       * View Audit log
       *
       * @method onActionViewAuditLog
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionViewAuditLog: function RDLA_onActionViewAuditLog(assets)
      {
         var openAuditLogWindow = function openAuditLogWindow()
         {
            return window.open(Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + '/rmaudit?nodeName='+ encodeURIComponent(assets.displayName) + '&nodeRef=' + assets.nodeRef.replace(':/',''), 'Audit_Log', 'resizable=yes,location=no,menubar=no,scrollbars=yes,status=yes,width=700,height=500');
         };
         // haven't yet opened window yet
         if (!this.fullLogWindowReference)
         {
            this.fullLogWindowReference = openAuditLogWindow.call(this);
         }
         else
         {
            // window has been opened already and is still open, so focus and reload it.
            if (!this.fullLogWindowReference.closed)
            {
               this.fullLogWindowReference.focus();
               this.fullLogWindowReference.location.reload();
            }
            //had been closed so reopen window
            else
            {
               this.fullLogWindowReference = openAuditLogWindow.call(this);
            }
         }
      },


      /**
       * Private action helper functions
       */

      /**
       * Copy/Move/File To implementation.
       *
       * @method _copyMoveFileTo
       * @param mode {String} Operation mode: copy|file|move
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       * @private
       */
      _copyMoveFileTo: function RDLA__copyMoveFileTo(mode, assets)
      {
         // Check mode is an allowed one
         if (!mode in
            {
               copy: true,
               file: true,
               move: true
            })
         {
            throw new Error("'" + mode + "' is not a valid Copy/Move/File to mode.");
         }

         if (!this.modules.copyMoveFileTo)
         {
            this.modules.copyMoveFileTo = new Alfresco.rm.module.CopyMoveFileTo(this.id + "-copyMoveFileTo");
         }

         this.modules.copyMoveFileTo.setOptions(
         {
            mode: mode,
            siteId: this.options.siteId,
            containerId: this.options.containerId,
            path: this.currentPath,
            files: assets
         }).showDialog();
      },

      /**
       * Transfer and Accession action result processing.
       *
       * @method _transferAccessionComplete
       * @param data {object} Object literal containing ajax request and response
       * @param obj {object} Caller-supplied object
       *    <pre>
       *       obj.displayName {string} Filename or number of files submitted to the action.
       *    </pre>
       * @private
       */
      _transferAccessionComplete: function RDLA__transferAccession(data, obj)
      {
         var displayName = obj.displayName;
         
         /**
          * Transfer / Accession container query success callback.
          *
          * @method fnTransferQuerySuccess
          * @param data {object} Object literal containing ajax request and response
          * @param obj {object} Caller-supplied object
          */
         var fnTransferQuerySuccess = function RDLA_onActionTransfer_fnTransferQuerySuccess(data, obj)
         {
            // Check the transfer details to optionally show the PDF warning
            if (data.json && data.json.transfer)
            {
               var transfer = data.json.transfer,
                  fileName = transfer.name,
                  accessionIndicator = transfer["rma:transferAccessionIndicator"],
                  pdfIndicator = transfer["rma:transferPDFIndicator"];

               // If we're a Document Library, then swap to the transfers filter and highlight the newly-created transfer
               if (this.name === "Alfresco.DocumentList")
               {
                  var fnAfterUpdate = function RDLA_onActionTransfer_fnTransferQuerySuccess_fnAfterUpdate()
                  {
                     YAHOO.Bubbling.fire("highlightFile",
                     {
                        fileName: fileName
                     });

                     if (pdfIndicator)
                     {
                        Alfresco.util.PopupManager.displayPrompt(
                        {
                           title: this.msg("message.pdf-record-fonts.title"),
                           text: this.msg(accessionIndicator ? "message.pdf-record-fonts.accession" : "message.pdf-record-fonts.transfer"),
                           icon: YAHOO.widget.SimpleDialog.ICON_WARN
                        });
                     }
                     else
                     {
                        Alfresco.util.PopupManager.displayMessage(
                        {
                           text: this.msg("message.transfer.success", displayName)
                        });
                     }
                  };
                  this.afterDocListUpdate.push(fnAfterUpdate);
                  YAHOO.Bubbling.fire("changeFilter",
                  {
                     filterOwner: "Alfresco.DocListFilePlan",
                     filterId: "transfers"
                  });
               }
               // Otherwise, use the metadataRefresh event
               else
               {
                  YAHOO.Bubbling.fire("metadataRefresh");
                  
                  if (pdfIndicator)
                  {
                     Alfresco.util.PopupManager.displayPrompt(
                     {
                        title: this.msg("message.pdf-record-fonts.title"),
                        text: this.msg(accessionIndicator ? "message.pdf-record-fonts.accession" : "message.pdf-record-fonts.transfer"),
                        icon: YAHOO.widget.SimpleDialog.ICON_WARN
                     });
                  }
                  else
                  {
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("message.transfer.success", displayName)
                     });
                  }
               }
            }
         };
         
         /**
          * Transfer / Accession container query failure callback.
          *
          * @method fnTransferQueryFailure
          * @param data {object} Object literal containing ajax request and response
          * @param obj {object} Caller-supplied object
          */
         var fnTransferQueryFailure = function RDLA_onActionTransfer_fnTransferQueryFailure(data, obj)
         {
            Alfresco.util.PopupManager.displayPrompt(
            {
               title: this.msg("message.pdf-record-fonts.title"),
               text: this.msg("message.pdf-record-fonts.unknown"),
               icon: YAHOO.widget.SimpleDialog.ICON_WARN
            });
         };

         // Extract the transfer container nodeRef to query it's properties
         if (data.json && data.json.results)
         {
            // Grab the resulting transfer container nodeRef
            var dataObj = data.config.dataObj,
               nodeRef = YAHOO.lang.isArray(dataObj.nodeRefs) ? dataObj.nodeRefs[0] : dataObj.nodeRef,
               transfer = new Alfresco.util.NodeRef(data.json.results[nodeRef]);
            
            // Now query the transfer nodeRef, looking for the rma:transferPDFIndicator flag
            Alfresco.util.Ajax.jsonGet(
            {
               url: Alfresco.constants.PROXY_URI + "slingshot/doclib/rm/transfer/node/" + transfer.uri,
               successCallback:
               {
                  fn: fnTransferQuerySuccess,
                  scope: this
               },
               failureCallback:
               {
                  fn: fnTransferQueryFailure,
                  scope: this
               }
            });
         }
      },

      /**
       * Records Management Repo Action.
       *
       * Accepts the following <param> declarations from the <action> config:
       *
       * action - The name of the action (e.g. closeRecordFolder)
       * success - (Optional) The name of the callback function
       * failure - (Optional) The name of the callback function
       * message - The stem of the I18N key to use when the action succeeds or fails
       *
       * @method onActionRecordsManagementRepoAction
       * @param record {object} Object literal representing the file or folder to be actioned
       * @param owner {HTMLElement} The action html element
       */
      onActionRecordsManagementRepoAction: function RDLA_onActionRecordsManagementRepoAction(record, owner)
      {
         // Get action params
         var params = this.getAction(record, owner).params;

         // Prepare genericAction config
         var config = {};

         // Add configured success callback if provided
         if (YAHOO.lang.isFunction(this[params.success]))
         {
            config.success =
            {
               callback:    
               {
                  fn: this[params.success],
                  obj: record,
                  scope: this
               }
            };
         }

         // Add configured failure callback if provided
         if (YAHOO.lang.isFunction(this[params.failure]))
         {
            config.failure =
            {
               callback:
               {
                  fn: this[params.failure],
                  obj: record,
                  scope: this
               }
            };
         }

         // Execute the repo action
         this._rmAction(params.message, record, params.action, null, config);
      },

      /**
       * RM action.
       *
       * @method _rmAction
       * @param i18n {string} Will be appended with ".success" or ".failure" depending on action outcome
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       * @param actionName {string} Name of repository action to run
       * @param actionParams {object} Optional object literal to pass parameters to the action
       * @param configOverride {object} Optional object literal to override default configuration parameters
       * @private
       */
      _rmAction: function RDLA__rmAction(i18n, assets, actionName, actionParams, configOverride)
      {
         var displayName = "",
            dataObj =
            {
               name: actionName
            };

         if (YAHOO.lang.isArray(assets))
         {
            displayName = this.msg("message.multi-select", assets.length);
            dataObj.nodeRefs = [];
            for (var i = 0, ii = assets.length; i < ii; i++)
            {
               dataObj.nodeRefs.push(assets[i].nodeRef);
            }
         }
         else
         {
            displayName = assets.displayName;
            dataObj.nodeRef = assets.nodeRef;
         }

         if (YAHOO.lang.isObject(actionParams))
         {
            dataObj.params = actionParams;
         }
         
         var config =
         {
            success:
            {
               event:
               {
                  name: "metadataRefresh"
               },
               message: this.msg(i18n + ".success", displayName)
            },
            failure:
            {
               message: this.msg(i18n + ".failure", displayName)
            },
            webscript:
            {
               method: Alfresco.util.Ajax.POST,
               stem: Alfresco.constants.PROXY_URI + "api/rma/actions/",
               name: "ExecutionQueue"
            },
            config:
            {
               requestContentType: Alfresco.util.Ajax.JSON,
               dataObj: dataObj
            }
         };
         
         if (YAHOO.lang.isObject(configOverride))
         {
            config = YAHOO.lang.merge(config, configOverride);
         }

         this.modules.actions.genericAction(config);
      },


      /**
       * LEGACY ACTION SUPPORT
       *
       * These action handlers are no longer needed by the document list component, but are needed
       * by this toolbar until it has been refactored to use the 4.0 actions configuration.
       */

      /**
       * Cut Off action.
       *
       * @method onActionCutoff
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionCutoff: function DLTB_onActionCutoff(assets)
      {
         this._rmAction("message.cutoff", assets, "cutoff");
      },

      /**
       * Transfer Complete action.
       *
       * @method onActionTransferComplete
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionTransferComplete: function DLTB_onActionTransferComplete(assets)
      {
         this._rmAction("message.transfer-complete", assets, "transferComplete");
      },

      /**
       * Undo Cut Off action.
       *
       * @method onActionUndoCutoff
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionUndoCutoff: function DLTB_onActionUndoCutoff(assets)
      {
         this._rmAction("message.undo-cutoff", assets, "unCutoff");
      },

      /**
       * Unfreeze record.
       *
       * @method onActionUnfreeze
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionUnfreeze: function DLTB_onActionUnfreeze(assets)
      {
         this._rmAction("message.unfreeze", assets, "unfreeze");
      },

      /**
       * Create Disposition.
       *
       * @method onActionUnfreeze
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionCreateDisposition: function DLTB_onActionCreateDisposition(assets)
      {
         this._rmAction("message.create-disposition-schedule", assets, "createDispositionSchedule");
      }

   };
})();
