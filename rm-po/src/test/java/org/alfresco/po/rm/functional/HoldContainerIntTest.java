/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.po.rm.functional;

import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.fileplan.filter.FilePlanFilter;
import org.alfresco.po.rm.fileplan.filter.hold.HoldsContainer;
import org.alfresco.po.rm.fileplan.toolbar.CreateNewHoldDialog;
import org.alfresco.po.share.util.FailedTestListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * This test suite tests the following new features:
 * <p>
 * <ul>
 *  <li>Creating a new hold in the holds container (also sub hold containers)</li>
 * </ul>
 * <p>
 * @author Tuna Aksoy
 * @version 2.2
 */
@Listeners(FailedTestListener.class)
public class HoldContainerIntTest extends AbstractIntegrationTest
{
    /** Constants for the new hold dialog */
    private static final String NAME = "New Hold";
    private static final String REASON = "Reason for hold";

    @Test
    public void createNewHold()
    {
        FilePlanPage filePlan = rmSiteDashBoard.selectFilePlan().render();
        FilePlanFilter filePlanFilter = filePlan.getFilePlanFilter();
        HoldsContainer holdsContainer = filePlanFilter.selectHoldsContainer().render();
        CreateNewHoldDialog newHoldDialog = holdsContainer.selectCreateNewHold().render();
        newHoldDialog.enterName(NAME);
        newHoldDialog.enterReason(REASON);
        newHoldDialog.tickDeleteHold(true);
        holdsContainer = ((HoldsContainer) newHoldDialog.selectSave()).render(NAME);
    }
}
