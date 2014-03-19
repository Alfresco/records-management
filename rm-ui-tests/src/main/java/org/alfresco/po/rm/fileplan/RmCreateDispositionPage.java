package org.alfresco.po.rm.fileplan;

import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Created by polly on 3/17/14.
 */
public class RmCreateDispositionPage extends FolderDetailsPage {

    public static By CREATE_DISPOSITION_BUTTON  = By.cssSelector("button[id$='createschedule-button-button']");
    public static By DISPOSITION_SECTION        = By.cssSelector("div[class$='disposition']");
    public static By EDIT_PROPERTIES_BUTTON     = By.cssSelector("button[id$='editproperties-button-button']");
    public static By EDIT_SCHEDULE_BUTTON       = By.cssSelector("button[id$='editschedule-button-button']");
    public static By EDIT_DISPOSITION_SECTION   = By.cssSelector("div[id$='rm-disposition-edit']");
    public static By ADD_STEP_BUTTON            = By.cssSelector("button[id$='createaction-button-button']");

    public static enum DispositionAction{
        ACCESSION(0, By.xpath("//a[text()='Accession']")),
        DESTROY(1,  By.xpath("//a[text()='Destroy']")),
        RETAIN(2,  By.xpath("//a[text()='Retain']")),
        TRANSFER(3,  By.xpath("//a[text()='Transfer']")),
        CUTOFF(4,  By.xpath("//a[text()='Cut off']"));

        private final int numberPosition;
        private final By value;

        DispositionAction(int numberPosition, By value)
        {
            this.numberPosition = numberPosition;
            this.value = value;
        }

        public By getValue()
        {
            return value;
        }
    }

    /**
     * Constructor
     *
     * @param drone
     */
    public RmCreateDispositionPage(WebDrone drone) {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RmCreateDispositionPage render(RenderTime timer)
    {
        elementRender(timer,
                getVisibleRenderElement(DISPOSITION_SECTION),
                getVisibleRenderElement(EDIT_PROPERTIES_BUTTON),
                getVisibleRenderElement(EDIT_SCHEDULE_BUTTON));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public RmCreateDispositionPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public RmCreateDispositionPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    public void click(By locator)
    {
        WebElement element = drone.findAndWait(locator);
        drone.mouseOverOnElement(element);
        element.click();
    }

    public void selectEditDisposition(){
        click(EDIT_SCHEDULE_BUTTON);
        drone.findAndWait(EDIT_DISPOSITION_SECTION);
    }

    public RmEditDispositionSchedulePage selectDispositionStep(DispositionAction step)
    {
        click(ADD_STEP_BUTTON);
        click(step.getValue());
        return new RmEditDispositionSchedulePage(drone).render();
    }


}
