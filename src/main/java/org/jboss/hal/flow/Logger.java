package org.jboss.hal.flow;

import elemental2.promise.Promise;

interface Logger {

    void start(String id, String message);

    void end(String id, String message);

    void failure(String id, String message);

    Promise<Void> markSuccessful();

    Promise<Void> markFailed();
}
