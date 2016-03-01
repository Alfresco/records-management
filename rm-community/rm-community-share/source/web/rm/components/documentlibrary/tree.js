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
