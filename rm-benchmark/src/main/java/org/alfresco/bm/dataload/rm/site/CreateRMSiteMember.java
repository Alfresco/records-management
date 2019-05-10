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

import java.text.MessageFormat;

import com.mongodb.DBObject;

import org.alfresco.bm.data.DataCreationState;
import org.alfresco.bm.dataload.RMBaseEventProcessor;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.event.selector.EventDataObject;
import org.alfresco.bm.event.selector.EventDataObject.STATUS;
import org.alfresco.bm.event.selector.EventProcessorResponse;
import org.alfresco.bm.site.SiteMemberData;
import org.alfresco.rest.rm.community.requests.gscore.api.RMUserAPI;
import org.apache.commons.lang3.StringUtils;


/**
* Create a RM site member.
*
* @author Silviu Dinuta
* @since 2.6
*/
public class CreateRMSiteMember extends RMBaseEventProcessor
{
    public static final String CREATED_RM_SITE_MEMBER_MSG_TEMPLATE = "Created RM site member: \n   Response: {0}";
    public static final String SITE_MEMBER_ALREADY_PROCESSED_MSG_TEMPLATE = "Site membership has already been processed: {0}";
    public static final String SITE_MEMBER_MISSING_MSG_TEMPLATE = "Site member is missing: {0}";
    public static final String MSG_KEY = "msg";
    public static final String INVALID_SITE_MEMBER_REQUEST_MSG = "Invalid site member request.";
    public static final String DEFAULT_EVENT_NAME_RM_SITE_MEMBER_CREATED = "rmSiteMemberCreated";
    public static final String FIELD_USERNAME = "username";
    private String eventNameRMSiteMemberCreated = DEFAULT_EVENT_NAME_RM_SITE_MEMBER_CREATED;

    /**
     * Override the {@link #DEFAULT_EVENT_NAME_RM_SITE_MEMBER_CREATED default} event name emitted when a rm site member is created
     */
    public void setEventNameRMSiteMemberCreated(String eventNameRMSiteMemberCreated)
    {
        this.eventNameRMSiteMemberCreated = eventNameRMSiteMemberCreated;
    }

    public String getEventNameRMSiteMemberCreated()
    {
        return eventNameRMSiteMemberCreated;
    }

    @Override
    protected EventResult processEvent(Event event) throws Exception
    {
        if (event == null)
        {
            throw new IllegalStateException("This processor requires an event.");
        }

        DBObject dataObj = (DBObject) event.getData();
        if (dataObj == null)
        {
            throw new IllegalStateException("This processor requires data with field " + FIELD_USERNAME);
        }
        String username = (String) dataObj.get(FIELD_USERNAME);

        EventProcessorResponse response = null;

        Event nextEvent = null;
        String msg = null;

        // Check the input
        if (StringUtils.isBlank(username))
        {
            dataObj.put(MSG_KEY, INVALID_SITE_MEMBER_REQUEST_MSG);
            return new EventResult(dataObj, false);
        }

        // Get the membership data
        SiteMemberData siteMember = siteDataService.getSiteMember(PATH_SNIPPET_RM_SITE_ID, username);
        if (siteMember == null)
        {
            dataObj.put(MSG_KEY, MessageFormat.format(SITE_MEMBER_MISSING_MSG_TEMPLATE, username));
            return new EventResult(dataObj, false);
        }
        if (siteMember.getCreationState() != DataCreationState.Scheduled)
        {
            dataObj.put(MSG_KEY, MessageFormat.format(SITE_MEMBER_ALREADY_PROCESSED_MSG_TEMPLATE, siteMember));
            return new EventResult(dataObj, false);
        }

        // Start by marking it as a failure in order to handle all failure paths
        siteDataService.setSiteMemberCreationState(PATH_SNIPPET_RM_SITE_ID, username, DataCreationState.Failed);
        String roleStr = siteMember.getRole();

        try
        {
            //assign RM roles to new members as admin user
            RMUserAPI rmUserAPI = getRestAPIFactory().getRMUserAPI();
            rmUserAPI.assignRoleToUser(username, roleStr);
            siteDataService.setSiteMemberCreationState(PATH_SNIPPET_RM_SITE_ID, username, DataCreationState.Created);

            siteMember = siteDataService.getSiteMember(PATH_SNIPPET_RM_SITE_ID, username);
            EventDataObject responseData = new EventDataObject(STATUS.SUCCESS, siteMember);
            response = new EventProcessorResponse("Added RM site member", true, responseData);

            msg = MessageFormat.format(CREATED_RM_SITE_MEMBER_MSG_TEMPLATE, response);
            nextEvent = new Event(eventNameRMSiteMemberCreated, null);
        }
        catch (Exception e)
        {
            // Failure
            throw new RuntimeException("Create RM site member as user: admin failed (" + e.getMessage() + "): " + siteMember, e);
        }

        eventProcessorLogger.debug(msg);
        EventResult result = new EventResult(msg, nextEvent);
        return result;
    }
}
