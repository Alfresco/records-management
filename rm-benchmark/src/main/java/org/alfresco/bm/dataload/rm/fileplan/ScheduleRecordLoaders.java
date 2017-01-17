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

package org.alfresco.bm.dataload.rm.fileplan;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

import org.alfresco.bm.cm.FolderData;
import org.alfresco.bm.dataload.RMBaseEventProcessor;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.session.SessionService;
import org.alfresco.rest.core.RestAPIFactory;
import org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponent;
import org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponentType;
import org.alfresco.rest.rm.community.requests.igCoreAPI.FilePlanComponentAPI;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;

public class ScheduleRecordLoaders extends RMBaseEventProcessor
{
    public static final String EVENT_NAME_LOAD_RECORDS = "loadRecords";
    public static final String EVENT_NAME_SCHEDULE_RECORD_LOADERS = "scheduleRecordLoaders";
    public static final String EVENT_NAME_LOADING_COMPLETE = "loadingRecordsComplete";
    public static final String EVENT_NAME_CONTINUE_LOADING_UNFILED_RECORD_FOLDERS = "scheduleUnfiledRecordFoldersLoaders";

    @Autowired
    private RestAPIFactory restAPIFactory;

    @Autowired
    private SessionService sessionService;

    private int maxActiveLoaders;
    private long loadCheckDelay;
    private boolean uploadRecords;
    private int recordsNumber;
    private String username;
    private String recordFolderPaths;
    private List<String> paths;
    private String eventNameLoadRecords = EVENT_NAME_LOAD_RECORDS;
    private String eventNameScheduleRecordLoaders = EVENT_NAME_SCHEDULE_RECORD_LOADERS;
    private String eventNameLoadingComplete = EVENT_NAME_LOADING_COMPLETE;
    private String eventNameContinueLoadingUnfiledRecordFolders = EVENT_NAME_CONTINUE_LOADING_UNFILED_RECORD_FOLDERS;

    private List<FolderData> recordFoldersThatNeedRecords = null;
    private HashMap<FolderData, Integer> mapOfRecordsPerRecordFolder = null;

    /**
     * @return the uploadRecords
     */
    public boolean isUploadRecords()
    {
        return uploadRecords;
    }

    /**
     * @param uploadRecords the uploadRecords to set
     */
    public void setUploadRecords(boolean uploadRecords)
    {
        this.uploadRecords = uploadRecords;
    }

    /**
     * @return the username
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * @return the recordFolderPaths
     */
    public String getRecordFolderPaths()
    {
        return recordFolderPaths;
    }

    /**
     * @param recordFolderPaths the recordFolderPaths to set
     */
    public void setRecordFolderPaths(String recordFolderPaths)
    {
        this.recordFolderPaths = recordFolderPaths;
        if (isNotBlank(this.recordFolderPaths))
        {
            paths = Arrays.asList(this.recordFolderPaths.split(","));
        }
    }

    /**
     * @return the recordsNumber
     */
    public int getRecordsNumber()
    {
        return recordsNumber;
    }

    /**
     * @param recordsNumber the recordsNumber to set
     */
    public void setRecordsNumber(int recordsNumber)
    {
        this.recordsNumber = recordsNumber;
    }

    /**
     * @return the loadCheckDelay
     */
    public long getLoadCheckDelay()
    {
        return loadCheckDelay;
    }

    /**
     * @param loadCheckDelay the loadCheckDelay to set
     */
    public void setLoadCheckDelay(long loadCheckDelay)
    {
        this.loadCheckDelay = loadCheckDelay;
    }



    /**
     * @return the maxActiveLoaders
     */
    public int getMaxActiveLoaders()
    {
        return maxActiveLoaders;
    }

    /**
     * @param maxActiveLoaders the maxActiveLoaders to set
     */
    public void setMaxActiveLoaders(int maxActiveLoaders)
    {
        this.maxActiveLoaders = maxActiveLoaders;
    }

    /**
     * @return the eventNameLoadRecords
     */
    public String getEventNameLoadRecords()
    {
        return eventNameLoadRecords;
    }

    /**
     * @param eventNameLoadRecords the eventNameLoadRecords to set
     */
    public void setEventNameLoadRecords(String eventNameLoadRecords)
    {
        this.eventNameLoadRecords = eventNameLoadRecords;
    }

    /**
     * @return the eventNameScheduleRecordLoaders
     */
    public String getEventNameScheduleRecordLoaders()
    {
        return eventNameScheduleRecordLoaders;
    }

    /**
     * @param eventNameScheduleRecordLoaders the eventNameScheduleRecordLoaders to set
     */
    public void setEventNameScheduleRecordLoaders(String eventNameScheduleRecordLoaders)
    {
        this.eventNameScheduleRecordLoaders = eventNameScheduleRecordLoaders;
    }

    /**
     * @return the eventNameLoadingComplete
     */
    public String getEventNameLoadingComplete()
    {
        return eventNameLoadingComplete;
    }

    /**
     * @param eventNameLoadingComplete the eventNameLoadingComplete to set
     */
    public void setEventNameLoadingComplete(String eventNameLoadingComplete)
    {
        this.eventNameLoadingComplete = eventNameLoadingComplete;
    }

    public String getEventNameContinueLoadingUnfiledRecordFolders()
    {
        return eventNameContinueLoadingUnfiledRecordFolders;
    }

    public void setEventNameContinueLoadingUnfiledRecordFolders(String eventNameContinueLoadingUnfiledRecordFolders)
    {
        this.eventNameContinueLoadingUnfiledRecordFolders = eventNameContinueLoadingUnfiledRecordFolders;
    }

    @Override
    protected EventResult processEvent(Event event) throws Exception
    {
        // Are there still sessions active?
        long sessionCount = sessionService.getActiveSessionsCount();
        int loaderSessionsToCreate = maxActiveLoaders - (int) sessionCount;
        List<Event> nextEvents = new ArrayList<Event>(maxActiveLoaders);

        // Do we actually need to do anything
        if (!isUploadRecords())
        {
            return new EventResult("Uploading of records into File Plan not wanted, continue with loading unfiled record folders structure.",
                                   new Event(getEventNameContinueLoadingUnfiledRecordFolders(), null));
        }
        if (recordsNumber > 0)
        {
            // Prepare Records
            prepareRecords(loaderSessionsToCreate, nextEvents);
        }

        // If there are no events, then we have finished
        String msg = null;
        if (loaderSessionsToCreate > 0 && nextEvents.size() == 0)
        {
            // There are no records to load even though there are sessions available
            recordFoldersThatNeedRecords = null;
            mapOfRecordsPerRecordFolder = null;
            Event nextEvent = new Event(eventNameLoadingComplete, null);
            nextEvents.add(nextEvent);
            msg = "Loading completed.  Raising 'done' event.";
        }
        else
        {
            // Reschedule self
            Event nextEvent = new Event(eventNameScheduleRecordLoaders, System.currentTimeMillis() + loadCheckDelay,
                        null);
            nextEvents.add(nextEvent);
            msg = "Raised further " + (nextEvents.size() - 1) + " events and rescheduled self.";
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(msg);
        }

        EventResult result = new EventResult(msg, nextEvents);
        return result;
    }

    private void calculateListOfEmptyFolders()
    {
        if (recordFoldersThatNeedRecords == null)
        {
            recordFoldersThatNeedRecords = new ArrayList<FolderData>();
            if (paths == null || paths.size() == 0)
            {
                // get the existing file plan folder structure
                recordFoldersThatNeedRecords.addAll(initialiseFoldersToExistingStructure(RECORD_FOLDER_CONTEXT));
            }
            else
            {
                LinkedHashSet<FolderData> structureFromExistentProvidedPaths = new LinkedHashSet<FolderData>();
                for (String path : paths)
                {
                    if(!path.startsWith("/"))
                    {
                        path = "/" + path;
                    }
                    //if the path is category and exists
                    FolderData folder = fileFolderService.getFolder(RECORD_CATEGORY_CONTEXT,
                                RECORD_CONTAINER_PATH + path);
                    if(folder == null)//if folder is not a category verify if it is a record folder and exists
                    {
                        folder = fileFolderService.getFolder(RECORD_FOLDER_CONTEXT,
                                    RECORD_CONTAINER_PATH + path);
                    }
                    if (folder != null)// if folder exists
                    {
                        structureFromExistentProvidedPaths.addAll(getRecordFolders(folder));
                    }
                    else
                    {
                        try
                        {
                            folder = createFolder(path);
                            recordFoldersThatNeedRecords.add(folder);
                        }
                        catch (Exception e)
                        {
                            // something went wrong on creating current path structure, not all required paths will be created
                        }
                    }
                }
                // add record folders from existent paths
                if (structureFromExistentProvidedPaths.size() > 0)
                {
                    recordFoldersThatNeedRecords.addAll(structureFromExistentProvidedPaths);
                }
                // configured paths did not existed in db and something went wrong with creation for all of them,
                // initialize to existing structure in this case
                if (recordFoldersThatNeedRecords.size() == 0)
                {
                    recordFoldersThatNeedRecords.addAll(initialiseFoldersToExistingStructure(RECORD_FOLDER_CONTEXT));
                }
            }
            if (recordFoldersThatNeedRecords.size() > 0)
            {
                mapOfRecordsPerRecordFolder = distributeNumberOfRecords(recordFoldersThatNeedRecords, recordsNumber);
            }
        }
    }

    private FolderData createFolder(String path) throws Exception
    {
        FilePlanComponentAPI api = restAPIFactory.getFilePlanComponentsAPI(new UserModel(getUsername(), getUsername()));
        List<String> pathElements = getPathElements(path);
        FolderData parentFolder = fileFolderService.getFolder("", RECORD_CONTAINER_PATH);
        // for(String pathElement: pathElements)
        int pathElementsLength = pathElements.size();
        for (int i = 0; i < pathElementsLength; i++)
        {
            String pathElement = pathElements.get(i);
            FilePlanComponent filePlanComponent = api.getFilePlanComponent(parentFolder.getId());
            FolderData folder = fileFolderService.getFolder(RECORD_CATEGORY_CONTEXT,
                        parentFolder.getPath() + "/" + pathElement);
            if (folder != null)
            {
                parentFolder = folder;
            }
            else
            {
                String filePlanComponentType = FilePlanComponentType.RECORD_CATEGORY_TYPE.toString();
                String context = RECORD_CATEGORY_CONTEXT;
                if (i == (pathElementsLength - 1))
                {
                    filePlanComponentType = FilePlanComponentType.RECORD_FOLDER_TYPE.toString();
                    context = RECORD_FOLDER_CONTEXT;
                }
                parentFolder = createFilePlanComponentWithFixedName(parentFolder, api, filePlanComponent, pathElement,
                            filePlanComponentType, context);
            }
        }
        return parentFolder;
    }

    private void prepareRecords(int loaderSessionsToCreate, List<Event> nextEvents)
    {
        calculateListOfEmptyFolders();
        List<FolderData> emptyFolders = new ArrayList<FolderData>();
        emptyFolders.addAll(recordFoldersThatNeedRecords);
        while (nextEvents.size() < loaderSessionsToCreate)
        {
            if (mapOfRecordsPerRecordFolder == null || mapOfRecordsPerRecordFolder.size() == 0)
            {
                break;
            }
            // Schedule a load for each folder
            for (FolderData emptyFolder : emptyFolders)
            {
                int recordsToCreate = mapOfRecordsPerRecordFolder.get(emptyFolder) - (int) emptyFolder.getFileCount();
                if (recordsToCreate <= 0)
                {
                    recordFoldersThatNeedRecords.remove(emptyFolder);
                    mapOfRecordsPerRecordFolder.remove(emptyFolder);
                }
                else
                {
                    try
                    {
                        // Create a lock folder that has too many files and folders so that it won't be picked up
                        // by this process in subsequent trawls
                        String lockPath = emptyFolder.getPath() + "/locked";
                        FolderData lockFolder = new FolderData(UUID.randomUUID().toString(), emptyFolder.getContext(),
                                    lockPath, Long.MAX_VALUE, Long.MAX_VALUE);
                        fileFolderService.createNewFolder(lockFolder);
                        // We locked this, so the load can be scheduled.
                        // The loader will remove the lock when it completes
                        DBObject loadData = BasicDBObjectBuilder.start().add(FIELD_CONTEXT, emptyFolder.getContext())
                                    .add(FIELD_PATH, emptyFolder.getPath())
                                    .add(FIELD_RECORDS_TO_CREATE, Integer.valueOf(recordsToCreate))
                                    .add(FIELD_SITE_MANAGER, username).get();
                        Event loadEvent = new Event(eventNameLoadRecords, loadData);
                        // Each load event must be associated with a session
                        String sessionId = sessionService.startSession(loadData);
                        loadEvent.setSessionId(sessionId);
                        // Add the event to the list
                        nextEvents.add(loadEvent);
                        recordFoldersThatNeedRecords.remove(emptyFolder);
                        mapOfRecordsPerRecordFolder.remove(emptyFolder);
                    }
                    catch (Exception e)
                    {
                        // The lock was already applied; find another
                        continue;
                    }
                }
                // Check if we have enough
                if (nextEvents.size() >= loaderSessionsToCreate)
                {
                    break;
                }
            }
        }
    }
}