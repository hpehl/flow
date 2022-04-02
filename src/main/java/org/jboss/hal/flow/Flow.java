package org.jboss.hal.flow;

import java.util.List;

import static org.jboss.hal.flow.FlowExecutor.Mode.PARALLEL;
import static org.jboss.hal.flow.FlowExecutor.Mode.SEQUENTIAL;

public interface Flow {

    static <C extends FlowContext> ParallelFlow<C> parallel(C context, List<Task<C>> tasks) {
        return new FlowExecutor<>(PARALLEL, context, tasks);
    }

    static <C extends FlowContext> SequentialFlow<C> series(C context, List<Task<C>> tasks) {
        return new FlowExecutor<>(SEQUENTIAL, context, tasks);
    }
}
