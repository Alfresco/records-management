<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/pages/faceted-search/faceted-search.get.js">

model.jsonModel.services.push("rm/services/AlfRmActionService",
      "alfresco/services/OptionsService",
      "rm/services/ClassifyService");

var searchAdviceNoResult = widgetUtils.findObject(widgets, "id", "FCTSRCH_SEARCH_ADVICE_NO_RESULTS"),
   searchResult = widgetUtils.findObject(searchAdviceNoResult, "id", "FCTSRCH_SEARCH_RESULT");

searchResult.config.additionalDocumentAndFolderActions = ["rm-classify-content", "rm-edit-classified-content"];
searchResult.config.widgetsAbove = [{
   name: "rm/renderers/ClassificationLabel"
}];

searchAdviceNoResult.config.widgets = [searchResult];

// Remove the existing search service and add it back in with different config.
var searchServiceIndex = model.jsonModel.services.indexOf("alfresco/services/SearchService");
if (searchServiceIndex !== -1) {
   model.jsonModel.services.splice(searchServiceIndex, 1);
}

model.jsonModel.services.push({
   name: "alfresco/services/SearchService",
   config: {
      searchAPI: url.context + "/proxy/alfresco/slingshot/rmsearch/faceted/rmsearch"
   }
});