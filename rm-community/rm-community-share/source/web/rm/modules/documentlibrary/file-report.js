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
 * "File Report" module for Records Management.
 *
 * @namespace Alfresco.module
 * @class Alfresco.rm.module.FileReport
 */
(function()
{
   /**
   * YUI Library aliases
   */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Element = YAHOO.util.Element;

   /**
    * Alfresco Slingshot aliases
    */
    var $html = Alfresco.util.encodeHTML,
       $combine = Alfresco.util.combinePaths;

   Alfresco.rm.module.FileReport = function(htmlId)
   {
      Alfresco.module.DoclibSiteFolder.superclass.constructor.call(this, htmlId);

      // Re-register with our own name
      this.name = "Alfresco.rm.module.FileReport";
      Alfresco.util.ComponentManager.reregister(this);

      // Initialise prototype properties
      this.pathsToExpand = [];

      return this;
   };

   YAHOO.extend(Alfresco.rm.module.FileReport, Alfresco.module.DoclibSiteFolder,
   {
      /**
       * Object container for initialization options
       */
      options:
      {
         /**
          * Evaluate child folders flag - for tree control
          *
          * @property evaluateChildFolders
          * @type boolean
          * @default true
          */
         evaluateChildFolders: true
      },

      /**
       * Event callback when dialog template has been loaded
       *
       * @method onTemplateLoaded
       * @override
       * @param response {object} Server response from load template XHR request
       */
      onTemplateLoaded: function DLSF_onTemplateLoaded(response)
      {
         Alfresco.rm.module.FileReport.superclass.onTemplateLoaded.call(this, response);

         this.widgets.unfiledRecordsCheckbox = Dom.get(this.id + "-unfiled-records");

         Event.addListener(this.widgets.unfiledRecordsCheckbox, "click", this.onCheckChange, this, true);
         Event.addListener(this.widgets.dialog.close, "click", this.onCancel, this, true);
      },

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @override
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.rm.module.FileReport} returns 'this' for method chaining
       */
      setOptions: function RMCMFT_setOptions(obj)
      {
         return Alfresco.rm.module.FileReport.superclass.setOptions.call(this, YAHOO.lang.merge(
         {
            templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "rm/modules/documentlibrary/file-report",
            files: obj.assets // To make the DoclibSiteFolder component happy
         }, obj));
      },

      /**
       * Check box change event handler
       *
       * @method onCheckChange
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onCheckChange: function RMCMFT_onCheckChange(e, p_obj)
      {
         var treeView = Dom.get(this.id + "-treeview")
         if (this.widgets.unfiledRecordsCheckbox.checked)
         {
            Dom.removeClass(treeView, "file-report-treeview-enabled");
            Dom.addClass(treeView, "file-report-treeview-disabled");
         }
         else
         {
            Dom.removeClass(treeView, "file-report-treeview-disabled");
            Dom.addClass(treeView, "file-report-treeview-enabled");
         }
      },

      /**
       * Dialog Cancel button event handler
       *
       * @method onCancel
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onCancel: function RMCMFT_onCancel(e, p_obj)
      {
         this.widgets.unfiledRecordsCheckbox.checked = true;
         var treeView = Dom.get(this.id + "-treeview")
         Dom.removeClass(treeView, "file-report-treeview-enabled");
         Dom.addClass(treeView, "file-report-treeview-disabled");

         Alfresco.rm.module.FileReport.superclass.onCancel.call(this, e, p_obj);
      },

      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Internal show dialog function
       * @method _showDialog
       * @override
       */
      _showDialog: function RMCMFT__showDialog()
      {
         this.widgets.okButton.set("label", this.msg("button.file"));

         var dialog = Alfresco.rm.module.FileReport.superclass._showDialog.apply(this, arguments);

         this.widgets.treeview.subscribe("expand", function(node)
         {
            if (this.widgets.unfiledRecordsCheckbox.checked)
            {
               return false;
            }
         }, this, true);

         this.widgets.treeview.subscribe("collapse", function(node)
         {
            if (this.widgets.unfiledRecordsCheckbox.checked)
            {
               return false;
            }
         }, this, true);

         return dialog;
      },

      /**
       * Build URI parameter string for treenode JSON data webscript
       *
       * @method _buildTreeNodeUrl
       * @param path {string} Path to query
       */
       _buildTreeNodeUrl: function RMCMFT__buildTreeNodeUrl(path)
       {
          var uriTemplate = Alfresco.constants.PROXY_URI + "slingshot/doclib/rm/treenode/site/{site}/{container}{path}";
          uriTemplate += "?children=" + this.options.evaluateChildFolders;

          var url = YAHOO.lang.substitute(uriTemplate,
          {
             site: encodeURIComponent(this.options.siteId),
             container: encodeURIComponent(this.options.containerId),
             path: Alfresco.util.encodeURIPath(path)
          });

          return url;
       }
   });

   /* Dummy instance to load optional YUI components early */
   var dummyInstance = new Alfresco.rm.module.FileReport("null");
})();
