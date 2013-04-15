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
       * Overrides the doBeforeSubmit from the base class
       *
       * @method doBeforeFormSubmit
       * @param form {HTMLFormElement} The create site form
       * @param obj {Object} Callback object
       */
      doBeforeFormSubmit: function RM_CreateSite_doBeforeFormSubmit(form, obj)
      {
         Alfresco.rm.module.CreateSite.superclass.doBeforeFormSubmit.call(this, form, obj);

         Dom.get(this.id + "-title").disabled = false;
         Dom.get(this.id + "-shortName").disabled = false;
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

         Dom.get(this.id + "-title").disabled = false;
         Dom.get(this.id + "-shortName").disabled = false;
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

         if (response.config.dataObj.sitePreset === "rm-site-dashboard")
         {
            Dom.get(this.id + "-title").disabled = true;
            Dom.get(this.id + "-shortName").disabled = true;
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
            Dom.get(this.id + "-title").disabled = true;
            Dom.get(this.id + "-shortName").disabled = true;
            Dom.get(this.id + "-type").value = "{http://www.alfresco.org/model/recordsmanagement/1.0}rmsite";

            this.widgets.okButton.set("disabled", false);
         }
         else
         {
            Dom.get(this.id + "-shortName").value = "";
            Dom.get(this.id + "-title").value = "";
            Dom.get(this.id + "-description").value = "";
            Dom.get(this.id + "-title").disabled = false;
            Dom.get(this.id + "-shortName").disabled = false;
            Dom.get(this.id + "-type").value = "{http://www.alfresco.org/model/site/1.0}site";

            this.widgets.okButton.set("disabled", true);
         }
      }
   });
})();

Alfresco.module.getCreateSiteInstance = function()
{
   var instanceId = "alfresco-rm-createSite-instance";
   return Alfresco.util.ComponentManager.get(instanceId) || new Alfresco.rm.module.CreateSite(instanceId);
};