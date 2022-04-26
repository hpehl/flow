package org.jboss.hal.flow;

/**
 * A callback for the outcome of the execution of {@linkplain Task asynchronous tasks}.
 * <p>
 * The context provides methods to check if the execution was {@linkplain FlowContext#successful() successful}, ran into a {@linkplain FlowContext#timeout() timeout} or {@linkplain FlowContext#failure() failed} with an {@linkplain FlowContext#failureReason() error}.
 *
 * @param <C> the type of the {@linkplain FlowContext context} shared between the tasks
 */
@FunctionalInterface
public interface FlowCallback<C extends FlowContext> {

    /**
     * Called when the execution of the {@linkplain Task asynchronous tasks} has been completed.
     * <p>
     * The context provides methods to check if the execution was {@linkplain FlowContext#successful() successful}, ran into a {@linkplain FlowContext#timeout() timeout} or {@linkplain FlowContext#failure() failed} with an {@linkplain FlowContext#failureReason() error}.
     *
     * @param context the context shared between the tasks
     */
    void finish(C context);
}
