<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/pages/faceted-search/faceted-search.get.js">

services.push("rm/services/AlfRmActionService",
      "alfresco/services/OptionsService",
      "rm/services/ClassifyService");

var searchAdviceNoResult = widgetUtils.findObject(widgets, "id", "FCTSRCH_SEARCH_ADVICE_NO_RESULTS"),
   searchResult = widgetUtils.findObject(searchAdviceNoResult, "id", "FCTSRCH_SEARCH_RESULT");

searchResult.config.additionalDocumentAndFolderActions = ["rm-classify-content", "rm-edit-classified-content"];
searchResult.config.widgetsAbove = [{
   name: "rm/renderers/ClassificationLabel"
}];

searchAdviceNoResult.config.widgets = [searchResult];