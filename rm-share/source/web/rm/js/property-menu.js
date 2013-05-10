/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
 * RM Property Selector Menu Component
 *
 * @namespace Alfresco
 * @class Alfresco.rm.component.PropertyMenu
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
    * RMPropertyMenu constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.rm.component.PropertyMenu} The new component instance
    * @constructor
    */
   Alfresco.rm.component.PropertyMenu = function RMPropertyMenu_constructor(htmlId)
   {
      Alfresco.rm.component.PropertyMenu.superclass.constructor.call(this, "Alfresco.rm.component.PropertyMenu", htmlId, ["button", "container", "menu"]);
      return this;
   };

   YAHOO.extend(Alfresco.rm.component.PropertyMenu, Alfresco.component.Base,
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
          * Flag indicating whether search related fields are visible or not.
          *
          * @property showSearchFields
          * @type boolean
          */
         showSearchFields: false,

         /**
          * Flag indicating whether special type related fields are visible or not.
          *
          * @property showSpecialTypeFields
          * @type boolean
          */
         showSpecialTypeFields: true,

         /**
          * Flag indicating whether IMAP related fields are visible or not.
          *
          * @property showIMAPFields
          * @type boolean
          */
         showIMAPFields: false,

         /**
          * Flag indicating whether Record Identifier field is visible or not.
          *
          * @property showIdentiferField
          * @type boolean
          */
         showIdentiferField: false,

         /**
          * Flag indicating whether special 'All' field is visible or not.
          *
          * @property showAllField
          * @type boolean
          */
         showAllField: false,

         /**
          * Flag passed to YUI menu constructor whether to wait for first display to render menu.
          *
          * @property lazyLoadMenu
          * @type boolean
          */
         lazyLoadMenu: true,

         /**
          * Flag indicating whether the menu button should update the label to mirror the selected item text.
          *
          * @property updateButtonLabel
          * @type boolean
          */
         updateButtonLabel: true,

         /**
          * Groups of custom properties
          *
          * @property group
          * @type Array
          */
         groups: []
      },

      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function RMPropertyMenu_onReady()
      {
         var items = [];

         if (this.options.showSearchFields)
         {
            // add content fields
            items.push(
            {
               text: this.msg("label.menu.content"),
               submenu:
               {
                  id: this.id + "_content",
                  itemdata:
                  [
                     { text: this.msg("label.name"), value: "cm:name" },
                     { text: this.msg("label.title"), value: "cm:title" },
                     { text: this.msg("label.description"), value: "cm:description" },
                     { text: this.msg("label.creator"), value: "cm:creator" },
                     { text: this.msg("label.created"), value: "cm:created" },
                     { text: this.msg("label.modifier"), value: "cm:modifier" },
                     { text: this.msg("label.modified"), value: "cm:modified" },
                     { text: this.msg("label.author"), value: "cm:author" }
                  ]
               }
            });

            // add record fields
            items.push(
            {
               text: this.msg("label.menu.records"),
               submenu:
               {
                  id: this.id + "_records",
                  itemdata:
                  [
                     { text: this.msg("label.originator"), value: "rma:originator" },
                     { text: this.msg("label.dateFiled"), value: "rma:dateFiled" },
                     { text: this.msg("label.publicationDate"), value: "rma:publicationDate" },
                     { text: this.msg("label.reviewDate"), value: "rma:reviewAsOf" },
                     { text: this.msg("label.originatingOrganization"), value: "rma:originatingOrganization" },
                     { text: this.msg("label.mediaType"), value: "rma:mediaType" },
                     { text: this.msg("label.format"), value: "rma:format" },
                     { text: this.msg("label.dateReceived"), value: "rma:dateReceived" },
                     { text: this.msg("label.location"), value: "rma:location" },
                     { text: this.msg("label.address"), value: "rma:address" },
                     { text: this.msg("label.otherAddress"), value: "rma:otherAddress" },
                     { text: this.msg("label.supplementalMarkingList"), value: "rmc:supplementalMarkingList" }
                  ]
               }
            });
         }
         else
         {
            // add content fields
            items.push(
            {
               text: this.msg("label.menu.content"),
               submenu:
               {
                  id: this.id + "_content",
                  itemdata:
                  [
                     { text: this.msg("label.name"), value: "cm:name" },
                     { text: this.msg("label.title"), value: "cm:title" },
                     { text: this.msg("label.description"), value: "cm:description" },
                     { text: this.msg("label.creator"), value: "cm:creator" },
                     { text: this.msg("label.created"), value: "cm:created" },
                     { text: this.msg("label.modifier"), value: "cm:modifier" },
                     { text: this.msg("label.modified"), value: "cm:modified" },
                     { text: this.msg("label.author"), value: "cm:author" }
                  ]
               }
            });

            // add record fields
            items.push(
            {
               text: this.msg("label.menu.records"),
               submenu:
               {
                  id: this.id + "_records",
                  itemdata:
                  [
                     { text: this.msg("label.originator"), value: "rma:originator" },
                     { text: this.msg("label.dateFiled"), value: "rma:dateFiled" },
                     { text: this.msg("label.publicationDate"), value: "rma:publicationDate" },
                     { text: this.msg("label.reviewDate"), value: "rma:reviewAsOf" },
                     { text: this.msg("label.originatingOrganization"), value: "rma:originatingOrganization" },
                     { text: this.msg("label.mediaType"), value: "rma:mediaType" },
                     { text: this.msg("label.format"), value: "rma:format" },
                     { text: this.msg("label.dateReceived"), value: "rma:dateReceived" },
                     { text: this.msg("label.location"), value: "rma:location" },
                     { text: this.msg("label.address"), value: "rma:address" },
                     { text: this.msg("label.otherAddress"), value: "rma:otherAddress" },
                     { text: this.msg("label.supplementalMarkingList"), value: "rmc:supplementalMarkingList" }
                  ]
               }
            });
         }

         if (this.options.showAllField)
         {
            items.splice(0, 0,
            {
               text: this.msg("label.all"),
               value: "ALL"
            });
         }

         if (this.options.showIdentiferField)
         {
            // insert RMA Identifer field
            items[1].submenu.itemdata.splice(0, 0,
            {
               text: this.msg("label.identifier"),
               value: "rma:identifier"
            });
         }

         if (this.options.showSearchFields)
         {
            // insert KEYWORDS special search field
            items[0].submenu.itemdata.splice(0, 0,
            {
               text: this.msg("label.keywords"),
               value: "keywords"
            });

            // insert search roll-up special field menu
            items.push(
            {
               text: this.msg("label.menu.disposition"),
               submenu:
               {
                  id: this.id + "_disposition",
                  itemdata:
                  [
                     { text: this.msg("label.dispositionEvents"), value: "rma:recordSearchDispositionEvents" },
                     { text: this.msg("label.dispositionActionName"), value: "rma:recordSearchDispositionActionName" },
                     { text: this.msg("label.dispositionActionAsOf"), value: "rma:recordSearchDispositionActionAsOf" },
                     { text: this.msg("label.dispositionEventsEligible"), value: "rma:recordSearchDispositionEventsEligible" },
                     { text: this.msg("label.dispositionPeriod"), value: "rma:recordSearchDispositionPeriod" },
                     { text: this.msg("label.hasDispositionSchedule"), value: "rma:recordSearchHasDispositionSchedule" },
                     { text: this.msg("label.dispositionInstructions"), value: "rma:recordSearchDispositionInstructions" },
                     { text: this.msg("label.dispositionAuthority"), value: "rma:recordSearchDispositionAuthority" },
                     { text: this.msg("label.holdReason"), value: "rma:recordSearchHoldReason" },
                     { text: this.msg("label.vitalRecordReviewPeriod"), value: "rma:recordSearchVitalRecordReviewPeriod" }
                  ]
               }
            });
         }

         if (this.options.showIMAPFields)
         {
            // insert IMAP field menu
            items.push(
            {
               text: this.msg("label.menu.imap"),
               submenu:
               {
                  id: this.id + "_imap",
                  itemdata:
                  [
                     { text: this.msg("label.imap.threadIndex"), value: "imap:threadIndex" },
                     { text: this.msg("label.imap.messageFrom"), value: "imap:messageFrom" },
                     { text: this.msg("label.imap.messageTo"), value: "imap:messageTo" },
                     { text: this.msg("label.imap.messageCc"), value: "imap:messageCc" },
                     { text: this.msg("label.imap.messageSubject"), value: "imap:messageSubject" },
                     { text: this.msg("label.imap.dateReceived"), value: "imap:dateReceived" },
                     { text: this.msg("label.imap.dateSent"), value: "imap:dateSent" }
                  ]
               }
            });
         }

         // Add groups of properties to the property menu
         for (var i=0, j=this.options.groups.length; i<j; i++)
         {
            var group = this.options.groups[i];

            var newMenuItem =
            {
               text: group.label,
               submenu:
               {
                  id: this.id + "_" + group.id,
                  itemdata: []
               }
            };

            if (group.properties.length != 0)
            {
               var itemdata = newMenuItem.submenu.itemdata;
               for (var l=0, k=group.properties.length; l<k; l++)
               {
                  var prop = group.properties[l];
                  itemdata.push(
                  {
                     text: $html(prop.label),
                     value: prop.prefix + ":" + prop.name
                  });
               }
               items.push(newMenuItem);
            }
         }

         // menu button widget setup
         this.widgets.menubtn = new YAHOO.widget.Button(this.id,
         {
            type: "menu",
            menu: items,
            lazyloadmenu: this.options.lazyLoadMenu
         });

         this.widgets.menubtn.getMenu().subscribe("click", function(e, args)
         {
            var menuItem = args[1];

            // only process if an actual menu item (not a header) has been selected
            if (menuItem.value)
            {
               var label = menuItem.cfg.getProperty("text");
               if (this.options.updateButtonLabel)
               {
                  // update menu button label
                  this.widgets.menubtn.set("label", label);
               }

               // fire event so page component can deal with it
               YAHOO.Bubbling.fire("PropertyMenuSelected",
               {
                  value: menuItem.value,
                  label: label
               });
            }
            else
            {
               if (this.options.updateButtonLabel)
               {
                  // reset menu button label
                  this.widgets.menubtn.set("label", this.msg("message.select"));
               }
            }
         }, this, true);
      }
   });
})();