<#if !hasAccess>
   <#include "./rm-console-access.ftl">
<#else>
<script type="text/javascript">//<![CDATA[
   new Alfresco.rm.component.PropertyMenu('${htmlid}-rmproperty-button').setOptions(
   {
      showIMAPFields: true,
      groups: ${jsonUtils.toJSONString(groups)}
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
         <input type="text" name="emailProperty-text" value="" id="emailProperty-text" style="width:200px;"/>
         <span id="${htmlid}-emailProperty-button" class="align-left yui-button yui-push-button">
            <span class="first-child">
               <button id="${htmlid}-emailProperty-button" type="button" class="thin-button"><img src="${page.url.context}/res/components/images/expanded.png" title="${msg('label.select-email')}"/></button>
            </span>
         </span>
         <div id="email-menu-container"></div>
         <span>${msg('label.to')}</span>
         <button id="${htmlid}-rmproperty-button">${msg("message.select")}</button>
         <span id="${htmlid}-add-mapping-button" class="align-left yui-button yui-push-button">
            <span class="first-child">
               <button id="${htmlid}-add-mapping-button" type="button">${msg('label.add')}</button>
            </span>
         </span>
      </div>
   </div>
   <div id="emailMappings-list" class="rm-email-mappings-list">
      <ul>
      </ul>
   </div>
</div>
</#if>