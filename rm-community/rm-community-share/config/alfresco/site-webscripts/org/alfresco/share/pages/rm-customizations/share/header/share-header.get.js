//<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-header.lib.js">
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

// Insert a new NotificationService as the first entry in the services array (because this
// is the first entry all others will be discarded)...
var RMSitePresetId = "rm-site-dashboard",
   isRMSitePreset = {
      targetId: "PRESET",
      is: [RMSitePresetId]
   };

model.jsonModel.services.unshift({
   name: "alfresco/services/NotificationService",
   config: {
      showProgressIndicator: true
   }
}, {
   name: "alfresco/services/SiteService",
   config: {
      legacyMode: false,
      additionalSitePresets: [{
         label: "description.recordsManagementSite",
         value: RMSitePresetId
      }],
      widgetsForCreateSiteDialogOverrides: [{
         id: "CREATE_SITE_FIELD_TITLE",
         config: {
            disablementConfig: {
               rules: [isRMSitePreset]
            },
            autoSetConfig: [{
               rulePassValue: msg.get("description.recordsManagementSite"),
               ruleFailValue: "",
               rules: [isRMSitePreset]
            }]
         }
      }, {
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
         id: "CREATE_SITE_FIELD_COMPLIANCE",
         targetPosition: "END",
         name: "alfresco/forms/controls/Select",
         config: {
            fieldId: "COMPLIANCE",
            label: "label.compliance",
            name: "siteCompliance",
            optionsConfig: {
               fixed: [
                  {
                     label: "compliance.standard",
                     value: "{http://www.alfresco.org/model/recordsmanagement/1.0}rmsite"
                  },
                  {
                     label: "compliance.dod5015",
                     value: "{http://www.alfresco.org/model/dod5015/1.0}site"
                  }
               ]
            },
            visibilityConfig: {
               rules: [isRMSitePreset]
            }
         }
      }]
   }
});