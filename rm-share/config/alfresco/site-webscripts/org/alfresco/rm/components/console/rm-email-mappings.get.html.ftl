<#if !hasAccess>
   <#include "./rm-console-access.ftl">
<#else>
<script type="text/javascript">//<![CDATA[
   new Alfresco.rm.component.PropertyMenu('${htmlid}-rmproperty-button').setOptions(
   {
      showIMAPFields: true,
      groups: YAHOO.lang.JSON.parse('${jsonUtils.toJSONString(groups)}')
   });
   new Alfresco.rm.component.RMEmailMappings('${htmlid}').setOptions(
   {
      email:
      [
      <#list emailmapkeys as emailmapkey>
         '${emailmapkey}'<#if emailmapkey_has_next>,</#if>
      </#list>
      ]
   }).setMessages(${messages});
//]]</script>

<div id="${htmlid}" class="rm-email-mappings">
   <div>
      <h2>${msg('label.email-mappings')}</h2>
      <div>
         <span>${msg('label.map')}</span>
         <input type="text" name="emailProperty-text" value="" id="emailProperty-text" />
         <button id="emailProperty-but" name="emailProperty-but" class="thin-button"><img src="${page.url.context}/res/components/images/expanded.png" title="${msg('label.select-email')}"/></button>
         <div id="email-menu-container"></div>
         <span>${msg('label.to')}</span>
         <button id="${htmlid}-rmproperty-button" name="rmproperty" class="thin-button">${msg("message.select")}</button>
         <button id="add-mapping" name="email-add" class="thin-button" disabled>${msg('label.add')}</button>
      </div>
   </div>
   <div id="emailMappings-list" class="rm-email-mappings-list">
      <ul>
      </ul>
   </div>
</div>
</#if>
