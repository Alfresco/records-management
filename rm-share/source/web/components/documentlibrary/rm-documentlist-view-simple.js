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
 * RM DocumentListSimpleViewRenderer component.
 *
 * @namespace Alfresco.rm
 * @class Alfresco.rm.DocumentListSimpleViewRenderer
 * @extends Alfresco.DocumentListSimpleViewRenderer
 */
(function()
{
   /**
    * Alfresco.rm.DocumentListSimpleViewRenderer constructor.
    *
    * @return {Alfresco.rm.DocumentListSimpleViewRenderer} The new Alfresco.rm.DocumentListSimpleViewRenderer instance
    * @constructor
    */
   Alfresco.rm.DocumentListSimpleViewRenderer = function(name, parentDocumentList)
   {
      Alfresco.rm.DocumentListSimpleViewRenderer.superclass.constructor.call(this, name, parentDocumentList);
      return this;
   };

   /**
    * Extend from Alfresco.DocumentListSimpleViewRenderer
    */
   YAHOO.extend(Alfresco.rm.DocumentListSimpleViewRenderer, Alfresco.DocumentListSimpleViewRenderer,
   {
      renderCellDescription: function Alfresco_rm_DocumentListSimpleViewRenderer_renderCellDescription(scope, elCell, oRecord, oColumn, oData)
      {
         Alfresco.rm.DocumentListSimpleViewRenderer.superclass.renderCellDescription.call(this, scope, elCell, oRecord, oColumn, oData);
         Alfresco.rm.classifiedBanner(elCell, oRecord, this.parentDocumentList.msg);
      }
   });
})();