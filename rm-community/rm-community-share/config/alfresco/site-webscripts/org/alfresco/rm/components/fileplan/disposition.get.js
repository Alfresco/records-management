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

/**
 * Main entrypoint for component webscript logic
 *
 * @method main
 */
function main()
{
   // Request the disposition actions
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);

   var nodeDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
   model.allowCreateDispositionSchedule = false;
   model.allowEditDispositionSchedule = false;
   if (nodeDetails)
   {
      model.displayName = nodeDetails.item.displayName;
      var actions = nodeDetails.item.node.rmNode.actions;
      for (var i = 0; i < actions.length; i++)
      {
         if (actions[i] == "createDispositionSchedule")
         {
            model.allowCreateDispositionSchedule = true;
         }
         if (actions[i] == "editDispositionSchedule")
         {
            model.allowEditDispositionSchedule = true;
         }
      }
   }

   var nodeRef = model.nodeRef.replace(":/", ""),
      scriptRemoteConnector = remote.connect("alfresco"),
      repoResponse = scriptRemoteConnector.get("/api/node/" + nodeRef + "/dispositionschedule?inherited=false");

   if (repoResponse.status == 401)
   {
      status.setCode(repoResponse.status, "error.loggedOut");
      return;
   }
   else if (repoResponse.status == 404)
   {
      model.hasDispositionSchedule = false;
   }
   else
   {
      var repoJSON = eval('(' + repoResponse + ')');

      // Check if we got a positive result
      if (repoJSON.data)
      {
         model.hasDispositionSchedule = true;

         repoResponse = scriptRemoteConnector.get("/api/rma/admin/listofvalues");
         var listOfValuesResult = eval('(' + repoResponse + ')'),
            periodTypesArray = listOfValuesResult.data.periodTypes.items,
            periodTypeLabels = {},
            periodType;

         for (var pti = 0; pti < periodTypesArray.length; pti++)
         {
            periodType = periodTypesArray[pti];
            periodTypeLabels[periodType.value] = periodType.label;
         }

         var schedule = repoJSON.data;
         if (schedule.instructions)
         {
            model.instructions = schedule.instructions;
         }
         if (schedule.authority)
         {
            model.authority = schedule.authority;
         }
         if (schedule.nodeRef)
         {
            model.dipositionScheduleNodeRef = schedule.nodeRef;
         }

         model.publishInProgress = schedule.publishInProgress;
         model.unpublishedUpdates = schedule.unpublishedUpdates;

         model.recordLevelDisposition = schedule.recordLevelDisposition;

         var actions = schedule.actions,
               periodTypeLabel;

         for (var i = 0; i < actions.length; i++)
         {
            var action = actions[i],
               p = action.period ? action.period.split("|") : [],
               periodType = p.length > 0 ? p[0] : null,
               periodAmount = p.length > 1 ? p[1] : null;

            if (periodType && periodType != "none")
            {
               periodTypeLabel = periodTypeLabels[periodType];
               periodTypeLabel = periodTypeLabel ? periodTypeLabel.toLowerCase() : "";
               if (!periodAmount || periodAmount == "" || periodAmount == "0")
               {
                  action.title = msg.get("label.title.noTime", [action.label, periodTypeLabel]);
               }
               else
               {
                  action.title = msg.get("label.title.complex", [action.label, periodAmount, periodTypeLabel]);
               }
            }
            else
            {
               action.title = msg.get("label.title.simple", [action.label]);
            }
         }
         model.actions = actions;
      }
      else if (repoJSON.status.code)
      {
         status.setCode(repoJSON.status.code, repoJSON.message);
         return;
      }
   }

}

main();
