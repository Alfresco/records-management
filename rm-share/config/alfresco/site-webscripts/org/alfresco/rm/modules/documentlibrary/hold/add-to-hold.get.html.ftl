<#assign el=args.htmlid?html>

<script type="text/javascript">//<![CDATA[
   new Alfresco.rm.module.AddToHold("${el}").setMessages(
      ${messages}
   );
//]]></script>

<div id="${el}-dialog">
   <div id="${el}-title" class="hd">${msg("header.hold")}</div>
   <div class="bd">
      <div id="${el}-listofholds" class="hold">
      </div>
      <div class="bdft">
         <input type="button" id="${el}-ok" value="${msg("button.ok")}" />
         <input type="button" id="${el}-cancel" value="${msg("button.cancel")}" />
      </div>
   </div>
</div>