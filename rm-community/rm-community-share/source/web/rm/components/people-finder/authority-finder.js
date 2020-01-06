/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2020 Alfresco Software Limited
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
 * RM AuthorityFinder component.
 *
 * Extends the base AuthorityFinder component
 *
 * @namespace Alfresco.rm
 * @class Alfresco.rm.AuthorityFinder
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $userProfile = Alfresco.util.userProfileLink;

   /**
    * RM AuthorityFinder constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.rm.AuthorityFinder} The new RM AuthorityFinder instance
    * @constructor
    */
   Alfresco.rm.AuthorityFinder = function(htmlId)
   {
      Alfresco.rm.AuthorityFinder.superclass.constructor.call(this, htmlId);
      return this;
   };

   YAHOO.extend(Alfresco.rm.AuthorityFinder, Alfresco.AuthorityFinder,
   {
      /**
       * Overrides the _setupDataTable from the base class.
       *
       * Setup the YUI DataTable with custom renderers.
       *
       * @method _setupDataTable
       * @private
       */
      _setupDataTable: function RM_AuthorityFinder__setupDataTable()
      {
         /**
          * DataTable Cell Renderers
          *
          * Each cell has a custom renderer defined as a custom function. See YUI documentation for details.
          * These MUST be inline in order to have access to the Alfresco.AuthorityFinder class (via the "me" variable).
          */
         var me = this;

         /**
          * Icon custom datacell formatter
          *
          * @method renderCellIcon
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellIcon = function AuthorityFinder_renderCellIcon(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            var authType = oRecord.getData("authorityType"),
               metadata = oRecord.getData("metadata") || {},
               avatarUrl = Alfresco.constants.URL_RESCONTEXT + "components/images/" + (authType == "USER" ? "no-user-photo-64.png" : "group-64.png");

            if (metadata.avatar && metadata.avatar.length !== 0)
            {
               avatarUrl = Alfresco.constants.PROXY_URI + metadata.avatar + "?c=queue&ph=true";
            }

            // Store calculated URL to icon
            oRecord.setData("calc_iconUrl", avatarUrl);

            elCell.innerHTML = '<img class="avatar" src="' + avatarUrl + '" alt="avatar" />';
         };

         /**
          * Description/detail custom datacell formatter - compact mode
          *
          * @method renderCellDescriptionCompact
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellDescriptionCompact = function AuthorityFinder_renderCellDescriptionCompact(elCell, oRecord, oColumn, oData)
         {
            var authType = oRecord.getData("authorityType"),
               metadata = oRecord.getData("metadata"),
               desc = '';

            if (authType == "USER")
            {
               var userName = oRecord.getData("shortName"),
                  jobTitle = metadata.jobTitle || "",
                  organization = metadata.organization || "";

               desc = '<h3 class="itemname">' + $userProfile(userName, oRecord.getData("displayName"), 'class="theme-color-1"') + ' <span class="lighter">(' + $html(userName) + ')</span></h3>';
               if (jobTitle.length > 0)
               {
                  desc += '<div class="detail">' + $html(jobTitle) + '</div>';
               }
               if (organization.length > 0)
               {
                  desc += '<div class="detail">&nbsp;(' + $html(organization) + ')</div>';
               }
            }
            else if (authType == "GROUP")
            {
               // Changed for RM to hide the 'fullName' in the result list
               desc = '<h3 class="itemname">' + $html(oRecord.getData("displayName")) + '</h3>';
            }
            elCell.innerHTML = desc;
         };

         /**
          * Description/detail custom datacell formatter
          *
          * @method renderCellDescription
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellDescription = function AuthorityFinder_renderCellDescription(elCell, oRecord, oColumn, oData)
         {
            var authType = oRecord.getData("authorityType"),
               metadata = oRecord.getData("metadata"),
               desc = '';

            if (authType == "USER")
            {
               var userName = oRecord.getData("shortName"),
                  jobTitle = metadata.jobTitle || "",
                  organization = metadata.organization || "";

               desc = '<h3 class="itemname">' + $userProfile(userName, oRecord.getData("displayName"), 'class="theme-color-1"') + ' <span class="lighter">(' + $html(userName) + ')</span></h3>';
               if (jobTitle.length > 0)
               {
                  desc += '<div class="detail"><span>' + me.msg("label.title") + ":</span> " + $html(jobTitle) + '</div>';
               }
               if (organization.length > 0)
               {
                  desc += '<div class="detail"><span>' + me.msg("label.company") + ":</span> " + $html(organization) + '</div>';
               }
            }
            else if (authType == "GROUP")
            {
               desc = '<h3 class="itemname">' + $html(oRecord.getData("displayName")) + '</h3>';
               desc += '<div class="detail"><span>' + me.msg("label.name") + ":</span> " + $html(oRecord.getData("fullName")) + '</div>';
            }
            elCell.innerHTML = desc;
         };

         /**
          * Add button datacell formatter
          *
          * @method renderCellIcon
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellAddButton = function AuthorityFinder_renderCellAddButton(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "text-align", "right");

            var domId = Alfresco.util.generateDomId(),
               desc = '<span id="' + domId + '"></span>',
               itemName = oRecord.getData("fullName");

            elCell.innerHTML = desc;

            // create button if require - it is not required in the fullpage view mode
            if (me.options.viewMode !== Alfresco.AuthorityFinder.VIEW_MODE_FULLPAGE)
            {
               var button = new YAHOO.widget.Button(
               {
                  type: "button",
                  label: me.msg("button.add") + " " + me.options.addButtonSuffix,
                  name: domId + "-name",
                  container: domId,
                  disabled: itemName in me.notAllowed,
                  onclick:
                  {
                     fn: me.onItemSelect,
                     obj: oRecord,
                     scope: me
                  }
               });
               me.itemSelectButtons[itemName] = button;

               if ((itemName in me.selectedItems) || (me.options.singleSelectMode && me.singleSelectedItem !== ""))
               {
                  me.itemSelectButtons[itemName].set("disabled", true);
               }
            }
         };

         // DataTable column defintions
         var isCompact = this.options.viewMode == Alfresco.AuthorityFinder.VIEW_MODE_COMPACT,
            columnDefinitions =
            [
               { key: "authorityType", label: "Icon", sortable: false, formatter: renderCellIcon, width: (isCompact ? 36 : 70) },
               { key: "fullName", label: "Description", sortable: false, formatter: (isCompact ? renderCellDescriptionCompact : renderCellDescription) },
               { key: "actions", label: "Actions", sortable: false, formatter: renderCellAddButton, width: 80 }
            ];

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-results", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 32,
            initialLoad: false,
            MSG_EMPTY: this.msg("message.instructions")
         });

         this.widgets.dataTable.doBeforeLoadData = function AuthorityFinder_doBeforeLoadData(sRequest, oResponse, oPayload)
         {
            if (oResponse.results)
            {
               this.renderLoopSize = Alfresco.util.RENDERLOOPSIZE;
            }
            return true;
         };

         // Enable row highlighting
         this.widgets.dataTable.subscribe("rowMouseoverEvent", this.widgets.dataTable.onEventHighlightRow);
         this.widgets.dataTable.subscribe("rowMouseoutEvent", this.widgets.dataTable.onEventUnhighlightRow);
      },

      /**
       * Overrides the _buildSearchParams from the base class.
       *
       * Build URI parameter string for Group Finder JSON data webscript
       *
       * @method _buildSearchParams
       * @param searchTerm {string} Search terms to query
       */
      _buildSearchParams: function RM_AuthorityFinder__buildSearchParams(searchTerm)
      {
         var searchParams = Alfresco.rm.AuthorityFinder.superclass._buildSearchParams.call(this, searchTerm),
            filePlanId = Alfresco.util.getQueryStringParameter("filePlanId");
         return searchParams + "&showGroups=true&zone=APP.RM&shortName=*" + filePlanId;
      }
   });
})();
