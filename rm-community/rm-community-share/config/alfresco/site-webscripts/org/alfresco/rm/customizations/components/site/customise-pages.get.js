/* For RM site we need to ensure that the RM specific pages are always available
 * even if they've been removed...
 * 
 * The presets.xml file contains the following for the RM site...
 * 
 * <sitePages>[{"pageId":"documentlibrary"}, {"pageId":"rmsearch"}]</sitePages>
 * <pageMetadata>{"documentlibrary":{"titleId":"page.rmDocumentLibrary.title", "descriptionId":"page.rmDocumentLibrary.description", "type":"dod5015"}}</pageMetadata>
 * 
 * ...this is the only place that the pageMetadata is defined by default. Once the page is removed the
 * data is effectively lost. So it is duplicated here (which is unfortunate but a necessity at the current time).
 * 
 */

var filePlanPage = null,
    rmSearchPage = null;

var defaultPages = model.pages;
if (defaultPages != null)
{
   // Check the current pages...
   for (var i=0; i<defaultPages.length; i++)
   {
      if (defaultPages[i].pageId == "documentlibrary")
      {
         filePlanPage = defaultPages[i];
      }
      else if (defaultPages[i].pageId == "rmsearch")
      {
         rmSearchPage = defaultPages[i];
      }
   }
    
   if (filePlanPage == null)
   {
      // The File Plan page is missing - add it back in...
      var filePlanPage = {};
      filePlanPage.pageId = "documentLibrary";
      filePlanPage.used = false;
      defaultPages.push(filePlanPage);
   }

   // Make sure the File Plan page is setup correctly for RM...
   filePlanPage.title = msg.get("page.rmDocumentLibrary.title");
   filePlanPage.description = msg.get("page.rmDocumentLibrary.description");
   filePlanPage.type = "dod5015";
   
   if (rmSearchPage == null)
   {
      // The records search is missing - add it back in...
      var rmSearchPage = {};
      rmSearchPage.pageId = "rmsearch";
      rmSearchPage.title = msg.get("page.rmSearch.title");
      rmSearchPage.description = msg.get("page.rmSearch.description");
      rmSearchPage.used = false;
      defaultPages.push(rmSearchPage);
   }
}