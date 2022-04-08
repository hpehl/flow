package org.jboss.hal.flow;

import java.util.List;
import java.util.function.Predicate;

import elemental2.promise.Promise;

import static org.jboss.hal.flow.FlowExecutor.Mode.PARALLEL;
import static org.jboss.hal.flow.FlowExecutor.Mode.SEQUENTIAL;

public interface Flow {

    String TIMEOUT_ERROR = "org.jboss.hal.flow.repeat.timeout";

    static <C extends FlowContext> Promise<C> parallel(C context, List<Task<C>> tasks) {
        return parallel(context, tasks, true);
    }

    static <C extends FlowContext> Promise<C> parallel(C context, List<Task<C>> tasks, boolean failFast) {
        return new FlowExecutor<>(PARALLEL, context, tasks, failFast).execute();
    }

    static <C extends FlowContext> Promise<C> series(C context, List<Task<C>> tasks) {
        return series(context, tasks, true);
    }

    static <C extends FlowContext> Promise<C> series(C context, List<Task<C>> tasks, boolean failFast) {
        return new FlowExecutor<>(SEQUENTIAL, context, tasks, failFast).execute();
    }

    static <C extends FlowContext> Promise<C> repeat(C context, Task<C> task, Predicate<C> until, int timeout) {
        return new FlowLoop<>(context, task, until, timeout, false).execute();
    }

    static <C extends FlowContext> Promise<C> repeat(C context, Task<C> task, Predicate<C> until, int timeout, boolean failFast) {
        return new FlowLoop<>(context, task, until, timeout, failFast).execute();
    }
}
