<#assign el=args.htmlid?html>
<div id="${el}-dialog">
   <div id="${el}-title" class="hd">${msg("header.remove-hold")}</div>
   <div class="bd">
      <div id="${el}-listofholds">
      </div>
   </div>
   <div class="bdft">
      <input type="button" id="${el}-ok" value="${msg("button.ok")}" />
      <input type="button" id="${el}-cancel" value="${msg("button.cancel")}" />
   </div>
</div>