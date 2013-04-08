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

/**
 * Rules "RequestInfo" Action module.
 *
 * @namespace Alfresco.module
 * @class Alfresco.module.RequestInfoAction
 */
(function()
{
   /**
   * YUI Library aliases
   */
   var Dom = YAHOO.util.Dom,
      KeyListener = YAHOO.util.KeyListener;

   /**
    * Alfresco Slingshot aliases
    */
    var $html = Alfresco.util.encodeHTML;

   Alfresco.module.RequestInfoAction = function(htmlId)
   {
      Alfresco.module.RequestInfoAction.superclass.constructor.call(this, "Alfresco.module.RequestInfoAction", htmlId, ["button", "container", "connection"]);
      return this;
   };

   YAHOO.extend(Alfresco.module.RequestInfoAction, Alfresco.component.Base,
   {
      // FIXME!!!!

      /**
       * Main entry point
       * @method showDialog
       * @param request {object} Data to fill the form with
       */
      showDialog: function RIA_showDialog(requestConfig)
      {
         // Intercept before dialog show and change the button label
         var doBeforeDialogShow = function DLTB_requestInfo_doBeforeDialogShow(p_form, p_dialog)
         {
            p_dialog.widgets.okButton.set("label", this.msg("button.request-info"));
         };

         var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&mode={mode}&submitType={submitType}&showCancelButton=true",
         {
            htmlid: this.id + "-startWorkflowForm-" + Alfresco.util.generateDomId(),
            itemKind: "workflow",
            itemId: "activiti$activitiRequestForInformation",
            mode: "create",
            submitType: "json",
            showCaption: true,
            //formUI: true,
            formUI: false,
            showCancelButton: true
         });

         // Using Forms Service, so always create new instance
         var requestInfo = new Alfresco.module.SimpleDialog(this.id + "-request-info");

         requestInfo.setOptions(
         {
            width: "auto",
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
                  YAHOO.Bubbling.fire("metadataRefresh");
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.request-info-success")
                  });
               },
               scope: this
            },
            onFailure:
            {
               fn: function DLTB__newContainer_failure(response)
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.request-info-failure")
                  });
               },
               scope: this
            }
         }).show();
      }
   });

   /* Dummy instance to load optional YUI components early */
   var dummyInstance = new Alfresco.module.RequestInfoAction("null");
})();
