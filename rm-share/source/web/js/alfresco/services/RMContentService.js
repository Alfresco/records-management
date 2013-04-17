/**
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
define(["dojo/_base/declare",
        "alfresco/services/ContentService",
        "dojo/_base/lang"],
        function(declare, ContentService, lang) {

   return declare([ContentService], {

      /**
       * Re-use the old Alfresco.rm.component.DocListToolbar scope.
       */
      i18nScope: "Alfresco.rm.component.DocListToolbar",

      /**
       * Re-use the toolbar properties for the DocumentList - this gives us access to the same labels for folders, etc.
       */
      i18nRequirements: [{i18nFile: "../../../WEB-INF/classes/alfresco/site-webscripts/org/alfresco/rm/components/documentlibrary/toolbar.get.properties"}],

      /**
       * Sets up the subscriptions for the RMContentService
       *
       * @constructor
       * @param {array} args Constructor arguments
       */
      constructor: function alfresco_services_RMContentService__constructor(args) {
         this.alfSubscribe("ALF_CURRENT_NODEREF_CHANGED", lang.hitch(this, "handleCurrentNodeChange"));
         this.alfSubscribe("ALF_CREATE_NEW_RM_CATEGORY", lang.hitch(this, "createNewRmCategory"));
         this.alfSubscribe("ALF_CREATE_NEW_RM_FOLDER", lang.hitch(this, "createNewRmFolder"));
      },

     /**
      *
      * @method createNewRmCategory
      */
      createNewRmCategory: function alfresco_services_RMContentService__createNewRmCategory(payload) {
         this._newContainer("rma:recordCategory");
      },

     /**
      *
      * @method createNewRmFolder
      */
      createNewRmFolder: function alfresco_services_RMContentService__createNewRmFolder(payload) {
         this._newContainer("rma:recordFolder");
      },

      /**
      *
      * @method _newContainer
      */
      _newContainer: function alfresco_services_RMContentService__newContainer(folderType)
      {
         var destination = this._currentNode.parent.nodeRef;

         // Intercept before dialog show
         var doBeforeDialogShow = function DLTB__newContainer_doBeforeDialogShow(p_form, p_dialog)
         {
            var label = "label.new-" + folderType.replace(":", "_");
            Dom.get(p_dialog.id + "-dialogTitle").innerHTML = this.message(label + ".title");
            Dom.get(p_dialog.id + "-dialogHeader").innerHTML = this.message(label + ".header");
         };

         var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true",
         {
            itemKind: "type",
            itemId: folderType,
            destination: destination,
            mode: "create",
            submitType: "json",
            formId: "doclib-common"
         });

         // Using Forms Service, so always create new instance
         var createFolder = new Alfresco.module.SimpleDialog(this.id + "-createFolder");

         createFolder.setOptions(
         {
            width: "33em",
            templateUrl: templateUrl,
            actionUrl: null,
            destroyOnHide: true,
            doBeforeDialogShow:
            {
               fn: doBeforeDialogShow,
               scope: this
            },
            onSuccess:
            {
               fn: function DLTB__newContainer_success(response)
               {
                  var folderName = response.config.dataObj["prop_cm_name"];
                  YAHOO.Bubbling.fire("folderCreated",
                  {
                     name: folderName,
                     parentNodeRef: destination
                  });
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.message("message.new-folder.success", folderName)
                  });
               },
               scope: this
            },
            onFailure:
            {
               fn: function DLTB__newContainer_failure(response)
               {
                  var msgKey = (folderType == "rma:recordCategory") ? "message.new-category.failure": "message.new-folder.failure";
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.message(msgKey)
                  });
               },
               scope: this
            }
         }).show();
      }
   });
});