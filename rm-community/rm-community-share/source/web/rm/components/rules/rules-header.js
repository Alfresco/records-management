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
 * RM RulesHeader template.
 *
 * Overrides the _displayDetails method so that the file plan label can be shown when creating a rule for the file plan
 *
 * @namespace Alfresco.rm
 * @class Alfresco.rm.RulesHeader
 */
(function()
{
   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
         $siteURL = Alfresco.util.siteURL;

   /**
    * RM RulesHeader constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.rm.RulesHeader} The new RM RulesHeader instance
    * @constructor
    */
   Alfresco.rm.RulesHeader = function RM_RulesHeader_constructor(htmlId)
   {
      Alfresco.rm.RulesHeader.superclass.constructor.call(this, htmlId);
      return this;
   };

   YAHOO.extend(Alfresco.rm.RulesHeader, Alfresco.RulesHeader,
   {
      /**
       * Overrides the _displayDetails method from the base class
       *
       * @method _displayDetails
       */
      _displayDetails: function RM_RulesHeader__displayDetails()
      {
         if (this.isReady && this.folderDetails)
         {
            Alfresco.rm.RulesHeader.superclass._displayDetails.call(this);
            if ($html(this.folderDetails.fileName) == "")
            {
               // Display file name
               this.widgets.titleEl.innerHTML = $html(this.msg("path.fileplan"));
            }
         }
      },

      /**
       * Overrides the _displayDetails method from the base class
       *
       * @method onNewRuleButtonClick
       * @param type
       * @param args
       */
      onNewRuleButtonClick: function RulesHeader_onNewRuleButtonClick(type, args)
      {
         var unfiled = Alfresco.util.getQueryStringParameter("unfiled");
         if(unfiled != "true")
         {
            unfiled = "false";
         }
         window.location.href = $siteURL("rule-edit?nodeRef={nodeRef}&unfiled=" + unfiled,
         {
            nodeRef: Alfresco.util.NodeRef(this.options.nodeRef).toString()
         });
      },
   });
})();
