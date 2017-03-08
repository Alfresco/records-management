package org.alfresco.bm.dataload.rm.exceptions;

/**
 * An event for this entity has already been scheduled
 * 
 * @author Ana Bozianu
 * @since 2.6
 */
public class EventAlreadyScheduledException extends RuntimeException
{
    private static final long serialVersionUID = -1987897752302161201L;

    public EventAlreadyScheduledException(String eventName, String entityId)
    {
        super("Event " + eventName + " for entity " + entityId + " has already been scheduled.");
    }
    
}
