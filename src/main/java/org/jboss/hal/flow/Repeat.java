package org.jboss.hal.flow;

import java.util.function.Predicate;

/**
 * An interface to control the {@linkplain Flow#repeat(FlowContext, Task) repeated} execution of an {@linkplain Task asynchronous task}.
 *
 * @param <C> the type of the {@linkplain FlowContext context} shared between tasks
 */
public interface Repeat<C extends FlowContext> extends Promisable<C>, Subscription<C> {

    /**
     * By default, the execution of {@linkplain Task tasks} fail fast.
     */
    boolean DEFAULT_FAIL_FAST = true;

    /**
     * By default, the interval between the iterations is 1 second.
     */
    long DEFAULT_INTERVAL = 1_000;

    /**
     * By default, the timeout for the loop is 10 seconds.
     */
    long DEFAULT_TIMEOUT = 10_000;

    /**
     * By default, the number of iterations are infinite.
     */
    int DEFAULT_ITERATIONS = -1;

    /**
     * The error message in case of a timeout.
     */
    String TIMEOUT_ERROR = "org.jboss.hal.flow.timeout";

    /**
     * The task is executed as long as the given predicate evaluates to {@code true}. Defaults to a precondition which always returns {@code true}.
     */
    Repeat<C> while_(Predicate<C> predicate);

    /**
     * Whether the execution of the {@linkplain Task task} should fail fast or fail last. Defaults to
     * {@value Repeat#DEFAULT_FAIL_FAST}.
     */
    Repeat<C> failFast(boolean failFast);

    /**
     * The interval in milliseconds between the iterations. Defaults to {@value Repeat#DEFAULT_INTERVAL} milliseconds.
     */
    Repeat<C> interval(long interval);

    /**
     * The timeout in milliseconds for the while loop. Defaults to {@value Repeat#DEFAULT_TIMEOUT} milliseconds.
     */
    Repeat<C> timeout(long timeout);

    /**
     * The maximal number of iterations of the loop. Defaults to an infinite loop ({@value Repeat#DEFAULT_ITERATIONS}).
     */
    Repeat<C> iterations(int iterations);
}
