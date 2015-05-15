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
    /** Classify action evaluator */
    private ClassifyActionEvaluator evaluator = new ClassifyActionEvaluator();

    /**
     * Given that the "node" json object does not exist
     * When evaluated
     * Then the result is false
     */
    @SuppressWarnings("unchecked")
    @Test
    public void nodeObjectDoesNotExist()
    {
        JSONObject jsonObject = new JSONObject();
        JSONObject node = new JSONObject();
        node.put("hasClearance", true);
        jsonObject.put("node1", node);

        assertFalse(evaluator.evaluate(jsonObject));
    }

    /**
     * Given that the "hasClearance" key exists
     * And the it's value is true
     * When evaluated
     * Then the result is true
     */
    @SuppressWarnings("unchecked")
    @Test
    public void hasClearanceValueTrue()
    {
        JSONObject jsonObject = new JSONObject();
        JSONObject node = new JSONObject();
        node.put("hasClearance", true);
        jsonObject.put("node", node);

        assertTrue(evaluator.evaluate(jsonObject));
    }

    /**
     * Given that the "hasClearance" key exists
     * And the it's value is false
     * When evaluated
     * Then the result is false
     */
    @SuppressWarnings("unchecked")
    @Test
    public void hasClearanceValueFalse()
    {
        JSONObject jsonObject = new JSONObject();
        JSONObject node = new JSONObject();
        node.put("hasClearance", false);
        jsonObject.put("node", node);

        assertFalse(evaluator.evaluate(jsonObject));
    }

    /**
     * Given that the "hasClearance" key exists
     * And the it's value is a string
     * When evaluated
     * A {@link ClassCastException} will be thrown
     */
    @SuppressWarnings("unchecked")
    @Test (expected=ClassCastException.class)
    public void hasClearanceValueString()
    {
        JSONObject jsonObject = new JSONObject();
        JSONObject node = new JSONObject();
        node.put("hasClearance", generateText());
        jsonObject.put("node", node);

        evaluator.evaluate(jsonObject);
    }

    /**
     * Given that the "hasClearance" key exists
     * And the it's value is <code>null</code>
     * When evaluated
     * Then the result is false
     */
    @SuppressWarnings("unchecked")
    @Test
    public void hasClearanceValueFalseNull()
    {
        JSONObject jsonObject = new JSONObject();
        JSONObject node = new JSONObject();
        node.put("hasClearance", null);
        jsonObject.put("node", node);

        assertFalse(evaluator.evaluate(jsonObject));
    }
}
