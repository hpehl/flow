package org.jboss.hal.flow;

public interface Sequence<C extends FlowContext> extends Promisable<C>, Subscription<C> {

    boolean DEFAULT_FAIL_FAST = true;

    Sequence<C> failFast(boolean failFast);
}
