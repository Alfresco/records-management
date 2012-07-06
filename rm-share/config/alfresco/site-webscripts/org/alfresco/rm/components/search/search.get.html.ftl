<#assign el=args.htmlid>
<script type="text/javascript">//<![CDATA[
   new Alfresco.rm.component.PropertyMenu('${el}-insertfield').setOptions(
   {
      showSearchFields: true,
      showIdentiferField: true,
      updateButtonLabel: false,
      groups: YAHOO.lang.JSON.parse('${jsonUtils.toJSONString(groups)}')
   }).setMessages(${messages});
   new Alfresco.rm.component.Search("${el}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      groups: YAHOO.lang.JSON.parse('${jsonUtils.toJSONString(groups)}')
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
               <span class="first-child"><button>${msg("button.savedsearches")}</button></span>
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
                  <input id="${el}-insertfield" type="button" name="insertfield" value="${msg("label.select")}" />
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
