package org.alfresco.module.org_alfresco_module_rm_share.resolver.doclib;

import org.alfresco.web.resolver.doclib.DefaultDoclistActionGroupResolver;
import org.json.simple.JSONObject;

/**
 * Once RM works with Server/Share Version 4.2b and above
 *  <br>
 *  <li>this Java Class</li><br>
 *  <li>the actionGroupResolver "resolver.rm.doclib.actionGroupFix" in rm-share-config.xml</li><br>
 *  <li>and the bean "resolver.rm.doclib.actionGroupFix" in rm-context.xml</li><br>
 *  <br>
 *  can be deleted.<br>
 *  <br>
 *  This actionGroupResolver fixes a bug (RM-512) which is fixed in r42733<br>
 *  <br>
 *  @see FilePlanDoclistActionGroupResolver
 */
public class FilePlanDoclistActionGroupResolverFix extends DefaultDoclistActionGroupResolver
{
   @Override
   public String resolve(JSONObject jsonObject, String view)
   {
      JSONObject node = (org.json.simple.JSONObject) jsonObject.get("node");
      Boolean isRmNode = (Boolean) node.get("isRmNode");
      if (isRmNode != null && isRmNode.booleanValue())
      {
         String actionGroupId = "rm-";
         boolean isLink = (Boolean) node.get("isLink");
         if (isLink)
         {
            actionGroupId += "link-";
         }
         else
         {
            JSONObject rmNode = (JSONObject) node.get("rmNode");
            actionGroupId += (String) rmNode.get("uiType") + "-";
         }
         if (view.equals("details"))
         {
            actionGroupId += "details";
         }
         else
         {
            actionGroupId += "browse";
         }
         return actionGroupId;          
      }
      else
      {
         return super.resolve(jsonObject, view);
      }
   }
}
