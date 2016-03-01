
/**
 * RMDnDUpload component.
 *
 * @namespace Alfresco
 * @class Alfresco.rm.component.DNDUpload
 * @extends Alfresco.DNDUpload
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      KeyListener = YAHOO.util.KeyListener;

   /**
    * RMDnDUpload constructor.
    *
    * RMDnDUpload is considered a singleton so constructor should be treated as private,
    * please use Alfresco.getRecordsHtmlUploadInstance() instead.
    *
    * @param htmlId {String} The HTML id of the parent element
    * @return {Alfresco.rm.component.DNDUpload} The new RMDnDUpload instance
    * @constructor
    * @private
    */
   Alfresco.rm.component.DNDUpload = function(htmlId)
   {
      Alfresco.rm.component.DNDUpload.superclass.constructor.call(this, htmlId);

      this.name = "Alfresco.rm.component.DNDUpload";
      Alfresco.util.ComponentManager.reregister(this);

      return this;
   };

   YAHOO.extend(Alfresco.rm.component.DNDUpload, Alfresco.DNDUpload,
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
       * Overrides the _applyConfig from the base class to adjust the gui.
       *
       * @method _applyConfig
       * @override
       */
      _applyConfig: function RMDnDUpload__applyConfig()
      {
         // Call super class that does the main part of the config attributes
         Alfresco.rm.component.DNDUpload.superclass._applyConfig.call(this);

         // Change the panel header text and the button label
         if (this.showConfig.mode === this.MODE_SINGLE_IMPORT)
         {
            this.titleText.innerHTML = this.msg("header.import");
            this.widgets.fileSelectionOverlayButton.set("label", this.msg("button.selectImportFile"));
         }
         else
         {
            this.titleText.innerHTML = this.msg("header.multiUpload");
            this.widgets.fileSelectionOverlayButton.set("label", this.msg("button.selectFiles"));
         }
      }
   });
})();
