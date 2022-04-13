package org.jboss.hal.flow;

public interface Subscription<C extends FlowContext> {

    void subscribe(SuccessCallback<C> onSuccess, FailureCallback<C> onFailure);
}
