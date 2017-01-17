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

package org.alfresco.bm.dataload.rm.fileplan;

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
import org.alfresco.bm.dataload.RMEventConstants;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.file.TestFileService;
import org.alfresco.bm.session.SessionService;
import org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponent;
import org.alfresco.rest.core.RestAPIFactory;
import org.alfresco.rest.rm.community.requests.igCoreAPI.FilePlanComponentAPI;
import org.alfresco.utility.model.UserModel;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for LoadRecordsUnitTest
 * @author Silviu Dinuta
 * @since 2.6
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class LoadRecordsUnitTest implements RMEventConstants
{
    @Mock
    private SessionService mockedSessionService;

    @Mock
    private FileFolderService mockedFileFolderService;

    @Mock
    private RestAPIFactory mockedRestApiFactory;

    @Mock
    private FilePlanComponentAPI mockedFilePlanComponentAPI;

    @Mock
    private TestFileService mockedTestFileService;

    @InjectMocks
    private LoadRecords loadRecords;

    @Test(expected=IllegalStateException.class)
    public void testWithNullEvent() throws Exception
    {
        loadRecords.processEvent(null, new StopWatch());
    }

    @Test(expected=IllegalStateException.class)
    public void testWithNullData() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        when(mockedEvent.getData()).thenReturn(null);
        loadRecords.processEvent(mockedEvent, new StopWatch());
    }

    @Test
    public void testWithNullContext() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn(null);
        when(mockedEvent.getData()).thenReturn(mockedData);
        EventResult result = loadRecords.processEvent(mockedEvent, new StopWatch());
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
        EventResult result = loadRecords.processEvent(mockedEvent, new StopWatch());
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
        EventResult result = loadRecords.processEvent(mockedEvent, new StopWatch());
        assertEquals(false, result.isSuccess());
        assertEquals("Request data not complete for records loading: " + mockedData, result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testWithNullSiteManager() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_RECORDS_TO_CREATE)).thenReturn(Integer.valueOf(0));
        when(mockedData.get(FIELD_SITE_MANAGER)).thenReturn(null);
        when(mockedEvent.getData()).thenReturn(mockedData);
        EventResult result = loadRecords.processEvent(mockedEvent, new StopWatch());
        assertEquals(false, result.isSuccess());
        assertEquals("Request data not complete for records loading: " + mockedData, result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testWithBlankSiteManager() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_RECORDS_TO_CREATE)).thenReturn(Integer.valueOf(0));
        when(mockedData.get(FIELD_SITE_MANAGER)).thenReturn("");
        when(mockedEvent.getData()).thenReturn(mockedData);
        EventResult result = loadRecords.processEvent(mockedEvent, new StopWatch());
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
        when(mockedData.get(FIELD_SITE_MANAGER)).thenReturn("aUser");
        when(mockedEvent.getData()).thenReturn(mockedData);
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(null);

        loadRecords.processEvent(mockedEvent, new StopWatch());
    }

    @Test
    public void testWithNullSessionID() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_RECORDS_TO_CREATE)).thenReturn(Integer.valueOf(0));
        when(mockedData.get(FIELD_SITE_MANAGER)).thenReturn("aUser");
        when(mockedEvent.getData()).thenReturn(mockedData);
        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(mockedFolder);
        when(mockedEvent.getSessionId()).thenReturn(null);

        EventResult result = loadRecords.processEvent(mockedEvent, new StopWatch());
        assertEquals(false, result.isSuccess());
        assertEquals("Load scheduling should create a session for each loader.", result.getData());
        assertEquals(0, result.getNextEvents().size());
    }
    @Test
    public void testLoadNoRecordsToCreate() throws Exception
    {
        int recordsToCreate = 0;
        loadRecords.setEventNameRecordsLoaded("recordsLoaded");
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_RECORDS_TO_CREATE)).thenReturn(Integer.valueOf(recordsToCreate));
        when(mockedData.get(FIELD_SITE_MANAGER)).thenReturn("aUser");
        when(mockedEvent.getData()).thenReturn(mockedData);

        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFolder.getId()).thenReturn("folderId");
        when(mockedFolder.getPath()).thenReturn("/aPath");
        when(mockedFolder.getContext()).thenReturn("someContext");
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(mockedFolder);
        when(mockedEvent.getSessionId()).thenReturn("someId");
        when(mockedRestApiFactory.getFilePlanComponentsAPI(any(UserModel.class))).thenReturn(mockedFilePlanComponentAPI);
        FilePlanComponent mockedFilePlanComponent = mock(FilePlanComponent.class);
        when(mockedFilePlanComponentAPI.getFilePlanComponent("folderId")).thenReturn(mockedFilePlanComponent);

        EventResult result = loadRecords.processEvent(mockedEvent, new StopWatch());
        verify(mockedFileFolderService, never()).deleteFolder(mockedFolder.getContext(), mockedFolder.getPath() + "/locked", false);
        verify(mockedTestFileService, never()).getFile();
        verify(mockedFilePlanComponentAPI, never()).createElectronicRecord(any(FilePlanComponent.class), any(File.class), eq("folderId"));

        verify(mockedFileFolderService, never()).incrementFileCount(any(String.class), any(String.class), any(Long.class));
        assertEquals(true, result.isSuccess());
        DBObject data = (DBObject) result.getData();
        assertEquals("Created " + recordsToCreate + " records.", data.get("msg"));
        assertEquals("/aPath", data.get(FIELD_PATH));
        assertEquals("aUser", data.get("username"));
        assertEquals(1, result.getNextEvents().size());
        Event event = result.getNextEvents().get(0);
        assertEquals("recordsLoaded", event.getName());
        DBObject eventData = (DBObject) event.getData();
        assertEquals("someContext", eventData.get(FIELD_CONTEXT));
        assertEquals("/aPath", eventData.get(FIELD_PATH));
    }

    @Test
    public void testLoadRecordsWithRestAPiException() throws Exception
    {
        int recordsToCreate = 3;
        loadRecords.setEventNameRecordsLoaded("recordsLoaded");
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_RECORDS_TO_CREATE)).thenReturn(Integer.valueOf(recordsToCreate));
        when(mockedData.get(FIELD_SITE_MANAGER)).thenReturn("aUser");
        when(mockedEvent.getData()).thenReturn(mockedData);

        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFolder.getId()).thenReturn("folderId");
        when(mockedFolder.getPath()).thenReturn("/aPath");
        when(mockedFolder.getContext()).thenReturn("someContext");
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(mockedFolder);
        when(mockedEvent.getSessionId()).thenReturn("someId");
        when(mockedRestApiFactory.getFilePlanComponentsAPI(any(UserModel.class))).thenReturn(mockedFilePlanComponentAPI);
        FilePlanComponent mockedFilePlanComponent = mock(FilePlanComponent.class);
        when(mockedFilePlanComponentAPI.getFilePlanComponent("folderId")).thenReturn(mockedFilePlanComponent);
        File mockedFile = mock(File.class);
        when(mockedTestFileService.getFile()).thenReturn(mockedFile);
        //TODO uncomment this and remove createFilePlanComponent call when RM-4564 issue is fixed
//        Mockito.doThrow(new Exception("someError")).when(mockedFilePlanComponentAPI).createElectronicRecord(any(FilePlanComponent.class), any(File.class), any(String.class));
        Mockito.doThrow(new Exception("someError")).when(mockedFilePlanComponentAPI).createFilePlanComponent(any(FilePlanComponent.class), any(String.class));

        EventResult result = loadRecords.processEvent(mockedEvent, new StopWatch());
        verify(mockedFileFolderService, never()).deleteFolder(mockedFolder.getContext(), mockedFolder.getPath() + "/locked", false);

        //TODO uncomment this when RM-4564 issue is fixed
//        verify(mockedTestFileService, times(1)).getFile();
        verify(mockedFilePlanComponentAPI, never()).createElectronicRecord(any(FilePlanComponent.class), any(File.class), eq("folderId"));

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

    //TODO uncomment this when RM-4564 issue is fixed
//    @Test
//    public void testLoadRecordsWithNoFileException() throws Exception
//    {
//        int recordsToCreate = 3;
//        loadRecords.setEventNameRecordsLoaded("recordsLoaded");
//        Event mockedEvent = mock(Event.class);
//        DBObject mockedData = mock(DBObject.class);
//        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
//        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
//        when(mockedData.get(FIELD_RECORDS_TO_CREATE)).thenReturn(Integer.valueOf(recordsToCreate));
//        when(mockedData.get(FIELD_SITE_MANAGER)).thenReturn("aUser");
//        when(mockedEvent.getData()).thenReturn(mockedData);
//
//        FolderData mockedFolder = mock(FolderData.class);
//        when(mockedFolder.getId()).thenReturn("folderId");
//        when(mockedFolder.getPath()).thenReturn("/aPath");
//        when(mockedFolder.getContext()).thenReturn("someContext");
//        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(mockedFolder);
//        when(mockedEvent.getSessionId()).thenReturn("someId");
//        when(mockedRestApiFactory.getFilePlanComponentsAPI(any(UserModel.class))).thenReturn(mockedFilePlanComponentAPI);
//        FilePlanComponent mockedFilePlanComponent = mock(FilePlanComponent.class);
//        when(mockedFilePlanComponentAPI.getFilePlanComponent("folderId")).thenReturn(mockedFilePlanComponent);
//
//
//        EventResult result = loadRecords.processEvent(mockedEvent, new StopWatch());
//        verify(mockedFileFolderService, never()).deleteFolder(mockedFolder.getContext(), mockedFolder.getPath() + "/locked", false);
//        verify(mockedTestFileService, times(1)).getFile();
//        verify(mockedFilePlanComponentAPI, never()).createElectronicRecord(any(FilePlanComponent.class), any(File.class), eq("folderId"));
//        verify(mockedFileFolderService, never()).incrementFileCount(any(String.class), any(String.class), any(Long.class));
//
//        assertEquals(false, result.isSuccess());
//        DBObject data = (DBObject) result.getData();
//        assertNotNull(data.get("error"));
//        assertEquals("No test files exist for upload: mockedTestFileService", data.get("error"));
//        assertEquals("aUser", data.get("username"));
//        assertEquals(mockedFolder.getPath(), data.get("path"));
//        assertNotNull(data.get("stack"));
//        assertEquals(0, result.getNextEvents().size());
//    }

    @Test
    public void testUploadRecords() throws Exception
    {
        int recordsToCreate = 3;
        loadRecords.setEventNameRecordsLoaded("recordsLoaded");
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_RECORDS_TO_CREATE)).thenReturn(Integer.valueOf(recordsToCreate));
        when(mockedData.get(FIELD_SITE_MANAGER)).thenReturn("aUser");
        when(mockedEvent.getData()).thenReturn(mockedData);

        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFolder.getId()).thenReturn("folderId");
        when(mockedFolder.getPath()).thenReturn("/aPath");
        when(mockedFolder.getContext()).thenReturn("someContext");
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(mockedFolder);
        when(mockedEvent.getSessionId()).thenReturn("someId");
        when(mockedRestApiFactory.getFilePlanComponentsAPI(any(UserModel.class))).thenReturn(mockedFilePlanComponentAPI);
        FilePlanComponent mockedFilePlanComponent = mock(FilePlanComponent.class);
        when(mockedFilePlanComponent.getId()).thenReturn("folderId");
        when(mockedFilePlanComponentAPI.getFilePlanComponent("folderId")).thenReturn(mockedFilePlanComponent);
        File mockedFile = mock(File.class);
        when(mockedTestFileService.getFile()).thenReturn(mockedFile);

        EventResult result = loadRecords.processEvent(mockedEvent, new StopWatch());

        verify(mockedFileFolderService, times(1)).deleteFolder(mockedFolder.getContext(), mockedFolder.getPath() + "/locked", false);
        //TODO uncomment this when RM-4564 issue is fixed
//        verify(mockedTestFileService, times(3)).getFile();
//        verify(mockedFilePlanComponentAPI, times(3)).createElectronicRecord(any(FilePlanComponent.class), any(File.class), eq("folderId"));
//        verify(mockedFileFolderService, times(3)).incrementFileCount(any(String.class), any(String.class), any(Long.class));

        assertEquals(true, result.isSuccess());
        DBObject data = (DBObject) result.getData();
        assertEquals("Created " + recordsToCreate + " records.", data.get("msg"));
        assertEquals("/aPath", data.get(FIELD_PATH));
        assertEquals("aUser", data.get("username"));
        assertEquals(1, result.getNextEvents().size());
        Event event = result.getNextEvents().get(0);
        assertEquals("recordsLoaded", event.getName());
        DBObject eventData = (DBObject) event.getData();
        assertEquals("someContext", eventData.get(FIELD_CONTEXT));
        assertEquals("/aPath", eventData.get(FIELD_PATH));
    }
}