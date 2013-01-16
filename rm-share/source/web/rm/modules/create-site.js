/**
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
 * RM CreateSite module
 *
 * Overrides/adds methods so that an RM site can be created from the create site dialog
 *
 * @namespace Alfresco.rm.module
 * @class Alfresco.rm.module.CreateSite
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event;

   /**
    * RM CreateSite module constructor.
    *
    * @param containerId {string} A unique id for this component
    * @return {Alfresco.rm.module.CreateSite} The new rm create site instance
    * @constructor
    */
   Alfresco.rm.module.CreateSite = function RM_CreateSite_constructor(containerId)
   {
      Alfresco.rm.module.CreateSite.superclass.constructor.call(this, containerId);
      return this;
   };

   YAHOO.extend(Alfresco.rm.module.CreateSite, Alfresco.module.CreateSite,
   {
      /**
       * Overrides the onTemplateLoaded from the base class.
       *
       * Called when the CreateSite html template has been returned from the server.
       * Creates the YUI gui objects such as buttons and a panel and shows it.
       *
       * @method onTemplateLoaded
       * @param response {object} An Alfresco.util.Ajax.request response object
       */
      onTemplateLoaded: function RM_CreateSite_onTemplateLoaded(response)
      {
         // Inject the template from the XHR request into a new DIV element
         var containerDiv = document.createElement("div");
         containerDiv.innerHTML = response.serverResponse.responseText;

         // The panel is created from the HTML returned in the XHR request, not the container
         var panelDiv = Dom.getFirstChild(containerDiv);

         this.widgets.panel = Alfresco.util.createYUIPanel(panelDiv);
         Event.removeListener(this.widgets.panel.close, "click");
         Event.addListener(this.widgets.panel.close, "click", this.onCancelButtonClick, this, true);

         // Create the cancel button
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel-button", this.onCancelButtonClick);

         // Create the ok button, the forms runtime will handle when its clicked
         this.widgets.okButton = Alfresco.util.createYUIButton(this, "ok-button", null,
         {
            type: "submit"
         });

         // Site access form controls
         this.widgets.siteVisibility = Dom.get(this.id + "-visibility");
         this.widgets.isPublic = Dom.get(this.id + "-isPublic");
         this.widgets.isModerated = Dom.get(this.id + "-isModerated");
         this.widgets.isPrivate = Dom.get(this.id + "-isPrivate");

         // Make sure we disable moderated if public isn't selected
         Event.addListener(this.widgets.isPublic, "focus", this.onVisibilityChange, this.widgets.isPublic, this);
         Event.addListener(this.widgets.isPrivate, "focus", this.onVisibilityChange, this.widgets.isPrivate, this);

         // Configure the forms runtime
         var createSiteForm = new Alfresco.forms.Form(this.id + "-form");

         // Balloon validation messages
         this.widgets.balloons = {};

         var elTitle = Dom.get(this.id + "-title"),
            elShortName = Dom.get(this.id + "-shortName");

         /**
          * Title field
          */
         // Title is mandatory
         createSiteForm.addValidation(elTitle, Alfresco.forms.validation.mandatory, null, "keyup", this.msg("validation-hint.mandatory"));
         // ...and has a maximum length
         createSiteForm.addValidation(elTitle, Alfresco.forms.validation.length,
         {
            max: 256,
            crop: true
         }, "keyup");

         // Auto-generate a short name as long as the user hasn't manually entered one first
         Event.addListener(elTitle, "keyup", function CreateSite_title_keyUp()
         {
            if (!this.shortNameEdited)
            {
               elShortName.value = this.safeURL(elTitle.value).substring(0, 72);
            }
         }, this, true);

         this.widgets.balloons[this.id + "-title"] = Alfresco.util.createBalloon(elTitle);

         // Remove the balloon after the text box has lost focus. This prevents multiple validation balloons overlapping.
         Event.addListener(elTitle, "blur", function CreateSite_title_blur()
         {
            if (this.widgets.balloons[this.id + "-title"])
            {
               this.widgets.balloons[this.id + "-title"].hide();
            }
         }, this, true);

         /**
          * Short name field
          */
         this.shortNameEdited = false;

         // Shortname is mandatory
         createSiteForm.addValidation(elShortName, Alfresco.forms.validation.mandatory, null, "keyup", this.msg("validation-hint.mandatory"));
         // ...and is restricted to a limited set of characters
         createSiteForm.addValidation(elShortName, Alfresco.forms.validation.regexMatch,
         {
            pattern: /^[ ]*[0-9a-zA-Z\-]+[ ]*$/
         }, "keyup", this.msg("validation-hint.siteName"));
         // ...and has a maximum length
         createSiteForm.addValidation(elShortName, Alfresco.forms.validation.length,
         {
            max: 72,
            crop: true
         }, "keyup");

         // Flag that the user has edited the short name
         Event.addListener(elShortName, "keyup", function CreateSite_shortName_keyUp()
         {
            this.shortNameEdited = elShortName.value.length > 0;
         }, this, true);

         this.widgets.balloons[this.id + "-shortName"] = Alfresco.util.createBalloon(elShortName);

         // Remove the balloon after the text box has lost focus. This prevents multiple validation balloons overlapping.
         Event.addListener(elShortName, "blur", function CreateSite_shortName_blur()
         {
            if (this.widgets.balloons[this.id + "-shortName"])
            {
               this.widgets.balloons[this.id + "-shortName"].hide();
            }
         }, this, true);

         /**
          * Description field
          */
         // Description kept to a reasonable length
         createSiteForm.addValidation(this.id + "-description", Alfresco.forms.validation.length,
         {
            max: 512,
            crop: true
         }, "keyup");

         var sitePresetEl = Dom.get(this.id + "-sitePreset");
         Event.addListener(sitePresetEl, "change", function CreateSite_sitePreset_change()
         {
            this.onSitePresetChange(sitePresetEl.options[sitePresetEl.selectedIndex].value);
         }, this, true);

         // Override Forms Runtime's error handling
         var scope = this;
         createSiteForm.addError = function CreateSite_form_addError(msg, field)
         {
            if (scope.widgets.panel.cfg.getProperty("visible"))
            {
               var balloon = scope.widgets.balloons[field.id];
               if (balloon)
               {
                  balloon.html(msg);
                  balloon.show();
               }
            }
         };

         // The ok button is the submit button, and it should be enabled when the form is ready
         createSiteForm.setShowSubmitStateDynamically(true, true);
         createSiteForm.setSubmitElements(this.widgets.okButton);
         createSiteForm.doBeforeFormSubmit =
         {
            fn: function()
            {
               var formEl = Dom.get(this.id + "-form");
               formEl.attributes.action.nodeValue = Alfresco.constants.URL_SERVICECONTEXT + "modules/create-site";

               this.widgets.okButton.set("disabled", true);
               this.widgets.cancelButton.set("disabled", true);

               // Site access
               var siteVisibility = "PUBLIC";
               if (this.widgets.isPublic.checked)
               {
                  if (this.widgets.isModerated.checked)
                  {
                     siteVisibility = "MODERATED";
                  }
               }
               else
               {
                  siteVisibility = "PRIVATE";
               }
               this.widgets.siteVisibility.value = siteVisibility;

               this.widgets.panel.hide();

               Dom.get(this.id + "-title").disabled = false;
               Dom.get(this.id + "-shortName").disabled = false;

               this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
               {
                  text: Alfresco.util.message("message.creating", this.name),
                  spanClass: "wait",
                  displayTime: 0
               });
            },
            obj: null,
            scope: this
         };

         // Submit as an ajax submit (not leave the page), in json format
         createSiteForm.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onCreateSiteSuccess,
               scope: this
            },
            failureCallback:
            {
               fn: this.onCreateSiteFailure,
               scope: this
            }
         });
         createSiteForm.setSubmitAsJSON(true);
         // We're in a popup, so need the tabbing fix
         createSiteForm.applyTabFix();
         createSiteForm.init();

         // Show the panel
         this._showPanel();
      },

      /**
       * Called when a preset has been selected.
       *
       * @method onSitePresetChange
       * @param sitePreset {string} Site preset
       */
      onSitePresetChange: function RM_CreateSite_onSitePresetChange(sitePreset)
      {
         var input = Dom.get(this.id + "-type");
         if (!input)
         {
            input = document.createElement("input");
            input.setAttribute("type", "hidden");
            input.setAttribute("name", "type");
            input.setAttribute("id", this.id + "-type");
            input.setAttribute("value", "{http://www.alfresco.org/model/recordsmanagement/1.0}rmsite");
            Dom.get(this.id + "-form").appendChild(input);
         }

         if (sitePreset === "rm-site-dashboard")
         {
            Dom.get(this.id + "-shortName").value = "rm";
            Dom.get(this.id + "-title").value = this.msg("title.recordsManagementSite");
            Dom.get(this.id + "-description").value = this.msg("description.recordsManagementSite");
            Dom.get(this.id + "-title").disabled = true;
            Dom.get(this.id + "-shortName").disabled = true;
            this.widgets.okButton.set("disabled", false);
         }
         else
         {
            input.setAttribute("disabled", true);
            Dom.get(this.id + "-shortName").value = "";
            Dom.get(this.id + "-title").value = "";
            Dom.get(this.id + "-description").value = "";
            Dom.get(this.id + "-title").disabled = false;
            Dom.get(this.id + "-shortName").disabled = false;
            this.widgets.okButton.set("disabled", true);
         }
      },

      /**
       * Called when user clicks on the cancel button.
       * Resets the form fields and closes the CreateSite panel.
       *
       * @method onCancelButtonClick
       * @param type
       * @param args
       */
      onCancelButtonClick: function RM_CreateSite_onCancelButtonClick(type, args)
      {
         Alfresco.rm.module.CreateSite.superclass.onCancelButtonClick.call(this, type, args);
         // Reset the form fields
         try
         {
            Dom.get(this.id + "-sitePreset").selectedIndex = 0;
            this.widgets.okButton.set("disabled", true);
            Dom.get(this.id + "-title").disabled = false;
            Dom.get(this.id + "-shortName").disabled = false;
         }
         catch(e)
         {
         }
      },

      /**
       * Called when a site failed to be created.
       *
       * @method onCreateSiteFailure
       * @param response
       */
      onCreateSiteFailure: function RM_CreateSite_onCreateSiteFailure(response)
      {
         Alfresco.rm.module.CreateSite.superclass.onCreateSiteFailure.call(this, response);
         if (response.config.dataObj.sitePreset === "rm-site-dashboard")
         {
            Dom.get(this.id + "-title").disabled = true;
            Dom.get(this.id + "-shortName").disabled = true;
         }
      }
   });
})();

Alfresco.module.getCreateSiteInstance = function()
{
   var instanceId = "alfresco-rm-createSite-instance";
   return Alfresco.util.ComponentManager.get(instanceId) || new Alfresco.rm.module.CreateSite(instanceId);
};