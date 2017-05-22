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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
    @Mock
    private SessionService mockedSessionService;

    @Mock
    private FileFolderService mockedFileFolderService;

    @Mock
    private ExtendedFileFolderService mockedAuxFileFolderService;

    @InjectMocks
    private ScheduleFilePlanLoaders scheduleFilePlanLoaders;

    @Test
    public void testScheduleRootCategories() throws Exception
    {
        int maxActiveLoaders = 8;
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

        EventResult result = scheduleFilePlanLoaders.processEvent(null, new StopWatch());

        verify(mockedFileFolderService, times(1)).createNewFolder(any(FolderData.class));
        verify(mockedSessionService, times(1)).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Raised further 1 events and rescheduled self.",result.getData());
        assertEquals(2, result.getNextEvents().size());

        Event firstEvent = result.getNextEvents().get(0);
        assertEquals("loadRecordCategories", firstEvent.getName());
        DBObject dataObj = (DBObject)firstEvent.getData();
        assertNotNull(dataObj);
        assertEquals(FILEPLAN_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
        assertEquals("/a", (String) dataObj.get(FIELD_PATH));
        assertEquals(Integer.valueOf(0), (Integer) dataObj.get(FIELD_CATEGORIES_TO_CREATE));
        assertEquals(Integer.valueOf(0), (Integer) dataObj.get(FIELD_FOLDERS_TO_CREATE));

        assertEquals("scheduleFilePlanLoaders", result.getNextEvents().get(1).getName());
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
        assertEquals("scheduleUnfiledRecordFoldersLoaders", result.getNextEvents().get(0).getName());
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

        List<FolderData> folders = Arrays.asList(mockedRootCategoryFolder, mockedChildCategoryFolder);
        when(mockedFileFolderService.getFoldersByCounts(RECORD_CATEGORY_CONTEXT, Long.valueOf(FILE_PLAN_LEVEL+1), Long.valueOf(scheduleFilePlanLoaders.getMaxLevel() - 1), 0L, Long.valueOf(categoriesChildrenNumber-1), null, null, 0, 100)).thenReturn(folders);

        when(mockedFileFolderService.getChildFolders(RECORD_CATEGORY_CONTEXT, "/a", 0, 100)).thenReturn(new ArrayList<FolderData>());
        when(mockedFileFolderService.getChildFolders(RECORD_CATEGORY_CONTEXT, "/b", 0, 100)).thenReturn(new ArrayList<FolderData>());
        EventResult result = scheduleFilePlanLoaders.processEvent(null, new StopWatch());

        verify(mockedFileFolderService, times(2)).createNewFolder(any(FolderData.class));
        verify(mockedSessionService, times(2)).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Raised further 2 events and rescheduled self.",result.getData());
        assertEquals(3, result.getNextEvents().size());

        Event firstEvent = result.getNextEvents().get(0);
        assertEquals("loadRecordCategories", firstEvent.getName());
        DBObject dataObj = (DBObject)firstEvent.getData();
        assertNotNull(dataObj);
        assertEquals(RECORD_CATEGORY_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
        assertEquals("/a", (String) dataObj.get(FIELD_PATH));
        assertEquals(Integer.valueOf(categoriesChildrenNumber), (Integer) dataObj.get(FIELD_CATEGORIES_TO_CREATE));
        assertEquals(Integer.valueOf(foldersChildrenNumber), (Integer) dataObj.get(FIELD_FOLDERS_TO_CREATE));

        Event secondEvent = result.getNextEvents().get(1);
        assertEquals("loadRecordCategories", secondEvent.getName());
        dataObj = (DBObject)secondEvent.getData();
        assertNotNull(dataObj);
        assertEquals(RECORD_CATEGORY_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
        assertEquals("/b", (String) dataObj.get(FIELD_PATH));
        assertEquals(Integer.valueOf(categoriesChildrenNumber), (Integer) dataObj.get(FIELD_CATEGORIES_TO_CREATE));
        assertEquals(Integer.valueOf(foldersChildrenNumber), (Integer) dataObj.get(FIELD_FOLDERS_TO_CREATE));

        assertEquals("scheduleFilePlanLoaders", result.getNextEvents().get(2).getName());
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

        EventResult result = scheduleFilePlanLoaders.processEvent(null, new StopWatch());

        verify(mockedFileFolderService, never()).createNewFolder(any(FolderData.class));
        verify(mockedSessionService, never()).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Loading completed.  Raising 'done' event.",result.getData());
        assertEquals(1, result.getNextEvents().size());
        assertEquals("scheduleUnfiledRecordFoldersLoaders", result.getNextEvents().get(0).getName());
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

        List<FolderData> folders = Arrays.asList(mockedRootCategoryFolder, mockedChildCategoryFolder);
        when(mockedFileFolderService.getFoldersByCounts(RECORD_CATEGORY_CONTEXT, Long.valueOf(FILE_PLAN_LEVEL+1), Long.valueOf(scheduleFilePlanLoaders.getMaxLevel() - 1 -1), 0L, Long.valueOf(foldersChildrenNumber-1), null, null, 0, 100)).thenReturn(folders);

        EventResult result = scheduleFilePlanLoaders.processEvent(null, new StopWatch());

        verify(mockedFileFolderService, never()).createNewFolder(any(FolderData.class));
        verify(mockedSessionService, never()).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Loading completed.  Raising 'done' event.",result.getData());
        assertEquals(1, result.getNextEvents().size());
        assertEquals("scheduleUnfiledRecordFoldersLoaders", result.getNextEvents().get(0).getName());
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

        List<FolderData> folders = Arrays.asList(mockedRootCategoryFolder, mockedChildCategoryFolder);
        when(mockedFileFolderService.getFoldersByCounts(RECORD_CATEGORY_CONTEXT, Long.valueOf(FILE_PLAN_LEVEL+1), Long.valueOf(scheduleFilePlanLoaders.getMaxLevel() - 1), 0L, Long.valueOf(foldersChildrenNumber-1), null, null, 0, 100)).thenReturn(folders);

        EventResult result = scheduleFilePlanLoaders.processEvent(null, new StopWatch());

        verify(mockedFileFolderService, times(2)).createNewFolder(any(FolderData.class));
        verify(mockedSessionService, times(2)).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Raised further 2 events and rescheduled self.",result.getData());
        assertEquals(3, result.getNextEvents().size());

        Event firstEvent = result.getNextEvents().get(0);
        assertEquals("loadRecordCategories", firstEvent.getName());
        DBObject dataObj = (DBObject)firstEvent.getData();
        assertNotNull(dataObj);
        assertEquals(RECORD_CATEGORY_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
        assertEquals("/a", (String) dataObj.get(FIELD_PATH));
        assertEquals(Integer.valueOf(categoriesChildrenNumber), (Integer) dataObj.get(FIELD_CATEGORIES_TO_CREATE));
        assertEquals(Integer.valueOf(foldersChildrenNumber), (Integer) dataObj.get(FIELD_FOLDERS_TO_CREATE));

        Event secondEvent = result.getNextEvents().get(1);
        assertEquals("loadRecordCategories", secondEvent.getName());
        dataObj = (DBObject)secondEvent.getData();
        assertNotNull(dataObj);
        assertEquals(RECORD_CATEGORY_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
        assertEquals("/b", (String) dataObj.get(FIELD_PATH));
        assertEquals(Integer.valueOf(categoriesChildrenNumber), (Integer) dataObj.get(FIELD_CATEGORIES_TO_CREATE));
        assertEquals(Integer.valueOf(foldersChildrenNumber), (Integer) dataObj.get(FIELD_FOLDERS_TO_CREATE));

        assertEquals("scheduleFilePlanLoaders", result.getNextEvents().get(2).getName());
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

        List<FolderData> folders = Arrays.asList(mockedRootCategoryFolder, mockedChildCategoryFolder);
        when(mockedFileFolderService.getFoldersByCounts(RECORD_CATEGORY_CONTEXT, Long.valueOf(FILE_PLAN_LEVEL+1), Long.valueOf(scheduleFilePlanLoaders.getMaxLevel() - 1), 0L, Long.valueOf(foldersChildrenNumber-1), null, null, 0, 100)).thenReturn(folders);

        EventResult result = scheduleFilePlanLoaders.processEvent(null, new StopWatch());

        verify(mockedFileFolderService, times(2)).createNewFolder(any(FolderData.class));
        verify(mockedSessionService, times(2)).startSession(any(DBObject.class));

        assertEquals(true, result.isSuccess());
        assertEquals("Raised further 2 events and rescheduled self.",result.getData());
        assertEquals(3, result.getNextEvents().size());

        Event firstEvent = result.getNextEvents().get(0);
        assertEquals("loadRecordCategories", firstEvent.getName());
        DBObject dataObj = (DBObject)firstEvent.getData();
        assertNotNull(dataObj);
        assertEquals(RECORD_CATEGORY_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
        assertEquals("/a", (String) dataObj.get(FIELD_PATH));
        assertEquals(Integer.valueOf(categoriesChildrenNumber), (Integer) dataObj.get(FIELD_CATEGORIES_TO_CREATE));
        assertEquals(Integer.valueOf(0), (Integer) dataObj.get(FIELD_FOLDERS_TO_CREATE));

        Event secondEvent = result.getNextEvents().get(1);
        assertEquals("loadRecordCategories", secondEvent.getName());
        dataObj = (DBObject)secondEvent.getData();
        assertNotNull(dataObj);
        assertEquals(RECORD_CATEGORY_CONTEXT, (String) dataObj.get(FIELD_CONTEXT));
        assertEquals("/b", (String) dataObj.get(FIELD_PATH));
        assertEquals(Integer.valueOf(categoriesChildrenNumber), (Integer) dataObj.get(FIELD_CATEGORIES_TO_CREATE));
        assertEquals(Integer.valueOf(0), (Integer) dataObj.get(FIELD_FOLDERS_TO_CREATE));

        assertEquals("scheduleFilePlanLoaders", result.getNextEvents().get(2).getName());
    }
}
