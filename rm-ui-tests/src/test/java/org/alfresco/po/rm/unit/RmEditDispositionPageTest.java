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
package org.alfresco.po.rm.unit;

import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.fileplan.RmCreateDispositionPage;
import org.alfresco.po.rm.fileplan.RmEditDispositionSchedulePage;
import org.alfresco.po.rm.functional.RmAbstractTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.alfresco.po.rm.fileplan.RmCreateDispositionPage.*;
import static org.alfresco.po.rm.fileplan.RmEditDispositionSchedulePage.*;
import static org.testng.Assert.assertTrue;

/**
 * Unit Tests for Create/Edit Disposition Page
 *
 * @author Polina Lushchinskaya
 * @version 1.1
 * @since 2.2
 */
public class RmEditDispositionPageTest extends RmAbstractTest
{
    /** Constants */
    private static final String NEW_CATEGORY = "New Category";

    @Override
    @BeforeClass(groups={"RM"})
    public void doSetup()
    {
        setup();
        FilePlanPage filePlanPage = (FilePlanPage) rmSiteDashBoard.selectFilePlan();
        filePlanPage.createCategory(NEW_CATEGORY, true);
    }

    @Test
    public void openCategoryDetailsPage(){
        FilePlanPage filePlanPage = new FilePlanPage(drone);
        filePlanPage.openDetailsPage(NEW_CATEGORY);
        assertTrue(isElementPresent(CREATE_DISPOSITION_BUTTON));
    }

    @Test (dependsOnMethods="openCategoryDetailsPage")
    public void createDisposition(){
        FilePlanPage filePlanPage = new FilePlanPage(drone);
        filePlanPage.openCreateDisposition().render();
        assertTrue(isElementPresent(EDIT_PROPERTIES_BUTTON));
        assertTrue(isElementPresent(EDIT_SCHEDULE_BUTTON));
    }

    @Test (dependsOnMethods="createDisposition")
    public void openEditDispositionPage(){
        RmCreateDispositionPage createDisposition = new RmCreateDispositionPage(drone);
        createDisposition.selectEditDisposition();
        assertTrue(isElementPresent(EDIT_DISPOSITION_SECTION));
        assertTrue(isElementPresent(ADD_STEP_BUTTON));
        //FIXME need to verify available steps from select. Now can verify only by text value
    }

    @Test (dependsOnMethods="openEditDispositionPage")
    public void verifyEditDispositionPage(){
        RmEditDispositionSchedulePage editDisposition = new RmEditDispositionSchedulePage(drone);
        editDisposition.selectDispositionStep(DispositionAction.CUTOFF);
        assertTrue(isElementPresent(DISPOSITION_FORM));
        assertTrue(isElementPresent(AFTER_PERIOD_CHKBOX));
        assertTrue(isElementPresent(WHEN_EVENT_OCCURS_CHKBOX));

        assertTrue(isElementPresent(DESCRIPTION_AREA));
        assertTrue(isElementPresent(PERIOD_INPUT));
        assertTrue(isElementPresent(PERIOD_SELECT));

        assertTrue(isElementPresent(PERIOD_ACTION_SELECT));
        assertTrue(isElementPresent(SAVE_BUTTON));
        assertTrue(isElementPresent(CANCEL_BUTTON));
    }
}
