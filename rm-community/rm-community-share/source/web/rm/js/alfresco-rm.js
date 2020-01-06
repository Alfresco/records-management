/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2020 Alfresco Software Limited
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
 * Alfresco RM top-level namespaces.
 */
Alfresco.rm = Alfresco.rm || {};
Alfresco.rm.component = Alfresco.rm.component || {};
Alfresco.rm.module = Alfresco.rm.module || {};
Alfresco.rm.template = Alfresco.rm.component.template || {};
Alfresco.rm.doclib = Alfresco.rm.component.doclib || {};

/**
 * Gets the value for the specified parameter from the URL
 *
 * @method getParamValueFromUrl
 */
Alfresco.rm.getParamValueFromUrl = function(param)
{
   var token,
      result = null,
      hash = window.location.hash,
      params = hash.replace('#', '').split("&");
   for (var i = 0; i < params.length; i++)
   {
      token = params[i].split("=");
      if (token[0] === param)
      {
         result = token[1];
         break;
      }
   }
   return result;
};

/**
 * Header check box click handler for a YUI data table.
 *
 * This functions expects that the check box column
 * is the first column in the table and the table
 * does not allow column dragging.
 *
 * If the header check box is ticked/unticked all check
 * boxes in the same column will be ticked/unticked.
 *
 * @method dataTableHeaderCheckboxClick
 */
Alfresco.rm.dataTableHeaderCheckboxClick = function(oArgs)
{
   var key = this.getColumnSet().headers[0][0];
   if (this.getColumn(oArgs.target).key == key)
   {
      var rs = this.getRecordSet(),
         checked = YAHOO.util.Event.getTarget(oArgs.event).checked;
      for (var i = 0; i < rs.getLength(); i++)
      {
         if (checked !== undefined)
         {
            rs.getRecord(i).setData(key, checked);
            this.getRow(i).cells[0].children[0].firstChild.checked = checked;
         }
      }
      YAHOO.Bubbling.fire("dataTableHeaderCheckboxChange",
      {
         headerCheckBoxChecked: checked
      });
   }
};

/**
 * Cell check box click handler for a YUI data table.
 *
 * This functions expects that the check box column
 * is the first column in the table and the table
 * does not allow column dragging.
 *
 * If a check box in the column is ticked/unticked
 * it will be check if all other check boxes have the
 * same state. If they are all ticked/unticked the
 * header checkbox will also be ticked/unticked.
 *
 * @method dataTableCheckboxClick
 */
Alfresco.rm.dataTableCheckboxClick = function(oArgs)
{
   var key = this.getColumnSet().headers[0][0],
      target = oArgs.target,
      column = this.getColumn(target);
   if (column.key == key)
   {
      var checked = target.checked,
         headerChecked = true,
         atLeastOneChecked = false,
         rs = this.getRecordSet();
      this.getRecord(target).setData(key, checked);
      for (var i = 0; i < rs.getLength(); i++)
      {
         var checkedData = rs.getRecord(i).getData(key);
         if (headerChecked && !checkedData)
         {
            headerChecked = false;
         }
         if (!atLeastOneChecked && checkedData)
         {
            atLeastOneChecked = true;
         }
         if (!headerChecked && atLeastOneChecked)
         {
            break;
         }
      }
      column.getThLinerEl().children[0].firstChild.checked = headerChecked;

      YAHOO.Bubbling.fire("dataTableCheckboxChange",
      {
         checkBoxChecked: checked,
         headerCheckBoxChecked: headerChecked,
         atLeastOneChecked: atLeastOneChecked
      });
   }
};

/**
 * Gets the nodeRefs of selected items in a data table.
 * This helper method is specific for a table with
 * check box column, which has the key 'check'.
 *
 * @method dataTableSelectedItems
 */
Alfresco.rm.dataTableSelectedItems = function(dataTable)
{
   var records = dataTable.getRecordSet().getRecords();
      selectedHolds = [];
   for (var i = 0; i < records.length; i++)
   {
      var record = records[i];
      if (record.getData('check'))
      {
         selectedHolds.push(record.getData('nodeRef'));
      }
   }
   return selectedHolds;
};

/**
 * Checks if the given site is an RM site by checking
 * the preset attribute of the give site object
 *
 * @method isRMPreset
 */
Alfresco.rm.isRMSite = function(site)
{
   var isRMSite = false;
   if (site && site.preset === "rm-site-dashboard")
   {
      isRMSite = true;
   }
   return isRMSite;
};
