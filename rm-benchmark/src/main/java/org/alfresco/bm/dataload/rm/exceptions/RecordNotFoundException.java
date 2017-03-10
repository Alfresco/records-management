package org.alfresco.bm.dataload.rm.exceptions;

/**
 * The record does not exist in the database
 * 
 * @author Ana Bozianu
 * @since 2.6
 *
 */
public class RecordNotFoundException extends RuntimeException
{
    private static final long serialVersionUID = -2152635518689808055L;

    public RecordNotFoundException(String id)
    {
        super("Record with id " + id + " could not be found.");
    }
}
