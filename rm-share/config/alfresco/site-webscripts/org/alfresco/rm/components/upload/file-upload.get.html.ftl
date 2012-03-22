<!-- Pre-requisite: flash-upload and html-upload components are also included on the page -->
<#assign fileUploadConfig = config.scoped["DocumentLibrary"]["file-upload"]!>
<#if fileUploadConfig.getChildValue??>
   <#assign adobeFlashEnabled = fileUploadConfig.getChildValue("adobe-flash-enabled")!"true">
</#if>
<script type="text/javascript">//<![CDATA[
new Alfresco.getRecordsFileUploadInstance().setOptions(
{
   adobeFlashEnabled: ${((adobeFlashEnabled!"true") == "true")?string},
   flashUploader: "Alfresco.rm.component.FlashUpload",
   htmlUploader: "Alfresco.rm.component.HtmlUpload"
});
//]]></script>
