<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

/*
 * FIXME: Performance issue!?! We should not call AlfrescoUtil.getNodeDetails every time.
 * This can be done in the core and the value can be saved to the model. Which will save
 * the import of a resource and another method call.
 */

function disableRecordDetailsComponent(value)
{
   var documentDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
   if (documentDetails != null)
   {
      var item = documentDetails.item;
      if (model.site != (item.location.site != null ? item.location.site.name : null) && item.node.isRmNode)
      {
         model[value] = null;
         if (value == "allowNewVersionUpload")
         {
            model.exist = false;
         }
      }
   }
}