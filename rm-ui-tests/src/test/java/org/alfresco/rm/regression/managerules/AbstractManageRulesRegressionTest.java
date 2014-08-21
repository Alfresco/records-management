package org.alfresco.rm.regression.managerules;

import static org.alfresco.po.rm.RmCreateRulePage.PROPERTY_VALUE_INPUT;
import static org.alfresco.po.rm.RmFolderRulesWithRules.EDIT_BUTTON;
import static org.alfresco.po.rm.RmFolderRulesWithRules.RULE_ITEMS;

import java.util.List;
import java.util.Map;

import org.alfresco.po.rm.RmActionSelectorEnterpImpl.PerformActions;
import org.alfresco.po.rm.RmCreateRulePage;
import org.alfresco.po.rm.RmFolderRulesPage;
import org.alfresco.po.rm.RmFolderRulesWithRules;
import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.WhenSelectorImpl;
import org.alfresco.rm.common.AbstractRegressionTest;
import org.openqa.selenium.WebElement;

/**
 * Abstract "manage rules" regression test.
 * 
 * @author Roy Wetherall
 * @since 2.2
 */
public abstract class AbstractManageRulesRegressionTest extends AbstractRegressionTest
{    
//    /**
//     * Use the exiting RM site
//     * 
//     * @see org.alfresco.po.rm.common.AbstractRecordsManagementTest#isExisitingRMSiteDeletedOnStartup()
//     */
//    @Override
//    protected boolean isExisitingRMSiteDeletedOnStartup()
//    {
//        return false;
//    }
//    
//    /**
//     * Don't delete the RM site at the end of the test
//     * 
//     * @see org.alfresco.po.rm.common.AbstractRecordsManagementTest#isRMSiteDeletedOnTearDown()
//     */
//    @Override
//    protected boolean isRMSiteDeletedOnTearDown()
//    {
//        return false;
//    }
    
    // TODO move to the page object
    public enum WhenOption
    {
        INBOUND, OUTBOUND, UPDATE
    }
    
    /**
     * Helper to create rule
     * 
     * TODO shouldn't this be on the page object?
     *
     * @param ruleTitle rule title
     * @param ruleAction rule action
     */
    protected void createRule(String ruleTitle, PerformActions ruleAction, WhenOption whenOption)
    {
        createRule(ruleTitle, ruleAction, whenOption, true, true);
    }
    
    /**
     * Helper to create a rule
     * 
     * TODO shouldn't this be on the page object?
     */
    protected void createRule(String ruleTitle, PerformActions ruleAction, WhenOption whenOption, boolean applytosubfolders, boolean isNoRule)
    {
        createRule(ruleTitle, ruleAction, whenOption, applytosubfolders, isNoRule, null);
    }
    
    /**
     * Helper to create a rule
     * 
     * TODO shouldn't this be on the page object?
     */
    protected void createRule(String ruleTitle, PerformActions ruleAction, WhenOption whenOption, boolean applytosubfolders, boolean isNoRule, Map<String, String> properties)
    {
        RmCreateRulePage rulesPage;
        FilePlanPage filePlan = drone.getCurrentPage().render();
        if (isNoRule)
        {
            RmFolderRulesPage manageRulesPage = filePlan.selectManageRules().render();
            rulesPage = manageRulesPage.openCreateRulePage().render();
        }
        else
        {
            RmFolderRulesWithRules manageRulesPage = filePlan.selectManageRulesWithRules().render();
            rulesPage = manageRulesPage.clickNewRuleButton().render();
        }
        rulesPage.fillNameField(ruleTitle);
        WhenSelectorImpl whenSelectorEnter = rulesPage.getWhenOptionObj();

        switch (whenOption)
        {
            case INBOUND:
                whenSelectorEnter.selectInbound();
                break;
            case OUTBOUND:
                whenSelectorEnter.selectOutbound();
                break;
            case UPDATE:
                whenSelectorEnter.selectUpdate();
                break;
        }

        // select the action
        rulesPage.selectRmAction(ruleAction.getValue());
        
        // TODO .. break this out for each action so they can be created easily
        if (ruleAction.equals(PerformActions.SET_PROPERTY_VALUE))
        {
            // get the property values from the map
            String propertyName = properties.get("propertyName");
            String propertyValue = properties.get("propertyValue");
            
            // set property and value on the action
            rulesPage.selectSetProperty(propertyName);            
            WebElement title = drone.find(PROPERTY_VALUE_INPUT);
            title.clear();
            title.sendKeys(propertyValue);            
        }
        
        // apply to sub folders
        if (applytosubfolders)
        {
            rulesPage.selectApplyToSubfolderCheckbox();
        }
        
        rulesPage.clickCreate().render();
        
        List<WebElement> ruleItems = drone.findAndWaitForElements(RULE_ITEMS);
        for (WebElement ruleItem : ruleItems)
        {
            if (ruleItem.getText().contains(ruleTitle))
            {
                ruleItem.click();

                drone.findAndWait(EDIT_BUTTON, MAX_WAIT_TIME);
            }
        }
    }

}
