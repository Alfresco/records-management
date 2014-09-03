<#assign el=args.htmlid>
<div id="${el}-dialog" class="rm-recorded-version-config">
   <div class="hd">${msg("header")}</div>
   <div class="bd">
      <form id="${el}-form" action="" method="POST">
         <div class="yui-gd">
            <div class="yui-u first" style="padding-right:0.4em;padding-top:0.6em"><label for="${el}-recordedVersions">${msg("label.recorded-versions")}:</label></div>
            <div class="yui-u">
               <select id="${el}-recordedVersions" name="recordedVersion">
                  <#list recordableVersions as recordableVersion>
                     <option value="${recordableVersion.policy?js_string}" <#if recordableVersion.selected == "true">selected</#if>>${msg("option." + recordableVersion.policy?js_string?lower_case + ".displayValue")}</option>
                  </#list>
               </select>
            </div>
         </div>
         <div class="bdft">
            <input type="button" id="${el}-ok" value="${msg("button.ok")}" tabindex="0" />
            <input type="button" id="${el}-cancel" value="${msg("button.cancel")}" tabindex="0" />
         </div>
      </form>
   </div>
</div>
