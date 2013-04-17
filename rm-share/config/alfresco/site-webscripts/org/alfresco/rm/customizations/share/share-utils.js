/**
 * Util method to get a wigdet from an array of widgets by it's id
 *
 * @param widgets An array of widgets
 * @param widgetId The id of the widget which should be retrieved
 * @returns Returns the widget with the specified id or throws an exception if the widget cannot be found.
 */
function getWidgetById(widgets, widgetId)
{
   var widget = null;
   for (var i = 0, length = widgets.length; i < length; i++)
   {
      if (widgets[i].id === widgetId)
      {
         widget = widgets[i];
         break;
      }
   }
   if (!widget)
   {
      throw "Could not find a widget with the id '" + widgetId + "'.";
   }
   return widget;
}