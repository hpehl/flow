package org.jboss.hal.flow;

/**
 * Enum for the execution status of a flow method.
 */
public enum FlowStatus {

    /**
     * The execution has not yet started.
     */
    NOT_STARTED,

    /**
     * The execution is in progress.
     */
    IN_PROGRESS,

    /**
     * The execution was successful.
     */
    SUCCESS,

    /**
     * The execution ran into a timeout.
     */
    TIMEOUT,

    /**
     * The execution failed.
     */
    FAILURE
}
