
/**
 * RM RulesNone component.
 *
 * @namespace Alfresco.rm
 * @class Alfresco.rm.RulesNone
 */
(function()
{
   /**
    * Alfresco.rm.RulesNone constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RulesNone} The new RM RulesNone instance
    * @constructor
    */
   Alfresco.rm.RulesNone = function(htmlId)
   {
      Alfresco.rm.RulesNone.superclass.constructor.call(this, htmlId);
      return this;
   };

   YAHOO.extend(Alfresco.rm.RulesNone, Alfresco.RulesNone,
   {
      /**
       * Called when user clicks on the link to rules set link.
       *
       * @method onLinkToRuleSetClick
       * @param event
       * @param obj
       */
      onLinkToRuleSetClick: function RM_RulesNone_onLinkToRuleSetClick(event, obj)
      {
         if (!this.modules.rulesPicker)
         {
            this.modules.rulesPicker = new Alfresco.rm.module.RulesPicker(this.id + "-rulesPicker");
         }

         Alfresco.rm.RulesNone.superclass.onLinkToRuleSetClick.call(this, event, obj);
      }
   });
})();
