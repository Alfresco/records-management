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
 * This Aikau Service extends the original ActionService to add RM related new functions
 *
 * @module rm/services/AlfRmActionService
 * @extends alfresco/services/ActionService
 *
 * @author Tuna Aksoy
 * @since 2.4.a
 */
define(["dojo/_base/declare",
      "alfresco/services/ActionService"],

function(declare, ActionService)
{
   return declare([ActionService],
   {
      /**
       * Handles requests to classify a content.
       *
       * @instance
       * @param {object} payload The payload from the original request
       */
      onClassifyContent: function alfresco_rm_services_AlfRmActionService__onClassifyContent(payload)
      {
         this.alfPublish("RM_CLASSIFY_CONTENT",
         {
            "item": payload.document
         });
      },

      /**
       * Handles requests to edit a classified content.
       *
       * @instance
       * @param {object} payload The payload from the original request
       */
      onEditClassifiedContent: function alfresco_rm_services_AlfRmActionService__onEditClassifiedContent(payload)
      {
         this.alfPublish("RM_EDIT_CLASSIFIED_CONTENT",
         {
            "item": payload.document
         });
      }
   });
});