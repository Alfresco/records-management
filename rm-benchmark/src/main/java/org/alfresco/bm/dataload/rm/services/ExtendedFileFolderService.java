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

package org.alfresco.bm.dataload.rm.services;

import org.alfresco.bm.cm.FileFolderService;

import com.mongodb.DB;

/**
 * Helper class, used to retain the number or folder that still need to be created. Extended from FileFolderService just to add possibility to drop collection.
 *
 * @author Silviu Dinuta
 * @since 2.6
 */
public class ExtendedFileFolderService extends FileFolderService
{

    public ExtendedFileFolderService(DB db, String collection)
    {
        super(db, collection);
    }

    public void drop()
    {
        this.collection.drop();
    }
}
