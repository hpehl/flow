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

import static elemental2.dom.DomGlobal.fetch;
import static elemental2.dom.DomGlobal.setTimeout;

class Tasks {

    private static final double FAILURE_PERCENTAGE = 0.08;
    static final String GOOD_TIME = "0";
    static final int INTERVAL = 1000;
    static final int TIMEOUT = 8000;

    private final Progress progress;
    private final Logger logger;
    private final boolean randomFailure;
    private final boolean failFast;

    Tasks(final Progress progress, final Logger logger, final boolean randomFailure, final boolean failFast) {
        this.progress = progress;
        this.logger = logger;
        this.randomFailure = randomFailure;
        this.failFast = failFast;
    }

    void parallel() {
        Sequence<FlowContext> sequence = Flow.parallel(context(), tasks())
                .failFast(failFast);
        sequence.subscribe(__ -> logger.markSuccessful(), (__, ___) -> logger.markFailed());

        // alternative promise based implementation
/*
        sequence.promise()
                .then(c -> {
                    logger.markSuccessful();
                    return null;
                })
                .catch_(error -> {
                    logger.markFailed();
                    return null;
                });
*/
    }

    void sequential() {
        Sequence<FlowContext> sequence = Flow.series(context(), tasks())
                .failFast(failFast);
        sequence.subscribe(__ -> logger.markSuccessful(), (__, ___) -> logger.markFailed());

        // alternative promise based implementation
/*
        sequence.promise()
                .then(c -> {
                    logger.markSuccessful();
                    return null;
                })
                .catch_(error -> {
                    logger.markFailed();
                    return null;
                });
*/
    }

    void repeat() {
        Repeat<FlowContext> repeat = Flow.repeat(context(), currentTime(),
                        context -> !context.pop("").endsWith(GOOD_TIME))
                .failFast(failFast)
                .interval(INTERVAL)
                .timeout(TIMEOUT);
        repeat.subscribe(__ -> logger.markSuccessful(), (__, ___) -> logger.markFailed());

        // alternative promise based implementation
/*
        repeat.promise()
                .then(c -> {
                    logger.markSuccessful();
                    return null;
                })
                .catch_(error -> {
                    logger.markFailed();
                    return null;
                });
*/
    }

    private FlowContext context() {
        return new FlowContext(progress);
    }

    private List<Task<FlowContext>> tasks() {
        return Arrays.asList(
                currentTime(),
                delay(3000),
                currentTime(),
                delay(2000),
                currentTime(),
                delay(1000),
                currentTime()
        );
    }

    private Task<FlowContext> delay(long milliseconds) {
        return context -> new Promise<>(
                (resolve, reject) -> {
                    String uniqueId = Id.unique();
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

    private Task<FlowContext> currentTime() {
        return context -> {
            String uniqueId = Id.unique();
            logger.start(uniqueId, "Fetch time...");
            return fetchTime().then(time -> {
                context.push(time);
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
