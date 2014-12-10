<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   var nodeDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site),
      node = nodeDetails.item.node;

   if (nodeDetails && node.isRmNode)
   {
      // Toolbar label
      var toolbarLabel = {
         id: "RM_RELATIONSHIP_TOOLBAR_LABEL",
         name: "alfresco/html/Label",
         align: "left",
         config: {
            label: msg.get("label.toolbar.relationships")
         }
      };

      // Toolbar add relationship button
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
               id: "RM_RELATIONSHIP_TOOLBAR_ADD_BUTTON",
               label: msg.get("label.button.new-relationship"),
               publishTopic: "ALF_RM_ADD_RELATIONSHIP",
               publishPayload: {
                  item: nodeDetails.item
               }
            }
         };
      }

      // Toolbar
      var toolbar = {
         id: "RM_RELATIONSHIP_TOOLBAR",
         name: "alfresco/documentlibrary/AlfToolbar",
         config: {
            widgets: [toolbarLabel, addRelationshipButton]
         }
      };

      // Relationship name
      var relationshipName = {
         name: "alfresco/documentlibrary/views/layouts/Cell",
         config: {
            widgets: [{
               id: "RM_RELATIONSHIP_TABLE_RELATIONSHIP_NAME",
               name: "alfresco/renderers/Property",
               config: {
                  propertyToRender: "node.relationshipLabel"
               }
            }]
         }
      };

      // Record icon
      var recordIcon = {
         name: "alfresco/documentlibrary/views/layouts/Cell",
         config: {
            widgets: [{
               id: "RM_RELATIONSHIP_TABLE_RECORD_ICON",
               name: "alfresco/rm/renderers/AlfRmFileType",
               config: {
                  size: "medium"
               }
            }]
         }
      };

      // Record info
      var recordInfo = {
         name: "alfresco/documentlibrary/views/layouts/Cell",
         config: {
            widgets: [{
               name: "alfresco/documentlibrary/views/layouts/Row",
               config: {
                  widgets:[{
                     name: "alfresco/renderers/PropertyLink",
                     config: {
                        id: "RM_RELATIONSHIP_TABLE_RECORD_NAME",
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
                              id: "RM_RELATIONSHIP_TABLE_RECORD_IDENTIFIER",
                              label: msg.get("details.record.identifier"),
                              propertyToRender: "node.properties.rma:identifier"
                           }
                        },{
                           name: "alfresco/renderers/Separator",
                           align: "left"
                        },{
                            name: "alfresco/renderers/Property",
                            config: {
                               id: "RM_RELATIONSHIP_TABLE_RECORD_VERSION",
                               label: msg.get("details.version.label"),
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
                              id: "RM_RELATIONSHIP_TABLE_RECORD_DESCRIPTION",
                              propertyToRender: "node.properties.cm:description"
                           }
                        }]
                     }
                  }]
               }
            }]
         }
      };

      // Delete action
      var nodeReference = node.nodeRef.replace("://", "/"),
         deleteAction = {};
      if (showAddDeleteRelationshipButton)
      {
         deleteAction = {
            name: "alfresco/documentlibrary/views/layouts/Cell",
            config: {
               widgets: [{
                  name: "alfresco/renderers/PublishAction",
                  config: {
                     id: "RM_RELATIONSHIP_TABLE_DELETE_ACTION",
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

      // Table
      var table = {
         name: "alfresco/lists/AlfList",
         config: {
            id: "RM_RELATIONSHIP_TABLE",
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

      // Json model
      model.jsonModel = {
         rootNodeId: "rm-relationships-table",
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