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

package org.alfresco.bm.dataload.rm.site;

import org.alfresco.bm.data.DataCreationState;
import org.alfresco.bm.dataload.RMBaseEventProcessor;
import org.alfresco.bm.dataload.rm.role.RMRole;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.event.selector.EventDataObject;
import org.alfresco.bm.event.selector.EventProcessorResponse;
import org.alfresco.bm.event.selector.EventDataObject.STATUS;
import org.alfresco.bm.site.SiteMemberData;
import org.alfresco.rest.core.RestAPIFactory;
import org.alfresco.rest.rm.community.requests.igCoreAPI.RMUserAPI;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.social.alfresco.connect.exception.AlfrescoException;

import com.mongodb.DBObject;

/**
* Create a RM site member.
*
* @author Silviu Dinuta
* @since 2.6
*/
public class CreateRMSiteMember extends RMBaseEventProcessor
{
    public static final String DEFAULT_EVENT_NAME_RM_SITE_MEMBER_CREATED = "rmSiteMemberCreated";
    public static final String FIELD_USERNAME = "username";
    private String eventNameRMSiteMemberCreated = DEFAULT_EVENT_NAME_RM_SITE_MEMBER_CREATED;
    @Autowired
    private RestAPIFactory restAPIFactory;

    /**
     * Override the {@link #DEFAULT_EVENT_NAME_RM_SITE_MEMBER_CREATED default} event name emitted when a rm site member is created
     */
    public void setEventNameRMSiteMemberCreated(String eventNameRMSiteMemberCreated)
    {
        this.eventNameRMSiteMemberCreated = eventNameRMSiteMemberCreated;
    }
    @Override
    protected EventResult processEvent(Event event) throws Exception
    {
        DBObject dataObj = (DBObject) event.getData();
        String username = (String) dataObj.get(FIELD_USERNAME);

        EventProcessorResponse response = null;

        Event nextEvent = null;
        String msg = null;

        // Check the input
        if (username == null)
        {
            dataObj.put("msg", "Invalid site member request.");
            return new EventResult(dataObj, false);
        }

        // Get the membership data
        SiteMemberData siteMember = siteDataService.getSiteMember(PATH_SNIPPET_RM_SITE_ID, username);
        if (siteMember == null)
        {
            dataObj.put("msg", "Site member is missing: " + username);
            return new EventResult(dataObj, false);
        }
        if (siteMember.getCreationState() != DataCreationState.Scheduled)
        {
            dataObj.put("msg", "Site membership has already been processed: " + siteMember);
            return new EventResult(dataObj, false);
        }

        // Start by marking it as a failure in order to handle all failure paths
        siteDataService.setSiteMemberCreationState(PATH_SNIPPET_RM_SITE_ID, username, DataCreationState.Failed);

        String roleStr = siteMember.getRole();
        RMRole role = RMRole.valueOf(roleStr);

        //TODO replace plain text admin user
        String runAs = "admin";
        try
        {
            RMUserAPI rmUserAPI = restAPIFactory.getRMUserAPI(new UserModel(runAs, runAs));
            rmUserAPI.assignRoleToUser(username, role.toString());
            siteDataService.setSiteMemberCreationState(PATH_SNIPPET_RM_SITE_ID, username, DataCreationState.Created);

            siteMember = siteDataService.getSiteMember(PATH_SNIPPET_RM_SITE_ID, username);
            EventDataObject responseData = new EventDataObject(STATUS.SUCCESS, siteMember);
            response = new EventProcessorResponse("Added RM site member", true, responseData);

            msg = "Created RM site member: \n" + "   Response: " + response;
            nextEvent = new Event(eventNameRMSiteMemberCreated, null);
        }
        catch (AlfrescoException e)
        {
            if (e.getStatusCode().equals(HttpStatus.CONFLICT))
            {
                // Already a member
                siteDataService.setSiteMemberCreationState(PATH_SNIPPET_RM_SITE_ID, username, DataCreationState.Created);

                siteMember = siteDataService.getSiteMember(PATH_SNIPPET_RM_SITE_ID, username);
                EventDataObject responseData = new EventDataObject(STATUS.SUCCESS, siteMember);
                response = new EventProcessorResponse("Added RM site member", true, responseData);

                msg = "Site member already exists on server: \n" + "   Response: " + response;
                nextEvent = new Event(eventNameRMSiteMemberCreated, null);
            }
            else
            {
                // Failure
                throw new RuntimeException("Create RM site member as user: " + runAs + " failed (" + e.getStatusCode() + "): " + siteMember, e);
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(msg);
        }

        EventResult result = new EventResult(msg, nextEvent);
        return result;
    }

}
