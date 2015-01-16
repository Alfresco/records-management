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

define(["dojo/_base/declare",
        "alfresco/services/CrudService"],
        function(declare, CrudService) {

   return declare([CrudService], {

      onCreate: function alfresco_rm_services_AlfRmCrudService__onCreate(payload) {
         var url = this.getUrlFromPayload(payload);
         this.serviceXhr({url: url,
                          data: this.clonePayload(payload),
                          method: "POST",
                          successMessage: payload.successMessage,
                          failureMessage: payload.failureMessage,
                          alfTopic: payload.alfResponseTopic,
                          successCallback: this.refreshRequest,
                          failureCallback: this.failureCallback,
                          callbackScope: this});
      },

      onUpdate: function alfresco_rm_services_AlfRmCrudService__onUpdate(payload) {
         var url = this.getUrlFromPayload(payload);
         this.serviceXhr({url: url,
                          data: this.clonePayload(payload),
                          method: "PUT",
                          successMessage: payload.successMessage,
                          failureMessage: payload.failureMessage,
                          alfTopic: payload.alfResponseTopic,
                          successCallback: this.refreshRequest,
                          failureCallback: this.failureCallback,
                          callbackScope: this});
      },

      requestDeleteConfirmation: function alfresco_rm_services_AlfRmCrudService__requestDeleteConfirmation(url, payload) {
         var responseTopic = this.generateUuid();
         this._deleteHandle = this.alfSubscribe(responseTopic, lang.hitch(this, this.onDeleteConfirmation), true);

         var title = (payload.confirmationTitle) ? payload.confirmationTitle : this.message("crudservice.generic.delete.title");
         var prompt = (payload.confirmationPrompt) ? payload.confirmationPrompt : this.message("crudservice.generic.delete.prompt");
         var confirmButtonLabel = (payload.confirmationButtonLabel) ? payload.confirmationButtonLabel : this.message("crudservice.generic.delete.confirmationButtonLabel");
         var cancelButtonLabel = (payload.cancellationButtonLabel) ? payload.cancellationButtonLabel : this.message("crudservice.generic.delete.cancellationButtonLabel");

         var dialog = new AlfDialog({
            generatePubSubScope: false,
            title: title,
            textContent: prompt,
            widgetsButtons: [
               {
                  name: "alfresco/buttons/AlfButton",
                  config: {
                     label: confirmButtonLabel,
                     publishTopic: responseTopic,
                     publishPayload: {
                        url: url,
                        pubSubScope: payload.pubSubScope,
                        responseTopic: payload.responseTopic,
                        successMessage: payload.successMessage,
                        failureMessage: payload.failureMessage
                     }
                  }
               },
               {
                  name: "alfresco/buttons/AlfButton",
                  config: {
                     label: cancelButtonLabel,
                     publishTopic: "close"
                  }
               }
            ]
         });
         dialog.show();
      },

      performDelete: function alfresco_rm_services_AlfRmCrudService__performDelete(url, payload) {
         this.serviceXhr({url: url,
                          method: "DELETE",
                          data: this.clonePayload(payload),
                          alfTopic: payload.responseTopic,
                          successMessage: payload.successMessage,
                          successCallback: this.refreshRequest,
                          failureMessage: payload.failureMessage,
                          failureCallback: this.failureCallback,
                          callbackScope: this});
      },

      failureCallback: function alfresco_rm_services_AlfRmCrudService__failureCallback(response, originalRequestConfig)
      {
         this.alfPublish("ALF_DISPLAY_PROMPT", {
            message: JSON.parse(response.response.data).message
         });
      }
   });
});