
/**
 * Rm RuleEdit component.
 *
 * @namespace Alfresco
 * @class Alfresco.RuleEdit
 */
(function()
{
   /**
    * Alfresco Slingshot aliases
    */
   var $siteURL = Alfresco.util.siteURL;

   /**
    * RuleEdit constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RuleEdit} The new RuleEdit instance
    * @constructor
    */
   Alfresco.rm.RuleEdit = function RM_RuleEdit_constructor(htmlId)
   {
      Alfresco.rm.RuleEdit.superclass.constructor.call(this, htmlId);
      return this;
   };

   YAHOO.extend(Alfresco.rm.RuleEdit, Alfresco.RuleEdit,
   {
      /**
       * Navigate to the main folder rules page
       *
       * @method _navigateToFoldersPage
       * @private
       */
      _navigateToFoldersPage: function RE__navigateToFoldersPage()
      {
         var unfiled = Alfresco.util.getQueryStringParameter("unfiled");
         if(unfiled != "true")
         {
            unfiled = "false";
         }
         window.location.href = $siteURL("folder-rules?nodeRef={nodeRef}&unfiled=" + unfiled,
         {
            nodeRef: Alfresco.util.NodeRef(this.options.nodeRef).toString()
         });
      }
   });
})();
