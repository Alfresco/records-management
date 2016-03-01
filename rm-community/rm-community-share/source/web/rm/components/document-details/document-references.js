/**
 * Document actions component - RM extensions.
 *
 * @namespace Alfresco
 * @class Alfresco.rm.component.DocumentReferences
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Sel = YAHOO.util.Selector;

   /**
    * RecordsDocumentReferences constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.rm.component.DocumentReferences} The new RecordsDocumentReferences instance
    * @constructor
    */
   Alfresco.rm.component.DocumentReferences = function(htmlId)
   {
      Alfresco.rm.component.DocumentReferences.superclass.constructor.call(this, "Alfresco.rm.component.DocumentReferences", htmlId);
      YAHOO.Bubbling.on("metadataRefresh", this.doRefresh, this);
      return this;
   };

   /**
    * Extend from Alfresco.DocumentActions
    */
   YAHOO.extend(Alfresco.rm.component.DocumentReferences, Alfresco.component.Base,
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
          * The nodeRef to the object that owns the disposition schedule that is configured
          *
          * @property nodeRef
          * @type Alfresco.util.NodeRef
          */
         nodeRef: null,

         /**
          * Current siteId.
          *
          * @property siteId
          * @type string
          */
         siteId: null,

         /**
          * Current containerId.
          *
          * @property containerId
          * @type string
          */
         containerId: null
      },

      doRefresh: function()
      {
         YAHOO.Bubbling.unsubscribe("metadataRefresh", this.doRefresh, this);

         var url = 'config/components/document-details/document-references?nodeRef={nodeRef}&container={containerId}';
         url += this.options.siteId ? '&site={siteId}' :  '';

         this.refresh(url);
      }
   });
})();
