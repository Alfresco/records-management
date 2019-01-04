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
