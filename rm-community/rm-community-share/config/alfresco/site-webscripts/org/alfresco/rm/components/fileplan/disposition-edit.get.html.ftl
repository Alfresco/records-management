<#--
 #%L
 Alfresco Records Management Module
 %%
 Copyright (C) 2005 - 2021 Alfresco Software Limited
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
<script type="text/javascript">//<![CDATA[
   new Alfresco.rm.component.DispositionEdit("${args.htmlid}").setMessages(
      ${messages}
   ).setOptions(
   {
      nodeRef: "${page.url.args.nodeRef?js_string}",
      siteId: "${page.url.templateArgs.site!""}",
      events:
      {
      <#list events as event>
         "${event.value}": { label: "${event.label?html}", automatic: ${event.automatic?string} }<#if (event_has_next)>, </#if>
      </#list>
   },
   actions:
   {
   <#list dispositionActions as action>
      "${action.value}": { label: "${action.label?html}" }<#if (action_has_next)>, </#if>
   </#list>
   }
});
//]]></script>
<#assign el=args.htmlid>
<div class="rm-disposition-edit">
   <div class="caption">
      <span class="mandatory-indicator">*</span> ${msg("label.required.fields")}
   </div>
   <div class="disposition-form">
      <div>
         <div class="header">${msg("header.action")}</div>
         <hr />
         <ol id="${el}-actionList" class="action-list">
            <li id="${el}-action-template-dummy">${msg("message.loadingSteps")}</li>
         </ol>
      </div>
   </div>

   <div id="${el}-flowButtons" class="flow-buttons">
      <hr />
      <input id="${el}-createaction-button" class="yui-button createaction createaction-button" name="createaction-button" value="${msg("button.createaction")}&nbsp;&#9662;" type="button" >
      <select id="${el}-createaction-menu" class="createaction-menu" name="createaction-menu">
      <#-- double html encoding required here due to YUI bug -->
      <#list dispositionActions as action>
         <option value="${action.value?html}">${action.label?html?html}</option>
      </#list>
      </select>
   </div>

   <div class="main-buttons">
      <hr />
      <span id="${el}-done-button" class="yui-button done">
          <span class="first-child">
              <button type="button">${msg("button.done")}</button>
          </span>
      </span>
   </div>

   <!-- Event and Action Templates -->
   <div style="display:none">
      <ul>
         <li id="${el}-event-template" class="event">
            <div class="action-event-relation">
               <span class="or">${msg("label.or")}</span>
               <span class="and">${msg("label.and")}</span>
            </div>
            <div class="action-event-name">
               <input type="hidden" name="events[]" class="action-event-name-value">
               <span class="action-event-name-label"></span>
            </div>
            <div class="action-event-completion"></div>
            <div class="action-event-buttons">
               <span class="delete" title="${msg("icon.deleteevent")}">&nbsp;</span>
            </div>
         </li>
      </ul>

      <ol>
         <li id="${el}-action-template" class="action collapsed">
            <div class="header">
               <div class="no"></div>
               <div class="buttons">
                  <span class="edit" title="${msg("icon.editaction")}">&nbsp;</span>
                  <span class="delete" title="${msg("icon.deleteaction")}">&nbsp;</span>
               </div>
               <div class="title"></div>
            </div>
            <div class="details" style="display:none;">
               <form class="action-form" method="" action="">
                  <input type="hidden" name="period" value="" class="period" />
                  <input type="hidden" name="id" value="" class="id" />
                  <input type="hidden" name="name" value="" class="action-name" />
                  <div class="section">
                     <span class="action-location-section">
                        ${msg("label.transferTo")}:
                        <select name="location" class="action-location">
                        <#list transferLocations as location>
                           <option value="${location.value?html}">${location.label?html}</option>
                        </#list>
                        </select>
                        <span class="action-location-restricted-section">&nbsp;</span>
                     </span>

                  </div>
                  <div class="section">
                     <input type="checkbox" class="period-enabled" checked="true" />
                     ${msg("label.after")}
                     <input type="text" class="period-amount" name="-"/>
                     <select class="period-unit">
                     <#list periodTypes as periodType>
                        <option value="${periodType.value?html}">${periodType.label?html}</option>
                     </#list>
                     </select>
                     ${msg("label.from")}
                     <select name="periodProperty" class="period-action">
                     </select>
                     <span class="action-or-step-conditions-span">${msg("label.or")}</span>
                     <span class="action-combine-step-conditions-span">
                        <input id="${el}-combine-or-radioButton" type="radio" name="combineDispositionStepConditions" class="combineDispositionStepConditions" value="false" checked="false" /> ${msg("label.or")}
                        <input id="${el}-combine-and-radioButton" type="radio" name="combineDispositionStepConditions" class="combineDispositionStepConditions" value="true" /> ${msg("label.and")}
                     </span>
                  
                  </div>

                  <div class="section">
                     <input type="checkbox" class="events-enabled" checked="true" />
                     ${msg("label.when")}
                  </div>
                  <div class="section events">
                     <div class="events-header">
                        <div class="event-name-header">${msg("header.event")}</div>
                        <div class="event-completion-header">${msg("header.completion")}</div>
                        <hr/>
                     </div>
                     <ul class="events-list">
                        <li id="${el}-event-template-dummy"></li>
                     </ul>
                     <div class="events-header">
                        <button class="addevent-button">${msg("button.addevent")}&nbsp;&#9662;</button>
                        <select class="addevent-menu">
                        <#-- double html encoding required here due to YUI bug -->
                        <#list events as event>
                           <option value="${event.value?html}">${event.label?html?html}</option>
                        </#list>
                        </select>
                        <hr />
                     </div>
                     <select name="eligibleOnFirstCompleteEvent" class="relation">
                        <option value="false">${msg("relation.option.and")}</option>
                        <option value="true">${msg("relation.option.or")}</option>
                     </select>
                  </div>
                  <div class="section">
                     <span class="action-ghost-section">
                        <input type="checkbox" name="ghostOnDestroy" class="ghostOnDestroy"/>
                        ${msg("label.ghostondestroy")}
                     </span>
                  </div>
                  <div class="section">
                     <hr />
                     ${msg("label.description")}: <span class="mandatory-indicator">*</span><br />
                     <textarea name="description" class="description"></textarea>
                  </div>
                  <div class="buttons">
                     <span class="yui-button saveaction">
                        <span class="first-child">
                           <button type="button">${msg("button.save")}</button>
                        </span>
                     </span>
                     <span class="yui-button cancel">
                        <span class="first-child">
                           <button type="button">${msg("button.cancel")}</button>
                        </span>
                     </span>
                  </div>
               </form>
            </div>
         </li>
      </ol>
   </div>

</div>
