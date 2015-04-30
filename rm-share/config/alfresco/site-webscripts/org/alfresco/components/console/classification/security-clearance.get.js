model.jsonModel = {
   services: [
      "alfresco/services/CrudService"
   ],
   widgets: [{
      name: "alfresco/lists/AlfFilteredList",
      config: {
         noDataMessage: msg.get("clearance.list.no.data.message"),
         pubSubScope: "SECURITY_CLEARANCE_",
         filteringTopics: ["_valueChangeof_FILTER"],
         useHash: true,
         loadDataPublishTopic: "ALF_CRUD_GET_ALL",
         loadDataPublishPayload: {
            url: "api/classification/clearance"
         },
         currentPageSize: -1,
         itemsProperty: "data.items",
         widgetsForFilters: [{
               id: "COMPOSITE_TEXTBOX",
               name: "alfresco/forms/controls/TextBox",
               config: {
                  fieldId: "TEXTBOX_FILTER",
                  name: "filter",
                  placeHolder: "clearance.filter.placeholder",
                  label: "clearance.filter.label",
                  additionalCssClasses: "security-clearance-filter"
               }
            }
         ],
         widgets: [{
            name: "alfresco/lists/views/AlfListView",
            config: {
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
                                 customClasses: "security-clearance-user-avatar",
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
   }]
};