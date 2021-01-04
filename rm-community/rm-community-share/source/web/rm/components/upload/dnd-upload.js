/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2021 Alfresco Software Limited
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
 * RMDnDUpload component.
 *
 * @namespace Alfresco
 * @class Alfresco.rm.component.DNDUpload
 * @extends Alfresco.DNDUpload
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      KeyListener = YAHOO.util.KeyListener;

   /**
    * RMDnDUpload constructor.
    *
    * RMDnDUpload is considered a singleton so constructor should be treated as private,
    * please use Alfresco.getRecordsHtmlUploadInstance() instead.
    *
    * @param htmlId {String} The HTML id of the parent element
    * @return {Alfresco.rm.component.DNDUpload} The new RMDnDUpload instance
    * @constructor
    * @private
    */
   Alfresco.rm.component.DNDUpload = function(htmlId)
   {
      Alfresco.rm.component.DNDUpload.superclass.constructor.call(this, htmlId);

      this.name = "Alfresco.rm.component.DNDUpload";
      Alfresco.util.ComponentManager.reregister(this);

      return this;
   };

   YAHOO.extend(Alfresco.rm.component.DNDUpload, Alfresco.DNDUpload,
   {
      /**
       * Shows uploader in single import mode.
       *
       * @property MODE_SINGLE_IMPORT
       * @static
       * @type int
       */
      MODE_SINGLE_IMPORT: 4,

      /**
       * Overrides the _applyConfig from the base class to adjust the gui.
       *
       * @method _applyConfig
       * @override
       */
      _applyConfig: function RMDnDUpload__applyConfig()
      {
         // Call super class that does the main part of the config attributes
         Alfresco.rm.component.DNDUpload.superclass._applyConfig.call(this);

         // Change the panel header text and the button label
         if (this.showConfig.mode === this.MODE_SINGLE_IMPORT)
         {
            this.titleText.innerHTML = this.msg("header.import");
            this.widgets.fileSelectionOverlayButton.set("label", this.msg("button.selectImportFile"));
         }
         else
         {
            this.titleText.innerHTML = this.msg("header.multiUpload");
            this.widgets.fileSelectionOverlayButton.set("label", this.msg("button.selectFiles"));
         }
      }
   });
})();
