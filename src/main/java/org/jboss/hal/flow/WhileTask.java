package org.jboss.hal.flow;

import java.util.List;
import java.util.function.Predicate;

import elemental2.promise.Promise;

import static org.jboss.hal.flow.Flow.while_;

/**
 * A task implementation that executes a {@linkplain Task task} as long as a {@linkplain Predicate predicate} evaluates to {@code true}.
 * <p>
 * This implementation makes it easy to nest the execution of {@linkplain Task asynchronous tasks} inside a call to {@link Flow#parallel(FlowContext, List)}, {@link Flow#series(FlowContext, List)} or {@link Flow#while_(FlowContext, Task, Predicate)}.
 *
 * @param <C> the type of the {@linkplain FlowContext context} shared between tasks
 */
public class WhileTask<C extends FlowContext> implements Task<C> {

    private final C context;
    private final Task<C> task;
    private final Predicate<C> until;
    private final boolean failFast;
    private final long interval;
    private final long timeout;

    /**
     * Creates a new task that executes the given {@linkplain Task tasks} as long as the given {@linkplain Predicate predicate} evaluates to {@code true}.
     * <p>
     * The task re-uses the {@linkplain FlowContext context} from the outer call to {@link Flow#parallel(FlowContext, List)}, {@link Flow#series(FlowContext, List)} or {@link Flow#while_(FlowContext, Task, Predicate)}.
     *
     * @param task     the task to execute while the predicate evaluates to {@code true}
     * @param until    the predicate used to decide whether to continue or break the loop
     * @param failFast whether the execution of the list should fail fast or fail last
     * @param interval the interval between the iterations
     * @param timeout  the timeout for the while loop
     */
    public WhileTask(final Task<C> task, Predicate<C> until,
            final boolean failFast, final long interval, final long timeout) {
        this(null, task, until, failFast, interval, timeout);
    }

    /**
     * Creates a new task that executes the given {@linkplain Task tasks} as long as the given {@linkplain Predicate predicate} evaluates to {@code true}.
     * <p>
     * The task uses the given {@linkplain FlowContext context} for the execution of the {@linkplain Task task}.
     *
     * @param context  the context shared between the iterations
     * @param task     the task to execute while the predicate evaluates to {@code true}
     * @param until    the predicate used to decide whether to continue or break the loop
     * @param failFast whether the execution of the list should fail fast or fail last
     * @param interval the interval between the iterations
     * @param timeout  the timeout for the while loop
     */
    public WhileTask(final C context, final Task<C> task, Predicate<C> until,
            final boolean failFast, final long interval, final long timeout) {
        this.context = context;
        this.task = task;
        this.until = until;
        this.interval = interval;
        this.timeout = timeout;
        this.failFast = failFast;
    }

    @Override
    public Promise<C> apply(final C context) {
        C contextToUse = this.context != null ? this.context : context;
        return while_(contextToUse, task, until).interval(interval).timeout(timeout).failFast(failFast).promise();
    }
}
