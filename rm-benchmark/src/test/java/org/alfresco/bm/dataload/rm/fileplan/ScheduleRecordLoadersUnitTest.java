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
    private static final String EMPTY_CONTEXT = "";

    @Mock
    private SessionService mockedSessionService;

    @Mock
    private FileFolderService mockedFileFolderService;

    @Mock
    private RestAPIFactory mockedRestApiFactory;

    @Mock
    private FilePlanComponentAPI mockedFilePlanComponentAPI;

    @InjectMocks
    private ScheduleRecordLoaders scheduleRecordLoaders;

    @Test
    public void testUploadRecordsNotWanted() throws Exception
    {
        scheduleRecordLoaders.setUploadRecords(false);
        EventResult result = scheduleRecordLoaders.processEvent(null, new StopWatch());

        verify(mockedFileFolderService, never()).createNewFolder(any(FolderData.class));
        verify(mockedFileFolderService, never()).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));
        verify(mockedSessionService, never()).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Uploading of records into File Plan not wanted, continue with loading unfiled record folders structure.",result.getData());
        assertEquals(1, result.getNextEvents().size());
        Event event = result.getNextEvents().get(0);
        assertEquals("scheduleUnfiledRecordFoldersLoaders", event.getName());
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
        assertEquals("loadingRecordsComplete", result.getNextEvents().get(0).getName());
    }

    @Test
    public void testUploadRecordsWithNoPreconfiguredPaths() throws Exception
    {
        int maxActiveLoaders = 8;
        int recordsNumber = 4;
        String paths = EMPTY_CONTEXT;
        String username = "bob";

        scheduleRecordLoaders.setUploadRecords(true);
        scheduleRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleRecordLoaders.setRecordsNumber(recordsNumber);
        scheduleRecordLoaders.setRecordFolderPaths(paths);
        scheduleRecordLoaders.setUsername(username);

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

        when(mockedRestApiFactory.getFilePlanComponentsAPI(any(UserModel.class))).thenReturn(mockedFilePlanComponentAPI);

        //returns record folders
        when(mockedFileFolderService.getFoldersByCounts(RECORD_FOLDER_CONTEXT, null, null, null, null, null, null, 0, 100)).thenReturn(Arrays.asList(mockedRecordFolder1, mockedRecordFolder2));
        when(mockedFileFolderService.getFoldersByCounts(RECORD_FOLDER_CONTEXT, null, null, null, null, null, null, 100, 100)).thenReturn(new ArrayList<FolderData>());

        EventResult result = scheduleRecordLoaders.processEvent(null, new StopWatch());

        verify(mockedFileFolderService, times(2)).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));

        // using the number of events here because the algorithm for distributing records to folders in this test, 2 folders can generate (0,4),(1,3),(2,2),(3,1) or (4,0)
        // and if the number of records to create is 0 the event for loading will not be scheduled
        int nextEventsSize = result.getNextEvents().size();
        verify(mockedFileFolderService, times(nextEventsSize - 1)).createNewFolder(any(FolderData.class));
        verify(mockedSessionService, times(nextEventsSize - 1)).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Raised further " + (nextEventsSize - 1) + " events and rescheduled self.", result.getData());

        Event firstEvent = result.getNextEvents().get(0);
        if(nextEventsSize - 1 == 2)
        {
            assertEquals("loadRecords", firstEvent.getName());
            DBObject dataObj = (DBObject)firstEvent.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_FOLDER_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(path1, (String) dataObj.get(FIELD_PATH));
            int value1 = (Integer) dataObj.get(FIELD_RECORDS_TO_CREATE);
            assertEquals(username, (String) dataObj.get(FIELD_SITE_MANAGER));

            Event secondEvent = result.getNextEvents().get(1);
            assertEquals("loadRecords", secondEvent.getName());
            dataObj = (DBObject)secondEvent.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_FOLDER_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(path2, (String) dataObj.get(FIELD_PATH));
            int value2 = (Integer) dataObj.get(FIELD_RECORDS_TO_CREATE);
            assertEquals(username, (String) dataObj.get(FIELD_SITE_MANAGER));
            assertEquals(recordsNumber, value1 + value2);
        }
        else
        {
            //in case that one of generated values is 0 we check that the scheduled event had created all records
            DBObject dataObj = (DBObject)firstEvent.getData();
            int value = (Integer) dataObj.get(FIELD_RECORDS_TO_CREATE);
            assertEquals(recordsNumber, value);
        }
        assertEquals("scheduleRecordLoaders", result.getNextEvents().get(nextEventsSize - 1).getName());
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
        String username = "bob";

        scheduleRecordLoaders.setUploadRecords(true);
        scheduleRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleRecordLoaders.setRecordsNumber(recordsNumber);
        scheduleRecordLoaders.setRecordFolderPaths(paths);
        scheduleRecordLoaders.setUsername(username);

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

        when(mockedRestApiFactory.getFilePlanComponentsAPI(any(UserModel.class))).thenReturn(mockedFilePlanComponentAPI);

        EventResult result = scheduleRecordLoaders.processEvent(null, new StopWatch());
        verify(mockedFileFolderService, never()).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));
        assertEquals(true, result.isSuccess());

        // using the number of events here because the algorithm for distributing records to folders in this test, 2 folders can generate (0,4),(1,3),(2,2),(3,1) or (4,0)
        // and if the number of records to create is 0 the event for loading will not be scheduled
        int nextEventsSize = result.getNextEvents().size();
        verify(mockedFileFolderService, times(nextEventsSize - 1)).createNewFolder(any(FolderData.class));
        verify(mockedSessionService, times(nextEventsSize - 1)).startSession(any(DBObject.class));
        assertEquals("Raised further " + (nextEventsSize - 1) + " events and rescheduled self.", result.getData());

        Event firstEvent = result.getNextEvents().get(0);
        if(nextEventsSize - 1 == 2)
        {
            assertEquals("loadRecords", firstEvent.getName());
            DBObject dataObj = (DBObject)firstEvent.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_FOLDER_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(entirePath1, (String) dataObj.get(FIELD_PATH));
            int value1 = (Integer) dataObj.get(FIELD_RECORDS_TO_CREATE);
            assertEquals(username, (String) dataObj.get(FIELD_SITE_MANAGER));

            Event secondEvent = result.getNextEvents().get(1);
            assertEquals("loadRecords", secondEvent.getName());
            dataObj = (DBObject)secondEvent.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_FOLDER_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(entirePath2, (String) dataObj.get(FIELD_PATH));
            int value2 = (Integer) dataObj.get(FIELD_RECORDS_TO_CREATE);
            assertEquals(username, (String) dataObj.get(FIELD_SITE_MANAGER));
            assertEquals(recordsNumber, value1 + value2);
        }
        else
        {
            //in case that one of generated values is 0 we check that the scheduled event had created all records
            DBObject dataObj = (DBObject)firstEvent.getData();
            int value = (Integer) dataObj.get(FIELD_RECORDS_TO_CREATE);
            assertEquals(recordsNumber, value);
        }
        assertEquals("scheduleRecordLoaders", result.getNextEvents().get(nextEventsSize - 1).getName());
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
        String username = "bob";

        scheduleRecordLoaders.setUploadRecords(true);
        scheduleRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleRecordLoaders.setRecordsNumber(recordsNumber);
        scheduleRecordLoaders.setRecordFolderPaths(paths);
        scheduleRecordLoaders.setUsername(username);

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

        when(mockedFileFolderService.getChildFolders(RECORD_CATEGORY_CONTEXT, entirePath1, 0, 100)).thenReturn(new ArrayList<FolderData>());
        when(mockedFileFolderService.getChildFolders(RECORD_FOLDER_CONTEXT, entirePath1, 0, 100)).thenReturn(Arrays.asList(mockedRecordFolder1));
        when(mockedFileFolderService.getChildFolders(RECORD_FOLDER_CONTEXT, entirePath1, 100, 100)).thenReturn(new ArrayList<FolderData>());

        when(mockedRestApiFactory.getFilePlanComponentsAPI(any(UserModel.class))).thenReturn(mockedFilePlanComponentAPI);

        EventResult result = scheduleRecordLoaders.processEvent(null, new StopWatch());
        verify(mockedFileFolderService, never()).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));
        verify(mockedFileFolderService, times(1)).getChildFolders(eq(RECORD_CATEGORY_CONTEXT), any(String.class), any(Integer.class), any(Integer.class));
        verify(mockedFileFolderService, times(2)).getChildFolders(eq(RECORD_FOLDER_CONTEXT), any(String.class), any(Integer.class), any(Integer.class));
        assertEquals(true, result.isSuccess());
        assertEquals(2, result.getNextEvents().size());

        verify(mockedFileFolderService, times(1)).createNewFolder(any(FolderData.class));
        verify(mockedSessionService, times(1)).startSession(any(DBObject.class));
        assertEquals("Raised further 1 events and rescheduled self.", result.getData());

        Event firstEvent = result.getNextEvents().get(0);
        assertEquals("loadRecords", firstEvent.getName());
        DBObject dataObj = (DBObject)firstEvent.getData();
        assertNotNull(dataObj);
        assertEquals(RECORD_FOLDER_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
        assertEquals(entirePath2, (String) dataObj.get(FIELD_PATH));
        int value = (Integer) dataObj.get(FIELD_RECORDS_TO_CREATE);
        assertEquals(username, (String) dataObj.get(FIELD_SITE_MANAGER));
        assertEquals(recordsNumber, value);

        assertEquals("scheduleRecordLoaders", result.getNextEvents().get(1).getName());
    }

    @Test
    public void testUploadRecordsWithExistentCategoryPathWithOneChildInside() throws Exception
    {
        int maxActiveLoaders = 8;
        int recordsNumber = 4;
        String configuredPath1 = "/e1/e2/e3";
        String entirePath1 = RECORD_CONTAINER_PATH + configuredPath1;
        String paths = configuredPath1;
        String username = "bob";

        scheduleRecordLoaders.setUploadRecords(true);
        scheduleRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleRecordLoaders.setRecordsNumber(recordsNumber);
        scheduleRecordLoaders.setRecordFolderPaths(paths);
        scheduleRecordLoaders.setUsername(username);

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

        when(mockedFileFolderService.getChildFolders(RECORD_CATEGORY_CONTEXT, entirePath1, 0, 100)).thenReturn(new ArrayList<FolderData>());
        when(mockedFileFolderService.getChildFolders(RECORD_FOLDER_CONTEXT, entirePath1, 0, 100)).thenReturn(Arrays.asList(mockedRecordFolder1));
        when(mockedFileFolderService.getChildFolders(RECORD_FOLDER_CONTEXT, entirePath1, 100, 100)).thenReturn(new ArrayList<FolderData>());

        when(mockedRestApiFactory.getFilePlanComponentsAPI(any(UserModel.class))).thenReturn(mockedFilePlanComponentAPI);

        EventResult result = scheduleRecordLoaders.processEvent(null, new StopWatch());
        verify(mockedFileFolderService, never()).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));
        verify(mockedFileFolderService, times(1)).getChildFolders(eq(RECORD_CATEGORY_CONTEXT), any(String.class), any(Integer.class), any(Integer.class));
        verify(mockedFileFolderService, times(2)).getChildFolders(eq(RECORD_FOLDER_CONTEXT), any(String.class), any(Integer.class), any(Integer.class));
        assertEquals(true, result.isSuccess());
        assertEquals(2, result.getNextEvents().size());

        verify(mockedFileFolderService, times(1)).createNewFolder(any(FolderData.class));
        verify(mockedSessionService, times(1)).startSession(any(DBObject.class));
        assertEquals("Raised further 1 events and rescheduled self.", result.getData());

        Event firstEvent = result.getNextEvents().get(0);
        assertEquals("loadRecords", firstEvent.getName());
        DBObject dataObj = (DBObject)firstEvent.getData();
        assertNotNull(dataObj);
        assertEquals(RECORD_FOLDER_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
        assertEquals(childPath, (String) dataObj.get(FIELD_PATH));
        int value = (Integer) dataObj.get(FIELD_RECORDS_TO_CREATE);
        assertEquals(username, (String) dataObj.get(FIELD_SITE_MANAGER));
        assertEquals(recordsNumber, value);

        assertEquals("scheduleRecordLoaders", result.getNextEvents().get(1).getName());
    }

    @Test
    public void testUploadRecordsWithNotExistentPreconfiguredPaths() throws Exception
    {
        int maxActiveLoaders = 8;
        int recordsNumber = 4;
        String configuredPath1 = "/e1/e2/e3";
        String configuredPath2 = "/e1/e2/e4";
        String paths = configuredPath1 + "," + configuredPath2;
        String username = "bob";

        scheduleRecordLoaders.setUploadRecords(true);
        scheduleRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleRecordLoaders.setRecordsNumber(recordsNumber);
        scheduleRecordLoaders.setRecordFolderPaths(paths);
        scheduleRecordLoaders.setUsername(username);

        //file plan should be always there
        FolderData mockedFilePlanContainer = mock(FolderData.class);
        when(mockedFilePlanContainer.getId()).thenReturn("filePlanId");
        when(mockedFilePlanContainer.getContext()).thenReturn(EMPTY_CONTEXT);
        when(mockedFilePlanContainer.getPath()).thenReturn(RECORD_CONTAINER_PATH);
        when(mockedFileFolderService.getFolder(EMPTY_CONTEXT, RECORD_CONTAINER_PATH)).thenReturn(mockedFilePlanContainer);

        when(mockedRestApiFactory.getFilePlanComponentsAPI(any(UserModel.class))).thenReturn(mockedFilePlanComponentAPI);
        FilePlanComponent mockedFilePlanContaineFilePlanComponent = mock(FilePlanComponent.class);
        when(mockedFilePlanContaineFilePlanComponent.getId()).thenReturn("filePlanId");
        when(mockedFilePlanComponentAPI.getFilePlanComponent("filePlanId")).thenReturn(mockedFilePlanContaineFilePlanComponent);

        //e1 category
        FilePlanComponent mockedE1FilePlanComponent = mock(FilePlanComponent.class);
        when(mockedE1FilePlanComponent .getId()).thenReturn("e1Id");
        when(mockedFilePlanComponentAPI.getFilePlanComponent("e1Id")).thenReturn(mockedE1FilePlanComponent);
        when(mockedFilePlanComponentAPI.createFilePlanComponent(any(FilePlanComponent.class), eq("filePlanId"))).thenReturn(mockedE1FilePlanComponent);

        String e1Path = RECORD_CONTAINER_PATH + "/e1";
        FolderData mockedE1 = mock(FolderData.class);
        when(mockedE1.getId()).thenReturn("e1Id");
        when(mockedE1.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedE1.getPath()).thenReturn(e1Path);
        when(mockedFileFolderService.getFolder(RECORD_CATEGORY_CONTEXT, e1Path)).thenReturn(null)
                                                                        .thenReturn(mockedE1);
        when(mockedFileFolderService.getFolder("e1Id")).thenReturn(mockedE1);

        //e2 category
        FilePlanComponent mockedE2FilePlanComponent = mock(FilePlanComponent.class);
        when(mockedE2FilePlanComponent .getId()).thenReturn("e2Id");
        when(mockedFilePlanComponentAPI.getFilePlanComponent("e2Id")).thenReturn(mockedE2FilePlanComponent);
        when(mockedFilePlanComponentAPI.createFilePlanComponent(any(FilePlanComponent.class), eq("e1Id"))).thenReturn(mockedE2FilePlanComponent);

        String e2Path = RECORD_CONTAINER_PATH + "/e1/e2";
        FolderData mockedE2 = mock(FolderData.class);
        when(mockedE2.getId()).thenReturn("e2Id");
        when(mockedE2.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedE2.getPath()).thenReturn(e2Path);
        when(mockedFileFolderService.getFolder(RECORD_CATEGORY_CONTEXT, e2Path)).thenReturn(null)
        .thenReturn(mockedE2);
        when(mockedFileFolderService.getFolder("e2Id")).thenReturn(mockedE2);

        //e3 folder
        FilePlanComponent mockedE3FilePlanComponent = mock(FilePlanComponent.class);
        when(mockedE3FilePlanComponent .getId()).thenReturn("e3Id");
        when(mockedFilePlanComponentAPI.getFilePlanComponent("e3Id")).thenReturn(mockedE3FilePlanComponent);

        String e3Path = RECORD_CONTAINER_PATH + "/e1/e2/e3";
        FolderData mockedE3 = mock(FolderData.class);
        when(mockedE3.getId()).thenReturn("e3Id");
        when(mockedE3.getContext()).thenReturn(RECORD_FOLDER_CONTEXT);
        when(mockedE3.getPath()).thenReturn(e3Path);
        when(mockedFileFolderService.getFolder(RECORD_FOLDER_CONTEXT, e3Path)).thenReturn(null)
                                                                        .thenReturn(null)
                                                                        .thenReturn(mockedE3);
        when(mockedFileFolderService.getFolder("e3Id")).thenReturn(mockedE3);

        //e4 folder
        FilePlanComponent mockedE4FilePlanComponent = mock(FilePlanComponent.class);
        when(mockedE4FilePlanComponent .getId()).thenReturn("e4Id");
        when(mockedFilePlanComponentAPI.getFilePlanComponent("e4Id")).thenReturn(mockedE4FilePlanComponent);

        String e4Path = RECORD_CONTAINER_PATH + "/e1/e2/e4";
        FolderData mockedE4 = mock(FolderData.class);
        when(mockedE4.getId()).thenReturn("e4Id");
        when(mockedE4.getContext()).thenReturn(RECORD_FOLDER_CONTEXT);
        when(mockedE4.getPath()).thenReturn(e4Path);
        when(mockedFileFolderService.getFolder(RECORD_FOLDER_CONTEXT, e4Path)).thenReturn(null)
                                                                        .thenReturn(null)
                                                                        .thenReturn(mockedE4);
        when(mockedFileFolderService.getFolder("e4Id")).thenReturn(mockedE4);

        when(mockedFilePlanComponentAPI.createFilePlanComponent(any(FilePlanComponent.class), eq("e2Id"))).thenReturn(mockedE3FilePlanComponent)
                                                                                                          .thenReturn(mockedE4FilePlanComponent);

        EventResult result = scheduleRecordLoaders.processEvent(null, new StopWatch());
        verify(mockedFileFolderService, never()).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));
        verify(mockedFileFolderService, times(4)).createNewFolder(any(String.class), any(String.class), any(String.class));
        verify(mockedFileFolderService, times(4)).incrementFolderCount(any(String.class), any(String.class), eq(1L));
        verify(mockedFileFolderService, times(4)).getFolder(any(String.class));
        assertEquals(true, result.isSuccess());

        // using the number of events here because the algorithm for distributing records to folders in this test, 2 folders can generate (0,4),(1,3),(2,2),(3,1) or (4,0)
        // and if the number of records to create is 0 the event for loading will not be scheduled
        int nextEventsSize = result.getNextEvents().size();
        verify(mockedFileFolderService, times(nextEventsSize -1)).createNewFolder(any(FolderData.class));
        verify(mockedSessionService, times(nextEventsSize - 1)).startSession(any(DBObject.class));

        assertEquals("Raised further " + (nextEventsSize - 1) + " events and rescheduled self.", result.getData());

        Event firstEvent = result.getNextEvents().get(0);
        if(nextEventsSize - 1 == 2)
        {
            assertEquals("loadRecords", firstEvent.getName());
            DBObject dataObj = (DBObject)firstEvent.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_FOLDER_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(e3Path, (String) dataObj.get(FIELD_PATH));
            int value1 = (Integer) dataObj.get(FIELD_RECORDS_TO_CREATE);
            assertEquals(username, (String) dataObj.get(FIELD_SITE_MANAGER));

            Event secondEvent = result.getNextEvents().get(1);
            assertEquals("loadRecords", secondEvent.getName());
            dataObj = (DBObject)secondEvent.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_FOLDER_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(e4Path, (String) dataObj.get(FIELD_PATH));
            int value2 = (Integer) dataObj.get(FIELD_RECORDS_TO_CREATE);
            assertEquals(username, (String) dataObj.get(FIELD_SITE_MANAGER));
            assertEquals(recordsNumber, value1 + value2);
        }
        else
        {
            //in case that one of generated values is 0 we check that the scheduled event had created all records
            DBObject dataObj = (DBObject)firstEvent.getData();
            int value = (Integer) dataObj.get(FIELD_RECORDS_TO_CREATE);
            assertEquals(recordsNumber, value);
        }
        assertEquals("scheduleRecordLoaders", result.getNextEvents().get(nextEventsSize - 1).getName());
    }

    @Test
    public void testUploadRecordsWithNotExistentPreconfiguredSinglePath() throws Exception
    {
        int maxActiveLoaders = 8;
        int recordsNumber = 4;
        String configuredPath1 = "/e1/e2/e3";
        String paths = configuredPath1;
        String username = "bob";

        scheduleRecordLoaders.setUploadRecords(true);
        scheduleRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleRecordLoaders.setRecordsNumber(recordsNumber);
        scheduleRecordLoaders.setRecordFolderPaths(paths);
        scheduleRecordLoaders.setUsername(username);

        //file plan should be always there
        FolderData mockedFilePlanContainer = mock(FolderData.class);
        when(mockedFilePlanContainer.getId()).thenReturn("filePlanId");
        when(mockedFilePlanContainer.getContext()).thenReturn(EMPTY_CONTEXT);
        when(mockedFilePlanContainer.getPath()).thenReturn(RECORD_CONTAINER_PATH);
        when(mockedFileFolderService.getFolder(EMPTY_CONTEXT, RECORD_CONTAINER_PATH)).thenReturn(mockedFilePlanContainer);

        when(mockedRestApiFactory.getFilePlanComponentsAPI(any(UserModel.class))).thenReturn(mockedFilePlanComponentAPI);
        FilePlanComponent mockedFilePlanContaineFilePlanComponent = mock(FilePlanComponent.class);
        when(mockedFilePlanContaineFilePlanComponent.getId()).thenReturn("filePlanId");
        when(mockedFilePlanComponentAPI.getFilePlanComponent("filePlanId")).thenReturn(mockedFilePlanContaineFilePlanComponent);

        //e1 category
        FilePlanComponent mockedE1FilePlanComponent = mock(FilePlanComponent.class);
        when(mockedE1FilePlanComponent .getId()).thenReturn("e1Id");
        when(mockedFilePlanComponentAPI.getFilePlanComponent("e1Id")).thenReturn(mockedE1FilePlanComponent);
        when(mockedFilePlanComponentAPI.createFilePlanComponent(any(FilePlanComponent.class), eq("filePlanId"))).thenReturn(mockedE1FilePlanComponent);

        String e1Path = RECORD_CONTAINER_PATH + "/e1";
        FolderData mockedE1 = mock(FolderData.class);
        when(mockedE1.getId()).thenReturn("e1Id");
        when(mockedE1.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedE1.getPath()).thenReturn(e1Path);
        when(mockedFileFolderService.getFolder(RECORD_CATEGORY_CONTEXT, e1Path)).thenReturn(null)
                                                                        .thenReturn(mockedE1);
        when(mockedFileFolderService.getFolder("e1Id")).thenReturn(mockedE1);

        //e2 category
        FilePlanComponent mockedE2FilePlanComponent = mock(FilePlanComponent.class);
        when(mockedE2FilePlanComponent .getId()).thenReturn("e2Id");
        when(mockedFilePlanComponentAPI.getFilePlanComponent("e2Id")).thenReturn(mockedE2FilePlanComponent);
        when(mockedFilePlanComponentAPI.createFilePlanComponent(any(FilePlanComponent.class), eq("e1Id"))).thenReturn(mockedE2FilePlanComponent);

        String e2Path = RECORD_CONTAINER_PATH + "/e1/e2";
        FolderData mockedE2 = mock(FolderData.class);
        when(mockedE2.getId()).thenReturn("e2Id");
        when(mockedE2.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedE2.getPath()).thenReturn(e2Path);
        when(mockedFileFolderService.getFolder(RECORD_CATEGORY_CONTEXT, e2Path)).thenReturn(null)
        .thenReturn(mockedE2);
        when(mockedFileFolderService.getFolder("e2Id")).thenReturn(mockedE2);

        //e3 folder
        FilePlanComponent mockedE3FilePlanComponent = mock(FilePlanComponent.class);
        when(mockedE3FilePlanComponent .getId()).thenReturn("e3Id");
        when(mockedFilePlanComponentAPI.getFilePlanComponent("e3Id")).thenReturn(mockedE3FilePlanComponent);

        String e3Path = RECORD_CONTAINER_PATH + "/e1/e2/e3";
        FolderData mockedE3 = mock(FolderData.class);
        when(mockedE3.getId()).thenReturn("e3Id");
        when(mockedE3.getContext()).thenReturn(RECORD_FOLDER_CONTEXT);
        when(mockedE3.getPath()).thenReturn(e3Path);
        when(mockedFileFolderService.getFolder(RECORD_FOLDER_CONTEXT, e3Path)).thenReturn(null)
                                                                        .thenReturn(null)
                                                                        .thenReturn(mockedE3);
        when(mockedFileFolderService.getFolder("e3Id")).thenReturn(mockedE3);
        when(mockedFilePlanComponentAPI.createFilePlanComponent(any(FilePlanComponent.class), eq("e2Id"))).thenReturn(mockedE3FilePlanComponent);

        EventResult result = scheduleRecordLoaders.processEvent(null, new StopWatch());
        verify(mockedFileFolderService, never()).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));
        verify(mockedFileFolderService, times(3)).createNewFolder(any(String.class), any(String.class), any(String.class));
        verify(mockedFileFolderService, times(3)).incrementFolderCount(any(String.class), any(String.class), eq(1L));
        verify(mockedFileFolderService, times(3)).getFolder(any(String.class));
        verify(mockedFileFolderService, times(1)).createNewFolder(any(FolderData.class));
        verify(mockedSessionService, times(1)).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Raised further 1 events and rescheduled self.", result.getData());
        assertEquals(2, result.getNextEvents().size());

        Event firstEvent = result.getNextEvents().get(0);
        assertEquals("loadRecords", firstEvent.getName());
        DBObject dataObj = (DBObject)firstEvent.getData();
        assertNotNull(dataObj);
        assertEquals(RECORD_FOLDER_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
        assertEquals(e3Path, (String) dataObj.get(FIELD_PATH));
        int value = (Integer) dataObj.get(FIELD_RECORDS_TO_CREATE);
        assertEquals(recordsNumber, value);
        assertEquals(username, (String) dataObj.get(FIELD_SITE_MANAGER));
        assertEquals("scheduleRecordLoaders", result.getNextEvents().get(1).getName());
    }

    @Test
    public void testUploadRecordsWithNotExistentOnePathElementPreconfiguredSinglePath() throws Exception
    {
        int maxActiveLoaders = 8;
        int recordsNumber = 4;
        String configuredPath1 = "/e1";
        String paths = configuredPath1;
        String username = "bob";

        scheduleRecordLoaders.setUploadRecords(true);
        scheduleRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleRecordLoaders.setRecordsNumber(recordsNumber);
        scheduleRecordLoaders.setRecordFolderPaths(paths);
        scheduleRecordLoaders.setUsername(username);

        //file plan should be always there
        FolderData mockedFilePlanContainer = mock(FolderData.class);
        when(mockedFilePlanContainer.getId()).thenReturn("filePlanId");
        when(mockedFilePlanContainer.getContext()).thenReturn(EMPTY_CONTEXT);
        when(mockedFilePlanContainer.getPath()).thenReturn(RECORD_CONTAINER_PATH);
        when(mockedFileFolderService.getFolder(EMPTY_CONTEXT, RECORD_CONTAINER_PATH)).thenReturn(mockedFilePlanContainer);

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
        when(mockedFileFolderService.getFoldersByCounts(RECORD_FOLDER_CONTEXT, null, null, null, null, null, null, 100, 100)).thenReturn(new ArrayList<FolderData>());

        when(mockedRestApiFactory.getFilePlanComponentsAPI(any(UserModel.class))).thenReturn(mockedFilePlanComponentAPI);
        FilePlanComponent mockedFilePlanContaineFilePlanComponent = mock(FilePlanComponent.class);
        when(mockedFilePlanContaineFilePlanComponent.getId()).thenReturn("filePlanId");
        when(mockedFilePlanComponentAPI.getFilePlanComponent("filePlanId")).thenReturn(mockedFilePlanContaineFilePlanComponent);

        //throw exception when trying to create record folder directly in filePlan
        Mockito.doThrow(new Exception("someError")).when(mockedFilePlanComponentAPI).createFilePlanComponent(any(FilePlanComponent.class), eq("filePlanId"));

        String e1Path = RECORD_CONTAINER_PATH + "/e1";
        when(mockedFileFolderService.getFolder(RECORD_FOLDER_CONTEXT, e1Path)).thenReturn(null);

        EventResult result = scheduleRecordLoaders.processEvent(null, new StopWatch());
        verify(mockedFileFolderService, never()).getFolder(any(String.class));
        verify(mockedFileFolderService, never()).incrementFolderCount(any(String.class), any(String.class), eq(1L));
        verify(mockedFileFolderService, never()).createNewFolder(any(String.class), any(String.class), any(String.class));
        verify(mockedFileFolderService, times(2)).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));

        // using the number of events here because the algorithm for distributing records to folders in this test, 2 folders can generate (0,4),(1,3),(2,2),(3,1) or (4,0)
        // and if the number of records to create is 0 the event for loading will not be scheduled
        int nextEventsSize = result.getNextEvents().size();
        verify(mockedFileFolderService, times(nextEventsSize - 1)).createNewFolder(any(FolderData.class));
        verify(mockedSessionService, times(nextEventsSize - 1)).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Raised further " + (nextEventsSize -1) + " events and rescheduled self.", result.getData());

        Event firstEvent = result.getNextEvents().get(0);
        if(nextEventsSize - 1 == 2)
        {
            assertEquals("loadRecords", firstEvent.getName());
            DBObject dataObj = (DBObject)firstEvent.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_FOLDER_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(path1, (String) dataObj.get(FIELD_PATH));
            int value1 = (Integer) dataObj.get(FIELD_RECORDS_TO_CREATE);
            assertEquals(username, (String) dataObj.get(FIELD_SITE_MANAGER));

            Event secondEvent = result.getNextEvents().get(1);
            assertEquals("loadRecords", secondEvent.getName());
            dataObj = (DBObject)secondEvent.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_FOLDER_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(path2, (String) dataObj.get(FIELD_PATH));
            int value2 = (Integer) dataObj.get(FIELD_RECORDS_TO_CREATE);
            assertEquals(username, (String) dataObj.get(FIELD_SITE_MANAGER));
            assertEquals(recordsNumber, value1 + value2);
        }
        else
        {
            //in case that one of generated values is 0 we check that the scheduled event had created all records
            DBObject dataObj = (DBObject)firstEvent.getData();
            int value = (Integer) dataObj.get(FIELD_RECORDS_TO_CREATE);
            assertEquals(recordsNumber, value);
        }
        assertEquals("scheduleRecordLoaders", result.getNextEvents().get(nextEventsSize - 1).getName());
    }

    @Test
    public void testUploadRecordsWithExceptionWhenCreatingPreconfiguredPaths() throws Exception
    {
        int maxActiveLoaders = 8;
        int recordsNumber = 4;
        String configuredPath1 = "/e1/e2/e3";
        String configuredPath2 = "/e1/e2/e4";
        String paths = configuredPath1 + "," + configuredPath2;
        String username = "bob";

        scheduleRecordLoaders.setUploadRecords(true);
        scheduleRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleRecordLoaders.setRecordsNumber(recordsNumber);
        scheduleRecordLoaders.setRecordFolderPaths(paths);
        scheduleRecordLoaders.setUsername(username);

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

        when(mockedRestApiFactory.getFilePlanComponentsAPI(any(UserModel.class))).thenReturn(mockedFilePlanComponentAPI);

        //returns available record folders
        when(mockedFileFolderService.getFoldersByCounts(RECORD_FOLDER_CONTEXT, null, null, null, null, null, null, 0, 100)).thenReturn(Arrays.asList(mockedRecordFolder1, mockedRecordFolder2));
        when(mockedFileFolderService.getFoldersByCounts(RECORD_FOLDER_CONTEXT, null, null, null, null, null, null, 100, 100)).thenReturn(new ArrayList<FolderData>());

        EventResult result = scheduleRecordLoaders.processEvent(null, new StopWatch());
        verify(mockedFileFolderService, times(2)).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));

        // using the number of events here because the algorithm for distributing records to folders in this test, 2 folders can generate (0,4),(1,3),(2,2),(3,1) or (4,0)
        // and if the number of records to create is 0 the event for loading will not be scheduled
        int nextEventsSize = result.getNextEvents().size();
        verify(mockedFileFolderService, times(nextEventsSize - 1)).createNewFolder(any(FolderData.class));
        verify(mockedSessionService, times(nextEventsSize - 1)).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Raised further " + (nextEventsSize -1) + " events and rescheduled self.", result.getData());

        Event firstEvent = result.getNextEvents().get(0);
        if(nextEventsSize - 1 == 2)
        {
            assertEquals("loadRecords", firstEvent.getName());
            DBObject dataObj = (DBObject)firstEvent.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_FOLDER_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(path1, (String) dataObj.get(FIELD_PATH));
            int value1 = (Integer) dataObj.get(FIELD_RECORDS_TO_CREATE);
            assertEquals(username, (String) dataObj.get(FIELD_SITE_MANAGER));

            Event secondEvent = result.getNextEvents().get(1);
            assertEquals("loadRecords", secondEvent.getName());
            dataObj = (DBObject)secondEvent.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_FOLDER_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(path2, (String) dataObj.get(FIELD_PATH));
            int value2 = (Integer) dataObj.get(FIELD_RECORDS_TO_CREATE);
            assertEquals(username, (String) dataObj.get(FIELD_SITE_MANAGER));
            assertEquals(recordsNumber, value1 + value2);
        }
        else
        {
            //in case that one of generated values is 0 we check that the scheduled event had created all records
            DBObject dataObj = (DBObject)firstEvent.getData();
            int value = (Integer) dataObj.get(FIELD_RECORDS_TO_CREATE);
            assertEquals(recordsNumber, value);
        }
        assertEquals("scheduleRecordLoaders", result.getNextEvents().get(nextEventsSize - 1).getName());
    }
}