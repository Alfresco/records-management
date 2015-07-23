/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_rm_share.evaluator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.internal.matchers.NotNull.NOT_NULL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Classify action evaluator unit test
 *
 * @author Tuna Aksoy
 * @since 3.0.a
 */
public class ClassifyActionEvaluatorUnitTest
{
    /** Constants */
    private static final String NODE = "node";
    private static final String ASPECTS = "aspects";
    private static final String PROPERTIES = "properties";
    private static final String ASPECT_CLASSIFIED = "clf:classified";
    private static final String PROP_CURRENT_CLASSIFICATION = "clf:currentClassification";
    private static final String HAS_CURRENT_USER_CLEARANCE = "hasCurrentUserClearance";

    /** Classify action evaluator */
    private ClassifyActionEvaluator evaluator = new ClassifyActionEvaluator();

    @Mock private JSONObject mockedJsonObject;
    @Mock private JSONObject mockedNodeJsonObject;
    @Mock private JSONObject mockedPropertiesJsonObject;
    @Mock private JSONArray mockedAspectsJsonArray;

    @Before
    public void setUp()
    {
        initMocks(this);
    }

    /**
     * Given that the node json object is not available
     * When evaluated
     * Then the result is false
     */
    @Test
    public void nodeObjectIsNull()
    {
        when(mockedJsonObject.get(NODE)).thenReturn(null);

        assertFalse(evaluator.evaluate(mockedJsonObject));
    }

    /**
     * Given that the current user does not have any clearance set
     * When evaluated
     * Then the result is false
     */
    @Test
    public void userDoesNotHaveClearance()
    {
        when(mockedJsonObject.get(NODE)).thenReturn(mockedNodeJsonObject);
        when(mockedNodeJsonObject.get(HAS_CURRENT_USER_CLEARANCE)).thenReturn(false);

        assertFalse(evaluator.evaluate(mockedJsonObject));
    }

    /**
     * Given that the "classified" aspect does not exist
     * When evaluated
     * Then the result is true
     */
    @Test
    public void classifiedAspectDoesNotExist()
    {
        when(mockedJsonObject.get(NODE)).thenReturn(mockedNodeJsonObject);
        when(mockedNodeJsonObject.get(HAS_CURRENT_USER_CLEARANCE)).thenReturn(true);
        when(mockedNodeJsonObject.get(ASPECTS)).thenReturn(mockedAspectsJsonArray);
        when(mockedAspectsJsonArray.contains(ASPECT_CLASSIFIED)).thenReturn(false);

        assertTrue(evaluator.evaluate(mockedJsonObject));
    }

    /**
     * Given that the "classified" aspect exists but current classification is not set
     * When evaluated
     * Then the result is true
     */
    @Test
    public void classifiedAspectWithoutCurrentClassification()
    {
        when(mockedJsonObject.get(NODE)).thenReturn(mockedNodeJsonObject);
        when(mockedNodeJsonObject.get(HAS_CURRENT_USER_CLEARANCE)).thenReturn(true);
        when(mockedNodeJsonObject.get(ASPECTS)).thenReturn(mockedAspectsJsonArray);
        when(mockedAspectsJsonArray.contains(ASPECT_CLASSIFIED)).thenReturn(true);
        when(mockedJsonObject.get(PROP_CURRENT_CLASSIFICATION)).thenReturn(null);

        assertTrue(evaluator.evaluate(mockedJsonObject));
    }

   /**
    * Given that the "classified" aspect exists and the current classification is set
    * When evaluated
    * Then the result is false
    */
    @Test
    public void classifiedAspectWithCurrentClassification()
    {
        when(mockedJsonObject.get(NODE)).thenReturn(mockedNodeJsonObject);
        when(mockedNodeJsonObject.get(HAS_CURRENT_USER_CLEARANCE)).thenReturn(true);
        when(mockedNodeJsonObject.get(ASPECTS)).thenReturn(mockedAspectsJsonArray);
        when(mockedAspectsJsonArray.contains(ASPECT_CLASSIFIED)).thenReturn(true);
        when(mockedNodeJsonObject.get(PROPERTIES)).thenReturn(mockedPropertiesJsonObject);
        when(mockedPropertiesJsonObject.get(PROP_CURRENT_CLASSIFICATION)).thenReturn(NOT_NULL);

        assertFalse(evaluator.evaluate(mockedJsonObject));
    }
}
