package org.alfresco.po.rm;

import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;

/**
 * Created by polly on 3/4/14.
 */
public class RmConsoleDefineRolesPage extends RmSitePage {

    private static final By NEW_ROLE = By.cssSelector("#newRole-button");


    /**
     * Constructor.
     *
     * @param drone {@link org.alfresco.webdrone.WebDrone}
     */
    protected RmConsoleDefineRolesPage(WebDrone drone) {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends HtmlPage> T render(RenderTime renderTime) {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends HtmlPage> T render(long l) {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends HtmlPage> T render() {
        return null;
    }
}
