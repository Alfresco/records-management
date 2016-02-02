<import resource="classpath:/alfresco/site-webscripts/org/alfresco/config/components/recordDetailUtils.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/document-details/document-versions.get.js">

if (!disableRecordDetailsComponent("allowNewVersionUpload"))
{
   model.widgets[0].name = "Alfresco.rm.DocumentVersions";
}