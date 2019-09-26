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

/**
 * This Aikau Service uses the Holds REST APIs
 * @author Roxana Lucanu
 *
 * @extends module:alfresco/services/BaseService
 */
define(["dojo/_base/declare",
        "alfresco/services/BaseService",
        "alfresco/core/CoreXhr",
        "service/constants/Default",
        "alfresco/core/topics",
        "alfresco/enums/urlTypes",
        "alfresco/core/NodeUtils",
        "dijit/registry",
        "dojo/dom-style",
        "dojo/_base/lang",
        "alfresco/core/TemporalUtils"],
        // "../../res/rm/modules/documentlibrary/hold/add-to-hold"],
   function (declare, BaseService, CoreXhr, AlfConstants, topics, urlTypes, NodeUtils, registry, domStyle,
             lang, TemporalUtils, hold) {

      return declare([BaseService, CoreXhr, TemporalUtils], {

         registerSubscriptions: function rm_services_holdService__registerSubscriptions() {
            this.alfSubscribe("RM_ADD_TO_HOLD_PAGE", lang.hitch(this, this.onActionAddToHold));
            this.alfSubscribe("RM_REMOVE_FROM_HOLD_PAGE", lang.hitch(this, this.onActionRemoveFromHold));
         },

         /**
          *
          *
          * @param payload
          */
         onActionAddToHold: function rm_services_holdService__onActionAddToHold(payload) {

            alert("@@@addToHold@@@");
            console.log("%%%%%");

            var item = payload.item

            this.alfPublish("ALF_CREATE_FORM_DIALOG_REQUEST", {
               dialogId: "ADD_TO_HOLD_DIALOG",
               dialogTitle: this.message("label.title.add-to-hold"),
               dialogConfirmationButtonTitle: this.message("label.button.ok"),
               dialogCancellationButtonTitle: this.message("label.button.cancel"),
               widgets: [{
                  name: "rm/modules/documentlibrary/hold/add-to-hold",
                  config: {
                     currentData: {
                        items: [item]
                     },
                     currentItem: item,
                     waitForPageWidgets: false
                  }
               }]
            }, true);
            //
            // if (!this.addToHold)
            // {
            //    this.addToHold = new hold.Alfresco.rm.module.AddToHold(this.id + "-listofholds");
            // }
            // var itemNodeRef;
            // if (YAHOO.lang.isArray(payload))
            // {
            //    itemNodeRef = [];
            //    for (var i = 0, l = payload.length; i < l; i++)
            //    {
            //       itemNodeRef.push(payload[i].nodeRef);
            //    }
            // }
            // else
            // {
            //    itemNodeRef = payload.nodeRef;
            // }
            // this.addToHold.setOptions({
            //    itemNodeRef: itemNodeRef
            // }).show();
         },
         /**
          *
          *
          * @param payload
          */
         onActionRemoveFromHold: function rm_services_holdService__onActionRemoveFromHold(payload) {
            // if (!this.modules.removeFromHold)
            // {
            //    this.modules.removeFromHold = new Alfresco.rm.module.RemoveFromHold(this.id + "-listofholds");
            // }
            // var itemNodeRef;
            // if (YAHOO.lang.isArray(payload))
            // {
            //    itemNodeRef = [];
            //    for (var i = 0, l = payload.length; i < l; i++)
            //    {
            //       itemNodeRef.push(payload[i].nodeRef);
            //    }
            // } else
            // {
            //    itemNodeRef = payload.nodeRef;
            // }
            // this.modules.removeFromHold.setOptions({
            //    itemNodeRef: itemNodeRef
            // }).show();
         }
      });
   });