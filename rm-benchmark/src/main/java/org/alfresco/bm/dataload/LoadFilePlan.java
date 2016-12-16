package org.alfresco.bm.dataload;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

import org.alfresco.bm.cm.FolderData;
import org.alfresco.bm.event.Event;
import org.alfresco.bm.event.EventResult;
import org.alfresco.bm.restapi.RestAPIFactory;
import org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponent;
import org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponentType;
import org.alfresco.rest.rm.community.requests.FilePlanComponentAPI;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class LoadFilePlan extends RmBaseEventProcessor
{

    public static final long DEFAULT_LOAD_FILEPLAN_DELAY = 100L;
    public static final int DEFAULT_ROOT_CATEGORY_NUMBER = 5;
    public static final int DEFAULT_AVERAGE_CATEGORY_DEPTH = 4;
    public static final int DEFAULT_AVERAGE_CATEGORY_VARIANCE = 2;
    private long loadFilePlanDelay = DEFAULT_LOAD_FILEPLAN_DELAY;

    private String eventNameRecordCategoryLoaded;

    @Autowired
    private RestAPIFactory restAPIFactory;


    public String getEventNameRecordCategoryLoaded()
    {
        return eventNameRecordCategoryLoaded;
    }

    public void setEventNameRecordCategoryLoaded(String eventNameRecordCategoryLoaded)
    {
        this.eventNameRecordCategoryLoaded = eventNameRecordCategoryLoaded;
    }

    public long getLoadFilePlanDelay()
    {
        return loadFilePlanDelay;
    }

    /**
     * Override the {@link #DEFAULT_LOAD_FILEPLAN_DELAY default} time between creation requests
     */
    public void setLoadFilePlanDelay(long loadFilePlanDelay)
    {
        this.loadFilePlanDelay = loadFilePlanDelay;
    }

    @Override
    public EventResult processEvent(Event event) throws Exception
    {
        super.suspendTimer();

        if (event == null)
        {
            throw new IllegalStateException("This processor requires an event.");
        }

        DBObject dataObj = (DBObject) event.getData();
        if (dataObj == null)
        {
            throw new IllegalStateException("This processor requires data with field " + FIELD_PATH);
        }

        String context = (String) dataObj.get(FIELD_CONTEXT);
        String path = (String) dataObj.get(FIELD_PATH);
        Integer rootCategoriesToCreate = (Integer) dataObj.get(FIELD_ROOT_CATEGORIES_TO_CREATE);
        Integer categoriesToCreate = (Integer) dataObj.get(FIELD_CATEGORIES_TO_CREATE);
        Integer foldersToCreate = (Integer) dataObj.get(FIELD_FOLDERS_TO_CREATE);
        String siteManager = (String) dataObj.get(FIELD_SITE_MANAGER);
        if (context == null || path == null || foldersToCreate == null || categoriesToCreate == null
                    || rootCategoriesToCreate == null || isBlank(siteManager))
        {
            return new EventResult("Request data not complete for folder loading: " + dataObj, false);
        }

        // Get the folder
        FolderData folder = fileFolderService.getFolder(context, path);
        if (folder == null)
        {
            throw new IllegalStateException("No such folder recorded: " + dataObj);
        }
        // Get the session
        String sessionId = event.getSessionId();
        if (sessionId == null)
        {
            return new EventResult("Load scheduling should create a session for each loader.", false);
        }

        return loadCategory(folder, rootCategoriesToCreate, categoriesToCreate, foldersToCreate, siteManager);
    }

    private EventResult loadCategory(FolderData container, int rootCategoriesToCreate, int categoriesToCreate,
                int foldersToCreate, String siteManager) throws IOException
    {
        FilePlanComponentAPI api = restAPIFactory.getFilePlanComponentAPI(siteManager);
        api.setParameters("include=path");

        try
        {
            List<Event> scheduleEvents = new ArrayList<Event>();
            FilePlanComponent filePlanComponent = api.getFilePlanComponent(container.getId());

            // Create root categories
            if(rootCategoriesToCreate > 0)
            {
                super.resumeTimer();
                createFilePlanComponent(container, api, filePlanComponent, rootCategoriesToCreate,
                            ROOT_CATEGORY_NAME_IDENTIFIER,
                            FilePlanComponentType.RECORD_CATEGORY_TYPE.toString(), RECORD_CATEGORY_CONTEXT, loadFilePlanDelay);
                super.suspendTimer();
                String lockedPath = container.getPath() + "/locked";
                fileFolderService.deleteFolder(container.getContext(), lockedPath, false);
            }

            // Create categories
            if(categoriesToCreate > 0)
            {
                super.resumeTimer();
                createFilePlanComponent(container, api, filePlanComponent, categoriesToCreate,
                            CATEGORY_NAME_IDENTIFIER,
                            FilePlanComponentType.RECORD_CATEGORY_TYPE.toString(), RECORD_CATEGORY_CONTEXT, loadFilePlanDelay);
                super.suspendTimer();
                String lockedPath = container.getPath() + "/locked";
                fileFolderService.deleteFolder(container.getContext(), lockedPath, false);
            }

            // Create folders
            if(foldersToCreate > 0)
            {
                super.resumeTimer();
                createFilePlanComponent(container, api, filePlanComponent, foldersToCreate,
                            RECORD_FOLDER_NAME_IDENTIFIER,
                            FilePlanComponentType.RECORD_FOLDER_TYPE.toString(), RECORD_FOLDER_CONTEXT, loadFilePlanDelay);
                super.suspendTimer();
                String lockedPath = container.getPath() + "/locked";
                fileFolderService.deleteFolder(container.getContext(), lockedPath, false);
            }

            DBObject eventData = BasicDBObjectBuilder.start()
                        .add(FIELD_CONTEXT, container.getContext())
                        .add(FIELD_PATH, container.getPath()).get();
            Event event = new Event(eventNameRecordCategoryLoaded,eventData);
            scheduleEvents.add(event);

            DBObject resultData = BasicDBObjectBuilder.start()
                        .add("msg", "Created " + rootCategoriesToCreate + " root categories, " + categoriesToCreate + " categories and " + foldersToCreate
                                    + " record folders.")
                        .add("path", container.getPath())
                        .add("username", siteManager).get();

            return new EventResult(resultData, scheduleEvents);
        }
        catch (Exception e)
        {
            String error = e.getMessage();
            String stack = ExceptionUtils.getStackTrace(e);
            // Grab REST API information
            DBObject data = BasicDBObjectBuilder.start().append("error", error).append("username", siteManager)
                        .append("folder", container).append("stack", stack).get();
            // Build failure result
            return new EventResult(data, false);
        }
    }
}