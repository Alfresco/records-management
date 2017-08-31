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

import static java.util.UUID.randomUUID;

import static org.alfresco.bm.data.DataCreationState.Created;
import static org.alfresco.bm.data.DataCreationState.Failed;
import static org.alfresco.bm.data.DataCreationState.Scheduled;
import static org.alfresco.bm.dataload.rm.role.RMRole.Administrator;
import static org.alfresco.bm.dataload.rm.site.CreateRMSite.DEFAULT_EVENT_NAME_SITE_CREATED;
import static org.alfresco.bm.dataload.rm.site.PrepareRMSite.FIELD_ONLY_DB_LOAD;
import static org.alfresco.bm.dataload.rm.site.PrepareRMSite.FIELD_SITE_ID;
import static org.alfresco.bm.dataload.rm.site.PrepareRMSite.FIELD_SITE_MANAGER_NAME;
import static org.alfresco.bm.dataload.rm.site.PrepareRMSite.FIELD_SITE_MANAGER_PASSWORD;
import static org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponentAlias.FILE_PLAN_ALIAS;
import static org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponentAlias.TRANSFERS_ALIAS;
import static org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponentAlias.UNFILED_RECORDS_CONTAINER_ALIAS;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import com.mongodb.DBObject;

import org.alfresco.bm.cm.FileFolderService;
import org.alfresco.bm.cm.FolderData;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.site.SiteData;
import org.alfresco.bm.site.SiteDataService;
import org.alfresco.bm.site.SiteMemberData;
import org.alfresco.rest.core.RMRestWrapper;
import org.alfresco.rest.core.RestAPIFactory;
import org.alfresco.rest.rm.community.model.fileplan.FilePlan;
import org.alfresco.rest.rm.community.model.site.RMSite;
import org.alfresco.rest.rm.community.model.transfercontainer.TransferContainer;
import org.alfresco.rest.rm.community.model.unfiledcontainer.UnfiledContainer;
import org.alfresco.rest.rm.community.requests.gscore.api.FilePlanAPI;
import org.alfresco.rest.rm.community.requests.gscore.api.RMSiteAPI;
import org.alfresco.rest.rm.community.requests.gscore.api.TransferContainerAPI;
import org.alfresco.rest.rm.community.requests.gscore.api.UnfiledContainerAPI;
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
public class CreateRMSiteUnitTest
{
    @Mock
    private RestAPIFactory mockedRestAPIFactory;

    @Mock
    private SiteDataService mockedSiteDataService;

    @Mock
    private FileFolderService mockedFileFolderService;

    @InjectMocks
    private CreateRMSite createRMSite;

    @Test(expected = IllegalArgumentException.class)
    public void testWithNullEvent() throws Exception
    {
        createRMSite.processEvent(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testWithNullData() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        when(mockedEvent.getData()).thenReturn(null);
        createRMSite.processEvent(mockedEvent);
    }

    @Test
    public void testWithNullSiteId() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_SITE_ID)).thenReturn(null);
        when(mockedEvent.getData()).thenReturn(mockedData);
        EventResult result = createRMSite.processEvent(mockedEvent);
        assertEquals(false, result.isSuccess());
        assertEquals("Requests data not complete for site creation: " + mockedData, result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testWithNullSiteManager() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_SITE_MANAGER_NAME)).thenReturn(null);
        when(mockedEvent.getData()).thenReturn(mockedData);
        EventResult result = createRMSite.processEvent(mockedEvent);
        assertEquals(false, result.isSuccess());
        assertEquals("Requests data not complete for site creation: " + mockedData, result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testWithNullOrEmptyPassword() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_SITE_MANAGER_NAME)).thenReturn("someUserName");
        when(mockedEvent.getData()).thenReturn(mockedData);

        //null password
        when(mockedData.get(FIELD_SITE_MANAGER_PASSWORD)).thenReturn(null);
        EventResult result = createRMSite.processEvent(mockedEvent);
        assertEquals(false, result.isSuccess());
        assertEquals("Requests data not complete for site creation: " + mockedData, result.getData());
        assertEquals(0, result.getNextEvents().size());

        //empty password
        when(mockedData.get(FIELD_SITE_MANAGER_PASSWORD)).thenReturn("");
        result = createRMSite.processEvent(mockedEvent);
        assertEquals(false, result.isSuccess());
        assertEquals("Requests data not complete for site creation: " + mockedData, result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testCreationStateNotCreated() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        SiteData mockedSiteData = mock(SiteData.class);
        String siteId = randomUUID().toString();
        when(mockedData.get(FIELD_SITE_ID)).thenReturn(siteId);
        when(mockedData.get(FIELD_SITE_MANAGER_NAME)).thenReturn(randomUUID().toString());
        when(mockedData.get(FIELD_SITE_MANAGER_PASSWORD)).thenReturn("password");
        when(mockedEvent.getData()).thenReturn(mockedData);
        when(mockedSiteDataService.getSite(siteId)).thenReturn(mockedSiteData);
        when(mockedSiteData.getCreationState()).thenReturn(Failed);
        EventResult result = createRMSite.processEvent(mockedEvent);
        assertEquals(false, result.isSuccess());
        verify(mockedSiteDataService, never()).addSite(any(SiteData.class));

        verify(mockedSiteDataService, never()).addSiteMember(any(SiteMemberData.class));
        assertEquals("Site state has changed: " + mockedSiteData, result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testCreationWhenRMSiteAleadyInDb() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        SiteData mockedSiteData = mock(SiteData.class);
        String siteId = randomUUID().toString();
        when(mockedData.get(FIELD_SITE_ID)).thenReturn(siteId);
        when(mockedData.get(FIELD_SITE_MANAGER_NAME)).thenReturn(randomUUID().toString());
        when(mockedData.get(FIELD_SITE_MANAGER_PASSWORD)).thenReturn("password");
        when(mockedEvent.getData()).thenReturn(mockedData);
        when(mockedSiteDataService.getSite(siteId)).thenReturn(mockedSiteData);
        when(mockedSiteData.getCreationState()).thenReturn(Created);
        EventResult result = createRMSite.processEvent(mockedEvent);
        verify(mockedSiteDataService, never()).addSite(any(SiteData.class));

        verify(mockedSiteDataService, never()).addSiteMember(any(SiteMemberData.class));
        assertEquals(false, result.isSuccess());
        assertEquals("RM Site already exists in DB: " + mockedSiteData, result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testRMSiteCreation() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        RMSiteAPI mockedRMSiteAPI = mock(RMSiteAPI.class);
        RMSite mockedRMSite = mock(RMSite.class);
        FilePlanAPI mockedFilePlanAPI = mock(FilePlanAPI.class);
        UnfiledContainerAPI mockedUnfiledContainerAPI = mock(UnfiledContainerAPI.class);
        TransferContainerAPI mockedTransferContainerAPI = mock(TransferContainerAPI.class);

        FilePlan mockedFilePlan = mock(FilePlan.class);
        UnfiledContainer mockedUnfiledRecordsContainer = mock(UnfiledContainer.class);
        TransferContainer mockedTransfers = mock(TransferContainer.class);

        String siteId = randomUUID().toString();
        String siteManager = randomUUID().toString();
        String password = "password";
        when(mockedData.get(FIELD_SITE_ID)).thenReturn(siteId);
        when(mockedData.get(FIELD_SITE_MANAGER_NAME)).thenReturn(siteManager);
        when(mockedData.get(FIELD_SITE_MANAGER_PASSWORD)).thenReturn(password);
        when(mockedEvent.getData()).thenReturn(mockedData);
        when(mockedSiteDataService.getSite(siteId)).thenReturn(null);

        when(mockedRestAPIFactory.getRMSiteAPI(any(UserModel.class))).thenReturn(mockedRMSiteAPI);
        when(mockedRMSiteAPI.createRMSite(any(RMSite.class))).thenReturn(mockedRMSite);
        when(mockedRMSiteAPI.existsRMSite()).thenReturn(true);

        when(mockedRestAPIFactory.getFilePlansAPI(any(UserModel.class))).thenReturn(mockedFilePlanAPI);
        when(mockedFilePlanAPI.getFilePlan(FILE_PLAN_ALIAS)).thenReturn(mockedFilePlan);
        when(mockedFilePlan.getId()).thenReturn(randomUUID().toString());

        when(mockedRestAPIFactory.getUnfiledContainersAPI(any(UserModel.class))).thenReturn(mockedUnfiledContainerAPI);
        when(mockedUnfiledContainerAPI.getUnfiledContainer(UNFILED_RECORDS_CONTAINER_ALIAS)).thenReturn(mockedUnfiledRecordsContainer);
        when(mockedUnfiledRecordsContainer.getId()).thenReturn(randomUUID().toString());

        when(mockedRestAPIFactory.getTransferContainerAPI(any(UserModel.class))).thenReturn(mockedTransferContainerAPI);
        when(mockedTransferContainerAPI.getTransferContainer(TRANSFERS_ALIAS)).thenReturn(mockedTransfers);
        when(mockedTransfers.getId()).thenReturn(randomUUID().toString());

        RMRestWrapper mockedRmRestWrapper = mock(RMRestWrapper.class);
        when(mockedRmRestWrapper.getStatusCode()).thenReturn(HttpStatus.CREATED.toString());
        when(mockedRestAPIFactory.getRmRestWrapper()).thenReturn(mockedRmRestWrapper);

        EventResult result = createRMSite.processEvent(mockedEvent);


        ArgumentCaptor<SiteData> siteData = forClass(SiteData.class);
        verify(mockedSiteDataService).addSite(siteData.capture());

        ArgumentCaptor<SiteMemberData> siteMemberData = forClass(SiteMemberData.class);
        verify(mockedSiteDataService).addSiteMember(siteMemberData.capture());


        // Check RM site
        SiteData siteDataValue = siteData.getValue();
        assertEquals(Scheduled, siteDataValue.getCreationState());

        // Check RM admin member
        SiteMemberData siteMemberDataValue = siteMemberData.getValue();
        assertEquals(Created, siteMemberDataValue.getCreationState());
        assertEquals(Administrator.toString(), siteMemberDataValue.getRole());

        verify(mockedRMSiteAPI, times(1)).createRMSite(any(RMSite.class));
        verify(mockedFileFolderService, times(3)).createNewFolder(any(FolderData.class));
        assertEquals(true, result.isSuccess());
        assertEquals("Created site: " + siteId + " Site creator: " + siteManager, (String) result.getData());
        List<Event> events = result.getNextEvents();
        assertEquals(1, events.size());
        Event event = events.get(0);
        assertEquals(DEFAULT_EVENT_NAME_SITE_CREATED, event.getName());
        DBObject data = (DBObject) event.getData();
        assertEquals(null, data);
    }

    @Test
    public void testLoadingInDbExistentRmSite() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        RMSiteAPI mockedRMSiteAPI = mock(RMSiteAPI.class);
        RMSite mockedRMSite = mock(RMSite.class);

        FilePlanAPI mockedFilePlanAPI = mock(FilePlanAPI.class);
        UnfiledContainerAPI mockedUnfiledContainerAPI = mock(UnfiledContainerAPI.class);
        TransferContainerAPI mockedTransferContainerAPI = mock(TransferContainerAPI.class);

        FilePlan mockedFilePlan = mock(FilePlan.class);
        UnfiledContainer mockedUnfiledRecordsContainer = mock(UnfiledContainer.class);
        TransferContainer mockedTransfers = mock(TransferContainer.class);

        String siteId = randomUUID().toString();
        String siteManager = randomUUID().toString();
        String password = "password";
        when(mockedData.get(FIELD_SITE_ID)).thenReturn(siteId);
        when(mockedData.get(FIELD_SITE_MANAGER_NAME)).thenReturn(siteManager);
        when(mockedData.get(FIELD_SITE_MANAGER_PASSWORD)).thenReturn(password);
        when(mockedData.get(FIELD_ONLY_DB_LOAD)).thenReturn(true);
        when(mockedEvent.getData()).thenReturn(mockedData);

        when(mockedRestAPIFactory.getRMSiteAPI(any(UserModel.class))).thenReturn(mockedRMSiteAPI);
        when(mockedRMSiteAPI.existsRMSite()).thenReturn(true);

        String guuid = randomUUID().toString();
        when(mockedRMSite.getGuid()).thenReturn(guuid);
        when(mockedRMSiteAPI.getSite()).thenReturn(mockedRMSite);

        when(mockedRestAPIFactory.getFilePlansAPI(any(UserModel.class))).thenReturn(mockedFilePlanAPI);
        when(mockedFilePlanAPI.getFilePlan(FILE_PLAN_ALIAS)).thenReturn(mockedFilePlan);
        when(mockedFilePlan.getId()).thenReturn(randomUUID().toString());

        when(mockedRestAPIFactory.getUnfiledContainersAPI(any(UserModel.class))).thenReturn(mockedUnfiledContainerAPI);
        when(mockedUnfiledContainerAPI.getUnfiledContainer(UNFILED_RECORDS_CONTAINER_ALIAS)).thenReturn(mockedUnfiledRecordsContainer);
        when(mockedUnfiledRecordsContainer.getId()).thenReturn(randomUUID().toString());

        when(mockedRestAPIFactory.getTransferContainerAPI(any(UserModel.class))).thenReturn(mockedTransferContainerAPI);
        when(mockedTransferContainerAPI.getTransferContainer(TRANSFERS_ALIAS)).thenReturn(mockedTransfers);
        when(mockedTransfers.getId()).thenReturn(randomUUID().toString());

        EventResult result = createRMSite.processEvent(mockedEvent);
        verify(mockedRMSiteAPI, never()).createRMSite(any(RMSite.class));
        verify(mockedSiteDataService, times(1)).addSite(any(SiteData.class));
        verify(mockedSiteDataService, times(1)).addSiteMember(any(SiteMemberData.class));

        verify(mockedFileFolderService, times(3)).createNewFolder(any(FolderData.class));
        assertEquals(true, result.isSuccess());
        assertEquals("RM site already exists, just loading it in the DB.", (String) result.getData());
        List<Event> events = result.getNextEvents();
        assertEquals(1, events.size());
        Event event = events.get(0);
        assertEquals(DEFAULT_EVENT_NAME_SITE_CREATED, event.getName());
        DBObject data = (DBObject) event.getData();
        assertEquals(null, data);
    }

    @Test(expected = RuntimeException.class)
    public void testRMSiteCreationExceptionThrown() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        Exception mockedException = mock(RuntimeException.class);
        String siteId = randomUUID().toString();
        String siteManager = randomUUID().toString();
        String password = "password";
        when(mockedData.get(FIELD_SITE_ID)).thenReturn(siteId);
        when(mockedData.get(FIELD_SITE_MANAGER_NAME)).thenReturn(siteManager);
        when(mockedData.get(FIELD_SITE_MANAGER_PASSWORD)).thenReturn(password);
        when(mockedEvent.getData()).thenReturn(mockedData);
        when(mockedSiteDataService.getSite(siteId)).thenReturn(null);
        when(mockedRestAPIFactory.getRMSiteAPI(new UserModel(siteManager, siteManager))).thenThrow(mockedException);
        when(mockedException.getMessage()).thenReturn(randomUUID().toString());
        createRMSite.processEvent(mockedEvent);
        verify(mockedSiteDataService, never()).addSite(any(SiteData.class));

        verify(mockedSiteDataService, never()).addSiteMember(any(SiteMemberData.class));
    }

    @Test
    public void testRMSiteCreationNotPossible() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        RMSiteAPI mockedRMSiteAPI = mock(RMSiteAPI.class);
        String siteId = randomUUID().toString();
        String siteManager = randomUUID().toString();
        String password = "password";
        when(mockedData.get(FIELD_SITE_ID)).thenReturn(siteId);
        when(mockedData.get(FIELD_SITE_MANAGER_NAME)).thenReturn(siteManager);
        when(mockedData.get(FIELD_SITE_MANAGER_PASSWORD)).thenReturn(password);
        when(mockedEvent.getData()).thenReturn(mockedData);
        when(mockedSiteDataService.getSite(siteId)).thenReturn(null);
        when(mockedRestAPIFactory.getRMSiteAPI(any(UserModel.class))).thenReturn(mockedRMSiteAPI);

        RMRestWrapper mockedRmRestWrapper = mock(RMRestWrapper.class);
        when(mockedRmRestWrapper.getStatusCode()).thenReturn(HttpStatus.FORBIDDEN.toString());
        when(mockedRestAPIFactory.getRmRestWrapper()).thenReturn(mockedRmRestWrapper);

        EventResult result = createRMSite.processEvent(mockedEvent);
        verify(mockedSiteDataService, never()).addSite(any(SiteData.class));
        verify(mockedSiteDataService, never()).addSiteMember(any(SiteMemberData.class));
        assertEquals(false, result.isSuccess());
        assertEquals("RM site could not be created.", result.getData());
        assertEquals(0, result.getNextEvents().size());
    }
}
