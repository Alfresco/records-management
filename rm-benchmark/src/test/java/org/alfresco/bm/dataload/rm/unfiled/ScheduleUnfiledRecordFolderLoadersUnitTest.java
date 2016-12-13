/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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

import org.alfresco.bm.cm.FileFolderService;
import org.alfresco.bm.cm.FolderData;
import org.alfresco.bm.dataload.RMEventConstants;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.session.SessionService;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mongodb.DBObject;

/**
 * Unit tests for ScheduleUnfiledRecordFolderLoaders
 * @author Silviu Dinuta
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class ScheduleUnfiledRecordFolderLoadersUnitTest implements RMEventConstants
{
    @Mock
    private SessionService mockedSessionService;

    @Mock
    private FileFolderService mockedFileFolderService;

    @InjectMocks
    private ScheduleUnfiledRecordFolderLoaders scheduleUnfiledRecordFolderLoaders;

    @Test
    public void testUnfiledRecordFoldersNotWanted() throws Exception
    {
        scheduleUnfiledRecordFolderLoaders.setCreateUnfiledRecordFolderStructure(false);
        EventResult result = scheduleUnfiledRecordFolderLoaders.processEvent(null, new StopWatch());

        verify(mockedFileFolderService, never()).createNewFolder(any(FolderData.class));
        verify(mockedSessionService, never()).startSession(any(DBObject.class));

        assertEquals(false, result.isSuccess());
        assertEquals("Unfiled Record Folders structure creation not wanted.",result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testUnfiledRecordFoldersNotWantedAndContinueLoadingData() throws Exception
    {
        scheduleUnfiledRecordFolderLoaders.setCreateUnfiledRecordFolderStructure(false);
        String unfiledRecordContainerPath = "/" + PATH_SNIPPET_SITES + "/" + PATH_SNIPPET_RM_SITE_ID + "/" + PATH_SNIPPET_FILE_PLAN + "/" + PATH_SNIPPET_UNFILED_RECORD_CONTAINER;
        FolderData mockedFolder = mock(FolderData.class);
        List<FolderData> unfiledContainerChildren = Arrays.asList(mockedFolder);
        when(mockedFileFolderService.getChildFolders(UNFILED_CONTEXT, unfiledRecordContainerPath, 0, 1)).thenReturn(unfiledContainerChildren);
        EventResult result = scheduleUnfiledRecordFolderLoaders.processEvent(null, new StopWatch());

        verify(mockedFileFolderService, never()).createNewFolder(any(FolderData.class));
        verify(mockedSessionService, never()).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Unfiled Record Folders structure creation not wanted, continue with loading unfiled records.",result.getData());
        assertEquals(1, result.getNextEvents().size());
        assertEquals("scheduleUnfiledRecordLoaders", result.getNextEvents().get(0).getName());
    }

    @Test
    public void testScheduleRootUnfiledRecordFolders() throws Exception
    {
        int maxActiveLoaders = 8;
        int rootUnfiledRecordFolderNumber = 4;
        int unfiledRecordFoldersDepth = 1;
        String username = "bob";

        scheduleUnfiledRecordFolderLoaders.setCreateUnfiledRecordFolderStructure(true);
        scheduleUnfiledRecordFolderLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleUnfiledRecordFolderLoaders.setUnfiledRecordFolderDepth(unfiledRecordFoldersDepth);
        scheduleUnfiledRecordFolderLoaders.setRootUnfiledRecordFolderNumber(rootUnfiledRecordFolderNumber);
        scheduleUnfiledRecordFolderLoaders.setUsername(username);
        FolderData mockedUnfiledRecordContainerFolder = mock(FolderData.class);
        when(mockedUnfiledRecordContainerFolder.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedUnfiledRecordContainerFolder.getPath()).thenReturn("/a");
        List<FolderData> folders = Arrays.asList(mockedUnfiledRecordContainerFolder);
        when(mockedFileFolderService.getFoldersByCounts(UNFILED_CONTEXT, 4L, 4L, 0L, Long.valueOf(rootUnfiledRecordFolderNumber-1), null, null, 0, 100)).thenReturn(folders);

        EventResult result = scheduleUnfiledRecordFolderLoaders.processEvent(null, new StopWatch());

        verify(mockedFileFolderService, times(1)).createNewFolder(any(FolderData.class));
        verify(mockedSessionService, times(1)).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Raised further 1 events and rescheduled self.",result.getData());
        assertEquals(2, result.getNextEvents().size());

        Event firstEvent = result.getNextEvents().get(0);
        assertEquals("loadUnfiledRecordFolders", firstEvent.getName());
        DBObject dataObj = (DBObject)firstEvent.getData();
        assertNotNull(dataObj);
        assertEquals(UNFILED_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
        assertEquals("/a", (String) dataObj.get(FIELD_PATH));
        assertEquals(Integer.valueOf(rootUnfiledRecordFolderNumber), (Integer) dataObj.get(FIELD_UNFILED_ROOT_FOLDERS_TO_CREATE));
        assertEquals(Integer.valueOf(0), (Integer) dataObj.get(FIELD_UNFILED_FOLDERS_TO_CREATE));
        assertEquals(username, (String) dataObj.get(FIELD_SITE_MANAGER));

        assertEquals("scheduleUnfiledFoldersLoaders", result.getNextEvents().get(1).getName());
    }

    @Test
    public void testScheduleRootUnfiledRecordFoldersWithDepth0() throws Exception
    {
        int maxActiveLoaders = 8;
        int rootUnfiledRecordFolderNumber = 4;
        int unfiledRecordFoldersDepth = 0;
        String username = "bob";

        scheduleUnfiledRecordFolderLoaders.setCreateUnfiledRecordFolderStructure(true);
        scheduleUnfiledRecordFolderLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleUnfiledRecordFolderLoaders.setUnfiledRecordFolderDepth(unfiledRecordFoldersDepth);
        scheduleUnfiledRecordFolderLoaders.setRootUnfiledRecordFolderNumber(rootUnfiledRecordFolderNumber);
        scheduleUnfiledRecordFolderLoaders.setUsername(username);
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
        assertEquals("loadingUnfiledRecordFoldersComplete", result.getNextEvents().get(0).getName());
    }

    @Test
    public void testScheduleUnfiledRecordFoldersChildren() throws Exception
    {
        int maxActiveLoaders = 8;
        int rootUnfiledRecordFolderNumber = 0;
        int unfiledRecordFolderChildrenNumber = 2;
        int unfiledRecordFolderDepth = 4;
        String username = "bob";

        scheduleUnfiledRecordFolderLoaders.setCreateUnfiledRecordFolderStructure(true);
        scheduleUnfiledRecordFolderLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleUnfiledRecordFolderLoaders.setRootUnfiledRecordFolderNumber(rootUnfiledRecordFolderNumber);
        scheduleUnfiledRecordFolderLoaders.setUnfiledRecordFolderDepth(unfiledRecordFolderDepth);
        scheduleUnfiledRecordFolderLoaders.setUnfiledRecordFolderNumber(unfiledRecordFolderChildrenNumber);
        scheduleUnfiledRecordFolderLoaders.setUsername(username);

        FolderData mockedUnfiledRecordContainerFolder = mock(FolderData.class);
        when(mockedUnfiledRecordContainerFolder.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedUnfiledRecordContainerFolder.getPath()).thenReturn("/a");

        FolderData mockedUnfiledRecordContainerFolder2 = mock(FolderData.class);
        when(mockedUnfiledRecordContainerFolder2.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedUnfiledRecordContainerFolder2.getPath()).thenReturn("/b");

        List<FolderData> folders = Arrays.asList(mockedUnfiledRecordContainerFolder, mockedUnfiledRecordContainerFolder2);
        when(mockedFileFolderService.getFoldersByCounts(UNFILED_CONTEXT, Long.valueOf(UNFILED_RECORD_CONTAINER_LEVEL+1), Long.valueOf(scheduleUnfiledRecordFolderLoaders.getMaxLevel() - 1), 0L, Long.valueOf(unfiledRecordFolderChildrenNumber-1), null, null, 0, 100)).thenReturn(folders);

        EventResult result = scheduleUnfiledRecordFolderLoaders.processEvent(null, new StopWatch());

        verify(mockedFileFolderService, times(2)).createNewFolder(any(FolderData.class));
        verify(mockedSessionService, times(2)).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Raised further 2 events and rescheduled self.",result.getData());
        assertEquals(3, result.getNextEvents().size());

        Event firstEvent = result.getNextEvents().get(0);
        assertEquals("loadUnfiledRecordFolders", firstEvent.getName());
        DBObject dataObj = (DBObject)firstEvent.getData();
        assertNotNull(dataObj);
        assertEquals(UNFILED_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
        assertEquals("/a", (String) dataObj.get(FIELD_PATH));
        assertEquals(Integer.valueOf(rootUnfiledRecordFolderNumber), (Integer) dataObj.get(FIELD_UNFILED_ROOT_FOLDERS_TO_CREATE));
        assertEquals(Integer.valueOf(unfiledRecordFolderChildrenNumber), (Integer) dataObj.get(FIELD_UNFILED_FOLDERS_TO_CREATE));
        assertEquals(username, (String) dataObj.get(FIELD_SITE_MANAGER));

        Event secondEvent = result.getNextEvents().get(1);
        assertEquals("loadUnfiledRecordFolders", secondEvent.getName());
        dataObj = (DBObject)secondEvent.getData();
        assertNotNull(dataObj);
        assertEquals(UNFILED_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
        assertEquals("/b", (String) dataObj.get(FIELD_PATH));
        assertEquals(Integer.valueOf(rootUnfiledRecordFolderNumber), (Integer) dataObj.get(FIELD_UNFILED_ROOT_FOLDERS_TO_CREATE));
        assertEquals(Integer.valueOf(unfiledRecordFolderChildrenNumber), (Integer) dataObj.get(FIELD_UNFILED_FOLDERS_TO_CREATE));
        assertEquals(username, (String) dataObj.get(FIELD_SITE_MANAGER));

        assertEquals("scheduleUnfiledFoldersLoaders", result.getNextEvents().get(2).getName());
    }

    @Test
    public void testScheduleUnfiledRecordFoldersChildrenWithDepthLessThan2() throws Exception
    {
        int maxActiveLoaders = 8;
        int rootUnfiledRecordFolderNumber = 0;
        int unfiledRecordFolderChildrenNumber = 2;
        int unfiledRecordFolderDepth = 1;
        String username = "bob";

        scheduleUnfiledRecordFolderLoaders.setCreateUnfiledRecordFolderStructure(true);
        scheduleUnfiledRecordFolderLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleUnfiledRecordFolderLoaders.setRootUnfiledRecordFolderNumber(rootUnfiledRecordFolderNumber);
        scheduleUnfiledRecordFolderLoaders.setUnfiledRecordFolderDepth(unfiledRecordFolderDepth);
        scheduleUnfiledRecordFolderLoaders.setUnfiledRecordFolderNumber(unfiledRecordFolderChildrenNumber);
        scheduleUnfiledRecordFolderLoaders.setUsername(username);

        FolderData mockedUnfiledRecordContainerFolder = mock(FolderData.class);
        when(mockedUnfiledRecordContainerFolder.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedUnfiledRecordContainerFolder.getPath()).thenReturn("/a");

        FolderData mockedUnfiledRecordContainerFolder2 = mock(FolderData.class);
        when(mockedUnfiledRecordContainerFolder2.getContext()).thenReturn(UNFILED_CONTEXT);
        when(mockedUnfiledRecordContainerFolder2.getPath()).thenReturn("/b");

        List<FolderData> folders = Arrays.asList(mockedUnfiledRecordContainerFolder, mockedUnfiledRecordContainerFolder2);
        when(mockedFileFolderService.getFoldersByCounts(UNFILED_CONTEXT, Long.valueOf(UNFILED_RECORD_CONTAINER_LEVEL+1), Long.valueOf(scheduleUnfiledRecordFolderLoaders.getMaxLevel() - 1), 0L, Long.valueOf(unfiledRecordFolderChildrenNumber-1), null, null, 0, 100)).thenReturn(folders);

        EventResult result = scheduleUnfiledRecordFolderLoaders.processEvent(null, new StopWatch());

        verify(mockedFileFolderService, never()).createNewFolder(any(FolderData.class));
        verify(mockedSessionService, never()).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Loading completed.  Raising 'done' event.",result.getData());
        assertEquals(1, result.getNextEvents().size());
        assertEquals("loadingUnfiledRecordFoldersComplete", result.getNextEvents().get(0).getName());
    }
}
