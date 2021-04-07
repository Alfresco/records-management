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
 * DocumentList Toolbar component.
 *
 * @namespace Alfresco
 * @class Alfresco.rm.component.DocListToolbar
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $siteURL = Alfresco.util.siteURL,
      $html = Alfresco.util.encodeHTML,
      $createYUIButton = Alfresco.util.createYUIButton,
      $popupManager = Alfresco.util.PopupManager;

   /**
    * RecordsDocListToolbar constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.rm.component.DocListToolbar} The new RecordsDocListToolbar instance
    * @constructor
    */
   Alfresco.rm.component.DocListToolbar = function(htmlId, registerListeners)
   {
      return Alfresco.rm.component.DocListToolbar.superclass.constructor.call(this, htmlId, registerListeners);
   };

   /**
    * Extend Alfresco.DocListToolbar
    */
   YAHOO.extend(Alfresco.rm.component.DocListToolbar, Alfresco.DocListToolbar);

   /**
    * Augment prototype with RecordsActions module, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentProto(Alfresco.rm.component.DocListToolbar, Alfresco.rm.doclib.Actions, true);

   /**
    * Augment prototype with main class implementation, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentObject(Alfresco.rm.component.DocListToolbar.prototype,
   {
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DLTB_onReady()
      {
         // New Record Category button: user needs "newCategory" access
         this.widgets.newCategory = $createYUIButton(this, "newCategory-button", this.onNewCategory,
         {
            disabled: true,
            value: "newCategory"
         });

         // New Record Folder button: user needs "newFolder" access
         this.widgets.newFolder = $createYUIButton(this, "newFolder-button", this.onNewFolder,
         {
            disabled: true,
            value: "newFolder"
         });

         // New Unfiled Records Folder button: user needs "newFolder" access
         this.widgets.newUnfiledRecordsFolder = $createYUIButton(this, "newUnfiledRecordsFolder-button", this.onNewUnfiledRecordsFolder,
         {
            disabled: true,
            value: "newUnfiledRecordsFolder"
         });

         // New Hold button: user needs "newHold" access
         this.widgets.newHold = $createYUIButton(this, "newHold-button", this.onNewHold,
         {
            disabled: true,
            value: "newHold"
         });

         // File Upload button: user needs "file" access
         this.widgets.fileUpload = $createYUIButton(this, "fileUpload-button", this.onFileUpload,
         {
            disabled: true,
            value: "file"
         });

         // Declare record button: user needs "file" access
         this.widgets.declareRecord = $createYUIButton(this, "declareRecord-button", this.onFileUpload,
         {
            disabled: true,
            value: "file"
         });

         // Import button: user needs "import" access
         this.widgets.importButton = $createYUIButton(this, "import-button", this.onImport,
         {
            disabled: true,
            value: "import"
         });

         // RM-318 - removing Report button temporarily
         /*this.widgets.reportButton = $createYUIButton(this, "report-button", this.onPrintReport,
         {
            disabled: true
         });*/

         // Export All button: user needs "export" access
         this.widgets.exportAllButton = $createYUIButton(this, "exportAll-button", this.onExportAll,
         {
            disabled: true,
            value: "export"
         });

         // Manage Rules button:
         this.widgets.manageRules = $createYUIButton(this, "manageRules-button", this.onManageRules,
         {
            disabled: true,
            value: "manageRules"
         });

         // Manage permissions button: user needs "file" permissions and the capability to modify permissions
         this.widgets.managePermissionsButton = $createYUIButton(this, "managePermissions-button", this.onManagePermissions,
         {
            disabled: true,
            value: "managePermissions"
         });

         // Manage Rules button:
         this.widgets.unfiledManageRules = $createYUIButton(this, "unfiledManageRules-button", this.onManageRules,
         {
            disabled: true,
            value: "manageRules"
         });

         // Manage permissions button for unfiled records toolbar: user needs "file" permissions and the capability to modify permissions
         this.widgets.unfiledManagePermissionsButton = $createYUIButton(this, "unfiledManagePermissions-button", this.onManagePermissions,
         {
            disabled: true,
            value: "managePermissions"
         });

         // Manage permissions button for holds toolbar: user needs "file" permissions and the capability to modify permissions
         this.widgets.holdPermissionsButton = $createYUIButton(this, "holdPermissions-button", this.onManagePermissions,
         {
            disabled: true,
            value: "managePermissions"
         });

         // Manage permissions button for transfers toolbar: user needs "file" permissions and the capability to modify permissions
         this.widgets.transferPermissionsButton = $createYUIButton(this, "transferPermissions-button", this.onManagePermissions,
         {
            disabled: true,
            value: "managePermissions"
         });

         // Selected Items menu button
         this.widgets.selectedItems = $createYUIButton(this, "selectedItems-button", this.onSelectedItems,
         {
            type: "menu",
            menu: "selectedItems-menu",
            lazyloadmenu: false,
            disabled: true
         });

         // Hide/Show NavBar button
         this.widgets.hideNavBar = $createYUIButton(this, "hideNavBar-button", this.onHideNavBar,
         {
            type: "checkbox",
            checked: this.options.hideNavBar
         });
         if (this.widgets.hideNavBar !== null)
         {
            this.widgets.hideNavBar.set("title", this.msg(this.options.hideNavBar ? "button.navbar.show" : "button.navbar.hide"));
            Dom.setStyle(this.id + "-navBar", "display", this.options.hideNavBar ? "none" : "block");
         }

         // Folder Up Navigation button
         this.widgets.folderUp =  Alfresco.util.createYUIButton(this, "folderUp-button", this.onFolderUp,
         {
            disabled: true
         });

         // DocLib Actions module
         this.modules.actions = new Alfresco.module.DoclibActions();

         // Reference to Document List component
         this.modules.docList = Alfresco.util.ComponentManager.findFirst("Alfresco.DocumentList");

         // Preferences service
         this.services.preferences = new Alfresco.service.Preferences();

         // Finally show the component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-body", "visibility", "visible");
      },

      /**
       * Document List Metadata event handler
       *
       * @method onDoclistMetadata
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDoclistMetadata: function DLTB_onDoclistMetadata(layer, args)
      {
         var obj = args[1];
         this.folderDetailsUrl = null;
         //doclistMetadata.parent.permissions.user.CreateChildren
         if (obj && obj.metadata && obj.metadata.parent)
         {
            this.doclistMetadata = Alfresco.util.deepCopy(obj.metadata);
            var p = obj.metadata.parent,
               userPerms;

            if (p && p.rmNode)
            {
               if (p.rmNode.uiType !== "fileplan")
               {
                  this.folderDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/rm-" + p.rmNode.uiType + "-details?nodeRef=" + p.nodeRef;
               }

               // Copy parent's viable actions into user permissions object for legacy support
               if (p.rmNode.actions)
               {
                  userPerms = p.permissions.user;
                  for (var l = 0, m = p.rmNode.actions.length; l < m; l++)
                  {
                     if (!userPerms.hasOwnProperty(p.rmNode.actions[l]))
                     {
                        userPerms[p.rmNode.actions[l]] = true;
                     }
                  }
               }
            }

            // We need to remove the "CreateChildren" permission so that the default "Upload Files" help templates are not rendered
            delete p.permissions.user.CreateChildren;
         }
      },

      /**
       * Selected Files Changed event handler.
       * Determines whether to enable or disable the multi-file action drop-down
       *
       * @method onSelectedFilesChanged
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onSelectedFilesChanged: function DLTB_onSelectedFilesChanged(layer, args)
      {
         /**
          * Legacy action support: loop through each record, adding the available actions as viable
          * "permissions" for the current user.
          */
         var files = this.modules.docList.getSelectedFiles(),
            file, rmNode, i, j, l, m,
            userPerms;

         if (files.length == 0)
         {
            this.widgets.selectedItems.set("disabled", true);
            return;
         }

         for (i = 0, j = files.length; i < j; i++)
         {
            file = files[i];
            rmNode = file.node.rmNode;
            if (rmNode && rmNode.actions)
            {
               userPerms = file.node.permissions.user;
               for (l = 0, m = rmNode.actions.length; l < m; l++)
               {
                  if (!userPerms.hasOwnProperty(rmNode.actions[l]))
                  {
                     userPerms[rmNode.actions[l]] = true;
                  }
               }
            }
            if (file.node.rmNode === undefined)
            {
               //Add in permission to non-rm nodes to bulk remove from hold if the node has the remove from hold single item action available
               for (count = 0, actionsCount = file.actions.length; count < actionsCount; count++)
               {
                  if (file.actions[count].id === 'rm-remove-from-hold')
                  {
                     file.node.permissions.user['removeFromHold'] = true;
                     break;
                  }
               }
            }
         }

         if (this.modules.docList)
         {
            var files = this.modules.docList.getSelectedFiles(), fileTypes = [], file,
               fileType, userAccess = {}, fileAccess, index,
               menuItems = this.widgets.selectedItems.getMenu().getItems(), menuItem,
               actionPermissions, typeGroups, typesSupported, disabled,
               commonAspects = [], allAspects = [], commonProperties = [], allProperties = [],
               i, ii, j, jj;

            var fnFileType = function fnFileType(file)
            {
               //Add check to make the selected items dropdown responsive to active content
               if (file.node.rmNode === undefined)
               {
                  return file.node.uiType;
               }
               else
               {
                  return file.node.rmNode.uiType;
               }
            };

            // first time around fill with permissions from first node
            // NOTE copy so we don't remove permissions from first node
            userAccess = Alfresco.util.deepCopy(files[0].node.permissions.user);
            // Check each file for user permissions
            for (i = 0, ii = files.length; i < ii; i++)
            {
               file = files[i];

               // Required user access level - logical AND of each file's permissions
               // every time after that remove permission if it isn't present on the current node.
               fileAccess = file.node.permissions.user;
               for (index in userAccess)
               {
                  if (!fileAccess.hasOwnProperty(index))
                  {
                      userAccess[index] = undefined;
                  }
                  else
                  {
                     userAccess[index] = userAccess[index] && fileAccess[index];
                  }
               }

               // Make a note of all selected file types Using a hybrid array/object so we can use both array.length and "x in object"
               fileType = fnFileType(file);
               if (!(fileType in fileTypes))
               {
                  fileTypes[fileType] = true;
                  fileTypes.push(fileType);
               }

               // Build a list of common aspects


               if (i === 0)
               {
                  // first time around fill with aspects/properties from first node -
                  // NOTE copy so we don't remove aspects/properties from file node.
                  commonAspects = Alfresco.util.deepCopy(file.node.aspects);
                  commonProperties = Alfresco.util.deepCopy(file.node.properties);
               } else
               {
                  // every time after that remove aspect if it isn't present on the current node.
                  for (j = 0, jj = commonAspects.length; j < jj; j++)
                  {
                     if (!Alfresco.util.arrayContains(file.node.aspects, commonAspects[j]))
                     {
                        Alfresco.util.arrayRemove(commonAspects, commonAspects[j])
                     }
                  }
               }

               // Build a list of all aspects
               for (j = 0, jj = file.node.aspects.length; j < jj; j++)
               {
                  if (!Alfresco.util.arrayContains(allAspects, file.node.aspects[j]))
                  {
                     allAspects.push(file.node.aspects[j])
                  }
               }

               // Build a list of all properties
               var properties = file.node.properties;
               for (var pIndex in properties)
               {
                  if (properties.hasOwnProperty(pIndex))
                  {
                     allProperties.push(pIndex);
                  }
               }
            }

            // Now go through the menu items, setting the disabled flag appropriately
            for (index in menuItems)
            {
               if (menuItems.hasOwnProperty(index))
               {
                  // Defaulting to enabled
                  menuItem = menuItems[index];
                  disabled = false;

                  if (menuItem.element.firstChild)
                  {
                     // Check permissions required - stored in "rel" attribute in the DOM
                     if (menuItem.element.firstChild.rel && menuItem.element.firstChild.rel !== "")
                     {
                        // Comma-separated indicates and "AND" match
                        actionPermissions = menuItem.element.firstChild.rel.split(",");
                        for (i = 0, ii = actionPermissions.length; i < ii; i++)
                        {
                           // Disable if the user doesn't have ALL the permissions
                           if (!userAccess[actionPermissions[i]])
                           {
                              disabled = true;
                              break;
                           }
                        }
                     }

                     // Check required aspects.
                     // Disable if any node DOES NOT have ALL required aspects
                     var hasAspects = Dom.getAttribute(menuItem.element.firstChild, "data-has-aspects");
                     if (hasAspects && hasAspects !== "")
                     {
                        hasAspects = hasAspects.split(",");
                        for (i = 0, ii = hasAspects.length; i < ii; i++)
                        {
                           if (!Alfresco.util.arrayContains(commonAspects, hasAspects[i]))
                           {
                              disabled = true;
                              break;
                           }
                        }
                     }

                     // Check forbidden aspects.
                     // Disable if any node DOES have ANY forbidden aspect
                     var notAspects = Dom.getAttribute(menuItem.element.firstChild, "data-not-aspects");
                     if (notAspects && notAspects !=="")
                     {
                        notAspects = notAspects.split(",");
                        for (i = 0, ii = notAspects.length; i < ii; i++)
                        {
                           if(Alfresco.util.arrayContains(allAspects, notAspects[i]))
                           {
                              disabled = true;
                              break;
                           }
                        }
                     }

                     // Check required properties.
                     // Disable if any node DOES NOT have ALL required properties
                     var hasProperties = Dom.getAttribute(menuItem.element.firstChild, "data-has-properties");
                     if (hasProperties && hasProperties !== "")
                     {
                        hasProperties = hasProperties.split(",");
                        for (i = 0, ii = hasProperties.length; i < ii; i++)
                        {
                           if (!Alfresco.util.arrayContains(commonProperties, hasProperties[i]))
                           {
                              disabled = true;
                              break;
                           }
                        }
                     }

                     // Check forbidden properties.
                     // Disable if any node DOES have ANY forbidden properties
                     var notProperties = Dom.getAttribute(menuItem.element.firstChild, "data-not-properties");
                     if (notProperties && notProperties !=="")
                     {
                        notProperties = notProperties.split(",");
                        for (i = 0, ii = notProperties.length; i < ii; i++)
                        {
                           if(Alfresco.util.arrayContains(allProperties, notProperties[i]))
                           {
                              disabled = true;
                              break;
                           }
                        }
                     }

                     if (!disabled)
                     {
                        // Check filetypes supported
                        if (menuItem.element.firstChild.type && menuItem.element.firstChild.type !== "")
                        {
                           // Pipe-separation indicates grouping of allowed file types
                           typeGroups = menuItem.element.firstChild.type.split("|");

                           for (i = 0; i < typeGroups.length; i++) // Do not optimize - bounds updated within loop
                           {
                              typesSupported = Alfresco.util.arrayToObject(typeGroups[i].split(","));

                              for (j = 0, jj = fileTypes.length; j < jj; j++)
                              {
                                 if (!(fileTypes[j] in typesSupported))
                                 {
                                    typeGroups.splice(i, 1);
                                    --i;
                                    break;
                                 }
                              }
                           }
                           disabled = (typeGroups.length === 0);
                        }
                     }
                     menuItem.cfg.setProperty("disabled", disabled);
                  }
               }
            }
            this.widgets.selectedItems.set("disabled", (files.length === 0));
         }
      },

      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * New Category button click handler
       *
       * @method onNewCategory
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onNewCategory: function DLTB_onNewCategory(e, p_obj)
      {
         this._newContainer("rma:recordCategory");
      },

      /**
       * New Folder button click handler
       *
       * @method onNewFolder
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onNewFolder: function DLTB_onNewFolder(e, p_obj)
      {
         this._newContainer("rma:recordFolder");
      },

      /**
       * New Unfiled Records Folder button click handler
       *
       * @method onNewUnfiledRecordsFolder
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onNewUnfiledRecordsFolder: function DLTB_onNewUnfiledRecordsFolder(e, p_obj)
      {
         this._newContainer("rma:unfiledRecordFolder");
      },

      /**
       * New Hold button click handler
       *
       * @method onNewHold
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onNewHold: function DLTB_onNewHold(e, p_obj)
      {
         this._newContainer("rma:hold");
      },

      /**
       * New Container handler
       *
       * @method _newContainer
       * @protected
       * @param folderType {string} Folder type to create
       */
      _newContainer: function DLTB__newContainer(folderType)
      {
         var destination = this._getFolderDestination();

         // Intercept before dialog show
         var doBeforeDialogShow = function DLTB__newContainer_doBeforeDialogShow(p_form, p_dialog)
         {
            var label = "label.new-" + folderType.replace(":", "_");
            Dom.get(p_dialog.id + "-dialogTitle").innerHTML = this.msg(label + ".title");
            Dom.get(p_dialog.id + "-dialogHeader").innerHTML = this.msg(label + ".header");
         };

         var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true",
         {
            itemKind: "type",
            itemId: folderType,
            destination: destination,
            mode: "create",
            submitType: "json",
            formId: "doclib-common"
         });

         // Using Forms Service, so always create new instance
         var createFolder = new Alfresco.module.SimpleDialog(this.id + "-createFolder");

         createFolder.setOptions(
         {
            width: "33em",
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
                  var folderName = response.config.dataObj["prop_cm_name"];
                  YAHOO.Bubbling.fire("folderCreated",
                  {
                     name: folderName,
                     parentNodeRef: destination
                  });
                  this._displayMessageByType(folderType, "success", folderName);
               },
               scope: this
            },
            onFailure:
            {
               fn: function DLTB__newContainer_failure(response)
               {
                  var folderName = response.config.dataObj["prop_cm_name"];
                  this._displayMessageByType(folderType, "failure", folderName);
                  createFolder.widgets.cancelButton.set("disabled", false);
               },
               scope: this
            }
         }).show();
      },

      /**
       * Helper method to display the correct message for each type in the UI
       *
       * @method _displayMessageByType
       * @param type The type of the created element
       * @param callbackAction The callback action type which is either "success" or "failure"
       * @param name The name of the created element
       */
      _displayMessageByType: function DLTB_getMessageByType(type, callbackAction, name)
      {
         var message = null;
         switch (type)
         {
            case "rma:recordCategory":
               message = "message.new-category.";
               break;
            case "rma:recordFolder":
               message = "message.new-folder.";
               break;
            case "rma:unfiledRecordFolder":
               message = "message.new-unfiledRecordsFolder.";
               break;
            case "rma:hold":
               message = "message.new-hold.";
               break;
            default:
               throw this.msg("message.new-unknown.error", type);
         }

         $popupManager.displayMessage(
         {
            text: this.msg(message + callbackAction, name)
         });
      },

      /**
       * Helper method to find the destination for record folder creation.
       *
       * @method _getFolderDestination
       */
      _getFolderDestination: function DLTB__getFolderDestination()
      {
         var destination = this.modules.docList.doclistMetadata.parent.nodeRef;
         var filterParam = Alfresco.rm.getParamValueFromUrl("filter");
         if (filterParam)
         {
            var filter = filterParam.split("|");
            if (filter[0] !== 'path' && filter[1])
            {
               destination = decodeURIComponent(filter[1]);
            }
         }
         return destination;
      },

      /**
       * File Upload button click handler
       *
       * @method onFileUpload
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onFileUpload: function DLTB_onFileUpload(e, p_obj)
      {
         var me = this;

         $popupManager.displayPrompt(
         {
            title: this.msg("message.file.type.title"),
            text: this.msg("message.file.type"),
            buttons: [
            {
               text: this.msg("button.electronic"),
               handler: function DLTB_onFileUpload_electronic()
               {
                  this.destroy();
                  me.onElectronicRecord.call(me);
               },
               isDefault: true
            },
            {
               text: this.msg("button.non-electronic"),
               handler: function DLTB_onFileUpload_nonElectronic()
               {
                  this.destroy();
                  me.onNonElectronicDocument.call(me);
               }
            },
            {
               text: this.msg("button.cancel"),
               handler: function DLTB_onFileUpload_cancel()
               {
                  this.destroy();
               }
            }]
         });
      },

      /**
       * Electronic Record button click handler
       *
       * @method onElectronicRecord
       */
      onElectronicRecord: function DLTB_onElectronicRecord()
      {
         if (this.fileUpload === null)
         {
            this.fileUpload = Alfresco.getRecordsFileUploadInstance();
         }

         // Show uploader for multiple files
         this.fileUpload.show(
         {
            containerId: this.options.containerId,
            destination: this._getFolderDestination(),
            filter: [],
            mode: this.fileUpload.MODE_MULTI_UPLOAD,
            thumbnails: "doclib",
            onFileUploadComplete:
            {
               fn: this.onFileUploadComplete,
               scope: this
            }
         });
      },

      /**
       * Non-Electronic Record button click handler
       *
       * @method onNonElectronicDocument
       */
      onNonElectronicDocument: function DLTB_onNonElectronicDocument()
      {
         var destination = this._getFolderDestination(),
            label = "label.new-rma_nonElectronicDocument",
            msgTitle = this.msg(label + ".title"),
            msgHeader = this.msg(label + ".header");

         // Intercept before dialog show
         var doBeforeDialogShow = function DLTB_onNonElectronicDocument_doBeforeDialogShow(p_form, p_dialog)
         {
            Dom.get(p_dialog.id + "-dialogTitle").innerHTML = msgTitle;
            Dom.get(p_dialog.id + "-dialogHeader").innerHTML = msgHeader;
         };

         var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&showCancelButton=true",
         {
            itemKind: "type",
            itemId: "rma:nonElectronicDocument",
            destination: destination,
            mode: "create",
            submitType: "json"
         });

         // Using Forms Service, so always create new instance
         var createRecord = new Alfresco.module.SimpleDialog(this.id + "-createRecord");

         createRecord.setOptions(
         {
            width: "33em",
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
               fn: function DLTB_onNonElectronicDocument_success(response)
               {
                  var fileName = response.config.dataObj["prop_cm_name"];
                  YAHOO.Bubbling.fire("metadataRefresh",
                  {
                     highlightFile: fileName
                  });
                  $popupManager.displayMessage(
                  {
                     text: this.msg("message.new-record.success", fileName)
                  });
               },
               scope: this
            },
            onFailure:
            {
               fn: function DLTB_onNonElectronicDocument_failure(response)
               {
                  var fileName = response.config.dataObj["prop_cm_name"];
                  $popupManager.displayMessage(
                  {
                     text: this.msg("message.new-record.failure", fileName)
                  });
               },
               scope: this
            }
         }).show();
      },

      /**
       * Import button click handler
       *
       * @method onImport
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onImport: function DLTB_onImport(e, p_obj)
      {
         // Create Uploader (Importer) component if it doesn't exist
         if (this.fileUpload === null)
         {
            this.fileUpload = Alfresco.getRecordsFileUploadInstance();
         }

         // Show uploader for single import file
         this.fileUpload.show(
         {
            mode: this.fileUpload.MODE_SINGLE_IMPORT,
            importDestination: this.modules.docList.doclistMetadata.parent.nodeRef,
            destination: this.modules.docList.doclistMetadata.parent.nodeRef,
            flashUploadURL: "api/rma/admin/import",
            htmlUploadURL: "api/rma/admin/import.html",
            filter: [
            {
               description: this.msg("label.filter-description.acp"),
               extensions: "*.acp"
            },
            {
               description: this.msg("label.filter-description.zip"),
               extensions: "*.zip"
            }]
         });
      },

      /**
       * Print Report button click handler
       *
       * @method onPrintReport
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onPrintReport: function DLTB_onPrintReport(e, p_obj)
      {
         var url = Alfresco.constants.URL_PAGECONTEXT + 'rm-fileplan-report?nodeRef=' + this.modules.docList.doclistMetadata.parent.nodeRef;
         window.open(url, 'rm-fileplan-report', 'width=550,height=650,scrollbars=yes,resizable=yes,toolbar=no,menubar=no');
      },

      /**
       * Export All button click handler
       *
       * @method onExportAll
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onExportAll: function DLTB_onExportAll(e, p_obj)
      {
         // Url to webscript to get the nodeRefs for the top level series
         var url = Alfresco.constants.PROXY_URI + "slingshot/doclib/rm/treenode/site/";
         url += encodeURIComponent(this.options.siteId) + "/" + encodeURIComponent(this.options.containerId);
         url += "?perms=false&children=false";

         // Load all series so they (and all their child objects) can be exported
         Alfresco.util.Ajax.jsonGet(
         {
            url: url,
            successCallback:
            {
               fn: function(serverResponse)
               {
                  if (serverResponse.json && serverResponse.json.items && serverResponse.json.items.length > 0)
                  {
                     // Display the export dialog and do the export
                     this.onActionExport(serverResponse.json.items);
                  }
                  else
                  {
                     $popupManager.displayMessage(
                     {
                        text: this.msg("message.nothing-to-export")
                     });
                  }
               },
               scope: this
            },
            failureMessage: this.msg("message.load-top-level-assets.failure")
         });
      },

      /**
       * Manage rules button click handler
       *
       * @method onManageRules
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onManageRules: function DLTB_onManageRules(e, p_obj)
      {
         var parent = this.modules.docList.doclistMetadata.parent,
            nodeRef = parent.nodeRef,
            unfiled = (parent.type === "rma:unfiledRecordContainer" || parent.type === "rma:unfiledRecordFolder") ? true : false,
            page = "folder-rules?nodeRef=" + nodeRef + "&unfiled=" + unfiled;

         window.location.href = $siteURL(page);
      },

      /**
       * Manage permissions button click handler
       *
       * @method onManagePermissions
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onManagePermissions: function DLTB_onManagePermissions(e, p_obj)
      {
         var parent = this.modules.docList.doclistMetadata.parent,
            nodeRef = parent.nodeRef,
            itemName = encodeURIComponent(parent.properties["cm:name"]),
            nodeType = parent.type,
            filePlanId = new Alfresco.util.NodeRef(parent.rmNode.filePlan).id,
            page = "manage-permissions?nodeRef=" + nodeRef + "&itemName=" + itemName + "&nodeType=" + nodeType + "&filePlanId=" + filePlanId;

         window.location.href = $siteURL(page);
      },


      /**
       * Delete Multiple Records confirmation.
       *
       * @method _onActionDeleteConfirm
       * @param records {array} Array containing records to be deleted
       * @private
       */
      _onActionDeleteConfirm: function DLTB__onActionDeleteConfirm(records)
      {
         var multipleRecords = [], i, ii;
         for (i = 0, ii = records.length; i < ii; i++)
         {
            multipleRecords.push(records[i].jsNode.nodeRef.nodeRef);
         }

         // Success callback function
         var fnSuccess = function DLTB__oADC_success(data, records)
         {
            var result;
            var successFileCount = 0;
            var successFolderCount = 0;

            for (i = 0, ii = data.json.totalResults; i < ii; i++)
            {
               result = data.json.results[i];

               if (result.success)
               {
                  if (result.type === "folder")
                  {
                     successFolderCount++;
                  }
                  else
                  {
                     successFileCount++;
                  }

                  YAHOO.Bubbling.fire(result.type === "folder" ? "folderDeleted" : "fileDeleted",
                  {
                     multiple: true,
                     nodeRef: result.nodeRef
                  });
               }
            }
            // Did the operation succeed?
            if (!data.json.overallSuccess)
            {
               Alfresco.util.PopupManager.displayMessage(
               {
            	   text: this.msg("message.multiple-delete.failure", data.json.successCount, data.json.failureCount)
               });
               // not automatically fired
               YAHOO.Bubbling.fire("filesDeleted");
               return;
            }

            this.modules.docList.totalRecords -= data.json.totalResults;
            YAHOO.Bubbling.fire("filesDeleted");
            // Activities, in Site mode only
            var successCount = successFolderCount + successFileCount;
            if (Alfresco.util.isValueSet(this.options.siteId))
            {
               var activityData;

               if (successCount > 0)
               {
                  if (successCount < this.options.groupActivitiesAt)
                  {
                     // Below cutoff for grouping Activities into one
                     for (i = 0; i < successCount; i++)
                     {
                        activityData =
                        {
                           fileName: data.json.results[i].id,
                           nodeRef: data.json.results[i].nodeRef,
                           path: this.currentPath,
                           parentNodeRef : this.doclistMetadata.parent.nodeRef
                        };

                        if (data.json.results[i].type === "folder")
                        {
                           this.modules.actions.postActivity(this.options.siteId, "folder-deleted", "documentlibrary", activityData);
                        }
                        else
                        {
                           this.modules.actions.postActivity(this.options.siteId, "file-deleted", "documentlibrary", activityData);
                        }
                     }
                  }
                  else
                  {
                     if (successFileCount > 0)
                     {
                        // grouped into one message
                        activityData =
                        {
                           fileCount: successFileCount,
                           path: this.currentPath,
                           parentNodeRef : this.doclistMetadata.parent.nodeRef
                        };
                        this.modules.actions.postActivity(this.options.siteId, "files-deleted", "documentlibrary", activityData);
                     }
                     if (successFolderCount > 0)
                     {
                        // grouped into one message
                        activityData =
                        {
                           fileCount: successFolderCount,
                           path: this.currentPath,
                           parentNodeRef : this.doclistMetadata.parent.nodeRef
                        };
                        this.modules.actions.postActivity(this.options.siteId, "folders-deleted", "documentlibrary", activityData);
                     }
                  }
               }
            }

            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.multiple-delete.success", successCount)
            });
         };

         // Construct the data object for the genericAction call
         this.modules.actions.genericAction(
         {
            success:
            {
               callback:
               {
                  fn: fnSuccess,
                  scope: this,
                  obj: records
               }
            },
            failure:
            {
               message: this.msg("message.multiple-delete.failure")
            },
            webscript:
            {
               method: Alfresco.util.Ajax.DELETE,
               name: "files"
            },
            wait:
            {
               message: this.msg("message.multiple-delete.please-wait")
            },
            config:
            {
               requestContentType: Alfresco.util.Ajax.JSON,
               dataObj:
               {
                  nodeRefs: multipleRecords
               }
            }
         });
      }


   }, true);
})();
