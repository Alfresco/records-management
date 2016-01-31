 <#assign el=args.htmlid?html>
 
 <@markup id="rm-custom-fields" target="fields" action="after" scope="global">

    <#-- RM COMPLIANCE -->
    <div class="yui-gd" id="${el}-compliance-field">
       <div class="yui-u first"><label for="${el}-rm-is-dod">${msg("label.compliance")}:</label></div>
       <div class="yui-u">
          <select id="${el}-compliance" name="compliance" tabindex="0">
             <#list compliance as item>
                <option value="${item.id}">${item.name}</option>
             </#list>
          </select>
       </div>
    </div>
    
 </@markup>
