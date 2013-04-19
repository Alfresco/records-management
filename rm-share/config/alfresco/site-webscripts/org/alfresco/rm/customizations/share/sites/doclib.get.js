<import resource="classpath:/alfresco/site-webscripts/org/alfresco/rm/customizations/share/share-utils.js">

// Add the RMContentService to the existing services
model.jsonModel.services.push("alfresco/services/RMContentService");

// Get the doclib sidebar main widget
var doclibSidebarMainWidget = function getDoclibSidebarMainWidget()
{
   var shareVertivalLayoutWidget = getWidgetById(model.jsonModel.widgets, "SHARE_VERTICAL_LAYOUT"),
      doclibSideBarWidget = getWidgetById(shareVertivalLayoutWidget.config.widgets, "DOCLIB_SIDEBAR");
   return getWidgetById(doclibSideBarWidget.config.widgets, "DOCLIB_SIDEBAR_MAIN");
}();

// Get the create content menu
var createContentMenu = function getCreateContentMenu()
{
   var doclibToolbarWidget = getWidgetById(doclibSidebarMainWidget.config.widgets, "DOCLIB_TOOLBAR"),
      doclibToolbarLeftMenuWidget = getWidgetById(doclibToolbarWidget.config.widgets, "DOCLIB_TOOLBAR_LEFT_MENU");
   return getWidgetById(doclibToolbarLeftMenuWidget.config.widgets, "DOCLIB_CREATE_CONTENT_MENU");
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
var doclibToolbarWidget = getWidgetById(doclibSidebarMainWidget.config.widgets, "DOCLIB_DOCUMENT_LIST");
doclibToolbarWidget.config.widgets = [
   {
      // FIXME: RMAlfSimpleView
      name: "alfresco/documentlibrary/views/AlfSimpleView"
   },
   {
      name: "alfresco/documentlibrary/views/RMAlfDetailedView"
   }
];