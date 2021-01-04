<#--
 #%L
 Alfresco Records Management Module
 %%
 Copyright (C) 2005 - 2021 Alfresco Software Limited
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
<#include "documentlist-v2.lib.ftl" />
<#include "/org/alfresco/components/form/form.dependencies.inc">

<@processJsonModel group="share"/>

<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/documentlibrary/toolbar.css" group="documentlibrary"/>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/rm/components/documentlibrary/toolbar.css" group="documentlibrary"/>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/rm/components/documentlibrary/documentlist.css" group="documentlibrary"/>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/documentlibrary/documentlist_v2.css" group="documentlibrary"/>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/rm/components/document-details/relationships.css" group="documentlibrary"/>
   <@viewRenderererCssDeps/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/components/documentlibrary/toolbar.js" group="documentlibrary"/>
   <@script type="text/javascript" src="${url.context}/res/rm/components/documentlibrary/toolbar.js" group="documentlibrary"/>
   <@script type="text/javascript" src="${url.context}/res/components/documentlibrary/documentlist.js" group="documentlibrary"/>
   <@script type="text/javascript" src="${url.context}/res/rm/components/documentlibrary/documentlist.js" group="documentlibrary"/>
   <@viewRenderererJsDeps/>
</@>

<@markup id="widgets">
   <@createWidgets group="documentlibrary"/>
</@>

<@uniqueIdDiv>
   <@markup id="html">
      <@documentlistTemplate/>
   </@>
</@>
