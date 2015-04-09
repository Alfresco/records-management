model.jsonModel = {
   services: [
      "alfresco/services/CrudService"
   ],
   widgets: [{
      name: "alfresco/lists/AlfList",
      config: {
         loadDataPublishTopic: "ALF_CRUD_GET_ALL",
         loadDataPublishPayload: {
            url: "api/classification/levels"
         },
         itemsProperty: "data.items",
         widgets: [{
            name: "alfresco/documentlibrary/views/AlfDocumentListView",
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