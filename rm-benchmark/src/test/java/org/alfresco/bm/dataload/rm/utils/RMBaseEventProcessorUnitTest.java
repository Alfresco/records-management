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

package org.alfresco.bm.dataload.rm.utils;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.alfresco.bm.cm.FolderData;
import org.alfresco.bm.dataload.RMEventConstants;
import org.alfresco.bm.dataload.rm.fileplan.ScheduleFilePlanLoaders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for RMBaseEventProcessor
 *
 * @author Silviu Dinuta
 * @since 2.6
 */
@RunWith(MockitoJUnitRunner.class)
public class RMBaseEventProcessorUnitTest implements RMEventConstants
{
    /**
     * Used one class that extends abstract class RMBaseEventProcessor here in order to test utility methods, chose ScheduleFilePlanLoaders, but we can use any other class
     */
    @InjectMocks
    private ScheduleFilePlanLoaders testRMBaseEventProcessor;

    @Test
    public void testDistributeNumberOfRecords() throws Exception
    {
        Random random = new Random();
        int k = random.nextInt(10)+1;
        List<FolderData> listOfFolders = new ArrayList<FolderData>();
        int sum = 0;
        for(int i = 0;i < k;i++)
        {
            int filesCount = random.nextInt(5)+1;
            FolderData mockedFolder = mock(FolderData.class);
            when(mockedFolder.getFileCount()).thenReturn(Long.valueOf(filesCount));
            listOfFolders.add(mockedFolder);
            sum += filesCount;
        }
        int numberOfRecords = random.nextInt(100)+1;
        HashMap<FolderData, Integer> distributeNumberOfRecords = testRMBaseEventProcessor.distributeNumberOfRecords(listOfFolders, numberOfRecords);
        int expectedSum = sum + numberOfRecords;
        int actualSum = 0;
        Collection<Integer> values = distributeNumberOfRecords.values();
        for(Integer value : values)
        {
            actualSum += value;
        }
        assertEquals(expectedSum, actualSum);
    }
}