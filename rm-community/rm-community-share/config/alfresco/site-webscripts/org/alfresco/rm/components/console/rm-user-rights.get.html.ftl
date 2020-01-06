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
<#if !hasAccess>
   <#include "./rm-console-access.ftl">
<#else>
<script type="text/javascript" charset="utf-8">
    new Alfresco.rm.component.RMUserRights('${htmlid}').setOptions({}).setMessages(${messages});
</script>

<div id="${htmlid}" class="rm-user-rights">
   <div class="yui-gc">
      <div class="yui-u first">
         <h1>${msg('label.user-rights')}</h1>
         <h2>${msg('label.users')}</h2>
         <div id="userrightsDT">
         </div>
         <h2>${msg('label.roles')}</h2>
         <div id="userrightsRoles">
            <p>${msg('label.no-roles')}</p>
         </div>
         <h2>${msg('label.groups')}</h2>
         <div id="userrightsGroups">
            <p>${msg('label.no-groups')}</p>
         </div>
      </div>
   </div>
</div>
</#if>
