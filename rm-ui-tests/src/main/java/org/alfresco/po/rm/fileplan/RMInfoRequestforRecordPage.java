package org.alfresco.po.rm.fileplan;

import java.util.List;

import org.alfresco.po.rm.fileplan.toolbar.Dialog;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class RMInfoRequestforRecordPage extends Dialog
{

    protected static final By FORM_ID = By.cssSelector("div.set>fieldset>legend");
    protected static final By SELECT_BUTTON = By.cssSelector("div[id$='request-info_assoc_rmwf_mixedAssignees-cntrl-itemGroupActions'] button");
    protected static final By PROVIDE_TEXT= By.cssSelector("#template_x002e_documentlist_v2_x002e_documentlibrary_x0023_default-request-info_prop_rmwf_requestedInformation");
    protected static final By REQUEST_INFO = By.cssSelector("button[id$='default-request-info-form-submit-button']");
    
    protected RMInfoRequestforRecordPage(WebDrone drone)
    {
        super(drone);
       
    }
    
    
    @Override
    public RMInfoRequestforRecordPage render(RenderTime timer)
    {
        basicRender(timer);
        webElementRender(timer);
        return this;
    }

    public boolean formPresent(String name)
    {

        boolean result = false;
        List<WebElement> formList = drone.findAll(FORM_ID);
        for (WebElement form : formList)
        {
            System.out.println("" + name);

            String title = form.getText().toString();
            System.out.println("" + title);
            if (title.equals(name))
            {

                result = true;
            }
        }
        return result;
    }
    
    public SelectDialog clickSelect(){
        drone.findAndWait(SELECT_BUTTON).click();
        return new SelectDialog(drone);
     }
    
    public void provideInfo(String comment){
        drone.find(PROVIDE_TEXT).sendKeys(comment);
        WebElement requestInfo = drone.findAndWait(REQUEST_INFO);
        requestInfo.click();
        drone.waitUntilVisible(By.cssSelector("div.bd"), "successfully", 10);
        drone.waitUntilNotVisibleWithParitalText(By.cssSelector("div.bd"), "successfully", 10);
        drone.getCurrentPage().render();
    }
    
    
}
