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
          * Scope the message keys used in this service
          *
          * @instance
          * @type String
          * @default "org.alfresco.rm.ClassifyService"
          */
         i18nScope: "org.alfresco.rm.ClassifyService",

         /**
          * An array of the i18n files to use with this service.
          *
          * @instance
          * @type {object[]}
          * @default [{i18nFile: "./i18n/ClassifyService.properties"}]
          */
         i18nRequirements: [{i18nFile: "./i18n/ClassifyService.properties"}],

         /**
          * URL used to get classification reasons.
          *
          * @instance
          * @type {string}
          * @default
          */
         reasonsAPIGet: "api/classification/reasons",

         /**
          * URL used to get classification levels.
          *
          * @instance
          * @type {string}
          * @default
          */
         levelsAPIGet: "api/classification/levels",

         /**
          * URL used to classify/edit content. Parse through lang.mixin for token substitution.
          *
          * @instance
          * @type {string}
          * @default
          */
         classifyAPICreateUpdate: "api/node/{nodeRefUrl}/classify",

         /**
          * @instance
          * @param {array} args Constructor arguments
          *
          * @listens RM_CLASSIFY_REASONS_GET
          * @listens RM_CLASSIFY_CONTENT
          * @listens RM_EDIT_CLASSIFIED_CONTENT
          * @listens RM_CLASSIFY
          * @listens RM_EDIT_CLASSIFIED
          */
         constructor: function rm_services_classifyService__constructor(args) {
            this.alfSubscribe("RM_CLASSIFY_REASONS_GET", lang.hitch(this, this.onGetReasons));
            this.alfSubscribe("RM_CLASSIFY_CONTENT", lang.hitch(this, this.onClassifyContent));
            this.alfSubscribe("RM_EDIT_CLASSIFIED_CONTENT", lang.hitch(this, this.onEditClassifiedContent));
            this.alfSubscribe("RM_CLASSIFY", lang.hitch(this, this.onCreate));
            this.alfSubscribe("RM_EDIT_CLASSIFIED", lang.hitch(this, this.onUpdate));
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
          * Helper method for creating publication events for classify/edit content dialogs
          *
          * @param configObject
          * @param payload
          */
         _publishClassificationFormDialogRequest: function rm_services_classifyService__publishClassificationFormDialogRequest(configObject, payload)
         {
            var dialogTitle = (Alfresco.rm.isRMSite(payload.item.location.site)) ? configObject.dialogTitleRm : configObject.dialogTitleCollab;

            this.alfPublish("ALF_CREATE_FORM_DIALOG_REQUEST", {
               dialogId: configObject.dialogId,
               dialogTitle: this.message(dialogTitle),
               dialogConfirmationButtonTitle: this.message(configObject.dialogConfirmationButtonTitle),
               dialogConfirmationButtonId: configObject.dialogConfirmationButtonId,
               dialogCancellationButtonTitle: this.message("label.button.cancel"),
               dialogCancellationButtonId: "CANCEL",
               formSubmissionTopic: configObject.formSubmissionTopic,
               formSubmissionPayloadMixin: {
                  nodeRef: payload.item.nodeRef
               },
               widgets: [
                  {
                     id: "LEVELS",
                     name: "alfresco/forms/controls/Select",
                     config: {
                        label: this.message("label.classify.levels"),
                        name: "classificationLevelId",
                        requirementConfig: {
                           initialValue: true
                        },
                        value: configObject.levelsValue,
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
                     id: "CLASSIFIED_BY",
                     name: "alfresco/forms/controls/TextBox",
                     config: {
                        label: this.message("label.classify.by"),
                        name: "classifiedBy",
                        value: configObject.classifiedByValue,
                        requirementConfig: {
                           initialValue: true
                        }
                     }
                  },{
                     id: "AGENCY",
                     name: "alfresco/forms/controls/TextBox",
                     config: {
                        label: this.message("label.classify.agency"),
                        name: "classificationAgency",
                        value: configObject.agencyValue
                     }
                  },{
                     id: "REASONS",
                     name: "alfresco/forms/controls/MultiSelectInput",
                     config: {
                        label: this.message("label.classify.reasons"),
                        name: "classificationReasons",
                        width: "400px",
                        requirementConfig: {
                           initialValue: true
                        },
                        value: configObject.reasonsValue,
                        optionsConfig: {
                           queryAttribute: "fullReason",
                           valueAttribute: "id",
                           labelAttribute: "fullReason",
                           labelFormat: {
                              choice: "{value}"
                           },
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
         onClassifyContent: function rm_services_classifyService__onClassifyContent(payload)
         {
            var configObject = {};
            configObject.dialogTitleRm = "label.classify.dialog.title.rm";
            configObject.dialogTitleCollab = "label.classify.dialog.title";
            configObject.dialogId = "CLASSIFY_CONTENT_DIALOG";
            configObject.dialogConfirmationButtonTitle = "label.button.create";
            configObject.dialogConfirmationButtonId = "OK";
            configObject.formSubmissionTopic = "RM_CLASSIFY";
            configObject.levelsValue = null;
            configObject.classifiedByValue = Alfresco.constants.USER_FULLNAME;
            configObject.agencyValue = null;
            configObject.reasonsValue = null;

            this._publishClassificationFormDialogRequest(configObject, payload);
         },

         /**
          * Triggered by the edit classified file/record actions. Shows dialog using [DialogService]
          *
          * @instance
          * @param payload
          *
          * @fires ALF_CREATE_FORM_DIALOG_REQUEST
          * @fires RM_CLASSIFY
          * @fires RM_CLASSIFY_REASONS_GET
          * @fires ALF_GET_FORM_CONTROL_OPTIONS
          */
         onEditClassifiedContent: function rm_services_classifyService_onEditClassifiedContent(payload)
         {
            var configObject = {},
               properties = payload.item.node.properties;

            configObject.dialogTitleRm = "label.edit.classification.dialog.title.rm";
            configObject.dialogTitleCollab = "label.edit.classification.dialog.title";
            configObject.dialogId = "EDIT_CLASSIFIED_CONTENT_DIALOG";
            configObject.dialogConfirmationButtonTitle = "label.button.edit";
            configObject.dialogConfirmationButtonId = "Edit";
            configObject.formSubmissionTopic = "RM_EDIT_CLASSIFIED";
            configObject.levelsValue = properties["clf_currentClassification"].id;
            configObject.classifiedByValue = properties["clf_classifiedBy"];
            configObject.agencyValue = properties["clf_classificationAgency"];
            configObject.reasonsValue = properties["clf_classificationReasons"];

            this._publishClassificationFormDialogRequest(configObject, payload);
         },

         /**
          * Helper method for the classify/edit content actions.
          *
          * @param payload
          * @param successMessage
          * @param failureMessage
          */
         _onClassifyAction: function rm_services_classifyService__onClassifyAction(payload, successMessage, failureMessage)
         {
            if (!payload.nodeRef)
            {
               this.alfLog("error", "nodeRef required");
            }

            // Update the payload before calling the superclass method:
            payload.nodeRefUrl = NodeUtils.processNodeRef(payload.nodeRef).uri;
            payload = lang.mixin(payload, {
               url: lang.replace(this.classifyAPICreateUpdate, payload),
               successMessage: this.message(successMessage),
               failureMessage: this.message(failureMessage)
            });
         },

         /**
          * Classifies the given content.
          *
          * @param payload
          */
         onCreate: function rm_services_classifyService_onCreate(payload)
         {
            this._onClassifyAction(payload, "label.classify.content.success", "label.classify.content.failure");

            this.inherited(arguments);
         },

         /**
          * Edits the classified content
          *
          * @param payload
          */
         onUpdate: function rm_services_classifyService_onUpdate(payload)
         {
            this._onClassifyAction(payload, "label.edit.classified.content.success", "label.edit.classified.content.failure");

            this.inherited(arguments);
         }
      });
   });