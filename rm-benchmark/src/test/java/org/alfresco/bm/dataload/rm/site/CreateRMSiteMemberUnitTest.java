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
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.MessageFormat;

import org.alfresco.bm.data.DataCreationState;
import org.alfresco.bm.dataload.RMEventConstants;
import org.alfresco.bm.dataload.rm.role.RMRole;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.site.SiteDataService;
import org.alfresco.bm.site.SiteMemberData;
import org.alfresco.rest.core.RestAPIFactory;
import org.alfresco.rest.rm.community.requests.igCoreAPI.RMUserAPI;
import org.alfresco.utility.model.UserModel;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.mongodb.DBObject;

/**
 * Unit test class for CreateRMSiteMember
 *
 * @author Silviu Dinuta
 * @since 2.6
 */
@RunWith(MockitoJUnitRunner.class)
public class CreateRMSiteMemberUnitTest implements RMEventConstants
{
    private static final String CREATED_RM_SITE_MEMBER_MSG = "Created RM site member: \n   Response: ";

    @Mock
    private SiteDataService mockedSiteDataService;

    @Mock
    private RestAPIFactory mockedRestAPIFactory;

    @InjectMocks
    private CreateRMSiteMember createRMSiteMember;

    @Test(expected=IllegalStateException.class)
    public void testWithNullEvent() throws Exception
    {
        createRMSiteMember.processEvent(null, new StopWatch());
    }

    @Test(expected=IllegalStateException.class)
    public void testWithNullData() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        when(mockedEvent.getData()).thenReturn(null);
        createRMSiteMember.processEvent(mockedEvent, new StopWatch());
    }

    @Test
    public void testWithNullUsername() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(CreateRMSiteMember.FIELD_USERNAME)).thenReturn(null);
        when(mockedEvent.getData()).thenReturn(mockedData);
        EventResult result = createRMSiteMember.processEvent(mockedEvent, new StopWatch());
        verify(mockedData, times(1)).put(CreateRMSiteMember.MSG_KEY, CreateRMSiteMember.INVALID_SITE_MEMBER_REQUEST_MSG);
        assertEquals(false, result.isSuccess());
        DBObject data = (DBObject) result.getData();
        assertNotNull(data);
    }

    @Test
    public void testSiteMemberMissing() throws Exception
    {
        String userName = "user1";
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(CreateRMSiteMember.FIELD_USERNAME)).thenReturn(userName);
        when(mockedEvent.getData()).thenReturn(mockedData);
        when(mockedSiteDataService.getSiteMember(PATH_SNIPPET_RM_SITE_ID, userName)).thenReturn(null);

        EventResult result = createRMSiteMember.processEvent(mockedEvent, new StopWatch());
        verify(mockedData, never()).put(CreateRMSiteMember.MSG_KEY, CreateRMSiteMember.INVALID_SITE_MEMBER_REQUEST_MSG);
        verify(mockedData, times(1)).put(CreateRMSiteMember.MSG_KEY, MessageFormat.format(CreateRMSiteMember.SITE_MEMBER_MISSING_MSG_TEMPLATE, userName));
        assertEquals(false, result.isSuccess());
        DBObject data = (DBObject) result.getData();
        assertNotNull(data);
    }

    @Test
    public void testAlreadyProcessedSiteMember() throws Exception
    {
        String userName = "user1";
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(CreateRMSiteMember.FIELD_USERNAME)).thenReturn(userName);
        when(mockedEvent.getData()).thenReturn(mockedData);
        SiteMemberData mockedSiteMemberData = mock(SiteMemberData.class);
        when(mockedSiteMemberData.getCreationState()).thenReturn(DataCreationState.Created);
        when(mockedSiteDataService.getSiteMember(PATH_SNIPPET_RM_SITE_ID, userName)).thenReturn(mockedSiteMemberData);

        EventResult result = createRMSiteMember.processEvent(mockedEvent, new StopWatch());
        verify(mockedData, never()).put(CreateRMSiteMember.MSG_KEY, CreateRMSiteMember.INVALID_SITE_MEMBER_REQUEST_MSG);
        verify(mockedData, never()).put(CreateRMSiteMember.MSG_KEY, MessageFormat.format(CreateRMSiteMember.SITE_MEMBER_MISSING_MSG_TEMPLATE, userName));
        verify(mockedData, times(1)).put(CreateRMSiteMember.MSG_KEY, MessageFormat.format(CreateRMSiteMember.SITE_MEMBER_ALREADY_PROCESSED_MSG_TEMPLATE, mockedSiteMemberData));
        assertEquals(false, result.isSuccess());
        DBObject data = (DBObject) result.getData();
        assertNotNull(data);
    }

    @Test
    public void testCreateSiteMember() throws Exception
    {
        String userName = "user1";
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(CreateRMSiteMember.FIELD_USERNAME)).thenReturn(userName);
        when(mockedEvent.getData()).thenReturn(mockedData);
        SiteMemberData mockedSiteMemberData = mock(SiteMemberData.class);
        when(mockedSiteMemberData.getCreationState()).thenReturn(DataCreationState.Scheduled);
        when(mockedSiteMemberData.getRole()).thenReturn(RMRole.ADMINISTRATOR.name());
        when(mockedSiteDataService.getSiteMember(PATH_SNIPPET_RM_SITE_ID, userName)).thenReturn(mockedSiteMemberData);
        RMUserAPI mockedRMUserAPI = mock(RMUserAPI.class);
        when(mockedRestAPIFactory.getRMUserAPI(any(UserModel.class))).thenReturn(mockedRMUserAPI);

        EventResult result = createRMSiteMember.processEvent(mockedEvent, new StopWatch());

        verify(mockedData, never()).put(CreateRMSiteMember.MSG_KEY, CreateRMSiteMember.INVALID_SITE_MEMBER_REQUEST_MSG);
        verify(mockedData, never()).put(CreateRMSiteMember.MSG_KEY, MessageFormat.format(CreateRMSiteMember.SITE_MEMBER_MISSING_MSG_TEMPLATE, userName));
        verify(mockedData, never()).put(CreateRMSiteMember.MSG_KEY, MessageFormat.format(CreateRMSiteMember.SITE_MEMBER_ALREADY_PROCESSED_MSG_TEMPLATE, mockedSiteMemberData));
        verify(mockedRMUserAPI, times(1)).assignRoleToUser(userName, RMRole.ADMINISTRATOR.toString());
        verify(mockedSiteDataService, times(1)).setSiteMemberCreationState(PATH_SNIPPET_RM_SITE_ID, userName, DataCreationState.Created);
        verify(mockedSiteDataService, times(2)).getSiteMember(PATH_SNIPPET_RM_SITE_ID, userName);
        assertEquals(true, result.isSuccess());
        String data = (String) result.getData();
        assertTrue(data.startsWith(CREATED_RM_SITE_MEMBER_MSG));
        assertEquals(1, result.getNextEvents().size());
        assertEquals(createRMSiteMember.getEventNameRMSiteMemberCreated(), result.getNextEvents().get(0).getName());
    }

    @Test(expected = RuntimeException.class)
    public void testCreateSiteMemberWithExceptionOnRestAPI() throws Exception
    {
        String userName = "user1";
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(CreateRMSiteMember.FIELD_USERNAME)).thenReturn(userName);
        when(mockedEvent.getData()).thenReturn(mockedData);
        SiteMemberData mockedSiteMemberData = mock(SiteMemberData.class);
        when(mockedSiteMemberData.getCreationState()).thenReturn(DataCreationState.Scheduled);
        when(mockedSiteMemberData.getRole()).thenReturn(RMRole.ADMINISTRATOR.name());
        when(mockedSiteDataService.getSiteMember(PATH_SNIPPET_RM_SITE_ID, userName)).thenReturn(mockedSiteMemberData);
        RMUserAPI mockedRMUserAPI = mock(RMUserAPI.class);
        when(mockedRestAPIFactory.getRMUserAPI(any(UserModel.class))).thenReturn(mockedRMUserAPI);

        Mockito.doThrow(new Exception("someError")).when(mockedRMUserAPI).assignRoleToUser(userName, RMRole.ADMINISTRATOR.toString());
        createRMSiteMember.processEvent(mockedEvent, new StopWatch());
    }
}
