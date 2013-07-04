/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
 * RecordsFlashUpload component.
 *
 * Popups a YUI panel and displays a filelist and buttons to browse for files
 * and upload them. Files can be removed and uploads can be cancelled.
 * For single file uploads version input can be submitted.
 *
 * A multi file upload scenario could look like:
 *
 * var flashUpload = Alfresco.component.getRecordsFlashUploadInstance();
 * var multiUploadConfig =
 * {
 *    siteId: siteId,
 *    containerId: doclibContainerId,
 *    path: docLibUploadPath,
 *    filter: [],
 *    mode: flashUpload.MODE_MULTI_UPLOAD,
 * }
 * this.flashUpload.show(multiUploadConfig);
 *
 * @namespace Alfresco.module
 * @class Alfresco.rm.component.FlashUpload
 * @extends Alfresco.FlashUpload
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;

   /**
    * RecordsFlashUpload constructor.
    *
    * RecordsFlashUpload is considered a singleton so constructor should be treated as private,
    * please use Alfresco.component.getRecordsFlashUploadInstance() instead.
    *
    * @param htmlId {String} The HTML id of the parent element
    * @return {Alfresco.component.RecordsFlashUpload} The new RecordsFlashUpload instance
    * @constructor
    * @private
    */
   Alfresco.rm.component.FlashUpload = function(htmlId)
   {
      Alfresco.rm.component.FlashUpload.superclass.constructor.call(this, htmlId);

      this.name = "Alfresco.rm.component.FlashUpload";
      this.defaultShowConfig.importDestination = null;
      this.defaultShowConfig.importUrl = null;

      Alfresco.util.ComponentManager.reregister(this);

      return this;
   };

   YAHOO.extend(Alfresco.rm.component.FlashUpload, Alfresco.FlashUpload,
   {
      /**
       * Shows uploader in single import mode.
       *
       * @property MODE_SINGLE_IMPORT
       * @static
       * @type int
       */
      MODE_SINGLE_IMPORT: 4,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Initial History Manager event registration
       *
       * @method onReady
       * @override
       */
      onReady: function RecordsFlashUpload_onReady()
      {
         Alfresco.rm.component.FlashUpload.superclass.onReady.call(this);

         var recordTypesContainer = Dom.get(this.id + "-recordTypes-select-container");
         this.widgets.recordTypes = [];
         if (recordTypesContainer)
         {
            var kids = Selector.filter(Dom.getChildren(recordTypesContainer), "span");

            for (var i = 0, ii = kids.length; i < ii; i++)
            {
               this.widgets.recordTypes.push(new YAHOO.widget.Button(kids[i],
               {
                  type: "checkbox"
               }));
            }
         }

         // Save a reference to the HTMLElement displaying recordTypeSection input so we can hide or show it
         this.widgets.recordTypeSection = Dom.get(this.id + "-recordTypeSection-div");
      },

      /**
       * Disables Flash uploader if an error is detected.
       * Possibly a temporary workaround for bugs in SWFObject v1.5
       *
       * @method _disableFlashUploader
       * @override
       */
      _disableFlashUploader: function FlashUpload__disableFlashUploader()
      {
         var fileUpload = Alfresco.util.ComponentManager.findFirst("Alfresco.RecordsFileUpload");
         if (fileUpload)
         {
            fileUpload.hasRequiredFlashPlayer = false;
         }
         return fileUpload;
      },

      /**
       * Adjust the gui according to the config passed into the show method.
       *
       * @method _applyConfig
       * @private
       * @override
       */
      _applyConfig: function RecordsFlashUpload__applyConfig()
      {
         Alfresco.rm.component.FlashUpload.superclass._applyConfig.call(this);

         // Set the panel title
         if (this.showConfig.mode === this.MODE_SINGLE_IMPORT)
         {
            this.titleText.innerHTML = this.msg("header.singleImport");
         }

         if (this.showConfig.mode === this.MODE_SINGLE_IMPORT)
         {
            // Hide the record type form
            Dom.addClass(this.widgets.recordTypeSection, "hidden");
         }
         else
         {
            // Display the record type form
            Dom.removeClass(this.widgets.recordTypeSection, "hidden");
         }
      },

      /**
       * Starts to upload as many files as specified by noOfUploadsToStart
       * as long as there are files left to upload.
       *
       * @method _uploadFromQueue
       * @param noOfUploadsToStart
       * @private
       * @override
       */
      _uploadFromQueue: function RecordsFlashUpload__uploadFromQueue(noOfUploadsToStart)
      {
         // Generate upload POST url
         var url = Alfresco.constants.PROXY_URI,
               fileParamName;

         if (this.showConfig.mode === this.MODE_SINGLE_IMPORT)
         {
            url += (this.showConfig.importURL) ? this.showConfig.importURL : "api/rma/admin/import";
            fileParamName = "archive";
         }
         else
         {
            url += (this.showConfig.uploadURL) ? this.showConfig.uploadURL : "api/upload";
            fileParamName = "filedata";
         }

         // Flash does not correctly bind to the session cookies during POST
         // so we manually patch the jsessionid directly onto the URL instead
         url += ";jsessionid=" + YAHOO.util.Cookie.get("JSESSIONID") + "?lang=" + Alfresco.constants.JS_LOCALE;

         // Pass the CSRF token if the CSRF token filter is enabled
         if (Alfresco.util.CSRFPolicy.isFilterEnabled())
         {
            url += "&" + Alfresco.util.CSRFPolicy.getParameter() + "=" + encodeURIComponent(Alfresco.util.CSRFPolicy.getToken());
         }

         // Find files to upload
         var startedUploads = 0,
            length = this.widgets.dataTable.getRecordSet().getLength(),
            record, flashId, fileInfo, attributes, contentType, aspects = [],
            recordType;

         // Record Types
         for (var i = 0, ii = this.widgets.recordTypes.length; i < ii; i++)
         {
            recordType = this.widgets.recordTypes[i];
            if (recordType.get("checked"))
            {
               aspects.push(recordType.get("value"))
            }
         }

         for (var i = 0; i < length && startedUploads < noOfUploadsToStart; i++)
         {
            record = this.widgets.dataTable.getRecordSet().getRecord(i);
            flashId = record.getData("id");
            fileInfo = this.fileStore[flashId];
            if (fileInfo.state === this.STATE_BROWSING)
            {
               // Upload has NOT been started for this file, start it now
               fileInfo.state = this.STATE_UPLOADING;
               if (this.showConfig.mode === this.MODE_SINGLE_IMPORT)
               {
                  attributes =
                  {
                     destination: this.showConfig.importDestination,
                     username: this.showConfig.username
                  };
               }
               else
               {
                  attributes =
                  {
                     siteId: this.showConfig.siteId,
                     containerId: this.showConfig.containerId,
                     username: this.showConfig.username
                  };
                  if (this.showConfig.mode === this.MODE_SINGLE_UPDATE)
                  {
                     attributes.updateNodeRef = this.showConfig.updateNodeRef;
                     attributes.majorVersion = !this.minorVersion.checked;
                     attributes.description = this.description.value;
                  }
                  else
                  {
                     attributes.uploadDirectory = this.showConfig.uploadDirectory;
                     attributes.contentType = fileInfo.contentType.options[fileInfo.contentType.selectedIndex].value;
                     attributes.aspects = aspects.join(",");
                     attributes.overwrite = this.showConfig.overwrite;
                     if (this.showConfig.thumbnails)
                     {
                        attributes.thumbnails = this.showConfig.thumbnails;
                     }
                  }
               }
               this.uploader.upload(flashId, url, "POST", attributes, fileParamName);
               startedUploads++;
            }
         }
      }
   });
})();
