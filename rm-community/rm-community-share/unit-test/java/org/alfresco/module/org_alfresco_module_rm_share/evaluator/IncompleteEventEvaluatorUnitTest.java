/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2020 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software.
 * -
 * If the software was purchased under a paid Alfresco license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 * -
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * -
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * -
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.alfresco.module.org_alfresco_module_rm_share.evaluator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Ross Gale
 * @since 2.7
 */
public class IncompleteEventEvaluatorUnitTest
{
    @Mock
    private JSONObject jsonObject;

    @Mock
    private JSONObject node;

    @Mock
    private HashMap rmNode;

    @Mock
    private HashMap properties;

    private IncompleteEventEvaluator incompleteEventEvaluator;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        incompleteEventEvaluator = new IncompleteEventEvaluator();
        when(jsonObject.get("node")).thenReturn(node);
        when(node.get("rmNode")).thenReturn(rmNode);
        when(rmNode.get("properties")).thenReturn(properties);
    }

    /**
     * Check the evaluate method when no value set in the combine property
     */
    @Test
    public void testEvaluateReturnsTrueWhenCombineOptionNotSelected()
    {
        assertTrue(incompleteEventEvaluator.evaluate(jsonObject));
    }

    /**
     * Check the evaluate method when the combine property is set to false
     */
    @Test
    public void testEvaluateReturnsTrueWhenCombineOptionSetToFalse()
    {
        when(properties.get("combineDispositionStepConditions")).thenReturn(false);
        assertTrue(incompleteEventEvaluator.evaluate(jsonObject));
    }

    /**
     * Check the evaluate method when the combine property set to true without an incomplete event
     */
    @Test
    public void testEvaluateReturnsTrueWhenCombineSelectedAndNoIncompleteEvents()
    {
        when(properties.get("combineDispositionStepConditions")).thenReturn(true);
        when(properties.containsKey("incompleteDispositionEvent")).thenReturn(false);
        assertTrue(incompleteEventEvaluator.evaluate(jsonObject));
    }

    /**
     * Check the evaluate method when the combine property set to true with an incomplete result
     */
    @Test
    public void testEvaluateReturnsFalseWhenCombineSelectedAndIncompleteEventPresent()
    {
        when(properties.get("combineDispositionStepConditions")).thenReturn(true);
        when(properties.containsKey("incompleteDispositionEvent")).thenReturn(true);
        when(properties.get("dispositionEventCombination")).thenReturn("and");
        assertFalse(incompleteEventEvaluator.evaluate(jsonObject));
    }

    /**
     * Check the evaluate method when the combine property is null
     */
    @Test
    public void testEvaluateReturnsTrueWhenCombineSelectedIsNull()
    {
        when(properties.get("combineDispositionStepConditions")).thenReturn(null);
        assertTrue(incompleteEventEvaluator.evaluate(jsonObject));
    }
}
