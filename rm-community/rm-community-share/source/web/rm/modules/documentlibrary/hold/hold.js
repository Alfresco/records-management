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
 * Base dialog class for hold actions.
 *
 * @namespace Alfresco.rm.module
 * @class Alfresco.rm.module.Hold
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var KeyListener = YAHOO.util.KeyListener;

   /**
    * Hold constructor.
    *
    * @param htmlId {string} A unique id for this component
    * @return {Alfresco.rm.module.Hold} The new Hold instance
    * @constructor
    */
   Alfresco.rm.module.Hold = function(htmlId)
   {
      Alfresco.rm.module.Hold.superclass.constructor.call(this, "Alfresco.rm.module.Hold", htmlId);
      return this;
   };

   YAHOO.extend(Alfresco.rm.module.Hold, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       */
      options:
      {
         /**
          * Node reference of the item which will be added/removed to/from the hold(s)
          *
          * @type string
          * @default null
          */
         itemNodeRef: null
      },

      /**
       * Shows the from hold dialog to the user.
       *
       * @method show
       */
      show: function Hold_show()
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
               url: this.templateUrl,
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
       * Called when the Hold html template has been returned from the server.
       * Creates the YUI gui objects such as buttons and a dialog and shows it.
       *
       * @method onTemplateLoaded
       * @param response {object} a Alfresco.util.Ajax.request response object
       */
      onTemplateLoaded: function Hold_onTemplateLoaded(response)
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
      _setupDataSource: function Hold__setupDataSource()
      {
         var itemNodeRef = "";
         if (!YAHOO.lang.isArray(this.options.itemNodeRef))
         {
            itemNodeRef = YAHOO.lang.substitute("itemNodeRef={itemNodeRef}&",
            {
               itemNodeRef: this.options.itemNodeRef
            });
         }

         var includedInHold = YAHOO.lang.substitute("includedInHold={includedInHold}",
         {
            includedInHold: (this.includedInHold).toString()
         });

         // DataSource definition
         var uriHolds = encodeURI(Alfresco.constants.PROXY_URI + "api/rma/holds?" + itemNodeRef + includedInHold + "&fileOnly=true");
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
      _setupDataTable: function Hold__setupDataTable()
      {
         // Data table column definitions
         var columnDefinitions =
            [{
               key: "check", label: "<input type='checkbox'>", sortable: false, formatter: "checkbox"
            },
            {
               key: "name", label: this.msg("name.column.header"), sortable: true, width: 450
            }];

         // ListDataTable definition
         this.widgets.listDataTable = new YAHOO.widget.DataTable(this.id + "-listofholds", columnDefinitions, this.widgets.listDataSource,
         {
            renderLoopSize: 32,
            scrollable: true,
            height: "200px",
            MSG_EMPTY: this.msg("message.empty.holds"),
            MSG_ERROR: this.msg("message.empty.holds")
         });

         this.widgets.listDataTable.on('theadCellClickEvent', Alfresco.rm.dataTableHeaderCheckboxClick);
         this.widgets.listDataTable.on('checkboxClickEvent', Alfresco.rm.dataTableCheckboxClick);
      },

      /**
       * Dialog OK button event handler
       *
       * @method onOK
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onOK: function Hold_onOK(e, p_obj)
      {
         var selectedHolds = Alfresco.rm.dataTableSelectedItems(this.widgets.listDataTable);
         if (selectedHolds.length > 0)
         {
            Alfresco.util.Ajax.request(
            {
               url: encodeURI(Alfresco.constants.PROXY_URI + "api/rma/holds"),
               method: this.onOKAjaxRequestMethodType,
               dataObj:
               {
                  "nodeRefs": !YAHOO.lang.isArray(this.options.itemNodeRef) ? [this.options.itemNodeRef] : this.options.itemNodeRef,
                  "holds": selectedHolds
               },
               requestContentType: Alfresco.util.Ajax.JSON,
               successCallback:
               {
                  fn: function(response)
                  {
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("message.hold-success")
                     });

                     YAHOO.Bubbling.fire("metadataRefresh");
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
                        text: failureMsg
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
      onCancel: function Hold_onCancel(e, p_obj)
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
      _reset: function Hold__reset()
      {
         this.widgets.escapeListener.disable();
         this.widgets.dialog.hide();
      },

      /**
       * Prepares the gui and shows the dialog.
       *
       * @method _showDialog
       * @private
       */
      _showDialog: function Hold__showDialog()
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
