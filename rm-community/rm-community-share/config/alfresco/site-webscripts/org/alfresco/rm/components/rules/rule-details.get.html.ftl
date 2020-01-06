<#--
 #%L
 Alfresco Records Management Module
 %%
 Copyright (C) 2005 - 2020 Alfresco Software Limited
 %%
 This file is part of the Alfresco software.
 -
 If the software was purchased under a paid Alfresco license, the terms of
 the paid license agreement will prevail.  Otherwise, the software is
 provided under the following open source license terms:
 -
 Alfresco is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 -
 Alfresco is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.
 -
 You should have received a copy of the GNU Lesser General Public License
 along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 #L%
-->
<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/rules/config/rule-config.css" group="rules"/>
   <@link href="${url.context}/res/components/rules/config/rule-config-type.css" group="rules"/>
   <@link href="${url.context}/res/components/rules/config/rule-config-condition.css" group="rules"/>
   <@link href="${url.context}/res/components/rules/rule-details.css" group="rules"/>
</@>

<@markup id="js">

   <#-- Modified for RM -->
   <@script src="${url.context}/res/rm/components/rules/config/rule-config-util.js" group="rules"/>

   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/rules/config/rule-config.js" group="rules"/>
   <@script src="${url.context}/res/components/rules/config/rule-config-type.js" group="rules"/>
   <@script src="${url.context}/res/components/rules/config/rule-config-condition.js" group="rules"/>
   <@script src="${url.context}/res/components/rules/config/rule-config-action.js" group="rules"/>
   <@script src="${url.context}/res/components/rules/rule-details.js" group="rules"/>

   <#-- Modified for RM -->
   <@script src="${url.context}/res/rm/components/rules/rule-config-action-custom.js" group="rules"/>
   <@script src="${url.context}/res/rm/components/rules/rule-details.js" group="rules"/>
</@>

<@markup id="widgets">
   <@createWidgets group="rules"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid>
      <div id="${el}-body" class="rule-details">
         <div id="${el}-display" class="display theme-bg-color-6 theme-border-3" style="display: none;">
            <div id="${el}-actions" class="actions">
               <input type="button" id="${el}-edit-button" value="${msg("button.edit")}" tabindex="0"/>
               <input type="button" id="${el}-delete-button" value="${msg("button.delete")}" tabindex="0"/>
            </div>
            <h2 id="${el}-title">&nbsp;</h2>
            <div>
               <em>${msg("label.description")}: </em><span id="${el}-description">&nbsp;</span>
            </div>
            <hr/>
            <div id="${el}-disabled" class="behaviour">${msg("label.disabled")}</div>
            <div id="${el}-executeAsynchronously" class="behaviour">${msg("label.executeAsynchronously")}</div>
            <div id="${el}-applyToChildren" class="behaviour">${msg("label.applyToChildren")}</div>
            <hr/>
            <div id="${el}-configsMessage">${msg("message.loading")}</div>
            <div id="${el}-configsContainer" class="hidden">
               <div id="${el}-ruleConfigType"></div>
               <div id="${el}-conditionSeparator" class="configuration-separator">&nbsp;</div>
               <div id="${el}-ruleConfigIfCondition" class="if"></div>
               <div id="${el}-ruleConfigUnlessCondition" class="unless"></div>
               <div class="configuration-separator">&nbsp;</div>
               <div id="${el}-ruleConfigAction"></div>
            </div>
         </div>
      </div>
   </@>
</@>

