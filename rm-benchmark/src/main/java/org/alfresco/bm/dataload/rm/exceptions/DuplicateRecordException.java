package org.alfresco.bm.dataload.rm.exceptions;

/**
 * A record with the same unique properties already exists in the DB
 * 
 * @author Ana Bozianu
 * @since 2.6
 */
public class DuplicateRecordException extends RuntimeException
{
    private static final long serialVersionUID = -5852958071531628292L;

    public DuplicateRecordException(String id, Exception cause)
    {
        super("Duplicate record with id " + id, cause);
    }
}
