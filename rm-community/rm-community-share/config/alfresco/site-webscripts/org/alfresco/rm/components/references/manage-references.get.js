/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2020 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software.
 * -
 * If the software was purchased under a paid Alfresco license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 * -
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * -
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * -
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
function getDocName(nodeRef)
{
   var result = remote.call("/api/metadata?shortQNames=true&nodeRef=" + nodeRef);
   if (result.status == 200)
   {
      var data = eval('(' + result + ')');
      return data.properties["cm:name"];
   }
   return nodeRef;
}

/*
 * Note, "From" is customreferences from this node and *not* from other documents to this node.
 */ 
function getDocReferences()
{
   var nodeRef = page.url.args.nodeRef.replace(":/", "");
   var result = remote.call("/api/node/" + nodeRef + "/customreferences");
   var processDocRefs = function(docrefs, useTargetRef)
   {
      for (var i = 0, len = docrefs.length; i < len; i++)
      {
         var ref = docrefs[i];
         ref.refDocName = (ref.referenceType == 'parentchild') ? getDocName(ref.childRef) : getDocName(useTargetRef ? ref.targetRef : ref.sourceRef);
         if (ref.referenceType == 'parentchild')
         {
            ref.label = ref.target;
            ref.targetRef = ref.childRef;
            ref.sourceRef = ref.parentRef;
         }
         docrefs[i] = ref;
      }      
      return docrefs;
   };
   
   if (result.status == 200)
   {
      var data = eval('(' + result + ')');
      var docrefs =
      {
         from: processDocRefs(data.data.customReferencesFrom, true),
         to: processDocRefs(data.data.customReferencesTo, false)
      };
      
      return docrefs;
   }
   return [];
}

model.references = getDocReferences();
