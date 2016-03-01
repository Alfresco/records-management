
/**
 * Dialog to add a record or a folder to hold(s).
 *
 * @namespace Alfresco.rm.module
 * @class Alfresco.rm.module.AddToHold
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var KeyListener = YAHOO.util.KeyListener;

   /**
    * AddToHold constructor.
    *
    * @param htmlId {string} A unique id for this component
    * @return {Alfresco.rm.module.AddToHold} The new AddToHold instance
    * @constructor
    */
   Alfresco.rm.module.AddToHold = function(htmlId)
   {
      Alfresco.rm.module.AddToHold.superclass.constructor.call(this, "Alfresco.rm.module.AddToHold", htmlId);

      // Re-register with our own name
      this.name = "Alfresco.rm.module.AddToHold";
      Alfresco.util.ComponentManager.reregister(this);

      return this;
   };

   YAHOO.extend(Alfresco.rm.module.AddToHold, Alfresco.rm.module.Hold,
   {
      /**
       * Indicates whether the holds should be retrieved which include the itemNodeRef
       *
       * @type boolean
       * @default false
       */
      includedInHold: false,

      /**
       * The URL for the dialog template
       *
       * @property templateUrl
       * @type string
       * @default Alfresco.constants.URL_SERVICECONTEXT + "rm/modules/documentlibrary/hold/add-to-hold"
       */
      templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "rm/modules/documentlibrary/hold/add-to-hold",

      /**
       * The ajax request method type when the user clicks on the OK button
       *
       * @property onOKAjaxRequestMethodType
       * @type string
       * @default Alfresco.util.Ajax.POST
       */
      onOKAjaxRequestMethodType: Alfresco.util.Ajax.POST
   });
})();