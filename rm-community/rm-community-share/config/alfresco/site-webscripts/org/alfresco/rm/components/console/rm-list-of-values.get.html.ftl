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
<#if !hasAccess>
   <#include "./rm-console-access.ftl">
<#else>
<!--[if IE]>
<iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe>
<![endif]-->
<input id="yui-history-field" type="hidden" />

<script type="text/javascript">//<![CDATA[
   new Alfresco.rm.component.RMListOfValues("${args.htmlid}").setMessages(
      ${messages}
   );
//]]></script>

<#assign el=args.htmlid>
<div id="${el}-body" class=".rm-list-of-values">

   <!-- View panel -->
   <div id="${el}-view" class="hidden">
      <!-- Title -->
      <div class="yui-g">
         <div class="yui-u first title">
            ${msg("label.view-listofvalues-title")}
         </div>
         <div class="yui-u buttons">
            <span class="yui-button yui-push-button" id="${el}-newlist-button">
               <span class="first-child"><button>${msg("button.newlist")}</button></span>
            </span>
         </div>
      </div>
      <!-- Lists -->
      <div id="${el}-listofvalues" class=".rm-list-of-values-list"></div>


   </div>

   <!-- Edit panel -->
   <div id="${el}-edit" class="hidden">
      <!-- Title -->
      <div id="${el}-edittitle" class="yui-u first title">
         ${msg("label.edit-listofvalue-title", "")}
      </div>

      <div class="yui-g list-header">
         <div class="yui-gf first">
            <!-- Values -->
            <div class="list-header-button">
               <span class="yui-button yui-push-button" id="${el}-newvalue-button">
                  <span class="first-child"><button>${msg("button.add")}</button></span>
               </span>
            </div>
            <div class="list-header-input">
               <input id="${el}-newvalue-input" type="text" value=""/>
            </div>
            <div class="list-header-title">${msg("label.values")}</div>
         </div>
         <div class="yui-gf">
            <!-- Access -->
            <div class="list-header-button">
               <span class="yui-button yui-push-button" id="${el}-addaccess-button">
                  <span class="first-child"><button>${msg("button.add")}</button></span>
               </span>
            </div>
            <div class="list-header-title">${msg("label.access")}</div>
         </div>
      </div>

      <div class="yui-g list-body">
         <div class="yui-u first">
            <!-- Values -->
            <div id="${el}-values" class="values-list"></div>
         </div>
         <div class="yui-u">
            <!-- Access -->
            <div id="${el}-access" class="access-list"></div>
         </div>
      </div>

      <!-- Done -->
      <div class="main-buttons">
         <hr />
         <span id="${el}-done-button" class="yui-button done">
             <span class="first-child">
                 <button>${msg("button.done")}</button>
             </span>
         </span>
      </div>

      <!-- Auhtority/Access Dialog -->
      <div id="${el}-authoritypicker" class=".rm-list-of-values authority-picker">
         <div class="hd"><span id="${el}-authoritypicker-title">${msg("panel.addaccess.header")}</span></div>
         <div class="bd rm-search-body">
            <div>
               <div id="${el}-search-authorityfinder"></div>
            </div>
         </div>
      </div>

   </div>

</div>
</#if>
