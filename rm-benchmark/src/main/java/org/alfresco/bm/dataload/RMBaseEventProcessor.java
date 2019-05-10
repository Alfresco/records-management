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

package org.alfresco.bm.dataload;

import static org.apache.commons.lang3.StringUtils.split;

import static org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponentType.CONTENT_TYPE;
import static org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponentType.NON_ELECTRONIC_RECORD_TYPE;
import static org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponentType.UNFILED_RECORD_FOLDER_TYPE;
import static org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponentType.RECORD_CATEGORY_TYPE;
import static org.alfresco.rest.rm.community.model.fileplancomponents.FilePlanComponentType.RECORD_FOLDER_TYPE;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.alfresco.bm.cm.FileFolderService;
import org.alfresco.bm.cm.FolderData;
import org.alfresco.bm.data.DataCreationState;
import org.alfresco.bm.dataload.rm.role.RMRole;
import org.alfresco.bm.dataload.rm.services.ExecutionState;
import org.alfresco.bm.dataload.rm.services.ExtendedFileFolderService;
import org.alfresco.bm.dataload.rm.services.RecordContext;
import org.alfresco.bm.dataload.rm.services.RecordData;
import org.alfresco.bm.dataload.rm.services.RecordService;
import org.alfresco.bm.event.AbstractEventProcessor;
import org.alfresco.bm.file.TestFileService;
import org.alfresco.bm.site.SiteData;
import org.alfresco.bm.site.SiteDataService;
import org.alfresco.bm.site.SiteMemberData;
import org.alfresco.bm.user.UserData;
import org.alfresco.bm.user.UserDataService;
import org.alfresco.rest.core.RestAPIFactory;
import org.alfresco.rest.rm.community.model.record.Record;
import org.alfresco.rest.rm.community.model.record.RecordProperties;
import org.alfresco.rest.rm.community.model.recordcategory.RecordCategory;
import org.alfresco.rest.rm.community.model.recordcategory.RecordCategoryChild;
import org.alfresco.rest.rm.community.model.recordcategory.RecordCategoryChildProperties;
import org.alfresco.rest.rm.community.model.recordcategory.RecordCategoryProperties;
import org.alfresco.rest.rm.community.model.unfiledcontainer.UnfiledContainerChild;
import org.alfresco.rest.rm.community.model.unfiledcontainer.UnfiledContainerChildProperties;
import org.alfresco.rest.rm.community.requests.gscore.api.FilePlanAPI;
import org.alfresco.rest.rm.community.requests.gscore.api.RecordCategoryAPI;
import org.alfresco.rest.rm.community.requests.gscore.api.RecordFolderAPI;
import org.alfresco.rest.rm.community.requests.gscore.api.UnfiledContainerAPI;
import org.alfresco.rest.rm.community.requests.gscore.api.UnfiledRecordFolderAPI;
import org.alfresco.utility.model.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpStatus;

/**
 * Base helper class for RM events.
 *
 * @author Silviu Dinuta
 * @since 2.6
 *
 */
public abstract class RMBaseEventProcessor extends AbstractEventProcessor implements RMEventConstants, ApplicationContextAware
{
    /** Resource for derived classes to use for logging */
    protected Logger eventProcessorLogger = LoggerFactory.getLogger(this.getClass());
    protected FileFolderService fileFolderService;
    protected ExtendedFileFolderService auxFileFolderService;
    protected TestFileService testFileService;
    private ApplicationContext applicationContext;

    @Autowired
    protected UserDataService userDataService;

    @Autowired
    protected SiteDataService siteDataService;

    @Autowired
    protected RecordService recordService;

    public RestAPIFactory getRestAPIFactory()
    {
        return applicationContext.getBean("restAPIFactory", RestAPIFactory.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    public void setFileFolderService(FileFolderService fileFolderService)
    {
        this.fileFolderService = fileFolderService;
    }

    public void setAuxFileFolderService(ExtendedFileFolderService auxFileFolderService)
    {
        this.auxFileFolderService = auxFileFolderService;
    }

    public void setTestFileService(TestFileService testFileService)
    {
        this.testFileService = testFileService;
    }

    /**
     * Helper method for creating one root record category with the name starting with provided nameIdentifier and a random generated string.
     *
     * @param folder - container that will contain created root record categories
     * @param userModel - UserModel instance with which rest api will be called
     * @param nameIdentifier - a string identifier that the created root record categories will start with
     * @param context - the context for created root record categories
     * @param loadFilePlanComponentDelay - delay between creation of root record categories
     * @throws Exception
     */
    public void createRootCategory(FolderData folder, UserModel userModel, String nameIdentifier, String context, long loadFilePlanComponentDelay) throws Exception
    {
        String unique;

        String folderPath = folder.getPath();
        unique = UUID.randomUUID().toString();
        String newfilePlanComponentName = nameIdentifier + unique;
        String newfilePlanComponentTitle = "title: " + newfilePlanComponentName;

        // Build root record category properties
        RecordCategory recordCategoryModel = RecordCategory.builder()
                    .name(newfilePlanComponentName)
                    .nodeType(RECORD_CATEGORY_TYPE)
                    .properties(RecordCategoryProperties.builder()
                                .title(newfilePlanComponentTitle)
                                .description(EMPTY)
                                .build())
                    .build();

        FilePlanAPI filePlansAPI = getRestAPIFactory().getFilePlansAPI(userModel);
        RecordCategory rootRecordCategory = filePlansAPI.createRootRecordCategory(recordCategoryModel, folder.getId());
        String newRootRecordCategoryId = rootRecordCategory.getId();
        fileFolderService.createNewFolder(newRootRecordCategoryId, context, folderPath + "/" + newfilePlanComponentName);
        TimeUnit.MILLISECONDS.sleep(loadFilePlanComponentDelay);

        // Increment counts
        fileFolderService.incrementFolderCount(folder.getContext(), folderPath, 1);
    }

    /**
     * Helper method for creating one sub-category with the name starting with provided nameIdentifier and a random generated string.
     *
     * @param folder - container that will contain created sub-category
     * @param userModel - UserModel instance with wich rest api will be called
     * @param nameIdentifier - a string identifier that the created sub-category will start with
     * @param context - the context for created sub-category
     * @param loadFilePlanComponentDelay - delay between creation of sub-categories
     * @throws Exception
     */
    public void createSubCategory(FolderData folder, UserModel userModel, String nameIdentifier, String context,
                long loadFilePlanComponentDelay) throws Exception
    {
        String unique;

        String folderPath = folder.getPath();
        unique = UUID.randomUUID().toString();
        String newfilePlanComponentName = nameIdentifier + unique;
        String newfilePlanComponentTitle = "title: " + newfilePlanComponentName;

        // Build child record category properties
        RecordCategoryChild recordCategoryChildModel = RecordCategoryChild.builder()
                    .name(newfilePlanComponentName)
                    .nodeType(RECORD_CATEGORY_TYPE)
                    .properties(RecordCategoryChildProperties.builder()
                                .title(newfilePlanComponentTitle)
                                .description(EMPTY)
                                .build())
                    .build();

        RecordCategoryAPI recordCategoryAPI = getRestAPIFactory().getRecordCategoryAPI(userModel);
        RecordCategoryChild childRecordCategory = recordCategoryAPI.createRecordCategoryChild(recordCategoryChildModel, folder.getId());
        String newChildRecordCategoryId = childRecordCategory.getId();
        fileFolderService.createNewFolder(newChildRecordCategoryId, context, folderPath + "/" + newfilePlanComponentName);
        TimeUnit.MILLISECONDS.sleep(loadFilePlanComponentDelay);

        // Increment counts
        fileFolderService.incrementFolderCount(folder.getContext(), folderPath, 1);
    }

    /**
     * Helper method for creating child record folder with the name starting with provided nameIdentifier and a random generated string.
     *
     * @param folder - container that will contain created record folder
     * @param userModel - UserModel instance with wich rest api will be called
     * @param nameIdentifier - a string identifier that the created child record folder will start with
     * @param context - the context for created child record folder
     * @param loadFilePlanComponentDelay - delay between creation of child record folders
     * @throws Exception
     */
    public void createRecordFolder(FolderData folder, UserModel userModel, String nameIdentifier, String context,
                long loadFilePlanComponentDelay) throws Exception
    {
        String unique;

        String folderPath = folder.getPath();
        unique = UUID.randomUUID().toString();
        String newfilePlanComponentName = nameIdentifier + unique;
        String newfilePlanComponentTitle = "title: " + newfilePlanComponentName;

        // Build child record folder properties
        RecordCategoryChild recordCategoryChildModel = RecordCategoryChild.builder()
                    .name(newfilePlanComponentName)
                    .nodeType(RECORD_FOLDER_TYPE)
                    .properties(RecordCategoryChildProperties.builder()
                                .title(newfilePlanComponentTitle)
                                .description(EMPTY)
                                .build())
                    .build();

        RecordCategoryAPI recordCategoryAPI = getRestAPIFactory().getRecordCategoryAPI(userModel);
        RecordCategoryChild childRecordFolder = recordCategoryAPI.createRecordCategoryChild(recordCategoryChildModel, folder.getId());
        String newChildRecordFolderId = childRecordFolder.getId();
        fileFolderService.createNewFolder(newChildRecordFolderId, context, folderPath + "/" + newfilePlanComponentName);
        TimeUnit.MILLISECONDS.sleep(loadFilePlanComponentDelay);
        // Increment counts
        fileFolderService.incrementFolderCount(folder.getContext(), folderPath, 1);
    }

    /**
     * Helper method for creating one root unfiled record folder with the name starting with provided nameIdentifier and a random generated string.
     *
     * @param folder - container that will contain created root unfiled record folder
     * @param userModel - UserModel instance with wich rest api will be called
     * @param nameIdentifier - a string identifier that the created root unfiled record folder will start with
     * @param context - the context for created root unfiled record folder
     * @param loadFilePlanComponentDelay - delay between creation of root unfiled record folders
     * @throws Exception
     */
    public void createRootUnfiledRecordFolder(FolderData folder, UserModel userModel, String nameIdentifier, String context, long loadFilePlanComponentDelay) throws Exception
    {
        String unique;

        String folderPath = folder.getPath();
        unique = UUID.randomUUID().toString();
        String newfilePlanComponentName = nameIdentifier + unique;
        String newfilePlanComponentTitle = "title: " + newfilePlanComponentName;

        // Build root unfiled records folder properties
        UnfiledContainerChild unfiledContainerChildModel = UnfiledContainerChild.builder()
                    .name(newfilePlanComponentName)
                    .nodeType(UNFILED_RECORD_FOLDER_TYPE)
                    .properties(UnfiledContainerChildProperties.builder()
                                .title(newfilePlanComponentTitle)
                                .description(EMPTY)
                                .build())
                    .build();

        UnfiledContainerAPI unfiledContainersAPI = getRestAPIFactory().getUnfiledContainersAPI(userModel);
        UnfiledContainerChild unfiledContainerChild = unfiledContainersAPI.createUnfiledContainerChild(unfiledContainerChildModel, folder.getId());
        String newUnfiledContainerChildId = unfiledContainerChild.getId();
        fileFolderService.createNewFolder(newUnfiledContainerChildId, context, folderPath + "/" + newfilePlanComponentName);
        TimeUnit.MILLISECONDS.sleep(loadFilePlanComponentDelay);

        // Increment counts
        fileFolderService.incrementFolderCount(folder.getContext(), folderPath, 1);
    }

    /**
     * Helper method for creating one unfiled record folder that have the name starting with provided nameIdentifier and a random generated string.
     *
     * @param folder - container that will contain created unfiled record folder
     * @param userModel - UserModel instance with wich rest api will be called
     * @param nameIdentifier - a string identifier that the created unfiled record folder will start with
     * @param context - the context for created unfiled record folder
     * @param loadFilePlanComponentDelay - delay between creation of unfiled record folders
     * @throws Exception
     */
    public void createUnfiledRecordFolder(FolderData folder, UserModel userModel, String nameIdentifier, String context, long loadFilePlanComponentDelay) throws Exception
    {
        String unique;

        String folderPath = folder.getPath();
        unique = UUID.randomUUID().toString();
        String newfilePlanComponentName = nameIdentifier + unique;
        String newfilePlanComponentTitle = "title: " + newfilePlanComponentName;

        // Build unfiled record folder properties
        UnfiledContainerChild unfiledRecordFolderChildModel = UnfiledContainerChild.builder()
                    .name(newfilePlanComponentName)
                    .nodeType(UNFILED_RECORD_FOLDER_TYPE)
                    .properties(UnfiledContainerChildProperties.builder()
                                .title(newfilePlanComponentTitle)
                                .description(EMPTY)
                                .build())
                    .build();

        UnfiledRecordFolderAPI unfiledRecordFoldersAPI = getRestAPIFactory().getUnfiledRecordFoldersAPI(userModel);
        UnfiledContainerChild unfiledRecordFolderChild = unfiledRecordFoldersAPI.createUnfiledRecordFolderChild(unfiledRecordFolderChildModel, folder.getId());
        String newUnfiledRecordFolderChildId = unfiledRecordFolderChild.getId();
        fileFolderService.createNewFolder(newUnfiledRecordFolderChildId, context, folderPath + "/" + newfilePlanComponentName);
        TimeUnit.MILLISECONDS.sleep(loadFilePlanComponentDelay);

        // Increment counts
        fileFolderService.incrementFolderCount(folder.getContext(), folderPath, 1);
    }

    /**
     * Helper method for creating a child record category with a specified name.
     *
     * @param folder - container that will contain created child record category
     * @param name - the name that created child record category will have
     * @return created child record category with a specified name
     * @throws Exception
     */
    public FolderData createRecordCategoryWithFixedName(FolderData folder, String name) throws Exception
    {
        FolderData createdFolder = null;
        String folderPath = folder.getPath();
        String newfilePlanComponentTitle = "title: " + name;
        // Build filePlan component records folder properties
        RecordCategoryChild recordCategoryChildModel = RecordCategoryChild.builder()
                    .name(name)
                    .nodeType(RECORD_CATEGORY_TYPE)
                    .properties(RecordCategoryChildProperties.builder()
                                .title(newfilePlanComponentTitle)
                                .description(EMPTY)
                                .build())
                    .build();

        RecordCategoryAPI recordCategoryAPI = getRestAPIFactory().getRecordCategoryAPI();
        RecordCategoryChild recordCategoryChild = recordCategoryAPI.createRecordCategoryChild(recordCategoryChildModel, folder.getId());

        String newRecordCategoryChildId = recordCategoryChild.getId();
        fileFolderService.createNewFolder(newRecordCategoryChildId, RECORD_CATEGORY_CONTEXT,
                    folderPath + "/" + name);
        // Increment counts
        fileFolderService.incrementFolderCount(folder.getContext(), folderPath, 1);
        createdFolder = fileFolderService.getFolder(newRecordCategoryChildId);
        return createdFolder;
    }

    /**
     * Helper method for creating a root record category with a specified name.
     *
     * @param folder - container that will contain created root record category
     * @param name - the name that created root record category will have
     * @return created root record category with a specified name
     * @throws Exception
     */
    public FolderData createRootRecordCategoryWithFixedName(FolderData folder, String name) throws Exception
    {
        FolderData createdFolder = null;
        String folderPath = folder.getPath();
        String newfilePlanComponentTitle = "title: " + name;
        // Build root category properties
        RecordCategory recordCategoryModel = RecordCategory.builder()
                    .name(name)
                    .nodeType(RECORD_CATEGORY_TYPE)
                    .properties(RecordCategoryProperties.builder()
                                .title(newfilePlanComponentTitle)
                                .description(EMPTY)
                                .build())
                    .build();

        FilePlanAPI filePlansAPI = getRestAPIFactory().getFilePlansAPI();
        RecordCategory rootCategory = filePlansAPI.createRootRecordCategory(recordCategoryModel, folder.getId());

        String newRootCategoryId = rootCategory.getId();
        fileFolderService.createNewFolder(newRootCategoryId, RECORD_CATEGORY_CONTEXT,
                    folderPath + "/" + name);
        // Increment counts
        fileFolderService.incrementFolderCount(folder.getContext(), folderPath, 1);
        createdFolder = fileFolderService.getFolder(newRootCategoryId);
        return createdFolder;
    }

    /**
     * Helper method for creating a child record folder with a specified name.
     *
     * @param folder - container that will contain created child record folder
     * @param name - the name that created child record folder will have
     * @return created child record folder with a specified name
     * @throws Exception
     */
    public FolderData createRecordFolderWithFixedName(FolderData folder, String name) throws Exception
    {
        FolderData createdFolder = null;
        String folderPath = folder.getPath();
        String newfilePlanComponentTitle = "title: " + name;
        // Build filePlan component records folder properties
        RecordCategoryChild recordCategoryChildModel = RecordCategoryChild.builder()
                    .name(name)
                    .nodeType(RECORD_FOLDER_TYPE)
                    .properties(RecordCategoryChildProperties.builder()
                                .title(newfilePlanComponentTitle)
                                .description(EMPTY)
                                .build())
                    .build();

        RecordCategoryAPI recordCategoryAPI = getRestAPIFactory().getRecordCategoryAPI();
        RecordCategoryChild recordFolderChild = recordCategoryAPI.createRecordCategoryChild(recordCategoryChildModel, folder.getId());

        String newRecordFolderChildId = recordFolderChild.getId();
        fileFolderService.createNewFolder(newRecordFolderChildId, RECORD_FOLDER_CONTEXT,
                    folderPath + "/" + name);
        // Increment counts
        fileFolderService.incrementFolderCount(folder.getContext(), folderPath, 1);
        createdFolder = fileFolderService.getFolder(newRecordFolderChildId);
        return createdFolder;
    }

    /**
     * Helper method for creating a root unfiled record folder with a specified name.
     *
     * @param folder - container that will contain created root unfiled record folder
     * @param name - the name that created root unfiled record folder will have
     * @return created root unfiled record folder with a specified name
     * @throws Exception
     */
    public FolderData createRootUnfiledRecordFolderWithFixedName(FolderData folder, String name) throws Exception
    {
        FolderData createdFolder = null;
        String folderPath = folder.getPath();
        String newfilePlanComponentTitle = "title: " + name;
        // Build root unfiled record folder properties
        UnfiledContainerChild unfiledContainerChildModel = UnfiledContainerChild.builder()
                    .name(name)
                    .nodeType(UNFILED_RECORD_FOLDER_TYPE)
                    .properties(UnfiledContainerChildProperties.builder()
                                .title(newfilePlanComponentTitle)
                                .description(EMPTY)
                                .build())
                    .build();

        UnfiledContainerAPI unfiledContainersAPI = getRestAPIFactory().getUnfiledContainersAPI();
        UnfiledContainerChild rootUnfiledRecordFolder = unfiledContainersAPI.createUnfiledContainerChild(unfiledContainerChildModel, folder.getId());

        String newRootUnfiledRecordFolderId = rootUnfiledRecordFolder.getId();
        fileFolderService.createNewFolder(newRootUnfiledRecordFolderId, UNFILED_CONTEXT,
                    folderPath + "/" + name);
        // Increment counts
        fileFolderService.incrementFolderCount(folder.getContext(), folderPath, 1);
        createdFolder = fileFolderService.getFolder(newRootUnfiledRecordFolderId);
        return createdFolder;
    }

    /**
     * Helper method for creating a child unfiled record folder with a specified name.
     *
     * @param folder - container that will contain created child unfiled record folder
     * @param name - the name that created child unfiled record folder will have
     * @return created child unfiled record folder with a specified name
     * @throws Exception
     */
    public FolderData createUnfiledRecordFolderWithFixedName(FolderData folder, String name) throws Exception
    {
        FolderData createdFolder = null;
        String folderPath = folder.getPath();
        String newfilePlanComponentTitle = "title: " + name;
        // Build unfiled record folder properties
        UnfiledContainerChild unfiledRecordFolderChildModel = UnfiledContainerChild.builder()
                    .name(name)
                    .nodeType(UNFILED_RECORD_FOLDER_TYPE)
                    .properties(UnfiledContainerChildProperties.builder()
                                .title(newfilePlanComponentTitle)
                                .description(EMPTY)
                                .build())
                    .build();

        UnfiledRecordFolderAPI unfiledRecordFoldersAPI = getRestAPIFactory().getUnfiledRecordFoldersAPI();
        UnfiledContainerChild unfiledRecordFolderChild = unfiledRecordFoldersAPI.createUnfiledRecordFolderChild(unfiledRecordFolderChildModel, folder.getId());

        String newUnfiledRecordFolderChildId = unfiledRecordFolderChild.getId();
        fileFolderService.createNewFolder(newUnfiledRecordFolderChildId, UNFILED_CONTEXT,
                    folderPath + "/" + name);
        // Increment counts
        fileFolderService.incrementFolderCount(folder.getContext(), folderPath, 1);
        createdFolder = fileFolderService.getFolder(newUnfiledRecordFolderChildId);
        return createdFolder;
    }

    /**
     * Helper method for creating a non-electronic document in specified record folder.
     *
     * @param folder - record folder that will contain created non-electronic document
     * @param userModel - UserModel instance with wich rest api will be called
     * @param nameIdentifier - a string identifier that the created non-electronic document will start with
     * @param loadFilePlanComponentDelay - delay between creation of non-electronic documents
     * @throws Exception
     */
    public void createNonElectonicRecordInRecordFolder(FolderData folder, UserModel userModel, String nameIdentifier, long loadFilePlanComponentDelay) throws Exception
    {
        String unique;

        String folderPath = folder.getPath();
        unique = UUID.randomUUID().toString();
        String newfilePlanComponentName = nameIdentifier + unique;
        String newfilePlanComponentTitle = "title: " + newfilePlanComponentName;

        // Build non electronic record properties
        Record recordModel = Record.builder()
                    .name(newfilePlanComponentName)
                    .nodeType(NON_ELECTRONIC_RECORD_TYPE)
                    .properties(RecordProperties.builder()
                                .title(newfilePlanComponentTitle)
                                .description(EMPTY)
                                .build())
                    .build();

        RecordFolderAPI recordFolderAPI = getRestAPIFactory().getRecordFolderAPI(userModel);
        String parentId = folder.getId();
        recordFolderAPI.createRecord(recordModel, parentId);
        TimeUnit.MILLISECONDS.sleep(loadFilePlanComponentDelay);

        // Increment counts
        fileFolderService.incrementFileCount(folder.getContext(), folderPath, 1);
    }

    /**
     * Helper method for creating specified number of non-electronic documents in specified unfiled container or unfiled record folder.
     *
     * @param folder - unfiled container or unfiled record folder that will contain created non-electronic document
     * @param userModel - UserModel instance with wich rest api will be called
     * @param nameIdentifier - a string identifier that the created non-electronic documents will start with
     * @param loadFilePlanComponentDelay - delay between creation of non-electronic documents
     * @throws Exception
     */
    public void createNonElectonicRecordInUnfiledContext(FolderData folder, UserModel userModel, String nameIdentifier,
                long loadFilePlanComponentDelay) throws Exception
    {
        String unique;

        boolean isUnfiledContainer = fileFolderService.getFolder(UNFILED_CONTEXT, UNFILED_RECORD_CONTAINER_PATH).equals(folder);
        String folderPath = folder.getPath();
        unique = UUID.randomUUID().toString();
        String newfilePlanComponentName = nameIdentifier + unique;
        String newfilePlanComponentTitle = "title: " + newfilePlanComponentName;

        // Build non electronic record properties
        UnfiledContainerChild unfiledContainerChildModel = UnfiledContainerChild.builder()
                    .name(newfilePlanComponentName)
                    .nodeType(NON_ELECTRONIC_RECORD_TYPE)
                    .properties(UnfiledContainerChildProperties.builder()
                                .title(newfilePlanComponentTitle)
                                .description(EMPTY)
                                .build())
                    .build();

        String newRecordId;
        String newRecordName;
        if(isUnfiledContainer)
        {
            UnfiledContainerAPI unfiledContainersAPI = getRestAPIFactory().getUnfiledContainersAPI(userModel);
            UnfiledContainerChild createdRecord = unfiledContainersAPI.createUnfiledContainerChild(unfiledContainerChildModel, folder.getId());
            newRecordId = createdRecord.getId();
            newRecordName = createdRecord.getName();
        }
        else
        {
            UnfiledRecordFolderAPI unfiledRecordFoldersAPI = getRestAPIFactory().getUnfiledRecordFoldersAPI(userModel);
            UnfiledContainerChild createdRecord = unfiledRecordFoldersAPI.createUnfiledRecordFolderChild(unfiledContainerChildModel, folder.getId());
            newRecordId = createdRecord.getId();
            newRecordName = createdRecord.getName();
        }

        RecordData record = new RecordData(newRecordId, RecordContext.RECORD, newRecordName, folderPath, null, ExecutionState.UNFILED_RECORD_DECLARED);
        recordService.createRecord(record);
        TimeUnit.MILLISECONDS.sleep(loadFilePlanComponentDelay);
        // Increment counts
        fileFolderService.incrementFileCount(folder.getContext(), folderPath, 1);
    }

    /**
     * Helper method for uploading one record on specified record folder.
     *
     * @param folder - record folder that will contain uploaded record
     * @param userModel - UserModel instance with wich rest api will be called
     * @param nameIdentifier - a string identifier that the uploaded record will start with
     * @param loadFilePlanComponentDelay - delay between upload record operations
     * @throws Exception
     */
    public void uploadElectronicRecordInRecordFolder(FolderData folder, UserModel userModel, String nameIdentifier, long loadFilePlanComponentDelay) throws Exception
    {
        String unique;

        String folderPath = folder.getPath();
        File file = testFileService.getFile();
        if (file == null)
        {
            throw new RuntimeException("No test files exist for upload: " + testFileService);
        }
        unique = UUID.randomUUID().toString();
        String newfilePlanComponentName = nameIdentifier + unique + "-" + file.getName();
        String newfilePlanComponentTitle = "title: " + newfilePlanComponentName;
        // Build record properties
        Record recordModel = Record.builder()
                    .name(newfilePlanComponentName)
                    .nodeType(CONTENT_TYPE)
                    .properties(RecordProperties.builder()
                                .title(newfilePlanComponentTitle)
                                .description(EMPTY)
                                .build())
                    .build();

        RecordFolderAPI recordFolderAPI = getRestAPIFactory().getRecordFolderAPI(userModel);
        recordFolderAPI.createRecord(recordModel, folder.getId(), file);
        TimeUnit.MILLISECONDS.sleep(loadFilePlanComponentDelay);
        fileFolderService.incrementFileCount(folder.getContext(), folderPath, 1);
    }

    /**
     * Helper method for uploading one record on specified unfiled container or unfiled record folder.
     *
     * @param folder - unfiled container or unfiled record folder that will contain uploaded records
     * @param userModel - UserModel instance with wich rest api will be called
     * @param nameIdentifier - a string identifier that the uploaded records will start with
     * @param loadFilePlanComponentDelay - delay between upload record operations
     * @throws Exception
     */
    public void uploadElectronicRecordInUnfiledContext(FolderData folder, UserModel userModel, String nameIdentifier,
                                                       long loadFilePlanComponentDelay) throws Exception // to-check: type of exception
    {
        String unique;

        boolean isUnfiledContainer = fileFolderService.getFolder(UNFILED_CONTEXT, UNFILED_RECORD_CONTAINER_PATH).equals(folder);
        String folderPath = folder.getPath();
        File file = testFileService.getFile();
        if (file == null)
        {
            throw new RuntimeException("No test files exist for upload: " + testFileService);
        }
        unique = UUID.randomUUID().toString();
        String newfilePlanComponentName = nameIdentifier + unique + "-" + file.getName();
        String newfilePlanComponentTitle = "title: " + newfilePlanComponentName;
        // Build record properties
        UnfiledContainerChild unfiledContainerChildModel = UnfiledContainerChild.builder()
                    .name(newfilePlanComponentName)
                    .nodeType(CONTENT_TYPE)
                    .properties(UnfiledContainerChildProperties.builder()
                                .title(newfilePlanComponentTitle)
                                .description(EMPTY)
                                .build())
                    .build();

        String newRecordId;
        String newRecordName;
        if(isUnfiledContainer)
        {
            UnfiledContainerAPI unfiledContainersAPI = getRestAPIFactory().getUnfiledContainersAPI(userModel);
            UnfiledContainerChild uploadedRecord = unfiledContainersAPI.uploadRecord(unfiledContainerChildModel, folder.getId(), file);
            newRecordId = uploadedRecord.getId();
            newRecordName = uploadedRecord.getName();
        }
        else
        {
            UnfiledRecordFolderAPI unfiledRecordFoldersAPI = getRestAPIFactory().getUnfiledRecordFoldersAPI(userModel);
            UnfiledContainerChild uploadedRecord = unfiledRecordFoldersAPI.uploadRecord(unfiledContainerChildModel, folder.getId(), file);
            newRecordId = uploadedRecord.getId();
            newRecordName = uploadedRecord.getName();
        }
        RecordData record = new RecordData(newRecordId, RecordContext.RECORD, newRecordName, folderPath, null, ExecutionState.UNFILED_RECORD_DECLARED);
        recordService.createRecord(record);
        fileFolderService.incrementFileCount(folder.getContext(), folderPath, 1);
    }

    /**
     * Helper method to distribute the number of records to create on the given list of folders.
     *
     * @param listOfFolders - list of available folders to distribute records into
     * @param numberOfRecords - number of records to be distributed
     * @return a map with folders as keys and the number of records to create on that folder as values.
     */
    public LinkedHashMap<FolderData, Integer> distributeNumberOfRecords(List<FolderData> listOfFolders, int numberOfRecords)
    {
        LinkedHashMap<FolderData, Integer> mapOfRecordsPerFolder = new LinkedHashMap<FolderData,Integer>();
        int[] generateRandomValues = generateRandomValues(listOfFolders.size(), numberOfRecords);
        int counter = 0;
        for(FolderData folder : listOfFolders)
        {
            int records = (int)(folder.getFileCount() + generateRandomValues[counter]);
            mapOfRecordsPerFolder.put(folder, records);
            counter++;
        }
        return mapOfRecordsPerFolder;
    }

    /**
     * Obtains all record folders underneath specified parent if the parent is a category or the parent itself if it is a record folder
     *
     * @param parentFolder - the parent folder to retrieve children folders from
     * @return all record folders underneath specified parent if the parent is a category or the parent itself if it is a record folder
     */
    protected Set<FolderData> getRecordFolders(FolderData parentFolder)
    {
        LinkedHashSet<FolderData> result = new LinkedHashSet<FolderData>();
        String context = parentFolder.getContext();
        if(RECORD_CATEGORY_CONTEXT.equals(context))
        {
            List<FolderData> directCategoryChildren = getDirectChildrenByContext(parentFolder, RECORD_CATEGORY_CONTEXT);
            if (!directCategoryChildren.isEmpty())
            {
                for (FolderData childFolder : directCategoryChildren)
                {
                    result.addAll(getRecordFolders(childFolder));
                }
            }

            List<FolderData> directRecordFolderChildren = getDirectChildrenByContext(parentFolder, RECORD_FOLDER_CONTEXT);
            if (!directRecordFolderChildren.isEmpty())
            {
                Iterator<FolderData> iterator = directRecordFolderChildren.iterator();
                while(iterator.hasNext())
                {
                    FolderData childFolder = iterator.next();
                    if(childFolder.getPath().endsWith("locked") && !isIdValid(childFolder.getId(), RECORD_FOLDER_CONTEXT))
                    {
                        iterator.remove();
                    }
                }
                result.addAll(directRecordFolderChildren);
            }
        }
        else if(RECORD_FOLDER_CONTEXT.equals(context))
        {
            if(!parentFolder.getPath().endsWith("locked") || (parentFolder.getPath().endsWith("locked") && isIdValid(parentFolder.getId(), RECORD_FOLDER_CONTEXT)))
            {
                result.add(parentFolder);
            }
        }
        return result;
    }

    /**
     * Helper method to obtain all direct children of specified context from provided folder.
     *
     * @param parentFolder - the folder to get children from
     * @param context - the context of the needed children
     * @return all direct children of specified context from provided folder.
     */
    public List<FolderData> getDirectChildrenByContext(FolderData parentFolder, String context)
    {
        int skip = 0;
        int limit = 100;
        List<FolderData> directChildren = new ArrayList<FolderData>();
        List<FolderData> childFolders = fileFolderService.getChildFolders(context, parentFolder.getPath(), skip, limit);
        while (!childFolders.isEmpty())
        {
            directChildren.addAll(childFolders);
            skip += limit;
            childFolders = fileFolderService.getChildFolders(context, parentFolder.getPath(), skip, limit);
        }
        return directChildren;
    }

    /**
     * Algorithm to distribute number of records to specified number of folders.
     *
     * @param numberOfFolders - number of available folders
     * @param numberOfRecords - number of records that need to be distributed in all available folders
     * @return an array with number of records to be created for each folder
     */
    private int[] generateRandomValues(int numberOfFolders, int numberOfRecords)
    {
        int[] aux = new int[numberOfFolders+1];
        int[] generatedValues = new int[numberOfFolders];
        Random r = new Random();
        for(int i = 1;i < numberOfFolders;i++)
        {
            aux[i] = r.nextInt(numberOfRecords)+1;
        }
        aux[0] = 0;
        aux[numberOfFolders] = numberOfRecords;
        Arrays.sort(aux);
        for(int i = 0;i < numberOfFolders;i++)
        {
            generatedValues[i] = aux[i+1] - aux[i];
        }
        return generatedValues;
    }

    /**
     * Helper method to obtain all folders with specified context.
     *
     * @param context - context of the wanted folders
     * @return all folders with specified context.
     */
    public List<FolderData> initialiseFoldersToExistingStructure(String context)
    {
        List<FolderData> existingFolderStructure = new ArrayList<FolderData>();
        int skip = 0;
        int limit = 100;
        List<FolderData> emptyFolders = fileFolderService.getFoldersByCounts(
                    context,
                    null, null,
                    null, null,
                    null, null,
                    skip, limit);
        while(!emptyFolders.isEmpty())
        {
            existingFolderStructure.addAll(emptyFolders);
            skip += limit;
            emptyFolders = fileFolderService.getFoldersByCounts(
                        context,
                        null, null,
                        null, null,
                        null, null,
                        skip, limit);
        }
        //check for locked folders
        Iterator<FolderData> iterator = existingFolderStructure.iterator();
        while(iterator.hasNext())
        {
            FolderData folder = iterator.next();
            if(folder.getPath().endsWith("locked") && !isIdValid(folder.getId(), context))
            {
                iterator.remove();
            }
        }
        return existingFolderStructure;
    }

    /**
     * Gets a random user from the RM site.
     */
    public UserData getRandomUser(Logger logger)
    {
        // Check
        SiteData siteData = siteDataService.getSite(PATH_SNIPPET_RM_SITE_ID);
        if (siteData == null)
        {
            throw new IllegalStateException("Unable to find site '" + PATH_SNIPPET_RM_SITE_ID + "'");
        }
        SiteMemberData siteMember = siteDataService.randomSiteMember(PATH_SNIPPET_RM_SITE_ID, DataCreationState.Created, null, RMRole.Administrator.toString());
        if (siteMember == null)
        {
            throw new IllegalStateException("Unable to find a user with specified roles for site: " + PATH_SNIPPET_RM_SITE_ID);
        }
        String username = siteMember.getUsername();
        // Retrieve the user data
        UserData user = userDataService.findUserByUsername(username);
        if (user == null)
        {
            throw new IllegalStateException("Unable to find a user '" + username + "' linked to site: " + PATH_SNIPPET_RM_SITE_ID);
        }
        // Done
        logger.debug("Found RM site member '" + username + "'");
        return user;
    }

    /**
     * Helper method used for creating in alfresco repo and in mongo DB, root record categories, record categories and record folders from configured path elements.
     *
     * @param path - path element
     * @return created record folder, or existing record folder, if already created
     * @throws Exception
     */
    public FolderData createRecordCategoryOrRecordFolder(String path) throws Exception
    {
        //create inexistent elements from configured paths as admin
        List<String> pathElements = Arrays.asList(split(path, "/"));
        FolderData parentFolder = fileFolderService.getFolder(FILEPLAN_CONTEXT, RECORD_CONTAINER_PATH);
        // for(String pathElement: pathElements)
        int pathElementsLength = pathElements.size();

        // when one path does not exist it must have at least path elements, one for root record category and one for record folder
        if (pathElementsLength == 1)
        {
            throw new Exception("At least 2 path elemets needed for creating record folders in which we can create records");
        }
        for (int i = 0; i < pathElementsLength; i++)
        {
            String pathElement = pathElements.get(i);
            FolderData folder = fileFolderService.getFolder(RECORD_CATEGORY_CONTEXT,
                        parentFolder.getPath() + "/" + pathElement);
            if (folder != null)
            {
                parentFolder = folder;
            }
            else
            {
                if(i == 0)
                {
                    //create root category
                    parentFolder = createRootRecordCategoryWithFixedName(parentFolder, pathElement);
                }
                else if (pathElementsLength > 1  && i == (pathElementsLength - 1))
                {
                    //create record folder
                    parentFolder = createRecordFolderWithFixedName(parentFolder, pathElement);
                }
                else
                {
                    //create child category
                    parentFolder = createRecordCategoryWithFixedName(parentFolder, pathElement);
                }
            }
        }
        return parentFolder;
    }

    /**
     * Helper method that initialize the record folders that can receive records.
     * This method, also calculates the number of records to  add to the initialized record folders.
     *
     * @param mapOfRecordsPerRecordFolder - linked hash map with available record folders as keys and calculated number of records to load/file in each record folder
     * @param paths - record category or record folder paths to load/file records in
     * @param numberOrRecords - number of records to load/file
     */
    public LinkedHashMap<FolderData, Integer> calculateListOfEmptyFolders(LinkedHashMap<FolderData, Integer> mapOfRecordsPerRecordFolder, List<String> paths, int numberOrRecords)
    {
        if (mapOfRecordsPerRecordFolder == null)
        {
            mapOfRecordsPerRecordFolder = new LinkedHashMap<FolderData, Integer>();
            List<FolderData> recordFoldersThatNeedRecords = new ArrayList<FolderData>();
            if (paths == null || paths.isEmpty())
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
                            folder = createRecordCategoryOrRecordFolder(path);
                            recordFoldersThatNeedRecords.add(folder);
                        }
                        catch (Exception e)
                        {
                            // something went wrong on creating current path structure, not all required paths will be created
                            eventProcessorLogger.debug("Path elements of " + path + "could not be created.", e);
                        }
                    }
                }
                // add record folders from existent paths
                if (!structureFromExistentProvidedPaths.isEmpty())
                {
                    recordFoldersThatNeedRecords.addAll(structureFromExistentProvidedPaths);
                }
                // configured paths did not existed in db and something went wrong with creation for all of them,
                // initialize to existing structure in this case
                if (recordFoldersThatNeedRecords.isEmpty())
                {
                    recordFoldersThatNeedRecords.addAll(initialiseFoldersToExistingStructure(RECORD_FOLDER_CONTEXT));
                }
            }
            if (!recordFoldersThatNeedRecords.isEmpty())
            {
                mapOfRecordsPerRecordFolder = distributeNumberOfRecords(recordFoldersThatNeedRecords, numberOrRecords);
            }
        }
        return mapOfRecordsPerRecordFolder;
    }

    /**
     * Obtains all unfiled record folders underneath specified parent folder plus the parent folder
     *
     * @param parentFolder - the parent folder that we need to get unfiled record folders from
     * @return all unfiled record folders underneath specified parent folder plus the parent folder
     */
    public Set<FolderData> getUnfiledRecordFolders(FolderData parentFolder)
    {
        LinkedHashSet<FolderData> result = new LinkedHashSet<FolderData>();
        int skip = 0;
        int limit = 100;
        List<FolderData> directChildren = new ArrayList<FolderData>();
        List<FolderData> childFolders = fileFolderService.getChildFolders(UNFILED_CONTEXT, parentFolder.getPath(), skip, limit);
        while(!childFolders.isEmpty())
        {
            directChildren.addAll(childFolders);
            skip += limit;
            childFolders = fileFolderService.getChildFolders(UNFILED_CONTEXT, parentFolder.getPath(), skip, limit);
        }
        if(!directChildren.isEmpty())
        {
            for(FolderData childFolder : directChildren)
            {
                result.addAll(getUnfiledRecordFolders(childFolder));
            }
        }
        if(!parentFolder.getPath().endsWith("locked") || (parentFolder.getPath().endsWith("locked") && isIdValid(parentFolder.getId(), UNFILED_CONTEXT)))
        {
            result.add(parentFolder);
        }
        return result;
    }

    /**
     * Helper method for obtaining all unfiled records from records MongoDb.
     *
     * @return all unfiled records from records MongoDb
     */
    public List<RecordData> getAllUnfiledRecords()
    {
        List<RecordData> existingRecords = new ArrayList<RecordData>();
        int skip = 0;
        int limit = 100;
        List<RecordData> recordsList = recordService.getRecordsInPaths(ExecutionState.UNFILED_RECORD_DECLARED.name(), null, skip, limit);
        while(!recordsList.isEmpty())
        {
            existingRecords.addAll(recordsList);
            skip += limit;
            recordsList =  recordService.getRecordsInPaths(ExecutionState.UNFILED_RECORD_DECLARED.name(), null, skip, limit);
        }
        return existingRecords;
    }

    /**
     * Helper method to check if one id is a valid record folder, unfiled container, or unfiled record folder id.
     *
     * @param id - folder id that is checked
     * @param context - context of the checked folder id
     * @return <code>true</code> if the id is valid, or <code>false</code> otherwhise.
     */
    private boolean isIdValid(String id, String context)
    {
        boolean result = false;
        if(RECORD_FOLDER_CONTEXT.equals(context))
        {
            RestAPIFactory restAPIFactory = getRestAPIFactory();
            restAPIFactory.getRecordFolderAPI().getRecordFolder(id);
            String statusCode = restAPIFactory.getRmRestWrapper().getStatusCode();
            if(HttpStatus.valueOf(Integer.parseInt(statusCode)) == HttpStatus.OK)
            {
                return true;
            }
        }
        else if (UNFILED_CONTEXT.equals(context))
        {
            RestAPIFactory restAPIFactory = getRestAPIFactory();
            restAPIFactory.getUnfiledRecordFoldersAPI().getUnfiledRecordFolder(id);
            String statusCode = restAPIFactory.getRmRestWrapper().getStatusCode();
            if(HttpStatus.valueOf(Integer.parseInt(statusCode)) == HttpStatus.OK)
            {
                return true;
            }
            else
            {
                restAPIFactory.getUnfiledContainersAPI().getUnfiledContainer(id);
                statusCode = restAPIFactory.getRmRestWrapper().getStatusCode();
                if(HttpStatus.valueOf(Integer.parseInt(statusCode)) == HttpStatus.OK)
                {
                    return true;
                }
            }
        }
        return result;
    }
}