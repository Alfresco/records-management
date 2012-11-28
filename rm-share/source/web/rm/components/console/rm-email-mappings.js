/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * RM Email Mappings component
 * 
 * @namespace Alfresco.rm.component
 * @class Alfresco.rm.component.RMEmailMappings
 */
(function RMEmailMappings()
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
   var $html = Alfresco.util.encodeHTML;

   /**
    * RM EmailMappings componentconstructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.MyDocuments} The new component instance
    * @constructor
    */
   Alfresco.rm.component.RMEmailMappings = function RM_EmailMappings_constructor(htmlId)
   {
      Alfresco.rm.component.RMEmailMappings.superclass.constructor.call(this, "Alfresco.rm.component.RMEmailMappings", htmlId, ["button", "container", "json", "menu"]);
      this.currentValues = {};
      this.dataUri = Alfresco.constants.PROXY_URI + "api/rma/admin/emailmap";
      YAHOO.Bubbling.on("EmailMappingsLoaded", this.onDataLoad, this);
      YAHOO.Bubbling.on("PropertyMenuSelected", this.onPropertyMenuSelected, this);

      return this;
   };

   YAHOO.extend(Alfresco.rm.component.RMEmailMappings, Alfresco.component.Base,
   {
      /**
       * Initialises event listening and custom events
       *
       * @method: initEvents
       */
      initEvents: function RM_EmailMappings_initEvents()
      {
         Event.on(this.id, 'click', this.onInteractionEvent, null, this);
         this.registerEventHandler('click', [
         {
            rule: 'button.delete-mapping',
            o:
            {
               handler :this.onDeleteMapping,
               scope: this
            }
         },
         {
            rule: 'button#add-mapping-button',
            o:
            {
               handler :this.onAddMapping,
               scope: this
            }
         },
         {
            rule: 'button#emailProperty-but-button',
            o:
            {
               handler: function showEmailProp(e)
               {
                  this.widgets['emailProperty-menu'].show();
               },
               scope: this
            }
         }]);

         return this;
      },

      /**
       * Handler for delete mapping button. Removes from datamap and DOM
       *
       * @method onDeleteMapping
       */
      onDeleteMapping: function RM_EmailMappings_onDeleteMapping(e)
      {
         var me = this,
            li = Dom.getAncestorByTagName(Event.getTarget(e), 'li'),
            fromTo = li.id.split('::');

         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg('message.delete.mapping.title'),
            text: this.msg('message.delete.mapping.text', li.textContent),
            buttons: [
            {
               text: this.msg('label.yes'),
               handler: function()
               {
                  Alfresco.util.Ajax.jsonRequest(
                  {
                     method: Alfresco.util.Ajax.DELETE,
                     url: YAHOO.lang.substitute(me.dataUri + "/{from}/{to}",
                     {
                        from: encodeURIComponent(fromTo[0]),
                        to: encodeURIComponent(fromTo[1])
                     }),
                     successCallback:
                     {
                        fn: function delete_mapping(args)
                        {
                           // Clear the existing list and load the result from the request
                           me.widgets['list'].innerHTML = "";
                           YAHOO.Bubbling.fire('EmailMappingsLoaded',
                           {
                              mappings: args.json.data
                           });

                           // Show a display message
                           Alfresco.util.PopupManager.displayMessage(
                           {
                              text: Alfresco.util.message('message.deleted', "Alfresco.rm.component.RMEmailMappings"),
                              spanClass: 'message',
                              modal: true,
                              noEscape: true,
                              displayTime: 1
                           });
                        },
                        scope: this
                     },
                     failureMessage: Alfresco.util.message("message.saveFailure", "Alfresco.rm.component.RMEmailMappings")
                  });

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
               isDefault: true
            }
            ]
         });
      },

      /**
       * Handler for add mapping button
       *
       * @method onAddMapping
       */
      onAddMapping: function RM_EmailMappings_onAddMapping()
      {
         if (this.currentValues['emailProperty'] && this.currentValues['rmProperty'])
         {
            var oMap =
            {
               from: this.currentValues['emailProperty'].value,
               to: this.currentValues['rmProperty'].value
            };

            var me = this;
            Alfresco.util.Ajax.jsonRequest(
            {
               method: Alfresco.util.Ajax.POST,
               url: me.dataUri,
               dataObj: oMap,
               successCallback:
               {
                  fn: function add_mapping(args)
                  {
                     var json = args.json;
                     if (json.success)
                     {
                        // Clear the existing list and load the result from the request
                        me.widgets['list'].innerHTML = "";
                        YAHOO.Bubbling.fire('EmailMappingsLoaded',
                        {
                           mappings: json.data
                        });

                        // Show a display message
                        Alfresco.util.PopupManager.displayMessage(
                        {
                           text: Alfresco.util.message('message.saveSuccess', "Alfresco.rm.component.RMEmailMappings"),
                           spanClass: 'message',
                           modal: true,
                           noEscape: true,
                           displayTime: 1
                        });
                     }
                     else
                     {
                        Alfresco.util.PopupManager.displayPrompt(
                        {
                           title: Alfresco.util.message("message.saveFailure", "Alfresco.rm.component.RMEmailMappings"),
                           text: json.message
                        });
                     }
                  },
                  scope: this
               },
               failureMessage: Alfresco.util.message("message.saveFailure", "Alfresco.rm.component.RMEmailMappings")
            });
         }
      },

      /**
       * Fired by YUI when parent element is available for scripting
       *
       * @method onReady
       */
      onReady: function RM_EmailMappings_onReady()
      {
         this.initEvents();

         var me = this;
         var elements = Sel.query('button', this.id).concat(Sel.query('input[type="submit"]', this.id));

         // Create widget button while reassigning classname to src element (since YUI removes classes).
         // We need the classname so we can identify what action to take when it is interacted with (event delegation).
         for (var i=0, len = elements.length; i<len; i++)
         {
            var el = elements[i];
            if (el.id.indexOf('-button') === -1 && el.className.indexOf('button-menu') === -1)
            {
               var id = el.id.replace(this.id + '-', '');
               this.widgets[id] = new YAHOO.widget.Button(el.id);
               this.widgets[id]._button.className = el.className;
            }
         }

         this.widgets['list'] = Sel.query('#emailMappings-list ul')[0];

         // textfields
         this.widgets['emailProperty-text'] = Dom.get('emailProperty-text');
         this.widgets['emailProperty-text'].value = "";

         // enable add button if textfields have entries
         var toggleAddButton = function toggleAddButton()
         {
            var emProp = YAHOO.lang.trim(this.widgets['emailProperty-text'].value);

            // update current value
            this.currentValues['emailProperty'] =
            {
               value: emProp,
               label: emProp
            };

            this.widgets['add-mapping'].set('disabled', (emProp.length === 0));
         };
         Event.addListener(this.widgets['emailProperty-text'], "keyup", toggleAddButton, null, this);

         // create menu
         this.widgets['emailProperty-menu'] = new YAHOO.widget.Menu("emailMappings-emailProperty-menu",
         {
            position:'dynamic',
            context:['emailProperty-text', 'tl', 'bl']
         });

         // Email property menu setup and event handler
         this.widgets['emailProperty-menu'].addItems(this.options.email);
         this.widgets['emailProperty-menu'].render('email-menu-container');
         this.widgets['emailProperty-menu'].subscribe('click', function (e, args)
         {
            var menuItem = args[1];
            var menuItemText = menuItem.cfg.getConfig().text;

            this.widgets['emailProperty-text'].value = menuItemText;
            this.currentValues['emailProperty'] =
            {
               value: menuItemText,
               label: menuItemText
            };
            this.widgets['add-mapping'].set('disabled', menuItem.cfg.getProperty('disabled'));
         }, this, true);

         this.load();
      },

      /**
       * Loads mapping data via AJAX
       *
       * @method load 
       */
      load: function RM_EmailMappings_onDataLoad()
      {
         Alfresco.util.Ajax.jsonRequest(
         {
            method: Alfresco.util.Ajax.GET,
            url: this.dataUri,
            successCallback:
            {
               fn: function(args)
               {
                  YAHOO.Bubbling.fire('EmailMappingsLoaded',
                  {
                     mappings: args.json.data
                  });
               },
               scope: this
            },
            failureMessage: Alfresco.util.message("message.loadFailure", "Alfresco.rm.component.RMEmailMappings")
         });
      },

      /**
       * Handler for when data mapping loads.
       *
       * @method onDataLoad
       * @param e {object} Dom event
       * @param args {object} Event parameters
       */
      onDataLoad : function RM_EmailMappings_onDataLoad(e, args)
      {
         args[1] = args[1].mappings;
         for (var i=0,len = args[1].mappings.length;i<len; i++)
         {
            this.renderMapping(args[1].mappings[i]);
         }
      },

      /**
       * Renders mapping to DOM.
       * 
       * @method renderMapping
       * @param {Object} oMap - Value object of 'from' and 'to' string variables
       */
      renderMapping : function RM_EmailMappings_renderMapping(oMap)
      {
         var newLi = document.createElement('li');
         newLi.id = oMap.from + '::' + oMap.to;
         newLi.innerHTML = YAHOO.lang.substitute("<p>{email} {to} {rm}</p>",
         {
            email: $html(oMap.from),
            to: this.msg('label.to'),
            rm: $html(oMap.to)
         });

         this.widgets['list'].appendChild(newLi);
         var button = new YAHOO.widget.Button(
         {
            id: oMap.from +'-'+ oMap.to + '-button',
            label: this.msg('label.delete'),
            container: newLi.id
         });
         button.on('click', this.onDeleteMapping, this ,true);
         button.appendTo(newLi);
      },

      /**
       * Event handler called when a value from the property selection menu has been selected
       * Updates currently stored values.
       * 
       * @method onPropertyMenuSelected
       * @param e {object} Dom event
       * @param args {object} Event parameters
       */
      onPropertyMenuSelected: function RM_EmailMappings_onPropertyMenuSelected(e, args)
      {
         var item = args[1];

         // set current value from rm property selection menu
         this.currentValues['rmProperty'] =
         {
            value: item.value,
            label: item.label
         };
      }
   });
})();