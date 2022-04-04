package org.jboss.hal.flow;

import java.util.Arrays;
import java.util.List;

import org.jboss.elemento.Id;

import elemental2.dom.Response;
import elemental2.promise.Promise;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

import static elemental2.dom.DomGlobal.console;
import static elemental2.dom.DomGlobal.fetch;
import static elemental2.dom.DomGlobal.setTimeout;

class Tasks {

    private static final double FAILURE_PERCENTAGE = 0.08;

    private final String tasksId;
    private final Progress progress;
    private final boolean randomFailure;
    private final boolean failFast;
    private final Logger logger;

    Tasks(final Progress progress, final Logger logger, final boolean randomFailure, final boolean failFast) {
        this.tasksId = Id.unique("tasks");
        this.progress = progress;
        this.randomFailure = randomFailure;
        this.failFast = failFast;
        this.logger = logger;
    }

    void parallel() {
        Flow.parallel(new FlowContext(progress), tasks(), failFast)
                .then(c -> {
                    logger.markSuccessful();
                    return null;
                })
                .catch_(error -> {
                    console.log("error: " + error);
                    logger.markFailed();
                    return null;
                });
    }

    void sequential() {
        Flow.series(new FlowContext(progress), tasks(), failFast)
                .then(c -> {
                    logger.markSuccessful();
                    return null;
                })
                .catch_(error -> {
                    console.log("error: " + error);
                    logger.markFailed();
                    return null;
                });
    }

    private List<Task<FlowContext>> tasks() {
        return Arrays.asList(
                currentTime("0"),
                delay("1", 3000),
                currentTime("2"),
                delay("3", 2000),
                currentTime("4"),
                delay("5", 1000),
                currentTime("6")
        );
    }

    private Task<FlowContext> delay(String id, long milliseconds) {
        final String uniqueId = Id.build(tasksId, id);
        return context -> new Promise<>(
                (resolve, reject) -> {
                    logger.start(uniqueId, "Wait " + milliseconds + " ms...");
                    setTimeout(ignore -> {
                        if (blowUp()) {
                            logger.failure(uniqueId, "Failed");
                            reject.onInvoke("Random failure");
                        } else {
                            logger.end(uniqueId, "Done");
                            resolve.onInvoke(context);
                        }
                    }, milliseconds);
                });
    }

    private Task<FlowContext> currentTime(String id) {
        final String uniqueId = Id.build(tasksId, id);
        return context -> {
            logger.start(uniqueId, "Fetch time...");
            return fetchTime().then(time -> {
                if (blowUp()) {
                    logger.failure(uniqueId, "Failed");
                    throw new RuntimeException("Random failure");
                } else {
                    logger.end(uniqueId, time);
                    return Promise.resolve(context);
                }
            });
        };
    }

    private boolean blowUp() {
        return randomFailure && Math.random() < FAILURE_PERCENTAGE;
    }

    private Promise<String> fetchTime() {
        return fetch("http://worldtimeapi.org/api/timezone/Europe/Berlin")
                .then(Response::json)
                .then(json -> Promise.resolve(Js.<Now>cast(json).time()));
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    private static class Now {

        String datetime;

        @JsOverlay
        final String time() {
            // datetime format is "2022-03-31T11:03:39.348365+02:00"
            return datetime.substring(11, 23);
        }
    }
}
