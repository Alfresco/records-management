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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.alfresco.bm.cm.FolderData;
import org.alfresco.bm.dataload.LoadSingleComponentUnitTest;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.rest.rm.community.model.unfiledcontainer.UnfiledContainer;
import org.alfresco.rest.rm.community.model.unfiledcontainer.UnfiledContainerChild;
import org.alfresco.utility.model.UserModel;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.mongodb.DBObject;

/**
 * Unit tests for load unfiled record operation from LoadSingleComponent
 *
 * @author Silviu Dinuta
 * @since 2.6
 */
@RunWith(MockitoJUnitRunner.class)
public class LoadUnfiledRecordUnitTest extends LoadSingleComponentUnitTest
{
    private static final String EVENT_UNFILED_RECORD_LOADED = "testUnfiledRecordLoaded";

    @Test
    public void testLoadUnfiledRecordOperationInUnfiledContainerWithRestAPiException() throws Exception
    {
        loadSingleComponent.setEventNameComplete(EVENT_UNFILED_RECORD_LOADED);
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_LOAD_OPERATION)).thenReturn(LOAD_UNFILED_RECORD_OPERATION);
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
        //TODO uncomment this and remove createFilePlanComponent call when RM-4564 issue is fixed
//        Mockito.doThrow(new Exception("someError")).when(mockedUnfiledContainerAPI).uploadRecord(any(UnfiledContainerChild.class), any(String.class), any(File.class));
        Mockito.doThrow(new Exception("someError")).when(mockedUnfiledContainerAPI).createUnfiledContainerChild(any(UnfiledContainerChild.class), any(String.class));

        mockSiteAndUserData();
        EventResult result = loadSingleComponent.processEvent(mockedEvent, new StopWatch());
        //TODO uncomment this when RM-4564 issue is fixed
//        verify(mockedTestFileService, times(1)).getFile();
//        verify(mockedUnfiledContainerAPI, never()).uploadRecord(any(UnfiledContainerChild.class), eq("folderId"), any(File.class));

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
    public void testLoadUnfiledRecordOperationInUnfiledRecordFolderWithRestAPiException() throws Exception
    {
        loadSingleComponent.setEventNameComplete(EVENT_UNFILED_RECORD_LOADED);
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_LOAD_OPERATION)).thenReturn(LOAD_UNFILED_RECORD_OPERATION);
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
        //TODO uncomment this and remove createFilePlanComponent call when RM-4564 issue is fixed
//        Mockito.doThrow(new Exception("someError")).when(mockedUnfiledRecordFolderAPI).uploadRecord(any(UnfiledContainerChild.class), any(String.class), any(File.class));
        Mockito.doThrow(new Exception("someError")).when(mockedUnfiledRecordFolderAPI).createUnfiledRecordFolderChild(any(UnfiledContainerChild.class), any(String.class));

        mockSiteAndUserData();
        EventResult result = loadSingleComponent.processEvent(mockedEvent, new StopWatch());

        //TODO uncomment this when RM-4564 issue is fixed
//        verify(mockedTestFileService, times(1)).getFile();
//        verify(mockedUnfiledRecordFolderAPI, never()).uploadRecord(any(UnfiledContainerChild.class), eq("folderId"), any(File.class));

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
//    public void testLoadUnfiledRecordOperationInUnfiledContainerWithNoFileException() throws Exception
//    {
//        int recordsToCreate = 3;
//        loasSingleComponent.setEventNameComplete(EVENT_UNFILED_RECORD_LOADED);
//        Event mockedEvent = mock(Event.class);
//        DBObject mockedData = mock(DBObject.class);
//        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
//        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
//        when(mockedData.get(FIELD_LOAD_OPERATION)).thenReturn(LOAD_UNFILED_RECORD_OPERATION);
//        when(mockedEvent.getData()).thenReturn(mockedData);
//
//        FolderData mockedFolder = mock(FolderData.class);
//        when(mockedFolder.getId()).thenReturn("folderId");
//        when(mockedFolder.getPath()).thenReturn("/aPath");
//        when(mockedFolder.getContext()).thenReturn("someContext");
//        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(mockedFolder);
//        when(mockedEvent.getSessionId()).thenReturn("someId");
//
//        when(mockedRestApiFactory.getUnfiledContainersAPI(any(UserModel.class))).thenReturn(mockedUnfiledContainerAPI);
//        when(mockedRestApiFactory.getUnfiledRecordFoldersAPI(any(UserModel.class))).thenReturn(mockedUnfiledRecordFolderAPI);
//
//        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, UNFILED_RECORD_CONTAINER_PATH)).thenReturn(mockedFolder);
//
//        UnfiledContainer mockedFilePlanComponent = mock(UnfiledContainer.class);
//        when(mockedUnfiledContainerAPI.getUnfiledContainer("folderId")).thenReturn(mockedFilePlanComponent);
//
//        mockSiteAndUserData();
//        EventResult result = loasSingleComponent.processEvent(mockedEvent, new StopWatch());
//        verify(mockedFileFolderService, never()).deleteFolder(mockedFolder.getContext(), mockedFolder.getPath() + "/locked", false);
//        verify(mockedTestFileService, times(1)).getFile();
//        verify(mockedUnfiledContainerAPI, never()).uploadRecord(any(UnfiledContainerChild.class), eq("folderId"), any(File.class));
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

    //TODO uncomment this when RM-4564 issue is fixed
//    @Test
//    public void testLoadUnfiledRecordOperationInUnfiledRecordFolderWithNoFileException() throws Exception
//    {
//        int recordsToCreate = 3;
//        loasSingleComponent.setEventNameComplete(EVENT_UNFILED_RECORD_LOADED);
//        Event mockedEvent = mock(Event.class);
//        DBObject mockedData = mock(DBObject.class);
//        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
//        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
//        when(mockedData.get(FIELD_LOAD_OPERATION)).thenReturn(LOAD_UNFILED_RECORD_OPERATION);
//        when(mockedEvent.getData()).thenReturn(mockedData);
//
//        FolderData mockedFolder = mock(FolderData.class);
//        when(mockedFolder.getId()).thenReturn("folderId");
//        when(mockedFolder.getPath()).thenReturn("/aPath");
//        when(mockedFolder.getContext()).thenReturn("someContext");
//        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(mockedFolder);
//        when(mockedEvent.getSessionId()).thenReturn("someId");
//
//        when(mockedRestApiFactory.getUnfiledContainersAPI(any(UserModel.class))).thenReturn(mockedUnfiledContainerAPI);
//        when(mockedRestApiFactory.getUnfiledRecordFoldersAPI(any(UserModel.class))).thenReturn(mockedUnfiledRecordFolderAPI);
//
//        FolderData mockedFolder1 = mock(FolderData.class);
//        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, UNFILED_RECORD_CONTAINER_PATH)).thenReturn(mockedFolder1);
//
//        UnfiledContainerChild mockedFilePlanComponent = mock(UnfiledContainerChild.class);
//        when(mockedUnfiledRecordFolderAPI.getUnfiledRecordFolder("folderId")).thenReturn(mockedFilePlanComponent);
//
//        mockSiteAndUserData();
//        EventResult result = loasSingleComponent.processEvent(mockedEvent, new StopWatch());
//        verify(mockedFileFolderService, never()).deleteFolder(mockedFolder.getContext(), mockedFolder.getPath() + "/locked", false);
//        verify(mockedTestFileService, times(1)).getFile();
//        verify(mockedUnfiledRecordFolderAPI, never()).uploadRecord(any(UnfiledContainerChild.class), eq("folderId"), any(File.class));
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
    public void testLoadUnfiledRecordOperationInUnfiledRecordFolder() throws Exception
    {
        loadSingleComponent.setEventNameComplete(EVENT_UNFILED_RECORD_LOADED);
        loadSingleComponent.setDelay(1L);
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_LOAD_OPERATION)).thenReturn(LOAD_UNFILED_RECORD_OPERATION);
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

        when(mockedUnfiledRecordFolderAPI.createUnfiledRecordFolderChild(any(UnfiledContainerChild.class), eq("folderId"))).thenReturn(mockRecord1)
                                                                                                                           .thenReturn(mockRecord2)
                                                                                                                           .thenReturn(mockRecord3);

        File mockedFile = mock(File.class);
        when(mockedTestFileService.getFile()).thenReturn(mockedFile);

        mockSiteAndUserData();
        EventResult result = loadSingleComponent.processEvent(mockedEvent, new StopWatch());
        //TODO uncomment this when RM-4564 issue is fixed
//        verify(mockedTestFileService, times(3)).getFile();
//        verify(mockedUnfiledRecordFolderAPI, times(3)).uploadRecord(any(UnfiledContainerChild.class), eq("folderId"), any(File.class));
//        verify(mockedFileFolderService, times(3)).incrementFileCount(any(String.class), any(String.class), any(Long.class));

        assertEquals(true, result.isSuccess());
        DBObject data = (DBObject) result.getData();
        assertEquals("Created 1 record.", data.get("msg"));
        assertEquals("/aPath", data.get(FIELD_PATH));
        assertEquals("aUser", data.get("username"));
        assertEquals(1, result.getNextEvents().size());
        Event event = result.getNextEvents().get(0);
        assertEquals(EVENT_UNFILED_RECORD_LOADED, event.getName());
        DBObject eventData = (DBObject) event.getData();
        assertEquals("someContext", eventData.get(FIELD_CONTEXT));
        assertEquals("/aPath", eventData.get(FIELD_PATH));
    }

    @Test
    public void testLoadUnfiledRecordOperationInUnfiledRecordContainer() throws Exception
    {
        loadSingleComponent.setEventNameComplete(EVENT_UNFILED_RECORD_LOADED);
        loadSingleComponent.setDelay(1L);
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_LOAD_OPERATION)).thenReturn(LOAD_UNFILED_RECORD_OPERATION);
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

        when(mockedUnfiledContainerAPI.createUnfiledContainerChild(any(UnfiledContainerChild.class), eq("folderId"))).thenReturn(mockRecord1)
                                                                                                                     .thenReturn(mockRecord2)
                                                                                                                     .thenReturn(mockRecord3);

        File mockedFile = mock(File.class);
        when(mockedTestFileService.getFile()).thenReturn(mockedFile);

        mockSiteAndUserData();
        EventResult result = loadSingleComponent.processEvent(mockedEvent, new StopWatch());

        //TODO uncomment this when RM-4564 issue is fixed
//        verify(mockedTestFileService, times(3)).getFile();
//        verify(mockedUnfiledContainerAPI, times(3)).uploadRecord(any(UnfiledContainerChild.class), eq("folderId"), any(File.class));
//        verify(mockedFileFolderService, times(3)).incrementFileCount(any(String.class), any(String.class), any(Long.class));

        assertEquals(true, result.isSuccess());
        DBObject data = (DBObject) result.getData();
        assertEquals("Created 1 record.", data.get("msg"));
        assertEquals("/aPath", data.get(FIELD_PATH));
        assertEquals("aUser", data.get("username"));
        assertEquals(1, result.getNextEvents().size());
        Event event = result.getNextEvents().get(0);
        assertEquals(EVENT_UNFILED_RECORD_LOADED, event.getName());
        DBObject eventData = (DBObject) event.getData();
        assertEquals("someContext", eventData.get(FIELD_CONTEXT));
        assertEquals("/aPath", eventData.get(FIELD_PATH));
    }
}
