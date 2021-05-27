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
<#assign el=args.htmlid>
<script type="text/javascript">//<![CDATA[
   new Alfresco.rm.component.PropertyMenu('${el}-insertfield').setOptions(
   {
      isEnterprise: ${isEnterprise?c},
      showSearchFields: true,
      showIdentiferField: true,
      updateButtonLabel: false,
      groups: ${jsonUtils.toJSONString(groups)}
   }).setMessages(${messages});
   new Alfresco.rm.component.Search("${el}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      groups: ${jsonUtils.toJSONString(groups)}
   }).setMessages(${messages});
//]]></script>

<div id="${el}-body" class="rm-search">
   <div class="yui-g" id="${el}-header">
      <div class="yui-u first">
         <div class="title">${msg("label.searchtitle")}</div>
      </div>
      <div class="yui-u topmargin">
         <!-- New Search button -->
         <div class="right-button">
            <span class="yui-button yui-push-button" id="${el}-newsearch-button">
               <span class="first-child"><button>${msg("button.newsearch")}</button></span>
            </span>
         </div>

         <!-- Save Search button -->
         <div class="right-button">
            <span class="yui-button yui-push-button" id="${el}-savesearch-button">
               <span class="first-child"><button>${msg("button.savesearch")}</button></span>
            </span>
         </div>

         <!-- Delete Search button -->
         <div class="right-button">
            <span class="yui-button yui-push-button" id="${el}-deletesearch-button">
               <span class="first-child"><button>${msg("button.deletesearch")}</button></span>
            </span>
         </div>

         <!-- Saved Searches menu button -->
         <div class="right-button">
            <span class="yui-button yui-push-button" id="${el}-savedsearches-button">
               <span class="first-child"><button>${msg("button.savedsearches")}&nbsp;&#9662;</button></span>
            </span>
         </div>
      </div>
   </div>

   <div id="${el}-tabs" class="yui-navset">
      <ul class="yui-nav" id="${el}-tabset">
         <li class="selected"><a href="#${el}-critera-tab"><em>${msg("label.criteria")}</em></a></li>
         <li><a href="#${el}-results-tab"><em>${msg("label.results")}</em></a></li>
      </ul>
      <div class="yui-content tab-content">
         <div id="${el}-critera-tab" class="terms">
            <span class="header">${msg("label.searchterm")}</span>
            <div>
               <span class="insertLabel">${msg("label.insertfield")}:</span>
               <span>
                  <input id="${el}-insertfield" type="button" name="insertfield" value="${msg("label.select")}&nbsp;&#9662;" />
               </span>
               <span class="insertDate">${msg("label.insertdate")}:</span>
               <div id="${el}-date" class="datepicker"></div>
               <a id="${el}-date-icon"><img src="${url.context}/res/components/form/images/calendar.png" class="datepicker-icon"/></a>
            </div>
            <div class="query">
               <!-- Query terms text input -->
               <textarea id="${el}-terms" rows="2" cols="40"></textarea>
            </div>
            <#include "options.ftl" />
            <div class="execute-search">
               <div class="rm-search-button">
                  <span class="yui-button yui-push-button" id="${el}-search-button">
                     <span class="first-child"><button>${msg("button.search")}</button></span>
                  </span>
               </div>
            </div>
         </div>

         <div id="${el}-results-tab">
            <div class="yui-g">
               <div class="yui-u first">
                  <span id="${el}-itemcount"></span>
               </div>
               <div class="yui-u alignright">
                  <span class="add-to-hold-button">
                     <span class="yui-button yui-push-button" id="${el}-add-to-hold-button">
                        <span class="first-child"><button>${msg("button.add-to-hold")}</button></span>
                     </span>
                  </span>
                  <span class="print-button">
                     <span class="yui-button yui-push-button" id="${el}-print-button">
                        <span class="first-child"><button>${msg("button.print")}</button></span>
                     </span>
                  </span>
                  <span class="export-button">
                     <span class="yui-button yui-push-button" id="${el}-export-button">
                        <span class="first-child"><button>${msg("button.export")}</button></span>
                     </span>
                  </span>
               </div>
            </div>
            <!-- results inserted here into YUI Datagrid -->
            <div id="${el}-results" class="rm-results"></div>
         </div>
      </div>
   </div>
</div>
