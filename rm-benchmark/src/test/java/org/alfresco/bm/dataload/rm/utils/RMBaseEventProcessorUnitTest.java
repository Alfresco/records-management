package org.alfresco.bm.dataload.rm.utils;

import java.util.HashMap;

import org.alfresco.bm.dataload.RMEventConstants;
import org.alfresco.bm.dataload.ScheduleFilePlanLoaders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for RMBaseEventProcessor
 *
 * @author Silviu Dinuta
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class RMBaseEventProcessorUnitTest implements RMEventConstants
{
    /**
     * Used one class that extends abstract class RmBaseEventProcessor here in order to test utility methods, chose ScheduleFilePlanLoaders, but we can use any other class
     */
    @InjectMocks
    private ScheduleFilePlanLoaders testRmBaseEventProcessor;

    @Test
    public void test() throws Exception
    {
        HashMap<Integer, Integer> distributeNumberOfRecords = testRmBaseEventProcessor.distributeNumberOfRecords(5, 4);
        System.out.println(distributeNumberOfRecords);
    }
}