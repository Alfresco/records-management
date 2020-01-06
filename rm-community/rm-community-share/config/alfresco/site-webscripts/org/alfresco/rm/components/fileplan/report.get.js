/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2020 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software.
 * -
 * If the software was purchased under a paid Alfresco license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 * -
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * -
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * -
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
/**
 * Main entrypoint for component webscript logic
 *
 * @method main
 */
function main()
{
   // Request the disposition actions
   var nodeRef = page.url.args.nodeRef.replace(":/", "");

   // Call the repo to get the fileplan report
   var scriptRemoteConnector = remote.connect("alfresco");
   var repoResponse = scriptRemoteConnector.get("/api/node/" + nodeRef + "/fileplanreport");
   if (repoResponse.status == 401)
   {
      status.setCode(repoResponse.status, "error.loggedOut");
      return;
   }
   else
   {
      var repoJSON = eval('(' + repoResponse + ')');

      // Check if we got a positive result
      if (repoJSON.data)
      {
         var report = repoJSON.data;
         model.firstName = report.firstName;
         model.lastName = report.lastName;
         model.printDate = report.printDate;
         if (report.recordSeries)
         {
            model.recordSeries = report.recordSeries;
         }
         else if (report.recordCategories)
         {
            model.recordCategories = report.recordCategories;
         }
         else if (report.recordFolders)
         {
               model.recordFolders = report.recordFolders;
         }
      }
      else if (repoJSON.status.code)
      {
         status.setCode(repoJSON.status.code, repoJSON.message);
         return;
      }
   }

}

main();
