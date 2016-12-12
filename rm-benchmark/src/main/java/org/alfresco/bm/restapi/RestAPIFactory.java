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
package org.alfresco.bm.restapi;

import org.alfresco.rest.rm.community.requests.FilePlanComponentAPI;
import org.alfresco.rest.rm.community.requests.RMSiteAPI;

/**
 * Rest API Factory interface
 *
 * @author Tuna Aksoy
 * @since 1.0
 */
public interface RestAPIFactory
{
    /**
     * Gets the {@link RMSiteAPI} as the given user
     *
     * @param username The user name
     * @return The {@link RMSiteAPI}
     */
    public RMSiteAPI getRMSiteAPI(String username);

    /**
     * Gets the {@link FilePlanComponentAPI} as the given user
     *
     * @param username The user name
     * @return The {@link FilePlanComponentAPI}
     */
    public FilePlanComponentAPI getFilePlanComponentAPI(String username);
}