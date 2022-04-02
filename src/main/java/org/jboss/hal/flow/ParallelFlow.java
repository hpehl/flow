package org.jboss.hal.flow;

public interface ParallelFlow<C extends FlowContext> {

    ParallelFlow<C> onErrorContinue();

    void subscribe(SuccessCallback<C> onSuccess, FailureCallback onFailure);
}
