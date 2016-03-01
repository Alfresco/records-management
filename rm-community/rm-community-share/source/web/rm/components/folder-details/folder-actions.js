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
