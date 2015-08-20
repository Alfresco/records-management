/**
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
 * @module alfresco/rm/renderers/ClassificationLabel
 * @extends alfresco/renderers/Banner
 * @author Tuna Aksoy
 * @since 3.0.a
 */
define(["dojo/_base/declare",
        "dojo/dom-class",
        "dojo/_base/lang",
        "alfresco/renderers/Banner"],

function(declare, domClass, lang, Banner)
{
   return declare([Banner],
   {
      /**
       * An array of the CSS files to use with this widget.
       *
       * @instance
       * @type {object[]}
       * @default [{cssFile:"./css/ClassificationLabel.css"}]
       */
      cssRequirements: [{cssFile:"./css/ClassificationLabel.css"}],

      /**
       * The classification level property name
       *
       * @instance
       * @type {string}
       * @default
       */
      classificationLevelPropertyName: "node.properties.clf_currentClassification.label",

      /**
       * The name of property which has the value if the content is classified or not
       *
       * @instance
       * @type {string}
       * @default
       */
      isClassifiedPropertyName: "node.isClassified",

      /**
       * Property to keep the info if the current content is classified or not
       *
       * @instance
       * @type {boolean}
       * @default
       */
      isClassified: false,

      /**
       * Extends the default implementation to set the banner message
       *
       * @instance
       */
      postMixInProperties: function alfresco_rm_renderers_ClassificationLabel__postMixInProperties() {
         this.isClassified = lang.getObject(this.isClassifiedPropertyName, false, this.currentItem);
         if (this.isClassified)
         {
            this.bannerMessage = lang.getObject(this.classificationLevelPropertyName, false, this.currentItem).toUpperCase();
         }
         this.inherited(arguments);
      },

      /**
       * Removes the "hidden" class if there is a message to render
       *
       * @instance
       */
      postCreate: function alfresco_rm_renderers_ClassificationLabel__postCreate()
      {
         if (this.isClassified)
         {
            domClass.add(this.bannerNode, "classification-label");
            domClass.remove(this.bannerNode, "alfresco-renderers-Banner");
            domClass.remove(this.bannerNode, "hidden");
         }
      }
   });
});