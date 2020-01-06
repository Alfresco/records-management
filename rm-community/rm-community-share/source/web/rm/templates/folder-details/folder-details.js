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
 * RecordsFolderDetails template - RM extensions.
 * 
 * @namespace Alfresco
 * @class Alfresco.rm.template.FolderDetails
 */
(function()
{
   /**
    * RecordsFolderDetails constructor.
    * 
    * @return {Alfresco.rm.template.FolderDetails} The new RecordsFolderDetails instance
    * @constructor
    */
   Alfresco.rm.template.FolderDetails = function RecordsFolderDetails_constructor()
   {
      Alfresco.rm.template.FolderDetails.superclass.constructor.call(this);
      
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("detailsRefresh", this.onReady, this);

      return this;
   };
   
   YAHOO.extend(Alfresco.rm.template.FolderDetails, Alfresco.FolderDetails,
   {
      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function RecordsFolderDetails_onReady()
      {
         var config =
         {
            method: "GET",
            url: Alfresco.constants.PROXY_URI + 'slingshot/doclib/rm/node/' + this.options.nodeRef.uri,
            successCallback: 
            { 
               fn: this._getDataSuccess, 
               scope: this 
            },
            failureMessage: "Failed to load data for folder details"
         };
         Alfresco.util.Ajax.request(config);
      }
   });
})();
