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
 * RM Rules Picker.
 *
 * Extends the rule picker module from core to hide some panels from rules picker dialog
 *
 * @namespace Alfresco.rm.module
 * @class Alfresco.rm.module.RulesPicker
 */
(function()
{
   /**
    * RM Rules Picker module constructor.
    *
    * @param containerId {string} A unique id for this component
    * @return {Alfresco.rm.module.RulesPicker} The new rm rules picker instance
    * @constructor
    */
   Alfresco.rm.module.RulesPicker = function RM_RulesPicker_constructor(containerId)
   {
      Alfresco.rm.module.RulesPicker.superclass.constructor.call(this, containerId);
      return this;
   };

   YAHOO.extend(Alfresco.rm.module.RulesPicker, Alfresco.module.RulesPicker,
   {
      /**
       * Internal show dialog function
       *
       * @method _showDialog
       * @override
       */
      _showDialog: function RM_RulesPicker__showDialog()
      {
         Dom.getPreviousSibling(this.id + "-modeGroup").setAttribute("style", "display:none;");
         Dom.getPreviousSibling(this.id + "-sitePicker").setAttribute("style", "display:none;");
         Dom.get(this.id + "-modeGroup").setAttribute("style", "display:none;");
         Dom.get(this.id + "-sitePicker").setAttribute("style", "display:none;");
         Dom.get(this.id + "-treeview").setAttribute("style", "width: 19.5em !important;");
         Dom.get(this.id + "-dialog").setAttribute("style", "width:31.5em; min-width:31.5em;");
         Dom.get(this.id + "-dialog").setAttribute("style", "visibility:inherit;");

         Alfresco.rm.module.RulesPicker.superclass._showDialog.call(this);
      }
   });
})();
