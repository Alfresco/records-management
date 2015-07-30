<#if form.mode == "view">
   <div class="form-field">
      <span class="viewmode-label">${field.label?html}:</span>
      <#if field.control.params.property?has_content && form.fields[field.control.params.property]??>
         <span class="viewmode-value" id="${fieldHtmlId}" onmouseover='javascript:new Alfresco.util.createInfoBalloon("${fieldHtmlId}", {html: "<ul><#list form.fields[field.control.params.property].value?split("|,") as x><li>${x}</li></#list></ul>", wrapperClass: "classification-reason-info-balloon"}).show();'>${field.value?html}</span>
      <#else>
         <span class="viewmode-value" id="${fieldHtmlId}">${msg("form.control.novalue")}</span>
      </#if>
   </div>
</#if>