package org.alfresco.bm.dataload.rm.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.bm.dataload.rm.exceptions.DuplicateRecordException;
import org.alfresco.bm.dataload.rm.exceptions.RecordNotFoundException;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.DuplicateKeyException;
import com.mongodb.QueryBuilder;

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
    public static final String FIELD_RANDOMIZER = "randomizer";

    public RecordService(DB db, String collection)
    {
        super(db, collection);
    }

    @Override
    protected void setupIndexes()
    {
        createMongoIndex("uidIdCtx", false, Arrays.asList(FIELD_CONTEXT, FIELD_PARENT_PATH));
        DBObject idxRecordStateParentPathCount = BasicDBObjectBuilder.start()
                    .add(FIELD_STATE, 1)
                    .add(FIELD_PARENT_PATH, 1)
                    .get();
            DBObject optRecordStateParentPathCount = BasicDBObjectBuilder.start()
                    .add("name", "idxRecordStateParentPathCount")
                    .add("unique", Boolean.FALSE)
                    .get();
            collection.createIndex(idxRecordStateParentPathCount, optRecordStateParentPathCount);

            DBObject idxRecordRand = BasicDBObjectBuilder.start()
                        .add(FIELD_STATE, 1)
                        .add(FIELD_PARENT_PATH, 1)
                        .add(FIELD_RANDOMIZER, 1)
                        .get();
            DBObject optRecordRand = BasicDBObjectBuilder.start()
                        .add("name", "idxRecordRand")
                        .add("unique", Boolean.FALSE)
                        .get();
            collection.createIndex(idxRecordRand, optRecordRand);
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
                .add(FIELD_RANDOMIZER, record.getRandomizer())
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
        Integer randomizer = (Integer)dbObject.get(FIELD_RANDOMIZER);

        RecordData record = new RecordData(id, RecordContext.valueOf(context), name, parentPath, inPlacePath, ExecutionState.valueOf(state));
        record.setRandomizer(randomizer);
        return record;
    }

    /**
     * Helper to get a random record from MongoDb that has the parent path in specified parent paths, or from all existent records if listOfParentPaths is null or empty.
     *
     * @param state - record execution state to search for
     * @param listOfParentPaths - list of parent paths to search in
     * @return a random record from MongoDb that has the parent path in specified parent paths, or from all existent records if listOfParentPaths is null or empty.
     */
    public RecordData getRandomRecord(String state, List<String> listOfParentPaths)
    {
        if (state == null)
        {
            throw new IllegalArgumentException();
        }
        QueryBuilder queryObjBuilder = QueryBuilder.start();

        queryObjBuilder.and(FIELD_STATE).is(state);
        if (listOfParentPaths != null && listOfParentPaths.size() > 0)
        {
            queryObjBuilder.and(FIELD_PARENT_PATH).in(listOfParentPaths);
        }
        DBObject queryObj = queryObjBuilder.get();

        int random = (int) (Math.random() * (double) 1e6);
        queryObj.put(FIELD_RANDOMIZER,
                new BasicDBObject("$gte", Integer.valueOf(random)));

        DBObject recordDataObj = collection.findOne(queryObj);
        if (recordDataObj == null)
        {
            recordDataObj = collection.findOne();
            queryObj.put(FIELD_RANDOMIZER,
                    new BasicDBObject("$lt", random));
            recordDataObj = collection.findOne(queryObj);
        }

        RecordData record = fromDBObject(recordDataObj);
        return record;
    }

    /**
     * Helper for counting the records with specified execution state, that has the parent path in specified list or from all existent records if listOfParentPaths is null or empty.
     *
     * @param state - record execution state to search for
     * @param listOfParentPaths - list of parent paths to search in
     * @return for counting the records with specified execution state, that has the parent path in specified list or from all existent records if listOfParentPaths is null or empty.
     */
    public long getRecordCountInSpecifiedPaths(String state, List<String> listOfParentPaths)
    {
        if (state == null)
        {
            throw new IllegalArgumentException();
        }
        QueryBuilder queryObjBuilder = QueryBuilder.start();

        queryObjBuilder.and(FIELD_STATE).is(state);
        if (listOfParentPaths != null && listOfParentPaths.size() > 0)
        {
            queryObjBuilder.and(FIELD_PARENT_PATH).in(listOfParentPaths);
        }
        DBObject queryObj = queryObjBuilder.get();
        long count = collection.count(queryObj);
        return count;
    }

    /**
     * Helper to return a list or records with specified execution state that are in specified parent paths. If listOfParentPaths is null or empty then all records with
     * specified execution state will be returned. Supports pagination.
     *
     * @param state - record execution state to search for
     * @param listOfParentPaths - list of parent paths to search in
     * @param skip - the number of entries to skip
     * @param limit - the number of entries to return
     * @return to return a list of records with specified execution state that are in specified parent paths. If listOfParentPaths is null or empty then all records with
     * specified execution state will be returned.
     */
    public List<RecordData> getRecordsInPaths(String state, List<String> listOfParentPaths, int skip, int limit)
    {
        if (state == null)
        {
            throw new IllegalArgumentException();
        }
        QueryBuilder queryObjBuilder = QueryBuilder.start();

        queryObjBuilder.and(FIELD_STATE).is(state);
        if (listOfParentPaths != null && listOfParentPaths.size() > 0)
        {
            queryObjBuilder.and(FIELD_PARENT_PATH).in(listOfParentPaths);
        }
        DBObject queryObj = queryObjBuilder.get();

        DBCursor cursor = collection.find(queryObj).skip(skip).limit(limit);
        List<RecordData> results = fromDBCursor(cursor);
        return results;
    }

    /**
     * Turn a cursor into an array of API-friendly objects
     */
    private List<RecordData> fromDBCursor(DBCursor cursor)
    {
        int count = cursor.count();
        try
        {
            List<RecordData> recordDatas = new ArrayList<>(count);
            while (cursor.hasNext())
            {
                DBObject recordDataObj = cursor.next();
                RecordData recordData = fromDBObject(recordDataObj);
                recordDatas.add(recordData);
            }
            // Done
            return recordDatas;
        }
        finally
        {
            cursor.close();
        }
    }
}
