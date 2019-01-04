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
