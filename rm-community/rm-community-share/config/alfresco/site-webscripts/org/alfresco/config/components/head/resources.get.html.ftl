<#--
 #%L
 Alfresco Records Management Module
 %%
 Copyright (C) 2005 - 2019 Alfresco Software Limited
 %%
 This file is part of the Alfresco software.
 -
 If the software was purchased under a paid Alfresco license, the terms of
 the paid license agreement will prevail.  Otherwise, the software is
 provided under the following open source license terms:
 -
 Alfresco is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 -
 Alfresco is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.
 -
 You should have received a copy of the GNU Lesser General Public License
 along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 #L%
-->
<@markup id="rm-resources" target="resources" action="after" scope="global">
   <@script src="${url.context}/res/rm/js/alfresco-rm.js" group="template-common"/>
   <@script src="${url.context}/res/rm/js/event-delegator.js" group="template-common"/>
   <@script src="${url.context}/res/rm/modules/create-site.js" group="template-common"/>
   <@script src="${url.context}/res/rm/modules/documentlibrary/hold/add-to-hold.js" group="template-common" />
   <@script src="${url.context}/res/rm/modules/documentlibrary/hold/hold.js" group="template-common" />
   <@script src="${url.context}/res/modules/simple-dialog.js" group="template-common" />

   <@link rel="stylesheet" type="text/css" href="${url.context}/res/rm/modules/documentlibrary/hold/hold.css" />
</@markup>

<@markup id="rm-shareConstants" target="shareConstants" action="after">
    <@inlineScript group="template-common">
        Alfresco.constants.USER_FULLNAME = "${(user.fullName!"")}";
    </@inlineScript>
</@markup>
