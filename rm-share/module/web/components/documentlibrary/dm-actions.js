/**
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

/**
 * Document Library Actions module
 *
 * @namespace Alfresco.doclib
 * @class Alfresco.doclib.Actions
 */
(function()
{
   YAHOO.Bubbling.fire("registerAction",
   {
      actionName: "onHideRecordAction",
      fn: function DLTB_onHideRecordAction(record, owner) {
         var me = this;
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("message.confirm.hide.record.title"),
            text: this.msg("message.confirm.hide.record"),
            buttons: [
            {
               text: this.msg("button.ok"),
               handler: function DLTB_onHideRecordAction_confirm_ok()
               {
                  me.onActionSimpleRepoAction(record, owner);
                  this.destroy();
               }
            },
            {
               text: this.msg("button.cancel"),
               handler: function DLTB_onHideRecordAction_confirm_cancel()
               {
                  this.destroy();
               },
               isDefault: true
            }]
         });
      }
   });
})();
