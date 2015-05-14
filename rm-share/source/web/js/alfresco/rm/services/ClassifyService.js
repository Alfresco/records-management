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
 * @module rm/services/ClassifyService
 * @extends module:alfresco/core/Core
 * @extends module:alfresco/service/crudService
 * @mixes module:alfresco/core/CoreXhr
 * @author David Webster
 * @since RM 3.0
 *
 * @event RM_CLASSIFY_REASONS_GET
 */

define(["dojo/_base/declare",
      "alfresco/core/Core",
      "alfresco/core/CoreXhr",
      "alfresco/services/CrudService",
      "service/constants/Default",
      "alfresco/core/NodeUtils",
      "dojo/_base/lang"],
   function (declare, AlfCore, AlfXhr, CrudService, AlfConstants, NodeUtils, lang) {

      return declare([AlfCore, AlfXhr, CrudService], {

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
          * URL used to get classification reasons.
          *
          * @instance
          * @type {string}
          * @default
          */
         reasonsAPIGet: "api/classification/reasons",

         /**
          *
          * URL used to get classification levels.
          *
          * @instance
          * @type {string}
          * @default
          */
         levelsAPIGet: "api/classification/levels",

         /**
          * URL used to classify content. Parse through lang.mixin for token substitution.
          *
          * @instance
          * @type {string}
          * @default
          */
         classifyAPICreate: "api/node/{nodeRefUrl}/classify",

         /**
          *
          * @instance
          * @param {array} args Constructor arguments
          *
          * @listens RM_CLASSIFY_REASONS_GET
          * @listens RM_CLASSIFY_CONTENT
          * @listens RM_CLASSIFY
          */
         constructor: function rm_services_classifyService__constructor(args) {
            this.alfSubscribe("RM_CLASSIFY_REASONS_GET", lang.hitch(this, this.onGetReasons));
            this.alfSubscribe("RM_CLASSIFY_CONTENT", lang.hitch(this, this.onClassifyContent));
            this.alfSubscribe("RM_CLASSIFY", lang.hitch(this, this.onCreate));
         },

         /**
          * Get all the classification reasons for the given node.
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
            else
            {
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
          * @fires ALF_GET_FORM_CONTROL_OPTIONS
          */
         onClassifyContent: function rm_services_classifyService__onClassifyContent(payload) {
            this.alfPublish("ALF_CREATE_FORM_DIALOG_REQUEST", {
               dialogTitle: this.message("label.classify.dialog.title"),
               dialogConfirmationButtonTitle: this.message("label.button.create"),
               dialogCancellationButtonTitle: this.message("label.button.cancel"),
               formSubmissionTopic: "RM_CLASSIFY",
               formSubmissionPayloadMixin: {
                  nodeRef: payload.item.nodeRef
               },
               widgets: [
                  {
                     id: "LEVELS",
                     name: "alfresco/forms/controls/Select",
                     config: {
                        label: "label.classify.levels",
                        name: "classificationLevelId",
                        requirementConfig: {
                           initialValue: true
                        },
                        optionsConfig: {
                           publishTopic: "ALF_GET_FORM_CONTROL_OPTIONS",
                           publishPayload: {
                              url: AlfConstants.PROXY_URI + this.levelsAPIGet,
                              itemsAttribute: "data.items",
                              labelAttribute: "displayLabel",
                              valueAttribute: "id"
                           }
                        }
                     }
                  },{
                     id: "AUTHORITY",
                     name: "alfresco/forms/controls/DojoValidationTextBox",
                     config: {
                        label: "label.classify.authority",
                        name: "classificationAuthority",
                        requirementConfig: {
                           initialValue: true
                        }
                     }
                  },{
                     id: "REASONS",
                     name: "alfresco/forms/controls/MultiSelectInput",
                     config: {
                        label: "label.classify.reasons",
                        name: "classificationReasons",
                        width: "400px",
                        requirementConfig: {
                           initialValue: true
                        },
                        optionsConfig: {
                           queryAttribute: "displayLabel",
                           valueAttribute: "id",
                           labelAttribute: "displayLabel",
                           publishTopic: "RM_CLASSIFY_REASONS_GET",
                           publishPayload: {
                              resultsProperty: "response.data.items"
                           },
                           searchStartsWith: false
                        }
                     }
                  }
               ]
            }, true);
         },

         /**
          * Classifies the given node.
          *
          * @param payload
          */
         onCreate: function rm_services_classifyService__onCreate(payload) {
            if (!payload.nodeRef)
            {
               this.alfLog("error", "nodeRef required");
            }

            // Update the payload before calling the superclass method:
            payload.nodeRefUrl = NodeUtils.processNodeRef(payload.nodeRef).uri;
            payload = lang.mixin(payload, {
               url: lang.replace(this.classifyAPICreate, payload),
               successMessage: this.message("label.classify.content.success"),
               failureMessage: this.message("label.classify.content.failure")
            });

            this.inherited(arguments);
         }
      });
   });