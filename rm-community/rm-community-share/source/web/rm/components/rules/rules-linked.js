
/**
 * RM RulesLinked template.
 *
 * @namespace Alfresco.rm.module
 * @class Alfresco.rm.module.RulesLinked
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var $siteURL = Alfresco.util.siteURL;

   /**
    * RM RulesLinked constructor.
    *
    * @param htmlId {string} A unique id for this component
    * @return {Alfresco.rm.module.RulesLinked} The new rm rules linked instance
    * @constructor
    */
   Alfresco.rm.module.RulesLinked = function RM_RulesLinked_constructor(htmlId)
   {
      Alfresco.rm.module.RulesLinked.superclass.constructor.call(this, htmlId);
      return this;
   };

   YAHOO.extend(Alfresco.rm.module.RulesLinked, Alfresco.RulesLinked,
   {
      /**
       * Called when user clicks on the change link from button.
       * Displays a rule folder dialog.
       *
       * @method onChangeLinkButtonClick
       * @param type
       * @param args
       */
      onChangeLinkButtonClick: function RulesLinked_onChangeLinkButtonClick(type, args)
      {
         if (!this.modules.rulesPicker)
         {
            this.modules.rulesPicker = new Alfresco.rm.module.RulesPicker(this.id + "-rulesPicker");
         }

         Alfresco.rm.module.RulesLinked.superclass.onChangeLinkButtonClick.call(this, type, args);
      },

      /**
       * Displays the corresponding details page for the current folder
       *
       * @method _navigateForward
       * @private
       */
      _navigateForward: function RM_RulesLinked__navigateForward()
      {
         /* Did we come from the document library? If so, then direct the user back there */
         if (document.referrer.match(/documentlibrary([?]|$)/) || document.referrer.match(/repository([?]|$)/))
         {
            // go back to the referrer page
            history.go(-1);
         }
         else
         {
            // go forward to the appropriate details page for the node
            window.location.href = $siteURL("rm-record-folder-details?nodeRef=" + this.options.nodeRef.toString());
         }
      }
   });
})();