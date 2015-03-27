<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   var nodeDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);

   if (nodeDetails && nodeDetails.item && nodeDetails.item.node && nodeDetails.item.node.isRmNode)
   {
      var node = nodeDetails.item.node;

      // Toolbar label
      var toolbarLabel = {
         id: "RM_RELATIONSHIP_TOOLBAR_LABEL",
         name: "alfresco/html/Label",
         align: "left",
         config: {
            additionalCssClasses: "rm-relationship-toolbar-label",
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
               additionalCssClasses: "rm-relationship-toolbar-add-button",
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
         // "AlfToolbar" does not extend "ProcessWidgets" so it's not possible to use "additionalCssClasses" hence we use an id for the css selector
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
               name: "alfresco/renderers/Property",
               config: {
                  renderedValueClass: "rm-relationship-table-relationship-name",
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
               name: "rm/renderers/AlfRmFileType",
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
                        renderedValueClass: "rm-relationship-table-record-name",
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
                              renderedValueClass: "rm-relationship-table-record-identifier",
                              label: msg.get("details.record.identifier"),
                              propertyToRender: "node.properties.rma:identifier"
                           }
                        },{
                           name: "alfresco/renderers/Separator",
                           align: "left"
                        },{
                            name: "alfresco/renderers/Property",
                            config: {
                               renderedValueClass: "rm-relationship-table-record-version",
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
            "alfresco/services/DocumentService",
            "alfresco/services/NotificationService",
            "rm/services/AlfRmCrudService",
            "rm/services/AlfRmDialogService",
            "rm/services/AlfRmActionService",
            "rm/services/AlfRmOptionsService"
         ],
         widgets: [toolbar, table]
      };
   }
}

main();