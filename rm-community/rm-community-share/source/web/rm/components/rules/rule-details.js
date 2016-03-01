
/**
 * Rm RuleDetails component.
 *
 * @namespace Alfresco
 * @class Alfresco.RuleDetails
 */
(function()
{
   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $siteURL = Alfresco.util.siteURL;

   /**
    * RuleDetails constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RuleDetails} The new RuleDetails instance
    * @constructor
    */
   Alfresco.rm.RuleDetails = function RM_RuleDetails_constructor(htmlId)
   {
      Alfresco.rm.RuleDetails.superclass.constructor.call(this, htmlId);
      return this;
   };

   YAHOO.extend(Alfresco.rm.RuleDetails, Alfresco.RuleDetails,
   {
      /**
       * Fired when the user clicks the Edit button.
       * Takes the user back to the edit rule page.
       *
       * @method onEditButtonClick
       * @param event {object} a "click" event
       */
      onEditButtonClick: function RM_RuleDetails_onEditButtonClick(event)
      {
         // Disable buttons to avoid double submits or cancel during post
         this.widgets.editButton.set("disabled", true);

         // Send the user to edit rule page
         var unfiled = Alfresco.util.getQueryStringParameter("unfiled");
         if(unfiled != "true")
         {
            unfiled = "false";
         }
         window.location.href = $siteURL("rule-edit?nodeRef={nodeRef}&ruleId={ruleId}&unfiled=" + unfiled,
         {
            nodeRef: Alfresco.util.NodeRef(this.options.nodeRef).toString(),
            ruleId: this.ruleDetails.id.toString()
         });
      }
   });
})();
