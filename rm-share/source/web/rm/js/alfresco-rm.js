/**
 * Alfresco RM top-level namespaces.
 */
Alfresco.rm = Alfresco.rm || {};
Alfresco.rm.component = Alfresco.rm.component || {};
Alfresco.rm.module = Alfresco.rm.module || {};
Alfresco.rm.template = Alfresco.rm.component.template || {};
Alfresco.rm.doclib = Alfresco.rm.component.doclib || {};

/**
 * Gets the value for the specified parameter from the URL
 *
 * @method getParamValueFromUrl
 */
Alfresco.rm.getParamValueFromUrl = function(param)
{
   var token,
      result = null,
      hash = window.location.hash,
      params = hash.replace('#', '').split("&");
   for (var i = 0; i < params.length; i++)
   {
      token = params[i].split("=");
      if (token[0] === param)
      {
         result = token[1];
         break;
      }
   }
   return result;
};