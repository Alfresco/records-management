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
 * RM ConsoleGroups tool component.
 *
 * Extends the existing console groups tool component
 *
 * @namespace Alfresco
 * @class Alfresco.rm.ConsoleGroups
 */
(function()
{
   /**
    * RM ConsoleGroups constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.rm.ConsoleGroups} The new RM ConsoleGroups instance
    * @constructor
    */
   Alfresco.rm.ConsoleGroups = function(htmlId)
   {
      Alfresco.rm.ConsoleGroups.superclass.constructor.call(this, htmlId);

      var parent = this;

      this.panelHandlers.searchPanelHandler.onBuildColumnInfo = function(serverResponse, itemInfo)
      {
         // Create columnInfo and its header
         var headerButtons = [];
         if (!itemInfo || itemInfo.cssClass == 'groups-item-group')
         {
            headerButtons.push({
               title: (itemInfo ? parent._msg("button.newsubgroup") : parent._msg("button.newgroup")),
               cssClass: "groups-newgroup-button",
               click: {
                  fn: this.onNewGroupClick,
                  scope: this
               }
            });
         }

         if (itemInfo && itemInfo.cssClass == 'groups-item-group')
         {
            // Only add the following button for NON root columns
            headerButtons.push({
               title: parent._msg("button.addgroup"),
               cssClass: "groups-addgroup-button",
               click: {
                  fn: this.onAddGroupClick,
                  scope: this
               }
            });
            headerButtons.push({
               title: parent._msg("button.adduser"),
               cssClass: "groups-adduser-button",
               click: {
                  fn: this.onAddUserClick,
                  scope: this
               }
            });
         }

         // Create column descriptor
         var column = {
            parent: itemInfo,
            header: {
               buttons: headerButtons
            },
            body: {
               items: []
            }
         };

         // Get data from request
         var obj = {};
         if (serverResponse)
         {
            // Parse response if there was one
            obj = YAHOO.lang.JSON.parse(serverResponse.responseText);

            // Translate group paging attributes to columnbrowser pagination attributes
            if (obj.paging)
            {
               column.pagination = {
                  totalRecords : obj.paging.totalItems,
                  recordOffset: obj.paging.skipCount
               };
            }
         }

         var updategroupButton =
         {
            title: parent._msg("button.updategroup"),
            cssClass: "groups-update-button",
            click: {
               fn: this.onUpdateClick,
                  scope: this
               }
         };

         var deletegroupButton =
         {
            title: parent._msg("button.deletegroup"),
            cssClass: "groups-delete-button",
            click: {
               fn: this.onDeleteClick,
               scope: this
            }
         };

         var deletegroupButtonDisabled =
         {
            title: parent._msg("button.deletegroup"),
            cssClass: "groups-delete-button-disabled",
            click: {
               fn: function()
               {
                  return false;
               },
               scope: this
            }
         };

         // Create item buttons for users and groups
         var groupButtons = [];
         groupButtons.push(updategroupButton);
         groupButtons.push(deletegroupButton);

         var groupButtonsDisabled = [];
         groupButtonsDisabled.push(updategroupButton);
         groupButtonsDisabled.push(deletegroupButtonDisabled);

         var usersButtons = [
            {
               title: parent._msg("button.removeuser"),
               cssClass: "users-remove-button",
               click: {
                  fn: this.onUserRemoveClick,
                  scope: this
               }
            }
         ];

         // Transform server respons to itemInfos and add them to the columnInfo's body
         for (var i = 0; obj.data && i < obj.data.length; i++)
         {
            var o = obj.data[i];
            var label = o.displayName;
            if (o.displayName !== o.shortName)
            {
               label += " (" + o.shortName + ")";
            }
            var item = {
               shortName: o.shortName,
               fullName: o.fullName,
               url: o.authorityType == 'GROUP' ? Alfresco.constants.PROXY_URI + o.url + "/children?sortBy=" + this.sortBy : null,
               hasNext: o.groupCount > 0 || o.userCount > 0,
               label: label,
               next : null,
               cssClass: o.authorityType == 'GROUP' ? "groups-item-group" : "groups-item-user",
               buttons: o.authorityType == 'GROUP' ? ((Alfresco.util.arrayContains(o.zones, "APP.SHARE") || Alfresco.util.arrayContains(o.zones, "APP.RM"))? groupButtonsDisabled : groupButtons) : usersButtons
            };
            column.body.items.push(item);
         }

         return column;
      };

      return this;
   };

   YAHOO.extend(Alfresco.rm.ConsoleGroups, Alfresco.ConsoleGroups, {});
})();
