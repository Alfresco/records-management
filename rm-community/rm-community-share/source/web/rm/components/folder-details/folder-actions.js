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
 * Folder actions component - RM extensions.
 * 
 * @namespace Alfresco
 * @class Alfresco.rm.doclib.FolderActions
 */
(function()
{
   /**
    * RecordsFolderActions constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.rm.doclib.FolderActions} The new RecordsFolderActions instance
    * @constructor
    */
   Alfresco.rm.doclib.FolderActions = function(htmlId)
   {
      Alfresco.rm.doclib.FolderActions.superclass.constructor.call(this, htmlId);
      YAHOO.Bubbling.on("metadataRefresh", function() {
         YAHOO.Bubbling.unsubscribe("metadataRefresh", this.doRefresh, this);
         window.location.reload(true);
      }, this);
      return this;
   };
   
   /**
    * Extend from Alfresco.FolderActions
    */
   YAHOO.extend(Alfresco.rm.doclib.FolderActions, Alfresco.FolderActions);
   
   /**
    * Augment prototype with RecordActions module, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentProto(Alfresco.rm.doclib.FolderActions, Alfresco.rm.doclib.Actions, true);

   YAHOO.lang.augmentObject(Alfresco.rm.doclib.FolderActions.prototype, {
      /**
       * Refresh component in response to filesPermissionsUpdated event
       *
       * @method doRefresh
       */
      doRefresh: function FolderActions_doRefresh()
      {
         YAHOO.Bubbling.unsubscribe("filesPermissionsUpdated", this.doRefresh, this);
         this.refresh('rm/components/folder-details/folder-actions?nodeRef={nodeRef}' + (this.options.siteId ? '&site={siteId}' : ''));
      }
   }, true);

})();
