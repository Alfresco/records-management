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
 <#assign el=args.htmlid?html>
 
 <@markup id="rm-custom-fields" target="fields" action="after" scope="global">

    <#-- RM COMPLIANCE -->
    <div class="yui-gd" id="${el}-compliance-field">
       <div class="yui-u first"><label for="${el}-rm-is-dod">${msg("label.compliance")}:</label></div>
       <div class="yui-u">
          <select id="${el}-compliance" name="compliance" tabindex="0">
             <#list compliance as item>
                <option value="${item.id}">${item.name}</option>
             </#list>
          </select>
       </div>
    </div>
    
 </@markup>
