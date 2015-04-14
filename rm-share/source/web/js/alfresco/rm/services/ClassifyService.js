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
 * This Aikau Service uses the classify REST APIs
 *
 * @module rm/service/ClassifyService
 * @extends module:alfresco/core/Core
 * @mixes module:alfresco/core/CoreXhr
 * @author David Webster
 * @since RM 3.0
 *
 * @event RM_CLASSIFY_REASONS_GET
 */

define(["dojo/_base/declare",
      "alfresco/core/Core",
      "alfresco/core/CoreXhr",
      "service/constants/Default",
      "dojo/_base/lang"],
   function (declare, AlfCore, AlfXhr, AlfConstants, lang) {

      return declare([AlfCore, AlfXhr], {

         /**
          * An array of the i18n files to use with this service.
          *
          * @instance
          * @type {object[]}
          * @default [{i18nFile: "./i18n/ClassifyService.properties"}]
          */
         i18nRequirements: [{i18nFile: "./i18n/ClassifyService.properties"}],

         /**
          *
          * URL used to get reasons.
          *
          * @instance
          * @type {string}
          * @default
          */
         reasonsAPIGet: "api/classification/reasons",

         /**
          *
          * @instance
          * @param {array} args Constructor arguments
          *
          * @listens RM_CLASSIFY_REASONS_GET
          */
         constructor: function rm_services_classifyService__constructor(args) {
            this.alfSubscribe("RM_CLASSIFY_REASONS_GET", lang.hitch(this, this.onGetReasons));
            this.alfSubscribe("RM_CLASSIFY_CONTENT", lang.hitch(this, this.onClassifyContent));
         },

         /**
          * Create a relationship between given nodes.
          *
          * @param payload
          */
         onGetReasons: function rm_services_classifyService__onGetReasons(payload) {
            if (payload && payload.alfResponseTopic) {
               var url = AlfConstants.PROXY_URI + this.reasonsAPIGet;
               this.serviceXhr({
                  url: url,
                  method: "GET",
                  alfTopic: payload.alfResponseTopic
               });

            }
            else {
               this.alfLog("error", "A request to get the classification reasons but the 'responseTopic' attributes was not provided in the payload", payload);
            }
         },

         /**
          * Triggered by the classify document and classify record actions. Shows dialog using [DialogService]
          *
          * @instance
          * @param payload
          *
          * @fires ALF_CREATE_FORM_DIALOG_REQUEST
          * @fires RM_CLASSIFY
          * @fires RM_CLASSIFY_REASONS_GET
          */
         onClassifyContent: function rm_services_classifyService__onClassifyContent(payload) {
            var item = payload.item,
               site = item.location.site.name;

            this.alfPublish("ALF_CREATE_FORM_DIALOG_REQUEST", {
               dialogTitle: this.message("label.classify.dialog.title"),
               dialogConfirmationButtonTitle: this.message("label.button.create"),
               dialogCancellationButtonTitle: this.message("label.button.cancel"),
               formSubmissionTopic: "RM_CLASSIFY",
               formSubmissionPayloadMixin: {
                  nodeRef: item.nodeRef
               },
               widgets: [
                  {
                     id: "REASONS",
                     name: "alfresco/forms/controls/MultiSelectInput",
                     config: {
                        label: "label.classify.reasons",
                        name: "reasons",
                        width: "400px",
                        optionsConfig: {
                           queryAttribute: "id",
                           valueAttribute: "id",
                           labelAttribute: "id",
                           publishTopic: "RM_CLASSIFY_REASONS_GET",
                           publishPayload: {
                              resultsProperty: "response.data.items"
                           }
                        }
                     }
                  }
               ]
            }, true);
         }
      });
   });