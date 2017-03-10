package org.alfresco.bm.dataload.rm.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

import org.alfresco.bm.dataload.rm.exceptions.DuplicateRecordException;
import org.alfresco.bm.dataload.rm.services.ExecutionState;
import org.alfresco.bm.dataload.rm.services.RecordContext;
import org.alfresco.bm.dataload.rm.services.RecordData;
import org.alfresco.bm.dataload.rm.services.RecordService;
import org.alfresco.mongo.MongoDBForTestsFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.mongodb.DB;

/**
 * Integration test for RecordService
 * 
 * @author Ana Bozianu
 * @since 2.6
 */
@RunWith(JUnit4.class)
public class RecordServiceTest
{
    private RecordService recordService;

    @Before
    public void setUp() throws Exception
    {
        MongoDBForTestsFactory mongoFactory = new MongoDBForTestsFactory();
        DB db = mongoFactory.getObject();
        recordService = new RecordService(db, "records");
    }

    @Test(expected = DuplicateRecordException.class)
    public void testRejectDuplicateRecordId()
    {
        String duplicateId = "test_id";

        RecordData record1 = new RecordData(duplicateId, RecordContext.IN_PLACE_RECORD, "test_name_1", "test_parentPath_1", "test_inPlacePath_1" , ExecutionState.SCHEDULED);
        recordService.createRecord(record1);

        RecordData record2 = new RecordData(duplicateId, RecordContext.RECORD, "test_name_2", "test_parentPath_2", "test_inPlacePath_2", ExecutionState.SUCCESS);
        recordService.createRecord(record2);

        doubleCheckOverwrittenMethods_notEqual(record1, record2);
    }

    @Test
    public void testAllowDuplicateContext()
    {
        RecordData record1 = new RecordData("test_id_1", RecordContext.IN_PLACE_RECORD, "test_name_1", "test_parentPath_1", "test_inPlacePath_1", ExecutionState.SCHEDULED);
        recordService.createRecord(record1);

        RecordData record2 = new RecordData("test_id_2", RecordContext.IN_PLACE_RECORD, "test_name_2", "test_parentPath_2", "test_inPlacePath_2", ExecutionState.SUCCESS);
        recordService.createRecord(record2);

        doubleCheckOverwrittenMethods_notEqual(record1, record2);
    }

    @Test
    public void testAllowDuplicateName()
    {
        String duplicateName = "test_name";

        RecordData record1 = new RecordData("test_id_1", RecordContext.IN_PLACE_RECORD, duplicateName, "test_parentPath_1", "test_inPlacePath_1", ExecutionState.SCHEDULED);
        recordService.createRecord(record1);

        RecordData record2 = new RecordData("test_id_2", RecordContext.RECORD, duplicateName, "test_parentPath_2", "test_inPlacePath_2", ExecutionState.SUCCESS);
        recordService.createRecord(record2);

        doubleCheckOverwrittenMethods_notEqual(record1, record2);
    }

    @Test
    public void testAllowDuplicateParentPath()
    {
        String duplicateParentPath = "test_parentPath";

        RecordData record1 = new RecordData("test_id_1", RecordContext.IN_PLACE_RECORD, "test_name_1", duplicateParentPath, "test_inPlacePath_1", ExecutionState.SCHEDULED);
        recordService.createRecord(record1);

        RecordData record2 = new RecordData("test_id_2", RecordContext.RECORD, "test_name_2", duplicateParentPath, "test_inPlacePath_2", ExecutionState.SUCCESS);
        recordService.createRecord(record2);

        doubleCheckOverwrittenMethods_notEqual(record1, record2);
    }

    @Test
    public void testAllowDuplicateInPlacePath()
    {
        String duplicateInPlacePath = "test_inPlacePath";

        RecordData record1 = new RecordData("test_id_1", RecordContext.IN_PLACE_RECORD, "test_name_1", "test_parentPath_1", duplicateInPlacePath, ExecutionState.SCHEDULED);
        recordService.createRecord(record1);

        RecordData record2 = new RecordData("test_id_2", RecordContext.RECORD, "test_name_2", "test_parentPath_2", duplicateInPlacePath, ExecutionState.SUCCESS);
        recordService.createRecord(record2);

        doubleCheckOverwrittenMethods_notEqual(record1, record2);
    }

    @Test
    public void testAllowDuplicateState()
    {
        RecordData record1 = new RecordData("test_id_1", RecordContext.IN_PLACE_RECORD, "test_name_1", "test_parentPath_1", "test_inPlacePath_1", ExecutionState.SCHEDULED);
        recordService.createRecord(record1);

        RecordData record2 = new RecordData("test_id_2", RecordContext.RECORD, "test_name_2", "test_parentPath_2", "test_inPlacePath_2", ExecutionState.SCHEDULED);
        recordService.createRecord(record2);

        doubleCheckOverwrittenMethods_notEqual(record1, record2);
    }

    /**
     * Given we created a record and store it in mongo
     * When we retrieve it by id
     * The all the properties are correctly retrieved
     */
    @Test
    public void testRetrieveRecordObject()
    {
        /*
         * Given
         */
        RecordData record1 = new RecordData("test_id", RecordContext.IN_PLACE_RECORD, "test_name", "test_parentPath", "test_inPlaceRecord", ExecutionState.SCHEDULED);
        recordService.createRecord(record1);

        /*
         * When
         */
        RecordData record2 = recordService.getRecord(record1.getId());

        /*
         * Then
         */
        assertEquals(record1.getId(), record2.getId());
        assertEquals(record1.getContext(), record2.getContext());
        assertEquals(record1.getName(), record2.getName());
        assertEquals(record1.getParentPath(), record2.getParentPath());
        assertEquals(record1.getInPlacePath(), record2.getInPlacePath());

        doubleCheckOverwrittenMethods_equal(record1, record2);
    }

    /**
     * Given a record object not stored in the database
     * When calling the update method
     * Then no error is returned and the record is not stored in the database
     */
    @Test
    public void testUpdateNonExistingRecord()
    {
        RecordData nonExistingRecord = new RecordData("non_existing_id", RecordContext.IN_PLACE_RECORD, "test_name", "test_parentPath", "test_inPlaceRecord", ExecutionState.SUCCESS);
        recordService.updateRecord(nonExistingRecord);
        assertNull(recordService.getRecordOrNull(nonExistingRecord.getId()));
    }

    /**
     * Double check equals, hashCode and toString methods behave correctly for non equal records
     * 
     * @param record1
     * @param record2
     */
    private void doubleCheckOverwrittenMethods_notEqual(RecordData record1, RecordData record2)
    {
        //double check equals, hashCode and toString methods
        assertFalse(record1.equals(record2));
        assertNotEquals(record1.hashCode(), record2.hashCode());
        assertNotEquals(record1.toString(), record2.toString());
    }

    /**
     * Double check equals, hashCode and toString methods behave correctly for equal records
     * 
     * @param record1
     * @param record2
     */
    private void doubleCheckOverwrittenMethods_equal(RecordData record1, RecordData record2)
    {
        //double check equals, hashCode and toString methods
        assertTrue(record1.equals(record2));
        assertEquals(record1.hashCode(), record2.hashCode());
        assertEquals(record1.toString(), record2.toString());
    }
}
