<#if form.mode == "view">
   <div class="form-field">
      <span class="viewmode-label">${field.label?html}:</span>
      <span class="viewmode-value" id="${fieldHtmlId}" onmouseover='javascript:new Alfresco.util.createInfoBalloon("${fieldHtmlId}", {html: "<ul><#list "${form.fields.prop_clfClassificationReasonLabels.value}"?split("|,") as x><li>${x}</li></#list></ul>", wrapperClass: "classification-reason-info-balloon"}).show();'>${field.value?html}</span>
   </div>
</#if>