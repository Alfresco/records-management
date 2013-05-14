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

         this.registerEventHandler('click','button#addGroup-button',
         {
            handler: this.onAddGroup,
            scope: this
         });

         this.registerEventHandler('click','button#deleteGroup-button',
         {
            handler: this.onDeleteGroup,
            scope: this
         });

         this.registerEventHandler('click','button#addUser-button',
         {
            handler: this.onAddUser,
            scope: this
         });

         this.registerEventHandler('click','button#deleteUser-button',
         {
            handler: this.onDeleteUser,
            scope: this
         });

         this.registerEventHandler('click','.role',
         {
            handler: this.onRoleSelect,
            scope: this
         });

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

         // query the list of roles, groups and users to populate the roles list
         this.updateRolesList();
      },

      /**
       * Event handler for add group button
       * @method onAddGroup
       * @param {e} Event object
       */
      onAddGroup: function RM_UsersAndGroups_onAddGroup(e)
      {
         // FIXME: See RM-690
      },

      /**
       * Event handler for delete group button
       * @method onDeleteGroup
       * @param {e} Event object
       */
      onDeleteGroup: function RM_UsersAndGroups_onDeleteGroup(e)
      {
         // FIXME: See RM-691
      },

      /**
       * Event handler for add user button
       * @method onAddUser
       * @param {e} Event object
       */
      onAddUser: function RM_UsersAndGroups_onAddUser(e)
      {
         // FIXME: See RM-690
      },

      /**
       * Event handler for delete user button
       * @method onDeleteUser
       * @param {e} Event object
       */
      onDeleteUser: function RM_UsersAndGroups_onDeleteUser(e)
      {
         // FIXME: See RM-691
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
               }
               else
               {
                  // deselect previously selected item
                  Dom.removeClass(liParent, "selected");
               }
            }
         }

         // clear the groups list
         var elListGroups = Dom.get("groups-list");
         elListGroups.innerHTML = "";

         // clear the users list
         var elListUsers = Dom.get("users-list");
         elListUsers.innerHTML = "";

         // display the query groups/users for the selected user role if any
         if (roleId)
         {
            var groups = this.roles[roleId].assignedGroups;
            groups.sort(this._sortByName);

            for (var i = 0, length = groups.length; i < length; i++)
            {
               var li = document.createElement("li");
               li.innerHTML = groups[i].displayLabel;
               elListGroups.appendChild(li);
            }

            var users = this.roles[roleId].assignedUsers;
            users.sort(this._sortByName);

            for (var i = 0, length = users.length; i < length; i++)
            {
               var li = document.createElement("li");
               li.innerHTML = users[i].displayLabel;
               elListUsers.appendChild(li);
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