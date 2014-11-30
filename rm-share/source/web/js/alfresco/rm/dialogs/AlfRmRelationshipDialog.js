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

      postCreate: function alfresco_rm_dialogs_AlfRmRelationshipDialog__postCreate()
      {
         this.inherited(arguments);
         this.alfSubscribe("ALF_ITEMS_SELECTED", lang.hitch(this, "onItemsSelected"), true);
         this.alfSubscribe("ALF_RECORD_SELECTED", lang.hitch(this, "onRecordSelected"), true);
         this.alfSubscribe("ALF_RECORD_REMOVED", lang.hitch(this, "onRecordRemoved"), true);
      },

      onItemsSelected: function alfresco_rm_dialogs_AlfRmRelationshipDialog__onItemsSelected(payload)
      {
         this.selectedItem = payload.pickedItems;
      },

      onRecordSelected: function alfresco_rm_dialogs_AlfRmRelationshipDialog__onRecordSelected(payload)
      {
         this.processWidgets([{
            name: "alfresco/rm/lists/AlfRmRelationshipList",
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
         }], domConstruct.create("div", {id: "alfresco_rm_dialogs_AlfRmRelationshipDialog"}, this.bodyNode, "last"));
      },

      onRecordRemoved: function alfresco_rm_dialogs_AlfRmRelationshipDialog__onRecordRemoved(payload)
      {
         domConstruct.destroy("alfresco_rm_dialogs_AlfRmRelationshipDialog");
         this.selectedItem = null;
      }
   });
});
