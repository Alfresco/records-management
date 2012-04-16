const REQUEST_MAX = 1000;

/**
 * Main entry point: Create collection of documents and folders in the given space
 *
 * @method doclist_main
 */
function doclist_main()
{
   // Use helper function to get the arguments
   var parsedArgs = ParseArgs.getParsedArgs();
   if (parsedArgs === null)
   {
      return;
   }

   var filter = args.filter,
      items = [];

   // Try to find a filter query based on the passed-in arguments
   var allNodes = [],
      totalRecords = 0,
      requestTotalCountMax = 0,
      paged = false,
      favourites = Common.getFavourites(),
      filterParams = Filters.getFilterParams(filter, parsedArgs,
      {
         favourites: favourites
      }),
      query = filterParams.query;
   
   if ((filter || "path") == "path")
   {
      // TODO also add DB filter by "node" (in addition to "path")
      var parentNode = parsedArgs.pathNode;
      if (parentNode !== null)
      {
         var skip = -1,
            max = -1;
          
         if (args.size != null)
         {
            max = args.size;
            
            if (args.pos > 0)
            {
               skip = (args.pos - 1) * max;
            }
         }
          
         var sortField = (args.sortField == null ? "cm:name" : args.sortField),
            sortAsc = (((args.sortAsc == null) || (args.sortAsc == "true")) ? true : false);

         // Get paged set
         requestTotalCountMax = skip + REQUEST_MAX;
         var pagedResult = parentNode.childFileFolders(true, true, filterParams.ignoreTypes, skip, max, requestTotalCountMax, sortField, sortAsc, "TODO");

         allNodes = pagedResult.page;
         totalRecords = pagedResult.totalResultCountUpper;
         paged = true;
      }
   }
   else
   {
       // Query the nodes - passing in sort and result limit parameters
       if (query !== "")
       {
          allNodes = search.query(
          {
             query: query,
             language: filterParams.language,
             page:
             {
                maxItems: (filterParams.limitResults ? parseInt(filterParams.limitResults, 10) : 0)
             },
             sort: filterParams.sort,
             templates: filterParams.templates,
             namespace: (filterParams.namespace ? filterParams.namespace : null)
          });

          totalRecords = allNodes.length;
       }
   }
   
   // Ensure folders and folderlinks appear at the top of the list
   var folderNodes = [],
      documentNodes = [];
   
   for each (node in allNodes)
   {
      try
      {
         if (node.isContainer || node.isLinkToContainer)
         {
            folderNodes.push(node);
         }
         else
         {
            documentNodes.push(node);
         }
      }
      catch (e)
      {
         // Possibly an old indexed node - ignore it
      }
   }
   
   // Node type counts
   var folderNodesCount = folderNodes.length,
      documentNodesCount = documentNodes.length,
      nodes;
   
   if (parsedArgs.type === "documents")
   {
      nodes = documentNodes;
      totalRecords -= folderNodesCount;
   }
   else
   {
      // TODO: Sorting with folders at end -- swap order of concat()
      nodes = folderNodes.concat(documentNodes);
   }
   
   // Pagination
   var pageSize = args.size || nodes.length,
      pagePos = args.pos || "1",
      startIndex = (pagePos - 1) * pageSize;
   
   if (!paged)
   {
       // Trim the nodes array down to the page size
       nodes = nodes.slice(startIndex, pagePos * pageSize);
   }
   
   // Common or variable parent container?
   var parent = null;
   
   if (!filterParams.variablePath)
   {
      // Parent node permissions (and Site role if applicable)
      parent = Evaluator.run(parsedArgs.pathNode, true);
   }

   var isThumbnailNameRegistered = thumbnailService.isThumbnailNameRegistered(THUMBNAIL_NAME),
      thumbnail = null,
      locationNode,
      item;
   
   // Loop through and evaluate each node in this result set
   for each (node in nodes)
   {
      // Get evaluated properties.
      item = Evaluator.run(node);
      if (item !== null)
      {
         item.isFavourite = (favourites[item.node.nodeRef] === true);
         item.likes = Common.getLikes(node);

         // Does this collection of nodes have potentially differering paths?
         if (filterParams.variablePath || item.isLink)
         {
            locationNode = item.isLink ? item.linkedNode : item.node;
            location = Common.getLocation(locationNode, parsedArgs.libraryRoot);
            // Parent node
            if (node.parent != null && 
            	(node.parent.hasPermission("Read") || node.parent.hasPermission("ReadRecords")))
            {
               item.parent = Evaluator.run(node.parent, true);
            }
         }
         else
         {
            location =
            {
               site: parsedArgs.location.site,
               siteTitle: parsedArgs.location.siteTitle,
               sitePreset: parsedArgs.location.sitePreset,
               container: parsedArgs.location.container,
               containerType: parsedArgs.location.containerType,
               path: parsedArgs.location.path,
               file: node.name
            };
         }
         
         // Resolved location
         item.location = location;
         
         // Check: thumbnail type is registered && node is a cm:content subtype && valid inputStream for content property
         if (isThumbnailNameRegistered && item.node.isSubType("cm:content") && item.node.properties.content.inputStream != null)
         {
            // Make sure we have a thumbnail.
            thumbnail = item.node.getThumbnail(THUMBNAIL_NAME);
            if (thumbnail === null)
            {
               // No thumbnail, so queue creation
               item.node.createThumbnail(THUMBNAIL_NAME, true);
            }
         }
         
         items.push(item);
      }
      else
      {
         --totalRecords;
      }
   }

   // Array Remove - By John Resig (MIT Licensed)
   var fnArrayRemove = function fnArrayRemove(array, from, to)
   {
     var rest = array.slice((to || from) + 1 || array.length);
     array.length = from < 0 ? array.length + from : from;
     return array.push.apply(array, rest);
   };
   
   /**
    * De-duplicate orignals for any existing working copies.
    * This can't be done in evaluator.lib.js as it has no knowledge of the current filter or UI operation.
    * Note: This may result in pages containing less than the configured amount of items (50 by default).
   */
   for each (item in items)
   {
      if (item.workingCopy.isWorkingCopy)
      {
         var workingCopySource = String(item.workingCopy.sourceNodeRef);
         for (var i = 0, ii = items.length; i < ii; i++)
         {
            if (String(items[i].node.nodeRef) == workingCopySource)
            {
               fnArrayRemove(items, i);
               --totalRecords;
               break;
            }
         }
      }
   }
   
   var paging =
   {
      totalRecords: totalRecords,
      startIndex: startIndex
   };
   
   if (paged && (totalRecords == requestTotalCountMax))
   {
      paging.totalRecordsUpper = requestTotalCountMax;
   }
   
   return (
   {
      luceneQuery: query,
      paging: paging,
      container: parsedArgs.rootNode,
      parent: parent,
      onlineEditing: utils.moduleInstalled("org.alfresco.module.vti"),
      itemCount:
      {
         folders: folderNodesCount,
         documents: documentNodesCount
      },
      items: items,
      customJSON: slingshotDocLib.getJSON()
   });
}
