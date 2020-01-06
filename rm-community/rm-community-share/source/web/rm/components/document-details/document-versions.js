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
 * RM Document Details Version component.
 *
 * Overrides the getDocumentVersionMarkup function to add RM specific information
 * and redefines dataSourceURLStem
 *
 * @namespace Alfresco.rm
 * @class Alfresco.rm.DocumentVersions
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
      $userProfileLink = Alfresco.util.userProfileLink,
      $userAvatar = Alfresco.Share.userAvatar;

   /**
    * RM DocumentVersions constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.rm.DocumentVersions} The new component instance
    * @constructor
    */
   Alfresco.rm.DocumentVersions = function RM_DocumentVersions_constructor(htmlId)
   {
      Alfresco.rm.DocumentVersions.superclass.constructor.call(this, htmlId);
      return this;
   };

   YAHOO.extend(Alfresco.rm.DocumentVersions, Alfresco.DocumentVersions,
   {
      /**
       * The data source URL stem
       *
       * @property dataSourceURLStem
       * @type string
       */
      dataSourceURLStem: Alfresco.constants.PROXY_URI + "api/rm/rm-version",

      /**
       * Builds and returns the markup for a version.
       *
       * @method getDocumentVersionMarkup
       * @param doc {Object} The details for the document
       */
      getDocumentVersionMarkup: function RM_DocumentVersions_getDocumentVersionMarkup(doc)
      {
         var downloadURL = Alfresco.constants.PROXY_URI + 'api/node/content/' + doc.nodeRef.replace(":/", "") + '/' + doc.name + '?a=true',
            html = '';

         html += '<div class="version-panel-left">'
         html += '   <span class="document-version">' + $html(doc.label) + '</span>';
         if (Alfresco.util.isValueSet(doc.recordNodeRef, false))
         {
            html += '<span class="document-recorded-version-history" title="' + this.msg("label.recorded-version") + '"/>';
         }
         html += '</div>';
         html += '<div class="version-panel-right">';
         html += '   <h3 class="thin dark" style="width:' + (Dom.getViewportWidth() * 0.25) + 'px;">'; 
         if (doc.isRecordedVersionDestroyed)
         {
             html += this.msg("label.destroyed-version");
         }
         else
         {
             html += $html(doc.name);
         }
         html += '</h3>';
         html += '   <span class="actions">';
         if (doc.isRecordedVersionDestroyed == false)
         {
            if (this.options.allowNewVersionUpload)
            {
               html += '   <a href="#" name=".onRevertVersionClick" rel="' + doc.label + '" class="' + this.id + ' revert" title="' + this.msg("label.revert") + '">&nbsp;</a>';
            }
            html += '      <a href="' + downloadURL + '" class="download" title="' + this.msg("label.download") + '">&nbsp;</a>';
            html += '      <a href="#" name=".onViewHistoricPropertiesClick" rel="' + doc.nodeRef + '" class="' + this.id + ' historicProperties" title="' + this.msg("label.historicProperties") + '">&nbsp;</a>';
         }
         html += '   </span>';
         html += '   <div class="clear"></div>';
         html += '   <div class="version-details">';
         html += '      <div class="version-details-left">'
         html += $userAvatar(doc.creator.userName, 32);
         html += '      </div>';
         html += '      <div class="version-details-right">';
         html += $userProfileLink(doc.creator.userName, doc.creator.firstName + ' ' + doc.creator.lastName, 'class="theme-color-1"') + ' ';
         html += Alfresco.util.relativeTime(Alfresco.util.fromISO8601(doc.createdDateISO)) + '<br />';
         html += ((doc.description || "").length > 0) ? $html(doc.description, true) : '<span class="faded">(' + this.msg("label.noComment") + ')</span>';
         html += '      </div>';
         html += '   </div>';
         html += '</div>';

         html += '<div class="clear"></div>';
         return html;
      }
   });
})();
