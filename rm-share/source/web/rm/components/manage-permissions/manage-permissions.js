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
       * Overrides the existing function to change the URL for the post request
       *
       * Called when user clicks on the save button.
       *
       * @method onSaveButtonClick
       * @param type
       * @param args
       */
      onSaveButton: function Permissions_onSaveButton(type, args)
      {
         this.widgets.saveButton.set("disabled", true);

         var permissions = [],
            perm;

         for (var i = 0, ii = this.permissions.current.length; i < ii; i++)
         {
            perm = this.permissions.current[i];
            // Newly created, or existing and removed or modified
            if ((perm.created && !perm.removed) || (!perm.created && (perm.removed || perm.modified)))
            {
               // Modified existing
               // First add a new one, see MNT-11725
               permissions.push(
               {
                  authority: perm.authority.name,
                  role: perm.role,
                  remove: perm.removed
               });

               // Remove old permission
               if (perm.modified && !perm.created)
               {
                  permissions.push(
                  {
                     authority: perm.authority.name,
                     role: this.permissions.original[i].role,
                     remove: true
                  });
               }
            }
         }

         if (permissions.length > 0 || this.permissions.isInherited !== this.permissions.originalIsInherited)
         {
            Alfresco.util.Ajax.jsonPost(
            {
               url: Alfresco.constants.PROXY_URI + "api/node/" + this.options.nodeRef.replace(":/", "") + "/rmpermissions",
               dataObj:
               {
                  permissions: permissions,
                  isInherited: this.permissions.isInherited
               },
               successCallback:
               {
                  fn: function(res)
                  {
                     // Return to appropriate location
                     this._navigateForward();
                  },
                  scope: this
               },
               failureCallback:
               {
                  fn: function(response)
                  {
                     var json = Alfresco.util.parseJSON(response.serverResponse.responseText);
                     Alfresco.util.PopupManager.displayPrompt(
                     {
                        title: this.msg("message.failure"),
                        text: this.msg("message.permissionsSaveFail", json.message)
                     });
                     this.widgets.saveButton.set("disabled", false);
                  },
                  scope: this
               }
            });
         }
         else
         {
            // Nothing to save
            this._navigateForward();
         }
      },

      /**
       * Overrides the existing function to add RM specific behaviour.
       *
       * Authority selected event handler. This event is fired from Authority picker.
       *
       * @method onAuthoritySelected
       * @param e DomEvent
       * @param args Event parameters (depends on event type)
       */
      onAuthoritySelected: function Permissions_onAuthoritySelected(e, args)
      {
         // Construct permission descriptor and add permission row.
         this.permissions.current.push(
         {
            authority:
            {
               name: args[1].itemName,
               displayName: args[1].displayName,
               iconUrl: args[1].iconUrl
            },
            role: this.settableRoles[1],
            created: true
         });

         // Remove authority selector popup
         this.widgets.addUserGroup.set("checked", false);
         Dom.removeClass(this.widgets.authorityFinder, "active");
         Dom.removeClass(this.id + "-inheritedContainer", "table-mask");
         Dom.removeClass(this.id + "-directContainer", "table-mask");
         this.showingAuthorityFinder = false;

         this.render();
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
            if (name === "ROLE_EXTENDED_READER" || name === "ROLE_EXTENDED_WRITER" || name === "GROUP_Administrator" + YAHOO.util.History.getQueryStringParameter("filePlanId"))
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