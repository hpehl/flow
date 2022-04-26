package org.jboss.hal.flow;

import elemental2.promise.IThenable;
import elemental2.promise.Promise;

abstract class FlowRunner<C extends FlowContext> implements Promisable<C>, Subscription<C> {

    static final String TIMEOUT_ERROR = "flow.timeout";

    final C context;

    protected FlowRunner(final C context, final int tasks) {
        this.context = context;
        if (tasks > 1) {
            this.context.progress.reset(tasks);
        } else {
            this.context.progress.reset();
        }
    }

    // ------------------------------------------------------ promisable API

    @Override
    public <V> Promise<V> then(final IThenable.ThenOnFulfilledCallbackFn<? super C, ? extends V> onFulfilled) {
        return runAndCatch(true).then(onFulfilled);
    }

    @Override
    public <V> Promise<V> then(final IThenable.ThenOnFulfilledCallbackFn<? super C, ? extends V> onFulfilled,
            final IThenable.ThenOnRejectedCallbackFn<? extends V> onRejected) {
        return runAndCatch(true).then(onFulfilled, onRejected);
    }

    @Override
    public <V> Promise<V> catch_(final Promise.CatchOnRejectedCallbackFn<? extends V> onRejected) {
        return runAndCatch(true).catch_(onRejected);
    }

    @Override
    public Promise<C> finally_(final Promise.FinallyOnFinallyCallbackFn onFinally) {
        return runAndCatch(true).finally_(onFinally);
    }

    @Override
    public Promise<C> promise() {
        return runAndCatch(true);
    }

    // ------------------------------------------------------ subscription API

    @Override
    public void subscribe(final FlowCallback<C> callback) {
        runAndCatch(false).then(c -> {
            callback.finish(c);
            return null;
        });
    }

    // ------------------------------------------------------ run

    Promise<C> runAndCatch(final boolean rejectOnError) {
        context.status = FlowStatus.IN_PROGRESS;
        return run()
                .then(c -> {
                    c.status = FlowStatus.SUCCESS;
                    return Promise.resolve(c);
                })
                .catch_(error -> {
                    if (TIMEOUT_ERROR.equals(error)) {
                        context.status = FlowStatus.TIMEOUT;
                    } else {
                        context.status = FlowStatus.FAILURE;
                        context.failure = String.valueOf(error);
                    }
                    if (rejectOnError) {
                        return Promise.reject(error);
                    } else {
                        return Promise.resolve(context);
                    }
                });
    }

    abstract Promise<C> run();
}
