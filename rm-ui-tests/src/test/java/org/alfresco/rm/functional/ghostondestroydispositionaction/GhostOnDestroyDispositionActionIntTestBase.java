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
package org.alfresco.rm.functional.ghostondestroydispositionaction;

import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.fileplan.RmCreateDispositionPage;
import org.alfresco.po.rm.fileplan.RmCreateDispositionPage.DispositionAction;
import org.alfresco.po.rm.fileplan.RmEditDispositionSchedulePage;
import org.alfresco.po.rm.fileplan.RmEditDispositionSchedulePage.AfterPeriodOf;
import org.alfresco.rm.common.AbstractIntegrationTest;
import org.testng.Assert;

/**
 * This is the base for the tests for the setting of the ghost on destroy
 * disposition action
 * 
 * @author Mark Hibbins
 * @version 2.2
 */
public abstract class GhostOnDestroyDispositionActionIntTestBase extends AbstractIntegrationTest
{
    private final static String CATEGORY = "category";
    private final static String FOLDER = "folder";

    public void testGhostDestroyDispositionAction(boolean ghost)
    {
        // get file plan root
        FilePlanPage filePlan = FilePlanPage.getFilePlanRoot(rmSiteDashBoard);

        // create new category
        filePlan = createNewCategory(filePlan, CATEGORY, CATEGORY, "");

        // click on category
        filePlan = filePlan.selectCategory(CATEGORY, drone.getDefaultWaitTime()).render();

        // navigate back to file plan
        filePlan = FilePlanPage.getFilePlanRoot(rmSiteDashBoard);

        // create disposition
        filePlan.openDetailsPage(CATEGORY);
        RmCreateDispositionPage createDisposition = filePlan.openCreateDisposition().render();
        RmEditDispositionSchedulePage editDisposition = createDisposition.selectEditDisposition();

        // add the cutoff action
        editDisposition.selectDispositionStep(DispositionAction.CUTOFF);
        editDisposition.selectAfterPeriodOf(AfterPeriodOf.IMMEDIATELY, testName);

        // add the destroy action
        editDisposition.selectDispositionStep(DispositionAction.DESTROY);
        editDisposition = editDisposition.selectAfterPeriodOf(AfterPeriodOf.IMMEDIATELY, testName, ghost);

        // click on done
        editDisposition.clickDoneButton();

        // navigate back to file plan category
        filePlan = FilePlanPage.getFilePlanRoot(rmSiteDashBoard);
        filePlan = filePlan.selectCategory(CATEGORY, drone.getDefaultWaitTime()).render();

        // create record folder
        filePlan = createNewRecordFolder(filePlan, FOLDER, FOLDER, "");

        // click on cut off
        filePlan.cutOffAction(FOLDER);

        // click on destroy
        filePlan = filePlan.destroyAction(FOLDER);

        // ensure that the folder has been removed
        filePlan = FilePlanPage.getFilePlanRoot(rmSiteDashBoard);
        filePlan = filePlan.selectCategory(CATEGORY, drone.getDefaultWaitTime()).render();
        Assert.assertEquals(filePlan.getFiles().size(), ghost ? 1 : 0);
    }
}