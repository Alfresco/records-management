package org.alfresco.po.rm;

import org.alfresco.po.share.site.RulesPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

public class RmRulesPage extends RulesPage
{

    public RmRulesPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RmRulesPage render(RenderTime timer)
    {
        while(true)
        {
            synchronized (this)
            {
                try{ this.wait(100L); }catch (InterruptedException ie) { }
            }
            try
            {
                if(drone.find(By.cssSelector("div#bd > div[id$='_rule-edit']")).isDisplayed())
                {
                    break;
                }
            }
            catch (NoSuchElementException nse) { }
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public RmRulesPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public RmRulesPage render()
    {
        return render(maxPageLoadingTime);
    }

    public void setFileToPath(String path, long timeout)
    {
        WebElement input = drone.findAndWait(By.className("yui-ac-input"), timeout);
        input.clear();
        input.sendKeys(path);
    }

    public void toggleCreateRecordPath(long timeout)
    {
        WebElement checkBox = drone.findAndWait(By.xpath("//span[@class='menutype_action menuname_fileTo paramtype_d_boolean paramname_createRecordPath']/child::input"), timeout);
        checkBox.click();
    }

    public void selectCreateButton(long timeout)
    {
        drone.findAndWait(By.cssSelector("button[id$='default-create-button-button']"), timeout).click();
    }
}
