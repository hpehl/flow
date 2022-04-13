package org.jboss.hal.flow;

@FunctionalInterface
public interface SuccessCallback<C extends FlowContext> {

    void success(C context);
}
