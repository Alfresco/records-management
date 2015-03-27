<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

var namesToGet =
{
   to:[],
   from:[]
};

function getDocNames(nodeRefs)
{
   if (nodeRefs.length > 0)
   {
      var docNames = [],
         connector = remote.connect("alfresco");
         result = connector.post("/api/forms/picker/items", jsonUtils.toJSONString(
         {
            "items": nodeRefs
         }), "application/json");

      model.result = result;
      if (result.status == 200)
      {
         var data = eval('(' + result + ')'),
            items = data.data.items;

         if (items.length > 0)
         {
            for (var i = 0, len = items.length; i < len; i++)
            {
               docNames.push(items[i].name);
            }
            return docNames;
         }
      }
      return [];
   }
}

/*
 * Note, "From" is customreferences from this node and *not* from other documents to this node.
 * @returns {Object}
 *  {
 *    toThisNode : [] // array of references to this node
 *    fromThisNode : [] // array of references from this node
 *  }
 */
function getDocReferences(nodeRef)
{
   var result = remote.call("/api/node/" + nodeRef.replace(":/", "") + "/customreferences");

   var marshallDocRefs = function marshallDocRefs(docrefs, type)
   {
      if (type == 'from')
      {
         labelField = 'source';
      }
      else
      {
         labelField = 'target';
      }
      for (var i = 0, len = docrefs.length; i < len; i++)
      {
         var ref = docrefs[i],
            refField,
            labelField;

         if (ref.referenceType == 'parentchild')
         {
            if (type == 'from')
            {
               refField = 'childRef';
               labelField = 'target';
            }
            else
            {
               refField = 'parentRef';
               labelField = 'source';
            }
         }
         else
         {
            if (type == 'from')
            {
               refField = 'targetRef';
            }
            else
            {
               refField = 'sourceRef';
               ref.targetRef = ref.sourceRef;
            }
         }

         // We have to get document names since this api call doesn't return them.
         // Collect an array of noderefs and get the names in a later request.
         namesToGet[type].push(ref[refField]);
         if (ref.referenceType == 'parentchild')
         {
            ref.label = ref[labelField];
            ref.targetRef = ref[refField];
         }
         docrefs[i]=ref;
      }

      return docrefs;
   };

   if (result.status == 200)
   {
      var data = eval('(' + result + ')');
      return (
      {
         toThisNode: marshallDocRefs(data.data.customReferencesTo, 'to'),
         fromThisNode: marshallDocRefs(data.data.customReferencesFrom, 'from')
      });
   }

   return (
   {
      toThisNode: [],
      fromThisNode: []
   });
}

function main()
{
   AlfrescoUtil.param("nodeRef");
   AlfrescoUtil.param("site", null);
   AlfrescoUtil.param("container", "documentLibrary");

   var nodeDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site, null);
   if (nodeDetails && nodeDetails.item.node.isRmNode)
   {
      model.references = getDocReferences(model.nodeRef);
      model.docNames = {};
      model.docNames.to = getDocNames(namesToGet.to);
      model.docNames.from = getDocNames(namesToGet.from);

      //model.parentNodeRef = nodeDetails.item.location.container.nodeRef;
      model.parentNodeRef = nodeDetails.item.node.rmNode.filePlan;
      model.docName = nodeDetails.item.displayName;
      model.allowEditReferences = false;
      var actions = nodeDetails.item.node.rmNode.actions;
      for (var i = 0; i < actions.length; i++)
      {
         if (actions[i] == "editReferences")
         {
            model.allowEditReferences = true;
         }
      }
   }
   else
   {
      model.docName = null;
   }
}

main();
