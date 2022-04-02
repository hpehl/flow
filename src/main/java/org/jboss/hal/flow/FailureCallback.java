package org.jboss.hal.flow;

@FunctionalInterface
public interface FailureCallback {

    void failed(String failure);
}
