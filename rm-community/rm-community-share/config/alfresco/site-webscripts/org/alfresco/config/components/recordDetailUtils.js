<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2019 Alfresco Software Limited
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

/*
 * FIXME: Performance issue!?! We should not call AlfrescoUtil.getNodeDetails every time.
 * This can be done in the core and the value can be saved to the model. Which will save
 * the import of a resource and another method call.
 */

function disableRecordDetailsComponent(value)
{
   var disabled = false;
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
         disabled = true;
      }
   }
   return disabled;
}
