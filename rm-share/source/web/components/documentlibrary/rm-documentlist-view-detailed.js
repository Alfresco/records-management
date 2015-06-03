/**
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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

/**
 * RM DocumentListViewRenderer component.
 *
 * @namespace Alfresco.rm
 * @class Alfresco.rm.DocumentListViewRenderer
 * @extends Alfresco.DocumentListViewRenderer
 */
(function()
{
   /**
    * Alfresco.rm.DocumentListViewRenderer constructor.
    *
    * @return {Alfresco.rm.DocumentListViewRenderer} The new Alfresco.rm.DocumentListViewRenderer instance
    * @constructor
    */
   Alfresco.rm.DocumentListViewRenderer = function(name, parentDocumentList)
   {
      Alfresco.rm.DocumentListViewRenderer.superclass.constructor.call(this, name, parentDocumentList);
      return this;
   };

   /**
    * Extend from Alfresco.DocumentListViewRenderer
    */
   YAHOO.extend(Alfresco.rm.DocumentListViewRenderer, Alfresco.DocumentListViewRenderer,
   {
      renderCellDescription: function Alfresco_rm_DocumentListViewRenderer_renderCellDescription(scope, elCell, oRecord, oColumn, oData)
      {
         Alfresco.rm.DocumentListViewRenderer.superclass.renderCellDescription.call(this, scope, elCell, oRecord, oColumn, oData);
         Alfresco.rm.classifiedBanner(elCell, oRecord, this.parentDocumentList.msg);
      }
   });
})();