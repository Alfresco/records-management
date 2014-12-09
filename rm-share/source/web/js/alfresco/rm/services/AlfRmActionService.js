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
        "dojo/_base/lang",
        "alfresco/core/Core",
        "alfresco/rm/services/AlfRmActionBridge"],
        function(declare, lang, AlfCore, AlfRmActionBridge) {

   return declare(AlfCore, {

      constructor: function alfresco_rm_services_AlfRmActionService__constructor(args) {
         lang.mixin(this, args);
         this.alfSubscribe("ALF_RM_ADD_RELATIONSHIP", lang.hitch(this, this.onAddRelationship));
      },

      onAddRelationship: function alfresco_rm_services_AlfRmActionService__onAddRelationship(payload)
      {
         var asset = payload.asset,
           owner = payload.owner;
      }
   });
});