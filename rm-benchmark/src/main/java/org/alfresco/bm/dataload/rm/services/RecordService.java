package org.alfresco.bm.dataload.rm.services;

import java.util.Arrays;

import org.alfresco.bm.dataload.rm.exceptions.DuplicateRecordException;
import org.alfresco.bm.dataload.rm.exceptions.RecordNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.DuplicateKeyException;

/**
 * Service to keep track of records.
 *
 * @author Ana Bozianu
 * @since 2.6
 */
public class RecordService extends BaseMongoService
{
    public static final String FIELD_ID = "_id";
    public static final String FIELD_CONTEXT = "context";
    public static final String FIELD_PARENT_PATH = "parentPath";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_IN_PLACE_PATH = "inPlacePath";
    public static final String FIELD_STATE = "state";

    private static Log logger = LogFactory.getLog(RecordService.class);

    public RecordService(DB db, String collection)
    {
        super(db, collection);
    }

    @Override
    protected void setupIndexes()
    {
        createMongoIndex("uidIdCtx", false, Arrays.asList(FIELD_CONTEXT, FIELD_PARENT_PATH));
    }

    /**
     * Create a new record entry
     * @param record  data for the record to create
     * @throws DuplicateRecordException  if a record with the same id already exists
     */
    public void createRecord(RecordData record) throws DuplicateRecordException
    {
        DBObject insertObj = BasicDBObjectBuilder.start()
                .add(FIELD_ID, record.getId())
                .add(FIELD_CONTEXT, record.getContext().name())
                .add(FIELD_PARENT_PATH, record.getParentPath())
                .add(FIELD_NAME, record.getName())
                .add(FIELD_IN_PLACE_PATH, record.getInPlacePath())
                .add(FIELD_STATE, record.getExecutionState().name()).get();

        try
        {
            collection.insert(insertObj);
        }
        catch (DuplicateKeyException e)
        {
            throw new DuplicateRecordException(record.getId(), e);
        }
    }

    /**
     * Updates a record's info
     * If the record doesn't exist nothing happens
     * 
     * @param newRecordData  info of the record to update
     */
    public void updateRecord(RecordData newRecordData)
    {
        DBObject queryObj = getRecordByIdQuery(newRecordData.getId());
        DBObject updateObj = BasicDBObjectBuilder.start()
                .push("$set")
                    .add(FIELD_NAME, newRecordData.getName())
                    .add(FIELD_PARENT_PATH, newRecordData.getParentPath())
                    .add(FIELD_STATE, newRecordData.getExecutionState().name())
                .pop()
                .get();
        collection.findAndModify(queryObj, null, null, false, updateObj, true, false);
    }

    /**
     * Retrieve a record by ID
     * 
     * @param id  the id of the record to retrieve
     * @return the record info
     * @throws RecordNotFoundException if a record with this id doesn't exist
     */
    public RecordData getRecord(String id) throws RecordNotFoundException
    {
        RecordData record = getRecordOrNull(id);
        if(record == null)
        {
            throw new RecordNotFoundException(id);
        }
        return record;
    }

    /**
     * Retrieve a record by ID
     * 
     * @param id  the id of the record to retrieve
     * @return the record info or null if the record doesn't exist
     */
    public RecordData getRecordOrNull(String id)
    {
        DBObject queryObj = getRecordByIdQuery(id);
        DBObject folderDataObj = collection.findOne(queryObj);
        RecordData record = fromDBObject(folderDataObj);
        return record;
    }

    /**
     * Utility method that returns a Mongo query object to search a record by id
     * 
     * @param id the id of the record to search for
     * @return  a mongo query that can be used to search for the provided record id
     */
    private DBObject getRecordByIdQuery(String id)
    {
        return BasicDBObjectBuilder.start()
                .add(FIELD_ID, id)
                .get();
    }

    /**
     * Helper to convert a Mongo DBObject into the API consumable object
     * <p/>
     * Note that <tt>null</tt> is handled as a <tt>null</tt> return.
     */
    private RecordData fromDBObject(DBObject dbObject)
    {
        if(dbObject == null)
        {
            return null;
        }

        String id = (String) dbObject.get(FIELD_ID);
        String context = (String) dbObject.get(FIELD_CONTEXT);
        String parentPath = (String) dbObject.get(FIELD_PARENT_PATH);
        String inPlacePath = (String) dbObject.get(FIELD_IN_PLACE_PATH);
        String name = (String) dbObject.get(FIELD_NAME);
        String state = (String)dbObject.get(FIELD_STATE);
        RecordData record = new RecordData(id, RecordContext.valueOf(context), name, parentPath, inPlacePath, ExecutionState.valueOf(state));
        return record;
    }

}
