if (model.item != null && (model.site != (model.item.location.site != null ? model.item.location.site.name : null) && model.node.isRmNode))
{
   model.paths = getPaths();
   model.widgets = getWidgets();
}

function getPaths()
{
   // NOTE: the %2525 double encoding madness is to cope with the fail of urlrewrite filter to correctly cope with encoded paths
   // see urlrewrite.xml
   var item = model.item,
      path = item.node.originatingLocationPath,
      targetPage = model.rootPage,
      targetPageLabel = model.rootLabelId,
      paths = [],
      folders,
      pathUrl = "",
      x, y;

   if (path)
   {
      paths.push(
      {
         href: targetPage + (path.length < 2 ? "?file=" + encodeURIComponent(item.fileName) : ""),
         label: msg.get(targetPageLabel),
         cssClass: "folder-link"
      });

      if (path.length > 1)
      {
         folders = path.substring(1, path.length).split("/");

         for (x = 0, y = folders.length; x < y; x++)
         {
            pathUrl += "/" + folders[x];
            paths.push(
            {
               href: targetPage + (y - x < 2 ? "?file=" + encodeURIComponent(item.fileName) + "&path=" : "?path=") + encodeURIComponent(pathUrl).replace(/%25/g,"%2525"),
               label: folders[x],
               cssClass: "folder-link folder-open"
            });
         }
      }
   }
   else
   {
      paths.push(
      {
         href: targetPage,
         label: msg.get(targetPageLabel),
         cssClass: "folder-link"
      });
   }

   return paths;
}

function getWidgets()
{
   // NOTE: To stop being redirected in a collaboration site for a declared record we need to set the actualSiteId the same as the siteId.
   // actualSiteId will only be used to check if a redirect is necessary so in this special case we can override the value.
   var widgets = model.widgets;
   var nodeHeaderOptions = widgets[0].options;
   nodeHeaderOptions.actualSiteId = model.site;
   // Disable favourite, likes and comments
   nodeHeaderOptions.showFavourite = false;
   nodeHeaderOptions.showLikes = false;
   nodeHeaderOptions.showComments = false;
   model.showComments = "false";
   return widgets;
}

var isClassified = model.node.isClassified;

// Hide quickShare link if content is classified
model.showQuickShare = (!isClassified).toString();

// Is the content classified?
model.isClassified = isClassified;