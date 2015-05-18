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

import org.alfresco.test.BaseUnitTest;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

/**
 * Classify action evaluator unit test
 *
 * @author Tuna Aksoy
 * @since 3.0
 */
public class ClassifyActionEvaluatorUnitTest extends BaseUnitTest
{
    /** Constants */
    private static final String NODE = "node";
    private static final String ASPECTS = "aspects";
    private static final String PROPERTIES = "properties";
    private static final String ASPECT_CLASSIFIED = "clf:classified";
    private static final String PROP_CURRENT_CLASSIFICATION = "clf:currentClassification";

    /** Classify action evaluator */
    private ClassifyActionEvaluator evaluator = new ClassifyActionEvaluator();

    /**
     * Given that the "classified" aspect does not exist
     * When evaluated
     * Then the result is true
     */
    @SuppressWarnings("unchecked")
    @Test
    public void classifiedAspectDoesNotExist()
    {
        JSONObject jsonObject = new JSONObject();
        JSONObject node = new JSONObject();
        JSONArray aspects = new JSONArray();
        node.put(ASPECTS, aspects);
        jsonObject.put(NODE, node);
        assertTrue(evaluator.evaluate(jsonObject));
    }

    /**
     * Given that the "classified" aspect exists but current classification is not set
     * When evaluated
     * Then the result is true
     */
    @SuppressWarnings("unchecked")
    @Test
    public void classifiedAspectWithoutCurrentClassification()
    {
        JSONObject jsonObject = new JSONObject();
        JSONObject node = new JSONObject();
        JSONArray aspects = new JSONArray();
        aspects.add(ASPECT_CLASSIFIED);
        node.put(ASPECTS, aspects);
        jsonObject.put(NODE, node);
        assertTrue(evaluator.evaluate(jsonObject));
    }

   /**
    * Given that the "classified" aspect exists and the current classification is set
    * When evaluated
    * Then the result is false
    */
    @SuppressWarnings("unchecked")
    @Test
    public void classifiedAspectWithCurrentClassification()
    {
        JSONObject jsonObject = new JSONObject();
        JSONObject node = new JSONObject();
        JSONArray aspects = new JSONArray();
        aspects.add(ASPECT_CLASSIFIED);
        JSONObject properties = new JSONObject();
        properties.put(PROP_CURRENT_CLASSIFICATION, generateText());
        node.put(ASPECTS, aspects);
        node.put(PROPERTIES, properties);
        jsonObject.put(NODE, node);
        assertFalse(evaluator.evaluate(jsonObject));
    }
}
