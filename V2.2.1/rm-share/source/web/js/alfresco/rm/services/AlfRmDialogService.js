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
        "alfresco/dialogs/AlfDialogService",
        "rm/dialogs/AlfRmDialog"],
        function(declare, lang, AlfDialogService, AlfRmDialog) {

   return declare([AlfDialogService], {

      onCreateDialogRequest: function alfresco_rm_services_AlfRmDialogService__onCreateDialogRequest(payload)
      {
         if (this.dialog != null && !this.dialog.keepDialog)
         {
            this.dialog.destroyRecursive();
         }

         var dialogConfig = {
            title: this.message(payload.dialogTitle),
            textContent: payload.textContent,
            widgetsContent: payload.widgetsContent,
            widgetsButtons: payload.widgetsButtons,
            additionalCssClasses: payload.additionalCssClasses ? payload.additionalCssClasses : "",
            contentWidth: payload.contentWidth ? payload.contentWidth : null,
            contentHeight: payload.contentHeight ? payload.contentHeight : null,
            handleOverflow: (payload.handleOverflow != null) ? payload.handleOverflow: true,
            fixedWidth: (payload.fixedWidth != null) ? payload.fixedWidth: false
         };
         this.dialog = new AlfRmDialog(dialogConfig);

         if (payload.publishOnShow)
         {
            array.forEach(payload.publishOnShow, lang.hitch(this, this.publishOnShow));
         }
         this.dialog.show();

         if (payload.hideTopic)
         {
            this.alfSubscribe(payload.hideTopic, lang.hitch(this.dialog, this.dialog.hide));
         }
      },

      onCreateFormDialogRequest: function alfresco_rm_services_AlfRmDialogService__onCreateFormDialogRequest(payload)
      {
         // Destroy any previously created dialog...
         if (this.dialog != null && !this.dialog.keepDialog)
         {
            this.dialog.destroyRecursive();
         }

         if (payload.widgets == null)
         {
            this.alfLog("warn", "A request was made to display a dialog but no 'widgets' attribute has been defined", payload, this);
         }
         else if (payload.formSubmissionTopic == null)
         {
            this.alfLog("warn", "A request was made to display a dialog but no 'formSubmissionTopic' attribute has been defined", payload, this);
         }
         else
         {
            try
            {
               // Create a new pubSubScope just for this request (to allow multiple dialogs to behave independently)...
               var pubSubScope = this.generateUuid();
               var subcriptionTopic =  pubSubScope + this._formConfirmationTopic;
               this.alfSubscribe(subcriptionTopic, lang.hitch(this, this.onFormDialogConfirmation));

               // Take a copy of the default configuration and mixin in the supplied config to override defaults
               // as appropriate...
               var config = lang.clone(this.defaultFormDialogConfig);
               // NOTE: Ideally we'd like to avoid cloning the payload here in case it contains native code, however
               //       we need to ensure that pubSubScopes do not get baked into the payload object that will be re-used
               //       the next time the dialog is opened. We will need to explore alternative solutions in the 5.1 timeframe
               var clonedPayload = lang.clone(payload);
               lang.mixin(config, clonedPayload);
               config.pubSubScope = pubSubScope;
               config.parentPubSubScope = this.parentPubSubScope;
               config.subcriptionTopic = subcriptionTopic; // Include the subscriptionTopic in the configuration the subscription can be cleaned up

               // Construct the form widgets and then construct the dialog using that configuration...
               var formValue = (config.formValue != null) ? config.formValue: {};
               var formConfig = this.createFormConfig(config.widgets, formValue);
               var dialogConfig = this.createDialogConfig(config, formConfig);
               this.dialog = new AlfRmDialog(dialogConfig);
               this.dialog.show();
            }
            catch (e)
            {
               this.alfLog("error", "The following error occurred creating a dialog for defined configuration", e, this.dialogConfig, this);
            }
         }
      },

      createDialogConfig: function alfresco_rm_services_AlfRmDialogService__createDialogConfig(config, formConfig)
      {
         var dialogConfig = {
            title: this.message(config.dialogTitle),
            pubSubScope: config.pubSubScope, // Scope the dialog content so that it doesn't pollute any other widgets,,
            handleOverflow: (config.handleOverflow != null) ? config.handleOverflow: true,
            fixedWidth: (config.fixedWidth != null) ? config.fixedWidth: false,
            parentPubSubScope: config.parentPubSubScope,
            additionalCssClasses: config.additionalCssClasses ? config.additionalCssClasses : "",
            keepDialog: (config.keepDialog != null) ? config.keepDialog : false,
            widgetsContent: [formConfig],
            widgetsButtons: [{
               name: "alfresco/buttons/AlfButton",
               config: {
                  id: config.id,
                  label: config.dialogConfirmationButtonTitle,
                  disableOnInvalidControls: true,
                  publishTopic: this._formConfirmationTopic,
                  publishPayload: {
                     formSubmissionTopic: config.formSubmissionTopic,
                     formSubmissionPayloadMixin: config.formSubmissionPayloadMixin
                  }
               }
            },{
               name: "alfresco/buttons/AlfButton",
               config: {
                  label: config.dialogCancellationButtonTitle,
                  publishTopic: "ALF_CLOSE_DIALOG"
               }
            }]
         };
         return dialogConfig;
      }
   });
});