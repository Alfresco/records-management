/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2019 Alfresco Software Limited
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
 * RM Util methods
 *
 * @module rm/util/RmUtils
 * @author David Webster
 * @since 2.5.1
 */
define(["dojo/_base/lang"],
   function (lang) {

      // The private container for the functionality and properties of the util
      var util = {

         /**
          * Locate one object inside another object
          *
          * @param object {object} The haystack to search through
          * @param parameters {object} The needle to find
          * @returns {object}
          */
         findObject: function rm_util_RmUtils__findObject(object, keyToFind, valueToFind) {

            // Store the object here if we find it.
            var returnValue = null;

            if (Array.isArray(object))
            {
               object.forEach(function (child, index) {
                  returnValue = util.findObject(child, keyToFind,  valueToFind) || returnValue;
               });
            }
            else if (typeof object === "object")
            {
               // Iterate over the object keys...
               Object.keys(object).forEach(function (key) {

                  if (key === keyToFind && object[key] === valueToFind)
                  {
                     returnValue = object;
                  }
                  else
                  {
                     // Recurse into the object...
                     returnValue = util.findObject(object[key], keyToFind, valueToFind) || returnValue;
                  }
               });
            }

            return returnValue;
         }
      };

      /**
       * The public API for this utility class
       *
       * @alias module:rm/util/util
       */
      return {

         /**
          * Locate one object inside another object
          *
          * @instance
          * @function
          * @param object {object} The haystack to search through
          * @param parameters {object} The needle to find
          * @returns {object}
          */
         findObject: lang.hitch(util, util.findObject)
      };
   });
