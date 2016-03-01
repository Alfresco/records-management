
/**
 * RM Rules Picker.
 *
 * Extends the rule picker module from core to hide some panels from rules picker dialog
 *
 * @namespace Alfresco.rm.module
 * @class Alfresco.rm.module.RulesPicker
 */
(function()
{
   /**
    * RM Rules Picker module constructor.
    *
    * @param containerId {string} A unique id for this component
    * @return {Alfresco.rm.module.RulesPicker} The new rm rules picker instance
    * @constructor
    */
   Alfresco.rm.module.RulesPicker = function RM_RulesPicker_constructor(containerId)
   {
      Alfresco.rm.module.RulesPicker.superclass.constructor.call(this, containerId);
      return this;
   };

   YAHOO.extend(Alfresco.rm.module.RulesPicker, Alfresco.module.RulesPicker,
   {
      /**
       * Internal show dialog function
       *
       * @method _showDialog
       * @override
       */
      _showDialog: function RM_RulesPicker__showDialog()
      {
         Dom.getPreviousSibling(this.id + "-modeGroup").setAttribute("style", "display:none;");
         Dom.getPreviousSibling(this.id + "-sitePicker").setAttribute("style", "display:none;");
         Dom.get(this.id + "-modeGroup").setAttribute("style", "display:none;");
         Dom.get(this.id + "-sitePicker").setAttribute("style", "display:none;");
         Dom.get(this.id + "-treeview").setAttribute("style", "width: 19.5em !important;");
         Dom.get(this.id + "-dialog").setAttribute("style", "width:31.5em; min-width:31.5em;");
         Dom.get(this.id + "-dialog").setAttribute("style", "visibility:inherit;");

         Alfresco.rm.module.RulesPicker.superclass._showDialog.call(this);
      }
   });
})();