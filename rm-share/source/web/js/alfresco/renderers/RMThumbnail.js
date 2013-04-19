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
        "alfresco/renderers/Thumbnail"],
        function(declare, Thumbnail) {

   return declare([Thumbnail], {

      /**
       * Returns a URL to the image to use when rendering a folder
       *
       * @method getFolderImage
       */
      getFolderImage: function alfresco_renderers_Thumbnail__getDefaultFolderImage() {
         return Alfresco.constants.URL_RESCONTEXT + "rm/components/documentlibrary/images/" + this.currentItem.node.rmNode.uiType + "-48.png";
      }
   });
});