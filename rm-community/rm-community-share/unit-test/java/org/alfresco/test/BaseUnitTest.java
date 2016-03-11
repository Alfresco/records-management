package org.alfresco.test;

import java.util.UUID;

/**
 * Base unit test
 * 
 * @author Roy Wetherall
 * @since 2.3
 */
public class BaseUnitTest
{
    /**
     * Helper method to generate text value
     * 
     * @return  String  generated text value
     */
    protected String generateText()
    {
        return UUID.randomUUID().toString();
    }

}
