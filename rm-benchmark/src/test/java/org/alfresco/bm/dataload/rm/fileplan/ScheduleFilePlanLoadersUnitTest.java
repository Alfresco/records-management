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

package org.alfresco.bm.dataload.rm.fileplan;

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
 * Unit tests for ScheduleFilePlanLoaders
 *
 * @author Silviu Dinuta
 * @since 2.6
 */
@RunWith(MockitoJUnitRunner.class)
public class ScheduleFilePlanLoadersUnitTest implements RMEventConstants
{
    private static final String EVENT_LOAD_SUB_CATEGORY = "testLoadSubCategory";

    private static final String EVENT_LOAD_RECORD_FOLDER = "testLoadRecordFolder";

    private static final String EVENT_FILE_PLANLOADING_COMPLETE = "testFilePlanloadingComplete";

    private static final String EVENT_LOAD_ROOT_RECORD_CATEGORY = "testLoadRootRecordCategory";

    private static final String EVENT_SCHEDULE_SELF = "testScheduleFilePlanLoaders";

    @Mock
    private SessionService mockedSessionService;

    @Mock
    private FileFolderService mockedFileFolderService;

    @Mock
    private ExtendedFileFolderService mockedAuxFileFolderService;

    @InjectMocks
    private ScheduleFilePlanLoaders scheduleFilePlanLoaders;

    @Before
    public void before()
    {
        MockitoAnnotations.initMocks(this);
        scheduleFilePlanLoaders.setEventNameLoadingComplete(EVENT_FILE_PLANLOADING_COMPLETE);
        scheduleFilePlanLoaders.setEventNameLoadRecordFolder(EVENT_LOAD_RECORD_FOLDER);
        scheduleFilePlanLoaders.setEventNameLoadRootRecordCategory(EVENT_LOAD_ROOT_RECORD_CATEGORY);
        scheduleFilePlanLoaders.setEventNameLoadSubCategory(EVENT_LOAD_SUB_CATEGORY);
        scheduleFilePlanLoaders.setEventNameScheduleLoaders(EVENT_SCHEDULE_SELF);
        scheduleFilePlanLoaders.setLoadCheckDelay(0L);
        scheduleFilePlanLoaders.setCreateFileplanFolderStructure(true);
    }

    @Test
    public void testScheduleRootCategories() throws Exception
    {
        int maxActiveLoaders = 8;
        int rootCategoriesNumber = 4;
        int categoryStructureDepth = 1;

        scheduleFilePlanLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleFilePlanLoaders.setCategoryNumber(rootCategoriesNumber);
        scheduleFilePlanLoaders.setCategoryStructureDepth(categoryStructureDepth);

        FolderData filePlanFolder = mock(FolderData.class);
        when(filePlanFolder.getContext()).thenReturn(FILEPLAN_CONTEXT);
        when(filePlanFolder.getPath()).thenReturn(RECORD_CONTAINER_PATH);
        when(mockedFileFolderService.getFolder(FILEPLAN_CONTEXT, RECORD_CONTAINER_PATH)).thenReturn(filePlanFolder);

        EventResult result = scheduleFilePlanLoaders.processEvent(null, new StopWatch());

        verify(mockedSessionService, times(4)).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Raised further 4 events and rescheduled self.",result.getData());
        assertEquals(5, result.getNextEvents().size());

        for(int i=0; i<4; i++)
        {
            Event event = result.getNextEvents().get(i);
            assertEquals(EVENT_LOAD_ROOT_RECORD_CATEGORY, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(FILEPLAN_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(RECORD_CONTAINER_PATH, (String) dataObj.get(FIELD_PATH));
            assertEquals(LOAD_ROOT_CATEGORY_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
        }
        assertEquals(EVENT_SCHEDULE_SELF, result.getNextEvents().get(4).getName());
    }

    @Test
    public void testSchedule4RootCategoriesWithMaxActiveLoaders3() throws Exception
    {
        int maxActiveLoaders = 3;
        int rootCategoriesNumber = 4;
        int categoryStructureDepth = 1;

        scheduleFilePlanLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleFilePlanLoaders.setCategoryNumber(rootCategoriesNumber);
        scheduleFilePlanLoaders.setCategoryStructureDepth(categoryStructureDepth);
        FolderData mockedFilePlanFolder = mock(FolderData.class);
        when(mockedFilePlanFolder.getContext()).thenReturn(FILEPLAN_CONTEXT);
        when(mockedFilePlanFolder.getPath()).thenReturn("/a");
        List<FolderData> folders = Arrays.asList(mockedFilePlanFolder);
        when(mockedFileFolderService.getFoldersByCounts(FILEPLAN_CONTEXT, 3L, 3L, 0L, Long.valueOf(rootCategoriesNumber-1), null, null, 0, 100)).thenReturn(folders);

        FolderData filePlanFolder = mock(FolderData.class);
        when(filePlanFolder.getContext()).thenReturn(FILEPLAN_CONTEXT);
        when(filePlanFolder.getPath()).thenReturn(RECORD_CONTAINER_PATH);
        when(mockedFileFolderService.getFolder(FILEPLAN_CONTEXT, RECORD_CONTAINER_PATH)).thenReturn(filePlanFolder);

        EventResult result = scheduleFilePlanLoaders.processEvent(null, new StopWatch());

        verify(mockedSessionService, times(3)).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Raised further 3 events and rescheduled self.",result.getData());
        assertEquals(4, result.getNextEvents().size());

        for(int i=0; i<3; i++)
        {
            Event event = result.getNextEvents().get(i);
            assertEquals(EVENT_LOAD_ROOT_RECORD_CATEGORY, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(FILEPLAN_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            assertEquals(RECORD_CONTAINER_PATH, (String) dataObj.get(FIELD_PATH));
            assertEquals(LOAD_ROOT_CATEGORY_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
        }
        assertEquals(EVENT_SCHEDULE_SELF, result.getNextEvents().get(3).getName());
    }

    @Test
    public void testScheduleRootCategoriesWtith0FilePlanDepth() throws Exception
    {
        int maxActiveLoaders = 8;
        int rootCategoriesNumber = 4;
        int categoryStructureDepth = 0;

        scheduleFilePlanLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleFilePlanLoaders.setCategoryNumber(rootCategoriesNumber);
        scheduleFilePlanLoaders.setCategoryStructureDepth(categoryStructureDepth);
        FolderData mockedFilePlanFolder = mock(FolderData.class);
        when(mockedFilePlanFolder.getContext()).thenReturn(FILEPLAN_CONTEXT);
        when(mockedFilePlanFolder.getPath()).thenReturn("/a");
        List<FolderData> folders = Arrays.asList(mockedFilePlanFolder);
        when(mockedFileFolderService.getFoldersByCounts(FILEPLAN_CONTEXT, 3L, 3L, 0L, Long.valueOf(rootCategoriesNumber-1), null, null, 0, 100)).thenReturn(folders);

        EventResult result = scheduleFilePlanLoaders.processEvent(null, new StopWatch());

        verify(mockedFileFolderService, never()).createNewFolder(any(FolderData.class));
        verify(mockedSessionService, never()).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Loading completed.  Raising 'done' event.",result.getData());
        assertEquals(1, result.getNextEvents().size());
        assertEquals(EVENT_FILE_PLANLOADING_COMPLETE, result.getNextEvents().get(0).getName());
    }

    @Test
    public void testScheduleChildrenCategoriesInCategories() throws Exception
    {
        int maxActiveLoaders = 8;
        int rootCategoriesNumber = 0;
        int categoriesChildrenNumber = 2;
        int foldersChildrenNumber= 0;
        int categoryStructureDepth = 4;

        scheduleFilePlanLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleFilePlanLoaders.setCategoryNumber(rootCategoriesNumber);
        scheduleFilePlanLoaders.setCategoryStructureDepth(categoryStructureDepth);
        scheduleFilePlanLoaders.setChildCategNumber(categoriesChildrenNumber);

        scheduleFilePlanLoaders.setFolderNumber(foldersChildrenNumber);
        scheduleFilePlanLoaders.setFolderCategoryMix(true);

        FolderData mockedRootCategoryFolder = mock(FolderData.class);
        when(mockedRootCategoryFolder.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedRootCategoryFolder.getPath()).thenReturn("/a");
        String name1 = ROOT_CATEGORY_NAME_IDENTIFIER + UUID.randomUUID().toString();
        when(mockedRootCategoryFolder.getName()).thenReturn(name1);

        FolderData mockedChildCategoryFolder = mock(FolderData.class);
        when(mockedChildCategoryFolder.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedChildCategoryFolder.getPath()).thenReturn("/b");
        String name2 = CATEGORY_NAME_IDENTIFIER + UUID.randomUUID().toString();
        when(mockedChildCategoryFolder.getName()).thenReturn(name2);

        FolderData filePlanFolder = mock(FolderData.class);
        when(filePlanFolder.getContext()).thenReturn(FILEPLAN_CONTEXT);
        when(filePlanFolder.getPath()).thenReturn(RECORD_CONTAINER_PATH);
        when(mockedFileFolderService.getFolder(FILEPLAN_CONTEXT, RECORD_CONTAINER_PATH)).thenReturn(filePlanFolder);

        String id1 = UUID.randomUUID().toString();
        FolderData auxFolder1 = mock(FolderData.class);
        when(auxFolder1.getFolderCount()).thenReturn(2L)
                                         .thenReturn(0L);
        when(mockedRootCategoryFolder.getId()).thenReturn(id1);
        when(mockedAuxFileFolderService.getFolder(id1)).thenReturn(auxFolder1);

        String id2 = UUID.randomUUID().toString();
        FolderData auxFolder2 = mock(FolderData.class);
        when(auxFolder2.getFolderCount()).thenReturn(2L)
                                         .thenReturn(0L);
        when(mockedChildCategoryFolder.getId()).thenReturn(id2);
        when(mockedAuxFileFolderService.getFolder(id2)).thenReturn(auxFolder2);


        List<FolderData> folders = Arrays.asList(mockedRootCategoryFolder, mockedChildCategoryFolder);
        when(mockedFileFolderService.getFoldersByCounts(RECORD_CATEGORY_CONTEXT, Long.valueOf(FILE_PLAN_LEVEL+1), Long.valueOf(scheduleFilePlanLoaders.getMaxLevel() - 1), 0L, Long.valueOf(categoriesChildrenNumber-1), null, null, 0, 100)).thenReturn(folders);

        when(mockedFileFolderService.getChildFolders(RECORD_CATEGORY_CONTEXT, "/a", 0, 100)).thenReturn(new ArrayList<>());
        when(mockedFileFolderService.getChildFolders(RECORD_CATEGORY_CONTEXT, "/b", 0, 100)).thenReturn(new ArrayList<>());
        EventResult result = scheduleFilePlanLoaders.processEvent(null, new StopWatch());

        verify(mockedSessionService, times(4)).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Raised further 4 events and rescheduled self.",result.getData());
        assertEquals(5, result.getNextEvents().size());

        for(int i=0; i<4; i++)
        {
            Event event = result.getNextEvents().get(i);
            assertEquals(EVENT_LOAD_SUB_CATEGORY, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_CATEGORY_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            if(i < 2)
            {
                assertEquals("/a", (String) dataObj.get(FIELD_PATH));
            }
            else
            {
                assertEquals("/b", (String) dataObj.get(FIELD_PATH));
            }
            assertEquals(LOAD_SUB_CATEGORY_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
        }
        assertEquals(EVENT_SCHEDULE_SELF, result.getNextEvents().get(4).getName());
    }

    @Test
    public void testCalculateMaxChildrenWithMix() throws Exception
    {
        int maxActiveLoaders = 8;
        int rootCategoriesNumber = 0;
        int categoriesChildrenNumber = 2;
        int foldersChildrenNumber= 0;
        int categoryStructureDepth = 4;

        scheduleFilePlanLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleFilePlanLoaders.setCategoryNumber(rootCategoriesNumber);
        scheduleFilePlanLoaders.setCategoryStructureDepth(categoryStructureDepth);
        scheduleFilePlanLoaders.setChildCategNumber(categoriesChildrenNumber);

        scheduleFilePlanLoaders.setFolderNumber(foldersChildrenNumber);
        scheduleFilePlanLoaders.setFolderCategoryMix(true);

        FolderData mockedRootCategoryFolder = mock(FolderData.class);
        when(mockedRootCategoryFolder.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedRootCategoryFolder.getPath()).thenReturn("/a");
        String name1 = ROOT_CATEGORY_NAME_IDENTIFIER + UUID.randomUUID().toString();
        when(mockedRootCategoryFolder.getName()).thenReturn(name1);

        FolderData mockedChildCategoryFolder = mock(FolderData.class);
        when(mockedChildCategoryFolder.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedChildCategoryFolder.getPath()).thenReturn("/b");
        String name2 = CATEGORY_NAME_IDENTIFIER + UUID.randomUUID().toString();
        when(mockedChildCategoryFolder.getName()).thenReturn(name2);

        FolderData filePlanFolder = mock(FolderData.class);
        when(filePlanFolder.getContext()).thenReturn(FILEPLAN_CONTEXT);
        when(filePlanFolder.getPath()).thenReturn(RECORD_CONTAINER_PATH);
        when(mockedFileFolderService.getFolder(FILEPLAN_CONTEXT, RECORD_CONTAINER_PATH)).thenReturn(filePlanFolder);

        String id1 = UUID.randomUUID().toString();
        FolderData auxFolder1 = mock(FolderData.class);
        when(auxFolder1.getFolderCount()).thenReturn(1L)
        .thenReturn(0L);
        when(mockedRootCategoryFolder.getId()).thenReturn(id1);
        when(mockedAuxFileFolderService.getFolder(id1)).thenReturn(auxFolder1);

        String id2 = UUID.randomUUID().toString();
        FolderData auxFolder2 = mock(FolderData.class);
        when(auxFolder2.getFolderCount()).thenReturn(1L)
        .thenReturn(0L);
        when(mockedChildCategoryFolder.getId()).thenReturn(id2);
        when(mockedAuxFileFolderService.getFolder(id2)).thenReturn(auxFolder2);


        List<FolderData> folders = Arrays.asList(mockedRootCategoryFolder, mockedChildCategoryFolder);
        when(mockedFileFolderService.getFoldersByCounts(RECORD_CATEGORY_CONTEXT, Long.valueOf(FILE_PLAN_LEVEL+1), Long.valueOf(scheduleFilePlanLoaders.getMaxLevel() - 1), 0L, Long.valueOf(categoriesChildrenNumber-1), null, null, 0, 100)).thenReturn(folders);

        FolderData mockedExitingCategory1 = mock(FolderData.class);
        FolderData mockedExitingCategory2 = mock(FolderData.class);
        when(mockedFileFolderService.getChildFolders(RECORD_CATEGORY_CONTEXT, "/a", 0, 100)).thenReturn(Arrays.asList(mockedExitingCategory1));
        when(mockedFileFolderService.getChildFolders(RECORD_CATEGORY_CONTEXT, "/b", 0, 100)).thenReturn(Arrays.asList(mockedExitingCategory2));
        when(mockedRootCategoryFolder.getFolderCount()).thenReturn(1L);

        when(mockedFileFolderService.getFoldersByCounts(RECORD_CATEGORY_CONTEXT, null, null, null, null, null, null, 0, 1)).thenReturn(Arrays.asList(mockedRootCategoryFolder));
        EventResult result = scheduleFilePlanLoaders.processEvent(null, new StopWatch());

        verify(mockedSessionService, times(2)).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Raised further 2 events and rescheduled self.",result.getData());
        assertEquals(3, result.getNextEvents().size());

        for(int i=0; i<2; i++)
        {
            Event event = result.getNextEvents().get(i);
            assertEquals(EVENT_LOAD_SUB_CATEGORY, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_CATEGORY_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            if(i == 0)
            {
                assertEquals("/a", (String) dataObj.get(FIELD_PATH));
            }
            else
            {
                assertEquals("/b", (String) dataObj.get(FIELD_PATH));
            }
            assertEquals(LOAD_SUB_CATEGORY_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
        }
        assertEquals(EVENT_SCHEDULE_SELF, result.getNextEvents().get(2).getName());
    }

    @Test
    public void testCalculateMaxChildrenWithoutMix() throws Exception
    {
        int maxActiveLoaders = 8;
        int rootCategoriesNumber = 0;
        int categoriesChildrenNumber = 2;
        int foldersChildrenNumber= 0;
        int categoryStructureDepth = 4;

        scheduleFilePlanLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleFilePlanLoaders.setCategoryNumber(rootCategoriesNumber);
        scheduleFilePlanLoaders.setCategoryStructureDepth(categoryStructureDepth);
        scheduleFilePlanLoaders.setChildCategNumber(categoriesChildrenNumber);

        scheduleFilePlanLoaders.setFolderNumber(foldersChildrenNumber);
        scheduleFilePlanLoaders.setFolderCategoryMix(false);

        FolderData mockedRootCategoryFolder = mock(FolderData.class);
        when(mockedRootCategoryFolder.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedRootCategoryFolder.getPath()).thenReturn("/a");
        String name1 = ROOT_CATEGORY_NAME_IDENTIFIER + UUID.randomUUID().toString();
        when(mockedRootCategoryFolder.getName()).thenReturn(name1);

        FolderData mockedChildCategoryFolder = mock(FolderData.class);
        when(mockedChildCategoryFolder.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedChildCategoryFolder.getPath()).thenReturn("/b");
        String name2 = CATEGORY_NAME_IDENTIFIER + UUID.randomUUID().toString();
        when(mockedChildCategoryFolder.getName()).thenReturn(name2);

        FolderData filePlanFolder = mock(FolderData.class);
        when(filePlanFolder.getContext()).thenReturn(FILEPLAN_CONTEXT);
        when(filePlanFolder.getPath()).thenReturn(RECORD_CONTAINER_PATH);
        when(mockedFileFolderService.getFolder(FILEPLAN_CONTEXT, RECORD_CONTAINER_PATH)).thenReturn(filePlanFolder);

        String id1 = UUID.randomUUID().toString();
        FolderData auxFolder1 = mock(FolderData.class);
        when(auxFolder1.getFolderCount()).thenReturn(1L)
        .thenReturn(0L);
        when(mockedRootCategoryFolder.getId()).thenReturn(id1);
        when(mockedAuxFileFolderService.getFolder(id1)).thenReturn(auxFolder1);

        String id2 = UUID.randomUUID().toString();
        FolderData auxFolder2 = mock(FolderData.class);
        when(auxFolder2.getFolderCount()).thenReturn(1L)
        .thenReturn(0L);
        when(mockedChildCategoryFolder.getId()).thenReturn(id2);
        when(mockedAuxFileFolderService.getFolder(id2)).thenReturn(auxFolder2);


        List<FolderData> folders = Arrays.asList(mockedRootCategoryFolder, mockedChildCategoryFolder);
        when(mockedFileFolderService.getFoldersByCounts(RECORD_CATEGORY_CONTEXT, Long.valueOf(FILE_PLAN_LEVEL+1), Long.valueOf(scheduleFilePlanLoaders.getMaxLevel() - 1), 0L, Long.valueOf(categoriesChildrenNumber-1), null, null, 0, 100)).thenReturn(folders);

        FolderData mockedExitingCategory1 = mock(FolderData.class);
        FolderData mockedExitingCategory2 = mock(FolderData.class);
        when(mockedFileFolderService.getChildFolders(RECORD_CATEGORY_CONTEXT, "/a", 0, 100)).thenReturn(Arrays.asList(mockedExitingCategory1));
        when(mockedFileFolderService.getChildFolders(RECORD_CATEGORY_CONTEXT, "/b", 0, 100)).thenReturn(Arrays.asList(mockedExitingCategory2));
        when(mockedRootCategoryFolder.getFolderCount()).thenReturn(1L);

        when(mockedFileFolderService.getFoldersByCounts(RECORD_CATEGORY_CONTEXT, null, null, null, null, null, null, 0, 1)).thenReturn(Arrays.asList(mockedRootCategoryFolder));
        EventResult result = scheduleFilePlanLoaders.processEvent(null, new StopWatch());

        verify(mockedSessionService, times(2)).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Raised further 2 events and rescheduled self.",result.getData());
        assertEquals(3, result.getNextEvents().size());

        for(int i=0; i<2; i++)
        {
            Event event = result.getNextEvents().get(i);
            assertEquals(EVENT_LOAD_SUB_CATEGORY, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_CATEGORY_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            if(i == 0)
            {
                assertEquals("/a", (String) dataObj.get(FIELD_PATH));
            }
            else
            {
                assertEquals("/b", (String) dataObj.get(FIELD_PATH));
            }
            assertEquals(LOAD_SUB_CATEGORY_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
        }
        assertEquals(EVENT_SCHEDULE_SELF, result.getNextEvents().get(2).getName());
    }

    @Test
    public void testSchedule4ChildrenCategoriesWithMaxActiveLoaders3() throws Exception
    {
        int maxActiveLoaders = 3;
        int rootCategoriesNumber = 0;
        int categoriesChildrenNumber = 2;
        int foldersChildrenNumber= 0;
        int categoryStructureDepth = 4;

        scheduleFilePlanLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleFilePlanLoaders.setCategoryNumber(rootCategoriesNumber);
        scheduleFilePlanLoaders.setCategoryStructureDepth(categoryStructureDepth);
        scheduleFilePlanLoaders.setChildCategNumber(categoriesChildrenNumber);

        scheduleFilePlanLoaders.setFolderNumber(foldersChildrenNumber);
        scheduleFilePlanLoaders.setFolderCategoryMix(true);

        FolderData mockedRootCategoryFolder = mock(FolderData.class);
        when(mockedRootCategoryFolder.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedRootCategoryFolder.getPath()).thenReturn("/a");
        String name1 = ROOT_CATEGORY_NAME_IDENTIFIER + UUID.randomUUID().toString();
        when(mockedRootCategoryFolder.getName()).thenReturn(name1);

        FolderData mockedChildCategoryFolder = mock(FolderData.class);
        when(mockedChildCategoryFolder.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedChildCategoryFolder.getPath()).thenReturn("/b");
        String name2 = CATEGORY_NAME_IDENTIFIER + UUID.randomUUID().toString();
        when(mockedChildCategoryFolder.getName()).thenReturn(name2);

        FolderData filePlanFolder = mock(FolderData.class);
        when(filePlanFolder.getContext()).thenReturn(FILEPLAN_CONTEXT);
        when(filePlanFolder.getPath()).thenReturn(RECORD_CONTAINER_PATH);
        when(mockedFileFolderService.getFolder(FILEPLAN_CONTEXT, RECORD_CONTAINER_PATH)).thenReturn(filePlanFolder);

        String id1 = UUID.randomUUID().toString();
        FolderData auxFolder1 = mock(FolderData.class);
        when(auxFolder1.getFolderCount()).thenReturn(2L)
        .thenReturn(0L);
        when(mockedRootCategoryFolder.getId()).thenReturn(id1);
        when(mockedAuxFileFolderService.getFolder(id1)).thenReturn(auxFolder1);

        String id2 = UUID.randomUUID().toString();
        FolderData auxFolder2 = mock(FolderData.class);
        when(auxFolder2.getFolderCount()).thenReturn(2L)
        .thenReturn(0L);
        when(mockedChildCategoryFolder.getId()).thenReturn(id2);
        when(mockedAuxFileFolderService.getFolder(id2)).thenReturn(auxFolder2);


        List<FolderData> folders = Arrays.asList(mockedRootCategoryFolder, mockedChildCategoryFolder);
        when(mockedFileFolderService.getFoldersByCounts(RECORD_CATEGORY_CONTEXT, Long.valueOf(FILE_PLAN_LEVEL+1), Long.valueOf(scheduleFilePlanLoaders.getMaxLevel() - 1), 0L, Long.valueOf(categoriesChildrenNumber-1), null, null, 0, 100)).thenReturn(folders);

        when(mockedFileFolderService.getChildFolders(RECORD_CATEGORY_CONTEXT, "/a", 0, 100)).thenReturn(new ArrayList<>());
        when(mockedFileFolderService.getChildFolders(RECORD_CATEGORY_CONTEXT, "/b", 0, 100)).thenReturn(new ArrayList<>());
        EventResult result = scheduleFilePlanLoaders.processEvent(null, new StopWatch());

        verify(mockedSessionService, times(3)).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Raised further 3 events and rescheduled self.",result.getData());
        assertEquals(4, result.getNextEvents().size());

        for(int i=0; i<3; i++)
        {
            Event event = result.getNextEvents().get(i);
            assertEquals(EVENT_LOAD_SUB_CATEGORY, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_CATEGORY_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            if(i < 2)
            {
                assertEquals("/a", (String) dataObj.get(FIELD_PATH));
            }
            else
            {
                assertEquals("/b", (String) dataObj.get(FIELD_PATH));
            }
            assertEquals(LOAD_SUB_CATEGORY_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
        }
        assertEquals(EVENT_SCHEDULE_SELF, result.getNextEvents().get(3).getName());
    }

    @Test
    public void testScheduleChildrenCategoriesInCategoriesWithFilePlanDepthLessThan2() throws Exception
    {
        int maxActiveLoaders = 8;
        int rootCategoriesNumber = 0;
        int categoriesChildrenNumber = 2;
        int foldersChildrenNumber= 0;
        int categoryStructureDepth = 1;

        scheduleFilePlanLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleFilePlanLoaders.setCategoryNumber(rootCategoriesNumber);
        scheduleFilePlanLoaders.setCategoryStructureDepth(categoryStructureDepth);
        scheduleFilePlanLoaders.setChildCategNumber(categoriesChildrenNumber);

        scheduleFilePlanLoaders.setFolderNumber(foldersChildrenNumber);
        scheduleFilePlanLoaders.setFolderCategoryMix(true);

        FolderData mockedRootCategoryFolder = mock(FolderData.class);
        when(mockedRootCategoryFolder.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedRootCategoryFolder.getPath()).thenReturn("/a");
        String name1 = ROOT_CATEGORY_NAME_IDENTIFIER + UUID.randomUUID().toString();
        when(mockedRootCategoryFolder.getName()).thenReturn(name1);

        FolderData mockedChildCategoryFolder = mock(FolderData.class);
        when(mockedChildCategoryFolder.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedChildCategoryFolder.getPath()).thenReturn("/b");
        String name2 = CATEGORY_NAME_IDENTIFIER + UUID.randomUUID().toString();
        when(mockedChildCategoryFolder.getName()).thenReturn(name2);

        List<FolderData> folders = Arrays.asList(mockedRootCategoryFolder, mockedChildCategoryFolder);
        when(mockedFileFolderService.getFoldersByCounts(RECORD_CATEGORY_CONTEXT, Long.valueOf(FILE_PLAN_LEVEL+1), Long.valueOf(scheduleFilePlanLoaders.getMaxLevel() - 1 -1), 0L, Long.valueOf(categoriesChildrenNumber-1), null, null, 0, 100)).thenReturn(folders);

        FolderData filePlanFolder = mock(FolderData.class);
        when(filePlanFolder.getContext()).thenReturn(FILEPLAN_CONTEXT);
        when(filePlanFolder.getPath()).thenReturn(RECORD_CONTAINER_PATH);
        when(mockedFileFolderService.getFolder(FILEPLAN_CONTEXT, RECORD_CONTAINER_PATH)).thenReturn(filePlanFolder);

        EventResult result = scheduleFilePlanLoaders.processEvent(null, new StopWatch());

        verify(mockedSessionService, never()).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Loading completed.  Raising 'done' event.",result.getData());
        assertEquals(1, result.getNextEvents().size());
        assertEquals(EVENT_FILE_PLANLOADING_COMPLETE, result.getNextEvents().get(0).getName());
    }

    @Test
    public void testScheduleChildrenRecordFoldersInCategoriesWithFilePlanDepthLessThan2() throws Exception
    {
        int maxActiveLoaders = 8;
        int rootCategoriesNumber = 0;
        int categoriesChildrenNumber = 0;
        int foldersChildrenNumber= 2;
        int categoryStructureDepth = 1;

        scheduleFilePlanLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleFilePlanLoaders.setCategoryNumber(rootCategoriesNumber);
        scheduleFilePlanLoaders.setCategoryStructureDepth(categoryStructureDepth);
        scheduleFilePlanLoaders.setChildCategNumber(categoriesChildrenNumber);

        scheduleFilePlanLoaders.setFolderNumber(foldersChildrenNumber);
        scheduleFilePlanLoaders.setFolderCategoryMix(true);

        FolderData mockedRootCategoryFolder = mock(FolderData.class);
        when(mockedRootCategoryFolder.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedRootCategoryFolder.getPath()).thenReturn("/a");
        String name1 = ROOT_CATEGORY_NAME_IDENTIFIER + UUID.randomUUID().toString();
        when(mockedRootCategoryFolder.getName()).thenReturn(name1);

        FolderData mockedChildCategoryFolder = mock(FolderData.class);
        when(mockedChildCategoryFolder.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedChildCategoryFolder.getPath()).thenReturn("/b");
        String name2 = CATEGORY_NAME_IDENTIFIER + UUID.randomUUID().toString();
        when(mockedChildCategoryFolder.getName()).thenReturn(name2);

        FolderData filePlanFolder = mock(FolderData.class);
        when(filePlanFolder.getContext()).thenReturn(FILEPLAN_CONTEXT);
        when(filePlanFolder.getPath()).thenReturn(RECORD_CONTAINER_PATH);
        when(mockedFileFolderService.getFolder(FILEPLAN_CONTEXT, RECORD_CONTAINER_PATH)).thenReturn(filePlanFolder);

        List<FolderData> folders = Arrays.asList(mockedRootCategoryFolder, mockedChildCategoryFolder);
        when(mockedFileFolderService.getFoldersByCounts(RECORD_CATEGORY_CONTEXT, Long.valueOf(FILE_PLAN_LEVEL+1), Long.valueOf(scheduleFilePlanLoaders.getMaxLevel() - 1 -1), 0L, Long.valueOf(foldersChildrenNumber-1), null, null, 0, 100)).thenReturn(folders);

        EventResult result = scheduleFilePlanLoaders.processEvent(null, new StopWatch());

        verify(mockedFileFolderService, never()).createNewFolder(any(FolderData.class));
        verify(mockedSessionService, never()).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Loading completed.  Raising 'done' event.",result.getData());
        assertEquals(1, result.getNextEvents().size());
        assertEquals(EVENT_FILE_PLANLOADING_COMPLETE, result.getNextEvents().get(0).getName());
    }

    @Test
    public void testScheduleChildrenRecordFoldersInCategories() throws Exception
    {
        int maxActiveLoaders = 8;
        int rootCategoriesNumber = 0;
        int categoriesChildrenNumber = 0;
        int foldersChildrenNumber= 2;
        int categoryStructureDepth = 4;

        scheduleFilePlanLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleFilePlanLoaders.setCategoryNumber(rootCategoriesNumber);
        scheduleFilePlanLoaders.setCategoryStructureDepth(categoryStructureDepth);
        scheduleFilePlanLoaders.setChildCategNumber(categoriesChildrenNumber);

        scheduleFilePlanLoaders.setFolderNumber(foldersChildrenNumber);
        scheduleFilePlanLoaders.setFolderCategoryMix(true);

        FolderData mockedRootCategoryFolder = mock(FolderData.class);
        when(mockedRootCategoryFolder.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedRootCategoryFolder.getPath()).thenReturn("/a");
        String name1 = ROOT_CATEGORY_NAME_IDENTIFIER + UUID.randomUUID().toString();
        when(mockedRootCategoryFolder.getName()).thenReturn(name1);

        FolderData mockedChildCategoryFolder = mock(FolderData.class);
        when(mockedChildCategoryFolder.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedChildCategoryFolder.getPath()).thenReturn("/b");
        String name2 = CATEGORY_NAME_IDENTIFIER + UUID.randomUUID().toString();
        when(mockedChildCategoryFolder.getName()).thenReturn(name2);

        FolderData filePlanFolder = mock(FolderData.class);
        when(filePlanFolder.getContext()).thenReturn(FILEPLAN_CONTEXT);
        when(filePlanFolder.getPath()).thenReturn(RECORD_CONTAINER_PATH);
        when(mockedFileFolderService.getFolder(FILEPLAN_CONTEXT, RECORD_CONTAINER_PATH)).thenReturn(filePlanFolder);

        List<FolderData> folders = Arrays.asList(mockedRootCategoryFolder, mockedChildCategoryFolder);
        when(mockedFileFolderService.getFoldersByCounts(RECORD_CATEGORY_CONTEXT, Long.valueOf(FILE_PLAN_LEVEL+1), Long.valueOf(scheduleFilePlanLoaders.getMaxLevel() - 1), 0L, Long.valueOf(foldersChildrenNumber-1), null, null, 0, 100)).thenReturn(folders);

        String id1 = UUID.randomUUID().toString();
        FolderData auxFolder1 = mock(FolderData.class);
        when(auxFolder1.getFileCount()).thenReturn(2L)
                                         .thenReturn(0L);
        when(mockedRootCategoryFolder.getId()).thenReturn(id1);
        when(mockedAuxFileFolderService.getFolder(id1)).thenReturn(auxFolder1);

        String id2 = UUID.randomUUID().toString();
        FolderData auxFolder2 = mock(FolderData.class);
        when(auxFolder2.getFileCount()).thenReturn(2L)
                                         .thenReturn(0L);
        when(mockedChildCategoryFolder.getId()).thenReturn(id2);
        when(mockedAuxFileFolderService.getFolder(id2)).thenReturn(auxFolder2);

        EventResult result = scheduleFilePlanLoaders.processEvent(null, new StopWatch());

        verify(mockedSessionService, times(4)).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Raised further 4 events and rescheduled self.",result.getData());
        assertEquals(5, result.getNextEvents().size());

        for(int i=0; i<4; i++)
        {
            Event event = result.getNextEvents().get(i);
            assertEquals(EVENT_LOAD_RECORD_FOLDER, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_CATEGORY_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            if(i < 2)
            {
                assertEquals("/a", (String) dataObj.get(FIELD_PATH));
            }
            else
            {
                assertEquals("/b", (String) dataObj.get(FIELD_PATH));
            }
            assertEquals(LOAD_RECORD_FOLDER_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
        }
        assertEquals(EVENT_SCHEDULE_SELF, result.getNextEvents().get(4).getName());
    }

    @Test
    public void testScheduleRecordFoldersOnLastLevel() throws Exception
    {
        int maxActiveLoaders = 8;
        int rootCategoriesNumber = 0;
        int categoriesChildrenNumber = 0;
        int foldersChildrenNumber= 2;
        int categoryStructureDepth = 4;

        scheduleFilePlanLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleFilePlanLoaders.setCategoryNumber(rootCategoriesNumber);
        scheduleFilePlanLoaders.setCategoryStructureDepth(categoryStructureDepth);
        scheduleFilePlanLoaders.setChildCategNumber(categoriesChildrenNumber);

        scheduleFilePlanLoaders.setFolderNumber(foldersChildrenNumber);
        scheduleFilePlanLoaders.setFolderCategoryMix(true);

        FolderData mockedRootCategoryFolder = mock(FolderData.class);
        when(mockedRootCategoryFolder.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedRootCategoryFolder.getPath()).thenReturn("/a");
        String name1 = ROOT_CATEGORY_NAME_IDENTIFIER + UUID.randomUUID().toString();
        when(mockedRootCategoryFolder.getName()).thenReturn(name1);

        FolderData mockedChildCategoryFolder = mock(FolderData.class);
        when(mockedChildCategoryFolder.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedChildCategoryFolder.getPath()).thenReturn("/b");
        String name2 = CATEGORY_NAME_IDENTIFIER + UUID.randomUUID().toString();
        when(mockedChildCategoryFolder.getName()).thenReturn(name2);

        FolderData filePlanFolder = mock(FolderData.class);
        when(filePlanFolder.getContext()).thenReturn(FILEPLAN_CONTEXT);
        when(filePlanFolder.getPath()).thenReturn(RECORD_CONTAINER_PATH);
        when(mockedFileFolderService.getFolder(FILEPLAN_CONTEXT, RECORD_CONTAINER_PATH)).thenReturn(filePlanFolder);

        List<FolderData> folders = Arrays.asList(mockedRootCategoryFolder, mockedChildCategoryFolder);
        when(mockedFileFolderService.getFoldersByCounts(RECORD_CATEGORY_CONTEXT, Long.valueOf(scheduleFilePlanLoaders.getMaxLevel()), Long.valueOf(scheduleFilePlanLoaders.getMaxLevel()), 0L, Long.valueOf(foldersChildrenNumber-1), null, null, 0, 100)).thenReturn(folders);

        String id1 = UUID.randomUUID().toString();
        FolderData auxFolder1 = mock(FolderData.class);
        when(auxFolder1.getFileCount()).thenReturn(2L)
        .thenReturn(0L);
        when(mockedRootCategoryFolder.getId()).thenReturn(id1);
        when(mockedAuxFileFolderService.getFolder(id1)).thenReturn(auxFolder1);

        String id2 = UUID.randomUUID().toString();
        FolderData auxFolder2 = mock(FolderData.class);
        when(auxFolder2.getFileCount()).thenReturn(2L)
        .thenReturn(0L);
        when(mockedChildCategoryFolder.getId()).thenReturn(id2);
        when(mockedAuxFileFolderService.getFolder(id2)).thenReturn(auxFolder2);

        EventResult result = scheduleFilePlanLoaders.processEvent(null, new StopWatch());

        verify(mockedSessionService, times(4)).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Raised further 4 events and rescheduled self.",result.getData());
        assertEquals(5, result.getNextEvents().size());

        for(int i=0; i<4; i++)
        {
            Event event = result.getNextEvents().get(i);
            assertEquals(EVENT_LOAD_RECORD_FOLDER, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_CATEGORY_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            if(i < 2)
            {
                assertEquals("/a", (String) dataObj.get(FIELD_PATH));
            }
            else
            {
                assertEquals("/b", (String) dataObj.get(FIELD_PATH));
            }
            assertEquals(LOAD_RECORD_FOLDER_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
        }
        assertEquals(EVENT_SCHEDULE_SELF, result.getNextEvents().get(4).getName());
    }

    @Test
    public void testSchedule4RecordFoldersOnLastLevelWithMaxActiveLoader3() throws Exception
    {
        int maxActiveLoaders = 3;
        int rootCategoriesNumber = 0;
        int categoriesChildrenNumber = 0;
        int foldersChildrenNumber= 2;
        int categoryStructureDepth = 4;

        scheduleFilePlanLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleFilePlanLoaders.setCategoryNumber(rootCategoriesNumber);
        scheduleFilePlanLoaders.setCategoryStructureDepth(categoryStructureDepth);
        scheduleFilePlanLoaders.setChildCategNumber(categoriesChildrenNumber);

        scheduleFilePlanLoaders.setFolderNumber(foldersChildrenNumber);
        scheduleFilePlanLoaders.setFolderCategoryMix(true);

        FolderData mockedRootCategoryFolder = mock(FolderData.class);
        when(mockedRootCategoryFolder.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedRootCategoryFolder.getPath()).thenReturn("/a");
        String name1 = ROOT_CATEGORY_NAME_IDENTIFIER + UUID.randomUUID().toString();
        when(mockedRootCategoryFolder.getName()).thenReturn(name1);

        FolderData mockedChildCategoryFolder = mock(FolderData.class);
        when(mockedChildCategoryFolder.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedChildCategoryFolder.getPath()).thenReturn("/b");
        String name2 = CATEGORY_NAME_IDENTIFIER + UUID.randomUUID().toString();
        when(mockedChildCategoryFolder.getName()).thenReturn(name2);

        FolderData filePlanFolder = mock(FolderData.class);
        when(filePlanFolder.getContext()).thenReturn(FILEPLAN_CONTEXT);
        when(filePlanFolder.getPath()).thenReturn(RECORD_CONTAINER_PATH);
        when(mockedFileFolderService.getFolder(FILEPLAN_CONTEXT, RECORD_CONTAINER_PATH)).thenReturn(filePlanFolder);

        List<FolderData> folders = Arrays.asList(mockedRootCategoryFolder, mockedChildCategoryFolder);
        when(mockedFileFolderService.getFoldersByCounts(RECORD_CATEGORY_CONTEXT, Long.valueOf(scheduleFilePlanLoaders.getMaxLevel()), Long.valueOf(scheduleFilePlanLoaders.getMaxLevel()), 0L, Long.valueOf(foldersChildrenNumber-1), null, null, 0, 100)).thenReturn(folders);

        String id1 = UUID.randomUUID().toString();
        FolderData auxFolder1 = mock(FolderData.class);
        when(auxFolder1.getFileCount()).thenReturn(2L)
        .thenReturn(0L);
        when(mockedRootCategoryFolder.getId()).thenReturn(id1);
        when(mockedAuxFileFolderService.getFolder(id1)).thenReturn(auxFolder1);

        String id2 = UUID.randomUUID().toString();
        FolderData auxFolder2 = mock(FolderData.class);
        when(auxFolder2.getFileCount()).thenReturn(2L)
        .thenReturn(0L);
        when(mockedChildCategoryFolder.getId()).thenReturn(id2);
        when(mockedAuxFileFolderService.getFolder(id2)).thenReturn(auxFolder2);

        EventResult result = scheduleFilePlanLoaders.processEvent(null, new StopWatch());

        verify(mockedSessionService, times(3)).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Raised further 3 events and rescheduled self.",result.getData());
        assertEquals(4, result.getNextEvents().size());

        for(int i=0; i<3; i++)
        {
            Event event = result.getNextEvents().get(i);
            assertEquals(EVENT_LOAD_RECORD_FOLDER, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_CATEGORY_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            if(i < 2)
            {
                assertEquals("/a", (String) dataObj.get(FIELD_PATH));
            }
            else
            {
                assertEquals("/b", (String) dataObj.get(FIELD_PATH));
            }
            assertEquals(LOAD_RECORD_FOLDER_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
        }
        assertEquals(EVENT_SCHEDULE_SELF, result.getNextEvents().get(3).getName());
    }

    @Test
    public void testSchedule4ChildrenRecordFoldersWithMaxActiveLoaders3() throws Exception
    {
        int maxActiveLoaders = 3;
        int rootCategoriesNumber = 0;
        int categoriesChildrenNumber = 0;
        int foldersChildrenNumber= 2;
        int categoryStructureDepth = 4;

        scheduleFilePlanLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleFilePlanLoaders.setCategoryNumber(rootCategoriesNumber);
        scheduleFilePlanLoaders.setCategoryStructureDepth(categoryStructureDepth);
        scheduleFilePlanLoaders.setChildCategNumber(categoriesChildrenNumber);

        scheduleFilePlanLoaders.setFolderNumber(foldersChildrenNumber);
        scheduleFilePlanLoaders.setFolderCategoryMix(true);

        FolderData mockedRootCategoryFolder = mock(FolderData.class);
        when(mockedRootCategoryFolder.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedRootCategoryFolder.getPath()).thenReturn("/a");
        String name1 = ROOT_CATEGORY_NAME_IDENTIFIER + UUID.randomUUID().toString();
        when(mockedRootCategoryFolder.getName()).thenReturn(name1);

        FolderData mockedChildCategoryFolder = mock(FolderData.class);
        when(mockedChildCategoryFolder.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedChildCategoryFolder.getPath()).thenReturn("/b");
        String name2 = CATEGORY_NAME_IDENTIFIER + UUID.randomUUID().toString();
        when(mockedChildCategoryFolder.getName()).thenReturn(name2);

        FolderData filePlanFolder = mock(FolderData.class);
        when(filePlanFolder.getContext()).thenReturn(FILEPLAN_CONTEXT);
        when(filePlanFolder.getPath()).thenReturn(RECORD_CONTAINER_PATH);
        when(mockedFileFolderService.getFolder(FILEPLAN_CONTEXT, RECORD_CONTAINER_PATH)).thenReturn(filePlanFolder);

        List<FolderData> folders = Arrays.asList(mockedRootCategoryFolder, mockedChildCategoryFolder);
        when(mockedFileFolderService.getFoldersByCounts(RECORD_CATEGORY_CONTEXT, Long.valueOf(FILE_PLAN_LEVEL+1), Long.valueOf(scheduleFilePlanLoaders.getMaxLevel() - 1), 0L, Long.valueOf(foldersChildrenNumber-1), null, null, 0, 100)).thenReturn(folders);

        String id1 = UUID.randomUUID().toString();
        FolderData auxFolder1 = mock(FolderData.class);
        when(auxFolder1.getFileCount()).thenReturn(2L)
        .thenReturn(0L);
        when(mockedRootCategoryFolder.getId()).thenReturn(id1);
        when(mockedAuxFileFolderService.getFolder(id1)).thenReturn(auxFolder1);

        String id2 = UUID.randomUUID().toString();
        FolderData auxFolder2 = mock(FolderData.class);
        when(auxFolder2.getFileCount()).thenReturn(2L)
        .thenReturn(0L);
        when(mockedChildCategoryFolder.getId()).thenReturn(id2);
        when(mockedAuxFileFolderService.getFolder(id2)).thenReturn(auxFolder2);

        EventResult result = scheduleFilePlanLoaders.processEvent(null, new StopWatch());

        verify(mockedSessionService, times(3)).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Raised further 3 events and rescheduled self.",result.getData());
        assertEquals(4, result.getNextEvents().size());

        for(int i=0; i<3; i++)
        {
            Event event = result.getNextEvents().get(i);
            assertEquals(EVENT_LOAD_RECORD_FOLDER, event.getName());
            DBObject dataObj = (DBObject)event.getData();
            assertNotNull(dataObj);
            assertEquals(RECORD_CATEGORY_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
            if(i < 2)
            {
                assertEquals("/a", (String) dataObj.get(FIELD_PATH));
            }
            else
            {
                assertEquals("/b", (String) dataObj.get(FIELD_PATH));
            }
            assertEquals(LOAD_RECORD_FOLDER_OPERATION, dataObj.get(FIELD_LOAD_OPERATION));
        }
        assertEquals(EVENT_SCHEDULE_SELF, result.getNextEvents().get(3).getName());
    }

    @Test
    public void testScheduleChildrenRecordFoldersWithoutMix() throws Exception
    {
        int maxActiveLoaders = 8;
        int rootCategoriesNumber = 0;
        int categoriesChildrenNumber = 0;
        int foldersChildrenNumber= 2;
        int categoryStructureDepth = 4;

        scheduleFilePlanLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleFilePlanLoaders.setCategoryNumber(rootCategoriesNumber);
        scheduleFilePlanLoaders.setCategoryStructureDepth(categoryStructureDepth);
        scheduleFilePlanLoaders.setChildCategNumber(categoriesChildrenNumber);

        scheduleFilePlanLoaders.setFolderNumber(foldersChildrenNumber);
        scheduleFilePlanLoaders.setFolderCategoryMix(false);

        FolderData mockedRootCategoryFolder = mock(FolderData.class);
        when(mockedRootCategoryFolder.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedRootCategoryFolder.getPath()).thenReturn("/a");
        String name1 = ROOT_CATEGORY_NAME_IDENTIFIER + UUID.randomUUID().toString();
        when(mockedRootCategoryFolder.getName()).thenReturn(name1);

        FolderData mockedChildCategoryFolder = mock(FolderData.class);
        when(mockedChildCategoryFolder.getContext()).thenReturn(RECORD_CATEGORY_CONTEXT);
        when(mockedChildCategoryFolder.getPath()).thenReturn("/b");
        String name2 = CATEGORY_NAME_IDENTIFIER + UUID.randomUUID().toString();
        when(mockedChildCategoryFolder.getName()).thenReturn(name2);

        FolderData filePlanFolder = mock(FolderData.class);
        when(filePlanFolder.getContext()).thenReturn(FILEPLAN_CONTEXT);
        when(filePlanFolder.getPath()).thenReturn(RECORD_CONTAINER_PATH);

        when(mockedFileFolderService.getFolder(FILEPLAN_CONTEXT, RECORD_CONTAINER_PATH)).thenReturn(filePlanFolder);

        String id1 = UUID.randomUUID().toString();
        FolderData auxFolder1 = mock(FolderData.class);
        when(auxFolder1.getFileCount()).thenReturn(2L)
                                         .thenReturn(0L);
        when(mockedRootCategoryFolder.getId()).thenReturn(id1);
        when(mockedAuxFileFolderService.getFolder(id1)).thenReturn(auxFolder1);

        String id2 = UUID.randomUUID().toString();
        FolderData auxFolder2 = mock(FolderData.class);
        when(auxFolder2.getFileCount()).thenReturn(2L)
                                         .thenReturn(0L);
        when(mockedChildCategoryFolder.getId()).thenReturn(id2);
        when(mockedAuxFileFolderService.getFolder(id2)).thenReturn(auxFolder2);


        List<FolderData> folders = Arrays.asList(mockedRootCategoryFolder, mockedChildCategoryFolder);
        when(mockedFileFolderService.getFoldersByCounts(RECORD_CATEGORY_CONTEXT, Long.valueOf(FILE_PLAN_LEVEL+1), Long.valueOf(scheduleFilePlanLoaders.getMaxLevel() - 1), 0L, Long.valueOf(foldersChildrenNumber-1), null, null, 0, 100)).thenReturn(folders);

        EventResult result = scheduleFilePlanLoaders.processEvent(null, new StopWatch());

        verify(mockedSessionService, never()).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Loading completed.  Raising 'done' event.",result.getData());
        assertEquals(1, result.getNextEvents().size());
        assertEquals(EVENT_FILE_PLANLOADING_COMPLETE, result.getNextEvents().get(0).getName());
    }

    @Test
    public void testFilePlanFoldersStructureNotWanted() throws Exception
    {
        scheduleFilePlanLoaders.setCreateFileplanFolderStructure(false);
        EventResult result = scheduleFilePlanLoaders.processEvent(null, new StopWatch());

        verify(mockedFileFolderService, never()).createNewFolder(any(FolderData.class));
        verify(mockedSessionService, never()).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("FilePlan folders structure creation not wanted, continue with loading data.",result.getData());
        assertEquals(1, result.getNextEvents().size());
    }
}
