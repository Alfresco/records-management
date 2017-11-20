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
import java.util.ArrayList;
import java.util.List;

import org.alfresco.bm.data.DataCreationState;
import org.alfresco.bm.dataload.RMBaseEventProcessor;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.site.SiteData;
import org.alfresco.bm.site.SiteMemberData;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
* Generate 'create RM site member' events for pending creation site members.
*
* @author Silviu Dinuta
* @since 2.6
*/
public class CreateRMSiteMembers extends RMBaseEventProcessor
{
    public static final String SCHEDULED_MEMBERS_MSG_TEMPLATE = "Scheduled {0} RM site member(s) for creation";
    public static final String DEFAULT_EVENT_NAME_RM_SITE_MEMBERS_CREATED = "rmSiteMembersCreated";
    public static final String DEFAULT_EVENT_NAME_CREATE_RM_SITE_MEMBER = "createRMSiteMember";
    public static final String DEFAULT_EVENT_NAME_CREATE_RM_SITE_MEMBERS = "createRMSiteMembers";
    public static final int DEFAULT_BATCH_SIZE = 100;
    public static final long DEFAULT_MEMBER_CREATION_DELAY = 100L;

    private String eventNameRMSiteMembersCreated = DEFAULT_EVENT_NAME_RM_SITE_MEMBERS_CREATED;
    private String eventNameCreateRMSiteMember = DEFAULT_EVENT_NAME_CREATE_RM_SITE_MEMBER;
    private String eventNameCreateRMSiteMembers = DEFAULT_EVENT_NAME_CREATE_RM_SITE_MEMBERS;
    private int batchSize = DEFAULT_BATCH_SIZE;
    private long memberCreationDelay = DEFAULT_MEMBER_CREATION_DELAY;

    /**
     * Override the {@link #DEFAULT_EVENT_NAME_RM_SITE_MEMBERS_CREATED default} event name for completion
     */
    public void setEventNameRMSiteMembersCreated(String eventNameRMSiteMembersCreated)
    {
        this.eventNameRMSiteMembersCreated = eventNameRMSiteMembersCreated;
    }

    public String getEventNameRMSiteMembersCreated()
    {
        return eventNameRMSiteMembersCreated;
    }

    /**
     * Override the {@link #DEFAULT_EVENT_NAME_CREATE_RM_SITE_MEMBER default} event name for creating a RM site member
     */
    public void setEventNameCreateRMSiteMember(String eventNameCreateRMSiteMember)
    {
        this.eventNameCreateRMSiteMember = eventNameCreateRMSiteMember;
    }

    public String getEventNameCreateRMSiteMember()
    {
        return eventNameCreateRMSiteMember;
    }

    /**
     * Override the {@link #DEFAULT_EVENT_NAME_CREATE_RM_SITE_MEMBERS default} event name for rescheduling this event
     */
    public void setEventNameCreateRMSiteMembers(String eventNameCreateRMSiteMembers)
    {
        this.eventNameCreateRMSiteMembers = eventNameCreateRMSiteMembers;
    }

    public String getEventNameCreateRMSiteMembers()
    {
        return eventNameCreateRMSiteMembers;
    }

    /**
     * Override the {@link #DEFAULT_BATCH_SIZE default} batch size for this event before it reschedules itself
     */
    public void setBatchSize(int batchSize)
    {
        this.batchSize = batchSize;
    }

    /**
     * Override the {@link #DEFAULT_MEMBER_CREATION_DELAY default} time between membership creation requests
     */
    public void setMemberCreationDelay(long memberCreationDelay)
    {
        this.memberCreationDelay = memberCreationDelay;
    }

    @Override
    protected EventResult processEvent(Event event) throws Exception
    {
        List<Event> nextEvents = new ArrayList<Event>();

        // Schedule events for each site member to be created
        int numSitesMembers = 0;

        List<SiteMemberData> pendingSiteMembers = siteDataService.getSiteMembers(PATH_SNIPPET_RM_SITE_ID, DataCreationState.NotScheduled, null, 0, batchSize);
        if (pendingSiteMembers.isEmpty())
        {
            // There is nothing more to do
            Event doneEvent = new Event(eventNameRMSiteMembersCreated, System.currentTimeMillis(), null);
            nextEvents.add(doneEvent);
        }
        else
        {
            long nextEventTime = System.currentTimeMillis();
            for (SiteMemberData siteMember : pendingSiteMembers)
            {
                // Do we need to schedule it?
                String siteId = siteMember.getSiteId();
                String username = siteMember.getUsername();
                SiteData site = siteDataService.getSite(siteId);

                // Ignore RM site not been prepared.
                if (site == null)
                {
                    // This site member cannot be created, so we mark it as an immediate failure
                    siteDataService.setSiteMemberCreationState(siteId, username, DataCreationState.Failed);
                    continue;
                }
                // RM Site created
                nextEventTime += memberCreationDelay;

                DBObject dataObj = new BasicDBObject()
                    .append(CreateRMSiteMember.FIELD_USERNAME, username);
                Event nextEvent = new Event(eventNameCreateRMSiteMember, nextEventTime, dataObj);
                nextEvents.add(nextEvent);
                numSitesMembers++;

                // The member creation is now scheduled
                siteDataService.setSiteMemberCreationState(siteId, username, DataCreationState.Scheduled);
            }

            // Reschedule for the next batch (might be zero next time)
            Event self = new Event(eventNameCreateRMSiteMembers, nextEventTime + memberCreationDelay, null);
            nextEvents.add(self);
        }

        // Return messages + next events
        return new EventResult(MessageFormat.format(SCHEDULED_MEMBERS_MSG_TEMPLATE, numSitesMembers), nextEvents);
    }

}
