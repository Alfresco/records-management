<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2019 Alfresco Software Limited
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

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   var nodeDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);

   if (nodeDetails && nodeDetails.item && nodeDetails.item.node && nodeDetails.item.node.isRmNode)
   {
      var node = nodeDetails.item.node;

      // Toolbar label
      var toolbarLabel = {
         id: "RM_RELATIONSHIP_TOOLBAR_LABEL",
         name: "alfresco/html/Label",
         align: "left",
         config: {
            additionalCssClasses: "rm-relationship-toolbar-label",
            label: msg.get("label.toolbar.relationships")
         }
      };

      // Toolbar add relationship button
      var actions = node.rmNode.actions,
         showAddDeleteRelationshipButton = false;
      for (var i = 0; i < actions.length; i++)
      {
         if (actions[i] === "editReferences")
         {
            showAddDeleteRelationshipButton = true;
         }
      }

      var addRelationshipButton = {};
      if (showAddDeleteRelationshipButton)
      {
         addRelationshipButton = {
            name: "alfresco/buttons/AlfButton",
            align: "right",
            config: {
               id: "RM_RELATIONSHIP_TOOLBAR_ADD_BUTTON",
               additionalCssClasses: "rm-relationship-toolbar-add-button",
               label: msg.get("label.button.new-relationship"),
               publishTopic: "RM_RELATIONSHIP_ADD",
               publishPayload: {
                  item: nodeDetails.item
               }
            }
         };
      }

      // Toolbar
      var toolbar = {
         // "AlfToolbar" does not extend "ProcessWidgets" so it's not possible to use "additionalCssClasses" hence we use an id for the css selector
         id: "RM_RELATIONSHIP_TOOLBAR",
         name: "alfresco/documentlibrary/AlfToolbar",
         config: {
            widgets: [toolbarLabel, addRelationshipButton]
         }
      };

      var table = {
         name: "rm/lists/AlfRmRelationshipList",
         config: {
            noDataMessage: msg.get("label.list.no.data.message"),
            loadDataPublishTopic: "RM_RELATIONSHIP_GET_ALL",
            loadDataPublishPayload: {
               nodeRef: node.nodeRef
            },
            itemsProperty: "data.items",
            showDeleteAction: showAddDeleteRelationshipButton,
            ShowRelationship: true,
            currentItem: node,
            site: model.site
         }
      };

      // Json model
      model.jsonModel = {
         rootNodeId: "rm-relationships-table",
         services: [
            "alfresco/services/DocumentService",
            "alfresco/services/NotificationService",
            "alfresco/services/OptionsService",
            "alfresco/services/DialogService",
            "rm/services/RelationshipService"
         ],
         widgets: [toolbar, table]
      };
   }
};

main();
