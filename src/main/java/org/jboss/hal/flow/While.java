package org.jboss.hal.flow;

import java.util.function.Predicate;

/**
 * An interface to control the {@linkplain Flow#while_(FlowContext, Task, Predicate) repeated} execution of an {@linkplain Task asynchronous task}.
 *
 * @param <C> the type of the {@linkplain FlowContext context} shared between tasks
 */
public interface While<C extends FlowContext> extends Promisable<C>, Subscription<C> {

    /**
     * By default, the execution of {@linkplain Task tasks} fails fast.
     */
    boolean DEFAULT_FAIL_FAST = true;

    /**
     * By default, the interval between the iterations is 1 second.
     */
    long DEFAULT_INTERVAL = 1_000;

    /**
     * By default, the timeout for the while loop is 10 seconds.
     */
    long DEFAULT_TIMEOUT = 10_000;

    /**
     * The error timeout.
     */
    String TIMEOUT_ERROR = "org.jboss.hal.flow.timeout";

    /**
     * Whether the execution of {@linkplain Task tasks} should fail fast or fail last.
     */
    While<C> failFast(boolean failFast);

    /**
     * The interval in milliseconds between the iterations.
     */
    While<C> interval(long interval);

    /**
     * The timeout in milliseconds for the while loop.
     */
    While<C> timeout(long timeout);
}
