package org.jboss.hal.flow;

import elemental2.promise.Promise;
import jsinterop.annotations.JsAsync;
import jsinterop.annotations.JsFunction;

@JsFunction
@FunctionalInterface
public interface Task<C extends FlowContext> {

    @JsAsync
    Promise<C> apply(C context);
}
