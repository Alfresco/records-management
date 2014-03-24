package org.alfresco.po.rm.functional.ManageRules;

import static org.alfresco.po.rm.RmCreateRulePage.CRITERIAS_SELECT;
import static org.alfresco.po.rm.RmCreateRulePage.SELECT_CRITERIA_DIALOG;
import static org.alfresco.po.rm.RmFolderRulesWithRules.EDIT_BUTTON;
import static org.alfresco.po.rm.RmFolderRulesWithRules.RULE_ITEMS;
import static org.testng.AssertJUnit.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.rm.RmActionSelectorEnterpImpl;
import org.alfresco.po.rm.RmConsoleUsersAndGroups;
import org.alfresco.po.rm.RmCreateRulePage;
import org.alfresco.po.rm.RmCreateRulePage.RuleCriterias;
import org.alfresco.po.rm.RmCreateRulePage.WhenExecute;
import org.alfresco.po.rm.RmFolderRulesPage;
import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.fileplan.RmCreateDispositionPage;
import org.alfresco.po.rm.fileplan.RmCreateDispositionPage.DispositionAction;
import org.alfresco.po.rm.fileplan.RmEditDispositionSchedulePage;
import org.alfresco.po.rm.fileplan.RmEditDispositionSchedulePage.AfterPeriodOf;
import org.alfresco.po.rm.functional.RmAbstractTest;
import org.alfresco.po.rm.util.RmPageObjectUtils;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.WhenSelectorImpl;
import org.alfresco.po.share.util.FailedTestListener;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Records management edit disposition page.
 *
 * @author Polina Lushchinskaya
 * @version 1.1
 * @since 2.2
 */

/**
 * FIXME: Please move this class to the regression subpackage
 */

@Listeners(FailedTestListener.class)
public class CriteriasTests1 extends RmAbstractTest
{
    /**
     * Executed after class
     */
    @Override
    @AfterClass(groups={"RM","nonCloud"})
    public void doTeardown()
    {
        ShareUtil.logout(drone);
        login();
        deleteRMSite();
    }

    @Test
    public void RMA_1221()
    {
        String testName = Thread.currentThread().getStackTrace()[1].getMethodName().replace("_", "-");
        String userName = testName + RmPageObjectUtils.getRandomString(3);

        try
        {
            ShareUtil.logout(drone);

            CreateUser(userName);
            login();
            assignUserToRole(drone, userName, RmConsoleUsersAndGroups.SystemRoles.RECORDS_MANAGEMENT_ADMINISTRATOR.getValue());

            ShareUtil.logout(drone);

            //login as user
            login(drone, userName, DEFAULT_USER_PASSWORD);
            OpenRmSite();
            rmSiteDashBoard.selectFilePlan();
            RmCreateRulePage rulesPage;
            FilePlanPage filePlan = drone.getCurrentPage().render();
            RmFolderRulesPage manageRulesPage = filePlan.selectManageRules().render();
            rulesPage = manageRulesPage.openCreateRulePage().render();

            List<String> suggestions = new ArrayList<String>();
            int suggestionToSelect = 0;
            for (RuleCriterias value : RuleCriterias.values())
            {
                for (WebElement listItem : getAllSelectOptions(CRITERIAS_SELECT, MAX_WAIT_TIME))
                {
                    suggestions.add(listItem.getText());
                    if (listItem.getText().equals(value.getValue()))
                    {
                        suggestionToSelect++;
                    }
                }
            }
            org.testng.Assert.assertEquals(suggestionToSelect, RuleCriterias.values().length,
                    "Failed to present all Actions");

            rulesPage.selectCriteriaOption(RuleCriterias.SHOW_MORE.getValue());
            org.testng.Assert.assertTrue(isElementPresent(SELECT_CRITERIA_DIALOG),
                    "Failed to Present Select Criteria Dialog");
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    //TODO Freeze action depricated, so wait until hold action appears
    //RMA-1992:Next - Destroy - Updated
    //@Test
    public void RMA_1992()
    {
        String testName = Thread.currentThread().getStackTrace()[1].getMethodName().replace("_", "-");
        String userName = testName + RmPageObjectUtils.getRandomString(3);
        String ruleName = testName + RmPageObjectUtils.getRandomString(3);
        String categoryName = testName + RmPageObjectUtils.getRandomString(3);
        String folderName = testName + RmPageObjectUtils.getRandomString(3);

        try
        {
            ShareUtil.logout(drone);

            CreateUser(userName);
            login();
            assignUserToRole(drone, userName, RmConsoleUsersAndGroups.SystemRoles.RECORDS_MANAGEMENT_ADMINISTRATOR.getValue());

            ShareUtil.logout(drone);

            //login as user
            login(drone, userName, DEFAULT_USER_PASSWORD);
            OpenRmSite();
            FilePlanPage filePlan = (FilePlanPage) rmSiteDashBoard.selectFilePlan();
            //Any disposition schedule for Category1 applied for folder is created, e.g. CutOff immediately, Accession immediately, Transfer immediately, Destroy immediately,
            filePlan.createCategory(categoryName, true);
            filePlan.openDetailsPage(categoryName);
            RmCreateDispositionPage createDisposition = filePlan.openCreateDisposition().render();
            RmEditDispositionSchedulePage editDisposition = createDisposition.selectEditDisposition();
            editDisposition.selectDispositionStep(DispositionAction.CUTOFF);
            editDisposition.selectAfterPeriodOf(AfterPeriodOf.IMMEDIATELY, testName);
            editDisposition.selectDispositionStep(DispositionAction.ACCESSION);
            editDisposition.selectAfterPeriodOf(AfterPeriodOf.IMMEDIATELY, testName);
            editDisposition.selectDispositionStep(DispositionAction.TRANSFER);
            editDisposition.selectAfterPeriodOf(AfterPeriodOf.IMMEDIATELY, testName);
            editDisposition.selectDispositionStep(DispositionAction.DESTROY);
            editDisposition.selectAfterPeriodOf(AfterPeriodOf.IMMEDIATELY, testName);
            createDisposition = editDisposition.clickDoneButton();

            OpenRmSite();
            rmSiteDashBoard.selectFilePlan();
            filePlan.navigateToFolder(categoryName);

            //Create an updated rule for Category1 with Has Disposition Action criteria: Next - Destroy, action: Close Record Folder
            RmFolderRulesPage manageRulesPage = filePlan.selectManageRules().render();
            RmCreateRulePage rulesPage = manageRulesPage.openCreateRulePage().render();
            rulesPage.fillNameField(ruleName);
            RmActionSelectorEnterpImpl actionSelectorEnter = rulesPage.getActionOptionsObj();
            WhenSelectorImpl whenSelectorEnter = rulesPage.getWhenOptionObj();
            whenSelectorEnter.selectUpdate();
            //TODO Set Hold Action
            actionSelectorEnter.selectAction(RmActionSelectorEnterpImpl.PerformActions.CLOSE_RECORD_FOLDER);
            rulesPage.selectApplyToSubfolderCheckbox();
            rulesPage.selectCriteriaOption(RuleCriterias.HAS_DISPOSITION_ACTION.getValue());
            rulesPage.selectWhenDispositionOption(WhenExecute.NEXT.getValue());
            rulesPage.selectDispositionStep(DispositionAction.DESTROY.getValue());
            rulesPage.clickCreate().render();
            List<WebElement> ruleItems = drone.findAndWaitForElements(RULE_ITEMS);
            for (WebElement ruleItem : ruleItems)
            {
                if (ruleItem.getText().contains(ruleName))
                {
                    ruleItem.click();
                    drone.findAndWait(EDIT_BUTTON, MAX_WAIT_TIME);
                }
            }
            //Create Folder1 in Category1
            OpenRmSite();
            rmSiteDashBoard.selectFilePlan();
            filePlan.navigateToFolder(categoryName);
            filePlan.createFolder(folderName);
            //Cutoff Folder
            filePlan.cutOffAction(folderName);
            rmSiteDashBoard.selectFilePlan();
            filePlan.navigateToFolder(categoryName);
            //TODO Verify if record is freeze
            //org.testng.Assert.assertFalse(filePlan.isFolderClosed(drone, folderName), "Folder is closed");

        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    //RMA-1993:Next - Transfer - Outbound(move)
//    @Test
    public void RMA_1993()
    {
        String testName = Thread.currentThread().getStackTrace()[1].getMethodName().replace("_", "-");
        String userName = testName + RmPageObjectUtils.getRandomString(3);
        String ruleName = testName + RmPageObjectUtils.getRandomString(3);
        String categoryName = testName + RmPageObjectUtils.getRandomString(3);
        String categoryName1 = testName + RmPageObjectUtils.getRandomString(3);
        String folderName = testName + RmPageObjectUtils.getRandomString(3);

        try
        {
            ShareUtil.logout(drone);

            CreateUser(userName);
            login();
            assignUserToRole(drone, userName, RmConsoleUsersAndGroups.SystemRoles.RECORDS_MANAGEMENT_ADMINISTRATOR.getValue());

            ShareUtil.logout(drone);

            //login as user
            login(drone, userName, DEFAULT_USER_PASSWORD);
            OpenRmSite();
            FilePlanPage filePlan = rmSiteDashBoard.selectFilePlan().render();
            /*
             * Any Category1> Folder1; Category2 are created
             */
            filePlan.createCategory(categoryName, true);
            filePlan.createCategory(categoryName1, true);
            filePlan.navigateToFolder(categoryName);
            filePlan.createFolder(folderName);
            /* Any disposition schedule for Category1 applied for folder is created, e.g.
             * CutOff after event is completed, Accession after the same event is completed,
             * Transfer after the same event is completed, Destroy immediately
             */
            filePlan = rmSiteDashBoard.selectFilePlan().render();
            filePlan.openDetailsPage(categoryName);
            RmCreateDispositionPage createDisposition = filePlan.openCreateDisposition().render();
            RmEditDispositionSchedulePage editDisposition = createDisposition.selectEditDisposition();
            editDisposition.selectDispositionStep(DispositionAction.CUTOFF);
            editDisposition.selectAfterPeriodOf(AfterPeriodOf.IMMEDIATELY, testName);
            editDisposition.selectDispositionStep(DispositionAction.ACCESSION);
            editDisposition.selectAfterPeriodOf(AfterPeriodOf.IMMEDIATELY, testName);
            editDisposition.selectDispositionStep(DispositionAction.TRANSFER);
            editDisposition.selectAfterPeriodOf(AfterPeriodOf.IMMEDIATELY, testName);
            editDisposition.selectDispositionStep(DispositionAction.DESTROY);
            editDisposition.selectAfterPeriodOf(AfterPeriodOf.IMMEDIATELY, testName);
            createDisposition = editDisposition.clickDoneButton();

            OpenRmSite();
            rmSiteDashBoard.selectFilePlan();
            filePlan.navigateToFolder(categoryName);

            //Create an updated rule for Category1 with Has Disposition Action criteria: Next - Destroy, action: Close Record Folder
            RmFolderRulesPage manageRulesPage = filePlan.selectManageRules().render();
            RmCreateRulePage rulesPage = manageRulesPage.openCreateRulePage().render();
            rulesPage.fillNameField(ruleName);
            RmActionSelectorEnterpImpl actionSelectorEnter = rulesPage.getActionOptionsObj();
            WhenSelectorImpl whenSelectorEnter = rulesPage.getWhenOptionObj();
            whenSelectorEnter.selectUpdate();
            //TODO Set Hold Action
            actionSelectorEnter.selectAction(RmActionSelectorEnterpImpl.PerformActions.CLOSE_RECORD_FOLDER);
            rulesPage.selectApplyToSubfolderCheckbox();
            rulesPage.selectCriteriaOption(RuleCriterias.HAS_DISPOSITION_ACTION.getValue());
            rulesPage.selectWhenDispositionOption(WhenExecute.NEXT.getValue());
            rulesPage.selectDispositionStep(DispositionAction.DESTROY.getValue());
            rulesPage.clickCreate().render();
            List<WebElement> ruleItems = drone.findAndWaitForElements(RULE_ITEMS);
            for (WebElement ruleItem : ruleItems)
            {
                if (ruleItem.getText().contains(ruleName))
                {
                    ruleItem.click();
                    drone.findAndWait(EDIT_BUTTON, MAX_WAIT_TIME);
                }
            }
            //Create Folder1 in Category1
            OpenRmSite();
            rmSiteDashBoard.selectFilePlan();
            filePlan.navigateToFolder(categoryName);
            filePlan.createFolder(folderName);
            //Cutoff Folder
            filePlan.cutOffAction(folderName);
            rmSiteDashBoard.selectFilePlan();
            filePlan.navigateToFolder(categoryName);
            //TODO Verify if record is freeze
            assertFalse(filePlan.isFolderClosed(folderName));

        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }
}
