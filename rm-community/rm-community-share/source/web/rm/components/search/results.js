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
 * Records results common code component.
 *
 * @namespace Alfresco
 * @class Alfresco.rm.component.Results
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * Search constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.rm.component.Results} The new RecordsSearch instance
    * @constructor
    */
   Alfresco.rm.component.Results = function(htmlId)
   {
      /* Mandatory properties */
      this.id = htmlId;

      this.sortby = [{"field": "rma:identifier", "order": "asc"}, {"field": "", "order": "asc"}, {"field": "", "order": "asc"}];

      YAHOO.Bubbling.on("dataTableHeaderCheckboxChange", this.onDataTableHeaderCheckboxChange, this);
      YAHOO.Bubbling.on("dataTableCheckboxChange", this.onDataTableCheckboxChange, this);

      return this;
   };

   Alfresco.rm.component.Results.prototype =
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * siteId to search in.
          *
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * Maximum number of results displayed.
          *
          * @property maxResults
          * @type int
          * @default 500
          */
         maxResults: 500,

         /**
          * Custom property groups
          *
          * @property groups
          * @type Array
          */
         groups: [],

         /**
          * Saved searches
          *
          * @property savedSearches
          * @type Array
          */
         savedSearches: []
      },

      /**
       * Object container for storing YUI widget instances.
       *
       * @property widgets
       * @type object
       */
      widgets: {},

      /**
       * Object container for storing module instances.
       *
       * @property modules
       * @type object
       */
      modules: {},

      /**
       * Number of search results.
       *
       * @property resultsCount
       * @type number
       */
      resultsCount: 0,

      /**
       * Array of NodeRef strings from the search results.
       *
       * @property resultNodeRefs
       * @type Array
       */
      resultNodeRefs: [],

      /**
       * True if there are more results than the ones listed in the table.
       *
       * @property hasMoreResults
       * @type boolean
       */
      hasMoreResults: false,

      /**
       * Array of sort descriptor objects. See constructor above for example object structure.
       *
       * @property sortby
       * @type Array
       */
      sortby: null,

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.rm.component.Results} returns 'this' for method chaining
       */
      setOptions: function RecordsResults_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.rm.component.Results} returns 'this' for method chaining
       */
      setMessages: function RecordsResults_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function RecordsResults_onReady()
      {
         var me = this;

         // Sorting option menus
         this.widgets.sortMenus = [];
         this.widgets.sortOrderMenus = [];
         this._setupSortControls(3);

         // Column hide/show meta-data options
         var onMetadataClick = function onMetadataClick(e)
         {
            var el = Event.getTarget(e);
            var columnKey = el.id.substring(el.id.lastIndexOf('-') + 1);
            var col = me.widgets.dataTable.getColumn(columnKey)
            if (col)
            {
               if (col.hidden)
               {
                  me.widgets.dataTable.showColumn(col);
               }
               else
               {
                  me.widgets.dataTable.hideColumn(col);
               }
            }
         };

         var elAcceptor = function(el)
         {
            return (el.id.indexOf("-metadata-") != -1);
         };

         var elVisitor = function(el)
         {
            Event.on(el, "click", onMetadataClick);
         };

         // Apply meta-data field event handlers via element visitor pattern
         Dom.getElementsBy(elAcceptor, "input", Dom.get(this.id + "-metadata"), elVisitor);

         var onResultOptionsClick = function onResultOptionsClick(e)
         {
            var elToggle = Dom.get(me.id + "-options-toggle");
            var el = Dom.get(me.id + "-options");
            if (el.style.display === "block")
            {
               el.style.display = "none";
               Dom.setStyle(elToggle, "background-image", "url(" + Alfresco.constants.URL_RESCONTEXT + "components/images/collapsed.png)");
            }
            else
            {
               el.style.display = "block";
               Dom.setStyle(elToggle, "background-image", "url(" + Alfresco.constants.URL_RESCONTEXT + "components/images/expanded.png)");
            }
         };

         // Click handler for result options
         Event.on(Dom.get(this.id + "-options-toggle"), "click", onResultOptionsClick);
         // Initial image background css
         Dom.setStyle(this.id + "-options-toggle", "url(" + Alfresco.constants.URL_RESCONTEXT + "components/images/expanded.png)");

         // add the well known fields
         var fields =
         [
            "nodeRef", "type", "name", "title", "description", "modifiedOn", "modifiedByUser", "modifiedBy",
            "createdOn", "createdByUser", "createdBy", "author", "size", "browseUrl", "parentFolder",
            "properties.rma_identifier", "properties.rma_dateFiled", "properties.rma_location",
            "properties.rmc_supplementalMarkingList", "properties.rma_reviewAsOf",
            "properties.rma_recordSearchDispositionEvents", "properties.rma_recordSearchHasDispositionSchedule",
            "properties.rma_recordSearchDispositionActionName", "properties.rma_recordSearchDispositionActionAsOf",
            "properties.rma_recordSearchDispositionInstructions", "properties.rma_recordSearchDispositionAuthority",
            "properties.rma_recordSearchDispositionPeriod", "properties.rma_recordSearchDispositionEventsEligible",
            "properties.rma_recordSearchVitalRecordReviewPeriod"
         ];

         // add the custom groups of properties
         for (var l=0, k=this.options.groups.length; l<k; l++)
         {
            var group = this.options.groups[l];

            for (var i=0, j=group.properties.length; i<j; i++)
            {
               var property = group.properties[i];
               fields.push("properties." + property.prefix + "_" + property.name);
            }
         }

         // DataSource definition
         var uriSearchResults = Alfresco.constants.PROXY_URI + "slingshot/rmsearch/" + this.options.siteId + "?";
         this.widgets.dataSource = new YAHOO.util.DataSource(uriSearchResults,
         {
            responseType: YAHOO.util.DataSource.TYPE_JSON,
            connXhrMode: "queueRequests",
            responseSchema:
            {
                resultsList: "items",
                fields: fields
            }
         });

         // setup of the datatable.
         this._setupDataTable();

         // Finally show the component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-body", "visibility", "visible");
      },

      /**
       * Data table header check box selection change handler
       *
       * @param {e} Event
       * @param {args} Object Event arguments
       */
      onDataTableHeaderCheckboxChange: function RecordsResults_onDataTableHeaderCheckboxChange(e, args)
      {
         this.widgets.addToHold.set("disabled", !args[1].headerCheckBoxChecked || this.resultsCount == 0);
      },

      /**
       * Data table check box selection change handler
       *
       * @param {e} Event
       * @param {args} Object Event arguments
       */
      onDataTableCheckboxChange: function RecordsResults_onDataTableCheckboxChange(e, args)
      {
         var activate = false;
         if (args[1].headerCheckBoxChecked || args[1].checkBoxChecked || args[1].atLeastOneChecked)
         {
            activate = true;
         }
         this.widgets.addToHold.set("disabled", !activate);
      },

      _setupDataTable: function RecordsResults_setupDataTable()
      {
         /**
          * DataTable Cell Renderers
          *
          * Each cell has a custom renderer defined as a custom function. See YUI documentation for details.
          * These MUST be inline in order to have access to the Alfresco.rm.component.Results class (via the "me" variable).
          */
         var me = this;

         /**
          * Record Icon image custom datacell formatter
          *
          * @method renderCellImage
          */
         var renderCellImage = function RecordsResults_renderCellImage(elCell, oRecord, oColumn, oData)
         {
            oColumn.width = 64;
            oColumn.height = 64;
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "height", oColumn.height + "px");
            Dom.setStyle(elCell.parentNode, "text-align", "center");

            var url = me._getBrowseUrlForRecord(oRecord);
            var imageUrl = Alfresco.constants.URL_RESCONTEXT + 'rm/components/documentlibrary/images/record-32.png';
            switch (oRecord.getData("type"))
            {
               case "rma:recordCategory":
                  imageUrl = Alfresco.constants.URL_RESCONTEXT + 'rm/components/documentlibrary/images/record-category-32.png';
                  break;
               case "rma:recordFolder":
                  imageUrl = Alfresco.constants.URL_RESCONTEXT + 'rm/components/documentlibrary/images/record-folder-32.png';
                  break;
            }

            var name = $html(oRecord.getData("name"));
            elCell.innerHTML = '<span><a href="' + encodeURI(url) + '"><img src="' + imageUrl + '" alt="' + name + '" title="' + name + '" /></a></span>';
         };

         /**
          * Vital Record indicator custom datacell formatter
          *
          * @method renderCellVitalRecord
          */
         var renderCellVitalRecord = function RecordsResults_renderCellVitalRecord(elCell, oRecord, oColumn, oData)
         {
            var reviewDate = oRecord.getData("properties.rma_reviewAsOf");
            if (reviewDate)
            {
               // found a vital record - is it due for review?
               var html;
               if (Alfresco.util.fromISO8601(reviewDate) < new Date())
               {
                  var imageUrl = Alfresco.constants.URL_RESCONTEXT + 'rm/components/documentlibrary/images/warning-16.png';
                  var review = $html(me._msg("label.dueForReview"));
                  html = '<span>' + $html(me._msg("label.yes")) + '&nbsp;<img src="' + imageUrl + '" alt="' + review + '" title="' + review + '"/></span>';
               }
               else
               {
                  html = '<span>' + $html(me._msg("label.yes")) + '</span>';
               }
               elCell.innerHTML = html;
            }
         };

         /**
          * URI custom datacell formatter
          *
          * @method renderCellURI
          */
         var renderCellURI = function RecordsResults_renderCellURI(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "white-space", "nowrap");
            var url = me._getBrowseUrlForRecord(oRecord);
            elCell.innerHTML = '<span><a href="' + encodeURI(url) + '">' + oRecord.getData("properties.rma_identifier") + '</a></span>';
         };

         /**
          * Date custom datacell formatter
          *
          * @method renderCellDate
          */
         var renderCellDate = function RecordsResults_renderCellDate(elCell, oRecord, oColumn, oData)
         {
            if (oData)
            {
               elCell.innerHTML = Alfresco.util.formatDate(Alfresco.util.fromISO8601(oData));
            }
         };

         /**
          * Generic HTML-safe custom datacell formatter
          */
         var renderCellSafeHTML = function renderCellSafeHTML(elCell, oRecord, oColumn, oData)
         {
            elCell.innerHTML = $html(oData);
         };

         /**
          * URI custom datacell sorter
          */
         var sortCellURI = function sortCellURI(a, b, desc)
         {
            // identifier format is: YYYY-NNNNNNNNNN where Y=4 digit year and N=zero padded DBID
            var sa = a.getData("properties.rma_identifier");
            var sb = b.getData("properties.rma_identifier");
            var numA = parseInt(sa.substring(0, 4) + sa.substring(5)),
                numB = parseInt(sb.substring(0, 4) + sb.substring(5));

            if (desc)
            {
               return (numA < numB ? 1 : (numA > numB ? -1 : 0));
            }
            return (numA < numB ? -1 : (numA > numB ? 1 : 0));
         };

         /**
          * Vital Record custom datacell sorter
          */
         var sortCellVitalRecord = function sortCellVitalRecord(a, b, desc)
         {
            var sa = null;
            var sb = null;
            if (a.getData("properties.rma_reviewAsOf"))
            {
               sa = Alfresco.util.fromISO8601(a.getData("properties.rma_reviewAsOf"));
            }
            if (b.getData("properties.rma_reviewAsOf"))
            {
               sb = Alfresco.util.fromISO8601(b.getData("properties.rma_reviewAsOf"));
            }
            if (sa === null && sb === null) return 0;
            if (desc)
            {
               if (sa === null) return -1;
               if (sb === null) return 1;
               return (sa < sb ? 1 : (sa > sb ? -1 : 0));
            }
            if (sa === null) return 1;
            if (sb === null) return -1;
            return (sa < sb ? -1 : (sa > sb ? 1 : 0));
         };

         // DataTable column defintions
         var columnDefinitions =
         [
            { key: "check", label: "<input type='checkbox'>", sortable: false, formatter: "checkbox" },
            { key: "image", label: me._msg("label.type"), sortable: false, field: "type", sortable: true, formatter: renderCellImage, width: "64px" },
            { key: "identifier", label: me._msg("label.identifier"), sortable: true, sortOptions: {sortFunction: sortCellURI}, resizeable: true, formatter: renderCellURI },

            { key: "name", label: me._msg("label.name"), field: "name", sortable: true, resizeable: true, formatter: renderCellSafeHTML },
            { key: "title", label: me._msg("label.title"), field: "title", sortable: true, resizeable: true, formatter: renderCellSafeHTML },
            { key: "description", label: me._msg("label.description"), field: "description", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "parentFolder", label: me._msg("label.parentFolder"), field: "parentFolder", sortable: true, resizeable: true, formatter: renderCellSafeHTML },
            { key: "creator", label: me._msg("label.creator"), field: "createdBy", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "created", label: me._msg("label.created"), field: "createdOn", sortable: true, resizeable: true, formatter: renderCellDate, hidden: true },
            { key: "modifier", label: me._msg("label.modifier"), field: "modifiedBy", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "modified", label: me._msg("label.modified"), field: "modifiedOn", sortable: true, resizeable: true, formatter: renderCellDate },
            { key: "author", label: me._msg("label.author"), field: "author", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },

            { key: "dateFiled", label: me._msg("label.dateFiled"), field: "properties.rma_dateFiled", sortable: true, resizeable: true, formatter: renderCellDate },
            { key: "reviewDate", label: me._msg("label.reviewDate"), field: "properties.rma_reviewAsOf", sortable: true, resizeable: true, formatter: renderCellDate, hidden: true },
            { key: "vitalRecord", label: me._msg("label.vitalRecord"), sortable: true, sortOptions: {sortFunction: sortCellVitalRecord}, resizeable: false, formatter: renderCellVitalRecord },
            { key: "location", label: me._msg("label.location"), field: "properties.rma_location", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "supplementalMarkingList", label: me._msg("label.supplementalMarkingList"), field: "properties.rmc_supplementalMarkingList", sortable: true, resizeable: true,  formatter: renderCellSafeHTML, hidden: true },

            { key: "dispositionEvents", label: me._msg("label.dispositionEvents"), field: "properties.rma_recordSearchDispositionEvents", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "dispositionActionName", label: me._msg("label.dispositionActionName"), field: "properties.rma_recordSearchDispositionActionName", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "dispositionActionAsOf", label: me._msg("label.dispositionActionAsOf"), field: "properties.rma_recordSearchDispositionActionAsOf", sortable: true, resizeable: true, formatter: renderCellDate, hidden: true },
            { key: "dispositionEventsEligible", label: me._msg("label.dispositionEventsEligible"), field: "properties.rma_recordSearchDispositionEventsEligible", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "dispositionPeriod", label: me._msg("label.dispositionPeriod"), field: "properties.rma_recordSearchDispositionPeriod", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "hasDispositionSchedule", label: me._msg("label.hasDispositionSchedule"), field: "properties.rma_recordSearchHasDispositionSchedule", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "dispositionInstructions", label: me._msg("label.dispositionInstructions"), field: "properties.rma_recordSearchDispositionInstructions", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "dispositionAuthority", label: me._msg("label.dispositionAuthority"), field: "properties.rma_recordSearchDispositionAuthority", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "vitalRecordReviewPeriod", label: me._msg("label.vitalRecordReviewPeriod"), field: "properties.rma_recordSearchVitalRecordReviewPeriod", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true }
         ];

         // add the custom groups of properties
         for (var l=0, k=this.options.groups.length; l<k; l++)
         {
          var group = this.options.groups[l];

            for (var i=0, j=group.properties.length; i<j; i++)
            {
            var prop = group.properties[i];
            columnDefinitions.push(
                       { key: prop.name, label: prop.label, field: "properties." + prop.prefix + "_" + prop.name, sortable: true, resizeable: true, hidden: true }
                    );
            }
         }

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-results", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 32,
            initialLoad: false,
            MSG_EMPTY: me._msg("message.empty")
         });

         this.widgets.dataTable.on('theadCellClickEvent', Alfresco.rm.dataTableHeaderCheckboxClick);
         this.widgets.dataTable.on('checkboxClickEvent', Alfresco.rm.dataTableCheckboxClick);

         // show initial message
         this._setDefaultDataTableErrors(this.widgets.dataTable);
         this.widgets.dataTable.render();

         // Override abstract function within DataTable to set custom error message
         this.widgets.dataTable.doBeforeLoadData = function RecordsResults_doBeforeLoadData(sRequest, oResponse, oPayload)
         {
            if (oResponse.error)
            {
               try
               {
                  // update message area if any
                  var el = Dom.get(me.id + "-itemcount");
                  if (el)
                  {
                     el.innerHTML = me._msg("message.error");
                  }
                  var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                  me.widgets.dataTable.set("MSG_ERROR", response.message);
               }
               catch(e)
               {
                  me._setDefaultDataTableErrors(me.widgets.dataTable);
               }
            }
            else if (oResponse.results)
            {
               // update the results count
               me.hasMoreResults = (oResponse.results.length > me.options.maxResults);
               if (me.hasMoreResults)
               {
                  oResponse.results = oResponse.results.slice(0, me.options.maxResults);
               }
               me.resultsCount = oResponse.results.length;
               me.renderLoopSize = 32;

               // collect noderefs (for export if required)
               me.resultNodeRefs = new Array(me.resultsCount);
               for (var i=0, j=me.resultsCount; i<j; i++)
               {
                  me.resultNodeRefs[i] = oResponse.results[i].nodeRef;
               }

               // update message area if any
               var el = Dom.get(me.id + "-itemcount");
               if (el)
               {
                  if (me.resultsCount === 0)
                  {
                     el.innerHTML = me._msg("message.empty");
                     me.widgets.dataTable.set("MSG_EMPTY", "");
                  }
                  else
                  {
                     el.innerHTML = me._msg("message.foundresults", me.resultsCount);
                  }
               }

               YAHOO.Bubbling.fire("searchComplete", {count: me.resultsCount});
            }

            // Must return true to have the "Loading..." message replaced by the error message
            return true;
         };
      },

      /**
       * Resets the YUI DataTable errors to our custom messages
       * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
       *
       * @method _setDefaultDataTableErrors
       * @param dataTable {object} Instance of the DataTable
       */
      _setDefaultDataTableErrors: function RecordsResults__setDefaultDataTableErrors(dataTable)
      {
         dataTable.set("MSG_EMPTY", Alfresco.util.message("message.empty", "Alfresco.rm.component.Results"));
         dataTable.set("MSG_ERROR", Alfresco.util.message("message.error", "Alfresco.rm.component.Results"));
      },

      /**
       * Initialises a number of paired sort order asc/des menu controls.
       *
       * @method _setupSortControls
       * @param count {string} Number of control pairs to initialise
       */
      _setupSortControls: function RecordResults__setupSortControls(count)
      {
         var me = this;
         for (var i=0; i<count; i++)
         {
            this.widgets.sortMenus[i] = new YAHOO.widget.Button(this.id + "-sort" + (i+1),
            {
               type: "menu",
               menu: this.id + "-sort" + (i+1) + "-menu",
               lazyloadmenu: false
            });
            this.widgets.sortMenus[i].getMenu().subscribe("click", function(p_sType, p_aArgs, index)
            {
               var menuItem = p_aArgs[1];
               if (menuItem)
               {
                  me.widgets.sortMenus[index].set("label", menuItem.cfg.getProperty("text")  + " " + Alfresco.constants.MENU_ARROW_SYMBOL);
                  me.sortby[index].field = menuItem.value;
               }
            }, i);
            this.widgets.sortOrderMenus[i] = new YAHOO.widget.Button(this.id + "-sort" + (i+1) + "-order",
            {
               type: "menu",
               menu: this.id + "-sort" + (i+1) + "-order-menu",
               lazyloadmenu: false
            });
            this.widgets.sortOrderMenus[i].getMenu().subscribe("click", function(p_sType, p_aArgs, index)
            {
               var menuItem = p_aArgs[1];
               if (menuItem)
               {
                  me.widgets.sortOrderMenus[index].set("label", menuItem.cfg.getProperty("text") + " " + Alfresco.constants.MENU_ARROW_SYMBOL);
                  me.sortby[index].order = menuItem.value;
               }
            }, i);
         }
      },

      /**
       * Clears results before a search is performed
       *
       * @method _clearSearchResults
       */
      _clearSearchResults: function RecordsResults__clearSearchResults()
      {
         // empty results table
         this.widgets.dataTable.set("MSG_EMPTY", this._msg("message.loading"));
         this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());
      },

      /**
       * Updates document list by calling data webscript with current site and path
       *
       * @method _performSearch
       * @param query {string} Query to execute
       */
      _performSearch: function RecordsResults__performSearch(query, filters)
      {
         // empty results table
         this._clearSearchResults();

         // update the ui to show that a search is on-going
         this.widgets.dataTable.render();

         function successHandler(sRequest, oResponse, oPayload)
         {
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
         }

         function failureHandler(sRequest, oResponse)
         {
            if (oResponse.status == 401)
            {
               // Our session has likely timed-out, so refresh to offer the login page
               window.location.reload();
            }
            else
            {
               try
               {
                  var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                  this.widgets.dataTable.set("MSG_ERROR", $html(response.message));
                  this.widgets.dataTable.showTableMessage($html(response.message), YAHOO.widget.DataTable.CLASS_ERROR);
               }
               catch(e)
               {
                  this._setDefaultDataTableErrors(this.widgets.dataTable);
                  this.widgets.dataTable.render();
               }
            }
         }

         this.widgets.dataSource.sendRequest(this._buildSearchParams(query, filters),
         {
            success: successHandler,
            failure: failureHandler,
            scope: this
         });
      },

      /**
       * Build URI sort parameter string for search.
       *
       * @method _buildSortParam
       * @return {string} The sort parameter string for search.
       */
      _buildSortParam: function RecordsResults__buildSortParam()
      {
         // encode and pack the sort fields as "property/dir" i.e. "cm:name/asc"
         // comma separated between each property and direction pair
         var sort = "";
         for (var i in this.sortby)
         {
            var field = this.sortby[i].field;
            if (field && field.length !== 0)
            {
               if (sort.length !== 0)
               {
                  sort += ",";
               }
               sort += field + '/' + this.sortby[i].order;
            }
         }
         return sort;
      },

      /**
       * Build URI parameter string for search JSON data webscript.
       *
       * @method _buildSearchParams
       * @param query {string} Query to execute
       * @return {string} The parameters string for search.
       */
      _buildSearchParams: function RecordsResults__buildSearchParams(query, filters)
      {
         // build the parameter string and encode each value
         var params = YAHOO.lang.substitute("site={site}&query={query}&sortby={sortby}&filters={filters}&maxResults={maxResults}",
         {
            site: encodeURIComponent(this.options.siteId),
            query : query !== null ? encodeURIComponent(query) : "",
            filters : encodeURIComponent(filters),
            sortby : encodeURIComponent(this._buildSortParam()),
            maxResults : this.options.maxResults + 1 // to be able to know whether we got more results
         });

         return params;
      },

      /**
       * Constructs the browse url for a given record.
       */
      _getBrowseUrlForRecord: function _getBrowseUrlForRecord(oRecord)
      {
         var url = "#";
         if (oRecord.getData("browseUrl") !== undefined)
         {
            url = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/" + oRecord.getData("browseUrl");
         }
         return url;
      }
   };
})();
