<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   var nodeDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site),
      node = nodeDetails.item.node;

   if (nodeDetails && node.isRmNode)
   {
      var actions = node.rmNode.actions,
         showAddDeleteRelationshipButton = false;
      for (var i = 0; i < actions.length; i++)
      {
         if (actions[i] === "editReferences")
         {
            showAddDeleteRelationshipButton = true;
         }
      }

      var addRelationshipButton = {};
      if (showAddDeleteRelationshipButton)
      {
         addRelationshipButton = {
            name: "alfresco/buttons/AlfButton",
            align: "right",
            config: {
               label: msg.get("label.button.new-relationship"),
               additionalCssClasses: "relationship-button",
               publishTopic: "ALF_RM_ADD_RELATIONSHIP",
               publishPayload: {
                  item: nodeDetails.item
               }
            }
         };
      }

      var toolbarLabel = {
         name: "alfresco/html/Label",
         align: "left",
         config: {
            label: msg.get("label.toolbar.relationships"),
            additionalCssClasses: "relationshipToolbarLabel"
         }
      };

      var toolbar = {
         name: "alfresco/layout/VerticalWidgets",
         config: {
            additionalCssClasses: "relationshipToolbar",
            widgets: [{
               name: "alfresco/documentlibrary/AlfToolbar",
               config: {
                  widgets: [toolbarLabel, addRelationshipButton]
               }
            }]
         }
      };

      var nodeReference = node.nodeRef.replace("://", "/");

      var relationshipName = {
         name: "alfresco/documentlibrary/views/layouts/Cell",
         config: {
            widgets: [{
               name: "alfresco/renderers/Property",
               config: {
                  propertyToRender: "node.relationshipLabel",
                  renderedValueClass : "relationship-name"
               }
            }]
         }
      };

      var recordIcon = {
         name: "alfresco/documentlibrary/views/layouts/Cell",
         config: {
            widgets: [{
               name: "alfresco/rm/renderers/AlfRmFileType",
               config: {
                  size: "medium"
               }
            }]
         }
      };

      var recordInfo = {
         name: "alfresco/documentlibrary/views/layouts/Cell",
         config: {
            widgets: [{
               name: "alfresco/documentlibrary/views/layouts/Row",
               config: {
                  widgets:[{
                     name: "alfresco/renderers/PropertyLink",
                     config: {
                        propertyToRender: "node.properties.cm:name",
                        renderedValueClass : "relationship-property-link-name",
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
                              renderedValueClass : "relationship-property-detail-label",
                              propertyToRender: "node.properties.rma:identifier"
                           }
                        },{
                           name: "alfresco/renderers/Separator",
                           align: "left"
                        },{
                            name: "alfresco/renderers/Property",
                            config: {
                               label: msg.get("details.version.label"),
                               renderedValueClass : "relationship-property-detail-label",
                               propertyToRender: "node.properties.rmv:versionLabel"
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
                              propertyToRender: "node.properties.cm:description"
                           }
                        }]
                     }
                  }]
               }
            }]
         }
      };

      var deleteAction = {};
      if (showAddDeleteRelationshipButton)
      {
         deleteAction = {
            name: "alfresco/documentlibrary/views/layouts/Cell",
            config: {
               widgets: [{
                  name: "alfresco/renderers/PublishAction",
                  config: {
                     iconClass: "delete-16",
                     altText: msg.get("label.delete-relationship"),
                     publishTopic: "ALF_CRUD_DELETE",
                     publishPayloadType: "PROCESS",
                     publishPayload: {
                        requiresConfirmation: true,
                        url: "api/node/" + nodeReference + "/targetnode/{node.nodeRef}/uniqueName/{node.relationshipUniqueName}",
                        confirmationTitle: msg.get("label.confirmation.title.delete-relationship"),
                        confirmationPrompt: msg.get("label.confirmationPrompt.delete-relationship"),
                        successMessage: msg.get("label.delete-relationship.successMessage")
                     },
                     publishPayloadModifiers: ["processCurrentItemTokens", "convertNodeRefToUrl"]
                  }
               }]
            }
         };
      };

      var table = {
         name: "alfresco/lists/AlfList",
         config: {
            noDataMessage: msg.get("label.list.no.data.message"),
            loadDataPublishTopic: "ALF_CRUD_GET_ALL",
            loadDataPublishPayload: {
               url: "api/node/" + nodeReference + "/relationships"
            },
            itemsProperty: "data.items",
            widgets: [{
               name: "alfresco/documentlibrary/views/AlfDocumentListView",
               config: {
                  widgets: [{
                     name: "alfresco/documentlibrary/views/layouts/Row",
                     config: {
                        widgets: [relationshipName, recordIcon, recordInfo, deleteAction]
                     }
                  }]
               }
            }]
         }
      };

      model.jsonModel = {
         rootNodeId: "relationships",
         services: [
            "alfresco/services/CrudService",
            "alfresco/services/OptionsService",
            "alfresco/services/DocumentService",
            "alfresco/services/NotificationService",
            "alfresco/rm/services/AlfRmDialogService",
            "alfresco/rm/services/AlfRmActionService"
         ],
         widgets: [toolbar, table]
      };
   }
}

main();