package org.alfresco.po.rm;

import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import static org.alfresco.po.rm.RmFolderRulesPage.*;

import java.util.List;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Records management Rules Page with existing Rules.
 *
 * @author Polina Lushchinskaya
 * @version 1.1
 */
public class RmFolderRulesWithRules extends FolderRulesPage {

    public static final By EDIT_BUTTON              = By.cssSelector("button[id*='edit-button-button']");
    public static final By DELETE_BUTTON            = By.cssSelector("button[id*='delete-button-button']");
    public static final By NEW_RULE_BUTTON          = By.cssSelector("button[id*='default-newRule-button-button']");
    public static final By RULE_DETAILS_BLOCK       = By.cssSelector("div[id*='default-body']>div[id*='rule-details']");
    public static final By RUN_RULES_BUTTON         = By.cssSelector("button[id$='default-runRules-menu-button']");
    public static final By RUN_RULES_FOR_FOLDER     = By.xpath("//a[text()='Run rules for this folder']");
    public static final By RUN_RULES_FOR_SUBFOLDER  = By.xpath("//a[text() = 'Run rules for this folder and its subfolders']");
    public static final By RULE_ITEMS               = By
            .cssSelector("ul[class*='rules-list-container']>li[class*='rules-list-item']");

    private static final By ALERT_DELETE_BLOCK      = By.cssSelector("div[id='prompt']");
    //Delete and Cancel button has same css.
    private static final By ALERT_DELETE_OK         = By.xpath("//button[text()='Delete']");
    private static final By SAVE_BUTTON             = By.xpath("//button[text()='Save']");


    public RmFolderRulesWithRules(WebDrone drone)
    {
        super(drone);
    }

    @Override
    public RmFolderRulesWithRules render(RenderTime timer)
    {
        elementRender(timer,
                getVisibleRenderElement(TITLE_SELECTOR),
                getVisibleRenderElement(EDIT_BUTTON),
                getVisibleRenderElement(DELETE_BUTTON));
        return this;
    }

    @Override
    public RmFolderRulesWithRules render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @Override
    public RmFolderRulesWithRules render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method verifies is Rules Details Block is displayed
     * @return true/false
     */
    private boolean isRuleDetailsDisplay()
    {
        if (drone.find(RULE_DETAILS_BLOCK).isDisplayed()
                && drone.find(EDIT_BUTTON).isDisplayed()
                && drone.find(DELETE_BUTTON).isDisplayed())
        {
            return true;
        }
        return false;
    }

    /**
     * Delete rule from rules detailes page
     * @param ruleName
     * @return {@link RmFolderRulesPage}
     */
    public RmFolderRulesPage deleteRule(String ruleName)
    {
        List<WebElement> ruleItems = drone.findAndWaitForElements(RULE_ITEMS);
        for (WebElement ruleItem : ruleItems)
        {
            if (ruleItem.getText().contains(ruleName))
            {
                ruleItem.click();
                render(new RenderTime(maxPageLoadingTime));
                click(DELETE_BUTTON);
                drone.findAndWait(ALERT_DELETE_BLOCK).findElement(ALERT_DELETE_OK).click();
                return new RmFolderRulesPage(drone).render();
            }
        }
        throw new PageOperationException("Rule with name:" + ruleName + " not found on Page");
    }

    /**
     * Action click on new rule button
     * @return {@link RmCreateRulePage}
     */
    public RmCreateRulePage clickNewRuleButton()
    {
        click(NEW_RULE_BUTTON);
        return drone.getCurrentPage().render();
    }

    /**
     * Action click on edit rule button
     * @return {@link RmCreateRulePage}
     */
    public RmCreateRulePage clickEditButton()
    {
        click(EDIT_BUTTON);
        return drone.getCurrentPage().render();
    }

    /**
     * Helper method that clicks by element
     *
     * @param locator element By locator
     */
    public void click(By locator)
    {
        WebElement element = drone.findAndWait(locator);
        drone.mouseOverOnElement(element);
        element.click();
    }

    /**
     * Helper method verifies if folder rule page is correct
     * @param folderName
     * @return
     */
    public boolean isPageCorrect(String folderName)
    {
        return (super.isTitleCorrect(folderName) && isRuleDetailsDisplay());
    }

}
