/**
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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

define(["dojo/_base/declare",
        "dojo/_base/lang",
        "service/constants/Default",
        "alfresco/core/Core",
        "alfresco/rm/services/AlfRmActionBridge"],
        function(declare, lang, AlfConstants, AlfCore, AlfRmActionBridge) {

   return declare(AlfCore, {

      constructor: function alfresco_rm_services_AlfRmActionService__constructor(args) {
         lang.mixin(this, args);
         this.alfSubscribe("ALF_RM_ADD_RELATIONSHIP", lang.hitch(this, this.onAddRelationship));
      },

      onAddRelationship: function alfresco_rm_services_AlfRmActionService__onAddRelationship(payload)
      {
         var asset = payload.asset,
           owner = payload.owner,
           site = asset.location.site.name;

         this.alfPublish("ALF_CREATE_FORM_DIALOG_REQUEST", {
            keepDialog: true,
            dialogTitle: this.message("label.title.new-relationship"),
            dialogConfirmationButtonTitle: this.message("label.button.create"),
            dialogCancellationButtonTitle: this.message("label.button.cancel"),
            formSubmissionTopic: "ALF_CRUD_CREATE",
            formSubmissionPayloadMixin: {
               url: "api/node/" + asset.nodeRef.replace("://", "/") + "/customreferences",
            },
            widgets: [{
               name: "alfresco/rm/lists/AlfRmRelationshipList",
               config: {
                  site: site,
                  currentData: {
                     items: [asset]
                  }
               }
            },{
               name: "alfresco/forms/controls/DojoSelect",
               config: {
                  name: "refId",
                  optionsConfig: {
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
               name: "alfresco/rm/forms/controls/AlfRmRecordPickerControl",
               config:
               {
                  name: "toNode",
                  site: site,
                  pickerRootNode: asset.node.rmNode.filePlan,
                  requirementConfig: {
                     initialValue: true
                  }
               }
            }]
         }, true);
      }
   });
});