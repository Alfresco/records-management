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

import java.util.concurrent.TimeUnit;

import org.alfresco.bm.cm.FolderData;
import org.alfresco.bm.dataload.rm.services.ExecutionState;
import org.alfresco.bm.dataload.rm.services.RecordData;
import org.alfresco.rest.rm.community.model.record.RecordBodyFile;
import org.alfresco.rest.rm.community.requests.gscore.api.RecordsAPI;
import org.alfresco.utility.model.UserModel;

import com.mongodb.DBObject;

/**
 * Common event for loading one single rm component. This event will load: one root record category, one sub-category, one record folder, one record, one root unfiled record folder,
 * one child unfiled record folder, one unfiled record. The will also file one unfiled record.
 *
 * @author Silviu Dinuta
 * @since 2.6
 *
 */
public class LoadSingleComponent extends RMAbstractLoadComponent
{
    /**
     * Helper method to file the unfiled record with specified id in specified record folder.
     *
     * @param folder - the record folded to file unfiled record in
     * @param recordId - the id of the unfiled record to be filed
     * @param userModel - the user model with which the unfiled record will be filed
     * @return String - the filing message
     * @throws Exception
     */
    private String fileRecordOperation(FolderData folder, String recordId, UserModel userModel) throws Exception
    {
        // FileRecords records
        String folderPath = folder.getPath();
        String parentId = folder.getId();

        RecordBodyFile recordBodyFileModel = RecordBodyFile.builder()
                    .targetParentId(parentId)
                    .build();

        RecordData randomRecord = recordService.getRecord(recordId);
        super.resumeTimer();
        RecordsAPI recordsAPI = getRestAPIFactory().getRecordsAPI(userModel);
        recordsAPI.fileRecord(recordBodyFileModel, randomRecord.getId());
        super.suspendTimer();
        // Increment counts
        fileFolderService.incrementFileCount(folder.getContext(), folderPath, 1);

        // Decrement counts for unfiled record folder or unfiled container
        String unfiledParentPath = randomRecord.getParentPath();
        fileFolderService.incrementFileCount(UNFILED_CONTEXT, unfiledParentPath, -1);

        //change parent path to the new parent
        randomRecord.setParentPath(folderPath);
        randomRecord.setExecutionState(ExecutionState.RECORD_FILED);
        recordService.updateRecord(randomRecord);
        TimeUnit.MILLISECONDS.sleep(getDelay());
        return "Filed record with id " + recordId + ".";
    }

    /**
     * Helper method to load one unfiled record in specified unfiled record folder, or record container.
     *
     * @param folder - the unfiled record container or unfiled record folded to load record in
     * @param userModel - the user model with which the unfiled record will be loaded
     * @return String - the loading message
     * @throws Exception
     */
    private String loadUnfiledRecordOperation(FolderData folder, UserModel userModel) throws Exception
    {
        //Create record
        super.resumeTimer();
        uploadElectronicRecordInUnfiledContext(folder, userModel, RECORD_NAME_IDENTIFIER, getDelay());
        super.suspendTimer();
        return "Created 1 record.";
    }

    /**
     * Helper method to load one record in specified record folder.
     *
     * @param folder - the record folded to load record in
     * @param userModel - the user model with which the record  will be loaded
     * @return String - the loading message
     * @throws Exception
     */
    private String loadRecordOperation(FolderData folder, UserModel userModel) throws Exception
    {
        // Create record
        super.resumeTimer();
        uploadElectronicRecordInRecordFolder(folder, userModel, RECORD_NAME_IDENTIFIER, getDelay());
        super.suspendTimer();
        return "Created 1 record.";
    }

    /**
     * Helper method to load one root record category in filePlan.
     *
     * @param folder - the filePlan folder to load root category in
     * @param userModel - the user model with which the root record category will be loaded
     * @return String - the loading message
     * @throws Exception
     */
    private String loadRootCategoryOperation(FolderData folder, UserModel userModel) throws Exception
    {
        // Create root category
        super.resumeTimer();
        createRootCategory(folder, userModel, ROOT_CATEGORY_NAME_IDENTIFIER, RECORD_CATEGORY_CONTEXT, getDelay());
        super.suspendTimer();
        return "Created 1 root category.";
    }

    /**
     * Helper method to load one sub-category in specified record category.
     *
     * @param folder - the record category folder to load sub-category in
     * @param userModel - the user model with which the sub-category will be loaded
     * @return String - the loading message
     * @throws Exception
     */
    private String loadSubCategoryOperation(FolderData folder, UserModel userModel) throws Exception
    {
        // Create sub-category
        super.resumeTimer();
        createSubCategory(folder, userModel, CATEGORY_NAME_IDENTIFIER, RECORD_CATEGORY_CONTEXT, getDelay());
        super.suspendTimer();
        return "Created 1 sub-category";
    }

    /**
     * Helper method to load one record folder in specified record category.
     *
     * @param folder - the record category folder to load record folder in
     * @param userModel - the user model with which the  record folder will be loaded
     * @return String - the loading message
     * @throws Exception
     */
    private String loadRecordFolderOperation(FolderData folder, UserModel userModel) throws Exception
    {
        // Create record folder
        super.resumeTimer();
        createRecordFolder(folder, userModel, RECORD_FOLDER_NAME_IDENTIFIER, RECORD_FOLDER_CONTEXT, getDelay());
        super.suspendTimer();
        return "Created 1 record folder.";
    }

    /**
     * Helper method to load one root unfiled record folder in unfiled record container.
     *
     * @param folder - the unfiled record container folder to load root unfiled record folder in
     * @param userModel - the user model with which the root unfiled record folder will be loaded
     * @return String - the loading message
     * @throws Exception
     */
    private String loadRootUnfiledRecordFolderOperation(FolderData folder, UserModel userModel) throws Exception
    {
        //Create one root unfiled record folder
        super.resumeTimer();
        createRootUnfiledRecordFolder(folder, userModel, ROOT_UNFILED_RECORD_FOLDER_NAME_IDENTIFIER, folder.getContext(), getDelay());
        super.suspendTimer();
        return "Created 1 root unfiled record folder.";
    }

    /**
     * Helper method to load one unfiled record folder in specified container.
     *
     * @param folder - the unfiled record folder to load unfiled record folder in
     * @param userModel - the user model with which the unfiled record folder will be loaded
     * @return String - the loading message
     * @throws Exception
     */
    private String loadUnfiledRecordFolderOperation(FolderData folder, UserModel userModel) throws Exception
    {
        //Create one unfiled record folder
        super.resumeTimer();
        createUnfiledRecordFolder(folder, userModel, UNFILED_RECORD_FOLDER_NAME_IDENTIFIER, folder.getContext(), getDelay());
        super.suspendTimer();
        return "Created 1 unfiled record folder.";
    }

    @Override
    String executeOperation(FolderData folder, DBObject dataObj, String operation, UserModel userModel) throws Exception
    {
        switch (operation)
        {
            case FILE_RECORD_OPERATION:
                String recordId = (String) dataObj.get(FIELD_RECORD_ID);
                return fileRecordOperation(folder, recordId, userModel);
            case LOAD_UNFILED_RECORD_OPERATION:
                return loadUnfiledRecordOperation(folder, userModel);
            case LOAD_RECORD_OPERATION:
                return loadRecordOperation(folder, userModel);
            case LOAD_ROOT_CATEGORY_OPERATION:
                return loadRootCategoryOperation(folder, userModel);
            case LOAD_SUB_CATEGORY_OPERATION:
                return loadSubCategoryOperation(folder, userModel);
            case LOAD_RECORD_FOLDER_OPERATION:
                return loadRecordFolderOperation(folder, userModel);
            case LOAD_ROOT_UNFILED_RECORD_FOLDER_OPERATION:
                return loadRootUnfiledRecordFolderOperation(folder, userModel);
            case LOAD_UNFILED_RECORD_FOLDER_OPERATION:
                return loadUnfiledRecordFolderOperation(folder, userModel);
            default:
                return "";
        }
    }
}
