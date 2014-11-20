package org.alfresco.po.rm.fileplan;

import java.util.NoSuchElementException;

import org.alfresco.po.rm.fileplan.toolbar.Dialog;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;

public class SelectDialog extends Dialog
{
    private static final By SEARCH_INPUT = By.cssSelector("div.search input.search-input");
    private static final By ADMIN_USER = By.cssSelector(".yui-dt-liner>h3");
    private static final By SEARCH_BUTTON = By.cssSelector("button[id$='rmwf_mixedAssignees-cntrl-picker-searchButton-button']");
    private static final By ADD_BUTTON = By.cssSelector(".addIcon");
    private static final By OK_BUTTON = By.cssSelector("button[id$='rmwf_mixedAssignees-cntrl-ok-button']");

    protected SelectDialog(WebDrone drone)
    {
        super(drone);

    }

    @Override
    public SelectDialog render(RenderTime timer)
    {
        basicRender(timer);
        webElementRender(timer);
        return this;
    }

    public void searchtUser(String user)
    {
        drone.findAndWait(SEARCH_INPUT).sendKeys(user);
        drone.findAndWait(SEARCH_BUTTON).click();
    }

    public boolean verifyIsUser()
    {
        boolean isUser = true;
        try
        {
            drone.findAndWait(ADMIN_USER);
        }
        catch (NoSuchElementException e)
        {
            isUser = false;
        }
        return isUser;

    }

    public void selectUser(String User)
    {
        drone.findAndWait(ADD_BUTTON).click();
        drone.findAndWait(OK_BUTTON).click();
    }
}
