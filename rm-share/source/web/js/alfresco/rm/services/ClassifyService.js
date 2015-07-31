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
 * @since RM 3.0.a
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
          * URL used to get exemption categories.
          *
          * @instance
          * @type {string}
          * @default
          */
         exemptionsAPIGet: "/api/classification/exemptioncategories",

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
            this.alfSubscribe("RM_CLASSIFY_EXEMPTIONS_GET", lang.hitch(this, this.onGetExemptions));
            this.alfSubscribe("RM_CLASSIFY_CONTENT", lang.hitch(this, this.onClassifyContent));
            this.alfSubscribe("RM_EDIT_CLASSIFIED_CONTENT", lang.hitch(this, this.onEditClassifiedContent));
            this.alfSubscribe("RM_CLASSIFY", lang.hitch(this, this.onCreate));
            this.alfSubscribe("RM_EDIT_CLASSIFIED", lang.hitch(this, this.onUpdate));
            this.alfSubscribe("ALF_CLASSIFY_VALIDATE_CLASSIFY_BY", lang.hitch(this, this.onValidateClassifiedBy));
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
          * Get all the declassification exemptions
          */
         onGetExemptions: function rm_services_classifyService__onGetExemptions(payload)
         {
            if (payload && payload.alfResponseTopic)
            {
               var url = AlfConstants.PROXY_URI + this.exemptionsAPIGet;
               this.serviceXhr({
                  url: url,
                  method: "GET",
                  alfTopic: payload.alfResponseTopic
               });
            }
            else
            {
               this.alfLog("error", "A request to get the declassification exemptions but the 'responseTopic' attributes was not provided in the payload", payload);
            }
         },

         /**
          * Helper method for creating publication events for classify/edit content dialogs
          *
          * @param configObject
          * @param payload
          */
         _publishClassificationFormDialogRequest: function rm_services_classifyService___publishClassificationFormDialogRequest(configObject, payload)
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
                        },
                        validationConfig: [
                           {
                              validation: "validationTopic",
                              validationTopic: "ALF_CLASSIFY_VALIDATE_CLASSIFY_BY",
                              errorMessage: this.message("label.classify.classifiedBy.validation.error")
                           }
                        ]
                     }
                  },{
                     // FIXME: Tooltip
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
                        width: "362px",
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
                  },{
                     id: "TAB_CONTAINER",
                     name: "alfresco/forms/TabbedControls",
                     config: {
                        widgets: [{
                           id: "DOWNGRADE_SCHEDULE",
                           title: this.message("label.classify.downgradeSchedule"),
                           name: "alfresco/forms/ControlColumn",
                           config: {
                              widgets: [{
                                 id: "DOWNGRADE_DATE",
                                 name: "alfresco/forms/controls/DateTextBox",
                                 config: {
                                    label: this.message("label.classify.downgradeDate"),
                                    name: "downgradeDate",
                                    value: configObject.downgradeDate
                                 }
                              },{
                                 // FIXME: Tooltip
                                 id: "DOWNGRADE_EVENT",
                                 name: "alfresco/forms/controls/TextBox",
                                 config: {
                                    label: this.message("label.classify.downgradeEvent"),
                                    name: "downgradeEvent",
                                    value: configObject.downgradeEvent
                                 }
                              },{
                                 id: "DOWNGRADE_INSTRUCTIONS",
                                 name: "alfresco/forms/controls/TextArea",
                                 config: {
                                    label: this.message("label.classify.downgradeInstructions"),
                                    name: "downgradeInstructions",
                                    value: configObject.downgradeInstructions
                                 }
                              }]
                           }
                        },{
                           id: "DECLASSIFICATION_SCHEDULE",
                           title: this.message("label.classify.declassificationSchedule"),
                           name: "alfresco/forms/ControlColumn",
                           config: {
                              widgets: [{
                                 id: "DECLASSIFICATION_DATE",
                                 name: "alfresco/forms/controls/DateTextBox",
                                 config: {
                                    label: this.message("label.classify.declassificationDate"),
                                    name: "declassificationDate",
                                    value: configObject.declassificationDate
                                 }
                              },{
                                 // FIXME: Tooltip
                                 id: "DECLASSIFICATION_EVENT",
                                 name: "alfresco/forms/controls/TextBox",
                                 config: {
                                    label: this.message("label.classify.declassificationEvent"),
                                    name: "declassificationEvent",
                                    value: configObject.declassificationEvent
                                 }
                              },{
                                 id: "EXEMPTIONS",
                                 name: "alfresco/forms/controls/MultiSelectInput",
                                 config: {
                                    label: this.message("label.classify.declassificationExemptions"),
                                    name: "declassificationExemptions",
                                    width: "362px",
                                    value: configObject.declassificationExemptions,
                                    optionsConfig: {
                                       queryAttribute: "fullCategory",
                                       valueAttribute: "id",
                                       labelAttribute: "fullCategory",
                                       labelFormat: {
                                          choice: "{value}"
                                       },
                                       publishTopic: "RM_CLASSIFY_EXEMPTIONS_GET",
                                       publishPayload: {
                                          resultsProperty: "response.data.items"
                                       },
                                       searchStartsWith: false
                                    }
                                 }
                              }]
                           }
                        }]
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
            configObject.classifiedByValue = Alfresco.constants.USER_FULLNAME;

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
         onEditClassifiedContent: function rm_services_classifyService__onEditClassifiedContent(payload)
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
            configObject.downgradeDate = properties["clf_downgradeDate"] && properties["clf_downgradeDate"].iso8601;
            configObject.downgradeEvent = properties["clf_downgradeEvent"];
            configObject.downgradeInstructions = properties["clf_downgradeInstructions"];
            configObject.declassificationDate = properties["clf_declassificationDate"] && properties["clf_declassificationDate"].iso8601;
            configObject.declassificationEvent = properties["clf_declassificationEvent"];
            configObject.declassificationExemptions = properties["clf_declassificationExemptions"];

            this._publishClassificationFormDialogRequest(configObject, payload);
         },

         /**
          * Helper method for the classify/edit content actions.
          *
          * @param payload
          * @param successMessage
          * @param failureMessage
          */
         _onClassifyAction: function rm_services_classifyService___onClassifyAction(payload, successMessage, failureMessage)
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
         onCreate: function rm_services_classifyService__onCreate(payload)
         {
            this._onClassifyAction(payload, "label.classify.content.success", "label.classify.content.failure");

            this.inherited(arguments);
         },

         /**
          * Edits the classified content
          *
          * @param payload
          */
         onUpdate: function rm_services_classifyService__onUpdate(payload)
         {
            this._onClassifyAction(payload, "label.edit.classified.content.success", "label.edit.classified.content.failure");

            this.inherited(arguments);
         },

         /**
          * Used to validate the classified by field.
          *
          * @param payload
          */
         onValidateClassifiedBy: function rm_services_classifyService__onValidateClassifiedBy(payload)
         {
            // Classified By field MUST NOT start with a whitespace nor can it consist of only whitespaces. RM-2373
            var isValid = (lang.trim(payload.value) !== "" && payload.value.substring(0,1) !== " ");

            this.alfPublish(payload.alfResponseTopic, {isValid: isValid});
         }
      });
   });