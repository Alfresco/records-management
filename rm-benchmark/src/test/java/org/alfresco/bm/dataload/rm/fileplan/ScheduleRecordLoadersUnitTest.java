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

import java.util.ArrayList;
import java.util.Arrays;

import org.alfresco.bm.cm.FileFolderService;
import org.alfresco.bm.cm.FolderData;
import org.alfresco.bm.dataload.RMEventConstants;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.session.SessionService;
import org.alfresco.rest.core.RestAPIFactory;
import org.alfresco.rest.rm.community.model.fileplan.FilePlan;
import org.alfresco.rest.rm.community.model.recordcategory.RecordCategory;
import org.alfresco.rest.rm.community.model.recordcategory.RecordCategoryChild;
import org.alfresco.rest.rm.community.requests.gscore.api.FilePlanAPI;
import org.alfresco.rest.rm.community.requests.gscore.api.RecordCategoryAPI;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import com.mongodb.DBObject;

/**
 * Unit tests for ScheduleRecordLoaders
 * @author Silviu Dinuta
 * @since 2.6
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ScheduleRecordLoadersUnitTest implements RMEventConstants
{
    private static final String EVENT_COMPLETE = "loadingRecordsComplete";

    private static final String EVENT_LOAD_RECORD = "loadRecord";

    private static final String EVENT_SCHEDULE_RECORD_LOADERS = "scheduleRecordLoaders";

    @Mock
    private SessionService mockedSessionService;

    @Mock
    private FileFolderService mockedFileFolderService;

    @Mock
    private RestAPIFactory mockedRestApiFactory;

    @Mock
    private ApplicationContext mockedApplicationContext;

    @Mock
    private FilePlanAPI mockedFilePlanAPI;

    @Mock
    private RecordCategoryAPI mockedRecordCategoryAPI;

    @InjectMocks
    private ScheduleRecordLoaders scheduleRecordLoaders;

    @Before
    public void before()
    {
        MockitoAnnotations.initMocks(this);
        scheduleRecordLoaders.setEventNameLoadingComplete(EVENT_COMPLETE);
        scheduleRecordLoaders.setEventNameLoadRecords(EVENT_LOAD_RECORD);
        scheduleRecordLoaders.setEventNameScheduleRecordLoaders(EVENT_SCHEDULE_RECORD_LOADERS);
        scheduleRecordLoaders.setLoadCheckDelay(0L);
    }

    @Test
    public void testUploadRecordsNotWanted() throws Exception
    {
        scheduleRecordLoaders.setUploadRecords(false);
        EventResult result = scheduleRecordLoaders.processEvent(null, new StopWatch());

        verify(mockedFileFolderService, never()).createNewFolder(any(FolderData.class));
        verify(mockedFileFolderService, never()).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));
        verify(mockedSessionService, never()).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Uploading of records into File Plan not wanted, continue with loading data.",result.getData());
        assertEquals(1, result.getNextEvents().size());
        Event event = result.getNextEvents().get(0);
        assertEquals("loadingRecordsComplete", event.getName());
    }

    @Test
    public void testUploadNoRecords() throws Exception
    {
        int maxActiveLoaders = 8;
        int recordsNumber = 0;

        scheduleRecordLoaders.setUploadRecords(true);
        scheduleRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleRecordLoaders.setRecordsNumber(recordsNumber);

        EventResult result = scheduleRecordLoaders.processEvent(null, new StopWatch());

        verify(mockedFileFolderService, never()).createNewFolder(any(FolderData.class));
        verify(mockedFileFolderService, never()).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));
        verify(mockedSessionService, never()).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Loading completed.  Raising 'done' event.",result.getData());
        assertEquals(1, result.getNextEvents().size());
        assertEquals(EVENT_COMPLETE, result.getNextEvents().get(0).getName());
    }

    @Test
    public void testUploadRecordsWithNoPreconfiguredPaths() throws Exception
    {
        int maxActiveLoaders = 8;
        int recordsNumber = 4;
        String paths = "";

        scheduleRecordLoaders.setUploadRecords(true);
        scheduleRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleRecordLoaders.setRecordsNumber(recordsNumber);
        scheduleRecordLoaders.setRecordFolderPaths(paths);

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

        when(mockedRestApiFactory.getFilePlansAPI()).thenReturn(mockedFilePlanAPI);
        when(mockedRestApiFactory.getRecordCategoryAPI()).thenReturn(mockedRecordCategoryAPI);

        //returns record folders
        when(mockedFileFolderService.getFoldersByCounts(RECORD_FOLDER_CONTEXT, null, null, null, null, null, null, 0, 100)).thenReturn(Arrays.asList(mockedRecordFolder1, mockedRecordFolder2));
        when(mockedFileFolderService.getFoldersByCounts(RECORD_FOLDER_CONTEXT, null, null, null, null, null, null, 100, 100)).thenReturn(new ArrayList<>());

        EventResult result = scheduleRecordLoaders.processEvent(null, new StopWatch());

        verify(mockedFileFolderService, times(2)).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));

        assertEquals(true, result.isSuccess());
        verify(mockedSessionService, times(recordsNumber)).startSession(any(DBObject.class));
        assertEquals("Raised further " + (recordsNumber) + " events and rescheduled self.", result.getData());
        assertEquals(recordsNumber + 1, result.getNextEvents().size());

        for(int i = 0; i < recordsNumber ; i++)
        {
            Event event = result.getNextEvents().get(i);
            assertEquals(EVENT_LOAD_RECORD, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_FOLDER_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(LOAD_RECORD_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
        }
        assertEquals(EVENT_SCHEDULE_RECORD_LOADERS, result.getNextEvents().get(recordsNumber).getName());
    }

    @Test
    public void testUploadRecordsWithExistentPreconfiguredPaths() throws Exception
    {
        int maxActiveLoaders = 8;
        int recordsNumber = 4;
        String configuredPath1 = "/e1/e2/e3";
        String configuredPath2 = "/e1/e2/e4";
        String entirePath1 = RECORD_CONTAINER_PATH + configuredPath1;
        String entirePath2 = RECORD_CONTAINER_PATH + configuredPath2;
        String paths = configuredPath1 + "," + configuredPath2;

        scheduleRecordLoaders.setUploadRecords(true);
        scheduleRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleRecordLoaders.setRecordsNumber(recordsNumber);
        scheduleRecordLoaders.setRecordFolderPaths(paths);

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

        when(mockedRestApiFactory.getFilePlansAPI()).thenReturn(mockedFilePlanAPI);
        when(mockedRestApiFactory.getRecordCategoryAPI()).thenReturn(mockedRecordCategoryAPI);

        EventResult result = scheduleRecordLoaders.processEvent(null, new StopWatch());
        verify(mockedFileFolderService, never()).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));

        assertEquals(true, result.isSuccess());
        verify(mockedSessionService, times(recordsNumber)).startSession(any(DBObject.class));
        assertEquals("Raised further " + (recordsNumber) + " events and rescheduled self.", result.getData());
        assertEquals(recordsNumber + 1, result.getNextEvents().size());

        for(int i = 0; i < recordsNumber ; i++)
        {
            Event event = result.getNextEvents().get(i);
            assertEquals(EVENT_LOAD_RECORD, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_FOLDER_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(LOAD_RECORD_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
        }
        assertEquals(EVENT_SCHEDULE_RECORD_LOADERS, result.getNextEvents().get(recordsNumber).getName());
    }

    @Test
    public void testUploadRecordsWithExistentPathsOneCategoryAndOneRecordFolderChildOfSpecifiecCategory() throws Exception
    {
        int maxActiveLoaders = 8;
        int recordsNumber = 4;
        String configuredPath1 = "/e1/e2/e3";
        String configuredPath2 = "/e1/e2/e3/e4";
        String entirePath1 = RECORD_CONTAINER_PATH + configuredPath1;
        String entirePath2 = RECORD_CONTAINER_PATH + configuredPath2;
        String paths = configuredPath1 + "," + configuredPath2;

        scheduleRecordLoaders.setUploadRecords(true);
        scheduleRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleRecordLoaders.setRecordsNumber(recordsNumber);
        scheduleRecordLoaders.setRecordFolderPaths(paths);

        FolderData mockedRecordCategory = mock(FolderData.class);
        when(mockedRecordCategory.getId()).thenReturn("recordCategoryId1");
        when(mockedRecordCategory.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedRecordCategory.getPath()).thenReturn(entirePath1);
        when(mockedFileFolderService.getFolder(RECORD_CATEGORY_CONTEXT, entirePath1)).thenReturn(mockedRecordCategory);

        FolderData mockedRecordFolder1 = mock(FolderData.class);
        when(mockedRecordFolder1.getId()).thenReturn("recordFolderId1");
        when(mockedRecordFolder1.getContext()).thenReturn(RECORD_FOLDER_CONTEXT);
        when(mockedRecordFolder1.getPath()).thenReturn(entirePath2);
        when(mockedFileFolderService.getFolder(RECORD_FOLDER_CONTEXT, entirePath2)).thenReturn(mockedRecordFolder1);

        when(mockedFileFolderService.getChildFolders(RECORD_CATEGORY_CONTEXT, entirePath1, 0, 100)).thenReturn(new ArrayList<>());
        when(mockedFileFolderService.getChildFolders(RECORD_FOLDER_CONTEXT, entirePath1, 0, 100)).thenReturn(Arrays.asList(mockedRecordFolder1));
        when(mockedFileFolderService.getChildFolders(RECORD_FOLDER_CONTEXT, entirePath1, 100, 100)).thenReturn(new ArrayList<>());

        when(mockedRestApiFactory.getFilePlansAPI()).thenReturn(mockedFilePlanAPI);
        when(mockedRestApiFactory.getRecordCategoryAPI()).thenReturn(mockedRecordCategoryAPI);

        EventResult result = scheduleRecordLoaders.processEvent(null, new StopWatch());
        verify(mockedFileFolderService, never()).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));
        verify(mockedFileFolderService, times(1)).getChildFolders(eq(RECORD_CATEGORY_CONTEXT), any(String.class), any(Integer.class), any(Integer.class));
        verify(mockedFileFolderService, times(2)).getChildFolders(eq(RECORD_FOLDER_CONTEXT), any(String.class), any(Integer.class), any(Integer.class));

        assertEquals(true, result.isSuccess());
        verify(mockedSessionService, times(recordsNumber)).startSession(any(DBObject.class));
        assertEquals("Raised further " + (recordsNumber) + " events and rescheduled self.", result.getData());
        assertEquals(recordsNumber + 1, result.getNextEvents().size());

        for(int i = 0; i < recordsNumber ; i++)
        {
            Event event = result.getNextEvents().get(i);
            assertEquals(EVENT_LOAD_RECORD, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_FOLDER_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(LOAD_RECORD_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
        }
        assertEquals(EVENT_SCHEDULE_RECORD_LOADERS, result.getNextEvents().get(recordsNumber).getName());
    }

    @Test
    public void testUploadRecordsWithExistentCategoryPathWithOneChildInside() throws Exception
    {
        int maxActiveLoaders = 8;
        int recordsNumber = 4;
        String configuredPath1 = "/e1/e2/e3";
        String entirePath1 = RECORD_CONTAINER_PATH + configuredPath1;
        String paths = configuredPath1;

        scheduleRecordLoaders.setUploadRecords(true);
        scheduleRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleRecordLoaders.setRecordsNumber(recordsNumber);
        scheduleRecordLoaders.setRecordFolderPaths(paths);

        FolderData mockedRecordCategory = mock(FolderData.class);
        when(mockedRecordCategory.getId()).thenReturn("recordCategoryId");
        when(mockedRecordCategory.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedRecordCategory.getPath()).thenReturn(entirePath1);
        when(mockedFileFolderService.getFolder(RECORD_CATEGORY_CONTEXT, entirePath1)).thenReturn(mockedRecordCategory);

        //child of configured path
        String childPath = entirePath1 + "/" + "child1";
        FolderData mockedRecordFolder1 = mock(FolderData.class);
        when(mockedRecordFolder1.getId()).thenReturn("recordFolderId");
        when(mockedRecordFolder1.getContext()).thenReturn(RECORD_FOLDER_CONTEXT);
        when(mockedRecordFolder1.getPath()).thenReturn(childPath);

        when(mockedFileFolderService.getFolder(RECORD_FOLDER_CONTEXT, childPath)).thenReturn(mockedRecordFolder1);

        when(mockedFileFolderService.getChildFolders(RECORD_CATEGORY_CONTEXT, entirePath1, 0, 100)).thenReturn(new ArrayList<>());
        when(mockedFileFolderService.getChildFolders(RECORD_FOLDER_CONTEXT, entirePath1, 0, 100)).thenReturn(Arrays.asList(mockedRecordFolder1));
        when(mockedFileFolderService.getChildFolders(RECORD_FOLDER_CONTEXT, entirePath1, 100, 100)).thenReturn(new ArrayList<>());

        when(mockedRestApiFactory.getFilePlansAPI()).thenReturn(mockedFilePlanAPI);
        when(mockedRestApiFactory.getRecordCategoryAPI()).thenReturn(mockedRecordCategoryAPI);

        EventResult result = scheduleRecordLoaders.processEvent(null, new StopWatch());
        verify(mockedFileFolderService, never()).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));
        verify(mockedFileFolderService, times(1)).getChildFolders(eq(RECORD_CATEGORY_CONTEXT), any(String.class), any(Integer.class), any(Integer.class));
        verify(mockedFileFolderService, times(2)).getChildFolders(eq(RECORD_FOLDER_CONTEXT), any(String.class), any(Integer.class), any(Integer.class));

        assertEquals(true, result.isSuccess());
        verify(mockedSessionService, times(recordsNumber)).startSession(any(DBObject.class));
        assertEquals("Raised further " + (recordsNumber) + " events and rescheduled self.", result.getData());
        assertEquals(recordsNumber + 1, result.getNextEvents().size());

        for(int i = 0; i < recordsNumber ; i++)
        {
            Event event = result.getNextEvents().get(i);
            assertEquals(EVENT_LOAD_RECORD, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_FOLDER_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(LOAD_RECORD_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
        }
        assertEquals(EVENT_SCHEDULE_RECORD_LOADERS, result.getNextEvents().get(recordsNumber).getName());
    }

    @Test
    public void testUploadRecordsWithNotExistentPreconfiguredPaths() throws Exception
    {
        int maxActiveLoaders = 3;
        int recordsNumber = 4;
        String configuredPath1 = "/e1/e2/e3";
        String configuredPath2 = "/e1/e2/e4";
        String paths = configuredPath1 + "," + configuredPath2;

        scheduleRecordLoaders.setUploadRecords(true);
        scheduleRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleRecordLoaders.setRecordsNumber(recordsNumber);
        scheduleRecordLoaders.setRecordFolderPaths(paths);

        //file plan should be always there
        FolderData mockedFilePlanContainer = mock(FolderData.class);
        when(mockedFilePlanContainer.getId()).thenReturn("filePlanId");
        when(mockedFilePlanContainer.getContext()).thenReturn(FILEPLAN_CONTEXT);
        when(mockedFilePlanContainer.getPath()).thenReturn(RECORD_CONTAINER_PATH);
        when(mockedFileFolderService.getFolder(FILEPLAN_CONTEXT, RECORD_CONTAINER_PATH)).thenReturn(mockedFilePlanContainer);

        when(mockedRestApiFactory.getFilePlansAPI()).thenReturn(mockedFilePlanAPI);
        when(mockedRestApiFactory.getRecordCategoryAPI()).thenReturn(mockedRecordCategoryAPI);

        FilePlan mockedFilePlan = mock(FilePlan.class);
        when(mockedFilePlan.getId()).thenReturn("filePlanId");
        when(mockedFilePlanAPI.getFilePlan("filePlanId")).thenReturn(mockedFilePlan);

        //e1 root category
        RecordCategory mockedE1FilePlanComponent = mock(RecordCategory.class);
        when(mockedE1FilePlanComponent.getId()).thenReturn("e1Id");
        when(mockedRecordCategoryAPI.getRecordCategory("e1Id")).thenReturn(mockedE1FilePlanComponent);
        when(mockedFilePlanAPI.createRootRecordCategory(any(RecordCategory.class), eq("filePlanId"))).thenReturn(mockedE1FilePlanComponent);

        String e1Path = RECORD_CONTAINER_PATH + "/e1";
        FolderData mockedE1 = mock(FolderData.class);
        when(mockedE1.getId()).thenReturn("e1Id");
        when(mockedE1.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedE1.getPath()).thenReturn(e1Path);
        when(mockedFileFolderService.getFolder(RECORD_CATEGORY_CONTEXT, e1Path)).thenReturn(null)
                                                                        .thenReturn(mockedE1);
        when(mockedFileFolderService.getFolder("e1Id")).thenReturn(mockedE1);

        //e2 child category
        RecordCategory mockedE2childRecordCategory = mock(RecordCategory.class);
        when(mockedE2childRecordCategory.getId()).thenReturn("e2Id");

        RecordCategoryChild mockedE2RecordCategoryChild = mock(RecordCategoryChild.class);
        when(mockedE2RecordCategoryChild.getId()).thenReturn("e2Id");

        when(mockedRecordCategoryAPI.getRecordCategory("e2Id")).thenReturn(mockedE2childRecordCategory);
        when(mockedRecordCategoryAPI.createRecordCategoryChild(any(RecordCategoryChild.class), eq("e1Id"))).thenReturn(mockedE2RecordCategoryChild);

        String e2Path = RECORD_CONTAINER_PATH + "/e1/e2";
        FolderData mockedE2 = mock(FolderData.class);
        when(mockedE2.getId()).thenReturn("e2Id");
        when(mockedE2.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedE2.getPath()).thenReturn(e2Path);
        when(mockedFileFolderService.getFolder(RECORD_CATEGORY_CONTEXT, e2Path)).thenReturn(null)
        .thenReturn(mockedE2);
        when(mockedFileFolderService.getFolder("e2Id")).thenReturn(mockedE2);

        //e3 record folder
        RecordCategoryChild mockedE3RecordFolder = mock(RecordCategoryChild.class);
        when(mockedE3RecordFolder .getId()).thenReturn("e3Id");

        String e3Path = RECORD_CONTAINER_PATH + "/e1/e2/e3";
        FolderData mockedE3 = mock(FolderData.class);
        when(mockedE3.getId()).thenReturn("e3Id");
        when(mockedE3.getContext()).thenReturn(RECORD_FOLDER_CONTEXT);
        when(mockedE3.getPath()).thenReturn(e3Path);
        when(mockedFileFolderService.getFolder(RECORD_FOLDER_CONTEXT, e3Path)).thenReturn(null)
                                                                        .thenReturn(null)
                                                                        .thenReturn(mockedE3);
        when(mockedFileFolderService.getFolder("e3Id")).thenReturn(mockedE3);

        //e4 record folder
        RecordCategoryChild mockedE4RecordFolder = mock(RecordCategoryChild.class);
        when(mockedE4RecordFolder .getId()).thenReturn("e4Id");

        String e4Path = RECORD_CONTAINER_PATH + "/e1/e2/e4";
        FolderData mockedE4 = mock(FolderData.class);
        when(mockedE4.getId()).thenReturn("e4Id");
        when(mockedE4.getContext()).thenReturn(RECORD_FOLDER_CONTEXT);
        when(mockedE4.getPath()).thenReturn(e4Path);
        when(mockedFileFolderService.getFolder(RECORD_FOLDER_CONTEXT, e4Path)).thenReturn(null)
                                                                        .thenReturn(null)
                                                                        .thenReturn(mockedE4);
        when(mockedFileFolderService.getFolder("e4Id")).thenReturn(mockedE4);

        when(mockedRecordCategoryAPI.createRecordCategoryChild(any(RecordCategoryChild.class), eq("e2Id"))).thenReturn(mockedE3RecordFolder)
                                                                                                          .thenReturn(mockedE4RecordFolder);

        when(mockedApplicationContext.getBean("restAPIFactory", RestAPIFactory.class)).thenReturn(mockedRestApiFactory);
        EventResult result = scheduleRecordLoaders.processEvent(null, new StopWatch());
        verify(mockedFileFolderService, never()).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));
        verify(mockedFileFolderService, times(4)).createNewFolder(any(String.class), any(String.class), any(String.class));
        verify(mockedFileFolderService, times(4)).incrementFolderCount(any(String.class), any(String.class), eq(1L));
        verify(mockedFileFolderService, times(4)).getFolder(any(String.class));
        assertEquals(true, result.isSuccess());

        verify(mockedSessionService, times(maxActiveLoaders)).startSession(any(DBObject.class));
        assertEquals("Raised further " + (maxActiveLoaders) + " events and rescheduled self.", result.getData());
        assertEquals(maxActiveLoaders + 1, result.getNextEvents().size());

        for(int i = 0; i < maxActiveLoaders ; i++)
        {
            Event event = result.getNextEvents().get(i);
            assertEquals(EVENT_LOAD_RECORD, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_FOLDER_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(LOAD_RECORD_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
        }
        assertEquals(EVENT_SCHEDULE_RECORD_LOADERS, result.getNextEvents().get(maxActiveLoaders).getName());
    }

    @Test
    public void testUploadRecordsWithNotExistentPreconfiguredSinglePath() throws Exception
    {
        int maxActiveLoaders = 8;
        int recordsNumber = 4;
        String configuredPath1 = "/e1/e2/e3";
        String paths = configuredPath1;

        scheduleRecordLoaders.setUploadRecords(true);
        scheduleRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleRecordLoaders.setRecordsNumber(recordsNumber);
        scheduleRecordLoaders.setRecordFolderPaths(paths);

        //file plan should be always there
        FolderData mockedFilePlanContainer = mock(FolderData.class);
        when(mockedFilePlanContainer.getId()).thenReturn("filePlanId");
        when(mockedFilePlanContainer.getContext()).thenReturn(FILEPLAN_CONTEXT);
        when(mockedFilePlanContainer.getPath()).thenReturn(RECORD_CONTAINER_PATH);
        when(mockedFileFolderService.getFolder(FILEPLAN_CONTEXT, RECORD_CONTAINER_PATH)).thenReturn(mockedFilePlanContainer);

        when(mockedRestApiFactory.getFilePlansAPI()).thenReturn(mockedFilePlanAPI);
        when(mockedRestApiFactory.getRecordCategoryAPI()).thenReturn(mockedRecordCategoryAPI);

        FilePlan mockedFilePlan = mock(FilePlan.class);
        when(mockedFilePlan.getId()).thenReturn("filePlanId");
        when(mockedFilePlanAPI.getFilePlan("filePlanId")).thenReturn(mockedFilePlan);

        //e1 root category
        RecordCategory mockedE1FilePlanComponent = mock(RecordCategory.class);
        when(mockedE1FilePlanComponent.getId()).thenReturn("e1Id");
        when(mockedRecordCategoryAPI.getRecordCategory("e1Id")).thenReturn(mockedE1FilePlanComponent);
        when(mockedFilePlanAPI.createRootRecordCategory(any(RecordCategory.class), eq("filePlanId"))).thenReturn(mockedE1FilePlanComponent);

        String e1Path = RECORD_CONTAINER_PATH + "/e1";
        FolderData mockedE1 = mock(FolderData.class);
        when(mockedE1.getId()).thenReturn("e1Id");
        when(mockedE1.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedE1.getPath()).thenReturn(e1Path);
        when(mockedFileFolderService.getFolder(RECORD_CATEGORY_CONTEXT, e1Path)).thenReturn(null)
                                                                        .thenReturn(mockedE1);
        when(mockedFileFolderService.getFolder("e1Id")).thenReturn(mockedE1);

        //e2 child category
        RecordCategory mockedE2childRecordCategory = mock(RecordCategory.class);
        when(mockedE2childRecordCategory.getId()).thenReturn("e2Id");

        RecordCategoryChild mockedE2RecordCategoryChild = mock(RecordCategoryChild.class);
        when(mockedE2RecordCategoryChild.getId()).thenReturn("e2Id");

        when(mockedRecordCategoryAPI.getRecordCategory("e2Id")).thenReturn(mockedE2childRecordCategory);
        when(mockedRecordCategoryAPI.createRecordCategoryChild(any(RecordCategoryChild.class), eq("e1Id"))).thenReturn(mockedE2RecordCategoryChild);

        String e2Path = RECORD_CONTAINER_PATH + "/e1/e2";
        FolderData mockedE2 = mock(FolderData.class);
        when(mockedE2.getId()).thenReturn("e2Id");
        when(mockedE2.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedE2.getPath()).thenReturn(e2Path);
        when(mockedFileFolderService.getFolder(RECORD_CATEGORY_CONTEXT, e2Path)).thenReturn(null)
        .thenReturn(mockedE2);
        when(mockedFileFolderService.getFolder("e2Id")).thenReturn(mockedE2);

        //e3 record folder
        RecordCategoryChild mockedE3RecordFolder = mock(RecordCategoryChild.class);
        when(mockedE3RecordFolder .getId()).thenReturn("e3Id");

        String e3Path = RECORD_CONTAINER_PATH + "/e1/e2/e3";
        FolderData mockedE3 = mock(FolderData.class);
        when(mockedE3.getId()).thenReturn("e3Id");
        when(mockedE3.getContext()).thenReturn(RECORD_FOLDER_CONTEXT);
        when(mockedE3.getPath()).thenReturn(e3Path);
        when(mockedFileFolderService.getFolder(RECORD_FOLDER_CONTEXT, e3Path)).thenReturn(null)
                                                                        .thenReturn(null)
                                                                        .thenReturn(mockedE3);
        when(mockedFileFolderService.getFolder("e3Id")).thenReturn(mockedE3);
        when(mockedRecordCategoryAPI.createRecordCategoryChild(any(RecordCategoryChild.class), eq("e2Id"))).thenReturn(mockedE3RecordFolder);

        when(mockedApplicationContext.getBean("restAPIFactory", RestAPIFactory.class)).thenReturn(mockedRestApiFactory);
        EventResult result = scheduleRecordLoaders.processEvent(null, new StopWatch());
        verify(mockedFileFolderService, never()).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));
        verify(mockedFileFolderService, times(3)).createNewFolder(any(String.class), any(String.class), any(String.class));
        verify(mockedFileFolderService, times(3)).incrementFolderCount(any(String.class), any(String.class), eq(1L));
        verify(mockedFileFolderService, times(3)).getFolder(any(String.class));
        verify(mockedSessionService, times(recordsNumber)).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Raised further " + (recordsNumber) + " events and rescheduled self.", result.getData());
        assertEquals(recordsNumber + 1, result.getNextEvents().size());

        for(int i = 0; i < recordsNumber ; i++)
        {
            Event event = result.getNextEvents().get(i);
            assertEquals(EVENT_LOAD_RECORD, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_FOLDER_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(LOAD_RECORD_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
        }
        assertEquals(EVENT_SCHEDULE_RECORD_LOADERS, result.getNextEvents().get(recordsNumber).getName());
    }

    @Test
    public void testUploadRecordsInNonExistentOnePathElCategory() throws Exception
    {
        int maxActiveLoaders = 8;
        int recordsNumber = 4;
        String configuredPath1 = "/e1";
        String paths = configuredPath1;

        scheduleRecordLoaders.setUploadRecords(true);
        scheduleRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleRecordLoaders.setRecordsNumber(recordsNumber);
        scheduleRecordLoaders.setRecordFolderPaths(paths);

        //file plan should be always there
        FolderData mockedFilePlanContainer = mock(FolderData.class);
        when(mockedFilePlanContainer.getId()).thenReturn("filePlanId");
        when(mockedFilePlanContainer.getContext()).thenReturn(FILEPLAN_CONTEXT);
        when(mockedFilePlanContainer.getPath()).thenReturn(RECORD_CONTAINER_PATH);
        when(mockedFileFolderService.getFolder(FILEPLAN_CONTEXT, RECORD_CONTAINER_PATH)).thenReturn(mockedFilePlanContainer);

        //2 existent record folders
        String path1 = RECORD_CONTAINER_PATH + "/categ1/folder1";
        FolderData mockedRecordFolder1 = mock(FolderData.class);
        when(mockedRecordFolder1.getId()).thenReturn("recordFolder1Id");
        when(mockedRecordFolder1.getContext()).thenReturn(RECORD_FOLDER_CONTEXT);
        when(mockedRecordFolder1.getPath()).thenReturn(path1);
        when(mockedFileFolderService.getFolder(RECORD_FOLDER_CONTEXT, path1)).thenReturn(mockedRecordFolder1);

        String path2 = RECORD_CONTAINER_PATH + "/categ2/folder2";
        FolderData mockedRecordFolder2 = mock(FolderData.class);
        when(mockedRecordFolder2.getId()).thenReturn("recordFolder2Id");
        when(mockedRecordFolder2.getContext()).thenReturn(RECORD_FOLDER_CONTEXT);
        when(mockedRecordFolder2.getPath()).thenReturn(path2);
        when(mockedFileFolderService.getFolder(RECORD_FOLDER_CONTEXT, path2)).thenReturn(mockedRecordFolder2);

        //returns available record folders
        when(mockedFileFolderService.getFoldersByCounts(RECORD_FOLDER_CONTEXT, null, null, null, null, null, null, 0, 100)).thenReturn(Arrays.asList(mockedRecordFolder1, mockedRecordFolder2));
        when(mockedFileFolderService.getFoldersByCounts(RECORD_FOLDER_CONTEXT, null, null, null, null, null, null, 100, 100)).thenReturn(new ArrayList<>());

        when(mockedRestApiFactory.getFilePlansAPI()).thenReturn(mockedFilePlanAPI);
        when(mockedRestApiFactory.getRecordCategoryAPI()).thenReturn(mockedRecordCategoryAPI);

        FilePlan mockedFilePlan = mock(FilePlan.class);
        when(mockedFilePlan.getId()).thenReturn("filePlanId");
        when(mockedFilePlanAPI.getFilePlan("filePlanId")).thenReturn(mockedFilePlan);

        String e1Path = RECORD_CONTAINER_PATH + "/e1";
        when(mockedFileFolderService.getFolder(RECORD_FOLDER_CONTEXT, e1Path)).thenReturn(null);

        EventResult result = scheduleRecordLoaders.processEvent(null, new StopWatch());
        verify(mockedFileFolderService, never()).getFolder(any(String.class));
        verify(mockedFileFolderService, never()).incrementFolderCount(any(String.class), any(String.class), eq(1L));
        verify(mockedFileFolderService, never()).createNewFolder(any(String.class), any(String.class), any(String.class));
        verify(mockedFileFolderService, times(2)).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));

        assertEquals(true, result.isSuccess());
        verify(mockedSessionService, times(recordsNumber)).startSession(any(DBObject.class));
        assertEquals("Raised further " + (recordsNumber) + " events and rescheduled self.", result.getData());
        assertEquals(recordsNumber + 1, result.getNextEvents().size());

        for(int i = 0; i < recordsNumber ; i++)
        {
            Event event = result.getNextEvents().get(i);
            assertEquals(EVENT_LOAD_RECORD, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_FOLDER_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(LOAD_RECORD_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
        }
        assertEquals(EVENT_SCHEDULE_RECORD_LOADERS, result.getNextEvents().get(recordsNumber).getName());
    }

    @Test
    public void testUploadRecordsWithExceptionWhenCreatingPreconfiguredPaths() throws Exception
    {
        int maxActiveLoaders = 8;
        int recordsNumber = 4;
        String configuredPath1 = "/e1/e2/e3";
        String configuredPath2 = "/e1/e2/e4";
        String paths = configuredPath1 + "," + configuredPath2;

        scheduleRecordLoaders.setUploadRecords(true);
        scheduleRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleRecordLoaders.setRecordsNumber(recordsNumber);
        scheduleRecordLoaders.setRecordFolderPaths(paths);

        String path1 = RECORD_CONTAINER_PATH + "/categ1/folder1";
        FolderData mockedRecordFolder1 = mock(FolderData.class);
        when(mockedRecordFolder1.getId()).thenReturn("recordFolder1Id");
        when(mockedRecordFolder1.getContext()).thenReturn(RECORD_FOLDER_CONTEXT);
        when(mockedRecordFolder1.getPath()).thenReturn(path1);
        when(mockedFileFolderService.getFolder(RECORD_FOLDER_CONTEXT, path1)).thenReturn(mockedRecordFolder1);

        String path2 = RECORD_CONTAINER_PATH + "/categ2/folder2";
        FolderData mockedRecordFolder2 = mock(FolderData.class);
        when(mockedRecordFolder2.getId()).thenReturn("recordFolder2Id");
        when(mockedRecordFolder2.getContext()).thenReturn(RECORD_FOLDER_CONTEXT);
        when(mockedRecordFolder2.getPath()).thenReturn(path2);
        when(mockedFileFolderService.getFolder(RECORD_FOLDER_CONTEXT, path2)).thenReturn(mockedRecordFolder2);

        when(mockedRestApiFactory.getFilePlansAPI()).thenReturn(mockedFilePlanAPI);
        when(mockedRestApiFactory.getRecordCategoryAPI()).thenReturn(mockedRecordCategoryAPI);

        //returns available record folders
        when(mockedFileFolderService.getFoldersByCounts(RECORD_FOLDER_CONTEXT, null, null, null, null, null, null, 0, 100)).thenReturn(Arrays.asList(mockedRecordFolder1, mockedRecordFolder2));
        when(mockedFileFolderService.getFoldersByCounts(RECORD_FOLDER_CONTEXT, null, null, null, null, null, null, 100, 100)).thenReturn(new ArrayList<>());

        EventResult result = scheduleRecordLoaders.processEvent(null, new StopWatch());
        verify(mockedFileFolderService, times(2)).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));

        assertEquals(true, result.isSuccess());
        verify(mockedSessionService, times(recordsNumber)).startSession(any(DBObject.class));
        assertEquals("Raised further " + (recordsNumber) + " events and rescheduled self.", result.getData());
        assertEquals(recordsNumber + 1, result.getNextEvents().size());

        for(int i = 0; i < recordsNumber ; i++)
        {
            Event event = result.getNextEvents().get(i);
            assertEquals(EVENT_LOAD_RECORD, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_FOLDER_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(LOAD_RECORD_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
        }
        assertEquals(EVENT_SCHEDULE_RECORD_LOADERS, result.getNextEvents().get(recordsNumber).getName());
    }
}