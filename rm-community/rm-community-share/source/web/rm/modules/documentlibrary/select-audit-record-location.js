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
 * Document Library Selector. Allows selection of document library (and folder) of a specified site
 *
 * @namespace Alfresco.module
 * @class Alfresco.rm.module.SelectAuditRecordLocation
 */
(function()
{
   Alfresco.rm.module.SelectAuditRecordLocation = function(htmlId)
   {
      Alfresco.rm.module.SelectAuditRecordLocation.superclass.constructor.call(this, htmlId);

      // Re-register with our own name
      this.name = "Alfresco.rm.module.SelectAuditRecordLocation";
      Alfresco.util.ComponentManager.reregister(this);

      return this;
   };

   YAHOO.extend(Alfresco.rm.module.SelectAuditRecordLocation, Alfresco.module.DoclibSiteFolder,
   {
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.rm.module.SelectAuditRecordLocation} returns 'this' for method chaining
       * @override
       */
      setOptions: function SARL_setOptions(obj)
      {
         return Alfresco.rm.module.SelectAuditRecordLocation.superclass.setOptions.call(this, YAHOO.lang.merge(
         {
            templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "rm/modules/documentlibrary/copy-move-link-file-to"
         }, obj));
      },


      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Dialog OK button event handler
       *
       * @method onOK
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       * @override
       */
      onOK: function SARL_onOK(e, p_obj)
      {
         var node = this.widgets.treeview.getNodeByProperty("path", this.currentPath);
         YAHOO.Bubbling.fire("AuditRecordLocationSelected",
         {
            nodeRef: node.data.nodeRef
         });
         this.widgets.dialog.hide();
      },

      /**
       * Build URI parameter string for treenode JSON data webscript
       *
       * @method _buildTreeNodeUrl
       * @param path {string} Path to query
       * @override
       */
       _buildTreeNodeUrl: function SARL__buildTreeNodeUrl(path)
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
   var dummyInstance = new Alfresco.rm.module.SelectAuditRecordLocation("null");
})();

