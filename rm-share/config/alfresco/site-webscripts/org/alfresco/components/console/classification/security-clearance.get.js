var securityClearanceServiceScope = "SECURITY_CLEARANCE_";

model.jsonModel = {
   services: [
      "rm/services/UserSecurityClearanceService"
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
         pubSubScope: securityClearanceServiceScope,
         filteringTopics: ["_valueChangeof_FILTER"],
         useHash: true,
         loadDataPublishTopic: "RM_USER_SECURITY_CLEARANCE_GET_ALL",
         loadDataPublishPayload: {
            url: "api/classification/clearance"
         },
         itemsProperty: "data.items",
         sortField: "cm:userName",
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
                  name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                  config: {
                     width: "50px",
                     label: ""
                  }
               },{
                  name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                  config: {
                     label: msg.get("header.cell.user.name"),
                     sortable: true,
                     sortValue: "cm:userName"
                  }
               },{
                  name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                  config: {
                     label: msg.get("header.cell.security.clearance")
                  }
               }],
               widgets: [{
                  name: "alfresco/documentlibrary/views/layouts/Row",
                  config: {
                     widgets: [{
                        name: "alfresco/documentlibrary/views/layouts/Cell",
                        config: {
                           widgets: [{
                              name: "alfresco/renderers/AvatarThumbnail",
                              config: {
                                 usernameProperty: "username",
                                 imageTitleProperty: "displayName",
                                 customClasses: "security-clearance-user-avatar"
                              }
                           }]
                        }
                     },{
                        name: "alfresco/documentlibrary/views/layouts/Cell",
                        config: {
                           widgets: [{
                              name: "alfresco/renderers/Property",
                              config: {
                                 propertyToRender: "completeName",
                                 renderedValueClass: "security-clearance-user-name"
                              }
                           }]
                        }
                     },{
                        name: "alfresco/documentlibrary/views/layouts/Cell",
                        config: {
                           widgets: [{
                              name: "alfresco/renderers/Property",
                              config: {
                                 propertyToRender: "classificationLabel",
                                 renderedValueClass: "security-clearance-user-classification-level"
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
         pubSubScope: securityClearanceServiceScope,
         widgets: [{
            name: "alfresco/lists/Paginator"
         }]
      }
   }]
};