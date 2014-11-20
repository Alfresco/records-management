package org.alfresco.po.rm.fileplan.toolbar;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * AuditLogDialog
 * 
 * @author hamara
 */

public class ViewAuditLogDialog extends SharePage
{

    private static final By LOG_CONTAINER = By.cssSelector(".yui-gc");
    private static final By lOG_RECORD = By.cssSelector(".audit-entry");
    private static final By LOG_AUDIT_ENTRIES = By.cssSelector("div.audit-entry-header span");

    /**
     * Constructor.
     *
     * @param drone {@link WebDrone}
     */
    public ViewAuditLogDialog(WebDrone drone)
    {
        super(drone);
    }

    /**
     * @see org.alfresco.webdrone.Render#render(org.alfresco.webdrone.RenderTime)
     */
    @SuppressWarnings("unchecked")
    @Override
    public ViewAuditLogDialog render(RenderTime timer)
    {
        WebDroneUtil.checkMandotaryParam("timer", timer);
        elementRender(timer, getVisibleRenderElement(LOG_CONTAINER), getVisibleRenderElement(lOG_RECORD));

        return this;
    }

    /**
     * @see org.alfresco.webdrone.Render#render()
     */
    @SuppressWarnings("unchecked")
    @Override
    public ViewAuditLogDialog render()
    {
        return this.render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * @see org.alfresco.webdrone.Render#render(long)
     */
    @SuppressWarnings("unchecked")
    @Override
    public ViewAuditLogDialog render(final long time)
    {
        WebDroneUtil.checkMandotaryParam("time", time);

        return this.render(new RenderTime(time));
    }

    /**
     * This Mehtod verifies whehter relevant log element present
     * in the logs container
     * Copy to Log
     * 
     * @return
     */
    public boolean isGetCopyLog()
    {
        boolean result;
        try
        {
            result = false;
            List<WebElement> webElements = drone.findAndWaitForElements(LOG_AUDIT_ENTRIES);
            for (WebElement webElement : webElements)
            {
                if (isCheckCopyElement(webElement))
                {
                    result = true;
                    break;
                }
            }
        }
        catch (TimeoutException e)
        {
            return false;
        }
        return result;
    }

    /**
     * This Mehtod verifies whehter relevant log element present
     * Add to hold
     * in the logs container
     * 
     * @return
     */
    public boolean isAddtoHoldLog()
    {
        boolean result;
        try
        {
            result = false;
            List<WebElement> webElements = drone.findAndWaitForElements(LOG_AUDIT_ENTRIES);
            for (WebElement webElement : webElements)
            {
                if (isAddToHoldElement(webElement))
                {
                    result = true;
                    break;
                }
            }
        }
        catch (TimeoutException e)
        {
            return false;
        }
        return result;
    }

    /**
     * This Mehtod verifies whehter relevant log element present
     * in the logs container
     * Remove from Hold
     * 
     * @return
     */
    public boolean isRemoveFromHoldLog()
    {
        boolean result;
        try
        {
            result = false;
            List<WebElement> webElements = drone.findAndWaitForElements(LOG_AUDIT_ENTRIES);
            for (WebElement webElement : webElements)
            {
                if (isRemoveFromHoldlement(webElement))
                {
                    result = true;
                    break;
                }
            }
        }
        catch (TimeoutException e)
        {
            return false;
        }
        return result;
    }

    /**
     * This method verifies whether the log element
     * contains the relevant message
     * 
     * @param webElement
     * @return
     */
    public boolean isCheckCopyElement(WebElement webElement)
    {
        boolean containsCopyElement = false;
        if (webElement.getText().equals("Copy to"))
        {
            containsCopyElement = true;
        }
        return containsCopyElement;
    }

    /**
     * This method verifies whether the log element
     * contains the relevant message
     * "add to hold"
     * @param webElement
     * @return
     */
    public boolean isAddToHoldElement(WebElement webElement)
    {
        boolean containsHoldElement = false;
        if (webElement.getText().equals("Add to Hold"))
        {
            containsHoldElement = true;
        }
        return containsHoldElement;
    }

    /**
     * This method verifies whether the log element
     * contains the relevant message
     * 
     * @param webElement
     * @return
     */
    public boolean isRemoveFromHoldlement(WebElement webElement)
    {
        boolean containsCopyElement = false;
        if (webElement.getText().equals("Remove from Hold"))
        {
            containsCopyElement = true;
        }
        return containsCopyElement;
    }

    /**
     * This method is to switch to File Plan page from
     * 
     * @throws InterruptedException
     */

    public void switchToRecordInfoPage() throws InterruptedException
    {
        WebDroneUtil.checkMandotaryParam("drone", drone);
        Set<String> windowHandles = drone.getWindowHandles();
        for (String windowHandle : windowHandles)
        {
            drone.switchToWindow(windowHandle);
            if (drone.getTitle().endsWith("File Plan"))
            {
                drone.switchToWindow(windowHandle);
                break;
            }
        }
    }

    /**
     * This method is to switch to Audit log page
     * 
     * @throws InterruptedException
     */

    public void switchToAuditLog() throws InterruptedException
    {
        WebDroneUtil.checkMandotaryParam("drone", drone);
        Set<String> windowHandles = drone.getWindowHandles();
        for (String windowHandle : windowHandles)
        {

            if (drone.getTitle().endsWith("Audit Log"))
            {
                drone.switchToWindow(windowHandle);
                break;
            }
        }
    }

}