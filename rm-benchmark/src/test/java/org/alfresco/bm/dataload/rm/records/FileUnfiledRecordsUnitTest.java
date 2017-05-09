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

package org.alfresco.bm.dataload.rm.records;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.bm.cm.FileFolderService;
import org.alfresco.bm.cm.FolderData;
import org.alfresco.bm.dataload.RMEventConstants;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.session.SessionService;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.mongodb.DBObject;

/**
 * Unit tests for FileUnfiledRecords
 *
 * @author Silviu Dinuta
 * @since 2.6
 */
@RunWith(MockitoJUnitRunner.class)
public class FileUnfiledRecordsUnitTest implements RMEventConstants
{
    private static final String EVENT_RESCHEDULE_SELF = "testFileUnfiledRecords";

    private static final String EVENT_FILE_UNFILED_RECORD = "testFileUnfiledRecord";

    private static final String EVENT_COMPLETE = "testUnfiledRecordsFiled";

    @Mock
    private SessionService mockedSessionService;

    @Mock
    private FileFolderService mockedFileFolderService;

    @InjectMocks
    private FileUnfiledRecords fileUnfiledRecords;

    @Before
    public void before()
    {
        MockitoAnnotations.initMocks(this);
        fileUnfiledRecords.setEventNameComplete(EVENT_COMPLETE);
        fileUnfiledRecords.setEventNameFileUnfiledRecord(EVENT_FILE_UNFILED_RECORD);
        fileUnfiledRecords.setEventNameRescheduleSelf(EVENT_RESCHEDULE_SELF);
        fileUnfiledRecords.setFileUnfiledRecordDelay(0L);
    }
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
        assertEquals("Request data not complete for filing unfiled records: " + mockedData, result.getData());
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
        assertEquals("Request data not complete for filing unfiled records: " + mockedData, result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testWithNullRecordsToFile() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_RECORDS_TO_FILE)).thenReturn(null);
        when(mockedEvent.getData()).thenReturn(mockedData);
        EventResult result = fileUnfiledRecords.processEvent(mockedEvent, new StopWatch());
        assertEquals(false, result.isSuccess());
        assertEquals("Request data not complete for filing unfiled records: " + mockedData, result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test(expected=IllegalStateException.class)
    public void testInexistentFolderForContextAndPath() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_RECORDS_TO_FILE)).thenReturn(new ArrayList<String>());
        when(mockedEvent.getData()).thenReturn(mockedData);
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(null);

        fileUnfiledRecords.processEvent(mockedEvent, new StopWatch());
    }

    @Test
    public void testNoRecordsToFile() throws Exception
    {
        fileUnfiledRecords.setMaxActiveLoaders(8);
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_RECORDS_TO_FILE)).thenReturn(new ArrayList<String>());
        when(mockedEvent.getData()).thenReturn(mockedData);

        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFolder.getId()).thenReturn("folderId");
        when(mockedFolder.getPath()).thenReturn("/aPath");
        when(mockedFolder.getContext()).thenReturn("someContext");
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(mockedFolder);

        EventResult result = fileUnfiledRecords.processEvent(mockedEvent, new StopWatch());
        verify(mockedFileFolderService, times(1)).deleteFolder(mockedFolder.getContext(), mockedFolder.getPath() + "/locked", false);
        verify(mockedSessionService, never()).startSession(any(DBObject.class));

        verify(mockedFileFolderService, never()).incrementFileCount(any(String.class), any(String.class), any(Long.class));
        assertEquals(true, result.isSuccess());
        assertEquals(ScheduleFilingUnfiledRecords.DONE_EVENT_MSG, result.getData());
        assertEquals(1, result.getNextEvents().size());
        Event event = result.getNextEvents().get(0);
        assertEquals(EVENT_COMPLETE, event.getName());
    }

    @Test
    public void testFileAllRecords() throws Exception
    {
        fileUnfiledRecords.setMaxActiveLoaders(8);
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        List<String> recordIds= new ArrayList<>();
        recordIds.addAll(Arrays.asList("recordId1", "recordId2", "recordId3"));
        when(mockedData.get(FIELD_RECORDS_TO_FILE)).thenReturn(recordIds);
        when(mockedEvent.getData()).thenReturn(mockedData);

        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFolder.getId()).thenReturn("folderId");
        when(mockedFolder.getPath()).thenReturn("/aPath");
        when(mockedFolder.getContext()).thenReturn("someContext");
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(mockedFolder);

        EventResult result = fileUnfiledRecords.processEvent(mockedEvent, new StopWatch());
        verify(mockedFileFolderService, never()).deleteFolder(mockedFolder.getContext(), mockedFolder.getPath() + "/locked", false);
        verify(mockedSessionService, times(3)).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        List<Event> nextEvents = result.getNextEvents();
        assertEquals(4, nextEvents.size());
        assertEquals("Raised further 3 events and rescheduled self.", result.getData());

        Event firstEvent = nextEvents.get(0);
        DBObject data = (DBObject) firstEvent.getData();
        assertEquals("someContext", data.get(FIELD_CONTEXT));
        assertEquals("/aPath", data.get(FIELD_PATH));
        assertEquals(FILE_RECORD_OPERATION, data.get(FIELD_LOAD_OPERATION));
        assertEquals("recordId1", data.get(FIELD_RECORD_ID));
        assertEquals(EVENT_FILE_UNFILED_RECORD, firstEvent.getName());

        Event secondEvent = nextEvents.get(1);
        data = (DBObject) secondEvent.getData();
        assertEquals("someContext", data.get(FIELD_CONTEXT));
        assertEquals("/aPath", data.get(FIELD_PATH));
        assertEquals(FILE_RECORD_OPERATION, data.get(FIELD_LOAD_OPERATION));
        assertEquals("recordId2", data.get(FIELD_RECORD_ID));
        assertEquals(EVENT_FILE_UNFILED_RECORD, secondEvent.getName());

        Event thirdEvent = nextEvents.get(2);
        data = (DBObject) thirdEvent.getData();
        assertEquals("someContext", data.get(FIELD_CONTEXT));
        assertEquals("/aPath", data.get(FIELD_PATH));
        assertEquals(FILE_RECORD_OPERATION, data.get(FIELD_LOAD_OPERATION));
        assertEquals("recordId3", data.get(FIELD_RECORD_ID));
        assertEquals(EVENT_FILE_UNFILED_RECORD, thirdEvent.getName());

        Event scheduleSelfEvent = nextEvents.get(3);
        data = (DBObject) scheduleSelfEvent.getData();
        assertEquals("someContext", data.get(FIELD_CONTEXT));
        assertEquals("/aPath", data.get(FIELD_PATH));
        @SuppressWarnings("unchecked")
        List<String> nextRecordsToFile = (List<String>) data.get(FIELD_RECORDS_TO_FILE);
        assertEquals(0, nextRecordsToFile.size());
        assertEquals(EVENT_RESCHEDULE_SELF, scheduleSelfEvent.getName());
    }
}
