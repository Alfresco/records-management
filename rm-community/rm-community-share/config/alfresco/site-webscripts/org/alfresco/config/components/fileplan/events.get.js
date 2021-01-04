<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2021 Alfresco Software Limited
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
   if (nodeDetails && nodeDetails.item.node.isRmNode)
   {
      model.allowCompleteEvent = false;
      model.allowUndoEvent = false;
      var actions = nodeDetails.item.node.rmNode.actions;
      for (var i = 0; i < actions.length; i++)
      {
         if (actions[i] == "completeEvent")
         {
            model.allowCompleteEvent = true;
         }
         if (actions[i] == "undoEvent")
         {
            model.allowUndoEvent = true;
         }
      }
   }
   else
   {
      // Signal to the template that the node doesn't exist and that events therefore shouldn't be displayed.
      model.nodeRef = null;
   }
}

main();
