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
