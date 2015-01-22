/**
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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
        "alfresco/services/OptionsService"],
        function(declare, lang, OptionsService) {

   return declare([OptionsService], {

      processOptions: function alfresco_rm_services_AlfRmOptionsService__processOptions(options, config, item, index) {
         var label = this.encodeHTML(lang.getObject(config.labelAttribute, false, item));
         var value = this.encodeHTML(lang.getObject(config.valueAttribute, false, item));
         if (label == null && value == null)
         {
            this.alfLog("warn", "Neither the label or value could be found in the item", item);
         }
         else
         {
            options.push({
               label: (label != null) ? label : value,
               value: value
            });
         }
      }
   });
});
