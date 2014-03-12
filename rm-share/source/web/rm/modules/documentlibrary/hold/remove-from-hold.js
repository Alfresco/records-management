/**
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
 * Dialog to remove a record or a folder from hold(s).
 *
 * @namespace Alfresco.rm.module
 * @class Alfresco.rm.module.RemoveFromHold
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var KeyListener = YAHOO.util.KeyListener;

   /**
    * RemoveFromHold constructor.
    *
    * @param htmlId {string} A unique id for this component
    * @return {Alfresco.rm.module.RemoveFromHold} The new RemoveFromHold instance
    * @constructor
    */
   Alfresco.rm.module.RemoveFromHold = function(htmlId)
   {
      Alfresco.rm.module.RemoveFromHold.superclass.constructor.call(this, "Alfresco.rm.module.RemoveFromHold", htmlId);
      return this;
   };

   YAHOO.extend(Alfresco.rm.module.RemoveFromHold, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       */
      options:
      {
         /**
          * Node reference of the item which will be removed from the hold(s)
          * @type string
          * @default null
          */
         itemNodeRef: null
      },

      /**
       * Selected holds.
       *
       * @property selectedNode
       * @type array
       * @default []
       */
      selectedHolds: [],

      /**
       * Shows the remove from hold dialog to the user.
       *
       * @method show
       */
      show: function RemoveFromHold_show()
      {
         if (this.widgets.dialog)
         {
            // Dialog is already in the DOM, so just show it
            this._showDialog();
         }
         else
         {
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "rm/modules/documentlibrary/hold/remove-from-hold",
               dataObj:
               {
                  htmlid: this.id
               },
               successCallback:
               {
                  fn: this.onTemplateLoaded,
                  scope: this
               },
               failureMessage: this.msg("failure.template.not.loaded"),
               execScripts: true
            });
         }
      },

      /**
       * Called when the RemoveFromHold html template has been returned from the server.
       * Creates the YUI gui objects such as buttons and a dialog and shows it.
       *
       * @method onTemplateLoaded
       * @param response {object} a Alfresco.util.Ajax.request response object
       */
      onTemplateLoaded: function RemoveFromHold_onTemplateLoaded(response)
      {
         // Inject the template from the XHR request into a new DIV element
         var containerDiv = document.createElement("div");
         containerDiv.innerHTML = response.serverResponse.responseText;

         // The dialog is created from the HTML returned in the XHR request, not the container
         var dialogDiv = Dom.getFirstChild(containerDiv);

         // Create the panel
         this.widgets.dialog = Alfresco.util.createYUIPanel(dialogDiv);

         // OK button
         this.widgets.okButton = Alfresco.util.createYUIButton(this, "ok", this.onOK);

         // Cancel button
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel", this.onCancel);

         // Show the dialog
         this._showDialog();
      },

      /**
       * Setup the data source for the data table.
       *
       * @method _setupListDataSource
       * @private
       */
      _setupDataSource: function RemoveFromHold__setupDataSource()
      {
         // DataSource definition
         var uriHolds = encodeURI(Alfresco.constants.PROXY_URI + "api/rma/holds?itemNodeRef=" + this.options.itemNodeRef);
         this.widgets.listDataSource = new YAHOO.util.DataSource(uriHolds,
         {
            responseType: YAHOO.util.DataSource.TYPE_JSON,
            connXhrMode: "queueRequests",
            responseSchema:
            {
               resultsList: "data.holds"
            }
         });
      },

      /**
       * Setup the data table.
       *
       * @method _setupListDataTable
       * @private
       */
      _setupDataTable: function RemoveFromHold__setupDataTable()
      {
         // Data table column definitions
         var columnDefinitions =
            [{
               key: "check", label: " ", sortable: false, formatter: "checkbox"
            },
            {
               key: "name", label: " ", sortable: false
            }];

         // ListDataTable definition
         this.widgets.listDataTable = new YAHOO.widget.DataTable(this.id + "-listofholds", columnDefinitions, this.widgets.listDataSource,
         {
            renderLoopSize: 32,
            scrollable: true,
            height: "300px",
            width: "200px",
            MSG_EMPTY: this.msg("message.empty.holds")
         });

         var me = this;
         this.widgets.listDataTable.on('checkboxClickEvent', function (oArgs)
         {
            var checkbox = oArgs.target,
               checked = checkbox.checked,
               record = this.getRecord(checkbox),
               nodeRef = record.getData("nodeRef");
            if (checked)
            {
               me.selectedHolds.push(nodeRef);
            }
            else
            {
               Alfresco.util.arrayRemove(me.selectedHolds, nodeRef);
            }
         });
      },

      /**
       * Dialog OK button event handler
       *
       * @method onOK
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onOK: function RemoveFromHold_onOK(e, p_obj)
      {
         if (this.selectedHolds.length > 0)
         {
            Alfresco.util.Ajax.request(
            {
               url: encodeURI(Alfresco.constants.PROXY_URI + "api/rma/holds"),
               method: Alfresco.util.Ajax.PUT,
               dataObj:
               {
                  "nodeRef": this.options.itemNodeRef,
                  "holds": this.selectedHolds
               },
               requestContentType: Alfresco.util.Ajax.JSON,
               successCallback:
               {
                  fn: function(response)
                  {
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("message.remove-success")
                     });
                     this._reset();
                  },
                  scope: this
               },
               failureCallback:
               {
                  fn: function(response)
                  {
                     var json = Alfresco.util.parseJSON(response.serverResponse.responseText),
                        failureMsg = response.serverResponse.responseText;
                     if (json != null && json.message != null)
                     {
                        failureMsg = json.message;
                     }

                     Alfresco.util.PopupManager.displayPrompt(
                     {
                        title: this.msg("message.failure"),
                        text: this.msg("message.remove-failure", failureMsg)
                     });
                     this._reset();
                  },
                  scope: this
               }
            });
         }
         else
         {
            this._reset()
         }
      },

      /**
       * Dialog Cancel button event handler
       *
       * @method onCancel
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onCancel: function RemoveFromHold_onCancel(e, p_obj)
      {
         this._reset();
      },

      /**
       * Disables the escape listener,
       * hides the dialog and
       * resets the array which keeps the selected holds.
       *
       * @method _reset
       * @private
       */
      _reset: function RemoveFromHold__reset()
      {
         this.widgets.escapeListener.disable();
         this.widgets.dialog.hide();
         this.selectedHolds = [];
      },

      /**
       * Prepares the gui and shows the dialog.
       *
       * @method _showDialog
       * @private
       */
      _showDialog: function RemoveFromHold__showDialog()
      {
         // Setup data table
         this._setupDataSource();
         this._setupDataTable();

         // Show the upload dialog
         this.widgets.dialog.show();

         if (!this.widgets.escapeListener)
         {
            // Register the ESC key to close the dialog
            this.widgets.escapeListener = new KeyListener(Dom.get(this.id + "-dialog"),
            {
               keys: KeyListener.KEY.ESCAPE
            },
            {
               fn: function(id, keyEvent)
               {
                  this.onCancel();
               },
               scope: this,
               correctScope: true
            });
            this.widgets.escapeListener.enable();
         }
      }
   });
})();