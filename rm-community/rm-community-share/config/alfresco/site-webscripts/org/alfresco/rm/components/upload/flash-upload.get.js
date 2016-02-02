/**
 * Custom content types
 */
function getContentTypes()
{
   // TODO: Data webscript call to return list of available types
   var contentTypes = [
   {
      id: "cm:content",
      value: "cm_content"
   }];

   return contentTypes;
}

/**
 * Record types
 */
function getRecordTypes()
{
   var result = remote.call("/api/rma/recordmetadataaspects"),
      recordTypes = [];

   if (result.status == 200)
   {
      var json = eval('(' + result + ')');
      recordTypes = recordTypes.concat(json.data.recordMetaDataAspects);
   }

   return recordTypes;
}

model.contentTypes = getContentTypes();
model.recordTypes = getRecordTypes();
