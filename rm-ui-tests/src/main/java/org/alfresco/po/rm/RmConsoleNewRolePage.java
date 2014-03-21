package org.alfresco.po.rm;

import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Records management New Role page.
 *
 * @author Polina Lushchinskaya
 * @version 1.1
 */
public class RmConsoleNewRolePage extends RmSitePage {

    public static enum RoleValue{
        MANAGE_RULES("input[id$='ManageRules']"),
        VIEW_RECORDS("input[id$='ViewRecords']");

        private final String cssSelector;

        RoleValue(String cssSelector)
        {
            this.cssSelector = cssSelector;
        }

        public String getValue()
        {
            return cssSelector;
        }
    }

    public static final By CREATE_BUTTON = By.xpath("//button[.='Create']");
    public static final By ROLE_NAME_INPUT = By.cssSelector("#roleName");

    /**
     * Constructor.
     *
     * @param drone {@link org.alfresco.webdrone.WebDrone}
     */
    public RmConsoleNewRolePage(WebDrone drone) {
        super(drone);
    }

    /**
     * @see org.alfresco.webdrone.Render#render(org.alfresco.webdrone.RenderTime)
     */
    @Override
    public RmConsoleNewRolePage render(RenderTime timer)
    {
        WebDroneUtil.checkMandotaryParam("timer", timer);

        while (true)
        {
            timer.start();
            try
            {
                // if search body is found we are rendered
                By rmConsole = By.cssSelector("div[id$='_rm-console']");
                WebElement rmConsoleElement = drone.find(rmConsole);
                if (rmConsoleElement.isDisplayed())
                {
                    break;
                }
            }
            catch (NoSuchElementException e)
            {
            }
            finally
            {
                timer.end();
            }
        }
        return this;
    }

    /**
     * @see org.alfresco.webdrone.Render#render(long)
     */

    @Override
    public RmConsoleNewRolePage render(long time)
    {
        RenderTime timer = new RenderTime(time);
        return render(timer);
    }

    /**
     * @see org.alfresco.webdrone.Render#render()
     */

    @Override
    public RmConsoleNewRolePage render()
    {
        RenderTime timer = new RenderTime(maxPageLoadingTime);
        return render(timer);
    }

    public static void checkRole(WebDrone drone, String value)
    {
        try
        {
            WebElement selectRole = drone.find(By.cssSelector(value));
            if(!selectRole.isSelected())
            {
                selectRole.click();
            }
        }
        catch (NoSuchElementException te)
        {
            te.getStackTrace();
        }
    }
}
