package org.jboss.hal.flow;

import elemental2.promise.IThenable.ThenOnFulfilledCallbackFn;
import elemental2.promise.IThenable.ThenOnRejectedCallbackFn;
import elemental2.promise.Promise;
import elemental2.promise.Promise.CatchOnRejectedCallbackFn;
import elemental2.promise.Promise.FinallyOnFinallyCallbackFn;

/**
 * An interface to use the promise when executing {@linkplain Task asynchronous tasks}.
 * <p>
 * <b>Error Handling</b><br/>
 * When using the methods in this interface, errors in tasks are propagated to the closest catch handler. This is different from the methods in {@link Subscription} that catches errors in tasks and stores them in the {@linkplain FlowContext context}.
 *
 * @param <C> the type of the {@linkplain FlowContext context} shared between tasks
 */
public interface Promisable<C extends FlowContext> {

    <V> Promise<V> then(ThenOnFulfilledCallbackFn<? super C, ? extends V> onFulfilled);

    <V> Promise<V> then(
            ThenOnFulfilledCallbackFn<? super C, ? extends V> onFulfilled,
            ThenOnRejectedCallbackFn<? extends V> onRejected);

    <V> Promise<V> catch_(CatchOnRejectedCallbackFn<? extends V> onRejected);

    Promise<C> finally_(FinallyOnFinallyCallbackFn onFinally);

    /**
     * Returns the promise of the asynchronous execution.
     */
    Promise<C> promise();
}
