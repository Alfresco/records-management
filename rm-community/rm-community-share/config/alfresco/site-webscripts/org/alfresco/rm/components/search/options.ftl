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
<div class="rm-options">
   <div id="${el}-options-toggle" class="separator">
      <div class="options">${msg("label.options")}</div>
   </div>
   <div class="bd options-hidden" id="${el}-options">
      <div class="yui-gb">
         <div class="yui-u first">
            <span class="header">${msg("label.metadata")}</span>
            <div id="${el}-metadata" class="metadata">
               <ul>
                  <li>
                     <input type="checkbox" id="${el}-metadata-identifier" checked="checked" />
                     <label for="${el}-metadata-identifier">${msg("label.identifier")}</label>
                  </li>
                  <li class="metadata-header">${msg("label.menu.content")}</li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-name" checked="checked" />
                     <label for="${el}-metadata-name">${msg("label.name")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-title" checked="checked" />
                     <label for="${el}-metadata-title">${msg("label.title")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-description" />
                     <label for="${el}-metadata-description">${msg("label.description")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-parentFolder" checked="checked" />
                     <label for="${el}-metadata-parentFolder">${msg("label.parentFolder")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-creator" />
                     <label for="${el}-metadata-creator">${msg("label.creator")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-created" />
                     <label for="${el}-metadata-created">${msg("label.created")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-modifier" />
                     <label for="${el}-metadata-modifier">${msg("label.modifier")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-modified" checked="checked" />
                     <label for="${el}-metadata-modified">${msg("label.modified")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-author" />
                     <label for="${el}-metadata-author">${msg("label.author")}</label>
                  </li>
                  <li class="metadata-header">${msg("label.menu.records")}</li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-dateFiled" checked="checked" />
                     <label for="${el}-metadata-dateFiled">${msg("label.dateFiled")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-reviewDate" />
                     <label for="${el}-metadata-reviewDate">${msg("label.reviewDate")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-vitalRecord" checked="checked" />
                     <label for="${el}-metadata-vitalRecord">${msg("label.vitalRecord")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-location" />
                     <label for="${el}-metadata-location">${msg("label.location")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-supplementalMarkingList" />
                     <label for="${el}-metadata-supplementalMarkingList">${msg("label.supplementalMarkingList")}</label>
                  </li>
                  <li class="metadata-header">${msg("label.menu.disposition")}</li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-dispositionEvents" />
                     <label for="${el}-metadata-dispositionEvents">${msg("label.dispositionEvents")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-dispositionActionName" />
                     <label for="${el}-metadata-dispositionActionName">${msg("label.dispositionActionName")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-dispositionActionAsOf" />
                     <label for="${el}-metadata-dispositionActionAsOf">${msg("label.dispositionActionAsOf")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-dispositionEventsEligible" />
                     <label for="${el}-metadata-dispositionEventsEligible">${msg("label.dispositionEventsEligible")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-dispositionPeriod" />
                     <label for="${el}-metadata-dispositionPeriod">${msg("label.dispositionPeriod")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-hasDispositionSchedule" />
                     <label for="${el}-metadata-hasDispositionSchedule">${msg("label.hasDispositionSchedule")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-dispositionInstructions" />
                     <label for="${el}-metadata-dispositionInstructions">${msg("label.dispositionInstructions")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-dispositionAuthority" />
                     <label for="${el}-metadata-dispositionAuthority">${msg("label.dispositionAuthority")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-vitalRecordReviewPeriod" />
                     <label for="${el}-metadata-vitalRecordReviewPeriod">${msg("label.vitalRecordReviewPeriod")}</label>
                  </li>

                  <#list groups as group>

                     <li class="metadata-header">${group.label?html}</li>
                     <#list group.properties as property>
                     	<li>
                     		<input type="checkbox" id="${el}-metadata-${property.name}" />
                     		<label for="${el}-metadata-${property.name}">${property.label?html}</label>
                     	</li>
                     </#list>

                  </#list>

               </ul>
            </div>
         </div>
         <div class="yui-u">
            <div class="sort">
               <span class="header">${msg("label.order")}</span>
               <#list 1..3 as i>
               <div>
                  <span class="sortlabel"><#if i=1>${msg("label.sortFirst")}<#else>${msg("label.sortNext")}</#if></span>
                  <span>
                     <input id="${el}-sort${i}" type="button" name="sort${i}" value="<#if i=1>${msg("label.identifier")}&nbsp;&#9662;<#else>${msg("label.sortNone")}&nbsp;&#9662;</#if>" />
                     <select id="${el}-sort${i}-menu">
                        <#if i!=1><option value="">${msg("label.sortNone")}</option></#if>
                        <option value="rma:identifier">${msg("label.identifier")}</option>
                        <option value="cm:name">${msg("label.name")}</option>
                        <option value="cm:title">${msg("label.title")}</option>
                        <option value="cm:description">${msg("label.description")}</option>
                        <option value="cm:creator">${msg("label.creator")}</option>
                        <option value="cm:created">${msg("label.created")}</option>
                        <option value="cm:modifier">${msg("label.modifier")}</option>
                        <option value="cm:modified">${msg("label.modified")}</option>
                        <option value="cm:author">${msg("label.author")}</option>
                        <option value="rma:dateFiled">${msg("label.dateFiled")}</option>
                        <option value="rma:reviewAsOf">${msg("label.reviewDate")}</option>
                        <option value="rma:location">${msg("label.location")}</option>
                        <option value="rmc:supplementalMarkingList">${msg("label.supplementalMarkingList")}</option>
                        <!-- double ?html encoding required here due to YUI bug -->
                        <#list groups as group>
                        	<#list group.properties as property>
                        		<option value="${property.prefix}:${property.name}">${property.label?html?html}</option>
                        	</#list>
                        </#list>
                     </select>
                  </span>
                  <span>
                     <input id="${el}-sort${i}-order" type="button" name="sort${i}-order" value="${msg("label.sortAscending")}&nbsp;&#9662;" />
                     <select id="${el}-sort${i}-order-menu">
                        <option value="asc">${msg("label.sortAscending")}</option>
                        <option value="dsc">${msg("label.sortDescending")}</option>
                     </select>
                  </span>
               </div>
               </#list>
            </div>
         </div>
         <div class="yui-u">
            <div class="components">
               <span class="header">${msg("label.components")}</span>
               <div>
                  <input type="checkbox" id="${el}-records" checked="checked" />
                  <label for="${el}-records">${msg("label.records")}</label>
               </div>
               <div class="indented">
                  <input type="checkbox" id="${el}-undeclared" />
                  <label for="${el}-undeclared">${msg("label.undeclared")}</label>
               </div>
               <div class="indented">
                  <input type="checkbox" id="${el}-vital" />
                  <label for="${el}-vital">${msg("label.vital")}</label>
               </div>
               <div>
                  <input type="checkbox" id="${el}-folders" />
                  <label for="${el}-folders">${msg("label.recordFolders")}</label>
               </div>
               <div>
                  <input type="checkbox" id="${el}-categories" />
                  <label for="${el}-categories">${msg("label.recordCategories")}</label>
               </div>
               <div>
                  <input type="checkbox" id="${el}-frozen" />
                  <label for="${el}-frozen">${msg("label.frozen")}</label>
               </div>
               <div>
                  <input type="checkbox" id="${el}-cutoff" />
                  <label for="${el}-cutoff">${msg("label.cutoff")}</label>
               </div>
            </div>
         </div>
      </div>
   </div>
</div>
