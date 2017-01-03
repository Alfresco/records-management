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

import java.util.UUID;

import com.mongodb.DBObject;

import org.alfresco.bm.cm.FileFolderService;
import org.alfresco.bm.cm.FolderData;
import org.alfresco.bm.dataload.RMEventConstants;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.session.SessionService;
import org.alfresco.rest.core.RestAPIFactory;
import org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponent;
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
 * Unit tests for LoadUnfiledRecordFolders
 * @author Silviu Dinuta
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class LoadUnfiledRecordFoldersUnitTest implements RMEventConstants
{
    @Mock
    private SessionService mockedSessionService;

    @Mock
    private FileFolderService mockedFileFolderService;

    @Mock
    private RestAPIFactory mockedRestApiFactory;

    @Mock
    private FilePlanComponentAPI mockedFilePlanComponentAPI;

    @InjectMocks
    private LoadUnfiledRecordFolders loadUnfiledRecordFolders;

    @Test(expected=IllegalStateException.class)
    public void testWithNullEvent() throws Exception
    {
        loadUnfiledRecordFolders.processEvent(null, new StopWatch());
    }

    @Test(expected=IllegalStateException.class)
    public void testWithNullData() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        when(mockedEvent.getData()).thenReturn(null);
        loadUnfiledRecordFolders.processEvent(mockedEvent, new StopWatch());
    }

    @Test
    public void testWithNullContext() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn(null);
        when(mockedEvent.getData()).thenReturn(mockedData);
        EventResult result = loadUnfiledRecordFolders.processEvent(mockedEvent, new StopWatch());
        assertEquals(false, result.isSuccess());
        assertEquals("Request data not complete for folder loading: " + mockedData, result.getData());
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
        EventResult result = loadUnfiledRecordFolders.processEvent(mockedEvent, new StopWatch());
        assertEquals(false, result.isSuccess());
        assertEquals("Request data not complete for folder loading: " + mockedData, result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testWithNullRootFoldersToCreate() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_UNFILED_ROOT_FOLDERS_TO_CREATE)).thenReturn(null);
        when(mockedEvent.getData()).thenReturn(mockedData);
        EventResult result = loadUnfiledRecordFolders.processEvent(mockedEvent, new StopWatch());
        assertEquals(false, result.isSuccess());
        assertEquals("Request data not complete for folder loading: " + mockedData, result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testWithNullFoldersToCreate() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_UNFILED_ROOT_FOLDERS_TO_CREATE)).thenReturn(Integer.valueOf(0));
        when(mockedData.get(FIELD_UNFILED_FOLDERS_TO_CREATE)).thenReturn(null);
        when(mockedEvent.getData()).thenReturn(mockedData);
        EventResult result = loadUnfiledRecordFolders.processEvent(mockedEvent, new StopWatch());
        assertEquals(false, result.isSuccess());
        assertEquals("Request data not complete for folder loading: " + mockedData, result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testWithNullSiteManager() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_UNFILED_ROOT_FOLDERS_TO_CREATE)).thenReturn(Integer.valueOf(0));
        when(mockedData.get(FIELD_UNFILED_FOLDERS_TO_CREATE)).thenReturn(Integer.valueOf(0));
        when(mockedData.get(FIELD_SITE_MANAGER)).thenReturn(null);
        when(mockedEvent.getData()).thenReturn(mockedData);
        EventResult result = loadUnfiledRecordFolders.processEvent(mockedEvent, new StopWatch());
        assertEquals(false, result.isSuccess());
        assertEquals("Request data not complete for folder loading: " + mockedData, result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testWithBlankSiteManager() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_UNFILED_ROOT_FOLDERS_TO_CREATE)).thenReturn(Integer.valueOf(0));
        when(mockedData.get(FIELD_UNFILED_FOLDERS_TO_CREATE)).thenReturn(Integer.valueOf(0));
        when(mockedData.get(FIELD_SITE_MANAGER)).thenReturn("");
        when(mockedEvent.getData()).thenReturn(mockedData);
        EventResult result = loadUnfiledRecordFolders.processEvent(mockedEvent, new StopWatch());
        assertEquals(false, result.isSuccess());
        assertEquals("Request data not complete for folder loading: " + mockedData, result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test(expected=IllegalStateException.class)
    public void testInexistentFolderForContextAndPath() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_UNFILED_ROOT_FOLDERS_TO_CREATE)).thenReturn(Integer.valueOf(0));
        when(mockedData.get(FIELD_UNFILED_FOLDERS_TO_CREATE)).thenReturn(Integer.valueOf(0));
        when(mockedData.get(FIELD_SITE_MANAGER)).thenReturn("aUser");
        when(mockedEvent.getData()).thenReturn(mockedData);
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(null);

        loadUnfiledRecordFolders.processEvent(mockedEvent, new StopWatch());
    }

    @Test
    public void testWithNullSessionID() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_UNFILED_ROOT_FOLDERS_TO_CREATE)).thenReturn(Integer.valueOf(0));
        when(mockedData.get(FIELD_UNFILED_FOLDERS_TO_CREATE)).thenReturn(Integer.valueOf(0));
        when(mockedData.get(FIELD_SITE_MANAGER)).thenReturn("aUser");
        when(mockedEvent.getData()).thenReturn(mockedData);
        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(mockedFolder);
        when(mockedEvent.getSessionId()).thenReturn(null);

        EventResult result = loadUnfiledRecordFolders.processEvent(mockedEvent, new StopWatch());
        assertEquals(false, result.isSuccess());
        assertEquals("Load scheduling should create a session for each loader.", result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testLoadNoUnfiledRecordFoldersToCreate() throws Exception
    {
        int rootUnfiledRecordFolders = 0;
        int unfiledRecordFolders = 0;
        loadUnfiledRecordFolders.setEventNameUnfiledRecordFoldersLoaded("unfiledRecordFoldersLoaded");
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_UNFILED_ROOT_FOLDERS_TO_CREATE)).thenReturn(Integer.valueOf(rootUnfiledRecordFolders));
        when(mockedData.get(FIELD_UNFILED_FOLDERS_TO_CREATE)).thenReturn(Integer.valueOf(unfiledRecordFolders));
        when(mockedData.get(FIELD_SITE_MANAGER)).thenReturn("aUser");
        when(mockedEvent.getData()).thenReturn(mockedData);

        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFolder.getId()).thenReturn("folderId");
        when(mockedFolder.getPath()).thenReturn("/aPath");
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(mockedFolder);
        when(mockedEvent.getSessionId()).thenReturn("someId");
        when(mockedRestApiFactory.getFilePlanComponentsAPI(any(UserModel.class))).thenReturn(mockedFilePlanComponentAPI);
        FilePlanComponent mockedFilePlanComponent = mock(FilePlanComponent.class);
        when(mockedFilePlanComponentAPI.getFilePlanComponent("folderId", "include=path")).thenReturn(mockedFilePlanComponent);

        EventResult result = loadUnfiledRecordFolders.processEvent(mockedEvent, new StopWatch());
        verify(mockedFileFolderService, never()).createNewFolder(any(String.class), any(String.class), any(String.class));
        verify(mockedFileFolderService, never()).incrementFolderCount(any(String.class), any(String.class), any(Long.class));
        assertEquals(true, result.isSuccess());
        DBObject data = (DBObject) result.getData();
        assertEquals("Created " + rootUnfiledRecordFolders + " root unfiled record folders and " + unfiledRecordFolders + " unfiled folders children.", data.get("msg"));
        assertEquals("/aPath", data.get(FIELD_PATH));
        assertEquals("aUser", data.get("username"));
        assertEquals(1, result.getNextEvents().size());
    }

    @Test
    public void testLoadRootUnfiledRecordFoldersWithExceptionOnRestApi() throws Exception
    {
        int rootUnfiledRecordFolders = 3;
        int unfiledRecordFolders = 0;
        loadUnfiledRecordFolders.setEventNameUnfiledRecordFoldersLoaded("unfiledRecordFoldersLoaded");
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_UNFILED_ROOT_FOLDERS_TO_CREATE)).thenReturn(Integer.valueOf(rootUnfiledRecordFolders));
        when(mockedData.get(FIELD_UNFILED_FOLDERS_TO_CREATE)).thenReturn(Integer.valueOf(unfiledRecordFolders));
        when(mockedData.get(FIELD_SITE_MANAGER)).thenReturn("aUser");
        when(mockedEvent.getData()).thenReturn(mockedData);

        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFolder.getId()).thenReturn("folderId");
        when(mockedFolder.getPath()).thenReturn("/aPath");
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(mockedFolder);
        when(mockedEvent.getSessionId()).thenReturn("someId");
        when(mockedRestApiFactory.getFilePlanComponentsAPI(any(UserModel.class))).thenReturn(mockedFilePlanComponentAPI);
        FilePlanComponent mockedFilePlanComponent = mock(FilePlanComponent.class);
        when(mockedFilePlanComponentAPI.getFilePlanComponent("folderId", "include=path")).thenReturn(mockedFilePlanComponent);

        Mockito.doThrow(new Exception("someError")).when(mockedFilePlanComponentAPI).createFilePlanComponent(any(FilePlanComponent.class), any(String.class), eq("include=path"));
        EventResult result = loadUnfiledRecordFolders.processEvent(mockedEvent, new StopWatch());
        verify(mockedFileFolderService, never()).createNewFolder(any(String.class), any(String.class), any(String.class));
        verify(mockedFileFolderService, never()).incrementFolderCount(any(String.class), any(String.class), any(Long.class));
        assertEquals(false, result.isSuccess());
        DBObject data = (DBObject) result.getData();
        assertNotNull(data.get("error"));
        assertEquals("aUser", data.get("username"));
        assertEquals(mockedFolder.getPath(), data.get("path"));
        assertNotNull(data.get("stack"));
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testLoadRootUnfiledRecordFolders() throws Exception
    {
        int rootUnfiledRecordFolders = 3;
        int unfiledRecordFolders = 0;
        loadUnfiledRecordFolders.setEventNameUnfiledRecordFoldersLoaded("unfiledRecordFoldersLoaded");
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_UNFILED_ROOT_FOLDERS_TO_CREATE)).thenReturn(Integer.valueOf(rootUnfiledRecordFolders));
        when(mockedData.get(FIELD_UNFILED_FOLDERS_TO_CREATE)).thenReturn(Integer.valueOf(unfiledRecordFolders));
        when(mockedData.get(FIELD_SITE_MANAGER)).thenReturn("aUser");
        when(mockedEvent.getData()).thenReturn(mockedData);

        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFolder.getId()).thenReturn("folderId");
        when(mockedFolder.getPath()).thenReturn("/aPath");
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(mockedFolder);
        when(mockedEvent.getSessionId()).thenReturn("someId");
        when(mockedRestApiFactory.getFilePlanComponentsAPI(any(UserModel.class))).thenReturn(mockedFilePlanComponentAPI);
        FilePlanComponent mockedFilePlanComponent = mock(FilePlanComponent.class);
        when(mockedFilePlanComponent.getId()).thenReturn("folderId");
        when(mockedFilePlanComponentAPI.getFilePlanComponent("folderId", "include=path")).thenReturn(mockedFilePlanComponent);
        FilePlanComponent mockedChildFilePlanComponent = mock(FilePlanComponent.class);
        when(mockedChildFilePlanComponent.getId()).thenReturn(UUID.randomUUID().toString());
        when(mockedFilePlanComponentAPI.createFilePlanComponent(any(FilePlanComponent.class), eq("folderId"), eq("include=path"))).thenReturn(mockedChildFilePlanComponent);

        EventResult result = loadUnfiledRecordFolders.processEvent(mockedEvent, new StopWatch());
        verify(mockedFileFolderService, times(rootUnfiledRecordFolders)).createNewFolder(any(String.class), any(String.class), any(String.class));
        verify(mockedFileFolderService, times(1)).incrementFolderCount(any(String.class), any(String.class), any(Long.class));
        assertEquals(true, result.isSuccess());
        DBObject data = (DBObject) result.getData();
        assertEquals("Created " + rootUnfiledRecordFolders + " root unfiled record folders and " + unfiledRecordFolders + " unfiled folders children.", data.get("msg"));
        assertEquals("/aPath", data.get(FIELD_PATH));
        assertEquals("aUser", data.get("username"));
        assertEquals(1, result.getNextEvents().size());
    }

    @Test
    public void testLoadUnfiledRecordFoldersChildren() throws Exception
    {
        int rootUnfiledRecordFolders = 0;
        int unfiledRecordFolders = 4;
        loadUnfiledRecordFolders.setEventNameUnfiledRecordFoldersLoaded("unfiledRecordFoldersLoaded");
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_UNFILED_ROOT_FOLDERS_TO_CREATE)).thenReturn(Integer.valueOf(rootUnfiledRecordFolders));
        when(mockedData.get(FIELD_UNFILED_FOLDERS_TO_CREATE)).thenReturn(Integer.valueOf(unfiledRecordFolders));
        when(mockedData.get(FIELD_SITE_MANAGER)).thenReturn("aUser");
        when(mockedEvent.getData()).thenReturn(mockedData);

        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFolder.getId()).thenReturn("folderId");
        when(mockedFolder.getPath()).thenReturn("/aPath");
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(mockedFolder);
        when(mockedEvent.getSessionId()).thenReturn("someId");
        when(mockedRestApiFactory.getFilePlanComponentsAPI(any(UserModel.class))).thenReturn(mockedFilePlanComponentAPI);
        FilePlanComponent mockedFilePlanComponent = mock(FilePlanComponent.class);
        when(mockedFilePlanComponent.getId()).thenReturn("folderId");
        when(mockedFilePlanComponentAPI.getFilePlanComponent("folderId", "include=path")).thenReturn(mockedFilePlanComponent);
        FilePlanComponent mockedChildFilePlanComponent = mock(FilePlanComponent.class);
        when(mockedChildFilePlanComponent.getId()).thenReturn(UUID.randomUUID().toString());
        when(mockedFilePlanComponentAPI.createFilePlanComponent(any(FilePlanComponent.class), eq("folderId"), eq("include=path"))).thenReturn(mockedChildFilePlanComponent);

        EventResult result = loadUnfiledRecordFolders.processEvent(mockedEvent, new StopWatch());
        verify(mockedFileFolderService, times(unfiledRecordFolders)).createNewFolder(any(String.class), any(String.class), any(String.class));
        verify(mockedFileFolderService, times(1)).incrementFolderCount(any(String.class), any(String.class), any(Long.class));
        assertEquals(true, result.isSuccess());
        DBObject data = (DBObject) result.getData();
        assertEquals("Created " + rootUnfiledRecordFolders + " root unfiled record folders and " + unfiledRecordFolders + " unfiled folders children.", data.get("msg"));
        assertEquals("/aPath", data.get(FIELD_PATH));
        assertEquals("aUser", data.get("username"));
        assertEquals(1, result.getNextEvents().size());
    }
}
