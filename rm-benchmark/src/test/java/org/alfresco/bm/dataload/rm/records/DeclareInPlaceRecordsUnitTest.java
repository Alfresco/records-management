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
import static org.mockito.Mockito.when;

import com.mongodb.DBObject;

import org.alfresco.bm.cm.FileFolderService;
import org.alfresco.bm.cm.FolderData;
import org.alfresco.bm.dataload.RMEventConstants;
import org.alfresco.bm.dataload.rm.services.ExecutionState;
import org.alfresco.bm.dataload.rm.services.RecordContext;
import org.alfresco.bm.dataload.rm.services.RecordData;
import org.alfresco.bm.dataload.rm.services.RecordService;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.rest.core.RMRestWrapper;
import org.alfresco.rest.core.RestAPIFactory;
import org.alfresco.rest.model.RestErrorModel;
import org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponentAlias;
import org.alfresco.rest.rm.community.model.record.Record;
import org.alfresco.rest.rm.community.model.unfiledcontainer.UnfiledContainer;
import org.alfresco.rest.rm.community.requests.gscore.api.FilesAPI;
import org.alfresco.rest.rm.community.requests.gscore.api.UnfiledContainerAPI;
import org.alfresco.utility.model.UserModel;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;

/**
 * Unit tests for DeclareInPlaceRecords
 *
 * @author Silviu Dinuta
 * @since 2.6
 */
@RunWith(MockitoJUnitRunner.class)
public class DeclareInPlaceRecordsUnitTest implements RMEventConstants
{
    private static final String EMPTY_STRING = "";

    @Mock
    private RestAPIFactory mockedRestAPIFactory;

    @Mock
    private RecordService mockedRecordService;

    @Mock
    private FileFolderService mockedFileFolderService;

    @Mock
    private ApplicationContext mockedApplicationContext;

    @InjectMocks
    private DeclareInPlaceRecords declareInPlaceRecords;

    @Test(expected=IllegalStateException.class)
    public void testWithNullEvent() throws Exception
    {
        declareInPlaceRecords.processEvent(null, new StopWatch());
    }

    @Test(expected=IllegalStateException.class)
    public void testWithNullData() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        when(mockedEvent.getData()).thenReturn(null);
        declareInPlaceRecords.processEvent(mockedEvent, new StopWatch());
    }

    @Test(expected = IllegalStateException.class)
    public void testWithNullSiteId() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_ID)).thenReturn(null);
        when(mockedEvent.getData()).thenReturn(mockedData);
        declareInPlaceRecords.processEvent(mockedEvent, new StopWatch());
    }

    @Test(expected = IllegalStateException.class)
    public void testWithEmptySiteId() throws Exception
    {
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_ID)).thenReturn(EMPTY_STRING);
        when(mockedEvent.getData()).thenReturn(mockedData);
        declareInPlaceRecords.processEvent(mockedEvent, new StopWatch());
    }

    @Test(expected = IllegalStateException.class)
    public void testWithNullUsername() throws Exception
    {
        String siteId = "testColabSiteId";
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_ID)).thenReturn(siteId);
        when(mockedData.get(FIELD_USERNAME)).thenReturn(null);
        when(mockedEvent.getData()).thenReturn(mockedData);
        declareInPlaceRecords.processEvent(mockedEvent, new StopWatch());
    }

    @Test(expected = IllegalStateException.class)
    public void testWithEmptyUsername() throws Exception
    {
        String siteId = "testColabSiteId";
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_ID)).thenReturn(siteId);
        when(mockedData.get(FIELD_USERNAME)).thenReturn(EMPTY_STRING);
        when(mockedEvent.getData()).thenReturn(mockedData);
        declareInPlaceRecords.processEvent(mockedEvent, new StopWatch());
    }

    @Test(expected = IllegalStateException.class)
    public void testWithNullPassword() throws Exception
    {
        String siteId = "testColabSiteId";
        String username = "testUserName";
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_ID)).thenReturn(siteId);
        when(mockedData.get(FIELD_USERNAME)).thenReturn(username);
        when(mockedData.get(FIELD_PASSWORD)).thenReturn(null);
        when(mockedEvent.getData()).thenReturn(mockedData);
        declareInPlaceRecords.processEvent(mockedEvent, new StopWatch());
    }

    @Test(expected = IllegalStateException.class)
    public void testWithEmptyPassword() throws Exception
    {
        String siteId = "testColabSiteId";
        String username = "testUserName";
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_ID)).thenReturn(siteId);
        when(mockedData.get(FIELD_USERNAME)).thenReturn(username);
        when(mockedData.get(FIELD_PASSWORD)).thenReturn(EMPTY_STRING);
        when(mockedEvent.getData()).thenReturn(mockedData);
        declareInPlaceRecords.processEvent(mockedEvent, new StopWatch());
    }

    @Test
    public void testDeclareAsRecordWithSuccess() throws Exception
    {
        String fileId = "testFileId";
        String username = "testUserName";
        String password = "testPassword";
        long delay = 10L;

        declareInPlaceRecords.setDeclareInPlaceRecordDelay(delay);
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_ID)).thenReturn(fileId);
        when(mockedData.get(FIELD_USERNAME)).thenReturn(username);
        when(mockedData.get(FIELD_PASSWORD)).thenReturn(password);
        when(mockedEvent.getData()).thenReturn(mockedData);

        RecordData dbRecord = new RecordData(fileId, RecordContext.IN_PLACE_RECORD, "testFileName", "testFilePath", "testInPlacePath", ExecutionState.SCHEDULED);
        when(mockedRecordService.getRecord(fileId)).thenReturn(dbRecord);

        FilesAPI mockedFilesAPI = mock(FilesAPI.class);
        when(mockedRestAPIFactory.getFilesAPI(any(UserModel.class))).thenReturn(mockedFilesAPI);
        Record record = mock(Record.class);
        when(record.getName()).thenReturn("newRecordName");
        when(record.getParentId()).thenReturn("unfiledContainerID");
        when(mockedFilesAPI.declareAsRecord(fileId)).thenReturn(record);
        RMRestWrapper mockedRmRestWrapper = mock(RMRestWrapper.class);
        when(mockedRmRestWrapper.getStatusCode()).thenReturn(Integer.toString(HttpStatus.CREATED.value()));
        when(mockedRestAPIFactory.getRmRestWrapper()).thenReturn(mockedRmRestWrapper);

        UnfiledContainer mockedUnfiledContainer = mock(UnfiledContainer.class);
        when(mockedUnfiledContainer.getId()).thenReturn("unfiledContainerID");
        UnfiledContainerAPI mockedUnfiledContainerAPI = mock(UnfiledContainerAPI.class);
        when(mockedUnfiledContainerAPI.getUnfiledContainer(FilePlanComponentAlias.UNFILED_RECORDS_CONTAINER_ALIAS)).thenReturn(mockedUnfiledContainer);
        when(mockedRestAPIFactory.getUnfiledContainersAPI()).thenReturn(mockedUnfiledContainerAPI);

        FolderData mockedParentFolder = mock(FolderData.class);
        when(mockedParentFolder.getPath()).thenReturn(UNFILED_RECORD_CONTAINER_PATH);
        when(mockedFileFolderService.getFolder("unfiledContainerID")).thenReturn(mockedParentFolder);
        when(mockedApplicationContext.getBean("restAPIFactory", RestAPIFactory.class)).thenReturn(mockedRestAPIFactory);
        EventResult result = declareInPlaceRecords.processEvent(mockedEvent, new StopWatch());
        assertEquals(true, result.isSuccess());
        assertEquals("Declaring file as record: \nsuccess", result.getData());
        assertEquals(1, result.getNextEvents().size());
        assertEquals(declareInPlaceRecords.getEventNameInPlaceRecordsDeclared(), result.getNextEvents().get(0).getName());
    }

    @Test
    public void testDeclareAsRecordWithNotDeclaredFail() throws Exception
    {
        String fileId = "testFileId";
        String username = "testUserName";
        String password = "testPassword";
        long delay = 10L;

        declareInPlaceRecords.setDeclareInPlaceRecordDelay(delay);
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_ID)).thenReturn(fileId);
        when(mockedData.get(FIELD_USERNAME)).thenReturn(username);
        when(mockedData.get(FIELD_PASSWORD)).thenReturn(password);
        when(mockedEvent.getData()).thenReturn(mockedData);

        RecordData dbRecord = new RecordData(fileId, RecordContext.IN_PLACE_RECORD, "testFileName", "testFilePath", "testInPlacePath", ExecutionState.SCHEDULED);
        when(mockedRecordService.getRecord(fileId)).thenReturn(dbRecord);

        FilesAPI mockedFilesAPI = mock(FilesAPI.class);
        when(mockedRestAPIFactory.getFilesAPI(any(UserModel.class))).thenReturn(mockedFilesAPI);
        Record record = mock(Record.class);
        when(record.getName()).thenReturn("newRecordName");
        when(record.getParentId()).thenReturn("recordParentId");
        when(mockedFilesAPI.declareAsRecord(fileId)).thenReturn(record);
        RMRestWrapper mockedRmRestWrapper = mock(RMRestWrapper.class);
        when(mockedRmRestWrapper.getStatusCode()).thenReturn(Integer.toString(HttpStatus.CREATED.value()));
        when(mockedRestAPIFactory.getRmRestWrapper()).thenReturn(mockedRmRestWrapper);

        UnfiledContainer mockedUnfiledContainer = mock(UnfiledContainer.class);
        when(mockedUnfiledContainer.getId()).thenReturn("unfiledContainerID");
        UnfiledContainerAPI mockedUnfiledContainerAPI = mock(UnfiledContainerAPI.class);
        when(mockedUnfiledContainerAPI.getUnfiledContainer(FilePlanComponentAlias.UNFILED_RECORDS_CONTAINER_ALIAS)).thenReturn(mockedUnfiledContainer);
        when(mockedRestAPIFactory.getUnfiledContainersAPI()).thenReturn(mockedUnfiledContainerAPI);

        when(mockedApplicationContext.getBean("restAPIFactory", RestAPIFactory.class)).thenReturn(mockedRestAPIFactory);
        EventResult result = declareInPlaceRecords.processEvent(mockedEvent, new StopWatch());
        assertEquals(false, result.isSuccess());
        assertEquals("Declaring record with id=" + fileId + " didn't take place.", result.getData());
    }

    @Test
    public void testDeclareAsRecordWithFail() throws Exception
    {
        String fileId = "testFileId";
        String username = "testUserName";
        String password = "testPassword";
        String summary = "testSummary";
        String stack = "testStack";
        long delay = 10L;

        declareInPlaceRecords.setDeclareInPlaceRecordDelay(delay);
        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_ID)).thenReturn(fileId);
        when(mockedData.get(FIELD_USERNAME)).thenReturn(username);
        when(mockedData.get(FIELD_PASSWORD)).thenReturn(password);
        when(mockedEvent.getData()).thenReturn(mockedData);

        RecordData dbRecord = new RecordData(fileId, RecordContext.IN_PLACE_RECORD, "testFileName", "testFilePath", "testInPlacePath", ExecutionState.SCHEDULED);
        when(mockedRecordService.getRecord(fileId)).thenReturn(dbRecord);

        FilesAPI mockedFilesAPI = mock(FilesAPI.class);
        when(mockedRestAPIFactory.getFilesAPI(any(UserModel.class))).thenReturn(mockedFilesAPI);
        RMRestWrapper mockedRmRestWrapper = mock(RMRestWrapper.class);
        when(mockedRmRestWrapper.getStatusCode()).thenReturn(Integer.toString(HttpStatus.UNAUTHORIZED.value()));
        RestErrorModel mockedRestErrorModel = mock(RestErrorModel.class);
        when(mockedRestErrorModel.getBriefSummary()).thenReturn(summary);
        when(mockedRestErrorModel.getStackTrace()).thenReturn(stack);

        when(mockedRmRestWrapper.assertLastError()).thenReturn(mockedRestErrorModel);
        when(mockedRestAPIFactory.getRmRestWrapper()).thenReturn(mockedRmRestWrapper);

        when(mockedApplicationContext.getBean("restAPIFactory", RestAPIFactory.class)).thenReturn(mockedRestAPIFactory);
        EventResult result = declareInPlaceRecords.processEvent(mockedEvent, new StopWatch());
        assertEquals(true, result.isSuccess());
        assertEquals("Declaring file as record: \nFailed with code 401.\n " + summary + ". \n" + stack, result.getData());
        assertEquals(1, result.getNextEvents().size());
        assertEquals(declareInPlaceRecords.getEventNameInPlaceRecordsDeclared(), result.getNextEvents().get(0).getName());
    }

    @Test
    public void testDeclareAsRecordWithRestAPIException() throws Exception
    {
        String fileId = "testFileId";
        String username = "testUserName";
        String password = "testPassword";

        Event mockedEvent = mock(Event.class);
        DBObject mockedData = mock(DBObject.class);
        when(mockedData.get(FIELD_ID)).thenReturn(fileId);
        when(mockedData.get(FIELD_USERNAME)).thenReturn(username);
        when(mockedData.get(FIELD_PASSWORD)).thenReturn(password);
        when(mockedEvent.getData()).thenReturn(mockedData);

        RecordData dbRecord = new RecordData(fileId, RecordContext.IN_PLACE_RECORD, "testFileName", "testFilePath", "testInPlacePath", ExecutionState.SCHEDULED);
        when(mockedRecordService.getRecord(fileId)).thenReturn(dbRecord);

        FilesAPI mockedFilesAPI = mock(FilesAPI.class);
        when(mockedRestAPIFactory.getFilesAPI(any(UserModel.class))).thenReturn(mockedFilesAPI);
        Mockito.doThrow(new RuntimeException("someError")).when(mockedFilesAPI).declareAsRecord(any(String.class));
        when(mockedApplicationContext.getBean("restAPIFactory", RestAPIFactory.class)).thenReturn(mockedRestAPIFactory);
        EventResult result = declareInPlaceRecords.processEvent(mockedEvent, new StopWatch());

        assertEquals(false, result.isSuccess());
        DBObject data = (DBObject) result.getData();
        assertNotNull(data.get("error"));
        assertEquals("someError", data.get("error"));
        assertEquals(fileId, data.get(FIELD_ID));
        assertEquals(username, data.get(FIELD_USERNAME));
        assertEquals(password, data.get(FIELD_PASSWORD));
        assertNotNull(data.get("stack"));
        assertEquals(0, result.getNextEvents().size());
    }
}
