<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   var nodeDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
   if (nodeDetails && nodeDetails.item.node.isRmNode)
   {
      model.allowCompleteEvent = false;
      model.allowUndoEvent = false;
      var actions = nodeDetails.item.node.rmNode.actions;
      for (var i = 0; i < actions.length; i++)
      {
         if (actions[i] == "completeEvent")
         {
            model.allowCompleteEvent = true;
         }
         if (actions[i] == "undoEvent")
         {
            model.allowUndoEvent = true;
         }
      }
   }
   else
   {
      // Signal to the template that the node doesn't exist and that events therefore shouldn't be displayed.
      model.nodeRef = null;
   }
}

main();
