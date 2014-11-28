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

      showDeleteAction: null,

      postCreate: function alfresco_relationship_RmRelationshipItem__postCreate(payload) {
         var config = [{
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
                              name: "alfresco/renderers/FileType",
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
                                             propertyToRender: "node.properties.rma:identifier"
                                          }
                                       },{
                                          name: "alfresco/renderers/Separator",
                                          align: "left"
                                       },{
                                          name: "alfresco/renderers/Version",
                                          config: {
                                             propertyToRender: "node.properties.cm:versionLabel"
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
                                             label: this.message("details.modified.by"),
                                             propertyToRender: "node.properties.cm:modifier"
                                          }
                                       },{
                                          name: "alfresco/renderers/Separator",
                                          align: "left"
                                       },{
                                          name: "alfresco/renderers/Property",
                                          config: {
                                             label: this.message("details.modified.on"),
                                             propertyToRender: "node.properties.cm:modified"
                                          }
                                       },{
                                          name: "alfresco/renderers/Separator",
                                          align: "left"
                                       },{
                                          name: "alfresco/renderers/Size",
                                          config: {
                                             propertyToRender: "node.size"
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
         }];
         this.processWidgets(config, this.domNode);
      }
   });
});