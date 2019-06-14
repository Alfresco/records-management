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

import java.util.ArrayList;
import java.util.List;

import org.alfresco.bm.cm.FolderData;
import org.alfresco.bm.dataload.RMBaseEventProcessor;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.session.SessionService;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

/**
 * Prepare event for unfiled record folders structure
 *
 * @author Silviu Dinuta
 * @since 2.6
 *
 */
public class ScheduleUnfiledRecordFolderLoaders extends RMBaseEventProcessor
{
    public static final String EVENT_NAME_SCHEDULE_LOADERS = "scheduleUnfiledFoldersLoaders";
    public static final String EVENT_NAME_LOADING_COMPLETE = "loadingUnfiledRecordFoldersComplete";

    @Autowired
    private SessionService sessionService;

    private int maxActiveLoaders;
    private int unfiledRecordFolderDepth;
    private int unfiledRecordFolderNumber;
    private boolean createUnfiledRecordFolderStructure;
    private int rootUnfiledRecordFolderNumber;
    private long loadCheckDelay;
    private int maxLevel;
    private String eventNameScheduleLoaders = EVENT_NAME_SCHEDULE_LOADERS;
    private String eventNameLoadingComplete = EVENT_NAME_LOADING_COMPLETE;
    private String eventNameLoadRootUnfiledRecordFolder = "loadRootUnfiledRecordFolder";
    private String eventNameLoadUnfiledRecordFolder = "loadUnfiledRecordFolder";
    private Integer rootUnfiledRecordFoldersToLoad = null;

    public void setMaxActiveLoaders(int maxActiveLoaders)
    {
        this.maxActiveLoaders = maxActiveLoaders;
    }

    public void setUnfiledRecordFolderDepth(int unfiledRecordFolderDepth)
    {
        this.unfiledRecordFolderDepth = unfiledRecordFolderDepth;
        this.maxLevel = this.unfiledRecordFolderDepth + 4;      // Add levels for "/Sites<L1>/siteId<L2>/documentLibrary<L3>/Unfiled Records<L4>"
    }

    public int getMaxLevel()
    {
        return maxLevel;
    }

    public void setUnfiledRecordFolderNumber(int unfiledRecordFolderNumber)
    {
        this.unfiledRecordFolderNumber = unfiledRecordFolderNumber;
    }

    public void setCreateUnfiledRecordFolderStructure(boolean createUnfiledRecordFolderStructure)
    {
        this.createUnfiledRecordFolderStructure = createUnfiledRecordFolderStructure;
    }

    public void setRootUnfiledRecordFolderNumber(int rootUnfiledRecordFolderNumber)
    {
        this.rootUnfiledRecordFolderNumber = rootUnfiledRecordFolderNumber;
    }

    public void setLoadCheckDelay(long loadCheckDelay)
    {
        this.loadCheckDelay = loadCheckDelay;
    }

    public String getEventNameScheduleLoaders()
    {
        return eventNameScheduleLoaders;
    }

    /**
     * Override the {@link #EVENT_NAME_SCHEDULE_LOADERS default} output event name
     */
    public void setEventNameScheduleLoaders(String eventNameScheduleLoaders)
    {
        this.eventNameScheduleLoaders = eventNameScheduleLoaders;
    }

    public String getEventNameLoadingComplete()
    {
        return eventNameLoadingComplete;
    }

    /**
     * Override the {@link #EVENT_NAME_LOADING_COMPLETE default} output event name
     */
    public void setEventNameLoadingComplete(String eventNameLoadingComplete)
    {
        this.eventNameLoadingComplete = eventNameLoadingComplete;
    }

    public String getEventNameLoadRootUnfiledRecordFolder()
    {
        return eventNameLoadRootUnfiledRecordFolder;
    }

    public void setEventNameLoadRootUnfiledRecordFolder(String eventNameLoadRootUnfiledRecordFolder)
    {
        this.eventNameLoadRootUnfiledRecordFolder = eventNameLoadRootUnfiledRecordFolder;
    }

    public String getEventNameLoadUnfiledRecordFolder()
    {
        return eventNameLoadUnfiledRecordFolder;
    }

    public void setEventNameLoadUnfiledRecordFolder(String eventNameLoadUnfiledRecordFolder)
    {
        this.eventNameLoadUnfiledRecordFolder = eventNameLoadUnfiledRecordFolder;
    }

    @Override
    protected EventResult processEvent(Event arg0) throws Exception
    {
        // Are there still sessions active?
        long sessionCount = sessionService.getActiveSessionsCount();
        int loaderSessionsToCreate = maxActiveLoaders - (int) sessionCount;
        List<Event> nextEvents = new ArrayList<Event>(maxActiveLoaders);

        // Do we actually need to do anything
        if (!createUnfiledRecordFolderStructure)
        {
            return new EventResult("Unfiled Record Folders structure creation not wanted, continue with loading data.", new Event(getEventNameLoadingComplete(), null));
        }
        if(unfiledRecordFolderDepth > 0)
        {
            //Load root unfiled record folders
            prepareRootUnfiledRecordFolders(loaderSessionsToCreate, nextEvents);
        }

        if(unfiledRecordFolderDepth > 1)
        {
            //Load unfiled record folder children
            prepareUnfiledRecordFolders(loaderSessionsToCreate, nextEvents);
        }
        // If there are no events, then we have finished
        String msg = null;
        if (loaderSessionsToCreate > 0 && nextEvents.isEmpty())
        {
            rootUnfiledRecordFoldersToLoad = null;
            auxFileFolderService.drop();
            // There are no files or folders to load even though there are sessions available
            Event nextEvent = new Event(getEventNameLoadingComplete(), null);
            nextEvents.add(nextEvent);
            msg = "Loading completed.  Raising 'done' event.";
        }
        else
        {
            // Reschedule self
            Event nextEvent = new Event(getEventNameScheduleLoaders(), System.currentTimeMillis() + loadCheckDelay, null);
            nextEvents.add(nextEvent);
            msg = "Raised further " + (nextEvents.size() - 1) + " events and rescheduled self.";
        }

        if (eventProcessorLogger.isDebugEnabled())
        {
            eventProcessorLogger.debug(msg);
        }

        EventResult result = new EventResult(msg, nextEvents);
        return result;
    }

    /**
     * Helper method for preparing the events that load the root unfiled record folders.
     *
     * @param loaderSessionsToCreate - the number of still active loader sessions
     * @param nextEvents - list of prepared events
     */
    private void prepareRootUnfiledRecordFolders(int loaderSessionsToCreate, List<Event> nextEvents)
    {
        //get unfiledRecordContainer folder
        FolderData unfiledRecordContainer = fileFolderService.getFolder(UNFILED_CONTEXT, UNFILED_RECORD_CONTAINER_PATH);
        if(rootUnfiledRecordFoldersToLoad == null)
        {
            rootUnfiledRecordFoldersToLoad = Math.max(rootUnfiledRecordFolderNumber - (int)unfiledRecordContainer.getFolderCount(), 0);
        }
        while (nextEvents.size() < loaderSessionsToCreate)
        {
            if (rootUnfiledRecordFoldersToLoad == 0)
            {
                // No root unfiled record folders needed
                break;
            }
            // Schedule a load for each folder
            for (int i = 0; i < rootUnfiledRecordFoldersToLoad; i++)
            {
                DBObject loadData = BasicDBObjectBuilder.start()
                            .add(FIELD_CONTEXT, unfiledRecordContainer.getContext())
                            .add(FIELD_PATH, unfiledRecordContainer.getPath())
                            .add(FIELD_LOAD_OPERATION, LOAD_ROOT_UNFILED_RECORD_FOLDER_OPERATION)
                            .get();
                Event loadEvent = new Event(getEventNameLoadRootUnfiledRecordFolder(), loadData);
                // Each load event must be associated with a session
                String sessionId = sessionService.startSession(loadData);
                loadEvent.setSessionId(sessionId);
                // Add the event to the list
                nextEvents.add(loadEvent);
                rootUnfiledRecordFoldersToLoad--;

                // Check if we have enough
                if (nextEvents.size() >= loaderSessionsToCreate)
                {
                    break;
                }
            }
        }
    }

    /**
     * Helper method for preparing the load of unfiled record folders children.
     *
     * @param loaderSessionsToCreate - the number of still active loader sessions
     * @param nextEvents - list of prepared events
     */
    private void prepareUnfiledRecordFolders(int loaderSessionsToCreate, List<Event> nextEvents)
    {
        int skip = 0;
        int limit = 100;
        while (nextEvents.size() < loaderSessionsToCreate)
        {
            // Get folders needing loading
            List<FolderData> emptyFolders = fileFolderService.getFoldersByCounts(
                    UNFILED_CONTEXT,
                    Long.valueOf(UNFILED_RECORD_CONTAINER_LEVEL+1),//min level is 5, level of root unfiled record folders
                    Long.valueOf(maxLevel-1),//max level is 4+unfiledRecordFolderDepth-1
                    0L, (long) (unfiledRecordFolderNumber - 1),//limit the maximum number of child folders to rootUnfiledRecordFolderNumber - 1
                    null, null,                                 // Ignore file limits
                    skip, limit);
            if (emptyFolders.isEmpty())
            {
                // The folders were populated in the mean time
                break;
            }
            // Schedule a load for each folder
            for (FolderData emptyFolder : emptyFolders)
            {
                FolderData folder = auxFileFolderService.getFolder(emptyFolder.getContext(), emptyFolder.getPath());
                if (folder == null)
                {
                    auxFileFolderService.createNewFolder(emptyFolder.getId(), emptyFolder.getContext(), emptyFolder.getPath());
                    int toCreateFolders = unfiledRecordFolderNumber - (int) emptyFolder.getFolderCount();
                    auxFileFolderService.incrementFolderCount(emptyFolder.getContext(), emptyFolder.getPath(), toCreateFolders);
                    folder = auxFileFolderService.getFolder(emptyFolder.getId());
                }
                int foldersToCreate  = (int) folder.getFolderCount();
                DBObject loadData = BasicDBObjectBuilder.start()
                            .add(FIELD_CONTEXT, emptyFolder.getContext())
                            .add(FIELD_PATH, emptyFolder.getPath())
                            .add(FIELD_LOAD_OPERATION, LOAD_UNFILED_RECORD_FOLDER_OPERATION)
                            .get();
                int j;
                for(j = 0; j < foldersToCreate; j++)
                {
                    Event loadEvent = new Event(getEventNameLoadUnfiledRecordFolder(), loadData);
                    // Each load event must be associated with a session
                    String recordFolderSessionId = sessionService.startSession(loadData);
                    loadEvent.setSessionId(recordFolderSessionId);
                    // Add the event to the list
                    nextEvents.add(loadEvent);
                    // Check if we have enough
                    if (nextEvents.size() >= loaderSessionsToCreate)
                    {
                        break;
                    }
                }
                if(j == foldersToCreate)
                {
                    auxFileFolderService.incrementFolderCount(emptyFolder.getContext(), emptyFolder.getPath(), -foldersToCreate);
                }
                else
                {
                    auxFileFolderService.incrementFolderCount(emptyFolder.getContext(), emptyFolder.getPath(), -j -1);
                }

                // Check if we have enough
                if (nextEvents.size() >= loaderSessionsToCreate)
                {
                    break;
                }
            }
            skip += limit;
        }
    }
}
