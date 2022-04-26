package org.jboss.hal.flow;

import elemental2.promise.Promise;

interface Logger {

    void logStart(String id, String message);

    void logEnd(String id, String message);

    void logFailure(String id, String message);

    Promise<Void> finish(FlowStatus result);
}
