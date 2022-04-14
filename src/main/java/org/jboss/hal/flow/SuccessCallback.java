package org.jboss.hal.flow;

/**
 * A callback for the successful execution of a list of {@linkplain Task asynchronous tasks}.
 *
 * @param <C> the type of the {@linkplain FlowContext context} shared between the tasks
 */
@FunctionalInterface
public interface SuccessCallback<C extends FlowContext> {

    /**
     * Called when the execution of the {@linkplain Task asynchronous tasks} has been successful.
     *
     * @param context the context shared between the tasks
     */
    void success(C context);
}
