/**
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

define(["dojo/_base/declare",
        "alfresco/lists/AlfList"],
        function(declare, AlfList) {

   return declare([AlfList], {

      site: null,

      currentData: null,

      showDeleteAction: false,

      postCreate: function alfresco_rm_lists_AlfRmRelationshipList__postCreate()
      {
         this.processWidgets([{
            name: "alfresco/documentlibrary/views/AlfDocumentListView",
            config: {
               currentData: this.currentData,
               widgets: [{
                  name: "alfresco/documentlibrary/views/layouts/Row",
                  config: {
                     widgets: [{
                        name: "alfresco/documentlibrary/views/layouts/Cell",
                        config: {
                           widgets: [{
                              name: "alfresco/rm/renderers/AlfRmFileType",
                              config: {
                                 size: "medium"
                              }
                           }]
                        }
                     },{
                        name: "alfresco/documentlibrary/views/layouts/Cell",
                        config: {
                           widgets: [{
                              name: "alfresco/documentlibrary/views/layouts/Row",
                              config: {
                                 widgets:[{
                                    name: "alfresco/renderers/PropertyLink",
                                    config: {
                                       propertyToRender: "node.properties.cm:name",
                                       renderedValueClass : "relationship-property-link-name",
                                       publishGlobal: true,
                                       publishTopic: "ALF_NAVIGATE_TO_PAGE",
                                       useCurrentItemAsPayload: false,
                                       publishPayloadType: "PROCESS",
                                       publishPayloadModifiers: ["processCurrentItemTokens"],
                                       publishPayload: {
                                          url: "site/" + this.site + "/document-details?nodeRef={node.nodeRef}",
                                          type: "SHARE_PAGE_RELATIVE"
                                       }
                                    }
                                 }]
                              }
                           },{
                              name: "alfresco/documentlibrary/views/layouts/Row",
                              config: {
                                 widgets:[{
                                    name: "alfresco/documentlibrary/views/layouts/Cell",
                                    config: {
                                       widgets: [{
                                          name: "alfresco/renderers/Property",
                                          config: {
                                             label: this.message("details.record.identifier"),
                                             renderedValueClass : "relationship-property-detail-label",
                                             propertyToRender: "node.properties.rma:identifier"
                                          }
                                       },{
                                          name: "alfresco/renderers/Separator",
                                          align: "left"
                                       },{
                                           name: "alfresco/renderers/Property",
                                           config: {
                                              label: this.message("details.version.label"),
                                              renderedValueClass : "relationship-property-detail-label",
                                              propertyToRender: "node.properties.rmv:versionLabel"
                                           }
                                       }]
                                    }
                                 }]
                              }
                           },{
                              name: "alfresco/documentlibrary/views/layouts/Row",
                              config: {
                                 widgets:[{
                                    name: "alfresco/documentlibrary/views/layouts/Cell",
                                    config: {
                                       widgets: [{
                                          name: "alfresco/renderers/Property",
                                          config: {
                                             propertyToRender: "node.properties.cm:description"
                                          }
                                       }]
                                    }
                                 }]
                              }
                           }]
                        }
                     },{
                        name: "alfresco/documentlibrary/views/layouts/Cell",
                        config: {
                           widgets: [{
                              name: "alfresco/renderers/PublishAction",
                              config: {
                                 iconClass: "delete-16",
                                 publishTopic: "ALF_RECORD_REMOVED",
                                 publishGlobal: true,
                                 visibilityConfig: {
                                    initialValue: this.showDeleteAction
                                 }
                              }
                           }]
                        }
                     }]
                  }
               }]
            }
         }], this.domNode);
      }
   });
});