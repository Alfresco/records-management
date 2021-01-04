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
﻿<#if !hasAccess>
   <#include "./rm-console-access.ftl">
<#else>
   <script type="text/javascript">//<![CDATA[
      new Alfresco.rm.component.RMUsersAndGroups("usersAndGroups").setOptions({}).setMessages(${messages});
   //]]></script>
   <div id="usersAndGroups">
      <div class="yui-gb view-role-list">
         <div id="roleSelection" class="yui-u first">
            <div class="list-header">
               <h3>${msg('label.roles')}</h3>
            </div>
            <div id="roles">
               <ul>
               </ul>
            </div>
         </div>
         <div id="roleContentGroups" class="yui-u">
            <div class="list-header">
               <div class="removeGroupButton">
                  <button id="removeGroup" class="action">${msg('label.remove-group')}</button>
               </div>
               <div class="addGroupButton">
                  <button id="addGroup" class="action">${msg('label.add-group')}</button>
               </div>
               <div class="groups-header">${msg('label.groups')}</div>
            </div>
            <div id="roleGroups" class="roleGroups">
               <ul class="groups-list" id="groups-list">
               </ul>
            </div>
         </div>
         <div id="roleContentUsers" class="yui-u">
            <div class="list-header">
               <div class="removeUserButton">
                  <button id="removeUser" class="action">${msg('label.remove-user')}</button>
               </div>
               <div class="addUserButton">
                  <button id="addUser" class="action">${msg('label.add-user')}</button>
               </div>
               <div class="users-header">${msg('label.users')}</div>
            </div>
            <div id="roleUsers" class="roleUsers">
               <ul class="users-list" id="users-list">
               </ul>
            </div>
         </div>
      </div>
   </div>
   <div id="rm-peoplepicker" class="groups people-picker" style="visibility: hidden;">
      <div class="hd"><span id="rm-peoplepicker-title">${msg("panel.adduser.header")}</span></div>
      <div class="bd rm-search-body">
         <div style="margin: auto 10px;">
            <div id="rm-search-peoplefinder"></div>
         </div>
      </div>
   </div>
   <div id="rm-grouppicker" class="groups group-picker" style="visibility: hidden;">
      <div class="hd"><span id="rm-grouppicker-title">${msg("panel.addgroup.header")}</span></div>
      <div class="bd rm-search-body">
         <div style="margin: auto 10px;">
            <div id="rm-search-groupfinder"></div>
         </div>
      </div>
   </div>
</#if>
