<script type="text/javascript">//<![CDATA[
   new Alfresco.rm.dashlet.Management("${args.htmlid}").setMessages(${messages});
//]]></script>
<div class="dashlet">
   <div class="title">${msg("label.title")}</div>
   <div class="body theme-color-1">
      <#if isRmAdmin>
         <div class="detail-list-item-alt theme-bg-color-2 theme-color-2 management-description">
            <h4>${msg("label.summary")}</h4>
         </div>
         <div id="${args.htmlid}-display-site" class="detail-list-item" <#if !foundsite>style="display:none"</#if>>
            <a href="${url.context}/page/site/rm/dashboard">${msg("label.display-site")}</a>
         </div>
         <div id="${args.htmlid}-create-site" class="detail-list-item" <#if foundsite>style="display:none"</#if>>
            <a id="${args.htmlid}-create-site-link" href="#">${msg("label.create-site")}</a>
         </div>
         <div id="${args.htmlid}-rm-console" class="detail-list-item last-item" <#if !foundsite>style="display:none"</#if>>
            <a id="${args.htmlid}-role-report-link" href="${url.context}/page/console/rm-console/">${msg("label.rm-console")}</a>
         </div>
      <#else>
         <div id="${args.htmlid}-not-privilaged" class="management-not-rm-admin">
            ${msg("label.not-rm-admin")}
         </div>
      </#if>
   </div>
</div>
