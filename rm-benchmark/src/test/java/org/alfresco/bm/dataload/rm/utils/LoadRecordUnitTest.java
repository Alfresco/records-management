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
import org.alfresco.rest.rm.community.model.record.Record;
import org.alfresco.rest.rm.community.model.recordfolder.RecordFolder;
import org.alfresco.utility.model.UserModel;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.mongodb.DBObject;

/**
 * Unit tests for load record operation from LoadSingleComponent
 *
 * @author Silviu Dinuta
 * @since 2.6
 */
@RunWith(MockitoJUnitRunner.class)
public class LoadRecordUnitTest extends LoadSingleComponentUnitTest
{
    @Test
    public void testLoadRecordOperationWithRestAPiException() throws Exception
    {
        loadSingleComponent.setEventNameComplete("recordLoaded");
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_LOAD_OPERATION)).thenReturn(LOAD_RECORD_OPERATION);
        when(mockedEvent.getData()).thenReturn(mockedData);

        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFolder.getId()).thenReturn("folderId");
        when(mockedFolder.getPath()).thenReturn("/aPath");
        when(mockedFolder.getContext()).thenReturn("someContext");
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(mockedFolder);
        when(mockedEvent.getSessionId()).thenReturn("someId");

        when(mockedRestApiFactory.getRecordFolderAPI(any(UserModel.class))).thenReturn(mockedRecordFolderAPI);

        RecordFolder mockedFilePlanComponent = mock(RecordFolder.class);
        when(mockedRecordFolderAPI.getRecordFolder("folderId")).thenReturn(mockedFilePlanComponent);

        File mockedFile = mock(File.class);
        when(mockedTestFileService.getFile()).thenReturn(mockedFile);
        //TODO uncomment this and remove createFilePlanComponent call when RM-4564 issue is fixed
//        Mockito.doThrow(new Exception("someError")).when(mockedRecordFolderAPI).createRecord(any(Record.class), any(String.class), any(File.class));
        Mockito.doThrow(new Exception("someError")).when(mockedRecordFolderAPI).createRecord(any(Record.class), any(String.class));

        mockSiteAndUserData();
        EventResult result = loadSingleComponent.processEvent(mockedEvent, new StopWatch());

        //TODO uncomment this when RM-4564 issue is fixed
//        verify(mockedTestFileService, times(1)).getFile();
        verify(mockedRecordFolderAPI, never()).createRecord(any(Record.class), eq("folderId"), any(File.class));

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
//    public void testLoadRecordOperationWithNoFileException() throws Exception
//    {
//        loadSingleComponent.setEventNameComplete("recordLoaded");
//        Event mockedEvent = mock(Event.class);
//        DBObject mockedData = mock(DBObject.class);
//        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
//        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
//        when(mockedData.get(FIELD_LOAD_OPERATION)).thenReturn(LOAD_RECORD_OPERATION);
//        when(mockedEvent.getData()).thenReturn(mockedData);
//
//        FolderData mockedFolder = mock(FolderData.class);
//        when(mockedFolder.getId()).thenReturn("folderId");
//        when(mockedFolder.getPath()).thenReturn("/aPath");
//        when(mockedFolder.getContext()).thenReturn("someContext");
//        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(mockedFolder);
//        when(mockedEvent.getSessionId()).thenReturn("someId");
//
//        when(mockedRestApiFactory.getRecordFolderAPI(any(UserModel.class))).thenReturn(mockedRecordFolderAPI);
//
//        RecordFolder mockedFilePlanComponent = mock(RecordFolder.class);
//        when(mockedRecordFolderAPI.getRecordFolder("folderId")).thenReturn(mockedFilePlanComponent);
//
//        mockSiteAndUserData();
//        EventResult result = loadSingleComponent.processEvent(mockedEvent, new StopWatch());
//        verify(mockedTestFileService, times(1)).getFile();
//        verify(mockedRecordFolderAPI, never()).createRecord(any(Record.class), eq("folderId"), any(File.class));
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
    public void testUploadRecordOperation() throws Exception
    {
        loadSingleComponent.setEventNameComplete("recordLoaded");
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_LOAD_OPERATION)).thenReturn(LOAD_RECORD_OPERATION);
        when(mockedEvent.getData()).thenReturn(mockedData);

        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFolder.getId()).thenReturn("folderId");
        when(mockedFolder.getPath()).thenReturn("/aPath");
        when(mockedFolder.getContext()).thenReturn("someContext");
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(mockedFolder);
        when(mockedEvent.getSessionId()).thenReturn("someId");

        when(mockedRestApiFactory.getRecordFolderAPI(any(UserModel.class))).thenReturn(mockedRecordFolderAPI);

        RecordFolder mockedFilePlanComponent = mock(RecordFolder.class);
        when(mockedFilePlanComponent.getId()).thenReturn("folderId");
        when(mockedRecordFolderAPI.getRecordFolder("folderId")).thenReturn(mockedFilePlanComponent);

        File mockedFile = mock(File.class);
        when(mockedTestFileService.getFile()).thenReturn(mockedFile);

        mockSiteAndUserData();
        EventResult result = loadSingleComponent.processEvent(mockedEvent, new StopWatch());

        //TODO uncomment this when RM-4564 issue is fixed
//        verify(mockedTestFileService, times(3)).getFile();
//        verify(mockedRecordFolderAPI, times(3)).createRecord(any(Record.class), eq("folderId"), any(File.class));
//        verify(mockedFileFolderService, times(3)).incrementFileCount(any(String.class), any(String.class), any(Long.class));

        assertEquals(true, result.isSuccess());
        DBObject data = (DBObject) result.getData();
        assertEquals("Created 1 record.", data.get("msg"));
        assertEquals("/aPath", data.get(FIELD_PATH));
        assertEquals("aUser", data.get("username"));
        assertEquals(1, result.getNextEvents().size());
        Event event = result.getNextEvents().get(0);
        assertEquals("recordLoaded", event.getName());
        DBObject eventData = (DBObject) event.getData();
        assertEquals("someContext", eventData.get(FIELD_CONTEXT));
        assertEquals("/aPath", eventData.get(FIELD_PATH));
    }
}
