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
import org.alfresco.bm.dataload.rm.services.RecordService;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.session.SessionService;
import org.alfresco.bm.site.SiteData;
import org.alfresco.bm.site.SiteDataService;
import org.alfresco.dataprep.SiteService.Visibility;
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
    private final String DEFAULT_COLLABORATION_SITE_ID = "testSiteId";
    private final int DEFAULT_MAX_ACTIVE_LOADERS = 8;

    @Mock
    private SiteDataService mockedSiteDataService;

    @Mock
    private SessionService mockedSessionService;

    @Mock
    private RecordService mockedRecordService;

    @Mock
    private RestWrapper mockedRestWrapper;

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
        String file1Id = "file1_id";
        String file2Id = "file2_id";
        String folderId = "folder_id";
        String file3Id = "file3_id";
        String file4Id = "file4_id";

        /*
         * Given
         */
        scheduleInPlaceRecordLoaders.setEnabled(true);
        scheduleInPlaceRecordLoaders.setMaxActiveLoaders(8);
        scheduleInPlaceRecordLoaders.setCollabSiteId(DEFAULT_COLLABORATION_SITE_ID);
        scheduleInPlaceRecordLoaders.setRecordDeclarationLimit("0");
        scheduleInPlaceRecordLoaders.setCollabSitePaths(null);

        // mock the collaboration site
        RestCoreAPI mockedCoreApi = mockCoreApi();
        Site collabSite = mockSitesEndpoint(mockedCoreApi, DEFAULT_COLLABORATION_SITE_ID);
        String documentLibrary = mockExistingCollaborationSite(collabSite, DEFAULT_COLLABORATION_SITE_ID, true);

        // mock listing the document library
        Node doclibNode = mockNodesEndpoint(mockedCoreApi, documentLibrary);
        List<RestNodeModel> level0Nodes = Arrays.asList(mockNodeModel(file1Id, true), mockNodeModel(file2Id, true), mockNodeModel(folderId, false));
        mockListChildren(doclibNode, false, level0Nodes);

        // mock listing the folder
        Node folderNode = mockNodesEndpoint(mockedCoreApi, folderId);
        List<RestNodeModel> level1Nodes = Arrays.asList(mockNodeModel(file3Id, true), mockNodeModel(file4Id, true));
        mockListChildren(folderNode, false, level1Nodes);

        /*
         * When
         */
        EventResult result = scheduleInPlaceRecordLoaders.processEvent(null, new StopWatch());

        /*
         * Then
         */
        assertEquals(true, result.isSuccess());
        List<String> scheduledFiles = Arrays.asList(file1Id, file2Id, file3Id, file4Id);
        validateScheduleFilesOutputMessage(new ArrayList<>(), scheduledFiles, (String)result.getData());
        validateFiredEvents(true, scheduledFiles, result.getNextEvents());
    }

    /**
     * Given the collaboration site already exists and is loaded in the database
     * When running the scheduler with numberOfRecordsToDeclare=1
     * Then we don't attempt to load or create the site and 1 file is created and scheduled to be declared
     * 
     * @throws Exception
     */
    @Test
    public void testDeclareRecordsColabSiteExistsAndLoadedInDb() throws Exception
    {
        String fileID = UUID.randomUUID().toString();

        /*
         * Given
         */
        String numberOfRecordsToDeclare = "1";
        scheduleInPlaceRecordLoaders.setEnabled(true);
        scheduleInPlaceRecordLoaders.setRecordDeclarationLimit(numberOfRecordsToDeclare);
        scheduleInPlaceRecordLoaders.setMaxActiveLoaders(DEFAULT_MAX_ACTIVE_LOADERS);
        scheduleInPlaceRecordLoaders.setCollabSiteId(DEFAULT_COLLABORATION_SITE_ID);
        scheduleInPlaceRecordLoaders.setCollabSitePaths(null);

        RestCoreAPI mockedRestCoreAPI = mockCoreApi();
        Site mockedSitesEndpoint = mockSitesEndpoint(mockedRestCoreAPI, DEFAULT_COLLABORATION_SITE_ID);
        String documentLibraryId = mockExistingCollaborationSite(mockedSitesEndpoint, DEFAULT_COLLABORATION_SITE_ID, true);

        Node doclibNodesEndpoint = mockNodesEndpoint(mockedRestCoreAPI, documentLibraryId);

        mockListChildren(doclibNodesEndpoint, false, new ArrayList<>());

        // mock node builder
        NodesBuilder mockedNodeBuilder = mock(NodesBuilder.class);
        when(doclibNodesEndpoint.defineNodes()).thenReturn(mockedNodeBuilder);

        // mock create folder helper method
        NodeDetail mockedAutoGeneratedFolder = mock(NodeDetail.class);
        when(mockedNodeBuilder.folder("AutoGeneratedFiles")).thenReturn(mockedAutoGeneratedFolder);

        // mock create file
        NodeDetail mockedFile = mock(NodeDetail.class);
        when(mockedFile.getId()).thenReturn(fileID);
        when(mockedFile.getName()).thenReturn("fileName");
        when(mockedAutoGeneratedFolder.file("recordToBe")).thenReturn(mockedFile);

        /*
         * When
         */
        EventResult result = scheduleInPlaceRecordLoaders.processEvent(null, new StopWatch());

        /*
         * Then
         */
        verify(mockedSitesEndpoint, never()).createSite();
        verify(mockedSiteDataService, never()).addSite(any(SiteData.class));
        assertEquals(true, result.isSuccess());
        List<String> createdAndScheduledFiles = Arrays.asList(fileID);
        validateScheduleFilesOutputMessage(createdAndScheduledFiles, createdAndScheduledFiles, (String)result.getData());
        validateFiredEvents(true, Arrays.asList(fileID), result.getNextEvents());
    }

    @Test
    public void testDeclareRecordsColabSiteDoesNotExistsAndLoadedInDb() throws Exception
    {
        String numberOfRecordsToDeclare = "1";
        int maxActiveLoaders = 8;
        String siteId = "testSiteId";
        String documentLibraryId = UUID.randomUUID().toString();
        scheduleInPlaceRecordLoaders.setEnabled(true);
        scheduleInPlaceRecordLoaders.setRecordDeclarationLimit(numberOfRecordsToDeclare);
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

        when(mockedRestWrapper.withCoreAPI()).thenReturn(mockedRestCoreAPI);
        when(mockedRestWrapper.getStatusCode()).thenReturn(Integer.toString(HttpStatus.SC_NOT_FOUND));

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
        when(mockedRestWrapper.withParams(any(String.class), any(String.class), any(String.class))).thenReturn(mockedRestWrapperWithParams);

        //for creating files
        NodeDetail mockedTargetNodeDetail = mock(NodeDetail.class);
        NodesBuilder mockedNodeBuilder = mock(NodesBuilder.class);
        when(mockedNodeBuilder.folder("AutoGeneratedFiles")).thenReturn(mockedTargetNodeDetail);
        when(mockedNode.defineNodes()).thenReturn(mockedNodeBuilder);
        when(mockedRestCoreAPI.usingNode(any(RepoTestModel.class))).thenReturn(mockedNode);

        NodeDetail mockedFile = mock(NodeDetail.class);
        String fileID = UUID.randomUUID().toString();
        when(mockedFile.getId()).thenReturn(fileID);
        when(mockedFile.getName()).thenReturn("fileName");
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
        scheduleInPlaceRecordLoaders.setRecordDeclarationLimit(numberOfRecordsToDeclare);
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

        when(mockedRestWrapper.withCoreAPI()).thenReturn(mockedRestCoreAPI);
        when(mockedRestWrapper.getStatusCode()).thenReturn(Integer.toString(HttpStatus.SC_OK));

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
        when(mockedRestWrapper.withParams(any(String.class), any(String.class), any(String.class))).thenReturn(mockedRestWrapperWithParams);

        //for creating files
        NodeDetail mockedTargetNodeDetail = mock(NodeDetail.class);
        NodesBuilder mockedNodeBuilder = mock(NodesBuilder.class);
        when(mockedNodeBuilder.folder("AutoGeneratedFiles")).thenReturn(mockedTargetNodeDetail);
        when(mockedNode.defineNodes()).thenReturn(mockedNodeBuilder);
        when(mockedRestWrapper.withCoreAPI()).thenReturn(mockedRestCoreAPI);

        NodeDetail mockedFile = mock(NodeDetail.class);
        String fileID = UUID.randomUUID().toString();
        when(mockedFile.getId()).thenReturn(fileID);
        when(mockedFile.getName()).thenReturn("recordName");
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
        scheduleInPlaceRecordLoaders.setRecordDeclarationLimit(numberOfRecordsToDeclare);
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

        when(mockedRestWrapper.withCoreAPI()).thenReturn(mockedRestCoreAPI);
        when(mockedRestWrapper.getStatusCode()).thenReturn(Integer.toString(HttpStatus.SC_NOT_FOUND));

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
        when(mockedRestWrapper.withParams(any(String.class), any(String.class), any(String.class))).thenReturn(mockedRestWrapperWithParams);

        //for creating files
        NodeDetail mockedTargetNodeDetail = mock(NodeDetail.class);
        NodesBuilder mockedNodeBuilder = mock(NodesBuilder.class);
        when(mockedNodeBuilder.folder("AutoGeneratedFiles")).thenReturn(mockedTargetNodeDetail);
        when(mockedNode.defineNodes()).thenReturn(mockedNodeBuilder);
        when(mockedRestCoreAPI.usingNode(any(RepoTestModel.class))).thenReturn(mockedNode);

        NodeDetail mockedFile = mock(NodeDetail.class);
        String fileID = UUID.randomUUID().toString();
        when(mockedFile.getId()).thenReturn(fileID);
        when(mockedFile.getName()).thenReturn("fileName");
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
        scheduleInPlaceRecordLoaders.setRecordDeclarationLimit(numberOfRecordsToDeclare);
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

        when(mockedRestWrapper.withCoreAPI()).thenReturn(mockedRestCoreAPI);
        when(mockedRestWrapper.getStatusCode()).thenReturn(Integer.toString(HttpStatus.SC_OK));

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
        when(mockedOnModel1.getName()).thenReturn("fileName1");
        RestNodeModel mockedFile1 = mock(RestNodeModel.class);
        when(mockedFile1.onModel()).thenReturn(mockedOnModel1);

        String childFileId2 = "childFileId2";
        RestNodeModel mockedOnModel2 = mock(RestNodeModel.class);
        when(mockedOnModel2.getIsFile()).thenReturn(true);
        when(mockedOnModel2.getId()).thenReturn(childFileId2);
        when(mockedOnModel2.getName()).thenReturn("fileName2");
        RestNodeModel mockedFile2 = mock(RestNodeModel.class);
        when(mockedFile2.onModel()).thenReturn(mockedOnModel2);

        when(mockedCollection.getEntries()).thenReturn(Arrays.asList(mockedFile1, mockedFile2));
        Node mockedNode = mock(Node.class);
        when(mockedNode.listChildren()).thenReturn(mockedCollection);
        RestCoreAPI mockedRestCoreAPIWithParams = mock(RestCoreAPI.class);
        when(mockedRestCoreAPIWithParams.usingNode(any(RepoTestModel.class))).thenReturn(mockedNode);
        RestWrapper mockedRestWrapperWithParams = mock(RestWrapper.class);
        when(mockedRestWrapperWithParams.withCoreAPI()).thenReturn(mockedRestCoreAPIWithParams);
        when(mockedRestWrapper.withParams(any(String.class), any(String.class), any(String.class))).thenReturn(mockedRestWrapperWithParams);

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
        scheduleInPlaceRecordLoaders.setRecordDeclarationLimit(numberOfRecordsToDeclare);
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

        when(mockedRestWrapper.withCoreAPI()).thenReturn(mockedRestCoreAPI);
        when(mockedRestWrapper.getStatusCode()).thenReturn(Integer.toString(HttpStatus.SC_OK))
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
        when(mockedRestWrapper.withParams(any(String.class), any(String.class), any(String.class))).thenReturn(mockedRestWrapperWithParams);

        //for creating files
        NodeDetail mockedTargetNodeDetail = mock(NodeDetail.class);
        NodesBuilder mockedNodeBuilder = mock(NodesBuilder.class);
        when(mockedNodeBuilder.folder("AutoGeneratedFiles")).thenReturn(mockedTargetNodeDetail);
        when(mockedNode.defineNodes()).thenReturn(mockedNodeBuilder);
        when(mockedRestCoreAPI.usingNode(any(RepoTestModel.class))).thenReturn(mockedNode);

        NodeDetail mockedFile = mock(NodeDetail.class);
        String fileID = UUID.randomUUID().toString();
        when(mockedFile.getId()).thenReturn(fileID);
        when(mockedFile.getName()).thenReturn("fileName");
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
        scheduleInPlaceRecordLoaders.setRecordDeclarationLimit(numberOfRecordsToDeclare);
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

        when(mockedRestWrapper.withCoreAPI()).thenReturn(mockedRestCoreAPI);
        when(mockedRestWrapper.getStatusCode()).thenReturn(Integer.toString(HttpStatus.SC_OK));

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
        when(mockedOnModel1.getName()).thenReturn("fileName1");
        RestNodeModel mockedFile1 = mock(RestNodeModel.class);
        when(mockedFile1.onModel()).thenReturn(mockedOnModel1);

        String childFileId2 = "childFileId2";
        RestNodeModel mockedOnModel2 = mock(RestNodeModel.class);
        when(mockedOnModel2.getIsFile()).thenReturn(true);
        when(mockedOnModel2.getId()).thenReturn(childFileId2);
        when(mockedOnModel2.getName()).thenReturn("fileName2");
        RestNodeModel mockedFile2 = mock(RestNodeModel.class);
        when(mockedFile2.onModel()).thenReturn(mockedOnModel2);

        when(mockedCollection.getEntries()).thenReturn(Arrays.asList(mockedFile1, mockedFile2));
        Node mockedNode = mock(Node.class);
        when(mockedNode.listChildren()).thenReturn(mockedCollection);
        RestCoreAPI mockedRestCoreAPIWithParams = mock(RestCoreAPI.class);
        when(mockedRestCoreAPIWithParams.usingNode(any(RepoTestModel.class))).thenReturn(mockedNode);
        RestWrapper mockedRestWrapperWithParams = mock(RestWrapper.class);
        when(mockedRestWrapperWithParams.withCoreAPI()).thenReturn(mockedRestCoreAPIWithParams);
        when(mockedRestWrapper.withParams(any(String.class), any(String.class), any(String.class))).thenReturn(mockedRestWrapperWithParams);

        //for creating files
        NodeDetail mockedTargetNodeDetail = mock(NodeDetail.class);
        NodesBuilder mockedNodeBuilder = mock(NodesBuilder.class);
        when(mockedNodeBuilder.folder("AutoGeneratedFiles")).thenReturn(mockedTargetNodeDetail);
        when(mockedNode.defineNodes()).thenReturn(mockedNodeBuilder);
        when(mockedRestCoreAPI.usingNode(any(RepoTestModel.class))).thenReturn(mockedNode);

        NodeDetail mockedFile = mock(NodeDetail.class);
        String fileID = UUID.randomUUID().toString();
        when(mockedFile.getId()).thenReturn(fileID);
        when(mockedFile.getName()).thenReturn("fileName");
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
        scheduleInPlaceRecordLoaders.setRecordDeclarationLimit(numberOfRecordsToDeclare);
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

        when(mockedRestWrapper.withCoreAPI()).thenReturn(mockedRestCoreAPI);
        when(mockedRestWrapper.getStatusCode()).thenReturn(Integer.toString(HttpStatus.SC_OK));

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
        when(mockedOnModel1.getName()).thenReturn("fileName1");
        RestNodeModel mockedFile1 = mock(RestNodeModel.class);
        when(mockedFile1.onModel()).thenReturn(mockedOnModel1);

        String childFileId2 = "childFileId2";
        RestNodeModel mockedOnModel2 = mock(RestNodeModel.class);
        when(mockedOnModel2.getIsFile()).thenReturn(true);
        when(mockedOnModel2.getId()).thenReturn(childFileId2);
        when(mockedOnModel2.getName()).thenReturn("fileName2");
        RestNodeModel mockedFile2 = mock(RestNodeModel.class);
        when(mockedFile2.onModel()).thenReturn(mockedOnModel2);

        String childFolderId1 = "childFolderId1";
        RestNodeModel mockedOnModel3 = mock(RestNodeModel.class);
        when(mockedOnModel3.getIsFile()).thenReturn(false);
        when(mockedOnModel3.getId()).thenReturn(childFolderId1);
        when(mockedOnModel3.getName()).thenReturn("fileName3");
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
        when(mockedRestWrapper.withParams(any(String.class), any(String.class), any(String.class))).thenReturn(mockedRestWrapperWithParams);

        //for creating files
        NodeDetail mockedTargetNodeDetail = mock(NodeDetail.class);
        NodesBuilder mockedNodeBuilder = mock(NodesBuilder.class);
        when(mockedNodeBuilder.folder("AutoGeneratedFiles")).thenReturn(mockedTargetNodeDetail);
        when(mockedNode.defineNodes()).thenReturn(mockedNodeBuilder);
        when(mockedRestCoreAPI.usingNode(any(RepoTestModel.class))).thenReturn(mockedNode);

        NodeDetail mockedFile = mock(NodeDetail.class);
        String fileID = UUID.randomUUID().toString();
        when(mockedFile.getId()).thenReturn(fileID);
        when(mockedFile.getName()).thenReturn("fileName");
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
        scheduleInPlaceRecordLoaders.setRecordDeclarationLimit(numberOfRecordsToDeclare);
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

        when(mockedRestWrapper.withCoreAPI()).thenReturn(mockedRestCoreAPI);
        when(mockedRestWrapper.getStatusCode()).thenReturn(Integer.toString(HttpStatus.SC_OK));

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
        when(mockedOnModel1.getName()).thenReturn("fileName1");
        RestNodeModel mockedFile1 = mock(RestNodeModel.class);
        when(mockedFile1.onModel()).thenReturn(mockedOnModel1);

        String childFileId2 = "childFileId2";
        RestNodeModel mockedOnModel2 = mock(RestNodeModel.class);
        when(mockedOnModel2.getIsFile()).thenReturn(true);
        when(mockedOnModel2.getId()).thenReturn(childFileId2);
        when(mockedOnModel2.getName()).thenReturn("fileName2");
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
        when(mockedRestWrapper.withParams(any(String.class), any(String.class), any(String.class))).thenReturn(mockedRestWrapperWithParams);

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
        scheduleInPlaceRecordLoaders.setRecordDeclarationLimit("");
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
        scheduleInPlaceRecordLoaders.setRecordDeclarationLimit("");
        scheduleInPlaceRecordLoaders.afterPropertiesSet();
    }

    /**
     * Utility method that mocks the core api (with and without params)
     * 
     * @return the mocked core api
     */
    private RestCoreAPI mockCoreApi()
    {
        RestCoreAPI mockedRestCoreAPI = mock(RestCoreAPI.class);
        when(mockedRestWrapper.withCoreAPI()).thenReturn(mockedRestCoreAPI);

        RestWrapper mockedRestWrapperWithParams = mock(RestWrapper.class);
        when(mockedRestWrapper.withParams("where=(isPrimary=true)", "relativePath=", "skipCount=0")).thenReturn(mockedRestWrapperWithParams);
        when(mockedRestWrapperWithParams.withCoreAPI()).thenReturn(mockedRestCoreAPI);

        return mockedRestCoreAPI;
    }

    /**
     * Utility method that mocks the sites endpoint
     * 
     * @param mockedCoreApi the mocked core api
     * @param siteId the id of the site to mock the endpoint for
     * @return the mocked sites endpoint
     */
    private Site mockSitesEndpoint(RestCoreAPI mockedCoreApi, String siteId)
    {
        /*
         * Mock the sites endpoint
         */
        Site mockedSiteEndpoint = mock(Site.class);
        when(mockedCoreApi.usingSite(siteId.toLowerCase())).thenReturn(mockedSiteEndpoint);
        return mockedSiteEndpoint;
    }

    /**
     * Utility method that mocks the nodes endpoint
     * 
     * @param mockedCoreApi the mocked core api
     * @param nodeId the id of the node to mock the endpoint for
     * @return the mocked endpoint
     * @throws Exception
     */
    private Node mockNodesEndpoint(RestCoreAPI mockedCoreApi, String nodeId) throws Exception
    {
        ArgumentMatcher<ContentModel> modelForCurrentNode = new ArgumentMatcher<ContentModel>() {
            @Override
            public boolean matches(Object argument) {
                return ((ContentModel) argument).getNodeRef().equals(nodeId);
            }
        };
        Node nodesEndpoint = mock(Node.class);
        doReturn(nodesEndpoint).when(mockedCoreApi).usingNode(argThat(modelForCurrentNode));
        return nodesEndpoint;
    }

    /**
     * Utility method that mocks the retrieval of an existing collaboration site
     *
     * @return the id of the generated document library
     * @throws Exception
     */
    private String mockExistingCollaborationSite(Site mockedSiteEndpoint, String siteId, boolean siteExistsInMongo) throws Exception
    {
        String documentLibraryId = UUID.randomUUID().toString();

        RestSiteModel colabSite = mock(RestSiteModel.class);
        when(mockedSiteEndpoint.getSite()).thenReturn(colabSite);
        when(colabSite.getVisibility()).thenReturn(Visibility.PUBLIC);

        /*
         * Mock document library
         */
        RestSiteContainerModel mockedRestSiteContainerModel = mock(RestSiteContainerModel.class);
        when(mockedRestSiteContainerModel.getId()).thenReturn(documentLibraryId);
        when(mockedSiteEndpoint.getSiteContainer("documentLibrary")).thenReturn(mockedRestSiteContainerModel);
        when(mockedRestWrapper.getStatusCode()).thenReturn(Integer.toString(HttpStatus.SC_OK));

        /*
         * Mock the mongo service
         */
        if(siteExistsInMongo)
        {
            SiteData mockedSiteData = mock(SiteData.class);
            when(mockedSiteDataService.getSite(siteId.toLowerCase())).thenReturn(mockedSiteData);
        }
        else
        {
            when(mockedSiteDataService.getSite(siteId.toLowerCase())).thenReturn(null);
        }

        return documentLibraryId;
    }
 
    /**
     * Utility method that mocks list children api call
     * 
     * @param mockedNodesEndpoint the nodes endpoint for the current node
     * @param hasMoreItems whether the mocked paginated list has more items, response parameter
     * @param children the list of children to return, response parameter
     * @throws Exception
     */
    private void mockListChildren(Node mockedNodesEndpoint, boolean hasMoreItems, List<RestNodeModel> children) throws Exception
    {
        RestNodeModelsCollection childrenCollection = mock(RestNodeModelsCollection.class);
        when(mockedNodesEndpoint.listChildren()).thenReturn(childrenCollection);

        /*
         * Mock the results
         */
        RestPaginationModel mockedPagination = mock(RestPaginationModel.class);
        when(childrenCollection.getPagination()).thenReturn(mockedPagination);
        when(mockedPagination.isHasMoreItems()).thenReturn(hasMoreItems);
        when(childrenCollection.getEntries()).thenReturn(children);
        when(mockedRestWrapper.getStatusCode()).thenReturn(Integer.toString(HttpStatus.SC_OK));
    }

    /**
     * Utility method that mock a node model
     * @param id the id of the node to mock
     * @param isFile isFile value of the node to mock
     * @return the mocked model for the file
     * @throws Exception
     */
    private RestNodeModel mockNodeModel(String id, boolean isFile) throws Exception
    {
        RestNodeModel node = mock(RestNodeModel.class);
        RestNodeModel onModelNode = mock(RestNodeModel.class);
        when(node.onModel()).thenReturn(onModelNode);

        when(onModelNode.getId()).thenReturn(id);
        when(onModelNode.getIsFile()).thenReturn(isFile);
        when(onModelNode.getName()).thenReturn("fileName");
        return node;
    }

    /**
     * Utility method that validates the list of events returned bu the scheduler
     * 
     * @param rescheduleSelfExpected whether we expect the event to reschedule itself
     * @param expectedScheduledFileIds the list of files we expect to be scheduled for declare
     * @param result the event result to validate
     */
    private void validateFiredEvents(boolean rescheduleSelfExpected, List<String> expectedScheduledFileIds, List<Event> nextEvents)
    {
        assertEquals(expectedScheduledFileIds.size() + 1, nextEvents.size());

        boolean rescheduleSelfEventFired = false;
        boolean doneEventFired = false;
        List<String> scheduledFiles = new ArrayList<>();
        for(Event event : nextEvents)
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

    /**
     * Validate the output message returned when files are scheduled
     * 
     * @param createdFiles the files created in the current run
     * @param scheduledFiles the files scheduled in the current fun
     * @param outputMessage the message to validate
     */
    private void validateScheduleFilesOutputMessage(List<String> createdFiles, List<String> scheduledFiles, String outputMessage)
    {
        StringBuilder template = new StringBuilder();
        template.append("Preparing files to declare: \n");
        for(String file: createdFiles)
        {
            template.append("Created file " + file + ".");
        }
        for(String file: scheduledFiles)
        {
            template.append("Sheduled file to be declared as record: " + file + ". ");
        }
        template.append("Raised further " + scheduledFiles.size() + " events and rescheduled self.");
        assertEquals(template.toString(), outputMessage);
    }
}
