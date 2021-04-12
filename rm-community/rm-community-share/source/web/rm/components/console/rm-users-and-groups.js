/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2021 Alfresco Software Limited
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
 * RM Users and Groups component
 *
 * @namespace Alfresco.rm.component
 * @class Alfresco.rm.component.RMUsersAndGroups
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       Sel = YAHOO.util.Selector;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * RM Users and Groups component constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.rm.component.RMUsersAndGroups} The new component instance
    * @constructor
    */
   Alfresco.rm.component.RMUsersAndGroups = function RM_UsersAndGroups_constructor(htmlId)
   {
      YAHOO.Bubbling.on("rmRoleSelected", this.onHandleAllButtons, this);
      YAHOO.Bubbling.on("rmGroupSelected", this.onHandleRemoveGroupButton, this);
      YAHOO.Bubbling.on("rmUserSelected", this.onHandleRemoveUserButton, this);

      return Alfresco.rm.component.RMUsersAndGroups.superclass.constructor.call(this, htmlId);
   };

   YAHOO.extend(Alfresco.rm.component.RMUsersAndGroups, Alfresco.admin.RMViewRoles,
   {
      /**
       * Initialises event listening and custom events
       *
       * @method: initEvents
       */
      initEvents: function RM_UsersAndGroups_initEvents()
      {
         Event.on(this.id, 'click', this.onInteractionEvent, null, this);

         this.registerEventHandler('click', 'button#addGroup-button',
         {
            handler: this.onAddGroup,
            scope: this
         });

         this.registerEventHandler('click', 'button#removeGroup-button',
         {
            handler: this.onRemoveGroup,
            scope: this
         });

         this.registerEventHandler('click', 'button#addUser-button',
         {
            handler: this.onAddUser,
            scope: this
         });

         this.registerEventHandler('click', 'button#removeUser-button',
         {
            handler: this.onRemoveUser,
            scope: this
         });

         this.registerEventHandler('click', '.role',
         {
            handler: this.onRoleSelect,
            scope: this
         });

         this.registerEventHandler('click', '.group',
         {
            handler: this.onGroupSelect,
            scope: this
         });

         this.registerEventHandler('click', '.user',
         {
            handler: this.onUserSelect,
            scope: this
         })

         return this;
      },

      /**
       * Query the list of roles to populate the roles list.
       *
       * @method updateRolesList
       */
      updateRolesList: function RM_UsersAndGroups_updateRolesList()
      {
         Alfresco.util.Ajax.request(
         {
            method: Alfresco.util.Ajax.GET,
            // FIXME: Use "/api/rma/admin/{store_type}/{store_id}/{id}/rmroles?auths={auths?}" See RM-3968
            url: Alfresco.constants.PROXY_URI + "api/rma/admin/rmroles?auths=true",
            successCallback:
            {
               fn: this.onRolesLoaded,
               scope: this
            },
            failureCallback:
            {
               fn: function(res)
               {
                  var json = Alfresco.util.parseJSON(res.serverResponse.responseText);
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     title: this.msg("message.failure"),
                     text: this.msg("message.get-roles-failure", json.message)
                  });
               },
               scope: this
            }
         });
      },

      /**
       * Extends the onRolesLoaded function from the base class
       *
       * @method onRolesLoaded
       * @param res {object} Response
       */
      onRolesLoaded: function RM_UsersAndGroups_onRolesLoaded(res)
      {
         Alfresco.rm.component.RMUsersAndGroups.superclass.onRolesLoaded.call(this, res);

         if (this.options.selectedGroupId)
         {
            this.updateSelectedGroupUI(this.options.selectedGroupId);
         }

         if (this.options.selectedUserId)
         {
            this.updateSelectedUserUI(this.options.selectedUserId);
         }
      },

      /**
       * Helper to update the users and groups list UI based on selected Role ID.
       *
       * @method updateSelectedRoleUI
       * @param {roleId} Role ID to update for, null to empty the list
       */
      updateSelectedRoleUI: function RM_UsersAndGroups_updateSelectedRoleUI(roleId)
      {
         // update selected item background
         var roleLinks = Dom.getElementsByClassName("role", "a");
         for (var r in roleLinks)
         {
            if (roleLinks.hasOwnProperty(r))
            {
               // role link ID is in the format "role-roleId"
               var roleLinkId = roleLinks[r].id,
                  liParent = Dom.get(roleLinkId).parentNode;

               if (roleLinkId.substring(5) === roleId)
               {
                  // found item to selected
                  Dom.addClass(liParent, "selected");
                  this.options.selectedRoleId = roleId;
                  YAHOO.Bubbling.fire("rmRoleSelected");
               }
               else
               {
                  // deselect previously selected item
                  Dom.removeClass(liParent, "selected");
               }
            }
         }

         // clear the groups list
         var elGroupsDiv = Dom.get("roleGroups"),
            elGroupsList = Dom.getFirstChild(elGroupsDiv);
         elGroupsList.innerHTML = "";

         // clear the users list
         var elUsersDiv = Dom.get("roleUsers");
            elUsersList = Dom.getFirstChild(elUsersDiv);
         elUsersList.innerHTML = "";

         // display the query groups/users for the selected user role if any
         if (roleId)
         {
            var groups = this.roles[roleId].assignedGroups;
            groups.sort(this._sortByName);

            for (var i = 0, length = groups.length; i < length; i++)
            {
               var li = document.createElement("li");
               li.innerHTML = '<a href="#" id="group-' + $html(groups[i].name) + '" class="group">' + $html(groups[i].displayLabel) + '</a>';
               elGroupsList.appendChild(li);
            }

            var users = this.roles[roleId].assignedUsers;
            users.sort(this._sortByName);

            for (var i = 0, length = users.length; i < length; i++)
            {
               var li = document.createElement("li");
               li.innerHTML = '<a href="#" id="user-' + $html(users[i].name) + '" class="user">' + $html(users[i].displayLabel) + '</a>';
               elUsersList.appendChild(li);
            }
         }
      },

      /**
       * Helper function for 'updateSelectedGroupUI' and 'updateSelectedUserUI' to avoid code duplication.
       *
       * @method _updateSelectedUI
       * @param {id} ID to update for (might be 'groupId' or 'userId')
       * @param {param} parameter used in the template file (might be 'group' or 'user')
       */
      _updateSelectedUI: function RM_UsersAndGroups__updateSelectedUI(id, param)
      {
         // update selected item background
         var links = Dom.getElementsByClassName(param, "a");
         for (var i in links)
         {
            if (links.hasOwnProperty(i))
            {
               // link id is in the format "group-groupId"/"user-userId"
               var linkId = links[i].id,
                  liParent = Dom.get(linkId).parentNode;

               if (linkId.substring(param.length + 1) === id)
               {
                  // found item to selected
                  Dom.addClass(liParent, "selected");
                  if (param === "group")
                  {
                     this.options.selectedGroupId = id;
                     YAHOO.Bubbling.fire("rmGroupSelected");
                  }
                  else if (param === "user")
                  {
                     this.options.selectedUserId = id;
                     YAHOO.Bubbling.fire("rmUserSelected");
                  }
                  else
                  {
                     throw "The paramter '" + param + "' is neither 'group' nor 'user'!";
                  }
               }
               else
               {
                  // deselect previously selected item
                  Dom.removeClass(liParent, "selected");
               }
            }
         }
      },

      /**
       * Helper to update the group selection.
       *
       * @method updateSelectedGroupUI
       * @param {groupId} Group ID to update for, null to empty the list
       */
      updateSelectedGroupUI: function RM_UsersAndGroups_updateSelectedGroupUI(groupId)
      {
         this._updateSelectedUI(groupId, "group");
      },

      /**
       * Helper to update the user selection.
       *
       * @method updateSelectedUserUI
       * @param {userId} User ID to update for, null to empty the list
       */
      updateSelectedUserUI: function RM_UsersAndGroups_updateSelectedUserUI(userId)
      {
         this._updateSelectedUI(userId, "user");
      },

      /**
       * Fired by YUI when parent element is available for scripting
       *
       * @method onReady
       */
      onReady: function RM_UsersAndGroups_onReady()
      {
         this.initEvents();

         var buttons = Sel.query('button', this.id),
            button, id;

         // Create widget button while reassigning classname to src element (since YUI removes classes).
         // We need the classname so we can identify what action to take when it is interacted with (event delegation).
         for (var i = 0, length = buttons.length; i < length; i++)
         {
            button = buttons[i];
            id = button.id.replace(this.id + '-', '');
            this.widgets[id] = new YAHOO.widget.Button(button.id);
            this.widgets[id]._button.className = button.className;
         }

         // well known buttons - set the initial state
         this.widgets.addGroup.set("disabled", true);
         this.widgets.removeGroup.set("disabled", true);
         this.widgets.addUser.set("disabled", true);
         this.widgets.removeUser.set("disabled", true);

         // get the selected role ID, group ID and user ID
         this.options.selectedRoleId = Alfresco.rm.getParamValueFromUrl("roleId");
         this.options.selectedGroupId = Alfresco.rm.getParamValueFromUrl("groupId");
         this.options.selectedUserId = Alfresco.rm.getParamValueFromUrl("userId");

         // query the list of roles, groups and users to populate the roles list
         this.updateRolesList();
      },

      /**
       * This event is fired when a role is selected.
       * The add buttons will be enable and the remove buttons will be disabled.
       *
       * @method onHandleAddButtons
       * @param e DomEvent
       * @param args Event parameters (depends on event type)
       */
      onHandleAllButtons: function RM_UsersAndGroups_onHandleAllButtons(e, args)
      {
         this.widgets.addGroup.set("disabled", false);
         this.widgets.addUser.set("disabled", false);
         this.widgets.removeGroup.set("disabled", true);
         this.widgets.removeUser.set("disabled", true);
      },

      /**
       * This event is fired when a group is selected.
       * The remove button for the groups column will be enabled.
       *
       * @method onHandleRemoveGroupButton
       * @param e DomEvent
       * @param args Event parameters (depends on event type)
       */
      onHandleRemoveGroupButton: function RM_UsersAndGroups_onEnableRemoveGroupButton(e, args)
      {
         this.widgets.removeGroup.set("disabled", false);
      },

      /**
       * This event is fired when a user is selected.
       * The remove button for the user column will be enabled.
       *
       * @method onHandleRemoveUserButton
       * @param e DomEvent
       * @param args Event parameters (depends on event type)
       */
      onHandleRemoveUserButton: function RM_UsersAndGroups_onEnableRemoveUserButton(e, args)
      {
         this.widgets.removeUser.set("disabled", false);
      },

      /**
       * Adds a user or group to a role.
       *
       * @param objectId The id to a user (userName) or a group (fullName)
       * @param roleId The id of the role that the object shall be added under
       * @param successMessage Message to display if the request is successful
       * @param failureMessage Message to display if the request fails
       */
      _addToRole: function RM_UsersAndGroups__addToRole(objectId, roleId, successMessage, failureMessage)
      {
         Alfresco.util.Ajax.jsonPost(
         {
            // FIXME: Use "/api/rm/{store_type}/{store_id}/{id}/roles/{roleId}/authorities/{authorityName}" See RM-3968
            url: Alfresco.constants.PROXY_URI + "api/rm/roles/" + encodeURIComponent(roleId) + "/authorities/" + encodeURIComponent(objectId),
            successCallback:
            {
               fn: function(o)
               {
                  // Display success message
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: successMessage
                  });

                  // Update list
                  this.updateRolesList();
               },
               scope: this
            },
            failureMessage: failureMessage
         });
      },

      /**
       * Group selected event handler.
       * This event is fired from Group picker - so we much ensure
       * the event is for the current panel by checking panel visibility.
       *
       * @method onGroupSelected
       * @param e DomEvent
       * @param args Event parameters (depends on event type)
       */
      onGroupSelected: function RM_UsersAndGroups_onGroupSelected(e, args)
      {
         var displayName = args[1].displayName,
            groupName = args[1].itemName,
            roleId = this.options.selectedRoleId,
            successMessage = this.msg("message.addgroup-success", displayName),
            failureMessage = this.msg("message.addgroup-failure", displayName);

         this._addToRole(groupName, roleId, successMessage, failureMessage);

         this.widgets.addGroupPanel.hide();
      },

      /**
       * Called when the group finder template has been loaded.
       * Creates a dialog and inserts the group finder for choosing groups to add.
       *
       * @method onGroupFinderLoaded
       * @param response The server response
       */
      onGroupFinderLoaded: function RM_UsersAndGroups__onGroupFinderLoaded(response)
      {
         // Inject the component from the XHR request into it's placeholder DIV element
         var finderDiv = Dom.get("rm-search-groupfinder");
         finderDiv.innerHTML = response.serverResponse.responseText;

         // Create the Add Group dialog
         this.widgets.addGroupPanel = Alfresco.util.createYUIPanel("rm-grouppicker")

         // Find the Group Finder by container ID
         this.modules.searchGroupFinder = Alfresco.util.ComponentManager.get("rm-search-groupfinder");

         // Set the correct options for our use
         this.modules.searchGroupFinder.setOptions(
         {
            viewMode: Alfresco.GroupFinder.VIEW_MODE_COMPACT,
            singleSelectMode: true
         });

         // Make sure we listen for events when the user selects a group
         YAHOO.Bubbling.on("itemSelected", this.onGroupSelected, this);

         YAHOO.lang.later(100, this, function()
         {
            // Show the panel
            this.widgets.addGroupPanel.show();
         });
      },

      /**
       * Called when the user has selected a person from the add user dialog.
       *
       * @method onPersonSelected
       * @param e DomEvent
       * @param args Event parameters (depends on event type)
       */
      onPersonSelected: function RM_UsersAndGroups_onPersonSelected(e, args)
      {
         var userName = args[1].userName,
            roleId = this.options.selectedRoleId,
            successMessage = this.msg("message.adduser-success", userName),
            failureMessage = this.msg("message.adduser-failure", userName);

         this._addToRole(userName, roleId, successMessage, failureMessage);

         this.widgets.addUserPanel.hide();
      },

      /**
       * Called when the people finder template has been loaded.
       * Creates a dialog and inserts the people finder for choosing users to add.
       *
       * @method onPeopleFinderLoaded
       * @param response The server response
       */
      onPeopleFinderLoaded: function RM_UsersAndGroups_onPeopleFinderLoaded(response)
      {
         // Inject the component from the XHR request into it's placeholder DIV element
         var finderDiv = Dom.get("rm-search-peoplefinder");
         finderDiv.innerHTML = response.serverResponse.responseText;

         // Create the Add User dialog
         this.widgets.addUserPanel = Alfresco.util.createYUIPanel("rm-peoplepicker");

         // Find the People Finder by container ID
         this.modules.searchPeopleFinder = Alfresco.util.ComponentManager.get("rm-search-peoplefinder");

         // Set the correct options for our use
         this.modules.searchPeopleFinder.setOptions(
         {
            viewMode: Alfresco.PeopleFinder.VIEW_MODE_COMPACT,
            singleSelectMode: true
         });

         // Make sure we listen for events when the user selects a person
         YAHOO.Bubbling.on("personSelected", this.onPersonSelected, this);

         YAHOO.lang.later(100, this, function()
         {
            // Show the panel
            this.widgets.addUserPanel.show();
         });
      },

      /**
       * Event handler for add group button
       *
       * @method onAddGroup
       * @param {e} Event object
       */
      onAddGroup: function RM_UsersAndGroups_onAddGroup(e)
      {
         if (this.widgets.addGroupPanel)
         {
            this.modules.searchGroupFinder.clearResults();
            this.widgets.addGroupPanel.show();
         }
         else
         {
            // Load in the Group Finder component from the server
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "components/people-finder/group-finder",
               dataObj:
               {
                  htmlid: "rm-search-groupfinder"
               },
               successCallback:
               {
                  fn: this.onGroupFinderLoaded,
                  scope: this
               },
               failureMessage: this.msg("message.load-groupFinder-failure"),
               execScripts: true
            });
         }
      },

      /**
       * Removes a user or group from a role.
       *
       * @param objectId The id to a user (userName) or a group (fullName)
       * @param roleId The id of the role that the object shall be removed from
       * @param successMessage Message to display if the request is successful
       * @param failureMessage Message to display if the request fails
       * @param displayPromptTitle Title for the display prompt
       * @param displayPromptText Text for the display prompt
       */
      _removeFromRole: function RM_UsersAndGroups__removeFromRole(objectId, roleId, successMessage, failureMessage, displayPromptTitle, displayPromptText)
      {
         var me = this;

         Alfresco.util.PopupManager.displayPrompt(
         {
            title: displayPromptTitle,
            text: displayPromptText,
            buttons: [
            {
               text: this.msg("button.yes"),
               handler: function RM_UsersAndGroups_remove_confirmYes()
               {
                  this.destroy();

                  Alfresco.util.Ajax.request(
                  {
                     method: Alfresco.util.Ajax.DELETE,
                     // FIXME: Use "/api/rm/{store_type}/{store_id}/{id}/roles/{roleId}/authorities/{authorityName}" See RM-3968
                     url: Alfresco.constants.PROXY_URI + "api/rm/roles/" + encodeURIComponent(roleId) + "/authorities/" + encodeURIComponent(objectId),
                     successCallback:
                     {
                        fn: function(o)
                        {
                           // Display success message
                           Alfresco.util.PopupManager.displayMessage(
                           {
                              text: successMessage
                           });

                           // Update list
                           me.updateRolesList();
                        },
                        scope: this
                     },
                     failureMessage: failureMessage
                  });
               }
            },
            {
               text: this.msg("button.no"),
               handler: function RM_UsersAndGroups_remove_confirmNo()
               {
                  this.destroy();
               },
               isDefault: true
            }]
         });
      },

      /**
       * Event handler for remove group button
       *
       * @method onRemoveGroup
       * @param {e} Event object
       */
      onRemoveGroup: function RM_UsersAndGroups_onRemoveGroup(e)
      {
         var groupId = this.options.selectedGroupId,
            roleId = this.options.selectedRoleId,
            role = this.roles[roleId],
            groupName = Alfresco.util.findInArray(role.assignedGroups, groupId, "name")["displayLabel"],
            successMessage = this.msg("message.removegroup-success", groupName),
            failureMessage = this.msg("message.removegroup-failure", groupName),
            displayPromptTitle = this.msg("message.confirm.removegroup.title"),
            displayPromptText = this.msg("message.confirm.removegroup", groupName);

         this._removeFromRole(groupId, roleId, successMessage, failureMessage, displayPromptTitle, displayPromptText);
      },

      /**
       * Event handler for add user button
       *
       * @method onAddUser
       * @param {e} Event object
       */
      onAddUser: function RM_UsersAndGroups_onAddUser(e)
      {
         if (this.widgets.addUserPanel)
         {
            this.modules.searchPeopleFinder.clearResults();
            this.widgets.addUserPanel.show();
         }
         else
         {
            // Load in the People Finder component from the server
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "components/people-finder/people-finder",
               dataObj:
               {
                  htmlid: "rm-search-peoplefinder"
               },
               successCallback:
               {
                  fn: this.onPeopleFinderLoaded,
                  scope: this
               },
               failureMessage: this.msg("message.load-peopleFinder-failure"),
               execScripts: true
            });
         }
      },

      /**
       * Event handler for remove user button
       *
       * @method onRemoveUser
       * @param {e} Event object
       */
      onRemoveUser: function RM_UsersAndGroups_onRemoveUser(e)
      {
         var userId = this.options.selectedUserId,
            roleId = this.options.selectedRoleId,
            successMessage = this.msg("message.removeuser-success", userId),
            failureMessage = this.msg("message.removeuser-failure", userId),
            displayPromptTitle = this.msg("message.confirm.removeuser.title"),
            displayPromptText = this.msg("message.confirm.removeuser", userId);

         this._removeFromRole(userId, roleId, successMessage, failureMessage, displayPromptTitle, displayPromptText);
      },

      /**
       * Helper function for 'onGroupSelect' and 'onUserSelect' to avoid code duplication.
       *
       * @method onGroupSelect
       * @param {e} Event object
       * @param {param} parameter used in the url (might be 'group' or 'user')
       */
      _onSelect: function RM_UsersAndGroups__onSelect(e, param)
      {
         Event.stopEvent(e);

         var el = Event.getTarget(e),
            urlParam = "&" + param + "Id=";

         // get the id of the element
         var id = el.id.substring(param.length + 1);

         // add/update id value
         var hash = window.location.hash;
         if (hash.indexOf(urlParam) !== -1)
         {
            hash = hash.replace(new RegExp('(' + urlParam + ')[^\&]+'), '$1' + encodeURI(id));
         }
         else
         {
            hash += urlParam + encodeURI(id);
         }
         window.location.hash = hash;

         if (param === "group")
         {
            this.updateSelectedGroupUI(id);
         }
         else if (param === "user")
         {
            this.updateSelectedUserUI(id);
         }
         else
         {
            throw "The paramter '" + param + "' is neither 'group' nor 'user'!";
         }
      },

      /**
       * Event handler for group selection
       *
       * @method onGroupSelect
       * @param {e} Event object
       */
      onGroupSelect: function RM_UsersAndGroups_onGroupSelect(e)
      {
         // update groupId value
         this._onSelect(e, "group");
      },

      /**
       * Event handler for user selection
       *
       * @method onUserSelect
       * @param {e} Event object
       */
      onUserSelect: function RM_UsersAndGroups_onUserSelect(e)
      {
         // update userId value
         this._onSelect(e, "user");
      },

      /**
       * Helper to Array.sort() by the 'name' field of an object.
       *
       * @method _sortByName
       * @return {Number}
       * @private
       */
      _sortByName: function RM_UsersAndGroups__sortByName(s1, s2)
      {
         var ss1 = s1.displayLabel.toLowerCase(), ss2 = s2.displayLabel.toLowerCase();
         return (ss1 > ss2) ? 1 : (ss1 < ss2) ? -1 : 0;
      }
  });
})();
