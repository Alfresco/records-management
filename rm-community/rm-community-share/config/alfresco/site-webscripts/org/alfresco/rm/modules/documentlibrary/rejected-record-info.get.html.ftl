<#--
 #%L
 Alfresco Records Management Module
 %%
 Copyright (C) 2005 - 2020 Alfresco Software Limited
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
<div id="rejectedRecordInfoDialog" class="rejected-record-info">
   <div class="hd">${msg("header")}</div>
   <div class="bd">
      <div class="yui-gd rejected-record-info-elements">
         <div class="description">
            <label id="rejectedRecordInfoDialog-description">${msg("label.description", '{displayName}')}</label>
         </div>
         <div class="elements">
            <label for="rejectedRecordInfoDialog-userId">${msg("label.by")}:</label>
            <input type="text" value="" id="rejectedRecordInfoDialog-userId" disabled />
            <label for="rejectedRecordInfoDialog-date">${msg("label.at")}:</label>
            <input type="text" value=""id="rejectedRecordInfoDialog-date" disabled />
            <label for="rejectedRecordInfoDialog-rejectReason">${msg("label.reason")}:</label>
            <textarea id="rejectedRecordInfoDialog-rejectReason" disabled></textarea>
         </div>
      </div>
      <div class="bdft">
         <input type="button" id="rejectedRecordInfoDialog-cancel" value="${msg("button.close")}" tabindex="0" />
      </div>
   </div>
</div>
