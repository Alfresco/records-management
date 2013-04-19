<import resource="classpath:/alfresco/site-webscripts/org/alfresco/rm/customizations/share/share-utils.js">

// Add the "Management Console" Link to header navigation menu bar
var shareVerticalLayoutWidget = getWidgetById(model.jsonModel.widgets, "SHARE_VERTICAL_LAYOUT"),
   headerTitleBarWidget = getWidgetById(shareVerticalLayoutWidget.config.widgets, "HEADER_TITLE_BAR"),
   headerNavigationMenuBarWidget = getWidgetById(headerTitleBarWidget.config.widgets, "HEADER_NAVIGATION_MENU_BAR"),
   siteNavigationWidgets = headerNavigationMenuBarWidget.config.widgets;

//FIXME: Add "Management Console" Link

// Add the customize dashboard link for RM pages
if (page.titleId == "page.rmSiteDashboard.title")
{
   var headerTitleMenuWidget = getWidgetById(headerTitleBarWidget.config.widgets, "HEADER_TITLE_MENU"),
      siteConfigurationPopUpWidget = getWidgetById(headerTitleMenuWidget.config.widgets, "SiteConfigurationPopup");

   siteConfigurationPopUpWidget.config.widgets.unshift({
      name: "alfresco/menus/AlfMenuItem",
      config: {
         id: "HEADER_CUSTOMIZE_SITE_DASHBOARD",
         label: "customize_dashboard.label",
         iconClass: "alf-cog-icon",
         targetUrl: "site/" + page.url.templateArgs.site + "/customise-site-dashboard"
      }
   });
}

