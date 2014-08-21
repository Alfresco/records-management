package org.alfresco.rm.po;
///*
// * Copyright (C) 2005-2014 Alfresco Software Limited.
// *
// * This file is part of Alfresco
// *
// * Alfresco is free software: you can redistribute it and/or modify
// * it under the terms of the GNU Lesser General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * Alfresco is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public License
// * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
// */
//package org.alfresco.po.rm.unit;
//
//import static org.alfresco.po.rm.fileplan.RmCreateDispositionPage.CREATE_DISPOSITION_BUTTON;
//import static org.alfresco.po.rm.fileplan.RmCreateDispositionPage.EDIT_PROPERTIES_BUTTON;
//import static org.alfresco.po.rm.fileplan.RmCreateDispositionPage.EDIT_SCHEDULE_BUTTON;
//import static org.alfresco.po.rm.fileplan.RmEditDispositionSchedulePage.ADD_STEP_BUTTON;
//import static org.alfresco.po.rm.fileplan.RmEditDispositionSchedulePage.AFTER_PERIOD_CHKBOX;
//import static org.alfresco.po.rm.fileplan.RmEditDispositionSchedulePage.CANCEL_BUTTON;
//import static org.alfresco.po.rm.fileplan.RmEditDispositionSchedulePage.DESCRIPTION_AREA;
//import static org.alfresco.po.rm.fileplan.RmEditDispositionSchedulePage.DISPOSITION_FORM;
//import static org.alfresco.po.rm.fileplan.RmEditDispositionSchedulePage.EDIT_DISPOSITION_SECTION;
//import static org.alfresco.po.rm.fileplan.RmEditDispositionSchedulePage.PERIOD_ACTION_SELECT;
//import static org.alfresco.po.rm.fileplan.RmEditDispositionSchedulePage.PERIOD_INPUT;
//import static org.alfresco.po.rm.fileplan.RmEditDispositionSchedulePage.PERIOD_SELECT;
//import static org.alfresco.po.rm.fileplan.RmEditDispositionSchedulePage.SAVE_BUTTON;
//import static org.alfresco.po.rm.fileplan.RmEditDispositionSchedulePage.WHEN_EVENT_OCCURS_CHKBOX;
//import static org.testng.Assert.assertTrue;
//
//import org.alfresco.po.rm.fileplan.FilePlanPage;
//import org.alfresco.po.rm.fileplan.RmCreateDispositionPage;
//import org.alfresco.po.rm.fileplan.RmCreateDispositionPage.DispositionAction;
//import org.alfresco.po.rm.fileplan.RmEditDispositionSchedulePage;
//import org.alfresco.po.rm.regression.managerules.v1.RmAbstractTest;
//import org.testng.Assert;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.Test;
//TODO FIXME 
///**
// * Unit Tests for Create/Edit Disposition Page
// *
// * @author Polina Lushchinskaya
// * @version 1.1
// * @since 2.2
// */
//public class RmEditDispositionPageTest extends RmAbstractTest
//{
//    /** Constants */
//    private static final String NEW_CATEGORY = "My New Category";
//
//    @BeforeClass
//    public void navigateToConsole()
//    {
//        FilePlanPage filePlanPage = rmSiteDashBoard.selectFilePlan().render();
//        filePlanPage.createCategory(NEW_CATEGORY, true);
//    }
//
//    @Test
//    public void openCategoryDetailsPage()
//    {
//        FilePlanPage filePlanPage = drone.getCurrentPage().render();
//        filePlanPage.openDetailsPage(NEW_CATEGORY);
//        assertTrue(isElementPresent(CREATE_DISPOSITION_BUTTON));
//    }
//
//    @Test (dependsOnMethods="openCategoryDetailsPage")
//    public void createDisposition(){
//        FilePlanPage filePlanPage = drone.getCurrentPage().render();
//        RmCreateDispositionPage createDisposition = filePlanPage.openCreateDisposition().render();
//        Assert.assertNotNull(createDisposition);
//    }
//
//    @Test (dependsOnMethods="createDisposition")
//    public void openEditDispositionPage(){
//        RmCreateDispositionPage createDisposition = drone.getCurrentPage().render();
//        createDisposition.selectEditDisposition().render();
//        assertTrue(isElementPresent(EDIT_DISPOSITION_SECTION));
//        assertTrue(isElementPresent(ADD_STEP_BUTTON));
//    }
//
//    @Test (dependsOnMethods="openEditDispositionPage")
//    public void verifyEditDispositionPage(){
//        RmEditDispositionSchedulePage editDisposition = new RmEditDispositionSchedulePage(drone);
//        editDisposition.selectDispositionStep(DispositionAction.CUTOFF);
//        assertTrue(isElementPresent(DISPOSITION_FORM));
//        assertTrue(isElementPresent(AFTER_PERIOD_CHKBOX));
//        assertTrue(isElementPresent(WHEN_EVENT_OCCURS_CHKBOX));
//
//        assertTrue(isElementPresent(DESCRIPTION_AREA));
//        assertTrue(isElementPresent(PERIOD_INPUT));
//        assertTrue(isElementPresent(PERIOD_SELECT));
//
//        assertTrue(isElementPresent(PERIOD_ACTION_SELECT));
//        assertTrue(isElementPresent(SAVE_BUTTON));
//        assertTrue(isElementPresent(CANCEL_BUTTON));
//    }
//}
