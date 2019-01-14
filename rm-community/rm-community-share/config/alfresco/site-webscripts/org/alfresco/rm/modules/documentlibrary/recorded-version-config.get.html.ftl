<#--
 #%L
 Alfresco Records Management Module
 %%
 Copyright (C) 2005 - 2019 Alfresco Software Limited
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
<#assign el=args.htmlid>
<div id="${el}-dialog" class="rm-recorded-version-config">
   <div class="hd">${msg("header")}</div>
   <div class="bd">
      <form id="${el}-form" action="" method="POST">
         <div class="yui-gd">
            <div class="yui-u first rm-recorded-versions"><label for="${el}-recordedVersions">${msg("label.recorded-versions")}:</label></div>
            <div class="yui-u">
               <select id="${el}-recordedVersions" name="recordedVersion" class="rm-recorded-versions-select">
                  <#list recordableVersions as recordableVersion>
                     <option value="${recordableVersion.policy?js_string}" <#if recordableVersion.selected == "true">selected</#if>>${msg("option." + recordableVersion.policy?js_string?lower_case + ".displayValue")}</option>
                  </#list>
               </select>
            </div>
         </div>
         <div class="bdft">
            <input type="button" id="${el}-ok" value="${msg("button.ok")}" tabindex="0" />
            <input type="button" id="${el}-cancel" value="${msg("button.cancel")}" tabindex="0" />
         </div>
      </form>
   </div>
</div>
