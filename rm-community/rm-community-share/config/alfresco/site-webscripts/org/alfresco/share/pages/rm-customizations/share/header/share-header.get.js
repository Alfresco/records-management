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
   },
   stdRMType = "{http://www.alfresco.org/model/recordsmanagement/1.0}rmsite",
   dod5015Type= "{http://www.alfresco.org/model/dod5015/1.0}site";


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
         id: "CREATE_SITE_FIELD_TYPE",
         targetPosition: "END",
         name: "alfresco/forms/controls/HiddenValue",
         config: {
            fieldId: "TYPE",
            name: "type",
            autoSetConfig: [{
               rulePassValue: stdRMType,
               rules: [{
                  targetId: "COMPLIANCE",
                  is: [stdRMType]
               }]
            },{
               rulePassValue: dod5015Type,
               rules: [{
                  targetId: "COMPLIANCE",
                  is: [dod5015Type]
               }]
            }, {
               ruleFailValue: "{http://www.alfresco.org/model/site/1.0}site",
               rules: [isRMSitePreset]
            }]
         }
      }]
   }
});