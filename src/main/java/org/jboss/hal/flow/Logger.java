package org.jboss.hal.flow;

interface Logger {

    void start(String id, String message);

    void end(String id, String message);

    void failure(String id, String message);

    void markSuccessful();

    void markFailed();
}
