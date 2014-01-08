﻿<#if !hasAccess>
   <#include "./rm-console-access.ftl">
<#else>
<#if (action='new' || action='edit')>

<script type="text/javascript">//<![CDATA[
   new Alfresco.rm.component.RMDefineRoles("manageRoles").setOptions(
   {
      action: "${action?js_string}",
      roleId: "${(roleId!"")?js_string}"
   }).setMessages(${messages});
//]]></script>

<div id="manageRoles">
   <#if (action='new')>
   <h2>${msg('label.new-role')}</h2>
   <#else>
   <h2>${msg('label.edit-role')}</h2>
   </#if>

   <form id="roleForm" action="">
      <div>
         <label for="roleName">${msg('label.name')}:</label>
         <input type="text" name="roleName" value="" id="roleName" maxlength="100" />
      </div>

      <h3>${msg('label.capabilities')}</h3>

      <#list groupedCapabilities as groupedCapabilities>
         <#assign
            groupedCapabilitiesKeys = groupedCapabilities?keys
            index = groupedCapabilities_index + 1
         >
         <#if (index < 10)>
            <#assign index = "0" + index>
         </#if>
         <#assign
            prefix = "group${index}"
            buttonId = "${prefix}SelectAll"
            capabilitiesId = "${prefix}Capabilities"
         >
         <div>
            <button id="${buttonId}" value="${buttonId}" class="selectAll action" type="button">${msg('label.select-all')}</button>
            <fieldset>
               <#list groupedCapabilitiesKeys?sort as groupedCapabilitiesKey>
                  <legend>${groupedCapabilities[groupedCapabilitiesKey].groupTitle}</legend>
                  <ul id="${capabilitiesId}" class="capabilities">
                     <#assign capabilities = groupedCapabilities[groupedCapabilitiesKey].capabilities>
                     <#list capabilities?keys?sort as capabilitiesKey>
                        <li><input name=${capabilitiesKey} type="checkbox" id=${capabilitiesKey} /><label for=${capabilitiesKey}>${capabilities[capabilitiesKey]}</label></li>
                     </#list>
                  </ul>
               </#list>
            </fieldset>
         </div>
      </#list>

      <div>
         <button name="submit" value="submit" id="submit"><#if (action='new')>${msg('label.create')}<#else>${msg('label.save')}</#if></button>
         <button name="submitCancel" value="Cancel" id="submitCancel" class="cancel">${msg('label.cancel')}</button>
      </div>
   </form>

</div>

<#else>

<script type="text/javascript">//<![CDATA[
   new Alfresco.admin.RMViewRoles("viewRoles").setOptions({}).setMessages(${messages});
//]]></script>

<div id="viewRoles">

   <div>
      <button id="newRole" value="newRole" class="action">${msg('label.new-role')}</button>
      <h2>${msg('label.roles')}</h2>
   </div>
   <div class="yui-gf view-role-list">
      <div id="roleSelection" class="yui-u first">
         <div class="list-header">
            <h3>${msg('label.roles')}</h3>
         </div>
         <div id="roles">
            <ul>
            </ul>
         </div>
      </div>

      <div id="roleContent" class="yui-u">
         <div class="list-header">
            <div class="editRoleButton">
               <button id="editRole" class="action">${msg('label.edit-role')}</button>
            </div>
            <div class="deleteRoleButton">
               <button id="deleteRole" class="action">${msg('label.delete-role')}</button>
            </div>
            <div class="capabilities-header">${msg('label.capabilities')}</div>
         </div>
         <div class="roleCapabilities">
            <ul class="capabilities-list" id="capabilities-list">
            </ul>
         </div>
      </div>
   </div>
</div>

</#if>
</#if>
