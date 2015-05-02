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
 * This Aikau Service interacts with the user clearance security REST API allowing you to get user security clearances.
 * It's possible to filter and sort user clearances and using paging is possible as well.
 *
 * @module rm/services/UserSecurityClearanceService
 * @extends alfresco/services/CrudService
 *
 * @author Tuna Aksoy
 * @since RM 3.0
 *
 * @event RM_USER_SECURITY_CLEARANCE_GET_ALL
 */

define(["dojo/_base/declare",
      "alfresco/services/CrudService",
      "dojo/_base/lang"],

      function(declare, CrudService, lang) {

   return declare([CrudService], {

      /**
       *
       * @instance
       * @param {array} args Constructor arguments
       *
       * @listens RM_USER_SECURITY_CLEARANCE_GET_ALL
       */
      constructor: function rm_services_userSecurityClearanceService__constructor(args)
      {
         this.alfSubscribe("RM_USER_SECURITY_CLEARANCE_GET_ALL", lang.hitch(this, this.onGetAll));
      },

      /**
       * Get all user security clearances.
       *
       * @param payload
       */
      onGetAll: function rm_services_userSecurityClearanceService__onGetAll(payload)
      {
         var url = lang.getObject("url", false, payload);
         if (!url) {
            this.alfLog("warn", "A request was made to service a UserSecurityClearance request but no 'url' attribute was provided on the payload", payload, this);
         }

         var sortAscending = payload.sortAscending;
         if (sortAscending != null)
         {
            url = this.addQueryParameter(url, "sortAscending", sortAscending);
         }

         var sortField = payload.sortField;
         if (sortField != null)
         {
            url = this.addQueryParameter(url, "sortField", sortField);
         }

         payload = lang.mixin(payload, {
            url: url
         });

         this.inherited(arguments);
      }
   });
});