// Add the "Management Console" Link to header navigation menu bar
var shareVerticalLayoutWidget = widgetUtils.findObject(model.jsonModel.widgets, "id", "SHARE_VERTICAL_LAYOUT"),
   headerTitleBarWidget = widgetUtils.findObject(shareVerticalLayoutWidget.config.widgets, "id", "HEADER_TITLE_BAR"),
   headerNavigationMenuBarWidget = widgetUtils.findObject(headerTitleBarWidget.config.widgets, "id", "HEADER_NAVIGATION_MENU_BAR"),
   siteNavigationWidgets = headerNavigationMenuBarWidget.config.widgets;

//FIXME: Add "Management Console" Link

// Add the customize dashboard link for RM pages
if (page.titleId == "page.rmSiteDashboard.title")
{
   var headerTitleMenuWidget = widgetUtils.findObject(headerTitleBarWidget.config.widgets, "id", "HEADER_TITLE_MENU"),
      siteConfigurationPopUpWidget = widgetUtils.findObject(headerTitleMenuWidget.config.widgets, "id", "SiteConfigurationPopup");

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

