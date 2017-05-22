package org.alfresco.bm.dataload.rm.services;

import org.alfresco.bm.cm.FileFolderService;

import com.mongodb.DB;

/**
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
