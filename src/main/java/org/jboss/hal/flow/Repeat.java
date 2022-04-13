package org.jboss.hal.flow;

public interface Repeat<C extends FlowContext> extends Promisable<C>, Subscription<C> {

    boolean DEFAULT_FAIL_FAST = true;
    long DEFAULT_INTERVAL = 1000;
    long DEFAULT_TIMEOUT = 10_000;
    String TIMEOUT_ERROR = "org.jboss.hal.flow.timeout";

    Repeat<C> failFast(boolean failFast);

    Repeat<C> interval(long interval);

    Repeat<C> timeout(long timeout);
}
