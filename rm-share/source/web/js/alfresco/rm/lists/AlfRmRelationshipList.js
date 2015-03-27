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
        "dojo/dom-class",
        "alfresco/lists/AlfList"],
        function(declare, domClass, AlfList) {

   return declare([AlfList], {

      /**
       * Extra css classes to add to the node.
       *
       * @instane
       * @type {string}
       * @default ""
       */
      additionalCssClasses: "",

      /**
       * placeholder var for the site name
       *
       * @instance
       * @type {string}
       * @default null
       */
      site: null,

      /**
       * placeholder var for the currentData
       *
       * @instance
       * @type {object[]}
       * @default null
       */
      currentData: null,

      /**
       * Should the actions column be shown?
       *
       * @instance
       * @type {boolean}
       * @default false
       */
      showDeleteAction: false,

      /**
       * Should the Relationship column be shown?
       *
       * @instance
       * @type {boolean}
       * @default false
       */
      showRelationship: false,

      /**
       * Instantiate the view widgets.
       *
       * @instance
       */
      postCreate: function alfresco_rm_lists_AlfRmRelationshipList__postCreate()
      {
         domClass.add(this.domNode, (this.additionalCssClasses != null ? this.additionalCssClasses : ""));

         this.processWidgets([{
            name: "alfresco/lists/views/AlfListView",
            config: {
               currentData: this.currentData,
               widgets: [{
                  name: "alfresco/lists/views/layouts/Row",
                  config: {
                     widgets: [{
                        name: "alfresco/documentlibrary/views/layouts/Cell",
                        config: {
                           widgets: [{
                              name: "alfresco/renderers/Property",
                              config: {
                                 renderedValueClass: "rm-relationship-table-relationship-name",
                                 propertyToRender: "node.relationshipLabel"
                              }
                           }]
                        },
                        visibilityConfig: {
                           initialValue: this.showRelationship
                        }
                     }, {
                        name: "alfresco/lists/views/layouts/Cell",
                        config: {
                           widgets: [{
                              name: "alfresco/renderers/FileType",
                              config: {
                                 size: "medium",
                                 imageUrl: "rm/images/filetypes/record-{size}.png"
                              }
                           }]
                        }
                     }, {
                        name: "alfresco/lists/views/layouts/Cell",
                        config: {
                           widgets: [{
                              name: "alfresco/lists/views/layouts/Row",
                              config: {
                                 widgets: [{
                                    name: "alfresco/renderers/PropertyLink",
                                    config: {
                                       renderedValueClass: "rm-relationship-table-record-name",
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
                           }, {
                              name: "alfresco/lists/views/layouts/Row",
                              config: {
                                 widgets: [{
                                    name: "alfresco/lists/views/layouts/Cell",
                                    config: {
                                       widgets: [{
                                          name: "alfresco/renderers/Property",
                                          config: {
                                             renderedValueClass: "rm-relationship-table-record-identifier",
                                             label: this.message("details.record.identifier"),
                                             propertyToRender: "node.properties.rma:identifier"
                                          }
                                       }, {
                                          name: "alfresco/renderers/Separator",
                                          align: "left"
                                       }, {
                                          name: "alfresco/renderers/Property",
                                          config: {
                                             renderedValueClass: "rm-relationship-table-record-version",
                                             label: this.message("details.version.label"),
                                             propertyToRender: "node.properties.rmv:versionLabel"
                                          }
                                       }]
                                    }
                                 }]
                              }
                           }, {
                              name: "alfresco/lists/views/layouts/Row",
                              config: {
                                 widgets: [{
                                    name: "alfresco/lists/views/layouts/Cell",
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
                     }, {
                        name: "alfresco/lists/views/layouts/Cell",
                        config: {
                           widgets: [{
                              name: "alfresco/renderers/PublishAction",
                              config: {
                                 iconClass: "delete-16",
                                 publishTopic: "RM_RELATIONSHIP_DELETE",
                                 publishPayload: {
                                    nodeRef: this.currentItem.nodeRef,
                                    // This gets replaced with the current Item when the payload is processed.
                                    node: "{node}"
                                 },
                                 publishPayloadType: "PROCESS",
                                 publishPayloadModifiers: ["processCurrentItemTokens"],
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
         // Render this to the viewNode, otherwise it becomes a child of the listnode and gets hidden by [hideChildren]{@link module:alfresco/lists/views/AlfListView#hideChildren}
         }], this.viewNode);
      }
   });
});