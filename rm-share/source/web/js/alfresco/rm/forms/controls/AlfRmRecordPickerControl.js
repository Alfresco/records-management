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
        "dojo/dom",
        "dojo/dom-construct",
        "alfresco/core/CoreWidgetProcessing",
        "alfresco/forms/controls/BaseFormControl"],
        function(declare, lang, dom, domConstruct, CoreWidgetProcessing, BaseFormControl) {

   return declare([CoreWidgetProcessing, BaseFormControl], {

      pickerRootNode: null,

      selectedItem: null,

      site: null,

      i18nRequirements: [{i18nFile: "./i18n/AlfRmRecordPickerControl.properties"}],

      constructor: function alfresco_forms_controls_BaseFormControl__constructor(args) {
         declare.safeMixin(this, args);

         this.alfSubscribe("ALF_RECORD_SELECTED", lang.hitch(this, "onRecordSelected"), true);
         this.alfSubscribe("ALF_RECORD_REMOVED", lang.hitch(this, "onRecordRemoved"), true);
      },

      getWidgetConfig: function alfresco_rm_forms_controls_AlfRmRecordPickerControl__getWidgetConfig() {
         return {
            id : this.generateUuid(),
            name: this.name,
            value: this.value
         };
      },

      createFormControl: function alfresco_rm_forms_controls_AlfRmRecordPickerControl__createFormControl(config, domNode) {

         this.itemSelectionPubSubScope = this.generateUuid();

         this.lastValue = null;

         var widgetsForControl = [{
            name: "alfresco/buttons/AlfButton",
            config: {
               additionalCssClasses: "rm-relationship-select-record-button",
               label: this.message("label.button.select-record"),
               publishTopic: "ALF_CREATE_DIALOG_REQUEST",
               publishPayload: {
                  dialogTitle: "picker.select.records.title",
                  handleOverflow: false,
                  widgetsContent: [{
                     name: "alfresco/layout/VerticalWidgets",
                     config: {
                        id: "RM_RELATIONSHIP_SELECT_RECORD_DIALOG_CONTAINER",
                        additionalCssClasses: "rm-relationship-select-record-dialog-container",
                        widgets: [{
                           name: "alfresco/buttons/AlfButton",
                           config: {
                              id: "RM_RELATIONSHIP_SELECT_RECORD_DIALOG_UP_BUTTON",
                              additionalCssClasses: "rm-relationship-select-record-dialog-up-button",
                              publishTopic: this.itemSelectionPubSubScope + "ALF_DOCLIST_PARENT_NAV",
                              showLabel: false,
                              iconClass: "alf-folder-up-icon",
                              disableOnInvalidControls: true
                           }
                        },{
                           name: "alfresco/pickers/Picker",
                           config: {
                              id: "RM_RELATIONSHIP_SELECT_RECORD_DIALOG_CONTENT",
                              pubSubScope: this.itemSelectionPubSubScope,
                              pickedItemsLabel: "picker.pickedRecords.label",
                              subPickersLabel: "",
                              widgetsForPickedItems: [{
                                 name: "alfresco/pickers/PickedItems",
                                 assignTo: "pickedItemsWidget",
                                 config: {
                                    // "PickedItems" does not extend "ProcessWidgets" so it's not possible to use "additionalCssClasses" hence we use an id for the css selector
                                    id: "RM_RELATIONSHIP_SELECT_RECORD_DIALOG_PICKED_ITEM_CONTENT",
                                    singleItemMode: true
                                 }
                              }],
                              widgetsForRootPicker: [{
                                 name: "alfresco/menus/AlfVerticalMenuBar",
                                 config: {
                                    visibilityConfig: {
                                       initialValue: false
                                    },
                                    widgets: [{
                                       name: "alfresco/menus/AlfMenuBarItem",
                                       config: {
                                          publishTopic: "ALF_ADD_PICKER",
                                          publishOnRender: true,
                                          publishPayload: {
                                             currentPickerDepth: 1,
                                             picker: [{
                                                name: "alfresco/pickers/DocumentListPicker",
                                                config: {
                                                   // "DocumentListPicker" does not extend "ProcessWidgets" so it's not possible to use "additionalCssClasses" hence we use an id for the css selector
                                                   id: "RM_RELATIONSHIP_SELECT_RECORD_DIALOG_DOCUMENT_LIST_CONTENT",
                                                   nodeRef: this.pickerRootNode
                                                }
                                             }]
                                          }
                                       }
                                    }]
                                 }
                              }]
                           }
                        }]
                     }
                  }],
                  widgetsButtons: [{
                     name: "alfresco/buttons/AlfButton",
                     config: {
                        id: "RM_RELATIONSHIP_SELECT_RECORD_DIALOG_OK_BUTTON",
                        label: "picker.ok.label",
                        publishTopic: "ALF_RECORD_SELECTED"
                     }
                  },{
                     name: "alfresco/buttons/AlfButton",
                     config: {
                        id: "RM_RELATIONSHIP_SELECT_RECORD_DIALOG_CANCEL_BUTTON",
                        label: "picker.cancel.label",
                        publishTopic: "NO_OP"
                     }
                  }]
               },
               publishGlobal: true
            }
         }];

         return this.processWidgets(widgetsForControl, this._controlNode);
      },

      setupChangeEvents: function alfresco_rm_forms_controls_AlfRmRecordPickerControl__setupChangeEvents() {
         this.alfSubscribe(this.itemSelectionPubSubScope + "ALF_ITEMS_SELECTED", lang.hitch(this, this.onItemsSelected), true);
      },

      onItemsSelected: function alfresco_rm_forms_controls_AlfRmRecordPickerControl__onItemsSelected(payload) {
         if (payload.pickedItems.length === 0)
         {
            this.selectedItem = null;
            this.value = null;
            this.lastValue = this.value;
         }
         if (payload.pickedItems.length === 1)
         {
            this.selectedItem = payload.pickedItems[0];
            this.value = lang.clone(this.selectedItem.nodeRef);
            this.lastValue = this.value;
         }
      },

      processValidationRules: function alfresco_rm_forms_controls_AlfRmRecordPickerControl__processValidationRules() {
         var valid = true;
         if (this._required === true && (!this.value || this.value.length === 0))
         {
            valid = false;
         }
         return valid;
      },

      getValue: function alfresco_rm_forms_controls_AlfRmRecordPickerControl__getValue() {
         return this.value;
      },

      onRecordSelected: function alfresco_rm_forms_controls_AlfRmRecordPickerControl__onRecordSelected(payload)
      {
         this.onValueChangeEvent(this.name, this.lastValue, this.value);

         if (this.selectedItem)
         {
            if (dom.byId("alfresco_rm_forms_controls_AlfRmRecordPickerControl"))
            {
               domConstruct.destroy("alfresco_rm_forms_controls_AlfRmRecordPickerControl");
            }

            this.processWidgets([{
               name: "rm/lists/AlfRmRelationshipList",
               config: {
                  additionalCssClasses: "rm-relationship-select-record-form-info-selected",
                  showDeleteAction: true,
                  site: this.site,
                  currentData: {
                     items: [this.selectedItem]
                  }
               }
            }], domConstruct.create("div", {id: "alfresco_rm_forms_controls_AlfRmRecordPickerControl"}, this.containerNode.parentElement, "last"));
         }
      },

      onRecordRemoved: function alfresco_rm_forms_controls_AlfRmRecordPickerControl__onRecordRemoved(payload)
      {
         domConstruct.destroy("alfresco_rm_forms_controls_AlfRmRecordPickerControl");
         this.selectedItem = null;
         this.value = null;
         this.onValueChangeEvent(this.name, this.lastValue, this.value);
      }
   });
});