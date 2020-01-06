/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2020 Alfresco Software Limited
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
 * Disposition component.
 *
 * @namespace Alfresco
 * @class Alfresco.rm.component.Disposition
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * Disposition constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.rm.component.Disposition} The new component instance
    * @constructor
    */
   Alfresco.rm.component.Disposition = function Disposition_constructor(htmlId)
   {
      Alfresco.rm.component.Disposition.superclass.constructor.call(this, "Alfresco.rm.component.Disposition", htmlId, ["button", "container"]);
      YAHOO.Bubbling.on("metadataRefresh", this.doRefresh, this);
      return this;
   };

   /**
    * Extend Alfresco.component.Base
    */
   YAHOO.extend(Alfresco.rm.component.Disposition, Alfresco.component.Base);

   /**
    * Augment prototype with Actions modules
    */
   YAHOO.lang.augmentProto(Alfresco.rm.component.Disposition, Alfresco.doclib.Actions, true);
   YAHOO.lang.augmentProto(Alfresco.rm.component.Disposition, Alfresco.rm.doclib.Actions, true);

   /**
    * Augment prototype with main class implementation, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentObject(Alfresco.rm.component.Disposition.prototype,
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * The nodeRef to the object that owns the disposition schedule that is configured
          *
          * @property nodeRef
          * @type {string}
          */
         nodeRef: null,

         /**
          * The display name to the object that owns the disposition schedule that is configured
          *
          * @property displayName
          * @type {string}
          */
         displayName: null,

         /**
          * The siteId to the site that this disposition belongs to
          *
          * @property siteId
          * @type {string}
          */
         siteId: null,

         /**
          * The nodeRef for the dispostion schedule
          *
          * @property dipositionScheduleNodeRef
          * @type {string}
          */
         dipositionScheduleNodeRef: null
      },

      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function Disposition_onReady()
      {
         this.modules.actions = new Alfresco.module.DoclibActions();

         // Create buttons
         this.widgets.editPropertiesButton = Alfresco.util.createYUIButton(this, "editproperties-button", this.onEditPropertiesButtonClick);
         this.widgets.editScheduleButton = Alfresco.util.createYUIButton(this, "editschedule-button", this.onEditScheduleButtonClick);
         this.widgets.createScheduleButton = Alfresco.util.createYUIButton(this, "createschedule-button", this.onCreateScheduleButtonClick);

         // Add listeners that displays/hides the description
         var actionsEl = Dom.get(this.id + "-actions"),
            actionEls = Dom.getElementsByClassName("action", "div", actionsEl),
            actionEl, more, a, description;
         
         for (var i = 0, ii = actionEls.length; i < ii; i++)
         {
            actionEl = actionEls[i];
            more = Dom.getElementsByClassName("more", "div", actionEl)[0];
            a = document.getElementsByTagName("a", more)[0];

            if (a)
            {
               description = Dom.getElementsByClassName("description", "div", actionEl)[0];
               Event.addListener(more, "click", function (event, obj)
               {
                  if (obj.description && Dom.hasClass(obj.more, "collapsed"))
                  {
                     Alfresco.util.Anim.fadeIn(obj.description);
                     Dom.removeClass(obj.more, "collapsed");
                     Dom.addClass(obj.more, "expanded");
                  }
                  else
                  {
                     Dom.setStyle(obj.description, "display", "none");
                     Dom.removeClass(obj.more, "expanded");
                     Dom.addClass(obj.more, "collapsed");
                  }
               },
               {
                  more: more,
                  description: description
               }, this);
            }
         }
      },

      /**
       * Fired when the user clicks the edit properties button.
       * Takes the user to the edit page.
       *
       * @method onEditPropertiesButtonClick
       * @param event {object} a "click" event
       */
      onEditPropertiesButtonClick: function Disposition_onEditPropertiesButtonClick(event)
      {
         // Disable buttons to avoid double submits or cancel during post
         this.widgets.editPropertiesButton.set("disabled", true);

         // Send the user to the edit proprties page
         document.location.href = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/edit-metadata?nodeRef=" + this.options.dipositionScheduleNodeRef;
      },

      /**
       * Fired when the user clicks the edit schedule button.
       * Takes the user to the edit page.
       *
       * @method onEditScheduleButtonClick
       * @param event {object} a "click" event
       */
      onEditScheduleButtonClick: function Disposition_onEditScheduleButtonClick(event)
      {
         // Disable buttons to avoid double submits or cancel during post
         this.widgets.editScheduleButton.set("disabled", true);

         // Send the user to the edit schedule page
         document.location.href = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/rm-disposition-edit?nodeRef=" + this.options.nodeRef;
      },

      /**
       * Fired when the user clicks the create schedule button.
       * Creates the schedule and refreshes itself
       *
       * @method onCreateScheduleButtonClick
       * @param event {object} a "click" event
       */
      onCreateScheduleButtonClick: function Disposition_onCreateScheduleButtonClick(event)
      {
         // Disable buttons to avoid double submits or cancel during post
         this.widgets.createScheduleButton.set("disabled", true);

         this.onActionCreateDisposition(
         {
            nodeRef: this.options.nodeRef,
            displayName: this.options.displayName
         });
      },

      doRefresh: function()
      {
         YAHOO.Bubbling.unsubscribe("metadataRefresh", this.doRefresh, this);

         var url = 'rm/components/fileplan/disposition?nodeRef={nodeRef}' + (this.options.siteId ? '&site={siteId}' :  '');

         this.refresh(url);
      }

   }, true);

})();
