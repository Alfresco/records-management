/**
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
define(["dojo/_base/declare",
        "alfresco/renderers/Property"],
        function(declare, Property) {

   return declare([Property], {

      /**
       * An array of the i18n files to use with this widget.
       *
       * @property i18nRequirements {Array}
       */
      i18nRequirements: [{i18nFile: "./i18n/RMCategoryIdentifier.properties"}],

      /**
       * Set up the attributes to be used when rendering the template.
       *
       * @method postMixInProperties
       */
      postMixInProperties: function alfresco_renderers_RMCategoryIdentifier__postMixInProperties() {
         var rmNode = this.currentItem.node.rmNode;
         if (rmNode.uiType === "record-category")
         {
            this.renderedValue = this.message("details.category.identifier", {
               0: rmNode.properties["rma:identifier"]
            });
         }
      }
   });
});