package org.jboss.hal.flow;

import java.util.List;
import java.util.function.Predicate;

import elemental2.promise.Promise;

import static org.jboss.hal.flow.Flow.sequential;

/**
 * A task implementation that executes a list of {@linkplain Task asynchronous tasks} in {@linkplain Flow#sequential(FlowContext, List) order}.
 * <p>
 * This implementation makes it easy to nest the execution of {@linkplain Task asynchronous tasks} inside a call to {@link Flow#parallel(FlowContext, List)}, {@link Flow#sequential(FlowContext, List)} or {@link Flow#while_(FlowContext, Task, Predicate)}.
 *
 * @param <C> the type of the {@linkplain FlowContext context} shared between tasks
 */
public class SequentialTasks<C extends FlowContext> implements Task<C> {

    private final C context;
    private final List<Task<C>> tasks;
    private final boolean failFast;

    /**
     * Creates a new task that executes the given list of {@linkplain Task asynchronous tasks} in {@linkplain Flow#sequential(FlowContext, List) order} re-using an existing {@linkplain FlowContext context}.
     * <p>
     * The task re-uses the {@linkplain FlowContext context} from the outer call to {@link Flow#parallel(FlowContext, List)}, {@link Flow#sequential(FlowContext, List)} or {@link Flow#while_(FlowContext, Task, Predicate)}.
     *
     * @param tasks    The list of tasks to execute
     * @param failFast whether the execution of the list should fail fast or fail last
     */
    public SequentialTasks(final List<Task<C>> tasks, final boolean failFast) {
        this(null, tasks, failFast);
    }

    /**
     * Creates a new task that executes the given list of {@linkplain Task asynchronous tasks} in {@linkplain Flow#sequential(FlowContext, List) order} using a new {@linkplain FlowContext context}.
     * <p>
     * The task uses the given {@linkplain FlowContext context} for the execution of the {@linkplain Task asynchronous tasks}.
     *
     * @param context  the context shared between tasks
     * @param tasks    The list of tasks to execute
     * @param failFast whether the execution of the list should fail fast or fail last
     */
    public SequentialTasks(final C context, final List<Task<C>> tasks, final boolean failFast) {
        this.context = context;
        this.tasks = tasks;
        this.failFast = failFast;
    }

    @Override
    public Promise<C> apply(final C context) {
        C contextToUse = this.context != null ? this.context : context;
        return sequential(contextToUse, tasks).failFast(failFast).promise();
    }
}
