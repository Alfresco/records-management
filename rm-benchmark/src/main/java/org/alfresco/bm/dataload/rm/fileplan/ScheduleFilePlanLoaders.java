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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

import org.alfresco.bm.cm.FolderData;
import org.alfresco.bm.dataload.RMBaseEventProcessor;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.session.SessionService;
import org.springframework.beans.factory.annotation.Autowired;

public class ScheduleFilePlanLoaders extends RMBaseEventProcessor
{
    public static final String EVENT_NAME_LOAD_RECORD_CATEGORIES = "loadRecordCategories";
    public static final String EVENT_NAME_SCHEDULE_LOADERS = "scheduleFilePlanLoaders";
    public static final String EVENT_NAME_LOADING_COMPLETE = "scheduleUnfiledRecordFoldersLoaders";

    @Autowired
    private SessionService sessionService;

    private int maxActiveLoaders;
    private long loadCheckDelay;
    private int childCategNumber;
    private int folderNumber;
    private int categoryStructureDepth;
    private int maxLevel;
    private int categoryNumber;
    private int childCategNumberVariance;
    private boolean folderCategoryMix;
    private String username;

    private String eventNameLoadRecordCategories = EVENT_NAME_LOAD_RECORD_CATEGORIES;
    private String eventNameScheduleLoaders = EVENT_NAME_SCHEDULE_LOADERS;
    private String eventNameLoadingComplete = EVENT_NAME_LOADING_COMPLETE;

    /**
     * Override the {@link #EVENT_NAME_LOAD_SITE_FILES default} output event name
     */
    public void setEventNameLoadRecordCategories(String eventNameLoadSiteCategories)
    {
        this.eventNameLoadRecordCategories = eventNameLoadSiteCategories;
    }

    /**
     * Override the {@link #EVENT_NAME_SCHEDULE_LOADERS default} output event name
     */
    public void setEventNameScheduleLoaders(String eventNameScheduleLoaders)
    {
        this.eventNameScheduleLoaders = eventNameScheduleLoaders;
    }

    /**
     * Override the {@link #EVENT_NAME_LOADING_COMPLETE default} output event name
     */
    public void setEventNameLoadingComplete(String eventNameLoadingComplete)
    {
        this.eventNameLoadingComplete = eventNameLoadingComplete;
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
     * @return the childCategNumber
     */
    public int getChildCategNumber()
    {
        return childCategNumber;
    }

    /**
     * @param childCategNumber the childCategNumber to set
     */
    public void setChildCategNumber(int childCategNumber)
    {
        this.childCategNumber = childCategNumber;
    }

    /**
     * @return the folderNumber
     */
    public int getFolderNumber()
    {
        return folderNumber;
    }

    /**
     * @param folderNumber the folderNumber to set
     */
    public void setFolderNumber(int folderNumberAverage)
    {
        this.folderNumber = folderNumberAverage;
    }

    /**
     * @return the categoryNumber
     */
    public int getCategoryNumber()
    {
        return categoryNumber;
    }

    /**
     * @param categoryNumber the categoryNumber to set
     */
    public void setCategoryNumber(int categoryNumber)
    {
        this.categoryNumber = categoryNumber;
    }

    /**
     * @return the childCategNumberVariance
     */
    public int getChildCategNumberVariance()
    {
        return childCategNumberVariance;
    }

    /**
     * @return the folderCategoryMix
     */
    public boolean isFolderCategoryMix()
    {
        return folderCategoryMix;
    }

    /**
     * @param folderCategoryMix the folderCategoryMix to set
     */
    public void setFolderCategoryMix(boolean folderCategoryMix)
    {
        this.folderCategoryMix = folderCategoryMix;
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
     * @return the filePlanDepth
     */
    public int getCategoryStructureDepth()
    {
        return categoryStructureDepth;
    }

    /**
     * @param filePlanDepth the filePlanDepth to set
     */
    public void setCategoryStructureDepth(int categoryStructureDepth)
    {
        this.categoryStructureDepth = categoryStructureDepth;
        this.maxLevel = this.categoryStructureDepth + 3;      // Add levels for "/Sites<L1>/siteId<L2>/documentLibrary<L3>/"
    }

    /**
     * @return maxLevel
     */
    public int getMaxLevel()
    {
        return maxLevel;
    }

    @Override
    public EventResult processEvent(Event event) throws Exception
    {
        // Are there still sessions active?
        long sessionCount = sessionService.getActiveSessionsCount();
        int loaderSessionsToCreate = maxActiveLoaders - (int) sessionCount;

        List<Event> nextEvents = new ArrayList<Event>(maxActiveLoaders);

        //load root categories
        if(categoryStructureDepth > 0)
        {
            prepareRootCategories(loaderSessionsToCreate, nextEvents);

            if(categoryStructureDepth > 1)
            {
                // Target categories that need subcategories and optionally, record folders
                prepareSubCategoriesAndRecordFolders(loaderSessionsToCreate, nextEvents);
            }

            // Load folders into categories on the lowest level - folder only loading
            prepareRecordFoldersOnLowestLevel(loaderSessionsToCreate, nextEvents);

        }

        // If there are no events, then we have finished
        String msg = null;
        if (loaderSessionsToCreate > 0 && nextEvents.size() == 0)
        {
            // There are no files or folders to load even though there are sessions available
            Event nextEvent = new Event(eventNameLoadingComplete, null);
            nextEvents.add(nextEvent);
            msg = "Loading completed.  Raising 'done' event.";
        }
        else
        {
            // Reschedule self
            Event nextEvent = new Event(eventNameScheduleLoaders, System.currentTimeMillis() + loadCheckDelay, null);
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

    private void prepareRootCategories(int loaderSessionsToCreate, List<Event> nextEvents)
    {
        int skip = 0;
        int limit = 100;
        while (nextEvents.size() < loaderSessionsToCreate)
        {
            // Get categories needing loading
            List<FolderData> emptyFolders = fileFolderService.getFoldersByCounts(
                        "",
                        Long.valueOf(FILE_PLAN_LEVEL), Long.valueOf(FILE_PLAN_LEVEL),//we need only file plan level here since we load root categories on filePlan
                        0L, Long.valueOf((categoryNumber - 1)),//limit the maximum number of child folders to number of needed root categories - 1
                        null, null, // Ignore file limits
                        skip, limit);
            if (emptyFolders.size() == 0)
            {
                // The folders were populated in the mean time
                break;
            }
            // Schedule a load for each folder
            for (FolderData emptyFolder : emptyFolders)
            {
                int rootCategoriesToCreate = categoryNumber - (int)emptyFolder.getFolderCount();
                try
                {
                    // Create a lock folder that has too many files and folders so that it won't be picked up
                    // by this process in subsequent trawls
                    String lockPath = emptyFolder.getPath() + "/locked";
                    FolderData lockFolder = new FolderData(UUID.randomUUID().toString(), emptyFolder.getContext(), lockPath, Long.MAX_VALUE,
                                Long.MAX_VALUE);
                    fileFolderService.createNewFolder(lockFolder);
                    // We locked this, so the load can be scheduled.
                    // The loader will remove the lock when it completes
                    DBObject loadData = BasicDBObjectBuilder.start().add(FIELD_CONTEXT, emptyFolder.getContext())
                                .add(FIELD_PATH, emptyFolder.getPath())
                                .add(FIELD_ROOT_CATEGORIES_TO_CREATE, Integer.valueOf(rootCategoriesToCreate))
                                .add(FIELD_CATEGORIES_TO_CREATE, Integer.valueOf(0))
                                .add(FIELD_FOLDERS_TO_CREATE, Integer.valueOf(0))
                                .add(FIELD_SITE_MANAGER, username)
                                .get();
                    Event loadEvent = new Event(eventNameLoadRecordCategories, loadData);
                    // Each load event must be associated with a session
                    String sessionId = sessionService.startSession(loadData);
                    loadEvent.setSessionId(sessionId);
                    // Add the event to the list
                    nextEvents.add(loadEvent);
                }
                catch (Exception e)
                {
                    // The lock was already applied; find another
                    continue;
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

    private void prepareSubCategoriesAndRecordFolders(int loaderSessionsToCreate, List<Event> nextEvents)
    {
        int skip = 0;
        int limit = 100;
        while (nextEvents.size() < loaderSessionsToCreate)
        {
            // Get categories needing loading
            // the maximum number of children a folder should contain so that it will be picked up for further loading
            int maxChildren = folderNumber + childCategNumber - 1;
            List<FolderData> emptyFolders = fileFolderService.getFoldersByCounts(
                        RECORD_CATEGORY_CONTEXT,
                        Long.valueOf(FILE_PLAN_LEVEL + 1),//min level FILE_PLAN_LEVEL + 1 = 4, root categories
                        Long.valueOf(maxLevel -1),//last level will be for record folders, FILE_PLAN_LEVEL+depth-1 required
                        0L, Long.valueOf(maxChildren),//maximum number of sub folders so that it will be picked up for further loading
                        null, null,
                        skip, limit);
            if (emptyFolders.size() == 0)
            {
                // The folders were populated in the mean time
                break;
            }
            // Schedule a load for each folder
            for (FolderData emptyFolder : emptyFolders)
            {
                int categoryCount = fileFolderService.getChildFolders(RECORD_CATEGORY_CONTEXT, emptyFolder.getPath(), skip, limit).size();
                int folderCount = fileFolderService.getChildFolders(RECORD_FOLDER_CONTEXT, emptyFolder.getPath(), skip, limit).size();

                int categoriesToCreate = childCategNumber - categoryCount;

                int foldersToCreate = 0;
                if (this.folderCategoryMix)
                {
                    foldersToCreate = folderNumber - folderCount;
                }

                try
                {
                    // Create a lock folder that has too many files and folders so that it won't be picked up
                    // by this process in subsequent trawls
                    String lockPath = emptyFolder.getPath() + "/locked";
                    FolderData lockFolder = new FolderData(UUID.randomUUID().toString(), emptyFolder.getContext(), lockPath, Long.MAX_VALUE,
                                Long.MAX_VALUE);
                    fileFolderService.createNewFolder(lockFolder);
                    // We locked this, so the load can be scheduled.
                    // The loader will remove the lock when it completes
                    DBObject loadData = BasicDBObjectBuilder.start().add(FIELD_CONTEXT, emptyFolder.getContext())
                                .add(FIELD_PATH, emptyFolder.getPath())
                                .add(FIELD_ROOT_CATEGORIES_TO_CREATE, Integer.valueOf(0))
                                .add(FIELD_CATEGORIES_TO_CREATE, Integer.valueOf(categoriesToCreate))
                                .add(FIELD_FOLDERS_TO_CREATE, Integer.valueOf(foldersToCreate))
                                .add(FIELD_SITE_MANAGER, username)
                                .get();
                    Event loadEvent = new Event(eventNameLoadRecordCategories, loadData);
                    // Each load event must be associated with a session
                    String sessionId = sessionService.startSession(loadData);
                    loadEvent.setSessionId(sessionId);
                    // Add the event to the list
                    nextEvents.add(loadEvent);
                }
                catch (Exception e)
                {
                    // The lock was already applied; find another
                    continue;
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

    private void prepareRecordFoldersOnLowestLevel(int loaderSessionsToCreate, List<Event> nextEvents)
    {
        int skip = 0;
        int limit = 100;

        while (nextEvents.size() < loaderSessionsToCreate)
        {
            // Get categories needing loading
            List<FolderData> emptyFolders = fileFolderService.getFoldersByCounts(
                        RECORD_CATEGORY_CONTEXT,
                        Long.valueOf(maxLevel), Long.valueOf(maxLevel),//max and min level are FILE_PLAN_LEVEL+depth, of last category, where we load lowest level of record folders
                        0L, Long.valueOf(folderNumber - 1),//limit the maximum number of child folders to number of record folder to create - 1
                        null, null, // Ignore file limits
                        skip, limit);

            if (emptyFolders.size() == 0)
            {
                // The folders were populated in the mean time
                break;
            }
            // Schedule a load for each folder
            for (FolderData emptyFolder : emptyFolders)
            {
                int folderCount = fileFolderService.getChildFolders(RECORD_FOLDER_CONTEXT, emptyFolder.getPath(), skip, limit).size();
                int foldersToCreate = folderNumber - folderCount;

                try
                {
                    // Create a lock folder that has too many files and folders so that it won't be picked up
                    // by this process in subsequent trawls
                    String lockPath = emptyFolder.getPath() + "/locked";
                    FolderData lockFolder = new FolderData(
                                UUID.randomUUID().toString(),
                                emptyFolder.getContext(), lockPath,
                                Long.MAX_VALUE, Long.MAX_VALUE);
                    fileFolderService.createNewFolder(lockFolder);
                    // We locked this, so the load can be scheduled.
                    // The loader will remove the lock when it completes
                    DBObject loadData = BasicDBObjectBuilder.start()
                                .add(FIELD_CONTEXT, emptyFolder.getContext())
                                .add(FIELD_PATH, emptyFolder.getPath())
                                .add(FIELD_ROOT_CATEGORIES_TO_CREATE, Integer.valueOf(0))
                                .add(FIELD_CATEGORIES_TO_CREATE, Integer.valueOf(0))
                                .add(FIELD_FOLDERS_TO_CREATE, Integer.valueOf(foldersToCreate))
                                .add(FIELD_SITE_MANAGER, username)
                                .get();
                    Event loadEvent = new Event(eventNameLoadRecordCategories, loadData);
                    // Each load event must be associated with a session
                    String sessionId = sessionService.startSession(loadData);
                    loadEvent.setSessionId(sessionId);
                    // Add the event to the list
                    nextEvents.add(loadEvent);
                }
                catch (Exception e)
                {
                    // The lock was already applied; find another
                    continue;
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
