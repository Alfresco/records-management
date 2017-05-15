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

package org.alfresco.bm.dataload.rm.unfiled;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import com.mongodb.DBObject;

import org.alfresco.bm.cm.FileFolderService;
import org.alfresco.bm.cm.FolderData;
import org.alfresco.bm.data.DataCreationState;
import org.alfresco.bm.dataload.RMEventConstants;
import org.alfresco.bm.dataload.rm.role.RMRole;
import org.alfresco.bm.dataload.rm.services.RecordService;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.file.TestFileService;
import org.alfresco.bm.session.SessionService;
import org.alfresco.bm.site.SiteData;
import org.alfresco.bm.site.SiteDataService;
import org.alfresco.bm.site.SiteMemberData;
import org.alfresco.bm.user.UserData;
import org.alfresco.bm.user.UserDataService;
import org.alfresco.rest.core.RestAPIFactory;
import org.alfresco.rest.rm.community.model.unfiledcontainer.UnfiledContainer;
import org.alfresco.rest.rm.community.model.unfiledcontainer.UnfiledContainerChild;
import org.alfresco.rest.rm.community.model.unfiledcontainer.UnfiledRecordFolder;
import org.alfresco.rest.rm.community.requests.gscore.api.UnfiledContainerAPI;
import org.alfresco.rest.rm.community.requests.gscore.api.UnfiledRecordFolderAPI;
import org.alfresco.utility.model.UserModel;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

/**
 * Unit tests for LoadUnfiledRecords
 * @author Silviu Dinuta
 * @since 2.6
 */
@RunWith(MockitoJUnitRunner.class)
public class LoadUnfiledRecordsUnitTest implements RMEventConstants
{
    @Mock
    private SessionService mockedSessionService;

    @Mock
    private FileFolderService mockedFileFolderService;

    @Mock
    private RestAPIFactory mockedRestApiFactory;

    @Mock
    private UnfiledContainerAPI mockedUnfiledContainerAPI;

    @Mock
    private UnfiledRecordFolderAPI mockedUnfiledRecordFolderAPI;

    @Mock TestFileService mockedTestFileService;

    @Mock
    private UserDataService mockedUserDataService;

    @Mock
    private SiteDataService mockedSiteDataService;

    @Mock
    private ApplicationContext mockedApplicationContext;

    @Mock
    private RecordService mockedReocodService;

    @InjectMocks
    private LoadUnfiledRecords loadUnfiledRecords;

    @Test(expected=IllegalStateException.class)
    public void testWithNullEvent() throws Exception
    {
        loadUnfiledRecords.processEvent(null, new StopWatch());
    }

    @Test(expected=IllegalStateException.class)
    public void testWithNullData() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        when(mockedEvent.getData()).thenReturn(null);
        loadUnfiledRecords.processEvent(mockedEvent, new StopWatch());
    }

    @Test
    public void testWithNullContext() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn(null);
        when(mockedEvent.getData()).thenReturn(mockedData);
        EventResult result = loadUnfiledRecords.processEvent(mockedEvent, new StopWatch());
        assertEquals(false, result.isSuccess());
        assertEquals("Request data not complete for records loading: " + mockedData, result.getData());
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
        EventResult result = loadUnfiledRecords.processEvent(mockedEvent, new StopWatch());
        assertEquals(false, result.isSuccess());
        assertEquals("Request data not complete for records loading: " + mockedData, result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testWithNullRecordsToCreate() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_RECORDS_TO_CREATE)).thenReturn(null);
        when(mockedEvent.getData()).thenReturn(mockedData);
        EventResult result = loadUnfiledRecords.processEvent(mockedEvent, new StopWatch());
        assertEquals(false, result.isSuccess());
        assertEquals("Request data not complete for records loading: " + mockedData, result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test(expected=IllegalStateException.class)
    public void testInexistentFolderForContextAndPath() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_RECORDS_TO_CREATE)).thenReturn(Integer.valueOf(0));
        when(mockedEvent.getData()).thenReturn(mockedData);
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(null);

        loadUnfiledRecords.processEvent(mockedEvent, new StopWatch());
    }

    @Test
    public void testWithNullSessionID() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_RECORDS_TO_CREATE)).thenReturn(Integer.valueOf(0));
        when(mockedEvent.getData()).thenReturn(mockedData);
        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(mockedFolder);
        when(mockedEvent.getSessionId()).thenReturn(null);

        EventResult result = loadUnfiledRecords.processEvent(mockedEvent, new StopWatch());
        assertEquals(false, result.isSuccess());
        assertEquals("Load scheduling should create a session for each loader.", result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testLoadNoRecordsToCreate() throws Exception
    {
        int recordsToCreate = 0;
        loadUnfiledRecords.setEventNameUnfiledRecordsLoaded("unfiledRecordsLoaded");
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_RECORDS_TO_CREATE)).thenReturn(Integer.valueOf(recordsToCreate));
        when(mockedEvent.getData()).thenReturn(mockedData);

        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFolder.getId()).thenReturn("folderId");
        when(mockedFolder.getPath()).thenReturn("/aPath");
        when(mockedFolder.getContext()).thenReturn("someContext");
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(mockedFolder);
        when(mockedEvent.getSessionId()).thenReturn("someId");

        when(mockedRestApiFactory.getUnfiledContainersAPI(any(UserModel.class))).thenReturn(mockedUnfiledContainerAPI);
        when(mockedRestApiFactory.getUnfiledRecordFoldersAPI(any(UserModel.class))).thenReturn(mockedUnfiledRecordFolderAPI);

        mockSiteAndUserData();
        EventResult result = loadUnfiledRecords.processEvent(mockedEvent, new StopWatch());
        verify(mockedFileFolderService, never()).deleteFolder(mockedFolder.getContext(), mockedFolder.getPath() + "/locked", false);
        verify(mockedTestFileService, never()).getFile();

        verify(mockedUnfiledContainerAPI, never()).uploadRecord(any(UnfiledContainerChild.class), eq("folderId"), any(File.class));
        verify(mockedUnfiledRecordFolderAPI, never()).uploadRecord(any(UnfiledContainerChild.class), eq("folderId"), any(File.class));

        verify(mockedFileFolderService, never()).incrementFileCount(any(String.class), any(String.class), any(Long.class));
        assertEquals(true, result.isSuccess());
        DBObject data = (DBObject) result.getData();
        assertEquals("Created " + recordsToCreate + " records.", data.get("msg"));
        assertEquals("/aPath", data.get(FIELD_PATH));
        assertEquals("aUser", data.get("username"));
        assertEquals(1, result.getNextEvents().size());
        Event event = result.getNextEvents().get(0);
        assertEquals("unfiledRecordsLoaded", event.getName());
        DBObject eventData = (DBObject) event.getData();
        assertEquals("someContext", eventData.get(FIELD_CONTEXT));
        assertEquals("/aPath", eventData.get(FIELD_PATH));
    }

    @Test
    public void testLoadRecordsInUnfiledContainerWithRestAPiException() throws Exception
    {
        int recordsToCreate = 3;
        loadUnfiledRecords.setEventNameUnfiledRecordsLoaded("unfiledRecordsLoaded");
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_RECORDS_TO_CREATE)).thenReturn(Integer.valueOf(recordsToCreate));
        when(mockedEvent.getData()).thenReturn(mockedData);

        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFolder.getId()).thenReturn("folderId");
        when(mockedFolder.getPath()).thenReturn("/aPath");
        when(mockedFolder.getContext()).thenReturn("someContext");
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(mockedFolder);
        when(mockedEvent.getSessionId()).thenReturn("someId");

        when(mockedRestApiFactory.getUnfiledContainersAPI(any(UserModel.class))).thenReturn(mockedUnfiledContainerAPI);
        when(mockedRestApiFactory.getUnfiledRecordFoldersAPI(any(UserModel.class))).thenReturn(mockedUnfiledRecordFolderAPI);

        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, UNFILED_RECORD_CONTAINER_PATH)).thenReturn(mockedFolder);

        UnfiledContainer mockedFilePlanComponent = mock(UnfiledContainer.class);
        when(mockedUnfiledContainerAPI.getUnfiledContainer("folderId")).thenReturn(mockedFilePlanComponent);

        File mockedFile = mock(File.class);
        when(mockedTestFileService.getFile()).thenReturn(mockedFile);
        Mockito.doThrow(new Exception("someError")).when(mockedUnfiledContainerAPI).uploadRecord(any(UnfiledContainerChild.class), any(String.class), any(File.class));

        mockSiteAndUserData();
        EventResult result = loadUnfiledRecords.processEvent(mockedEvent, new StopWatch());
        verify(mockedFileFolderService, never()).deleteFolder(mockedFolder.getContext(), mockedFolder.getPath() + "/locked", false);

        verify(mockedTestFileService, times(1)).getFile();
        verify(mockedUnfiledContainerAPI, times(1)).uploadRecord(any(UnfiledContainerChild.class), eq("folderId"), any(File.class));

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
    public void testLoadRecordsInUnfiledRecordFolderWithRestAPiException() throws Exception
    {
        int recordsToCreate = 3;
        loadUnfiledRecords.setEventNameUnfiledRecordsLoaded("unfiledRecordsLoaded");
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_RECORDS_TO_CREATE)).thenReturn(Integer.valueOf(recordsToCreate));
        when(mockedEvent.getData()).thenReturn(mockedData);

        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFolder.getId()).thenReturn("folderId");
        when(mockedFolder.getPath()).thenReturn("/aPath");
        when(mockedFolder.getContext()).thenReturn("someContext");
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(mockedFolder);
        when(mockedEvent.getSessionId()).thenReturn("someId");

        when(mockedRestApiFactory.getUnfiledContainersAPI(any(UserModel.class))).thenReturn(mockedUnfiledContainerAPI);
        when(mockedRestApiFactory.getUnfiledRecordFoldersAPI(any(UserModel.class))).thenReturn(mockedUnfiledRecordFolderAPI);

        FolderData mockedFolder1 = mock(FolderData.class);
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, UNFILED_RECORD_CONTAINER_PATH)).thenReturn(mockedFolder1);

        File mockedFile = mock(File.class);
        when(mockedTestFileService.getFile()).thenReturn(mockedFile);
        Mockito.doThrow(new Exception("someError")).when(mockedUnfiledRecordFolderAPI).uploadRecord(any(UnfiledContainerChild.class), any(String.class), any(File.class));

        mockSiteAndUserData();
        EventResult result = loadUnfiledRecords.processEvent(mockedEvent, new StopWatch());
        verify(mockedFileFolderService, never()).deleteFolder(mockedFolder.getContext(), mockedFolder.getPath() + "/locked", false);

        verify(mockedTestFileService, times(1)).getFile();
        verify(mockedUnfiledRecordFolderAPI, times(1)).uploadRecord(any(UnfiledContainerChild.class), eq("folderId"), any(File.class));

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
    public void testLoadRecordsInUnfiledContainerWithNoFileException() throws Exception
    {
        int recordsToCreate = 3;
        loadUnfiledRecords.setEventNameUnfiledRecordsLoaded("unfiledRecordsLoaded");
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_RECORDS_TO_CREATE)).thenReturn(Integer.valueOf(recordsToCreate));
        when(mockedEvent.getData()).thenReturn(mockedData);

        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFolder.getId()).thenReturn("folderId");
        when(mockedFolder.getPath()).thenReturn("/aPath");
        when(mockedFolder.getContext()).thenReturn("someContext");
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(mockedFolder);
        when(mockedEvent.getSessionId()).thenReturn("someId");

        when(mockedRestApiFactory.getUnfiledContainersAPI(any(UserModel.class))).thenReturn(mockedUnfiledContainerAPI);
        when(mockedRestApiFactory.getUnfiledRecordFoldersAPI(any(UserModel.class))).thenReturn(mockedUnfiledRecordFolderAPI);

        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, UNFILED_RECORD_CONTAINER_PATH)).thenReturn(mockedFolder);

        UnfiledContainer mockedFilePlanComponent = mock(UnfiledContainer.class);
        when(mockedUnfiledContainerAPI.getUnfiledContainer("folderId")).thenReturn(mockedFilePlanComponent);

        mockSiteAndUserData();
        EventResult result = loadUnfiledRecords.processEvent(mockedEvent, new StopWatch());
        verify(mockedFileFolderService, never()).deleteFolder(mockedFolder.getContext(), mockedFolder.getPath() + "/locked", false);
        verify(mockedTestFileService, times(1)).getFile();
        verify(mockedUnfiledContainerAPI, never()).uploadRecord(any(UnfiledContainerChild.class), eq("folderId"), any(File.class));
        verify(mockedFileFolderService, never()).incrementFileCount(any(String.class), any(String.class), any(Long.class));

        assertEquals(false, result.isSuccess());
        DBObject data = (DBObject) result.getData();
        assertNotNull(data.get("error"));
        assertEquals("No test files exist for upload: mockedTestFileService", data.get("error"));
        assertEquals("aUser", data.get("username"));
        assertEquals(mockedFolder.getPath(), data.get("path"));
        assertNotNull(data.get("stack"));
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testLoadRecordsInUnfiledRecordFolderWithNoFileException() throws Exception
    {
        int recordsToCreate = 3;
        loadUnfiledRecords.setEventNameUnfiledRecordsLoaded("unfiledRecordsLoaded");
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_RECORDS_TO_CREATE)).thenReturn(Integer.valueOf(recordsToCreate));
        when(mockedEvent.getData()).thenReturn(mockedData);

        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFolder.getId()).thenReturn("folderId");
        when(mockedFolder.getPath()).thenReturn("/aPath");
        when(mockedFolder.getContext()).thenReturn("someContext");
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(mockedFolder);
        when(mockedEvent.getSessionId()).thenReturn("someId");

        when(mockedRestApiFactory.getUnfiledContainersAPI(any(UserModel.class))).thenReturn(mockedUnfiledContainerAPI);
        when(mockedRestApiFactory.getUnfiledRecordFoldersAPI(any(UserModel.class))).thenReturn(mockedUnfiledRecordFolderAPI);

        FolderData mockedFolder1 = mock(FolderData.class);
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, UNFILED_RECORD_CONTAINER_PATH)).thenReturn(mockedFolder1);

        UnfiledRecordFolder mockedUnfiledRecordFolder = mock(UnfiledRecordFolder.class);
        when(mockedUnfiledRecordFolderAPI.getUnfiledRecordFolder("folderId")).thenReturn(mockedUnfiledRecordFolder);

        mockSiteAndUserData();
        EventResult result = loadUnfiledRecords.processEvent(mockedEvent, new StopWatch());
        verify(mockedFileFolderService, never()).deleteFolder(mockedFolder.getContext(), mockedFolder.getPath() + "/locked", false);
        verify(mockedTestFileService, times(1)).getFile();
        verify(mockedUnfiledRecordFolderAPI, never()).uploadRecord(any(UnfiledContainerChild.class), eq("folderId"), any(File.class));
        verify(mockedFileFolderService, never()).incrementFileCount(any(String.class), any(String.class), any(Long.class));

        assertEquals(false, result.isSuccess());
        DBObject data = (DBObject) result.getData();
        assertNotNull(data.get("error"));
        assertEquals("No test files exist for upload: mockedTestFileService", data.get("error"));
        assertEquals("aUser", data.get("username"));
        assertEquals(mockedFolder.getPath(), data.get("path"));
        assertNotNull(data.get("stack"));
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testUploadRecordsInUnfiledRecordFolder() throws Exception
    {
        int recordsToCreate = 3;
        loadUnfiledRecords.setEventNameUnfiledRecordsLoaded("unfiledRecordsLoaded");
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_RECORDS_TO_CREATE)).thenReturn(Integer.valueOf(recordsToCreate));
        when(mockedEvent.getData()).thenReturn(mockedData);

        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFolder.getId()).thenReturn("folderId");
        when(mockedFolder.getPath()).thenReturn("/aPath");
        when(mockedFolder.getContext()).thenReturn("someContext");
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(mockedFolder);
        when(mockedEvent.getSessionId()).thenReturn("someId");

        when(mockedRestApiFactory.getUnfiledContainersAPI(any(UserModel.class))).thenReturn(mockedUnfiledContainerAPI);
        when(mockedRestApiFactory.getUnfiledRecordFoldersAPI(any(UserModel.class))).thenReturn(mockedUnfiledRecordFolderAPI);

        FolderData mockedFolder1 = mock(FolderData.class);
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, UNFILED_RECORD_CONTAINER_PATH)).thenReturn(mockedFolder1);

        UnfiledContainerChild mockRecord1 = mock(UnfiledContainerChild.class);
        String recordId1 = "recordId1";
        String recordName1= "recordName1";
        when(mockRecord1.getId()).thenReturn(recordId1);
        when(mockRecord1.getName()).thenReturn(recordName1);

        UnfiledContainerChild mockRecord2 = mock(UnfiledContainerChild.class);
        String recordId2 = "recordId2";
        String recordName2= "recordName2";
        when(mockRecord2.getId()).thenReturn(recordId2);
        when(mockRecord2.getName()).thenReturn(recordName2);

        UnfiledContainerChild mockRecord3 = mock(UnfiledContainerChild.class);
        String recordId3 = "recordId3";
        String recordName3= "recordName3";
        when(mockRecord3.getId()).thenReturn(recordId3);
        when(mockRecord3.getName()).thenReturn(recordName3);

        when(mockedUnfiledRecordFolderAPI.uploadRecord(any(UnfiledContainerChild.class), eq("folderId"), any(File.class))).thenReturn(mockRecord1)
                                                                                                                           .thenReturn(mockRecord2)
                                                                                                                           .thenReturn(mockRecord3);

        File mockedFile = mock(File.class);
        when(mockedTestFileService.getFile()).thenReturn(mockedFile);

        mockSiteAndUserData();
        EventResult result = loadUnfiledRecords.processEvent(mockedEvent, new StopWatch());

        verify(mockedFileFolderService, times(1)).deleteFolder(mockedFolder.getContext(), mockedFolder.getPath() + "/locked", false);
        verify(mockedTestFileService, times(3)).getFile();
        verify(mockedUnfiledRecordFolderAPI, times(3)).uploadRecord(any(UnfiledContainerChild.class), eq("folderId"), any(File.class));
        verify(mockedFileFolderService, times(3)).incrementFileCount(any(String.class), any(String.class), any(Long.class));

        assertEquals(true, result.isSuccess());
        DBObject data = (DBObject) result.getData();
        assertEquals("Created " + recordsToCreate + " records.", data.get("msg"));
        assertEquals("/aPath", data.get(FIELD_PATH));
        assertEquals("aUser", data.get("username"));
        assertEquals(1, result.getNextEvents().size());
        Event event = result.getNextEvents().get(0);
        assertEquals("unfiledRecordsLoaded", event.getName());
        DBObject eventData = (DBObject) event.getData();
        assertEquals("someContext", eventData.get(FIELD_CONTEXT));
        assertEquals("/aPath", eventData.get(FIELD_PATH));
    }

    @Test
    public void testUploadRecordsInUnfiledContainer() throws Exception
    {
        int recordsToCreate = 3;
        loadUnfiledRecords.setEventNameUnfiledRecordsLoaded("unfiledRecordsLoaded");
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_RECORDS_TO_CREATE)).thenReturn(Integer.valueOf(recordsToCreate));
        when(mockedEvent.getData()).thenReturn(mockedData);

        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFolder.getId()).thenReturn("folderId");
        when(mockedFolder.getPath()).thenReturn("/aPath");
        when(mockedFolder.getContext()).thenReturn("someContext");
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(mockedFolder);
        when(mockedEvent.getSessionId()).thenReturn("someId");

        when(mockedRestApiFactory.getUnfiledContainersAPI(any(UserModel.class))).thenReturn(mockedUnfiledContainerAPI);
        when(mockedRestApiFactory.getUnfiledRecordFoldersAPI(any(UserModel.class))).thenReturn(mockedUnfiledRecordFolderAPI);

        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, UNFILED_RECORD_CONTAINER_PATH)).thenReturn(mockedFolder);

        UnfiledContainer mockedFilePlanComponent = mock(UnfiledContainer.class);
        when(mockedFilePlanComponent.getId()).thenReturn("folderId");
        when(mockedUnfiledContainerAPI.getUnfiledContainer("folderId")).thenReturn(mockedFilePlanComponent);

        UnfiledContainerChild mockRecord1 = mock(UnfiledContainerChild.class);
        String recordId1 = "recordId1";
        String recordName1= "recordName1";
        when(mockRecord1.getId()).thenReturn(recordId1);
        when(mockRecord1.getName()).thenReturn(recordName1);

        UnfiledContainerChild mockRecord2 = mock(UnfiledContainerChild.class);
        String recordId2 = "recordId2";
        String recordName2= "recordName2";
        when(mockRecord2.getId()).thenReturn(recordId2);
        when(mockRecord2.getName()).thenReturn(recordName2);

        UnfiledContainerChild mockRecord3 = mock(UnfiledContainerChild.class);
        String recordId3 = "recordId3";
        String recordName3= "recordName3";
        when(mockRecord3.getId()).thenReturn(recordId3);
        when(mockRecord3.getName()).thenReturn(recordName3);

        when(mockedUnfiledContainerAPI.uploadRecord(any(UnfiledContainerChild.class), eq("folderId"), any(File.class))).thenReturn(mockRecord1)
                                                                                                                       .thenReturn(mockRecord2)
                                                                                                                       .thenReturn(mockRecord3);

        File mockedFile = mock(File.class);
        when(mockedTestFileService.getFile()).thenReturn(mockedFile);

        mockSiteAndUserData();
        EventResult result = loadUnfiledRecords.processEvent(mockedEvent, new StopWatch());

        verify(mockedFileFolderService, times(1)).deleteFolder(mockedFolder.getContext(), mockedFolder.getPath() + "/locked", false);
        verify(mockedTestFileService, times(3)).getFile();
        verify(mockedUnfiledContainerAPI, times(3)).uploadRecord(any(UnfiledContainerChild.class), eq("folderId"), any(File.class));
        verify(mockedFileFolderService, times(3)).incrementFileCount(any(String.class), any(String.class), any(Long.class));

        assertEquals(true, result.isSuccess());
        DBObject data = (DBObject) result.getData();
        assertEquals("Created " + recordsToCreate + " records.", data.get("msg"));
        assertEquals("/aPath", data.get(FIELD_PATH));
        assertEquals("aUser", data.get("username"));
        assertEquals(1, result.getNextEvents().size());
        Event event = result.getNextEvents().get(0);
        assertEquals("unfiledRecordsLoaded", event.getName());
        DBObject eventData = (DBObject) event.getData();
        assertEquals("someContext", eventData.get(FIELD_CONTEXT));
        assertEquals("/aPath", eventData.get(FIELD_PATH));
    }

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
    }
}