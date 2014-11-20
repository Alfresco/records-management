package org.alfresco.rm.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.rm.RmCreateSitePage;
import org.alfresco.po.rm.RmCreateSitePage.RMSiteCompliance;
import org.alfresco.po.rm.RmSiteDashBoardPage;
import org.alfresco.po.rm.util.RmPageObjectUtils;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.NewUserPage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.UserProfilePage;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.util.ShareTestProperty;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;

public class AbstractTest
{

    private static Log logger = LogFactory.getLog(AbstractTest.class);
    private static ApplicationContext ctx;
    protected static String password;
    protected static String username;
    protected static String shareUrl;
    protected WebDrone drone;
    private String testName;
    public static final String SLASH = File.separator;
    private static final String SRC_ROOT = System.getProperty("user.dir") + SLASH;
    protected static final String DATA_FOLDER = SRC_ROOT + "testdata" + SLASH;
    
    public AbstractTest()
    {
        super();
    }

    public WebDrone getDrone()
    {
        return drone;
    }

    protected void setupContext() throws Exception
    {
        if(logger.isTraceEnabled())
        {
            logger.trace("Starting test context");
        }
    
        List<String> contextXMLList = new ArrayList<String>();
        contextXMLList.add("share-po-test-context.xml");
        contextXMLList.add("webdrone-context.xml");
        ctx = new ClassPathXmlApplicationContext(contextXMLList.toArray(new String[contextXMLList.size()]));
    
        ShareTestProperty t = (ShareTestProperty) ctx.getBean("shareTestProperties");
        shareUrl = t.getShareUrl();
        username = t.getUsername();
        password = t.getPassword();
    }

    /**
     * Helper to log admin user into dashboard.
     * 
     * @return DashBoardPage page object.
     * @throws Exception if error
     */
    public DashBoardPage loginAs(final String... userInfo) throws Exception
    {
        if(shareUrl == null)
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("null shareUrl");
            }
        }
        return ShareUtil.loginAs(drone, shareUrl, userInfo).render();
    }
    
    /**
     * Helper method to create RM site.  
     * @throws Exception 
     */
    protected void createRMSite(RMSiteCompliance compliance) throws Exception
    {
        getWebDrone();
        drone.navigateTo(shareUrl);
        RmSiteDashBoardPage page = loginToRMSiteDashBoard(username, password).render();
        // Click create site dialog
        RmCreateSitePage createSite = page.getRMNavigation().selectCreateSite().render();
        // Create RM Site
        createSite.createRMSite(compliance).render();
        logout();
        closeWebDrone();
    }

    /**
     * Helper method that logs the current user out of share
     */
    protected void logout()
    {
        ShareUtil.logout(drone);
    }

    public void getWebDrone() throws Exception
    {
        drone = (WebDrone) ctx.getBean("webDrone");
        drone.maximize();
    }

    @AfterClass(alwaysRun = true)
    public void closeWebDrone()
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("Closing web drone");
        }
        // Close the browser
        if (drone != null)
        {
            drone.quit();
            drone = null;
        }
    }

    /**
     * Helper method that logs into share and navigates
     * to rm site dash board.
     *
     * @param userName  user name
     * @param password  password
     */
    protected HtmlPage loginToRMSiteDashBoard(String userName, String password)
    {
        drone.navigateTo(shareUrl);
        LoginPage loginPage = new LoginPage(drone).render();
        loginPage.loginAs(userName, password);
        String url = shareUrl.replace("/share", "/share/page/site/rm/dashboard");
        drone.navigateTo(url);
        return drone.getCurrentPage();
    }

    /**
     * Helper method to generate a name from the class name
     * 
     * @return  {@link String}  random name incorporating class name
     */
    protected String generateNameFromClass()
    {
        return getClass().getSimpleName().replace("_", "-") + RmPageObjectUtils.getRandomString(3);
    }

    /**
     * 
     * @return
     */
    protected String genearateNameFromTest()
    {
        return testName.replace("_", "-") + RmPageObjectUtils.getRandomString(3);
    }

    @BeforeMethod(alwaysRun = true)
    protected void startSession(Method method) throws Exception
    { 
        testName = method.getName(); 
        if(logger.isTraceEnabled())
        {
            logger.trace(String.format("Test run:%s.%s", 
                                        method.getDeclaringClass().getCanonicalName(),
                                        testName));
        }
    }

    /**
     * Helper method that logs into share and navigates
     * to rm site dashboard.
     *
     * @param userName  user name
     * @param password  password
     */
    protected void login(String userName, String password)
    {
        drone.navigateTo(shareUrl);
        LoginPage loginPage = new LoginPage(drone).render();
    
        loginPage.loginAs(userName, password);
        String url = shareUrl.replace("/share", "/share/page/site/rm/dashboard");
        drone.navigateTo(url);
    }

    /**
     * Helper method for
     * creating enterpriseuser
     * 
     * @param uname
     * @return
     * @throws Exception
     */
    public boolean createEnterpriseUserWithAdminGroup(String uname) throws Exception
    {
        DashBoardPage dashBoard = ShareUtil.loginAs(drone, shareUrl, username, password).render();
        UserSearchPage page = dashBoard.getNav().getUsersPage().render();
        NewUserPage newPage = page.selectNewUser().render();
        String userinfo = uname + "@test.com";
        UserSearchPage userCreated = newPage.createEnterpriseUserWithGroup(uname, userinfo, userinfo, userinfo, "password", "ALFRESCO_ADMINISTRATORS").render();
        userCreated.searchFor(userinfo).render();
        boolean userFound = userCreated.hasResults();
        ShareUtil.logout(drone);
        return userFound;
    }
    
    /**
     * Delete the given user from Alfresco.
     * 
     * @param userName - User Name
     */
    public void deleteUser(String userName)
    {
        DashBoardPage dashBoard = ShareUtil.loginAs(drone, shareUrl, username, password).render();
        UserSearchPage userSearchPage = dashBoard.getNav().getUsersPage().render();
        userSearchPage = userSearchPage.searchFor(userName).render();
        UserProfilePage userProfile = userSearchPage.clickOnUser(userName).render();
        userSearchPage = userProfile.deleteUser().render();
    }
    
    public void saveScreenShot(String methodName) throws IOException
    {
        if(StringUtils.isEmpty(methodName))
        {
            throw new IllegalArgumentException("Method Name can't be empty or null.");
        }
        File file = drone.getScreenShot();
        File tmp = new File("target/webdrone-" + methodName + ".png");
        FileUtils.copyFile(file, tmp);
        //Commented OS Screen Shot Since Tests are on Selenium Grid
//        try 
//        {
//            saveOsScreenShot(methodName);
//        } 
//        catch (AWTException e) 
//        {
//            logger.error("Not able to take the OS screen shot: " + e);
//        }
    }
    
    public void savePageSource(String methodName) throws IOException
    {
        String htmlSource = ((WebDroneImpl) drone).getDriver().getPageSource();
        File file = new File("target/webdrone-" + methodName + ".html");
        FileUtils.writeStringToFile(file, htmlSource);
    }
    
    public File getFileFromTestData(String fileName, String fileContents)
    {
        String fileLocation = DATA_FOLDER + fileName;
        return newFile(fileLocation, fileContents);
    }
    
    /**
     * Helper to create a new file, empty or with specified contents if one does
     * not exist. Logs if File already exists
     * 
     * @param filename String Complete path of the file to be created
     * @param contents String Contents for text file
     * @return File
     */
    public static File newFile(String filename, String contents)
    {
        File file = new File(filename);

        try
        {
            if (!file.exists())
            {

                if (!contents.isEmpty())
                {
                    OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8").newEncoder());
                    writer.write(contents);
                    writer.close();
                }
                else
                {
                    file.createNewFile();
                }
            }
            else
            {
                logger.debug("Filename already exists: " + filename);
            }
        }
        catch (IOException ex)
        {
            logger.error("Unable to create sample file", ex);
        }
        return file;
    }
    
}