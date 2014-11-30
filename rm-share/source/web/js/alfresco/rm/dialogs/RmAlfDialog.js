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
        "dojo/dom-construct",
        "alfresco/dialogs/AlfDialog"],
        function(declare, lang, domConstruct, AlfDialog) {

   return declare([AlfDialog], {

      keepDialog: false,

      selectedItem: null,

      postCreate: function alfresco_dialogs_RmAlfDialog__postCreate() {
         this.inherited(arguments);
         this.alfSubscribe("ALF_ITEMS_SELECTED", lang.hitch(this, "onItemsSelected"), true);
         this.alfSubscribe("ALF_RECORD_SELECTED", lang.hitch(this, "onRecordSelected"), true);
         this.alfSubscribe("ALF_RECORD_REMOVED", lang.hitch(this, "onRecordRemoved"), true);
      },

      onItemsSelected: function alfresco_dialogs_RmAlfDialog__onItemsSelected(payload) {
         this.selectedItem = payload.pickedItems;
      },

      onRecordSelected: function alfresco_dialogs_RmAlfDialog__onRecordSelected(payload) {
         this.processWidgets([{
            name: "alfresco/rm/relationship/RmRelationshipItem",
            config: {
               showDeleteAction: true,
               site: payload.site,
               currentData: {
                  items: this.selectedItem
               }
            }
         },{
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "toNode",
               value: this.selectedItem[0].nodeRef,
               visibilityConfig: {
                  initialValue: false
               }
            }
         }], domConstruct.create("div", {}, this.bodyNode, "last"));
      },

      onRecordRemoved: function alfresco_dialogs_RmAlfDialog__onRecordRemoved(payload)
      {
         this.bodyNode.lastElementChild.innerHTML = "";
         this.selectedItem = null;
      }
   });
});
