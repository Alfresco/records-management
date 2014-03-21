package org.alfresco.po.rm;

import org.alfresco.po.share.site.contentrule.createrules.CreateRulePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Records management Link To Rule page.
 *
 * @author Polina Lushchinskaya
 * @version 1.1
 */
public class RmLinkToRulePage extends RmFolderRulesWithRules {

    private static final By UNLINK_RULE_BUTTON = By.cssSelector("button[id$='unlink-button-button']");
    private static final By VIEW_RULE_SET_BUTTON = By.cssSelector("button[id$='view-button-button']");
    private static final By CHANGE_BUTTON = By.cssSelector("button[id$='change-button-button']");
    public static final By RULE_ITEMS = By.cssSelector("div[class$='rules-linked']");

    public RmLinkToRulePage(WebDrone drone) {
        super(drone);
    }

    @Override
    public RmLinkToRulePage render(RenderTime timer)
    {
        elementRender(timer,
                getVisibleRenderElement(UNLINK_RULE_BUTTON),
                getVisibleRenderElement(VIEW_RULE_SET_BUTTON),
                getVisibleRenderElement(CHANGE_BUTTON),
                getVisibleRenderElement(RULE_ITEMS));
        return this;
    }

    @Override
    public RmLinkToRulePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @Override
    public RmLinkToRulePage render(final long time)
    {
        return render(new RenderTime(time));
    }
}
