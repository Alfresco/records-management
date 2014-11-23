<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   var nodeDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
   if (nodeDetails && nodeDetails.item.node.isRmNode)
   {
      model.jsonModel = {
         rootNodeId: "relationships",
         services: [
            "alfresco/services/CrudService"
         ],
         widgets: [{
            name: "alfresco/layout/VerticalWidgets",
            config: {
               additionalCssClasses: "relationshipToolbar",
               widgets: [{
                  name: "alfresco/documentlibrary/AlfToolbar",
                  config: {
                     widgets: [{
                        name: "alfresco/html/Label",
                        align: "left",
                        config: {
                           label: msg.get("label.toolbar.relationships"),
                           additionalCssClasses: "relationshipToolbarLabel"
                        }
                     },{
                        name: "alfresco/buttons/AlfButton",
                        align: "right",
                        config: {
                           label: msg.get("label.button.new-relationship"),
                           additionalCssClasses: "relationship-button",
                           publishTopic: "ALF_CREATE_FORM_DIALOG_REQUEST",
                           publishPayloadType: "PROCESS",
                           publishPayloadModifiers: ["processCurrentItemTokens"]
                        }
                     }]
                  }
               }]
            }
         },{
            name: "alfresco/lists/AlfList",
            config: {
               noDataMessage: msg.get("label.list.no.data.message"),
               loadDataPublishTopic: "ALF_CRUD_GET_ALL",
               loadDataPublishPayload: {
                  url: "api/node/" + nodeDetails.item.node.nodeRef.replace("://", "/") + "/relationships"
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
                                       propertyToRender: "node.relationshipLabel",
                                       additionalCssClasses: "relationshipLabel"
                                    }
                                 }]
                              }
                           },{
                              name: "alfresco/documentlibrary/views/layouts/Cell",
                              config: {
                                 widgets: [{
                                    name: "alfresco/documentlibrary/views/layouts/Row",
                                    config: {
                                       widgets:[{
                                          name: "alfresco/renderers/PropertyLink",
                                          config: {
                                             propertyToRender: "node.properties.cm:name",
                                             publishGlobal: true,
                                             publishTopic: "ALF_NAVIGATE_TO_PAGE",
                                             useCurrentItemAsPayload: false,
                                             publishPayloadType: "PROCESS",
                                             publishPayloadModifiers: ["processCurrentItemTokens"],
                                             publishPayload: {
                                                url: "site/" + model.site + "/document-details?nodeRef={node.nodeRef}",
                                                type: "SHARE_PAGE_RELATIVE"
                                             }
                                          }
                                       }]
                                    }
                                 },{
                                    name: "alfresco/documentlibrary/views/layouts/Row",
                                    config: {
                                       widgets:[{
                                          name: "alfresco/documentlibrary/views/layouts/Cell",
                                          config: {
                                             widgets: [{
                                                name: "alfresco/renderers/Property",
                                                config: {
                                                   label: msg.get("details.record.identifier"),
                                                   propertyToRender: "node.properties.rma:identifier"
                                                }
                                             },{
                                                name: "alfresco/renderers/Separator",
                                                align: "left"
                                             },{
                                                name: "alfresco/renderers/Version",
                                                config: {
                                                   propertyToRender: "node.properties.cm:versionLabel"
                                                }
                                             }]
                                          }
                                       }]
                                    }
                                 },{
                                    name: "alfresco/documentlibrary/views/layouts/Row",
                                    config: {
                                       widgets:[{
                                          name: "alfresco/documentlibrary/views/layouts/Cell",
                                          config: {
                                             widgets: [{
                                                name: "alfresco/renderers/Property",
                                                config: {
                                                   label: msg.get("details.modified.by"),
                                                   propertyToRender: "node.properties.cm:modifier"
                                                }
                                             },{
                                                name: "alfresco/renderers/Separator",
                                                align: "left"
                                             },{
                                                name: "alfresco/renderers/Property",
                                                config: {
                                                   label: msg.get("details.modified.on"),
                                                   propertyToRender: "node.properties.cm:modified"
                                                }
                                             },{
                                                name: "alfresco/renderers/Separator",
                                                align: "left"
                                             },{
                                                name: "alfresco/renderers/Size",
                                                config: {
                                                   propertyToRender: "node.size"
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
                  }
               }]
            }
         }]
      }
   }
}

main();