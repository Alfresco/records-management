/**
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
        "alfresco/dialogs/AlfDialog",
        "alfresco/dialogs/AlfDialogService"],
        function(declare, AlfDialog, AlfDialogService) {

   return declare([AlfDialogService], {

      onCreateDialogRequest: function alfresco_dialogs_RmAlfDialogService__onCreateDialogRequest(payload) {
         if (this.dialog != null && !payload.keepDialog)
         {
            this.dialog.destroyRecursive();
         }

         var dialogConfig = {
            title: this.message(payload.dialogTitle),
            textContent: payload.textContent,
            widgetsContent: payload.widgetsContent,
            widgetsButtons: payload.widgetsButtons,
            additionalCssClasses: payload.additionalCssClasses ? payload.additionalCssClasses : "",
            contentWidth: payload.contentWidth ? payload.contentWidth : null,
            contentHeight: payload.contentHeight ? payload.contentHeight : null,
            handleOverflow: (payload.handleOverflow != null) ? payload.handleOverflow: true,
            fixedWidth: (payload.fixedWidth != null) ? payload.fixedWidth: false
         };
         this.dialog = new AlfDialog(dialogConfig);

         if (payload.publishOnShow)
         {
            array.forEach(payload.publishOnShow, lang.hitch(this, this.publishOnShow));
         }
         this.dialog.show();

         if (payload.hideTopic)
         {
            this.alfSubscribe(payload.hideTopic, lang.hitch(this.dialog, this.dialog.hide));
         }
      }
   });
});