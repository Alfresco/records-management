<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/include/toolbar.lib.js">

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

function getRMActionSet(myConfig)
{
   // Actions
   var multiSelectConfig = config.scoped["DocumentLibrary"]["multi-select"],
      multiSelectActions = multiSelectConfig.getChildren("action"),
      actionSet = [];

   var multiSelectAction;
   for (var i = 0; i < multiSelectActions.size(); i++)
   {
      multiSelectAction = multiSelectActions.get(i);
      attr = multiSelectAction.attributes;

      if(attr["rmaction"] == "true" && (!attr["syncMode"] || attr["syncMode"].toString() == syncMode.value))
      {
         // Multi-Select Actions
         action = {
            id: attr["id"] ? attr["id"].toString() : "",
            type: attr["type"] ? attr["type"].toString() : "",
            permission: attr["permission"] ? attr["permission"].toString() : "",
            asset: attr["asset"] ? attr["asset"].toString() : "",
            href: attr["href"] ? attr["href"].toString() : "",
            label: attr["label"] ? attr["label"].toString() : "",
            hasAspect: attr["hasAspect"] ? attr["hasAspect"].toString() : "",
            notAspect: attr["notAspect"] ? attr["notAspect"].toString() : "",
            hasProperty: attr["hasProperty"] ? attr["hasProperty"].toString() : "",
            notProperty: attr["notProperty"] ? attr["notProperty"].toString() : ""
         };

         actionSet.push(action)
      }
   }

   model.actionSet = actionSet;
}

function rm_main()
{
   var myConfig = new XML(config.script);

   getPreferences();
   getRMActionSet(myConfig);
   getCreateContent(myConfig);
   getRepositoryBrowserRoot();

   toolbar.syncMode = syncMode.value;
}

rm_main();
