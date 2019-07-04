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
import static org.junit.Assert.assertNotNull;
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
import org.alfresco.bm.dataload.rm.services.ExecutionState;
import org.alfresco.bm.dataload.rm.services.RecordData;
import org.alfresco.bm.dataload.rm.services.RecordService;
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
 * Unit tests for ScheduleFilingUnfiledRecordsUnitTest
 *
 * @author Silviu Dinuta
 * @since 2.6
 */
@RunWith(MockitoJUnitRunner.class)
public class ScheduleFilingUnfiledRecordsUnitTest implements RMEventConstants
{
    private static final String TEST_EVENT_RESCHEDULE_SELF = "testScheduleFilingUnfiledRecords";
    private static final String TEST_EVENT_FILE_UNFILED_RECORDS = "testFileUnfiledRecord";
    private static final String TEST_EVENT_COMPLETE = "testFilingUnfiledRecordsComplete";

    @Mock
    private SessionService mockedSessionService;

    @Mock
    private FileFolderService mockedFileFolderService;

    @Mock
    private RecordService mockedRecordService;

    @InjectMocks
    private ScheduleFilingUnfiledRecords scheduleFilingUnfiledRecords;

    @Before
    public void before()
    {
        MockitoAnnotations.initMocks(this);
        scheduleFilingUnfiledRecords.setEventNameComplete(TEST_EVENT_COMPLETE);
        scheduleFilingUnfiledRecords.setEventNameFileUnfiledRecords(TEST_EVENT_FILE_UNFILED_RECORDS);
        scheduleFilingUnfiledRecords.setEventNameRescheduleSelf(TEST_EVENT_RESCHEDULE_SELF);
        scheduleFilingUnfiledRecords.setLoadCheckDelay(0L);
    }

    @Test
    public void testFilingUnfiledRecordsNotWanted() throws Exception
    {
        scheduleFilingUnfiledRecords.setFileUnfiledRecords(false);
        EventResult result = scheduleFilingUnfiledRecords.processEvent(null, new StopWatch());

        verify(mockedFileFolderService, never()).createNewFolder(any(FolderData.class));
        verify(mockedFileFolderService, never()).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));

        assertEquals(true, result.isSuccess());
        assertEquals(ScheduleFilingUnfiledRecords.FILING_UNFILED_RECORDS_NOT_WANTED_MSG, result.getData());
        assertEquals(1, result.getNextEvents().size());
        Event event = result.getNextEvents().get(0);
        assertEquals(TEST_EVENT_COMPLETE, event.getName());
    }

    @Test
    public void testFileAllAndNoUnfiledRecordsToLoad() throws Exception
    {
        scheduleFilingUnfiledRecords.setFileUnfiledRecords(true);
        scheduleFilingUnfiledRecords.setMaxActiveLoaders(8);
        when(mockedRecordService.getRecordCountInSpecifiedPaths(ExecutionState.UNFILED_RECORD_DECLARED.name(), null)).thenReturn(0L);

        // record filing limit "0"
        scheduleFilingUnfiledRecords.setRecordFilingLimit("0");
        EventResult result = scheduleFilingUnfiledRecords.processEvent(null, new StopWatch());

        verify(mockedFileFolderService, never()).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));

        assertEquals(true, result.isSuccess());
        assertEquals(ScheduleFilingUnfiledRecords.DONE_EVENT_MSG, result.getData());
        assertEquals(1, result.getNextEvents().size());
        Event event = result.getNextEvents().get(0);
        assertEquals(TEST_EVENT_COMPLETE, event.getName());

        // record filing limit empty
        scheduleFilingUnfiledRecords.setRecordFilingLimit("");
        result = scheduleFilingUnfiledRecords.processEvent(null, new StopWatch());

        verify(mockedFileFolderService, never()).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));

        assertEquals(true, result.isSuccess());
        assertEquals(ScheduleFilingUnfiledRecords.DONE_EVENT_MSG, result.getData());
        assertEquals(1, result.getNextEvents().size());
        event = result.getNextEvents().get(0);
        assertEquals(TEST_EVENT_COMPLETE, event.getName());

        // record filing limit null
        scheduleFilingUnfiledRecords.setRecordFilingLimit(null);
        result = scheduleFilingUnfiledRecords.processEvent(null, new StopWatch());

        verify(mockedFileFolderService, never()).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));

        assertEquals(true, result.isSuccess());
        assertEquals(ScheduleFilingUnfiledRecords.DONE_EVENT_MSG, result.getData());
        assertEquals(1, result.getNextEvents().size());
        event = result.getNextEvents().get(0);
        assertEquals(TEST_EVENT_COMPLETE, event.getName());
    }

    @Test
    public void testFileRecordsWithNoPreconfiguredFilingToPaths() throws Exception
    {
        int maxActiveLoaders = 8;
        int recordsNumber = 4;
        String recordId1 = "recordId1";
        String recordId2 = "recordId2";
        String recordId3 = "recordId3";
        String recordId4 = "recordId4";
        String paths = "";

        scheduleFilingUnfiledRecords.setFileUnfiledRecords(true);
        scheduleFilingUnfiledRecords.setMaxActiveLoaders(maxActiveLoaders);
        scheduleFilingUnfiledRecords.setRecordFilingLimit("0");
        scheduleFilingUnfiledRecords.setFileToRecordFolderPaths(paths);
        when(mockedRecordService.getRecordCountInSpecifiedPaths(ExecutionState.UNFILED_RECORD_DECLARED.name(), null)).thenReturn(4L);

        String path1 = RECORD_CONTAINER_PATH + "/RootCateg1/recordFolder1";
        FolderData mockedRecordFolder1 = mock(FolderData.class);
        when(mockedRecordFolder1.getId()).thenReturn("recordFolder1Id");
        when(mockedRecordFolder1.getContext()).thenReturn(RECORD_FOLDER_CONTEXT);
        when(mockedRecordFolder1.getPath()).thenReturn(path1);
        when(mockedFileFolderService.getFolder(RECORD_FOLDER_CONTEXT, path1)).thenReturn(mockedRecordFolder1);

        String path2 = RECORD_CONTAINER_PATH + "/RootCateg1/recordFolder2";
        FolderData mockedRecordFolder2 = mock(FolderData.class);
        when(mockedRecordFolder2.getId()).thenReturn("recordFolder2Id");
        when(mockedRecordFolder2.getContext()).thenReturn(RECORD_FOLDER_CONTEXT);
        when(mockedRecordFolder2.getPath()).thenReturn(path2);
        when(mockedFileFolderService.getFolder(RECORD_FOLDER_CONTEXT, path2)).thenReturn(mockedRecordFolder2);

        //returns record folders
        when(mockedFileFolderService.getFoldersByCounts(RECORD_FOLDER_CONTEXT, null, null, null, null, null, null, 0, 100)).thenReturn(Arrays.asList(mockedRecordFolder1, mockedRecordFolder2));
        when(mockedFileFolderService.getFoldersByCounts(RECORD_FOLDER_CONTEXT, null, null, null, null, null, null, 100, 100)).thenReturn(new ArrayList<>());

        RecordData mockedRecordData1 = mock(RecordData.class);
        when(mockedRecordData1.getId()).thenReturn(recordId1);

        RecordData mockedRecordData2 = mock(RecordData.class);
        when(mockedRecordData2.getId()).thenReturn(recordId2);

        RecordData mockedRecordData3 = mock(RecordData.class);
        when(mockedRecordData3.getId()).thenReturn(recordId3);

        RecordData mockedRecordData4 = mock(RecordData.class);
        when(mockedRecordData4.getId()).thenReturn(recordId4);

        when(mockedRecordService.getRandomRecord(ExecutionState.UNFILED_RECORD_DECLARED.name(), null)).thenReturn(mockedRecordData1)
                                                                                                      .thenReturn(mockedRecordData2)
                                                                                                      .thenReturn(mockedRecordData3)
                                                                                                      .thenReturn(mockedRecordData4);


        EventResult result = scheduleFilingUnfiledRecords.processEvent(null, new StopWatch());

        verify(mockedFileFolderService, times(2)).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));

        assertEquals(true, result.isSuccess());
        verify(mockedSessionService, times(recordsNumber)).startSession(any(DBObject.class));
        assertEquals("Raised further " + (recordsNumber) + " events and rescheduled self.", result.getData());
        assertEquals(recordsNumber + 1, result.getNextEvents().size());

        List<String> listOfIds = Arrays.asList(recordId1, recordId2, recordId3, recordId4);
        for(int i = 0; i < recordsNumber ; i++)
        {
            Event event = result.getNextEvents().get(i);
            assertEquals(TEST_EVENT_FILE_UNFILED_RECORDS, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_FOLDER_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(FILE_RECORD_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
            assertEquals(listOfIds.get(i), dataObj.get(FIELD_RECORD_ID));
        }
        assertEquals(TEST_EVENT_RESCHEDULE_SELF, result.getNextEvents().get(recordsNumber).getName());
    }

    @Test
    public void testFileRecordsWithExistentPreconfiguredFilingToPaths() throws Exception
    {
        int maxActiveLoaders = 8;
        int recordsNumber = 4;
        String recordId1 = "recordId1";
        String recordId2 = "recordId2";
        String recordId3 = "recordId3";
        String recordId4 = "recordId4";
        String configuredPath1 = "/e1/e2/e3";
        String configuredPath2 = "/e1/e2/e4";
        String entirePath1 = RECORD_CONTAINER_PATH + configuredPath1;
        String entirePath2 = RECORD_CONTAINER_PATH + configuredPath2;
        String paths = configuredPath1 + "," + configuredPath2;

        scheduleFilingUnfiledRecords.setFileUnfiledRecords(true);
        scheduleFilingUnfiledRecords.setMaxActiveLoaders(maxActiveLoaders);
        scheduleFilingUnfiledRecords.setRecordFilingLimit("0");
        scheduleFilingUnfiledRecords.setFileToRecordFolderPaths(paths);
        when(mockedRecordService.getRecordCountInSpecifiedPaths(ExecutionState.UNFILED_RECORD_DECLARED.name(), null)).thenReturn(4L);

        FolderData mockedRecordFolder1 = mock(FolderData.class);
        when(mockedRecordFolder1.getId()).thenReturn("recordFolder1Id");
        when(mockedRecordFolder1.getContext()).thenReturn(RECORD_FOLDER_CONTEXT);
        when(mockedRecordFolder1.getPath()).thenReturn(entirePath1);
        when(mockedFileFolderService.getFolder(RECORD_FOLDER_CONTEXT, entirePath1)).thenReturn(mockedRecordFolder1);

        FolderData mockedRecordFolder2 = mock(FolderData.class);
        when(mockedRecordFolder2.getId()).thenReturn("recordFolder2Id");
        when(mockedRecordFolder2.getContext()).thenReturn(RECORD_FOLDER_CONTEXT);
        when(mockedRecordFolder2.getPath()).thenReturn(entirePath2);
        when(mockedFileFolderService.getFolder(RECORD_FOLDER_CONTEXT, entirePath2)).thenReturn(mockedRecordFolder2);

        RecordData mockedRecordData1 = mock(RecordData.class);
        when(mockedRecordData1.getId()).thenReturn(recordId1);

        RecordData mockedRecordData2 = mock(RecordData.class);
        when(mockedRecordData2.getId()).thenReturn(recordId2);

        RecordData mockedRecordData3 = mock(RecordData.class);
        when(mockedRecordData3.getId()).thenReturn(recordId3);

        RecordData mockedRecordData4 = mock(RecordData.class);
        when(mockedRecordData4.getId()).thenReturn(recordId4);

        when(mockedRecordService.getRandomRecord(ExecutionState.UNFILED_RECORD_DECLARED.name(), null)).thenReturn(mockedRecordData1)
                                                                                                      .thenReturn(mockedRecordData2)
                                                                                                      .thenReturn(mockedRecordData3)
                                                                                                      .thenReturn(mockedRecordData4);


        EventResult result = scheduleFilingUnfiledRecords.processEvent(null, new StopWatch());
        verify(mockedFileFolderService, never()).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));
        assertEquals(true, result.isSuccess());
        verify(mockedSessionService, times(recordsNumber)).startSession(any(DBObject.class));
        assertEquals("Raised further " + (recordsNumber) + " events and rescheduled self.", result.getData());
        assertEquals(recordsNumber + 1, result.getNextEvents().size());

        List<String> listOfIds = Arrays.asList(recordId1, recordId2, recordId3, recordId4);
        for(int i = 0; i < recordsNumber ; i++)
        {
            Event event = result.getNextEvents().get(i);
            assertEquals(TEST_EVENT_FILE_UNFILED_RECORDS, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_FOLDER_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(FILE_RECORD_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
            assertEquals(listOfIds.get(i), dataObj.get(FIELD_RECORD_ID));
        }
        assertEquals(TEST_EVENT_RESCHEDULE_SELF, result.getNextEvents().get(recordsNumber).getName());
    }

    @Test
    public void testFileRecordsWithLessRecordsThanRequestedInDb() throws Exception
    {
        int maxActiveLoaders = 8;
        String recordId1 = "recordId1";
        String recordId2 = "recordId2";
        String configuredPath1 = "/e1/e2/e3";
        String entirePath1 = RECORD_CONTAINER_PATH + configuredPath1;
        String paths = configuredPath1;

        scheduleFilingUnfiledRecords.setFileUnfiledRecords(true);
        scheduleFilingUnfiledRecords.setMaxActiveLoaders(maxActiveLoaders);
        scheduleFilingUnfiledRecords.setRecordFilingLimit("4");
        scheduleFilingUnfiledRecords.setFileToRecordFolderPaths(paths);

        FolderData mockedRecordFolder1 = mock(FolderData.class);
        when(mockedRecordFolder1.getId()).thenReturn("recordFolder1Id");
        when(mockedRecordFolder1.getContext()).thenReturn(RECORD_FOLDER_CONTEXT);
        when(mockedRecordFolder1.getPath()).thenReturn(entirePath1);
        when(mockedFileFolderService.getFolder(RECORD_FOLDER_CONTEXT, entirePath1)).thenReturn(mockedRecordFolder1);

        RecordData mockedRecordData1 = mock(RecordData.class);
        when(mockedRecordData1.getId()).thenReturn(recordId1);

        RecordData mockedRecordData2 = mock(RecordData.class);
        when(mockedRecordData2.getId()).thenReturn(recordId2);

        when(mockedRecordService.getRandomRecord(ExecutionState.UNFILED_RECORD_DECLARED.name(), null)).thenReturn(mockedRecordData1)
                                                                                                      .thenReturn(mockedRecordData2)
                                                                                                      .thenReturn(null);

        EventResult result = scheduleFilingUnfiledRecords.processEvent(null, new StopWatch());
        verify(mockedFileFolderService, never()).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));
        assertEquals(true, result.isSuccess());
        verify(mockedSessionService, times(2)).startSession(any(DBObject.class));
        assertEquals("Raised further " + (2) + " events and rescheduled self.", result.getData());
        assertEquals(3, result.getNextEvents().size());

        List<String> listOfIds = Arrays.asList(recordId1, recordId2);
        for(int i = 0; i < 2 ; i++)
        {
            Event event = result.getNextEvents().get(i);
            assertEquals(TEST_EVENT_FILE_UNFILED_RECORDS, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_FOLDER_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(FILE_RECORD_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
            assertEquals(listOfIds.get(i), dataObj.get(FIELD_RECORD_ID));
        }
        assertEquals(TEST_EVENT_RESCHEDULE_SELF, result.getNextEvents().get(2).getName());
    }

    @Test
    public void testFileRecordsWithConfiguredInexistentFilingFromPaths() throws Exception
    {
        int maxActiveLoaders = 3;
        String configuredPath1 = "/e1/e2/e3";
        String entirePath1 = RECORD_CONTAINER_PATH + configuredPath1;
        String paths = configuredPath1;
        String recordId1 = "recordId1";
        String recordParentPath1 = "/recordParentPath1";
        String recordParentFullPath1 = UNFILED_RECORD_CONTAINER_PATH + recordParentPath1;
        String recordId2 = "recordId2";
        String recordParentPath2 = "/recordParentPath2";
        String recordParentFullPath2 = UNFILED_RECORD_CONTAINER_PATH + recordParentPath2;
        String recordId3 = "recordId3";
        String recordParentPath3 = "/recordParentPath3";
        String recordParentFullPath3 = UNFILED_RECORD_CONTAINER_PATH + recordParentPath3;
        String recordId4 = "recordId4";
        String recordParentPath4 = "/recordParentPath4";
        String recordParentFullPath4 = UNFILED_RECORD_CONTAINER_PATH + recordParentPath4;

        String fileFromPathsStr = "/inexisistingPath1,/inexisistingPath2,/inexisistingPath3";

        scheduleFilingUnfiledRecords.setFileUnfiledRecords(true);
        scheduleFilingUnfiledRecords.setMaxActiveLoaders(maxActiveLoaders);
        scheduleFilingUnfiledRecords.setRecordFilingLimit("4");
        scheduleFilingUnfiledRecords.setFileToRecordFolderPaths(paths);
        scheduleFilingUnfiledRecords.setFileFromUnfiledPaths(fileFromPathsStr);

        FolderData mockedRecordFolder1 = mock(FolderData.class);
        when(mockedRecordFolder1.getId()).thenReturn("recordFolder1Id");
        when(mockedRecordFolder1.getContext()).thenReturn(RECORD_FOLDER_CONTEXT);
        when(mockedRecordFolder1.getPath()).thenReturn(entirePath1);
        when(mockedFileFolderService.getFolder(RECORD_FOLDER_CONTEXT, entirePath1)).thenReturn(mockedRecordFolder1);

        RecordData mockedRecordData1 = mock(RecordData.class);
        when(mockedRecordData1.getId()).thenReturn(recordId1);
        when(mockedRecordData1.getParentPath()).thenReturn(recordParentFullPath1);

        RecordData mockedRecordData2 = mock(RecordData.class);
        when(mockedRecordData2.getId()).thenReturn(recordId2);
        when(mockedRecordData2.getParentPath()).thenReturn(recordParentFullPath2);

        RecordData mockedRecordData3 = mock(RecordData.class);
        when(mockedRecordData3.getId()).thenReturn(recordId3);
        when(mockedRecordData3.getParentPath()).thenReturn(recordParentFullPath3);

        RecordData mockedRecordData4 = mock(RecordData.class);
        when(mockedRecordData4.getId()).thenReturn(recordId4);
        when(mockedRecordData4.getParentPath()).thenReturn(recordParentFullPath4);

        when(mockedRecordService.getRecordsInPaths(ExecutionState.UNFILED_RECORD_DECLARED.name(), null, 0, 100))
                  .thenReturn(Arrays.asList(mockedRecordData1, mockedRecordData2, mockedRecordData3, mockedRecordData4));
        when(mockedRecordService.getRecordsInPaths(ExecutionState.UNFILED_RECORD_DECLARED.name(), null, 100, 100)).thenReturn(new ArrayList<>());

        when(mockedRecordService.getRandomRecord(ExecutionState.UNFILED_RECORD_DECLARED.name(), Arrays.asList(recordParentFullPath1, recordParentFullPath2, recordParentFullPath3, recordParentFullPath4)))
        .thenReturn(mockedRecordData1)
        .thenReturn(mockedRecordData2)
        .thenReturn(mockedRecordData3)
        .thenReturn(mockedRecordData4);

        EventResult result = scheduleFilingUnfiledRecords.processEvent(null, new StopWatch());
        verify(mockedFileFolderService, never()).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));
        verify(mockedRecordService, times(maxActiveLoaders)).updateRecord(any(RecordData.class));
        assertEquals(true, result.isSuccess());
        verify(mockedSessionService, times(maxActiveLoaders)).startSession(any(DBObject.class));
        assertEquals("Raised further " + (maxActiveLoaders) + " events and rescheduled self.", result.getData());
        assertEquals(maxActiveLoaders + 1, result.getNextEvents().size());

        List<String> listOfIds = Arrays.asList(recordId1, recordId2, recordId3, recordId4);
        for(int i = 0; i < maxActiveLoaders ; i++)
        {
            Event event = result.getNextEvents().get(i);
            assertEquals(TEST_EVENT_FILE_UNFILED_RECORDS, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_FOLDER_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(FILE_RECORD_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
            assertEquals(listOfIds.get(i), dataObj.get(FIELD_RECORD_ID));
        }
        assertEquals(TEST_EVENT_RESCHEDULE_SELF, result.getNextEvents().get(maxActiveLoaders).getName());
    }

    @Test
    public void testFileRecordsWithExistentFilingFromPaths() throws Exception
    {
        int maxActiveLoaders = 8;
        String configuredPath1 = "/e1/e2/e3";
        String entirePath1 = RECORD_CONTAINER_PATH + configuredPath1;
        String paths = configuredPath1;
        String recordId1 = "recordId1";
        String recordParentPath1 = "recordParentPath1";
        String recordParentFullPath1 = UNFILED_RECORD_CONTAINER_PATH + "/" + recordParentPath1;
        String recordId2 = "recordId2";
        String recordParentPath2 = "/recordParentPath2";
        String recordParentFullPath2 = UNFILED_RECORD_CONTAINER_PATH + recordParentPath2;
        String recordId3 = "recordId3";
        String recordParentPath3 = "/recordParentPath3";
        String recordParentFullPath3 = UNFILED_RECORD_CONTAINER_PATH + recordParentPath3;
        String recordId4 = "recordId4";
        String recordParentPath4 = "/recordParentPath4";
        String recordParentFullPath4 = UNFILED_RECORD_CONTAINER_PATH + recordParentPath4;

        String fileFromPathsStr = recordParentPath1 + "," + recordParentPath2 + "," + recordParentPath3 + "," + recordParentPath4;

        scheduleFilingUnfiledRecords.setFileUnfiledRecords(true);
        scheduleFilingUnfiledRecords.setMaxActiveLoaders(maxActiveLoaders);
        scheduleFilingUnfiledRecords.setRecordFilingLimit("4");
        scheduleFilingUnfiledRecords.setFileToRecordFolderPaths(paths);
        scheduleFilingUnfiledRecords.setFileFromUnfiledPaths(fileFromPathsStr);

        FolderData mockedRecordFolder1 = mock(FolderData.class);
        when(mockedRecordFolder1.getId()).thenReturn("recordFolder1Id");
        when(mockedRecordFolder1.getContext()).thenReturn(RECORD_FOLDER_CONTEXT);
        when(mockedRecordFolder1.getPath()).thenReturn(entirePath1);
        when(mockedFileFolderService.getFolder(RECORD_FOLDER_CONTEXT, entirePath1)).thenReturn(mockedRecordFolder1);

        RecordData mockedRecordData1 = mock(RecordData.class);
        when(mockedRecordData1.getId()).thenReturn(recordId1);
        when(mockedRecordData1.getParentPath()).thenReturn(recordParentFullPath1);

        RecordData mockedRecordData2 = mock(RecordData.class);
        when(mockedRecordData2.getId()).thenReturn(recordId2);
        when(mockedRecordData2.getParentPath()).thenReturn(recordParentFullPath2);

        RecordData mockedRecordData3 = mock(RecordData.class);
        when(mockedRecordData3.getId()).thenReturn(recordId3);
        when(mockedRecordData3.getParentPath()).thenReturn(recordParentFullPath3);

        RecordData mockedRecordData4 = mock(RecordData.class);
        when(mockedRecordData4.getId()).thenReturn(recordId4);
        when(mockedRecordData4.getParentPath()).thenReturn(recordParentFullPath4);

        when(mockedRecordService.getRecordsInPaths(ExecutionState.UNFILED_RECORD_DECLARED.name(), null, 0, 100))
        .thenReturn(Arrays.asList(mockedRecordData1, mockedRecordData2, mockedRecordData3, mockedRecordData4));
        when(mockedRecordService.getRecordsInPaths(ExecutionState.UNFILED_RECORD_DECLARED.name(), null, 100, 100)).thenReturn(new ArrayList<>());

        when(mockedRecordService.getRandomRecord(ExecutionState.UNFILED_RECORD_DECLARED.name(), Arrays.asList(recordParentFullPath1, recordParentFullPath2, recordParentFullPath3)))
        .thenReturn(mockedRecordData1)
        .thenReturn(mockedRecordData2)
        .thenReturn(mockedRecordData3)
        .thenReturn(null);

        FolderData mockedUnfiledRecordFolder = mock(FolderData.class);
        when(mockedUnfiledRecordFolder.getId()).thenReturn("folderId1");
        when(mockedUnfiledRecordFolder.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedUnfiledRecordFolder.getPath()).thenReturn(recordParentFullPath1);
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, recordParentFullPath1)).thenReturn(mockedUnfiledRecordFolder);

        FolderData mockedUnfiledRecordFolder1 = mock(FolderData.class);
        when(mockedUnfiledRecordFolder1.getId()).thenReturn("newfolderId2");
        when(mockedUnfiledRecordFolder1.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedUnfiledRecordFolder1.getPath()).thenReturn(recordParentFullPath2);
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, recordParentFullPath2)).thenReturn(mockedUnfiledRecordFolder1);

        FolderData mockedUnfiledRecordFolder2 = mock(FolderData.class);
        when(mockedUnfiledRecordFolder2.getId()).thenReturn("newfolderId3");
        when(mockedUnfiledRecordFolder2.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedUnfiledRecordFolder2.getPath()).thenReturn(recordParentFullPath3);
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, recordParentFullPath3)).thenReturn(mockedUnfiledRecordFolder2);

        EventResult result = scheduleFilingUnfiledRecords.processEvent(null, new StopWatch());
        verify(mockedFileFolderService, never()).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));
        verify(mockedRecordService, times(3)).updateRecord(any(RecordData.class));
        assertEquals(true, result.isSuccess());
        verify(mockedSessionService, times(3)).startSession(any(DBObject.class));
        assertEquals("Raised further " + (3) + " events and rescheduled self.", result.getData());
        assertEquals(4, result.getNextEvents().size());

        List<String> listOfIds = Arrays.asList(recordId1, recordId2, recordId3, recordId4);
        for(int i = 0; i < 3 ; i++)
        {
            Event event = result.getNextEvents().get(i);
            assertEquals(TEST_EVENT_FILE_UNFILED_RECORDS, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_FOLDER_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(FILE_RECORD_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
            assertEquals(listOfIds.get(i), dataObj.get(FIELD_RECORD_ID));
        }
        assertEquals(TEST_EVENT_RESCHEDULE_SELF, result.getNextEvents().get(3).getName());
    }

    @Test
    public void testFileRecordsWithNoRecordsInDB() throws Exception
    {
        int maxActiveLoaders = 8;
        String configuredPath1 = "/e1/e2/e3";
        String entirePath1 = RECORD_CONTAINER_PATH + configuredPath1;
        String paths = configuredPath1;

        scheduleFilingUnfiledRecords.setFileUnfiledRecords(true);
        scheduleFilingUnfiledRecords.setMaxActiveLoaders(maxActiveLoaders);
        scheduleFilingUnfiledRecords.setRecordFilingLimit("4");
        scheduleFilingUnfiledRecords.setFileToRecordFolderPaths(paths);

        FolderData mockedRecordFolder1 = mock(FolderData.class);
        when(mockedRecordFolder1.getId()).thenReturn("recordFolder1Id");
        when(mockedRecordFolder1.getContext()).thenReturn(RECORD_FOLDER_CONTEXT);
        when(mockedRecordFolder1.getPath()).thenReturn(entirePath1);
        when(mockedFileFolderService.getFolder(RECORD_FOLDER_CONTEXT, entirePath1)).thenReturn(mockedRecordFolder1);

        when(mockedRecordService.getRandomRecord(ExecutionState.UNFILED_RECORD_DECLARED.name(), null)).thenReturn(null);

        EventResult result = scheduleFilingUnfiledRecords.processEvent(null, new StopWatch());
        verify(mockedFileFolderService, never()).createNewFolder(any(FolderData.class));
        verify(mockedFileFolderService, never()).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));

        assertEquals(true, result.isSuccess());
        assertEquals(ScheduleFilingUnfiledRecords.DONE_EVENT_MSG, result.getData());
        assertEquals(1, result.getNextEvents().size());
        Event event = result.getNextEvents().get(0);
        assertEquals(TEST_EVENT_COMPLETE, event.getName());
    }

    @Test
    public void testFileRecordsFromUnfiledRecordContainer() throws Exception
    {
        int maxActiveLoaders = 8;
        String configuredPath1 = "/e1/e2/e3";
        String entirePath1 = RECORD_CONTAINER_PATH + configuredPath1;
        String paths = configuredPath1;
        String recordId1 = "recordId1";
        String recordParentPath1 = "/";
        String recordParentFullPath1 = UNFILED_RECORD_CONTAINER_PATH;
        String recordId2 = "recordId2";
        String recordParentPath2 = "/recordParentPath2";
        String recordParentFullPath2 = UNFILED_RECORD_CONTAINER_PATH + recordParentPath2;
        String recordId3 = "recordId3";
        String recordParentPath3 = "/recordParentPath3";
        String recordParentFullPath3 = UNFILED_RECORD_CONTAINER_PATH + recordParentPath3;
        String recordId4 = "recordId4";
        String recordParentPath4 = "/recordParentPath4";
        String recordParentFullPath4 = UNFILED_RECORD_CONTAINER_PATH + recordParentPath4;

        String fileFromPathsStr = recordParentPath1;

        scheduleFilingUnfiledRecords.setFileUnfiledRecords(true);
        scheduleFilingUnfiledRecords.setMaxActiveLoaders(maxActiveLoaders);
        scheduleFilingUnfiledRecords.setRecordFilingLimit("4");
        scheduleFilingUnfiledRecords.setFileToRecordFolderPaths(paths);
        scheduleFilingUnfiledRecords.setFileFromUnfiledPaths(fileFromPathsStr);

        FolderData mockedRecordFolder1 = mock(FolderData.class);
        when(mockedRecordFolder1.getId()).thenReturn("recordFolder1Id");
        when(mockedRecordFolder1.getContext()).thenReturn(RECORD_FOLDER_CONTEXT);
        when(mockedRecordFolder1.getPath()).thenReturn(entirePath1);
        when(mockedFileFolderService.getFolder(RECORD_FOLDER_CONTEXT, entirePath1)).thenReturn(mockedRecordFolder1);

        RecordData mockedRecordData1 = mock(RecordData.class);
        when(mockedRecordData1.getId()).thenReturn(recordId1);
        when(mockedRecordData1.getParentPath()).thenReturn(recordParentFullPath1);

        RecordData mockedRecordData2 = mock(RecordData.class);
        when(mockedRecordData2.getId()).thenReturn(recordId2);
        when(mockedRecordData2.getParentPath()).thenReturn(recordParentFullPath2);

        RecordData mockedRecordData3 = mock(RecordData.class);
        when(mockedRecordData3.getId()).thenReturn(recordId3);
        when(mockedRecordData3.getParentPath()).thenReturn(recordParentFullPath3);

        RecordData mockedRecordData4 = mock(RecordData.class);
        when(mockedRecordData4.getId()).thenReturn(recordId4);
        when(mockedRecordData4.getParentPath()).thenReturn(recordParentFullPath4);

        when(mockedRecordService.getRecordsInPaths(ExecutionState.UNFILED_RECORD_DECLARED.name(), null, 0, 100))
        .thenReturn(Arrays.asList(mockedRecordData1, mockedRecordData2, mockedRecordData3, mockedRecordData4));
        when(mockedRecordService.getRecordsInPaths(ExecutionState.UNFILED_RECORD_DECLARED.name(), null, 100, 100)).thenReturn(new ArrayList<>());

        when(mockedRecordService.getRandomRecord(ExecutionState.UNFILED_RECORD_DECLARED.name(), Arrays.asList(recordParentFullPath2, recordParentFullPath3, recordParentFullPath4, recordParentFullPath1)))
        .thenReturn(mockedRecordData1)
        .thenReturn(mockedRecordData2)
        .thenReturn(mockedRecordData3)
        .thenReturn(mockedRecordData4)
        .thenReturn(null);

        FolderData mockedUnfiledRecordFolder = mock(FolderData.class);
        when(mockedUnfiledRecordFolder.getId()).thenReturn("folderId1");
        when(mockedUnfiledRecordFolder.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedUnfiledRecordFolder.getPath()).thenReturn(recordParentFullPath1);
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, recordParentFullPath1)).thenReturn(mockedUnfiledRecordFolder);

        FolderData mockedUnfiledRecordFolder1 = mock(FolderData.class);
        when(mockedUnfiledRecordFolder1.getId()).thenReturn("newfolderId2");
        when(mockedUnfiledRecordFolder1.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedUnfiledRecordFolder1.getPath()).thenReturn(recordParentFullPath2);
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, recordParentFullPath2)).thenReturn(mockedUnfiledRecordFolder1);

        FolderData mockedUnfiledRecordFolder2 = mock(FolderData.class);
        when(mockedUnfiledRecordFolder2.getId()).thenReturn("newfolderId3");
        when(mockedUnfiledRecordFolder2.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedUnfiledRecordFolder2.getPath()).thenReturn(recordParentFullPath3);
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, recordParentFullPath3)).thenReturn(mockedUnfiledRecordFolder2);

        FolderData mockedUnfiledRecordFolder3 = mock(FolderData.class);
        when(mockedUnfiledRecordFolder3.getId()).thenReturn("newfolderId4");
        when(mockedUnfiledRecordFolder3.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedUnfiledRecordFolder3.getPath()).thenReturn(recordParentFullPath4);
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, recordParentFullPath4)).thenReturn(mockedUnfiledRecordFolder3);
        when(mockedFileFolderService.getChildFolders(UNFILED_CONTEXT, recordParentFullPath1, 0, 100))
        .thenReturn(Arrays.asList(mockedUnfiledRecordFolder1, mockedUnfiledRecordFolder2, mockedUnfiledRecordFolder3))
        .thenReturn(new ArrayList<>());

        EventResult result = scheduleFilingUnfiledRecords.processEvent(null, new StopWatch());
        verify(mockedFileFolderService, never()).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));
        verify(mockedRecordService, times(4)).updateRecord(any(RecordData.class));
        assertEquals(true, result.isSuccess());
        verify(mockedSessionService, times(4)).startSession(any(DBObject.class));
        assertEquals("Raised further " + (4) + " events and rescheduled self.", result.getData());
        assertEquals(5, result.getNextEvents().size());

        List<String> listOfIds = Arrays.asList(recordId1, recordId2, recordId3, recordId4);
        for(int i = 0; i < 4 ; i++)
        {
            Event event = result.getNextEvents().get(i);
            assertEquals(TEST_EVENT_FILE_UNFILED_RECORDS, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_FOLDER_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(FILE_RECORD_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
            assertEquals(listOfIds.get(i), dataObj.get(FIELD_RECORD_ID));
        }
        assertEquals(TEST_EVENT_RESCHEDULE_SELF, result.getNextEvents().get(4).getName());
    }
}
