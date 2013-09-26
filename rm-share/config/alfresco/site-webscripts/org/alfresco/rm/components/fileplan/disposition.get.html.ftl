<script type="text/javascript">//<![CDATA[
   new Alfresco.rm.component.Disposition("${args.htmlid}").setOptions(
   {
      nodeRef: "${(nodeRef!"")?js_string}",
      displayName: "${(displayName!"")?js_string}",
      siteId: "${site!""}",
      dipositionScheduleNodeRef: "${dipositionScheduleNodeRef!""}"
   }).setMessages(
      ${messages}
   );

//]]></script>
<#assign el=args.htmlid>

<div class="disposition">

   <div class="heading">${msg("disposition-schedule.heading")}</div>

<#if hasDispositionSchedule>
   <div>
      <div class="header">
         <div class="title">${msg("title.properties")}</div>
         <div class="buttons">
            <#if allowEditDispositionSchedule>
            <span id="${el}-editproperties-button" class="yui-button inline-button">
               <span class="first-child">
                  <button type="button">${msg("button.edit")}</button>
               </span>
            </span>
            </#if>
         </div>
      </div>

      <div class="properties">
         <div class="field">
            <span class="label">${msg("label.dispositionAuthority")}:</span>
            <span class="value">${(authority!"")?html}</span>
         </div>
         <div class="field">
            <span class="label">${msg("label.dispositionInstructions")}:</span>
            <#assign instructionsValue=instructions?html?replace("(\n)", "<br/>",'r')?replace("((http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?\\^=%&:\\/~\\+#]*[\\w\\-\\@?\\^=%&\\/~\\+#])?)", "<a href=\"$1\" target=\"_blank\">$1</a>", "r")>
            <span class="value">${(instructionsValue!"")}</span>
         </div>
         <div class="field">
            <span class="label">${msg("label.appliedTo")}:</span>
            <span class="value"><#if (recordLevelDisposition)>${msg("label.appliedTo.record")}<#else>${msg("label.appliedTo.folder")}</#if></span>
         </div>
         <div class="field">
            <span class="label">${msg("label.unpublishedUpdates")}:</span>
            <span class="value">${unpublishedUpdates?string("Yes", "No")}</span>
         </div>	
         
      </div>

      <div class="header">
         <div class="title">${msg("title.actions")}</div>
         <div class="buttons">
            <#if allowEditDispositionSchedule>
            <span id="${el}-editschedule-button" class="yui-button inline-button">
               <span class="first-child">
                  <button type="button">${msg("button.edit")}</button>
               </span>
            </span>
            </#if>
         </div>
      </div>

      <div id="${el}-actions" class="actions">
      <#if (actions?size > 0)>
         <#list actions as action>
         <div class="action">
            <div class="no">${action.index + 1}</div>
            <div class="more collapsed"><a href="#">${msg("link.description")}</a></div>
            <div class="name">${action.title}</div>
            <div class="description" style="display: none;"><#if (action.description?has_content)>${action.description?html}<#else>${msg("label.nodescription")}</#if></div>
         </div>
         </#list>
      <#else>
         ${msg("label.noactions")}
      </#if>
      </div>
   </div>
<#else>
   <div>
      <div class="header">
         <div class="title">${msg("title.noDispositionSchedule")}</div>
         <div class="buttons">
            <#if allowCreateDispositionSchedule>
            <span id="${el}-createschedule-button" class="yui-button inline-button">
               <span class="first-child">
                  <button type="button">${msg("button.createDispositionSchedule")}</button>
               </span>
            </span>
            </#if>
         </div>
      </div>
   </div>
</#if>

</div>
