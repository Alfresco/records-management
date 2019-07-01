package org.alfresco.bm.dataload.rm.services;

/**
 * The different states of a test execution during a benchmark test.
 * Used to lock a resources between the schedule and execute events.
 *
 * @author Ana Bozianu
 * @since 2.6
 */
public enum ExecutionState
{
    SCHEDULED,
    UNFILED_RECORD_DECLARED,
    UNFILED_RECORD_SCHEDULED_FOR_FILING,
    RECORD_FILED,
    FAILED
}
