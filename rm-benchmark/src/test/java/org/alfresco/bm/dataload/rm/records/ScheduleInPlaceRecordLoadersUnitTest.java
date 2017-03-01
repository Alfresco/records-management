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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.alfresco.bm.dataload.RMEventConstants;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.session.SessionService;
import org.alfresco.bm.site.SiteData;
import org.alfresco.bm.site.SiteDataService;
import org.alfresco.rest.core.RestWrapper;
import org.alfresco.rest.model.RestNodeModel;
import org.alfresco.rest.model.RestNodeModelsCollection;
import org.alfresco.rest.model.RestPaginationModel;
import org.alfresco.rest.model.RestSiteContainerModel;
import org.alfresco.rest.model.RestSiteModel;
import org.alfresco.rest.model.builder.NodesBuilder;
import org.alfresco.rest.model.builder.NodesBuilder.NodeDetail;
import org.alfresco.rest.requests.Node;
import org.alfresco.rest.requests.Site;
import org.alfresco.rest.requests.coreAPI.RestCoreAPI;
import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.model.RepoTestModel;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.social.alfresco.api.entities.Site.Visibility;

import com.mongodb.DBObject;

/**
 * Unit tests for ScheduleInPlaceRecordLoaders
 *
 * @author Silviu Dinuta
 * @since 2.6
 */
@RunWith(MockitoJUnitRunner.class)
public class ScheduleInPlaceRecordLoadersUnitTest
{
    @Mock
    private SiteDataService mockedSiteDataService;

    @Mock
    private SessionService mockedSessionService;

    @Mock
    private RestWrapper mockedCoreAPI;

    @InjectMocks
    private ScheduleInPlaceRecordLoaders scheduleInPlaceRecordLoaders;

    @Test
    public void testWithEnabledFalse() throws Exception
    {
        scheduleInPlaceRecordLoaders.setEnabled(false);
        EventResult result = scheduleInPlaceRecordLoaders.processEvent(null, new StopWatch());
        assertEquals(true, result.isSuccess());
        assertEquals(ScheduleInPlaceRecordLoaders.DECLARING_NOT_WANTED_MSG, result.getData());
        assertEquals(1, result.getNextEvents().size());
        assertEquals(scheduleInPlaceRecordLoaders.getEventNameComplete(), result.getNextEvents().get(0).getName());
    }

    /**
     * Given the collaboration site exists and contains undeclared files with the following structure
     * - documentLibrary
     *   - file1
     *   - file2
     *   - folder
     *     - file3
     *     - file4
     * When running the scheduler with numberOfRecordsToDeclare=0 
     * Then all existing files should be scheduled for declaring
     *  
     * @throws Exception
     */
    @Test
    public void testDeclareAllExistingFiles() throws Exception
    {
        String numberOfRecordsToDeclare = "0";
        String siteId = "testSiteId";

        /*
         * Given
         */
        scheduleInPlaceRecordLoaders.setEnabled(true);
        scheduleInPlaceRecordLoaders.setMaxActiveLoaders(8);
        scheduleInPlaceRecordLoaders.setRecordsToDeclare(numberOfRecordsToDeclare);
        scheduleInPlaceRecordLoaders.setCollabSitePaths(null);

        scheduleInPlaceRecordLoaders.setCollabSiteId(siteId);
        String documentLibrary = mockExistingCollaborationSite(siteId);

        RestCoreAPI coreApi = mockCoreApiWithParams("where=(isPrimary=true)", "relativePath="+"");

        String file1Id = "file1_id";
        String file2Id = "file2_id";
        String folderId = "folder_id";
        List<RestNodeModel> level0Nodes = Arrays.asList(mockNode(file1Id, true), mockNode(file2Id, true), mockNode(folderId, false));
        mockListChildren(coreApi, documentLibrary, "", false, level0Nodes);
        
        String file3Id = "file3_id";
        String file4Id = "file4_id";
        List<RestNodeModel> level1Nodes = Arrays.asList(mockNode(file3Id, true), mockNode(file4Id, true));
        mockListChildren(coreApi, folderId, "", false, level1Nodes);

        /*
         * When
         */
        EventResult result = scheduleInPlaceRecordLoaders.processEvent(null, new StopWatch());

        /*
         * Then
         */
        assertEquals(true, result.isSuccess());
        assertEquals(5, result.getNextEvents().size());

        validateFiredEvents(true, Arrays.asList(file1Id, file2Id, file3Id, file4Id), result);
    }

    @Test
    public void testDeclareRecordsColabSiteExistsAndLoadedInDb() throws Exception
    {
        String numberOfRecordsToDeclare = "1";
        int maxActiveLoaders = 8;
        String siteId = "testSiteId";
        String documentLibraryId = UUID.randomUUID().toString();
        scheduleInPlaceRecordLoaders.setEnabled(true);
        scheduleInPlaceRecordLoaders.setRecordsToDeclare(numberOfRecordsToDeclare);
        scheduleInPlaceRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleInPlaceRecordLoaders.setCollabSiteId(siteId);
        scheduleInPlaceRecordLoaders.setCollabSitePaths(null);

        RestSiteModel mockedRestSiteModel = mock(RestSiteModel.class);
        RestSiteContainerModel mockedRestSiteContainerModel = mock(RestSiteContainerModel.class);
        when(mockedRestSiteContainerModel.getId()).thenReturn(documentLibraryId);

        Site mockedSite = mock(Site.class);
        when(mockedSite.getSite()).thenReturn(mockedRestSiteModel);
        when(mockedSite.getSiteContainer("documentLibrary")).thenReturn(mockedRestSiteContainerModel);


        RestCoreAPI mockedRestCoreAPI = mock(RestCoreAPI.class);
        when(mockedRestCoreAPI.usingSite(siteId.toLowerCase())).thenReturn(mockedSite);

        when(mockedCoreAPI.withCoreAPI()).thenReturn(mockedRestCoreAPI);
        when(mockedCoreAPI.getStatusCode()).thenReturn(Integer.toString(HttpStatus.SC_OK));

        SiteData mockedSiteData = mock(SiteData.class);
        when(mockedSiteDataService.getSite(siteId.toLowerCase())).thenReturn(mockedSiteData);

        RestPaginationModel mockedPagination = mock(RestPaginationModel.class);
        when(mockedPagination.isHasMoreItems()).thenReturn(false);
        RestNodeModelsCollection mockedCollection = mock(RestNodeModelsCollection.class);
        when(mockedCollection.getPagination()).thenReturn(mockedPagination);
        Node mockedNode = mock(Node.class);
        when(mockedNode.listChildren()).thenReturn(mockedCollection);
        RestCoreAPI mockedRestCoreAPIWithParams = mock(RestCoreAPI.class);
        when(mockedRestCoreAPIWithParams.usingNode(any(RepoTestModel.class))).thenReturn(mockedNode);
        RestWrapper mockedRestWrapperWithParams = mock(RestWrapper.class);
        when(mockedRestWrapperWithParams.withCoreAPI()).thenReturn(mockedRestCoreAPIWithParams);
        when(mockedCoreAPI.withParams(any(String.class), any(String.class))).thenReturn(mockedRestWrapperWithParams);

        //for creating files
        NodeDetail mockedTargetNodeDetail = mock(NodeDetail.class);
        NodesBuilder mockedNodeBuilder = mock(NodesBuilder.class);
        when(mockedNodeBuilder.folder("AutoGeneratedFiles")).thenReturn(mockedTargetNodeDetail);
        when(mockedNode.defineNodes()).thenReturn(mockedNodeBuilder);
        when(mockedRestCoreAPI.usingNode(any(RepoTestModel.class))).thenReturn(mockedNode);

        NodeDetail mockedFile = mock(NodeDetail.class);
        String fileID = UUID.randomUUID().toString();
        when(mockedFile.getId()).thenReturn(fileID);
        when(mockedTargetNodeDetail.file("recordToBe")).thenReturn(mockedFile);

        EventResult result = scheduleInPlaceRecordLoaders.processEvent(null, new StopWatch());
        verify(mockedSite, never()).createSite();
        verify(mockedSiteDataService, never()).addSite(any(SiteData.class));
        assertEquals(true, result.isSuccess());
        String template = "Preparing files to declare: \nCreated file {0}.Sheduled file to be declared as record: {1}. Raised further {2} events and rescheduled self.";
        assertEquals(MessageFormat.format(template, fileID, fileID, 1), result.getData());
        assertEquals(2, result.getNextEvents().size());
        assertEquals(scheduleInPlaceRecordLoaders.getEventNameDeclareInPlaceRecord(), result.getNextEvents().get(0).getName());
        assertEquals(scheduleInPlaceRecordLoaders.getEventNameRescheduleSelf(), result.getNextEvents().get(1).getName());
    }

    @Test
    public void testDeclareRecordsColabSiteDoesNotExistsAndLoadedInDb() throws Exception
    {
        String numberOfRecordsToDeclare = "1";
        int maxActiveLoaders = 8;
        String siteId = "testSiteId";
        String documentLibraryId = UUID.randomUUID().toString();
        scheduleInPlaceRecordLoaders.setEnabled(true);
        scheduleInPlaceRecordLoaders.setRecordsToDeclare(numberOfRecordsToDeclare);
        scheduleInPlaceRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleInPlaceRecordLoaders.setCollabSiteId(siteId);
        scheduleInPlaceRecordLoaders.setCollabSitePaths(null);

        RestSiteModel mockedRestSiteModel = mock(RestSiteModel.class);
        RestSiteContainerModel mockedRestSiteContainerModel = mock(RestSiteContainerModel.class);
        when(mockedRestSiteContainerModel.getId()).thenReturn(documentLibraryId);

        Site mockedSite = mock(Site.class);
        when(mockedSite.getSite()).thenReturn(null);
        when(mockedSite.createSite()).thenReturn(mockedRestSiteModel);
        when(mockedSite.getSiteContainer("documentLibrary")).thenReturn(mockedRestSiteContainerModel);

        RestCoreAPI mockedRestCoreAPI = mock(RestCoreAPI.class);
        when(mockedRestCoreAPI.usingSite(siteId.toLowerCase())).thenReturn(mockedSite);

        when(mockedCoreAPI.withCoreAPI()).thenReturn(mockedRestCoreAPI);
        when(mockedCoreAPI.getStatusCode()).thenReturn(Integer.toString(HttpStatus.SC_NOT_FOUND));

        SiteData mockedSiteData = mock(SiteData.class);
        when(mockedSiteDataService.getSite(siteId.toLowerCase())).thenReturn(mockedSiteData);

        RestPaginationModel mockedPagination = mock(RestPaginationModel.class);
        when(mockedPagination.isHasMoreItems()).thenReturn(false);
        RestNodeModelsCollection mockedCollection = mock(RestNodeModelsCollection.class);
        when(mockedCollection.getPagination()).thenReturn(mockedPagination);
        Node mockedNode = mock(Node.class);
        when(mockedNode.listChildren()).thenReturn(mockedCollection);
        RestCoreAPI mockedRestCoreAPIWithParams = mock(RestCoreAPI.class);
        when(mockedRestCoreAPIWithParams.usingNode(any(RepoTestModel.class))).thenReturn(mockedNode);
        RestWrapper mockedRestWrapperWithParams = mock(RestWrapper.class);
        when(mockedRestWrapperWithParams.withCoreAPI()).thenReturn(mockedRestCoreAPIWithParams);
        when(mockedCoreAPI.withParams(any(String.class), any(String.class))).thenReturn(mockedRestWrapperWithParams);

        //for creating files
        NodeDetail mockedTargetNodeDetail = mock(NodeDetail.class);
        NodesBuilder mockedNodeBuilder = mock(NodesBuilder.class);
        when(mockedNodeBuilder.folder("AutoGeneratedFiles")).thenReturn(mockedTargetNodeDetail);
        when(mockedNode.defineNodes()).thenReturn(mockedNodeBuilder);
        when(mockedRestCoreAPI.usingNode(any(RepoTestModel.class))).thenReturn(mockedNode);

        NodeDetail mockedFile = mock(NodeDetail.class);
        String fileID = UUID.randomUUID().toString();
        when(mockedFile.getId()).thenReturn(fileID);
        when(mockedTargetNodeDetail.file("recordToBe")).thenReturn(mockedFile);

        EventResult result = scheduleInPlaceRecordLoaders.processEvent(null, new StopWatch());
        verify(mockedSite, times(1)).createSite();
        verify(mockedSiteDataService, never()).addSite(any(SiteData.class));
        assertEquals(true, result.isSuccess());
        String template = "Preparing files to declare: \nCreated file {0}.Sheduled file to be declared as record: {1}. Raised further {2} events and rescheduled self.";
        assertEquals(MessageFormat.format(template, fileID, fileID, 1), result.getData());
        assertEquals(2, result.getNextEvents().size());
        assertEquals(scheduleInPlaceRecordLoaders.getEventNameDeclareInPlaceRecord(), result.getNextEvents().get(0).getName());
        assertEquals(scheduleInPlaceRecordLoaders.getEventNameRescheduleSelf(), result.getNextEvents().get(1).getName());
    }

    @Test
    public void testDeclareRecordsColabSiteExistsNotLoadedInDb() throws Exception
    {
        String numberOfRecordsToDeclare = "1";
        int maxActiveLoaders = 8;
        String siteId = "testSiteId";
        String documentLibraryId = UUID.randomUUID().toString();
        String siteTitle = "someTitle";
        String guid = UUID.randomUUID().toString();
        String description = "someDescription";
        String preset = "somePreset";
        Visibility visibility = Visibility.PUBLIC;
        scheduleInPlaceRecordLoaders.setEnabled(true);
        scheduleInPlaceRecordLoaders.setRecordsToDeclare(numberOfRecordsToDeclare);
        scheduleInPlaceRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleInPlaceRecordLoaders.setCollabSiteId(siteId);
        scheduleInPlaceRecordLoaders.setCollabSitePaths(null);

        RestSiteModel mockedRestSiteModel = mock(RestSiteModel.class);
        when(mockedRestSiteModel.getTitle()).thenReturn(siteTitle);
        when(mockedRestSiteModel.getGuid()).thenReturn(guid);
        when(mockedRestSiteModel.getDescription()).thenReturn(description);
        when(mockedRestSiteModel.getPreset()).thenReturn(preset);
        when(mockedRestSiteModel.getVisibility()).thenReturn(visibility);

        RestSiteContainerModel mockedRestSiteContainerModel = mock(RestSiteContainerModel.class);
        when(mockedRestSiteContainerModel.getId()).thenReturn(documentLibraryId);

        Site mockedSite = mock(Site.class);
        when(mockedSite.getSite()).thenReturn(mockedRestSiteModel);
        when(mockedSite.getSiteContainer("documentLibrary")).thenReturn(mockedRestSiteContainerModel);

        RestCoreAPI mockedRestCoreAPI = mock(RestCoreAPI.class);
        when(mockedRestCoreAPI.usingSite(siteId.toLowerCase())).thenReturn(mockedSite);

        when(mockedCoreAPI.withCoreAPI()).thenReturn(mockedRestCoreAPI);
        when(mockedCoreAPI.getStatusCode()).thenReturn(Integer.toString(HttpStatus.SC_OK));

        when(mockedSiteDataService.getSite(siteId.toLowerCase())).thenReturn(null);

        RestPaginationModel mockedPagination = mock(RestPaginationModel.class);
        when(mockedPagination.isHasMoreItems()).thenReturn(false);
        RestNodeModelsCollection mockedCollection = mock(RestNodeModelsCollection.class);
        when(mockedCollection.getPagination()).thenReturn(mockedPagination);
        Node mockedNode = mock(Node.class);
        when(mockedNode.listChildren()).thenReturn(mockedCollection);


        when(mockedRestCoreAPI.usingNode(any(RepoTestModel.class))).thenReturn(mockedNode);
        RestWrapper mockedRestWrapperWithParams = mock(RestWrapper.class);
        when(mockedRestWrapperWithParams.withCoreAPI()).thenReturn(mockedRestCoreAPI);
        when(mockedCoreAPI.withParams(any(String.class), any(String.class))).thenReturn(mockedRestWrapperWithParams);

        //for creating files
        NodeDetail mockedTargetNodeDetail = mock(NodeDetail.class);
        NodesBuilder mockedNodeBuilder = mock(NodesBuilder.class);
        when(mockedNodeBuilder.folder("AutoGeneratedFiles")).thenReturn(mockedTargetNodeDetail);
        when(mockedNode.defineNodes()).thenReturn(mockedNodeBuilder);
        when(mockedCoreAPI.withCoreAPI()).thenReturn(mockedRestCoreAPI);

        NodeDetail mockedFile = mock(NodeDetail.class);
        String fileID = UUID.randomUUID().toString();
        when(mockedFile.getId()).thenReturn(fileID);
        when(mockedTargetNodeDetail.file("recordToBe")).thenReturn(mockedFile);

        EventResult result = scheduleInPlaceRecordLoaders.processEvent(null, new StopWatch());
        verify(mockedSite, never()).createSite();
        verify(mockedSiteDataService, times(1)).addSite(any(SiteData.class));
        assertEquals(true, result.isSuccess());
        String template = "Preparing files to declare: \n Added site \"{0}\" as created.\nCreated file {1}.Sheduled file to be declared as record: {2}. Raised further {3} events and rescheduled self.";
        assertEquals(MessageFormat.format(template, siteId.toLowerCase(),fileID, fileID, 1), result.getData());
        assertEquals(2, result.getNextEvents().size());
        assertEquals(scheduleInPlaceRecordLoaders.getEventNameDeclareInPlaceRecord(), result.getNextEvents().get(0).getName());
        assertEquals(scheduleInPlaceRecordLoaders.getEventNameRescheduleSelf(), result.getNextEvents().get(1).getName());
    }

    @Test
    public void testDeclareRecordsColabSiteDoesNotExistsNotLoadedInDb() throws Exception
    {
        String numberOfRecordsToDeclare = "1";
        int maxActiveLoaders = 8;
        String siteId = "testSiteId";
        String documentLibraryId = UUID.randomUUID().toString();
        String siteTitle = "someTitle";
        String guid = UUID.randomUUID().toString();
        String description = "someDescription";
        String preset = "somePreset";
        Visibility visibility = Visibility.PUBLIC;
        scheduleInPlaceRecordLoaders.setEnabled(true);
        scheduleInPlaceRecordLoaders.setRecordsToDeclare(numberOfRecordsToDeclare);
        scheduleInPlaceRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleInPlaceRecordLoaders.setCollabSiteId(siteId);
        scheduleInPlaceRecordLoaders.setCollabSitePaths(null);

        RestSiteModel mockedRestSiteModel = mock(RestSiteModel.class);
        when(mockedRestSiteModel.getTitle()).thenReturn(siteTitle);
        when(mockedRestSiteModel.getGuid()).thenReturn(guid);
        when(mockedRestSiteModel.getDescription()).thenReturn(description);
        when(mockedRestSiteModel.getPreset()).thenReturn(preset);
        when(mockedRestSiteModel.getVisibility()).thenReturn(visibility);

        RestSiteContainerModel mockedRestSiteContainerModel = mock(RestSiteContainerModel.class);
        when(mockedRestSiteContainerModel.getId()).thenReturn(documentLibraryId);

        Site mockedSite = mock(Site.class);
        when(mockedSite.getSite()).thenReturn(null);
        when(mockedSite.createSite()).thenReturn(mockedRestSiteModel);
        when(mockedSite.getSiteContainer("documentLibrary")).thenReturn(mockedRestSiteContainerModel);

        RestCoreAPI mockedRestCoreAPI = mock(RestCoreAPI.class);
        when(mockedRestCoreAPI.usingSite(siteId.toLowerCase())).thenReturn(mockedSite);

        when(mockedCoreAPI.withCoreAPI()).thenReturn(mockedRestCoreAPI);
        when(mockedCoreAPI.getStatusCode()).thenReturn(Integer.toString(HttpStatus.SC_NOT_FOUND));

        when(mockedSiteDataService.getSite(siteId.toLowerCase())).thenReturn(null);

        RestPaginationModel mockedPagination = mock(RestPaginationModel.class);
        when(mockedPagination.isHasMoreItems()).thenReturn(false);
        RestNodeModelsCollection mockedCollection = mock(RestNodeModelsCollection.class);
        when(mockedCollection.getPagination()).thenReturn(mockedPagination);
        Node mockedNode = mock(Node.class);
        when(mockedNode.listChildren()).thenReturn(mockedCollection);
        RestCoreAPI mockedRestCoreAPIWithParams = mock(RestCoreAPI.class);
        when(mockedRestCoreAPIWithParams.usingNode(any(RepoTestModel.class))).thenReturn(mockedNode);
        RestWrapper mockedRestWrapperWithParams = mock(RestWrapper.class);
        when(mockedRestWrapperWithParams.withCoreAPI()).thenReturn(mockedRestCoreAPIWithParams);
        when(mockedCoreAPI.withParams(any(String.class), any(String.class))).thenReturn(mockedRestWrapperWithParams);

        //for creating files
        NodeDetail mockedTargetNodeDetail = mock(NodeDetail.class);
        NodesBuilder mockedNodeBuilder = mock(NodesBuilder.class);
        when(mockedNodeBuilder.folder("AutoGeneratedFiles")).thenReturn(mockedTargetNodeDetail);
        when(mockedNode.defineNodes()).thenReturn(mockedNodeBuilder);
        when(mockedRestCoreAPI.usingNode(any(RepoTestModel.class))).thenReturn(mockedNode);

        NodeDetail mockedFile = mock(NodeDetail.class);
        String fileID = UUID.randomUUID().toString();
        when(mockedFile.getId()).thenReturn(fileID);
        when(mockedTargetNodeDetail.file("recordToBe")).thenReturn(mockedFile);

        EventResult result = scheduleInPlaceRecordLoaders.processEvent(null, new StopWatch());
        verify(mockedSite, times(1)).createSite();
        verify(mockedSiteDataService, times(1)).addSite(any(SiteData.class));
        assertEquals(true, result.isSuccess());
        String template = "Preparing files to declare: \n Added site \"{0}\" as created.\nCreated file {1}.Sheduled file to be declared as record: {2}. Raised further {3} events and rescheduled self.";
        assertEquals(MessageFormat.format(template, siteId.toLowerCase(),fileID, fileID, 1), result.getData());
        assertEquals(2, result.getNextEvents().size());
        assertEquals(scheduleInPlaceRecordLoaders.getEventNameDeclareInPlaceRecord(), result.getNextEvents().get(0).getName());
        assertEquals(scheduleInPlaceRecordLoaders.getEventNameRescheduleSelf(), result.getNextEvents().get(1).getName());
    }

    @Test
    public void testDeclareRecordsFromDocumentLibrary() throws Exception
    {
        String numberOfRecordsToDeclare = "2";
        int maxActiveLoaders = 8;
        String siteId = "testSiteId";
        String documentLibraryId = UUID.randomUUID().toString();
        scheduleInPlaceRecordLoaders.setEnabled(true);
        scheduleInPlaceRecordLoaders.setRecordsToDeclare(numberOfRecordsToDeclare);
        scheduleInPlaceRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleInPlaceRecordLoaders.setCollabSiteId(siteId);
        scheduleInPlaceRecordLoaders.setCollabSitePaths(null);

        RestSiteModel mockedRestSiteModel = mock(RestSiteModel.class);
        RestSiteContainerModel mockedRestSiteContainerModel = mock(RestSiteContainerModel.class);
        when(mockedRestSiteContainerModel.getId()).thenReturn(documentLibraryId);

        Site mockedSite = mock(Site.class);
        when(mockedSite.getSite()).thenReturn(mockedRestSiteModel);
        when(mockedSite.getSiteContainer("documentLibrary")).thenReturn(mockedRestSiteContainerModel);


        RestCoreAPI mockedRestCoreAPI = mock(RestCoreAPI.class);
        when(mockedRestCoreAPI.usingSite(siteId.toLowerCase())).thenReturn(mockedSite);

        when(mockedCoreAPI.withCoreAPI()).thenReturn(mockedRestCoreAPI);
        when(mockedCoreAPI.getStatusCode()).thenReturn(Integer.toString(HttpStatus.SC_OK));

        SiteData mockedSiteData = mock(SiteData.class);
        when(mockedSiteDataService.getSite(siteId.toLowerCase())).thenReturn(mockedSiteData);

        RestPaginationModel mockedPagination = mock(RestPaginationModel.class);
        when(mockedPagination.isHasMoreItems()).thenReturn(false);
        RestNodeModelsCollection mockedCollection = mock(RestNodeModelsCollection.class);
        when(mockedCollection.getPagination()).thenReturn(mockedPagination);

        String childFileId1 = "childFileId1";
        RestNodeModel mockedOnModel1 = mock(RestNodeModel.class);
        when(mockedOnModel1.getIsFile()).thenReturn(true);
        when(mockedOnModel1.getId()).thenReturn(childFileId1);
        RestNodeModel mockedFile1 = mock(RestNodeModel.class);
        when(mockedFile1.onModel()).thenReturn(mockedOnModel1);

        String childFileId2 = "childFileId2";
        RestNodeModel mockedOnModel2 = mock(RestNodeModel.class);
        when(mockedOnModel2.getIsFile()).thenReturn(true);
        when(mockedOnModel2.getId()).thenReturn(childFileId2);
        RestNodeModel mockedFile2 = mock(RestNodeModel.class);
        when(mockedFile2.onModel()).thenReturn(mockedOnModel2);

        when(mockedCollection.getEntries()).thenReturn(Arrays.asList(mockedFile1, mockedFile2));
        Node mockedNode = mock(Node.class);
        when(mockedNode.listChildren()).thenReturn(mockedCollection);
        RestCoreAPI mockedRestCoreAPIWithParams = mock(RestCoreAPI.class);
        when(mockedRestCoreAPIWithParams.usingNode(any(RepoTestModel.class))).thenReturn(mockedNode);
        RestWrapper mockedRestWrapperWithParams = mock(RestWrapper.class);
        when(mockedRestWrapperWithParams.withCoreAPI()).thenReturn(mockedRestCoreAPIWithParams);
        when(mockedCoreAPI.withParams(any(String.class), any(String.class))).thenReturn(mockedRestWrapperWithParams);

        EventResult result = scheduleInPlaceRecordLoaders.processEvent(null, new StopWatch());
        verify(mockedSite, never()).createSite();
        verify(mockedSiteDataService, never()).addSite(any(SiteData.class));
        assertEquals(true, result.isSuccess());
        String template = "Preparing files to declare: \nSheduled file to be declared as record: {0}. Sheduled file to be declared as record: {1}. Raised further {2} events and rescheduled self.";
        assertEquals(MessageFormat.format(template, childFileId1, childFileId2, 2), result.getData());
        assertEquals(3, result.getNextEvents().size());
        assertEquals(scheduleInPlaceRecordLoaders.getEventNameDeclareInPlaceRecord(), result.getNextEvents().get(0).getName());
        assertEquals(scheduleInPlaceRecordLoaders.getEventNameDeclareInPlaceRecord(), result.getNextEvents().get(1).getName());
        assertEquals(scheduleInPlaceRecordLoaders.getEventNameRescheduleSelf(), result.getNextEvents().get(2).getName());
    }

    @Test
    public void testDeclareRecordsNonExistentNodeId() throws Exception
    {
        String numberOfRecordsToDeclare = "1";
        int maxActiveLoaders = 8;
        String siteId = "testSiteId";
        String documentLibraryId = UUID.randomUUID().toString();
        scheduleInPlaceRecordLoaders.setEnabled(true);
        scheduleInPlaceRecordLoaders.setRecordsToDeclare(numberOfRecordsToDeclare);
        scheduleInPlaceRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleInPlaceRecordLoaders.setCollabSiteId(siteId);
        scheduleInPlaceRecordLoaders.setCollabSitePaths(null);

        RestSiteModel mockedRestSiteModel = mock(RestSiteModel.class);
        RestSiteContainerModel mockedRestSiteContainerModel = mock(RestSiteContainerModel.class);
        when(mockedRestSiteContainerModel.getId()).thenReturn(documentLibraryId);

        Site mockedSite = mock(Site.class);
        when(mockedSite.getSite()).thenReturn(mockedRestSiteModel);
        when(mockedSite.getSiteContainer("documentLibrary")).thenReturn(mockedRestSiteContainerModel);


        RestCoreAPI mockedRestCoreAPI = mock(RestCoreAPI.class);
        when(mockedRestCoreAPI.usingSite(siteId.toLowerCase())).thenReturn(mockedSite);

        when(mockedCoreAPI.withCoreAPI()).thenReturn(mockedRestCoreAPI);
        when(mockedCoreAPI.getStatusCode()).thenReturn(Integer.toString(HttpStatus.SC_OK))
                                           .thenReturn(Integer.toString(HttpStatus.SC_NOT_FOUND));

        SiteData mockedSiteData = mock(SiteData.class);
        when(mockedSiteDataService.getSite(siteId.toLowerCase())).thenReturn(mockedSiteData);

        RestPaginationModel mockedPagination = mock(RestPaginationModel.class);
        when(mockedPagination.isHasMoreItems()).thenReturn(false);
        RestNodeModelsCollection mockedCollection = mock(RestNodeModelsCollection.class);
        when(mockedCollection.getPagination()).thenReturn(mockedPagination);

        Node mockedNode = mock(Node.class);
        when(mockedNode.listChildren()).thenReturn(mockedCollection);
        RestCoreAPI mockedRestCoreAPIWithParams = mock(RestCoreAPI.class);
        when(mockedRestCoreAPIWithParams.usingNode(any(RepoTestModel.class))).thenReturn(mockedNode);
        RestWrapper mockedRestWrapperWithParams = mock(RestWrapper.class);
        when(mockedRestWrapperWithParams.withCoreAPI()).thenReturn(mockedRestCoreAPIWithParams);
        when(mockedCoreAPI.withParams(any(String.class), any(String.class))).thenReturn(mockedRestWrapperWithParams);

        //for creating files
        NodeDetail mockedTargetNodeDetail = mock(NodeDetail.class);
        NodesBuilder mockedNodeBuilder = mock(NodesBuilder.class);
        when(mockedNodeBuilder.folder("AutoGeneratedFiles")).thenReturn(mockedTargetNodeDetail);
        when(mockedNode.defineNodes()).thenReturn(mockedNodeBuilder);
        when(mockedRestCoreAPI.usingNode(any(RepoTestModel.class))).thenReturn(mockedNode);

        NodeDetail mockedFile = mock(NodeDetail.class);
        String fileID = UUID.randomUUID().toString();
        when(mockedFile.getId()).thenReturn(fileID);
        when(mockedTargetNodeDetail.file("recordToBe")).thenReturn(mockedFile);

        EventResult result = scheduleInPlaceRecordLoaders.processEvent(null, new StopWatch());
        verify(mockedSite, never()).createSite();
        verify(mockedSiteDataService, never()).addSite(any(SiteData.class));
        assertEquals(true, result.isSuccess());
        String template = "Preparing files to declare: \nCreated file {0}.Sheduled file to be declared as record: {1}. Raised further {2} events and rescheduled self.";
        assertEquals(MessageFormat.format(template, fileID, fileID, 1), result.getData());
        assertEquals(2, result.getNextEvents().size());
        assertEquals(scheduleInPlaceRecordLoaders.getEventNameDeclareInPlaceRecord(), result.getNextEvents().get(0).getName());
        assertEquals(scheduleInPlaceRecordLoaders.getEventNameRescheduleSelf(), result.getNextEvents().get(1).getName());
    }

    @Test
    public void testDeclareRecordsFromDocumentLibraryAndOneCreated() throws Exception
    {
        String numberOfRecordsToDeclare = "3";
        int maxActiveLoaders = 8;
        String siteId = "testSiteId";
        String documentLibraryId = UUID.randomUUID().toString();
        scheduleInPlaceRecordLoaders.setEnabled(true);
        scheduleInPlaceRecordLoaders.setRecordsToDeclare(numberOfRecordsToDeclare);
        scheduleInPlaceRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleInPlaceRecordLoaders.setCollabSiteId(siteId);
        scheduleInPlaceRecordLoaders.setCollabSitePaths(null);

        RestSiteModel mockedRestSiteModel = mock(RestSiteModel.class);
        RestSiteContainerModel mockedRestSiteContainerModel = mock(RestSiteContainerModel.class);
        when(mockedRestSiteContainerModel.getId()).thenReturn(documentLibraryId);

        Site mockedSite = mock(Site.class);
        when(mockedSite.getSite()).thenReturn(mockedRestSiteModel);
        when(mockedSite.getSiteContainer("documentLibrary")).thenReturn(mockedRestSiteContainerModel);


        RestCoreAPI mockedRestCoreAPI = mock(RestCoreAPI.class);
        when(mockedRestCoreAPI.usingSite(siteId.toLowerCase())).thenReturn(mockedSite);

        when(mockedCoreAPI.withCoreAPI()).thenReturn(mockedRestCoreAPI);
        when(mockedCoreAPI.getStatusCode()).thenReturn(Integer.toString(HttpStatus.SC_OK));

        SiteData mockedSiteData = mock(SiteData.class);
        when(mockedSiteDataService.getSite(siteId.toLowerCase())).thenReturn(mockedSiteData);

        RestPaginationModel mockedPagination = mock(RestPaginationModel.class);
        when(mockedPagination.isHasMoreItems()).thenReturn(false);
        RestNodeModelsCollection mockedCollection = mock(RestNodeModelsCollection.class);
        when(mockedCollection.getPagination()).thenReturn(mockedPagination);

        String childFileId1 = "childFileId1";
        RestNodeModel mockedOnModel1 = mock(RestNodeModel.class);
        when(mockedOnModel1.getIsFile()).thenReturn(true);
        when(mockedOnModel1.getId()).thenReturn(childFileId1);
        RestNodeModel mockedFile1 = mock(RestNodeModel.class);
        when(mockedFile1.onModel()).thenReturn(mockedOnModel1);

        String childFileId2 = "childFileId2";
        RestNodeModel mockedOnModel2 = mock(RestNodeModel.class);
        when(mockedOnModel2.getIsFile()).thenReturn(true);
        when(mockedOnModel2.getId()).thenReturn(childFileId2);
        RestNodeModel mockedFile2 = mock(RestNodeModel.class);
        when(mockedFile2.onModel()).thenReturn(mockedOnModel2);

        when(mockedCollection.getEntries()).thenReturn(Arrays.asList(mockedFile1, mockedFile2));
        Node mockedNode = mock(Node.class);
        when(mockedNode.listChildren()).thenReturn(mockedCollection);
        RestCoreAPI mockedRestCoreAPIWithParams = mock(RestCoreAPI.class);
        when(mockedRestCoreAPIWithParams.usingNode(any(RepoTestModel.class))).thenReturn(mockedNode);
        RestWrapper mockedRestWrapperWithParams = mock(RestWrapper.class);
        when(mockedRestWrapperWithParams.withCoreAPI()).thenReturn(mockedRestCoreAPIWithParams);
        when(mockedCoreAPI.withParams(any(String.class), any(String.class))).thenReturn(mockedRestWrapperWithParams);

        //for creating files
        NodeDetail mockedTargetNodeDetail = mock(NodeDetail.class);
        NodesBuilder mockedNodeBuilder = mock(NodesBuilder.class);
        when(mockedNodeBuilder.folder("AutoGeneratedFiles")).thenReturn(mockedTargetNodeDetail);
        when(mockedNode.defineNodes()).thenReturn(mockedNodeBuilder);
        when(mockedRestCoreAPI.usingNode(any(RepoTestModel.class))).thenReturn(mockedNode);

        NodeDetail mockedFile = mock(NodeDetail.class);
        String fileID = UUID.randomUUID().toString();
        when(mockedFile.getId()).thenReturn(fileID);
        when(mockedTargetNodeDetail.file("recordToBe")).thenReturn(mockedFile);

        EventResult result = scheduleInPlaceRecordLoaders.processEvent(null, new StopWatch());
        verify(mockedSite, never()).createSite();
        verify(mockedSiteDataService, never()).addSite(any(SiteData.class));
        assertEquals(true, result.isSuccess());
        String template = "Preparing files to declare: \nCreated file {0}.Sheduled file to be declared as record: {1}. Sheduled file to be declared as record: {2}. Sheduled file to be declared as record: {3}. Raised further {4} events and rescheduled self.";
        assertEquals(MessageFormat.format(template, fileID, childFileId1, childFileId2, fileID, 3), result.getData());
        assertEquals(4, result.getNextEvents().size());
        assertEquals(scheduleInPlaceRecordLoaders.getEventNameDeclareInPlaceRecord(), result.getNextEvents().get(0).getName());
        assertEquals(scheduleInPlaceRecordLoaders.getEventNameDeclareInPlaceRecord(), result.getNextEvents().get(1).getName());
        assertEquals(scheduleInPlaceRecordLoaders.getEventNameDeclareInPlaceRecord(), result.getNextEvents().get(2).getName());
        assertEquals(scheduleInPlaceRecordLoaders.getEventNameRescheduleSelf(), result.getNextEvents().get(3).getName());
    }

    @Test
    public void testDeclareRecordsFromDocumentLibraryOneFromOneChildFolderAndOneCreated() throws Exception
    {
        String numberOfRecordsToDeclare = "3";
        int maxActiveLoaders = 8;
        String siteId = "testSiteId";
        String documentLibraryId = UUID.randomUUID().toString();
        scheduleInPlaceRecordLoaders.setEnabled(true);
        scheduleInPlaceRecordLoaders.setRecordsToDeclare(numberOfRecordsToDeclare);
        scheduleInPlaceRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleInPlaceRecordLoaders.setCollabSiteId(siteId);
        scheduleInPlaceRecordLoaders.setCollabSitePaths(null);

        RestSiteModel mockedRestSiteModel = mock(RestSiteModel.class);
        RestSiteContainerModel mockedRestSiteContainerModel = mock(RestSiteContainerModel.class);
        when(mockedRestSiteContainerModel.getId()).thenReturn(documentLibraryId);

        Site mockedSite = mock(Site.class);
        when(mockedSite.getSite()).thenReturn(mockedRestSiteModel);
        when(mockedSite.getSiteContainer("documentLibrary")).thenReturn(mockedRestSiteContainerModel);


        RestCoreAPI mockedRestCoreAPI = mock(RestCoreAPI.class);
        when(mockedRestCoreAPI.usingSite(siteId.toLowerCase())).thenReturn(mockedSite);

        when(mockedCoreAPI.withCoreAPI()).thenReturn(mockedRestCoreAPI);
        when(mockedCoreAPI.getStatusCode()).thenReturn(Integer.toString(HttpStatus.SC_OK));

        SiteData mockedSiteData = mock(SiteData.class);
        when(mockedSiteDataService.getSite(siteId.toLowerCase())).thenReturn(mockedSiteData);

        RestPaginationModel mockedPagination = mock(RestPaginationModel.class);
        when(mockedPagination.isHasMoreItems()).thenReturn(false);
        RestNodeModelsCollection mockedCollection = mock(RestNodeModelsCollection.class);
        when(mockedCollection.getPagination()).thenReturn(mockedPagination);

        String childFileId1 = "childFileId1";
        RestNodeModel mockedOnModel1 = mock(RestNodeModel.class);
        when(mockedOnModel1.getIsFile()).thenReturn(true);
        when(mockedOnModel1.getId()).thenReturn(childFileId1);
        RestNodeModel mockedFile1 = mock(RestNodeModel.class);
        when(mockedFile1.onModel()).thenReturn(mockedOnModel1);

        String childFileId2 = "childFileId2";
        RestNodeModel mockedOnModel2 = mock(RestNodeModel.class);
        when(mockedOnModel2.getIsFile()).thenReturn(true);
        when(mockedOnModel2.getId()).thenReturn(childFileId2);
        RestNodeModel mockedFile2 = mock(RestNodeModel.class);
        when(mockedFile2.onModel()).thenReturn(mockedOnModel2);

        String childFolderId1 = "childFolderId1";
        RestNodeModel mockedOnModel3 = mock(RestNodeModel.class);
        when(mockedOnModel3.getIsFile()).thenReturn(false);
        when(mockedOnModel3.getId()).thenReturn(childFolderId1);
        RestNodeModel mockedFolder1 = mock(RestNodeModel.class);
        when(mockedFolder1.onModel()).thenReturn(mockedOnModel3);

        when(mockedCollection.getEntries()).thenReturn(Arrays.asList(mockedFile1, mockedFolder1))
                                           .thenReturn(Arrays.asList(mockedFile2));
        Node mockedNode = mock(Node.class);
        when(mockedNode.listChildren()).thenReturn(mockedCollection);
        RestCoreAPI mockedRestCoreAPIWithParams = mock(RestCoreAPI.class);
        when(mockedRestCoreAPIWithParams.usingNode(any(RepoTestModel.class))).thenReturn(mockedNode);
        RestWrapper mockedRestWrapperWithParams = mock(RestWrapper.class);
        when(mockedRestWrapperWithParams.withCoreAPI()).thenReturn(mockedRestCoreAPIWithParams);
        when(mockedCoreAPI.withParams(any(String.class), any(String.class))).thenReturn(mockedRestWrapperWithParams);

        //for creating files
        NodeDetail mockedTargetNodeDetail = mock(NodeDetail.class);
        NodesBuilder mockedNodeBuilder = mock(NodesBuilder.class);
        when(mockedNodeBuilder.folder("AutoGeneratedFiles")).thenReturn(mockedTargetNodeDetail);
        when(mockedNode.defineNodes()).thenReturn(mockedNodeBuilder);
        when(mockedRestCoreAPI.usingNode(any(RepoTestModel.class))).thenReturn(mockedNode);

        NodeDetail mockedFile = mock(NodeDetail.class);
        String fileID = UUID.randomUUID().toString();
        when(mockedFile.getId()).thenReturn(fileID);
        when(mockedTargetNodeDetail.file("recordToBe")).thenReturn(mockedFile);

        EventResult result = scheduleInPlaceRecordLoaders.processEvent(null, new StopWatch());
        verify(mockedSite, never()).createSite();
        verify(mockedSiteDataService, never()).addSite(any(SiteData.class));
        assertEquals(true, result.isSuccess());
        String template = "Preparing files to declare: \nCreated file {0}.Sheduled file to be declared as record: {1}. Sheduled file to be declared as record: {2}. Sheduled file to be declared as record: {3}. Raised further {4} events and rescheduled self.";
        assertEquals(MessageFormat.format(template, fileID, childFileId1, childFileId2, fileID, 3), result.getData());
        assertEquals(4, result.getNextEvents().size());
        assertEquals(scheduleInPlaceRecordLoaders.getEventNameDeclareInPlaceRecord(), result.getNextEvents().get(0).getName());
        assertEquals(scheduleInPlaceRecordLoaders.getEventNameDeclareInPlaceRecord(), result.getNextEvents().get(1).getName());
        assertEquals(scheduleInPlaceRecordLoaders.getEventNameDeclareInPlaceRecord(), result.getNextEvents().get(2).getName());
        assertEquals(scheduleInPlaceRecordLoaders.getEventNameRescheduleSelf(), result.getNextEvents().get(3).getName());
    }

    @Test
    public void testDeclareRecordsFromTwiDiferentPaths() throws Exception
    {
        String numberOfRecordsToDeclare = "2";
        int maxActiveLoaders = 8;
        String siteId = "testSiteId";
        String documentLibraryId = UUID.randomUUID().toString();
        scheduleInPlaceRecordLoaders.setEnabled(true);
        scheduleInPlaceRecordLoaders.setRecordsToDeclare(numberOfRecordsToDeclare);
        scheduleInPlaceRecordLoaders.setMaxActiveLoaders(maxActiveLoaders);
        scheduleInPlaceRecordLoaders.setCollabSiteId(siteId);
        scheduleInPlaceRecordLoaders.setCollabSitePaths("path1,path2");

        RestSiteModel mockedRestSiteModel = mock(RestSiteModel.class);
        RestSiteContainerModel mockedRestSiteContainerModel = mock(RestSiteContainerModel.class);
        when(mockedRestSiteContainerModel.getId()).thenReturn(documentLibraryId);

        Site mockedSite = mock(Site.class);
        when(mockedSite.getSite()).thenReturn(mockedRestSiteModel);
        when(mockedSite.getSiteContainer("documentLibrary")).thenReturn(mockedRestSiteContainerModel);


        RestCoreAPI mockedRestCoreAPI = mock(RestCoreAPI.class);
        when(mockedRestCoreAPI.usingSite(siteId.toLowerCase())).thenReturn(mockedSite);

        when(mockedCoreAPI.withCoreAPI()).thenReturn(mockedRestCoreAPI);
        when(mockedCoreAPI.getStatusCode()).thenReturn(Integer.toString(HttpStatus.SC_OK));

        SiteData mockedSiteData = mock(SiteData.class);
        when(mockedSiteDataService.getSite(siteId.toLowerCase())).thenReturn(mockedSiteData);

        RestPaginationModel mockedPagination = mock(RestPaginationModel.class);
        when(mockedPagination.isHasMoreItems()).thenReturn(false);
        RestNodeModelsCollection mockedCollection = mock(RestNodeModelsCollection.class);
        when(mockedCollection.getPagination()).thenReturn(mockedPagination);

        String childFileId1 = "childFileId1";
        RestNodeModel mockedOnModel1 = mock(RestNodeModel.class);
        when(mockedOnModel1.getIsFile()).thenReturn(true);
        when(mockedOnModel1.getId()).thenReturn(childFileId1);
        RestNodeModel mockedFile1 = mock(RestNodeModel.class);
        when(mockedFile1.onModel()).thenReturn(mockedOnModel1);

        String childFileId2 = "childFileId2";
        RestNodeModel mockedOnModel2 = mock(RestNodeModel.class);
        when(mockedOnModel2.getIsFile()).thenReturn(true);
        when(mockedOnModel2.getId()).thenReturn(childFileId2);
        RestNodeModel mockedFile2 = mock(RestNodeModel.class);
        when(mockedFile2.onModel()).thenReturn(mockedOnModel2);

        when(mockedCollection.getEntries()).thenReturn(Arrays.asList(mockedFile1))
                                           .thenReturn(Arrays.asList(mockedFile2));
        Node mockedNode = mock(Node.class);
        when(mockedNode.listChildren()).thenReturn(mockedCollection);
        RestCoreAPI mockedRestCoreAPIWithParams = mock(RestCoreAPI.class);
        when(mockedRestCoreAPIWithParams.usingNode(any(RepoTestModel.class))).thenReturn(mockedNode);
        RestWrapper mockedRestWrapperWithParams = mock(RestWrapper.class);
        when(mockedRestWrapperWithParams.withCoreAPI()).thenReturn(mockedRestCoreAPIWithParams);
        when(mockedCoreAPI.withParams(any(String.class), any(String.class))).thenReturn(mockedRestWrapperWithParams);

        EventResult result = scheduleInPlaceRecordLoaders.processEvent(null, new StopWatch());
        verify(mockedSite, never()).createSite();
        verify(mockedSiteDataService, never()).addSite(any(SiteData.class));
        assertEquals(true, result.isSuccess());
        String template = "Preparing files to declare: \nSheduled file to be declared as record: {0}. Sheduled file to be declared as record: {1}. Raised further {2} events and rescheduled self.";
        assertEquals(MessageFormat.format(template, childFileId1, childFileId2, 2), result.getData());
        assertEquals(3, result.getNextEvents().size());
        assertEquals(scheduleInPlaceRecordLoaders.getEventNameDeclareInPlaceRecord(), result.getNextEvents().get(0).getName());
        assertEquals(scheduleInPlaceRecordLoaders.getEventNameDeclareInPlaceRecord(), result.getNextEvents().get(1).getName());
        assertEquals(scheduleInPlaceRecordLoaders.getEventNameRescheduleSelf(), result.getNextEvents().get(2).getName());
    }

    @Test(expected = Exception.class)
    public void testNullActiveLoaders() throws Exception
    {
        scheduleInPlaceRecordLoaders.setCollabSiteId("someSiteID");
        scheduleInPlaceRecordLoaders.setCollabSitePaths("path1");
        scheduleInPlaceRecordLoaders.setUsername("someUser");
        scheduleInPlaceRecordLoaders.setPassword("somePassword");
        scheduleInPlaceRecordLoaders.setEventNameRescheduleSelf("someEvent1");
        scheduleInPlaceRecordLoaders.setEventNameComplete("someEvent2");
        scheduleInPlaceRecordLoaders.setEventNameDeclareInPlaceRecord("someEvent3");
        scheduleInPlaceRecordLoaders.setRecordsToDeclare("");
        scheduleInPlaceRecordLoaders.afterPropertiesSet();
    }

    @Test(expected = Exception.class)
    public void testNullSiteId() throws Exception
    {
        scheduleInPlaceRecordLoaders.setMaxActiveLoaders(8);
        scheduleInPlaceRecordLoaders.setCollabSiteId(null);
        scheduleInPlaceRecordLoaders.afterPropertiesSet();
    }

    @Test(expected = Exception.class)
    public void testNullPaths() throws Exception
    {
        scheduleInPlaceRecordLoaders.setMaxActiveLoaders(8);
        scheduleInPlaceRecordLoaders.setCollabSiteId("someSiteID");
        scheduleInPlaceRecordLoaders.setCollabSitePaths(null);
        scheduleInPlaceRecordLoaders.afterPropertiesSet();
    }

    @Test(expected = Exception.class)
    public void testNullUserName() throws Exception
    {
        scheduleInPlaceRecordLoaders.setMaxActiveLoaders(8);
        scheduleInPlaceRecordLoaders.setCollabSiteId("someSiteID");
        scheduleInPlaceRecordLoaders.setCollabSitePaths("path1");
        scheduleInPlaceRecordLoaders.setUsername(null);
        scheduleInPlaceRecordLoaders.afterPropertiesSet();
    }

    @Test(expected = Exception.class)
    public void testNullPassword() throws Exception
    {
        scheduleInPlaceRecordLoaders.setMaxActiveLoaders(8);
        scheduleInPlaceRecordLoaders.setCollabSiteId("someSiteID");
        scheduleInPlaceRecordLoaders.setCollabSitePaths("path1");
        scheduleInPlaceRecordLoaders.setUsername("someUser");
        scheduleInPlaceRecordLoaders.setPassword(null);
        scheduleInPlaceRecordLoaders.afterPropertiesSet();
    }

    @Test(expected = Exception.class)
    public void testNullScheduleSelfEvent() throws Exception
    {
        scheduleInPlaceRecordLoaders.setMaxActiveLoaders(8);
        scheduleInPlaceRecordLoaders.setCollabSiteId("someSiteID");
        scheduleInPlaceRecordLoaders.setCollabSitePaths("path1");
        scheduleInPlaceRecordLoaders.setUsername("someUser");
        scheduleInPlaceRecordLoaders.setPassword("somePassword");
        scheduleInPlaceRecordLoaders.setEventNameRescheduleSelf(null);
        scheduleInPlaceRecordLoaders.afterPropertiesSet();
    }

    @Test(expected = Exception.class)
    public void testNullCompleteEvent() throws Exception
    {
        scheduleInPlaceRecordLoaders.setMaxActiveLoaders(8);
        scheduleInPlaceRecordLoaders.setCollabSiteId("someSiteID");
        scheduleInPlaceRecordLoaders.setCollabSitePaths("path1");
        scheduleInPlaceRecordLoaders.setUsername("someUser");
        scheduleInPlaceRecordLoaders.setPassword("somePassword");
        scheduleInPlaceRecordLoaders.setEventNameRescheduleSelf("someEvent1");
        scheduleInPlaceRecordLoaders.setEventNameComplete(null);
        scheduleInPlaceRecordLoaders.afterPropertiesSet();
    }

    @Test(expected = Exception.class)
    public void testNullScheduleDeclareInPlaceRecordEvent() throws Exception
    {
        scheduleInPlaceRecordLoaders.setMaxActiveLoaders(8);
        scheduleInPlaceRecordLoaders.setCollabSiteId("someSiteID");
        scheduleInPlaceRecordLoaders.setCollabSitePaths("path1");
        scheduleInPlaceRecordLoaders.setUsername("someUser");
        scheduleInPlaceRecordLoaders.setPassword("somePassword");
        scheduleInPlaceRecordLoaders.setEventNameRescheduleSelf("someEvent1");
        scheduleInPlaceRecordLoaders.setEventNameComplete("someEvent2");
        scheduleInPlaceRecordLoaders.setEventNameDeclareInPlaceRecord(null);
        scheduleInPlaceRecordLoaders.afterPropertiesSet();
    }

    @Test(expected = Exception.class)
    public void testNullRecordsToDeclare() throws Exception
    {
        scheduleInPlaceRecordLoaders.setMaxActiveLoaders(8);
        scheduleInPlaceRecordLoaders.setCollabSiteId("someSiteID");
        scheduleInPlaceRecordLoaders.setCollabSitePaths("path1");
        scheduleInPlaceRecordLoaders.setUsername("someUser");
        scheduleInPlaceRecordLoaders.setPassword("somePassword");
        scheduleInPlaceRecordLoaders.setEventNameRescheduleSelf("someEvent1");
        scheduleInPlaceRecordLoaders.setEventNameComplete("someEvent2");
        scheduleInPlaceRecordLoaders.setEventNameDeclareInPlaceRecord("someEvent3");
        scheduleInPlaceRecordLoaders.afterPropertiesSet();
    }

    @Test
    public void testAfterPropertiesSuccess() throws Exception
    {
        scheduleInPlaceRecordLoaders.setMaxActiveLoaders(8);
        scheduleInPlaceRecordLoaders.setCollabSiteId("someSiteID");
        scheduleInPlaceRecordLoaders.setCollabSitePaths("path1");
        scheduleInPlaceRecordLoaders.setUsername("someUser");
        scheduleInPlaceRecordLoaders.setPassword("somePassword");
        scheduleInPlaceRecordLoaders.setEventNameRescheduleSelf("someEvent1");
        scheduleInPlaceRecordLoaders.setEventNameComplete("someEvent2");
        scheduleInPlaceRecordLoaders.setEventNameDeclareInPlaceRecord("someEvent3");
        scheduleInPlaceRecordLoaders.setRecordsToDeclare("");
        scheduleInPlaceRecordLoaders.afterPropertiesSet();
    }

    /**
     * Utility method that mocks the retrieval of an existing collaboration site
     *
     * @return the id of the generated document library
     * @throws Exception
     */
    private String mockExistingCollaborationSite(String siteId) throws Exception
    {
        String documentLibraryId = UUID.randomUUID().toString();

        /*
         * Mock the rest core API
         */
        RestCoreAPI mockedRestCoreAPI = mock(RestCoreAPI.class);
        when(mockedCoreAPI.withCoreAPI()).thenReturn(mockedRestCoreAPI);
        when(mockedCoreAPI.getStatusCode()).thenReturn(Integer.toString(HttpStatus.SC_OK));

        /*
         * Mock the site
         */
        Site mockedSiteEndpoint = mock(Site.class);
        when(mockedRestCoreAPI.usingSite(siteId.toLowerCase())).thenReturn(mockedSiteEndpoint);
        RestSiteModel colabSite = mock(RestSiteModel.class);
        when(mockedSiteEndpoint.getSite()).thenReturn(colabSite);
        when(colabSite.getVisibility()).thenReturn(Visibility.PUBLIC);

        /*
         * Mock document library
         */
        RestSiteContainerModel mockedRestSiteContainerModel = mock(RestSiteContainerModel.class);
        when(mockedRestSiteContainerModel.getId()).thenReturn(documentLibraryId);
        when(mockedSiteEndpoint.getSiteContainer("documentLibrary")).thenReturn(mockedRestSiteContainerModel);

        return documentLibraryId;
    }

    /**
     * Utility method that mocks the core api with parameters
     * 
     * @param params parameters to use in the api
     * @return the mocked core api
     */
    private RestCoreAPI mockCoreApiWithParams(String... params)
    {
        RestWrapper mockedRestWrapperWithParams = mock(RestWrapper.class);
        when(mockedCoreAPI.withParams(params)).thenReturn(mockedRestWrapperWithParams);

        RestCoreAPI mockedRestCoreAPI = mock(RestCoreAPI.class);
        when(mockedRestWrapperWithParams.withCoreAPI()).thenReturn(mockedRestCoreAPI);
        when(mockedCoreAPI.getStatusCode()).thenReturn(Integer.toString(HttpStatus.SC_OK));

        return mockedRestCoreAPI;
    }
 
    /**
     * Utility method that mocks list children api call
     * 
     * @param mockedRestCoreAPI the core api to use
     * @param currentNodeId the node to list from, request parameter 
     * @param relativePath the relative path to use for the list, request parameter
     * @param hasMoreItems whether the mocked paginated list has more items, response parameter
     * @param children the list of children to return, response parameter
     * @throws Exception
     */
    private void mockListChildren(RestCoreAPI mockedRestCoreAPI, String currentNodeId, String relativePath, boolean hasMoreItems, List<RestNodeModel> children) throws Exception
    {
        /*
         * Mock the listChildren call
         */
        ArgumentMatcher<ContentModel> modelForCurrentNode = new ArgumentMatcher<ContentModel>() {
            @Override
            public boolean matches(Object argument) {
                return ((ContentModel) argument).getNodeRef().equals(currentNodeId);
            }
        };
        Node nodesEndpoint = mock(Node.class);
        doReturn(nodesEndpoint).when(mockedRestCoreAPI).usingNode(argThat(modelForCurrentNode));

        RestNodeModelsCollection childrenCollection = mock(RestNodeModelsCollection.class);
        when(nodesEndpoint.listChildren()).thenReturn(childrenCollection);

        /*
         * Mock the results
         */
        RestPaginationModel mockedPagination = mock(RestPaginationModel.class);
        when(childrenCollection.getPagination()).thenReturn(mockedPagination);
        when(mockedPagination.isHasMoreItems()).thenReturn(hasMoreItems);
        when(childrenCollection.getEntries()).thenReturn(children);
    }

    /**
     * Utility method that mock a node model
     * @param id the id of the node to mock
     * @param isFile isFile value of the node to mock
     * @return the mocked model for the file
     * @throws Exception
     */
    private RestNodeModel mockNode(String id, boolean isFile) throws Exception
    {
        RestNodeModel node = mock(RestNodeModel.class);
        RestNodeModel onModelNode = mock(RestNodeModel.class);
        when(node.onModel()).thenReturn(onModelNode);

        when(onModelNode.getId()).thenReturn(id);
        when(onModelNode.getIsFile()).thenReturn(isFile);
        return node;
    }

    /**
     * Utility method that validates the list of events returned bu the scheduler
     * 
     * @param rescheduleSelfExpected whether we expect the event to reschedule itself
     * @param expectedScheduledFileIds the list of files we expect to be scheduled for declare
     * @param result the event result to validate
     */
    private void validateFiredEvents(boolean rescheduleSelfExpected, List<String> expectedScheduledFileIds, EventResult result)
    {
        boolean rescheduleSelfEventFired = false;
        boolean doneEventFired = false;
        List<String> scheduledFiles = new ArrayList<>();
        for(Event event : result.getNextEvents())
        {
            if(event.getName().equals(scheduleInPlaceRecordLoaders.getEventNameRescheduleSelf()))
            {
                if(!rescheduleSelfExpected)
                {
                    fail("Reschedule self not expected but fired!");
                }
                if(rescheduleSelfEventFired)
                {
                    fail("Reschedule self fired multiple times!");
                }
                rescheduleSelfEventFired = true;
            }
            else if(event.getName().equals(scheduleInPlaceRecordLoaders.getEventNameComplete()))
            {
                if(rescheduleSelfExpected)
                {
                    fail("Reschedule self was expected but Done event was fired instead!");
                }
                if(doneEventFired)
                {
                    fail("Done event fired multiple times!");
                }
                doneEventFired = true;
            }
            else if(event.getName().equals(scheduleInPlaceRecordLoaders.getEventNameDeclareInPlaceRecord()))
            {
                DBObject dataObj = (DBObject) event.getData();
                String id = (String) dataObj.get(RMEventConstants.FIELD_ID);
                scheduledFiles.add(id);
            }
            else
            {
                fail("Unexpected event name " + event.getName());
            }
        }

        assertFalse("Both reschedule self and done events were fired in the same time", rescheduleSelfEventFired && doneEventFired);
        if(rescheduleSelfExpected)
        {
            assertTrue(rescheduleSelfEventFired);
        }
        else
        {
            assertTrue(doneEventFired);
        }

        assertArrayEquals(expectedScheduledFileIds.toArray(), scheduledFiles.toArray());
    }
}
