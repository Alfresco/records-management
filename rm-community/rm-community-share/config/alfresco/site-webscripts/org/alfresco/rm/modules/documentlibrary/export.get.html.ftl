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
<#assign el=args.htmlid>
<div id="${el}-dialog" class="rm-export">
   <div class="hd">${msg("header")}</div>
   <div class="bd">
      <form id="${el}-form" action="" method="POST" enctype="multipart/form-data" accept-charset="utf-8">
         <input id="${el}-nodeRefs" type="hidden" name="nodeRefs" value=""/>
         <input type="hidden" name="format" value="html"/>
         <input id="${el}-failureCallbackFunction" type="hidden" name="failureCallbackFunction" value=""/>
         <input id="${el}-failureCallbackScope" type="hidden" name="failureCallbackScope" value=""/>
         <div class="yui-gd">
            <div class="yui-u">${msg("label.transferFormat")}</div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><input id="${el}-acp" name="transferFormat" type="radio" value="false" checked="true"/></div>
            <div class="yui-u"><label for="${el}-acp">${msg("label.acp")}</label></div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><input id="${el}-zip" name="transferFormat" type="radio" value="true"/></div>
            <div class="yui-u"><label for="${el}-zip">${msg("label.zip")}</label></div>
         </div>
         <div class="bdft">
            <input type="button" id="${el}-ok" value="${msg("button.ok")}" tabindex="0" />
            <input type="button" id="${el}-cancel" value="${msg("button.cancel")}" tabindex="0" />
         </div>
      </form>
   </div>
</div>
