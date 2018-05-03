/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2018 Alfresco Software Limited
 * %%
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
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
        when(properties.containsKey("combineDispositionStepConditions")).thenReturn(false);
        assertTrue(incompleteEventEvaluator.evaluate(jsonObject));
    }

    /**
     * Check the evaluate method when the combine property is set to false
     */
    @Test
    public void testEvaluateReturnsTrueWhenCombineOptionSetToFalse()
    {
        when(properties.containsKey("combineDispositionStepConditions")).thenReturn(true);
        when(properties.get("combineDispositionStepConditions")).thenReturn(false);
        assertTrue(incompleteEventEvaluator.evaluate(jsonObject));
    }

    /**
     * Check the evaluate method when the combine property set to true without an incomplete event
     */
    @Test
    public void testEvaluateReturnsTrueWhenCombineSelectedAndNoIncompleteEvents()
    {
        when(properties.containsKey("combineDispositionStepConditions")).thenReturn(true);
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
        when(properties.containsKey("combineDispositionStepConditions")).thenReturn(true);
        when(properties.get("combineDispositionStepConditions")).thenReturn(true);
        when(properties.containsKey("incompleteDispositionEvent")).thenReturn(true);
        assertFalse(incompleteEventEvaluator.evaluate(jsonObject));
    }
}
