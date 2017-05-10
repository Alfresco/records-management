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

/**
 * Interface that contains useful constants used in RM benchmark events.
 *
 * @author Silviu Dinuta
 * @since 2.6
 */
public interface RMEventConstants
{
    String FIELD_CONTEXT = "context";
    String FIELD_PATH = "path";
    String FIELD_ID = "id";
    String FIELD_USERNAME = "username";
    String FIELD_PASSWORD = "password";

    String FIELD_UNFILED_ROOT_FOLDERS_TO_CREATE = "rootFoldersToCreate";
    String FIELD_UNFILED_FOLDERS_TO_CREATE = "foldersToCreate";
    String FIELD_RECORDS_TO_CREATE = "recordsToCreate";

    String RECORD_FOLDER_NAME_IDENTIFIER = "bmFolder";
    String CATEGORY_NAME_IDENTIFIER = "bmCateg";
    String ROOT_CATEGORY_NAME_IDENTIFIER = "bmRootCateg";
    String ROOT_UNFILED_RECORD_FOLDER_NAME_IDENTIFIER = "UnfiledRoot";
    String UNFILED_RECORD_FOLDER_NAME_IDENTIFIER = "UnfiledFolder";
    String RECORD_NAME_IDENTIFIER = "Record";

    String FIELD_ROOT_CATEGORIES_TO_CREATE = "rootCategoriesToCreate";
    String FIELD_CATEGORIES_TO_CREATE = "categoriesToCreate";
    String FIELD_FOLDERS_TO_CREATE = "foldersToCreate";

    int FILE_PLAN_LEVEL = 3;
    int UNFILED_RECORD_CONTAINER_LEVEL = 4;
    String UNFILED_CONTEXT = "unfiled";
    String TRANSFER_CONTEXT = "transfers";
    String FILEPLAN_CONTEXT = "filePlan";
    String RECORD_FOLDER_CONTEXT = "recordFolder";
    String RECORD_CATEGORY_CONTEXT = "recordCategory";

    String PATH_SNIPPET_SITES = "Sites";
    String PATH_SNIPPET_FILE_PLAN = "documentLibrary";
    String PATH_SNIPPET_RM_SITE_ID = "rm";
    String PATH_SNIPPET_UNFILED_RECORD_CONTAINER = "Unfiled Records";

    String RECORD_CONTAINER_PATH = "/" + PATH_SNIPPET_SITES +
                                                        "/" + PATH_SNIPPET_RM_SITE_ID +
                                                        "/" + PATH_SNIPPET_FILE_PLAN;

    String UNFILED_RECORD_CONTAINER_PATH = RECORD_CONTAINER_PATH +
                                                        "/" + PATH_SNIPPET_UNFILED_RECORD_CONTAINER;
}