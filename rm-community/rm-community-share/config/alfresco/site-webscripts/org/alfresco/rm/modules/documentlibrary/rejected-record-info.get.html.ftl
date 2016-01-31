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
