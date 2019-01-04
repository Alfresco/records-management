/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2019 Alfresco Software Limited
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
       Event = YAHOO.util.Event,
       KeyListener = YAHOO.util.KeyListener;

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
         Alfresco.rm.module.CreateSite.superclass.onTemplateLoaded.call(this, response);
         Event.removeListener(this.widgets.panel.close, "click");
         Event.addListener(this.widgets.panel.close, "click", this.onCancelButtonClick, this, true);
      },

      /**
       * Helper method to disable certain form elements like Name, URL Name and Visibility
       *
       * @method disableFormElements
       */
      disableFormElements: function RM_CreateSite_disableFormElements()
      {
         Dom.get(this.id + "-title").disabled = true;
         Dom.get(this.id + "-shortName").disabled = true;
         Dom.get(this.id + "-isPrivate").disabled = true;
         Dom.get(this.id + "-isModerated").disabled = true;
         Dom.get(this.id + "-compliance-field").hidden = false;
      },

      /**
       * Helper method to enable certain form elements like Name, URL Name and Visibility
       *
       * @method enableFormElements
       */
      enableFormElements: function RM_CreateSite_enableFormElements()
      {
         Dom.get(this.id + "-title").disabled = false;
         Dom.get(this.id + "-shortName").disabled = false;
         Dom.get(this.id + "-isPrivate").disabled = false;
         Dom.get(this.id + "-isModerated").disabled = false;
         Dom.get(this.id + "-compliance-field").hidden = true;
      },

      /**
       * Overrides the doBeforeSubmit from the base class
       *
       * @method doBeforeFormSubmit
       * @param form {HTMLFormElement} The create site form
       * @param obj {Object} Callback object
       */
      doBeforeFormSubmit: function RM_CreateSite_doBeforeFormSubmit(form, obj)
      {
         var sitePresetEl = Dom.get(this.id + "-sitePreset");
         if (sitePresetEl.value === "rm-site-dashboard")
         {
            Dom.get(this.id + "-type").value = Dom.get(this.id + "-compliance").value;
         }
         else
         {
            Dom.get(this.id + "-type").value = "{http://www.alfresco.org/model/site/1.0}site";
         }

         Alfresco.rm.module.CreateSite.superclass.doBeforeFormSubmit.call(this, form, obj);
         this.enableFormElements();
      },

      /**
       * Overrides the onCancelButtonClick from the base class
       *
       * @method onCancelButtonClick
       * @param type
       * @param args
       */
      onCancelButtonClick: function RM_CreateSite_onCancelButtonClick(type, args)
      {
         Alfresco.rm.module.CreateSite.superclass.onCancelButtonClick.call(this, type, args);
         this.enableFormElements();
      },

      /**
       * Overrides the onCreateSiteFailure from the base class
       *
       * @method onCreateSiteFailure
       * @param response
       */
      onCreateSiteFailure: function RM_CreateSite_onCreateSiteFailure(response)
      {
         Alfresco.rm.module.CreateSite.superclass.onCreateSiteFailure.call(this, response);

         var sitePresetEl = Dom.get(this.id + "-sitePreset");
         if (sitePresetEl.value === "rm-site-dashboard")
         {
            this.disableFormElements();
         }
         else
         {
            this.enableFormElements();
         }
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
            Dom.get(this.id + "-form").appendChild(input);
         }

         if (sitePreset === "rm-site-dashboard")
         {
            Dom.get(this.id + "-shortName").value = "rm";
            Dom.get(this.id + "-title").value = this.msg("title.recordsManagementSite");
            Dom.get(this.id + "-description").value = this.msg("description.recordsManagementSite");
            Dom.get(this.id + "-isPublic").checked = true;
            Dom.get(this.id + "-isModerated").checked = false;

            this.disableFormElements();
         }
         else
         {
            Dom.get(this.id + "-shortName").value = "";
            Dom.get(this.id + "-title").value = "";
            Dom.get(this.id + "-description").value = "";

            this.enableFormElements();
         }
      }
   });
})();

Alfresco.module.getCreateSiteInstance = function()
{
   var instanceId = "alfresco-rm-createSite-instance";
   return Alfresco.util.ComponentManager.get(instanceId) || new Alfresco.rm.module.CreateSite(instanceId);
};
