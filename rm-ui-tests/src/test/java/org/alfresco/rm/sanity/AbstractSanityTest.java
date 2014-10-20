package org.alfresco.rm.sanity;

import org.alfresco.rm.common.AbstractTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

/**
 * This Abstractclass provides the
 * utility methods for
 * initial test set up
 * and test specific
 * actions
 * 
 * @author Hema Amara
 */
public class AbstractSanityTest extends AbstractTest
{

    /**
     * Helper method to generate a name from the method name
     * 
     * @return {@link String} Method name underscore are replaced with dashes.
     */
    protected String getTestName()
    {
        return Thread.currentThread().getStackTrace()[2].getMethodName().replace("_", "-");
    }

    @BeforeSuite(alwaysRun = true)
    @Parameters({ "contextFileName" })
    public void setupContext() throws Exception
    {
        super.setupContext();
    }

    @BeforeClass(alwaysRun = true)
    public void getWebDrone() throws Exception
    {
        super.getWebDrone();
    }

}
