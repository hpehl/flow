package org.jboss.hal.flow;

import java.util.List;

/**
 * An interface to control the {@linkplain Flow#parallel(FlowContext, List) parallel} and {@linkplain Flow#sequential(FlowContext, List) sequential} execution of {@linkplain Task asynchronous tasks}.
 *
 * @param <C> the type of the {@linkplain FlowContext context} shared between tasks
 */
public interface Sequence<C extends FlowContext> extends Promisable<C>, Subscription<C> {

    /**
     * By default, the execution of {@linkplain Task tasks} fail fast.
     */
    boolean DEFAULT_FAIL_FAST = true;

    /**
     * By default, no timeout is used.
     */
    long DEFAULT_TIMEOUT = -1;

    /**
     * Whether the execution of {@linkplain Task tasks} should fail fast or fail last. Defaults to {@value DEFAULT_FAIL_FAST}.
     */
    Sequence<C> failFast(boolean failFast);

    /**
     * The timeout in milliseconds for the sequence. Defaults to no timeout ({@value #DEFAULT_TIMEOUT}).
     * <p>
     * Please note that this only applies to sequential flows. The timeout has no effect for parallel flows. The promise API does not provide a method to cancel running promises.
     */
    Sequence<C> timeout(long timeout);
}
