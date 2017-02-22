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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import com.mongodb.DBObject;

import org.alfresco.bm.cm.FileFolderService;
import org.alfresco.bm.cm.FolderData;
import org.alfresco.bm.data.DataCreationState;
import org.alfresco.bm.dataload.RMEventConstants;
import org.alfresco.bm.dataload.rm.role.RMRole;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.session.SessionService;
import org.alfresco.bm.site.SiteData;
import org.alfresco.bm.site.SiteDataService;
import org.alfresco.bm.site.SiteMemberData;
import org.alfresco.bm.user.UserData;
import org.alfresco.bm.user.UserDataService;
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

/**
 * Unit tests for LoadFilePlan
 *
 * @author Silviu Dinuta
 * @since 2.6
 */
@RunWith(MockitoJUnitRunner.class)
public class LoadFilePlanUnitTest implements RMEventConstants
{
    @Mock
    private SessionService mockedSessionService;

    @Mock
    private FileFolderService mockedFileFolderService;

    @Mock
    private RestAPIFactory mockedRestAPIFactory;

    @Mock
    private FilePlanComponentAPI mockedFilePlanComponentAPI;

    @Mock
    private UserDataService mockedUserDataService;

    @Mock
    private SiteDataService mockedSiteDataService;

    @InjectMocks
    private LoadFilePlan loadFilePlan;

    @Test(expected=IllegalStateException.class)
    public void testWithNullEvent() throws Exception
    {
        loadFilePlan.processEvent(null, new StopWatch());
    }

    @Test(expected=IllegalStateException.class)
    public void testWithNullData() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        when(mockedEvent.getData()).thenReturn(null);
        loadFilePlan.processEvent(mockedEvent, new StopWatch());
    }

    @Test
    public void testWithNullContext() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn(null);
        when(mockedEvent.getData()).thenReturn(mockedData);
        EventResult result = loadFilePlan.processEvent(mockedEvent, new StopWatch());
        assertEquals(false, result.isSuccess());
        assertEquals("Request data not complete for folder loading: " + mockedData, result.getData());
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
        EventResult result = loadFilePlan.processEvent(mockedEvent, new StopWatch());
        assertEquals(false, result.isSuccess());
        assertEquals("Request data not complete for folder loading: " + mockedData, result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testWithNullRootCategoriesToCreate() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_ROOT_CATEGORIES_TO_CREATE)).thenReturn(null);
        when(mockedEvent.getData()).thenReturn(mockedData);
        EventResult result = loadFilePlan.processEvent(mockedEvent, new StopWatch());
        assertEquals(false, result.isSuccess());
        assertEquals("Request data not complete for folder loading: " + mockedData, result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testWithNullChildCategoriesToCreate() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_ROOT_CATEGORIES_TO_CREATE)).thenReturn(Integer.valueOf(0));
        when(mockedData.get(FIELD_CATEGORIES_TO_CREATE)).thenReturn(null);
        when(mockedEvent.getData()).thenReturn(mockedData);
        EventResult result = loadFilePlan.processEvent(mockedEvent, new StopWatch());
        assertEquals(false, result.isSuccess());
        assertEquals("Request data not complete for folder loading: " + mockedData, result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testWithNullChildRecordFoldersToCreate() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_ROOT_CATEGORIES_TO_CREATE)).thenReturn(Integer.valueOf(0));
        when(mockedData.get(FIELD_CATEGORIES_TO_CREATE)).thenReturn(Integer.valueOf(0));
        when(mockedData.get(FIELD_FOLDERS_TO_CREATE)).thenReturn(null);
        when(mockedEvent.getData()).thenReturn(mockedData);
        EventResult result = loadFilePlan.processEvent(mockedEvent, new StopWatch());
        assertEquals(false, result.isSuccess());
        assertEquals("Request data not complete for folder loading: " + mockedData, result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test(expected=IllegalStateException.class)
    public void testInexistentFolderForContextAndPath() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_ROOT_CATEGORIES_TO_CREATE)).thenReturn(Integer.valueOf(0));
        when(mockedData.get(FIELD_CATEGORIES_TO_CREATE)).thenReturn(Integer.valueOf(0));
        when(mockedData.get(FIELD_FOLDERS_TO_CREATE)).thenReturn(Integer.valueOf(0));
        when(mockedEvent.getData()).thenReturn(mockedData);
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(null);
        loadFilePlan.setFileFolderService(mockedFileFolderService);

        loadFilePlan.processEvent(mockedEvent, new StopWatch());
    }

    @Test
    public void testWithNullSessionID() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("someContext");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_ROOT_CATEGORIES_TO_CREATE)).thenReturn(Integer.valueOf(0));
        when(mockedData.get(FIELD_CATEGORIES_TO_CREATE)).thenReturn(Integer.valueOf(0));
        when(mockedData.get(FIELD_FOLDERS_TO_CREATE)).thenReturn(Integer.valueOf(0));
        when(mockedEvent.getData()).thenReturn(mockedData);
        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFileFolderService.getFolder("someContext", "/aPath")).thenReturn(mockedFolder);
        loadFilePlan.setFileFolderService(mockedFileFolderService);
        when(mockedEvent.getSessionId()).thenReturn(null);

        EventResult result = loadFilePlan.processEvent(mockedEvent, new StopWatch());
        assertEquals(false, result.isSuccess());
        assertEquals("Load scheduling should create a session for each loader.", result.getData());
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testLoadNothingToCreate() throws Exception
    {
        int rootCategoriesNumber = 0;
        int childCategoriesNumber = 0;
        int childRecordFolderNumber = 0;

        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_ROOT_CATEGORIES_TO_CREATE)).thenReturn(Integer.valueOf(rootCategoriesNumber));
        when(mockedData.get(FIELD_CATEGORIES_TO_CREATE)).thenReturn(Integer.valueOf(childCategoriesNumber));
        when(mockedData.get(FIELD_FOLDERS_TO_CREATE)).thenReturn(Integer.valueOf(childRecordFolderNumber));
        when(mockedEvent.getData()).thenReturn(mockedData);
        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFolder.getId()).thenReturn("folderId");
        when(mockedFolder.getPath()).thenReturn("/aPath");
        when(mockedFileFolderService.getFolder("", "/aPath")).thenReturn(mockedFolder);
        loadFilePlan.setEventNameRecordCategoryLoaded("recordCategoriesLoaded");
        loadFilePlan.setFileFolderService(mockedFileFolderService);
        when(mockedEvent.getSessionId()).thenReturn("someId");

        when(mockedRestAPIFactory.getFilePlanComponentsAPI(any(UserModel.class))).thenReturn(mockedFilePlanComponentAPI);
        FilePlanComponent mockedFilePlanComponent = mock(FilePlanComponent.class);
        when(mockedFilePlanComponent.getId()).thenReturn("folderId");
        when(mockedFilePlanComponentAPI.getFilePlanComponent("folderId")).thenReturn(mockedFilePlanComponent);
        FilePlanComponent mockedChildFilePlanComponent = mock(FilePlanComponent.class);
        when(mockedChildFilePlanComponent.getId()).thenReturn(UUID.randomUUID().toString());
        when(mockedFilePlanComponentAPI.createFilePlanComponent(any(FilePlanComponent.class), eq("folderId"))).thenReturn(mockedChildFilePlanComponent);

        mockSiteAndUserData();
        EventResult result = loadFilePlan.processEvent(mockedEvent, new StopWatch());
        verify(mockedFileFolderService, never()).createNewFolder(any(String.class), any(String.class), any(String.class));
        verify(mockedFileFolderService, never()).incrementFolderCount(any(String.class), any(String.class), any(Long.class));
        assertEquals(true, result.isSuccess());
        DBObject data = (DBObject) result.getData();
        assertEquals("Created " + rootCategoriesNumber + " root categories, " + childCategoriesNumber + " categories and " + childRecordFolderNumber + " record folders.", data.get("msg"));
        assertEquals("/aPath", data.get(FIELD_PATH));
        assertEquals("aUser", data.get("username"));
        assertEquals(1, result.getNextEvents().size());
    }

    @Test
    public void testLoadRootCategoriesWithExceptionOnRestApi() throws Exception
    {
        int rootCategoriesNumber = 3;
        int childCategoriesNumber = 0;
        int childRecordFolderNumber = 0;

        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_ROOT_CATEGORIES_TO_CREATE)).thenReturn(Integer.valueOf(rootCategoriesNumber));
        when(mockedData.get(FIELD_CATEGORIES_TO_CREATE)).thenReturn(Integer.valueOf(childCategoriesNumber));
        when(mockedData.get(FIELD_FOLDERS_TO_CREATE)).thenReturn(Integer.valueOf(childRecordFolderNumber));
        when(mockedEvent.getData()).thenReturn(mockedData);
        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFolder.getId()).thenReturn("folderId");
        when(mockedFolder.getPath()).thenReturn("/aPath");
        when(mockedFileFolderService.getFolder("", "/aPath")).thenReturn(mockedFolder);
        loadFilePlan.setEventNameRecordCategoryLoaded("recordCategoriesLoaded");
        loadFilePlan.setFileFolderService(mockedFileFolderService);
        when(mockedEvent.getSessionId()).thenReturn("someId");
        when(mockedRestAPIFactory.getFilePlanComponentsAPI(any(UserModel.class))).thenReturn(mockedFilePlanComponentAPI);
        FilePlanComponent mockedFilePlanComponent = mock(FilePlanComponent.class);
        when(mockedFilePlanComponent.getId()).thenReturn("folderId");
        when(mockedFilePlanComponentAPI.getFilePlanComponent("folderId")).thenReturn(mockedFilePlanComponent);

        Mockito.doThrow(new Exception("someError")).when(mockedFilePlanComponentAPI).createFilePlanComponent(any(FilePlanComponent.class), any(String.class));
        mockSiteAndUserData();
        EventResult result = loadFilePlan.processEvent(mockedEvent, new StopWatch());
        verify(mockedFileFolderService, never()).createNewFolder(any(String.class), any(String.class), any(String.class));
        verify(mockedFileFolderService, never()).incrementFolderCount(any(String.class), any(String.class), any(Long.class));
        assertEquals(false, result.isSuccess());
        DBObject data = (DBObject) result.getData();
        assertNotNull(data.get("error"));
        assertEquals("aUser", data.get("username"));
        assertEquals(mockedFolder.getPath(), data.get("path"));
        assertNotNull(data.get("stack"));
        assertEquals(0, result.getNextEvents().size());
    }

    @Test
    public void testLoadRootCategories() throws Exception
    {
        int rootCategoriesNumber = 3;
        int childCategoriesNumber = 0;
        int childRecordFolderNumber = 0;

        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_ROOT_CATEGORIES_TO_CREATE)).thenReturn(Integer.valueOf(rootCategoriesNumber));
        when(mockedData.get(FIELD_CATEGORIES_TO_CREATE)).thenReturn(Integer.valueOf(childCategoriesNumber));
        when(mockedData.get(FIELD_FOLDERS_TO_CREATE)).thenReturn(Integer.valueOf(childRecordFolderNumber));
        when(mockedEvent.getData()).thenReturn(mockedData);
        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFolder.getId()).thenReturn("folderId");
        when(mockedFolder.getPath()).thenReturn("/aPath");
        when(mockedFileFolderService.getFolder("", "/aPath")).thenReturn(mockedFolder);
        loadFilePlan.setEventNameRecordCategoryLoaded("recordCategoriesLoaded");
        loadFilePlan.setFileFolderService(mockedFileFolderService);
        when(mockedEvent.getSessionId()).thenReturn("someId");

        when(mockedRestAPIFactory.getFilePlanComponentsAPI(any(UserModel.class))).thenReturn(mockedFilePlanComponentAPI);
        FilePlanComponent mockedFilePlanComponent = mock(FilePlanComponent.class);
        when(mockedFilePlanComponent.getId()).thenReturn("folderId");
        when(mockedFilePlanComponentAPI.getFilePlanComponent("folderId")).thenReturn(mockedFilePlanComponent);
        FilePlanComponent mockedChildFilePlanComponent = mock(FilePlanComponent.class);
        when(mockedChildFilePlanComponent.getId()).thenReturn(UUID.randomUUID().toString());
        when(mockedFilePlanComponentAPI.createFilePlanComponent(any(FilePlanComponent.class), eq("folderId"))).thenReturn(mockedChildFilePlanComponent);

        mockSiteAndUserData();

        EventResult result = loadFilePlan.processEvent(mockedEvent, new StopWatch());
        verify(mockedFileFolderService, times(rootCategoriesNumber)).createNewFolder(any(String.class), any(String.class), any(String.class));
        verify(mockedFileFolderService, times(1)).incrementFolderCount(any(String.class), any(String.class), any(Long.class));
        assertEquals(true, result.isSuccess());
        DBObject data = (DBObject) result.getData();
        assertEquals("Created " + rootCategoriesNumber + " root categories, " + childCategoriesNumber + " categories and " + childRecordFolderNumber + " record folders.", data.get("msg"));
        assertEquals("/aPath", data.get(FIELD_PATH));
        assertEquals("aUser", data.get("username"));
        assertEquals(1, result.getNextEvents().size());
    }

    @Test
    public void testLoadChildrenCategories() throws Exception
    {
        int rootCategoriesNumber = 0;
        int childCategoriesNumber = 4;
        int childRecordFolderNumber = 0;

        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_ROOT_CATEGORIES_TO_CREATE)).thenReturn(Integer.valueOf(rootCategoriesNumber));
        when(mockedData.get(FIELD_CATEGORIES_TO_CREATE)).thenReturn(Integer.valueOf(childCategoriesNumber));
        when(mockedData.get(FIELD_FOLDERS_TO_CREATE)).thenReturn(Integer.valueOf(childRecordFolderNumber));
        when(mockedEvent.getData()).thenReturn(mockedData);
        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFolder.getId()).thenReturn("folderId");
        when(mockedFolder.getPath()).thenReturn("/aPath");
        when(mockedFileFolderService.getFolder("", "/aPath")).thenReturn(mockedFolder);
        loadFilePlan.setEventNameRecordCategoryLoaded("recordCategoriesLoaded");
        loadFilePlan.setFileFolderService(mockedFileFolderService);
        when(mockedEvent.getSessionId()).thenReturn("someId");

        when(mockedRestAPIFactory.getFilePlanComponentsAPI(any(UserModel.class))).thenReturn(mockedFilePlanComponentAPI);
        FilePlanComponent mockedFilePlanComponent = mock(FilePlanComponent.class);
        when(mockedFilePlanComponent.getId()).thenReturn("folderId");
        when(mockedFilePlanComponentAPI.getFilePlanComponent("folderId")).thenReturn(mockedFilePlanComponent);
        FilePlanComponent mockedChildFilePlanComponent = mock(FilePlanComponent.class);
        when(mockedChildFilePlanComponent.getId()).thenReturn(UUID.randomUUID().toString());
        when(mockedFilePlanComponentAPI.createFilePlanComponent(any(FilePlanComponent.class), eq("folderId"))).thenReturn(mockedChildFilePlanComponent);

        mockSiteAndUserData();

        EventResult result = loadFilePlan.processEvent(mockedEvent, new StopWatch());
        verify(mockedFileFolderService, times(childCategoriesNumber)).createNewFolder(any(String.class), any(String.class), any(String.class));
        verify(mockedFileFolderService, times(1)).incrementFolderCount(any(String.class), any(String.class), any(Long.class));
        assertEquals(true, result.isSuccess());
        DBObject data = (DBObject) result.getData();
        assertEquals("Created " + rootCategoriesNumber + " root categories, " + childCategoriesNumber + " categories and " + childRecordFolderNumber + " record folders.", data.get("msg"));
        assertEquals("/aPath", data.get(FIELD_PATH));
        assertEquals("aUser", data.get("username"));
        assertEquals(1, result.getNextEvents().size());
    }

    @Test
    public void testLoadChildrenRecordFolders() throws Exception
    {
        int rootCategoriesNumber = 0;
        int childCategoriesNumber = 0;
        int childRecordFolderNumber = 5;

        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_CONTEXT)).thenReturn("");
        when(mockedData.get(FIELD_PATH)).thenReturn("/aPath");
        when(mockedData.get(FIELD_ROOT_CATEGORIES_TO_CREATE)).thenReturn(Integer.valueOf(rootCategoriesNumber));
        when(mockedData.get(FIELD_CATEGORIES_TO_CREATE)).thenReturn(Integer.valueOf(childCategoriesNumber));
        when(mockedData.get(FIELD_FOLDERS_TO_CREATE)).thenReturn(Integer.valueOf(childRecordFolderNumber));
        when(mockedEvent.getData()).thenReturn(mockedData);
        FolderData mockedFolder = mock(FolderData.class);
        when(mockedFolder.getId()).thenReturn("folderId");
        when(mockedFolder.getPath()).thenReturn("/aPath");
        when(mockedFileFolderService.getFolder("", "/aPath")).thenReturn(mockedFolder);
        loadFilePlan.setEventNameRecordCategoryLoaded("recordCategoriesLoaded");
        loadFilePlan.setFileFolderService(mockedFileFolderService);
        when(mockedEvent.getSessionId()).thenReturn("someId");

        when(mockedRestAPIFactory.getFilePlanComponentsAPI(any(UserModel.class))).thenReturn(mockedFilePlanComponentAPI);
        FilePlanComponent mockedFilePlanComponent = mock(FilePlanComponent.class);
        when(mockedFilePlanComponent.getId()).thenReturn("folderId");
        when(mockedFilePlanComponentAPI.getFilePlanComponent("folderId")).thenReturn(mockedFilePlanComponent);
        FilePlanComponent mockedChildFilePlanComponent = mock(FilePlanComponent.class);
        when(mockedChildFilePlanComponent.getId()).thenReturn(UUID.randomUUID().toString());
        when(mockedFilePlanComponentAPI.createFilePlanComponent(any(FilePlanComponent.class), eq("folderId"))).thenReturn(mockedChildFilePlanComponent);

        mockSiteAndUserData();

        EventResult result = loadFilePlan.processEvent(mockedEvent, new StopWatch());
        verify(mockedFileFolderService, times(childRecordFolderNumber)).createNewFolder(any(String.class), any(String.class), any(String.class));
        verify(mockedFileFolderService, times(1)).incrementFolderCount(any(String.class), any(String.class), any(Long.class));
        assertEquals(true, result.isSuccess());
        DBObject data = (DBObject) result.getData();
        assertEquals("Created " + rootCategoriesNumber + " root categories, " + childCategoriesNumber + " categories and " + childRecordFolderNumber + " record folders.", data.get("msg"));
        assertEquals("/aPath", data.get(FIELD_PATH));
        assertEquals("aUser", data.get("username"));
        assertEquals(1, result.getNextEvents().size());
    }

    /**
     * Helper method for mocking user data
     */
    private void mockSiteAndUserData()
    {
        SiteData mockedSiteData = mock(SiteData.class);
        when(mockedSiteDataService.getSite(PATH_SNIPPET_RM_SITE_ID)).thenReturn(mockedSiteData);
        SiteMemberData mockedSiteMemberData = mock(SiteMemberData.class);
        when(mockedSiteMemberData.getUsername()).thenReturn("aUser");
        when(mockedSiteDataService.randomSiteMember(PATH_SNIPPET_RM_SITE_ID, DataCreationState.Created, null, RMRole.Administrator.toString())).thenReturn(mockedSiteMemberData);
        UserData mockedUserData = mock(UserData.class);
        when(mockedUserData.getUsername()).thenReturn("aUser");
        when(mockedUserData.getPassword()).thenReturn("aUser");
        when(mockedUserDataService.findUserByUsername("aUser")).thenReturn(mockedUserData);
    }
}
