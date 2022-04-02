package org.jboss.hal.flow;

public interface SequentialFlow<C extends FlowContext> {

    SequentialFlow<C> onErrorResumeNext();

    void subscribe(SuccessCallback<C> onSuccess);

    void subscribe(SuccessCallback<C> onSuccess, FailureCallback onFailure);
}
