package org.jboss.hal.flow;

import elemental2.promise.Promise;

public interface Promisable<C extends FlowContext> {

    Promise<C> promise();
}
