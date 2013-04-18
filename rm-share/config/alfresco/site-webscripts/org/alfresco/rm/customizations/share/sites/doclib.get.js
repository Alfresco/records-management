// Add the RMContentService to the existing services
model.jsonModel.services.push("alfresco/services/RMContentService");

// Get the doclib sidebar main widget
var doclibSidebarMainWidget = function getDoclibSidebarMainWidget()
{
   var shareVertivalLayoutWidget = widgetUtils.findObject(model.jsonModel.widgets, "id", "SHARE_VERTICAL_LAYOUT"),
      doclibSideBarWidget = widgetUtils.findObject(shareVertivalLayoutWidget.config.widgets, "id", "DOCLIB_SIDEBAR");
   return widgetUtils.findObject(doclibSideBarWidget.config.widgets, "id", "DOCLIB_SIDEBAR_MAIN");
}();

// Get the create content menu
var createContentMenu = function getCreateContentMenu()
{
   var doclibToolbarWidget = widgetUtils.findObject(doclibSidebarMainWidget.config.widgets, "id", "DOCLIB_TOOLBAR"),
      doclibToolbarLeftMenuWidget = widgetUtils.findObject(doclibToolbarWidget.config.widgets, "id", "DOCLIB_TOOLBAR_LEFT_MENU");
   return widgetUtils.findObject(doclibToolbarLeftMenuWidget.config.widgets, "id", "DOCLIB_CREATE_CONTENT_MENU");
}();

// Add the options to create record categories/folders...
createContentMenu.config.widgets = [
   {
      name: "alfresco/menus/AlfMenuItem",
      config: {
         label: msg.get("button.new-category"),
         iconClass: "alf-showfolders-icon",
         publishTopic: "ALF_CREATE_NEW_RM_CATEGORY"
      }
   },
   {
      name: "alfresco/menus/AlfMenuItem",
      config: {
         label: msg.get("button.new-folder"),
         iconClass: "alf-showfolders-icon",
         publishTopic: "ALF_CREATE_NEW_RM_FOLDER"
      }
   }
];

// Change the available views for RM
var doclibToolbarWidget = widgetUtils.findObject(doclibSidebarMainWidget.config.widgets, "id", "DOCLIB_DOCUMENT_LIST");
doclibToolbarWidget.config.widgets = [
   {
      // FIXME: RMAlfSimpleView
      name: "alfresco/documentlibrary/views/AlfSimpleView"
   },
   {
      name: "alfresco/documentlibrary/views/RMAlfDetailedView"
   }
];