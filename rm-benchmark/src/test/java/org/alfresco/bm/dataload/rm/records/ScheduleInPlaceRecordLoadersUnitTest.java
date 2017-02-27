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

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.UUID;

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
import org.alfresco.utility.model.RepoTestModel;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.social.alfresco.api.entities.Site.Visibility;

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

    @Test
    public void testWithNoRecordsToDeclare() throws Exception
    {
        String numberOfRecordsToDeclare = "0";
        scheduleInPlaceRecordLoaders.setEnabled(true);
        scheduleInPlaceRecordLoaders.setRecordsToDeclare(numberOfRecordsToDeclare);
        EventResult result = scheduleInPlaceRecordLoaders.processEvent(null, new StopWatch());
        assertEquals(true, result.isSuccess());
        assertEquals(ScheduleInPlaceRecordLoaders.DONE_EVENT_MSG, result.getData());
        assertEquals(1, result.getNextEvents().size());
        assertEquals(scheduleInPlaceRecordLoaders.getEventNameComplete(), result.getNextEvents().get(0).getName());
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
        Node mockedNodeTargetNode = mock(Node.class);
        when(mockedNodeTargetNode.defineNodes()).thenReturn(mockedNodeBuilder);
        RestCoreAPI mockedRestCoreAPIWithRelativePath = mock(RestCoreAPI.class);
        when(mockedRestCoreAPIWithRelativePath.usingNode(any(RepoTestModel.class))).thenReturn(mockedNodeTargetNode);
        RestWrapper mockedRestWrapperWithRelativePath = mock(RestWrapper.class);
        when(mockedRestWrapperWithRelativePath.withCoreAPI()).thenReturn(mockedRestCoreAPIWithRelativePath);
        when(mockedCoreAPI.withParams(any(String.class))).thenReturn(mockedRestWrapperWithRelativePath);

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
        Node mockedNodeTargetNode = mock(Node.class);
        when(mockedNodeTargetNode.defineNodes()).thenReturn(mockedNodeBuilder);
        RestCoreAPI mockedRestCoreAPIWithRelativePath = mock(RestCoreAPI.class);
        when(mockedRestCoreAPIWithRelativePath.usingNode(any(RepoTestModel.class))).thenReturn(mockedNodeTargetNode);
        RestWrapper mockedRestWrapperWithRelativePath = mock(RestWrapper.class);
        when(mockedRestWrapperWithRelativePath.withCoreAPI()).thenReturn(mockedRestCoreAPIWithRelativePath);
        when(mockedCoreAPI.withParams(any(String.class))).thenReturn(mockedRestWrapperWithRelativePath);

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
        RestCoreAPI mockedRestCoreAPIWithParams = mock(RestCoreAPI.class);
        when(mockedRestCoreAPIWithParams.usingNode(any(RepoTestModel.class))).thenReturn(mockedNode);
        RestWrapper mockedRestWrapperWithParams = mock(RestWrapper.class);
        when(mockedRestWrapperWithParams.withCoreAPI()).thenReturn(mockedRestCoreAPIWithParams);
        when(mockedCoreAPI.withParams(any(String.class), any(String.class))).thenReturn(mockedRestWrapperWithParams);

        //for creating files
        NodeDetail mockedTargetNodeDetail = mock(NodeDetail.class);
        NodesBuilder mockedNodeBuilder = mock(NodesBuilder.class);
        when(mockedNodeBuilder.folder("AutoGeneratedFiles")).thenReturn(mockedTargetNodeDetail);
        Node mockedNodeTargetNode = mock(Node.class);
        when(mockedNodeTargetNode.defineNodes()).thenReturn(mockedNodeBuilder);
        RestCoreAPI mockedRestCoreAPIWithRelativePath = mock(RestCoreAPI.class);
        when(mockedRestCoreAPIWithRelativePath.usingNode(any(RepoTestModel.class))).thenReturn(mockedNodeTargetNode);
        RestWrapper mockedRestWrapperWithRelativePath = mock(RestWrapper.class);
        when(mockedRestWrapperWithRelativePath.withCoreAPI()).thenReturn(mockedRestCoreAPIWithRelativePath);
        when(mockedCoreAPI.withParams(any(String.class))).thenReturn(mockedRestWrapperWithRelativePath);

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
        Node mockedNodeTargetNode = mock(Node.class);
        when(mockedNodeTargetNode.defineNodes()).thenReturn(mockedNodeBuilder);
        RestCoreAPI mockedRestCoreAPIWithRelativePath = mock(RestCoreAPI.class);
        when(mockedRestCoreAPIWithRelativePath.usingNode(any(RepoTestModel.class))).thenReturn(mockedNodeTargetNode);
        RestWrapper mockedRestWrapperWithRelativePath = mock(RestWrapper.class);
        when(mockedRestWrapperWithRelativePath.withCoreAPI()).thenReturn(mockedRestCoreAPIWithRelativePath);
        when(mockedCoreAPI.withParams(any(String.class))).thenReturn(mockedRestWrapperWithRelativePath);

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
        Node mockedNodeTargetNode = mock(Node.class);
        when(mockedNodeTargetNode.defineNodes()).thenReturn(mockedNodeBuilder);
        RestCoreAPI mockedRestCoreAPIWithRelativePath = mock(RestCoreAPI.class);
        when(mockedRestCoreAPIWithRelativePath.usingNode(any(RepoTestModel.class))).thenReturn(mockedNodeTargetNode);
        RestWrapper mockedRestWrapperWithRelativePath = mock(RestWrapper.class);
        when(mockedRestWrapperWithRelativePath.withCoreAPI()).thenReturn(mockedRestCoreAPIWithRelativePath);
        when(mockedCoreAPI.withParams(any(String.class))).thenReturn(mockedRestWrapperWithRelativePath);

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
        Node mockedNodeTargetNode = mock(Node.class);
        when(mockedNodeTargetNode.defineNodes()).thenReturn(mockedNodeBuilder);
        RestCoreAPI mockedRestCoreAPIWithRelativePath = mock(RestCoreAPI.class);
        when(mockedRestCoreAPIWithRelativePath.usingNode(any(RepoTestModel.class))).thenReturn(mockedNodeTargetNode);
        RestWrapper mockedRestWrapperWithRelativePath = mock(RestWrapper.class);
        when(mockedRestWrapperWithRelativePath.withCoreAPI()).thenReturn(mockedRestCoreAPIWithRelativePath);
        when(mockedCoreAPI.withParams(any(String.class))).thenReturn(mockedRestWrapperWithRelativePath);

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
        Node mockedNodeTargetNode = mock(Node.class);
        when(mockedNodeTargetNode.defineNodes()).thenReturn(mockedNodeBuilder);
        RestCoreAPI mockedRestCoreAPIWithRelativePath = mock(RestCoreAPI.class);
        when(mockedRestCoreAPIWithRelativePath.usingNode(any(RepoTestModel.class))).thenReturn(mockedNodeTargetNode);
        RestWrapper mockedRestWrapperWithRelativePath = mock(RestWrapper.class);
        when(mockedRestWrapperWithRelativePath.withCoreAPI()).thenReturn(mockedRestCoreAPIWithRelativePath);
        when(mockedCoreAPI.withParams(any(String.class))).thenReturn(mockedRestWrapperWithRelativePath);

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
}
