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
      YAHOO.Bubbling.on("rmGroupSelected", this.onHandleDeleteGroupButton, this);
      YAHOO.Bubbling.on("rmUserSelected", this.onHandleDeleteUserButton, this);

      return Alfresco.rm.component.RMUsersAndGroups.superclass.constructor.call(this, htmlId);
   };

   YAHOO.extend(Alfresco.rm.component.RMUsersAndGroups, Alfresco.admin.RMViewRoles,
   {
      /**
       * Initialises event listening and custom events
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

         this.registerEventHandler('click', 'button#deleteGroup-button',
         {
            handler: this.onDeleteGroup,
            scope: this
         });

         this.registerEventHandler('click', 'button#addUser-button',
         {
            handler: this.onAddUser,
            scope: this
         });

         this.registerEventHandler('click', 'button#deleteUser-button',
         {
            handler: this.onDeleteUser,
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
         this.widgets.deleteGroup.set("disabled", true);
         this.widgets.addUser.set("disabled", true);
         this.widgets.deleteUser.set("disabled", true);

         // get the selected role ID, group ID and user ID
         this.options.selectedRoleId = this.getValueFromUrl("roleId");
         this.options.selectedGroupId = this.getValueFromUrl("groupId");
         this.options.selectedUserId = this.getValueFromUrl("userId");

         // query the list of roles, groups and users to populate the roles list
         this.updateRolesList();
      },

      /**
       * This event is fired when a role is selected.
       * The add buttons will be enable and the delete buttons will be disabled.
       *
       * @method onHandleAddButtons
       * @param e DomEvent
       * @param args Event parameters (depends on event type)
       */
      onHandleAllButtons: function RM_UsersAndGroups_onHandleAllButtons(e, args)
      {
         this.widgets.addGroup.set("disabled", false);
         this.widgets.addUser.set("disabled", false);
         this.widgets.deleteGroup.set("disabled", true);
         this.widgets.deleteUser.set("disabled", true);
      },

      /**
       * This event is fired when a group is selected.
       * The delete button for the groups column will be enabled.
       *
       * @method onHandleDeleteGroupButton
       * @param e DomEvent
       * @param args Event parameters (depends on event type)
       */
      onHandleDeleteGroupButton: function RM_UsersAndGroups_onEnableDeleteGroupButton(e, args)
      {
         this.widgets.deleteGroup.set("disabled", false);
      },

      /**
       * This event is fired when a user is selected.
       * The delete button for the user column will be enabled.
       *
       * @method onHandleDeleteUserButton
       * @param e DomEvent
       * @param args Event parameters (depends on event type)
       */
      onHandleDeleteUserButton: function RM_UsersAndGroups_onEnableDeleteUserButton(e, args)
      {
         this.widgets.deleteUser.set("disabled", false);
      },

      /**
       * Event handler for add group button
       * @method onAddGroup
       * @param {e} Event object
       */
      onAddGroup: function RM_UsersAndGroups_onAddGroup(e)
      {
         // FIXME: See RM-690
         var roleId = this.options.selectedRoleId;
      },

      /**
       * Event handler for delete group button
       * @method onDeleteGroup
       * @param {e} Event object
       */
      onDeleteGroup: function RM_UsersAndGroups_onDeleteGroup(e)
      {
         // FIXME: See RM-691
         var roleId = this.options.selectedRoleId,
            groupId = this.options.selectedGroupId;
      },

      /**
       * Event handler for add user button
       * @method onAddUser
       * @param {e} Event object
       */
      onAddUser: function RM_UsersAndGroups_onAddUser(e)
      {
         // FIXME: See RM-690
         var roleId = this.options.selectedRoleId;
      },

      /**
       * Event handler for delete user button
       * @method onDeleteUser
       * @param {e} Event object
       */
      onDeleteUser: function RM_UsersAndGroups_onDeleteUser(e)
      {
         // FIXME: See RM-691
         var roleId = this.options.selectedRoleId,
            userId = this.options.selectedUserId;
      },

      /**
       * Event handler for group selection
       * @method onGroupSelect
       * @param {e} Event object
       */
      onGroupSelect: function RM_UsersAndGroups_onGroupSelect(e)
      {
         var el = Event.getTarget(e);

         // get the ID of the element - in the format "group-groupId" and extract the groupId value
         var groupId = el.id.substring(6);

         // update roleId value
         window.location.hash += '&groupId=' + encodeURI(groupId);

         // update groupId value
         this.updateSelectedGroupUI(groupId);

         Event.stopEvent(e);
      },

      /**
       * Event handler for user selection
       * @method onUserSelect
       * @param {e} Event object
       */
      onUserSelect: function RM_UsersAndGroups_onUserSelect(e)
      {
         var el = Event.getTarget(e);

         // get the ID of the element - in the format "user-userId" and extract the userId value
         var userId = el.id.substring(5);

         // update roleId value
         window.location.hash += '&userId=' + encodeURI(userId);

         // update userId value
         this.updateSelectedUserUI(userId);

         Event.stopEvent(e);
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
            url: Alfresco.constants.PROXY_URI + "api/rma/admin/rmroles?user=" + Alfresco.constants.USERNAME + "&auths=true",
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
       * Helper to update the group selection.
       *
       * @method updateSelectedGroupUI
       * @param {groupId} Group ID to update for, null to empty the list
       */
      updateSelectedGroupUI: function RM_UsersAndGroups_updateSelectedGroupUI(groupId)
      {
         // update selected item background
         var groupLinks = Dom.getElementsByClassName("group", "a");
         for (var i in groupLinks)
         {
            if (groupLinks.hasOwnProperty(i))
            {
               // group link ID is in the format "group-groupId"
               var groupLinkId = groupLinks[i].id,
                  liParent = Dom.get(groupLinkId).parentNode;

               if (groupLinkId.substring(6) === groupId)
               {
                  // found item to selected
                  Dom.addClass(liParent, "selected");
                  this.options.selectedGroupId = groupId;
                  YAHOO.Bubbling.fire("rmGroupSelected");
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
       * Helper to update the user selection.
       *
       * @method updateSelectedUserUI
       * @param {userId} User ID to update for, null to empty the list
       */
      updateSelectedUserUI: function RM_UsersAndGroups_updateSelectedUserUI(userId)
      {
         // update selected item background
         var userLinks = Dom.getElementsByClassName("user", "a");
         for (var i in userLinks)
         {
            if (userLinks.hasOwnProperty(i))
            {
               // user link ID is in the format "role-roleId"
               var userLinkId = userLinks[i].id,
               liParent = Dom.get(userLinkId).parentNode;

               if (userLinkId.substring(5) === userId)
               {
                  // found item to selected
                  Dom.addClass(liParent, "selected");
                  this.options.selectedUserId = userId;
                  YAHOO.Bubbling.fire("rmUserSelected");
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