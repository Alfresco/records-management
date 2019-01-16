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
 * Document actions component - RM extensions.
 *
 * @namespace Alfresco
 * @class Alfresco.rm.component.DocumentReferences
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Sel = YAHOO.util.Selector;

   /**
    * RecordsDocumentReferences constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.rm.component.DocumentReferences} The new RecordsDocumentReferences instance
    * @constructor
    */
   Alfresco.rm.component.DocumentReferences = function(htmlId)
   {
      Alfresco.rm.component.DocumentReferences.superclass.constructor.call(this, "Alfresco.rm.component.DocumentReferences", htmlId);
      YAHOO.Bubbling.on("metadataRefresh", this.doRefresh, this);
      return this;
   };

   /**
    * Extend from Alfresco.DocumentActions
    */
   YAHOO.extend(Alfresco.rm.component.DocumentReferences, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * The nodeRef to the object that owns the disposition schedule that is configured
          *
          * @property nodeRef
          * @type Alfresco.util.NodeRef
          */
         nodeRef: null,

         /**
          * Current siteId.
          *
          * @property siteId
          * @type string
          */
         siteId: null,

         /**
          * Current containerId.
          *
          * @property containerId
          * @type string
          */
         containerId: null
      },

      doRefresh: function()
      {
         YAHOO.Bubbling.unsubscribe("metadataRefresh", this.doRefresh, this);

         var url = 'config/components/document-details/document-references?nodeRef={nodeRef}&container={containerId}';
         url += this.options.siteId ? '&site={siteId}' :  '';

         this.refresh(url);
      }
   });
})();
