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
<script type="text/javascript">//<![CDATA[
   Alfresco.util.addMessages(${messages}, "Alfresco.rm.module.SaveSearch");
//]]></script>
<div id="${args.htmlid}-dialog" class="rm-save-search">
   <div class="hd">${msg("header.savesearch")}</div>
   <div class="bd">
      <form id="${args.htmlid}-form" method="POST" action="" enctype="application/json">
         <input id="${args.htmlid}-query" type="hidden" name="query" value="" />
         <input id="${args.htmlid}-params" type="hidden" name="params" value="" />
         <input id="${args.htmlid}-sort" type="hidden" name="sort" value="" />
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-name">${msg("label.name")}:</label></div>
            <div class="yui-u"><input id="${args.htmlid}-name" type="text" name="name" tabindex="0" maxlength="255" />&nbsp;*</div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-description">${msg("label.description")}:</label></div>
            <div class="yui-u"><input id="${args.htmlid}-description" type="text" name="description" tabindex="0" maxlength="255" /></div>
         </div>
         <div class="bdft">
            <input type="submit" id="${args.htmlid}-save-button" value="${msg("button.save")}" tabindex="0"/>
            <input type="button" id="${args.htmlid}-cancel-button" value="${msg("button.cancel")}" tabindex="0"/>
         </div>
      </form>
   </div>
</div>
