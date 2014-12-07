<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/rm/components/documentlibrary/documentlist-v2.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/upload/uploadable.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/include/documentlist.lib.js">

doclibCommon();

function widgets()
{
   var useTitle = "true";
   var docLibConfig = config.scoped["DocumentLibrary"];
   if (docLibConfig != null)
   {
      var tmp = docLibConfig["use-title"];
      useTitle = tmp != null ? tmp : "true";
   }

   var docListToolbar = {
      id: "DocListToolbar",
      name: "Alfresco.rm.component.DocListToolbar",
      assignTo: "docListToolbar",
      options: {
         siteId: (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         hideNavBar: Boolean(toolbar.preferences.hideNavBar)
      }
   };

   var documentList = {
      id : "DocumentList",
      name : "Alfresco.rm.component.DocumentList",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         containerId : template.properties.container != null ? template.properties.container : "documentLibrary",
         rootNode : model.rootNode != null ? model.rootNode : "null",
         usePagination : (args.pagination == "true"),
         sortAscending : (model.preferences.sortAscending != null ? model.preferences.sortAscending : true),
         sortField : model.preferences.sortField != null ? model.preferences.sortField : "cm:name",
         showFolders : (model.preferences.showFolders != null ? model.preferences.showFolders : true),
         simpleView : model.preferences.simpleView != null ? model.preferences.simpleView : "null",
         viewRenderers: model.viewRenderers,
         highlightFile : page.url.args["file"] != null ? page.url.args["file"] : "",
         replicationUrlMapping : model.replicationUrlMapping != null ? model.replicationUrlMapping : "{}",
         repositoryBrowsing : model.rootNode != null,
         useTitle : (model.useTitle != null ? model.useTitle == "true" : true),
         userIsSiteManager : model.userIsSiteManager,
         associatedToolbar: { _alfValue: "docListToolbar", _alfType: "REFERENCE" }
      }
   };
   if (model.repositoryUrl != null)
   {
      documentList.options.repositoryUrl = model.repositoryUrl;
   }

   model.widgets = [docListToolbar, documentList];

   model.nodeRef = "workspace://SpacesStore/f6e231e6-0e6d-426c-a7aa-1ce1f0be450b";
   AlfrescoUtil.param('site', null);
   var nodeDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);

   model.jsonModel = {
      rootNodeId: "onAddRelationship",
      services: [
                 "alfresco/services/CrudService",
                 "alfresco/services/OptionsService",
                 "alfresco/services/DocumentService",
                 "alfresco/rm/services/AlfRmRelationshipDialogService"
              ],
      widgets: [{
         name: "alfresco/buttons/AlfButton",
         config: {
            label: msg.get("label.button.new-relationship"),
            additionalCssClasses: "relationship-button",
            publishTopic: "ALF_CREATE_FORM_DIALOG_REQUEST",
            publishPayloadType: "PROCESS",
            publishPayloadModifiers: ["processCurrentItemTokens"],
            publishPayload: {
               keepDialog: true,
               dialogTitle: msg.get("label.title.new-relationship"),
               dialogConfirmationButtonTitle: msg.get("label.button.create"),
               dialogCancellationButtonTitle: msg.get("label.button.cancel"),
               formSubmissionTopic: "ALF_CRUD_CREATE",
               formSubmissionPayloadMixin: {
                  // FIXME!!!
                  url: "api/node/" + "workspace/SpacesStore/f6e231e6-0e6d-426c-a7aa-1ce1f0be450b" + "/customreferences",
               },
               widgets: [{
                  name: "alfresco/rm/lists/AlfRmRelationshipList",
                  config: {
                     // FIXME!!!
                     site: "rm",
                     currentData: {
                        items: [nodeDetails.item]
                     }
                  }
               },{
                  name: "alfresco/forms/controls/DojoSelect",
                  config: {
                     name: "refId",
                     optionsConfig: {
                        publishTopic: "ALF_GET_FORM_CONTROL_OPTIONS",
                        publishPayload: {
                           url: url.context + "/proxy/alfresco/api/rma/admin/relationshiplabels",
                           itemsAttribute: "data.relationshipLabels",
                           labelAttribute: "label",
                           valueAttribute: "uniqueName"
                        }
                     }
                  }
               },{
                  name: "alfresco/rm/forms/controls/AlfRmRecordPickerControl",
                  config:
                  {
                     name: "toNode",
                     // FIXME!!!
                     site: "rm",
                     pickerRootNode: nodeDetails.item.node.rmNode.filePlan,
                     requirementConfig: {
                        initialValue: true
                     }
                  }
               }]
            }
         }
      }]
   };
}

widgets();
