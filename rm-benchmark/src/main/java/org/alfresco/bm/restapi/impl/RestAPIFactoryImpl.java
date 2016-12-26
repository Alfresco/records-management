/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 * #L%
 */
package org.alfresco.bm.restapi.impl;

import static com.jayway.restassured.RestAssured.given;

import static org.alfresco.rest.rm.community.util.ParameterCheck.mandatoryString;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.specification.RequestSpecification;

import org.alfresco.bm.restapi.RestAPIFactory;
import org.alfresco.bm.user.UserData;
import org.alfresco.bm.user.UserDataService;
import org.alfresco.rest.core.RestWrapper;
import org.alfresco.rest.rm.community.requests.igCoreAPI.FilePlanComponentAPI;
import org.alfresco.rest.rm.community.requests.igCoreAPI.RMSiteAPI;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * REST API Factory Implementation
 *
 * @author Tuna Aksoy
 * @since 1.0
 */
public class RestAPIFactoryImpl implements RestAPIFactory
{
    @Autowired
    private RMSiteAPI rmSiteAPI;

    @Autowired
    private FilePlanComponentAPI filePlanComponentAPI;

    @Autowired
    private UserDataService userDataService;

    private String scheme;

    private String host;

    private int port;

    private String rmBasePath;

    /**
     * @return the scheme
     */
    protected String getScheme()
    {
        return this.scheme;
    }

    /**
     * @param scheme the scheme to set
     */
    public void setScheme(String scheme)
    {
        this.scheme = scheme;
    }

    /**
     * @return the host
     */
    protected String getHost()
    {
        return this.host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host)
    {
        this.host = host;
    }

    /**
     * @return the port
     */
    protected int getPort()
    {
        return this.port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port)
    {
        this.port = port;
    }

    /**
     * @return the rmBasePath
     */
    protected String getRmBasePath()
    {
        return this.rmBasePath;
    }

    /**
     * @param rmBasePath the rmBasePath to set
     */
    public void setRmBasePath(String rmBasePath)
    {
        this.rmBasePath = rmBasePath;
    }

    /**
     * @return the userDataService
     */
    public UserDataService getUserDataService()
    {
        return this.userDataService;
    }

    /**
     * @param userDataService the userDataService to set
     */
    public void setUserDataService(UserDataService userDataService)
    {
        this.userDataService = userDataService;
    }

    private ExtendedRestWrapper createExtendedRestWrapper(String username)
    {
        String password = getPassword(username);
        ExtendedRestWrapper extendedRestWrapper = new ExtendedRestWrapper(username, password);
        extendedRestWrapper.authenticateUser(new UserModel(username, password));
        return extendedRestWrapper;
    }

    private String getPassword(String username)
    {
        String password;
        UserData user = userDataService.findUserByUsername(username);

        if (user != null)
        {
            password = user.getPassword();
        }
        else
        {
            throw new RuntimeException("Username not held in local data mirror: " + username);
        }

        return password;
    }

    @Service
    @Scope(value = "prototype")
    private class ExtendedRestWrapper extends RestWrapper
    {
        private final String username;
        private final String password;

        /**
         * @return the username
         */
        public String getUsername()
        {
            return this.username;
        }

        /**
         * @return the password
         */
        public String getPassword()
        {
            return this.password;
        }

        public ExtendedRestWrapper(String username, String password)
        {
            this.username = username;
            this.password = password;
        }

        /**
         * @see org.alfresco.rest.core.RestWrapper#onRequest()
         */
        @Override
        protected RequestSpecification onRequest()
        {
            return given(new RequestSpecBuilder().
                    setBaseUri(getScheme() + "://" + getHost()).
                    setPort(getPort()).
                    setBasePath(getRmBasePath()).
                    build().auth().basic(getUsername(), getPassword()));
        }
    }

    /**
     * @see org.alfresco.bm.restapi.RestAPIFactory#getRMSiteAPI(java.lang.String)
     */
    public RMSiteAPI getRMSiteAPI(String username)
    {
        mandatoryString("username", username);
        rmSiteAPI.useRestClient(createExtendedRestWrapper(username));
        return rmSiteAPI;
    }

    /**
     * @see org.alfresco.bm.restapi.RestAPIFactory#getFilePlanComponentAPI(java.lang.String)
     */
    public FilePlanComponentAPI getFilePlanComponentAPI(String username)
    {
        mandatoryString("username", username);
        filePlanComponentAPI.useRestClient(createExtendedRestWrapper(username));
        return filePlanComponentAPI;
    }
}
