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
 * DocumentList TreeView component.
 * 
 * @namespace Alfresco
 * @class Alfresco.rm.component.DocListTree
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

   /**
    * Records DocumentList TreeView constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.rm.component.DocListTree} The new RecordsDocListTree instance
    * @constructor
    */
   Alfresco.rm.component.DocListTree = function DLT_constructor(htmlId)
   {
      return Alfresco.rm.component.DocListTree.superclass.constructor.call(this, htmlId);
   };
   
   YAHOO.extend(Alfresco.rm.component.DocListTree, Alfresco.DocListTree,
   {
      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Build URI parameter string for treenode JSON data webscript
       *
       * @method _buildTreeNodeUrl
       * @param path {string} Path to query
       */
       _buildTreeNodeUrl: function DLT__buildTreeNodeUrl(path)
       {
          var uriTemplate ="slingshot/doclib/rm/treenode/site/" + $combine(encodeURIComponent(this.options.siteId), encodeURIComponent(this.options.containerId), Alfresco.util.encodeURIPath(path));
          return  Alfresco.constants.PROXY_URI + uriTemplate + "?perms=false&children=" + this.options.evaluateChildFolders;
       }
   });
})();
