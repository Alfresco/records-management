<#assign el=args.htmlid>
<script type="text/javascript">//<![CDATA[
   new Alfresco.rm.component.DocListToolbar("${el}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      hideNavBar: ${(preferences.hideNavBar!false)?string}
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${el}-body" class="toolbar no-check-bg">

   <div id="${el}-headerBar" class="header-bar flat-button theme-bg-2">
      <div class="left">
         <div class="hideable toolbar-hidden DocListTree">
            <div class="new-folder"><button id="${el}-newCategory-button">${msg("button.new-category")}</button></div>
         </div>
         <div class="hideable toolbar-hidden DocListTree">
            <div class="new-folder"><button id="${el}-newFolder-button">${msg("button.new-folder")}</button></div>
         </div>
         <div class="hideable toolbar-hidden DocListTree">
            <div class="file-upload"><button id="${el}-fileUpload-button">${msg("button.upload")}</button></div>
         </div>
         <div id="${el}-import-section" class="hideable toolbar-hidden DocListTree">
            <div class="import"><button id="${el}-import-button" class="no-access-check">${msg("button.import")}</button></div>
         </div>
         <div class="hideable toolbar-hidden DocListTree">
            <div class="export-all"><button id="${el}-exportAll-button">${msg("button.export-all")}</button></div>
         </div>
         
         <div class="hideable toolbar-hidden DocListTree">
            <div class="report"><button id="${el}-report-button">${msg("button.report")}</button></div>
         </div>

         <div class="selected-items">
            <button id="${el}-selectedItems-button" class="no-access-check">${msg("menu.selected-items")}</button>
            <div id="${el}-selectedItems-menu" class="yuimenu">
               <div class="bd">
                  <ul>
                  <#list actionSet as action>
                     <li><a rel="${action.permission!""}" href="#"><span class="${action.id}">${msg(action.label)}</span></a></li>
                  </#list>
                     <li><hr /></li>
                     <li><a rel="" href="#"><span class="onActionDeselectAll">${msg("menu.selected-items.deselect-all")}</span></a></li>
                  </ul>
               </div>
            </div>
         </div>
      </div>
      <div class="right">
         <div class="hide-navbar"><button id="${el}-hideNavBar-button"></button></div>
      </div>
   </div>

   <div id="${el}-navBar" class="nav-bar flat-button theme-bg-4">
      <div class="hideable toolbar-hidden DocListTree">
         <div class="folder-up"><button id="${el}-folderUp-button" class="no-access-check"></button></div>
         <div class="separator">&nbsp;</div>
      </div>
      <div class="hideable toolbar-hidden DocListFilePlan_transfers">
         <div class="folder-up"><button id="${el}-transfersFolderUp-button" class="no-access-check"></button></div>
         <div class="separator">&nbsp;</div>
      </div>
      <div class="hideable toolbar-hidden DocListFilePlan_holds">
         <div class="folder-up"><button id="${el}-holdsFolderUp-button" class="no-access-check"></button></div>
         <div class="separator">&nbsp;</div>
      </div>
      <div id="${el}-breadcrumb" class="breadcrumb hideable toolbar-hidden DocListTree"></div>
      <div id="${el}-description" class="description hideable toolbar-hidden DocListFilter TagFilter DocListSavedSearch DocListFilePlan"></div>
   </div>

</div>
