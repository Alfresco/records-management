package org.alfresco.po.rm.fileplan.toolbar;

import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Created by polly on 3/13/14.
 */
public class CreateNewRecordDialog extends SharePage {

    public static final By ELECTRONIC_BUTTON         = By.xpath("//button[text()='Electronic']");
    public static final By NON_ELECTRONIC_BUTTON     = By.xpath("//button[text()='Non-electronic']");
    public static final By CANCEL_FILE_BUTTON             = By.xpath("//button[text()='Cancel']");
    protected static final By NAME_INPUT             = By.
            cssSelector("input[id$='_default-createRecord_prop_cm_name']");
    protected static final By TITLE_INPUT            = By.
            cssSelector("input[id$='_default-createRecord_prop_cm_title']");
    protected static final By PHYSICAL_SIZE_INPUT    = By.
            cssSelector("input[id$='_default-createRecord_prop_rma_physicalSize']");
    protected static final By NUMBER_OF_COPIES_INPUT = By.
            cssSelector("input[id$='_default-createRecord_prop_rma_numberOfCopies']");
    protected static final By STORAGE_LOCATION_INPUT =
            By.cssSelector("input[id$='_default-createRecord_prop_rma_storageLocation']");
    protected static final By SHELF_INPUT            = By.
            cssSelector("input[id$='_default-createRecord_prop_rma_shelf']");
    protected static final By BOX_INPUT              = By.
            cssSelector("input[id$='default-createRecord_prop_rma_box']");
    protected static final By FILE_INPUT             = By.
            cssSelector("input[id$='_default-createRecord_prop_rma_file']");
    protected static final By DESCRIPTION_INPUT      = By.tagName("textarea");
    protected static final By SAVE_BUTTON            = By.
            cssSelector("button[id$='createRecord-form-submit-button']");
    protected static final By CANCEL_BUTTON          = By.
            cssSelector("button[id$='createRecord-form-cancel-button']");
    /**
     * Constructor.
     *
     * @param drone {@link org.alfresco.webdrone.WebDrone}
     */
    public CreateNewRecordDialog(WebDrone drone) {
        super(drone);
    }

    /**
     * @see org.alfresco.po.rm.fileplan.toolbar.Dialog#render(org.alfresco.webdrone.RenderTime)
     */
    @SuppressWarnings("unchecked")
    @Override
    public CreateNewRecordDialog render(RenderTime timer)
    {
        elementRender(timer,
                getVisibleRenderElement(NAME_INPUT),
                getVisibleRenderElement(TITLE_INPUT),
                getVisibleRenderElement(PHYSICAL_SIZE_INPUT),
                getVisibleRenderElement(NUMBER_OF_COPIES_INPUT),
                getVisibleRenderElement(STORAGE_LOCATION_INPUT),
                getVisibleRenderElement(SHELF_INPUT),
                getVisibleRenderElement(BOX_INPUT),
                getVisibleRenderElement(FILE_INPUT),
                getVisibleRenderElement(DESCRIPTION_INPUT));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreateNewRecordDialog render()
    {
        return this.render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreateNewRecordDialog render(final long time)
    {
        return this.render(new RenderTime(time));
    }

    /**
     * Enter name value to name input field.
     *
     * @param name {@link String} Name of new category
     */
    public void enterName(final String name)
    {
        WebDroneUtil.checkMandotaryParam("name", name);

        WebElement nameInput = drone.findAndWait(NAME_INPUT);
        nameInput.clear();
        nameInput.sendKeys(name);
    }

    /**
     * Enter title value to title input field.
     *
     * @param title {@link String} New folder title
     */
    public void enterTitle(final String title)
    {
        WebDroneUtil.checkMandotaryParam("title", title);

        WebElement titleInput = drone.findAndWait(TITLE_INPUT);
        titleInput.clear();
        titleInput.sendKeys(title);
    }

    /**
     * Enter description value to description input field.
     *
     * @param description {@link String} New category/folder description
     */
    public void enterDescription(final String description)
    {
        WebDroneUtil.checkMandotaryParam("description", description);

        WebElement descriptionInput = drone.findAndWait(DESCRIPTION_INPUT);
        descriptionInput.clear();
        descriptionInput.sendKeys(description);
    }

    /**
     * Action that selects the save button.
     *
     * @return {@link org.alfresco.webdrone.HtmlPage} Returns the current page
     */
    public FilePlanPage selectSave()
    {
        WebElement save = drone.findAndWait(SAVE_BUTTON);
        save.click();
        canResume();
        return new FilePlanPage(drone).render();
    }

    /**
     * Action that selects the cancel button.
     *
     * @return {@link HtmlPage} Returns the current page
     */
    public FilePlanPage selectCancel()
    {
        WebElement cancel = drone.findAndWait(CANCEL_BUTTON);
        cancel.click();
        return new FilePlanPage(drone).render();
    }
}
