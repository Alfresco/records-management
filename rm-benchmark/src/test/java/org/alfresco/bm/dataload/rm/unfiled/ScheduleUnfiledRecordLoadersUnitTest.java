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

import java.util.ArrayList;
import java.util.Arrays;

import com.mongodb.DBObject;

import org.alfresco.bm.cm.FileFolderService;
import org.alfresco.bm.cm.FolderData;
import org.alfresco.bm.dataload.RMEventConstants;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.session.SessionService;
import org.alfresco.rest.core.RestAPIFactory;
import org.alfresco.rest.rm.community.model.unfiledcontainer.UnfiledContainer;
import org.alfresco.rest.rm.community.model.unfiledcontainer.UnfiledContainerChild;
import org.alfresco.rest.rm.community.requests.gscore.api.UnfiledContainerAPI;
import org.alfresco.rest.rm.community.requests.gscore.api.UnfiledRecordFolderAPI;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

/**
 * Unit tests for ScheduleUnfiledRecordLoaders
 * @author Silviu Dinuta
 * @since 2.6
 */
@RunWith(MockitoJUnitRunner.class)
public class ScheduleUnfiledRecordLoadersUnitTest implements RMEventConstants
{
    private static final String EVENT_COMPLETE = "testLoadingUnfiledRecordsComplete";

    private static final String EVENT_LOAD_UNFILED_RECORD = "testLoadUnfiledRecord";

    private static final String EVENT_SCHEDULE_UNFILED_RECORD_LOADERS = "testScheduleUnfiledRecordLoaders";

    @Mock
    private SessionService mockedSessionService;

    @Mock
    private FileFolderService mockedFileFolderService;

    @Mock
    private RestAPIFactory mockedRestApiFactory;

    @Mock
    private ApplicationContext mockedApplicationContext;

    @Mock
    private UnfiledContainerAPI mockedUnfiledContainerAPI;

    @Mock
    private UnfiledRecordFolderAPI mockedUnfiledRecorFolderAPI;

    @InjectMocks
    private ScheduleUnfiledRecordLoaders scheduleUnfiledRecordLoaders;

    @Before
    public void before()
    {
        MockitoAnnotations.initMocks(this);
        scheduleUnfiledRecordLoaders.setEventNameLoadingComplete(EVENT_COMPLETE);
        scheduleUnfiledRecordLoaders.setEventNameLoadUnfiledRecords(EVENT_LOAD_UNFILED_RECORD);
        scheduleUnfiledRecordLoaders.setEventNameScheduleLoaders(EVENT_SCHEDULE_UNFILED_RECORD_LOADERS);
        scheduleUnfiledRecordLoaders.setLoadCheckDelay(0L);
    }

    @Test
    public void testUploadRecordsNotWanted() throws Exception
    {
        scheduleUnfiledRecordLoaders.setUploadUnfiledRecords(false);
        EventResult result = scheduleUnfiledRecordLoaders.processEvent(null, new StopWatch());

        verify(mockedFileFolderService, never()).createNewFolder(any(FolderData.class));
        verify(mockedFileFolderService, never()).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Uploading of Unfiled Records not wanted.",result.getData());
        assertEquals(1, result.getNextEvents().size());
    }

    @Test
    public void testUploadNoRecords() throws Exception
    {
        int maxActiveLoaders = 8;
        int unfiledRecordsNumber = 0;

        scheduleUnfiledRecordLoaders.setUploadUnfiledRecords(true);
        scheduleUnfiledRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleUnfiledRecordLoaders.setUnfiledRecordsNumber(unfiledRecordsNumber);

        EventResult result = scheduleUnfiledRecordLoaders.processEvent(null, new StopWatch());

        verify(mockedFileFolderService, never()).createNewFolder(any(FolderData.class));
        verify(mockedFileFolderService, never()).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Loading completed.  Raising 'done' event.",result.getData());
        assertEquals(1, result.getNextEvents().size());
        assertEquals(EVENT_COMPLETE, result.getNextEvents().get(0).getName());
    }

    @Test
    public void testUploadRecordsWithNoPreconfiguredPaths() throws Exception
    {
        int maxActiveLoaders = 8;
        int unfiledRecordsNumber = 4;
        String paths = "";

        scheduleUnfiledRecordLoaders.setUploadUnfiledRecords(true);
        scheduleUnfiledRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleUnfiledRecordLoaders.setUnfiledRecordsNumber(unfiledRecordsNumber);
        scheduleUnfiledRecordLoaders.setUnfiledRecordFolderPaths(paths);

        FolderData mockedUnfiledRecordContainer = mock(FolderData.class);
        when(mockedUnfiledRecordContainer.getId()).thenReturn("unfiledRecordContainerId");
        when(mockedUnfiledRecordContainer.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedUnfiledRecordContainer.getPath()).thenReturn(UNFILED_RECORD_CONTAINER_PATH);
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, UNFILED_RECORD_CONTAINER_PATH)).thenReturn(mockedUnfiledRecordContainer);

        FolderData mockedUnfiledRecordFolder = mock(FolderData.class);
        when(mockedUnfiledRecordFolder.getId()).thenReturn("folderId");
        when(mockedUnfiledRecordFolder.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedUnfiledRecordFolder.getPath()).thenReturn(UNFILED_RECORD_CONTAINER_PATH + "/folder1");
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, UNFILED_RECORD_CONTAINER_PATH + "/folder1")).thenReturn(mockedUnfiledRecordFolder);

        when(mockedRestApiFactory.getUnfiledContainersAPI()).thenReturn(mockedUnfiledContainerAPI);
        when(mockedRestApiFactory.getUnfiledRecordFoldersAPI()).thenReturn(mockedUnfiledRecorFolderAPI);

        //returns unfiled record container here, this is always available plus another unfiled record folder
        when(mockedFileFolderService.getFoldersByCounts(UNFILED_CONTEXT, null, null, null, null, null, null, 0, 100)).thenReturn(Arrays.asList(mockedUnfiledRecordContainer, mockedUnfiledRecordFolder));
        when(mockedFileFolderService.getFoldersByCounts(UNFILED_CONTEXT, null, null, null, null, null, null, 100, 100)).thenReturn(new ArrayList<>());

        EventResult result = scheduleUnfiledRecordLoaders.processEvent(null, new StopWatch());

        verify(mockedFileFolderService, times(2)).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));

        verify(mockedSessionService, times(4)).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Raised further " + (unfiledRecordsNumber) + " events and rescheduled self.", result.getData());
        assertEquals(unfiledRecordsNumber + 1, result.getNextEvents().size());

        for(int i = 0; i< unfiledRecordsNumber ; i++)
        {
            Event event = result.getNextEvents().get(0);
            assertEquals(EVENT_LOAD_UNFILED_RECORD, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(UNFILED_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(LOAD_UNFILED_RECORD_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
        }
        assertEquals(EVENT_SCHEDULE_UNFILED_RECORD_LOADERS, result.getNextEvents().get(unfiledRecordsNumber).getName());
    }

    @Test
    public void testUploadRecordsWithExistentPreconfiguredPaths() throws Exception
    {
        int maxActiveLoaders = 8;
        int unfiledRecordsNumber = 4;
        String configuredPath1 = "e1/e2/e3";
        String configuredPath2 = "/e1/e2/e4";
        String entirePath1 = UNFILED_RECORD_CONTAINER_PATH + configuredPath1;
        String entirePath2 = UNFILED_RECORD_CONTAINER_PATH + configuredPath2;
        String paths = configuredPath1 + "," + configuredPath2;

        scheduleUnfiledRecordLoaders.setUploadUnfiledRecords(true);
        scheduleUnfiledRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleUnfiledRecordLoaders.setUnfiledRecordsNumber(unfiledRecordsNumber);
        scheduleUnfiledRecordLoaders.setUnfiledRecordFolderPaths(paths);

        FolderData mockedUnfiledRecordFolder = mock(FolderData.class);
        when(mockedUnfiledRecordFolder.getId()).thenReturn("folderId1");
        when(mockedUnfiledRecordFolder.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedUnfiledRecordFolder.getPath()).thenReturn(entirePath1);
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, entirePath1)).thenReturn(mockedUnfiledRecordFolder);

        FolderData mockedUnfiledRecordFolder1 = mock(FolderData.class);
        when(mockedUnfiledRecordFolder1.getId()).thenReturn("newfolderId2");
        when(mockedUnfiledRecordFolder1.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedUnfiledRecordFolder1.getPath()).thenReturn(entirePath2);
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, entirePath2)).thenReturn(mockedUnfiledRecordFolder1);

        when(mockedRestApiFactory.getUnfiledContainersAPI()).thenReturn(mockedUnfiledContainerAPI);
        when(mockedRestApiFactory.getUnfiledRecordFoldersAPI()).thenReturn(mockedUnfiledRecorFolderAPI);

        EventResult result = scheduleUnfiledRecordLoaders.processEvent(null, new StopWatch());
        verify(mockedFileFolderService, never()).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));
        verify(mockedSessionService, times(4)).startSession(any(DBObject.class));
        assertEquals(true, result.isSuccess());

        assertEquals("Raised further 4 events and rescheduled self.", result.getData());
        assertEquals(5,result.getNextEvents().size());

        for(int i = 0; i< unfiledRecordsNumber ; i++)
        {
            Event event = result.getNextEvents().get(0);
            assertEquals(EVENT_LOAD_UNFILED_RECORD, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(UNFILED_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(LOAD_UNFILED_RECORD_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
        }
        assertEquals(EVENT_SCHEDULE_UNFILED_RECORD_LOADERS, result.getNextEvents().get(4).getName());
    }

    @Test
    public void testUploadRecordsWithExistentPathsOneChildOfTheOther() throws Exception
    {
        int maxActiveLoaders = 8;
        int unfiledRecordsNumber = 4;
        String configuredPath1 = "/e1/e2/e3";
        String configuredPath2 = "/e1/e2/e3/e4";
        String entirePath1 = UNFILED_RECORD_CONTAINER_PATH + configuredPath1;
        String entirePath2 = UNFILED_RECORD_CONTAINER_PATH + configuredPath2;
        String paths = configuredPath1 + "," + configuredPath2;

        scheduleUnfiledRecordLoaders.setUploadUnfiledRecords(true);
        scheduleUnfiledRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleUnfiledRecordLoaders.setUnfiledRecordsNumber(unfiledRecordsNumber);
        scheduleUnfiledRecordLoaders.setUnfiledRecordFolderPaths(paths);

        FolderData mockedUnfiledRecordFolder = mock(FolderData.class);
        when(mockedUnfiledRecordFolder.getId()).thenReturn("folderId1");
        when(mockedUnfiledRecordFolder.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedUnfiledRecordFolder.getPath()).thenReturn(entirePath1);
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, entirePath1)).thenReturn(mockedUnfiledRecordFolder);

        FolderData mockedUnfiledRecordFolder1 = mock(FolderData.class);
        when(mockedUnfiledRecordFolder1.getId()).thenReturn("newfolderId2");
        when(mockedUnfiledRecordFolder1.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedUnfiledRecordFolder1.getPath()).thenReturn(entirePath2);
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, entirePath2)).thenReturn(mockedUnfiledRecordFolder1);

        when(mockedFileFolderService.getChildFolders(UNFILED_CONTEXT, entirePath1, 0, 100)).thenReturn(Arrays.asList(mockedUnfiledRecordFolder1));
        when(mockedFileFolderService.getChildFolders(UNFILED_CONTEXT, entirePath1, 100, 100)).thenReturn(new ArrayList<>());
        when(mockedFileFolderService.getChildFolders(UNFILED_CONTEXT, entirePath2, 0, 100)).thenReturn(new ArrayList<>());

        when(mockedRestApiFactory.getUnfiledContainersAPI()).thenReturn(mockedUnfiledContainerAPI);
        when(mockedRestApiFactory.getUnfiledRecordFoldersAPI()).thenReturn(mockedUnfiledRecorFolderAPI);

        EventResult result = scheduleUnfiledRecordLoaders.processEvent(null, new StopWatch());
        verify(mockedFileFolderService, never()).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));
        verify(mockedFileFolderService, times(4)).getChildFolders(any(String.class), any(String.class), any(Integer.class), any(Integer.class));
        assertEquals(true, result.isSuccess());

        verify(mockedSessionService, times(4)).startSession(any(DBObject.class));
        assertEquals("Raised further " + (unfiledRecordsNumber) + " events and rescheduled self.", result.getData());
        assertEquals(unfiledRecordsNumber+1, result.getNextEvents().size());

        for(int i = 0; i< unfiledRecordsNumber ; i++)
        {
            Event event = result.getNextEvents().get(0);
            assertEquals(EVENT_LOAD_UNFILED_RECORD, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(UNFILED_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(LOAD_UNFILED_RECORD_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
        }
        assertEquals(EVENT_SCHEDULE_UNFILED_RECORD_LOADERS, result.getNextEvents().get(unfiledRecordsNumber).getName());
    }

    @Test
    public void testUploadRecordsWithExistentPathWithOneChildInside() throws Exception
    {
        int maxActiveLoaders = 8;
        int unfiledRecordsNumber = 4;
        String configuredPath1 = "/e1/e2/e3";
        String entirePath1 = UNFILED_RECORD_CONTAINER_PATH + configuredPath1;
        String paths = configuredPath1;

        scheduleUnfiledRecordLoaders.setUploadUnfiledRecords(true);
        scheduleUnfiledRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleUnfiledRecordLoaders.setUnfiledRecordsNumber(unfiledRecordsNumber);
        scheduleUnfiledRecordLoaders.setUnfiledRecordFolderPaths(paths);

        FolderData mockedUnfiledRecordFolder = mock(FolderData.class);
        when(mockedUnfiledRecordFolder.getId()).thenReturn("folderId1");
        when(mockedUnfiledRecordFolder.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedUnfiledRecordFolder.getPath()).thenReturn(entirePath1);
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, entirePath1)).thenReturn(mockedUnfiledRecordFolder);

        //child of configured path
        String childPath = entirePath1 + "/" + "child1";
        FolderData mockedUnfiledRecordFolder1 = mock(FolderData.class);
        when(mockedUnfiledRecordFolder1.getId()).thenReturn("newfolderId2");
        when(mockedUnfiledRecordFolder1.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedUnfiledRecordFolder1.getPath()).thenReturn(childPath);

        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, childPath)).thenReturn(mockedUnfiledRecordFolder1);
        when(mockedFileFolderService.getChildFolders(UNFILED_CONTEXT, entirePath1, 0, 100)).thenReturn(Arrays.asList(mockedUnfiledRecordFolder1));
        when(mockedFileFolderService.getChildFolders(UNFILED_CONTEXT, entirePath1, 100, 100)).thenReturn(new ArrayList<>());
        when(mockedFileFolderService.getChildFolders(UNFILED_CONTEXT, childPath, 0, 100)).thenReturn(new ArrayList<>());

        when(mockedRestApiFactory.getUnfiledContainersAPI()).thenReturn(mockedUnfiledContainerAPI);
        when(mockedRestApiFactory.getUnfiledRecordFoldersAPI()).thenReturn(mockedUnfiledRecorFolderAPI);

        EventResult result = scheduleUnfiledRecordLoaders.processEvent(null, new StopWatch());
        verify(mockedFileFolderService, never()).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));
        verify(mockedFileFolderService, times(3)).getChildFolders(any(String.class), any(String.class), any(Integer.class), any(Integer.class));
        assertEquals(true, result.isSuccess());

        verify(mockedSessionService, times(4)).startSession(any(DBObject.class));
        assertEquals("Raised further 4 events and rescheduled self.", result.getData());
        assertEquals(5, result.getNextEvents().size());

        for(int i = 0; i< unfiledRecordsNumber ; i++)
        {
            Event event = result.getNextEvents().get(0);
            assertEquals(EVENT_LOAD_UNFILED_RECORD, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(UNFILED_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(LOAD_UNFILED_RECORD_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
        }
        assertEquals(EVENT_SCHEDULE_UNFILED_RECORD_LOADERS, result.getNextEvents().get(unfiledRecordsNumber).getName());
    }

    @Test
    public void testUploadRecordsWithNotExistentPreconfiguredPaths() throws Exception
    {
        int maxActiveLoaders = 3;
        int unfiledRecordsNumber = 4;
        String configuredPath1 = "/e1/e2/e3";
        String configuredPath2 = "/e1/e2/e4";
        String paths = configuredPath1 + "," + configuredPath2;

        scheduleUnfiledRecordLoaders.setUploadUnfiledRecords(true);
        scheduleUnfiledRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleUnfiledRecordLoaders.setUnfiledRecordsNumber(unfiledRecordsNumber);
        scheduleUnfiledRecordLoaders.setUnfiledRecordFolderPaths(paths);

        //unfiledRecord should be always there
        FolderData mockedUnfiledRecordContainer = mock(FolderData.class);
        when(mockedUnfiledRecordContainer.getId()).thenReturn("unfiledRecordContainerId");
        when(mockedUnfiledRecordContainer.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedUnfiledRecordContainer.getPath()).thenReturn(UNFILED_RECORD_CONTAINER_PATH);
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, UNFILED_RECORD_CONTAINER_PATH)).thenReturn(mockedUnfiledRecordContainer);

        when(mockedRestApiFactory.getUnfiledContainersAPI()).thenReturn(mockedUnfiledContainerAPI);
        when(mockedRestApiFactory.getUnfiledRecordFoldersAPI()).thenReturn(mockedUnfiledRecorFolderAPI);

        UnfiledContainer mockedUnfiledContainer = mock(UnfiledContainer.class);
        when(mockedUnfiledContainer.getId()).thenReturn("unfiledRecordContainerId");
        when(mockedUnfiledContainerAPI.getUnfiledContainer("unfiledRecordContainerId")).thenReturn(mockedUnfiledContainer);

        //e1 folder
        UnfiledContainerChild mockedE1RootUnfiledRecordFolder = mock(UnfiledContainerChild.class);
        when(mockedE1RootUnfiledRecordFolder.getId()).thenReturn("e1Id");
        when(mockedUnfiledContainerAPI.createUnfiledContainerChild(any(UnfiledContainerChild.class), eq("unfiledRecordContainerId"))).thenReturn(mockedE1RootUnfiledRecordFolder);

        String e1Path = UNFILED_RECORD_CONTAINER_PATH + "/e1";
        FolderData mockedE1 = mock(FolderData.class);
        when(mockedE1.getId()).thenReturn("e1Id");
        when(mockedE1.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedE1.getPath()).thenReturn(e1Path);
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, e1Path)).thenReturn(null)
                                                                        .thenReturn(mockedE1);
        when(mockedFileFolderService.getFolder("e1Id")).thenReturn(mockedE1);

        //e2 folder
        UnfiledContainerChild mockedE2ChildunfiledRecordFolder = mock(UnfiledContainerChild.class);
        when(mockedE2ChildunfiledRecordFolder .getId()).thenReturn("e2Id");
        when(mockedUnfiledRecorFolderAPI.createUnfiledRecordFolderChild(any(UnfiledContainerChild.class), eq("e1Id"))).thenReturn(mockedE2ChildunfiledRecordFolder);

        String e2Path = UNFILED_RECORD_CONTAINER_PATH + "/e1/e2";
        FolderData mockedE2 = mock(FolderData.class);
        when(mockedE2.getId()).thenReturn("e2Id");
        when(mockedE2.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedE2.getPath()).thenReturn(e2Path);
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, e2Path)).thenReturn(null)
        .thenReturn(mockedE2);
        when(mockedFileFolderService.getFolder("e2Id")).thenReturn(mockedE2);

        //e3 folder
        UnfiledContainerChild mockedE3FilePlanComponent = mock(UnfiledContainerChild.class);
        when(mockedE3FilePlanComponent .getId()).thenReturn("e3Id");

        String e3Path = UNFILED_RECORD_CONTAINER_PATH + "/e1/e2/e3";
        FolderData mockedE3 = mock(FolderData.class);
        when(mockedE3.getId()).thenReturn("e3Id");
        when(mockedE3.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedE3.getPath()).thenReturn(e3Path);
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, e3Path)).thenReturn(null)
                                                                        .thenReturn(null)
                                                                        .thenReturn(mockedE3);
        when(mockedFileFolderService.getFolder("e3Id")).thenReturn(mockedE3);

        //e4 folder
        UnfiledContainerChild mockedE4FilePlanComponent = mock(UnfiledContainerChild.class);
        when(mockedE4FilePlanComponent .getId()).thenReturn("e4Id");

        String e4Path = UNFILED_RECORD_CONTAINER_PATH + "/e1/e2/e4";
        FolderData mockedE4 = mock(FolderData.class);
        when(mockedE4.getId()).thenReturn("e4Id");
        when(mockedE4.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedE4.getPath()).thenReturn(e4Path);
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, e4Path)).thenReturn(null)
                                                                        .thenReturn(null)
                                                                        .thenReturn(mockedE4);
        when(mockedFileFolderService.getFolder("e4Id")).thenReturn(mockedE4);

        when(mockedUnfiledRecorFolderAPI.createUnfiledRecordFolderChild(any(UnfiledContainerChild.class), eq("e2Id"))).thenReturn(mockedE3FilePlanComponent)
                                                                                                          .thenReturn(mockedE4FilePlanComponent);

        when(mockedApplicationContext.getBean("restAPIFactory", RestAPIFactory.class)).thenReturn(mockedRestApiFactory);
        EventResult result = scheduleUnfiledRecordLoaders.processEvent(null, new StopWatch());
        verify(mockedFileFolderService, never()).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));
        verify(mockedFileFolderService, times(4)).createNewFolder(any(String.class), any(String.class), any(String.class));
        verify(mockedFileFolderService, times(4)).incrementFolderCount(any(String.class), any(String.class), eq(1L));
        verify(mockedFileFolderService, times(4)).getFolder(any(String.class));
        verify(mockedSessionService, times(3)).startSession(any(DBObject.class));
        assertEquals(true, result.isSuccess());

        assertEquals("Raised further " + (maxActiveLoaders) + " events and rescheduled self.", result.getData());
        assertEquals(maxActiveLoaders + 1, result.getNextEvents().size());

        for(int i = 0; i < maxActiveLoaders ; i++)
        {
            Event event = result.getNextEvents().get(0);
            assertEquals(EVENT_LOAD_UNFILED_RECORD, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(UNFILED_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(LOAD_UNFILED_RECORD_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
        }
        assertEquals(EVENT_SCHEDULE_UNFILED_RECORD_LOADERS, result.getNextEvents().get(maxActiveLoaders).getName());
    }

    @Test
    public void testUploadRecordsWithNotExistentPreconfiguredSinglePath() throws Exception
    {
        int maxActiveLoaders = 8;
        int unfiledRecordsNumber = 4;
        String configuredPath1 = "/e1/e2/e3";
        String paths = configuredPath1;

        scheduleUnfiledRecordLoaders.setUploadUnfiledRecords(true);
        scheduleUnfiledRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleUnfiledRecordLoaders.setUnfiledRecordsNumber(unfiledRecordsNumber);
        scheduleUnfiledRecordLoaders.setUnfiledRecordFolderPaths(paths);

        //unfiledRecord should be always there
        FolderData mockedUnfiledRecordContainer = mock(FolderData.class);
        when(mockedUnfiledRecordContainer.getId()).thenReturn("unfiledRecordContainerId");
        when(mockedUnfiledRecordContainer.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedUnfiledRecordContainer.getPath()).thenReturn(UNFILED_RECORD_CONTAINER_PATH);
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, UNFILED_RECORD_CONTAINER_PATH)).thenReturn(mockedUnfiledRecordContainer);

        when(mockedRestApiFactory.getUnfiledContainersAPI()).thenReturn(mockedUnfiledContainerAPI);
        when(mockedRestApiFactory.getUnfiledRecordFoldersAPI()).thenReturn(mockedUnfiledRecorFolderAPI);

        UnfiledContainer mockedUnfiledContainer = mock(UnfiledContainer.class);
        when(mockedUnfiledContainer.getId()).thenReturn("unfiledRecordContainerId");
        when(mockedUnfiledContainerAPI.getUnfiledContainer("unfiledRecordContainerId")).thenReturn(mockedUnfiledContainer);

        //e1 folder
        UnfiledContainerChild mockedE1RootUnfiledRecordFolder = mock(UnfiledContainerChild.class);
        when(mockedE1RootUnfiledRecordFolder.getId()).thenReturn("e1Id");
        when(mockedUnfiledContainerAPI.createUnfiledContainerChild(any(UnfiledContainerChild.class), eq("unfiledRecordContainerId"))).thenReturn(mockedE1RootUnfiledRecordFolder);

        String e1Path = UNFILED_RECORD_CONTAINER_PATH + "/e1";
        FolderData mockedE1 = mock(FolderData.class);
        when(mockedE1.getId()).thenReturn("e1Id");
        when(mockedE1.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedE1.getPath()).thenReturn(e1Path);
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, e1Path)).thenReturn(null)
        .thenReturn(mockedE1);
        when(mockedFileFolderService.getFolder("e1Id")).thenReturn(mockedE1);

        //e2 folder
        UnfiledContainerChild mockedE2ChildunfiledRecordFolder = mock(UnfiledContainerChild.class);
        when(mockedE2ChildunfiledRecordFolder .getId()).thenReturn("e2Id");
        when(mockedUnfiledRecorFolderAPI.createUnfiledRecordFolderChild(any(UnfiledContainerChild.class), eq("e1Id"))).thenReturn(mockedE2ChildunfiledRecordFolder);

        String e2Path = UNFILED_RECORD_CONTAINER_PATH + "/e1/e2";
        FolderData mockedE2 = mock(FolderData.class);
        when(mockedE2.getId()).thenReturn("e2Id");
        when(mockedE2.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedE2.getPath()).thenReturn(e2Path);
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, e2Path)).thenReturn(null)
        .thenReturn(mockedE2);
        when(mockedFileFolderService.getFolder("e2Id")).thenReturn(mockedE2);

        //e3 folder
        UnfiledContainerChild mockedE3FilePlanComponent = mock(UnfiledContainerChild.class);
        when(mockedE3FilePlanComponent .getId()).thenReturn("e3Id");

        String e3Path = UNFILED_RECORD_CONTAINER_PATH + "/e1/e2/e3";
        FolderData mockedE3 = mock(FolderData.class);
        when(mockedE3.getId()).thenReturn("e3Id");
        when(mockedE3.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedE3.getPath()).thenReturn(e3Path);
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, e3Path)).thenReturn(null)
                                                                        .thenReturn(null)
                                                                        .thenReturn(mockedE3);
        when(mockedFileFolderService.getFolder("e3Id")).thenReturn(mockedE3);
        when(mockedUnfiledRecorFolderAPI.createUnfiledRecordFolderChild(any(UnfiledContainerChild.class), eq("e2Id"))).thenReturn(mockedE3FilePlanComponent);

        when(mockedApplicationContext.getBean("restAPIFactory", RestAPIFactory.class)).thenReturn(mockedRestApiFactory);
        EventResult result = scheduleUnfiledRecordLoaders.processEvent(null, new StopWatch());
        verify(mockedFileFolderService, never()).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));
        verify(mockedFileFolderService, times(3)).createNewFolder(any(String.class), any(String.class), any(String.class));
        verify(mockedFileFolderService, times(3)).incrementFolderCount(any(String.class), any(String.class), eq(1L));
        verify(mockedFileFolderService, times(3)).getFolder(any(String.class));
        verify(mockedSessionService, times(4)).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Raised further 4 events and rescheduled self.", result.getData());
        assertEquals(5, result.getNextEvents().size());

        for(int i = 0; i< unfiledRecordsNumber ; i++)
        {
            Event event = result.getNextEvents().get(0);
            assertEquals(EVENT_LOAD_UNFILED_RECORD, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(UNFILED_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(e3Path, (String) dataObj.get(FIELD_PATH));
            assertEquals(LOAD_UNFILED_RECORD_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
        }
        assertEquals(EVENT_SCHEDULE_UNFILED_RECORD_LOADERS, result.getNextEvents().get(4).getName());
    }

    @Test
    public void testUploadRecordsWithExceptionWhenCreatingPreconfiguredPaths() throws Exception
    {
        int maxActiveLoaders = 8;
        int unfiledRecordsNumber = 4;
        String configuredPath1 = "/e1/e2/e3";
        String configuredPath2 = "/e1/e2/e4";
        String paths = configuredPath1 + "," + configuredPath2;

        scheduleUnfiledRecordLoaders.setUploadUnfiledRecords(true);
        scheduleUnfiledRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleUnfiledRecordLoaders.setUnfiledRecordsNumber(unfiledRecordsNumber);
        scheduleUnfiledRecordLoaders.setUnfiledRecordFolderPaths(paths);

        FolderData mockedUnfiledRecordContainer = mock(FolderData.class);
        when(mockedUnfiledRecordContainer.getId()).thenReturn("unfiledRecordContainerId");
        when(mockedUnfiledRecordContainer.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedUnfiledRecordContainer.getPath()).thenReturn(UNFILED_RECORD_CONTAINER_PATH);
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, UNFILED_RECORD_CONTAINER_PATH)).thenReturn(mockedUnfiledRecordContainer);

        FolderData mockedUnfiledRecordFolder = mock(FolderData.class);
        when(mockedUnfiledRecordFolder.getId()).thenReturn("folderId");
        when(mockedUnfiledRecordFolder.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedUnfiledRecordFolder.getPath()).thenReturn(UNFILED_RECORD_CONTAINER_PATH + "/folder1");
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, UNFILED_RECORD_CONTAINER_PATH + "/folder1")).thenReturn(mockedUnfiledRecordFolder);

        when(mockedRestApiFactory.getUnfiledContainersAPI()).thenReturn(mockedUnfiledContainerAPI);
        when(mockedRestApiFactory.getUnfiledRecordFoldersAPI()).thenReturn(mockedUnfiledRecorFolderAPI);

        //returns unfiled record container here, this is always available plus another unfiled record folder
        when(mockedFileFolderService.getFoldersByCounts(UNFILED_CONTEXT, null, null, null, null, null, null, 0, 100)).thenReturn(Arrays.asList(mockedUnfiledRecordContainer, mockedUnfiledRecordFolder));
        when(mockedFileFolderService.getFoldersByCounts(UNFILED_CONTEXT, null, null, null, null, null, null, 100, 100)).thenReturn(new ArrayList<>());

        EventResult result = scheduleUnfiledRecordLoaders.processEvent(null, new StopWatch());
        verify(mockedFileFolderService, times(2)).getFoldersByCounts(any(String.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Long.class), any(Integer.class), any(Integer.class));

        verify(mockedSessionService, times(4)).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Raised further " + (unfiledRecordsNumber) + " events and rescheduled self.", result.getData());
        assertEquals(unfiledRecordsNumber + 1, result.getNextEvents().size());

        for(int i = 0; i < unfiledRecordsNumber ; i++)
        {
            Event event = result.getNextEvents().get(0);
            assertEquals(EVENT_LOAD_UNFILED_RECORD, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(UNFILED_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(LOAD_UNFILED_RECORD_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
        }
        assertEquals(EVENT_SCHEDULE_UNFILED_RECORD_LOADERS, result.getNextEvents().get(unfiledRecordsNumber).getName());
    }
}