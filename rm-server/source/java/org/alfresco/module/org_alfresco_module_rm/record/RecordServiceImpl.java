/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.module.org_alfresco_module_rm.record;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_rm.identifier.IdentifierService;
import org.alfresco.module.org_alfresco_module_rm.model.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_rm.security.RecordsManagementSecurityService;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.ParameterCheck;

/**
 * Record service implementation
 * 
 * @author Roy Wetherall
 * @since 2.1
 */
public class RecordServiceImpl implements RecordService, RecordsManagementModel
{
   /** Node service **/
   private NodeService nodeService;

   /** Indentiy service */
   private IdentifierService identifierService;

   /** Dictionary service */
   private DictionaryService dictionaryService;

//   /** Policy component */
//   private PolicyComponent policyComponent;

   /** Permission service */
   private PermissionService permissionService;

   /** Records management security service */
   private RecordsManagementSecurityService recordsManagementSecurityService;

   /** List of available record meta-data aspects */
   private Set<QName> recordMetaDataAspects;

   public void setNodeService(NodeService nodeService)
   {
      this.nodeService = nodeService;
   }

   public void setIdentifierService(IdentifierService identifierService)
   {
      this.identifierService = identifierService;
   }

   public void setDictionaryService(DictionaryService dictionaryService)
   {
      this.dictionaryService = dictionaryService;
   }

//   public void setPolicyComponent(PolicyComponent policyComponent)
//   {
//      this.policyComponent = policyComponent;
//   }

   public void setPermissionService(PermissionService permissionService)
   {
      this.permissionService = permissionService;
   }

   public void setRecordsManagementSecurityService(RecordsManagementSecurityService recordsManagementSecurityService)
   {
      this.recordsManagementSecurityService = recordsManagementSecurityService;
   }

   public void init()
   {
//       policyComponent.bindAssociationBehaviour(
//               QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateChildAssociation"), 
//               TYPE_UNFILED_RECORD_CONTAINER, 
//               ContentModel.ASSOC_CONTAINS,
//               new JavaBehaviour(this, "onCreateNewRecord", NotificationFrequency.TRANSACTION_COMMIT));
   }

//   public void onCreateNewRecord(final ChildAssociationRef childAssocRef, boolean bNew)
//   {
//      AuthenticationUtil.runAsSystem(new RunAsWork<Void>()
//      {
//         @Override
//         public Void doWork() throws Exception
//         {
//            NodeRef nodeRef = childAssocRef.getChildRef();
//            if (nodeService.exists(nodeRef) == true)
//            {
//               QName type = nodeService.getType(nodeRef);
//               if (ContentModel.TYPE_CONTENT.equals(type) == true ||
//                     dictionaryService.isSubClass(type, ContentModel.TYPE_CONTENT) == true)
//               {
//                  makeRecord(nodeRef);
//               }
//               else
//               {
//                  throw new AlfrescoRuntimeException("Only content can be created as a record.");
//               }
//            }
//            return null;
//         }
//      });
//    }

   /**
    * @see org.alfresco.module.org_alfresco_module_rm.record.RecordService#getRecordMetaDataAspects()
    */
   @Override
   public Set<QName> getRecordMetaDataAspects() 
   {
      if (recordMetaDataAspects == null)
      {
         recordMetaDataAspects = new HashSet<QName>(7);
         Collection<QName> aspects = dictionaryService.getAllAspects();
         for (QName aspect : aspects) 
         {
            AspectDefinition def = dictionaryService.getAspect(aspect);
            if (def != null)
            {
               QName parent = def.getParentName();
               if (parent != null && ASPECT_RECORD_META_DATA.equals(parent) == true)
               {
                  recordMetaDataAspects.add(aspect);
               }
            }
         }
      }
      return recordMetaDataAspects;
   }

   /**
    * @see org.alfresco.module.org_alfresco_module_rm.record.RecordService#isRecord(org.alfresco.service.cmr.repository.NodeRef)
    */
   @Override
   public boolean isRecord(NodeRef nodeRef)
   {
      ParameterCheck.mandatory("nodeRef", nodeRef);

      return nodeService.hasAspect(nodeRef, ASPECT_RECORD);
   }

   /**
    * @see org.alfresco.module.org_alfresco_module_rm.record.RecordService#isDeclared(org.alfresco.service.cmr.repository.NodeRef)
    */
   @Override
   public boolean isDeclared(NodeRef record)
   {
      ParameterCheck.mandatory("record", record);

      return nodeService.hasAspect(record, ASPECT_DECLARED_RECORD);
   } 

   /**
    * @see org.alfresco.module.org_alfresco_module_rm.record.RecordService#createRecordFromDocument(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.cmr.repository.NodeRef)
    */
   @Override
   public void createRecordFromDocument(NodeRef filePlan, NodeRef document)
   {
      ParameterCheck.mandatory("filePlan", filePlan);
      ParameterCheck.mandatory("document", document);

      // skip everything if the document is already a record
      if (nodeService.hasAspect(document, ASPECT_RECORD) == false)
      {
         // get the new record container for the file plan
         NodeRef newRecordContainer = getUnfiledRecordContainer(filePlan);
         if (newRecordContainer == null)
         {
            throw new AlfrescoRuntimeException("Unable to create record, because new record container could not be found.");
         }

         // get the documents primary parent assoc
         ChildAssociationRef parentAssoc = nodeService.getPrimaryParent(document);

         // move the document into the file plan
         nodeService.moveNode(document, newRecordContainer, ContentModel.ASSOC_CONTAINS, parentAssoc.getQName());

         // maintain the original primary location
         nodeService.addChild(parentAssoc.getParentRef(), document, parentAssoc.getTypeQName(), parentAssoc.getQName());

         // make the document a record
         makeRecord(document);

         // get the documents readers
         Long aclId = nodeService.getNodeAclId(document);
         Set<String> readers = permissionService.getReaders(aclId);

         // set the readers
         recordsManagementSecurityService.setExtendedReaders(document, readers);
      }
   }

   /**
    * @see org.alfresco.module.org_alfresco_module_rm.record.RecordService#getUnfiledRecordContainer(org.alfresco.service.cmr.repository.NodeRef)
    */
   @Override
   public NodeRef getUnfiledRecordContainer(NodeRef filePlan)
   {
      ParameterCheck.mandatory("filePlan", filePlan);

      List<ChildAssociationRef> assocs = nodeService.getChildAssocs(filePlan, ASSOC_UNFILED_RECORDS, RegexQNamePattern.MATCH_ALL);
      if (assocs.size() != 1)
      {
         throw new AlfrescoRuntimeException("Error getting the unfiled container, because the container cannot be indentified.");
      }

      return assocs.get(0).getChildRef();
   }

   /**
    * Helper Methods
    */

   /**
    * Creates a record from the given document
    * 
    * @param document   the document from which a record will be created
    */
   private void makeRecord(NodeRef document)
   {
      nodeService.addAspect(document, RecordsManagementModel.ASPECT_RECORD, null);

      String recordId = identifierService.generateIdentifier(ASPECT_RECORD, nodeService.getPrimaryParent(document).getParentRef());
      nodeService.setProperty(document, PROP_IDENTIFIER, recordId);
   }
}
