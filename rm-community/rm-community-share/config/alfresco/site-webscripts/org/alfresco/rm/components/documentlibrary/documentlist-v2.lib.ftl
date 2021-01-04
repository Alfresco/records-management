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
<#macro viewRenderererJsDeps>
   <#list viewJsDeps as dep>
      <@script type="text/javascript" src="${url.context}/res/${dep}" group="documentlibrary"/>
   </#list>
</#macro>

<#macro viewRenderererCssDeps>
   <#list viewCssDeps as dep>
      <@link rel="stylesheet" type="text/css" href="${url.context}/res/${dep}" group="documentlibrary"/>
   </#list>
</#macro>

<#macro documentlistTemplate>
   <#nested>
   <#assign id=args.htmlid?html>
   <div id="${id}-body" class="toolbar no-check-bg">
      <div id="${id}-headerBar" class="header-bar flat-button theme-bg-2">
         <div class="left">
            <div class="hideable toolbar-hidden DocListTree DocListFilePlan_transfers DocListFilePlan_holds DocListFilePlan_unfiledRecords DocListSavedSearch_savedsearch">
               <div class="file-select">
                  <button id="${id}-fileSelect-button" name="doclist-fileSelect-button">${msg("menu.select")}&nbsp;&#9662;</button>
                  <div id="${id}-fileSelect-menu" class="yuimenu">
                     <div class="bd">
                        <ul>
                           <li><a href="#"><span class="selectRecords">${msg("menu.select.records")}</span></a></li>
                           <li><a href="#"><span class="selectUndeclaredRecords">${msg("menu.select.undeclaredRecords")}</span></a></li>
                           <li><a href="#"><span class="selectRecordFolders">${msg("menu.select.recordFolders")}</span></a></li>
                           <li><a href="#"><span class="selectRecordCategories">${msg("menu.select.recordCategories")}</span></a></li>
                           <li><a href="#"><span class="selectAll">${msg("menu.select.all")}</span></a></li>
                           <li><a href="#"><span class="selectInvert">${msg("menu.select.invert")}</span></a></li>
                           <li><a href="#"><span class="selectNone">${msg("menu.select.none")}</span></a></li>
                        </ul>
                     </div>
                  </div>
               </div>
            </div>
            <div class="hideable toolbar-hidden DocListTree">
               <div class="new-folder"><button id="${id}-newCategory-button">${msg("button.new-category")}</button></div>
            </div>
            <div class="hideable toolbar-hidden DocListTree">
               <div class="new-folder"><button id="${id}-newFolder-button">${msg("button.new-folder")}</button></div>
            </div>
            <div class="hideable toolbar-hidden DocListFilePlan_unfiledRecords">
               <div class="new-folder"><button id="${id}-newUnfiledRecordsFolder-button">${msg("button.new-unfiledRecords-folder")}</button></div>
            </div>
            <div class="hideable toolbar-hidden DocListFilePlan_holds">
               <div class="new-folder"><button id="${id}-newHold-button">${msg("button.new-hold")}</button></div>
            </div>
            <div class="hideable toolbar-hidden DocListTree">
               <div class="file-upload"><button id="${id}-fileUpload-button">${msg("button.upload")}</button></div>
            </div>
            <div class="hideable toolbar-hidden DocListFilePlan_unfiledRecords">
               <div class="file-upload"><button id="${id}-declareRecord-button">${msg("button.declare-record")}</button></div>
            </div>
            <div id="${id}-import-section" class="hideable toolbar-hidden DocListTree">
               <div class="import"><button id="${id}-import-button">${msg("button.import")}</button></div>
            </div>
            <div class="hideable toolbar-hidden DocListTree">
               <div class="export-all" ><button id="${id}-exportAll-button">${msg("button.export-all")}</button></div>
            </div>
            <div class="hideable toolbar-hidden DocListTree">
               <div class="manage-permissions"><button id="${id}-managePermissions-button">${msg("button.manage-permissions")}</button></div>
            </div>
            <div class="hideable toolbar-hidden DocListTree">
                <div class="manage-rules"><button id="${id}-manageRules-button">${msg("button.manage-rules")}</button></div>
            </div>
            <#-- RM-318 - removing Report button temporarily
            <div class="hideable toolbar-hidden DocListTree">
               <div class="report"><button id="${id}-report-button">${msg("button.report")}</button></div>
            </div>
            -->
            <div class="hideable toolbar-hidden DocListFilePlan_unfiledRecords">
               <div class="manage-permissions"><button id="${id}-unfiledManagePermissions-button">${msg("button.manage-permissions")}</button></div>
            </div>
            <div class="hideable toolbar-hidden DocListFilePlan_holds">
               <div class="manage-permissions"><button id="${id}-holdPermissions-button">${msg("button.manage-permissions")}</button></div>
            </div>
            <div class="hideable toolbar-hidden DocListFilePlan_transfers">
               <div class="manage-permissions"><button id="${id}-transferPermissions-button">${msg("button.manage-permissions")}</button></div>
            </div>
            <div class="hideable toolbar-hidden DocListFilePlan_unfiledRecords">
                <div class="manage-rules"><button id="${id}-unfiledManageRules-button">${msg("button.manage-rules")}</button></div>
            </div>
            <div class="selected-items">
               <button id="${id}-selectedItems-button" class="no-access-check">${msg("menu.selected-items")}&nbsp;&#9662;</button>
               <div id="${id}-selectedItems-menu" class="yuimenu">
                  <div class="bd">
                     <ul>
                     <#list actionSet as action>
                        <li><a type="${action.asset!""}" rel="${action.permission!""}" href="#" data-has-aspects="${action.hasAspect}" data-not-aspects="${action.notAspect}" data-has-properties="${action.hasProperty}" data-not-properties="${action.notProperty}"><span class="${action.id}">${msg(action.label)}</span></a></li>
                     </#list>
                        <li><hr /></li>
                        <li><a rel="" href="#"><span class="onActionDeselectAll">${msg("menu.selected-items.deselect-all")}</span></a></li>
                     </ul>
                  </div>
               </div>
            </div>
         </div>
         <div class="right">
            <div class="options-select">
               <button id="${id}-options-button" name="doclist-options-button">${msg("button.options")}&nbsp;&#9662;</button>
               <div id="${id}-options-menu" class="yuimenu">
                  <div class="bd">
                     <ul>
                        <#if preferences.showFolders!true>
                           <li><a href="#"><span class="hideFolders">${msg("button.folders.hide")}</span></a></li>
                        <#else>
                           <li><a href="#"><span class="showFolders">${msg("button.folders.show")}</span></a></li>
                        </#if>
                        <#if preferences.hideNavBar!false>
                           <li><a href="#"><span class="showPath">${msg("button.navbar.show")}</span></a></li>
                        <#else>
                           <li><a href="#"><span class="hidePath">${msg("button.navbar.hide")}</span></a></li>
                        </#if>
                        <li><a href="#"><span class="fullWindow">${msg("button.fullwindow.enter")}</span></a></li>
                        <li><a href="#"><span class="fullScreen">${msg("button.fullscreen.enter")}</span></a></li>
                        <@markup id="documentListViewRendererSelect">
                          <#if viewRenderers??>
                             <#list viewRenderers as viewRenderer>
                                <li class="${viewRenderer.iconClass}"><a href="#"><span class="view ${viewRenderer.id}">${msg(viewRenderer.label)}</span></a></li>
                             </#list>
                          </#if>
                        </@>
                        <li><a href="#"><span class="removeDefaultView">${msg("button.removeDefaultView")}</span></a></li>
                        <li><a href="#"><span class="setDefaultView">${msg("button.setDefaultView")}</span></a></li>
                     </ul>
                  </div>
               </div>
            </div>
            <@markup id="documentListSortSelect">
              <div class="sort-field">
                 <span id="${id}-sortField-button" class="yui-button yui-push-button">
                    <span class="first-child">
                       <button name="doclist-sortField-button"></button>
                    </span>
                 </span>
                 <!-- <span class="separator">&nbsp;</span> -->
                 <select id="${id}-sortField-menu">
                 <#list sortOptions as sort>
                    <option value="${(sort.value!"")?html}" <#if sort.direction??>title="${sort.direction?string}"</#if>>${msg(sort.label)}</option>
                 </#list>
                 </select>
              </div>
              <div class="sort-direction">
                 <span id="${id}-sortAscending-button" class="yui-button yui-push-button">
                    <span class="first-child">
                       <button name="doclist-sortAscending-button"></button>
                    </span>
                 </span>
              </div>
            </@>
            <@markup id="galleryViewSlider">
              <div id="${id}-gallery-slider" class="alf-gallery-slider hidden">
                 <div class="alf-gallery-slider-small"><img src="${url.context}/res/components/documentlibrary/images/gallery-size-small-16.png"></div>
                 <div id="${id}-gallery-slider-bg" class="yui-h-slider alf-gallery-slider-bg">
                 <div id="${id}-gallery-slider-thumb" class="yui-slider-thumb alf-gallery-slider-thumb"><img src="${url.context}/res/components/documentlibrary/images/thumb-n.png"></div>
              </div>
              <div class="alf-gallery-slider-large"><img src="${url.context}/res/components/documentlibrary/images/gallery-size-large-16.png"></div>
              </div>
            </@>
         </div>
      </div>
      <div id="${id}-navBar" class="nav-bar flat-button theme-bg-2">
         <div class="hideable toolbar-hidden DocListTree">
            <div class="folder-up"><button id="${id}-folderUp-button" class="no-access-check"></button></div>
            <div class="separator">&nbsp;</div>
         </div>
         <div id="${id}-breadcrumb" class="breadcrumb hideable toolbar-hidden DocListTree"></div>
         <div id="${id}-description" class="description hideable toolbar-hidden DocListFilter TagFilter DocListSavedSearch DocListFilePlan"></div>
      </div>
   </div>

   <!--[if IE]>
      <iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe>
   <![endif]-->
   <input id="yui-history-field" type="hidden" />
   <div id="${id}-dl-body" class="doclist no-check-bg">

      <#--
         INFORMATION TEMPLATES
      -->
      <div id="${id}-main-template" class="hidden">
         <div>
         </div>
      </div>

      <#-- No items message -->
      <div id="${id}-no-items-template" class="hidden">
         <div class="docListInstructionTitle">${msg("no.items.title")}</div>
      </div>

      <#-- Hidden sub-folders message -->
      <div id="${id}-hidden-subfolders-template" class="hidden">
         <div class="docListInstructionTitle">${msg("no.items.title")}</div>
         <div id="${id}-show-folders-template" class="docListInstructionColumn">
            <img class="docListInstructionImage docListLinkedInstruction" src="${url.context}/res/components/documentlibrary/images/help-folder-48.png">
            <a class="docListInstructionTextSmall docListLinkedInstruction"><#-- We don't know the number of hidden subfolders at this point so this needs to be inserted --></a>
         </div>
      </div>

      <#-- HTML 5 drag and drop instructions -->
      <div id="${id}-dnd-instructions-template" class="hidden">
         <div id="${id}-dnd-instructions">
            <span class="docListInstructionTitle">${msg("dnd.drop.title")}</span>
            <div>
               <div class="docListInstructionColumn docListInstructionColumnRightBorder">
                  <img class="docListInstructionImage" src="${url.context}/res/components/documentlibrary/images/help-drop-list-target-96.png">
                  <span class="docListInstructionText">${msg("dnd.drop.doclist.description")}</span>
               </div>
               <div class="docListInstructionColumn">
                  <img class="docListInstructionImage" src="${url.context}/res/components/documentlibrary/images/help-drop-folder-target-96.png">
                  <span class="docListInstructionText">${msg("dnd.drop.folder.description")}</span>
               </div>
               <div style="clear:both"></div>
            </div>
         </div>
      </div>

      <#-- Standard upload instructions -->
      <div id="${id}-upload-instructions-template" class="hidden">
         <div class="docListInstructionTitle">${msg("standard.upload.title")}</div>
         <div id="${id}-standard-upload-link-template" class="docListInstructionColumn">
            <img class="docListInstructionImage docListLinkedInstruction" src="${url.context}/res/components/documentlibrary/images/help-upload-96.png">
            <span class="docListInstructionText"><a class="docListLinkedInstruction">${msg("standard.upload.description")}</a></span>
         </div>
      </div>

      <#-- Other options? -->
      <div id="${id}-other-options-template" class="hidden">
         <div class="docListOtherOptions">${msg("other.options")}</div>
      </div>

      <#-- The following DOM structures should be editing with respect to documentlist.js function
           fired by the Doclists "tableMsgShowEvent" as it uses this structure to associate the
           image and anchor with the appropriate actions. NOTE: This is only a template that will
           be cloned, during the cloning the id will be appended with "-instance" to ensure uniqueness
           within the page, this allows us to locate each DOM node individually. -->

      <#-- Standard upload (when user has create access) -->
      <div id="${id}-standard-upload-template" class="hidden">
        <div id="${id}-standard-upload-link-template">
           <img class="docListOtherOptionsImage docListLinkedInstruction" src="${url.context}/res/components/documentlibrary/images/help-upload-48.png">
           <span class="docListOtherOptionsText"><a class="docListLinkedInstruction">${msg("dnd.upload.description")}</a></span>
        </div>
      </div>

      <#-- New Folder (when user has create access) -->
      <div id="${id}-new-folder-template" class="hidden">
        <div id="${id}-new-folder-link-template">
           <img class="docListOtherOptionsImage docListLinkedInstruction" src="${url.context}/res/components/documentlibrary/images/help-new-folder-48.png">
           <span class="docListOtherOptionsText"><a class="docListLinkedInstruction">${msg("dnd.newfolder.description")}</a></span>
        </div>
      </div>

      <#-- Hidden sub-folders message -->
      <div id="${id}-show-folders-template" class="hidden">
         <img class="docListOtherOptionsImage docListLinkedInstruction" src="${url.context}/res/components/documentlibrary/images/help-folder-48.png">
         <span class="docListOtherOptionsText"><a class="docListLinkedInstruction"><#-- We don't know the number of hidden subfolders at this point so this needs to be inserted --></a></span>
      </div>
      <#--
         END OF INFORMATION TEMPLATES
      -->

      <#-- Top Bar: Select, Pagination, Sorting & View controls -->
      <div id="${id}-doclistBar" class="yui-gc doclist-bar flat-button no-check-bg"></div>
      <div class="alf-fullscreen-exit-button" class="hidden">
        <span class="yui-button">
            <span class="first-child">
                <button type="button" title="${msg("button.fullscreen.exit")}" id="${args.htmlid}-fullscreen-exit-button"></button>
            </span>
         </span>
      </div>

      <#-- Main Panel: Document List -->
      <@markup id="documentListContainer">
      <div id="${id}-documents" class="documents"></div>
      <div id="${id}-gallery" class="alf-gallery documents"></div>
      <div id="${id}-gallery-empty" class="hidden">
         <div class="yui-dt-liner"></div>
      </div>
      <div id="${args.htmlid}-filmstrip" class="alf-filmstrip alf-gallery documents">
            <div id="${args.htmlid}-filmstrip-main-content" class="alf-filmstrip-main-content">
                <div id="${args.htmlid}-filmstrip-carousel"></div>
                <div id="${args.htmlid}-filmstrip-nav-main-previous" class="alf-filmstrip-nav-button alf-filmstrip-main-nav-button alf-filmstrip-nav-prev">
                    <img src="${page.url.context}/components/documentlibrary/images/filmstrip-main-nav-prev.png" />
                </div>
                <div id="${args.htmlid}-filmstrip-nav-main-next" class="alf-filmstrip-nav-button alf-filmstrip-main-nav-button alf-filmstrip-nav-next">
                    <img src="${page.url.context}/components/documentlibrary/images/filmstrip-main-nav-next.png" />
                </div>
            </div>
            <div id="${args.htmlid}-filmstrip-nav" class="alf-filmstrip-nav">
                <div id="${args.htmlid}-filmstrip-nav-handle" class="alf-filmstrip-nav-handle"></div>
                <div id="${args.htmlid}-filmstrip-nav-carousel"></div>
                <div id="${args.htmlid}-filmstrip-nav-buttons" class="alf-filmstrip-nav-buttons">
                    <div id="${args.htmlid}-filmstrip-nav-previous" class="alf-filmstrip-nav-button alf-filmstrip-nav-prev">
                        <img src="${page.url.context}/components/documentlibrary/images/filmstrip-content-nav-prev.png" />
                    </div>
                    <div id="${args.htmlid}-filmstrip-nav-next" class="alf-filmstrip-nav-button alf-filmstrip-nav-next">
                        <img src="${page.url.context}/components/documentlibrary/images/filmstrip-content-nav-next.png" />
                    </div>
                </div>
            </div>
       </div>
      </@>

      <#-- Bottom Bar: Paginator -->
      <div id="${id}-doclistBarBottom" class="yui-gc doclist-bar doclist-bar-bottom flat-button">
         <div class="yui-u first">
            <div class="file-select">&nbsp;</div>
            <div id="${id}-paginatorBottom" class="paginator"></div>
         </div>
      </div>

      <#--
         RENDERING TEMPLATES
      -->
      <div style="display: none">

         <#-- Action Set "More" template -->
         <div id="${id}-moreActions">
            <div class="internal-show-more" id="onActionShowMore"><a href="#" class="show-more" alt="${msg("actions.more")}"><span>${msg("actions.more")}</span></a></div>
            <div class="more-actions hidden"></div>
         </div>

         <#-- Document List Gallery View Templates-->
         <div id="${id}-gallery-item-template" class="alf-gallery-item hidden">
            <div class="alf-gallery-item-thumbnail">
               <div class="alf-header">
                  <div class="alf-select"></div>
                     <a href="javascript:void(0)" class="alf-show-detail">&nbsp;</a>
               </div>
               <div class="alf-label"></div>
            </div>
            <div class="alf-detail" style="display: none;">
               <div class="bd">
                  <div class="alf-detail-thumbnail"></div>
                  <div class="alf-status"></div>
                  <div class="alf-actions"></div>
                  <div style="clear: both;"></div>
                  <div class="alf-description"></div>
               </div>
            </div>
         </div>

         <#-- Document List Filmstrip View Templates -->
         <div id="${args.htmlid}-filmstrip-nav-item-template" class="alf-filmstrip-nav-item hidden">
            <div class="alf-filmstrip-nav-item-thumbnail">
               <div class="alf-label"></div>
            </div>
         </div>
         <div id="${args.htmlid}-filmstrip-item-template" class="alf-gallery-item hidden">
            <div class="alf-gallery-item-thumbnail">
               <div class="alf-header">
                  <div class="alf-select"></div>
                  <a href="javascript:void(0)" class="alf-show-detail">&nbsp;</a>
                  <div class="alf-label"></div>
               </div>
            </div>
            <div class="alf-detail">
               <div class="bd">
                  <div class="alf-status"></div>
                  <div class="alf-actions"></div>
                  <div style="clear: both;"></div>
                  <div class="alf-description"></div>
               </div>
            </div>
         </div>
      </div>
   </div>
</#macro>
