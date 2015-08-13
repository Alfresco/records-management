// Get the levels for the dropdown
var levels = [];
var levelsObj = {};
var result = remote.call("/api/clearance/levels");
if (result.status.code == status.STATUS_OK) {
   var rawData = JSON.parse(result);
   if (rawData && rawData.data && rawData.data.items) {
      var items = rawData.data.items;
      for (var i = 0; i < items.length; i++) {
         levels.push({
            value: items[i].id,
            label: items[i].displayLabel
         });

         levelsObj[items[i].id] = items[i].displayLabel;
      }
   }
}

model.jsonModel = {
   services: [
      "rm/services/UserSecurityClearanceService",
      "alfresco/services/DialogService"
   ],
   widgets: [{
      id: "SET_PAGE_TITLE",
      name: "alfresco/header/SetTitle",
      config: {
         title: msg.get("security-clearance.page.title")
      }
   },{
      name: "alfresco/lists/AlfFilteredList",
      config: {
         noDataMessage: msg.get("clearance.list.no.data.message"),
         filteringTopics: ["_valueChangeof_FILTER"],
         useHash: true,
         loadDataPublishTopic: "RM_USER_SECURITY_CLEARANCE_GET_ALL",
         itemsProperty: "data.items",
         sortField: "cm:firstName,cm:lastName,cm:userName",
         startIndexProperty: "data.startIndex",
         totalResultsProperty: "data.total",
         widgetsForFilters: [{
               id: "COMPOSITE_TEXTBOX",
               name: "alfresco/forms/controls/TextBox",
               config: {
                  fieldId: "TEXTBOX_FILTER",
                  name: "nameFilter",
                  placeHolder: "clearance.filter.placeholder",
                  label: "clearance.filter.label",
                  additionalCssClasses: "security-clearance-filter"
               }
            }
         ],
         widgets: [{
            name: "alfresco/lists/views/AlfListView",
            config: {
               widgetsForHeader: [{
                  name: "alfresco/lists/views/layouts/HeaderCell",
                  config: {
                     width: "50px",
                     label: ""
                  }
               },{
                  name: "alfresco/lists/views/layouts/HeaderCell",
                  config: {
                     label: msg.get("header.cell.user.name"),
                     sortable: true,
                     sortValue: "cm:firstName,cm:lastName,cm:userName"
                  }
               },{
                  name: "alfresco/lists/views/layouts/HeaderCell",
                  config: {
                     label: msg.get("header.cell.security.clearance")
                  }
               }],
               widgets: [{
                  name: "alfresco/lists/views/layouts/Row",
                  config: {
                     widgets: [{
                        name: "alfresco/lists/views/layouts/Cell",
                        config: {
                           widgets: [{
                              name: "alfresco/renderers/AvatarThumbnail",
                              config: {
                                 usernameProperty: "username",
                                 imageTitleProperty: "displayName",
                                 customClasses: "security-clearance-user-avatar",
                                 publishGlobal: true,
                                 publishTopic: "RM_USER_SECURITY_CLEARANCE_VIEW_USER",
                                 useCurrentItemAsPayload: false,
                                 publishPayloadType: "PROCESS",
                                 publishPayloadModifiers: ["processCurrentItemTokens"],
                                 publishPayload: {
                                    userName: "{userName}"
                                 }
                              }
                           }]
                        }
                     },{
                        name: "alfresco/lists/views/layouts/Cell",
                        config: {
                           widgets: [{
                              name: "alfresco/renderers/PropertyLink",
                              config: {
                                 propertyToRender: "completeName",
                                 renderedValueClass: "security-clearance-user-name",
                                 publishGlobal: true,
                                 publishTopic: "RM_USER_SECURITY_CLEARANCE_VIEW_USER",
                                 useCurrentItemAsPayload: false,
                                 publishPayloadType: "PROCESS",
                                 publishPayloadModifiers: ["processCurrentItemTokens"],
                                 publishPayload: {
                                    userName: "{userName}"
                                 }
                              }
                           }]
                        }
                     },{
                        name: "alfresco/lists/views/layouts/Cell",
                        config: {
                           widgets: [{
                              name: "alfresco/renderers/PublishingDropDownMenu",
                              id: "MODIFY_CLEARANCE",
                              config: {
                                 publishTopic: "RM_USER_SECURITY_CLEARANCE_SET",
                                 publishPayload: {
                                    username: {
                                       alfType: "item",
                                       alfProperty: "userName"
                                    },
                                    completeName: {
                                       alfType: "item",
                                       alfProperty: "completeName"
                                    },
                                    clearanceId: {
                                       alfType: "payload",
                                       alfProperty: "value"
                                    },
                                    clearanceLabel: {
                                       alfType: "payload",
                                       alfProperty: "label"
                                    },
                                    levels: levelsObj
                                 },
                                 publishPayloadType: "BUILD",
                                 publishGlobal: true,
                                 propertyToRender: "classificationId",
                                 additionalCssClasses: "security-clearance-user-classification-level",
                                 optionsConfig: {
                                    fixed: levels
                                 }
                              }
                           }]
                        }
                     }]
                  }
               }]
            }
         }]
      }
   },{
      name: "alfresco/layout/CenteredWidgets",
      config: {
         widgets: [{
            name: "alfresco/lists/Paginator"
         }]
      }
   }]
};