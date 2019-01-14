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
 * Dialog to remove a record or a folder from hold(s).
 *
 * @namespace Alfresco.rm.module
 * @class Alfresco.rm.module.RemoveFromHold
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var KeyListener = YAHOO.util.KeyListener;

   /**
    * RemoveFromHold constructor.
    *
    * @param htmlId {string} A unique id for this component
    * @return {Alfresco.rm.module.RemoveFromHold} The new RemoveFromHold instance
    * @constructor
    */
   Alfresco.rm.module.RemoveFromHold = function(htmlId)
   {
      Alfresco.rm.module.RemoveFromHold.superclass.constructor.call(this, "Alfresco.rm.module.RemoveFromHold", htmlId);

      // Re-register with our own name
      this.name = "Alfresco.rm.module.RemoveFromHold";
      Alfresco.util.ComponentManager.reregister(this);

      return this;
   };

   YAHOO.extend(Alfresco.rm.module.RemoveFromHold, Alfresco.rm.module.Hold,
   {
      /**
       * Indicates whether the holds should be retrieved which include the itemNodeRef
       *
       * @type boolean
       * @default true
       */
      includedInHold: true,

      /**
       * The URL for the dialog template
       *
       * @property templateUrl
       * @type string
       * @default Alfresco.constants.URL_SERVICECONTEXT + "rm/modules/documentlibrary/hold/remove-from-hold"
       */
      templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "rm/modules/documentlibrary/hold/remove-from-hold",

      /**
       * The ajax request method type when the user clicks on the OK button
       *
       * @property onOKAjaxRequestMethodType
       * @type string
       * @default Alfresco.util.Ajax.PUT
       */
      onOKAjaxRequestMethodType: Alfresco.util.Ajax.PUT
   });
})();
