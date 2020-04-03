//<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-header.lib.js">
/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2020 Alfresco Software Limited
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

// Insert a new NotificationService as the first entry in the services array (because this
// is the first entry all others will be discarded)...
var siteService = widgetUtils.findObject(model.jsonModel, "id", "SITE_SERVICE"),
   RMSitePresetId = "rm-site-dashboard",
   isRMSitePreset = {
      targetId: "PRESET",
      is: [RMSitePresetId]
   },
   stdRMType = "{http://www.alfresco.org/model/recordsmanagement/1.0}rmsite",
   dod5015Type = "{http://www.alfresco.org/model/dod5015/1.0}site",
   additionalSitePresets = (siteService && siteService.config && siteService.config.additionalSitePresets) ?
      siteService.config.additionalSitePresets : [],
   widgetsForCreateSiteDialogOverrides = (siteService && siteService.config && siteService.config.widgetsForCreateSiteDialogOverrides) ?
      siteService.config.widgetsForCreateSiteDialogOverrides : [];

additionalSitePresets.unshift({
   label: "description.recordsManagementSite",
   value: RMSitePresetId
});

widgetsForCreateSiteDialogOverrides = widgetsForCreateSiteDialogOverrides.concat([{
   // Force site tile to match the rm site title
   id: "CREATE_SITE_FIELD_TITLE",
   config: {
      disablementConfig: {
         rules: [isRMSitePreset]
      },
      autoSetConfig: [{
         rulePassValue: msg.get("title.recordsManagementSite"),
         ruleFailValue: "",
         rules: [isRMSitePreset]
      }]
   }
}, {
   // Force site shortname to be "rm"
   id: "CREATE_SITE_FIELD_SHORTNAME",
   config: {
      disablementConfig: {
         rules: [isRMSitePreset]
      },
      autoSetConfig: [{
         rulePassValue: "rm",
         ruleFailValue: "",
         rules: [isRMSitePreset]
      }]
   }
}, {
   // Force site visibility to be public
   id: "CREATE_SITE_FIELD_VISIBILITY",
   config: {
      disablementConfig: {
         rules: [isRMSitePreset]
      },
      autoSetConfig: [{
         rulePassValue: "PUBLIC",
         rules: [isRMSitePreset]
      }]
   }
}, {
   // Add default site description (which can be modified)
   id: "CREATE_SITE_FIELD_DESCRIPTION",
   config: {
      autoSetConfig: [{
         rulePassValue: msg.get("description.recordsManagementSite"),
         ruleFailValue: "",
         rules: [isRMSitePreset]
      }]
   }
}, {
   // Add compliance dropdown option
   id: "CREATE_SITE_FIELD_COMPLIANCE",
   targetPosition: "END",
   name: "alfresco/forms/controls/Select",
   config: {
      fieldId: "COMPLIANCE",
      label: "label.compliance",
      name: "compliance",
      optionsConfig: {
         fixed: [
            {
               label: "compliance.standard",
               value: stdRMType
            },
            {
               label: "compliance.dod5015",
               value: dod5015Type
            }
         ]
      },
      visibilityConfig: {
         rules: [isRMSitePreset]
      }
   }
}, {
   // Add hidden type field
   id: "CREATE_SITE_FIELD_TYPE",
   targetPosition: "END",
   name: "alfresco/forms/controls/HiddenValue",
   config: {
      fieldId: "TYPE",
      name: "type",
      autoSetConfig: [{
         ruleFailValue: "{http://www.alfresco.org/model/site/1.0}site",
         rules: [isRMSitePreset]
      }, {
         rulePassValue: stdRMType,
         rules: [
            isRMSitePreset,
            {
               targetId: "COMPLIANCE",
               is: [stdRMType]
            }
         ]
      }, {
         rulePassValue: dod5015Type,
         rules: [
            isRMSitePreset,
            {
               targetId: "COMPLIANCE",
               is: [dod5015Type]
            }
         ]
      }]
   }
}]);

var siteServiceConfig = {
   legacyMode: false,
   additionalSitePresets: additionalSitePresets,
   widgetsForCreateSiteDialogOverrides: widgetsForCreateSiteDialogOverrides
};

model.jsonModel.services.unshift({
   name: "alfresco/services/NotificationService",
   config: {
      showProgressIndicator: true
   }
}, {
   id: "SITE_SERVICE",
   name: "alfresco/services/SiteService",
   config: siteServiceConfig
});
