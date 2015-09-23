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
 * @since 2.4.a
 *
 * @event RM_USER_SECURITY_CLEARANCE_GET_ALL
 * @event RM_USER_SECURITY_CLEARANCE_SET
 * @event RM_USER_SECURITY_CLEARANCE_SET_CONFIRMED
 */

define(["dojo/_base/declare",
      "alfresco/services/CrudService",
      "service/constants/Default",
      "dojo/_base/array",
      "dojo/_base/lang",
      "alfresco/util/urlUtils"],

      function(declare, CrudService, AlfConstants, array, lang, urlUtils) {

   return declare([CrudService], {

      /**
       * An array of the i18n files to use with this service.
       *
       * @instance
       * @type {object[]}
       * @default [{i18nFile: "./i18n/UserSecurityClearanceService.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/UserSecurityClearanceService.properties"}],

      /**
       * The URL used to get and set a users' security clearance.
       *
       * @instance
       * @type {string}
       * @default api/classification/clearance
       */
      clearanceApi: "api/classification/clearance",

      /**
       *
       * @instance
       * @param {array} args Constructor arguments
       *
       * @listens RM_USER_SECURITY_CLEARANCE_GET_ALL
       * @listens RM_USER_SECURITY_CLEARANCE_SET
       * @listens RM_USER_SECURITY_CLEARANCE_SET_CONFIRMED
       */
      constructor: function rm_services_userSecurityClearanceService__constructor(args)
      {
         this.alfSubscribe("RM_USER_SECURITY_CLEARANCE_GET_ALL", lang.hitch(this, this.onGetAll));
         this.alfSubscribe("RM_USER_SECURITY_CLEARANCE_SET", lang.hitch(this, this.onSet));
         this.alfSubscribe("RM_USER_SECURITY_CLEARANCE_SET_CONFIRMED", lang.hitch(this, this.onSetConfirmed));
         this.alfSubscribe("RM_USER_SECURITY_CLEARANCE_VIEW_USER", lang.hitch(this, this.onViewUser));
      },

      /**
       * @instance
       * @see module:alfresco/util/urlUtils#addQueryParameter
       */
      addQueryParameter: function alfresco_services_userSecurityClearanceService__addQueryParameter() {
         var url = arguments[0],
             param = arguments[1],
             value = arguments[2],
             encodeValue = true;
         return urlUtils.addQueryParameter(url, param, value, encodeValue);
      },

      /**
       * Get all user security clearances.
       *
       * @param payload
       */
      onGetAll: function rm_services_userSecurityClearanceService__onGetAll(payload)
      {
         var url = this.clearanceApi;

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
            url: url,
            preventCache: true
         });

         this.inherited(arguments);
      },

      /**
       * This is called to set the user clearance level. It prompts the user for confirmation.
       * Payload should look like:
       * @example
       * {
       *    clearanceId: "TopSecret"
       *    clearanceLabel: "Top Secret"
       *    completeName: "Mike Jackson (mjackson)"
       *    levels: {
       *       Confidential: "Confidential"
       *       Secret: "Secret"
       *       TopSecret: "Top Secret"
       *       Unclassified: "No Clearance"
       *    }
       *    username: "mjackson"
       * }
       *
       * @param payload
       * @fires ALF_CREATE_DIALOG_REQUEST
       * @fires RM_USER_SECURITY_CLEARANCE_SET_CONFIRMED
       */
      onSet: function rm_services_userSecurityClearance_onSet(payload)
      {
         this.alfPublish("ALF_CREATE_DIALOG_REQUEST", {
            dialogTitle: this.message("userClearance.set.dialog.title"),
            handleOverflow: false,
            textContent: this.message("userClearance.set.dialog.content", {0: payload.completeName, 1: payload.levels[payload.clearanceId]}),
            cancelPublishTopic: payload.responseTopic + "_CANCEL",
            widgetsButtons: [
               {
                  name: "alfresco/buttons/AlfButton",
                  id: "OK",
                  config: {
                     label: "userClearance.set.dialog.confirm",
                     publishTopic: "RM_USER_SECURITY_CLEARANCE_SET_CONFIRMED",
                     publishPayload: payload
                  }
               },
               {
                  name: "alfresco/buttons/AlfButton",
                  id: "CANCEL",
                  config: {
                     label: "userClearance.set.dialog.cancel",
                     publishTopic: payload.responseTopic + "_CANCEL"
                  }
               }
            ]
         }, true);
      },

      /**
       * This calls the api to set the actual clearance level for a user.
       *
       * Payload should contain a username and a clearanceId
       * Note: Success and failure notifications are handled externally to this service
       * (e.g. built in to the publishingDropdownMenu used on the admin console page)
       *
       * @param payload
       */
      onSetConfirmed: function rm_services_userSecurityClearance_onSetConfirmed(payload)
      {
         var url = AlfConstants.PROXY_URI + this.clearanceApi;

         url = this.addQueryParameter(url, "username", payload.username);
         url = this.addQueryParameter(url, "clearanceId", payload.clearanceId);

         this.serviceXhr({
            url: url,
            method: "PUT",
            alfTopic: payload.responseTopic
         });
      },

      /**
       * Navigate to the user's admin console profile page.
       *
       * @param payload
       */
      onViewUser: function rm_services_userSecurityClearance_onViewUser(payload)
      {
         var publishPayload = {
            url: "console/admin-console/users",
            hashParams: {
               state: "panel=view&userid=" + payload.userName
            },
            type: "PAGE_RELATIVE"
         };

         this.alfPublish("ALF_NAVIGATE_TO_PAGE", publishPayload, true);
      }
   });
});
