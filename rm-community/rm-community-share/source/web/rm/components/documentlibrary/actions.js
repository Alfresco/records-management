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
            filePlan = new Alfresco.util.NodeRef(jsNode.properties.rma_rootNodeRef),
            filePlanUri = filePlan.uri,
            filePlanId = filePlan.id;

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
            transfersZipUrl: $combine(Alfresco.constants.PROXY_URI, "api/node", filePlanUri, "transfers", nodeRef.id),
            managePermissionsUrl: fnPageURL("manage-permissions?nodeRef=" + nodeRef + "&itemName=" + encodeURIComponent(record.displayName) + "&nodeType=" + jsNode.type + "&filePlanId=" + filePlanId)
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
         this._copyMoveLinkFileTo("copy", assets);
      },

      /**
       * Copy single unfiled document or folder.
       *
       * @method onActionCopyUnfiledTo
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionCopyUnfiledTo: function RDLA_onActionCopyTo(assets)
      {
         this._copyMoveLinkFileToUnfiled("copy", assets);
      },

      /**
       * Copy record.
       *
       * @method onActionCopyRecordTo
       * @param assets {object} Object literal representing one or more record(s) to be actioned
       */
      onActionCopyRecordTo: function RDLA_onActionCopyTo(assets)
      {
         this.onActionCopyTo(assets);
      },

      /**
       * Copy unfiled record.
       *
       * @method onActionCopyUnfiledRecordTo
       * @param assets {object} Object literal representing one or more record(s) to be actioned
       */
      onActionCopyUnfiledRecordTo: function RDLA_onActionCopyTo(assets)
      {
         this.onActionCopyUnfiledTo(assets);
      },

      /**
       * Copy record folder.
       *
       * @method onActionCopyRecordFolderTo
       * @param assets {object} Object literal representing one or more record folder(s) to be actioned
       */
      onActionCopyRecordFolderTo: function RDLA_onActionCopyTo(assets)
      {
         this.onActionCopyTo(assets);
      },

      /**
       * Copy unfiled record folder.
       *
       * @method onActionCopyUnfiledRecordFolderTo
       * @param assets {object} Object literal representing one or more record folder(s) to be actioned
       */
      onActionCopyUnfiledRecordFolderTo: function RDLA_onActionCopyUnfiledRecordFolderTo(assets)
      {
         this.onActionCopyUnfiledTo(assets);
      },

      /**
       * Copy record category.
       *
       * @method onActionCopyRecordCategoryTo
       * @param assets {object} Object literal representing one or more record category (categories) to be actioned
       */
      onActionCopyRecordCategoryTo: function RDLA_onActionCopyTo(assets)
      {
         this.onActionCopyTo(assets);
      },

      /**
       * Link single document or folder.
       *
       * @method onActionLinkTo
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionLinkTo: function RDLA_onActionLinkTo(assets)
      {
         this._copyMoveLinkFileTo("link", assets);
      },

      /**
       * Unlink record from current record folder
       *
       * @method onActionUnlinkFrom
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionUnlinkFrom: function RDLA_onActionUnlinkFrom(assets)
      {
        var me = this;

          // Show the first confirmation dialog
          Alfresco.util.PopupManager.displayPrompt(
          {
             title: this.msg("message.confirm.unlink.title"),
             text: this.msg("message.confirm.unlink.text"),
             buttons: [
             {
                text: this.msg("button.ok"),
                handler: function RDLA_onActionUnlinkFrom_confirm_ok()
                {
                   // Hide the confirmation dialog
                   this.destroy();

                   me._rmAction("message.unlink", assets, "unlinkFrom",
                   {
                      "recordFolder": me.doclistMetadata.parent.nodeRef
                   });

                },
                isDefault: true
             },
             {
                text: this.msg("button.cancel"),
                handler: function RDLA_onActionUnlinkFrom_confirm_cancel()
                {
                   // Hide the confirmation dialog
                   this.destroy();
                }
             }]
          });

      },

      /**
       * Move single document or folder.
       *
       * @method onActionMoveTo
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionMoveTo: function RDLA_onActionMoveTo(assets)
      {
         this._copyMoveLinkFileTo("move", assets);
      },

      /**
       * Move single unfiled document or folder.
       *
       * @method onActionMoveToUnfiled
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionMoveToUnfiled: function RDLA_onActionMoveToUnfiled(assets)
      {
         this._copyMoveLinkFileToUnfiled("move", assets);
      },

      /**
       * Move unfiled document
       *
       * @method onActionMoveUnfiledRecordTo
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionMoveUnfiledRecordTo: function RDLA_onActionMoveUnfiledRecordTo(assets)
      {
         this.onActionMoveToUnfiled(assets);
      },

      /**
       * Move unfiled record folder
       *
       * @method onActionMoveUnfiledRecordTo
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionMoveUnfiledRecordFolderTo: function RDLA_onActionMoveUnfiledRecordFolderTo(assets)
      {
         this.onActionMoveToUnfiled(assets);
      },

      /**
       * Move record
       *
       * @method onActionMoveRecordTo
       * @param assets {object} Object literal representing one or more record(s) to be actioned
       */
      onActionMoveRecordTo: function RDLA_onActionMoveTo(assets)
      {
         this.onActionMoveTo(assets);
      },

      /**
       * Move record folder
       *
       * @method onActionMoveRecordFolderTo
       * @param assets {object} Object literal representing one or more record folder(s) to be actioned
       */
      onActionMoveRecordFolderTo: function RDLA_onActionMoveTo(assets)
      {
         this.onActionMoveTo(assets);
      },

      /**
       * Move record category
       *
       * @method onActionMoveRecordCategoryTo
       * @param assets {object} Object literal representing one or more record category (categories) to be actioned
       */
      onActionMoveRecordCategoryTo: function RDLA_onActionMoveTo(assets)
      {
         this.onActionMoveTo(assets);
      },

      /**
       * Delete record
       *
       * @method onActionDeleteRecord
       * @param assets {object} Object literal representing one or more record(s) to be actioned
       */
      onActionDeleteRecord: function RDLA_onActionDeleteRecord(assets)
      {
        this.onActionDelete(assets);
      },

      /**
       * Delete record folder
       *
       * @method onActionDeleteRecordFolder
       * @param assets {object} Object literal representing one or more record folder(s) to be actioned
       */
      onActionDeleteRecordFolder: function RDLA_onActionDeleteRecordFolder(assets)
      {
         this.onActionDelete(assets);
      },

      /**
       * Delete record category
       *
       * @method onActionDeleteRecordCategory
       * @param assets {object} Object literal representing one or more record category (categories) to be actioned
       */
      onActionDeleteRecordCategory: function RDLA_onActionDeleteRecordCategory(assets)
      {
         this.onActionDelete(assets);
      },

      /**
       * File single document.
       *
       * @method onActionFileTo
       * @param assets {object} Object literal representing one or more file(s) to be actioned
       */
      onActionFileTo: function RDLA_onActionFileTo(assets)
      {
         this._copyMoveLinkFileTo("file", assets);
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
            success:
            {
               event:
               {
                  name: "metadataRefresh"
               },
               callback:
               {
                  fn: function RDLA_oAD_success(data)
                  {
                     var results = data.json.results;
                     if (results && results != null && results[data.config.dataObj.nodeRef] === "missingProperties")
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
                     }
                     else
                     {
                        Alfresco.util.PopupManager.displayMessage(
                        {
                           text: this.msg("message.declare.success", displayName)
                        });
                     }
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
                           me._rmAction("message.destroy", assets, "destroy", null,
                           {
                              success:
                              {
                                 callback:
                                 {
                                    fn: function()
                                    {
                                       Alfresco.util.PopupManager.displayMessage(
                                       {
                                          text: me.msg("message.destroy.success", $html(assets.displayName))
                                       });

                                       if (me.actionsView === "details")
                                       {
                                          var encodedPath = me.currentPath.length > 1 ? "?path=" + encodeURIComponent(me.currentPath) : "";
                                          window.location = Alfresco.util.siteURL("documentlibrary" + encodedPath);
                                       }
                                       else
                                       {
                                          YAHOO.Bubbling.fire("metadataRefresh");
                                       }
                                    },
                                    scope: this
                                 }
                              }
                           });
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

      _getRmUserInput: function RDLA_getRmUserInput(config)
      {
         if (Alfresco.util.PopupManager.defaultGetUserInputConfig.buttons[0].text === null)
         {
            /**
             * This default value could not be set at instantion time since the
             * localized messages weren't present at that time
             */
          Alfresco.util.PopupManager.defaultGetUserInputConfig.buttons[0].text = Alfresco.util.message("button.ok", this.name);
         }
         if (Alfresco.util.PopupManager.defaultGetUserInputConfig.buttons[1].text === null)
         {
          Alfresco.util.PopupManager.defaultGetUserInputConfig.buttons[1].text = Alfresco.util.message("button.cancel", this.name);
         }

         // Merge users config and the default config and check manadatory properties
         var c = YAHOO.lang.merge(Alfresco.util.PopupManager.defaultGetUserInputConfig, config);

         // Create the SimpleDialog that will display the text
         var prompt = new YAHOO.widget.SimpleDialog("userInput",
         {
            close: c.close,
            constraintoviewport: c.constraintoviewport,
            draggable: c.draggable,
            effect: c.effect,
            modal: c.modal,
            visible: c.visible,
            zIndex: this.zIndex++
         });

         // Show the title if it exists
         if (c.title)
         {
            prompt.setHeader($html(c.title));
         }

         // Generate the HTML mark-up if not overridden
         var html = c.html,
            id = Alfresco.util.generateDomId();

         if (html === null)
         {
            html = "";
            if (c.text)
            {
               html += '<label for="' + id + '">' + (c.noEscape ? c.text : $html(c.text)) + '</label><br/>';
            }
            if (c.input == "textarea")
            {
               html += '<textarea id="' + id + '" tabindex="0">' + c.value + '</textarea>';
            }
            else if (c.input == "text")
            {
               html += '<input id="' + id + '" tabindex="0" type="text" value="' + c.value + '"/>';
            }
         }
         prompt.setBody(html);

         // Show the icon if it exists
         if (c.icon)
         {
            prompt.cfg.setProperty("icon", c.icon);
         }

         // Add the buttons to the dialog
         if (c.buttons)
         {
            if (c.okButtonText)
            {
               // Override OK button label
               c.buttons[0].text = c.okButtonText;
            }

            // Default handler if no custom button passed-in
            if (typeof config.buttons == "undefined" || typeof config.buttons[0] == "undefined")
            {
               // OK button click handler
               c.buttons[0].handler = {
                  fn: function(event, obj)
                  {
                     // Grab the input, destroy the pop-up, then callback with the value
                     var value = null;
                     if (YUIDom.get(obj.id))
                     {
                        var inputEl = YUIDom.get(obj.id);
                        value = YAHOO.lang.trim(inputEl.value || inputEl.text);
                     }
                     this.destroy();
                     if (obj.callback.fn)
                     {
                        obj.callback.fn.call(obj.callback.scope || window, value, obj.callback.obj);
                     }
                  },
                  obj:
                  {
                     id: id,
                     callback: c.callback
                  }
               };
            }
            prompt.cfg.queueProperty("buttons", c.buttons);
         }

         // Add the dialog to the dom, center it and show it (unless flagged not to).
         prompt.render(document.body);

         // Make sure ok button only is enabled  if textfield contains content
         if (c.html === null && prompt.getButtons().length > 0)
         {
            // Make sure button only is disabled if textinput has a proper value
            var okButton = prompt.getButtons()[0];
            YAHOO.util.Event.addListener(id, "keyup", function(event, okButton)
            {
               okButton.set("disabled", YAHOO.lang.trim(this.value || this.text || "").length == 0);
            }, okButton);
            okButton.set("disabled", YAHOO.lang.trim(c.value).length == 0)
         }

         // Center and display
         prompt.center();
         if (c.initialShow)
         {
            prompt.show();
         }

         // If a default value was given, set the selectionStart and selectionEnd properties
         if (c.html === null && c.value !== "")
         {
            YUIDom.get(id).selectionStart = 0;
            YUIDom.get(id).selectionEnd = c.value.length;
         }

         // Register the ESC key to close the panel
         var escapeListener = new YAHOO.util.KeyListener(document,
         {
            keys: YAHOO.util.KeyListener.KEY.ESCAPE
         },
         {
            fn: function(id, keyEvent)
            {
               this.destroy();
            },
            scope: prompt,
            correctScope: true
         });
         escapeListener.enable();

         if (YUIDom.get(id))
         {
            YUIDom.get(id).focus();
         }

         return prompt;
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
            panel,
            calendar;

         var asOfDate = Alfresco.util.fromISO8601(properties.rma_recordSearchDispositionActionAsOf.iso8601),

         panel = this._getRmUserInput(
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
            asOfDate = Alfresco.util.fromISO8601(properties.rma_reviewAsOf.iso8601);
         }

         var calendarId = Alfresco.util.generateDomId(),
            panel,
            calendar;

         panel = this._getRmUserInput(
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
       * Called from the hidden iframe if the Export action fails.
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
       * File report action.
       *
       * @method onActionFileReport
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       * @param owner {HTMLElement} The action html element
       * @param type {string} The report type
       */
      onActionFileReport: function RDLA_onActionFileReport(assets, owner, type)
      {
         if (!this.modules.fileReport)
         {
            this.modules.fileReport = new Alfresco.rm.module.FileReport(this.id + "-fileReport");
         }

         var selectedNodePath = "";
         if (this.modules.fileReport.selectedNode)
         {
            selectedNodePath = this.modules.fileReport.selectedNode.data.path;
         }

         this.modules.fileReport.setOptions(
         {
            siteId: this.options.siteId,
            containerId: this.options.containerId,
            path: selectedNodePath,
            assets: assets,
            owner: owner,
            type: type
         }).showDialog();

         var me = this;
         this.modules.fileReport.onOK = function RDLA_onActionFileReport_onOK()
         {
            var destination;

            if (Dom.get(this.id + "-unfiled-records").checked == false)
            {
               destination = this.selectedNode.data.nodeRef;
            }
            else if (me.actionsView === "details")
            {
               destination = me.recordData.node.rmNode.unfiledRecordContainer;
            }
            else
            {
               destination = me.doclistMetadata.parent.rmNode.unfiledRecordContainer;
            }

            me.onActionRecordsManagementRepoAction(this.options.assets, this.options.owner,
            {
               "reportType": this.options.type,
               "destination": destination
            });
         };
      },

      /**
       * Success callback for file report.
       *
       * @method fileReportSuccess
       * @param data {object} Object literal containing ajax request and response
       * @param obj {object} Caller-supplied object
       *    <pre>
       *       obj.displayName {string} Filename or number of files submitted to the action.
       *    </pre>
       */
      fileReportSuccess: function RDLA_fileReportSuccess(data, obj)
      {
         // Hide the dialog
         this.modules.fileReport.widgets.dialog.hide();

         // Record name
         var recordName = data.json.results[data.config.dataObj.nodeRef];

         // Display success message
         Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("message.file-success", recordName)
         });
      },

      /**
       * Failure callback for file report.
       *
       * @method fileReportFailure
       * @param data {object} Object literal containing ajax request and response
       * @param obj {object} Caller-supplied object
       *    <pre>
       *       obj.displayName {string} Filename or number of files submitted to the action.
       *    </pre>
       */
      fileReportFailure: function RDLA_fileReportFailure(data, obj)
      {
         // Display error
         var text = this.msg("message.file-failure");
         if(data.json && data.json.message)
         {
            text = data.json.message;
         }
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: Alfresco.util.message("message.failure"),
            text: text
         });

         // Enable dialog buttons again
         this.modules.fileReport.widgets.okButton.set("disabled", false);
         this.modules.fileReport.widgets.cancelButton.set("disabled", false);
      },

      /**
       * File transport report action.
       *
       * @method onActionFileTransferReport
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       * @param owner {HTMLElement} The action html element
       */
      onActionFileTransferReport: function RDLA_onActionFileTransferReport(assets, owner)
      {
         this.onActionFileReport(assets, owner, "rmr:transferReport");
      },

      /**
       * File destruction report action.
       *
       * @method onActionFileDestructionReport
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       * @param owner {HTMLElement} The action html element
       */
      onActionFileDestructionReport: function RDLA_onActionFileDestructionReport(assets, owner)
      {
         this.onActionFileReport(assets, owner, "rmr:destructionReport");
      },

      /**
       * File hold report action.
       *
       * @method onActionFileHoldReport
       * @param assets {object} Object literal representing one or more hold(s) to be actioned
       * @param owner {HTMLElement} The action html element
       */
      onActionFileHoldReport: function RDLA_onActionFileHoldReport(assets, owner)
      {
         this.onActionFileReport(assets, owner, "rmr:holdReport");
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
            return window.open(Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + '/rm-audit?nodeName='+ encodeURIComponent(assets.displayName) + '&nodeRef=' + assets.nodeRef.replace(':/',''), 'Audit_Log', 'resizable=yes,location=no,menubar=no,scrollbars=yes,status=yes,width=700,height=500');
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
       * Copy/Move/Link/File To implementation.
       *
       * @method _copyMoveLinkFileTo
       * @param mode {String} Operation mode: copy|move|link|file
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       * @private
       */
      _copyMoveLinkFileTo: function RDLA__copyMoveLinkFileTo(mode, assets)
      {
         // Check mode is an allowed one
         if (!mode in
            {
               copy: true,
               move: true,
               link: true,
               file: true
            })
         {
            throw new Error("'" + mode + "' is not a valid Copy/Move/Link/File to mode.");
         }

         if (!this.modules.copyMoveLinkFileTo)
         {
            this.modules.copyMoveLinkFileTo = new Alfresco.rm.module.CopyMoveLinkFileTo(this.id + "-copyMoveLinkFileTo");
         }

         this.modules.copyMoveLinkFileTo.setOptions(
         {
            mode: mode,
            siteId: this.options.siteId,
            containerId: this.options.containerId,
            path: this.currentPath,
            files: assets,
            unfiled: false
         }).showDialog();
      },

     /**
      * Copy/Move/Link/File To implementation for unfiled.
      *
      * @method _copyMoveLinkUnfi,edFileTo
      * @param mode {String} Operation mode: copy|move|link|file
      * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
      * @private
      */

     _copyMoveLinkFileToUnfiled: function RDLA__copyMoveLinkFileToUnfiled(mode, assets)
     {
        // Check mode is an allowed one
        if (!mode in
           {
              copy: true,
              move: true,
              link: true,
              file: true
           })
        {
           throw new Error("'" + mode + "' is not a valid Copy/Move/Link/File to mode.");
        }

        if (!this.modules.copyMoveLinkFileTo)
        {
           this.modules.copyMoveLinkFileTo = new Alfresco.rm.module.CopyMoveLinkFileTo(this.id + "-copyMoveLinkFileTo");
        }

        this.modules.copyMoveLinkFileTo.setOptions(
        {
           mode: mode,
           siteId: this.options.siteId,
           containerId: this.options.containerId,
           path: "/Unfiled Records",
           files: assets,
           unfiled: true
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
       * @param actionParams {object} Optional object literal to pass parameters to the action
       */
      onActionRecordsManagementRepoAction: function RDLA_onActionRecordsManagementRepoAction(record, owner, actionParams)
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
         this._rmAction(params.message, record, params.action, actionParams, config);
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
       * Cut Off record
       *
       * @method onActionCutoffRecord
       * @param assets {object} Object literal representing one or more record(s) to be actioned
       */
      onActionCutoffRecord: function DLTB_onActionCutoffRecord(assets)
      {
         this.onActionCutoff(assets);
      },

      /**
       * Cut Off record folder
       *
       * @method onActionCutoffRecordFolder
       * @param assets {object} Object literal representing one or more record folder(s) to be actioned
       */
      onActionCutoffRecordFolder: function DLTB_onActionCutoffRecordFolder(assets)
      {
         this.onActionCutoff(assets);
      },

      /**
       * Cut Off record category
       *
       * @method onActionCutoffRecordCategory
       * @param assets {object} Object literal representing one or more record category (categories) to be actioned
       */
      onActionCutoffRecordCategory: function DLTB_onActionCutoffRecordCategory(assets)
      {
         this.onActionCutoff(assets);
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
       * Create Disposition.
       *
       * @method onActionCreateDisposition
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionCreateDisposition: function DLTB_onActionCreateDisposition(assets)
      {
         this._rmAction("message.create-disposition-schedule", assets, "createDispositionSchedule");
      },

      /**
       * Reject action for an unfiled record
       *
       * @method onActionReject
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionReject: function DLTB_onActionReject(assets)
      {
         Alfresco.util.PopupManager.getUserInput(
         {
            title: this.msg("message.reject.title"),
            text: this.msg("message.reject.reason"),
            okButtonText: this.msg("button.reject.record"),
            callback:
            {
               fn: function RDLA_onActionReject_callback(value)
               {
                  this._rmAction("message.reject", assets, "reject",
                  {
                     "reason": value
                  },
                  {
                     success:
                     {
                        callback:
                        {
                           fn: function()
                           {
                              Alfresco.util.PopupManager.displayMessage(
                              {
                                 text: this.msg("message.reject.success", $html(assets.displayName))
                              });

                              if (this.actionsView === "details")
                              {
                                 window.location = Alfresco.util.siteURL("documentlibrary#filter=unfiledRecords")
                              }
                              else
                              {
                                 YAHOO.Bubbling.fire("metadataRefresh");
                              }
                           },
                           scope: this
                        }
                     }
                  });
               },
               scope: this
            }
         });
      },

      /**
       * Request info action start a worklfow for an unfiled record
       *
       * @method onActionRequestInfo
       * @param assets {object} Object literal representing a record to be actioned
       */
      onActionRequestInfo: function DLTB_onActionRequestInfo(assets)
      {
         // Intercept before dialog show and change the button label
         var doBeforeDialogShow = function DLTB_requestInfo_doBeforeDialogShow(p_form, p_dialog)
         {
            p_dialog.widgets.okButton.set("label", this.msg("button.request-info"));
         };

         var executed = false;
         YAHOO.Bubbling.on("objectFinderReady", function DLTB_onActionRequestInfo_onObjectFinderReady(layer, args)
         {
            var objectFinder = args[1].eventGroup;
            if (objectFinder.options.field == "assoc_packageItems" && objectFinder.eventGroup.indexOf(this.id) == 0 && executed == false)
            {
               executed = true;
               objectFinder.selectItems(assets.node.nodeRef);
            }
         }, this);

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
            },
            onSuccess:
            {
               fn: function DLTB__newContainer_success(response)
               {
                  YAHOO.Bubbling.fire("metadataRefresh");
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.request-info-success")
                  });
               },
               scope: this
            },
            onFailure:
            {
               fn: function DLTB__newContainer_failure(response)
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.request-info-failure")
                  });
               },
               scope: this
            }
         }).show();
      },

      /**
       * Add a record/folder to the hold(s)
       *
       * @method onActionAddToHold
       * @param assets {object} Object literal representing one or more record(s) to be actioned
       */
      onActionAddToHold: function RDLA_onActionAddToHold(assets)
      {
         if (!this.modules.addToHold)
         {
            this.modules.addToHold = new Alfresco.rm.module.AddToHold(this.id + "-listofholds");
         }
         var itemNodeRef;
         if (YAHOO.lang.isArray(assets))
         {
            itemNodeRef = [];
            for (var i = 0, l = assets.length; i < l; i++)
            {
               itemNodeRef.push(assets[i].nodeRef);
            }
         }
         else
         {
            itemNodeRef = assets.nodeRef;
         }
         this.modules.addToHold.setOptions({
            itemNodeRef: itemNodeRef
         }).show();
      },

      /**
       * Add a record folder to the hold(s)
       *
       * @method onActionAddToHoldRecordFolder
       * @param assets {object} Object literal representing one or more record(s) to be actioned
       */
      onActionAddToHoldRecordFolder: function RDLA_onActionAddToHoldRecordFolder(assets)
      {
         this.onActionAddToHold(assets);
      },

      /**
       * Add a record to the hold(s)
       *
       * @method onActionAddToHoldRecord
       * @param assets {object} Object literal representing one or more record(s) to be actioned
       */
      onActionAddToHoldRecord: function RDLA_onActionAddToHoldRecord(assets)
      {
         this.onActionAddToHold(assets);
      },

      /**
       * Remove a record/folder from the hold(s)
       *
       * @method onActionRemoveFromHold
       * @param assets {object} Object literal representing one or more record(s) to be actioned
       */
      onActionRemoveFromHold: function RDLA_onActionRemoveFromHold(assets)
      {
         if (!this.modules.removeFromHold)
         {
            this.modules.removeFromHold = new Alfresco.rm.module.RemoveFromHold(this.id + "-listofholds");
         }
         this.modules.removeFromHold.setOptions({
            itemNodeRef: assets.nodeRef
         }).show();
      },

      /**
       * Delete hold
       *
       * @method onHoldDelete
       * @param assets {object} Object literal representing one or more record(s) to be actioned
       * @param owner {HTMLElement} The action html element
       */
      onHoldDelete: function RDLA_onHoldDelete(assets, owner)
      {
         var me = this;
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("message.confirm.delete-hold.title"),
            text: this.msg("message.confirm.delete-hold", assets.displayName),
            buttons: [
            {
               text: this.msg("button.ok"),
               handler: function RDLA_onHoldDelete_confirm_ok()
               {
                  me._rmAction("message.delete-hold", assets, "deleteHold", null,
                  {
                     failure:
                     {
                        callback:
                        {
                           fn: function RDLA_onHoldDelete_failure(data)
                           {
                              var text = me.msg("message.delete-hold.failure");
                              if(data.json && data.json.message)
                              {
                                 text = data.json.message;
                              }
                              Alfresco.util.PopupManager.displayPrompt(
                              {
                                 title: Alfresco.util.message("message.failure"),
                                 text: text
                              });
                           },
                           scope: this
                        }
                     }
                  });
                  this.destroy();
               }
            },
            {
               text: this.msg("button.cancel"),
               handler: function RDLA_onHoldDelete_confirm_cancel()
               {
                  this.destroy();
               },
               isDefault: true
            }]
         });
      },

      /**
       * Helper method to publish aikau events
       *
       * @method _publishAikauEvent
       * @param assets {object} Object literal representing one or more record(s) to be actioned
       * @param owner {HTMLElement} The action html element
       * @param topic The aikau topic which will be published
       */
      _publishAikauEvent: function RDLA___publishAikauEvent(assets, owner, topic)
      {
         require(["rm/services/AlfRmActionBridge"], function(Bridge) {
            var bridge = new Bridge();
            bridge.alfPublish(topic, {
               "item": assets,
               "owner": owner
            });
         });
      },

      /**
       * Add relatinship action
       *
       * @method onAddRelationship
       * @param assets {object} Object literal representing one or more record(s) to be actioned
       * @param owner {HTMLElement} The action html element
       */
      onAddRelationship: function RDLA_onAddRelationship(assets, owner)
      {
         this._publishAikauEvent(assets, owner, "RM_RELATIONSHIP_ADD");
      }
   };
})();
