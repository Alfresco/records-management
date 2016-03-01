 
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
