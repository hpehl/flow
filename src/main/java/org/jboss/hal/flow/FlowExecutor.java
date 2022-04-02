package org.jboss.hal.flow;

import java.util.Iterator;
import java.util.List;

import elemental2.core.JsError;
import elemental2.promise.IThenable;
import elemental2.promise.Promise;
import elemental2.promise.Promise.PromiseExecutorCallbackFn.RejectCallbackFn;
import elemental2.promise.Promise.PromiseExecutorCallbackFn.ResolveCallbackFn;
import jsinterop.annotations.JsType;

import static elemental2.dom.DomGlobal.console;

public class FlowExecutor<C extends FlowContext> implements SequentialFlow<C>, ParallelFlow<C> {

    private final Mode mode;
    private final C context;
    private final List<Task<C>> tasks;
    private final Iterator<Task<C>> iterator;
    private boolean failFast;

    FlowExecutor(final Mode mode, final C context, final List<Task<C>> tasks) {
        this.mode = mode;
        this.context = context;
        this.tasks = tasks;
        this.iterator = tasks.iterator();
        this.context.progress.reset(tasks.size());
        this.failFast = true;
    }

    // ------------------------------------------------------ public API

    @Override
    public ParallelFlow<C> onErrorContinue() {
        failFast = false;
        return this;
    }

    @Override
    public SequentialFlow<C> onErrorResumeNext() {
        failFast = false;
        return this;
    }

    @Override
    public void subscribe(final SuccessCallback<C> onSuccess) {
        subscribe(onSuccess, failure -> console.warn(
                "No failure callback was registered for " +
                        mode.name().toLowerCase() + " flow subscription. " +
                        "Ignoring failure: '" + failure + "'"));
    }

    @Override
    public void subscribe(SuccessCallback<C> onSuccess, FailureCallback onFailure) {
        if (tasks.isEmpty()) {
            onSuccess.success(context);
            return;
        }
        switch (mode) {
            case PARALLEL:
                parallel(onSuccess, onFailure);
                break;
            case SEQUENTIAL:
                sequential(onSuccess, onFailure);
                break;
            default:
                throw new IllegalStateException("Unexpected flow execution mode: " + mode);
        }
    }

    // ------------------------------------------------------ internal API

    @SuppressWarnings({"unchecked"})
    void parallel(SuccessCallback<C> onSuccess, FailureCallback onFailure) {
        Promise<C>[] promises = tasks.stream()
                .map(task -> task.apply(context).then(c -> {
                    this.context.progress.tick();
                    return Promise.resolve(c);
                }))
                .toArray(Promise[]::new);
        if (failFast) {
            ParallelPromise.all(promises)
                    .then(all -> {
                        context.progress.finish();
                        onSuccess.success(context);
                        return null;
                    })
                    .catch_(error -> {
                        onFailure.failed(promiseFailure(error));
                        return null;
                    });
        } else {
            ParallelPromise.allSettled(promises)
                    .then(all -> {
                        context.progress.finish();
                        onSuccess.success(context);
                        return null;
                    })
                    .catch_(error -> {
                        // Should never happen
                        String failure = promiseFailure(error);
                        console.error("Unexpected failure '" + failure + "' in parallel flow subscription");
                        onFailure.failed(failure);
                        return null;
                    });
        }
    }

    void sequential(SuccessCallback<C> onSuccess, FailureCallback onFailure) {
        new Promise<>(this::next)
                .then(c -> {
                    context.progress.finish();
                    onSuccess.success(c);
                    return null;
                })
                .catch_(error -> {
                    onFailure.failed(promiseFailure(error));
                    return null;
                });
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

    private String promiseFailure(Object error) {
        if (error instanceof JsError) {
            return ((JsError) error).message;
        } else if (error instanceof Throwable) {
            return ((Throwable) error).getMessage();
        } else {
            return String.valueOf(error);
        }
    }


    enum Mode {PARALLEL, SEQUENTIAL}


    // We use our own Promise API for the parallel flow
    @JsType(isNative = true, name = "Promise", namespace = "<global>")
    static class ParallelPromise {

        static native <V> Promise<Object[]> all(IThenable<? extends V>[] promises);

        static native <V> Promise<Object[]> allSettled(IThenable<? extends V>[] promises);
    }


    @SuppressWarnings("unused")
    @JsType(isNative = true, name = "Object", namespace = "<global>")
    static class Outcome<C extends FlowContext> {

        String status;
        C value;
        Object reason;
    }
}
