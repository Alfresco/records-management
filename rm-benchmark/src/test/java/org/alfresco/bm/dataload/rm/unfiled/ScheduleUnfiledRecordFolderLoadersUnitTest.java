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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.mongodb.DBObject;

import org.alfresco.bm.cm.FileFolderService;
import org.alfresco.bm.cm.FolderData;
import org.alfresco.bm.dataload.RMEventConstants;
import org.alfresco.bm.dataload.rm.services.ExtendedFileFolderService;
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

/**
 * Unit tests for ScheduleUnfiledRecordFolderLoaders
 * @author Silviu Dinuta
 * @since 2.6
 */
@RunWith(MockitoJUnitRunner.class)
public class ScheduleUnfiledRecordFolderLoadersUnitTest implements RMEventConstants
{
    private static final String EVENT_LOAD_UNFILED_RECORD_FOLDER = "testLoadUnfiledRecordFolder";

    private static final String EVENT_COMPLETE = "testLoadingUnfiledRecordFoldersComplete";

    private static final String EVENT_SCHEDULE_SELF = "testScheduleUnfiledFoldersLoaders";

    private static final String EVENT_LOAD_ROOT_UNFILED_RECORD_FOLDER = "testLoadRootUnfiledRecordFolder";

    private static final String EVENT_CONTINUE_LOADING_DATA = "testScheduleUnfiledRecordLoaders";

    @Mock
    private SessionService mockedSessionService;

    @Mock
    private FileFolderService mockedFileFolderService;

    @Mock
    private ExtendedFileFolderService mockedAuxFileFolderService;

    @InjectMocks
    private ScheduleUnfiledRecordFolderLoaders scheduleUnfiledRecordFolderLoaders;

    @Before
    public void before()
    {
        MockitoAnnotations.initMocks(this);
        scheduleUnfiledRecordFolderLoaders.setEventNameLoadingComplete(EVENT_COMPLETE);
        scheduleUnfiledRecordFolderLoaders.setEventNameLoadRootUnfiledRecordFolder(EVENT_LOAD_ROOT_UNFILED_RECORD_FOLDER);
        scheduleUnfiledRecordFolderLoaders.setEventNameLoadUnfiledRecordFolder(EVENT_LOAD_UNFILED_RECORD_FOLDER);
        scheduleUnfiledRecordFolderLoaders.setEventNameContinueLoadingUnfiledRecords(EVENT_CONTINUE_LOADING_DATA);
        scheduleUnfiledRecordFolderLoaders.setEventNameScheduleLoaders(EVENT_SCHEDULE_SELF);
        scheduleUnfiledRecordFolderLoaders.setLoadCheckDelay(0L);
    }

    @Test
    public void testUnfiledRecordFoldersNotWanted() throws Exception
    {
        scheduleUnfiledRecordFolderLoaders.setCreateUnfiledRecordFolderStructure(false);
        EventResult result = scheduleUnfiledRecordFolderLoaders.processEvent(null, new StopWatch());

        verify(mockedFileFolderService, never()).createNewFolder(any(FolderData.class));
        verify(mockedSessionService, never()).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Unfiled Record Folders structure creation not wanted.",result.getData());
        assertEquals(1, result.getNextEvents().size());
    }

    @Test
    public void testUnfiledRecordFoldersNotWantedAndContinueLoadingData() throws Exception
    {
        scheduleUnfiledRecordFolderLoaders.setCreateUnfiledRecordFolderStructure(false);
        FolderData mockedFolder = mock(FolderData.class);
        List<FolderData> unfiledContainerChildren = Arrays.asList(mockedFolder);
        when(mockedFileFolderService.getChildFolders(UNFILED_CONTEXT, UNFILED_RECORD_CONTAINER_PATH, 0, 1)).thenReturn(unfiledContainerChildren);
        EventResult result = scheduleUnfiledRecordFolderLoaders.processEvent(null, new StopWatch());

        verify(mockedFileFolderService, never()).createNewFolder(any(FolderData.class));
        verify(mockedSessionService, never()).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Unfiled Record Folders structure creation not wanted, continue with loading unfiled records.",result.getData());
        assertEquals(1, result.getNextEvents().size());
        assertEquals(EVENT_CONTINUE_LOADING_DATA, result.getNextEvents().get(0).getName());
    }

    @Test
    public void testScheduleRootUnfiledRecordFolders() throws Exception
    {
        int maxActiveLoaders = 8;
        int rootUnfiledRecordFolderNumber = 4;
        int unfiledRecordFoldersDepth = 1;

        scheduleUnfiledRecordFolderLoaders.setCreateUnfiledRecordFolderStructure(true);
        scheduleUnfiledRecordFolderLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleUnfiledRecordFolderLoaders.setUnfiledRecordFolderDepth(unfiledRecordFoldersDepth);
        scheduleUnfiledRecordFolderLoaders.setRootUnfiledRecordFolderNumber(rootUnfiledRecordFolderNumber);

        FolderData unfiledContainerFolder = mock(FolderData.class);
        when(unfiledContainerFolder.getContext()).thenReturn(UNFILED_CONTEXT);
        when(unfiledContainerFolder.getPath()).thenReturn(UNFILED_RECORD_CONTAINER_PATH);
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, UNFILED_RECORD_CONTAINER_PATH)).thenReturn(unfiledContainerFolder);

        EventResult result = scheduleUnfiledRecordFolderLoaders.processEvent(null, new StopWatch());

        verify(mockedSessionService, times(4)).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Raised further 4 events and rescheduled self.",result.getData());
        assertEquals(5, result.getNextEvents().size());

        for(int i=0; i<4; i++)
        {
            Event event = result.getNextEvents().get(i);
            assertEquals(EVENT_LOAD_ROOT_UNFILED_RECORD_FOLDER, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(UNFILED_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(UNFILED_RECORD_CONTAINER_PATH, (String) dataObj.get(FIELD_PATH));
            assertEquals(LOAD_ROOT_UNFILED_RECORD_FOLDER_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
        }
        assertEquals(EVENT_SCHEDULE_SELF, result.getNextEvents().get(4).getName());
    }

    @Test
    public void testSchedule4RootUnfiledRecordFoldersWithMaxActiveLoaders3() throws Exception
    {
        int maxActiveLoaders = 3;
        int rootUnfiledRecordFolderNumber = 4;
        int unfiledRecordFoldersDepth = 1;

        scheduleUnfiledRecordFolderLoaders.setCreateUnfiledRecordFolderStructure(true);
        scheduleUnfiledRecordFolderLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleUnfiledRecordFolderLoaders.setUnfiledRecordFolderDepth(unfiledRecordFoldersDepth);
        scheduleUnfiledRecordFolderLoaders.setRootUnfiledRecordFolderNumber(rootUnfiledRecordFolderNumber);

        FolderData unfiledContainerFolder = mock(FolderData.class);
        when(unfiledContainerFolder.getContext()).thenReturn(UNFILED_CONTEXT);
        when(unfiledContainerFolder.getPath()).thenReturn(UNFILED_RECORD_CONTAINER_PATH);
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, UNFILED_RECORD_CONTAINER_PATH)).thenReturn(unfiledContainerFolder);

        EventResult result = scheduleUnfiledRecordFolderLoaders.processEvent(null, new StopWatch());

        verify(mockedSessionService, times(3)).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Raised further 3 events and rescheduled self.",result.getData());
        assertEquals(4, result.getNextEvents().size());

        for(int i=0; i<3; i++)
        {
            Event event = result.getNextEvents().get(i);
            assertEquals(EVENT_LOAD_ROOT_UNFILED_RECORD_FOLDER, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(UNFILED_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(UNFILED_RECORD_CONTAINER_PATH, (String) dataObj.get(FIELD_PATH));
            assertEquals(LOAD_ROOT_UNFILED_RECORD_FOLDER_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
        }
        assertEquals(EVENT_SCHEDULE_SELF, result.getNextEvents().get(3).getName());
    }

    @Test
    public void testScheduleRootUnfiledRecordFoldersWithDepth0() throws Exception
    {
        int maxActiveLoaders = 8;
        int rootUnfiledRecordFolderNumber = 4;
        int unfiledRecordFoldersDepth = 0;

        scheduleUnfiledRecordFolderLoaders.setCreateUnfiledRecordFolderStructure(true);
        scheduleUnfiledRecordFolderLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleUnfiledRecordFolderLoaders.setUnfiledRecordFolderDepth(unfiledRecordFoldersDepth);
        scheduleUnfiledRecordFolderLoaders.setRootUnfiledRecordFolderNumber(rootUnfiledRecordFolderNumber);
        FolderData mockedUnfiledRecordContainerFolder = mock(FolderData.class);
        when(mockedUnfiledRecordContainerFolder.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedUnfiledRecordContainerFolder.getPath()).thenReturn("/a");
        List<FolderData> folders = Arrays.asList(mockedUnfiledRecordContainerFolder);
        when(mockedFileFolderService.getFoldersByCounts(UNFILED_CONTEXT, 4L, 4L, 0L, Long.valueOf(rootUnfiledRecordFolderNumber-1), null, null, 0, 100)).thenReturn(folders);

        EventResult result = scheduleUnfiledRecordFolderLoaders.processEvent(null, new StopWatch());

        verify(mockedFileFolderService, never()).createNewFolder(any(FolderData.class));
        verify(mockedSessionService, never()).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Loading completed.  Raising 'done' event.",result.getData());
        assertEquals(1, result.getNextEvents().size());
        assertEquals(EVENT_COMPLETE, result.getNextEvents().get(0).getName());
    }

    @Test
    public void testScheduleUnfiledRecordFoldersChildren() throws Exception
    {
        int maxActiveLoaders = 8;
        int rootUnfiledRecordFolderNumber = 0;
        int unfiledRecordFolderChildrenNumber = 2;
        int unfiledRecordFolderDepth = 4;

        scheduleUnfiledRecordFolderLoaders.setCreateUnfiledRecordFolderStructure(true);
        scheduleUnfiledRecordFolderLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleUnfiledRecordFolderLoaders.setRootUnfiledRecordFolderNumber(rootUnfiledRecordFolderNumber);
        scheduleUnfiledRecordFolderLoaders.setUnfiledRecordFolderDepth(unfiledRecordFolderDepth);
        scheduleUnfiledRecordFolderLoaders.setUnfiledRecordFolderNumber(unfiledRecordFolderChildrenNumber);

        FolderData unfiledContainerFolder = mock(FolderData.class);
        when(unfiledContainerFolder.getContext()).thenReturn(UNFILED_CONTEXT);
        when(unfiledContainerFolder.getPath()).thenReturn(UNFILED_RECORD_CONTAINER_PATH);
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, UNFILED_RECORD_CONTAINER_PATH)).thenReturn(unfiledContainerFolder);

        FolderData mockedUnfiledRecordFolder1 = mock(FolderData.class);
        when(mockedUnfiledRecordFolder1.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedUnfiledRecordFolder1.getPath()).thenReturn("/a");

        FolderData mockedUnfiledRecordFolder2 = mock(FolderData.class);
        when(mockedUnfiledRecordFolder2.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedUnfiledRecordFolder2.getPath()).thenReturn("/b");

        List<FolderData> folders = Arrays.asList(mockedUnfiledRecordFolder1, mockedUnfiledRecordFolder2);
        when(mockedFileFolderService.getFoldersByCounts(UNFILED_CONTEXT, Long.valueOf(UNFILED_RECORD_CONTAINER_LEVEL+1), Long.valueOf(scheduleUnfiledRecordFolderLoaders.getMaxLevel() - 1), 0L, Long.valueOf(unfiledRecordFolderChildrenNumber-1), null, null, 0, 100)).thenReturn(folders);

        String id1 = UUID.randomUUID().toString();
        FolderData auxFolder1 = mock(FolderData.class);
        when(auxFolder1.getFolderCount()).thenReturn(2L)
        .thenReturn(0L);
        when(mockedUnfiledRecordFolder1.getId()).thenReturn(id1);
        when(mockedAuxFileFolderService.getFolder(id1)).thenReturn(auxFolder1);

        String id2 = UUID.randomUUID().toString();
        FolderData auxFolder2 = mock(FolderData.class);
        when(auxFolder2.getFolderCount()).thenReturn(2L)
        .thenReturn(0L);
        when(mockedUnfiledRecordFolder2.getId()).thenReturn(id2);
        when(mockedAuxFileFolderService.getFolder(id2)).thenReturn(auxFolder2);

        EventResult result = scheduleUnfiledRecordFolderLoaders.processEvent(null, new StopWatch());

        verify(mockedSessionService, times(4)).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Raised further 4 events and rescheduled self.",result.getData());
        assertEquals(5, result.getNextEvents().size());

        for(int i=0; i<4; i++)
        {
            Event event = result.getNextEvents().get(i);
            assertEquals(EVENT_LOAD_UNFILED_RECORD_FOLDER, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(UNFILED_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(LOAD_UNFILED_RECORD_FOLDER_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
            if(i < 2)
            {
                assertEquals("/a", (String) dataObj.get(FIELD_PATH));
            }
            else
            {
                assertEquals("/b", (String) dataObj.get(FIELD_PATH));
            }
        }
        assertEquals(EVENT_SCHEDULE_SELF, result.getNextEvents().get(4).getName());
    }

    @Test
    public void testSchedule4UnfiledRecordFoldersWithMaxActiveLoaders3() throws Exception
    {
        int maxActiveLoaders = 3;
        int rootUnfiledRecordFolderNumber = 0;
        int unfiledRecordFolderChildrenNumber = 2;
        int unfiledRecordFolderDepth = 4;

        scheduleUnfiledRecordFolderLoaders.setCreateUnfiledRecordFolderStructure(true);
        scheduleUnfiledRecordFolderLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleUnfiledRecordFolderLoaders.setRootUnfiledRecordFolderNumber(rootUnfiledRecordFolderNumber);
        scheduleUnfiledRecordFolderLoaders.setUnfiledRecordFolderDepth(unfiledRecordFolderDepth);
        scheduleUnfiledRecordFolderLoaders.setUnfiledRecordFolderNumber(unfiledRecordFolderChildrenNumber);

        FolderData unfiledContainerFolder = mock(FolderData.class);
        when(unfiledContainerFolder.getContext()).thenReturn(UNFILED_CONTEXT);
        when(unfiledContainerFolder.getPath()).thenReturn(UNFILED_RECORD_CONTAINER_PATH);
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, UNFILED_RECORD_CONTAINER_PATH)).thenReturn(unfiledContainerFolder);

        FolderData mockedUnfiledRecordFolder1 = mock(FolderData.class);
        when(mockedUnfiledRecordFolder1.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedUnfiledRecordFolder1.getPath()).thenReturn("/a");

        FolderData mockedUnfiledRecordFolder2 = mock(FolderData.class);
        when(mockedUnfiledRecordFolder2.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedUnfiledRecordFolder2.getPath()).thenReturn("/b");

        List<FolderData> folders = Arrays.asList(mockedUnfiledRecordFolder1, mockedUnfiledRecordFolder2);
        when(mockedFileFolderService.getFoldersByCounts(UNFILED_CONTEXT, Long.valueOf(UNFILED_RECORD_CONTAINER_LEVEL+1), Long.valueOf(scheduleUnfiledRecordFolderLoaders.getMaxLevel() - 1), 0L, Long.valueOf(unfiledRecordFolderChildrenNumber-1), null, null, 0, 100)).thenReturn(folders);

        String id1 = UUID.randomUUID().toString();
        FolderData auxFolder1 = mock(FolderData.class);
        when(auxFolder1.getFolderCount()).thenReturn(2L)
        .thenReturn(0L);
        when(mockedUnfiledRecordFolder1.getId()).thenReturn(id1);
        when(mockedAuxFileFolderService.getFolder(id1)).thenReturn(auxFolder1);

        String id2 = UUID.randomUUID().toString();
        FolderData auxFolder2 = mock(FolderData.class);
        when(auxFolder2.getFolderCount()).thenReturn(2L)
        .thenReturn(0L);
        when(mockedUnfiledRecordFolder2.getId()).thenReturn(id2);
        when(mockedAuxFileFolderService.getFolder(id2)).thenReturn(auxFolder2);

        EventResult result = scheduleUnfiledRecordFolderLoaders.processEvent(null, new StopWatch());

        verify(mockedSessionService, times(3)).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Raised further 3 events and rescheduled self.",result.getData());
        assertEquals(4, result.getNextEvents().size());

        for(int i=0; i<3; i++)
        {
            Event event = result.getNextEvents().get(i);
            assertEquals(EVENT_LOAD_UNFILED_RECORD_FOLDER, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(UNFILED_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(LOAD_UNFILED_RECORD_FOLDER_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
            if(i < 2)
            {
                assertEquals("/a", (String) dataObj.get(FIELD_PATH));
            }
            else
            {
                assertEquals("/b", (String) dataObj.get(FIELD_PATH));
            }
        }
        assertEquals(EVENT_SCHEDULE_SELF, result.getNextEvents().get(3).getName());
    }

    @Test
    public void testScheduleUnfiledRecordFoldersChildrenWithDepthLessThan2() throws Exception
    {
        int maxActiveLoaders = 8;
        int rootUnfiledRecordFolderNumber = 0;
        int unfiledRecordFolderChildrenNumber = 2;
        int unfiledRecordFolderDepth = 1;

        scheduleUnfiledRecordFolderLoaders.setCreateUnfiledRecordFolderStructure(true);
        scheduleUnfiledRecordFolderLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleUnfiledRecordFolderLoaders.setRootUnfiledRecordFolderNumber(rootUnfiledRecordFolderNumber);
        scheduleUnfiledRecordFolderLoaders.setUnfiledRecordFolderDepth(unfiledRecordFolderDepth);
        scheduleUnfiledRecordFolderLoaders.setUnfiledRecordFolderNumber(unfiledRecordFolderChildrenNumber);

        FolderData mockedUnfiledRecordContainerFolder = mock(FolderData.class);
        when(mockedUnfiledRecordContainerFolder.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedUnfiledRecordContainerFolder.getPath()).thenReturn("/a");

        FolderData mockedUnfiledRecordContainerFolder2 = mock(FolderData.class);
        when(mockedUnfiledRecordContainerFolder2.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedUnfiledRecordContainerFolder2.getPath()).thenReturn("/b");

        List<FolderData> folders = Arrays.asList(mockedUnfiledRecordContainerFolder, mockedUnfiledRecordContainerFolder2);
        when(mockedFileFolderService.getFoldersByCounts(UNFILED_CONTEXT, Long.valueOf(UNFILED_RECORD_CONTAINER_LEVEL+1), Long.valueOf(scheduleUnfiledRecordFolderLoaders.getMaxLevel() - 1), 0L, Long.valueOf(unfiledRecordFolderChildrenNumber-1), null, null, 0, 100)).thenReturn(folders);

        FolderData unfiledContainerFolder = mock(FolderData.class);
        when(unfiledContainerFolder.getContext()).thenReturn(UNFILED_CONTEXT);
        when(unfiledContainerFolder.getPath()).thenReturn(UNFILED_RECORD_CONTAINER_PATH);
        when(mockedFileFolderService.getFolder(UNFILED_CONTEXT, UNFILED_RECORD_CONTAINER_PATH)).thenReturn(unfiledContainerFolder);

        EventResult result = scheduleUnfiledRecordFolderLoaders.processEvent(null, new StopWatch());

        verify(mockedFileFolderService, never()).createNewFolder(any(FolderData.class));
        verify(mockedSessionService, never()).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Loading completed.  Raising 'done' event.",result.getData());
        assertEquals(1, result.getNextEvents().size());
        assertEquals(EVENT_COMPLETE, result.getNextEvents().get(0).getName());
    }
}
