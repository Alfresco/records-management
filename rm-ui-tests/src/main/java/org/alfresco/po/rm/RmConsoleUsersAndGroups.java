package org.alfresco.po.rm;

import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Created by polly on 3/5/14.
 */
public class RmConsoleUsersAndGroups extends DocumentLibraryPage {

    public static final By ADD_BUTTON = By.xpath("//button[@id='addUser-button']");
    public static final By ADD_USER_FORM = By.cssSelector("div[id$='peoplepicker']");
    public static final By SEARCH_USER_INPUT = By.cssSelector("input[id$='rm-search-peoplefinder-search-text']");
    public static final By SEARCH_USER_BUTTON = By.xpath("//button[contains(@id, 'search-button') and (text()='Search')]");
    private static final By CREATED_ALERT  = By.xpath(".//*[@id='message']/div/span");

    public enum SystemRoles{
        RECORDS_MANAGEMENT_ADMINISTRATOR("Administrator"),
        RECORDS_MANAGEMENT_POWER_USER("PowerUser"),
        RECORDS_MANAGEMENT_RECORDS_MANAGER("RecordsManager"),
        RECORDS_MANAGEMENT_SECURITY_OFFICER("SecurityOfficer"),
        RECORDS_MANAGEMENT_User("User");

        private final String cssSelector;

        SystemRoles(String cssSelector)
        {
            this.cssSelector = cssSelector;
        }

        public String getValue()
        {
            return cssSelector;
        }

    }
    /**
     * Constructor.
     *
     * @param drone {@link org.alfresco.webdrone.WebDrone}
     */
    public RmConsoleUsersAndGroups(WebDrone drone) {
        super(drone);
    }

    /**
     * @see org.alfresco.webdrone.Render#render(org.alfresco.webdrone.RenderTime)
     */
    @SuppressWarnings("unchecked")
    @Override
    public RmConsoleUsersAndGroups render(RenderTime timer)
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
    @SuppressWarnings("unchecked")
    @Override
    public RmConsoleUsersAndGroups render(long time)
    {
        RenderTime timer = new RenderTime(time);
        return render(timer);
    }

    /**
     * @see org.alfresco.webdrone.Render#render()
     */
    @SuppressWarnings("unchecked")
    @Override
    public RmConsoleUsersAndGroups render()
    {
        RenderTime timer = new RenderTime(maxPageLoadingTime);
        return render(timer);
    }

    public static void selectGroup(final WebDrone drone, String groupName){
        WebElement group = drone.findAndWait(By.cssSelector("#role-" + groupName));
        group.click();
    }

    public static boolean isDisplay(final WebDrone drone, By locator)
    {
        try
        {
            return drone.findAndWait(locator, 2000).isDisplayed();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    public static By addUserButton(String userName){
        return By.xpath("//span[contains(text(), '" + userName + "')]/ancestor::tr//button[contains(text(), 'Add')]");
    }

    public void waitUntilCreatedAlert()
    {
        drone.waitUntilElementPresent(CREATED_ALERT, 5);
        drone.waitUntilElementDeletedFromDom(CREATED_ALERT, 5);
    }
}
