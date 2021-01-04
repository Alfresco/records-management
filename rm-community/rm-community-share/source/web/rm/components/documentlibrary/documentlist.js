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
 * RM DocumentList component.
 *
 * @namespace Alfresco
 * @class Alfresco.rm.component.DocumentList
 * @superclass Alfresco.DocumentList
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
   var $html = Alfresco.util.encodeHTML,
      $combine = Alfresco.util.combinePaths,
      $jsonDate = Alfresco.util.fromExplodedJSONDate,
      $date = function $date(date, format) { return Alfresco.util.formatDate(Alfresco.util.fromISO8601(date), format) },
      $isValueSet = Alfresco.util.isValueSet;

   /**
    * RecordsDocumentList constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.rm.component.DocumentList} The new Records DocumentList instance
    * @constructor
    */
   Alfresco.rm.component.DocumentList = function(htmlId)
   {
      Alfresco.rm.component.DocumentList.superclass.constructor.call(this, htmlId);

      this.dataSourceUrl = $combine(Alfresco.constants.URL_SERVICECONTEXT, "rm/components/documentlibrary/data/doclist/");

      return this;
   };

   /**
    * Extend Alfresco.DocumentList
    */
   YAHOO.extend(Alfresco.rm.component.DocumentList, Alfresco.DocumentList);

   /**
    * Augment prototype with RecordsActions module, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentProto(Alfresco.rm.component.DocumentList, Alfresco.rm.doclib.Actions, true);

   /**
    * Augment prototype with main class implementation, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentObject(Alfresco.rm.component.DocumentList.prototype,
   {
      /**
       * Fired by YUI when parent element is available for scripting.
       *
       * @method onReady
       * @override
       */
      onReady: function RDL_onReady()
      {
         // Disable drag and drop upload
         this.dragAndDropAllowed = false;

         // Set-up custom title renderers
         this._setupTitleRenderers();

         return Alfresco.rm.component.DocumentList.superclass.onReady.apply(this, arguments);
      },

      /**
       * Configure Records Management title renderers
       *
       * @method _setupMetadataRenderers
       */
      _setupTitleRenderers: function RDL__setupTitleRenderers()
      {
         // Transfer Container title
         this.registerRenderer("RM_transferContainer", function rma_transferContainer(record, label)
         {
            var properties = record.jsNode.properties,
               transferTitle = this.msg("details.transfer-container.title", $html(record.displayName)),
               filterObj =
               {
                  filterId: "transfers",
                  filterData: record.nodeRef,
                  filterDisplay: transferTitle
               };

            // Transfer location if available
            var titleHTML = "";
            if ($isValueSet(properties.rma_transferLocation))
            {
               titleHTML = '<span class="title">(' + $html(properties.rma_transferLocation) + ')</span>';
            }

            return '<h3 class="filename"><a class="filter-change" href="#" rel="' + Alfresco.DocumentList.generateFilterMarkup(filterObj) + '">' +
               $html(transferTitle) + '</a>' + titleHTML + '</h3>';
         });

         // Hold title
         this.registerRenderer("RM_hold", function rma_hold(record, label)
         {
            var holdName = $html(record.jsNode.properties.cm_name),
               filterObj =
               {
                  filterId: "holds",
                  filterData: record.nodeRef,
                  filterDisplay: holdName
               };

            return '<h3 class="filename"><a class="filter-change" href="#" rel="' + Alfresco.DocumentList.generateFilterMarkup(filterObj) + '">' + holdName + '</a></h3>';
         });

         // Unfiled Record Folder title
         this.registerRenderer("RM_unfiledRecordFolder", function rma_unfiledRecordFolder(record, label)
         {
            var displayName = $html(record.jsNode.properties.cm_name);
            filterObj =
            {
               filterId: "unfiledRecords",
               filterData: record.nodeRef,
               filterDisplay: displayName
            };

            return '<h3 class="filename"><a class="filter-change" href="#" rel="' + Alfresco.DocumentList.generateFilterMarkup(filterObj) + '">' + displayName + '</a></h3>';
         });
      },

      /**
       * Configure Records Management metadata renderers
       *
       * @method _setupMetadataRenderers
       */
      _setupMetadataRenderers: function RDL__setupMetadataRenderers()
      {
         // Vital Record Indicator
         this.registerRenderer("RM_vitalRecordIndicator", function rma_vitalRecordIndicator(record, label)
         {
            var vitalRecord = record.jsNode.properties["rma:vitalRecordIndicator"],
               html = "";

            if (vitalRecord !== undefined)
            {
               html = '<span class="item">' + label + this.msg(vitalRecord == "true" ? "label.yes" : "label.no") + '</span>';
            }
            return html;
         });

         // Created by
         this.registerRenderer("RM_createdBy", function(record, label)
         {
            return '<span class="item">' + label + Alfresco.DocumentList.generateUserLink(this, record.jsNode.properties.creator) + '</span>';
         });

         // Modified by
         this.registerRenderer("RM_modifiedBy", function(record, label)
         {
            return '<span class="item">' + label + Alfresco.DocumentList.generateUserLink(this, record.jsNode.properties.modifier) + '</span>';
         });

         // Modified on
         this.registerRenderer("RM_modifiedOn", function(record, label)
         {
            return '<span class="item">' + label + Alfresco.util.formatDate(record.jsNode.properties.modified.iso8601) + '</span>';
         });

         // Date Filed
         this.registerRenderer("RM_dateFiled", function(record, label)
         {
          var dateFiled = record.jsNode.properties["rma:dateFiled"],
             html = "";
          if (dateFiled !== undefined)
             {
                html = '<span class="item">' + label + Alfresco.util.formatDate(record.jsNode.properties.rma_dateFiled.iso8601) + '</span>';
             }
          return html;
         });

         // Publication Date
         this.registerRenderer("RM_publicationDate", function(record, label)
         {
            return '<span class="item">' + label + Alfresco.util.formatDate(record.jsNode.properties.rma_publicationDate.iso8601, "defaultDateOnly") + '</span>';
         });

         // Hold Reason
         this.registerRenderer("RM_holdReason", function(record, label)
         {
            var holdReason = $html(record.jsNode.properties.rma_holdReason) || scope.msg("details.hold.reason.none");

            return '<span class="item">' + label + holdReason + '</span>';
         });

         return Alfresco.rm.component.DocumentList.superclass._setupMetadataRenderers.apply(this, arguments);
      },

      /**
       * DataTable Cell Renderers
       */

      /**
       * Returns thumbnail custom datacell formatter
       *
       * @method fnRenderCellThumbnail
       */
      fnRenderCellThumbnail: function RDL_fnRenderCellThumbnail()
      {
         var scope = this;

         /**
          * Thumbnail custom datacell formatter
          *
          * @method renderCellThumbnail
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function RDL_renderCellThumbnail(elCell, oRecord, oColumn, oData)
         {
            if (oRecord.getData().node.rmNode === undefined)
            {
               var nodeuitype = oRecord.getData().node.uiType;
            }
            else
            {
               var nodeuitype = oRecord.getData().node.rmNode.uiType;
            }

            var record = oRecord.getData(),
               node = record.jsNode,
               properties = node.properties,
               name = record.fileName,
               title = properties.title,
               type = nodeuitype,
               isLink = node.isLink,
               locn = record.location,
               extn = name.substring(name.lastIndexOf(".")),
               docDetailsUrl;

            if (scope.options.viewRendererName === "simple")
            {
               /**
                * Simple View
                */
               oColumn.width = 40;
               Dom.setStyle(elCell, "width", oColumn.width + "px");
               Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

               switch (type)
               {
                  case "record-category":
                  case "record-folder":
                  case "metadata-stub-folder":
                     elCell.innerHTML = '<span class="folder-small">' + (isLink ? '<span class="link"></span>' : '') + '<a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generatePathMarkup(locn) + '"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'rm/components/documentlibrary/images/' + type + '-32.png" /></a>';
                     break;

                  case "transfer-container":
                     var filterObj =
                     {
                        filterId: "transfers",
                        filterData: record.nodeRef,
                        filterDisplay: scope.msg("details.transfer-container.title", $html(record.displayName))
                     };
                     elCell.innerHTML = '<span class="folder-small">' + (isLink ? '<span class="link"></span>' : '') + '<a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generateFilterMarkup(filterObj) + '"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'rm/components/documentlibrary/images/' + type + '-32.png" /></a>';
                     break;

                  case "hold":
                     var filterObj =
                     {
                        filterId: "holds",
                        filterData: record.nodeRef,
                        filterDisplay: $html(record.displayName)
                     };
                     elCell.innerHTML = '<span class="folder-small">' + (isLink ? '<span class="link"></span>' : '') + '<a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generateFilterMarkup(filterObj) + '"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'rm/components/documentlibrary/images/' + type + '-32.png" /></a>';
                     break;

                  case "unfiled-record-folder":
                     var filterObj =
                     {
                        filterId: "unfiledRecords",
                        filterData: record.nodeRef,
                        filterDisplay: $html(record.displayName)
                     };
                     elCell.innerHTML = '<span class="folder-small">' + (isLink ? '<span class="link"></span>' : '') + '<a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generateFilterMarkup(filterObj) + '"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'rm/components/documentlibrary/images/' + type + '-32.png" /></a>';
                     break;

                  case "record-nonelec":
                  case "undeclared-record-nonelec":
                     elCell.innerHTML = '<span class="folder-small">' + (isLink ? '<span class="link"></span>' : '') + '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'rm/components/documentlibrary/images/non-electronic-32.png" />';
                     break;

                  case "metadata-stub":
                     var id = scope.id + '-preview-' + oRecord.getId();
                     docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + scope.options.siteId + "/document-details?nodeRef=" + record.nodeRef;
                     elCell.innerHTML = '<span id="' + id + '" class="icon32">' + (isLink ? '<span class="link"></span>' : '') + '<a href="' + docDetailsUrl + '"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'rm/components/documentlibrary/images/meta-stub-32.png" /></a></span>';
                     break;

                  case "folder":
                     elCell.innerHTML = '<span class="folder-small">' + (isLink ? '<span class="link"></span>' : '') + '<a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generatePathMarkup(locn) + '"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/documentlibrary/images/folder-32.png" /></a>';
                     break;

                  default:
                     var id = scope.id + '-preview-' + oRecord.getId();
                     docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + scope.options.siteId + "/document-details?nodeRef=" + record.nodeRef;
                     elCell.innerHTML = '<span id="' + id + '" class="icon32">' + (isLink ? '<span class="link"></span>' : '') + '<a href="' + docDetailsUrl + '"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/filetypes/' + Alfresco.util.getFileIcon(name) + '" alt="' + extn + '" title="' + $html(title) + '" /></a></span>';

                     // Preview tooltip
                     scope.previewTooltips.push(id);
                     break;
               }
            }
            else
            {
               /**
                * Detailed View
                */
               oColumn.width = 100;
               Dom.setStyle(elCell, "width", oColumn.width + "px");
               Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

               switch (type)
               {
                  case "record-category":
                  case "record-folder":
                  case "metadata-stub-folder":
                     elCell.innerHTML = '<span class="folder">' + (isLink ? '<span class="link"></span>' : '') + '<a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generatePathMarkup(locn) + '"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'rm/components/documentlibrary/images/' + type + '-48.png" /></a>';
                     break;

                  case "transfer-container":
                     var filterObj =
                     {
                        filterId: "transfers",
                        filterData: record.nodeRef,
                        filterDisplay: scope.msg("details.transfer-container.title", $html(record.displayName))
                     };
                     elCell.innerHTML = '<span class="folder">' + (isLink ? '<span class="link"></span>' : '') + '<a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generateFilterMarkup(filterObj) + '"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'rm/components/documentlibrary/images/' + type + '-48.png" /></a>';
                     break;

                  case "hold":
                     var filterObj =
                     {
                        filterId: "holds",
                        filterData: record.nodeRef,
                        filterDisplay: $html(record.displayName)
                     };
                     elCell.innerHTML = '<span class="folder">' + (isLink ? '<span class="link"></span>' : '') + '<a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generateFilterMarkup(filterObj) + '"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'rm/components/documentlibrary/images/' + type + '-48.png" /></a>';
                     break;

                  case "unfiled-record-folder":
                     var filterObj =
                     {
                        filterId: "unfiledRecords",
                        filterData: record.nodeRef,
                        filterDisplay: $html(record.displayName)
                     };
                     elCell.innerHTML = '<span class="folder">' + (isLink ? '<span class="link"></span>' : '') + '<a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generateFilterMarkup(filterObj) + '"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'rm/components/documentlibrary/images/' + type + '-48.png" /></a>';
                     break;

                  case "record-nonelec":
                  case "undeclared-record-nonelec":
                     elCell.innerHTML = '<span class="folder">' + (isLink ? '<span class="link"></span>' : '') + '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'rm/components/documentlibrary/images/non-electronic-75x100.png" />';
                     break;

                  case "metadata-stub":
                     docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + scope.options.siteId + "/document-details?nodeRef=" + record.nodeRef;
                     elCell.innerHTML = '<span class="thumbnail">' + (isLink ? '<span class="link"></span>' : '') + '<a href="' + docDetailsUrl + '"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'rm/components/documentlibrary/images/meta-stub-75x100.png" /></a></span>';
                     break;

                  case "folder":
                     elCell.innerHTML = '<span class="folder">' + (isLink ? '<span class="link"></span>' : '') + '<a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generatePathMarkup(locn) + '"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/documentlibrary/images/folder-48.png" /></a>';
                     break;

                  default:
                     docDetailsUrl = $combine(Alfresco.constants.URL_PAGECONTEXT, "site", scope.options.siteId, "document-details?nodeRef=" + record.nodeRef);
                     elCell.innerHTML = '<span class="thumbnail">' + (isLink ? '<span class="link"></span>' : '') + '<a href="' + docDetailsUrl + '"><img src="' + Alfresco.DocumentList.generateThumbnailUrl(record) + '" alt="' + extn + '" title="' + $html(title) + '" /></a></span>';
                     break;
               }
            }
         };
      },

      /**
       * Public functions
       *
       * Functions designed to be called form external sources
       */

      /**
       * Public function to select files by specified groups
       *
       * @method selectFiles
       * @param p_selectType {string} Can be one of the following:
       * <pre>
       * selectAll - all documents and folders
       * selectNone - deselect all
       * selectInvert - invert selection
       * selectRecords - select all records
       * selectFolders - select all folders
       * </pre>
       */
      selectFiles: function RDL_selectFiles(p_selectType)
      {
         var recordSet = this.widgets.dataTable.getRecordSet(),
            checks = YAHOO.util.Selector.query('input[type="checkbox"]', this.widgets.dataTable.getTbodyEl()),
            len = checks.length,
            record, i, fnCheck, typeMap;

         var typeMapping =
         {
            selectRecords:
            {
               "record": true,
               "record-nonelec": true
            },
            selectUndeclaredRecords:
            {
               "undeclared-record": true,
               "undeclared-record-nonelec": true
            },
            selectRecordFolders:
            {
               "record-folder": true
            },
            selectRecordCategories:
            {
               "record-category": true
            }
         };

         switch (p_selectType)
         {
            case "selectAll":
               fnCheck = function(assetType, isChecked)
               {
                  return true;
               };
               break;

            case "selectNone":
               fnCheck = function(assetType, isChecked)
               {
                  return false;
               };
               break;

            case "selectInvert":
               fnCheck = function(assetType, isChecked)
               {
                  return !isChecked;
               };
               break;

            case "selectRecords":
            case "selectUndeclaredRecords":
            case "selectRecordFolders":
            case "selectRecordCategories":
               typeMap = typeMapping[p_selectType];
               fnCheck = function(assetType, isChecked)
               {
                  if (typeof typeMap === "object")
                  {
                     return typeMap[assetType];
                  }
                  return assetType == typeMap;
               };
               break;

            default:
               fnCheck = function(assetType, isChecked)
               {
                  return isChecked;
               };
         }

         for (i = 0; i < len; i++)
         {
            record = recordSet.getRecord(i);
            if (record.getData("node").rmNode === undefined)
            {
               nodeuitype = record.getData("node").uiType;
            }
            else
            {
               nodeuitype = record.getData("node").rmNode.uiType;
            }
            this.selectedFiles[record.getData("nodeRef")] = checks[i].checked = fnCheck(nodeuitype, checks[i].checked);
         }

         YAHOO.Bubbling.fire("selectedFilesChanged");
      },

      /**
       * DocList View change filter request event handler
       *
       * This function extends the original function in order to
       * reflect the changed filter id in the url
       *
       * @method onChangeFilter
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (new filterId)
       */
      onChangeFilter: function RDL_onChangeFilter(layer, args)
      {
         Alfresco.rm.component.DocumentList.superclass.onChangeFilter.call(this, layer, args);
         var currentFilter = this.currentFilter.filterId,
            filterId = args[1].filterId;
         if (currentFilter !== filterId && filterId !== "path" && filterId !== "savedsearch")
         {
            var hash = window.location.hash;
            hash = hash.replace(/(filter=)[^\&]+/, '$1' + filterId);
            window.location.hash = hash;
         }
         if (currentFilter === "path" && args[1].filterData === "/Unfiled Records")
         {
            var hash = window.location.hash;
            hash = hash.replace(/(filter=)[^\&]+/, '$1' + "unfiledRecords") + "&page=" + this.currentPage;
            window.location.hash = hash;
         }
      }
   }, true);
})();
