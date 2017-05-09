/*
 * Copyright (C) 2005-2017 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.bm.dataload.rm.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.alfresco.bm.cm.FileFolderService;
import org.alfresco.bm.cm.FolderData;
import org.alfresco.bm.data.DataCreationState;
import org.alfresco.bm.dataload.LoadSingleComponent;
import org.alfresco.bm.dataload.RMEventConstants;
import org.alfresco.bm.dataload.rm.role.RMRole;
import org.alfresco.bm.dataload.rm.services.RecordData;
import org.alfresco.bm.dataload.rm.services.RecordService;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.session.SessionService;
import org.alfresco.bm.site.SiteData;
import org.alfresco.bm.site.SiteDataService;
import org.alfresco.bm.site.SiteMemberData;
import org.alfresco.bm.user.UserData;
import org.alfresco.bm.user.UserDataService;
import org.alfresco.rest.core.RestAPIFactory;
import org.alfresco.rest.rm.community.model.record.RecordBodyFile;
import org.alfresco.rest.rm.community.requests.gscore.api.RecordsAPI;
import org.alfresco.utility.model.UserModel;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import com.mongodb.DBObject;

/**
 * Unit tests for FileUnfiledRecords
 *
 * @author Silviu Dinuta
 * @since 2.6
 */
@RunWith(MockitoJUnitRunner.class)
public class LoadSingleComponentUnitTest implements RMEventConstants
{
    private static final String EVENT_UNFILED_RECORD_FILED = "testUnfiledRecordFiled";

    @Mock
    private SessionService mockedSessionService;

    @Mock
    private FileFolderService mockedFileFolderService;

    @Mock
    private RestAPIFactory mockedRestApiFactory;

    @Mock
    private UserDataService mockedUserDataService;

    @Mock
    private SiteDataService mockedSiteDataService;

    @Mock
    private ApplicationContext mockedApplicationContext;

    @Mock
    private RecordService mockedRecordService;

    @Mock
    private RecordsAPI mockedRecordsAPI;

    /**
     * Common tests for all operations
     */
    @InjectMocks
    private LoadSingleComponent fileUnfiledRecords;
    @Test(expected=IllegalStateException.class)
    public void testWithNullEvent() throws Exception
    {
        fileUnfiledRecords.processEvent(null, new StopWatch());
    }

    @Test(expected=IllegalStateException.class)
    public void testWithNullData() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        when(mockedEvent.getData()).thenReturn(null);
        fileUnfiledRecords.processEvent(mockedEvent, new StopWatch());
    }

    @Test
    public void testWithNullContext() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn(null);
        when(mockedEvent.getData()).thenReturn(mockedData);
        EventResult result = fileUnfiledRecords.processEvent(mockedEvent, new StopWatch());
        assertEquals(false, result.isSuccess());
        assertEquals("Request data not complete for filing unfiled record: " + mockedData, result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testWithNullPath() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn(null);
        when(mockedEvent.getData()).thenReturn(mockedData);
        EventResult result = fileUnfiledRecords.processEvent(mockedEvent, new StopWatch());
        assertEquals(false, result.isSuccess());
        assertEquals("Request data not complete for filing unfiled record: " + mockedData, result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testWithNullOperation() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_LOAD_OPERATION)).thenReturn(null);
        when(mockedEvent.getData()).thenReturn(mockedData);
        EventResult result = fileUnfiledRecords.processEvent(mockedEvent, new StopWatch());
        assertEquals(false, result.isSuccess());
        assertEquals("Request data not complete for filing unfiled record: " + mockedData, result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test(expected=IllegalStateException.class)
    public void testInexistentFolderForContextAndPath() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_LOAD_OPERATION)).thenReturn("someOperation");
        when(mockedEvent.getData()).thenReturn(mockedData);
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(null);

        fileUnfiledRecords.processEvent(mockedEvent, new StopWatch());
    }

    @Test
    public void testWithNullSessionID() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_LOAD_OPERATION)).thenReturn("someOperation");
        when(mockedEvent.getData()).thenReturn(mockedData);
        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(mockedFolder);
        when(mockedEvent.getSessionId()).thenReturn(null);

        EventResult result = fileUnfiledRecords.processEvent(mockedEvent, new StopWatch());
        assertEquals(false, result.isSuccess());
        assertEquals("Load scheduling should create a session for each loader.", result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    /**
     * Tests for file record operation
     */
    @Test
    public void testFileRecordOperationWithNullRecordId() throws Exception
    {
        fileUnfiledRecords.setEventNameComplete(EVENT_UNFILED_RECORD_FILED);
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_LOAD_OPERATION)).thenReturn(FILE_RECORD_OPERATION);
        when(mockedData.get(FIELD_RECORD_ID)).thenReturn(null);
        when(mockedEvent.getData()).thenReturn(mockedData);

        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFolder.getId()).thenReturn("folderId");
        when(mockedFolder.getPath()).thenReturn("/aPath");
        when(mockedFolder.getContext()).thenReturn("someContext");
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(mockedFolder);
        when(mockedEvent.getSessionId()).thenReturn("someId");

        EventResult result = fileUnfiledRecords.processEvent(mockedEvent, new StopWatch());
        assertEquals(false, result.isSuccess());
        assertEquals("Request data not complete for filing unfiled record: " + mockedData, result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testFileRecordOperationWithEmptyRecordId() throws Exception
    {
        fileUnfiledRecords.setEventNameComplete(EVENT_UNFILED_RECORD_FILED);
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_LOAD_OPERATION)).thenReturn(FILE_RECORD_OPERATION);
        when(mockedData.get(FIELD_RECORD_ID)).thenReturn("");
        when(mockedEvent.getData()).thenReturn(mockedData);

        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFolder.getId()).thenReturn("folderId");
        when(mockedFolder.getPath()).thenReturn("/aPath");
        when(mockedFolder.getContext()).thenReturn("someContext");
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(mockedFolder);
        when(mockedEvent.getSessionId()).thenReturn("someId");

        EventResult result = fileUnfiledRecords.processEvent(mockedEvent, new StopWatch());
        assertEquals(false, result.isSuccess());
        assertEquals("Request data not complete for filing unfiled record: " + mockedData, result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testFileRecordOperationWithRestAPiException() throws Exception
    {
        fileUnfiledRecords.setEventNameComplete(EVENT_UNFILED_RECORD_FILED);
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_LOAD_OPERATION)).thenReturn(FILE_RECORD_OPERATION);
        when(mockedData.get(FIELD_RECORD_ID)).thenReturn("recordId1");
        when(mockedEvent.getData()).thenReturn(mockedData);

        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFolder.getId()).thenReturn("folderId");
        when(mockedFolder.getPath()).thenReturn("/aPath");
        when(mockedFolder.getContext()).thenReturn("someContext");
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(mockedFolder);
        when(mockedEvent.getSessionId()).thenReturn("someId");

        RecordData mockedRecordData = mock(RecordData.class);
        when(mockedRecordService.getRecord("recordId1")).thenReturn(mockedRecordData);
        Mockito.doThrow(new Exception("someError")).when(mockedRecordsAPI).fileRecord(any(RecordBodyFile.class), any(String.class));

        mockSiteAndUserData();
        EventResult result = fileUnfiledRecords.processEvent(mockedEvent, new StopWatch());
        verify(mockedFileFolderService, never()).deleteFolder(mockedFolder.getContext(), mockedFolder.getPath() + "/locked", false);
        verify(mockedFileFolderService, never()).incrementFileCount(any(String.class), any(String.class), any(Long.class));

        assertEquals(false, result.isSuccess());
        DBObject data = (DBObject) result.getData();
        assertNotNull(data.get("error"));

        assertEquals("someError", data.get("error"));

        assertEquals("aUser", data.get("username"));
        assertEquals(mockedFolder.getPath(), data.get("path"));
        assertNotNull(data.get("stack"));
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testFileRecordOperation() throws Exception
    {
        fileUnfiledRecords.setEventNameComplete(EVENT_UNFILED_RECORD_FILED);
        fileUnfiledRecords.setDelay(1L);
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_LOAD_OPERATION)).thenReturn(FILE_RECORD_OPERATION);
        when(mockedData.get(FIELD_RECORD_ID)).thenReturn("recordId1");
        when(mockedEvent.getData()).thenReturn(mockedData);

        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFolder.getId()).thenReturn("folderId");
        when(mockedFolder.getPath()).thenReturn("/aPath");
        when(mockedFolder.getContext()).thenReturn("someContext");
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(mockedFolder);
        when(mockedEvent.getSessionId()).thenReturn("someId");

        String recordId1 = "recordId1";
        String recordParentPath1 = "/recordParentPath1";
        RecordData mockedRecordData1 = mock(RecordData.class);
        when(mockedRecordData1.getId()).thenReturn(recordId1);
        when(mockedRecordData1.getParentPath()).thenReturn(recordParentPath1);
        when(mockedRecordService.getRecord(recordId1)).thenReturn(mockedRecordData1);

        mockSiteAndUserData();
        EventResult result = fileUnfiledRecords.processEvent(mockedEvent, new StopWatch());
        verify(mockedFileFolderService, times(1)).incrementFileCount(mockedFolder.getContext(), mockedFolder.getPath(), 1);
        verify(mockedFileFolderService, times(1)).incrementFileCount(UNFILED_CONTEXT, mockedRecordData1.getParentPath(), -1);
        verify(mockedRecordService, times(1)).updateRecord(any(RecordData.class));

        assertEquals(true, result.isSuccess());
        DBObject data = (DBObject) result.getData();
        assertEquals("Filed record with id " + recordId1 + ".", data.get("msg"));
        assertEquals("/aPath", data.get(FIELD_PATH));
        assertEquals("aUser", data.get("username"));
        assertEquals(1, result.getNextEvents().size());
        Event event = result.getNextEvents().get(0);
        assertEquals(EVENT_UNFILED_RECORD_FILED, event.getName());
        DBObject eventData = (DBObject) event.getData();
        assertEquals("someContext", eventData.get(FIELD_CONTEXT));
        assertEquals("/aPath", eventData.get(FIELD_PATH));
    }

    /**
     * Helper methods
     */

    /**
     * Helper method for mocking user data
     */
    private void mockSiteAndUserData()
    {
        SiteData mockedSiteData = mock(SiteData.class);
        when(mockedSiteDataService.getSite(PATH_SNIPPET_RM_SITE_ID)).thenReturn(mockedSiteData);
        SiteMemberData mockedSiteMemberData = mock(SiteMemberData.class);
        when(mockedSiteMemberData.getUsername()).thenReturn("aUser");
        when(mockedSiteDataService.randomSiteMember(PATH_SNIPPET_RM_SITE_ID, DataCreationState.Created, null, RMRole.Administrator.toString())).thenReturn(mockedSiteMemberData);
        UserData mockedUserData = mock(UserData.class);
        when(mockedUserData.getUsername()).thenReturn("aUser");
        when(mockedUserData.getPassword()).thenReturn("aUser");
        when(mockedUserDataService.findUserByUsername("aUser")).thenReturn(mockedUserData);
        when(mockedApplicationContext.getBean("restAPIFactory", RestAPIFactory.class)).thenReturn(mockedRestApiFactory);
        when(mockedRestApiFactory.getRecordsAPI(any(UserModel.class))).thenReturn(mockedRecordsAPI);
    }
}
