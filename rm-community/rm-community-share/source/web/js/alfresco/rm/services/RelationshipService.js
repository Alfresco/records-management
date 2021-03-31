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
 * This Aikau Service interacts with the relationship REST API allowing you to create and manage document relationships
 *
 * @module rm/service/RelationshipService
 * @extends alfresco/service/crudService
 *
 * @author David Webster
 * @since 2.4.a
 *
 * @event RM_RELATIONSHIP_CREATE
 * @event RM_RELATIONSHIP_GET_ALL
 * @event RM_RELATIONSHIP_DELETE
 * @event RM_RELATIONSHIP_ADD
 */

define(["dojo/_base/declare",
      "alfresco/services/CrudService",
      "service/constants/Default",
      "alfresco/core/NodeUtils",
      "dojo/_base/lang"],
   function(declare, CrudService, AlfConstants, NodeUtils, lang) {

      return declare([CrudService], {

         /**
          * Scope the message keys used in this service
          *
          * @instance
          * @type String
          * @default "org.alfresco.rm.RelationshipService"
          */
         i18nScope: "org.alfresco.rm.RelationshipService",

         /**
          * An array of the i18n files to use with this service.
          *
          * @instance
          * @type {object[]}
          * @default [{i18nFile: "./i18n/RelationshipService.properties"}]
          */
         i18nRequirements: [{i18nFile: "./i18n/RelationshipService.properties"}],

         /**
          *
          * URL used to get relationships. Parse through lang.mixin for token substitution.
          *
          * @instance
          * @type {string}
          * @default
          */
         relationshipAPIGet: "api/node/{nodeRefUrl}/relationships",

         /**
          * URL used to delete relationships. Parse through lang.mixin for token substitution.
          *
          * @instance
          * @type {string}
          * @default
          */
         relationshipAPIDelete: "api/node/{nodeRefUrl}/targetnode/{targetNodeRef}/uniqueName/{relationshipUniqueName}",


         /**
          * URL used to create relationships. Parse through lang.mixin for token substitution.
          *
          * @instance
          * @type {string}
          * @default
          */
         relationshipAPICreate: "api/node/{nodeRefUrl}/customreferences",

         /**
          *
          * @instance
          * @param {array} args Constructor arguments
          *
          * @listens RM_RELATIONSHIP_GET_ALL
          * @listens RM_RELATIONSHIP_DELETE
          */
         registerSubscriptions: function rm_services_relationshipService__registerSubscriptions(args)
         {
            this.alfSubscribe("RM_RELATIONSHIP_CREATE", lang.hitch(this, this.onCreate));
            this.alfSubscribe("RM_RELATIONSHIP_GET_ALL", lang.hitch(this, this.onGetAll));
            this.alfSubscribe("RM_RELATIONSHIP_DELETE", lang.hitch(this, this.onDelete));
            this.alfSubscribe("RM_RELATIONSHIP_ADD", lang.hitch(this, this.onAddRelationship));
         },

         /**
          * Create a relationship between given nodes.
          *
          * @param payload
          */
         onCreate: function rm_services_relationshipService__onCreate(payload) {
            if (!payload.nodeRef)
            {
               this.alfLog("error", "nodeRef required");
            }

            // Update the payload before calling the superclass method:
            payload.nodeRefUrl = NodeUtils.processNodeRef(payload.nodeRef).uri;
            payload = lang.mixin(payload, {
               url: lang.replace(this.relationshipAPICreate, payload),
               successMessage: this.message("label.add.relationship.success")
            });

            this.inherited(arguments);
         },


         /**
          * Get all relationships for given node.
          *
          * @param payload
          */
         onGetAll: function rm_services_relationshipService__onGetAll(payload) {

            if (!payload.nodeRef)
            {
               this.alfLog("error", "nodeRef required");
            }

            // Update the payload before calling the superclass method:
            payload.nodeRefUrl = NodeUtils.processNodeRef(payload.nodeRef).uri;
            payload = lang.mixin(payload, {
               url: lang.replace(this.relationshipAPIGet, payload)
            });

            this.inherited(arguments);
         },

         /**
          *
          * Delete the relationship for the given nodes
          *
          * @instance
          * @param payload
          */
         onDelete: function rm_services_relationshipService__onDelete(payload) {
            if (!payload.nodeRef)
            {
               this.alfLog("error", "nodeRef required");
            }

            // Update the payload before calling the superclass method:
            payload.nodeRefUrl = NodeUtils.processNodeRef(payload.nodeRef).uri;
            payload = lang.mixin(payload, {
               url: lang.replace(this.relationshipAPIDelete, {
                     nodeRefUrl: payload.nodeRefUrl,
                     targetNodeRef: NodeUtils.processNodeRef(payload.node.nodeRef).uri,
                     relationshipUniqueName: payload.node.relationshipUniqueName
                  }),
               requiresConfirmation: true,
               confirmationTitle: this.message("label.confirmation.title.delete-relationship"),
               confirmationPrompt: this.message("label.confirmationPrompt.delete-relationship", payload.node.properties['cm:name']),
               successMessage: this.message("label.delete-relationship.successMessage", payload.node.properties['cm:name'])
            });

            this.inherited(arguments);
         },

         /**
          * Triggered by the add relationship action. Shows dialog using [DialogService]
          *
          * @instance
          * @param payload
          *
          * @fires ALF_CREATE_FORM_DIALOG_REQUEST
          * @fires RM_RELATIONSHIP_CREATE
          * @fires ALF_GET_FORM_CONTROL_OPTIONS
          */
         onAddRelationship: function rm_services_relationshipService__onAddRelationship(payload){
            var item = payload.item,
               site = item.location.site.name;

            this.alfPublish("ALF_CREATE_FORM_DIALOG_REQUEST", {
               dialogId: "ADD_RELATIONSHIP_DIALOG",
               dialogTitle: this.message("label.title.new-relationship"),
               dialogConfirmationButtonTitle: this.message("label.button.create"),
               dialogCancellationButtonTitle: this.message("label.button.cancel"),
               formSubmissionTopic: "RM_RELATIONSHIP_CREATE",
               formSubmissionPayloadMixin: {
                  nodeRef: item.nodeRef
               },
               widgets: [{
                  name: "rm/lists/AlfRmRelationshipList",
                  config: {
                     additionalCssClasses: "rm-relationship-select-record-form-info",
                     site: site,
                     currentData: {
                        items: [item]
                     },
                     currentItem: item,
                     waitForPageWidgets: false
                  }
               },{
                  name: "alfresco/forms/controls/Select",
                  config: {
                     additionalCssClasses: "rm-relationship-select-record-form-select",
                     name: "refId",
                     optionsConfig: {
                        // TODO should this be abstracted out of here?
                        publishTopic: "ALF_GET_FORM_CONTROL_OPTIONS",
                        publishPayload: {
                           url: AlfConstants.PROXY_URI + "api/rma/admin/relationshiplabels",
                           itemsAttribute: "data.relationshipLabels",
                           labelAttribute: "label",
                           valueAttribute: "uniqueName"
                        }
                     }
                  }
               },{
                  name: "rm/forms/controls/AlfRmRecordPickerControl",
                  config:
                  {
                     additionalCssClasses: "rm-relationship-select-record-form-control",
                     name: "toNode",
                     site: site,
                     pickerRootNode: item.node.rmNode.filePlan,
                     requirementConfig: {
                        initialValue: true
                     }
                  }
               }]
            }, true);

         }
      });
   });
