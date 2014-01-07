<#--
Added the readonly attribute and the colors for the style. If you set the read-only parameter
in rm-share-workflow-form-config.xml for a field the control template becomes disabled which
means the user will not be able to copy any text form the textarea.
-->

<#if field.control.params.rows??><#assign rows=field.control.params.rows><#else><#assign rows=3></#if>
<#if field.control.params.columns??><#assign columns=field.control.params.columns><#else><#assign columns=60></#if>

<div class="form-field">
   <#if form.mode == "view">
      <div class="viewmode-field">
         <#if field.mandatory && field.value == "">
            <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
         </#if>
         <span class="viewmode-label">${field.label?html}:</span>
         <#if field.control.params.activateLinks?? && field.control.params.activateLinks == "true">
            <#assign fieldValue=field.value?html?replace("((http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?\\^=%&:\\/~\\+#]*[\\w\\-\\@?\\^=%&\\/~\\+#])?)", "<a href=\"$1\" target=\"_blank\">$1</a>", "r")>
         <#else>
            <#assign fieldValue=field.value?html>
         </#if>
         <span class="viewmode-value"><#if fieldValue == "">${msg("form.control.novalue")}<#else>${fieldValue}</#if></span>
      </div>
   <#else>
      <label for="${fieldHtmlId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
      <textarea id="${fieldHtmlId}" name="${field.name}" rows="${rows}" cols="${columns}" tabindex="0" readonly="true"
                <#if field.description??>title="${field.description}"</#if>
                <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
                <#assign readOnlyColorStyle="color: #808080;background-color: #D4D0C8">
                <#if field.control.params.style??>
                   style="${readOnlyColorStyle};${field.control.params.style}"
                <#else>
                   style="${readOnlyColorStyle}"
               </#if>
            <#if field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if>>${field.value?html}</textarea>
      <@formLib.renderFieldHelp field=field />
   </#if>
</div>