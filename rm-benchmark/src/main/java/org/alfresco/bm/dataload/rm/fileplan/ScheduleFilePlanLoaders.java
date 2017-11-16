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

import org.alfresco.bm.cm.FolderData;
import org.alfresco.bm.dataload.RMBaseEventProcessor;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.session.SessionService;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

/**
 * Prepare event for loading root categories, record categories and record folders.
 *
 * @author Silviu Dinuta
 * @since 2.6
 */
public class ScheduleFilePlanLoaders extends RMBaseEventProcessor
{
    public static final String EVENT_NAME_SCHEDULE_LOADERS = "scheduleFilePlanLoaders";
    public static final String EVENT_NAME_LOADING_COMPLETE = "filePlanloadingComplete";
    public static final String EVENT_NAME_LOAD_ROOT_RECORD_CATEGORY = "loadRootRecordCategory";
    public static final String EVENT_NAME_LOAD_SUB_CATEGORY = "loadSubCategory";
    public static final String EVENT_NAME_LOAD_RECORD_FOLDER = "loadRecordFolder";

    @Autowired
    private SessionService sessionService;

    private boolean createFileplanFolderStructure;
    private int maxActiveLoaders;
    private long loadCheckDelay;
    private int childCategNumber;
    private int folderNumber;
    private int categoryStructureDepth;
    private int maxLevel;
    private int categoryNumber;
    private boolean folderCategoryMix;

    private String eventNameScheduleLoaders = EVENT_NAME_SCHEDULE_LOADERS;
    private String eventNameLoadingComplete = EVENT_NAME_LOADING_COMPLETE;
    private String eventNameLoadRootRecordCategory = EVENT_NAME_LOAD_ROOT_RECORD_CATEGORY;
    private String eventNameLoadSubCategory = EVENT_NAME_LOAD_SUB_CATEGORY;
    private String eventNameLoadRecordFolder = EVENT_NAME_LOAD_RECORD_FOLDER;
    private Integer rootCategoriesToLoad = null;
    private Integer maxChildren = null;

    public void setCreateFileplanFolderStructure(boolean createFileplanFolderStructure)
    {
        this.createFileplanFolderStructure = createFileplanFolderStructure;
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

    public String getEventNameLoadRootRecordCategory()
    {
        return eventNameLoadRootRecordCategory;
    }

    public void setEventNameLoadRootRecordCategory(String eventNameLoadRootRecordCategory)
    {
        this.eventNameLoadRootRecordCategory = eventNameLoadRootRecordCategory;
    }

    public String getEventNameLoadSubCategory()
    {
        return eventNameLoadSubCategory;
    }

    public void setEventNameLoadSubCategory(String eventNameLoadSubCategory)
    {
        this.eventNameLoadSubCategory = eventNameLoadSubCategory;
    }

    public String getEventNameLoadRecordFolder()
    {
        return eventNameLoadRecordFolder;
    }

    public void setEventNameLoadRecordFolder(String eventNameLoadRecordFolder)
    {
        this.eventNameLoadRecordFolder = eventNameLoadRecordFolder;
    }

    /**
     * @param maxActiveLoaders the maxActiveLoaders to set
     */
    public void setMaxActiveLoaders(int maxActiveLoaders)
    {
        this.maxActiveLoaders = maxActiveLoaders;
    }

    /**
     * @param loadCheckDelay the loadCheckDelay to set
     */
    public void setLoadCheckDelay(long loadCheckDelay)
    {
        this.loadCheckDelay = loadCheckDelay;
    }

    /**
     * @param childCategNumber the childCategNumber to set
     */
    public void setChildCategNumber(int childCategNumber)
    {
        this.childCategNumber = childCategNumber;
    }

    /**
     * @param folderNumberAverage the folderNumber to set
     */
    public void setFolderNumber(int folderNumberAverage)
    {
        this.folderNumber = folderNumberAverage;
    }

    /**
     * @param categoryNumber the categoryNumber to set
     */
    public void setCategoryNumber(int categoryNumber)
    {
        this.categoryNumber = categoryNumber;
    }

    /**
     * @param folderCategoryMix the folderCategoryMix to set
     */
    public void setFolderCategoryMix(boolean folderCategoryMix)
    {
        this.folderCategoryMix = folderCategoryMix;
    }

    /**
     * @param categoryStructureDepth the filePlanDepth to set
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

        // Do we actually need to do anything
        if (!createFileplanFolderStructure)
        {
            return new EventResult("FilePlan folders structure creation not wanted, continue with loading data.",  new Event(eventNameLoadingComplete, null));
        }

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
        if (loaderSessionsToCreate > 0 && nextEvents.isEmpty())
        {
            rootCategoriesToLoad = null;
            maxChildren = null;
            auxFileFolderService.drop();
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

    /**
     * Helper method for preparing the events that load the root record categories.
     *
     * @param loaderSessionsToCreate - the number of still active loader sessions
     * @param nextEvents - list of prepared events
     */
    private void prepareRootCategories(int loaderSessionsToCreate, List<Event> nextEvents)
    {
        //get filePlan folder
        FolderData filePlan = fileFolderService.getFolder(FILEPLAN_CONTEXT, RECORD_CONTAINER_PATH);
        if(rootCategoriesToLoad == null)
        {
            rootCategoriesToLoad = Math.max(categoryNumber - (int)filePlan.getFolderCount(), 0);
        }

        while (nextEvents.size() < loaderSessionsToCreate)
        {
            if (rootCategoriesToLoad == 0)
            {
                // No root record categories needed
                break;
            }
            for (int i = 0; i < rootCategoriesToLoad; i++)
            {
                DBObject loadData = BasicDBObjectBuilder.start()
                            .add(FIELD_CONTEXT, filePlan.getContext())
                            .add(FIELD_PATH, filePlan.getPath())
                            .add(FIELD_LOAD_OPERATION, LOAD_ROOT_CATEGORY_OPERATION)
                            .get();
                Event loadEvent = new Event(getEventNameLoadRootRecordCategory(), loadData);
                // Each load event must be associated with a session
                String sessionId = sessionService.startSession(loadData);
                loadEvent.setSessionId(sessionId);
                // Add the event to the list
                nextEvents.add(loadEvent);
                rootCategoriesToLoad--;

                // Check if we have enough
                if (nextEvents.size() >= loaderSessionsToCreate)
                {
                    break;
                }
            }
        }
    }

    /**
     * Helper method for preparing the load events for record categories children and record folders children without the last level of record folders.
     *
     * @param loaderSessionsToCreate - the number of still active loader sessions
     * @param nextEvents - list of prepared events
     */
    private void prepareSubCategoriesAndRecordFolders(int loaderSessionsToCreate, List<Event> nextEvents)
    {
        int skip = 0;
        int limit = 100;
        while (nextEvents.size() < loaderSessionsToCreate)
        {
            // Get categories needing loading
            // the maximum number of children a folder should contain so that it will be picked up for further loading
            calculateMaxChildren();
            List<FolderData> emptyFolders = fileFolderService.getFoldersByCounts(
                        RECORD_CATEGORY_CONTEXT,
                        Long.valueOf(FILE_PLAN_LEVEL + 1),//min level FILE_PLAN_LEVEL + 1 = 4, root categories
                        Long.valueOf(maxLevel -1),//last level will be for record folders, FILE_PLAN_LEVEL+depth-1 required
                        0L, Long.valueOf(maxChildren),//maximum number of sub folders so that it will be picked up for further loading
                        null, null,
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
                    int toCreateCateg = childCategNumber - getDirectChildrenByContext(emptyFolder, RECORD_CATEGORY_CONTEXT).size();
                    auxFileFolderService.incrementFolderCount(emptyFolder.getContext(), emptyFolder.getPath(), toCreateCateg);
                    if (this.folderCategoryMix)
                    {
                        int toCreateFolders = folderNumber - getDirectChildrenByContext(emptyFolder, RECORD_FOLDER_CONTEXT).size();
                        auxFileFolderService.incrementFileCount(emptyFolder.getContext(), emptyFolder.getPath(), toCreateFolders);
                    }
                    folder = auxFileFolderService.getFolder(emptyFolder.getId());
                }
                int categoriesToCreate = (int) folder.getFolderCount();

                DBObject loadData = BasicDBObjectBuilder.start().add(FIELD_CONTEXT, emptyFolder.getContext())
                            .add(FIELD_PATH, emptyFolder.getPath())
                            .add(FIELD_LOAD_OPERATION, LOAD_SUB_CATEGORY_OPERATION)
                            .get();
                int i;
                for(i = 0; i < categoriesToCreate; i++)
                {
                    Event loadEvent = new Event(getEventNameLoadSubCategory(), loadData);
                    // Each load event must be associated with a session
                    String sessionId = sessionService.startSession(loadData);
                    loadEvent.setSessionId(sessionId);
                    // Add the event to the list
                    nextEvents.add(loadEvent);
                    // Check if we have enough
                    if (nextEvents.size() >= loaderSessionsToCreate)
                    {
                        break;
                    }
                }
                if(i == categoriesToCreate)
                {
                    auxFileFolderService.incrementFolderCount(emptyFolder.getContext(), emptyFolder.getPath(), -categoriesToCreate);
                }
                else
                {
                    auxFileFolderService.incrementFolderCount(emptyFolder.getContext(), emptyFolder.getPath(), - i-1);
                }

                if (this.folderCategoryMix)
                {
                    int foldersToCreate  = (int) folder.getFileCount();
                    DBObject loadRecordFolderData = BasicDBObjectBuilder.start().add(FIELD_CONTEXT, emptyFolder.getContext())
                                .add(FIELD_PATH, emptyFolder.getPath())
                                .add(FIELD_LOAD_OPERATION, LOAD_RECORD_FOLDER_OPERATION)
                                .get();
                    int j;
                    for(j = 0; j < foldersToCreate; j++)
                    {
                        Event loadRecordFolderEvent = new Event(getEventNameLoadRecordFolder(), loadRecordFolderData);
                        // Each load event must be associated with a session
                        String recordFolderSessionId = sessionService.startSession(loadRecordFolderData);
                        loadRecordFolderEvent.setSessionId(recordFolderSessionId);
                        // Add the event to the list
                        nextEvents.add(loadRecordFolderEvent);
                        // Check if we have enough
                        if (nextEvents.size() >= loaderSessionsToCreate)
                        {
                            break;
                        }
                    }
                    if(j == foldersToCreate)
                    {
                        auxFileFolderService.incrementFileCount(emptyFolder.getContext(), emptyFolder.getPath(), -foldersToCreate);
                    }
                    else
                    {
                        auxFileFolderService.incrementFileCount(emptyFolder.getContext(), emptyFolder.getPath(), -j -1);
                    }
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

    /**
     * Helper method for preparing the load events for record folders children from the last level.
     *
     * @param loaderSessionsToCreate - the number of still active loader sessions
     * @param nextEvents - list of prepared events
     */
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
                    int toCreateFolders = folderNumber - getDirectChildrenByContext(emptyFolder, RECORD_FOLDER_CONTEXT).size();
                    auxFileFolderService.incrementFileCount(emptyFolder.getContext(), emptyFolder.getPath(), toCreateFolders);
                    folder = auxFileFolderService.getFolder(emptyFolder.getId());
                }
                int foldersToCreate  = (int) folder.getFileCount();
                DBObject loadRecordFolderData = BasicDBObjectBuilder.start().add(FIELD_CONTEXT, emptyFolder.getContext())
                            .add(FIELD_PATH, emptyFolder.getPath())
                            .add(FIELD_LOAD_OPERATION, LOAD_RECORD_FOLDER_OPERATION)
                            .get();
                int j;
                for(j = 0; j < foldersToCreate; j++)
                {
                    Event loadRecordFolderEvent = new Event(getEventNameLoadRecordFolder(), loadRecordFolderData);
                    // Each load event must be associated with a session
                    String recordFolderSessionId = sessionService.startSession(loadRecordFolderData);
                    loadRecordFolderEvent.setSessionId(recordFolderSessionId);
                    // Add the event to the list
                    nextEvents.add(loadRecordFolderEvent);
                    // Check if we have enough
                    if (nextEvents.size() >= loaderSessionsToCreate)
                    {
                        break;
                    }
                }
                if(j == foldersToCreate)
                {
                    auxFileFolderService.incrementFileCount(emptyFolder.getContext(), emptyFolder.getPath(), -foldersToCreate);
                }
                else
                {
                    auxFileFolderService.incrementFileCount(emptyFolder.getContext(), emptyFolder.getPath(), -j -1);
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

    /**
     * Helper method to calculate maximum children that one category should have in order to be returned in search result for categories that still need sub-categories and record folders.
     */
    private void calculateMaxChildren()
    {
        if (maxChildren == null)
        {
            //get one category to check existing number of categories and record folders
            List<FolderData> categories = fileFolderService.getFoldersByCounts(
                        RECORD_CATEGORY_CONTEXT,
                        null, null,//ignore levels
                        null, null,//ignore folder limits
                        null, null, // Ignore file limits
                        0, 1);
            if (folderCategoryMix)
            {
                maxChildren = folderNumber + childCategNumber - 1;
                if (!categories.isEmpty())
                {
                    FolderData existentRecordCategory = categories.get(0);
                    int childCategoriesNumber = getDirectChildrenByContext(existentRecordCategory, RECORD_CATEGORY_CONTEXT).size();
                    int childRecordFoldersNumber = getDirectChildrenByContext(existentRecordCategory, RECORD_FOLDER_CONTEXT).size();
                    maxChildren = Math.max(folderNumber, childRecordFoldersNumber) + Math.max(childCategNumber, childCategoriesNumber) - 1;
                }
            }
            else
            {
                maxChildren = childCategNumber - 1;
                if (!categories.isEmpty())
                {
                    FolderData existentRecordCategory = categories.get(0);
                    int childCategoriesNumber = getDirectChildrenByContext(existentRecordCategory, RECORD_CATEGORY_CONTEXT).size();
                    int folderCount = (int)existentRecordCategory.getFolderCount();
                    maxChildren = Math.max(folderCount, folderCount + childCategNumber - childCategoriesNumber) -1;
                }
            }
        }
    }
}
