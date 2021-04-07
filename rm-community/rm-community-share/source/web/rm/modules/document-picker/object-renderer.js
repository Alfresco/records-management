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
 * RM_ObjectRenderer component
 * 
 * Overrides certain methods so RM doc picker can display RM icons
 * 
 * @namespace Alfresco
 * @class Alfresco.RM_ObjectRenderer
 */
(function RM_ObjectRenderer()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event;

   /**
    * RM_ObjectRenderer component constructor.
    * 
    * @param {object} Instance of the DocumentPicker
    * @return {Alfresco.module.ObjectRenderer} The new ObjectRenderer instance
    * @constructor
    */
   Alfresco.rm.module.ObjectRenderer = function RM_ObjectRenderer_constructor(DocumentPicker)
   {
      Alfresco.rm.module.ObjectRenderer.superclass.constructor.call(this,DocumentPicker);

      return this;
   };
    
   YAHOO.extend(Alfresco.rm.module.ObjectRenderer, Alfresco.ObjectRenderer,
   {
/**
    * Generate item icon URL - displays RM icons depending on type
    *
    * @method getIconURL
    * @param item {object} Item object literal
    * @param size {number} Icon size (16, 32)
    */
   getIconURL : function RM_ObjectRenderer_getIconURL(item, size)
   {
      var types = item.type.split(':');
      if (types[0] !== 'rma' && types[0] !== 'dod')
      {
         return Alfresco.rm.module.ObjectRenderer.superclass.getIconURL.call(this, item, size);
      }
      else
      {
         var type = "";
         switch (types[1])
         {
            case "recordSeries":
            {
               type = 'record-series';
               break;
            }
            case "recordCategory":
            {
               type = 'record-category';
               break;
            }
            case "recordFolder":
            {
               type = 'record-folder';
               break;
            }
            case "nonElectronicDocument":
            {
               type = 'non-electronic';
               break;
            }
            case "metadataStub":
            {
               type = 'meta-stub';
               break;
            }
            default:
            {
               return Alfresco.constants.URL_RESCONTEXT + 'components/images/filetypes/' + Alfresco.util.getFileIcon(item.name, item.type, size); 
            }
         }
         return Alfresco.constants.URL_RESCONTEXT + 'rm/components/documentlibrary/images/' + type + '-'+size+'.png';
      }
   }      
   });
   
})();
