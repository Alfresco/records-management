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
