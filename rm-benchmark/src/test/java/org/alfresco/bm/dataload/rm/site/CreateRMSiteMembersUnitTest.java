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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.bm.data.DataCreationState;
import org.alfresco.bm.dataload.RMEventConstants;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.site.SiteData;
import org.alfresco.bm.site.SiteDataService;
import org.alfresco.bm.site.SiteMemberData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mongodb.DBObject;

/**
 * Unit test class for CreateRMSiteMembers
 *
 * @author Silviu Dinuta
 * @since 2.6
 */
@RunWith(MockitoJUnitRunner.class)
public class CreateRMSiteMembersUnitTest implements RMEventConstants
{
    @Mock
    private SiteDataService mockedSiteDataService;

    @InjectMocks
    private CreateRMSiteMembers createRMSiteMembers;

    @Test
    public void testNoSiteMembersScheduled() throws Exception
    {
        int batchSize = 100;

        createRMSiteMembers.setBatchSize(batchSize);
        when(mockedSiteDataService.getSiteMembers(PATH_SNIPPET_RM_SITE_ID, DataCreationState.NotScheduled, null, 0, batchSize)).thenReturn(new ArrayList<>());

        EventResult result = createRMSiteMembers.processEvent(null);
        verify(mockedSiteDataService, never()).getSite(PATH_SNIPPET_RM_SITE_ID);
        verify(mockedSiteDataService, never()).setSiteMemberCreationState(eq(PATH_SNIPPET_RM_SITE_ID), any(String.class), eq(DataCreationState.Failed));
        verify(mockedSiteDataService, never()).setSiteMemberCreationState(eq(PATH_SNIPPET_RM_SITE_ID), any(String.class), eq(DataCreationState.Scheduled));

        assertEquals(true, result.isSuccess());
        assertEquals(MessageFormat.format(CreateRMSiteMembers.SCHEDULED_MEMBERS_MSG_TEMPLATE, 0), result.getData());
        List<Event> nextEvents = result.getNextEvents();
        assertEquals(1, nextEvents.size());
        assertEquals(createRMSiteMembers.getEventNameRMSiteMembersCreated(), nextEvents.get(0).getName());
    }

    @Test
    public void testNoSiteIdForScheduledMembers() throws Exception
    {
        int batchSize = 100;

        createRMSiteMembers.setBatchSize(batchSize);
        SiteMemberData mockedSiteMemberData1 = mock(SiteMemberData.class);
        SiteMemberData mockedSiteMemberData2 = mock(SiteMemberData.class);
        when(mockedSiteMemberData1.getSiteId()).thenReturn(PATH_SNIPPET_RM_SITE_ID);
        when(mockedSiteMemberData2.getSiteId()).thenReturn(PATH_SNIPPET_RM_SITE_ID);
        when(mockedSiteDataService.getSiteMembers(PATH_SNIPPET_RM_SITE_ID, DataCreationState.NotScheduled, null, 0, batchSize)).thenReturn(Arrays.asList(mockedSiteMemberData1, mockedSiteMemberData2));
        when(mockedSiteDataService.getSite(PATH_SNIPPET_RM_SITE_ID)).thenReturn(null);

        EventResult result = createRMSiteMembers.processEvent(null);
        verify(mockedSiteDataService, times(2)).getSite(PATH_SNIPPET_RM_SITE_ID);
        verify(mockedSiteDataService, times(2)).setSiteMemberCreationState(eq(PATH_SNIPPET_RM_SITE_ID), any(String.class), eq(DataCreationState.Failed));
        verify(mockedSiteDataService, never()).setSiteMemberCreationState(eq(PATH_SNIPPET_RM_SITE_ID), any(String.class), eq(DataCreationState.Scheduled));

        assertEquals(true, result.isSuccess());
        assertEquals(MessageFormat.format(CreateRMSiteMembers.SCHEDULED_MEMBERS_MSG_TEMPLATE, 0), result.getData());
        List<Event> nextEvents = result.getNextEvents();
        assertEquals(1, nextEvents.size());
        assertEquals(createRMSiteMembers.getEventNameCreateRMSiteMembers(), nextEvents.get(0).getName());
    }

    @Test
    public void testScheduleSiteMembers() throws Exception
    {
        int batchSize = 100;

        createRMSiteMembers.setBatchSize(batchSize);
        SiteMemberData mockedSiteMemberData1 = mock(SiteMemberData.class);
        SiteMemberData mockedSiteMemberData2 = mock(SiteMemberData.class);
        when(mockedSiteMemberData1.getSiteId()).thenReturn(PATH_SNIPPET_RM_SITE_ID);
        when(mockedSiteMemberData2.getSiteId()).thenReturn(PATH_SNIPPET_RM_SITE_ID);
        String username1 = "user1";
        String username2 = "user2";
        when(mockedSiteMemberData1.getUsername()).thenReturn(username1);
        when(mockedSiteMemberData2.getUsername()).thenReturn(username2);
        when(mockedSiteDataService.getSiteMembers(PATH_SNIPPET_RM_SITE_ID, DataCreationState.NotScheduled, null, 0, batchSize)).thenReturn(Arrays.asList(mockedSiteMemberData1, mockedSiteMemberData2));

        SiteData mockedSiteData = mock(SiteData.class);
        when(mockedSiteDataService.getSite(PATH_SNIPPET_RM_SITE_ID)).thenReturn(mockedSiteData);
        EventResult result = createRMSiteMembers.processEvent(null);
        verify(mockedSiteDataService, times(2)).getSite(PATH_SNIPPET_RM_SITE_ID);
        verify(mockedSiteDataService, never()).setSiteMemberCreationState(eq(PATH_SNIPPET_RM_SITE_ID), any(String.class), eq(DataCreationState.Failed));
        verify(mockedSiteDataService, times(2)).setSiteMemberCreationState(eq(PATH_SNIPPET_RM_SITE_ID), any(String.class), eq(DataCreationState.Scheduled));

        assertEquals(true, result.isSuccess());
        assertEquals(MessageFormat.format(CreateRMSiteMembers.SCHEDULED_MEMBERS_MSG_TEMPLATE, 2), result.getData());
        List<Event> nextEvents = result.getNextEvents();
        assertEquals(3, nextEvents.size());

        //event1
        Event event = nextEvents.get(0);
        assertEquals(createRMSiteMembers.getEventNameCreateRMSiteMember(), event.getName());
        DBObject dataObj = (DBObject)event.getData();
        assertNotNull(dataObj);
        assertEquals(username1, (String) dataObj.get(CreateRMSiteMember.FIELD_USERNAME));

        //event2
        event = nextEvents.get(1);
        assertEquals(createRMSiteMembers.getEventNameCreateRMSiteMember(), event.getName());
        dataObj = (DBObject)event.getData();
        assertNotNull(dataObj);
        assertEquals(username2, (String) dataObj.get(CreateRMSiteMember.FIELD_USERNAME));

        //self event
        assertEquals(createRMSiteMembers.getEventNameCreateRMSiteMembers(), nextEvents.get(2).getName());
    }
}
