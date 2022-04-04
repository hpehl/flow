package org.jboss.hal.flow;

import java.util.Iterator;
import java.util.List;

import elemental2.promise.IThenable;
import elemental2.promise.Promise;
import elemental2.promise.Promise.PromiseExecutorCallbackFn.RejectCallbackFn;
import elemental2.promise.Promise.PromiseExecutorCallbackFn.ResolveCallbackFn;
import jsinterop.annotations.JsType;

public class FlowExecutor<C extends FlowContext> {

    private final Mode mode;
    private final C context;
    private final List<Task<C>> tasks;
    private final Iterator<Task<C>> iterator;
    private final boolean failFast;

    FlowExecutor(final Mode mode, final C context, final List<Task<C>> tasks, final boolean failFast) {
        this.mode = mode;
        this.context = context;
        this.tasks = tasks;
        this.iterator = tasks.iterator();
        this.context.progress.reset(tasks.size());
        this.failFast = failFast;
    }

    // ------------------------------------------------------ public API

    Promise<C> execute() {
        if (tasks.isEmpty()) {
            return Promise.resolve(context);
        } else {
            switch (mode) {
                case PARALLEL:
                    return parallel();
                case SEQUENTIAL:
                    return sequential();
                default:
                    throw new IllegalStateException("Unexpected flow execution mode: " + mode);
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    Promise<C> parallel() {
        Promise[] promises = tasks.stream()
                .map(task -> task.apply(context).then(c -> {
                    this.context.progress.tick();
                    return Promise.resolve(c);
                }))
                .toArray(Promise[]::new);
        if (failFast) {
            return FlowPromise.all(promises)
                    .then(all -> {
                        context.progress.finish();
                        return Promise.resolve(context);
                    });
        } else {
            return FlowPromise.allSettled(promises)
                    .then(all -> {
                        context.progress.finish();
                        return Promise.resolve(context);
                    });
        }
    }

    Promise<C> sequential() {
        return new Promise<>(this::next)
                .then(c -> {
                    context.progress.finish();
                    return Promise.resolve(context);
                })
                .catch_(Promise::reject);
    }

    private void next(ResolveCallbackFn<C> resolve, RejectCallbackFn reject) {
        iterator.next().apply(context)
                .then(c -> {
                    if (iterator.hasNext()) {
                        context.progress.tick();
                        next(resolve, reject);
                    } else {
                        resolve.onInvoke(c);
                    }
                    return null;
                })
                .catch_(error -> {
                    if (failFast) {
                        reject.onInvoke(error);
                    } else {
                        if (iterator.hasNext()) {
                            context.progress.tick();
                            next(resolve, reject);
                        } else {
                            resolve.onInvoke(context);
                        }
                    }
                    return null;
                });

    }


    enum Mode {PARALLEL, SEQUENTIAL}


    // We use our own Promise API for the parallel flow
    @JsType(isNative = true, name = "Promise", namespace = "<global>")
    static class FlowPromise {

        static native <V> Promise<Object[]> all(IThenable<? extends V>[] promises);

        static native <V> Promise<Object[]> allSettled(IThenable<? extends V>[] promises);
    }
}
