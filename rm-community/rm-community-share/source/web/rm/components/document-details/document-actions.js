/**
 * Document actions component - RM extensions.
 *
 * @namespace Alfresco
 * @class Alfresco.rm.component.DocumentActions
 */
(function()
{
   /**
    * RecordsDocumentActions constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.rm.component.DocumentActions} The new RecordsDocumentActions instance
    * @constructor
    */
   Alfresco.rm.component.DocumentActions = function(htmlId)
   {
      Alfresco.rm.component.DocumentActions.superclass.constructor.call(this, htmlId);
      YAHOO.Bubbling.on("metadataRefresh", function() {
         YAHOO.Bubbling.unsubscribe("metadataRefresh", this.doRefresh, this);
         window.location.reload(true);
      }, this);
      return this;
   };

   /**
    * Extend from Alfresco.DocumentActions
    */
   YAHOO.extend(Alfresco.rm.component.DocumentActions, Alfresco.DocumentActions);

   /**
    * Augment prototype with RecordActions module, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentProto(Alfresco.rm.component.DocumentActions, Alfresco.rm.doclib.Actions, true);

   /**
    * Augment prototype with main class implementation, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentObject(Alfresco.rm.component.DocumentActions.prototype,
   {
      doRefresh: function RecordsDocumentActions_doRefresh()
      {
         var url = 'rm/components/document-details/document-actions?nodeRef={nodeRef}&container={containerId}';
         url += this.options.siteId ? '&site={siteId}' :  '';
         this.refresh(url);
      }
   }, true);
})();