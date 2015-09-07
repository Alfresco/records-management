// add rm site dashboard
model.sitePresets.push(
{
   id: "rm-site-dashboard",
   name: msg.get("type.recordsManagementSite")
});

// compliance options
var compliance =
[
   {
      id: "{http://www.alfresco.org/model/recordsmanagement/1.0}rmsite",
      name: msg.get("compliance.standard")
  },
  {
      id: "{http://www.alfresco.org/model/dod5015/1.0}site",
      name: msg.get("compliance.dod5015")
  }
];
model.compliance = compliance;

// FIXME: This should be removed after updating the current Alfresco dependency (5.1.a-EA)
model.sitePresetsClass = (model.sitePresets.length == 1) ? " hidden" : "";