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
            label: this.msg("dataSet.select.label") + " " + Alfresco.constants.MENU_ARROW_SYMBOL,
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
         if (menuItem)
         {
            if (menuItem.cfg.getProperty("disabled"))
            {
               this.widgets.dataSets.set("label", this.msg("dataSet.select.label"));
               this.widgets.dataSets.value = undefined;
               this.widgets.importButton.set("disabled", true);
            }
            else
            {
               this.widgets.dataSets.set("label", menuItem.cfg.getProperty("text"));
               this.widgets.dataSets.value = menuItem.value;
               this.widgets.importButton.set("disabled", false);
            }
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
               fn: function(response)
               {
                  waitMessage.destroy();

                  if (response.json.success)
                  {
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("dataSet.message.import-ok")
                     });

                     // Disable the menu item so it cannot be selected again
                     this.widgets.dataSets.get("selectedMenuItem").cfg.setProperty("disabled", true);
                     this.widgets.dataSets.set("label", this.msg("dataSet.select.label"));
                     this.widgets.dataSets.value = undefined;
                     // Enable the menu button
                     this.widgets.dataSets.set("disabled", false);
                  }
                  else
                  {
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: response.json.message
                     });

                     // Enable the menu button
                     this.widgets.dataSets.set("disabled", false);
                  }
               },
               scope: this
            },
            failureCallback:
            {
               fn: function(response)
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
