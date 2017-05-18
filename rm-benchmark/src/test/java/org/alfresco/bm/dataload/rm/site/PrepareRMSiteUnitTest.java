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

import static org.alfresco.bm.data.DataCreationState.Created;
import static org.alfresco.bm.dataload.rm.site.PrepareRMSite.DEFAULT_EVENT_NAME_RM_SITE_PREPARED;
import static org.alfresco.bm.dataload.rm.site.PrepareRMSite.FIELD_ONLY_DB_LOAD;
import static org.alfresco.bm.dataload.rm.site.PrepareRMSite.FIELD_SITE_ID;
import static org.alfresco.bm.dataload.rm.site.PrepareRMSite.FIELD_SITE_MANAGER;
import static org.alfresco.bm.dataload.rm.site.PrepareRMSite.FIELD_SITE_MANAGERS_PASSWORD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.util.Assert.notNull;

import java.util.List;

import com.mongodb.DBObject;

import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.site.SiteData;
import org.alfresco.bm.site.SiteDataService;
import org.alfresco.bm.user.UserData;
import org.alfresco.bm.user.UserDataService;
import org.alfresco.rest.core.RestAPIFactory;
import org.alfresco.rest.core.RestWrapper;
import org.alfresco.rest.requests.People;
import org.alfresco.rest.requests.coreAPI.RestCoreAPI;
import org.alfresco.rest.rm.community.requests.gscore.api.RMSiteAPI;
import org.alfresco.utility.model.UserModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

/**
 * Unit test for prepare RM site event processor
 *
 * @author Tuna Aksoy
 * @since 2.6
 */
@RunWith(MockitoJUnitRunner.class)
public class PrepareRMSiteUnitTest
{
    private static final String RM_SITE_ID = "rm";

    @Mock
    private UserDataService mockedUserDataService;

    @Mock
    private SiteDataService mockedSiteDataService;

    @Mock
    private RestAPIFactory mockedRestAPIFactory;

    @Mock RMSiteAPI mockedRMSiteAPI;

    @Mock
    private RestWrapper mockedRestCoreAPI;

    @InjectMocks
    private PrepareRMSite prepareRMSite;

    @Test
    public void testCreateRMSiteWhenRMSiteDoesNotExistAndNotLoadedInDb() throws Exception
    {
        String username = "bob";
        String password = "secret";

        prepareRMSite.setUsername(username);
        prepareRMSite.setPassword(password);
        when(mockedRestAPIFactory.getRMSiteAPI(any(UserModel.class))).thenReturn(mockedRMSiteAPI);
        when(mockedRMSiteAPI.existsRMSite()).thenReturn(false);

        People mockedPeople = mock(People.class);
        RestCoreAPI mockedCoreAPI = mock(RestCoreAPI.class);
        when(mockedCoreAPI.usingAuthUser()).thenReturn(mockedPeople);
        when(mockedRestCoreAPI.withCoreAPI()).thenReturn(mockedCoreAPI);
        when(mockedRestCoreAPI.getStatusCode()).thenReturn(HttpStatus.OK.toString());

        EventResult result = prepareRMSite.processEvent(null);

        ArgumentCaptor<UserData> userData = forClass(UserData.class);
        verify(mockedUserDataService).createNewUser(userData.capture());

        // Check events
        assertEquals(true, result.isSuccess());
        List<Event> events = result.getNextEvents();
        assertEquals(1, events.size());
        Event event = events.get(0);
        assertEquals(DEFAULT_EVENT_NAME_RM_SITE_PREPARED, event.getName());
        DBObject data = (DBObject) event.getData();
        notNull(data);
        assertEquals(RM_SITE_ID, (String) data.get(FIELD_SITE_ID));
        assertEquals(username, (String) data.get(FIELD_SITE_MANAGER));
        assertEquals(password, (String) data.get(FIELD_SITE_MANAGERS_PASSWORD));
        assertEquals(null, (String) data.get(FIELD_ONLY_DB_LOAD));
    }

    @Test
    public void testRMSiteDoesExistAndNotLoadedInMongoDB() throws Exception
    {
        String username = "bob";
        String password = "secret";

        prepareRMSite.setUsername(username);
        prepareRMSite.setPassword(password);
        when(mockedRestAPIFactory.getRMSiteAPI(any(UserModel.class))).thenReturn(mockedRMSiteAPI);
        when(mockedRMSiteAPI.existsRMSite()).thenReturn(true);

        People mockedPeople = mock(People.class);
        RestCoreAPI mockedCoreAPI = mock(RestCoreAPI.class);
        when(mockedCoreAPI.usingAuthUser()).thenReturn(mockedPeople);
        when(mockedRestCoreAPI.withCoreAPI()).thenReturn(mockedCoreAPI);
        when(mockedRestCoreAPI.getStatusCode()).thenReturn(HttpStatus.OK.toString());

        EventResult result = prepareRMSite.processEvent(null);

        ArgumentCaptor<UserData> userData = forClass(UserData.class);
        verify(mockedUserDataService).createNewUser(userData.capture());

        // Check RM admin user
        assertEquals(Created, userData.getValue().getCreationState());

        // Check events
        assertEquals(true, result.isSuccess());
        List<Event> events = result.getNextEvents();
        assertEquals(1, events.size());
        Event event = events.get(0);
        assertEquals("loadRMSiteIntoDB", event.getName());
        DBObject data = (DBObject) event.getData();
        notNull(data);
        assertEquals(RM_SITE_ID, (String) data.get(FIELD_SITE_ID));
        assertEquals(username, (String) data.get(FIELD_SITE_MANAGER));
        assertEquals(password, (String) data.get(FIELD_SITE_MANAGERS_PASSWORD));
        assertEquals(true, (Boolean) data.get(FIELD_ONLY_DB_LOAD));
    }

    @Test
    public void testRMSiteDoesExistAndLoadedInMongoDB() throws Exception
    {
        String username = "bob";
        String password = "secret";

        prepareRMSite.setUsername(username);
        prepareRMSite.setPassword(password);
        when(mockedRestAPIFactory.getRMSiteAPI(any(UserModel.class))).thenReturn(mockedRMSiteAPI);
        when(mockedRMSiteAPI.existsRMSite()).thenReturn(true);
        SiteData mockedSiteData = mock(SiteData.class);
        when(mockedSiteData.getSiteId()).thenReturn(RM_SITE_ID);
        when(mockedSiteData.getCreationState()).thenReturn(Created);
        when(mockedSiteDataService.getSite(RM_SITE_ID)).thenReturn(mockedSiteData);

        People mockedPeople = mock(People.class);
        RestCoreAPI mockedCoreAPI = mock(RestCoreAPI.class);
        when(mockedCoreAPI.usingAuthUser()).thenReturn(mockedPeople);
        when(mockedRestCoreAPI.withCoreAPI()).thenReturn(mockedCoreAPI);
        when(mockedRestCoreAPI.getStatusCode()).thenReturn(HttpStatus.OK.toString());

        EventResult result = prepareRMSite.processEvent(null);

        ArgumentCaptor<UserData> userData = forClass(UserData.class);
        verify(mockedUserDataService).createNewUser(userData.capture());

        // Check RM admin user
        assertEquals(Created, userData.getValue().getCreationState());

        // Check events
        assertEquals(true, result.isSuccess());
        assertEquals("RM Site already created, continue loading data.", result.getData());
        List<Event> events = result.getNextEvents();
        assertEquals(1, events.size());
        Event event = events.get(0);
        assertEquals("scheduleFilePlanLoaders", event.getName());
        DBObject data = (DBObject) event.getData();
        assertNull(data);
    }

    @Test
    public void testRMSiteDoesNotExistAndLoadedInMongoDB() throws Exception
    {
        String username = "bob";
        String password = "secret";

        prepareRMSite.setUsername(username);
        prepareRMSite.setPassword(password);
        when(mockedRestAPIFactory.getRMSiteAPI(any(UserModel.class))).thenReturn(mockedRMSiteAPI);
        when(mockedRMSiteAPI.existsRMSite()).thenReturn(false);
        SiteData mockedSiteData = mock(SiteData.class);
        when(mockedSiteData.getSiteId()).thenReturn(RM_SITE_ID);
        when(mockedSiteData.getCreationState()).thenReturn(Created);
        when(mockedSiteDataService.getSite(RM_SITE_ID)).thenReturn(mockedSiteData);

        People mockedPeople = mock(People.class);
        RestCoreAPI mockedCoreAPI = mock(RestCoreAPI.class);
        when(mockedCoreAPI.usingAuthUser()).thenReturn(mockedPeople);
        when(mockedRestCoreAPI.withCoreAPI()).thenReturn(mockedCoreAPI);
        when(mockedRestCoreAPI.getStatusCode()).thenReturn(HttpStatus.OK.toString());
        EventResult result = prepareRMSite.processEvent(null);

        ArgumentCaptor<UserData> userData = forClass(UserData.class);
        verify(mockedUserDataService).createNewUser(userData.capture());

        // Check RM admin user
        assertEquals(Created, userData.getValue().getCreationState());

        // Check events
        assertEquals(true, result.isSuccess());
        assertEquals("Preparing Records Management: \n", result.getData());
        List<Event> events = result.getNextEvents();
        assertEquals(1, events.size());
        Event event = events.get(0);
        assertEquals(DEFAULT_EVENT_NAME_RM_SITE_PREPARED, event.getName());
        DBObject data = (DBObject) event.getData();
        notNull(data);
        assertEquals(mockedSiteData.getSiteId(), (String) data.get(FIELD_SITE_ID));
        assertEquals(username, (String) data.get(FIELD_SITE_MANAGER));
        assertEquals(password, (String) data.get(FIELD_SITE_MANAGERS_PASSWORD));
        assertEquals(null, (String) data.get(FIELD_ONLY_DB_LOAD));
    }

    @Test
    public void testRMSiteCreatorDoesNotExist() throws Exception
    {
        String username = "bob";
        String password = "secret";

        prepareRMSite.setUsername(username);
        prepareRMSite.setPassword(password);

        People mockedPeople = mock(People.class);
        RestCoreAPI mockedCoreAPI = mock(RestCoreAPI.class);
        when(mockedCoreAPI.usingAuthUser()).thenReturn(mockedPeople);
        when(mockedRestCoreAPI.withCoreAPI()).thenReturn(mockedCoreAPI);
        when(mockedRestCoreAPI.getStatusCode()).thenReturn(HttpStatus.UNAUTHORIZED.toString());
        EventResult result = prepareRMSite.processEvent(null);
        assertEquals(false, result.isSuccess());
        assertEquals("Provided RM Site Creator does not exist, or provided credentials are not valid.", result.getData());
    }
}
