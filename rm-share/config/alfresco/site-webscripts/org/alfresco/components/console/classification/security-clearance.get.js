model.jsonModel = {
   services: [
      "alfresco/services/CrudService"
   ],
   widgets: [{
      name: "alfresco/lists/AlfFilteredList",
      config: {
         pubSubScope: "SECURITY_CLEARANCE_",
         filteringTopics: ["_valueChangeof_FILTER"],
         useHash: true,
         // TODO: API doesn't filter data yet.
         loadDataPublishTopic: "ALF_CRUD_GET_ALL",
         loadDataPublishPayload: {
            url: "api/classification/levels"
         },
         itemsProperty: "data.items",
         widgetsForFilters: [
            {
               id: "COMPOSITE_TEXTBOX",
               name: "alfresco/forms/controls/TextBox",
               config: {
                  fieldId: "TEXTBOX_FILTER",
                  name: "id",
                  placeHolder: "Filter by id",
                  label: "Id filter"
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
                              name: "alfresco/renderers/Property",
                              config: {
                                 propertyToRender: "id"
                              }
                           }]
                        }
                     },{
                        name: "alfresco/documentlibrary/views/layouts/Cell",
                        config: {
                           widgets: [{
                              name: "alfresco/renderers/Property",
                              config: {
                                 propertyToRender: "displayLabel"
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