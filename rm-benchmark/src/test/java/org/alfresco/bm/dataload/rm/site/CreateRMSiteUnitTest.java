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

import static org.alfresco.bm.data.DataCreationState.Failed;
import static org.alfresco.bm.data.DataCreationState.Scheduled;
import static org.alfresco.bm.data.DataCreationState.Created;
import static org.alfresco.bm.dataload.rm.site.CreateRMSite.DEFAULT_EVENT_NAME_SITE_CREATED;
import static org.alfresco.bm.dataload.rm.site.PrepareRMSite.FIELD_SITE_ID;
import static org.alfresco.bm.dataload.rm.site.PrepareRMSite.FIELD_SITE_MANAGER;
import static org.alfresco.bm.dataload.rm.site.PrepareRMSite.FIELD_ONLY_DB_LOAD;
import static org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponentAlias.FILE_PLAN_ALIAS;
import static org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponentAlias.HOLDS_ALIAS;
import static org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponentAlias.TRANSFERS_ALIAS;
import static org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponentAlias.UNFILED_RECORDS_CONTAINER_ALIAS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
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
import org.alfresco.bm.restapi.RestAPIFactory;
import org.alfresco.bm.site.SiteData;
import org.alfresco.bm.site.SiteDataService;
import org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponent;
import org.alfresco.rest.rm.community.model.site.RMSite;
import org.alfresco.rest.rm.community.requests.FilePlanComponentAPI;
import org.alfresco.rest.rm.community.requests.RMSiteAPI;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit test for prepare RM site event processor
 *
 * @author Tuna Aksoy
 * @since 1.0
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
        when(mockedData.get(FIELD_SITE_MANAGER)).thenReturn(null);
        when(mockedEvent.getData()).thenReturn(mockedData);
        EventResult result = createRMSite.processEvent(mockedEvent);
        assertEquals(false, result.isSuccess());
        assertEquals("Requests data not complete for site creation: " + mockedData, result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testWithNullSiteData() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        String siteId = randomUUID().toString();
        when(mockedData.get(FIELD_SITE_ID)).thenReturn(siteId);
        when(mockedData.get(FIELD_SITE_MANAGER)).thenReturn(randomUUID().toString());
        when(mockedEvent.getData()).thenReturn(mockedData);
        when(mockedSiteDataService.getSite(siteId)).thenReturn(null);
        EventResult result = createRMSite.processEvent(mockedEvent);
        assertEquals(false, result.isSuccess());
        assertEquals("Site has been removed: " + siteId, result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testCreationStateNotScheduled() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        SiteData mockedSiteData = mock(SiteData.class);
        String siteId = randomUUID().toString();
        when(mockedData.get(FIELD_SITE_ID)).thenReturn(siteId);
        when(mockedData.get(FIELD_SITE_MANAGER)).thenReturn(randomUUID().toString());
        when(mockedEvent.getData()).thenReturn(mockedData);
        when(mockedSiteDataService.getSite(siteId)).thenReturn(mockedSiteData);
        when(mockedSiteData.getCreationState()).thenReturn(Failed);
        EventResult result = createRMSite.processEvent(mockedEvent);
        assertEquals(false, result.isSuccess());
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
        when(mockedData.get(FIELD_SITE_MANAGER)).thenReturn(randomUUID().toString());
        when(mockedEvent.getData()).thenReturn(mockedData);
        when(mockedSiteDataService.getSite(siteId)).thenReturn(mockedSiteData);
        when(mockedSiteData.getCreationState()).thenReturn(Created);
        EventResult result = createRMSite.processEvent(mockedEvent);
        assertEquals(false, result.isSuccess());
        assertEquals("RM Site already exists in DB: " + mockedSiteData, result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testRMSiteCreation() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        SiteData mockedSiteData = mock(SiteData.class);
        RMSiteAPI mockedRMSiteAPI = mock(RMSiteAPI.class);
        RMSite mockedRMSite = mock(RMSite.class);
        FilePlanComponentAPI mockedFilePlanComponentAPI = mock(FilePlanComponentAPI.class);
        FilePlanComponent mockedFilePlan = mock(FilePlanComponent.class);
        FilePlanComponent mockedUnfiledRecordsContainer = mock(FilePlanComponent.class);
        FilePlanComponent mockedTransfers = mock(FilePlanComponent.class);
        FilePlanComponent mockedHolds = mock(FilePlanComponent.class);
        String siteId = randomUUID().toString();
        String siteManager = randomUUID().toString();
        when(mockedData.get(FIELD_SITE_ID)).thenReturn(siteId);
        when(mockedData.get(FIELD_SITE_MANAGER)).thenReturn(siteManager);
        when(mockedEvent.getData()).thenReturn(mockedData);
        when(mockedSiteDataService.getSite(siteId)).thenReturn(mockedSiteData);
        when(mockedSiteData.getCreationState()).thenReturn(Scheduled);
        when(mockedRestAPIFactory.getRMSiteAPI(siteManager)).thenReturn(mockedRMSiteAPI);
        when(mockedRMSiteAPI.createRMSite(any(RMSite.class))).thenReturn(mockedRMSite);
        when(mockedRMSiteAPI.existsRMSite()).thenReturn(true);

        when(mockedRestAPIFactory.getFilePlanComponentAPI(siteManager)).thenReturn(mockedFilePlanComponentAPI);
        when(mockedFilePlanComponentAPI.getFilePlanComponent(FILE_PLAN_ALIAS)).thenReturn(mockedFilePlan);
        when(mockedFilePlan.getId()).thenReturn(randomUUID().toString());
        when(mockedFilePlanComponentAPI.getFilePlanComponent(UNFILED_RECORDS_CONTAINER_ALIAS)).thenReturn(mockedFilePlan);
        when(mockedUnfiledRecordsContainer.getId()).thenReturn(randomUUID().toString());
        when(mockedFilePlanComponentAPI.getFilePlanComponent(TRANSFERS_ALIAS)).thenReturn(mockedFilePlan);
        when(mockedTransfers.getId()).thenReturn(randomUUID().toString());
        when(mockedFilePlanComponentAPI.getFilePlanComponent(HOLDS_ALIAS)).thenReturn(mockedFilePlan);
        when(mockedHolds.getId()).thenReturn(randomUUID().toString());

        EventResult result = createRMSite.processEvent(mockedEvent);
        verify(mockedRMSiteAPI, times(1)).createRMSite(any(RMSite.class));
        verify(mockedFileFolderService, times(4)).createNewFolder(any(FolderData.class));
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
    public void testLoadingInDbExistendRmSite() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        SiteData mockedSiteData = mock(SiteData.class);
        RMSiteAPI mockedRMSiteAPI = mock(RMSiteAPI.class);
        RMSite mockedRMSite = mock(RMSite.class);
        FilePlanComponentAPI mockedFilePlanComponentAPI = mock(FilePlanComponentAPI.class);
        FilePlanComponent mockedFilePlan = mock(FilePlanComponent.class);
        FilePlanComponent mockedUnfiledRecordsContainer = mock(FilePlanComponent.class);
        FilePlanComponent mockedTransfers = mock(FilePlanComponent.class);
        FilePlanComponent mockedHolds = mock(FilePlanComponent.class);
        String siteId = randomUUID().toString();
        String siteManager = randomUUID().toString();
        when(mockedData.get(FIELD_SITE_ID)).thenReturn(siteId);
        when(mockedData.get(FIELD_SITE_MANAGER)).thenReturn(siteManager);
        when(mockedData.get(FIELD_ONLY_DB_LOAD)).thenReturn(true);
        when(mockedEvent.getData()).thenReturn(mockedData);

        when(mockedSiteDataService.getSite(siteId)).thenReturn(mockedSiteData);
        when(mockedSiteData.getCreationState()).thenReturn(Scheduled);
        when(mockedRestAPIFactory.getRMSiteAPI(siteManager)).thenReturn(mockedRMSiteAPI);
        when(mockedRMSiteAPI.existsRMSite()).thenReturn(true);

        String guuid = randomUUID().toString();
        when(mockedRMSite.getGuid()).thenReturn(guuid);
        when(mockedRMSiteAPI.getSite()).thenReturn(mockedRMSite);

        when(mockedRestAPIFactory.getFilePlanComponentAPI(siteManager)).thenReturn(mockedFilePlanComponentAPI);
        when(mockedFilePlanComponentAPI.getFilePlanComponent(FILE_PLAN_ALIAS)).thenReturn(mockedFilePlan);
        when(mockedFilePlan.getId()).thenReturn(randomUUID().toString());
        when(mockedFilePlanComponentAPI.getFilePlanComponent(UNFILED_RECORDS_CONTAINER_ALIAS)).thenReturn(mockedFilePlan);
        when(mockedUnfiledRecordsContainer.getId()).thenReturn(randomUUID().toString());
        when(mockedFilePlanComponentAPI.getFilePlanComponent(TRANSFERS_ALIAS)).thenReturn(mockedFilePlan);
        when(mockedTransfers.getId()).thenReturn(randomUUID().toString());
        when(mockedFilePlanComponentAPI.getFilePlanComponent(HOLDS_ALIAS)).thenReturn(mockedFilePlan);
        when(mockedHolds.getId()).thenReturn(randomUUID().toString());

        EventResult result = createRMSite.processEvent(mockedEvent);
        verify(mockedRMSiteAPI, never()).createRMSite(any(RMSite.class));
        verify(mockedFileFolderService, times(4)).createNewFolder(any(FolderData.class));
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
        SiteData mockedSiteData = mock(SiteData.class);
        Exception mockedException = mock(RuntimeException.class);
        String siteId = randomUUID().toString();
        String siteManager = randomUUID().toString();
        when(mockedData.get(FIELD_SITE_ID)).thenReturn(siteId);
        when(mockedData.get(FIELD_SITE_MANAGER)).thenReturn(siteManager);
        when(mockedEvent.getData()).thenReturn(mockedData);
        when(mockedSiteDataService.getSite(siteId)).thenReturn(mockedSiteData);
        when(mockedSiteData.getCreationState()).thenReturn(Scheduled);
        when(mockedRestAPIFactory.getRMSiteAPI(siteManager)).thenThrow(mockedException);
        when(mockedException.getMessage()).thenReturn(randomUUID().toString());
        createRMSite.processEvent(mockedEvent);
    }
}
