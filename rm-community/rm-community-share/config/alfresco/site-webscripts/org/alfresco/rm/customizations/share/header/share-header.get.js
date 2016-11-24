<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-header.lib.js">

/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software.
 * -
 * If the software was purchased under a paid Alfresco license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 * -
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * -
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * -
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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

var siteService=widgetUtils.findObject(model.jsonModel, "name", "alfresco/services/SiteService");



//enable Aikau Dialog
this.siteService.config.legacyMode=false;

var existingPresets= siteService.config.sitePresets;

var rmSitePreset = { 
      label: "description.recordsManagementSite", 
      value: "rm-site-dashboard" 
};

var existingPresets = siteService.config.sitePresets;

if (!existingPresets)
{
   siteService.config.sitePresets = [
      { label: "create-site.dialog.type.collaboration", value: "site-dashboard" },
      { label: "description.recordsManagementSite", value: "rm-site-dashboard" }
      ];
}
else
{
   sitePresets.config.sitePresets.push(rmSitePreset)
}



siteService.config.widgetsForCreateSiteDialog=  [
   {
      id: "CREATE_SITE_FIELD_PRESET",
      name: "alfresco/forms/controls/Select",
      config: {
         fieldId: "PRESET",
         label: "create-site.dialog.type.label",
         name: "sitePreset",
         optionsConfig: {
            fixed: "{sitePresets}"
         }
      }
   },
   {
      id: "CREATE_SITE_FIELD_TITLE",
      name: "alfresco/forms/controls/TextBox",
      config: {
         fieldId: "TITLE",
         label: "create-site.dialog.name.label",
         name: "title",
         requirementConfig: {
            initialValue: true
         },
         validationConfig: [
            {
               validation: "maxLength",
               length: 256,
               errorMessage: "create-site.dialog.name.maxLength"
            },
            {
               scopeValidation: true,
               warnOnly: true,
               validation: "validationTopic",
               validationTopic: "ALF_VALIDATE_SITE_IDENTIFIER",
               validationValueProperty: "title",
               negate: true,
               validationResultProperty: "response.used",
               errorMessage: "create-site-dialog.title.already.used"
            }
            ],
            disablementConfig: {
               rules: [
                  {
                     targetId: "PRESET",
                     is: [
                        "rm-site-dashboard"
                        ]
                  }
                  ]
            },
            autoSetConfig: [  
               {  
                  rulePassValue: msg.get("description.recordsManagementSite"),  
                  ruleFailValue: "",  
                  rules: [  
                     {  
                        targetId: "PRESET",  
                        is: ["rm-site-dashboard"]  
                     }  
                     ]  
               }  
               ]  
      }
   },
   {
      id: "CREATE_SITE_FIELD_SHORTNAME",
      name: "alfresco/forms/controls/MaskingTextBox",
      config: {
         fieldId: "SHORTNAME",
//       targetId: "TITLE",
         replacements: [
            {
               regex: "[^a-z0-9-\\s]",
               flags: "gi"
            },
            {
               regex: "\\s+",
               replacement: "-",
               flags: "g"
            }
            ],
            label: "create-site.dialog.urlname.label",
            description: "create-site.dialog.urlname.description",
            name: "shortName",
            requirementConfig: {
               initialValue: true
            },
            validationConfig: [
               {
                  validation: "maxLength",
                  length: 72,
                  errorMessage: "create-site.dialog.urlname.maxLength"
               },
               {
                  validation: "regex",
                  regex: "^[0-9a-zA-Z-]+$",
                  errorMessage: "create-site.dialog.urlname.regex"
               },
               {
                  scopeValidation: true,
                  validation: "validationTopic",
                  validationTopic: "ALF_VALIDATE_SITE_IDENTIFIER",
                  validationValueProperty: "shortName",
                  negate: true,
                  validationResultProperty: "response.used",
                  errorMessage: "create-site-dialog.name.already.used"
               }
               ],
               disablementConfig: {
                  rules: [
                     {
                        targetId: "PRESET",
                        is: [
                           "rm-site-dashboard"
                           ]
                     }
                     ]
               },  
               autoSetConfig: [  
                  {  
                     rulePassValue: "rm",  
                     ruleFailValue: "",  
                     rules: [  
                        {  
                           targetId: "PRESET",  
                           is: ["rm-site-dashboard"]  
                        }  
                        ]
                  }  
                  ]
      }
   },
   {
      id: "CREATE_SITE_FIELD_DESCRIPTION",
      name: "alfresco/forms/controls/TextArea",
      config: {
         fieldId: "DESCRIPTION",
         label: "create-site.dialog.description.label",
         name: "description",
         validationConfig: [
            {
               validation: "maxLength",
               length: 512,
               errorMessage: "create-site.dialog.description.maxLength"
            }
            ]
      }
   },
   {
      id: "CREATE_SITE_FIELD_VISIBILITY",
      name: "alfresco/forms/controls/RadioButtons",
      config: {
         fieldId: "VISIBILITY",
         label: "create-site.dialog.visibility.label",
         name: "visibility",
         optionsConfig: {
            fixed: [
               { 
                  label: "create-site.dialog.visibility.public", 
                  description: "create-site.dialog.visibility.public.description",
                  value: "PUBLIC" 
               },
               { 
                  label: "create-site.dialog.visibility.moderated", 
                  description: "create-site.dialog.visibility.moderated.description",
                  value: "MODERATED" 
               },
               { 
                  label: "create-site.dialog.visibility.private",  
                  description: "create-site.dialog.visibility.private.description",
                  value: "PRIVATE" 
               }
               ]
         }
      }
   },
   {
      id: "CREATE_SITE_FIELD_COMPLIANCE",
      name: "alfresco/forms/controls/Select",
      config: {
         fieldId: "COMPLIANCE",
         label: "label.compliance",
         name: "siteCompliance",
         optionsConfig: {
            fixed:  [
               { label: "compliance.standard", value:"{http://www.alfresco.org/model/recordsmanagement/1.0}rmsite" },
               { label: "compliance.dod5015", value: "{http://www.alfresco.org/model/dod5015/1.0}site" }
               ]
         },
         visibilityConfig: {
            rules: [
               {
                  targetId: "PRESET",
                  is: ["rm-site-dashboard"]  
               }
               ]
         }
      }      
   }
];

