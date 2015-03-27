<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-header.lib.js">

// Add "Management Console" Link
var siteNavigationWidgets = getSiteNavigationWidgets(),
   isRmPageTitle = page.titleId == "page.rmSiteDashboard.title";
if (siteNavigationWidgets.length > 0)
{
   // Highlight "Site Dashboard"
   siteNavigationWidgets[0].config.selected = isRmPageTitle;

   var managementConsoleWidget = {
      id: "HEADER_SITE_RM_MANAGEMENT_CONSOLE",
      name: "alfresco/menus/AlfMenuBarItem",
      config: {
         id: "HEADER_SITE_RM_MANAGEMENT_CONSOLE",
         label: msg.get("page.rmConsole.title"),
         targetUrl: "console/rm-console/",
         selected: false
      }
   };
      
   if (siteNavigationWidgets.length < config.global.header.maxDisplayedSitePages)
   {
      siteNavigationWidgets.push(managementConsoleWidget);
   }
   else 
   {
      lastNavigationWidget = siteNavigationWidgets.pop();
      if(lastNavigationWidget.config.widgets == undefined) 
      {
         siteNavigationWidgets.push(lastNavigationWidget);
         siteNavigationWidgets.push(managementConsoleWidget);
         var forMoreMenu = siteNavigationWidgets.splice(config.global.header.maxDisplayedSitePages - 1, siteNavigationWidgets.length - config.global.header.maxDisplayedSitePages + 1);
         siteNavigationWidgets.push({
            id: "HEADER_SITE_MORE_PAGES",
            name: "alfresco/menus/AlfMenuBarPopup",
            config: {
               id: "HEADER_SITE_MORE_PAGES",
               label: "page.navigation.more.label",
               widgets: [
                  {
                     name: "alfresco/menus/AlfMenuGroup",
                     config: {
                        widgets: forMoreMenu
                     }
                  }
               ]
            }
         });
      }
      else
      {
         lastNavigationWidget.config.widgets[0].config.widgets.push(managementConsoleWidget);
         siteNavigationWidgets.push(lastNavigationWidget);
      }
   }
   widgetUtils.findObject(model.jsonModel, "id", "HEADER_NAVIGATION_MENU_BAR").config.widgets = siteNavigationWidgets;
}

// Add "Customize Dashboard" Link
if (isRmPageTitle)
{
   // FIXME: Id changes in share-header breaks RM backwards Compatibility to 4.2.d
   // Change this implementation after releasing 4.2.e
   var titleBarModel = getTitleBarModel(),
      customizeSiteDashboard = getCustomizeSiteDashboard(titleBarModel);
   if (customizeSiteDashboard)
   {
      var widgets = customizeSiteDashboard.config.widgets;
      widgets.unshift({
         name: "alfresco/menus/AlfMenuItem",
         config: {
            id: "HEADER_CUSTOMIZE_SITE_DASHBOARD",
            label: "customize_dashboard.label",
            iconClass: "alf-cog-icon",
            targetUrl: "site/" + page.url.templateArgs.site + "/customise-site-dashboard"
         }
      });
      widgetUtils.findObject(model.jsonModel, "id", "HEADER_TITLE_MENU").config.widgets = titleBarModel;
   }
}

function getCustomizeSiteDashboard(titleBarModel)
{
   var result,
      i,
      length = titleBarModel.length;

   for (i = 0; i < length; i++)
   {
      if (titleBarModel[i].id == "HEADER_SITE_CONFIGURATION_DROPDOWN")
      {
         result = titleBarModel[i];
         break;
      }
   }
   return result;
}