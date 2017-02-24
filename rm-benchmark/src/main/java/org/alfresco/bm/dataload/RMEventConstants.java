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

public interface RMEventConstants
{
    public static final String FIELD_SITE_MANAGER = "siteManager";
    public static final String FIELD_CONTEXT = "context";
    public static final String FIELD_PATH = "path";
    public static final String FIELD_ID = "id";
    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_PASSWORD = "password";

    public static final String FIELD_UNFILED_ROOT_FOLDERS_TO_CREATE = "rootFoldersToCreate";
    public static final String FIELD_UNFILED_FOLDERS_TO_CREATE = "foldersToCreate";
    public static final String FIELD_RECORDS_TO_CREATE = "recordsToCreate";

    public static final String RECORD_FOLDER_NAME_IDENTIFIER = "bmFolder";
    public static final String CATEGORY_NAME_IDENTIFIER = "bmCateg";
    public static final String ROOT_CATEGORY_NAME_IDENTIFIER = "bmRootCateg";
    public static final String ROOT_UNFILED_RECORD_FOLDER_NAME_IDENTIFIER = "UnfiledRoot";
    public static final String UNFILED_RECORD_FOLDER_NAME_IDENTIFIER = "UnfiledFolder";
    public static final String RECORD_NAME_IDENTIFIER = "Record";

    public static final String FIELD_ROOT_CATEGORIES_TO_CREATE = "rootCategoriesToCreate";
    public static final String FIELD_CATEGORIES_TO_CREATE = "categoriesToCreate";
    public static final String FIELD_FOLDERS_TO_CREATE = "foldersToCreate";

    public static final int FILE_PLAN_LEVEL = 3;
    public static final int UNFILED_RECORD_CONTAINER_LEVEL = 4;
    public static final String UNFILED_CONTEXT = "unfiled";
    public static final String RECORD_FOLDER_CONTEXT = "recordFolder";
    public static final String RECORD_CATEGORY_CONTEXT = "recordCategory";

    public static final String PATH_SNIPPET_SITES = "Sites";
    public static final String PATH_SNIPPET_FILE_PLAN = "documentLibrary";
    public static final String PATH_SNIPPET_RM_SITE_ID = "rm";
    public static final String PATH_SNIPPET_UNFILED_RECORD_CONTAINER = "Unfiled Records";

    public static final String RECORD_CONTAINER_PATH = "/" + PATH_SNIPPET_SITES +
                                                        "/" + PATH_SNIPPET_RM_SITE_ID +
                                                        "/" + PATH_SNIPPET_FILE_PLAN;

    public static final String UNFILED_RECORD_CONTAINER_PATH = RECORD_CONTAINER_PATH +
                                                        "/" + PATH_SNIPPET_UNFILED_RECORD_CONTAINER;
}