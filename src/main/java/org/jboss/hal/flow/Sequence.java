package org.jboss.hal.flow;

import java.util.List;

/**
 * An interface to control the {@linkplain Flow#parallel(FlowContext, List) parallel} and {@linkplain Flow#sequential(FlowContext, List) sequential} execution of {@linkplain Task asynchronous tasks}.
 *
 * @param <C> the type of the {@linkplain FlowContext context} shared between tasks
 */
public interface Sequence<C extends FlowContext> extends Promisable<C>, Subscription<C> {

    /**
     * By default, the execution of {@linkplain Task tasks} fails fast.
     */
    boolean DEFAULT_FAIL_FAST = true;

    /**
     * Whether the execution of {@linkplain Task tasks} should fail fast or fail last.
     */
    Sequence<C> failFast(boolean failFast);
}
