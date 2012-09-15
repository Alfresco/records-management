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

Alfresco.rm = Alfresco.rm || {};
Alfresco.rm.dashlet = Alfresco.rm.dashlet || {};

/**
 * Dashboard DataSet component.
 * 
 * @namespace Alfresco
 * @class Alfresco.rm.dashlet.DataSet
 */
(function()
{
   /**
    * Dashboard DataSet constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.rm.dashlet.DataSet} The new component instance
    * @constructor
    */
   Alfresco.rm.dashlet.DataSet = function DataSet_constructor(htmlId)
   {
      return Alfresco.rm.dashlet.DataSet.superclass.constructor.call(this, "Alfresco.rm.dashlet.DataSet", htmlId);
   };

   YAHOO.extend(Alfresco.rm.dashlet.DataSet, Alfresco.component.Base,
   {
      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function DataSet_onReady()
      {
         this.widgets.dataSets = Alfresco.util.createYUIButton(this, "dataSets", this.onDataSetSelected,
         {
            type: "menu",
            menu: "dataSets-menu",
            title: this.msg("dataSet.select.title"),
            label: this.msg("dataSet.select.label"),
            lazyloadmenu: false
         });

         this.widgets.importButton = Alfresco.util.createYUIButton(this, "import-button", this.onImportClicked,
         {
            disabled: true
         });
      },

      /**
       * Data set selection handler.
       *
       * @method onDataSetSelected
       * @param type {string} Event type
       * @param args {object} Event arguments
       */
      onDataSetSelected: function DataSet_onDataSetSelected(type, args)
      {
         var menuItem = args[1];
         this.options.selectedItem = menuItem;
         if (menuItem)
         {
            this.widgets.dataSets.set("label", menuItem.cfg.getProperty("text"));
            this.widgets.dataSets.value = menuItem.value;
            this.widgets.importButton.set("disabled", false);
         }
      },

      /**
       * Import button click handler.
       *
       * @method onImportClicked
       * @param type {string} Event type
       * @param args {object} Event arguments
       */
      onImportClicked: function DataSet_onDataSetSelected(type, args)
      {
         // Disable the import button
         this.widgets.importButton.set("disabled", true);

         // Disable the menu button
         this.widgets.dataSets.set("disabled", true);

         // Display a message while importing the test data
         var waitMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("dataSet.message.importing"),
            spanClass: "wait",
            displayTime: 0
         });

         // Call web-tier to perform data set import
         Alfresco.util.Ajax.jsonPost(
         {
            url: Alfresco.constants.PROXY_URI + "api/rma/datasets/" + this.widgets.dataSets.value + "?site=" + Alfresco.constants.SITE,
            successCallback:
            {
               fn: function()
               {
                  waitMessage.destroy();
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("dataSet.message.import-ok")
                  });
                  // Enable the menu button
                  this.widgets.dataSets.set("disabled", false);

                  // Remove the loaded data set from the menu button
                  var menu = this.widgets.dataSets.getMenu();
                  menu.removeItem(this.options.selectedItem);
                  this.widgets.dataSets.set("label", this.msg("dataSet.select.label"));
                  delete this.widgets.dataSets.value;
                  if (menu.getItems() == 0)
                  {
                     menu.body = null;
                     menu.element.innerHTML = "";
                  }
               },
               scope: this
            },
            failureCallback:
            {
               fn: function()
               {
                  waitMessage.destroy();
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("dataSet.message.import-fail")
                  });
                  // Enable the menu button
                  this.widgets.dataSets.set("disabled", false);
               },
               scope: this
            }
         });
      }
   });
})();
