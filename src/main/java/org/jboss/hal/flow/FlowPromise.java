package org.jboss.hal.flow;

import elemental2.promise.IThenable;
import elemental2.promise.Promise;
import jsinterop.annotations.JsType;

// We redefine parts of the promise API
@JsType(isNative = true, name = "Promise", namespace = "<global>")
class FlowPromise {

    static native <V> Promise<Object[]> all(IThenable<? extends V>[] promises);

    static native <V> Promise<Object[]> allSettled(IThenable<? extends V>[] promises);
}
