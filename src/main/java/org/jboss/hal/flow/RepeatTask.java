package org.jboss.hal.flow;

import java.util.List;
import java.util.function.Predicate;

import elemental2.promise.Promise;

import static org.jboss.hal.flow.Flow.repeat;

/**
 * A task implementation that executes a {@linkplain Task task} as long as certain conditions are  met.
 * <p>
 * This implementation makes it easy to nest the execution of {@linkplain Task asynchronous tasks} inside a call to {@link Flow#parallel(FlowContext, List)}, {@link Flow#sequential(FlowContext, List)} or {@link Flow#repeat(FlowContext, Task)}.
 *
 * @param <C> the type of the {@linkplain FlowContext context} shared between tasks
 */
public class RepeatTask<C extends FlowContext> implements Task<C> {

    private final C context;
    private final Task<C> task;
    private final Predicate<C> predicate;
    private final boolean failFast;
    private final long interval;
    private final long timeout;
    private final int iterations;

    /**
     * Creates a new task that executes the given {@linkplain Task tasks} as long as the given {@linkplain Predicate predicate} evaluates to {@code true}.
     * <p>
     * The task re-uses the {@linkplain FlowContext context} from the outer call to {@link Flow#parallel(FlowContext, List)}, {@link Flow#sequential(FlowContext, List)} or {@link Flow#repeat(FlowContext, Task)}.
     *
     * @param task       the task to execute while the predicate evaluates to {@code true}
     * @param predicate  the predicate used to decide whether to continue or break the loop
     * @param failFast   whether the execution of the list should fail fast or fail last
     * @param interval   the interval between the iterations
     * @param timeout    the timeout for the while loop
     * @param iterations the maximal iterations the loop
     */
    public RepeatTask(final Task<C> task, Predicate<C> predicate,
            final boolean failFast, final long interval, final long timeout, final int iterations) {
        this(null, task, predicate, failFast, interval, timeout, iterations);
    }

    /**
     * Creates a new task that executes the given {@linkplain Task tasks} as long as the given {@linkplain Predicate predicate} evaluates to {@code true}.
     * <p>
     * The task uses the given {@linkplain FlowContext context} for the execution of the {@linkplain Task task}.
     *
     * @param context    the context shared between the iterations
     * @param task       the task to execute while the predicate evaluates to {@code true}
     * @param predicate  the predicate used to decide whether to continue or break the loop
     * @param failFast   whether the execution of the list should fail fast or fail last
     * @param interval   the interval between the iterations
     * @param timeout    the timeout for the while loop
     * @param iterations the maximal iterations the loop
     */
    public RepeatTask(final C context, final Task<C> task, Predicate<C> predicate,
            final boolean failFast, final long interval, final long timeout, final int iterations) {
        this.context = context;
        this.task = task;
        this.predicate = predicate;
        this.interval = interval;
        this.timeout = timeout;
        this.failFast = failFast;
        this.iterations = iterations;
    }

    @Override
    public Promise<C> apply(final C context) {
        C contextToUse = this.context != null ? this.context : context;
        return repeat(contextToUse, task)
                .while_(predicate)
                .failFast(failFast)
                .interval(interval)
                .timeout(timeout)
                .iterations(iterations)
                .promise();
    }
}
