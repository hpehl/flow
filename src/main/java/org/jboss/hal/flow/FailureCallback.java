package org.jboss.hal.flow;

@FunctionalInterface
public interface FailureCallback<C extends FlowContext> {

    void failed(C context, String failure);
}
