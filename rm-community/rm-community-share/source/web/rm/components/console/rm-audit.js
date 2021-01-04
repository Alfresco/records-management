/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2021 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software.
 * -
 * If the software was purchased under a paid Alfresco license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 * -
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * -
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * -
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
/**
 * RM Audit component
 *
 * @namespace Alfresco.rm.component
 * @class Alfresco.rm.component.RMAudit
 */
(function ()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Sel = YAHOO.util.Selector;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $formatDate = Alfresco.util.formatDate,
      $fromISO8601 = Alfresco.util.fromISO8601;

   /**
    * RM Audit componentconstructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.rm.component.RMAudit} The new component instance
    * @constructor
    */
   Alfresco.rm.component.RMAudit = function RM_Audit_constructor(htmlId)
   {
      Alfresco.rm.component.RMAudit.superclass.constructor.call(this, "Alfresco.rm.component.RMAudit", htmlId,["button", "container", "datasource", "datatable", "paginator", "json", "calendar"]);

      YAHOO.Bubbling.on("PropertyMenuSelected", this.onPropertyMenuSelected, this);

      this.showingFilter = false;

      //search filter person
      this.activePerson = "";

      //query parameters for datasource
      this.queryParams = {};

      return this;
   };

   YAHOO.lang.augmentObject(Alfresco.rm.component.RMAudit,
   {
      VIEW_MODE_DEFAULT: "",
      VIEW_MODE_COMPACT: "COMPACT"
   });

   YAHOO.extend(Alfresco.rm.component.RMAudit, Alfresco.component.Base,
   {
      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function RM_Audit_onReady()
      {
         this.initEvents();

         // Create "Export" and "File As Record" buttons...
         this.widgets.exportButton = Alfresco.util.createYUIButton(this, "export", this.onExportLog,
         {
            disabled: !Alfresco.util.arrayContains(this.options.capabilities, 'ExportAudit')
         });
         this.widgets.fileRecordButton = Alfresco.util.createYUIButton(this, "file-record", this.onFileRecord,
         {
            disabled: !Alfresco.util.arrayContains(this.options.capabilities, 'DeclareAuditAsRecord')
         });

         // Create the logging control buttons...
         this.widgets.toggleLogleButton = Alfresco.util.createYUIButton(this, "toggle", this.onToggleLog,
         {
            disabled: !Alfresco.util.arrayContains(this.options.capabilities, 'EnableDisableAuditByTypes')
         });
         this.widgets.viewLogButton = Alfresco.util.createYUIButton(this, "view", this.onViewLog,
         {
            disabled: !Alfresco.util.arrayContains(this.options.capabilities, 'AccessAudit')
         });
         this.widgets.clearLogButton = Alfresco.util.createYUIButton(this, "clear", this.onClearLog,
         {
            disabled: !Alfresco.util.arrayContains(this.options.capabilities, 'DeleteAudit')
         });

         // Create the filter buttons...
         this.widgets.applyFilterButton = Alfresco.util.createYUIButton(this, "apply", this.onApplyFilters,
         {
            disabled: !Alfresco.util.arrayContains(this.options.capabilities, 'SelectAuditMetadata')
         });
         this.widgets.specifyfilterButton = Alfresco.util.createYUIButton(this, "specifyfilter", this.onSpecifyFilterLog,
         {
            disabled: this.widgets.applyFilterButton ? this.widgets.applyFilterButton.get("disabled") : false
         });

         // Initialize data URI
         // An audit log for node and not in console (all nodes)
         if (this.options.nodeRef)
         {
            var nodeRef = this.options.nodeRef.split('/');
            this.dataUri = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/node/{store_type}/{store_id}/{id}/rmauditlog", { store_type: nodeRef[0], store_id: nodeRef[1], id: nodeRef[2] });
         }
         else {
            this.dataUri = Alfresco.constants.PROXY_URI+'api/rma/admin/rmauditlog';
         }

         this.initWidgets();

         // Check if the filter elements should be disabled
         if (this.widgets.specifyfilterButton && this.widgets.specifyfilterButton.get("disabled"))
         {
            Dom.get(this.id + "-entries").disabled = true;
            Dom.get(this.id + "-fromDate").disabled = true;
            Dom.get(this.id + "-fromDate-icon").innerHTML = "";
            Dom.get(this.id + "-toDate").disabled = true;
            Dom.get(this.id + "-toDate-icon").innerHTML = "";
         }
      },

      /**
       * Initialises event listening and custom events
       */
      initEvents : function RM_Audit_initEvents()
      {
         Event.on(this.id, 'click', this.onInteractionEvent, null, this);

         // Register events
         if (this.options.viewMode === Alfresco.rm.component.RMAudit.VIEW_MODE_DEFAULT)
         {
            this.registerEventHandler('click',[
               {
                  rule : 'button.audit-details',
                  o : {
                     handler: this.onShowDetails,
                     scope : this
                  }
               }
            ]);
         }

         this.registerEventHandler('click',
         [
            {
               rule : 'a.personFilterRemove img',
               o : {
                     handler:this.onRemoveFilter,
                     scope : this
               }
            }
         ]);

         // Decoupled event listeners
         YAHOO.Bubbling.on("personSelected", this.onPersonSelected, this);
         YAHOO.Bubbling.on('PersonFilterActivated',this.onPersonFilterActivated, this);
         YAHOO.Bubbling.on('PersonFilterDeactivated',this.onPersonFilterDeactivated,this);
         YAHOO.Bubbling.on('AuditRecordLocationSelected', this.onAuditRecordLocationSelected, this);

         return this;
      },

      initWidgets: function RM_Audit_initWidgets()
      {
         var me = this;

         if (this.options.viewMode==Alfresco.rm.component.RMAudit.VIEW_MODE_DEFAULT)
         {
            // Initialize dates in UI
            this.widgets['status-date'] = Dom.get(this.id+'-status-date');

//            this.validAuditDates = (this.options.startDate!=="");
//            if (this.validAuditDates)
//            {
//               if (this.options.viewMode==Alfresco.rm.component.RMAudit.VIEW_MODE_COMPACT)
//               {
//                  Dom.get(this.id+'-from-date').innerHTML += ' ' + formatDate(fromISO8601(this.options.startDate),   Alfresco.thirdparty.dateFormat.masks.fullDatetime);
//                  Dom.get(this.id+'-to-date').innerHTML += ' ' + formatDate(fromISO8601(this.options.stopDate),   Alfresco.thirdparty.dateFormat.masks.fullDatetime);
//               }
//            }

            // Initialise menus

            //events menu
            this.widgets['eventMenu'] = new YAHOO.widget.Button(this.id + "-events",
            {
               type: "menu",
               menu: this.id + "-events-menu"
            });

            this.widgets['eventMenu'].on("selectedMenuItemChange", function selectedEventMenuItemChange(event)
            {
               var oMenuItem = event.newValue;
               if (oMenuItem)
               {
                  this.set('label', oMenuItem.cfg.getProperty('text'));
               }
               if (oMenuItem.value !== "ALL")
               {
                  me.queryParams.event=oMenuItem.value;
               }
               else
               {
                  delete me.queryParams.event;
               }
            });

            // Initialise calendar pickers
            // fromDate calendar
            var theDate = new Date();
            var page = (theDate.getMonth() + 1) + "/" + theDate.getFullYear();
            var selected = (theDate.getMonth() + 1) + "/" + theDate.getDate() + "/" + theDate.getFullYear();
            this.widgets.fromCalendar = new YAHOO.widget.Calendar(null, this.id + "-fromDate-cal", { title: this.msg("message.select-from-date"), close: true });
            this.widgets.fromCalendar.cfg.setProperty("pagedate", page);
            this.widgets.fromCalendar.cfg.setProperty("selected", selected);
            this.widgets.fromCalendar.selectEvent.subscribe(this.onDatePickerSelection,  {cal:this.widgets.fromCalendar,el:Dom.get(this.id+'-fromDate'),scope:this},true);

            Event.addListener(this.id + "-fromDate-icon", "click", function () { this.widgets.toCalendar.hide();this.widgets.fromCalendar.show(); }, this, true);

            YAHOO.util.Event.on(this.id + "-fromDate", "click", function() {
               me.widgets.toCalendar.hide();
               me.widgets.fromCalendar.show();
            });

            //toDate calendar
            this.widgets.toCalendar = new YAHOO.widget.Calendar(null, this.id + "-toDate-cal", { title: this.msg("message.select-to-date"), close: true });
            this.widgets.toCalendar.cfg.setProperty("pagedate", page);
            this.widgets.toCalendar.cfg.setProperty("selected", selected);
            this.widgets.toCalendar.selectEvent.subscribe(this.onDatePickerSelection, {cal:this.widgets.toCalendar,el:Dom.get(this.id+'-toDate'),scope:this}, true );

            Event.addListener(this.id + "-toDate-icon", "click", function () { this.widgets.fromCalendar.hide();this.widgets.toCalendar.show(); }, this, true);

            YAHOO.util.Event.on(this.id + "-toDate", "click", function() {
               me.widgets.fromCalendar.hide();
               me.widgets.toCalendar.show();
            });

            // render the calendar control
            this.widgets.fromCalendar.render();
            this.widgets.toCalendar.render();

            YAHOO.util.Event.on(this.id + "-entries", "keyup", function() {
               var field = Dom.get(this.id);
               me.widgets.applyFilterButton.set("disabled", isNaN(field.value));
            });

            this.toggleUI();

            //Sets up datatable and datasource.
            var DS = this.widgets['auditDataSource'] = new YAHOO.util.DataSource(this.dataUri,
            {
               responseType: YAHOO.util.DataSource.TYPE_JSON,
               responseSchema:
               {
                  resultsList:'data.entries',
                  metaFields:
                  {
                     "enabled": "data.enabled",
                     "stopDate": "data.stopped",
                     "startDate": "data.started"
                  }
               }
            });

            DS.doBeforeCallback = function ( oRequest , oFullResponse , oParsedResponse , oCallback )
            {
               me.options.results = oFullResponse.data.entries;

               // Enable/disable export and file record buttons
               if (me.options.results.length===0)
               {
                  me.widgets.exportButton.set('disabled',true);
                  me.widgets.fileRecordButton.set('disabled',true);
               }
               else
               {
                  me.widgets.exportButton.set('disabled',false);
                  me.widgets.fileRecordButton.set('disabled',false);
               }
               return oParsedResponse;
            };

            //date cell formatter
            var renderCellDate = function RecordsResults_renderCellDate(elCell, oRecord, oColumn, oData)
            {
               if (oData)
               {
                  elCell.innerHTML = $formatDate($fromISO8601(oData));
               }
            };

            var renderCellSafeHTML = function RecordsResults_renderCellSafeHTML(elCell, oRecord, oColumn, oData)
            {
               if (oData)
               {
                  elCell.innerHTML = $html(oData);
               }
            };

            // Add the custom formatter to the shortcuts
            YAHOO.widget.DataTable.Formatter.eventCellFormatter = function eventCellFormatter(elLiner, oRecord, oColumn, oData)
            {
               var oRecordData = oRecord._oData;
               if (oRecordData.createPerson === true)
               {
                  if(oRecordData.nodeName != "")
                  {
                      elLiner.innerHTML = oRecordData.event + '&nbsp;-&nbsp;<span class="audit-link-item"><a class="theme-color-1 site-link" href="' + Alfresco.util.profileURL(oRecordData.nodeName) + '">' + $html(oRecordData.nodeName) + '</a></span>';
                  }
                  else
                  {
                      elLiner.innerHTML = oRecordData.event + '&nbsp;&nbsp;&nbsp;';
                  }
               }
               else
               {
                  if (oRecordData.noAvailableLink === true)
                  {
                     elLiner.innerHTML = oRecordData.event + '&nbsp;-&nbsp;' + $html(oRecordData.nodeName) + '&nbsp;&nbsp;&nbsp;';
                  }
                  else
                  {
                     if (oRecordData.nodeName != "")
                     {
                        if (oRecordData.nodeName != "documentLibrary")
                        {
                           var linkPath = me.getNodeDetailsLink(oRecordData);
                           elLiner.innerHTML = oRecordData.event + '&nbsp;-&nbsp;<span class="audit-link-item"><a href="' + linkPath + '">' + $html(oRecordData.nodeName) + '</a></span>&nbsp;&nbsp;&nbsp;';
                        }
                        else
                        {
                           elLiner.innerHTML = oRecordData.event + '&nbsp;&nbsp;&nbsp;';
                        }
                     }
                     else
                     {
                        elLiner.innerHTML = oRecordData.event + '&nbsp;&nbsp;&nbsp;';
                     }
                  }
               }

               //add details button
               var but = new YAHOO.widget.Button(
               {
                  label:me.msg('label.button-details'),
                  //use an id that easily references the results using an array index.
                  id:'log-' + me.recCount++,
                  container:elLiner
               });
               //need this for display
               but.addClass('audit-details-button');
               //and this for event handling1
               but._button.className = 'audit-details';
            };

            this.widgets['auditDataTable'] = new YAHOO.widget.DataTable(this.id+"-auditDT",
                [
                  {key:"timestamp", label:this.msg('label.timestamp'), formatter: renderCellDate, sortable:true, resizeable:true},
                  {key:"fullName", label:this.msg('label.user'), formatter: renderCellSafeHTML, sortable:true, resizeable:true},
                  {key:"userRole", label:this.msg('label.role'), formatter: renderCellSafeHTML, sortable:true, resizeable:true},
                  {key:"event", label:this.msg('label.event'),  formatter:"eventCellFormatter", sortable:true, resizeable:true}
               ],
               DS,
               {
                  caption:this.msg('label.pagination','0'),
                  MSG_EMPTY: this.msg('message.empty'),
                  initialLoad : false
               }
            );

            //we use our own internal counter as oRecord._nCount (from within eventcellformatter)
            //accumulates for all requests, not just the current request which is what we need.
            this.widgets['auditDataTable'].doBeforeLoadData = function doBeforeLoadData(sRequest , oResponse , oPayload)
            {
               // reset
               me.recCount = 0;
               return true;
            };
            //subscribe to event so we can update UI
            this.widgets['auditDataSource'].subscribe('responseParseEvent', this.updateUI, this, true);

            // Load the People Finder component from the server
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "components/people-finder/people-finder",
               dataObj:
               {
                  htmlid: this.id + "-peoplefinder"
               },
               successCallback:
               {
                  fn: this.onPeopleFinderLoaded,
                  scope: this
               },
               failureMessage: this.msg('message.load-peopleFinder-failure'),
               execScripts: true
            });
         }
      },

      /**
       * Updates the UI to show status of UI and start/stop buttons
       */
      toggleUI: function toggleUI()
      {
         //get started/stopped (status) time
         //var statusDate = (this.options.enabled) ? this.options.startDate : this.options.stopDate;
         //var statusMessage = (this.options.enabled) ? 'label.started-at' : 'label.stopped-at';
         //this.widgets['status-date'].innerHTML = this.msg(statusMessage,formatDate(fromISO8601(statusDate),   Alfresco.thirdparty.dateFormat.masks.fullDatetime));

         //update start/stop button
         if (this.options.viewMode==Alfresco.rm.component.RMAudit.VIEW_MODE_DEFAULT)
         {
            if (this.widgets['toggleLogleButton'])
            {
               //if (YAHOO.lang.isUndefined(this.options.enabled) == false)
               //{
                  this.widgets['toggleLogleButton'].set('value',this.options.enabled);
                  this.widgets['toggleLogleButton'].set('label',(this.options.enabled)? this.msg('label.button-stop') : this.msg('label.button-start'));
               //}
            }
         }
      },

      /**
       * Handler for start/stop log button
       */
      onToggleLog: function onToggleLog()
      {
         var me = this;
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: (this.options.enabled) ? this.msg('label.stop-log-title') : this.msg('label.start-log-title'),
            text: (this.options.enabled) ? this.msg('label.stop-log-confirmation') : this.msg('label.start-log-confirmation'),
            buttons: [
            {
               text: this.msg('label.yes'),
               handler: function()
               {
                  me._toggleLog();
                  this.destroy();
               },
               isDefault: false
            },
            {
               text: this.msg('label.no'),
               handler: function()
               {
                  this.destroy();
               },
               isDefault: false
            }
            ]
         });
      },

      /**
       * Handler for clear log button.
       */
      onClearLog: function RM_Audit_onClearLog()
      {
         var me = this;
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg('label.clear-log-title'),
            text: this.msg('label.clear-log-confirmation'),
            buttons: [
            {
               text: this.msg('label.yes'), // To early to localize at this time, do it when called instead
               handler: function()
               {
                  me._clearLog();
                  this.destroy();
               },
               isDefault: false
            },
            {
               text: this.msg('label.no'), // To early to localize at this time, do it when called instead
               handler: function()
               {
                  this.destroy();
               },
               isDefault: false
            }
            ]
         });
      },

      /**
       * Handler for view log button. Displays log in new window
       */
      onViewLog: function RM_Audit_onViewLog()
      {
         var openAuditLogWindow = function openAuditLogWindow()
         {
            return window.open(Alfresco.constants.URL_PAGECONTEXT + 'site/' + this.options.siteId + '/rm-audit', 'Audit_Log', 'resizable=yes,location=no,menubar=no,scrollbars=yes,status=yes,width=1024,height=768');
         };
         // haven't yet opened window yet
         if (!this.fullLogWindowReference)
         {
            this.fullLogWindowReference = openAuditLogWindow.call(this);
         }
         else
         {
            // window has been opened already and is still open, so focus and reload it.
            if (!this.fullLogWindowReference.closed)
            {
               this.fullLogWindowReference.focus();
               this.fullLogWindowReference.location.reload();
            }
            //had been closed so reopen window
            else
            {
               this.fullLogWindowReference = openAuditLogWindow.call(this);
            }
         }
      },

      /**
       * Handler for export log button. Exports log
       */
      onExportLog: function RM_Audit_onExportLog()
      {
         var exportUri = this.dataUri + this._buildQuery();
         //we can't add 'export' to this.queryParams (for buildQuery to generate query)
         //since export is a reserved word. So we add it manually.
         exportUri += (exportUri.indexOf('?')==-1) ? '?export=true&format=html' : '&export=true&format=html';
         window.location.href = exportUri;
      },

      /**
       * Handler for file as record log button. Files log as record
       */
      onFileRecord: function RM_Audit_onFileRecord()
      {
         //show location dialog
         if (!this.modules.selectAuditRecordLocation)
         {
            this.modules.selectAuditRecordLocation = new Alfresco.rm.module.SelectAuditRecordLocation(this.id + "-copyMoveLinkTo");
            Alfresco.util.addMessages(Alfresco.messages.scope[this.name], "Alfresco.rm.module.SelectAuditRecordLocation");
         }

         this.modules.selectAuditRecordLocation.setOptions(
         {
            siteId: this.options.siteId,
            containerId: this.options.containerId,
            path: '',
            files: {}
         }).showDialog();
      },

      onAuditRecordLocationSelected : function RM_Audit_AuditRecordLocationSelected(e, args)
      {
         var me = this;
         var dataObj = {
            destination: args[1].nodeRef,
            user: this.queryParams.user,
            size: this.queryParams.size,
            event: this.queryParams.event,
            from: this.queryParams.from,
            to: this.queryParams.to,
            property: this.queryParams.property
         };

         if (this.activePerson)
         {
            dataObj.user = this.activePerson.userName;
         }

         var theUrl = this.dataUri + this._buildQuery();

         Alfresco.util.Ajax.jsonPost(
         {
            url: theUrl,
            dataObj : dataObj,
            successCallback:
            {
               fn: function RM_Audit_FileRecord_success(serverResponse)
               {
                  // apply current property values to form
                  if (serverResponse.json)
                  {
                     var data = serverResponse.json;

                     if (data.success)
                     {
                        Alfresco.util.PopupManager.displayPrompt(
                        {
                           title: this.msg('label.file-record'),
                           text: this.msg("label.filed-log-message"),
                           noEscape: true,
                           buttons: [
                           {
                              text: this.msg('button.view-record'),
                              handler: function viewRecordHandler()
                              {
                                 window.location.href = Alfresco.constants.URL_PAGECONTEXT+'site/' +  me.options.siteId + '/document-details?nodeRef=' + data.record;
                              }
                           },
                           {
                              text: this.msg('button.ok'),
                              handler: function()
                              {
                                 this.destroy();
                              },
                              isDefault: true
                           }
                           ]
                        });
                     }
                     else
                     {
                        Alfresco.util.PopupManager.displayPrompt(
                        {
                           title: me.msg('label.file-record'),
                           text: data.message,
                           buttons: [
                           {
                              text: me.msg('button.ok'),
                              handler: function()
                              {
                                 this.destroy();
                              },
                              isDefault: false
                           }]
                        });
                     }
                  }
               },
               scope: this
            },
            failureCallback: {
               fn: function fail_file_record(o)
               {
                  if (o.serverResponse.status==400)
                  {

                     Alfresco.util.PopupManager.displayPrompt(
                     {
                        title: me.msg('label.file-record'),
                        text: o.serverResponse.statusText,
                        buttons: [
                        {
                           text: me.msg('button.ok'),
                           handler: function()
                           {
                              this.destroy();
                           },
                           isDefault: false
                        }
                        ]
                     });
                  }
               }
            }
         });
      },

      /**
       * Handles the date being changed in the date picker YUI control.
       *
       * @method onDatePickerSelection
       * @param type
       * @param args
       * @param obj
       * @private
       */
      onDatePickerSelection: function RM_Audit_onDatePickerSelection(type, args, obj)
      {
         // update the date field - contains an array of [year, month, day]
         var selected = args[0][0];
         obj.cal.hide();
         // convert to query date format and insert
         var date = YAHOO.lang.substitute("{year}-{month}-{day}",
         {
            year: selected[0],
            month: Alfresco.util.pad(selected[1], 2),
            day: Alfresco.util.pad(selected[2], 2)
         });
         obj.el.value  = date;
      },

      /**
       * Handler for specify button. Shows the fields in order for user to
       * filter results. Needs to be replaced by people picker
       *
       */
      onSpecifyFilterLog: function RM_Audit_onSpecifyFilterLog()
      {
         if (!this.showingFilter)
         {
            Dom.addClass(this.widgets['people-finder'], 'active');
            this.modules.peopleFinder.clearResults();
            this.widgets.specifyfilterButton.set('label',Alfresco.util.message('label.button-cancel', 'Alfresco.rm.component.RMAudit'));
            this.showingFilter = true;
         }
         else
         {
            Dom.removeClass(this.widgets['people-finder'], 'active');
            this.widgets.specifyfilterButton.set('label',this.msg('label.button-specify'));
            this.showingFilter = false;
         }
      },

      /**
       * Handler for when a person is selected
       */
      onPersonSelected: function RM_Audit_onPersonSelected(e, args)
      {
         Dom.addClass(Sel.query('.personFilter',this.id)[0], 'active');
         var person = args[1];
         this._changeFilterText($html(person.firstName + ' ' + person.lastName));
         this.widgets.specifyfilterButton.set('label',this.msg('label.button-specify'));
         Dom.removeClass(this.widgets['people-finder'],'active');
         this.showingFilter = false;
         YAHOO.Bubbling.fire('PersonFilterActivated',{person:person});
      },

      /**
       * Changes the text displayed to show how the audit log is being filtered
       *
       * @param {text} String object Text to update UI with. If empty string, UI is updated with default label
       */
      _changeFilterText: function(text)
      {
         var el = Sel.query('.personFilter span',this.id)[0];
         el.innerHTML = (text !== "") ? text : this.msg('label.default-filter');
      },

      /**
       * Handler for when people finder has finished loading
       *
       * @param {text} String object HTML template response from people-finder call
       */
      onPeopleFinderLoaded: function RM_Audit_onPeopleFinderLoaded(response)
      {
         // Inject the component from the XHR request into it's placeholder DIV element
         var finderDiv = Dom.get(this.id + "-peoplefinder");
         this.widgets['people-finder'] = finderDiv;
         finderDiv.innerHTML = response.serverResponse.responseText;
         // Find the People Finder by container ID
         this.modules.peopleFinder = Alfresco.util.ComponentManager.get(this.id + "-peoplefinder");
         // Set the correct options for our use
         this.modules.peopleFinder.setOptions(
         {
            viewMode: Alfresco.PeopleFinder.VIEW_MODE_COMPACT,
            singleSelectMode: true
         });
      },

      /**
       * Remove filter handler
       *
       * Removes filtered user's name from UI
       *
       * @param {e} Event
       * @param {args} Object Event arguments
       */
      onRemoveFilter: function RM_Audit_RemoveFilter(e, args)
      {
         Dom.removeClass(Sel.query('.personFilter',this.id)[0], 'active');
         this._changeFilterText('');
         YAHOO.Bubbling.fire('PersonFilterDeactivated',{person:null});
      },

      /**
       * Clears logs via ajax call and gives user feedback
       *
       */
      _clearLog: function RM_Audit_clearLog()
      {
         var me = this;
         Alfresco.util.PopupManager.displayMessage({
            text: this.msg('label.clearing-log-message'),
            spanClass: 'message',
            modal: true,
            noEscape: true,
            displayTime: 1
         });
         Alfresco.util.Ajax.jsonDelete(
         {
            url: Alfresco.constants.PROXY_URI + "api/rma/admin/rmauditlog",
            successCallback:
            {
               fn: function(serverResponse)
               {
                  // apply current property values to form
                  if (serverResponse.json)
                  {
                     var data = serverResponse.json.data;
                     this.options.enabled = data.enabled;
                      Alfresco.util.PopupManager.displayMessage({
                        text: this.msg('label.cleared-log-message'),
                        spanClass: 'message',
                        modal: true,
                        noEscape: true,
                        displayTime: 1
                     });
                     this._query();
                  }
               },
               scope: this
            },
            failureMessage: me.msg("message.clear-log-fail")
         });
      },

      /**
       * toggles logs via ajax call and gives user feedback
       */
      _toggleLog: function RM_Audit_toggleLog()
      {
         var me = this;
         Alfresco.util.PopupManager.displayMessage({
            text: (this.options.enabled) ? this.msg('label.stopping-log-message'): this.msg('label.starting-log-message'),
            spanClass: 'message',
            modal: true,
            noEscape: true,
            displayTime: 1
         });

         Alfresco.util.Ajax.jsonPut(
         {
            url: Alfresco.constants.PROXY_URI + "api/rma/admin/rmauditlog",
            dataObj:
            {
               enabled:!this.options.enabled
            },
            successCallback:
            {
               fn: function(serverResponse)
               {
                  // apply current property values to form
                  if (serverResponse.json)
                  {
                     var data = serverResponse.json.data;
                     this.options.enabled = data.enabled;
                     me.toggleUI();
                     Alfresco.util.PopupManager.displayMessage({
                        text: (this.options.enabled) ? this.msg('label.started-log-message'): this.msg('label.stopped-log-message'),
                        spanClass: 'message',
                        modal: true,
                        noEscape: true,
                        displayTime: 1
                     });
                  }
               },
               scope: this
            },
            failureMessage: me.msg((this.options.enabled) ? "message.stop-log-fail" : "message.start-log-fail")
         });

      },

      onPersonFilterActivated: function RM_Audit_personFilterActivated(e,args)
      {
         this.activePerson = args[1].person;
         this.queryParams.user = this.activePerson.userName;
      },

      onPersonFilterDeactivated: function RM_Audit_personFilterDeactivated(e,args)
      {
         this.activePerson = "";
         delete this.queryParams.user;
      },

      /**
       * Bubbling event handler called when a value from the property selection menu has been picked
       */
      onPropertyMenuSelected: function RM_Audit_onPropertyMenuSelected(e, args)
      {
         var item = args[1];

         if (item.value !== "ALL")
         {
            this.queryParams.property = item.value;
         }
         else
         {
            delete this.queryParams.property;
         }
      },

      /**
       * Displays dialog with more info about log entry
       */
      onShowDetails: function RM_Audit__showDetails(e)
      {
         var el = Event.getTarget(e);
         var data = this.widgets.auditDataTable.getRecord(el).getData();
         var me = this;

         if (!this.widgets.auditDialog)
         {
            // Construct the YUI Dialog that will display the message
            this.widgets.auditDialog = new YAHOO.widget.Dialog("auditEntry",
            {
               visible: false,
               close: true,
               draggable: true,
               modal: true,
               fixedcenter:true,
               zIndex: 1000
            });
         }

         var body = '<table id="auditEntryDetails">'+
            '<tr>'+
               '<th>' + this.msg('label.event') + ':</th>'+
               '<td>' + data.event + '</td>'+
            '</tr>'+
            '<tr>'+
               '<th>' + this.msg('label.user') + ':</th>'+
               '<td>' + $html(data.fullName) + '</td>'+
            '</tr>'+
            '<tr>'+
               '<th>' + this.msg('label.timestamp') + ':</th>'+
               '<td>' + $formatDate($fromISO8601(data.timestamp)) + '</td>'+
            '</tr>'+
            '<tr>'+
               '<th>' + this.msg('label.role') + ':</th>'+
               '<td>' + $html(data.userRole) + '</td>'+
            '</tr>'+
         '</table>';

         body+='<div class="details">';

         if (data.path)
         {
            var displayPath = data.path.substring(data.path.indexOf("/Sites"));

            body+='<table id="auditEntry-nodeDetails">'+
               '<tr>'+
                  '<th>' + this.msg('label.identifier') + ':</th>'+
                  '<td>' + $html(data.identifier) + '</td>'+
               '</tr>'+
               '<tr>'+
                  '<th>' + this.msg('label.type') + ':</th>'+
                  '<td>' + $html(data.nodeType) + '</td>'+
               '</tr>'+
               '<tr>'+
                  '<th>' + this.msg('label.location') + ':</th>'+
                  '<td class="audit-link-item"><a href="' + me.getNodeDetailsLink(data) + '">' + $html(displayPath.replace('/Sites', '')) + '</a></td>'+
               '</tr>'+
            '</table>';
         }

         if (data.changedValues.length>0)
         {
            var changedValuesHTML = '';
            var changedValuesHTMLTemplate = '<tr{className}>'+
               '<td>{name}</td>'+
               '<td>{previous}</td>'+
               '<td>{new}</td>'+
            '</tr>';
            for (var i=0,len=data.changedValues.length;i<len;i++)
            {
               var o = data.changedValues[i];
               o.className = (i%2===0) ? '' : ' class="odd"';
               o.previous = o.previous || '<none>';
               changedValuesHTML += YAHOO.lang.substitute(changedValuesHTMLTemplate, o, function(p_key, p_value, p_meta)
               {
                  return $html(p_value);
               });
            }

            body+='<table id="auditEntry-changedValues">'+
               '<thead>'+
                  '<tr>'+
                     '<th>'+ this.msg('label.property') +'</th>'+
                     '<th>'+ this.msg('label.previous-value') +'</th>'+
                     '<th>'+ this.msg('label.new-value') +'</th>'+
                  '</tr>'+
               '</thead>'+
               '<tbody>'+
                     changedValuesHTML+
               '</tbody>'+
            '</table>';
         }

         body +='</div>';
         this.widgets.auditDialog.setHeader(this.msg('label.dialog-title',data.event));
         this.widgets.auditDialog.setBody(body);
         this.widgets.auditDialog.render(document.body);
         this.widgets.auditDialog.center();
         if(YAHOO.env.ua.ie==6)
         {
            Dom.get(Sel.query('#auditEntry .bd')[0]).style.height='25em';
         }
         this.widgets.auditDialog.show();
      },

      _buildQuery : function RM_Audit__buildQuery()
      {
         //default to 20 if none given
         if ((this.options.viewMode==Alfresco.rm.component.RMAudit.VIEW_MODE_DEFAULT) && YAHOO.lang.isUndefined(this.queryParams.size))
         {
            this.queryParams.size=20;
         }
         var qs = [];
         for (var p in this.queryParams)
         {
            qs.push(p+'='+this.queryParams[p]);
         }
         qs = '?'+qs.join('&');
         return qs;
      },

      onApplyFilters : function RM_Audit__applyFilters()
      {
         var entriesValue = Dom.get(this.id+'-entries').value;
         //event,property and user filters set the queryParams using their handlers so only need to manually
         //check if entries or date filters are been used and if so, add to query params.
         var filterMap = [{uiId:this.id+'-entries',queryId:'size'},{uiId:this.id+'-fromDate',queryId:'from'},{uiId:this.id+'-toDate',queryId:'to'}];
         for (var i=0,len = filterMap.length;i<len;i++)
         {
            var mapping = filterMap[i];
            var uiValue = Dom.get(mapping.uiId).value;
            if (uiValue!=="")
            {
               this.queryParams[mapping.queryId] = uiValue;
            }
            else
            {
               delete this.queryParams[mapping.queryId];
            }
         }
         this._query();
      },

      _query : function RM_Audit__reQuery()
      {
         var q = this._buildQuery();
         // Sends a request to the DataSource for more data
         var oCallback = {
            success : this.widgets['auditDataTable'].onDataReturnInitializeTable,
            failure : this.widgets['auditDataTable'].onDataReturnInitializeTable,
            scope : this.widgets['auditDataTable']
         };
         this.widgets['auditDataTable'].getDataSource().sendRequest(q, oCallback);
      },
      /**
       * Updates UI and options metadata
       *
       */
      updateUI : function RM_Audit_updateUI(o)
      {
         var response = o.response;
         this.options.enabled = response.meta.enabled;
         //this.options.startDate = response.meta.startDate;
         //this.options.stopDate = response.meta.stopDate;
         this.toggleUI();
         //update caption
         this.widgets['auditDataTable']._elCaption.innerHTML = this.msg('label.pagination', response.results.length);
      },

      /**
       * Compute the node details link based on site and nodeType
       *
       */
      getNodeDetailsLink: function RM_Audit_getNodeDetailsLink(data) {
         var nodeDetailsLink = Alfresco.constants.URL_PAGECONTEXT;
         var details;

         if (data.nodeType === "Record Folder" || data.nodeType === "Unfiled Record Folder")
         {
            details = "site/rm/rm-record-folder-details?nodeRef=";
         }
         else if (data.nodeType === "Record Category")
         {
            details = "site/rm/rm-record-category-details?nodeRef=";
         }
         else
         {
            var siteId = "rm";
            //example: /Company Home/Sites/siteId/documentLibrary/folderName/nodeName
            if (data.path.indexOf('/Sites/') !== -1)
            {
               var res = data.path.split("/");
               siteId = res[3];
            }

            details = 'site/' + siteId + '/document-details?nodeRef=';
         }
         nodeDetailsLink += details + data.nodeRef;
         return nodeDetailsLink;
      }
   });
})();
