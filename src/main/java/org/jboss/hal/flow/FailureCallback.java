package org.jboss.hal.flow;

/**
 * A callback for the failed execution a list of {@linkplain Task asynchronous tasks}.
 *
 * @param <C> the type of the {@linkplain FlowContext context} shared between the tasks
 */
@FunctionalInterface
public interface FailureCallback<C extends FlowContext> {

    /**
     * Called when the execution of the {@linkplain Task asynchronous tasks} failed.
     *
     * @param context the context shared between the tasks
     * @param failure the reason why the execution failed
     */
    void failed(C context, String failure);
}
