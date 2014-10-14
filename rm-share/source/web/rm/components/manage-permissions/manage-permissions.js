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

/**
 * RM Manage Permissions component.
 * Overrides/adds methods for the manage permissions page in RM site.
 *
 * @namespace Alfresco.rm
 * @class Alfresco.rm.component.ManagePermissions
 */
(function()
{
   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * RM Manage Permissions constructor.
    *
    * @param {String} htmlId The HTML id of the element
    * @return {Alfresco.rm.component.ManagePermissions} The new manage permissions instance
    * @constructor
    */
   Alfresco.rm.component.ManagePermissions = function(htmlId)
   {
      Alfresco.rm.component.ManagePermissions.superclass.constructor.call(this, htmlId);
      return this;
   };

   YAHOO.extend(Alfresco.rm.component.ManagePermissions, Alfresco.component.ManagePermissions,
   {
      /**
       * Overrides the existing function to change the authority finder
       *
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function Permissions_onReady()
      {
         // YUI Buttons
         this.widgets.inherited = Alfresco.util.createYUIButton(this, "inheritedButton", this.onInheritedButton);
         this.widgets.saveButton = Alfresco.util.createYUIButton(this, "okButton", this.onSaveButton);
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancelButton", this.onCancelButton);

         // DataSource set-up and event registration
         this._setupDataSources();

         // DataTable set-up and event registration
         this._setupDataTables();

         // Load the Authority Finder component
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "components/people-finder/authority-finder",
            dataObj:
            {
               htmlid: this.id + "-authorityFinder",
               module: "rm"
            },
            successCallback:
            {
               fn: this.onAuthorityFinderLoaded,
               scope: this
            },
            failureMessage: this.msg("message.authorityFinderFail"),
            execScripts: true
         });

         if (this.options.site)
         {
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.PROXY_URI + "api/sites/" + encodeURIComponent(this.options.site) + "/memberships/",
               successCallback:
               {
                  fn: function(o)
                  {
                     for (var i = 0; i < o.json.length; i++)
                     {
                        this.sitePermissions[o.json[i].authority.fullName] = o.json[i].role;
                     }
                  },
                  scope: this
               }
            });
         }

         // Finally show the component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-body", "visibility", "visible");
      },

      /**
       * Overrides the existing function to disable the permissions button for the RM administrator
       *
       * Returns role custom datacell formatter
       *
       * @method fnRenderCellRole
       */
      fnRenderCellRole: function RM_Permissions_fnRenderCellRole()
      {
         var scope = this;

         /**
          * Role custom datacell formatter
          *
          * @method renderCellRole
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function RM_Permissions_renderCellRole(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            var role = oRecord.getData("role"),
               index = oRecord.getData("index"),
               menuId = "roles-" + oRecord.getId(),
               menuData = [];

            // Special case handling for non-settable roles
            if (!scope._isRoleEditable(role) || !scope.settableRoles.hasOwnProperty(role))
            {
               elCell.innerHTML = '<span>' + $html(scope._i18nRole(oRecord.getData("role"))) + '</span>';
            }
            else
            {
               menuData = menuData.concat(scope.settableRolesMenuData);

               // Internationalise the roles strings displayed:
               for (var j = 0, jj = menuData.length; j < jj; j++)
               {
                  menuData[j].text = scope._i18nRole(menuData[j].value);
               }

               elCell.innerHTML = '<span id="' + menuId + '"></span>';

               // Roles
               var rolesButton = new YAHOO.widget.Button(
               {
                  container: menuId,
                  type: "menu",
                  menu: menuData
               });
               rolesButton.getMenu().subscribe("click", function(p_sType, p_aArgs)
               {
                  return function Permissions_rolesButtonClicked(p_button, p_index)
                  {
                     var menuItem = p_aArgs[1];
                     if (menuItem)
                     {
                        p_button.set("label", scope._i18nRole(menuItem.value));
                        scope.onRoleChanged.call(scope, p_aArgs[1], p_index);
                     }
                  }(rolesButton, index);
               });
               rolesButton.set("label", $html(scope._i18nRole(oRecord.getData("role"))));

               if (oRecord.getData("authority").name === "GROUP_Administrator" + YAHOO.util.History.getQueryStringParameter("filePlanId"))
               {
                  Alfresco.util.disableYUIButton(rolesButton);
               }
            }
         };
      },

      /**
       * Overrides the existing function to remove the delete button for the RM administrator
       *
       * Returns actions custom datacell formatter
       *
       * @method fnRenderCellActions
       */
      fnRenderCellActions: function Permissions_fnRenderCellActions()
      {
         var scope = this;

         /**
          * Actions custom datacell formatter
          *
          * @method renderCellActions
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function Permissions_renderCellActions(elCell, oRecord, oColumn, oData)
         {
            var role = oRecord.getData("role");

            Dom.setStyle(elCell, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            var html = '<div id="' + scope.id + '-actions-' + oRecord.getId() + '" class="hidden action-set">';
            if (scope._isRoleEditable(role) && oRecord.getData("authority").name !== "GROUP_Administrator" + YAHOO.util.History.getQueryStringParameter("filePlanId"))
            {
               html += '<div class="onActionDelete"><a class="action-link" title="' + scope.msg("button.delete") + '"><span>' + scope.msg("button.delete") + '</span></a></div>';
            }
            html += '</div>';
            elCell.innerHTML = html;
         };
      },

      /**
       * Overrides the existing function to add RM specific behaviour.
       *
       * Success handler called when the AJAX call to the doclist permissions web script returns successfully
       *
       * @method onPermissionsLoaded
       * @param response {object} Ajax response details
       */
      onPermissionsLoaded: function RM_Permissions_onPermissionsLoaded(response)
      {
         var data = response.json,
            direct = this._filterSpecialRoles(data.direct),
            inherited = this._filterSpecialRoles(data.inherited);

         // Update local copy of permissions
         this.permissions =
         {
            originalIsInherited: data.isInherited,
            isInherited: data.isInherited,
            canReadInherited: data.canReadInherited,
            inherited: inherited,
            original: Alfresco.util.deepCopy(direct),
            current: Alfresco.util.deepCopy(direct)
         };

         // Does the user have permissions to read the parent node's permissions?
         if (!this.permissions.canReadInherited)
         {
            this.widgets.dtInherited.set("MSG_EMPTY", this.msg("message.empty.no-permission"));
         }

         // Need the inheritance warning?
         this.inheritanceWarning = !data.isInherited;

         // Roles the user is allowed to select from
         this.settableRoles = ["Filing", "ReadRecords"];
         this.settableRolesMenuData = [];
         for (var i = 0, ii = this.settableRoles.length; i < ii; i++)
         {
            this.settableRoles[this.settableRoles[i]] = true;
            this.settableRolesMenuData.push(
            {
               text: this.settableRoles[i],
               value: this.settableRoles[i]
            });
         }

         this._disableInheritPermissionsButtton();

         this.deferredReady.fulfil("onPermissionsLoaded");
      },

      /**
       * Filters roles out which are specific to RM.
       *
       * @method _filterSpecialRoles
       * @param roles {array} The list to filter
       * @private
       */
      _filterSpecialRoles: function RM_Permissions__filterSpecialRoles(roles)
      {
         var filteredRoles = [];

         for (var i = 0; i < roles.length; i++)
         {
            var name = roles[i].authority.name;
            if (name === "ROLE_EXTENDED_READER" || name === "ROLE_EXTENDED_WRITER")
            {
               continue;
            }
            else
            {
               var role = roles[i].role;
               if (role === "Filing" || role === "ReadRecords")
               {
                  filteredRoles.push(roles[i]);
               }
            }
         }

         return filteredRoles;
      },

      /**
       * Disables the inherit permissions button for fileplan, unfiled records container and root categories
       *
       * @method _disableInheritPermissionsButtton
       * @private
       */
      _disableInheritPermissionsButtton: function RM_Permissions__disableInheritPermissionsButtton()
      {
         var nodeType = YAHOO.util.History.getQueryStringParameter("nodeType");
         if (nodeType === "rma:unfiledRecordContainer" ||
               nodeType === "rma:filePlan" ||
               (nodeType === "rma:recordCategory") && (new Alfresco.util.NodeRef(this.nodeData.location.parent.nodeRef)).id === YAHOO.util.History.getQueryStringParameter("filePlanId"))
         {
            Alfresco.util.disableYUIButton(this.widgets.inherited);
         }
      }
   });
})();