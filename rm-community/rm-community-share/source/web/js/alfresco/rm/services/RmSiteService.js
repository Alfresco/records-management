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
/**
 * This Aikau Service extends the SiteService to allow creating an RM Site
 *
 * @module rm/service/RmSiteService
 * @extends alfresco/service/SiteService
 *
 * @author David Webster
 * @since 2.5.1
 *
 */

define(["dojo/_base/declare",
        "alfresco/services/SiteService",
        "rm/util/RmUtils"],
   function(declare, SiteService, rmUtils) {

      return declare([SiteService], {
         /**
          * Override the SiteService init to add in additional code first.
          */
         initService: function rm_services_rmSiteService_initService() {
            var RMSitePresetId = "rm-site-dashboard",
               isRMSitePreset = {
               targetId: "PRESET",
               is: [RMSitePresetId]
            };

            //Add additional preset for RM Site:
            this.additionalSitePresets = this.additionalSitePresets || [];

            this.additionalSitePresets.push({
               label: "description.recordsManagementSite",
               value: RMSitePresetId
            });

            // Update site title to fixed value and disable editing if RM preset is selected.
            var siteTitle = rmUtils.findObject(this.widgetsForCreateSiteDialog, "id", "CREATE_SITE_FIELD_TITLE");

            siteTitle.config.disablementConfig = siteTitle.config.disablementConfig || {};
            siteTitle.config.disablementConfig.rules = siteTitle.config.disablementConfig.rules || [];
            siteTitle.config.disablementConfig.rules.push(isRMSitePreset);

            siteTitle.config.autoSetConfig = siteTitle.config.autoSetConfig || [];
            siteTitle.config.autoSetConfig.push({
               rulePassValue: this.message("description.recordsManagementSite"),
               ruleFailValue: "",
               rules: [isRMSitePreset]
            });

            // Update site short name to fixed value and disable editing if RM Preset is selected.
            var siteShortname = rmUtils.findObject(this.widgetsForCreateSiteDialog, "id", "CREATE_SITE_FIELD_SHORTNAME");

            siteShortname.config.disablementConfig = siteShortname.config.disablementConfig || {};
            siteShortname.config.disablementConfig.rules = siteShortname.config.disablementConfig.rules || [];
            siteShortname.config.disablementConfig.rules.push(isRMSitePreset);

            siteShortname.config.autoSetConfig = siteShortname.config.autoSetConfig || [];
            siteShortname.config.autoSetConfig.push({
               rulePassValue: "rm",
               ruleFailValue: "",
               rules: [isRMSitePreset]
            });

            this.widgetsForCreateSiteDialog.push({
                  id: "CREATE_SITE_FIELD_COMPLIANCE",
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
               }
            );

            this.inherited(arguments);
         }
      });
   });
