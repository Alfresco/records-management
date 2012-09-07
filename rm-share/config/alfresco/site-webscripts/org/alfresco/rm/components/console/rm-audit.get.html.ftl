<#if !hasAccess>
   <#include "./rm-console-access.ftl">
<#else>
<script type="text/javascript">//<![CDATA[
   <#--
   new Alfresco.rm.component.PropertyMenu('${htmlid}-audit-property').setOptions(
   {
      showIdentiferField: true,
      showAllField: true,
      groups: YAHOO.lang.JSON.parse('${jsonUtils.toJSONString(groups)}')
   });
   -->
   new Alfresco.rm.component.RMAudit('${htmlid}-audit').setOptions(
   {
      siteId: "${page.url.templateArgs.site!"rm"}",
      containerId: "${template.properties.container!"documentLibrary"}",
      viewMode: Alfresco.rm.component.RMAudit.VIEW_MODE_DEFAULT,
      enabled : ${enabled?string},
      auditEvents: ${eventsStr},
      capabilities: ${capabilities}
   }).setMessages(${messages});
//]]</script>
  
  <div id="${htmlid}-audit" class="rm-audit">
    <div class="yui-gc">
      <div class="yui-u first">
          <div id="${htmlid}-audit-info" class="rm-audit-info">
             <h2>${msg("label.header-title")}</h2>
          </div>
      </div>
      <div class="rm-audit-details-button">
         <div id="${htmlid}-auditActions" class="rm-auditActions">
            <span class="audit-toggle">
               <span class="yui-button yui-push-button" id="${htmlid}-audit-toggle">
                  <span class="first-child"><button><#if enabled>${msg('label.button-start')}<#else>${msg('label.button-stop')}</#if></button></span>
               </span>
            </span>
            <span class="audit-view">
               <span class="yui-button yui-push-button" id="${htmlid}-audit-view">
                  <span class="first-child"><button>${msg("label.button-view-log")}</button></span>
               </span>
            </span>
            <span class="audit-clear">
               <span class="yui-button yui-push-button" id="${htmlid}-audit-clear">
                  <span class="first-child"><button>${msg("label.button-clear")}</button></span>
               </span>
            </span>
         </div>
      </div>
    </div>
    <div class="yui-gb filters">
       <div class="yui-u first">
          <div id="${htmlid}-entriesFilter" class="filter">
             <div class="hd">
                <label for="${htmlid}-audit-entries">${msg('label.header-entries')}:</label>
             </div>
             <div class="bd">
                <input type="text" name="${htmlid}-audit-entries" value="" id="${htmlid}-audit-entries" />
             </div>
          </div>
       </div>
       <div class="yui-u">
          <div id="${htmlid}-dateFilter" class="filter"> 
             <div class="hd">
                <label for="${htmlid}-audit-fromDate">${msg('label.header-from')}:</label>
             </div>
             <div class="bd">
                <input type="text" name="${htmlid}-audit-fromDate" value="" id="${htmlid}-audit-fromDate" readonly="true"/>
                <a id="${htmlid}-audit-fromDate-icon" class="datepicker-icon">
                   <img class="datepicker-icon" src="${url.context}/res/components/form/images/calendar.png" />
                </a>
                <div id="${htmlid}-audit-fromDate-cal" class="datepicker"></div>
                <label for="${htmlid}-audit-toDate">${msg('label.header-to')}:</label><input type="text"  name="${htmlid}-audit-toDate" value="" id="${htmlid}-audit-toDate"  readonly="true"/>
                <a id="${htmlid}-audit-toDate-icon" class="datepicker-icon">
                   <img class="datepicker-icon" src="${url.context}/res/components/form/images/calendar.png" />
                </a>
                <div id="${htmlid}-audit-toDate-cal" class="datepicker"></div>
             </div>
          </div>
      </div>
      <div class="yui-u">
         <div id="${htmlid}-userFilter" class="filter">
            <div class="hd">
               <label for="specify">${msg('label.header-users')}:</label>
            </div>
            <div class="bd">
               <label for="audit-specifyfilter">${msg("label.show-log-for")}</label> 
               <div id="${htmlid}-audit-personFilter" class="personFilter"><span>${msg("label.all-users")}</span><a id="${htmlid}-personFilterRemove" class="personFilterRemove"><img src="${page.url.context}/res/components/images/remove-icon-16.png"  alt="${msg('label.remove-filter')}"/></a></div>
               <span class="audit-specifyfilter">
                  <span class="yui-button yui-push-button" id="${htmlid}-audit-specifyfilter">
                     <span class="first-child"><button>${msg("label.button-specify")}</button></span>
                  </span>
               </span>
               <div id="${htmlid}-audit-peoplefinder" class="rm-audit-peoplefinder"></div>
            </div>
         </div>
      </div>
   </div>
   <div class="yui-g">
      <div class="yui-u first">&nbsp;</div>
      <div class="rm-audit-details-button">
         <div class="rm-audit-apply-filter">
            <div class="hd">
               <span class="audit-apply">
                  <span class="yui-button yui-push-button" id="${htmlid}-audit-apply">
                     <span class="first-child"><button>${msg('label.button-apply')}</button></span>
                  </span>
               </span>
            </div>
         </div>
      </div>
   </div>
   <div id="${htmlid}-audit-log" class="yui-gc audit-log">
      <div class="yui-u first">&nbsp;</div>
      <div class="rm-audit-details-button">
         <span class="audit-export">
            <span class="yui-button yui-push-button" id="${htmlid}-audit-export">
               <span class="first-child"><button>${msg("label.button-export")}</button></span>
            </span>
         </span>
         <span class="audit-file-record">
            <span class="yui-button yui-push-button" id="${htmlid}-audit-file-record">
               <span class="first-child"><button>${msg("label.button-file-record")}</button></span>
            </span>
         </span>
      </div>
   </div>
   <div class="yui-gc">
      <div id="${htmlid}-audit-auditDT" class="rm-auditDT"></div>
    </div>
  </div>
</#if>
