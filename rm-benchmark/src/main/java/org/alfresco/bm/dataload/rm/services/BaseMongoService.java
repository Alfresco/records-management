package org.alfresco.bm.dataload.rm.services;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;

/**
 * Base class for Mongo services
 * 
 * @author Ana Bozianu
 * @since 2.6
 */
public abstract class BaseMongoService implements InitializingBean
{
    public static final int INDEX_SORT_ASSCENDING = 1;

    /** The collection the current service uses to store entities */
    protected final DBCollection collection;

    public BaseMongoService(DB db, String collection)
    {
        this.collection = db.getCollection(collection);
    }

    /**
     * Utility method that creates a Mongo DB index
     * 
     * @param indexName  the name of the index to create
     * @param unique  whether the index is unique. A unique index causes MongoDB to reject all documents 
     *                that contain a duplicate value for the indexed field.
     * @param fields  the fields to add in the compound index. 
     */
    protected void createMongoIndex(String indexName, boolean unique, List<String> fields)
    {
        BasicDBObjectBuilder keysBuilder = BasicDBObjectBuilder.start();
        for(String field: fields)
        {
            keysBuilder.add(field, INDEX_SORT_ASSCENDING);
        }
        DBObject options = BasicDBObjectBuilder.start()
                .add("name", indexName)
                .add("unique", unique)
                .get();
        collection.createIndex(keysBuilder.get(), options);
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        collection.setWriteConcern(WriteConcern.ACKNOWLEDGED);
        setupIndexes();
    }

    /**
     * Set Mongo collection indexes
     */
    protected abstract void setupIndexes();
}
