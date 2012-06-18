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
    * RecordsDocListToolbar constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.rm.component.DocListToolbar} The new RecordsDocListToolbar instance
    * @constructor
    */
   Alfresco.rm.component.DocListToolbar = function(htmlId)
   {
      return Alfresco.rm.component.DocListToolbar.superclass.constructor.call(this, htmlId);
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
         this.widgets.newCategory = Alfresco.util.createYUIButton(this, "newCategory-button", this.onNewCategory,
         {
            disabled: true,
            value: "newCategory"
         });
         // New Record Folder button: user needs "newFolder" access
         this.widgets.newFolder = Alfresco.util.createYUIButton(this, "newFolder-button", this.onNewFolder,
         {
            disabled: true,
            value: "newFolder"
         });

         // File Upload button: user needs "file" access
         this.widgets.fileUpload = Alfresco.util.createYUIButton(this, "fileUpload-button", this.onFileUpload,
         {
            disabled: true,
            value: "file"
         });

         // Import button: user needs "import" access
         this.widgets.importButton = Alfresco.util.createYUIButton(this, "import-button", this.onImport,
         {
            disabled: true,
            value: "import"
         });

         
         // RM-318 - removing Report button temporarily
         /*this.widgets.reportButton = Alfresco.util.createYUIButton(this, "report-button", this.onPrintReport,
         {
            disabled: true
         });*/
       
         // Export All button: user needs "export" access
         this.widgets.exportAllButton = Alfresco.util.createYUIButton(this, "exportAll-button", this.onExportAll,
         {
            disabled: true,
            value: "export"
         });

         // Selected Items menu button
         this.widgets.selectedItems = Alfresco.util.createYUIButton(this, "selectedItems-button", this.onSelectedItems,
         {
            type: "menu", 
            menu: "selectedItems-menu",
            lazyloadmenu: false,
            disabled: true
         });

         // Hide/Show NavBar button
         this.widgets.hideNavBar = Alfresco.util.createYUIButton(this, "hideNavBar-button", this.onHideNavBar,
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

         // Transfers Folder Up Navigation button
         this.widgets.transfersFolderUp =  Alfresco.util.createYUIButton(this, "transfersFolderUp-button", this.onFilterFolderUp,
         {
            disabled: true
         });

         // Holds Folder Up Navigation button
         this.widgets.holdsFolderUp =  Alfresco.util.createYUIButton(this, "holdsFolderUp-button", this.onFilterFolderUp,
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

         Alfresco.util.Ajax.jsonGet(
         {
            url: Alfresco.constants.PROXY_URI + "api/rma/admin/rmroles?user=" + encodeURIComponent(Alfresco.constants.USERNAME),
            successCallback:
            {
               fn: function(response)
               {
                  if (response.json && response.json.data)
                  {                     
                     // Fire event to inform any listening components that the users rmroles are available
                     YAHOO.Bubbling.fire("userRMRoles",
                     {
                        roles: response.json.data
                     });
                  }                  
               },
               scope: this
            }
         });
      },

      /**
       * Filter Changed event handler
       *
       * @method onFilterChanged
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onFilterChanged: function DLTB_onFilterChanged(layer, args)
      {
         Alfresco.rm.component.DocListToolbar.superclass.onFilterChanged.apply(this, arguments);
         
         var upFolderEnabled = (this.currentFilter.filterId == "holds" && this.currentFilter.filterData !== "");
         this.widgets.holdsFolderUp.set("disabled", !upFolderEnabled);

         upFolderEnabled = (this.currentFilter.filterId == "transfers" && this.currentFilter.filterData !== "");
         this.widgets.transfersFolderUp.set("disabled", !upFolderEnabled);
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
         }

         Alfresco.rm.component.DocListToolbar.superclass.onSelectedFilesChanged.apply(this, arguments);
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
       * New Container handler
       *
       * @method _newContainer
       * @protected
       * @param folderType {string} Folder type to create
       */
      _newContainer: function DLTB__newContainer(folderType)
      {
         var destination = this.modules.docList.doclistMetadata.parent.nodeRef;

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
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.new-folder.success", folderName)
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
                     text: this.msg("message.new-folder.failure")
                  });
               },
               scope: this
            }
         }).show();
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
         
         Alfresco.util.PopupManager.displayPrompt(
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
            siteId: this.options.siteId,
            containerId: this.options.containerId,
            uploadDirectory: this.currentPath,
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
         var destination = this.modules.docList.doclistMetadata.parent.nodeRef,
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
                  Alfresco.util.PopupManager.displayMessage(
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
                  Alfresco.util.PopupManager.displayMessage(
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
                     Alfresco.util.PopupManager.displayMessage(
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
       * Transfers/Holds Folder Up Navigate button click handler
       *
       * @method onFilterFolderUp
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onFilterFolderUp: function DLTB_onFilterFolderUp(e, p_obj)
      {
         YAHOO.Bubbling.fire("changeFilter",
         {
            filterId: this.currentFilter.filterId,
            filterData: ""
         });
         Event.preventDefault(e);
      }
   }, true);
})();
